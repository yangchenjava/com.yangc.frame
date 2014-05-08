package com.yangc.common;

public class DaoThreadUtil {

	public static ThreadLocal<Pagination> pagination = new ThreadLocal<Pagination>();

	/**
	 * @功能: 清空线程对象
	 * @作者: yangc
	 * @创建日期: 2013-7-18 14:19:43
	 */
	public static void clear() {
		pagination.remove();
	}

}
