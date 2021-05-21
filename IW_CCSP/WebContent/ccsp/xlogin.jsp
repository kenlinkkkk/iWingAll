<%-- <%@page import="com.xtel.cms.db.orm.xauthen.XjschAccount"%> --%>
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
<%-- <%@ page import="org.apache.commons.httpclient.*"%> --%>
<%@ page import="com.ligerdev.appbase.utils.encrypt.*"%>
<%@page import="java.math.BigDecimal"%>
<%@page import="java.util.Map.*"%>
<%@page import="javax.servlet.http.Cookie"%>

<%@ page trimDirectiveWhitespaces="true" %>
<%@include file="_utils.app.jsp"%>   

<%
	if(request.getParameter("compile") != null){
		logger.info("check compile ============");
		out.print("compile ok, v1.0");
		return;
	}
	String msg = "";
	boolean authen = false;
	String transid = (String) request.getAttribute("transid");
	String keyAuthen = "SBbcjckqwb324b32421";
	
	if(transid == null){
		transid = request.getParameter("transid");
	}
	String username = request.getParameter("username");
	String password = request.getParameter("password");
	ArrayList<String> fixUser = (ArrayList<String>) request.getAttribute("fixUser");
	
	if(username != null){
		// vừa submit đăng nhập 
		logger.info(transid + ", submit account " + username + "/" + password);
			 
		if(fixUser.contains(username + "/" + password)){
			logger.info(transid + ", post login success"); 
			String encryptStr = username + "/" + password;
			encryptStr = Encrypter.encodeMD5("XLOGIN", encryptStr);
			
			// request.getSession(true).setAttribute(keyAuthen, encryptStr);
			setCookie(transid, response, keyAuthen, "1", 24 * 3); // 3 days
			request.setAttribute("transid", transid);
			request.getSession(true).setAttribute("user", username);
			
			RequestDispatcher rd = request.getRequestDispatcher(String.valueOf(request.getAttribute("oldfile")));
	        rd.forward(request, response); 
	        return;
		} else {
			request.getSession(true).removeAttribute("user");
			msg = "Wrong account, please try again!";
			logger.info(transid + ", wrong account, plz retry");
		}
	} else {
		// vừa chuyển từ bên remote sang (ko có trong session), kiểm tra cookie
		String value = getCookie(transid, request, keyAuthen);
		if(value != null){
			logger.info(transid + ", found cookie, checking");
			String sql = "select * from xjsch_account where status = 1";
			
			for(int i = 0; i < fixUser.size(); i++){
				String encryptStr = fixUser.get(i);
				encryptStr = Encrypter.encodeMD5("XLOGIN", encryptStr);
				
				if(encryptStr.equalsIgnoreCase(value)){
					logger.info(transid + ", hit cookie => bypass form login");
					request.setAttribute("transid", transid);
					
					username = fixUser.get(i).split("/")[0];
					request.getSession(true).setAttribute("user", username);
					
					RequestDispatcher rd = request.getRequestDispatcher(String.valueOf(request.getAttribute("oldfile"))); 
			        rd.forward(request, response); 
			        return;
				}
			}
		}
		logger.info(transid + ", show html login");
	}
	
	
%>

<%!
public static String getCookie(String transid, HttpServletRequest request, String name){
	Cookie c[] = request.getCookies(); 
	if(c != null && c.length > 0){
		for(Cookie a: c){
			if(a.getName().equalsIgnoreCase(name)){ 
				return a.getValue(); 
			}
		}
	}
	return null;
}

public static void setCookie(String transid, HttpServletResponse response, String key, String value, int hours){
	int expiryTime = 60 * 60 * hours;  // 7 days
	Cookie myCookie = new Cookie(key, value);
	myCookie.setMaxAge(expiryTime); 
	myCookie.setPath("/"); 
	response.addCookie(myCookie); 
}

public String getTableHTML(String transid, ArrayList<LinkedHashMap<String, String>> listRow){
	String str = "";
	try {
 		if(listRow == null || listRow.size() == 0){
	 		return "";
	 	}
 		str += ("<table style='border-collapse: collapse;' border='1'>");
 		
 		LinkedHashMap<String, String> tempRow = listRow.get(0);
 		for(LinkedHashMap<String, String> b: listRow){
 			if(b.size() > tempRow.size()){
 				tempRow = b;
 			}
 		}
	 	ArrayList<String> listColumnName = new ArrayList<String>(tempRow.keySet());
	 	int count = 0;
	 	
		for (String s: listColumnName) { 
			if(count == 0){
				str += "<th>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Service&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</th>";
			} else {
				str += "<th>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;URL" + count + "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</th>";
			}
			count ++;
		}
	 	for(LinkedHashMap<String, String> row: listRow){
	 		str += ("<tr>");
	 		
	 		Set<Entry<String, String>> set = row.entrySet();
	 		Iterator<Entry<String, String>> iter = set.iterator();
	 		int countShow = 0;
	 		
	 		while(iter.hasNext()){
	 			Entry<String, String> e = iter.next();
	 			countShow ++;
	 			
	 			str += ("<td>");
	 			if("xservice".equalsIgnoreCase(e.getKey())){
	 				str += (e.getValue());
	 			} else {
	 				String linkXSSH = e.getValue();
	 				linkXSSH = Encrypter.encrypt(linkXSSH);
	 				
	 				String linkRedirect = "?u=" + URLEncoder.encode(linkXSSH, "UTF-8"); 
		 			str += ("<a style=\"text-decoration:none;\" href='" + linkRedirect + "' target='_blank'>" + e.getKey() + "</a>");
	 			}
	 			str += ("</td>");
	 		}
	 		for(int i = 0; i < tempRow.size() - countShow; i ++){
	 			str += ("<td>");
	 			str += ("</td>");
	 		}
	 		str += ("<tr>");
	 	}
	 	str += ("</table>"); 
	 	
 	} catch (Exception e){
 		logger.info(transid + ", Exception: " + e.getMessage(), e);
 	}
	return str;
}  
%>
<!DOCTYPE html>
<html >
  <head>
    <meta charset="UTF-8">
    <title>Login form</title>
    <% 
    BrowserAgentInfo agentInfo = new BrowserAgentInfo(request);
		if(request.getParameter("m") != null || agentInfo.detectSmartphone()){
			out.write("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">");
		}
	%>
    <!--link href='http://fonts.googleapis.com/css?family=Lato:400,700' rel='stylesheet' type='text/css'-->
	<link rel="stylesheet" href="css/login/normalize.css"> 
    <link rel="stylesheet" href="css/login/style.css">  
  </head>
  <body>

    <section class="login-form-wrap">
	  <h1>login</h1>
	  <form class="login-form" method="POST" action="">
	  	  <b style="color: red;"><% out.print(msg); %></b>
	  	  
		  <input type="text" name="username" required placeholder="username">
		  <input type="password" name="password" required placeholder="password">
		  <input hidden="true" name="transid" value="<%= transid %>">
		  <input type="submit" value="Login">
	  </form> 
	</section> 
  </body>
</html>
