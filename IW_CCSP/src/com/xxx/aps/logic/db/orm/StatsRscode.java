package com.xxx.aps.logic.db.orm;

import java.io.Serializable;
import com.ligerdev.appbase.utils.db.AntTable;
import com.ligerdev.appbase.utils.db.AntColumn;

@AntTable(catalog = "opencms", name = "stats_rscode", label = "stats_rscode", key = "")
public class StatsRscode implements Serializable, Cloneable {

	private int createdTime;
	private String rsCode;
	private int counter;
	private int loadNumber;
	private String groupCpid; 
	private int fee;
	private String pkgCode;
	private String reason;
	private String cpid;
	private String application;
	private String info;
	
	// ext
	private String transid;

	public StatsRscode() {
		// StatsRscode stats = new StatsRscode(createdTime, rsCode, counter, loadNumber, groupCpid, fee, pkgCode, reason, cpid, application, info)
	}
	
	//	public StatsResultCharge(int date, String rsCode, int loadNumber, String groupCpid,
	//			String transid, int fee, String pkgCode, String reason) {
	//		
	//	}
	
	public StatsRscode(int createdTime, String rsCode, int counter, int loadNumber, String groupCpid, int fee,
				String pkgCode, String reason, String cpid, String application, String info) {
		this.createdTime = createdTime;
		this.rsCode = rsCode;
		this.counter = counter;
		this.loadNumber = loadNumber;
		this.groupCpid = groupCpid;
		this.setFee(fee);
		this.setPkgCode(pkgCode);
		this.setReason(reason);
		this.cpid = cpid;
		this.application = application;
		this.info = info;
	}

	@AntColumn(name = "created_time", size = 11, label = "created_time")
	public void setCreatedTime(int createdTime) {
		this.createdTime = createdTime;
	}

	@AntColumn(name = "created_time", size = 11, label = "created_time")
	public int getCreatedTime() {
		return this.createdTime;
	}

	@AntColumn(name = "rs_code", size = 25, label = "rs_code")
	public void setRsCode(String rsCode) {
		this.rsCode = rsCode;
	}

	@AntColumn(name = "rs_code", size = 25, label = "rs_code")
	public String getRsCode() {
		return this.rsCode;
	}

	@AntColumn(name = "counter", size = 6, label = "counter")
	public void setCounter(int counter) {
		this.counter = counter;
	}

	@AntColumn(name = "counter", size = 6, label = "counter")
	public int getCounter() {
		return this.counter;
	}

	@AntColumn(name = "load_number", size = 2, label = "load_number")
	public void setLoadNumber(int loadNumber) {
		this.loadNumber = loadNumber;
	}

	@AntColumn(name = "load_number", size = 2, label = "load_number")
	public int getLoadNumber() {
		return this.loadNumber;
	}

	@AntColumn(name = "group_cpid", size = 25, label = "group_cpid")
	public void setGroupCpid(String groupCpid) {
		this.groupCpid = groupCpid;
	}

	@AntColumn(name = "group_cpid", size = 25, label = "group_cpid")
	public String getGroupCpid() {
		return this.groupCpid;
	}

	@AntColumn(name = "fee", size = 25, label = "fee")
	public int getFee() {
		return fee;
	}

	@AntColumn(name = "fee", size = 25, label = "fee")
	public void setFee(int fee) {
		this.fee = fee;
	}

	@AntColumn(name = "pkg_code", size = 25, label = "pkg_code")
	public String getPkgCode() {
		return pkgCode;
	}

	@AntColumn(name = "pkg_code", size = 25, label = "pkg_code")
	public void setPkgCode(String pkgCode) {
		this.pkgCode = pkgCode;
	}

	@AntColumn(name = "reason", size = 25, label = "reason")
	public String getReason() {
		return reason;
	}

	@AntColumn(name = "reason", size = 25, label = "reason")
	public void setReason(String reason) {
		this.reason = reason;
	}

	@AntColumn(name = "cpid", size = 25, label = "cpid")
	public String getCpid() {
		return cpid;
	}

	@AntColumn(name = "cpid", size = 25, label = "cpid")
	public void setCpid(String cpid) {
		this.cpid = cpid;
	}

	@AntColumn(name = "application", size = 25, label = "application")
	public String getApplication() {
		return application;
	}

	@AntColumn(name = "application", size = 25, label = "application")
	public void setApplication(String application) {
		this.application = application;
	}

	@AntColumn(name = "info", size = 25, label = "info")
	public String getInfo() {
		return info;
	}

	@AntColumn(name = "info", size = 25, label = "info")
	public void setInfo(String info) {
		this.info = info;
	}

	@Override
	public String toString() {
		return "StatsRscode [createdTime=" + createdTime + ", rsCode=" + rsCode + ", counter=" + counter
				+ ", loadNumber=" + loadNumber + ", groupCpid=" + groupCpid + ", fee=" + fee + ", pkgCode=" + pkgCode
				+ ", reason=" + reason + ", cpid=" + cpid + ", application=" + application + ", info=" + info + "]";
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	public String getTransid() {
		return transid;
	}

	public void setTransid(String transid) {
		this.transid = transid;
	}
}