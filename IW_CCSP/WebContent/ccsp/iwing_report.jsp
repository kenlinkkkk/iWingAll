<%@page import="com.ligerdev.appbase.utils.traffic.SendTraffic" %>
<%@page import="com.ligerdev.appbase.utils.db.BaseDAO" %>
<%@page import="com.ligerdev.appbase.utils.encrypt.Encrypter" %>
<%@page import="com.ligerdev.appbase.utils.http.HttpClientUtils" %>
<%@page import="com.ligerdev.appbase.utils.encrypt.XBase64" %>
<%@page import="com.ligerdev.appbase.utils.cache.CacheSyncFile" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@page import="com.ligerdev.appbase.utils.encrypt.AES" %>
<%@page import="com.ligerdev.appbase.utils.BaseUtils" %>
<%@page import="com.ligerdev.appbase.utils.textbase.StringGenerator" %>
<%@page import="com.ligerdev.appbase.utils.textbase.Log4jLoader" %>
<%@page import="com.ligerdev.appbase.utils.db.BaseDAO" %>
<%@page import="java.math.BigDecimal" %>
<%@page import="java.lang.reflect.Method" %>
<%@ page import="org.w3c.dom.Element" %>
<%@ page import="java.io.*" %>
<%@ page import="java.sql.*" %>
<%@ page import="org.w3c.dom.*" %>
<%@ page import="java.util.regex.*" %>
<%@ page import="java.net.*" %>
<%@ page import="java.util.*" %>
<%@ page import="java.util.Date" %>
<%@ page import="java.text.*" %>
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

  synchronized (xbasedao) {
    String sql = null;
    try {
      String transid = StringGenerator.randomCharacters(8);
      ArrayList<LinkedHashMap<String, String>> data = null;

      String month = request.getParameter("month");
      if (BaseUtils.isBlank(month)) {
        month = new SimpleDateFormat("yyyyMM").format(new Date());
      }
      ArrayList<String> listDate = getDatesInMonth(month);

      sql = "select hisnote1 NGAY, " +
              "COALESCE(sum(case when action in ('FirstREG','ReREG') and cpid='ID' then 1 else 0 end), 0) reg_id, " +
              "COALESCE(sum(case when action in ('FirstREG','ReREG') and cpid='TK' then 1 else 0 end), 0) reg_tk, " +
              "COALESCE(sum(case when action in ('FirstREG','ReREG') and cpid='DK ID' then 1 else 0 end), 0) reg_dkid, " +
              "COALESCE(sum(case when action in ('FirstREG','ReREG') and cpid in ('DK TK', 'KT') then 1 else 0 end), 0) reg_kt, " +
              "COALESCE(sum(case when action in ('FirstREG','ReREG') and cpid='KM ID ID' then 1 else 0 end), 0) reg_avb, " +
              "COALESCE(sum(case when action in ('FirstREG','ReREG') and cpid in ('ID', 'TK', 'DK ID', 'KT', 'DK TK', 'KM ID ID') then 1 else 0 end), 0) reg_total, " +
              "COALESCE(sum(case when action in ('UNREG') and msisdn in (select distinct(msisdn) " +
              "from his_"+month+" where action in ('FirstREG','ReREG') and cpid='ID' and date(created_time) = date(DATE_ADD(a.created_time,INTERVAL -1 day))) then 1 else 0 end),0) unreg_id_24h, " +
              "COALESCE(sum(case when action in ('UNREG') and msisdn in (select distinct(msisdn) " +
              "from his_"+month+" where action in ('FirstREG','ReREG') and cpid='TK' and date(created_time) = date(DATE_ADD(a.created_time,INTERVAL -1 day))) then 1 else 0 end),0) unreg_tk_24h, " +
              "COALESCE(sum(case when action in ('UNREG') and msisdn in (select distinct(msisdn) " +
              "from his_"+month+" where action in ('FirstREG','ReREG') and cpid='DK ID' and date(created_time) = date(DATE_ADD(a.created_time,INTERVAL -1 day))) then 1 else 0 end),0) unreg_dkid_24h, " +
              "COALESCE(sum(case when action in ('UNREG') and msisdn in (select distinct(msisdn) " +
              "from his_"+month+" where action in ('FirstREG','ReREG') and cpid in ('DK TK', 'KT') and date(created_time) = date(DATE_ADD(a.created_time,INTERVAL -1 day))) then 1 else 0 end),0) unreg_kt_24h, " +
              "COALESCE(sum(case when action in ('UNREG') and msisdn in (select distinct(msisdn) " +
              "from his_"+month+" where action in ('FirstREG','ReREG') and cpid='KM ID ID' and date(created_time) = date(DATE_ADD(a.created_time,INTERVAL -1 day))) then 1 else 0 end),0) unreg_avb_24h, " +
              "COALESCE(sum(case when action in ('UNREG') and msisdn in (select distinct(msisdn) " +
              "from his_"+month+" where action in ('FirstREG', 'ReREG') and cpid in ('ID', 'TK', 'DK ID','KT', 'DK TK', 'KM ID ID') and date(created_time) = date(date_add(a.created_time, interval -1 day))) then 1 else 0 end), 0) unreg_total, " +
              "COALESCE(sum(case when action in ('FirstREG','ReREG') and cpid='ID' then fee else 0 end), 0) quantity_reg_id, " +
              "COALESCE(sum(case when action in ('FirstREG','ReREG') and cpid='TK' then fee else 0 end), 0) quantity_reg_tk, " +
              "COALESCE(sum(case when action in ('FirstREG','ReREG') and cpid='DK ID' then fee else 0 end), 0) quantity_reg_dkid, " +
              "COALESCE(sum(case when action in ('FirstREG','ReREG') and cpid in ('DK TK', 'KT') then fee else 0 end), 0) quantity_reg_kt, " +
              "COALESCE(sum(case when action not in ('UNREG') and msisdn in (select distinct(msisdn) from his_"+month+" where cpid='KM ID ID' and date(created_time) = date(date_add(a.created_time, interval -1 day))) then fee else 0 end), 0) quantity_avb, " +
              "COALESCE(sum(case when action not in ('UNREG') then fee else 0 end),0) total " +
              "from his_"+month+" a group by NGAY order by NGAY desc";

      String unit = request.getParameter("unit");
      data = baseDAO.getDataTableStr("", sql, null, null);

      String header = "<th colspan='20'>" + serviceName + "</th>";
      header += "<tr class='text-center'><th rowspan='3'>Ngày</th><th colspan='5'>TB đăng ký</th><th rowspan='3'>Tổng ĐK</th><th colspan='5'>TB hủy (24h)</th><th rowspan='3'>Tổng hủy</th><th colspan='5'>Doanh thu khách hàng</th><th rowspan='3'>Tổng doanh thu</th><th rowspan='3'>Doanh thu lũy kế</th></tr>\n" +
              "\t<tr class='text-center'><th colspan='2'>LiveInfo ads</th><th>SMS 090</th><th>Nanova</th><th>AVB</th><th colspan='2'>LiveInfo ads</th><th>SMS 090</th><th>Nanova</th><th>AVB</th><th colspan='2'>LiveInfo ads</th><th>SMS 090</th><th>Nanova</th><th>AVB</th></tr>\n" +
              "\t<tr class='text-center'><th>DK ID</th><th>DK TK</th><th>ID</th><th>TK</th><th>KM ID</th><th>DK ID</th><th>DK TK</th><th>ID</th><th>TK</th><th>KM ID</th><th>DK ID</th><th>DK TK</th><th>ID</th><th>TK</th><th>KM ID</th></tr>";
      reportWAP = showTable1("", logger, data, header, listDate, unit, "reg_dkid", "reg_kt", "reg_id", "reg_tk", "reg_avb","reg_total", "unreg_dkid_24h", "unreg_kt_24h", "unreg_id_24h", "unreg_tk_24h", "unreg_avb_24h","unreg_total", "quantity_reg_dkid", "quantity_reg_kt","quantity_reg_id", "quantity_reg_tk", "quantity_avb", "total");
    } catch (Exception e) {
      out.print("Exception: SQL: " + sql + " | exceptionMsg: " + e.getMessage());
      reportWAP = serviceName;
    }
  }
%>


<html>
<head>
  <style>
    td {
      min-width:80px;
    }
    .text-center { text-align: center; }
    .text-end { text-align: end;}
  </style>
</head>

<body>
<%= reportWAP %>
<br/><br/>
</body>
</html>


<%!
  Logger logger = Logger.getLogger("LOG");
  BaseDAO baseDAO = BaseDAO.getInstance("main");

  public ArrayList<String> getDatesInMonth(String month) {
    Date o = null;
    SimpleDateFormat yyyyMMdd = new SimpleDateFormat("yyyyMMdd");
    try {
      o = yyyyMMdd.parse(month + "01");
    } catch (Exception e) {}

    ArrayList<String> list = new ArrayList<String>();

    int i = 0;
    while (month.equalsIgnoreCase(BaseUtils.formatTime("yyyyMM", o))) {
      if (yyyyMMdd.format(o).compareTo(yyyyMMdd.format(new Date())) > 0) {
        break;
      }
      list.add(BaseUtils.formatTime("yyyyMMdd", o));
      o = BaseUtils.addTime(o, Calendar.DATE, 1);
    }
    return list;
  }

  private String findValue(ArrayList<LinkedHashMap<String, String>> listRow, String date, String column) {
    if (listRow == null || listRow.size() == 0) {
      return "0";
    }
    for (LinkedHashMap<String, String> row : listRow) {
      String dateDB = String.valueOf(row.get("NGAY")).replace("-", "");

      if (date.equalsIgnoreCase(dateDB)) {
        return row.get(column);
      }
    }
    return "0";
  }

  private String showTable1(String transid, Logger logger, ArrayList<LinkedHashMap<String, String>> listRow,
                            String header, ArrayList<String> listDate, String unitStr, String... columns) {
    String str = "";
    int unit = BaseUtils.parseInt(unitStr, 0);
    try {
      str += ("<table style='border-collapse: collapse;' border='1'>");
      str += header;
      Long total = 0L;
      Hashtable<String, Long> totalMap = new Hashtable<String, Long>();
      NumberFormat nf = new DecimalFormat("###,###,###,###");

      for (String date : listDate) {
        int count = 1;
        str += ("<tr> <td class='text-center'>"+ formatDate(date) +"</td>");

        for (String column : columns) {
          String value = findValue(listRow, date, column);

          if (unit > 0) {
            String s = formatFloat((float) BaseUtils.parseLong(value, 0L) / unit, 1);
            if (BaseUtils.parseFloat(s, 0F) <= 0) {
              s = "<b style='color: red'>" + s + "<b>";
            }
            str += ("<td class='text-end'>" + s + "</td>");
          } else {
            str += ("<td class='text-end'>" + nf.format(BaseUtils.parseLong(value, 0L)) + "</td>");
          }
          if (column.equalsIgnoreCase("total")) {
            total += BaseUtils.parseLong(value, 0L);
          }
        }
        str += ("<td class='text-end'>"+ nf.format(total) +"</td>");
        str += ("<tr>");
      }
      str += ("</table>");

    } catch (Exception e) {
      logger.info(transid + ", Exception: " + e.getMessage());
    }
    return str;
  }

  public static String formatFloat(Float value, int temp) {
    if (value == null) {
      return null;
    }
    return String.format("%." + temp + "f", value);
  }

  public static String formatDate(String dateStr) throws ParseException {
    Date date = StringToDate(dateStr);
    return new SimpleDateFormat("dd/MM/yyyy").format(date);
  }

  public static Date StringToDate(String dob) throws ParseException {
    //Instantiating the SimpleDateFormat class
    SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
    //Parsing the given String to Date object
    return formatter.parse(dob);
  }
%>