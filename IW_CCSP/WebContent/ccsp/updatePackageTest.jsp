<%@page import="com.xxx.aps.processor.UpdateStatsRsCode" %>
<%@page import="com.xxx.aps.logic.entity.SqlBean" %>
<%@page import="com.xxx.aps.processor.ExecuteSQL" %>
<%@page import="com.xxx.aps.processor.SaveActionHis" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@page import="com.ligerdev.appbase.utils.http.HttpClientUtils" %>
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
<%@ page import="com.xxx.aps.logic.db.orm.*" %>
<%@ page import="com.ligerdev.appbase.utils.cache.*" %>
<%@ page import="com.ligerdev.appbase.utils.db.*" %>

<%@ page trimDirectiveWhitespaces="true" %>
<%@include file="_utils.app.jsp" %>
<%@include file="_ccsp.client.jsp" %>

<%!
    public static CacheSyncFile cacheCCSP = CacheSyncFile.getInstance("CCSP", 10000);
%>

<%
    if (request.getParameter("compile") != null) {
        logger.info("check compile ============");
        out.print("compile ok, v1.0");
        return;
    }
	/*
		// khi đăng ký, hủy, gia hạn thì CCSP call vào file này

		// check compile
		curl 'http://localhost:8888/ccsp/updatePackage.jsp?compile'
		http://localhost:8888/ccsp/updatePackage.jsp?compile
		http://mhocvui.vn/ccsp/updatePackage.jsp?compile

		// DK
		http://45.121.26.232/ccsp/updatePackage.jsp?isdn=909090529&status=1&seviceCode=TingTing&packageCode=V1&commandCode=DK+V1&expireDatetime=18/08/2018 23:59:59&channel=SMS&message_send=Chuc Mung QK DK thanh cong!

		// HUY
		http://45.121.26.232/ccsp/updatePackage.jsp?isdn=909090529&status=3&seviceCode=TingTing&packageCode=V1&channel=SMS&expireDatetime=18/08/2018 23:59:59&message_send=HUY thanh cong

		// renew
		http://45.121.26.232/ccsp/updatePackage.jsp?isdn=909090529&status=0&seviceCode=TingTing&packageCode=V1&channel=SYS&expireDatetime=28/08/2018 23:59:59

		// MO
		http://45.121.26.232/ccsp/forwardMessage.jsp?isdn=909090529&content=KT
	 */
    String sqlGetCallAvb = "select day_call from call_avb where status=1";
    ArrayList<String> listDay = baseDAO.getListBySql("", String.class, sqlGetCallAvb,null,null);
    String dayNow = new SimpleDateFormat("yyyyMMdd").format(new Date());
    out.println(listDay.size());
    for (String s : listDay) {

        out.println(s);
    }
    out.println(dayNow);
    if (listDay.contains(dayNow)) {
        out.println("not now");
    }else {
        out.println("AVB");
    }
%>



