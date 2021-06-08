<%@ page import="java.io.Serializable" %>
<%@ page import="java.util.Date" %>
<%@ page import="com.ligerdev.appbase.utils.db.AntColumn" %>
<%@ page import="com.ligerdev.appbase.utils.db.XBaseDAO" %>
<%@ page import="com.ligerdev.appbase.utils.db.AntTable" %>
<%@ page import="com.ligerdev.appbase.utils.textbase.StringGenerator" %>
<%@ page import="org.apache.log4j.Logger" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.util.Calendar" %>
<%@ page import="com.google.gson.Gson" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="com.xxx.aps.logic.db.orm.Subscriber" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%
    String rs = "-1";
    String transid = "CTKM" + StringGenerator.randomCharacters(5) + "@";
    String sqlGetPromotion = "SELECT * FROM dm_ctkm WHERE status =1";
    PromotionInfo promotionInfo = xbaseDAO.getBeanBySql(transid, PromotionInfo.class, sqlGetPromotion);
    Date date = new Date();

    ReportEffect reportEffect = new ReportEffect();

    reportEffect.setServiceName("iWing");
    reportEffect.setCpName("Nanova");
    reportEffect.setPromotionName(promotionInfo.getName());
    reportEffect.setPromotionTime(
            new SimpleDateFormat("dd/MM/yyyy").format(promotionInfo.getBenginTime())
                    +" - "+
                    new SimpleDateFormat("dd/MM/yyyy").format(promotionInfo.getEndTime()));
    reportEffect.setPromotionType(promotionInfo.getPromotionType());
    reportEffect.setPromotionMoney(promotionInfo.getTopupFee());
    reportEffect.setPromotionData("0");
    reportEffect.setReportType("DAILY");
    reportEffect.setReportKey(new SimpleDateFormat("yyyyMMdd").format(promotionInfo.getBenginTime()) +"_"+ new SimpleDateFormat("yyyyMMdd").format(promotionInfo.getEndTime()));
    reportEffect.setIsSend(0);
    reportEffect.setStatus(1);
    reportEffect.setReportDate(date);
    reportEffect.setReportTime(date);

    if (date.after(promotionInfo.getBenginTime()) && date.before(promotionInfo.getEndTime())) {
        String dateStr = new SimpleDateFormat("yyyyMMdd").format(date);

        // thue bao tham gia ctkm
        String countFirstReg = "SELECT COUNT(*) from " +
                "his_"+ new SimpleDateFormat("yyyyMM").format(date) +" where " +
                "action = 'FirstREG' " +
                "and hisnote1 = '"+ dateStr +"'";

        Integer subsNew = xbaseDAO.getFirstCell(transid, countFirstReg, Integer.class);
        reportEffect.setSubJoin(subsNew);

        //thue bao phat sinh cuoc trong ngay
        String countCharge = "SELECT count(DISTINCT(msisdn)) FROM " +
                "his_"+ new SimpleDateFormat("yyyyMM").format(date) +" WHERE action in ('FirstREG', 'RENEW') and fee > 0 and hisnote1='"+ new SimpleDateFormat("yyyyMMdd").format(date) +"'";
        Integer subCharge = xbaseDAO.getFirstCell(transid, countCharge, Integer.class);
        reportEffect.setSubCharge(subCharge);

        //thue bao du dk cong thuong
        String countTopup = "SELECT count(DISTINCT(msisdn)) FROM kmtq WHERE DATE(created_time) ='"+ new SimpleDateFormat("yyyy-MM-dd").format(date) +"'";
        Integer subTopup = xbaseDAO.getFirstCell(transid, countTopup, Integer.class);
        reportEffect.setSubCanTopup(subTopup);

        //thue bao da cong thuong
        String countTopupSuccess = "SELECT count(DISTINCT(msisdn)) FROM kmtq WHERE DATE(created_time) ='"+ new SimpleDateFormat("yyyy-MM-dd").format(date) +"' AND status = 1";
        Integer subTopupSuc = xbaseDAO.getFirstCell(transid, countTopupSuccess, Integer.class);
        reportEffect.setSubTopupMoney(subTopupSuc);
        reportEffect.setSubTopupData(0);

        //thue bao tru cuoc that bai
        String subChargeFailSql = "SELECT count(DISTINCT(msisdn)) FROM subscriber where subnote5='CTKM_T_C0'";
        Integer subChargeFail = xbaseDAO.getFirstCell(transid, subChargeFailSql, Integer.class);
        reportEffect.setSubChargeFail(subChargeFail);

        //thue bao huy truoc khi du dieu kien cong thuong
        String subCancelSql = "SELECT count(DISTINCT(msisdn)) FROM subscriber where subnote5='CTKM_T_C2'";
        Integer subCancel = xbaseDAO.getFirstCell(transid, subCancelSql, Integer.class);
        reportEffect.setSubCancel(subCancel);
    }

    String findSql = "SELECT * FROM report_effect_ctkm where report_date='"+ reportEffect.getReportDate()+"'";
    ReportEffect rpe = xbaseDAO.getBeanBySql(transid, Subscriber.class, findSql);
    if (rpe == null) {
        rs =  xbaseDAO.insertBean(transid, reportEffect);
    } else {
        rs = xbaseDAO.updateBean(transid, reportEffect);
    }
%>

<%!
    private static XBaseDAO xbaseDAO = XBaseDAO.getInstance("main");
    //    private static XBaseDAO xbaseDAO2 = XBaseDAO.getInstance("main2");
    private static Logger logger = Logger.getLogger("LOG");

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

    @AntTable(catalog = "vas", name = "report_effect_ctkm", key = "id")
    public static class ReportEffect implements Serializable, Cloneable {
        private int id;
        private String serviceName;
        private String cpName;
        private String promotionName;
        private String promotionTime;
        private String promotionType;
        private int promotionMoney;
        private String promotionData;
        private int subJoin;
        private int subCharge;
        private int subCanTopup;
        private int subTopupMoney;
        private int subTopupData;
        private int subChargeFail;
        private int subCancel;
        private Date reportDate;
        private Date reportTime;
        private String reportType;
        private String reportKey;
        private int isSend;
        private int status;

        public ReportEffect() {
        }

        public ReportEffect(int id, String serviceName, String cpName, String promotionName, String promotionTime, String promotionType, int promotionMoney, String promotionData, int subJoin, int subCharge, int subCanTopup, int subTopupMoney, int subTopupData, int subChargeFail, int subCancel, Date reportDate, Date reportTime, String reportType, String reportKey, int isSend, int status) {
            this.id = id;
            this.serviceName = serviceName;
            this.cpName = cpName;
            this.promotionName = promotionName;
            this.promotionTime = promotionTime;
            this.promotionType = promotionType;
            this.promotionMoney = promotionMoney;
            this.promotionData = promotionData;
            this.subJoin = subJoin;
            this.subCharge = subCharge;
            this.subCanTopup = subCanTopup;
            this.subTopupMoney = subTopupMoney;
            this.subTopupData = subTopupData;
            this.subChargeFail = subChargeFail;
            this.subCancel = subCancel;
            this.reportDate = reportDate;
            this.reportTime = reportTime;
            this.reportType = reportType;
            this.reportKey = reportKey;
            this.isSend = isSend;
            this.status = status;
        }

        @AntColumn(name = "id", auto_increment = true, size = 11, label = "id")
        public int getId() { return id; }

        @AntColumn(name = "id", auto_increment = true, size = 11, label = "id")
        public void setId(int id) { this.id = id; }

        @AntColumn(name = "service_name", label = "service_name")
        public String getServiceName() { return serviceName; }

        @AntColumn(name = "service_name", label = "service_name")
        public void setServiceName(String serviceName) { this.serviceName = serviceName; }

        @AntColumn(name = "cp_name", label = "cp_name")
        public String getCpName() { return cpName; }

        @AntColumn(name = "cp_name", label = "cp_name")
        public void setCpName(String cpName) { this.cpName = cpName; }

        @AntColumn(name = "promotion_name", label = "promotion_name")
        public String getPromotionName() { return promotionName; }

        @AntColumn(name = "promotion_name", label = "promotion_name")
        public void setPromotionName(String promotionName) { this.promotionName = promotionName; }

        @AntColumn(name = "promotion_time", label = "promotion_time")
        public String getPromotionTime() { return promotionTime; }

        @AntColumn(name = "promotion_time", label = "promotion_time")
        public void setPromotionTime(String promotionTime) { this.promotionTime = promotionTime; }

        @AntColumn(name = "promotion_type", label = "promotion_type")
        public String getPromotionType() { return promotionType; }

        @AntColumn(name = "promotion_type", label = "promotion_type")
        public void setPromotionType(String promotionType) { this.promotionType = promotionType; }

        @AntColumn(name = "promotion_money", label = "promotion_money")
        public int getPromotionMoney() { return promotionMoney; }

        @AntColumn(name = "promotion_money", label = "promotion_money")
        public void setPromotionMoney(int promotionMoney) { this.promotionMoney = promotionMoney; }

        @AntColumn(name = "promotion_data", label = "promotion_data")
        public String getPromotionData() { return promotionData; }

        @AntColumn(name = "promotion_data", label = "promotion_data")
        public void setPromotionData(String promotionData) { this.promotionData = promotionData; }

        @AntColumn(name = "sub_join", label = "sub_join")
        public int getSubJoin() { return subJoin; }

        @AntColumn(name = "sub_join", label = "sub_join")
        public void setSubJoin(int subJoin) { this.subJoin = subJoin; }

        @AntColumn(name = "sub_charge", label = "sub_charge")
        public int getSubCharge() { return subCharge; }

        @AntColumn(name = "sub_charge", label = "sub_charge")
        public void setSubCharge(int subCharge) { this.subCharge = subCharge; }

        @AntColumn(name = "sub_charge", label = "sub_charge")
        public int getSubCanTopup() { return subCanTopup; }

        @AntColumn(name = "sub_can_topup", label = "sub_can_topup")
        public void setSubCanTopup(int subCanTopup) { this.subCanTopup = subCanTopup; }

        @AntColumn(name = "sub_topup_money", label = "sub_topup_money")
        public int getSubTopupMoney() { return subTopupMoney; }

        @AntColumn(name = "sub_topup_money", label = "sub_topup_money")
        public void setSubTopupMoney(int subTopupMoney) { this.subTopupMoney = subTopupMoney; }

        @AntColumn(name = "sub_topup_data", label = "sub_topup_data")
        public int getSubTopupData() { return subTopupData; }

        @AntColumn(name = "sub_topup_data", label = "sub_topup_data")
        public void setSubTopupData(int subTopupData) { this.subTopupData = subTopupData; }

        @AntColumn(name = "sub_charge_fail", label = "sub_charge_fail")
        public int getSubChargeFail() { return subChargeFail; }

        @AntColumn(name = "sub_charge_fail", label = "sub_charge_fail")
        public void setSubChargeFail(int subChargeFail) { this.subChargeFail = subChargeFail; }

        @AntColumn(name = "sub_cancel", label = "sub_cancel")
        public int getSubCancel() { return subCancel; }

        @AntColumn(name = "sub_cancel", label = "sub_cancel")
        public void setSubCancel(int subCancel) { this.subCancel = subCancel; }

        @AntColumn(name = "report_date", label = "report_date")
        public Date getReportDate() { return reportDate; }

        @AntColumn(name = "report_date", label = "report_date")
        public void setReportDate(Date reportDate) { this.reportDate = reportDate; }

        @AntColumn(name = "report_time", label = "report_time")
        public Date getReportTime() { return reportTime; }

        @AntColumn(name = "report_time", label = "report_time")
        public void setReportTime(Date reportTime) { this.reportTime = reportTime; }

        @AntColumn(name = "report_type", label = "report_type")
        public String getReportType() { return reportType; }

        @AntColumn(name = "report_type", label = "report_type")
        public void setReportType(String reportType) { this.reportType = reportType; }

        @AntColumn(name = "report_key", label = "report_key")
        public String getReportKey() { return reportKey; }

        @AntColumn(name = "report_key", label = "report_key")
        public void setReportKey(String reportKey) { this.reportKey = reportKey; }

        @AntColumn(name = "is_send", label = "is_send")
        public int getIsSend() { return isSend; }

        @AntColumn(name = "is_send", label = "is_send")
        public void setIsSend(int isSend) { this.isSend = isSend; }

        @AntColumn(name = "status", label = "status")
        public int getStatus() { return status; }

        @AntColumn(name = "status", label = "status")
        public void setStatus(int status) { this.status = status; }

        @Override
        public String toString() {
            return "ReportEffect{" +
                    "id=" + id +
                    ", serviceName='" + serviceName + '\'' +
                    ", cpName='" + cpName + '\'' +
                    ", promotionName='" + promotionName + '\'' +
                    ", promotionTime='" + promotionTime + '\'' +
                    ", promotionType='" + promotionType + '\'' +
                    ", promotionMoney='" + promotionMoney + '\'' +
                    ", promotionData='" + promotionData + '\'' +
                    ", subJoin=" + subJoin +
                    ", subCharge=" + subCharge +
                    ", subCanTopup=" + subCanTopup +
                    ", subTopupMoney=" + subTopupMoney +
                    ", subTopupData=" + subTopupData +
                    ", subChargeFail=" + subChargeFail +
                    ", subCancel=" + subCancel +
                    ", reportDate=" + reportDate +
                    ", reportTime=" + reportTime +
                    ", reportType='" + reportType + '\'' +
                    ", reportKey='" + reportKey + '\'' +
                    ", isSend=" + isSend +
                    ", status=" + status +
                    '}';
        }
    }


%>

<%=rs%>