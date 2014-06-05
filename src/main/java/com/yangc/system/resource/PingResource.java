package com.yangc.system.resource;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;

import com.yangc.bean.ResultBean;
import com.yangc.exception.WebApplicationException;

@Path("/ping")
public class PingResource {

	private static final Logger logger = Logger.getLogger(PingResource.class);

	/**
	 * @功能: 检查是否通畅
	 * @作者: yangc
	 * @创建日期: 2013年12月23日 下午2:13:04
	 * @return
	 */
	@POST
	@Path("system")
	@Produces(MediaType.APPLICATION_JSON)
	public Response system() {
		logger.info("system");
		try {
			return Response.ok(new ResultBean(true, "")).build();
		} catch (Exception e) {
			e.printStackTrace();
			return WebApplicationException.build();
		}
	}

}
