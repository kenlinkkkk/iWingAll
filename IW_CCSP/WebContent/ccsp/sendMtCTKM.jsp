<%-- <%@page import="com.xtel.cms.db.orm.xauthen.XjschAccount"%> --%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="org.w3c.dom.Element" %>
<%@ page import="java.io.*" %>
<%@ page import="java.sql.*" %>
<%@ page import="org.w3c.dom.Document" %>
<%@ page import="org.apache.commons.lang.StringUtils" %>
<%@ page import="com.ligerdev.appbase.utils.http.*" %>
<%@ page import="java.util.regex.*" %>
<%@ page import="com.ligerdev.appbase.utils.cache.*" %>
<%@ page import="com.ligerdev.appbase.utils.*" %>
<%@ page import="com.ligerdev.appbase.utils.textbase.*" %>
<%@ page import="java.net.*" %>
<%@ page import="java.util.*" %>
<%@ page import="java.util.Date" %>
<%@ page import="org.apache.log4j.*" %>
<%@ page import="com.ligerdev.appbase.utils.db.*" %>
<%@ page import="java.text.*" %>
<%-- <%@ page import="org.apache.commons.httpclient.*"%> --%>
<%@ page import="com.ligerdev.appbase.utils.encrypt.*" %>
<%@page import="java.math.BigDecimal" %>
<%@page import="java.util.Map.*" %>
<%@page import="javax.servlet.http.Cookie" %>
<%@ page import="java.util.concurrent.atomic.AtomicInteger" %>
<%@ page import="com.xxx.aps.logic.db.orm.Subscriber" %>
<%@ page import="com.xxx.aps.logic.db.orm.ActionHis" %>
<%@ page import="com.xxx.aps.processor.ExecuteSQL" %>
<%@ page import="com.xxx.aps.logic.entity.SqlBean" %>
<%@ page import="com.xxx.aps.logic.db.orm.MtHis" %>
<%@ page import="com.xxx.aps.utils.AppUtils" %>
<%@ page import="javax.ws.rs.Encoded" %>

<%@ page trimDirectiveWhitespaces="true" %>

<%
    if (request.getParameter("compile") != null) {
        logger.info("check compile ============");
        out.print("compile ok, v1.0");
        return;
    }
    logger.info("========call sendMtCTKM===========");

//    String folderCdr = "/opt/kns/cdr/ccsp/ready/";
    String folderCdr = "/home/ftp/mobifonecdr/";
    File files[] = new File(folderCdr).listFiles();
    moveBackupFile();
    if (files == null || files.length == 0) {
        // response.getWriter().print("blank folder");
        return;
    }
			/*
				ON3_1598_TT_MVAS;934531615;23281111;16/06/2020 14:17:34
                ON1_1598_TT_MVAS;934531615;23281026;16/06/2020 14:12:23
                ON4_1598_TT_MVAS;934531615;23281150;16/06/2020 14:19:08
                ON2_1598_TT_MVAS;934531615;23281048;16/06/2020 14:13:48
*/

    final Comparator<File> compare1 = new Comparator<File>() {
        // @Override
        public int compare(File o1, File o2) {
            return o1.getName().compareTo(o2.getName());
        }
    };
    Arrays.sort(files, compare1);
    final AtomicInteger counter = new AtomicInteger();

    for (File f : files) {
        logger.info(transid + ", found: " + f.getAbsolutePath());
//
        if (f.getName().endsWith("txt")) {
//
            BaseUtils.readFile(f, new IReadFile() {
                public boolean readLine(String line) throws Exception {
                    try {
                        if (!line.startsWith("prom_code")) {
                            String str[] = line.split(";");
                            String prom_code = str[0];
                            String syntax = str[0].split("_")[1];
                            String msisdn = str[1];
                            String prom_his_id = str[2];
                            String issue_datetime = str[3];
                            msisdn = BaseUtils.formatMsisdn(msisdn, "84", "84");
                            logger.info(transid + " syntax:" + syntax + " prom_code:" + prom_code + " msisdn:" + msisdn + " prom_his_id:" + prom_his_id + " issue_datetime:" + issue_datetime);

                            String sqlUpdateHis = "select 1 from his_" + new SimpleDateFormat("yyyyMM").format(new Date()) + " " +
                                    "where msisdn ='" + msisdn + "' and action='FirstREG' and pkg_code='ID'";
                            if (baseDAO.hasResult(transid, sqlUpdateHis) == true) {
                                if (line.contains("ON1") || line.contains("ON3")) {
                                    String updateStatsKMTQ = "update his_" + new SimpleDateFormat("yyyyMM").format(new Date()) + " " +
                                            "set hisnote3='IK_MONEYv1' where msisdn ='" + msisdn + "' and action='FirstREG' and pkg_code='ID'";

                                    xbaseDAO.execSql(transid, updateStatsKMTQ);
                                } else {
                                    String updateStatsKMTQ = "update his_" + new SimpleDateFormat("yyyyMM").format(new Date()) + " " +
                                            "set hisnote3='IK_DATAv1' where msisdn ='" + msisdn + "' and action='FirstREG' and pkg_code='ID'";

                                    xbaseDAO.execSql(transid, updateStatsKMTQ);
                                }

                            }

                            String URLSendSms = "http://localhost:8888/ccsp/sendsms.jsp?msisdn=@msisdn&content=@content";

                            String sqlCheckSendNotiKM = "select 1 from his_sms_notiKM where msisdn = '" + msisdn + "'";
                            String sqlCheckActiveSubs = "select 1 from subscriber where msisdn ='" + msisdn + "' and status=1 and package_id='ID' and deactive_time is null";
                            if (baseDAO.hasResult(transid, sqlCheckSendNotiKM) == false && baseDAO.hasResult(transid, sqlCheckActiveSubs) == true) {

                                if (line.contains("ON1") || line.contains("ON3")) {
                                    String content = "(KM) Tuyệt vời! Quý khách được tham gia chương trình khuyến mãi với cơ hội được cộng 10.000đ khi duy trì liên tiếp gói cước iDating trong vòng 72h (thời gian cộng tiền vào tài khoản từ 14h00 đến 17h59)." +
                                            " Chi tiết truy cập http://iwing.vn  hoặc liên hệ 9090 (200d/phut).Trân trọng cảm ơn!";
                                    URLSendSms = URLSendSms.replace("@content", URLEncoder.encode(content));
                                    URLSendSms = URLSendSms.replace("@msisdn", msisdn);
                                    HttpClientUtils.getDefault(transid, URLSendSms, 5000);
                                    if(line.contains("ON1")){
                                        syntax="ON1";
                                    }else {
                                        syntax="ON3";
                                    }
                                    String sqlInsertNoti = "insert into his_sms_notiKM(msisdn,syntax,created_time) values ('" + msisdn + "','" + syntax + "',now())";

                                    xbaseDAO.execSql(transid, sqlInsertNoti);


                                } else {
                                    String content = "(KM) Xin chúc mừng ! Quý khách được tham gia chương trình khuyến mại với cơ hội nhận được 1GB truy cập 3G/4G tốc độ cao từ dịch vụ iWing của MobiFone khi duy trì gói cước iDating sau 72h." +
                                            " Chi tiết truy cập http://iwing.vn hoặc liên hệ 9090 (200d/phut). Trân trọng cảm ơn!";
                                    URLSendSms = URLSendSms.replace("@content", URLEncoder.encode(content));
                                    URLSendSms = URLSendSms.replace("@msisdn", msisdn);
                                    HttpClientUtils.getDefault(transid, URLSendSms, 5000);
                                    if(line.contains("ON2")){
                                        syntax="ON2";
                                    }else {
                                        syntax="ON4";
                                    }
                                    String sqlInsertNoti = "insert into his_sms_notiKM(msisdn,syntax,created_time) values ('" + msisdn + "','" + syntax + "',now())";

                                    xbaseDAO.execSql(transid, sqlInsertNoti);
                                }
                            }

                        }
                    } catch (Exception e) {
                        logger.info(transid + ", exception: " + e.getMessage() + ", line: " + line);
                    }
                    return true;
                }
            });
        }
        String folderBackup = "/home/ftp/backup_mobifonecdr/";
        boolean bb = f.renameTo(new File(folderBackup + f.getName()));

//        boolean b = f.delete();
//        logger.info(transid + ", delete file " + f.getName() + ", rs: " + b);
    }


%>

<%!

    private static Logger logger = Logger.getLogger("LOG");
    private static BaseDAO baseDAO = BaseDAO.getInstance("main");
    private static XBaseDAO xbaseDAO = XBaseDAO.getInstance("main");
    private static String transid = "CR@" + StringGenerator.randomCharacters(4);

    private static Subscriber getSubscriber(String transid, String msisdn, String pkgCode) {
        String sql = "select * from subscriber where msisdn = '" + msisdn + "' and package_id = '" + pkgCode + "'";
        return xbaseDAO.getBeanBySql(transid, Subscriber.class, sql);
    }

    private static void moveBackupFile() {
        try {
            String folder = "/home/ftp/backup_mobifonecdr/"; // ko đổi
            File ff = new File(folder);

            if (ff.exists() == false) {
                return;
            }
            File f[] = ff.listFiles();
            String mileStone = new SimpleDateFormat("yyyyMMdd").format(BaseUtils.addTime(new Date(), Calendar.DATE, -1));
            logger.info(transid + " mileStone" + mileStone);
            for (File f2 : f) {
                if (f2.isDirectory()) {
                    continue;
                }
                String name = f2.getName();
                if (name.endsWith(".txt") == false && name.endsWith(".gz") == false) {
                    continue;
                }
                String dateFile = f2.getName().split("_")[1].substring(0, 8);
                if (dateFile.compareTo(mileStone) > 0) {
                    continue;
                }
                String folderdate = folder + dateFile + "/";
                if (new File(folderdate).exists() == false) {
                    new File(folderdate).mkdirs();
                }
                boolean rs = f2.renameTo(new File(folderdate + f2.getName()));
                logger.info("MoveFile2SubFolder: " + f2.getName() + " = " + rs);
            }
        } catch (Exception e) {
            logger.info("Exception: " + e.getMessage(), e);
        }
    }
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Login form</title>
    <!--link href='http://fonts.googleapis.com/css?family=Lato:400,700' rel='stylesheet' type='text/css'-->
</head>
<body>


</body>
</html>