package com.xxx.aps.logic.entity;

import java.io.Serializable;
import com.ligerdev.appbase.utils.db.AntTable;
import com.ligerdev.appbase.utils.db.AntColumn;


/*
	- 15/12/2017 16:19:09: Generated by sql
	select count(*) counter, active_channel, package_id from subscriber where status = 1 group by active_channel, package_id
*/
@AntTable(catalog="xtel", name="ActivePkgBean", label="ActivePkgBean", key="")
public class ActivePkgBean implements Serializable, Cloneable {

	private int counter;
	private String activeChannel;
	private String packageId;
	private String cpid;

	public ActivePkgBean(){
	}
	
	public ActivePkgBean(int counter, String activeChannel, String packageId, String cpid){
		this();
		this.counter = counter;
		this.activeChannel = activeChannel;
		this.packageId = packageId;
		this.cpid = cpid;
	}
	
	@AntColumn(name="counter", size=21, label="counter")
	public void setCounter(int counter){
		this.counter = counter;
	}
	
	@AntColumn(name="counter", size=21, label="counter")
	public int getCounter(){
		return this.counter;
	}
	
	@AntColumn(name="active_channel", size=10, label="active_channel")
	public void setActiveChannel(String activeChannel){
		this.activeChannel = activeChannel;
	}
	
	@AntColumn(name="active_channel", size=10, label="active_channel")
	public String getActiveChannel(){
		return this.activeChannel;
	}
	
	@AntColumn(name="package_id", size=15, label="package_id")
	public void setPackageId(String packageId){
		this.packageId = packageId;
	}
	
	@AntColumn(name="package_id", size=15, label="package_id")
	public String getPackageId(){
		return this.packageId;
	}
	
	@AntColumn(name="cpid", size=15, label="cpid")
	public String getCpid() {
		return cpid;
	}

	@AntColumn(name="cpid", size=15, label="cpid")
	public void setCpid(String cpid) {
		this.cpid = cpid;
	}
	
	@Override
	public String toString() {
		return "["
			+ "counter=" + counter
			+ ", activeChannel=" + activeChannel
			+ ", packageId=" + packageId
			+ ", cpid=" + cpid
			+ "]";
	}
	
	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	} 
}