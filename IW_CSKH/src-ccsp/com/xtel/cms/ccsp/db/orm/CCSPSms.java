package com.xtel.cms.ccsp.db.orm;

import java.io.Serializable;
import java.util.Date;

public class CCSPSms implements Comparable<CCSPSms> , Serializable{

	private int id;
	private String type;
	private String msisdn;
	private Date createdTime;
	private String command;
	private String content;
	private String transid;

	public CCSPSms() {
		// TODO Auto-generated constructor stub
	}

	public CCSPSms(int id, String type, String msisdn, Date createdTime,
			String command, String content, String transid) {
		super();
		this.id = id;
		this.type = type;
		this.msisdn = msisdn;
		this.createdTime = createdTime;
		this.command = command;
		this.content = content;
		this.setTransid(transid);
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getMsisdn() {
		return msisdn;
	}

	public void setMsisdn(String msisdn) {
		this.msisdn = msisdn;
	}

	public Date getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(Date createdTime) {
		this.createdTime = createdTime;
	}

	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	@Override
	public int compareTo(CCSPSms o) {
		// TODO Auto-generated method stub
		try {
			return createdTime.compareTo(o.createdTime);
		} catch (Exception e) {
		}
		return 0;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return super.toString();
	}

	public String getTransid() {
		return transid;
	}

	public void setTransid(String transid) {
		this.transid = transid;
	}

}
