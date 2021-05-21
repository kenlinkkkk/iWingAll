package com.ligerdev.cmms.component.menuleft;

import java.io.Serializable;

import com.ligerdev.cmms.component.IMenuCfg;

public class CfgMenuItem implements Serializable, IMenuCfg {

	private String name;
	private String value;
	private String page;
	private String icon;

	public CfgMenuItem() {
		// TODO Auto-generated constructor stub
	}

	public CfgMenuItem(String name, String value, String page, String icon) {
		super();
		this.name = name;
		this.value = value;
		this.page = page;
		this.icon = icon;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getPage() {
		return page;
	}

	public void setPage(String page) {
		this.page = page;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}
}
