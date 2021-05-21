package com.xtel.cms.ccsp.db.orm;

import java.io.Serializable;
import java.util.Date;

import com.ligerdev.appbase.utils.db.AntColumn;
import com.ligerdev.appbase.utils.db.AntTable;

@AntTable(catalog="smovie", name="his_" + AntTable.DATE_PATTERN, label="", key="id", time_pattern="yyyyMM")
public class CCSPActionHis implements Serializable {

	private int id;
	private String msisdn;
	private String action;
	private Date createdTime = new Date();
	private int fee;
	private String transId;
	private String note;
	private String channel;
	private int result;
	private String pkgCode;
	private String cpid;
	private Date expireTime;
	private Integer regdate;
	
	private int expireint;
	private int activeint;
	
	// add 2018 04 24
	/*private String subnote1;
	private String subnote2;
	private String subnote3; 
	private String subnote4;
	private String subnote5; 
	
	private String hisnote1;
	private String hisnote2;
	private String hisnote3;*/
	
	// ext
	private String pkgName;
	private int score;
	
	public CCSPActionHis(){
	}
	
	public static void main(String[] args) {
		//ActionHis a = new ActionHis(id, msisdn, action, createdTime, fee, transId, note, channel, result,
		//		pkgCode, cpid, info, expireTime, regdate, activeTime, subnote1, subnote2, subnote3, subnote4,
		//		subnote5, expireint, activeint, totalpkg, hisnote1, hisnote2, hisnote3, hisnote4, hisnote5);
	}
	
	public CCSPActionHis(int id, String msisdn, String action, Date createdTime, int fee, String transId, 
			String note, String channel, int result,  
			String pkgCode, String cpid, Date expireTime, Integer regdate  
			//,String subnote1, String subnote2, String subnote3, String subnote4, 
			//String subnote5, int expireint, int activeint, String hisnote1, String hisnote2, String hisnote3
		){
		this();
		if(createdTime == null){
			createdTime = new Date();
		}
		this.id = id;
		this.msisdn = msisdn;
		this.action = action;
		this.createdTime = createdTime;
		this.fee = fee;
		this.transId = transId;
		this.note = note;
		this.channel = channel;
		this.result = result;
		this.pkgCode = pkgCode;
		this.cpid = cpid;
		this.expireTime = expireTime;
		this.regdate = regdate;
		/*this.subnote1 = subnote1;
		this.subnote2 = subnote2;
		this.subnote3 = subnote3;
		this.subnote4 = subnote4;
		this.subnote5 = subnote5;
		this.hisnote1 = hisnote1;
		this.hisnote2 = hisnote2;
		this.hisnote3 = hisnote3;
		this.expireint = expireint;
		this.activeint = activeint;*/
	}
	
	@AntColumn(name="id", auto_increment=true, size=11, label="id")
	public void setId(int id){
		this.id = id;
	}
	
	@AntColumn(name="id", auto_increment=true, size=11, label="id")
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
	
	@AntColumn(name="action", size=15, label="action")
	public void setAction(String action){
		this.action = action;
	}
	
	@AntColumn(name="action", size=15, label="action")
	public String getAction(){
		return this.action;
	}
	
	@AntColumn(name="created_time", size=19, label="created_time")
	public void setCreatedTime(Date createdTime){
		this.createdTime = createdTime;
	}
	
	@AntColumn(name="created_time", size=19, label="created_time")
	public Date getCreatedTime(){
		return this.createdTime;
	}
	
	@AntColumn(name="fee", size=5, label="fee")
	public void setFee(int fee){
		this.fee = fee;
	}
	
	@AntColumn(name="fee", size=5, label="fee")
	public int getFee(){
		return this.fee;
	}
	
	@AntColumn(name="trans_id", size=15, label="trans_id")
	public void setTransId(String transId){
		this.transId = transId;
	}
	
	@AntColumn(name="trans_id", size=15, label="trans_id")
	public String getTransId(){
		return this.transId;
	}
	
	@AntColumn(name="note", size=20, label="note")
	public void setNote(String note){
		this.note = note;
	}
	
	@AntColumn(name="note", size=20, label="note")
	public String getNote(){
		return this.note;
	}
	
	@AntColumn(name="channel", size=20, label="channel")
	public void setChannel(String channel){
		this.channel = channel;
	}
	
	@AntColumn(name="channel", size=20, label="channel")
	public String getChannel(){
		return this.channel;
	}
	
	@AntColumn(name="result", size=3, label="result")
	public void setResult(int result){
		this.result = result;
	}
	
	@AntColumn(name="result", size=3, label="result")
	public int getResult(){
		return this.result;
	}
	
	@AntColumn(name="pkg_code", size=10, label="pkg_code")
	public String getPkgCode() {
		return pkgCode;
	}

	@AntColumn(name="pkg_code", size=10, label="pkg_code")
	public void setPkgCode(String pkgCode) {
		this.pkgCode = pkgCode;
	}

	@AntColumn(name="cpid", size=10, label="cpid")
	public String getCpid() {
		return cpid;
	}

	@AntColumn(name="cpid", size=10, label="cpid")
	public void setCpid(String cpid) {
		this.cpid = cpid;
	} 
	
	@AntColumn(name = "expire_time", size = 20, label = "expire_time")
	public Date getExpireTime() {
		return expireTime;
	}

	@AntColumn(name = "expire_time", size = 20, label = "expire_time")
	public void setExpireTime(Date expireTime) {
		this.expireTime = expireTime;
	} 
	
	@AntColumn(name = "regdate", size = 20, label = "regdate")
	public Integer getRegdate() {
		return regdate;
	}

	@AntColumn(name = "regdate", size = 20, label = "regdate")
	public void setRegdate(Integer regdate) {
		this.regdate = regdate;
	} 
	
	/*@AntColumn(name="subnote1", size=30, label="subnote1")
	public void setSubnote1(String subnote1){
		this.subnote1 = subnote1;
	}
	
	@AntColumn(name="subnote1", size=30, label="subnote1")
	public String getSubnote1(){
		return this.subnote1;
	}
	
	@AntColumn(name="subnote2", size=30, label="subnote2")
	public void setSubnote2(String subnote2){
		this.subnote2 = subnote2;
	}
	
	@AntColumn(name="subnote2", size=30, label="subnote2")
	public String getSubnote2(){
		return this.subnote2;
	}
	
	@AntColumn(name="subnote3", size=30, label="subnote3")
	public void setSubnote3(String subnote3){
		this.subnote3 = subnote3;
	}
	
	@AntColumn(name="subnote3", size=30, label="subnote3")
	public String getSubnote3(){
		return this.subnote3;
	}
	
	@AntColumn(name="subnote4", size=30, label="subnote4")
	public void setSubnote4(String subnote4){
		this.subnote4 = subnote4;
	}
	
	@AntColumn(name="subnote4", size=30, label="subnote4")
	public String getSubnote4(){
		return this.subnote4;
	}
	
	@AntColumn(name="subnote5", size=30, label="subnote5")
	public void setSubnote5(String subnote5){
		this.subnote5 = subnote5;
	}
	
	@AntColumn(name="subnote5", size=30, label="subnote5")
	public String getSubnote5(){
		return this.subnote5;
	}*/
	
	@AntColumn(name="expireint", size=5, label="expireint")
	public void setExpireint(int expireint){
		this.expireint = expireint;
	}
	
	@AntColumn(name="expireint", size=5, label="expireint")
	public int getExpireint(){
		return this.expireint;
	}
	
	@AntColumn(name="activeint", size=5, label="activeint")
	public void setActiveint(int activeint){
		this.activeint = activeint;
	}
	
	@AntColumn(name="activeint", size=5, label="activeint")
	public int getActiveint(){
		return this.activeint;
	} 
	
	/*@AntColumn(name="hisnote1", size=30, label="hisnote1")
	public void setHisnote1(String hisnote1){
		this.hisnote1 = hisnote1;
	}
	
	@AntColumn(name="hisnote1", size=30, label="hisnote1")
	public String getHisnote1(){
		return this.hisnote1;
	}
	
	@AntColumn(name="hisnote2", size=30, label="hisnote2")
	public void setHisnote2(String hisnote2){
		this.hisnote2 = hisnote2;
	}
	
	@AntColumn(name="hisnote2", size=30, label="hisnote2")
	public String getHisnote2(){
		return this.hisnote2;
	}
	
	@AntColumn(name="hisnote3", size=30, label="hisnote3")
	public void setHisnote3(String hisnote3){
		this.hisnote3 = hisnote3;
	}
	
	@AntColumn(name="hisnote3", size=30, label="hisnote3")
	public String getHisnote3(){
		return this.hisnote3;
	} */
	
	@Override
	public String toString() {
		return "["
			+ "id=" + id
			+ ", msisdn=" + msisdn
			+ ", action=" + action
			+ ", createdTime=" + createdTime
			+ ", fee=" + fee
			+ ", transId=" + transId
			+ ", note=" + note
			+ ", channel=" + channel
			+ ", pkgCode=" + pkgCode
			+ ", result=" + result
			+ ", cpid=" + cpid
			+ ", expireTime=" + expireTime
			+ ", regdate=" + regdate
			
			+ ", expireint=" + expireint
			+ ", activeint=" + activeint
			
			/*+ ", subnote1=" + subnote1
			+ ", subnote2=" + subnote2
			+ ", subnote3=" + subnote3
			+ ", subnote4=" + subnote4
			+ ", subnote5=" + subnote5
			+ ", hisnote1=" + hisnote1
			+ ", hisnote2=" + hisnote2
			+ ", hisnote3=" + hisnote3*/
			+ "]";
	}

	public String getPkgName() {
		return pkgName;
	}

	public void setPkgName(String pkgName) {
		this.pkgName = pkgName;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	} 
}




