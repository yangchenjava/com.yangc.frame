package com.yangc.system.resource;

import java.util.Collection;
import java.util.List;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;

import com.yangc.bean.ResultBean;
import com.yangc.exception.WebApplicationException;
import com.yangc.shiro.utils.ShiroUtils;
import com.yangc.system.bean.AuthTree;
import com.yangc.system.bean.Permission;
import com.yangc.system.service.AclService;

@Path("/acl")
public class AclResource {

	private static final Logger logger = LogManager.getLogger(AclResource.class);

	private AclService aclService;

	/**
	 * @功能: 获取用户拥有的权限
	 * @作者: yangc
	 * @创建日期: 2014年6月13日 下午1:00:14
	 * @return
	 */
	@POST
	@Path("getUserPermission")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getUserPermission() {
		logger.info("getUserPermission");
		try {
			Collection<String> userPermission = ShiroUtils.getUserPermission(SecurityUtils.getSubject().getPrincipals());
			return Response.ok(userPermission).build();
		} catch (Exception e) {
			e.printStackTrace();
			return WebApplicationException.build();
		}
	}

	/**
	 * @功能: 某个角色所拥有的权限
	 * @作者: yangc
	 * @创建日期: 2013年12月24日 下午10:54:50
	 * @return
	 */
	@POST
	@Path("getAuthTreeList")
	@Produces(MediaType.APPLICATION_JSON)
	@RequiresPermissions("role:" + Permission.SEL)
	public Response getAuthTreeList(@FormParam("roleId") Long roleId, @FormParam("parentMenuId") Long parentMenuId) {
		logger.info("getAuthTreeList - roleId=" + roleId + ", parentMenuId=" + parentMenuId);
		try {
			List<AuthTree> authTreeList = this.aclService.getAclListByRoleIdAndParentMenuId(roleId, parentMenuId);
			return Response.ok(authTreeList).build();
		} catch (Exception e) {
			e.printStackTrace();
			return WebApplicationException.build();
		}
	}

	/**
	 * @功能: 添加或修改权限
	 * @作者: yangc
	 * @创建日期: 2013年12月24日 下午11:14:16
	 * @return
	 */
	@POST
	@Path("addOrUpdateAcl")
	@Produces(MediaType.APPLICATION_JSON)
	@RequiresPermissions("role:" + Permission.ADD)
	public Response addOrUpdateAcl(@FormParam("roleId") Long roleId, @FormParam("menuId") Long menuId, @FormParam("permission") int permission, @FormParam("allow") int allow) {
		logger.info("addOrUpdateAcl - roleId=" + roleId + ", menuId=" + menuId + ", permission=" + permission + ", allow=" + allow);
		try {
			this.aclService.addOrUpdateAcl(roleId, menuId, permission, allow);
			// 清除所有权限缓存信息
			ShiroUtils.clearAllCachedAuthorizationInfo();
			return Response.ok(new ResultBean(true, "")).build();
		} catch (Exception e) {
			e.printStackTrace();
			return WebApplicationException.build();
		}
	}

	public void setAclService(AclService aclService) {
		this.aclService = aclService;
	}

}
