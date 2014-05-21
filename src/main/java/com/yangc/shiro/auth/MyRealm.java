package com.yangc.shiro.auth;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.SimplePrincipalCollection;

import com.yangc.system.bean.oracle.Permission;
import com.yangc.system.bean.oracle.TSysAcl;
import com.yangc.system.bean.oracle.TSysUser;
import com.yangc.system.service.AclService;
import com.yangc.system.service.UserService;
import com.yangc.utils.ParamUtils;

public class MyRealm extends AuthorizingRealm {

	private UserService userService;
	private AclService aclService;

	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
		// String username=(String) principals.getPrimaryPrincipal();
		TSysUser user = (TSysUser) SecurityUtils.getSubject().getSession().getAttribute(ParamUtils.LOGIN_USER);
		List<TSysAcl> aclList = this.aclService.getAclListByUserId(user.getId());

		SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
		if (aclList != null) {
			for (TSysAcl acl : aclList) {
				long operateStatus = acl.getOperateStatus();
				String menuAlias = acl.getMenuAlias();
				if (Permission.isPermission(operateStatus, Permission.SEL)) info.addStringPermission(menuAlias + ":" + Permission.SEL);
				if (Permission.isPermission(operateStatus, Permission.ADD)) info.addStringPermission(menuAlias + ":" + Permission.ADD);
				if (Permission.isPermission(operateStatus, Permission.UPD)) info.addStringPermission(menuAlias + ":" + Permission.UPD);
				if (Permission.isPermission(operateStatus, Permission.DEL)) info.addStringPermission(menuAlias + ":" + Permission.DEL);
			}
		}
		return info;
	}

	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authToken) throws AuthenticationException {
		UsernamePasswordToken token = (UsernamePasswordToken) authToken;
		String username = token.getUsername();
		String password = String.valueOf(token.getPassword());
		if (StringUtils.isBlank(username) || StringUtils.isBlank(password)) {
			throw new AuthenticationException("用户名或密码不能为空");
		} else {
			List<TSysUser> users = this.userService.getUserListByUsernameAndPassword(username, password);
			if (users == null || users.isEmpty()) {
				throw new AuthenticationException("用户不存在");
			} else if (users.size() > 1) {
				throw new AuthenticationException("用户重复");
			} else {
				SecurityUtils.getSubject().getSession().setAttribute(ParamUtils.LOGIN_USER, users.get(0));
				return new SimpleAuthenticationInfo(username, password, this.getName());
			}
		}
	}

	/**
	 * @功能: 清除用户权限缓存信息
	 * @作者: yangc
	 * @创建日期: 2014年5月21日 上午10:24:18
	 * @param principal username
	 */
	public void clearCachedAuthorizationInfo(Object principal) {
		if (principal != null) {
			SimplePrincipalCollection principals = new SimplePrincipalCollection(principal, this.getName());
			this.clearCachedAuthorizationInfo(principals);
		}
	}

	/**
	 * @功能: 清除所有权限缓存信息
	 * @作者: yangc
	 * @创建日期: 2014年5月21日 下午7:54:41
	 */
	public void clearAllCachedAuthorizationInfo() {
		Cache<Object, AuthorizationInfo> cache = this.getAuthorizationCache();
		if (cache != null) {
			for (Object key : cache.keys()) {
				cache.remove(key);
			}
		}
	}

	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	public void setAclService(AclService aclService) {
		this.aclService = aclService;
	}

}
