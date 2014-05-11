package com.yangc.system.resource;

import java.io.IOException;
import java.net.URI;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yangc.bean.ResultBean;
import com.yangc.exception.WebApplicationException;
import com.yangc.system.bean.oracle.TSysUser;
import com.yangc.system.service.UserService;
import com.yangc.utils.LoginUserUtils;
import com.yangc.utils.ParamUtils;

@Path("/user")
public class UserResource {

	public static final Logger logger = LoggerFactory.getLogger(UserResource.class);

	private UserService userService;

	/**
	 * @功能: 校验登录
	 * @作者: yangc
	 * @创建日期: 2012-9-10 上午12:04:21
	 * @return
	 */
	@POST
	@Path("login")
	@Produces(MediaType.APPLICATION_JSON)
	public Response login(@QueryParam("username") String username, @QueryParam("password") String password, @Context HttpServletRequest request) {
		logger.info("login - username=" + username + ", password=" + password);
		ResultBean resultBean = new ResultBean();
		try {
			if (StringUtils.isBlank(username) || StringUtils.isBlank(password)) {
				resultBean.setSuccess(false);
				resultBean.setMessage("用户名或密码不能为空");
			} else {
				List<TSysUser> users = this.userService.getUserListByUsernameAndPassword(username, password);
				if (users == null || users.isEmpty()) {
					resultBean.setSuccess(false);
					resultBean.setMessage("用户不存在");
				} else if (users.size() > 1) {
					resultBean.setSuccess(false);
					resultBean.setMessage("用户重复");
				} else {
					TSysUser user = users.get(0);
					HttpSession session = request.getSession();
					session.setAttribute(ParamUtils.LOGIN_USER, user);
					resultBean.setSuccess(true);
					resultBean.setMessage(ParamUtils.INDEX_PAGE);
				}
			}
			return Response.ok(resultBean).build();
		} catch (Exception e) {
			logger.error(e.getMessage());
			return WebApplicationException.build();
		}
	}

	/**
	 * @功能: 退出系统
	 * @作者: yangc
	 * @创建日期: 2012-9-10 上午12:04:33
	 * @return
	 */
	@POST
	@Path("logout")
	@Consumes({ MediaType.APPLICATION_FORM_URLENCODED })
	public Response logout(@Context HttpServletRequest request, @Context UriInfo uriInfo) {
		logger.info("logout");
		HttpSession session = request.getSession();
		session.removeAttribute(ParamUtils.LOGIN_USER);
		session.invalidate();
		URI uri = uriInfo.getBaseUriBuilder().path("/jsp/login.jsp").build();
		// 这种方式下的跳转采用的是GET方法
		// return Response.seeOther(uri).build();
		// 这个方法的跳转方式GET,POST等会延用进入该方法时的方法,如果是POST方法进入的那么跳转后的方法还是post
		return Response.temporaryRedirect(uri).build();
	}

	/**
	 * @功能: 修改密码
	 * @作者: yangc
	 * @创建日期: 2013年12月21日 下午4:24:12
	 * @return
	 * @throws IOException
	 */
	@POST
	@Path("changePassword")
	@Produces(MediaType.APPLICATION_JSON)
	public Response changePassword(@QueryParam("password") String password, @QueryParam("newPassword") String newPassword, @Context HttpServletRequest request) {
		ResultBean resultBean = new ResultBean();
		try {
			TSysUser user = LoginUserUtils.getLoginUser(request);
			logger.info("changePassword - userId=" + user.getId() + ", password=" + password + ", newPassword=" + newPassword);
			if (StringUtils.isBlank(password) || StringUtils.isBlank(newPassword)) {
				resultBean.setSuccess(false);
				resultBean.setMessage("原密码或新密码不能为空");
			} else {
				if (!user.getPassword().equals(password)) {
					resultBean.setSuccess(false);
					resultBean.setMessage("原密码输入错误");
				} else {
					this.userService.updPassword(user.getId(), newPassword);
					resultBean.setSuccess(true);
					resultBean.setMessage("修改成功");
				}
			}
			return Response.ok(resultBean).build();
		} catch (Exception e) {
			logger.error(e.getMessage());
			return WebApplicationException.build();
		}
	}

	public void setUserService(UserService userService) {
		this.userService = userService;
	}

}
