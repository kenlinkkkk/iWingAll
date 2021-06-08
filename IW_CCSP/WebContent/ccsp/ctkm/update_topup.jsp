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
<%@ page import="java.util.Map" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="com.google.gson.Gson" %>
<%@ page import="com.xxx.aps.processor.ExecuteSQL" %>
<%@ page import="com.xxx.aps.logic.entity.SqlBean" %>

<%
    if(request.getParameter("compile") != null){
        out.print("compile ok, v1.0");
        return;
    }

    int rs = -1;
    String transid = "TOPUP@" + StringGenerator.randomCharacters(5) + "@";
    logger.info(transid + "::begin topup response ======================");
    String idTopup = request.getParameter("id");
    String msisdnTopup = request.getParameter("msisdn");
    String topupStatus = request.getParameter("topup_status");
    String topupResponse = request.getParameter("topup_response");
    String topupTime = request.getParameter("topup_time");
    logger.info(transid + "::Params::id=" + idTopup +":-:msisdn="+ msisdnTopup +":-:status="+ topupStatus +":-:response="+ topupResponse +":-:time="+ topupTime);

    String kmtqTopup = "Update kmtq set " +
            "topup_time =  STR_TO_DATE('"+ topupTime +"', '%Y%m%d%H%i%s'), " +
            "status = " + Integer.parseInt(topupStatus) + " " +
            "where id = " + Integer.parseInt(idTopup);

    logger.info(transid + "::Q:: " + kmtqTopup);

    String updateSub = "update subscriber set" +
            "subnote5 = 'topup' where msisdn ='" + msisdnTopup +"' and package_id = 'ID'";

    rs = xbaseDAO.execSql(transid, kmtqTopup);
    logger.info(transid + "::end topup response ======================");
    out.print(rs);
%>

<%!
    private static XBaseDAO xbaseDAO = XBaseDAO.getInstance("main");
    private static Logger logger = Logger.getLogger("LOG");

    class ObjTopup {
        private int id;
        private String msisdn;
        private int topup_type; // 1: topup tien - 0:topup data
        private int topup_status; // 0: trang thai topup
        private String reg_time;
        private String topup_amount;

        public ObjTopup() {
        }

        public ObjTopup(int id, String msisdn, int topup_type, int topup_status, String reg_time, String topup_amount) {
            this.id = id;
            this.msisdn = msisdn;
            this.topup_type = topup_type;
            this.topup_status = topup_status;
            this.reg_time = reg_time;
            this.topup_amount = topup_amount;
        }

        public int getId()                              {       return id;                          }
        public void setId(int id)                       {       this.id = id;                       }
        public String getMsisdn()                       {       return msisdn;                      }
        public void setMsisdn(String msisdn)            {       this.msisdn = msisdn;               }
        public int getTopupType()                       {       return topup_type;                  }
        public void setTopupType(int topup_type)        {       this.topup_type = topup_type;       }
        public int getTopupStatus()                     {       return topup_status;                }
        public void setTopupStatus(int topup_status)    {       this.topup_status = topup_status;   }
        public String getRegTime()                      {       return reg_time;                    }
        public void setRegTime(String reg_time)         {       this.reg_time = reg_time;           }
        public String getTopupAmount()                  {       return topup_amount;                }
        public void setTopupAmount(String topup_amount) {       this.topup_amount = topup_amount;   }
    }

    class TopupResponse {
        private int id;
        private int top_status;
        private String topup_response;
        private String topup_time;

        public int getId() {    return id;        }
        public void setId(int id) {this.id = id;        }
        public int getTop_status() {return top_status;        }
        public void setTop_status(int top_status) {this.top_status = top_status;        }
        public String getTopup_response() {return topup_response;        }
        public void setTopup_response(String topup_response) {this.topup_response = topup_response;        }
        public String getTopup_time() {return topup_time;        }
        public void setTopup_time(String topup_time) {this.topup_time = topup_time; }
    }

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
%>