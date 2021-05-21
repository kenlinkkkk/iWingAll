package com.xtel.cms.utils;

public class XBean {

	private String value;
	private String caption;

	public XBean(String value, String caption) {
		super();
		this.value = value;
		this.caption = caption;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getCaption() {
		return caption;
	}

	public void setCaption(String caption) {
		this.caption = caption;
	}

	@Override
	public String toString() {
		return caption;
	}
}
