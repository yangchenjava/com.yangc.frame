package com.yangc.system.service.impl;

import java.util.List;

import com.yangc.dao.BaseDao;
import com.yangc.system.bean.oracle.TSysRole;
import com.yangc.system.service.AclService;
import com.yangc.system.service.RoleService;
import com.yangc.system.service.UsersrolesService;

@SuppressWarnings("unchecked")
public class RoleServiceImpl implements RoleService {

	private BaseDao baseDao;
	private UsersrolesService usersrolesService;
	private AclService aclService;

	@Override
	public void addOrUpdateRole(Long roleId, String roleName) {
		TSysRole role = roleId == null ? new TSysRole() : (TSysRole) this.baseDao.get(TSysRole.class, roleId);
		role.setRoleName(roleName);
		this.baseDao.saveOrUpdate(role);
	}

	@Override
	public void delRole(Long roleId) {
		this.usersrolesService.delUsersrolesByMainId(roleId, 1);
		this.aclService.delAcl(roleId, 0);
		this.baseDao.updateOrDelete("delete TSysRole where id = ?", new Object[] { roleId });
	}

	@Override
	public List<TSysRole> getRoleList() {
		return this.baseDao.findAll("from TSysRole order by id", null);
	}

	@Override
	public List<TSysRole> getRoleList_page() {
		return this.baseDao.find("from TSysRole order by id", null);
	}

	public void setBaseDao(BaseDao baseDao) {
		this.baseDao = baseDao;
	}

	public void setUsersrolesService(UsersrolesService usersrolesService) {
		this.usersrolesService = usersrolesService;
	}

	public void setAclService(AclService aclService) {
		this.aclService = aclService;
	}

}
