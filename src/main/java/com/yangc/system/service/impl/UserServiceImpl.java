package com.yangc.system.service.impl;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.yangc.dao.BaseDao;
import com.yangc.system.bean.TSysUser;
import com.yangc.system.service.UserService;
import com.yangc.system.service.UsersrolesService;
import com.yangc.utils.Constants;
import com.yangc.utils.lang.NumberUtils;

public class UserServiceImpl implements UserService {

	private BaseDao baseDao;
	private UsersrolesService usersrolesService;

	@Override
	public void addOrUpdateUser(Long userId, String username, Long personId, String roleIds) {
		TSysUser user = this.baseDao.get(TSysUser.class, userId);
		if (user == null) {
			user = new TSysUser();
			user.setPassword(Constants.DEFAULT_PASSWORD);
		}
		user.setUsername(username);
		user.setPersonId(personId);
		this.baseDao.saveOrUpdate(user);

		// 保存role
		userId = user.getId();
		this.usersrolesService.delUsersrolesByMainId(userId, 0);
		if (StringUtils.isNotBlank(roleIds)) {
			for (String roleId : roleIds.split(",")) {
				this.usersrolesService.addUsersroles(userId, NumberUtils.toLong(roleId));
			}
		}
	}

	@Override
	public void delUser(Long userId) {
		this.usersrolesService.delUsersrolesByMainId(userId, 0);
		this.baseDao.updateOrDelete("delete TSysUser where id = ?", new Object[] { userId });
	}

	@Override
	public void updatePassword(Long userId, String password) {
		this.baseDao.updateOrDelete("update TSysUser set password = ? where id = ?", new Object[] { password, userId });
	}

	@Override
	public TSysUser getUserByUsername(String username) {
		return this.baseDao.get("from TSysUser where username = ?", new Object[] { username });
	}

	@Override
	public TSysUser getUserByPersonId(Long personId) {
		return this.baseDao.get("from TSysUser where personId = ?", new Object[] { personId });
	}

	@Override
	public List<TSysUser> getUserListByUsernameAndPassword(String username, String password) {
		String hql = "select new TSysUser(u.id, u.username, u.password, p.name as personName) from TSysUser u, TSysPerson p where u.personId = p.id and username = ? and password = ?";
		return this.baseDao.findAll(hql, new Object[] { username, password });
	}

	public void setBaseDao(BaseDao baseDao) {
		this.baseDao = baseDao;
	}

	public void setUsersrolesService(UsersrolesService usersrolesService) {
		this.usersrolesService = usersrolesService;
	}

}
