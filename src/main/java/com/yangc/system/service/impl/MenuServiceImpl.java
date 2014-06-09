package com.yangc.system.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.gson.reflect.TypeToken;
import com.yangc.dao.BaseDao;
import com.yangc.dao.JdbcDao;
import com.yangc.system.bean.oracle.MenuTree;
import com.yangc.system.bean.oracle.TSysMenu;
import com.yangc.system.service.AclService;
import com.yangc.system.service.MenuService;
import com.yangc.utils.Constants;
import com.yangc.utils.cache.RedisUtils;

public class MenuServiceImpl implements MenuService {

	private BaseDao baseDao;
	private JdbcDao jdbcDao;
	private AclService aclService;

	@Override
	public void addOrUpdateMenu(Long menuId, String menuName, String menuUrl, Long parentMenuId, Long serialNum, Long isshow, String description) {
		TSysMenu menu = menuId == null ? new TSysMenu() : (TSysMenu) this.baseDao.get(TSysMenu.class, menuId);
		menu.setMenuName(menuName);
		menu.setMenuUrl(menuUrl);
		menu.setParentMenuId(parentMenuId);
		menu.setSerialNum(serialNum);
		menu.setIsshow(isshow);
		menu.setDescription(description);
		this.baseDao.saveOrUpdate(menu);

		// 清空菜单缓存
		this.clearMenuCache();
	}

	@Override
	public void updParentMenuId(Long menuId, Long parentMenuId) {
		this.baseDao.updateOrDelete("update TSysMenu set parentMenuId = ? where id = ?", new Object[] { parentMenuId, menuId });

		// 清空菜单缓存
		this.clearMenuCache();
	}

	@Override
	public void delMenu(Long menuId) throws IllegalStateException {
		int totalCount = this.baseDao.getCount("select count(m) from TSysMenu m where m.parentMenuId = ?", new Object[] { menuId });
		if (totalCount > 0) {
			throw new IllegalStateException("该节点下存在子节点");
		}
		this.aclService.delAcl(menuId, 1);
		this.baseDao.updateOrDelete("delete TSysMenu where id = ?", new Object[] { menuId });

		// 清空菜单缓存
		this.clearMenuCache();
	}

	@Override
	public int getNodePosition(Long menuId) {
		String sql = JdbcDao.SQL_MAPPING.get("system.menu.getNodePosition");
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("menuId", menuId);
		return this.jdbcDao.getCount(sql, paramMap);
	}

	@Override
	public List<MenuTree> getMenuListByParentMenuId(Long parentMenuId) {
		String sql = JdbcDao.SQL_MAPPING.get("system.menu.getMenusByParentId");
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("parentMenuId", parentMenuId);
		List<Map<String, Object>> mapList = this.jdbcDao.findAll(sql, paramMap);
		if (null == mapList || mapList.isEmpty()) return null;

		List<MenuTree> menuTreeList = new ArrayList<MenuTree>();
		for (Map<String, Object> map : mapList) {
			Long menuId = ((Number) map.get("ID")).longValue();
			String menuName = (String) map.get("MENU_NAME");
			String menuUrl = (String) map.get("MENU_URL");
			Long serialNum = ((Number) map.get("SERIAL_NUM")).longValue();
			Long isshow = ((Number) map.get("ISSHOW")).longValue();
			String description = (String) map.get("DESCRIPTION");
			long totalCount = ((Number) map.get("TOTALCOUNT")).longValue();

			MenuTree menuTree = new MenuTree();
			menuTree.setLeaf(totalCount == 0);
			menuTree.setMenuId(menuId);
			menuTree.setMenuName(menuName);
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
		// 查询redis缓存
		RedisUtils cache = RedisUtils.getInstance();
		String key = Constants.MENU_TOP + "_" + parentMenuId + "_" + userId;
		List<TSysMenu> menus = cache.get(key, new TypeToken<List<TSysMenu>>() {
		});
		if (menus != null && !menus.isEmpty()) {
			return menus;
		}

		// 查询数据库
		String sql = JdbcDao.SQL_MAPPING.get("system.menu.getTopFrame");
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("parentMenuId", parentMenuId);
		paramMap.put("userId", userId);
		List<Map<String, Object>> mapList = this.jdbcDao.findAll(sql, paramMap);
		if (null == mapList || mapList.isEmpty()) return null;

		menus = new ArrayList<TSysMenu>();
		for (Map<String, Object> map : mapList) {
			TSysMenu menu = new TSysMenu();
			menu.setId(((Number) map.get("ID")).longValue());
			menu.setMenuName((String) map.get("MENU_NAME"));
			menu.setMenuUrl((String) map.get("MENU_URL"));
			menus.add(menu);
		}

		// 设置redis缓存
		cache.set(key, menus);

		return menus;
	}

	@Override
	public List<TSysMenu> getMainFrame(Long parentMenuId, Long userId) {
		// 查询redis缓存
		RedisUtils cache = RedisUtils.getInstance();
		String key = Constants.MENU_MAIN + "_" + parentMenuId + "_" + userId;
		List<TSysMenu> menus = cache.get(key, new TypeToken<List<TSysMenu>>() {
		});
		if (menus != null && !menus.isEmpty()) {
			return menus;
		}

		// 查询数据库
		String sql = JdbcDao.SQL_MAPPING.get("system.menu.getMainFrame");
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("parentMenuId", parentMenuId);
		paramMap.put("userId", userId);
		List<Map<String, Object>> mapList = this.jdbcDao.findAll(sql, paramMap);
		if (null == mapList || mapList.isEmpty()) return null;

		Map<Long, Map<TSysMenu, List<TSysMenu>>> tempMap = new LinkedHashMap<Long, Map<TSysMenu, List<TSysMenu>>>();
		for (int i = 1, size = mapList.size(); i < size; i++) {
			Map<String, Object> map = mapList.get(i);
			Long id = ((Number) map.get("ID")).longValue();
			String menuName = (String) map.get("MENU_NAME");
			Long pid = ((Number) map.get("PARENT_MENU_ID")).longValue();

			if (pid == parentMenuId) {
				Map<TSysMenu, List<TSysMenu>> value = new HashMap<TSysMenu, List<TSysMenu>>();
				TSysMenu menu = new TSysMenu();
				menu.setId(id);
				menu.setMenuName(menuName);
				value.put(menu, new ArrayList<TSysMenu>());
				tempMap.put(id, value);
			} else {
				Map<TSysMenu, List<TSysMenu>> value = tempMap.get(pid);
				if (null == value || value.isEmpty()) continue;
				TSysMenu menu = new TSysMenu();
				menu.setId(id);
				menu.setMenuName(menuName);
				menu.setMenuUrl((String) map.get("MENU_URL"));
				menu.setParentMenuId(pid);
				value.entrySet().iterator().next().getValue().add(menu);
			}
		}

		menus = new ArrayList<TSysMenu>();
		for (Entry<Long, Map<TSysMenu, List<TSysMenu>>> entry : tempMap.entrySet()) {
			Entry<TSysMenu, List<TSysMenu>> en = entry.getValue().entrySet().iterator().next();
			TSysMenu menu = en.getKey();
			menu.setChildRenMenu(en.getValue());
			menus.add(menu);
		}

		// 设置redis缓存
		cache.set(key, menus);

		return menus;
	}

	/**
	 * @功能: 清空菜单缓存
	 * @作者: yangc
	 * @创建日期: 2014年6月9日 下午3:44:02
	 */
	private void clearMenuCache() {
		RedisUtils cache = RedisUtils.getInstance();
		String[] menuTopKeys = cache.keys(Constants.MENU_TOP + "*").toArray(new String[] {});
		String[] menuMainKeys = cache.keys(Constants.MENU_MAIN + "*").toArray(new String[] {});
		String[] keys = new String[menuTopKeys.length + menuMainKeys.length];
		System.arraycopy(menuTopKeys, 0, keys, 0, menuTopKeys.length);
		System.arraycopy(menuMainKeys, 0, keys, menuTopKeys.length, menuMainKeys.length);
		cache.del(keys);
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
