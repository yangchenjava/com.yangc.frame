package com.yangc.system.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.collections4.MapUtils;

import com.yangc.dao.BaseDao;
import com.yangc.dao.JdbcDao;
import com.yangc.system.bean.MenuTree;
import com.yangc.system.bean.TSysMenu;
import com.yangc.system.service.AclService;
import com.yangc.system.service.MenuService;

public class MenuServiceImpl implements MenuService {

	private BaseDao baseDao;
	private JdbcDao jdbcDao;
	private AclService aclService;

	@Override
	public void addOrUpdateMenu(Long menuId, String menuName, String menuAlias, String menuUrl, Long parentMenuId, Long serialNum, Long isshow, String description) {
		TSysMenu menu = this.baseDao.get(TSysMenu.class, menuId);
		if (menu == null) {
			menu = new TSysMenu();
		}
		menu.setMenuName(menuName);
		menu.setMenuAlias(menuAlias);
		menu.setMenuUrl(menuUrl);
		menu.setParentMenuId(parentMenuId);
		menu.setSerialNum(serialNum);
		menu.setIsshow(isshow);
		menu.setDescription(description);
		this.baseDao.saveOrUpdate(menu);
	}

	@Override
	public void updateParentMenuId(Long menuId, Long parentMenuId) {
		this.baseDao.updateOrDelete("update TSysMenu set parentMenuId = ? where id = ?", new Object[] { parentMenuId, menuId });
	}

	@Override
	public void delMenu(Long menuId) throws IllegalStateException {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("menuId", menuId);
		int totalCount = this.baseDao.getCount("select count(m) from TSysMenu m where m.parentMenuId = :menuId", paramMap);
		if (totalCount > 0) {
			throw new IllegalStateException("该节点下存在子节点");
		}
		this.aclService.delAcl(menuId, 1);
		this.baseDao.updateOrDelete("delete TSysMenu where id = ?", new Object[] { menuId });
	}

	@Override
	public int getNodePosition(Long menuId) {
		String sql = JdbcDao.SQL_MAPPING.get("system.menu.getNodePosition");
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("menuId", menuId);
		return this.jdbcDao.getCount(sql, paramMap);
	}

	@Override
	public List<MenuTree> getMenuTreeListByParentMenuId(Long parentMenuId) {
		String sql = JdbcDao.SQL_MAPPING.get("system.menu.getMenuTreeListByParentMenuId");
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("parentMenuId", parentMenuId);
		List<Map<String, Object>> mapList = this.jdbcDao.findAll(sql, paramMap);
		if (mapList == null || mapList.isEmpty()) return null;

		List<MenuTree> menuTreeList = new ArrayList<MenuTree>();
		for (Map<String, Object> map : mapList) {
			Long menuId = ((Number) map.get("ID")).longValue();
			String menuName = (String) map.get("MENU_NAME");
			String menuAlias = (String) map.get("MENU_ALIAS");
			String menuUrl = (String) map.get("MENU_URL");
			Long serialNum = ((Number) map.get("SERIAL_NUM")).longValue();
			Long isshow = ((Number) map.get("ISSHOW")).longValue();
			String description = (String) map.get("DESCRIPTION");
			long totalCount = ((Number) map.get("TOTALCOUNT")).longValue();

			MenuTree menuTree = new MenuTree();
			menuTree.setLeaf(totalCount == 0);
			menuTree.setMenuId(menuId);
			menuTree.setMenuName(menuName);
			menuTree.setMenuAlias(menuAlias);
			menuTree.setMenuUrl(menuUrl);
			menuTree.setParentMenuId(parentMenuId);
			menuTree.setSerialNum(serialNum);
			menuTree.setIsshow(isshow);
			menuTree.setDescription(description);
			menuTreeList.add(menuTree);
		}
		return menuTreeList;
	}

	@Override
	public List<TSysMenu> getTopFrame(Long parentMenuId, Long userId) {
		String sql = JdbcDao.SQL_MAPPING.get("system.menu.getTopFrame");
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("parentMenuId", parentMenuId);
		paramMap.put("userId", userId);
		List<Map<String, Object>> mapList = this.jdbcDao.findAll(sql, paramMap);
		if (mapList == null || mapList.isEmpty()) return null;

		List<TSysMenu> menus = new ArrayList<TSysMenu>();
		for (Map<String, Object> map : mapList) {
			TSysMenu menu = new TSysMenu();
			menu.setId(((Number) map.get("ID")).longValue());
			menu.setMenuName((String) map.get("MENU_NAME"));
			menu.setMenuUrl((String) map.get("MENU_URL"));
			menus.add(menu);
		}
		return menus;
	}

	@Override
	public List<TSysMenu> getMainFrame(Long parentMenuId, Long userId) {
		String sql = JdbcDao.SQL_MAPPING.get("system.menu.getMainFrame");
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("parentMenuId", parentMenuId);
		paramMap.put("userId", userId);
		List<Map<String, Object>> mapList = this.jdbcDao.findAll(sql, paramMap);
		if (mapList == null || mapList.isEmpty()) return null;

		Map<Long, TSysMenu> tempMap = new LinkedHashMap<Long, TSysMenu>();
		for (Map<String, Object> map : mapList) {
			Long id = MapUtils.getLong(map, "ID");
			String menuName = MapUtils.getString(map, "MENU_NAME");
			Long pid = MapUtils.getLong(map, "PARENT_MENU_ID");

			if (pid.longValue() == parentMenuId.longValue()) {
				TSysMenu menu = new TSysMenu();
				menu.setId(id);
				menu.setMenuName(menuName);
				menu.setParentMenuId(pid);
				menu.setChildRenMenu(new ArrayList<TSysMenu>());
				tempMap.put(id, menu);
			} else {
				TSysMenu parentMenu = tempMap.get(pid);
				if (parentMenu == null) continue;
				TSysMenu menu = new TSysMenu();
				menu.setId(id);
				menu.setMenuName(menuName);
				menu.setMenuAlias(MapUtils.getString(map, "MENU_ALIAS"));
				menu.setMenuUrl(MapUtils.getString(map, "MENU_URL"));
				menu.setParentMenuId(pid);

				List<TSysMenu> childRenMenu = parentMenu.getChildRenMenu();
				childRenMenu.add(menu);
				parentMenu.setChildRenMenu(childRenMenu);
			}
		}

		List<TSysMenu> menus = new ArrayList<TSysMenu>();
		for (Entry<Long, TSysMenu> entry : tempMap.entrySet()) {
			menus.add(entry.getValue());
		}
		return menus;
	}

	public void setBaseDao(BaseDao baseDao) {
		this.baseDao = baseDao;
	}

	public void setJdbcDao(JdbcDao jdbcDao) {
		this.jdbcDao = jdbcDao;
	}

	public void setAclService(AclService aclService) {
		this.aclService = aclService;
	}

}
