package com.xxx.aps.logic.db.orm;

import java.io.Serializable;
import java.util.Date;

import com.ligerdev.appbase.utils.db.AntColumn;
import com.ligerdev.appbase.utils.db.AntTable;

@AntTable(catalog="smovie", name="pkg_policy", label="pkg_policy", key="code")
public class PkgPolicy implements Serializable, Comparable<PkgPolicy>, Cloneable {

	private String name;
	private int freeDay;
	private int useDay;
	private int renewFee;
	private int reRegisterFee;
	private int status;
	private Date createdTime;
	private String desc;
	private String code;
	private int firstRegisterFee;
	private int freeOnCycle;
	private int order;
	private String smsReg;
	private String smsRegY;
	private String smsUnreg;
	private String feeDesc;
	private String groupId;
	private int pkgWap;
	private String cateid;
	private String contentid;
	private String note1;
	private String note2;
	private String note3;
	private String note4;
	private String note5;

	public PkgPolicy(){
	}
	
	public PkgPolicy(String name, int freeDay, int useDay, int renewFee, int reRegisterFee, int status, 
			Date createdTime, String desc, String code, int firstRegisterFee, int freeOnCycle, int order, 
			String smsReg, String feeDesc, String groupId, String smsUnreg, String smsRegY, 
			int pkgWap, String cateid, String contentid){
		this();
		this.name = name;
		this.freeDay = freeDay;
		this.useDay = useDay;
		this.renewFee = renewFee;
		this.reRegisterFee = reRegisterFee;
		this.status = status;
		this.createdTime = createdTime;
		this.desc = desc;
		this.code = code;
		this.firstRegisterFee = firstRegisterFee;
		this.freeOnCycle = freeOnCycle;
		this.order = order;
		this.setSmsReg(smsReg);
		this.feeDesc = feeDesc;
		this.groupId = groupId;
		this.setSmsUnreg(smsUnreg);
		this.smsRegY = smsRegY;
		this.pkgWap = pkgWap;
		this.cateid = cateid;
		this.contentid = contentid;
	}
	
	@AntColumn(name="name", size=45, label="name")
	public void setName(String name){
		this.name = name;
	}
	
	@AntColumn(name="name", size=45, label="name")
	public String getName(){
		return this.name;
	}
	
	@AntColumn(name="free_day", size=3, label="free_day")
	public void setFreeDay(int freeDay){
		this.freeDay = freeDay;
	}
	
	@AntColumn(name="free_day", size=3, label="free_day")
	public int getFreeDay(){
		return this.freeDay;
	}
	
	@AntColumn(name="use_day", size=3, label="use_day")
	public void setUseDay(int useDay){
		this.useDay = useDay;
	}
	
	@AntColumn(name="use_day", size=3, label="use_day")
	public int getUseDay(){
		return this.useDay;
	}
	
	@AntColumn(name="renew_fee", size=5, label="renew_fee")
	public void setRenewFee(int renewFee){
		this.renewFee = renewFee;
	}
	
	@AntColumn(name="renew_fee", size=5, label="renew_fee")
	public int getRenewFee(){
		return this.renewFee;
	}
	
	@AntColumn(name="re_register_fee", size=5, label="re_register_fee")
	public void setReRegisterFee(int reRegisterFee){
		this.reRegisterFee = reRegisterFee;
	}
	
	@AntColumn(name="re_register_fee", size=5, label="re_register_fee")
	public int getReRegisterFee(){
		return this.reRegisterFee;
	}
	
	@AntColumn(name="status", size=2, label="status")
	public void setStatus(int status){
		this.status = status;
	}
	
	@AntColumn(name="status", size=2, label="status")
	public int getStatus(){
		return this.status;
	}
	
	// @AntColumn(name="created_time", size=19, label="created_time")
	public void setCreatedTime(Date createdTime){
		this.createdTime = createdTime;
	}
	
	// @AntColumn(name="created_time", size=19, label="created_time")
	public Date getCreatedTime(){
		return this.createdTime;
	}
	
	@AntColumn(name="desc1", size=45, label="desc1")
	public void setDesc(String desc){
		this.desc = desc;
	}
	
	@AntColumn(name="desc1", size=45, label="desc1")
	public String getDesc(){
		return this.desc;
	}
	
	@AntColumn(name="code", size=15, label="code")
	public void setCode(String code){
		this.code = code;
	}
	
	@AntColumn(name="code", size=15, label="code")
	public String getCode(){
		return this.code;
	}
	
	@AntColumn(name="first_register_fee", size=5, label="first_register_fee")
	public void setFirstRegisterFee(int firstRegisterFee){
		this.firstRegisterFee = firstRegisterFee;
	}
	
	@AntColumn(name="first_register_fee", size=5, label="first_register_fee")
	public int getFirstRegisterFee(){
		return this.firstRegisterFee;
	}
	
	@AntColumn(name="free_on_cycle", size=2, label="free_on_cycle")
	public void setFreeOnCycle(int freeOnCycle){
		this.freeOnCycle = freeOnCycle;
	}
	
	@AntColumn(name="free_on_cycle", size=2, label="free_on_cycle")
	public int getFreeOnCycle(){
		return this.freeOnCycle;
	}
	
	 @AntColumn(name="order1", size=3, label="order1")
	public int getOrder() {
		return order;
	}

	 @AntColumn(name="order1", size=3, label="order1")
	public void setOrder(int order) {
		this.order = order;
	}
	
	@AntColumn(name="fee_desc", size=3, label="fee_desc")
	public String getFeeDesc() {
		return feeDesc;
	}

	@AntColumn(name="fee_desc", size=3, label="fee_desc")
	public void setFeeDesc(String feeDesc) {
		this.feeDesc = feeDesc;
	}
	
	@AntColumn(name="group_id", size=3, label="group_id")
	public String getGroupId() {
		return groupId;
	}

	@AntColumn(name="group_id", size=3, label="group_id")
	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}
	
	@AntColumn(name="sms_unreg", size=3, label="sms_unreg")
	public String getSmsUnreg() {
		return smsUnreg;
	}

	@AntColumn(name="sms_unreg", size=3, label="sms_unreg")
	public void setSmsUnreg(String smsUnreg) {
		this.smsUnreg = smsUnreg;
	}

	@AntColumn(name="sms_reg", size=3, label="sms_reg")
	public String getSmsReg() {
		return smsReg;
	}

	@AntColumn(name="sms_reg", size=3, label="sms_reg")
	public void setSmsReg(String smsReg) {
		this.smsReg = smsReg;
	} 
	
	@AntColumn(name="sms_reg_y", size=3, label="sms_reg_y")
	public String getSmsRegY() {
		return smsRegY;
	}

	@AntColumn(name="sms_reg_y", size=3, label="sms_reg_y")
	public void setSmsRegY(String smsRegY) {
		this.smsRegY = smsRegY;
	} 
	
	@AntColumn(name="pkg_wap", size=3, label="pkg_wap")
	public int getPkgWap() {
		return pkgWap;
	}

	@AntColumn(name="pkg_wap", size=3, label="pkg_wap")
	public void setPkgWap(int pkgWap) {
		this.pkgWap = pkgWap;
	}
	
	@AntColumn(name="cateid", size=3, label="cateid")
	public String getCateid() {
		return cateid;
	}

	@AntColumn(name="cateid", size=3, label="cateid")
	public void setCateid(String cateid) {
		this.cateid = cateid;
	}

	@AntColumn(name="contentid", size=3, label="contentid")
	public String getContentid() {
		return contentid;
	}

	@AntColumn(name="contentid", size=3, label="contentid")
	public void setContentid(String contentid) {
		this.contentid = contentid;
	}
	
	@AntColumn(name="note1", size=3, label="note1")
	public String getNote1() {
		return note1;
	}

	@AntColumn(name="note1", size=3, label="note1")
	public void setNote1(String note1) {
		this.note1 = note1;
	}

	@AntColumn(name="note2", size=3, label="note2")
	public String getNote2() {
		return note2;
	}

	@AntColumn(name="note2", size=3, label="note2")
	public void setNote2(String note2) {
		this.note2 = note2;
	}

	@AntColumn(name="note3", size=3, label="note3")
	public String getNote3() {
		return note3;
	}

	@AntColumn(name="note3", size=3, label="note3")
	public void setNote3(String note3) {
		this.note3 = note3;
	}

	@AntColumn(name="note4", size=3, label="note4")
	public String getNote4() {
		return note4;
	}

	@AntColumn(name="note4", size=3, label="note4")
	public void setNote4(String note4) {
		this.note4 = note4;
	}

	@AntColumn(name="note5", size=3, label="note5")
	public String getNote5() {
		return note5;
	}

	@AntColumn(name="note5", size=3, label="note5")
	public void setNote5(String note5) {
		this.note5 = note5;
	}

	@Override
	public String toString() {
		return "["
			+ "name=" + name
			+ ", freeDay=" + freeDay
			+ ", useDay=" + useDay
			+ ", renewFee=" + renewFee
			+ ", reRegisterFee=" + reRegisterFee
			+ ", status=" + status
			+ ", createdTime=" + createdTime
			+ ", desc=" + desc
			+ ", code=" + code
			+ ", firstRegisterFee=" + firstRegisterFee
			+ ", freeOnCycle=" + freeOnCycle
			+ ", order=" + order
			+ ", smsReg=" + smsReg
			+ ", smsRegY=" + smsRegY
			+ ", smsUnreg=" + smsUnreg
			+ ", feeDesc=" + feeDesc
			+ ", groupId=" + groupId
			+ ", pkgWap=" + pkgWap
			+ ", cateid=" + cateid
			+ ", contentid=" + contentid
			+ "]";
	}

	@Override
	public int compareTo(PkgPolicy o) {
		return order - o.getOrder();
	} 
	
	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	} 
}
