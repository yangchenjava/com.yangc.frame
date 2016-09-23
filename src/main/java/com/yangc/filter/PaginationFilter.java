package com.yangc.filter;

import java.io.IOException;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.yangc.common.Pagination;
import com.yangc.common.PaginationThreadUtils;

public class PaginationFilter implements Filter {

	private static final Logger logger = LogManager.getLogger(PaginationFilter.class);

	// 前端js对分页请求的名字
	private static final String PAGE_SIZE = "limit";
	private static final String PAGE_NOW = "page";

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
	}

	@Override
	@SuppressWarnings("unchecked")
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) request;
		String uri = req.getRequestURI();
		if (uri.endsWith("_page")) {
			Pagination pagination = PaginationThreadUtils.get();
			if (pagination == null) {
				pagination = new Pagination();
				PaginationThreadUtils.set(pagination);
			}
			Map<String, String[]> params = req.getParameterMap();
			// 设置要跳转到的页数
			if (params.get(PAGE_NOW) == null) {
				pagination.setPageNow(1);
			} else {
				String pageNow = params.get(PAGE_NOW)[0];
				if (StringUtils.isBlank(pageNow)) {
					pagination.setPageNow(1);
				} else {
					pagination.setPageNow(NumberUtils.toInt(pageNow, 1));
				}
			}
			// 设置每页的行数
			if (params.get(PAGE_SIZE) != null) {
				String pageSize = params.get(PAGE_SIZE)[0];
				if (StringUtils.isNotBlank(pageSize)) {
					pagination.setPageSize(NumberUtils.toInt(pageSize));
				}
			}
			logger.info("PaginationFilter - pageNow={}, pageSize={}", pagination.getPageNow(), pagination.getPageSize());
		}
		chain.doFilter(request, response);
		PaginationThreadUtils.clear();
	}

	@Override
	public void destroy() {
	}

}
