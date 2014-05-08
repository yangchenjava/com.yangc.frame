package com.yangc.bean;

import java.util.List;

import com.yangc.common.DaoThreadUtil;
import com.yangc.common.Pagination;

public class DataGridBean {

	private List<?> dataGrid;
	private int totalCount;

	public DataGridBean() {
		Pagination pagination = DaoThreadUtil.pagination.get();
		this.totalCount = pagination.getTotalCount();
	}

	public List<?> getDataGrid() {
		return dataGrid;
	}

	public void setDataGrid(List<?> dataGrid) {
		this.dataGrid = dataGrid;
	}

	public int getTotalCount() {
		return totalCount;
	}

}
