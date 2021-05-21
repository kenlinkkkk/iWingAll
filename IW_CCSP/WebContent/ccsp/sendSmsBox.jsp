<%@ page language="java" contentType="text/html; charset=UTF-8"  pageEncoding="UTF-8"%>
<%@ page import="org.w3c.dom.Element"%>
<%@ page import="java.io.*"%>
<%@ page import="java.sql.*"%>
<%@ page import="org.w3c.dom.Document"%>
<%@ page import="org.apache.commons.lang.StringUtils"%>
<%@ page import="com.ligerdev.appbase.utils.http.*" %>
<%@ page import="java.util.regex.*" %>
<%@ page import="com.ligerdev.appbase.utils.cache.*" %>
<%@ page import="com.ligerdev.appbase.utils.*" %>
<%@ page import="com.ligerdev.appbase.utils.textbase.*" %>
<%@ page import="java.net.*" %>
<%@ page import="java.util.*" %>
<%@ page import="java.util.Date" %>
<%@ page import="org.apache.log4j.*" %> 
<%@ page import="com.ligerdev.appbase.utils.db.*"%>
<%@ page import="java.text.*"%>
<%@ page import="com.ligerdev.appbase.utils.encrypt.*"%>
<%@ page trimDirectiveWhitespaces="true" %>
<%@include file="_ccsp.client.jsp"%>

<%
	// box gửi SMS giao diện jsp
	// http://daugiaibid.vn/ccs/sendSmsBox.jsp
	
	if(request.getParameter("compile") != null){
		logger.info("check compile ============");
		out.print("compile ok, v1.0");
		return;
	}
	String transid = "SMSBOX@" + StringGenerator.randomCharacters(8);
	
	//------- check login ---------
	if(request.getParameter("logout") != null){
		request.getSession(true).removeAttribute("user");
		out.print("<script>window.location.href='?';</script>");
		return;
	}
	String username = (String) request.getSession(true).getAttribute("user");
	
	// if(request.getSession(true).getAttribute(keyAuthen) == null){
	if(username == null){ 
		logger.info(transid + ", not authen => forward 2 login page");
		request.setAttribute("oldfile", "sendSmsBox.jsp");
		
		ArrayList<String> fixUser = new ArrayList<String>();
		fixUser.add("x/x");
		request.setAttribute("fixUser", fixUser);
		
		RequestDispatcher rd = request.getRequestDispatcher("xlogin.jsp");
		rd.forward(request, response);
		return;
	} 
	username = username.replace(" ", "").toLowerCase();
	//----------------

	String msgError = "";
	String msisdn = request.getParameter("msisdn");
	String content = request.getParameter("content");
	
	content = content == null ? "" : content;
	msisdn = msisdn == null ? "" : msisdn;
	
	if(request.getParameter("Send") != null){
		boolean saveDB = true;	 // mặc định là có saveDb, ko muốn lưu thì truyền saveDB=false
				
		if(request.getParameter("saveDB") != null){
			saveDB = "true".equalsIgnoreCase(request.getParameter("saveDB"));
		}
		if(saveDB){
			MtHis mt = new MtHis(0, msisdn, content, 0, transid, "SMSBOX", new Date(), "SMS");
			int rs = sendSMS1(transid, mt);
			msgError = (String.valueOf(rs).startsWith("0") ? "OK| " : "FAIL| ") + (rs + "| " + transid);
		} else {
			String temp = sendSMS0(transid, msisdn, content);
			int rs = String.valueOf(temp).contains("<return>OK</return>") ? 0 : 1;
			msgError = (String.valueOf(rs).startsWith("0") ? "OK| " : "FAIL| ") + (rs + "| " + transid);
		}
	}
%>

<%!
	static Logger logger = Log4jLoader.getLogger();
%>

<html>
<head>
</head>
<body>
<form action="" method="post">
	<h4>SMS Sender - CCSP - MobiFone</h4>
	<br/>
	
	<div>
		Số đt: <br/>
		<input type="text" name="msisdn" style="width: 120px" value="<% out.print(msisdn); %>"></input> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		<br/>  
		<br/><br/>Nội dung: <br/> 
		<textarea rows="8" cols="90" name="content"><% out.print(content); %></textarea>
		<br/>  
		<input type="submit" name="Send" style="width: 70px"></input>
		<a style="color: red"><% out.print(msgError); %></a>
	</div>

</form>
</body>
<html>

