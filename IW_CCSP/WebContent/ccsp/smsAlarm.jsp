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
    // 936248538, 909090529
// curl 'http://localhost:8888/ccsp/smsAlarm.jsp?content=testSmsAlarm'

    ArrayList<String> listMsisdn = new ArrayList<String>();
    {
        listMsisdn.add("841289103304");
        listMsisdn.add("84909090529");
    }

//    String msisdn = "841289103304";
//    msisdn = BaseUtils.formatMsisdn(msisdn, "84", "84");
    String transid = request.getParameter("transid");

    int moid = BaseUtils.parseInt(request.getParameter("moid"), 0);
    String mtcommand = request.getParameter("cmd");
    String content = request.getParameter("content");

    for (String msisdn : listMsisdn) {
        msisdn = BaseUtils.formatMsisdn(msisdn, "84", "84");
        if(BaseUtils.isBlank(transid)){
            transid = "SMS@" + StringGenerator.randomCharacters(5) + "@" + BaseUtils.formatMsisdn(msisdn, "84", "");
            logger.info(transid + ", ############ sendSMS: " + msisdn + "| content: " + content);
        } else {
            logger.info(transid + ", sendSMS: " + msisdn + "| content: " + content);
        }

        boolean saveDB = true;	 // mặc định là có saveDb, ko muốn lưu thì truyền saveDB=false
        if(request.getParameter("saveDB") != null){
            saveDB = "true".equalsIgnoreCase(request.getParameter("saveDB"));
        }
        if(saveDB){
            MtHis mt = new MtHis(0, msisdn, content, moid, transid, mtcommand, new Date(), "SMS");
            int rs = sendSMS1(transid, mt);
            out.print(rs + "|" + transid);
            return;
        }
        String temp = sendSMS0(transid, msisdn, content);
        int rs = String.valueOf(temp).contains("<return>OK</return>") ? 0 : 1;
        out.print(rs + "|" + transid);
    }

%>
<%!
    private static BaseDAO baseDAO = BaseDAO.getInstance("main");
    private static XBaseDAO xbaseDAO = XBaseDAO.getInstance("main");
    private static Logger logger = Logger.getLogger("LOG");
%>