package com.xtel.cms.utils;

public class CtkmDuration {

	public String type; // month, week
	public int order;
	public String name;
	public String from;
	public String to;
	
	public CtkmDuration() {
		// TODO Auto-generated constructor stub
	}
	
	public CtkmDuration(String type, int order, String name, String from, String to) {
		this.type = type;
		this.order = order;
		this.name = name;
		this.from = from;
		this.to = to;
	}
}
