package com.yangc.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 资源文件扫描器
 * @author yangc
 */
public final class PropertiesFileScanUtils {

	private static Logger logger = LogManager.getLogger(PropertiesFileScanUtils.class);

	private static final Map<String, String> properties = new HashMap<String, String>();

	/* 初始化 */
	static {
		try {
			logger.info("==========开始搜索满足条件的属性文件=========");
			init(Constants.CLASSPATH);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 扫描属性文件
	 * @throws IOException
	 */
	private static void init(String filePath) throws IOException {
		/* 从类路径开始扫描 */
		File file = new File(filePath);
		if (file.isDirectory()) {
			File[] files = file.listFiles();
			for (File f : files) {
				if (!f.isDirectory() && f.getName().endsWith("-message.properties")) {
					logger.info("搜索到属性文件:" + f.getAbsolutePath());
					Properties temp = new Properties();
					temp.load(new FileInputStream(f));
					Set<Object> keys = temp.keySet();
					for (Iterator<Object> it = keys.iterator(); it.hasNext();) {
						String key = (String) it.next();
						if (properties.containsKey(key)) {
							logger.error("属性文件:" + f.getAbsolutePath() + "定义的键(" + key + ")重复");
							throw new IOException("属性文件:" + f.getAbsolutePath() + "定义的键(" + key + ")重复");
						} else {
							properties.put(key, temp.getProperty(key));
						}
					}
				} else if (f.isDirectory()) {
					/* 如果是目录的话递归 */
					init(f.getAbsolutePath());
				} else {
					continue;
				}
			}
		}
	}

	public static String getMessage(String key) {
		return properties.get(key);
	}

}
