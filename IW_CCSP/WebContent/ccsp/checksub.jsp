<%@page import="com.ligerdev.appbase.utils.entities.PairStringString" %>
<%@page import="java.util.Map.Entry" %>
<%-- <%@page import="com.xxx.aps.XmlConfigs"%> --%>
<%@page import="com.ligerdev.appbase.utils.db.XBaseDAO" %>
<%@page import="com.ligerdev.appbase.utils.http.HttpClientUtils" %>
<%@page import="com.ligerdev.appbase.utils.db.BaseDAO" %>
<%@page import="com.ligerdev.appbase.utils.BaseUtils" %>
<%@page import="com.ligerdev.appbase.utils.http.HttpServerUtils" %>
<%@page import="com.ligerdev.appbase.utils.textbase.StringGenerator" %>
<%@ page import="org.w3c.dom.Element" %>
<%@ page import="java.io.*" %>
<%@ page import="java.sql.*" %>
<%@ page import="org.w3c.dom.Document" %>
<%@ page import="java.util.regex.*" %>
<%@ page import="java.net.*" %>
<%@ page import="java.util.*" %>
<%@ page import="java.util.Date" %>
<%@ page import="java.text.*" %>
<%@ page import="org.apache.log4j.*" %>
<%-- <%@ page import="com.xxx.aps.logic.db.orm.*" %>  --%>
<%@ page import="com.ligerdev.appbase.utils.cache.*" %>

<%@ page trimDirectiveWhitespaces="true" %>
<%@include file="_utils.app.jsp" %>
<%@include file="_ccsp.client.jsp" %>

<%
    // 936248538, 909090529
// curl 'http://localhost:8888/ccsp/checksub.jsp?msisdn=909090529&type=1'

    if (request.getParameter("compile") != null) {
        out.print("compile ok, v1.0");
        return;
    }
    String msisdn = request.getParameter("msisdn");
    String friend_code = request.getParameter("msisdn");
    msisdn = BaseUtils.formatMsisdn(msisdn, "84", "84");
    String type = request.getParameter("type");
    String transid = "CHECK@" + StringGenerator.randomCharacters(5) + "@" + BaseUtils.formatMsisdn(msisdn, "84", "");
    logger.info(transid + ", ############ checksub: " + msisdn + "| type: " + type);

    if (BaseUtils.isBlank(msisdn) || BaseUtils.isBlank(type)) {
        out.print("blank params");
        logger.info(transid + ", blank params");
        return;
    }
    if ("1".equalsIgnoreCase(type)) {
        // return các mã gói đang active cách nhau dấu phẩy, nếu ko có gói nào return chuỗi: NOT_ACTIVE
        String sql = "select package_id from subscriber where status = 1 and msisdn = '" + msisdn + "'";
        ArrayList<String> list = xbaseDAO.getListBySql(transid, String.class, sql, null, null);

        if (list == null || list.size() == 0) {
            out.print("NOT_ACTIVE");
            return;
        }
        String temp = "";
        for (String pkg : list) {
            temp += "," + pkg;
        }
        temp = temp.replaceFirst(",", "");
        out.print(temp);
        return;
    } else if ("2".equalsIgnoreCase(type)) {
        // curl 'http://localhost:8888/ccsp/checksub.jsp?msisdn=909090529&type=2'
        // return theo kiểu module API cũ

        String sql = "select package_id from subscriber where status = 1 and msisdn = '" + msisdn + "'";
        ArrayList<String> list = xbaseDAO.getListBySql(transid, String.class, sql, null, null);

        if (list == null || list.size() == 0) {
            out.print("0|null|" + transid);
            return;
        }
        String temp = "";
        for (String pkg : list) {
            temp += "," + pkg;
        }
        temp = temp.replaceFirst(",", "");
        String s = list.size() + "|" + temp + "|" + transid;

        out.print(s);
        return;
    } else if ("3".equalsIgnoreCase(type)) {
        // curl 'http://localhost:8888/ccsp/checksub.jsp?msisdn=909090529&type=3'
        // return theo kiểu module API cũ

        String sql = "select 1 from subscriber where msisdn = '" + msisdn + "' and status = 1";

        if (baseDAO.hasResult(transid, sql)) {
            out.print("true");
            return;
        }
        out.print("false");
        return;
    } else if ("checkpass".equalsIgnoreCase(type)) {
        // curl 'http://localhost:8888/ccsp/checksub.jsp?msisdn=946055810&password=98650&type=checkpass'
        // checkpass
        String pass = String.valueOf(request.getParameter("password")).replace("'", "").replace("\"", "");
        String sql = "select 1 from subscriber where msisdn = '" + msisdn + "' and password = '" + pass + "'";

        if (baseDAO.hasResult(transid, sql)) {
            out.print("true");
            return;
        }

        String sqlGetMsisdn = "select msisdn from friend_code where friend_code='" + friend_code + "'";
        String msisdnByFC = baseDAO.getFirstCell(transid, sqlGetMsisdn, String.class);
        if (msisdnByFC != null && !msisdnByFC.equals("")) {
            String sqlCheckPass = "select 1 from subscriber where msisdn = '" + msisdnByFC + "' and password = '" + pass + "' and package_id='YV'";
            if (baseDAO.hasResult(transid, sqlCheckPass)) {
                out.print("true");
                return;
            }
        }

        out.print("false");
        return;
    } else if ("dk".equalsIgnoreCase(type)) {
        String pass = request.getParameter("pass");
        // curl 'http://localhost:8888/ccsp/checksub.jsp?msisdn=84898569261&type=dk&pass=12345'
        String cpid = null;
        String groupCpid = null;
        String channel = "web";
        if ("web".equalsIgnoreCase(channel)) {
            cpid = "dkweb";
            groupCpid = "SMS";
        }
        String format = "dd/MM/yyyy HH:mm:ss";
        String note = null;
        Date expireTime = new Date();
        int regdate = Integer.parseInt(new SimpleDateFormat("yyyyMMdd").format(new Date()));
        Date activeTime = new Date();
        String subnote1 = groupCpid;
        String subnote2 = "0";   // số tiền trừ dc của gói, ko reset khi hủy/dk lại
        String subnote3 = "0";     // số tiền trừ dc của gói, reset khi hủy/dk lại
        String subnote4 = null, subnote5 = null;

        // => thường thì sẽ là đăng ký lại nếu giá  > 0 (kịch bản dk lần đầu toàn thấy free)
//        String actionType = charge_price > 0 ? "ReREG" : "FirstREG";
        int expireint = 0, activeint = 0;

        // kiểm tra xem số này đã active gói hay chưa
        Subscriber subs = getSubscriber(transid, msisdn, "YV");


        String passCheck = xbaseDAO.getFirstCell(transid, "select password from subscriber where msisdn = '" + msisdn + "'", String.class);
        if (passCheck == null) {
            passCheck = pass;
        } else {
            out.print("exist account");
            return;
        }

        if (subs == null) {
            // insert
            subs = new Subscriber(msisdn, new Date(), expireTime, "YV", activeTime, null,
                    null, channel, null, 3, note, cpid, passCheck,
                    0, subnote1, subnote2, subnote3, subnote4, subnote5);
            xbaseDAO.insertBean(transid, subs);
        }
        out.print(passCheck);
        return;
    } else if ("deleteSub".equalsIgnoreCase(type)) {
        String sql = "delete from subscriber where msisdn='" + msisdn + "'";
        int result = xbaseDAO.execSql(transid, sql);

        out.print("success delete " + msisdn);
        return;
    }
    out.print("wrong_type");

%>


<%!
    //    private static BaseDAO baseDAO = BaseDAO.getInstance("main");
//    private static XBaseDAO xbaseDAO = XBaseDAO.getInstance("main");
//    private static Logger logger = Logger.getLogger("LOG");
%>