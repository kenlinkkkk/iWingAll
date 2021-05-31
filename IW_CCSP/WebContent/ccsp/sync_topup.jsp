<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.ligerdev.appbase.utils.db.AntTable" %>
<%@ page import="java.io.Serializable" %>
<%@ page import="com.ligerdev.appbase.utils.db.AntColumn" %>
<%@ page import="com.ligerdev.appbase.utils.db.XBaseDAO" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="com.ligerdev.appbase.utils.textbase.StringGenerator" %>
<%@ page import="org.apache.log4j.Logger" %>
<%@ page import="java.util.Date" %>
<%@ page import="java.text.DateFormat" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.text.ParseException" %>
<%@ page import="com.xxx.aps.logic.db.orm.Subscriber" %>


<%@ page trimDirectiveWhitespaces="true" %>


<%
    String transid = "F" + StringGenerator.randomCharacters(5) + "@";
    logger.info(transid + "============ start sync topup ===============");
    String sql = "SELECT * FROM subscriber WHERE active_channel IN ('SMS','AVB') AND STATUS = 1 AND deactive_time IS NULL AND created_time <= last_renew - INTERVAL 72 HOUR AND DATE(created_time) >= '2021-05-21' AND subnote5 IS NULL";

    ArrayList<Subscriber> listSubs = xbaseDAO2.getListBySql(transid, Subscriber.class, sql, null, null);
    for (Subscriber sub : listSubs) {
        KmtqTopup kmtqTopup = new KmtqTopup();
        kmtqTopup.setMsisdn(sub.getMsisdn());
        kmtqTopup.setCreatedTime(sub.getLastRenew());
        kmtqTopup.setMakeTime(new Date());
        kmtqTopup.setChannel(sub.getActiveChannel());
        kmtqTopup.setStatus(0);
        kmtqTopup.setTopupFee(10000);
        kmtqTopup.setMt("");
        kmtqTopup.setType(1);
        kmtqTopup.setCategory("iMedia");
        kmtqTopup.setPartner(0);

        int result = xbaseDAO.insertBean(transid, kmtqTopup);
        if (result != 0) {
            sub.setSubnote5("CTKM_T6");
            xbaseDAO.updateBean(transid, sub);
        }

    }
    logger.info(transid + "============ end sync topup size: " + listYl.size() + " sync success: " + count + " ===============");
%>


<%!

    private static XBaseDAO xbaseDAO = XBaseDAO.getInstance("main");
    private static XBaseDAO xbaseDAO2 = XBaseDAO.getInstance("main2");
    private static Logger logger = Logger.getLogger("LOG");

    @AntTable(catalog = "vas", name = "kmtq", label = "kmtq", key = "id")
    public static class KmtqTopup implements Serializable, Cloneable {

        private int id;
        private String msisdn;//
        private Date createdTime;//
        private Date deactiveTime;
        private Date makeTime;// now
        private String channel;// 
        private int status;// 0 chưa ; 2 ok ; 3 fail
        private int topupFee;//
        private Date topupTime;
        private String mt;//
        private Date requestTime;
        private Integer type;// 0 
        private Integer errorCode;
        private String promotionType;
        private String tracenumber;
        private String category;// Ting
        private Integer partner;// 0:Fs ; 2:imedia ; 1:FS

        public KmtqTopup() {
        }

        public KmtqTopup(int id, String msisdn, Date createdTime, Date deactiveTime, Date makeTime, String channel, int status, int topupFee, Date topupTime, String mt, Date requestTime, Integer type, Integer errorCode, String promotionType, String tracenumber, String category, Integer partner) {
            this();
            this.id = id;
            this.msisdn = msisdn;
            this.createdTime = createdTime;
            this.deactiveTime = deactiveTime;
            this.makeTime = makeTime;
            this.channel = channel;
            this.status = status;
            this.topupFee = topupFee;
            this.topupTime = topupTime;
            this.mt = mt;
            this.requestTime = requestTime;
            this.type = type;
            this.errorCode = errorCode;
            this.promotionType = promotionType;
            this.tracenumber = tracenumber;
            this.category = category;
            this.partner = partner;
        }

        @AntColumn(name = "id", auto_increment = true, size = 11, label = "id")
        public void setId(int id) {
            this.id = id;
        }

        @AntColumn(name = "id", auto_increment = true, size = 11, label = "id")
        public int getId() {
            return this.id;
        }

        @AntColumn(name = "msisdn", size = 20, label = "msisdn")
        public void setMsisdn(String msisdn) {
            this.msisdn = msisdn;
        }

        @AntColumn(name = "msisdn", size = 20, label = "msisdn")
        public String getMsisdn() {
            return this.msisdn;
        }

        @AntColumn(name = "created_time", size = 19, label = "created_time")
        public void setCreatedTime(Date createdTime) {
            this.createdTime = createdTime;
        }

        @AntColumn(name = "created_time", size = 19, label = "created_time")
        public Date getCreatedTime() {
            return this.createdTime;
        }

        @AntColumn(name = "deactive_time", size = 19, label = "deactive_time")
        public void setDeactiveTime(Date deactiveTime) {
            this.deactiveTime = deactiveTime;
        }

        @AntColumn(name = "deactive_time", size = 19, label = "deactive_time")
        public Date getDeactiveTime() {
            return this.deactiveTime;
        }

        @AntColumn(name = "make_time", size = 19, label = "make_time")
        public void setMakeTime(Date makeTime) {
            this.makeTime = makeTime;
        }

        @AntColumn(name = "make_time", size = 19, label = "make_time")
        public Date getMakeTime() {
            return this.makeTime;
        }

        @AntColumn(name = "channel", size = 10, label = "channel")
        public void setChannel(String channel) {
            this.channel = channel;
        }

        @AntColumn(name = "channel", size = 10, label = "channel")
        public String getChannel() {
            return this.channel;
        }

        @AntColumn(name = "status", size = 11, label = "status")
        public void setStatus(int status) {
            this.status = status;
        }

        @AntColumn(name = "status", size = 11, label = "status")
        public int getStatus() {
            return this.status;
        }

        @AntColumn(name = "topup_fee", size = 11, label = "topup_fee")
        public void setTopupFee(int topupFee) {
            this.topupFee = topupFee;
        }

        @AntColumn(name = "topup_fee", size = 11, label = "topup_fee")
        public int getTopupFee() {
            return this.topupFee;
        }

        @AntColumn(name = "topup_time", size = 19, label = "topup_time")
        public void setTopupTime(Date topupTime) {
            this.topupTime = topupTime;
        }

        @AntColumn(name = "topup_time", size = 19, label = "topup_time")
        public Date getTopupTime() {
            return this.topupTime;
        }

        @AntColumn(name = "mt", size = 500, label = "mt")
        public void setMt(String mt) {
            this.mt = mt;
        }

        @AntColumn(name = "mt", size = 500, label = "mt")
        public String getMt() {
            return this.mt;
        }

        @AntColumn(name = "request_time", size = 19, label = "request_time")
        public void setRequestTime(Date requestTime) {
            this.requestTime = requestTime;
        }

        @AntColumn(name = "request_time", size = 19, label = "request_time")
        public Date getRequestTime() {
            return this.requestTime;
        }

        @AntColumn(name = "type", size = 11, label = "type")
        public void setType(Integer type) {
            this.type = type;
        }

        @AntColumn(name = "type", size = 11, label = "type")
        public Integer getType() {
            return this.type;
        }

        @AntColumn(name = "error_code", size = 11, label = "error_code")
        public void setErrorCode(Integer errorCode) {
            this.errorCode = errorCode;
        }

        @AntColumn(name = "error_code", size = 11, label = "error_code")
        public Integer getErrorCode() {
            return this.errorCode;
        }

        @AntColumn(name = "promotion_type", size = 20, label = "promotion_type")
        public void setPromotionType(String promotionType) {
            this.promotionType = promotionType;
        }

        @AntColumn(name = "promotion_type", size = 20, label = "promotion_type")
        public String getPromotionType() {
            return this.promotionType;
        }

        @AntColumn(name = "tracenumber", size = 30, label = "tracenumber")
        public void setTracenumber(String tracenumber) {
            this.tracenumber = tracenumber;
        }

        @AntColumn(name = "tracenumber", size = 30, label = "tracenumber")
        public String getTracenumber() {
            return this.tracenumber;
        }

        @AntColumn(name = "category", size = 20, label = "category")
        public void setCategory(String category) {
            this.category = category;
        }

        @AntColumn(name = "category", size = 20, label = "category")
        public String getCategory() {
            return this.category;
        }

        @AntColumn(name = "partner", size = 4, label = "partner")
        public void setPartner(Integer partner) {
            this.partner = partner;
        }

        @AntColumn(name = "partner", size = 4, label = "partner")
        public Integer getPartner() {
            return this.partner;
        }

        @Override
        public String toString() {
            return "["
                    + "id=" + id
                    + ", msisdn=" + msisdn
                    + ", createdTime=" + createdTime
                    + ", deactiveTime=" + deactiveTime
                    + ", makeTime=" + makeTime
                    + ", channel=" + channel
                    + ", status=" + status
                    + ", topupFee=" + topupFee
                    + ", topupTime=" + topupTime
                    + ", mt=" + mt
                    + ", requestTime=" + requestTime
                    + ", type=" + type
                    + ", errorCode=" + errorCode
                    + ", promotionType=" + promotionType
                    + ", tracenumber=" + tracenumber
                    + ", category=" + category
                    + ", partner=" + partner
                    + "]";
        }

        @Override
        public Object clone() throws CloneNotSupportedException {
            return super.clone();
        }
    }


    @AntTable(catalog = "game_api", name = "yl_api_transaction", label = "yl_api_transaction", key = "id")
    public static class YlApiTransaction implements Serializable, Cloneable {

        private int id;
        private Date createdDate;
        private String msisdn;
        private Integer playedTimes;
        private String rewardCode;
        private Float popupValue;
        private Date poupTime;
        private Integer poupStatus;
        private String poupInfo;

        public YlApiTransaction() {
        }

        public YlApiTransaction(int id, Date createdDate, String msisdn, Integer playedTimes, String rewardCode, Float popupValue, Date poupTime, Integer poupStatus, String poupInfo) {
            this();
            this.id = id;
            this.createdDate = createdDate;
            this.msisdn = msisdn;
            this.playedTimes = playedTimes;
            this.rewardCode = rewardCode;
            this.popupValue = popupValue;
            this.poupTime = poupTime;
            this.poupStatus = poupStatus;
            this.poupInfo = poupInfo;
        }

        @AntColumn(name = "id", auto_increment = true, size = 20, label = "id")
        public void setId(int id) {
            this.id = id;
        }

        @AntColumn(name = "id", auto_increment = true, size = 20, label = "id")
        public int getId() {
            return this.id;
        }

        @AntColumn(name = "created_date", size = 19, label = "created_date")
        public void setCreatedDate(Date createdDate) {
            this.createdDate = createdDate;
        }

        @AntColumn(name = "created_date", size = 19, label = "created_date")
        public Date getCreatedDate() {
            return this.createdDate;
        }

        @AntColumn(name = "msisdn", size = 16, label = "msisdn")
        public void setMsisdn(String msisdn) {
            this.msisdn = msisdn;
        }

        @AntColumn(name = "msisdn", size = 16, label = "msisdn")
        public String getMsisdn() {
            return this.msisdn;
        }

        @AntColumn(name = "played_times", size = 20, label = "played_times")
        public void setPlayedTimes(Integer playedTimes) {
            this.playedTimes = playedTimes;
        }

        @AntColumn(name = "played_times", size = 20, label = "played_times")
        public Integer getPlayedTimes() {
            return this.playedTimes;
        }

        @AntColumn(name = "reward_code", size = 255, label = "reward_code")
        public void setRewardCode(String rewardCode) {
            this.rewardCode = rewardCode;
        }

        @AntColumn(name = "reward_code", size = 255, label = "reward_code")
        public String getRewardCode() {
            return this.rewardCode;
        }

        @AntColumn(name = "popup_value", size = 20, label = "popup_value")
        public void setPopupValue(Float popupValue) {
            this.popupValue = popupValue;
        }

        @AntColumn(name = "popup_value", size = 20, label = "popup_value")
        public Float getPopupValue() {
            return this.popupValue;
        }

        @AntColumn(name = "poup_time", size = 19, label = "poup_time")
        public void setPoupTime(Date poupTime) {
            this.poupTime = poupTime;
        }

        @AntColumn(name = "poup_time", size = 19, label = "poup_time")
        public Date getPoupTime() {
            return this.poupTime;
        }

        @AntColumn(name = "poup_status", size = 1, label = "poup_status")
        public void setPoupStatus(Integer poupStatus) {
            this.poupStatus = poupStatus;
        }

        @AntColumn(name = "poup_status", size = 1, label = "poup_status")
        public Integer getPoupStatus() {
            return this.poupStatus;
        }

        @AntColumn(name = "poup_info", size = 255, label = "poup_info")
        public void setPoupInfo(String poupInfo) {
            this.poupInfo = poupInfo;
        }

        @AntColumn(name = "poup_info", size = 255, label = "poup_info")
        public String getPoupInfo() {
            return this.poupInfo;
        }

        @Override
        public String toString() {
            return "["
                    + "id=" + id
                    + ", createdDate=" + createdDate
                    + ", msisdn=" + msisdn
                    + ", playedTimes=" + playedTimes
                    + ", rewardCode=" + rewardCode
                    + ", popupValue=" + popupValue
                    + ", poupTime=" + poupTime
                    + ", poupStatus=" + poupStatus
                    + ", poupInfo=" + poupInfo
                    + "]";
        }

        @Override
        public Object clone() throws CloneNotSupportedException {
            return super.clone();
        }
    }

%>