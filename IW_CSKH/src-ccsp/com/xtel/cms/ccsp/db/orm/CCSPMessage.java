package com.xtel.cms.ccsp.db.orm;

import java.io.Serializable;
import java.util.Hashtable;

import com.ligerdev.appbase.utils.db.AntColumn;
import com.ligerdev.appbase.utils.db.AntTable;

@AntTable(catalog="smovie", name="message", label="message", key="id")
public class CCSPMessage implements Serializable {

	private int id;
	private String code;
	private int status;
	private String desc;
	private String content;
	private String note;
	
	// ext
	private Hashtable<String, String> queryString = new Hashtable<String, String>();

	public CCSPMessage(){
		// Message msg = new Message(id, code, status, desc, content, note);
	}
	
	public CCSPMessage(int id, String code, int status, String desc, String content, String note){
		this();
		this.id = id;
		this.code = code;
		this.status = status;
		this.desc = desc;
		this.content = content;
		this.note = note;
	}
	
	@AntColumn(name="id", auto_increment=true, size=11, label="id")
	public void setId(int id){
		this.id = id;
	}
	
	@AntColumn(name="id", auto_increment=true, size=11, label="id")
	public int getId(){
		return this.id;
	}
	
	@AntColumn(name="code", size=10, label="code")
	public void setCode(String code){
		this.code = code;
	}
	
	@AntColumn(name="code", size=10, label="code")
	public String getCode(){
		return this.code;
	}
	
	@AntColumn(name="status", size=2, label="status")
	public void setStatus(int status){
		this.status = status;
	}
	
	@AntColumn(name="status", size=2, label="status")
	public int getStatus(){
		return this.status;
	}
	
	@AntColumn(name="desc", size=45, label="desc")
	public void setDesc(String desc){
		this.desc = desc;
	}
	
	@AntColumn(name="desc", size=45, label="desc")
	public String getDesc(){
		return this.desc;
	}
	
	@AntColumn(name="content", size=450, label="content")
	public void setContent(String content){
		this.content = content;
	}
	
	@AntColumn(name="content", size=450, label="content")
	public String getContent(){
		return this.content;
	}
	
	@AntColumn(name="note", size=45, label="note")
	public void setNote(String note){
		this.note = note;
	}
	
	@AntColumn(name="note", size=45, label="note")
	public String getNote(){
		return this.note;
	}
	
	@Override
	public String toString() {
		return "["
			+ "id=" + id
			+ ", code=" + code
			+ ", status=" + status
			+ ", desc=" + desc
			+ ", content=" + content
			+ ", note=" + note
			+ "]";
	}

	public Hashtable<String, String> getQueryString() {
		return queryString;
	}

	public void setQueryString(Hashtable<String, String> queryString) {
		this.queryString = queryString;
	}
}
