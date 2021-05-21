package com.ligerdev.cmms.component.dashboard;

import java.io.Serializable;
import java.util.ArrayList;

import com.ligerdev.cmms.component.IMenuCfg;

public class CfgDashCate implements Serializable, IMenuCfg {

	private String name;
	private String location;
	private String value;
	private ArrayList<CfgDashItem> items;

	public CfgDashCate() {
		// TODO Auto-generated constructor stub
	}

	public CfgDashCate(String name, String location, String value) {
		super();
		this.name = name;
		this.location = location;
		this.value = value;
	}

	public void addItem(CfgDashItem item) {
		if (items == null) {
			items = new ArrayList<CfgDashItem>();
		}
		items.add(item);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public ArrayList<CfgDashItem> getItems() {
		return items;
	}

	public void setItems(ArrayList<CfgDashItem> items) {
		this.items = items;
	}

	@Override
	public String getPage() {
		return null;
	}
}
