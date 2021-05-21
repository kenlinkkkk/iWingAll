<%@page import="com.xxx.aps.logic.db.orm.PkgPolicy"%>
<%@page import="com.google.gson.GsonBuilder"%>
<%@page import="com.google.gson.Gson"%>
<%@page import="com.ligerdev.appbase.utils.entities.PairStringString"%>
<%@page import="java.util.Map.Entry"%>
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

<%@ page language="java" contentType="text/plain; charset=UTF-8"  pageEncoding="UTF-8"%>
<%@ page trimDirectiveWhitespaces="true" %> 
<%-- <%@include file="_utils.app.jsp"%>    --%>
<%-- <%@include file="_ccsp.client.jsp"%>    --%>

<%
// 936248538, 909090529
// http://115.146.127.95/apiamobi/getpack.jsp?service=funtv

if(request.getParameter("compile") != null){
	out.print("compile ok, v1.0");
	return;
}
String transid = "GETPACK@" + StringGenerator.randomCharacters(5);
logger.info(transid + ", ############ getpack: " + request.getQueryString() + ", ip: " + request.getRemoteAddr());
 
String sql = "select * from pkg_policy where status = 1 order by order1";
ArrayList<PkgPolicy> list = xbaseDAO.getListBySql(transid, PkgPolicy.class, sql, null, null);
SysPackage p = null;

if(list == null || list.size() == 0){
	p = new SysPackage(404, "not found", null);
} else {
	p = new SysPackage(0, "success", list);
}
String s = prettyGson.toJson(p);
out.print(s);
%>
 

<%!
private static Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
private static Gson prettyGson = new GsonBuilder().setDateFormat("yyyy-MM-dd").setPrettyPrinting().create();
private static BaseDAO baseDAO = BaseDAO.getInstance("main");
private static XBaseDAO xbaseDAO = XBaseDAO.getInstance("main");
private static Logger logger = Logger.getLogger("LOG");

public class SysPackage {

	private int code;
	private String desc;
	private ArrayList<PkgPolicy> listPack;

	public SysPackage() {
		// TODO Auto-generated constructor stub
	}

	public SysPackage(int code, String desc, ArrayList<PkgPolicy> listPack) {
		super();
		this.code = code;
		this.desc = desc;
		this.listPack = listPack;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public ArrayList<PkgPolicy> getListPack() {
		return listPack;
	}

	public void setListPack(ArrayList<PkgPolicy> listPack) {
		this.listPack = listPack;
	}
}
%>