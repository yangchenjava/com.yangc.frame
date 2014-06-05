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
import com.yangc.system.bean.oracle.Permission;
import com.yangc.system.bean.oracle.TSysDepartment;
import com.yangc.system.service.DeptService;

@Path("/dept")
public class DeptResource {

	private static final Logger logger = Logger.getLogger(DeptResource.class);

	private DeptService deptService;

	/**
	 * @功能: 查询所有部门(下拉列表)
	 * @作者: yangc
	 * @创建日期: 2013年12月23日 下午2:13:04
	 * @return
	 */
	@POST
	@Path("getDeptList")
	@Produces(MediaType.APPLICATION_JSON)
	@RequiresPermissions("dept:" + Permission.SEL)
	public Response getDeptList() {
		logger.info("getDeptList");
		try {
			List<TSysDepartment> deptList = this.deptService.getDeptList();
			return Response.ok(deptList).build();
		} catch (Exception e) {
			e.printStackTrace();
			return WebApplicationException.build();
		}
	}

	/**
	 * @功能: 查询所有部门(分页)
	 * @作者: yangc
	 * @创建日期: 2013年12月23日 下午2:13:04
	 * @return
	 */
	@POST
	@Path("getDeptList_page")
	@Produces(MediaType.APPLICATION_JSON)
	@RequiresPermissions("dept:" + Permission.SEL)
	public Response getDeptList_page() {
		logger.info("getDeptList_page");
		try {
			List<TSysDepartment> deptList = this.deptService.getDeptList_page();
			DataGridBean dataGridBean = new DataGridBean();
			dataGridBean.setDataGrid(deptList);
			return Response.ok(dataGridBean).build();
		} catch (Exception e) {
			e.printStackTrace();
			return WebApplicationException.build();
		}
	}

	/**
	 * @功能: 添加部门
	 * @作者: yangc
	 * @创建日期: 2013年12月23日 下午2:59:26
	 * @return
	 */
	@POST
	@Path("addDept")
	@Produces(MediaType.APPLICATION_JSON)
	@RequiresPermissions("dept:" + Permission.ADD)
	public Response addDept(@FormParam("deptName") String deptName, @FormParam("serialNum") Long serialNum) {
		logger.info("addDept - deptName=" + deptName + ", serialNum=" + serialNum);
		try {
			this.deptService.addOrUpdateDept(null, deptName, serialNum);
			return Response.ok(new ResultBean(true, "添加成功")).build();
		} catch (Exception e) {
			e.printStackTrace();
			return WebApplicationException.build();
		}
	}

	/**
	 * @功能: 修改部门
	 * @作者: yangc
	 * @创建日期: 2013年12月23日 下午2:59:26
	 * @return
	 */
	@POST
	@Path("updateDept")
	@Produces(MediaType.APPLICATION_JSON)
	@RequiresPermissions("dept:" + Permission.UPD)
	public Response updateDept(@FormParam("id") Long id, @FormParam("deptName") String deptName, @FormParam("serialNum") Long serialNum) {
		logger.info("updateDept - id=" + id + ", deptName=" + deptName + ", serialNum=" + serialNum);
		try {
			this.deptService.addOrUpdateDept(id, deptName, serialNum);
			return Response.ok(new ResultBean(true, "修改成功")).build();
		} catch (Exception e) {
			e.printStackTrace();
			return WebApplicationException.build();
		}
	}

	/**
	 * @功能: 删除部门
	 * @作者: yangc
	 * @创建日期: 2013年12月23日 下午3:02:44
	 * @return
	 */
	@POST
	@Path("delDept")
	@Produces(MediaType.APPLICATION_JSON)
	@RequiresPermissions("dept:" + Permission.DEL)
	public Response delDept(@FormParam("id") Long id) {
		logger.info("delDept - id=" + id);
		ResultBean resultBean = new ResultBean();
		try {
			this.deptService.delDept(id);
			resultBean.setSuccess(true);
			resultBean.setMessage("删除成功");
			return Response.ok(resultBean).build();
		} catch (IllegalStateException e) {
			resultBean.setSuccess(false);
			resultBean.setMessage(e.getMessage());
			return Response.ok(resultBean).build();
		} catch (Exception e) {
			e.printStackTrace();
			return WebApplicationException.build();
		}
	}

	public void setDeptService(DeptService deptService) {
		this.deptService = deptService;
	}

}
