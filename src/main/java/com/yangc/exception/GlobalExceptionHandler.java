package com.yangc.exception;

import java.net.URI;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.shiro.authz.UnauthorizedException;

import com.yangc.bean.ResultBean;
import com.yangc.utils.Constants;

@Provider
public class GlobalExceptionHandler implements ExceptionMapper<Exception> {

	public static final Logger logger = Logger.getLogger(GlobalExceptionHandler.class);

	@Context
	private UriInfo uriInfo;

	@Context
	private HttpServletRequest request;

	@Override
	public Response toResponse(Exception exception) {
		logger.error(exception.getMessage());
		if (exception instanceof UnauthorizedException) {
			String header = request.getHeader("X-Requested-With");
			// 异步
			if (StringUtils.isNotBlank(header) && (header.equals("X-Requested-With") || header.equals("XMLHttpRequest"))) {
				return Response.ok(new ResultBean(false, "没有权限")).type(MediaType.APPLICATION_JSON).build();
			}
			// 同步
			else {
				URI uri = uriInfo.getBaseUriBuilder().path(Constants.EXCEPTION_PAGE).build();
				return Response.seeOther(uri).type(MediaType.APPLICATION_FORM_URLENCODED).build();
			}
		}
		return WebApplicationException.build();
	}

}
