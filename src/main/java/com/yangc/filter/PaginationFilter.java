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

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.yangc.common.Pagination;
import com.yangc.common.PaginationThreadUtils;

@SuppressWarnings("unchecked")
public class PaginationFilter implements Filter {

	public static final Logger logger = Logger.getLogger(PaginationFilter.class);

	// 前端js对分页请求的名字
	private static final String PAGE_SIZE = "limit";
	private static final String PAGE_NOW = "page";

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) request;
		String uri = req.getRequestURI();
		if (uri.endsWith("_page")) {
			Pagination pagination = PaginationThreadUtils.get();
			if (pagination == null) {
				pagination = new Pagination();
				PaginationThreadUtils.set(pagination);
			}
			Map<String, Object> params = req.getParameterMap();
			// 设置要跳转到的页数
			if (params.get(PAGE_NOW) == null) {
				pagination.setPageNow(1);
			} else {
				String pageNow = ((String[]) params.get(PAGE_NOW))[0];
				if (StringUtils.isBlank(pageNow)) {
					pagination.setPageNow(1);
				} else {
					pagination.setPageNow(Integer.parseInt(pageNow));
				}
			}
			// 设置每页的行数
			if (params.get(PAGE_SIZE) != null) {
				String pageSize = ((String[]) params.get(PAGE_SIZE))[0];
				if (StringUtils.isNotBlank(pageSize)) {
					pagination.setPageSize(Integer.parseInt(pageSize));
				}
			}
			logger.info("PaginationInterceptor - pageNow=" + pagination.getPageNow() + ", pageSize=" + pagination.getPageSize());
		}
		chain.doFilter(request, response);
		PaginationThreadUtils.clear();
	}

	@Override
	public void destroy() {
	}

}
