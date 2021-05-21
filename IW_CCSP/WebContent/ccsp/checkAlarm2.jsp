<%@page import="com.ligerdev.appbase.monitor.MNTUtils"%>
<%@page import="com.ligerdev.appbase.monitor.MNTRunCmd"%>
<%@page import="com.ligerdev.appbase.utils.entities.PairStringString"%>
<%-- <%@page import="org.apache.commons.io.FileUtils"%> --%>
<%@page import="org.apache.commons.lang.StringEscapeUtils"%>
<%@page import="com.ligerdev.appbase.utils.http.*"%>
<%@page import="com.ligerdev.appbase.utils.*"%>
<%@ page import="org.w3c.dom.Element"%>
<%@ page import="java.util.regex.*" %>
<%@ page import="java.net.*" %>
<%@ page import="java.text.*"%>
<%@ page import="java.util.Map.*" %>
<%@ page import="com.ligerdev.appbase.utils.db.*" %>
<%@ page import="com.ligerdev.appbase.utils.textbase.*" %>
<%@ page import="org.w3c.dom.Element"%>
<%@ page import="java.io.*"%>
<%@ page import="java.sql.*"%>
<%@ page import="org.w3c.dom.Document"%>
<%@ page import="com.ligerdev.appbase.utils.http.*" %>
<%@ page import="java.util.regex.*" %>
<%@ page import="com.ligerdev.appbase.utils.cache.*" %>
<%@ page import="com.ligerdev.appbase.utils.*" %>
<%-- <%@ page import="org.apache.commons.fileupload.servlet.*" %> --%>
<%@ page import="java.util.*" %>
<%@ page import="java.util.Date" %>
<%@ page import="org.apache.log4j.*" %> 
<%@ page import="com.ligerdev.appbase.utils.db.*"%>
<%-- <%@ page import="org.apache.commons.httpclient.*"%> --%>
<%@ page import="com.ligerdev.appbase.utils.encrypt.*"%>
<%@ page import="java.util.*"%>
<%-- <%@ page import="org.apache.commons.fileupload.FileItem"%> --%>
<%-- <%@ page import="org.apache.commons.fileupload.disk.DiskFileItemFactory"%> --%>
<%-- <%@ page import="org.apache.commons.fileupload.servlet.ServletFileUpload"%> --%>
<%@ page import="java.util.*"%>

<%@ page language="java" contentType="text/plain; charset=UTF-8"  pageEncoding="UTF-8"%>
<%@ page trimDirectiveWhitespaces="true" %>

<%!
public static Logger logger = Log4jLoader.getLogger();
public static BaseDAO baseDAO = BaseDAO.getInstance("main");
public static XBaseDAO xbaseDAO = XBaseDAO.getInstance("main");
%>

<%
	// amb
	// http://115.146.127.95/apiamobi/checkAlarm.jsp?alarmmode=2
	// http://115.146.127.95/apivhpd/checkAlarm.jsp?alarmmode=2
	// http://115.146.127.95/apianxh/checkAlarm.jsp?alarmmode=2
	// http://115.146.127.95/apimobion/checkAlarm.jsp?alarmmode=2
	
	// ===================================================================== 1: module
	String today = new SimpleDateFormat("yyyyMMdd").format(new Date()); 
	String thisMonth = new SimpleDateFormat("yyyyMM").format(new Date());
	String yesterday = new SimpleDateFormat("yyyyMMdd").format(BaseUtils.addTime(new Date(), Calendar.DATE, -1));
	
	ArrayList<PairStringString> module = new ArrayList<PairStringString>();
	String service = "";
	String strRespOK = "";
	// xmedia
	boolean icall1 = new File("icall1.service").exists();
	boolean icall2 = new File("icall2.service").exists();
	boolean xemlien1 = new File("xemlien1.service").exists();
	boolean tingting1 = new File("tingting1.service").exists();
	boolean dgtt1 = new File("dgtt1.service").exists();
	boolean xmediaVps1 = new File("xmediaVps1.service").exists();  // asiahalong
	boolean iwing = new File("iwing.service").exists();  // asiahalong

	// tanphong, xtel, vinhhung
	boolean alobook1 = new File("alobook1.service").exists();
	boolean xtel_vnp = new File("dautri.service").exists();
	boolean m14app1 = new File("m14app1.service").exists();
	boolean taichinh1 = new File("taichinh1.service").exists();
	boolean yte1 = new File("yte1.service").exists();
	boolean xtelVpsDucBi = new File("vpsDucBi1.service").exists();  // asiahalong
	
	// svm
	boolean esport1 = new File("esport1.service").exists();
	boolean fastshare1 = new File("fastshare1.service").exists();
	boolean smovie = new File("smovie1.service").exists();
	boolean vesport1 = new File("vesport1.service").exists();
	boolean nnnx1 = new File("nnnx1.service").exists();
	boolean vmovie = new File("vmovie1.service").exists();

	// vdg
	boolean sdct1 = new File("sdct1.service").exists();
	boolean sdct2 = new File("sdct2.service").exists();
	boolean xstt1 = new File("xstt1.service").exists();
	boolean xspl1 = new File("xspl1.service").exists();
	
	if(icall1){   // MBF
		service = "ICALL1";
		module.add(new PairStringString("haproxy", "haproxy"));
		module.add(new PairStringString("mysql/mysql", "mysql")); 
		module.add(new PairStringString("kns_ftp_charge ", "ftp_charge "));
		module.add(new PairStringString("tomcat7/conf", "tomcat7"));
		module.add(new PairStringString("MainNeif", "MainNeif"));
		module.add(new PairStringString("MNTMainApp", "MNTMainApp"));
		module.add(new PairStringString("mbf_report", "mbf_report"));
		module.add(new PairStringString("kns_ftp_vasreport", "kns_ftp_vasreport"));
		module.add(new PairStringString("ChargeCallApp", "ChargeCallApp"));
		module.add(new PairStringString("kns_diameter", "kns_diameter"));
		module.add(new PairStringString("jty_apiweb", "jty_apiweb"));
		module.add(new PairStringString("jty_ivrapi", "jty_ivrapi"));
		module.add(new PairStringString("kns_ftp_ctkm", "kns_ftp_ctkm"));
		module.add(new PairStringString("kns_ftp_vasgate", "kns_ftp_vasgate"));
		module.add(new PairStringString("icall_monitoring_asterisk", "icall_monitoring_asterisk"));
		module.add(new PairStringString("jty_xjschapp12", "cregwap_bak"));
		module.add(new PairStringString("jty_aps_extend", "jty_aps_extend")); 
	} 
	else if(icall2){ // MBF
		service = "ICALL2"; 
		module.add(new PairStringString("haproxy", "haproxy"));
		module.add(new PairStringString("syncdatafile", "backupSubApp"));
		module.add(new PairStringString("ChargeCallApp", "ChargeCallApp"));
		module.add(new PairStringString("icall_monitoring_asterisk", "icall_monitoring_asterisk"));
		module.add(new PairStringString("kns_smppgw", "kns_smppgw"));
		module.add(new PairStringString("MNTMainApp", "MNTMainApp"));
		module.add(new PairStringString("kns_renew", "kns_renew"));
		module.add(new PairStringString("fpt_neif", "fpt_neif"));
		module.add(new PairStringString("jty_xjschapp22", "cregwap_bak"));
		module.add(new PairStringString("kns_ftpUnreg_postpaid", "kns_ftpUnreg_postpaid"));
		module.add(new PairStringString("kns_ftpUnreg_prepaid", "kns_ftpUnreg_prepaid"));
	} 
	else if(xemlien1){  // MBF
		service = "XemLien1";
		module.add(new PairStringString("haproxy", "haproxy"));
		module.add(new PairStringString("mysql/mysql", "mysql")); 
		module.add(new PairStringString("jty_pkg_ns", "jty_pkg_ns"));
		//-------
		module.add(new PairStringString("fpt_neif", "fpt_neif"));
		module.add(new PairStringString("MainNeif", "MainNeif"));
		//-------
		module.add(new PairStringString("kns_smppgw", "kns_smppgw"));
		module.add(new PairStringString("tomcatCMS/conf", "tomcatCMS"));
		module.add(new PairStringString("tomcat8000/conf", "tomcat8000")); // cms cũ , vẫn dùng
		module.add(new PairStringString("jty_apiweb", "jty_apiweb"));
		module.add(new PairStringString("kns_diameter", "kns_diameter")); 
		module.add(new PairStringString("kns_renew", "kns_renew"));
		module.add(new PairStringString("email_daily_report_private", "email_daily_report"));
		//-------
		module.add(new PairStringString("kns_ftpUnreg_postpaid", "kns_ftpUnreg_postpaid"));
		module.add(new PairStringString("kns_ftpUnreg_prepaid", "kns_ftpUnreg_prepaid"));
		//-------
		module.add(new PairStringString("kns_ftp_vasreport", "kns_ftp_vasreport"));
		module.add(new PairStringString("kns_ftp_vasgate", "kns_ftp_vasgate"));
		module.add(new PairStringString("kns_ftp_charge", "kns_ftp_charge"));
	} 
	else if(alobook1){ // MBF
		service = "Alobook1";
		module.add(new PairStringString("mysql/mysql", "mysql")); 
		module.add(new PairStringString("jty_xjschapp12", "cregwap_bak"));  
		//-------
		module.add(new PairStringString("kns_smppgw", "kns_smppgw"));
		module.add(new PairStringString("tomcat_cms/conf", "tomcat_cms"));
		module.add(new PairStringString("tomcat8080/conf", "tomcat8080")); // cms cũ , vẫn dùng
		module.add(new PairStringString("jty_apiweb", "jty_apiweb"));
		module.add(new PairStringString("kns_diameter", "kns_diameter")); 
		module.add(new PairStringString("kns_renew", "kns_renew"));
		module.add(new PairStringString("email_daily_report_private", "email_daily_report"));
		//-------
		module.add(new PairStringString("kns_ftpUnreg_postpaid", "kns_ftpUnreg_postpaid"));
		module.add(new PairStringString("kns_ftpUnreg_prepaid", "kns_ftpUnreg_prepaid"));
		//-------
		module.add(new PairStringString("kns_ftp_vasreport", "kns_ftp_vasreport"));
		module.add(new PairStringString("kns_ftp_vasgate", "kns_ftp_vasgate"));
		module.add(new PairStringString("kns_ftp_charge", "kns_ftp_charge"));
	}
	else if(xtel_vnp){ // VNP VC
		service = "XTelVnp";
		module.add(new PairStringString("mysql/mysql", "mysql")); 
		module.add(new PairStringString("haproxy", "haproxy"));
		module.add(new PairStringString("httpd", "httpd"));
		//-------
		module.add(new PairStringString("kns_apivc_liveshow", "api_liveshow"));
		module.add(new PairStringString("api_vc_dautri", "api_dautri"));
		module.add(new PairStringString("tpmm_api", "api_tpmm"));
		//-------
		module.add(new PairStringString("tpmm_ftp_client_vascloud", "tpmm_ftpClient"));
		module.add(new PairStringString("dautri_ftp_client_vascloud", "dautri_ftpClient"));
		module.add(new PairStringString("kns_ftp_client_vascloud", "liveshow_ftpClient"));
		//-------
		module.add(new PairStringString("tomcat_vasdealer/conf", "tomcat_vasdealer"));
	}
	else if(esport1){ // MBF
		service = "ESport1";
		module.add(new PairStringString("tomcat7/conf", "tomcat7"));
		module.add(new PairStringString("esport_api", "esport_api"));
		module.add(new PairStringString("esports_email", "esports_email"));
		module.add(new PairStringString("esport_renew", "esport_renew"));
		module.add(new PairStringString("esport_ftpUnreg_postpaid", "ftpUnreg_postpaid"));
		module.add(new PairStringString("esport_ftpUnreg_prepaid", "ftpUnreg_prepaid"));
		module.add(new PairStringString("esport_ftp_vasgate", "esport_ftp_vasgate"));
		module.add(new PairStringString("esport_ftp_vasreport", "esport_ftp_vasreport"));
		module.add(new PairStringString("esport_diameter", "esport_diameter"));
		module.add(new PairStringString("esport_ftp_charge", "esport_ftp_charge"));
	}
	else if(fastshare1){ // MBF
		service = "Fastshare1";
		module.add(new PairStringString("fshare_rtec", "fshare_rtec"));
		module.add(new PairStringString("fastshare_email", "fastshare_email"));
		module.add(new PairStringString("jty_fshare_api", "jty_fshare_api"));
		module.add(new PairStringString("fastshare_ftp_charge", "fastshare_ftp_charge"));
		module.add(new PairStringString("fshare_airtime", "fshare_airtime"));
		module.add(new PairStringString("fshare_smppgw", "fshare_smppgw"));
		module.add(new PairStringString("tomcat7/conf", "tomcat7"));
		module.add(new PairStringString("fastshare_diameter", "fastshare_diameter"));
		module.add(new PairStringString("XJSCHAPI", "XJSCHAPI")); 
	}
	else if(smovie){ // MBF
		service = "SMOVIE1";
		module.add(new PairStringString("smv_ftpUnreg_postpaid", "smv_ftpUnreg_postpaid"));
		module.add(new PairStringString("smv_ftpUnreg_prepaid", "smv_ftpUnreg_prepaid"));
		module.add(new PairStringString("smv_diameter", "smv_diameter"));
		module.add(new PairStringString("smv_ftp_vasgate", "smv_ftp_vasgate"));
		module.add(new PairStringString("smv_ftp_vasreport", "smv_ftp_vasreport"));
		module.add(new PairStringString("smv_ftp_charge", "smv_ftp_charge"));
		module.add(new PairStringString("smv_api", "smv_api"));
		module.add(new PairStringString("smv_renew", "smv_renew"));
		module.add(new PairStringString("smv_smppgw", "smv_smppgw"));
		module.add(new PairStringString("smv_email_daily", "smv_email_daily"));
		module.add(new PairStringString("jty_xjsch", "jty_xjsch"));
		module.add(new PairStringString("cregwap", "cregwap"));
		module.add(new PairStringString("tomcat7/conf", "tomcat7"));
	}
	else if(vesport1){ // VESPORT VNP
		service = "VESPORT1";
		module.add(new PairStringString("httpd", "httpd"));
		module.add(new PairStringString("oracle", "oracle"));
		module.add(new PairStringString("tomcat7/conf", "tomcat7"));
		module.add(new PairStringString("WowzaMediaServer", "WowzaMediaServer"));
		module.add(new PairStringString("kns_ftp_client_vascloud", "kns_ftp_client"));
		module.add(new PairStringString("esport_api", "vesport_api"));
	}
	else if(vmovie){ // VMOVIES VNP
		service = "VMOVIE1";
		module.add(new PairStringString("httpd", "httpd"));
		module.add(new PairStringString("oracle", "oracle"));
		module.add(new PairStringString("tomcat7/conf", "tomcat7"));
		module.add(new PairStringString("WowzaMediaServer", "WowzaMediaServer"));
		module.add(new PairStringString("kns_ftp_client_vascloud", "kns_ftp_client"));
		module.add(new PairStringString("vmovies_api", "vmovies_api"));
	}
	else if(nnnx1){ // NNNX VNP
		service = "NNNX1";
		module.add(new PairStringString("httpd", "httpd"));
		module.add(new PairStringString("oracle", "oracle"));
		module.add(new PairStringString("tomcat7/conf", "tomcat7"));
		module.add(new PairStringString("kns_ftp_client_vascloud", "kns_ftp_client"));
		module.add(new PairStringString("nnnx_api", "nnnx_api"));
	}
	else if(m14app1){ // MBF
		service = "M14App1";
		module.add(new PairStringString("tomcat6060/conf", "tomcat6060"));
		module.add(new PairStringString("tomcat6061/conf", "tomcat6061"));
		module.add(new PairStringString("tomcat6062/conf", "tomcat6062"));
		module.add(new PairStringString("jettyrunner2", "jettyrunner2"));
		module.add(new PairStringString("ftpUnreg_postpaid", "ftpUnreg_postpaid"));
		module.add(new PairStringString("kns_ftp_charge", "kns_ftp_charge"));
		module.add(new PairStringString("clingme_sync_job", "clingme_sync_job"));
		module.add(new PairStringString("jty_pkg_db", "jty_pkg_db"));
		module.add(new PairStringString("ftp_upload_kpi_cdr", "ftp_upload_kpi_cdr")); // sync kpi file
		module.add(new PairStringString("cmstq/conf", "CMS_TQ")); // opt/WEB/cmstq
		module.add(new PairStringString("kns_diameter", "kns_diameter"));
		module.add(new PairStringString("ftpUnreg_prepaid", "ftpUnreg_prepaid"));
		module.add(new PairStringString("m14_email_daily", "m14_email_daily"));
		module.add(new PairStringString("cmsm14/conf", "CMSM14")); // opt/WEB/cmsm14
		module.add(new PairStringString("ftp_client_cdr", "ftpClientBlacklist")); // /opt/WEB/ftpCdrBlacklist/
		module.add(new PairStringString("m14_smppgw", "m14_smppgw"));
		module.add(new PairStringString("tomcat8080/conf", "tomcat8080"));  // api vasgate
	}
	else if(sdct1){ // MBF
		service = "SDCT1";
		module.add(new PairStringString("httpd", "httpd"));
		module.add(new PairStringString("haproxy", "haproxy"));
		module.add(new PairStringString("mysql/mysql", "mysql")); 
		module.add(new PairStringString("jty_pkg_db", "jty_pkg_db"));
		module.add(new PairStringString("fpt_neif", "fpt_neif"));
		module.add(new PairStringString("MainNeif", "MainNeif"));
		module.add(new PairStringString("jty_pkg_ns", "jty_pkg_ns"));
		module.add(new PairStringString("jty_xjschapp12", "jty_xjschapp12"));
		module.add(new PairStringString("start_xkbox", "start_xkbox"));
		//-----
		module.add(new PairStringString("kns_ftp_vasreport", "kns_ftp_vasreport"));
		module.add(new PairStringString("kns_ftp_charge", "kns_ftp_charge"));
		module.add(new PairStringString("kns_ftp_sales", "kns_ftp_sales"));
		module.add(new PairStringString("kns_ftp_vasgate", "kns_ftp_vasgate"));
		//-----
		module.add(new PairStringString("kns_ftpUnreg_postpaid", "ftpUnreg_postpaid"));
		module.add(new PairStringString("kns_ftpUnreg_prepaid", "ftpUnreg_prepaid"));
		module.add(new PairStringString("kns_ftpUnreg_td", "ftpUnreg_td"));
		//-----
		module.add(new PairStringString("jty_apiweb", "jty_apiweb"));
		module.add(new PairStringString("esport_email_daily", "email_daily"));
		module.add(new PairStringString("kns_diameter", "kns_diameter"));
		module.add(new PairStringString("kns_renew", "kns_renew"));
		module.add(new PairStringString("tomcat7/conf", "tomcat7"));
	} 
	else if(sdct2){ // MBF
		service = "SDCT2";
		module.add(new PairStringString("haproxy", "haproxy"));
		module.add(new PairStringString("mysql/mysql", "mysql")); 
		module.add(new PairStringString("kns_smppgw", "kns_smppgw"));
	}
	else if(xstt1){ // VNP VC
		service = "XSTT1";
		module.add(new PairStringString("httpd", "httpd"));
		module.add(new PairStringString("mysql/mysql", "mysql")); 
		module.add(new PairStringString("xstt_api", "xstt_api"));
		module.add(new PairStringString("ftp_client_vascloud", "ftp_client_vascloud"));
		module.add(new PairStringString("tomcat7/conf", "tomcat7"));
	}
	else if(xspl1){ // MBF
		service = "XSPL1";
		module.add(new PairStringString("httpd", "httpd"));
		module.add(new PairStringString("mysqld/mysqld", "mysql")); 
		module.add(new PairStringString("haproxy", "haproxy"));
		//----
		module.add(new PairStringString("start_keybox", "start_keybox"));
		module.add(new PairStringString("skgw_neif", "skgw_neif"));
		module.add(new PairStringString("email_daily_report_private", "email_daily_report"));
		module.add(new PairStringString("fpt_neif", "fpt_neif"));
		//----
		module.add(new PairStringString("kns_ftpUnreg_postpaid", "ftpUnreg_postpaid"));
		module.add(new PairStringString("kns_ftpUnreg_prepaid", "ftpUnreg_prepaid"));
		module.add(new PairStringString("kns_ftp_vasgate", "kns_ftp_vasgate"));
		module.add(new PairStringString("kns_ftp_vasreport", "kns_ftp_vasreport"));
		module.add(new PairStringString("kns_ftp_charge", "kns_ftp_charge"));
		//----
		module.add(new PairStringString("mnt_smsgw", "mnt_smsgw"));
		module.add(new PairStringString("tomcat7/conf", "tomcat7"));
		module.add(new PairStringString("jty_apiweb", "jty_apiweb"));
		module.add(new PairStringString("kns_smppgw", "kns_smppgw"));
		module.add(new PairStringString("kns_renew", "kns_renew"));
		module.add(new PairStringString("kns_diameter", "kns_diameter"));
	}
	else if(taichinh1){
		service = "TaiChinh1";
		module.add(new PairStringString("httpd", "httpd"));
		module.add(new PairStringString("mysqld/mysqld", "mysql")); 
		module.add(new PairStringString("haproxy", "haproxy"));
		//---
		module.add(new PairStringString("cmsads_tomcat_7/conf", "cmsads_tomcat_7")); // cmsAds: ofctmp/
		module.add(new PairStringString("kns_ftp_client_vascloud", "ftp_client_vascloud"));
		module.add(new PairStringString("cregwap", "cregwap"));
		module.add(new PairStringString("kns_vc_api", "kns_vc_api"));
		module.add(new PairStringString("tomcat7/conf", "tomcat7"));
		module.add(new PairStringString("kns_smppgw", "kns_smppgw"));
	}
	else if(yte1){
		service = "YTE1";
		module.add(new PairStringString("httpd", "httpd"));
		module.add(new PairStringString("mysqld/mysqld", "mysql")); 
		module.add(new PairStringString("haproxy", "haproxy"));
		//---
		module.add(new PairStringString("kns_ftp_client_vascloud", "ftp_client_vascloud"));
		module.add(new PairStringString("cregwap", "cregwap"));
		module.add(new PairStringString("kns_vcyte_api", "kns_vcyte_api"));
		module.add(new PairStringString("tomcat7/conf", "tomcat7"));
		module.add(new PairStringString("kns_smppgw", "kns_smppgw"));
	}else if(iwing){
		service = "IWING";
//		module.add(new PairStringString("httpd", "httpd"));
		module.add(new PairStringString("mysql/mysql", "mysql"));
		module.add(new PairStringString("haproxy", "haproxy"));
		//---
		module.add(new PairStringString("ccsp_api", "ccsp_api"));
		module.add(new PairStringString("kns_ftp_vasreport", "kns_ftp_vasreport"));
		module.add(new PairStringString("ftp_client_cdr", "ftp_client_cdr"));
		module.add(new PairStringString("tomcatCMS/conf", "tomcatCMS"));
	}
	else if(tingting1){
		service = "TINGTING1";
		module.add(new PairStringString("httpd", "httpd"));
		module.add(new PairStringString("mysql/mysql", "mysql")); 
		module.add(new PairStringString("haproxy", "haproxy"));
		//---
		module.add(new PairStringString("ccsp_api", "ccsp_api"));
		module.add(new PairStringString("kns_ftp_vasreport", "kns_ftp_vasreport"));
		module.add(new PairStringString("ftp_client_cdr", "ftp_client_cdr"));
		module.add(new PairStringString("tomcatCMS/conf", "tomcatCMS"));
	}
	else if(dgtt1){
		service = "DoanGia1";
		module.add(new PairStringString("httpd", "httpd"));
		module.add(new PairStringString("mysql/mysql", "mysql")); 
		module.add(new PairStringString("haproxy", "haproxy"));
		//---
		module.add(new PairStringString("ccsp_api", "ccsp_api"));
		module.add(new PairStringString("kns_ftp_vasreport", "kns_ftp_vasreport"));
		module.add(new PairStringString("ftp_client_cdr", "ftp_client_cdr"));
		module.add(new PairStringString("tomcatCMS/conf", "tomcatCMS"));
		module.add(new PairStringString("jty_pkg_dg", "jty_pkg_dg"));
		module.add(new PairStringString("email_daily_report", "email_daily_report"));
	}
	else if(xmediaVps1){
		service = "xmediaVps1";
		module.add(new PairStringString("httpd", "httpd"));
		module.add(new PairStringString("mysqld/mysqld", "mysql")); 
		module.add(new PairStringString("ducbi_xnews_tomcat7_cms/conf", "cms_tomcat"));
	}
	else if(xtelVpsDucBi){
		service = "xtelVpsDucBi";
		module.add(new PairStringString("mysqld/mysqld", "mysql")); 
	}
	for(PairStringString p: module){
		String cmd = "ps aux| grep " + p.getName() + " | grep -v grep | wc -l";
		String resp = MNTUtils.runCmd("", cmd, true);
		int rsInt = BaseUtils.parseInt(resp, 0);
		
		if(rsInt < 1){
			String s = "1|5|" + service + ": " + p.getName() + " is not running...";
			out.print(s);
			return;
		}
	}
	strRespOK += service + ": Process.V1=" + module.size();
	
	// ======================================================================== DoanGiaTrungThuong xmd
	if(Calendar.getInstance().get(Calendar.HOUR_OF_DAY) >= 11
		&& Calendar.getInstance().get(Calendar.HOUR_OF_DAY) <= 15
		&& (iwing)
	){	
		String sql = "select count(id) from kmtq where status != 2 and category like 'doangia%' and make_time > date(now()) + interval -3 day";
		int count = xbaseDAO.getFirstCell("", sql, Integer.class);
		
		if(count > 0){
			String s = "4|5|" + service + ": KMTQ/topupKM fail = " + count;
			out.print(s); 
			return;
		}
		strRespOK += ", topupKM=OK";
	}
	// ===================================================================== 1.2: CDR CCSP 
	
	if(Calendar.getInstance().get(Calendar.HOUR_OF_DAY) >= 8
			&& (iwing == true || dgtt1)
		){
		String cmd = "ls -la /opt/kns/cdr/ccsp/backup/" + today + "_*txt | wc -l";  
		String resp = MNTUtils.runCmd("", cmd, true);
		int rsInt = BaseUtils.parseInt(resp, 0);
		
		if(rsInt < 1){
			String s = "4|5|" + service + ": CDR CCSP is not found...";
			out.print(s); 
			return;
		}
		strRespOK += ", CdrCCSP=" + rsInt;
	}
	// ===================================================================== 1.3: FEE (bảng stats_rscode)
	if(Calendar.getInstance().get(Calendar.HOUR_OF_DAY) >= 8
		&& (
				icall1 || xemlien1 || alobook1 || esport1 || nnnx1 || smovie || vesport1 || vmovie || xtel_vnp  || m14app1 || sdct1 
				|| xstt1 || xspl1 || yte1 || taichinh1 || dgtt1 || tingting1 || iwing
			)
	){
		String sql = "select sum(counter*fee) total_fee from stats_rscode where rs_code = 'CPS-0000' and reason = 'RENEW' and created_time = " + today;
		
		if(esport1 || nnnx1 || smovie || vesport1 || vmovie){ // SVM oracle
			sql = "select sum(fee) total_fee from ACTION_HIS where result = 0 and DATA_PART = "  + today;
		}
		else if(xtel_vnp || xstt1 || yte1 || taichinh1){ 
			// his table, VNP bangr stats_rscode cũng có data, chỉ khác MBF là rs_code  = 0, nhưng vì dv doanh thu nhỏ nên lấy ở bảng his cho chính xác
			sql = "select sum(fee) total_fee from his_" + new SimpleDateFormat("yyyyMM").format(new Date())
					+ " where result = 0 and action = 'RENEW' and created_time >= " + today; 
		}
		else if(m14app1){
			sql = "select sum(price) from z_his_" + thisMonth + " where created_date >= date(now()) and status = 1"; 
		}
		Long fee = xbaseDAO.getFirstCell("", sql, Long.class);
		
		if(fee == null || fee < 10000){ 
			String s = "4|5|" + service + ": RenewFee today = " + fee + "đ";
			out.print(s); 
			return;
		}
		strRespOK += ", RenewFee=" + (fee/1000) + "k"; 
	} 
	//------ FS SVM MBF
	if(fastshare1){
		int minute = 5;
		int HH = Integer.parseInt(new SimpleDateFormat("yyyyMMdd").format(new Date()));
		if(HH > 20){
			minute = 7;
		}
		if(HH > 22){
			minute = 9;
		}
		if(HH < 7){
			minute = 8;
		}
		if(HH < 5){
			minute = 11;
		}
		String sql = "select sum(AMOUNT) from cdr where parse_time >= " + new SimpleDateFormat("yyyyMMdd").format(BaseUtils.addTime(new Date(), Calendar.DATE, -1)) 
					+ " and CREATED_TIME >= sysdate - " + minute + "/(24*60) and result = 0";
		Long fee = xbaseDAO.getFirstCell("checkalarm", sql, Long.class);

		if(fee == null || fee <= 0){ 
			String s = "4|5|" + service + ": No transaction in " + minute + " minutes";
			out.print(s); 
			return;
		}
		strRespOK += ", TransFee=" + (fee/1000) + "k/" + minute + "m";  
	}
	
	//------ his_yyyyMM vnp
	if(Calendar.getInstance().get(Calendar.HOUR_OF_DAY) >= 8
			&& (xtel_vnp) // dv này có dautri, tpmm ...
	){
		{ // dautri
			String sql = "select sum(fee) total_fee from dautri.his_" + new SimpleDateFormat("yyyyMM").format(new Date())
					+ " where result = 0 and action = 'RENEW' and created_time >= " + today; 
			Long fee = xbaseDAO.getFirstCell("", sql, Long.class);
			
			if(fee == null || fee < 100000){ 
				String s = "4|5|" + service + ": DauTriFee today = " + fee + "k";
				out.print(s); 
				return;
			}
			strRespOK += ", DauTriFee=" + (fee/1000) + "k"; 
		}
		/* { // TPMM
			String sql = "select sum(fee) total_fee from tpmn.his_" + new SimpleDateFormat("yyyyMM").format(new Date())
					+ " where result = 0 and action = 'RENEW' and created_time >= " + today; 
			Long fee = xbaseDAO.getFirstCell("", sql, Long.class);
			
			if(fee == null || fee < 1000){ 
				String s = "4|5|" + service + ": TpmmFee today = " + fee + "k";
				out.print(s); 
				return;
			}
			strRespOK += ", TpmmFee=" + (fee/1000) + "k"; 
		} */
	} 
	
	// ===================================================================== 2: CDR cước MBF
	// cdr này đầu ngày chưa có, nên tầm 6h sáng mới check
	if(Calendar.getInstance().get(Calendar.HOUR_OF_DAY) >= 8
			&& (icall1 || xemlien1 || alobook1 || esport1 || fastshare1 || smovie || m14app1 || sdct1 || xspl1)
	){
		String filePath = "/opt/kns/cdr/chargegw_backup/" + today + "/";
		if(fastshare1){
			filePath = "/opt/kns/cdr/share_backup/" + today + "/";
		} else if(m14app1){
			filePath = "/home/chargegw_backup/" + today + "/";
		} 
		File files [] = new File(filePath).listFiles(new FilenameFilter(){
			public boolean accept(File dir, String name){
				return name.endsWith(".cdr");
			}
		});
		if(files == null || files.length == 0){ 
			String s = "2|5|" + service + ": CDR diameter not found1, filePath: " + filePath; 
			out.print(s);
			return;
		}
		long lastFile = files[0].lastModified();
		File lf = files[0];
		
		for(File f: files){
			if(f.lastModified() > lastFile){
				lastFile = f.lastModified();
				lf = f;
			}
		}
		// nếu file cuối cùng mà cách đây 1x tiếng (quá tgian các khoảng retry) 
		if(System.currentTimeMillis() - lastFile >= 60000L * 60 * 14){
			String s = "3|5|" + service + ": new CDR diameter not found, lastfile: " + lf.getName(); 
			out.print(s);
			return;
		}
		strRespOK += ", CdrFee=" + files.length;
	}
	// ===================================================================== 3: CDR vasreport MBF
	
	if(Calendar.getInstance().get(Calendar.HOUR_OF_DAY) >= 6
			&& (icall1 || xemlien1 || alobook1 || esport1 || smovie || m14app1 || sdct1 || xspl1)
	){
		String cmd = "ls -la /opt/kns/cdr/syn_daily_backup/*" + yesterday + "*.txt | wc -l";  
		
		if(m14app1){
			cmd = "ls -la /home/vasreport/*" + yesterday + "*.txt | wc -l";  
		}
		String resp = MNTUtils.runCmd("", cmd, true);
		int rsInt = BaseUtils.parseInt(resp, 0);
		
		if(rsInt < 3){
			String s = "4|5|" + service + ": CDR VasReport is not found...";
			out.print(s); 
			return;
		}
		strRespOK += ", CdrVRP=" + rsInt;
	}
	
	// ===================================================================== 3.2: CDR Vascloud VNP
	if(Calendar.getInstance().get(Calendar.HOUR_OF_DAY) >= 8
			&& (xtel_vnp || nnnx1 || vesport1 || vmovie || xstt1 || yte1 || taichinh1)
	){
		{ // liveshow
			String cmd = "ls -la /opt/kns/cdr/vascloud_backup/*" + today + "* | wc -l";  
			String resp = MNTUtils.runCmd("", cmd, true);
			int rsInt = BaseUtils.parseInt(resp, 0);
			
			if(rsInt < 3){
				String s = "4|5|" + service + ": CdrVC is not found...";
				out.print(s); 
				return;
			}
			strRespOK += ", CdrVC=" + rsInt;
		}
		
		if(xtel_vnp){
			{ // dautri
				String cmd = "ls -la /opt/dautri/cdr/vascloud_backup/*" + today + "* | wc -l";  
				String resp = MNTUtils.runCmd("", cmd, true);
				int rsInt = BaseUtils.parseInt(resp, 0);
				
				if(rsInt < 3){
					String s = "4|5|" + service + ": CdrVC_DTR is not found...";
					out.print(s); 
					return;
				}
				strRespOK += ", CdrVC_DTR=" + rsInt;
			}
			/* { // tpmm
				String cmd = "ls -la /opt/tpmm/cdr/vascloud_backup/*" + today + "* | wc -l";  
				String resp = MNTUtils.runCmd("", cmd, true);
				int rsInt = BaseUtils.parseInt(resp, 0);
				
				if(rsInt < 3){
					String s = "4|5|" + service + ": CdrVC_TPMM is not found...";
					out.print(s); 
					return;
				}
				strRespOK += ", CdrVC_TPMM=" + rsInt;
			} */
		}
	}	
			
	
	// ===================================================================== 4: CDR vasgate MBF
	if(Calendar.getInstance().get(Calendar.HOUR_OF_DAY) >= 8
			&&  (icall1 || xemlien1 || alobook1 || esport1 || smovie || m14app1 || sdct1 || xspl1)
	){
		// ls -la /opt/kns/cdr/vasgate_mvideo_backup/20190615/*txt | wc -l
		String cmd = "ls -la /opt/kns/cdr/vasgate_backup/" + today + "/*txt | wc -l";  
		
		if(m14app1){
			cmd = "ls -la /home/vasgate/" + today + "/*txt | wc -l";   
		}
		String resp = MNTUtils.runCmd("", cmd, true);
		int rsInt = BaseUtils.parseInt(resp, 0);
		
		if(rsInt < 1){
			String s = "5|5|" + service + ": CDR VasGate is not found...";
			out.print(s); 
			return;
		}
		strRespOK += ", CdrVG=" + rsInt;
	}
	
	// ===================================================================== 5: NEIF FEE MBF 
	if(Calendar.getInstance().get(Calendar.HOUR_OF_DAY) >= 10
			&&  (icall1 || xemlien1 || sdct1 || xspl1)
	){  
		String sql = "select sum(counter*fee) from stats_rscode where load_number=8888 and rs_code='CPS-0000' and created_time = " + today;
		Long fee = xbaseDAO.getFirstCell("", sql, Long.class);
		
		if(fee == null || fee < 10000){ 
			String s = "4|5|" + service + ": NeifFee today = " + fee + "đ";
			out.print(s); 
			return;
		}
		strRespOK += ", NeifFee=" + (fee/1000) + "k"; 
	}
	// ===================================================================== 6: RAM
	try {
		String cmd = "free -b | grep Mem";
		String resp = MNTUtils.runCmd("", cmd, true);
		String array[] = resp.replace("\t", " ").trim().replaceAll(" +", " ").split(" ");
		long total= Long.parseLong(array[1]);
		//String used= array[2];
		long free= Long.parseLong(array[3]);
		//String shared= array[4];
		//String buffers= array[5];
		long cache= Long.parseLong(array[6]);
		
		long realFree = free + cache;
		long realUsed = total - realFree;
		long realUsedPercent = (100 * realUsed) / total;
		
		if(realUsedPercent >= 90){
			String s = "6|5|" + service + ": Use " + realUsedPercent + "% of RAM"; 
			out.print(s);
			return;
		}
		strRespOK += ", RamTotal=" + (total/(1024*1024*1024)) + "GB";
		strRespOK += "/Free=" + (realFree/(1024*1024))+ "MB";
		strRespOK += "/Use=" + realUsedPercent + "%"; 
		
	} catch (Exception e){
		strRespOK += ", Ram=CheckERR";
	}
	// ===================================================================== 6: DISK
	try {
		String cmd = "df -P | grep -v Filesystem"; 
		String resp = MNTUtils.runCmd("", cmd, true);
		String array[] = resp.split("\n");
		strRespOK += ", Disk";
		
		for(String line: array){
			//usedDiskPercent=`echo ${disk[i]} | awk '{ print $5}' | sed 's/%//g'`
			//usedDiskName=`echo ${disk[i]} | awk '{ print $1}' | sed 's/%//g'`
			
			if(line.contains("/media/CentOS")){
				continue;
			}
			line = line.replace("\t", " ").trim().replaceAll(" +", " ");
			String percents[] = line.split(" ");
			String name = percents[0];
			
			if(name.contains("/")){
				name = name.substring(name.lastIndexOf("/") + 1);
			}
			int percent = Integer.parseInt(percents[4].replace("%", "")); 
			strRespOK += "/" + name + "=" + percent + "%"; 
			
			if(percent > 90){
				String s = "7|5|" + service + ": Disk " + percents[0] + " is used " + percent + "%"; 
				out.print(s);
				return;			
			} 
		}
	} catch (Exception e){
		strRespOK += ", Disk=CheckERR";
	}
	// strRespOK = strRespOK.replaceFirst(", ", "");
	out.print("0|5|" + strRespOK);
%>


