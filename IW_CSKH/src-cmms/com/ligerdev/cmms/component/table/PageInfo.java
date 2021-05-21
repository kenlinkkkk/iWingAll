package com.ligerdev.cmms.component.table;

import java.io.Serializable;

@SuppressWarnings("serial")
public class PageInfo implements Serializable {
	
	private String value = "";
	private String label = "";
	private boolean isCurrent;
	private boolean disable;

	public PageInfo(String label, String value) {
		this.value = value;
		this.label = label;
	}

	public PageInfo(String label, String value, boolean disable) {
		this(label, value);
		this.disable = disable;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public boolean getIsCurrent() {
		return isCurrent;
	}

	public void setIsCurrent(boolean isCurrent) {
		this.isCurrent = isCurrent;
	}

	public boolean isDisable() {
		return disable;
	}

	public void setDisable(boolean disable) {
		this.disable = disable;
	}
}
