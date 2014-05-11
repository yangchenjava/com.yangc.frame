package com.yangc.filter;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;

import com.yangc.bean.ResultBean;
import com.yangc.utils.ParamUtils;
import com.yangc.utils.json.JsonUtils;

public class SessionFilter implements Filter {

	@Override
	public void init(FilterConfig config) throws ServletException {
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) request;
		String uri = req.getRequestURI();
		HttpSession session = req.getSession();
		Object obj = session.getAttribute(ParamUtils.LOGIN_USER);
		if (obj == null && !uri.contains("userAction!login.html")) {
			String header = req.getHeader("X-Requested-With");
			if (StringUtils.isNotBlank(header) && header.equals("X-Requested-With")) {
				response.setContentType("application/json;charset=UTF-8");
				PrintWriter pw = response.getWriter();
				pw.write(JsonUtils.toJson(new ResultBean(false, "登录超时, 请刷新页面!")));
				pw.flush();
				pw.close();
			} else {
				req.getRequestDispatcher("/jsp/login.jsp").forward(request, response);
			}
			return;
		}
		chain.doFilter(request, response);
	}

	@Override
	public void destroy() {
	}

}
