package com.yangc.system.service.impl;

import java.util.List;

import com.yangc.dao.BaseDao;
import com.yangc.system.bean.oracle.TSysUsersroles;
import com.yangc.system.service.UsersrolesService;

@SuppressWarnings("unchecked")
public class UsersrolesServiceImpl implements UsersrolesService {

	private BaseDao baseDao;

	@Override
	public void addUsersroles(Long userId, Long roleId) {
		TSysUsersroles ur = new TSysUsersroles();
		ur.setUserId(userId);
		ur.setRoleId(roleId);
		this.baseDao.save(ur);
	}

	@Override
	public void delUsersroles(Long userId, Long roleId) {
		this.baseDao.updateOrDelete("delete TSysUsersroles where userId = ? and roleId = ?", new Object[] { userId, roleId });
	}

	@Override
	public void delUsersrolesByMainId(Long mainId, int userOrRole) {
		String hql = userOrRole == 0 ? "delete TSysUsersroles where userId = ?" : "delete TSysUsersroles where roleId = ?";
		this.baseDao.updateOrDelete(hql, new Object[] { mainId });
	}

	@Override
	public List<TSysUsersroles> getUsersrolesListByUserId(Long userId) {
		return this.baseDao.findAll("from TSysUsersroles where userId = ?", new Object[] { userId });
	}

	public void setBaseDao(BaseDao baseDao) {
		this.baseDao = baseDao;
	}

}
