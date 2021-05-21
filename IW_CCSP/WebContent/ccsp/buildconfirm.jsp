<%@page import="com.xxx.aps.processor.UpdateStatsRsCode"%>
<%@page import="com.xxx.aps.XmlConfigs"%>
<%@page import="com.ligerdev.appbase.utils.encrypt.AES"%>
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
<%@include file="_utils.app.jsp"%>   
<%-- <%@include file="_ccsp.client.jsp"%>    --%>

<%
// 936248538, 909090529
// http://x.ting.ting.vn/ccsp/buildconfirm.jsp?p=...&cp=WAP

if(request.getParameter("compile") != null){
	out.print("compile ok, v1.0");
	return;
}
try {
	CacheSyncFile cacheCCSP = CacheSyncFile.getInstance("CCSP", 10000);
	int today = Integer.parseInt(new SimpleDateFormat("yyyyMMdd").format(new Date()));
	String msisdn = request.getHeader("MSISDN");
	
	if(BaseUtils.isBlank(msisdn)){
		msisdn = request.getParameter("n");
		
		if(BaseUtils.parseLong(msisdn, -1L) > 0){
			msisdn = (Long.parseLong(msisdn) / 2) + "";
		}  
	} 
	msisdn = BaseUtils.formatMsisdn(msisdn, "84", "84"); 
	
	String pkgcode = request.getParameter("p");
	String ip = getFirstIPForwardedFor(request); 
	String cpid = request.getParameter("cp");
	String groupCpid = request.getParameter("gr");

	if(cpid == null){
		cpid = "2CX";
	}
	if(groupCpid == null){
		groupCpid = "WAP";
	}
	String transid = "BCF@" + StringGenerator.randomCharacters(5) + "@" + BaseUtils.formatMsisdn(msisdn, "84", "");
	logger.info(transid + ", ############ buildconfirm: " + msisdn + "| queryStr: " + request.getQueryString());
	logger.info(transid + ", referer: " +  request.getHeader("Referer") + " | IP: " + ip);  
	// getHeaders(transid, request, true);
	
	StatsRscode stats = new StatsRscode(today, "TOTAL", 1, -1, groupCpid, 0, pkgcode, "RedirectXT", cpid, "#", "#");   
	UpdateStatsRsCode.queue.put(stats);
	
	/* if(msisdn == null){ 
		String url = "http://mhocvui.vn?"; 
		response.sendRedirect(url);
		logger.info(transid + ", redirect to: " + url);
		
		stats = new StatsRscode(today, "BLANK", 1, -1, groupCpid, 0, pkgcode, "RedirectXT", cpid, "#", "#");   
		UpdateStatsRsCode.queue.put(stats);
		return;
	} */
	if(BaseUtils.isBlank(pkgcode)){
		out.print("parameter not found");
		logger.info(transid + ", blank parameter");
		return;
	} 
	stats = new StatsRscode(today, "MSISDN", 1, -1, groupCpid, 0, pkgcode, "RedirectXT", cpid, "#", "#");   
	UpdateStatsRsCode.queue.put(stats); 
	
	String cpKey = "Vny5fgACqu6lGsbq";
	String domain = "iwing.vn?" + StringGenerator.randomCharacters(6);  
	String info = "Mien Phi 01 ngay";

	String reqid = "1" + StringGenerator.randomDigits(9); 
	String data = reqid + "&DK " + pkgcode + "&" + pkgcode  + "&http://" + domain + "&" + info;
	  
	if(msisdn != null){  
		// thử tính rule 10 phút để log
		String key10minute = "key10minute." + msisdn;
		
		if(cacheCCSP.getObject(key10minute) != null){
			// dưới 10 phút
			logger.info(transid + ", rerequest less than xMinute");
			stats = new StatsRscode(today, "LT10", 1, -1, groupCpid, 0, pkgcode, "RedirectXT", cpid, "#", "#");   
			UpdateStatsRsCode.queue.put(stats);
		} else {
			logger.info(transid + ", rerequest more than xMinute");
			stats = new StatsRscode(today, "GT10", 1, -1, groupCpid, 0, pkgcode, "RedirectXT", cpid, "#", "#");   
			UpdateStatsRsCode.queue.put(stats);
		}
		cacheCCSP.put(key10minute, "1", 60 * 10 + 30); 
		
		// put cache để khi nhận hàm đăng ký lấy ở cache ra dc CPID (hiện chưa dùng)
		String key = msisdn + "." + pkgcode.toUpperCase();
		cacheCCSP.put(key, cpid + "@" + groupCpid, 120); 
		logger.info(transid + ", put cache: " + key); 
	}
	logger.info(transid + ", data before encrypt: " + data);
	String encode = new AES(cpKey).encrypt(data);
	// String url = "http://free.mobifone.vn/confirm?sp=9443&link=" + URLEncoder.encode(encode);  

	logger.info(transid + ", encode: " + encode);
	String url = "http://free.mobifone.vn/confirm?sp=" + XmlConfigs.shortcode + "&link=" + encode; 
	logger.info(transid + ", URL: " + url);

	if(request.getParameter("show") != null){
		try{
			response.getWriter().write(data + "<br/>");
			response.getWriter().write(encode + "<br/>");
			response.getWriter().write(url + "<br/>");
		}catch(Exception e){} 
		return;
	}
	sendRedirectORG(url, response);
	
} catch (Exception e){
	out.print("wrong parameter");
	logger.info("wrong parameter");
	return;
}
%>
 

<%!
public void sendRedirectORG(String linkRedirect, HttpServletResponse response) throws IOException{ 
	PrintWriter out = response.getWriter();
	out.write("<html>");
	out.write("<head>");
	out.write("<meta name=\"referrer\" content=\"origin\" />"); // REF chỉ có domain
	out.write("</head>");
	out.write("<body>");
	out.write("		<script>"); 
	out.write("			window.location=\"" + linkRedirect + "\"");  
	out.write("		</script>");
	out.write("</body>"); 
	out.write("</html>");
	out.close();
} 
%>