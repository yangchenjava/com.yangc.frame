package com.yangc.dao.impl;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.yangc.common.Pagination;
import com.yangc.common.PaginationThreadUtils;
import com.yangc.dao.JdbcDao;
import com.yangc.utils.Constants;

public class JdbcDaoImpl implements JdbcDao {

	private NamedParameterJdbcTemplate npJdbcTemplate;
	private JdbcTemplate jdbcTemplate;

	public JdbcDaoImpl(NamedParameterJdbcTemplate namedParameterJdbcTemplate, JdbcTemplate jdbcTemplate) {
		this.npJdbcTemplate = namedParameterJdbcTemplate;
		this.jdbcTemplate = jdbcTemplate;

		// 加载sql内容
		List<File> fileList = this.getFileList(Constants.CLASSPATH + "config/multi/jdbc/");
		for (File file : fileList) {
			this.loadFileContents(file);
		}
	}

	/**
	 * 指定目录下探测符合命名规范的文件信息集合
	 */
	private List<File> getFileList(String fileDir) {
		List<File> fileList = new ArrayList<File>();
		File dir = new File(fileDir);
		if (dir.exists()) {
			File[] files = dir.listFiles();
			if (files == null || files.length == 0) {
				return fileList;
			}
			for (File file : files) {
				// 判断是否符合命名规范
				if (file.isFile() && file.getName().endsWith(Constants.DB_NAME + "-sql.xml")) {
					fileList.add(file);
				} else if (file.isDirectory()) {
					fileList.addAll(this.getFileList(file.getPath()));
				}
			}
		}
		return fileList;
	}

	/**
	 * 加载结果文件内容
	 */
	private void loadFileContents(File file) {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document document = builder.parse(file);
			Element root = document.getDocumentElement();
			NodeList childNodes = root.getElementsByTagName("sql");
			if (childNodes != null) {
				for (int i = 0; i < childNodes.getLength(); i++) {
					Element childNode = (Element) childNodes.item(i);
					JdbcDao.SQL_MAPPING.put(childNode.getAttribute("name"), childNode.getTextContent().trim());
				}
			}
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public int saveOrUpdate(String sql, Map<String, Object> paramMap) {
		return this.npJdbcTemplate.update(sql, paramMap);
	}

	@Override
	public int delete(String sql, Map<String, Object> paramMap) {
		return this.npJdbcTemplate.update(sql, paramMap);
	}

	@Override
	public int[] batchExecute(String sql, final List<Object[]> paramList) {
		return this.jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
			// 返回更新的记录数
			@Override
			public int getBatchSize() {
				return paramList.size();
			}

			// 设置参数
			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				Object[] objs = paramList.get(i);
				for (int j = 0, length = objs.length; j < length; j++) {
					ps.setObject(j + 1, objs[j]);
				}
			}
		});
	}

	@Override
	public List<Map<String, Object>> find(String sql, Map<String, Object> paramMap) {
		/* 获取分页情况 */
		Pagination pagination = PaginationThreadUtils.get();
		if (pagination == null) {
			pagination = new Pagination();
			PaginationThreadUtils.set(pagination);
			pagination.setPageNow(1);
		}
		if (pagination.getTotalCount() == 0) {
			String countSql = "SELECT COUNT(1) FROM (" + sql + ") TEMP_TABLE_";
			pagination.setTotalCount(this.getCount(countSql, paramMap));
		}
		int firstResult = (pagination.getPageNow() - 1) * pagination.getPageSize();
		/* 校验分页情况 */
		if (firstResult >= pagination.getTotalCount() || firstResult < 0) {
			pagination.setPageNow(1);
		}
		/* 如果总数返回0, 直接返回空 */
		if (pagination.getTotalCount() == 0) {
			return null;
		}
		if (Constants.DB_NAME.equals("oracle")) {
			return this.queryForOracle(sql, paramMap);
		} else if (Constants.DB_NAME.equals("mysql")) {
			return this.queryForMysql(sql, paramMap);
		}
		return null;
	}

	private List<Map<String, Object>> queryForOracle(String sql, Map<String, Object> paramMap) {
		Pagination pagination = PaginationThreadUtils.get();
		int firstResult = (pagination.getPageNow() - 1) * pagination.getPageSize();
		int maxResults = pagination.getPageSize();
		StringBuilder sb = new StringBuilder();
		sb.append("SELECT * FROM (");
		sb.append("SELECT TEMP_TABLE_.*, ROWNUM ROWNUM_ FROM (").append(sql).append(") TEMP_TABLE_");
		sb.append(" WHERE ROWNUM <= ").append(firstResult + maxResults).append(")");
		sb.append(" WHERE ROWNUM_ > ").append(firstResult);
		return this.findAll(sb.toString(), paramMap);
	}

	private List<Map<String, Object>> queryForMysql(String sql, Map<String, Object> paramMap) {
		Pagination pagination = PaginationThreadUtils.get();
		int firstResult = (pagination.getPageNow() - 1) * pagination.getPageSize();
		int maxResults = pagination.getPageSize();
		StringBuilder sb = new StringBuilder();
		sb.append("SELECT * FROM (").append(sql).append(") TEMP_TABLE_");
		sb.append(" LIMIT ").append(firstResult).append(", ").append(maxResults);
		return this.findAll(sb.toString(), paramMap);
	}

	@Override
	public List<Map<String, Object>> findAll(String sql, Map<String, Object> paramMap) {
		return this.npJdbcTemplate.query(sql, paramMap, new RowMapper<Map<String, Object>>() {
			@Override
			public Map<String, Object> mapRow(ResultSet rs, int rowNum) throws SQLException {
				Map<String, Object> map = new HashMap<String, Object>();
				ResultSetMetaData meta = rs.getMetaData();
				for (int i = 1, count = meta.getColumnCount(); i <= count; i++) {
					Object obj = rs.getObject(i);
					String columnLabel = meta.getColumnLabel(i).toUpperCase();
					if (obj instanceof java.sql.Clob) {
						java.sql.Clob clob = rs.getClob(i);
						map.put(columnLabel, clob.getSubString((long) 1, (int) clob.length()));
					} else if (obj instanceof java.sql.Date || obj instanceof java.sql.Timestamp) {
						java.sql.Timestamp timestamp = rs.getTimestamp(i);
						map.put(columnLabel, timestamp);
					} else {
						map.put(columnLabel, obj);
					}
				}
				return map;
			}
		});
	}

	@Override
	public int getCount(String sql, Map<String, Object> paramMap) {
		return this.npJdbcTemplate.queryForObject(sql, paramMap, Integer.class);
	}

	@Override
	public Connection getConn() {
		try {
			return this.jdbcTemplate.getDataSource().getConnection();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

}
