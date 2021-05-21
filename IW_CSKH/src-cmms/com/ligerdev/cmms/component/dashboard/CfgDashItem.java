package com.ligerdev.cmms.component.dashboard;

import java.io.Serializable;

import com.ligerdev.cmms.component.IMenuCfg;

public class CfgDashItem implements Serializable, IMenuCfg {

	private String name;
	private String icon;
	private String value;
	private String page;
	private String tooltip;

	public CfgDashItem() {
		// TODO Auto-generated constructor stub
	}

	public CfgDashItem(String name, String icon, String value, String page, String tooltip) {
		super();
		this.name = name;
		this.icon = icon;
		this.value = value;
		this.page = page;
		this.setTooltip(tooltip);
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	} 

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
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

	public String getTooltip() {
		return tooltip;
	}

	public void setTooltip(String tooltip) {
		this.tooltip = tooltip;
	}
}
