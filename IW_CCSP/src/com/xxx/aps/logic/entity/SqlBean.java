package com.xxx.aps.logic.entity;

import java.io.Serializable;

public class SqlBean implements Serializable {

	private String transid;
	private String sql;

	public SqlBean() {
		// TODO Auto-generated constructor stub
	}

	public SqlBean(String transid, String sql) {
		super();
		this.transid = transid;
		this.sql = sql;
	}

	public String getTransid() {
		return transid;
	}

	public void setTransid(String transid) {
		this.transid = transid;
	}

	public String getSql() {
		return sql;
	}

	public void setSql(String sql) {
		this.sql = sql;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return super.toString();
	}

}
