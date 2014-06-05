package com.yangc.system.resource;

import java.util.List;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;
import org.apache.shiro.authz.annotation.RequiresPermissions;

import com.yangc.bean.DataGridBean;
import com.yangc.bean.ResultBean;
import com.yangc.exception.WebApplicationException;
import com.yangc.shiro.utils.ShiroUtils;
import com.yangc.system.bean.oracle.Permission;
import com.yangc.system.bean.oracle.TSysRole;
import com.yangc.system.service.RoleService;

@Path("/role")
public class RoleResource {

	private static final Logger logger = Logger.getLogger(RoleResource.class);

	private RoleService roleService;

	@POST
	@Path("getRoleList")
	@Produces(MediaType.APPLICATION_JSON)
	@RequiresPermissions("role:" + Permission.SEL)
	public Response getRoleList() {
		logger.info("getRoleList");
		try {
			List<TSysRole> roleList = this.roleService.getRoleList();
			return Response.ok(roleList).build();
		} catch (Exception e) {
			e.printStackTrace();
			return WebApplicationException.build();
		}
	}

	/**
	 * @功能: 查询所有角色(分页)
	 * @作者: yangc
	 * @创建日期: 2013年12月23日 下午7:16:59
	 * @return
	 */
	@POST
	@Path("getRoleList_page")
	@Produces(MediaType.APPLICATION_JSON)
	@RequiresPermissions("role:" + Permission.SEL)
	public Response getRoleList_page() {
		logger.info("getRoleList_page");
		try {
			List<TSysRole> roleList = this.roleService.getRoleList_page();
			DataGridBean dataGridBean = new DataGridBean();
			dataGridBean.setDataGrid(roleList);
			return Response.ok(dataGridBean).build();
		} catch (Exception e) {
			e.printStackTrace();
			return WebApplicationException.build();
		}
	}

	@POST
	@Path("addRole")
	@Produces(MediaType.APPLICATION_JSON)
	@RequiresPermissions("role:" + Permission.ADD)
	public Response addRole(@FormParam("roleName") String roleName) {
		logger.info("addRole - roleName=" + roleName);
		try {
			this.roleService.addOrUpdateRole(null, roleName);
			return Response.ok(new ResultBean(true, "添加成功")).build();
		} catch (Exception e) {
			e.printStackTrace();
			return WebApplicationException.build();
		}
	}

	@POST
	@Path("updateRole")
	@Produces(MediaType.APPLICATION_JSON)
	@RequiresPermissions("role:" + Permission.UPD)
	public Response updateRole(@FormParam("id") Long id, @FormParam("roleName") String roleName) {
		logger.info("updateRole - id=" + id + ", roleName=" + roleName);
		try {
			this.roleService.addOrUpdateRole(id, roleName);
			return Response.ok(new ResultBean(true, "修改成功")).build();
		} catch (Exception e) {
			e.printStackTrace();
			return WebApplicationException.build();
		}
	}

	@POST
	@Path("delRole")
	@Produces(MediaType.APPLICATION_JSON)
	@RequiresPermissions("role:" + Permission.DEL)
	public Response delRole(@FormParam("id") Long id) {
		logger.info("delRole - id=" + id);
		try {
			this.roleService.delRole(id);
			// 清除所有权限缓存信息
			ShiroUtils.clearAllCachedAuthorizationInfo();
			return Response.ok(new ResultBean(true, "删除成功")).build();
		} catch (Exception e) {
			e.printStackTrace();
			return WebApplicationException.build();
		}
	}

	public void setRoleService(RoleService roleService) {
		this.roleService = roleService;
	}

}
