<%@ page language="java" contentType="application/json; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.ligerdev.appbase.utils.db.AntTable" %>
<%@ page import="java.io.Serializable" %>
<%@ page import="com.ligerdev.appbase.utils.db.AntColumn" %>
<%@ page import="com.ligerdev.appbase.utils.db.XBaseDAO" %>
<%@ page import="com.ligerdev.appbase.utils.db.BaseDAO" %>
<%@ page import="com.ligerdev.appbase.utils.textbase.StringGenerator" %>
<%@page import="com.ligerdev.appbase.utils.BaseUtils" %>
<%@ page import="org.apache.log4j.Logger" %>
<%@ page import="java.text.DateFormat" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.text.ParseException" %>
<%@ page import="com.xxx.aps.logic.db.orm.Subscriber" %>
<%@ page import="com.google.gson.Gson" %>
<%@ page import="java.util.*" %>

<%!
  BaseDAO xbasedao = BaseDAO.getInstance("main");
%>

<%
  String rs = "-1";
  synchronized (xbasedao) {
    if(request.getParameter("compile") != null){
      out.print("compile ok, v1.0");
      return;
    }

    String transid = "REPORT@" + StringGenerator.randomCharacters(5) + "@";
    String month = request.getParameter("day");

    if (BaseUtils.isBlank(month)) {
        month = new SimpleDateFormat("yyyyMM").format(new Date());
    }
    ArrayList<LinkedHashMap<String, String>> data = null;
    try {
      String sql = "select date_format(str_to_date(hisnote1,'%Y%m%d'), '%m/%d/%Y') date_report, " +
              "coalesce(sum(case when pkg_code='ID' and action not in ('UNREG') then fee else 0 end), 0) total, " +
              "coalesce(sum(case when action in ('FirstREG','ReREG') then 1 else 0 end),0) reg_day, " +
              "coalesce(sum(case when action in ('UNREG') then 1 else 0 end),0) unreg_day " +
              "from his_"+ month +" where hisnote1 = date_format(date_add(now(), interval -1 day), '%Y%m%d') group by date_report order by date_report asc";

      data = baseDAO.getDataTableStr("", sql, null, null);
      Gson gson = new Gson();
      rs = gson.toJson(data.get(0));
    } catch (Exception e) {}

//  Map<String, Object> result = new HashMap<String, Object>();
//  result.put("code", 1);
//  result.put("message", "List Subs topup");
//  if (list.size() == 0) {
//    result.put("data", null);
//  } else {
//    result.put("data", list.get(0));
//  }


  }
%>

<%!
  BaseDAO baseDAO = BaseDAO.getInstance("main");
  private static Logger logger = Logger.getLogger("LOG");

  public static String formatDate(String dateStr, String type){
    try {
      Date date = new SimpleDateFormat("yyyyMMddHHmmss").parse(dateStr);
      if (type.equals("day")) {
        return new SimpleDateFormat("yyyyMMdd").format(date);
      } else {
        return new SimpleDateFormat("yyyyMM").format(date);
      }
    } catch (Exception e) {
      return "1";
    }
  }

  class ObjResult {
    private String date;
    private Long total;
    private Long reg_day;
    private Long unreg_day;

    public ObjResult() {
    }

    public ObjResult(String date, Long total, Long reg_day, Long unreg_day) {
      this.date = date;
      this.total = total;
      this.reg_day = reg_day;
      this.unreg_day = unreg_day;
    }

    public String getDate() {
      return date;
    }

    public void setDate(String date) {
      this.date = date;
    }

    public Long getTotal() {
      return total;
    }

    public void setTotal(Long total) {
      this.total = total;
    }

    public Long getReg_day() {
      return reg_day;
    }

    public void setReg_day(Long reg_day) {
      this.reg_day = reg_day;
    }

    public Long getUnreg_day() {
      return unreg_day;
    }

    public void setUnreg_day(Long unreg_day) {
      this.unreg_day = unreg_day;
    }

    @Override
    public String toString() {
      return "ObjResult{" +
              "date='" + date + '\'' +
              ", total=" + total +
              ", reg_day=" + reg_day +
              ", unreg_day=" + unreg_day +
              '}';
    }
  }
%>

<%=rs%>