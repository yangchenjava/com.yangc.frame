package com.yangc.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

public class FileUtils {

	/**
	 * @功能: 获取目录
	 * @作者: yangc
	 * @创建日期: 2012-12-14 下午02:00:16
	 */
	public static File getDir(String dirPath) {
		File dir = new File(dirPath);
		if (!dir.exists()) {
			dir.mkdirs();
		} else if (!dir.isDirectory()) {
			dir.delete();
			dir.mkdirs();
		}
		return dir;
	}

	/**
	 * @功能: 获取文件
	 * @作者: yangc
	 * @创建日期: 2013-1-16 下午12:19:12
	 */
	public static File getFile(String filePath) {
		File file = new File(filePath);
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else if (!file.isFile()) {
			file.delete();
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return file;
	}

	/**
	 * @功能: 获取目录下的所有子文件
	 * @作者: yangc
	 * @创建日期: 2012-12-14 下午02:00:37
	 */
	public static File[] getFiles(String dirPath) {
		File dir = getDir(dirPath);
		return dir.listFiles();
	}

	/**
	 * @功能: 获取目录下的所有子文件的名称
	 * @作者: yangc
	 * @创建日期: 2013-2-2 下午02:03:15
	 */
	public static String[] getFileNames(String dirPath) {
		File dir = getDir(dirPath);
		return dir.list();
	}

	/**
	 * @功能: 删除指定文件
	 * @作者: yangc
	 * @创建日期: 2012-12-14 下午02:01:05
	 */
	public static boolean deleteFile(String filePath) {
		File file = new File(filePath);
		if (file.exists() && file.canRead()) {
			return file.delete();
		}
		return false;
	}

	/**
	 * @功能: 读文件
	 * @作者: yangc
	 * @创建日期: 2013-1-16 下午12:18:58
	 */
	public static String readFile(String filePath) {
		File file = getFile(filePath);
		StringBuilder sb = new StringBuilder();
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
			String str = null;
			while ((str = br.readLine()) != null) {
				sb.append(str.trim());
			}
			br.close();
			br = null;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null) br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return sb.toString();
	}

	/**
	 * @功能: 写文件
	 * @作者: yangc
	 * @创建日期: 2013-1-16 下午12:18:38
	 */
	public static void writeFile(String filePath, String content, boolean append) {
		File file = getFile(filePath);
		PrintWriter pw = null;
		try {
			pw = new PrintWriter(new FileOutputStream(file, append));
			pw.println(content);
			pw.flush();
			pw.close();
			pw = null;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			if (pw != null) pw.close();
		}
	}

}
