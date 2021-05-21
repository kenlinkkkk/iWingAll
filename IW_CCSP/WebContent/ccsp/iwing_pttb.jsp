<%@page import="com.ligerdev.appbase.utils.traffic.SendTraffic"%>
<%@page import="com.ligerdev.appbase.utils.db.BaseDAO"%>
<%@page import="com.ligerdev.appbase.utils.encrypt.Encrypter"%>
<%@page import="com.ligerdev.appbase.utils.http.HttpClientUtils"%>
<%@page import="com.ligerdev.appbase.utils.encrypt.XBase64"%>
<%@page import="com.ligerdev.appbase.utils.cache.CacheSyncFile"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"  pageEncoding="UTF-8"%>
<%@page import="com.ligerdev.appbase.utils.encrypt.AES"%>
<%@page import="com.ligerdev.appbase.utils.BaseUtils"%>
<%@page import="com.ligerdev.appbase.utils.textbase.StringGenerator"%>
<%@page import="com.ligerdev.appbase.utils.textbase.Log4jLoader"%>
<%@page import="com.ligerdev.appbase.utils.db.BaseDAO"%>
<%@page import="java.math.BigDecimal"%>
<%@page import="java.lang.reflect.Method"%>
<%@ page import="org.w3c.dom.Element"%> 
<%@ page import="java.io.*"%>
<%@ page import="java.sql.*"%>
<%@ page import="org.w3c.dom.*"%>
<%@ page import="java.util.regex.*" %>
<%@ page import="java.net.*" %>
<%@ page import="java.util.*" %>
<%@ page import="java.util.Date" %>
<%@ page import="java.text.*"%>
<%@ page import="org.apache.log4j.*" %> 
<%@ page import="java.util.Map.*" %>
<%@ page import="java.util.*" %>
<%@ page import="java.awt.datatransfer.*" %>

<%@ page trimDirectiveWhitespaces="true" %>
<%-- <%@include file="FunringUtils.jsp"%>  --%>
<%-- <%@include file="XAuthenFunctions.jsp"%>  --%>
 
 
<%!
	BaseDAO xbasedao = BaseDAO.getInstance("main");
%>

<%
	String reportWAP = null;
	String serviceName = "IWING";
	// http://m.m14.vn/cregwap/TS_REPORT_FEE.jsp?type=all

	synchronized (xbasedao){ 
		String sql = null;
		try {
			String transid = StringGenerator.randomCharacters(8);
			ArrayList<LinkedHashMap<String, String>> data = null;
			
			String month = request.getParameter("month");
			if(BaseUtils.isBlank(month)){
				month = new SimpleDateFormat("yyyyMM").format(new Date());
			}
			ArrayList<String> listDate = getDatesInMonth(month);
			
			sql = "select hisnote1 NGAY"  
					+ ", COALESCE(sum(fee), 0) FEE " 
					+ ", COALESCE(sum(case when action in ('FirstREG', 'ReREG') then 1 else 0 end), 0) REG " 
					+ ", COALESCE(sum(case when action in ('UNREG', 'DELETE') then 1 else 0 end), 0) UNREG " 
					+ ", COALESCE(sum(case when action in ('RENEW') and result = 0 then 1 else 0 end), 0) CHARGE " 
					+ ", COALESCE(sum(case when action in ('ACTIVE2') then regdate else 0 end), 0) SUBs " 
			  	+ "from his_" + month + " where result = 0 group by NGAY order by NGAY desc";
			
			String unit = request.getParameter("unit");
			data = baseDAO.getDataTableStr("", sql, null, null);

			String header = "<th colspan='5'>" + serviceName + "</th>";
			header += "<tr>  <td>FEE</td>  <td>REG</td>  <td>UNREG</td>  <td>Charge</td>  <td>Active</td>  </tr>";
			reportWAP = showTable1("", logger, data, header, listDate, unit, "FEE", "REG", "UNREG", "CHARGE", "SUBs");   
			reportWAP = reportWAP.replace("FEE", "Doanh Thu").replace("UNREG", "Hủy").replace("REG", "ĐK");  
			
		} catch (Exception e){
			out.print("Exception: SQL: " + sql + " | exceptionMsg: " + e.getMessage());
			reportWAP = serviceName;
		}
	}
%>


<html>
<body> 
	<table border="0" style='border-collapse: collapse;'>
		<tr>
			<td style='vertical-align: top;'> 
					 <%= reportWAP %>
			</td>
		</tr>
	</table>
	<br/><br/>
</body> 
</html>
 
 
<%! 
Logger logger = Logger.getLogger("LOG");
BaseDAO baseDAO = BaseDAO.getInstance("main");

public ArrayList<String> getDatesInMonth(String month){
	Date o = null;
	SimpleDateFormat yyyyMMdd = new SimpleDateFormat("yyyyMMdd");
	try {
		o = yyyyMMdd.parse(month + "01"); 
	} catch (Exception e) {}
	
	ArrayList<String> list = new ArrayList<String> ();
	
	int i = 0;
	while(month.equalsIgnoreCase(BaseUtils.formatTime("yyyyMM", o))){
		if(yyyyMMdd.format(o).compareTo(yyyyMMdd.format(new Date())) > 0){
			break;
		}
		list.add(BaseUtils.formatTime("yyyyMMdd", o));
		o = BaseUtils.addTime(o, Calendar.DATE, 1); 
	}
	Collections.reverse(list);
	return list;
}

private String findValue(ArrayList<LinkedHashMap<String, String>> listRow, String date, String column){
	if(listRow == null || listRow.size() == 0){
		return "0";
	}
	for(LinkedHashMap<String, String> row: listRow){
		String dateDB = String.valueOf(row.get("NGAY")).replace("-", "");
		
		if(date.equalsIgnoreCase(dateDB)){
			return row.get(column);
		}
	}
	return "0";
}

private String showTable1(String transid, Logger logger, ArrayList<LinkedHashMap<String, String>> listRow, 
		String header, ArrayList<String> listDate, String unitStr, String ... columns){
	String str = "";
	int unit = BaseUtils.parseInt(unitStr, 0);
	try {
 		str += ("<table style='border-collapse: collapse;' border='1'>");
		str += header;
		
		Hashtable<String, Long> totalMap = new Hashtable<String, Long>();
		NumberFormat nf = new DecimalFormat("###,###,###,###");
		
		for(String date: listDate){
			str += ("<tr>");
			
			for(String column: columns){
				String value = findValue(listRow, date, column);
				
				if(unit > 0){
					String s = formatFloat((float)BaseUtils.parseLong(value, 0L)/unit, 1);
					if(BaseUtils.parseFloat(s, 0F) <= 0){
						s = "<b style='color: red'>" + s + "<b>";
					} 
					str += ("<td>" + s + "</td>"); 
				} else {
					str += ("<td>" + nf.format(BaseUtils.parseLong(value, 0L)) + "</td>"); 
				}
	            Long temp = totalMap.get(column);
	            temp = temp == null ? 0L: temp;
	            totalMap.put(column, temp + BaseUtils.parseLong(value, 0L));
			}
			str += ("<tr>");
		}
		str += "<tr>";
		for(String column: columns){
			Long total = totalMap.get(column);
			total = total == null ? 0L: total;
			
			if(unit > 0){
		 		String s = formatFloat((float)total/unit, 1);
		 		str += "<td><b>" + s + "</b></td>"; 
		 	} else {
		 		str += "<td><b>" + nf.format(total) + "</b></td>"; 
		 	}
		}
		str += "</tr>";
	 	str += ("</table>");
	 	
 	} catch (Exception e){
 		logger.info(transid + ", Exception: " + e.getMessage());
 	}
	return str;
}  

public static String formatFloat(Float value, int temp) {
	if(value == null) {
		return null;
	}
	return String.format("%." + temp + "f",  value);
}
%>