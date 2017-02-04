package com.yangc.exception;

import javax.ws.rs.core.Response;

import com.yangc.bean.ResultBean;

public class WebApplicationException {

	public static Response build() {
		return Response.serverError().entity(new ResultBean(false, "服务器异常")).build();
	}

}
