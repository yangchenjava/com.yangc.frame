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
import org.apache.shiro.cache.Cache;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.apache.shiro.subject.Subject;

import com.yangc.system.bean.oracle.TSysUser;
import com.yangc.system.service.UserService;
import com.yangc.utils.ParamUtils;

public class MyRealm extends AuthorizingRealm {

	private UserService userService;

	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
		principals.getPrimaryPrincipal();
		return null;
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
				Subject currentUser = SecurityUtils.getSubject();
				currentUser.getSession().setAttribute(ParamUtils.LOGIN_USER, users.get(0));
				return new SimpleAuthenticationInfo(username, password, this.getName());
			}
		}
	}

	public void clearCachedAuthorizationInfo(String principal) {
		SimplePrincipalCollection principals = new SimplePrincipalCollection(principal, this.getName());
		this.clearCachedAuthorizationInfo(principals);
	}

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

}
