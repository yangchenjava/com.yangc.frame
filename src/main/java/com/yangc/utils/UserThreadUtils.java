package com.yangc.utils;

import com.yangc.system.bean.oracle.TSysUser;

public class UserThreadUtils {

	private static final ThreadLocal<TSysUser> user = new ThreadLocal<TSysUser>();

	public static TSysUser get() {
		return user.get();
	}

	public static void set(TSysUser value) {
		user.set(value);
	}

	public static void clear() {
		user.remove();
	}

}
