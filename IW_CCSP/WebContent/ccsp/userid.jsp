<%@page import="com.ligerdev.appbase.utils.entities.PairStringString"%>
<%@page import="java.util.Map.Entry"%>
<%-- <%@page import="com.xxx.aps.XmlConfigs"%> --%>
<%@page import="com.ligerdev.appbase.utils.db.XBaseDAO"%>
<%@page import="com.ligerdev.appbase.utils.http.HttpClientUtils"%>
<%@page import="com.ligerdev.appbase.utils.db.BaseDAO"%>
<%@page import="com.ligerdev.appbase.utils.BaseUtils"%>
<%@page import="com.ligerdev.appbase.utils.http.HttpServerUtils"%>
<%@page import="com.ligerdev.appbase.utils.textbase.StringGenerator"%>
<%@ page import="org.w3c.dom.Element"%>
<%@ page import="java.io.*"%>
<%@ page import="java.sql.*"%>
<%@ page import="org.w3c.dom.Document"%>
<%@ page import="java.util.regex.*" %>
<%@ page import="java.net.*" %>
<%@ page import="java.util.*" %>
<%@ page import="java.util.Date" %>
<%@ page import="java.text.*"%>
<%@ page import="org.apache.log4j.*" %>  
<%-- <%@ page import="com.xxx.aps.logic.db.orm.*" %>  --%>
<%@ page import="com.ligerdev.appbase.utils.cache.*" %>

<%@ page trimDirectiveWhitespaces="true" %> 
<%-- <%@include file="_utils.app.jsp"%>    --%>
<%-- <%@include file="_ccsp.client.jsp"%>    --%>

<%
// đặt bên dmz của hulaa, để API bên 
String transid = request.getParameter("transid");
String action = request.getParameter("action");
String msisdn = request.getParameter("msisdn");
msisdn = BaseUtils.formatMsisdn_84(msisdn);
String code = request.getParameter("code"); 

if("put".equalsIgnoreCase(action)){
	String sql = "insert into friend_code(msisdn, friend_code) values ('" + msisdn + "', '" + code + "')";
	int rs = xbaseDAO.execSql(transid, sql);
	out.print("" + rs);
	
	logger.info(transid + ", respCode => "  + rs);
	return;
}
if("code2msisdn".equalsIgnoreCase(action)){
	String sql = "select msisdn from friend_code where friend_code = '" + code + "'"; 
	String value =  xbaseDAO.getFirstCell(transid, sql, String.class);
	out.print("" + value);
	
	logger.info(transid + ", respMsisdn => "  + value);
	return;
}
if("getOrCreate".equalsIgnoreCase(action)){
	String sql = "select friend_code from friend_code where msisdn = '" + msisdn + "'";
	String value =  xbaseDAO.getFirstCell(transid, sql, String.class);
	
	if(value == null){
		value = StringGenerator.randomDigits(9); 
		sql = "insert into friend_code(msisdn, friend_code) values ('" + msisdn + "', '" + value + "')";
		xbaseDAO.execSql(transid, sql);
	}
	out.print("" + value);
	logger.info(transid + ", respCode => "  + value);
	return; 
}
// action get
String sql = "select friend_code from friend_code where msisdn = '" + msisdn + "'";
String value =  xbaseDAO.getFirstCell(transid, sql, String.class);
out.print("" + value);
logger.info(transid + ", respCode => "  + value);
return;
%>
 

<%!
private static BaseDAO baseDAO = BaseDAO.getInstance("main");
private static XBaseDAO xbaseDAO = XBaseDAO.getInstance("main");
private static Logger logger = Logger.getLogger("LOG");
%>