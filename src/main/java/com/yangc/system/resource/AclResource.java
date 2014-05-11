package com.yangc.system.resource;

import java.io.IOException;
import java.util.List;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yangc.bean.ResultBean;
import com.yangc.exception.WebApplicationException;
import com.yangc.system.bean.oracle.AuthTree;
import com.yangc.system.service.AclService;

@Path("/acl")
public class AclResource {

	public static final Logger logger = LoggerFactory.getLogger(AclResource.class);

	private AclService aclService;

	/**
	 * @功能: 某个角色所拥有的权限
	 * @作者: yangc
	 * @创建日期: 2013年12月24日 下午10:54:50
	 * @return
	 * @throws IOException
	 */
	@POST
	@Path("getAuthTreeList")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAuthTreeList(@QueryParam("roleId") Long roleId, @QueryParam("parentMenuId") Long parentMenuId) {
		logger.info("getAuthTreeList - roleId=" + roleId + ", parentMenuId=" + parentMenuId);
		try {
			List<AuthTree> authTreeList = this.aclService.getAclListByRoleIdAndParentMenuId(roleId, parentMenuId);
			return Response.ok(authTreeList).build();
		} catch (Exception e) {
			logger.error(e.getMessage());
			return WebApplicationException.build();
		}
	}

	/**
	 * @功能: 添加或修改权限
	 * @作者: yangc
	 * @创建日期: 2013年12月24日 下午11:14:16
	 * @return
	 * @throws IOException
	 */
	@POST
	@Path("addOrUpdateAcl")
	@Produces(MediaType.APPLICATION_JSON)
	public Response addOrUpdateAcl(@QueryParam("roleId") Long roleId, @QueryParam("menuId") Long menuId, @QueryParam("permission") int permission, @QueryParam("allow") int allow) {
		logger.info("addOrUpdateAcl - roleId=" + roleId + ", menuId=" + menuId + ", permission=" + permission + ", allow=" + allow);
		try {
			this.aclService.addOrUpdateAcl(roleId, menuId, permission, allow);
			return Response.ok(new ResultBean(true, "")).build();
		} catch (Exception e) {
			logger.error(e.getMessage());
			return WebApplicationException.build();
		}
	}

	public void setAclService(AclService aclService) {
		this.aclService = aclService;
	}

}
