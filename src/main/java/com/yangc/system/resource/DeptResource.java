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
import com.yangc.system.bean.oracle.TSysDepartment;
import com.yangc.system.service.DeptService;

@Path("/dept")
public class DeptResource {

	public static final Logger logger = Logger.getLogger(DeptResource.class);

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
	public Response getDeptList() {
		logger.info("getDeptList");
		try {
			List<TSysDepartment> deptList = this.deptService.getDeptList();
			return Response.ok(deptList).build();
		} catch (Exception e) {
			logger.error(e.getMessage());
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
	public Response getDeptList_page() {
		logger.info("getDeptList_page");
		try {
			List<TSysDepartment> deptList = this.deptService.getDeptList_page();
			DataGridBean dataGridBean = new DataGridBean();
			dataGridBean.setDataGrid(deptList);
			return Response.ok(dataGridBean).build();
		} catch (Exception e) {
			logger.error(e.getMessage());
			return WebApplicationException.build();
		}
	}

	/**
	 * @功能: 添加或修改部门
	 * @作者: yangc
	 * @创建日期: 2013年12月23日 下午2:59:26
	 * @return
	 */
	@POST
	@Path("addOrUpdateDept")
	@Produces(MediaType.APPLICATION_JSON)
	public Response addOrUpdateDept(@FormParam("id") Long id, @FormParam("deptName") String deptName, @FormParam("serialNum") Long serialNum) {
		logger.info("addOrUpdateDept - id=" + id + ", deptName=" + deptName + ", serialNum=" + serialNum);
		ResultBean resultBean = new ResultBean();
		try {
			resultBean.setSuccess(true);
			if (id == null) {
				resultBean.setMessage("添加成功");
			} else {
				resultBean.setMessage("修改成功");
			}
			this.deptService.addOrUpdateDept(id, deptName, serialNum);
			return Response.ok(resultBean).build();
		} catch (Exception e) {
			logger.error(e.getMessage());
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
			logger.error(e.getMessage());
			return WebApplicationException.build();
		}
	}

	public void setDeptService(DeptService deptService) {
		this.deptService = deptService;
	}

}
