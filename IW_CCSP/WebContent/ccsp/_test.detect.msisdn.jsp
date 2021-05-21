<%@page import="com.ligerdev.appbase.utils.encrypt.AES"%>
<%@page import="com.xxx.aps.XmlConfigs"%>
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
// http://mhocvui.vn/ccsp/_test.detect.msisdn.jsp
// test trên dv hocvui
/* 

1: vào trang chủ, 
	-nếu có tham số link=j3bfj23f => thực hiện như mục 2 bên dưới
	-nếu ko có tham số này thực hiện tiếp các bước 
		check session xem có số đt ko, request.getSession(true).getAttribute("MSISDN");
		nếu = chuỗi rỗng => coi là ko nhận diện dc
		nếu có số => hiển thị Xin chào 0909090xxxx (xử lý xxxx ở cuối)
		nếu = null (chưa thử nhận diện) => redirect sang link X: http://free.mobifone.vn/isdn?sp=9359&link=@link
			String backUrl = "http://mhocvui.vn?";
			String linkX = new AES("RWuoxsZlODP8SUoD").encrypt(backUrl); 
			response.sendRedirect(linkX);
		
		khi rediret sang Link X, MBF sẽ redirect về link back với tham số link=12312hhv3
			http://mhocvui.vn?link=12312hhv3

2: nếu tham số link = rỗng => ko nhận diện dc, add session số đt = rỗng: 
		request.getSession(true).setAttribute("MSISDN", "");
		(ko nhận diện dc)

   nếu khác rỗng => giải mã: 
	   	String msisdn = new AES("RWuoxsZlODP8SUoD").decrypt(link); 
   		request.getSession(true).setAttribute("MSISDN", msisdn);
   		(Xin chào 090909xxxx)

 */

String transid = "TEST@" + StringGenerator.randomCharacters(10);
String link = request.getParameter("link");

if(link == null){
	String backUrl = "http://mhocvui.vn/ccsp/_test.detect.msisdn.jsp?";
	String encrt = new AES("RWuoxsZlODP8SUoD").encrypt(backUrl); 
	encrt = BaseUtils.urlEncode(encrt);
	String url = "http://free.mobifone.vn/isdn?sp=" + XmlConfigs.shortcode + "&link=" + encrt;
	sendRedirectORG(url, response);
} else {
	String encrt = new AES("RWuoxsZlODP8SUoD").decrypt(link); 
	out.print(encrt);
} 
%>
 

<%!
private static BaseDAO baseDAO = BaseDAO.getInstance("main");
private static XBaseDAO xbaseDAO = XBaseDAO.getInstance("main");
private static Logger logger = Logger.getLogger("LOG");

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







