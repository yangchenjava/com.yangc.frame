package com.yangc.system.resource;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.util.Date;

import javax.imageio.ImageIO;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;

import com.yangc.bean.ResultBean;
import com.yangc.exception.WebApplicationException;
import com.yangc.shiro.utils.ShiroUtils;
import com.yangc.system.bean.oracle.TSysUser;
import com.yangc.system.service.UserService;
import com.yangc.utils.Constants;
import com.yangc.utils.Message;
import com.yangc.utils.lang.CaptchaUtils;
import com.yangc.utils.lang.CaptchaUtils.CAPTCHA_TYPE;

@Path("/user")
public class UserResource {

	public static final Logger logger = Logger.getLogger(UserResource.class);

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
	public Response login(@FormParam("username") String username, @FormParam("password") String password) {
		logger.info("login - username=" + username + ", password=" + password);
		ResultBean resultBean = new ResultBean();
		Subject subject = null;
		Session session = null;
		try {
			subject = SecurityUtils.getSubject();
			session = subject.getSession();
			subject.login(new UsernamePasswordToken(username, password));
			session.removeAttribute(Constants.ENTER_COUNT);
			resultBean.setSuccess(true);
			resultBean.setMessage(Constants.INDEX_PAGE);
			return Response.ok(resultBean).build();
		} catch (AuthenticationException e) {
			resultBean.setSuccess(false);
			resultBean.setMessage(e.getMessage());
			resultBean.setOther((Integer) session.getAttribute(Constants.ENTER_COUNT) > Integer.parseInt(Message.getMessage("shiro.captcha")) ? "captcha" : "");
			return Response.ok(resultBean).build();
		} catch (Exception e) {
			e.printStackTrace();
			return WebApplicationException.build();
		}
	}

	/**
	 * @功能: 退出系统
	 * @作者: yangc
	 * @创建日期: 2012-9-10 上午12:04:33
	 * @return
	 */
	@GET
	@Path("logout")
	@Produces({ MediaType.APPLICATION_FORM_URLENCODED })
	public Response logout(@Context UriInfo uriInfo) {
		logger.info("logout");
		// session会销毁,在SessionListener监听session销毁,清理权限缓存
		SecurityUtils.getSubject().logout();
		URI uri = uriInfo.getBaseUriBuilder().path(Constants.LOGIN_PAGE).build();
		// 这个方法的跳转方式GET,POST等会延用进入该方法时的方法,如果是POST方法进入的那么跳转后的方法还是post
		// return Response.temporaryRedirect(uri).build();
		// 这种方式下的跳转采用的是GET方法
		return Response.seeOther(uri).build();
	}

	@GET
	@Path("captcha")
	@Produces({ "image/jpeg" })
	public Response captcha() {
		logger.info("logout");
		String code = CaptchaUtils.getCode(4, CAPTCHA_TYPE.ALL);
		BufferedImage bi = CaptchaUtils.getBufferedImage(100, 28, 4, CAPTCHA_TYPE.ALL, code);

		SecurityUtils.getSubject().getSession().setAttribute(CaptchaUtils.CAPTCHA, code);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			ImageIO.write(bi, "jpg", baos);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return Response.ok(baos.toByteArray()).header("Pragma", "no-cache").header("Cache-Control", "no-cache").expires(new Date(0)).build();
	}

	/**
	 * @功能: 修改密码
	 * @作者: yangc
	 * @创建日期: 2013年12月21日 下午4:24:12
	 * @return
	 */
	@POST
	@Path("changePassword")
	@Produces(MediaType.APPLICATION_JSON)
	public Response changePassword(@FormParam("password") String password, @FormParam("newPassword") String newPassword) {
		ResultBean resultBean = new ResultBean();
		try {
			TSysUser user = ShiroUtils.getCurrentUser();
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
			e.printStackTrace();
			return WebApplicationException.build();
		}
	}

	public void setUserService(UserService userService) {
		this.userService = userService;
	}

}
