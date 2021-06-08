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
    int rs = 0;
    String transid = "F" + StringGenerator.randomCharacters(5) + "@";
    logger.info(transid + "============ start sync topup ===============");
    String getCTKM  = "SELECT * FROM dm_ctkm where status = 1";
    PromotionInfo promotionInfo = xbaseDAO.getBeanBySql(transid, PromotionInfo.class, getCTKM);

    String sql = "SELECT * FROM subscriber WHERE active_channel IN ('SMS','AVB') AND STATUS = 1 AND deactive_time IS NULL AND last_renew is not null and active_time <= now() - INTERVAL 72 HOUR AND subnote5 IS NULL AND " +
            "created_time between '"+ new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(promotionInfo.getBenginTime())+"' " +
            "and '"+ new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(promotionInfo.getEndTime())+"'";
//    String sql = "SELECT * FROM subscriber WHERE msisdn in ('84904554255', '84702069793')";

    ArrayList<Subscriber> listSubs = xbaseDAO.getListBySql(transid, Subscriber.class, sql, null, null);
    for (Subscriber sub : listSubs) {
        KmtqTopup kmtqTopup = new KmtqTopup();
        kmtqTopup.setMsisdn(sub.getMsisdn());
        kmtqTopup.setCreatedTime(sub.getLastRenew());
        kmtqTopup.setMakeTime(new Date());
        kmtqTopup.setChannel(sub.getActiveChannel());
        kmtqTopup.setPromotionType("iMediaMONEY");
        kmtqTopup.setStatus(0);
        kmtqTopup.setTopupFee(10000);
        kmtqTopup.setMt("");
        kmtqTopup.setType(1);
        kmtqTopup.setCategory("iMedia");
        kmtqTopup.setPartner(0);

        int result = xbaseDAO.insertBean(transid, kmtqTopup);
        if (result != 0) {
            rs += 1;
            sub.setSubnote5("CTKM_T6_SUC");
            xbaseDAO.updateBean(transid, sub);
        }
    }

    logger.info(transid + "============ end sync topup size: " + listSubs.size() + " sync success: " + rs + " ===============");
%>


<%!

    private static XBaseDAO xbaseDAO = XBaseDAO.getInstance("main");
//    private static XBaseDAO xbaseDAO2 = XBaseDAO.getInstance("main2");
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

    @AntTable(catalog = "vas", name = "dm_ctkm", label = "dm_ctkm", key = "id")
    public static class PromotionInfo implements Serializable, Cloneable {
        private int id;
        private String name;
        private Date benginTime;
        private Date endTime;
        private String category;
        private String promotionType;
        private int status;
        private String content;
        private int topupFee;

        public PromotionInfo() {
        }

        public PromotionInfo(int id, String name, Date benginTime, Date endTime, String category, String promotionType, int status, String content, int topupFee) {
            this.id = id;
            this.name = name;
            this.benginTime = benginTime;
            this.endTime = endTime;
            this.category = category;
            this.promotionType = promotionType;
            this.status = status;
            this.content = content;
            this.topupFee = topupFee;
        }

        @AntColumn(name = "id", auto_increment = true, size = 11, label = "id")
        public int getId() { return id; }

        @AntColumn(name = "id", auto_increment = true, size = 11, label = "id")
        public void setId(int id) { this.id = id; }

        @AntColumn(name = "name", label = "id")
        public String getName() { return name; }

        @AntColumn(name = "name", label = "id")
        public void setName(String name) { this.name = name; }

        @AntColumn(name = "begin_time", label = "begin_time")
        public Date getBenginTime() { return benginTime; }

        @AntColumn(name = "begin_time", label = "begin_time")
        public void setBenginTime(Date benginTime) { this.benginTime = benginTime; }

        @AntColumn(name = "end_time", label = "end_time")
        public Date getEndTime() { return endTime; }

        @AntColumn(name = "end_time", label = "end_time")
        public void setEndTime(Date endTime) { this.endTime = endTime; }

        @AntColumn(name = "category", label = "category")
        public String getCategory() { return category; }

        @AntColumn(name = "category", label = "category")
        public void setCategory(String category) { this.category = category; }

        @AntColumn(name = "promotion_type", label = "promotion_type")
        public String getPromotionType() { return promotionType; }

        @AntColumn(name = "promotion_type", label = "promotion_type")
        public void setPromotionType(String promotionType) { this.promotionType = promotionType; }

        @AntColumn(name = "status", label = "status")
        public int getStatus() { return status; }

        @AntColumn(name = "status", label = "status")
        public void setStatus(int status) { this.status = status; }

        @AntColumn(name = "noidung", label = "noidung")
        public String getContent() { return content; }

        @AntColumn(name = "noidung", label = "noidung")
        public void setContent(String content) { this.content = content; }

        @AntColumn(name = "topup_fee", label = "topup_fee")
        public int getTopupFee() { return topupFee; }

        @AntColumn(name = "topup_fee", label = "topup_fee")
        public void setTopupFee(int topupFee) { this.topupFee = topupFee; }

        @Override
        public String toString() {
            return "PromotionInfo{" +
                    "id=" + id +
                    ", name='" + name + '\'' +
                    ", benginTime=" + benginTime +
                    ", endTime=" + endTime +
                    ", category='" + category + '\'' +
                    ", promotionType='" + promotionType + '\'' +
                    ", status=" + status +
                    ", content='" + content + '\'' +
                    ", topupFee=" + topupFee +
                    '}';
        }
    }
%>
<%=rs%>