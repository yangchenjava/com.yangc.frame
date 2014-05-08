package com.yangc.system.bean.oracle;

import com.yangc.bean.BaseBean;

public class TSysPerson extends BaseBean {

	private static final long serialVersionUID = 1086104255350281027L;

	private String name;
	private Long sex;
	private String phone;
	private Long deptId;
	private String deptName;
	private String spell;

	private Long userId;
	private String username;
	private String password;

	private String roleIds;

	public TSysPerson() {
	}

	public TSysPerson(String name, String spell) {
		this.name = name;
		this.spell = spell;
	}

	public TSysPerson(Long id, String name, Long sex, String phone, Long deptId, String deptName, String spell, Long userId, String username, String password) {
		this.setId(id);
		this.name = name;
		this.sex = sex;
		this.phone = phone;
		this.deptId = deptId;
		this.deptName = deptName;
		this.spell = spell;
		this.userId = userId;
		this.username = username;
		this.password = password;
	}

	@Override
	public String toString() {
		return "TSysPerson [name=" + name + ", sex=" + sex + ", phone=" + phone + ", deptId=" + deptId + ", deptName=" + deptName + ", spell=" + spell + ", userId=" + userId + ", username="
				+ username + ", password=" + password + ", roleIds=" + roleIds + "]";
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Long getSex() {
		return sex;
	}

	public void setSex(Long sex) {
		this.sex = sex;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public Long getDeptId() {
		return deptId;
	}

	public void setDeptId(Long deptId) {
		this.deptId = deptId;
	}

	public String getDeptName() {
		return deptName;
	}

	public void setDeptName(String deptName) {
		this.deptName = deptName;
	}

	public String getSpell() {
		return spell;
	}

	public void setSpell(String spell) {
		this.spell = spell;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getRoleIds() {
		return roleIds;
	}

	public void setRoleIds(String roleIds) {
		this.roleIds = roleIds;
	}

}