package com.xtel.cms.ccsp.db.orm;

import java.io.Serializable;
import java.util.Date;

import com.ligerdev.appbase.utils.db.AntColumn;
import com.ligerdev.appbase.utils.db.AntTable;

@AntTable(catalog="smovie", name="mo_" + AntTable.DATE_PATTERN, label="", key="id", time_pattern="yyyyMM")
public class CCSPMoHis implements Serializable {

	private int id;
	private String msisdn;
	private String content;
	private String transId;
	private String command;
	private Date createdTime;

	// ext
	private boolean isFakeMO; 
	
	public CCSPMoHis(){
		// MoHis mo = new MoHis(0, msisdn, "#ussd0", transId, "UHD", new Date());
	}
	
	public CCSPMoHis(int id, String msisdn, String content, String transId, String command, Date createdTime){
		this();
		this.id = id;
		this.msisdn = msisdn;
		this.content = content;
		this.transId = transId;
		this.command = command;
		this.createdTime = createdTime;
	}
	
	@AntColumn(name="id", size=8, label="id", auto_increment=true)
	public void setId(int id){
		this.id = id;
	}
	
	@AntColumn(name="id", size=8, label="id", auto_increment=true)
	public int getId(){
		return this.id;
	}
	
	@AntColumn(name="msisdn", size=13, label="msisdn")
	public void setMsisdn(String msisdn){
		this.msisdn = msisdn;
	}
	
	@AntColumn(name="msisdn", size=13, label="msisdn")
	public String getMsisdn(){
		return this.msisdn;
	}
	
	@AntColumn(name="content", size=50, label="content")
	public void setContent(String content){
		this.content = content;
	}
	
	@AntColumn(name="content", size=50, label="content")
	public String getContent(){
		return this.content;
	}
	
	@AntColumn(name="trans_id", size=15, label="trans_id")
	public void setTransId(String transId){
		this.transId = transId;
	}
	
	@AntColumn(name="trans_id", size=15, label="trans_id")
	public String getTransId(){
		return this.transId;
	}
	
	@AntColumn(name="command", size=15, label="command")
	public void setCommand(String command){
		this.command = command;
	}
	
	@AntColumn(name="command", size=15, label="command")
	public String getCommand(){
		return this.command;
	}
	
	@AntColumn(name="created_time", size=19, label="created_time")
	public void setCreatedTime(Date createdTime){
		this.createdTime = createdTime;
	}
	
	@AntColumn(name="created_time", size=19, label="created_time")
	public Date getCreatedTime(){
		return this.createdTime;
	}

	@Override
	public String toString() {
		return "["
			+ "id=" + id
			+ ", msisdn=" + msisdn
			+ ", content=" + content
			+ ", transId=" + transId
			+ ", command=" + command
			+ ", createdTime=" + createdTime
			+ "]";
	}
	
	public boolean isFakeMO() {
		return isFakeMO;
	}

	public void setFakeMO(boolean isFakeMO) {
		this.isFakeMO = isFakeMO;
	}
}
