<%@ page import="com.ligerdev.appbase.utils.BaseUtils" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="org.apache.log4j.Logger" %>
<%@ page import="com.ligerdev.appbase.utils.db.BaseDAO" %>
<%@ page import="java.util.*" %>
<%@ page import="java.util.Map.*" %>
<%@ page import="com.ligerdev.appbase.utils.db.AntTable" %>
<%@ page import="java.io.Serializable" %>
<%@ page import="com.ligerdev.appbase.utils.db.AntColumn" %>
<%@ page import="com.ligerdev.appbase.utils.textbase.StringGenerator" %>
<%@ page import="java.text.DecimalFormat" %>
<%--
  Created by IntelliJ IDEA.
  User: vietc
  Date: 10/29/2018
  Time: 4:39 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    logger.info(transid + "  " + "AAAA");
    String monthTemp = null;
    if (request.getParameter("month") != null) {
        monthTemp = request.getParameter("month");
    } else {
        monthTemp = BaseUtils.formatTime("yyyyMM", new Date());
    }

    String month = monthTemp;
    ArrayList<String> listDate = getDatesInMonth(month, "yyyy-MM-dd");

    logger.info(transid + "  " + monthTemp);
    String sql = null;
    ArrayList<LinkedHashMap<String, String>> data = null;
    ArrayList<LinkedHashMap<String, String>> data2 = null;
    Date date = BaseUtils.parseTime("yyyyMMdd", request.getParameter("date"), new Date());
    String dateStrORG = new SimpleDateFormat("yyyyMMdd").format(date);
    String dateStr = new SimpleDateFormat("yyyyMMdd").format(BaseUtils.addTime(date, Calendar.DATE, -7));
    sql = "select DATE_REPORT_S as Time,SUB_ACTIVE as 'Active',TOTAL_REGISTER as 'Đăng ký'," +
            "TOTAL_UNREGISTER as 'Hủy',REVENUE_TOTAL as 'Tổng'," +
            "LAST_PERIOD_REVENUE as 'Cùng kỳ tuần',REVENUE_CUMULATIVE as 'Tổng tháng',LAST_PERIOD_ACCUMULATIVE as 'Cùng kỳ'" +
            " from (select *, DATE_FORMAT(date_report, '%d/%m/%Y') DATE_REPORT_S, IFNULL((select REVENUE_CUMULATIVE from daily_summary b " +
            "where DATE_FORMAT(a.date_report - INTERVAL 1 MONTH , '%d/%m/%Y') = DATE_FORMAT(b.date_report, '%d/%m/%Y')),0) " +
            "LAST_PERIOD_ACCUMULATIVE, IFNULL((select REVENUE_TOTAL from daily_summary b " +
            "where DATE_FORMAT(SUBDATE(a.date_report,7), '%d/%m/%Y') = DATE_FORMAT(b.date_report, '%d/%m/%Y')),0) " +
            "LAST_PERIOD_REVENUE, IFNULL(( select ((select sum(counter) from stats_rscode where reason='RENEW' " +
            "and created_time=DATE_FORMAT(date_report, '%Y%m%d') and rs_code='CPS-0000')*100  / (select sum(counter) " +
            "from stats_rscode where reason='RENEW' and created_time=DATE_FORMAT(date_report, '%Y%m%d') and load_number < 0600))   ),   0  )" +
            " RENEW_RATIO from daily_summary a where DATE_FORMAT(date_report, '%Y-%m-%d') between DATE_FORMAT(NOW() - i" +
            "nterval 1 day,'%Y-%m-01') and SUBDATE(NOW(),1) and date_report>'20190609' order by date_report desc)as a;";
    data = baseDAO.getDataTableStr("", sql, null, null);
    String compareFirstTime = showTable2("", logger, data, listDate);


%>


<html>
<head>
    <meta charset="UTF-8"/>
    <meta name="viewport"
          content="width=device-width, user-scalable=no, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0"/>
    <meta http-equiv="X-UA-Compatible" content="ie=edge"/>
    <title>Statistical</title>
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/4.1.0/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://use.fontawesome.com/releases/v5.1.0/css/all.css"
          integrity="sha384-lKuwvrZot6UHsBSfcMvOkWwlCMgc0TaWr+30HWe3a4ltaBwTZhyTEggF5tJv8tbt" crossorigin="anonymous">
</head>
<style>
    table, td {
        border: 3px solid -moz-mac-accentdarkestshadow;
    }

    th {
        border: 3px solid -moz-mac-accentdarkestshadow;
        background: darkgray;
    }

</style>
<body>
<div style="width: 60%;">
    <h3>Bảng doanh thu</h3>
    <%= compareFirstTime %>


</div>


<%--<div style="text-align: center">--%>
<%--<br/><br/><br/>--%>
<%--___________________________________________________________________________________________________________________________________________<br/>--%>

<%--<a style='color: blue'>note: xem ngày khác, thêm parameter vào URL: ?date=yyyyMMdd</a>--%>
<%--<br/><br/><br/><br/>--%>
<%--</div>--%>
</body>
</html>
<%

    try {
        out.print("<br/><b>Doanh thu tháng:</b><br/>-------------------");
        Date timeTmp = new Date();
        for (; ; ) {
            int monthTmp = Integer.parseInt(BaseUtils.formatTime("yyyyMM", timeTmp));
            if (monthTmp < 201911) {
                break;
            }
            String monthStr = BaseUtils.formatTime("MM/yyyy", timeTmp);
            out.println("<br/>Tháng " + monthStr
                    + ":&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href='?month=" + monthTmp + "&detail' target='_blank'>Chi tiết</a>"
                    + "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
                    + ": <a href='?month=" + monthTmp + "' target='_blank'>Tổng hợp</a>"

            );
            timeTmp = BaseUtils.addTime(timeTmp, Calendar.MONTH, -1);
        }
    } catch (Exception e) {
        logger.info(transid + ", Exception: " + e.getMessage());
    }

%>
<%!
    public ArrayList<String> getDatesInMonth(String date, String outputFormat) {
        Date o = BaseUtils.parseTime("yyyyMM", date);
        ArrayList<String> list = new ArrayList<String>();

        int i = 0;
        while (date.equalsIgnoreCase(BaseUtils.formatTime("yyyyMM", o))) {
            if (o.after(new Date())) {
                break;
            }
            i++;
            if (i > 3) {
                //	break;// test
            }
            list.add(BaseUtils.formatTime(outputFormat, o));
            o = BaseUtils.addTime(o, Calendar.DATE, 1);
        }
        return list;
    }

    private long getTotalCol(ArrayList<String> dates, ArrayList<LinkedHashMap<String, String>> list, String valueCol, String dateCol) {
        long rs = 0;
        for (String date : dates) {
            rs += getValueInDate(date, list, valueCol, dateCol);
        }
        return rs;
    }

    private long getValueInDate(String date, ArrayList<LinkedHashMap<String, String>> list, String valueCol, String dateCol) {
        try {
            if (list == null || list.size() == 0) {
                return 0;
            }
            for (LinkedHashMap<String, String> m : list) {
                if ((date + "").equalsIgnoreCase(m.get(dateCol))) {
                    return Long.parseLong(m.get(valueCol));
                }
            }
        } catch (Exception e) {
        }
        return 0;
    }

    final String transid = "ahelp@" + StringGenerator.randomCharacters(6);
    Logger logger = Logger.getLogger("LOG");
    BaseDAO baseDAO = BaseDAO.getInstance("main");
    final DecimalFormat nf = new DecimalFormat("###,###,###,###");

    private String showTable1(String transid, Logger logger, ArrayList<LinkedHashMap<String, String>> listRow, ArrayList<String> listDate) {
        String str = "";
        try {
            if (listRow == null || listRow.size() == 0) {
                return "";
            }
            str += ("<table class='table table-striple' style='border-collapse: collapse;' border='1'>");
            LinkedHashMap<String, String> tmp = listRow.get(0);
            Iterator<Map.Entry<String, String>> iterator = tmp.entrySet().iterator();
//            str += ("<thead class='thead'>");

            str += ("<tr>");
            while (iterator.hasNext()) {
                Entry<String, String> entry = (Entry<String, String>) iterator.next();

                str += ("<th>" + entry.getKey() + "</th>");

            }
            str += ("</tr>");
//            str += ("</thead>");
            for (LinkedHashMap<String, String> row : listRow) {
                iterator = row.entrySet().iterator();
                str += ("<tr>");

                while (iterator.hasNext()) {
                    Entry<String, String> entry = (Entry<String, String>) iterator.next();
                    String value = entry.getValue();
                    String val = "";
                    try {
                        val = nf.format(Long.parseLong(value));

                    } catch (Exception e) {
                        val = value;
                    }
                    str += ("<td>" + val + "</td>");
                }
                str += ("<tr>");
            }
            str += ("<td>Tổng</td>");
            str += ("<td><b>" + nf.format(getTotalCol(listDate, listRow, "sub_A_success", "Time")) + "</b></td>");
// 	 	 	 		out.print("<td><b>" + nf.format(getTotalCol(listDate, doanhthu, "FEE_SMS", "NGAY")) + "</b></td>");
// 	 	 	 		out.print("<td><b>" + nf.format(getTotalCol(listDate, doanhthu, "FEE_VBC", "NGAY")) + "</b></td>");
// 	 	 	 		out.print("<td><b>" + nf.format(getTotalCol(listDate, doanhthu, "FEE_IVR", "NGAY")) + "</b></td>");

            str += ("<td><b>" + nf.format(getTotalCol(listDate, listRow, "share_B_success", "Time")) + "</b></td>");
            str += ("<td><b>" + nf.format(getTotalCol(listDate, listRow, "diff", "Time")) + "</b></td>");

// 	 	 	 		out.print("<td></td>");
// 	 	 	 		out.print("<td></td>");
// 	 	 	 		out.print("<td></td>");
            str += ("</tr>");
            str += ("</table>");

        } catch (Exception e) {
            logger.info(transid + ", Exception: " + e.getMessage());
        }
        return str;
    }

    private String showTable2(String transid, Logger logger, ArrayList<LinkedHashMap<String, String>> listRow, ArrayList<String> listDate) {
        String str = "";
        try {
            if (listRow == null || listRow.size() == 0) {
                return "";
            }
            str += ("<table class='table table-striple' style='border-collapse: collapse;' border='1'>");
            LinkedHashMap<String, String> tmp = listRow.get(0);
            Iterator<Map.Entry<String, String>> iterator = tmp.entrySet().iterator();
//            str += ("<thead class='thead'>");
            str += ("<tr>");
            str += ("<th>");
            str += ("</th>");
            str += ("<th colspan=\"3\" style=\"text-align: center\">");
            str += ("Thuê bao");
            str += ("</th>");
            str += ("<th colspan=\"2\" style=\"text-align: center\">");
            str += ("Doanh thu theo ngày");
            str += ("</th>");
            str += ("<th colspan=\"2\" style=\"text-align: center\">");
            str += ("Lũy kế tháng");
            str += ("</th>");


            str += ("</tr>");

            str += ("<tr>");
            while (iterator.hasNext()) {
                Entry<String, String> entry = (Entry<String, String>) iterator.next();

                str += ("<th>" + entry.getKey() + "</th>");

            }
            str += ("</tr>");
//            str += ("</thead>");
            for (LinkedHashMap<String, String> row : listRow) {
                iterator = row.entrySet().iterator();
                str += ("<tr>");

                while (iterator.hasNext()) {
                    Entry<String, String> entry = (Entry<String, String>) iterator.next();
                    String value = entry.getValue();
//                    Long val = Long.parseLong(value);
                    String val = "";
                    try {
                        val = nf.format(Long.parseLong(value));

                    } catch (Exception e) {
                        val = value;
                    }
                    str += ("<td>" + val + "</td>");
                }
                str += ("<tr>");
            }
//            str += ("<td>Tổng</td>");
//            str += ("<td><b>" + nf.format(getTotalCol(listDate, listRow, "Đăng ký", "Time")) + "</b></td>");
//
//            str += ("<td><b>" + nf.format(getTotalCol(listDate, listRow, "Hủy", "Time")) + "</b></td>");
//            str += ("<td><b>" + nf.format(getTotalCol(listDate, listRow, "Tổng", "Time")) + "</b></td>");
//            str += ("<td><b>" + nf.format(getTotalCol(listDate, listRow, "Cùng kỳ tuần", "Time")) + "</b></td>");
//            str += ("<td><b>" + nf.format(getTotalCol(listDate, listRow, "Tổng tháng", "Time")) + "</b></td>");
//            str += ("<td><b>" + nf.format(getTotalCol(listDate, listRow, "Cùng kỳ", "Time")) + "</b></td>");

// 	 	 	 		out.print("<td></td>");
// 	 	 	 		out.print("<td></td>");
// 	 	 	 		out.print("<td></td>");
            str += ("</tr>");
            str += ("</table>");

        } catch (Exception e) {
            logger.info(transid + ", Exception: " + e.getMessage());
        }
        return str;
    }

    private String showTable3(String transid, Logger logger, ArrayList<LinkedHashMap<String, String>> listRow, ArrayList<String> listDate) {
        String str = "";
        try {
            if (listRow == null || listRow.size() == 0) {
                return "";
            }
            str += ("<table class='table table-striple' style='border-collapse: collapse;' border='1'>");
            LinkedHashMap<String, String> tmp = listRow.get(0);
            Iterator<Map.Entry<String, String>> iterator = tmp.entrySet().iterator();
//            str += ("<thead class='thead'>");

            str += ("<tr>");
            while (iterator.hasNext()) {
                Entry<String, String> entry = (Entry<String, String>) iterator.next();

                str += ("<th>" + entry.getKey() + "</th>");

            }
            str += ("</tr>");
//            str += ("</thead>");
            for (LinkedHashMap<String, String> row : listRow) {
                iterator = row.entrySet().iterator();
                str += ("<tr>");

                while (iterator.hasNext()) {
                    Entry<String, String> entry = (Entry<String, String>) iterator.next();
                    String value = entry.getValue();
//                    Long val = Long.parseLong(value);
                    String val = "";
                    try {
                        val = nf.format(Long.parseLong(value));

                    } catch (Exception e) {
                        val = value;
                    }
                    str += ("<td>" + val + "</td>");
                }
                str += ("<tr>");
            }
            str += ("<td>Tổng</td>");
            str += ("<td><b>" + nf.format(getTotalCol(listDate, listRow, "count", "Time")) + "</b></td>");


// 	 	 	 		out.print("<td></td>");
// 	 	 	 		out.print("<td></td>");
// 	 	 	 		out.print("<td></td>");
            str += ("</tr>");
            str += ("</table>");

        } catch (Exception e) {
            logger.info(transid + ", Exception: " + e.getMessage());
        }
        return str;
    }
%>