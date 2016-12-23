package com.yangc.filter;

import java.io.IOException;
import java.util.Map;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.filter.OncePerRequestFilter;

import com.yangc.common.Pagination;
import com.yangc.common.PaginationThreadUtils;

public class PaginationFilter extends OncePerRequestFilter {

	private static final Logger logger = LogManager.getLogger(PaginationFilter.class);

	// 前端js对分页请求的名字
	private static final String PAGE_SIZE = "limit";
	private static final String PAGE_NOW = "page";

	@Override
	@SuppressWarnings("unchecked")
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
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
		filterChain.doFilter(request, response);
		PaginationThreadUtils.clear();
	}

}
