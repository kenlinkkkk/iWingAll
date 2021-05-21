<%@ page language="java" contentType="text/html; charset=UTF-8"  pageEncoding="UTF-8"%>
<%@page import="com.xxx.aps.XmlConfigs"%>
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
<%@ page import="com.xxx.aps.logic.db.orm.*" %> 
<%@ page import="com.ligerdev.appbase.utils.cache.*" %>
<%@include file="_ccsp.client.jsp"%>  

<% 
	//mặc định luôn luôn response ok
	String rsOK = "{\"resultCode\":\"0\", result: 0}";
	out.print(rsOK);  
	
	String transid = (String) request.getAttribute("transid");
	String msisdn = (String) request.getAttribute("msisdn");
	Integer moid = (Integer) request.getAttribute("moid");
	exeMO_KT(transid, msisdn, moid);
%>

<%!
	public void exeMO_KT(String transid, String msisdn, int moid){
		XBaseDAO baseDAO = XBaseDAO.getInstance("main");
		
		String sql = "select * from subscriber where msisdn = ? and status =1";
		ArrayList<Subscriber> listSubs = baseDAO.getListBySql(transid, Subscriber.class, sql, null, null, msisdn);
		 
		if(listSubs != null && listSubs.size() > 1){
			 /*
				 Quý khách đang sử dụng các gói cước sau của dịch vụ XXXX: [tên gói cước 1], giá cước […] VNĐ/ngày, 
				 hạn sử dụng tới hh:mm:ss dd/mm/yyyy; [tên gói cước 2], giá cước […] VNĐ/ngày, hạn sử dụng tới hh:mm:ss dd/mm/yyyy.
				 Để hủy dịch vụ: [tên gói cước 1], soạn [cú pháp hủy] gửi 9669, [tên gói cước 2], soạn [cú pháp hủy] gửi 9669.
				 Chi tiết liên hệ 04.32191146 (cước gọi cố định). Trân trọng cảm ơn!
				 
				 ---------------
				 Quý khách đang sử dụng các gói cước sau của dịch vụ XXXX: [PKG_INFO].
				 Chi tiết liên hệ 04.32191146 (cước gọi cố định). Trân trọng cảm ơn!
			*/
			String packages_info = ""; 
			
			for(int i = 0 ; listSubs != null && i < listSubs.size(); i ++){
				Subscriber sub = listSubs.get(i);
				
				PkgPolicy pkgPolicy = baseDAO.getBeanByKey(transid, PkgPolicy.class, sub.getPackageId());
				packages_info += pkgPolicy.getName() + ", giá cước " + pkgPolicy.getFeeDesc() 
								+ ", đăng ký từ " + new SimpleDateFormat("HH:mm:ss dd/MM/yyyy").format(sub.getActiveTime())
								+ ", hạn sử dụng tới " + new SimpleDateFormat("HH:mm:ss dd/MM/yyyy").format(sub.getExpireTime());  
				
				if(i + 1 < listSubs.size()){
					packages_info += "; ";
				} else {
					packages_info += ". ";
				}
			}
			packages_info += "Để hủy dịch vụ: ";
			
			for(int i = 0 ; listSubs != null && i < listSubs.size(); i ++){
				Subscriber sub = listSubs.get(i);
				
				PkgPolicy pkgPolicy = baseDAO.getBeanByKey(transid, PkgPolicy.class, sub.getPackageId());
				packages_info += pkgPolicy.getName() + ", soạn " + pkgPolicy.getSmsUnreg() + " gửi " + XmlConfigs.shortcode;  
				
				if(i + 1 < listSubs.size()){
					packages_info += ", ";
				} else {
					packages_info += ". ";
				}
			}
			MtHis mt = new MtHis(0, msisdn, packages_info, moid, transid, "KT2", new Date(), "SMS");
			sendSMS1(transid, mt);
		}
		else if(listSubs != null && listSubs.size() == 1) {
			/* 
				 Quý khách đang sử dụng gói cước [tên gói cước] của dịch Xổ số Phát Lộc. Giá cước […] VNĐ/ngày.
				 Hạn sử dụng tới hh:mm:ss dd/mm/yyyy. Để hủy dịch vụ, soạn [cú pháp hủy] gửi 9669 Chi tiết liên hệ 04.32191146 (cước gọi cố định). 
				 Trân trọng cảm ơn!
				 
				 ===================
				 Quý khách đang sử dụng gói cước [PKG_NAME] của dịch Xổ số Phát Lộc. Giá cước [FEE_DESC].
				 Hạn sử dụng tới [EXPIRED_TIME]. Để hủy dịch vụ, soạn [cú pháp hủy] gửi 9669 Chi tiết liên hệ 04.32191146 (cước gọi cố định). 
				 Trân trọng cảm ơn!
			*/
			Subscriber sub = listSubs.get(0);
			PkgPolicy p = baseDAO.getBeanByKey(transid, PkgPolicy.class, sub.getPackageId());
			String expTime = new SimpleDateFormat("HH:mm dd/MM/yyyy").format(sub.getExpireTime());
			
			String content = "Quý khách đang sử dụng gói cước " + p.getName() + " của dịch vụ " + XmlConfigs.servicename + ". Giá cước " + p.getFeeDesc() + ". " + 
							 "Hạn sử dụng tới " + expTime + ". Để hủy dịch vụ, soạn " + p.getSmsUnreg() + " gửi " + XmlConfigs.shortcode
							 + " Chi tiết liên hệ " + XmlConfigs.cskh_number + " (cước gọi cố định). " + 
							 "Trân trọng cảm ơn!";
			
			MtHis mt = new MtHis(0, msisdn, content, moid, transid, "KT1", new Date(), "SMS");
			sendSMS1(transid, mt);
		}
		else {
			String content = "Hiện tại, Quý khách chưa sử dụng gói cước nào của dịch vụ " + XmlConfigs.servicename
							+ ". Chi tiết liên hệ " + XmlConfigs.cskh_number + " (cước gọi cố định). Trân trọng cảm ơn!";
			
			MtHis mt = new MtHis(0, msisdn, content, moid, transid, "KT0", new Date(), "SMS");
			sendSMS1(transid, mt);
		}
	}
%>