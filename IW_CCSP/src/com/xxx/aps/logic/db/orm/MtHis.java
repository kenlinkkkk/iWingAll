package com.xxx.aps.logic.db.orm;

import java.io.Serializable;
import com.ligerdev.appbase.utils.db.AntTable;
import com.ligerdev.appbase.utils.db.AntColumn;
import java.util.Date;

@AntTable(catalog="smovie", name="mt_" + AntTable.DATE_PATTERN, label="", key="id", time_pattern="yyyyMM")
public class MtHis implements Serializable, Cloneable {

	private int id;
	private String msisdn;
	private String content;
	private int moId;
	private String transId;
	private String command;
	private Date createdTime;
	private String channel;
	private int type;
	private int result;
	
	// ext
	private String shortcode;

	public MtHis(){
	}
	
	public static void main(String[] args) {
		// MtHis mt = new MtHis(id, msisdn, content, moId, transId, command, createdTime, channel, type, result)
	}
	
	public MtHis(int id, String msisdn, String content, int moId, String transId, 
			String command, Date createdTime, String channel){
		this(moId, msisdn, content, moId, transId, command, createdTime, channel, 0, 0);
	}
	
	public MtHis(int id, String msisdn, String content, int moId, String transId, 
			String command, Date createdTime, String channel, int type, int result){
		this();
		this.id = id;
		this.msisdn = msisdn;
		this.content = content;
		this.moId = moId;
		this.transId = transId;
		this.command = command;
		this.createdTime = createdTime;
		this.channel = channel;
		this.type = type;
		this.result = result;
	}
	
	@AntColumn(name="id", auto_increment=true, size=8, label="id")
	public void setId(int id){
		this.id = id;
	}
	
	@AntColumn(name="id", auto_increment=true, size=8, label="id")
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
	
	@AntColumn(name="content", size=1000, label="content")
	public void setContent(String content){
		this.content = content;
	}
	
	@AntColumn(name="content", size=1000, label="content")
	public String getContent(){
		return this.content;
	}
	
	@AntColumn(name="mo_id", size=8, label="mo_id")
	public void setMoId(int moId){
		this.moId = moId;
	}
	
	@AntColumn(name="mo_id", size=8, label="mo_id")
	public int getMoId(){
		return this.moId;
	}
	
	@AntColumn(name="trans_id", size=15, label="trans_id")
	public void setTransId(String transId){
		this.transId = transId;
	}
	
	@AntColumn(name="trans_id", size=15, label="trans_id")
	public String getTransId(){
		return this.transId;
	}
	
	@AntColumn(name="command", size=30, label="command")
	public void setCommand(String command){
		this.command = command;
	}
	
	@AntColumn(name="command", size=30, label="command")
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
	
	@AntColumn(name="channel", size=5, label="channel")
	public void setChannel(String channel){
		this.channel = channel;
	}
	
	@AntColumn(name="channel", size=5, label="channel")
	public String getChannel(){
		return this.channel;
	}
	
	@AntColumn(name="type", size=2, label="type")
	public void setType(int type){
		this.type = type;
	}
	
	@AntColumn(name="type", size=2, label="type")
	public int getType(){
		return this.type;
	}
	
	@AntColumn(name="result", size=5, label="result")
	public void setResult(int result){
		this.result = result;
	}
	
	@AntColumn(name="result", size=5, label="result")
	public int getResult(){
		return this.result;
	}
	
	@Override
	public String toString() {
		return "["
			+ "id=" + id
			+ ", msisdn=" + msisdn
			+ ", content=" + content
			+ ", moId=" + moId
			+ ", transId=" + transId
			+ ", command=" + command
			+ ", createdTime=" + createdTime
			+ ", channel=" + channel
			+ ", type=" + type
			+ ", result=" + result
			+ "]";
	}
	
	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	public String getShortcode() {
		return shortcode;
	}

	public void setShortcode(String shortcode) {
		this.shortcode = shortcode;
	}
}
