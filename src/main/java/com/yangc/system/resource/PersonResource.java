package com.yangc.system.resource;

import java.net.URLDecoder;
import java.util.List;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.yangc.bean.DataGridBean;
import com.yangc.bean.ResultBean;
import com.yangc.exception.WebApplicationException;
import com.yangc.system.bean.oracle.TSysPerson;
import com.yangc.system.bean.oracle.TSysUsersroles;
import com.yangc.system.service.PersonService;
import com.yangc.system.service.UsersrolesService;

@Path("/person")
public class PersonResource {

	public static final Logger logger = Logger.getLogger(PersonResource.class);

	private PersonService personService;
	private UsersrolesService usersrolesService;

	/**
	 * @功能: 查询所有用户(自动完成)
	 * @作者: yangc
	 * @创建日期: 2013年12月23日 下午5:30:25
	 * @return
	 */
	@POST
	@Path("getPersonList")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getPersonList() {
		logger.info("getPersonList");
		try {
			List<TSysPerson> personList = this.personService.getPersonList();
			return Response.ok(personList).build();
		} catch (Exception e) {
			logger.error(e.getMessage());
			return WebApplicationException.build();
		}
	}

	/**
	 * @功能: 查询所有用户(分页)
	 * @作者: yangc
	 * @创建日期: 2013年12月23日 下午5:30:25
	 * @return
	 */
	@POST
	@Path("getPersonListByPersonNameAndDeptId_page")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getPersonListByPersonNameAndDeptId_page(@FormParam("name") String name, @FormParam("deptId") Long deptId) {
		try {
			if (StringUtils.isNotBlank(name)) {
				name = URLDecoder.decode(name, "UTF-8");
			}
			logger.info("getPersonListByPersonNameAndDeptId_page - name=" + name + ", deptId=" + deptId);
			List<TSysPerson> personList = this.personService.getPersonListByPersonNameAndDeptId_page(name, deptId);
			DataGridBean dataGridBean = new DataGridBean();
			dataGridBean.setDataGrid(personList);
			return Response.ok(dataGridBean).build();
		} catch (Exception e) {
			logger.error(e.getMessage());
			return WebApplicationException.build();
		}
	}

	/**
	 * @功能: 根据userId获取roleIds
	 * @作者: yangc
	 * @创建日期: 2013年12月24日 上午10:49:47
	 * @return
	 */
	@POST
	@Path("getRoleIdsByUserId")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getRoleIdsByUserId(@FormParam("userId") Long userId) {
		logger.info("getRoleIdsByUserId - userId=" + userId);
		try {
			TSysPerson person = new TSysPerson();
			List<TSysUsersroles> usersrolesList = this.usersrolesService.getUsersrolesListByUserId(userId);
			if (usersrolesList == null || usersrolesList.isEmpty()) {
				person.setRoleIds("");
			} else {
				StringBuilder sb = new StringBuilder();
				for (TSysUsersroles usersroles : usersrolesList) {
					sb.append(usersroles.getRoleId()).append(",");
				}
				person.setRoleIds(sb.substring(0, sb.length() - 1));
			}
			return Response.ok(person).build();
		} catch (Exception e) {
			logger.error(e.getMessage());
			return WebApplicationException.build();
		}
	}

	/**
	 * @功能: 添加或修改用户
	 * @作者: yangc
	 * @创建日期: 2013年12月23日 下午5:49:16
	 * @return
	 */
	@POST
	@Path("addOrUpdatePerson")
	@Produces(MediaType.APPLICATION_JSON)
	public Response addOrUpdatePerson(@FormParam("id") Long id, @FormParam("name") String name, @FormParam("sex") Long sex, @FormParam("phone") String phone, @FormParam("deptId") Long deptId,
			@FormParam("userId") Long userId, @FormParam("username") String username, @FormParam("password") String password, @FormParam("roleIds") String roleIds) {
		logger.info("addOrUpdatePerson - id=" + id + ", name=" + name + ", sex=" + sex + ", phone=" + phone + ", deptId=" + deptId + ", userId=" + userId + ", username=" + username + ", password="
				+ password + ", roleIds=" + roleIds);
		ResultBean resultBean = new ResultBean();
		try {
			resultBean.setSuccess(true);
			if (id == null) {
				resultBean.setMessage("添加成功");
			} else {
				resultBean.setMessage("修改成功");
			}
			this.personService.addOrUpdatePerson(id, name, sex, phone, deptId, userId, username, password, roleIds);
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

	/**
	 * @功能: 删除用户
	 * @作者: yangc
	 * @创建日期: 2013年12月23日 下午7:00:20
	 * @return
	 */
	@POST
	@Path("delPerson")
	@Produces(MediaType.APPLICATION_JSON)
	public Response delPerson(@FormParam("id") Long id) {
		try {
			logger.info("delPerson - id=" + id);
			this.personService.delPerson(id);
			return Response.ok(new ResultBean(true, "删除成功")).build();
		} catch (Exception e) {
			logger.error(e.getMessage());
			return WebApplicationException.build();
		}
	}

	public void setPersonService(PersonService personService) {
		this.personService = personService;
	}

	public void setUsersrolesService(UsersrolesService usersrolesService) {
		this.usersrolesService = usersrolesService;
	}

}
