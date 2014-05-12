package com.yangc.system.resource;

import java.util.List;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;

import com.yangc.bean.DataGridBean;
import com.yangc.bean.ResultBean;
import com.yangc.exception.WebApplicationException;
import com.yangc.system.bean.oracle.TSysRole;
import com.yangc.system.service.RoleService;

@Path("/role")
public class RoleResource {

	public static final Logger logger = Logger.getLogger(RoleResource.class);

	private RoleService roleService;

	@POST
	@Path("getRoleList")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getRoleList() {
		logger.info("getRoleList");
		try {
			List<TSysRole> roleList = this.roleService.getRoleList();
			return Response.ok(roleList).build();
		} catch (Exception e) {
			logger.error(e.getMessage());
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
	public Response getRoleList_page() {
		logger.info("getRoleList_page");
		try {
			List<TSysRole> roleList = this.roleService.getRoleList_page();
			DataGridBean dataGridBean = new DataGridBean();
			dataGridBean.setDataGrid(roleList);
			return Response.ok(dataGridBean).build();
		} catch (Exception e) {
			logger.error(e.getMessage());
			return WebApplicationException.build();
		}
	}

	@POST
	@Path("addOrUpdateRole")
	@Produces(MediaType.APPLICATION_JSON)
	public Response addOrUpdateRole(@FormParam("id") Long id, @FormParam("roleName") String roleName) {
		logger.info("addOrUpdateRole - id=" + id + ", roleName=" + roleName);
		ResultBean resultBean = new ResultBean();
		try {
			resultBean.setSuccess(true);
			if (id == null) {
				resultBean.setMessage("添加成功");
			} else {
				resultBean.setMessage("修改成功");
			}
			this.roleService.addOrUpdateRole(id, roleName);
			return Response.ok(resultBean).build();
		} catch (Exception e) {
			logger.error(e.getMessage());
			return WebApplicationException.build();
		}
	}

	@POST
	@Path("delRole")
	@Produces(MediaType.APPLICATION_JSON)
	public Response delRole(@FormParam("id") Long id) {
		logger.info("delRole - id=" + id);
		try {
			this.roleService.delRole(id);
			return Response.ok(new ResultBean(true, "删除成功")).build();
		} catch (Exception e) {
			logger.error(e.getMessage());
			return WebApplicationException.build();
		}
	}

	public void setRoleService(RoleService roleService) {
		this.roleService = roleService;
	}

}
