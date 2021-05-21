<%@page import="com.sun.mail.imap.AppendUID" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<%@page import="com.ligerdev.appbase.utils.db.BaseDAO" %>
<%@page import="com.ligerdev.appbase.utils.BaseUtils" %>
<%@page import="com.ligerdev.appbase.utils.http.HttpServerUtils" %>
<%@page import="com.ligerdev.appbase.utils.textbase.StringGenerator" %>
<%@ page import="org.w3c.dom.Element" %>
<%@ page import="java.io.*" %>
<%@ page import="java.sql.*" %>
<%@ page import="org.w3c.dom.Document" %>
<%@ page import="java.util.regex.*" %>
<%@ page import="java.net.*" %>
<%@ page import="java.util.*" %>
<%@ page import="java.util.Date" %>
<%@ page import="java.text.*" %>
<%@ page import="org.apache.log4j.*" %>
<%@ page import="com.xxx.aps.logic.db.orm.*" %>

<%@ page trimDirectiveWhitespaces="true" %>
<%@include file="_utils.app.jsp" %>
<%@include file="_ccsp.client.jsp" %>

<%!
    // ...
%>

<%
    if (request.getParameter("compile") != null) {
        out.print("compile ok, v1.0");
        return;
    }
	/*
		// khi khách hàng soạn MO => MOBIFONE (CCSP) call sang file này (khai báo URL này cho CCSP để họ khai trên CCSP)
		// trừ 1 số MO như đk, hủy, KT, HD ... thì CCSP không call sang file này

		http://45.121.26.232/ccsp/forwardMessage.jsp?isdn=909090529&content=KT
		curl 'http://localhost:8888/ccsp/forwardMessage.jsp?compile'
		curl 'http://localhost:8888/ccsp/updatePackage.jsp?compile'

		// tingting
		curl 'http://localhost:8888/ust/minusMoneyCheckMO.jsp?compile'
		curl 'http://localhost:8888/ust/receiverServiceReq.jsp?compile'
		curl 'http://localhost:8888/ust/sendMessage.jsp?compile'
	 */
    String msisdn = BaseUtils.formatMsisdn(request.getParameter("isdn"), "84", "84");
    long l1 = System.currentTimeMillis();

    if (msisdn == null) {
        out.print("blank msisdn");
        return;
    }
    //mặc định luôn luôn response ok
    String rsOK = "{\"resultCode\":\"1\", result: 1}";
    out.print(rsOK);

    int today = Integer.parseInt(new SimpleDateFormat("yyyyMMdd").format(new Date()));
    String transid = "F" + StringGenerator.randomCharacters(5) + "@" + BaseUtils.formatMsisdn(msisdn, "84", "");
    try {
        String moContent = request.getParameter("content");
        moContent = String.valueOf(moContent).trim().toLowerCase().replaceAll(" +", " ");
        String body = BaseUtils.readInputStream(request.getInputStream());
        boolean isPost = "POST".equals(request.getMethod());

        logger.info(transid + ", ############ receive req: msisdn: " + msisdn
                + ", moContent: " + moContent + ", ip: " + request.getRemoteAddr()
                + ", queryStr: " + request.getQueryString()
                + ", reqBody: " + body + ", isPost: " + isPost
        );
        // getHeaders(transid, request, true);
        // getParameters(transid, request, true);
        String s = getParameterString(transid, request);
        logger.info(transid + ", parameter: " + s);

        String reqid = request.getParameter("request_id");
        MoHis mo = new MoHis(0, msisdn, moContent, transid, "INV", new Date(), reqid);

        if (isTingTingService()
            // && moContent.startsWith("#mua") == false
                ) {
            int moid = xbaseDAO.insertBean(transid, mo);
            mo.setId(moid);

            // fix tạm, do UPS ko sửa dc
            if ("KT".equalsIgnoreCase(moContent) == false
                    && "HD".equalsIgnoreCase(moContent) == false
                    && "MK".equalsIgnoreCase(moContent) == false
                    && "DIEM".equalsIgnoreCase(moContent) == false
                    && "MUA1".equalsIgnoreCase(moContent) == false
                    ) {
                // sai cú pháp
                mo.setCommand("WRSYNTAX");
                String content = "Tin nhắn sai cú pháp. Để được hướng dẫn, soạn HD gửi 9296 hoặc truy cập http://ting-ting.com.vn hoặc liên hệ 024 85860616 (cước gọi cố định). Trân trọng cảm ơn!";
                MtHis mt = new MtHis(0, msisdn, content, moid, transid, "WRSYNTAX", new Date(), "SMS");
                sendSMS1(transid, mt);
                return;
            }
            // put để bên hàm trung gian cho UPS => xmedia => mbf get ra để lấy số liệu
            cache.put("moid@" + msisdn, mo, 20 * 2);
            cache.put("moid@" + msisdn + "@" + reqid, mo, 20 * 2);

            // String url = "http://91.213.52.150:8088/vmp/forward";
            // http://91.213.52.150:8088/cdrtopup?msisdns=84909090529,84909090528,84909090427
            String url = "http://10.54.157.187:80/vmp/forward?";
            logger.info(transid + ", forward MO to UPS: " + url);

            HashMap<String, String> postDataParams = new HashMap<String, String>();
            postDataParams.put("content", request.getParameter("content"));
            postDataParams.put("isdn", request.getParameter("isdn"));
            postDataParams.put("request_id", reqid);

            // String upsResp = get(transid, url, postDataParams, 10000);
            String upsResp = postParams(transid, url, postDataParams, 10000);
            upsResp = String.valueOf(upsResp).replace("\n", " ").replaceAll(" +", " ");
            logger.info(transid + ", resp MO from UPS: " + upsResp + " | time: " + BaseUtils.getDurations(System.currentTimeMillis(), l1));

            return;
        } else if ("KT".equalsIgnoreCase(moContent)) {
            mo.setCommand("KT");
            int moid = insertMO(transid, mo);
            logger.info(transid + ", cmd = KT");

            // set Attr
            request.setAttribute("transid", transid);
            request.setAttribute("msisdn", msisdn);
            request.setAttribute("moid", moid);

            RequestDispatcher rd = request.getRequestDispatcher("_cmd.kt.jsp");
            rd.forward(request, response);
            return;
        } else if ("HD".equalsIgnoreCase(moContent) || "GT".equalsIgnoreCase(moContent) || "HELP".equalsIgnoreCase(moContent)) {
            mo.setCommand("HD");
            int moid = insertMO(transid, mo);

            String content = "Hướng dẫn sử dụng dịch vụ ...."; // thường thì tin này CCSP sẽ trả
            MtHis mt = new MtHis(0, msisdn, content, moid, transid, "HD", new Date(), "SMS");
            sendSMS1(transid, mt);
        } else if ((isHocVuiService() || isPokiService()) && "MK".equalsIgnoreCase(moContent)) {
            mo.setCommand("MK");
            int moid = insertMO(transid, mo);
            String sql = "select * from subscriber where msisdn = '" + msisdn + "' and status = 1";
            Subscriber subs = xbaseDAO.getBeanBySql(transid, Subscriber.class, sql);
            String content = "";

            if (subs != null) {
                content = "Mật khẩu để sử dụng dịch vụ Học vui của Quý khách là " + subs.getPassword() + ". Xem hướng dẫn soạn HD gửi 9359. Chi tiết truy cập http://mhocvui.vn hoặc liên hệ 024.36688777 (cước gọi cố định). Trân trọng cảm ơn!";
                if (isPokiService()) {
                    content = "Mật khẩu để sử dụng dịch vụ POKI của Quý khách là " + subs.getPassword() + ". Xem hướng dẫn soạn HD gửi 9382. Chi tiết truy cập http://mpoki.vn hoặc liên hệ 9090 (cước gọi cố định). Trân trọng cảm ơn!";
                }
                MtHis mt = new MtHis(0, msisdn, content, moid, transid, "MKACT", new Date(), "SMS");
                sendSMS1(transid, mt);
            } else {
                content = "Quý Khách chưa đăng ký dịch vụ Học vui. Quý khách vui lòng soạn DK TH gửi 9359, giá cước 3.000đ/ngày hoặc truy cập địa chỉ http://mhocvui.vn và làm theo hướng dẫn. Chi tiết liên hệ 024.36688777 (cước gọi cố định). Trân trọng cảm ơn!";
                if (isPokiService()) {
                    content = "Quý Khách chưa đăng ký dịch vụ POKI. Quý khách vui lòng soạn DK PN gửi 9382, giá cước 2.000đ/ngày hoặc truy cập địa chỉ http://mpoki.vn và làm theo hướng dẫn. Chi tiết liên hệ 9090 (cước gọi cố định). Trân trọng cảm ơn!";
                }
                MtHis mt = new MtHis(0, msisdn, content, moid, transid, "MKNACT", new Date(), "SMS");
                sendSMS1(transid, mt);
            }
        } else if ("#MUA1".equalsIgnoreCase(moContent)) {
            // MO test cho tingting
            int moid = insertMO(transid, mo);
            chargeContent(transid, msisdn, 3000, "000001", "0000000001", "VIP", reqid, moContent);
        } else if ("#MUA2".equalsIgnoreCase(moContent)) {
            // MO test cho tingting
            int moid = insertMO(transid, mo);
            chargeContent(transid, msisdn, 5000, "000002", "0000000002", "VIP", reqid, moContent);
        } else if ("brandname".equalsIgnoreCase(moContent)) {
            mo.setCommand("TEST");
            int moid = insertMO(transid, mo);

            String content = "Test MT brandname ..."; // thường thì tin này CCSP sẽ trả
            MtHis mt = new MtHis(0, msisdn, content, moid, transid, "TEST", new Date(), "SMS");
            mt.setShortcode("TingTing");
            sendSMS1(transid, mt);
        } else if ("mk".equalsIgnoreCase(moContent)) {
            mo.setCommand("mk");
            int moid = insertMO(transid, mo);

            String sql = "select 1 from subscriber where msisdn = '" + msisdn + "' and package_id='YV' and status = 1";
            String content = "";
            if (baseDAO.hasResult(transid, sql)) {
                String sqlGetPass = "Select password from subscriber where msisdn='" + mo.getMsisdn() + "' and package_id='YV' and status=1";
                String pass = xbaseDAO.getFirstCell(transid, sqlGetPass, String.class);


                content = "Mật khẩu để sử dụng dịch vụ IWing là " + pass + ". " +
                        "Bạn sử dụng mật khẩu này cùng số điện thoại " + mo.getMsisdn() + " để đăng nhập " +
                        "và sử dụng gói cước tại http://iwing.vn. Dịch vụ hoàn toàn miễn cước 3G/4G. Trân trọng cảm ơn!"; // thường thì tin này CCSP sẽ trả

            } else {
                content = "Quý khách chưa đăng ký dịch vụ IWing. " +
                        "Để đăng ký gói YoVideo, soạn DK YV gửi 9856 (3.000đ/ngày). " +
                        "Chi tiết truy cập http://iwing.vn. hoặc liên hệ 9090(200đ/ph). Trân trọng cảm ơn!"; // thường thì tin này CCSP sẽ trả

            }

            MtHis mt = new MtHis(0, msisdn, content, moid, transid, "getML", new Date(), "SMS");
            mt.setShortcode("iwing");
            sendSMS1(transid, mt);
        } else if ("huy id".equalsIgnoreCase(moContent)) {


            String sqlCheckSub = "select 1 from subscriber where msisdn = '" + msisdn + "' and package_id='ID' and status = 1";
            String content = "";
            if (baseDAO.hasResult(transid, sqlCheckSub)) {


                String rsUnregMsisdn = unRegMsisdn(transid, msisdn, "999", "HUY ID", "ID", "1");
                content = "Quý khách đã hủy thành công gói Kết bạn tích hợp thoại iDating dịch vụ iWing. Để đăng ký lại soạn DK ID gửi 999. Chi tiết truy cập http://iwing.vn hoặc liên hệ 9090 (200đ/ph). Trân trọng cảm ơn!";
                logger.info(transid + " unreg msisdn:" + msisdn + " rs:" + rsUnregMsisdn);
            } else {
                content = "Quý khách chưa đăng ký gói Kết bạn tích hợp thoại iDating dịch vụ iWing. Để đăng ký soạn DK ID gửi 999. Chi tiết truy cập http://iwing.vn hoặc liên hệ 9090 (200đ/ph). Trân trọng cảm ơn!";
                logger.info(transid + " not active msisdn:" + msisdn);
            }
            int moid = insertMO(transid, mo);
            MtHis mt = new MtHis(0, msisdn, content, moid, transid, "WRSYNTAX", new Date(), "SMS");
            sendSMS1(transid, mt);
        } else {
            mo.setCommand("WRSYNTAX");
            int moid = insertMO(transid, mo);
            String content = "Cú pháp thực hiện chưa đúng. Để đăng ký gói Kết bạn soạn DK ID gửi 999 (3.000đ/ngày). Để đăng ký gói YoVideo soạn DK YV gửi 9856 (3.000đ/ngày) Chi tiết truy cập http://iwing.vn hoặc liên hệ 9090 (200đ/ph). Trân trọng cảm ơn!";

            if (isHocVuiService()) {
                content = "Nội dung tin nhắn sai cú pháp. Xem hướng dẫn soạn HD gửi 9359. Để đăng ký dịch vụ, soạn DK TH gửi 9359. Chi tiết truy cập http://mhocvui.vn hoặc liên hệ 024.36688777 (cước gọi cố định). Trân trọng cảm ơn!";
            }
            MtHis mt = new MtHis(0, msisdn, content, moid, transid, "WRSYNTAX", new Date(), "SMS");
            sendSMS1(transid, mt);
        }
    } catch (Exception e) {
        logger.info(transid + ", Exception: " + e.getMessage(), e);
    }


%>







