<%@page import="com.xxx.aps.processor.UpdateStatsRsCode" %>
<%@page import="com.xxx.aps.logic.entity.SqlBean" %>
<%@page import="com.xxx.aps.processor.ExecuteSQL" %>
<%@page import="com.xxx.aps.processor.SaveActionHis" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@page import="com.ligerdev.appbase.utils.http.HttpClientUtils" %>
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
<%@ page import="com.ligerdev.appbase.utils.cache.*" %>

<%@ page trimDirectiveWhitespaces="true" %>
<%@include file="_utils.app.jsp" %>
<%@include file="_ccsp.client.jsp" %>

<%!
    public static CacheSyncFile cacheCCSP = CacheSyncFile.getInstance("CCSP", 10000);
%>

<%
    if (request.getParameter("compile") != null) {
        logger.info("check compile ============");
        out.print("compile ok, v1.0");
        return;
    }
	/*
		// khi đăng ký, hủy, gia hạn thì CCSP call vào file này

		// check compile
		curl 'http://localhost/ccsp/updatePackage.jsp?compile'
		http://localhost:8888/ccsp/updatePackage.jsp?compile
		http://mhocvui.vn/ccsp/updatePackage.jsp?compile

		// DK
		http://45.121.26.232/ccsp/updatePackage.jsp?isdn=909090529&status=1&seviceCode=TingTing&packageCode=V1&commandCode=DK+V1&expireDatetime=18/08/2018 23:59:59&channel=SMS&message_send=Chuc Mung QK DK thanh cong!

		// HUY
		http://45.121.26.232/ccsp/updatePackage.jsp?isdn=909090529&status=3&seviceCode=TingTing&packageCode=V1&channel=SMS&expireDatetime=18/08/2018 23:59:59&message_send=HUY thanh cong

		// renew
		http://45.121.26.232/ccsp/updatePackage.jsp?isdn=909090529&status=0&seviceCode=TingTing&packageCode=V1&channel=SYS&expireDatetime=28/08/2018 23:59:59

		// MO
		http://45.121.26.232/ccsp/forwardMessage.jsp?isdn=909090529&content=KT
	 */
    String msisdn = BaseUtils.formatMsisdn(request.getParameter("isdn"), "84", "84");
    long l1 = System.currentTimeMillis();

    if (msisdn == null) {
        out.print("blank msisdn");
        return;
    }
    // mặc định luôn luôn response ok
    String rsOK = "{\"resultCode\":\"1\", result: 1}";
    out.print(rsOK);

    int today = Integer.parseInt(new SimpleDateFormat("yyyyMMdd").format(new Date()));
    final String transid = "U" + StringGenerator.randomCharacters(5) + "@" + BaseUtils.formatMsisdn(msisdn, "84", "");
    try {
        String format = "dd/MM/yyyy HH:mm:ss";
        int status = BaseUtils.parseInt(request.getParameter("status"), -1);
        int charge_price = BaseUtils.parseInt(request.getParameter("charge_price"), 0);

        logger.info(transid + ", ############ receive req: " + request.getRemoteAddr()
                + ", msisdn: " + msisdn + ", status: " + status + ", fee: " + charge_price
                + " | queryStr: " + request.getQueryString());

        // getHeaders(transid, request, true);
        // getParameters(transid, request, true);
        String s = getParameterString(transid, request);
        logger.info(transid + ", parameter: " + s);

        String seviceCode = request.getParameter("seviceCode");
        // String groupCode = request.getParameter("groupCode");
        String packageCode = String.valueOf(request.getParameter("packageCode")).toUpperCase();
        // String regDatetime = request.getParameter("regDatetime");
        // String staDatetime = request.getParameter("staDatetime");
        // String endDatetime = request.getParameter("endDatetime");
        String expireDatetime = request.getParameter("expireDatetime");
        // String commandCode = request.getParameter("commandCode");
        String commandCode = request.getParameter("org_request");
        String channel = request.getParameter("channel");
        String message_send = request.getParameter("message_send");

        if (channel == null || channel.equalsIgnoreCase("CP")) {
            channel = "SMS";
        }

        // có tình huống thấy CCSP lỗi, hủy mà vẫn truyền status = 1
        // fix tạm để test, sau có thể bỏ sau khi CCSP fix
        if (commandCode.toLowerCase().startsWith("huy") && status != 3) {
            logger.info(transid + ", !!!!!!!!!!!!!!!! check status ...");
            status = 3;
        }
        if (status == 0) {  // gia han
            Subscriber subs = getSubscriber(transid, msisdn, packageCode);
            Date expireTime = BaseUtils.parseTime(format, expireDatetime);

            int moid = 0;

            if ("SMS".equalsIgnoreCase(channel) && BaseUtils.isNotBlank(commandCode)) {
                MoHis mo = new MoHis(0, msisdn, commandCode, transid, "RENEW@" + packageCode, new Date(), null);

                if (XmlConfigs.short_sms_table) {
                    String table = "mo_" + new SimpleDateFormat("yyyy").format(new Date());
                    moid = xbaseDAO.insertBean(transid, table, mo);
                } else {
                    moid = xbaseDAO.insertBean(transid, mo);
                }
            }

            if (subs != null) {
                if (subs.getSubnote4() != null && !subs.getSubnote4().equalsIgnoreCase("")) {
                    logger.info(transid + "commandcode:" + commandCode + " subnote4:" + subs.getSubnote4() + " cpid:" + subs.getCpid());
                    if ("GHMK ID ID".equalsIgnoreCase(commandCode.toUpperCase()) && subs.getSubnote4().equalsIgnoreCase("dknotcharge") && subs.getCpid().equalsIgnoreCase("DK ID NOT EM ID")) {
                        String pass = xbaseDAO.getFirstCell(transid, "select password from subscriber where msisdn = '" + msisdn + "'", String.class);
                        if (pass == null) {
                            pass = StringGenerator.randomDigits(5);
                        }
                        logger.info(transid + " GHMK ID");
                        if ("ID".equalsIgnoreCase(packageCode)) {
                            String sql = "select friend_code from friend_code where msisdn = '" + msisdn + "'";
                            String value = xbaseDAO.getFirstCell(transid, sql, String.class);

                            if (value == null) {
                                value = StringGenerator.randomDigits(9);
                                sql = "insert into friend_code(msisdn, friend_code) values ('" + msisdn + "', '" + value + "')";
                                xbaseDAO.execSql(transid, sql);
                            }
                            logger.info(transid + ", respCode => " + value);

                            String contentMT = "Quý Khách đã là thành viên của mạng xã hội kết bạn iWing," +
                                    " Quý Khách có 15 phút gọi nội mạng miễn phí mỗi ngày. Mã số kết bạn của QK là " + value + "." +
                                    " Mật khẩu là: " + pass + ". Truy cập  http://iwing.vn để tham gia cộng đồng kết bạn bốn phương. Trân trọng cảm ơn.";
                            MtHis passMT = new MtHis(0, msisdn, contentMT, moid, transid, "RENEW@", new Date(), "SMS");
                            sendSMS1Brandname(transid, passMT, "brandname");
//                            sendSMS0(transid, msisdn, contentMT, "brandname");

                            String sqlUpdateNotEm = "update subscriber set subnote4='chagesuccess',cpid='DK ID ID' where msisdn = '" + msisdn + "' and package_id='ID'";
                            int rsUpdateNotEm = xbaseDAO.execSql(transid, sqlUpdateNotEm);
                            logger.info("update subscriber chart not em resulr: " + rsUpdateNotEm);
                        }
                    }
                }
            }

            // thống kê
            int HHmm = Integer.parseInt(new SimpleDateFormat("HHmm").format(new Date()));
            int loadNumber = HHmm >= 830 ? -1 : 0; // quy ước load lần đầu ban đêm là 0, sau 8h30 sáng (retry), quy ước = -1

            if (subs != null) {
                int regdate = Integer.parseInt(new SimpleDateFormat("yyyyMMdd").format(subs.getActiveTime()));
                int expireint = BaseUtils.getDateDiff(subs.getExpireTime(), new Date());
                int activeint = BaseUtils.getDateDiff(subs.getActiveTime(), new Date());
                String hisnote1 = new SimpleDateFormat("yyyyMMdd").format(new Date());
                String hisnote2 = null, hisnote3 = null;

                if (charge_price > 0) {
                    subs.setSubnote2("" + (BaseUtils.parseInt(subs.getSubnote2(), 0) + charge_price));
                    subs.setSubnote3("" + (BaseUtils.parseInt(subs.getSubnote3(), 0) + charge_price));

                    ActionHis actionHis = new ActionHis(0, msisdn, "RENEW", new Date(), charge_price, transid, subs.getNote(), subs.getActiveChannel(), 0,
                            packageCode, subs.getCpid(), expireTime, regdate, subs.getSubnote1(),
                            subs.getSubnote2(), subs.getSubnote3(), subs.getSubnote4(), subs.getSubnote5(),
                            expireint, activeint, hisnote1, hisnote2, hisnote3);
                    SaveActionHis.queue.put(actionHis);

                    String sql =
                            "update subscriber set " +
                                    // "status = 1, " +
                                    "last_renew = now(), " +
                                    "subnote2 = '" + subs.getSubnote2() + "', " +
                                    "subnote3 = '" + subs.getSubnote3() + "', " +
                                    "expire_time = '" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(expireTime) + "' " +
                                    "where msisdn = '" + msisdn + "' and package_id = '" + packageCode + "'";
                    ExecuteSQL.queue.put(new SqlBean(transid, sql));



                    // thống kê
                    StatsRscode stats = new StatsRscode(today, "CPS-0000", 1, loadNumber,
                            subs.getActiveChannel(), charge_price, packageCode, "RENEW", subs.getCpid(), "#", "#");
                    UpdateStatsRsCode.queue.put(stats);
                } else {
                    // gia hạn thất bại
                    String sql =
                            "update subscriber set " +
                                    "last_retry = now() " +
                                    "where msisdn = '" + msisdn + "' and package_id = '" + packageCode + "'";
                    ExecuteSQL.queue.put(new SqlBean(transid, sql));

                    // thống kê
                    StatsRscode stats = new StatsRscode(today, "CPS-1001", 1, loadNumber, channel, charge_price, packageCode, "RENEW", subs.getCpid(), "#", "#");
                    UpdateStatsRsCode.queue.put(stats);
                }
            } else {
                logger.info(transid + ", @@@@@@@@@@@ NOTE , renew but subs is not exist");
                String hisnote1 = new SimpleDateFormat("yyyyMMdd").format(new Date());

                if (charge_price > 0) {
                    ActionHis actionHis = new ActionHis(0, msisdn, "RENEW", new Date(), charge_price, transid, null, channel, 0,
                            packageCode, null, null, 0, null,
                            null, null, null, null,
                            0, 0, hisnote1, null, null);
                    SaveActionHis.queue.put(actionHis);

                    // thống kê
                    StatsRscode stats = new StatsRscode(today, "CPS-0000", 1, loadNumber, channel, charge_price, packageCode, "RENEW", "#", "#", "#");
                    UpdateStatsRsCode.queue.put(stats);
                } else {
                    // thống kê
                    StatsRscode stats = new StatsRscode(today, "CPS-1001", 1, loadNumber, channel, charge_price, packageCode, "RENEW", "#", "#", "#");
                    UpdateStatsRsCode.queue.put(stats);
                }
            }
            logger.info(transid + ", renew done, time: " + BaseUtils.getDurations(System.currentTimeMillis(), l1));
            return;
        }
        if (status == 1) {  // dang ky

            String cpid = null;
            String groupCpid = null;
            if (channel.equalsIgnoreCase("CP")) {
                channel = "SMS";
            }

            if ("SMS".equalsIgnoreCase(channel)) {
                cpid = commandCode;
                groupCpid = "SMS";
            } else {
                String key = msisdn + "." + packageCode;
                cpid = (String) cacheCCSP.getObject(key);

                if (cpid != null) {
                    logger.info(transid + ", hit cache cpid: " + key + "=" + cpid);

                    if (cpid.contains("@")) {  // cpid@groupcpid
                        groupCpid = cpid.split("@")[1];
                        cpid = cpid.split("@")[0];
                    }
                }
            }

            String sqlGetCallAvb = "select day_call from call_avb where status=1";
            ArrayList<String> listDay = xbaseDAO.getListBySql(transid, String.class, sqlGetCallAvb, null, null);
            String dayNow = new SimpleDateFormat("yyyyMMdd").format(new Date());

            if (listDay.contains(dayNow)) {
                channel = "AVB";
                groupCpid = "AVB";
                logger.info(transid + " regAvb msisdn:" + msisdn);
            }


            String note = null;
            Date expireTime = BaseUtils.parseTime(format, expireDatetime);
            int regdate = Integer.parseInt(new SimpleDateFormat("yyyyMMdd").format(new Date()));
            Date activeTime = new Date();
            String subnote1 = groupCpid;
            String subnote2 = "0";   // số tiền trừ dc của gói, ko reset khi hủy/dk lại
            String subnote3 = "0";     // số tiền trừ dc của gói, reset khi hủy/dk lại
            String subnote4 = null, subnote5 = null;

            // => thường thì sẽ là đăng ký lại nếu giá  > 0 (kịch bản dk lần đầu toàn thấy free)
//            String actionType = charge_price > 0 ? "ReREG" : "FirstREG";
            String actionType = "FirstREG";
            String hisnote1 = new SimpleDateFormat("yyyyMMdd").format(new Date());
            String hisnote2 = null, hisnote3 = null;
            int expireint = 0, activeint = 0;

            // kiểm tra xem số này đã active gói hay chưa
            Subscriber subs = getSubscriber(transid, msisdn, packageCode);

            if (subs != null) {
                actionType = "ReREG";
                subnote2 = "" + (BaseUtils.parseInt(subs.getSubnote2(), 0) + charge_price);
                // subnote3 = "" + (BaseUtils.parseInt(subs.getSubnote3(), 0) + charge_price);
                subnote3 = "" + (charge_price);
            }
            ActionHis actionHis = new ActionHis(0, msisdn, actionType, new Date(), charge_price, transid, note, channel, 0,
                    packageCode, cpid, expireTime, regdate, subnote1, subnote2, subnote3, subnote4, subnote5,
                    expireint, activeint, hisnote1, hisnote2, hisnote3);
            SaveActionHis.queue.put(actionHis);

            String pass = xbaseDAO.getFirstCell(transid, "select password from subscriber where msisdn = '" + msisdn + "'", String.class);
            if (pass == null) {
                pass = StringGenerator.randomDigits(5);
            }
            int subType = 0;
            boolean isActiveBefore = false;
            if ("DK ID NOT EM ID".equalsIgnoreCase(commandCode)) {
                subnote4 = "dknotcharge";
            }
            if (subs == null) {
                // insert

                subs = new Subscriber(msisdn, new Date(), expireTime, packageCode, activeTime, null,
                        null, channel, null, status, note, cpid, pass,
                        0, subnote1, subnote2, subnote3, subnote4, subnote5);
                xbaseDAO.insertBean(transid, subs);
            } else {
                if (subs.getStatus() == 1) {
                    logger.info(transid + ", activing before (unsync ccsp) => update subscriber");
                    isActiveBefore = true;
                }
                String sql =
                        "update subscriber set " +
                                "status = 1, " +
                                "active_time = now(), " +
                                "cpid = '" + cpid + "', " +
                                "active_channel = '" + channel + "', " +
                                "note = '" + note + "', " +
                                "password = '" + pass + "', " +
                                "ignore_sms_content = 0, " +
                                "subnote1 = '" + subnote1 + "', " +
                                "subnote2 = '" + subnote2 + "', " +
                                "subnote3 = '" + subnote3 + "', " +
                                "subnote4 = '" + subnote4 + "', " +
                                "expire_time = '" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(expireTime) + "' " +
                                "where msisdn = '" + msisdn + "' and package_id = '" + packageCode + "'";
                xbaseDAO.execSql(transid, sql);
            }
            int moid = 0;

            if ("SMS".equalsIgnoreCase(channel) && BaseUtils.isNotBlank(commandCode)) {
                MoHis mo = new MoHis(0, msisdn, commandCode, transid, "REG@" + packageCode, new Date(), null);

                if (XmlConfigs.short_sms_table) {
                    String table = "mo_" + new SimpleDateFormat("yyyy").format(new Date());
                    moid = xbaseDAO.insertBean(transid, table, mo);
                } else {
                    moid = xbaseDAO.insertBean(transid, mo);
                }
            }
            logger.info("dk1");
            if (BaseUtils.isNotBlank(message_send)) {
                // MT này do CCSP trả, CP chỉ insert DB để tra cứu CSKH
                MtHis mt = new MtHis(0, msisdn, message_send, moid, transid, "REG@" + packageCode, new Date(), "SMS");
                insertMT(transid, mt);
            } else {
                logger.info("dk2" + packageCode.toUpperCase());
                if ("ID".equalsIgnoreCase(packageCode)) {
                    logger.info("dk3" + packageCode.toUpperCase());
                    MoHis mo = new MoHis(0, msisdn, commandCode, transid, "REG@" + packageCode, new Date(), null);
                    if (XmlConfigs.short_sms_table) {
                        String table = "mo_" + new SimpleDateFormat("yyyy").format(new Date());
                        moid = xbaseDAO.insertBean(transid, table, mo);
                    } else {
                        moid = xbaseDAO.insertBean(transid, mo);
                    }
                    MtHis mt = new MtHis(0, msisdn, "Quy khach DK thanh cong goi cuoc Ket ban tich hop thoai ID dich vu iWing," +
                            " tu dong gia han hang ngay (su dung tai VN). Quy khach duoc tang 15 Phut thoai noi mang mien phi hang ngay." +
                            " Han su dung den ngay dd/mm/yyyy hh:mm:ss. Gia goi 3.000 dong/ngay." +
                            " De huy goi soan HUY ID gui 999. Chi tiet lien he 9090 (200d/phut).  Tran trong cam on!", moid, transid, "REG@" + packageCode, new Date(), "SMS");
                    insertMT(transid, mt);

                }
            }
            // thống kê đăng ký
            cpid = cpid == null ? "#" : cpid;
            StatsRscode stats = new StatsRscode(today, "CPS-0000", 1, -1, channel, charge_price, packageCode, actionType, cpid, "#", "#");
            UpdateStatsRsCode.queue.put(stats);

            if ("DK ID NOT EM ID".equalsIgnoreCase(cpid)) {
                logger.info(transid + " DK ID NOT EM ID not send mess");
            } else if ("ID".equalsIgnoreCase(packageCode)) {
                String sql = "select friend_code from friend_code where msisdn = '" + msisdn + "'";
                String value = xbaseDAO.getFirstCell(transid, sql, String.class);

                if (value == null) {
                    value = StringGenerator.randomDigits(9);
                    sql = "insert into friend_code(msisdn, friend_code) values ('" + msisdn + "', '" + value + "')";
                    xbaseDAO.execSql(transid, sql);
                }
                logger.info(transid + ", respCode => " + value);

                String contentMT = "Quý Khách đã là thành viên của mạng xã hội kết bạn iWing," +
                        " Quý Khách có 15 phút gọi nội mạng miễn phí mỗi ngày. Mã số kết bạn của QK là " + value + "." +
                        " Mật khẩu là: " + pass + ". Truy cập  http://iwing.vn để tham gia cộng đồng kết bạn bốn phương. Trân trọng cảm ơn.";
                MtHis passMT = new MtHis(0, msisdn, contentMT, moid, transid, "REG@", new Date(), "SMS");
                sendSMS1Brandname(transid, passMT, "brandname");
                if (!isActiveBefore) {
                    String contentKM = "(KM) Tuyệt vời! Quý khách được tham gia chương trình khuyến mãi với cơ hội được cộng 10.000đ khi duy trì " +
                            "liên tiếp gói cước iDating trong vòng 72h (thời gian cộng tiền vào tài khoản từ 14h00 đến 17h59)." +
                            " Chi tiết truy cập http://iwing.vn  hoặc liên hệ 9090 (200đ/phút).Trân trọng cảm ơn!";
                    MtHis promotionMT = new MtHis(0, msisdn, contentKM, moid, transid, "REG@", new Date(), "SMS");
                    sendSMS1Brandname(transid, promotionMT, "brandname");
                } else {
                    if (channel.equals("SMS") || channel.equals("AVB")) {
                        String contentKM = "Rất tiếc! Quý khách đã tham gia CTKM trước đó. Chi tiết liên hệ 9090 (200đ/phút). Trân trọng cám ơn!";
                        MtHis promotionMT = new MtHis(0, msisdn, contentKM, moid, transid, "REG@", new Date(), "SMS");
                        sendSMS1Brandname(transid, promotionMT, "brandname");
                    } else {
                        String contentKM = "Rất tiếc! Quý khách không thuộc đối tượng tham gia CTKM. Chi tiết liên hệ 9090 (200đ/phút). Trân trọng cám ơn!";
                        MtHis promotionMT = new MtHis(0, msisdn, contentKM, moid, transid, "REG@", new Date(), "SMS");
                        sendSMS1Brandname(transid, promotionMT, "brandname");
                    }
                }
            }


            logger.info(transid + ", register done, time: " + BaseUtils.getDurations(System.currentTimeMillis(), l1));
            return;
        }
        if (status == 2) { // chờ confirm
            logger.info(transid + ", wait confirm Y");
            return;
        }
        if (status == 4 && (commandCode.startsWith("HTHUY") || commandCode.startsWith("HTHUY"))) {
            // hủy gửi lên 999 => chuẩn hóa status
            status = 3;
            if (commandCode.startsWith("HTHUY")) {
                channel = "SYS";
            }
        }
        if (status == 3) {

            // hủy
            charge_price = 0;
            String note = null;
            Subscriber subs = getSubscriber(transid, msisdn, packageCode);

            if (commandCode.startsWith("HTHUY")) {
                return;
            }

            if (subs == null || subs.getStatus() == 3) {
                logger.info(transid + ", unregistered before => return");
                return;
            }
            int regdate = Integer.parseInt(new SimpleDateFormat("yyyyMMdd").format(subs.getActiveTime()));
            int expireint = BaseUtils.getDateDiff(subs.getExpireTime(), new Date());
            int activeint = BaseUtils.getDateDiff(subs.getActiveTime(), new Date());
            String hisnote1 = new SimpleDateFormat("yyyyMMdd").format(new Date());
            String hisnote2 = subs.getActiveChannel(), hisnote3 = null;

            ActionHis actionHis = new ActionHis(0, msisdn, "UNREG", new Date(), charge_price, transid, note, channel, 0,
                    packageCode, subs.getCpid(), subs.getExpireTime(), regdate, subs.getSubnote1(),
                    subs.getSubnote2(), subs.getSubnote3(), subs.getSubnote4(), subs.getSubnote5(),
                    expireint, activeint, hisnote1, hisnote2, hisnote3);
            SaveActionHis.queue.put(actionHis);

            String sql =
                    "update subscriber set " +
                            "status = 3, subnote4='huy', " +
                            "deactive_channel = '" + channel + "', " +
                            "deactive_time = now() " +
                            "where msisdn = '" + msisdn + "' and package_id = '" + packageCode + "' ";
            xbaseDAO.execSql(transid, sql);
            int moid = 0;

            if ("SMS".equalsIgnoreCase(channel) && BaseUtils.isNotBlank(commandCode)) {
                MoHis mo = new MoHis(0, msisdn, commandCode, transid, "UNREG@" + packageCode, new Date(), null);

                if (XmlConfigs.short_sms_table) {
                    String table = "mo_" + new SimpleDateFormat("yyyy").format(new Date());
                    moid = xbaseDAO.insertBean(transid, table, mo);
                } else {
                    moid = xbaseDAO.insertBean(transid, mo);
                }
            }
            logger.info("huy1");
            if (BaseUtils.isNotBlank(message_send)) {
                MtHis mt = new MtHis(0, msisdn, message_send, moid, transid, "UNREG@" + packageCode, new Date(), "SMS");
                insertMT(transid, mt);
            } else {
                logger.info("huy2" + packageCode.toUpperCase());
                if ("ID".equalsIgnoreCase(packageCode)) {
                    logger.info("huy3" + packageCode);
                    MoHis mo = new MoHis(0, msisdn, "HUY ID", transid, "UNREG@" + packageCode, new Date(), null);
                    if (XmlConfigs.short_sms_table) {
                        String table = "mo_" + new SimpleDateFormat("yyyy").format(new Date());
                        moid = xbaseDAO.insertBean(transid, table, mo);
                    } else {
                        moid = xbaseDAO.insertBean(transid, mo);
                    }
                    MtHis mt = new MtHis(0, msisdn, "Yeu cau huy goi cuoc ID cua Quy khach thanh cong. " +
                            "Vui long truy cap www.mobifone.vn hoac lien he 9090 (200d/phut) de biet them chi tiet va " +
                            "de tranh phat sinh cuoc cao. Xin cam on!", moid, transid, "UNREG@" + packageCode, new Date(), "SMS");
                    insertMT(transid, mt);
                }
            }
            // thống kê hủy
            StatsRscode stats = new StatsRscode(today, "CPS-0000", 1, -1, channel, charge_price, packageCode, "UNREG", subs.getCpid(), "#", "#");
            UpdateStatsRsCode.queue.put(stats);
            logger.info(transid + ", unregister done, time: " + BaseUtils.getDurations(System.currentTimeMillis(), l1));
            return;
        }
        // ignore
        logger.info(transid + ", wrong action = " + status);

    } catch (Exception e) {
        logger.info(transid + ", Exception: " + e.getMessage(), e);
    }
%>