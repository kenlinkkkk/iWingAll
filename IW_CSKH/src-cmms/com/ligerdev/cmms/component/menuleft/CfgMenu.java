package com.ligerdev.cmms.component.menuleft;

import java.io.Serializable;
import java.util.ArrayList;

import com.ligerdev.cmms.component.IMenuCfg;

public class CfgMenu implements Serializable, IMenuCfg {

	private String name;
	private String value;
	private String icon;
	private String page;
	private ArrayList<IMenuCfg> items;

	public CfgMenu() {
		// TODO Auto-generated constructor stub
	}

	public CfgMenu(String name, String icon, String value, String page, ArrayList<IMenuCfg> items) {
		this.name = name;
		this.items = items;
		this.icon = icon;
		this.value = value;
		this.page = page;
	}

	public void addIMenuCfg(IMenuCfg cfg) {
		if (items == null) {
			items = new ArrayList<IMenuCfg>();
		}
		items.add(cfg);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ArrayList<IMenuCfg> getItems() {
		return items;
	}

	public void setItems(ArrayList<IMenuCfg> items) {
		this.items = items;
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

	@Override
	public String getPage() {
		return page;
	}

	public void setPage(String page) {
		this.page = page;
	}
}
