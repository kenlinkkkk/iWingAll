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
<%@include file="_ccsp.client.jsp"%>   

<%
if(request.getParameter("compile") != null){
	out.print("compile ok, v1.0");
	return;
}
// http://45.121.26.232/ccsp/updateMT3Day.jsp?pkgcode=VIP&content=...
String content = request.getParameter("content");
String pkgcode = request.getParameter("pkgcode");

String transid = "UPDATEMT3DAY@" + StringGenerator.randomCharacters(5);
logger.info(transid + ", ############ content: " + content + " | pkgcode: " + pkgcode);

if(BaseUtils.isBlank(content) || BaseUtils.isBlank(pkgcode)){
	out.print("blank params");
	logger.info(transid + ", blank params");
	return;
}
String a = updateSmsRemind(transid, XmlConfigs.servicename, pkgcode, content);
out.println(a + "");
%>
 

<%!
private static BaseDAO baseDAO = BaseDAO.getInstance("main");
private static XBaseDAO xbaseDAO = XBaseDAO.getInstance("main");
private static Logger logger = Logger.getLogger("LOG");
%>