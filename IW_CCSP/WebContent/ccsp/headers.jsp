<%@page import="org.apache.log4j.Logger"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<%@ page import="java.io.*"%>
<%@ page import="java.net.*"%> 
<%@ page import="java.util.regex.*" %>
<%@ page import="java.net.*" %> 
<%@ page import="java.util.*" %>
<%@ page import="java.util.Map.*" %>
<%@ page import="java.util.Date" %>
<%@ page trimDirectiveWhitespaces="true" %>

<%  
	if(request.getParameter("compile") != null){
		out.print("compile ok, v1.0");
		return;
	}
	// ISO-8859-1
	response.setHeader("Access-Control-Allow-Origin", "*"); 
	// response.addHeader("X-Frame-Options", "DENY");    
	// response.setHeader("X-Frame-Options", "SAMEORIGIN"); // DENY or SAMEORIGIN
	Logger logger = Logger.getLogger("LOG");
	logger.info("======== show headers - queryStr: " + request.getQueryString());
	Enumeration<String> enumHeaders = request.getHeaderNames();
	
	response.setContentType("text");
	response.setCharacterEncoding("UTF-8");
	request.setCharacterEncoding("UTF-8");
	
	response.getWriter().write("v10 ======== headers\n");
	while(enumHeaders.hasMoreElements()){
		String key = enumHeaders.nextElement();
		Enumeration<String> enums = request.getHeaders(key);
		
		while(enums.hasMoreElements()){
			String value = enums.nextElement();
			String str = "header: " + key + "=" + value;
			response.getWriter().write(str + "\n");
		}
	}
	
	response.getWriter().write("======== params\n");
	Map<String, String[]> map = request.getParameterMap();
	Set<Entry<String, String[]>> set = map.entrySet();
	Iterator<Entry<String, String[]>> iter = set.iterator();
	
	while(iter.hasNext()){
		Entry<String, String[]> e = (Entry<String, String[]>) iter.next();
		String key = e.getKey();
		String value[] = e.getValue();
		
		for(int i = 0 ; value != null && i < value.length; i ++){
			String str = "param: " + key + "=" + value[i];
			// String str = "param: " + key + "=" + request.getParameter(key);
			// String str = "param: " + key + "=" + new String(request.getParameter(key).getBytes("ISO-8859-1"), "UTF-8");
			response.getWriter().write(str + "\n"); 
		}
	}
	boolean isPost = "POST".equals(request.getMethod());
	response.getWriter().write("======== method: " + request.getMethod());
	
	if(isPost){
		String body = readInputStream(request.getInputStream());
		response.getWriter().write("body: " + body);
	}
%>

<%!
public static String readInputStream(InputStream is){
	InputStreamReader rd = null;
	BufferedReader in = null;
	try {
		rd = new InputStreamReader(is, "UTF-8");
		in = new BufferedReader(rd);
		String line;
		String all = "";
		while ((line = in.readLine()) != null) {
			all += "\n" + line;
		}
		all = all.replaceFirst("\n", "");
		return all;
	} catch (Exception e) {
	} finally {
		if(in != null){
			try {
				in.close();
			} catch (Exception e2) {
			}
		}
		if(rd != null){
			try {
				rd.close();
			} catch (Exception e2) {
			}
		}
		if(is != null){
			try {
				is.close();
			} catch (Exception e2) {
			}
		}
	}
	return null;
}
%>


