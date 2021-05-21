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
<%-- <%@include file="_utils.app.jsp"%>    --%>
<%@include file="_ccsp.client.jsp" %>

<%
    // 936248538, 909090529

// TT service
// http://45.121.26.232/ccsp/unregister.jsp?msisdn=84909090529&pkgcode=VIP
// http://daugiaibid.vn/ccsp/unregister.jsp?msisdn=84909090529&pkgcode=VIP

// localhost
// curl 'http://localhost:8888/ccsp/unregister.jsp?msisdn=909090529&pkgcode=IB'

    if (request.getParameter("compile") != null) {
        out.print("compile ok, v1.0");
        return;
    }
    String msisdn = request.getParameter("msisdn");
    msisdn = BaseUtils.formatMsisdn(msisdn, "84", "84");
    String transid = request.getParameter("transid");
    String pkgcode = request.getParameter("pkgcode");

    if (BaseUtils.isBlank(transid)) {
        transid = "UNREG@" + StringGenerator.randomCharacters(5) + "@" + BaseUtils.formatMsisdn(msisdn, "84", "");
        logger.info(transid + ", ############ unregister WCC: " + msisdn + ", pkg: " + pkgcode);
    } else {
        logger.info(transid + ", unregister WCC: " + msisdn + ", pkg: " + pkgcode);
    }
    if (BaseUtils.isBlank(msisdn) || BaseUtils.isBlank(pkgcode)) {
        out.print("blank params");
        logger.info(transid + ", blank params");
        return;
    }
//    if (XmlConfigs.servicename.toLowerCase().contains("iwing")) {
//        // dv này chỉ có 1 gói nên ccsp khai mã hủy bằng HUY
//
//        String a = actionWCC(transid, msisdn, "HUY", pkgcode, "");
//        out.println(a + "");
//        return;
//    }
    String a = "";
    if (pkgcode.contains("id")||pkgcode.contains("ID")) {
        a = unRegMsisdn(transid, msisdn, "999", "HUYCSP " + pkgcode, pkgcode, "");
    } else {
        a = actionWCC(transid, msisdn, "HUY " + pkgcode, pkgcode, "");
    }

    out.println(a + "");

// IBID_HUY_EXIST: QuÃ½ khÃ¡ch chÆ°a Ä‘Äƒng kÃ½ dá»‹ch vá»¥ ...
// IBID_HUY_SUCC: QuÃ½ khÃ¡ch Ä‘Ã£ há»§y thÃ nh cÃ´ng gÃ³i ...
%>


<%!
    private static BaseDAO baseDAO = BaseDAO.getInstance("main");
    private static XBaseDAO xbaseDAO = XBaseDAO.getInstance("main");
    private static Logger logger = Logger.getLogger("LOG");
%>