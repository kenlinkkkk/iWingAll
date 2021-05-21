package com.xtel.cms.ccsp.db.orm;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.ligerdev.appbase.utils.db.AntColumn;
import com.ligerdev.appbase.utils.db.AntTable;
import com.vaadin.ui.Button;

@AntTable(catalog="vcare", name="subscriber", label="subscriber", key="msisdn")
public class CCSPSubscriber implements Serializable, Cloneable {

	private String msisdn;
	private Date createdTime;
	private Date expireTime;
	private String packageId;
	private Date activeTime;
	private Date deactiveTime;
	private Date lastRenew;
	private String activeChannel;
	private String deactiveChannel;
	private int status;
	private String note;
	private String cpid;
	private String password; 
	private int ignoreSmsContent;
	
	// add 2018 05 24
	//private String subnote1; // chỉ có ở ccsp mà ko có ở bản thường
	private String subnote2;
	private String subnote3; 
	private String subnote4;
	private String subnote5;
	
	// add 2020 05 05
	private int noteint1;
	private int noteint2;
	private int noteint3;
	private int noteint4;
	private int noteint5;
		
	// ext;
	private String transid;
	private String feeDesc;
	private String pkgDesc;
	private Button button;	// hủy
	private Button button2; // hủy ko MT
	private String subService; // amob
	
	// ext CatTienSaPlay
	private int scoreRenew;
	private int scoreBuy;
	private int scoreUse;
	private int scoreTotal;
 	
	public CCSPSubscriber(){
	}
	
	public static void main(String[] args) {
		//Subscriber s = new Subscriber(msisdn, createdTime, expireTime, packageId, activeTime, deactiveTime,
		//		lastRetry, lastRenew, activeChannel, deactiveChannel, status, note, cpid, password, 
		//		ignoreSmsContent, subnote1, subnote2, subnote3, subnote4, subnote5);
	}
	
	public CCSPSubscriber(String msisdn, Date createdTime, Date expireTime, 
			String packageId, Date activeTime, Date deactiveTime,  
			Date lastRenew, String activeChannel, String deactiveChannel,  
			int status, String note, String cpid, String password,  int ignoreSmsContent
			// ,String subnote1, String subnote2, String subnote3, String subnote4,  String subnote5
		){
		this();
		this.msisdn = msisdn;
		this.createdTime = createdTime;
		this.expireTime = expireTime;
		this.packageId = packageId;
		this.activeTime = activeTime;
		this.deactiveTime = deactiveTime;
		this.lastRenew = lastRenew;
		this.activeChannel = activeChannel;
		this.deactiveChannel = deactiveChannel;
		this.status = status;
		this.note = note;
		this.cpid = cpid;
		this.password = password;
		this.ignoreSmsContent = ignoreSmsContent;
		/*this.subnote1 = subnote1;
		this.subnote2 = subnote2;
		this.subnote3 = subnote3;
		this.subnote4 = subnote4;
		this.subnote5 = subnote5;*/
	}
	
	// ext
	public int getRegDate() {
		Date d = activeTime;
		if(d == null) {
			d = new Date();
		}
		return Integer.parseInt(new SimpleDateFormat("yyyyMMdd").format(d));
	}
	
	@AntColumn(name="msisdn", size=13, label="msisdn")
	public void setMsisdn(String msisdn){
		this.msisdn = msisdn;
	}
	
	@AntColumn(name="msisdn", size=13, label="msisdn")
	public String getMsisdn(){
		return this.msisdn;
	}
	
	@AntColumn(name="created_time", size=19, label="created_time")
	public void setCreatedTime(Date createdTime){
		this.createdTime = createdTime;
	}
	
	// @AntColumn(name="created_time", size=19, label="created_time")
	public Date getCreatedTime(){
		return this.createdTime;
	}
	
	@AntColumn(name="expire_time", size=19, label="expire_time")
	public void setExpireTime(Date expireTime){
		this.expireTime = expireTime;
	}
	
	@AntColumn(name="expire_time", size=19, label="expire_time")
	public Date getExpireTime(){
		return this.expireTime;
	}
	
	@AntColumn(name="package_id", size=2, label="package_id")
	public void setPackageId(String packageId){
		this.packageId = packageId;
	}
	
	@AntColumn(name="package_id", size=2, label="package_id")
	public String getPackageId(){
		return this.packageId;
	}
	
	@AntColumn(name="active_time", size=19, label="active_time")
	public void setActiveTime(Date activeTime){
		this.activeTime = activeTime;
	}
	
	@AntColumn(name="active_time", size=19, label="active_time")
	public Date getActiveTime(){
		return this.activeTime;
	}
	
	@AntColumn(name="deactive_time", size=19, label="deactive_time")
	public void setDeactiveTime(Date deactiveTime){
		this.deactiveTime = deactiveTime;
	}
	
	@AntColumn(name="deactive_time", size=19, label="deactive_time")
	public Date getDeactiveTime(){
		return this.deactiveTime;
	}
	
	@AntColumn(name="last_renew", size=19, label="last_renew")
	public void setLastRenew(Date lastRenew){
		this.lastRenew = lastRenew;
	}
	
	@AntColumn(name="last_renew", size=19, label="last_renew")
	public Date getLastRenew(){
		return this.lastRenew;
	}
	
	@AntColumn(name="active_channel", size=20, label="active_channel")
	public void setActiveChannel(String activeChannel){
		this.activeChannel = activeChannel;
	}
	
	@AntColumn(name="active_channel", size=20, label="active_channel")
	public String getActiveChannel(){
		return this.activeChannel;
	}
	
	@AntColumn(name="deactive_channel", size=20, label="deactive_channel")
	public void setDeactiveChannel(String deactiveChannel){
		this.deactiveChannel = deactiveChannel;
	}
	
	@AntColumn(name="deactive_channel", size=20, label="deactive_channel")
	public String getDeactiveChannel(){
		return this.deactiveChannel;
	}
	
	@AntColumn(name="status", size=2, label="status")
	public void setStatus(int status){
		this.status = status;
	}
	
	@AntColumn(name="status", size=2, label="status")
	public int getStatus(){
		return this.status;
	}
	
	@AntColumn(name="note", size=20, label="note")
	public void setNote(String note){
		this.note = note;
	}
	
	@AntColumn(name="note", size=20, label="note")
	public String getNote(){
		return this.note;
	}
	
	@AntColumn(name="cpid", size=10, label="cpid")
	public void setCpid(String cpid){
		this.cpid = cpid;
	}
	
	@AntColumn(name="cpid", size=10, label="cpid")
	public String getCpid(){
		return this.cpid;
	}
	
	@AntColumn(name = "ignore_sms_content", size = 20, label = "ignore_sms_content")
	public int getIgnoreSmsContent() {
		return ignoreSmsContent;
	}

	@AntColumn(name = "ignore_sms_content", size = 20, label = "ignore_sms_content")
	public void setIgnoreSmsContent(int ignoreSmsContent) {
		this.ignoreSmsContent = ignoreSmsContent;
	}
	
	@AntColumn(name = "password", size = 20, label = "password")
	public String getPassword() {
		return password;
	}

	@AntColumn(name = "password", size = 20, label = "password")
	public void setPassword(String password) {
		this.password = password;
	} 
	
	@AntColumn(name="noteint1", size=30, label="noteint1")
	public int getNoteint1() {
		return noteint1;
	}

	@AntColumn(name="noteint1", size=30, label="noteint1")
	public void setNoteint1(int noteint1) {
		this.noteint1 = noteint1;
	}

	@AntColumn(name="noteint2", size=30, label="noteint2")
	public int getNoteint2() {
		return noteint2;
	}

	@AntColumn(name="noteint2", size=30, label="noteint2")
	public void setNoteint2(int noteint2) {
		this.noteint2 = noteint2;
	}

	@AntColumn(name="noteint3", size=30, label="noteint3")
	public int getNoteint3() {
		return noteint3;
	}

	@AntColumn(name="noteint3", size=30, label="noteint3")
	public void setNoteint3(int noteint3) {
		this.noteint3 = noteint3;
	}

	@AntColumn(name="noteint4", size=30, label="noteint4")
	public int getNoteint4() {
		return noteint4;
	}

	@AntColumn(name="noteint4", size=30, label="noteint4")
	public void setNoteint4(int noteint4) {
		this.noteint4 = noteint4;
	}

	@AntColumn(name="noteint5", size=30, label="noteint5")
	public int getNoteint5() {
		return noteint5;
	}

	@AntColumn(name="noteint5", size=30, label="noteint5")
	public void setNoteint5(int noteint5) {
		this.noteint5 = noteint5;
	}
	
	/*
	@AntColumn(name="subnote1", size=30, label="subnote1")
	public void setSubnote1(String subnote1){
		this.subnote1 = subnote1;
	}
	
	@AntColumn(name="subnote1", size=30, label="subnote1")
	public String getSubnote1(){
		return this.subnote1;
	}*/
	
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
	} 
	
	
	@Override
	public String toString() {
		return "["
			+ "msisdn=" + msisdn
			+ ", createdTime=" + createdTime
			+ ", expireTime=" + expireTime
			+ ", packageId=" + packageId
			+ ", activeTime=" + activeTime
			+ ", deactiveTime=" + deactiveTime
			+ ", lastRenew=" + lastRenew
			+ ", activeChannel=" + activeChannel
			+ ", deactiveChannel=" + deactiveChannel
			+ ", status=" + status
			+ ", note=" + note
			+ ", cpid=" + cpid
			+ ", ignoreSmsContent=" + ignoreSmsContent
			+ ", password=" + password
			//+ ", subnote1=" + subnote1
			+ ", subnote2=" + subnote2
			+ ", subnote3=" + subnote3
			+ ", subnote4=" + subnote4
			+ ", subnote5=" + subnote5
			+ ", noteint1=" + noteint1
			+ ", noteint2=" + noteint2
			+ ", noteint3=" + noteint3
			+ ", noteint4=" + noteint4
			+ ", noteint5=" + noteint5
			+ "]";
	}

	public String getTransid() {
		return transid;
	}

	public void setTransid(String transid) {
		this.transid = transid;
	}

	public String getFeeDesc() {
		return feeDesc;
	}

	public void setFeeDesc(String feeDesc) {
		this.feeDesc = feeDesc;
	}

	public Button getButton() {
		return button;
	}

	public void setButton(Button button) {
		this.button = button;
	}

	public String getPkgDesc() {
		return pkgDesc;
	}

	public void setPkgDesc(String pkgDesc) {
		this.pkgDesc = pkgDesc;
	}

	public String getSubService() {
		return subService;
	}

	public void setSubService(String subService) {
		this.subService = subService;
	}

	public int getScoreRenew() {
		return scoreRenew;
	}

	public void setScoreRenew(int scoreRenew) {
		this.scoreRenew = scoreRenew;
	}

	public int getScoreBuy() {
		return scoreBuy;
	}

	public void setScoreBuy(int scoreBuy) {
		this.scoreBuy = scoreBuy;
	}

	public int getScoreUse() {
		return scoreUse;
	}

	public void setScoreUse(int scoreUse) {
		this.scoreUse = scoreUse;
	}

	public int getScoreTotal() {
		return scoreTotal;
	}

	public void setScoreTotal(int scoreTotal) {
		this.scoreTotal = scoreTotal;
	}

	public Button getButton2() {
		return button2;
	}

	public void setButton2(Button button2) {
		this.button2 = button2;
	}  
	
	
}
