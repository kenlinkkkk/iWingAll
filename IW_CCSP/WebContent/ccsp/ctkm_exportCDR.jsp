<%@page import="com.ligerdev.appbase.utils.BaseUtils"%>
<%@page import="com.ligerdev.appbase.utils.db.BaseDAO"%>
<%@page import="com.ligerdev.appbase.utils.textbase.Log4jLoader"%>
<%@page import="org.apache.log4j.Logger"%>
<%@ page import="java.util.Date" %>
<%@ page import="java.util.Calendar" %>
<%@ page import="java.text.DateFormat" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.sql.ResultSet" %>
<%@ page import="java.sql.Connection" %>
<%@ page trimDirectiveWhitespaces="true"%>
<%@ page import="java.io.File" %>
<%@ page import="java.sql.PreparedStatement" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="com.ligerdev.appbase.utils.textbase.StringGenerator" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>
<%!
    Logger log = Log4jLoader.getLogger();
    BaseDAO baseDAO = BaseDAO.getInstance("main");

%>
<%
    /*

    /opt/kns/bin/api/root/ccsp/exportmbf/ctkm_exportCDR.jsp
    http://localhost:8888/ccsp/exportmbf/ctkm_exportCDR.jsp
    http://doangia.mobifoneplus.vn/ccsp/exportmbf/ctkm_exportCDR.jsp
    curl 'http://localhost:8888/ccsp/exportmbf/ctkm_exportCDR.jsp?compile'

    */
    if(request.getParameter("compile") != null){
        out.write("1.12.5.19");
        return;
    }

    String transid= StringGenerator.randomCharacters(8);
    log.info("#######_"+transid+" ctkm_exportCDR");
    Date now=new Date();
    Calendar c = Calendar.getInstance();
    c.setTime(now);
    c.add(Calendar.DATE, -1);
    Date day=c.getTime();

    String folderPath="/opt/kns/cdr/syn_daily/CTKM/";
    //folderPath="/opt/kns/cdr/xxxsyn_daily/CTKM/";
    String ngayhomqua=DateToStringByFormatConfig(day,"yyyyMMdd");
    Date dayhomqua=StringToDateByFormatConfig(ngayhomqua,"yyyyMMdd");
    out.print(dangkyngaygoimesay(transid, dayhomqua,folderPath, ngayhomqua, log, baseDAO));
    out.print(ngayhomqua);

%>

<%!
    private static String dangkyngaygoimesay(String transid, Date dayhomqua, String folderPath, String ngayhomqua, Logger log, BaseDAO baseDAO) {
        //Đăng ký ngay - gọi mê say- Từ 00h00:00 ngày 17/06/2021 đến hết 23h59:59 ngày 12/09/2021
        String name = "dangkyngaygoimesay";
        //CDR thuê bao đăng ký
        Date beginctkm = null;
        Date endctkm = null;
        beginctkm = StringToDateByFormatConfig("20210617", "yyyyMMdd");
        endctkm = StringToDateByFormatConfig("20210913", "yyyyMMdd");
        if(beginctkm.getTime()<=dayhomqua.getTime()&&dayhomqua.getTime()<=endctkm.getTime()){
            String where=" where report_time>='20210617' and status_exportcdr=0 and category='ikv3' and created_time>='"+DateToStringByFormatConfig(beginctkm,"yyyyMMdd")+"' and created_time<='"+DateToStringByFormatConfig(endctkm,"yyyyMMdd")+"' + interval 1 day";
            String sqlselect="select msisdn from stats_kmtq "+where;
            String sqlupdate="update stats_kmtq set status_exportcdr=2 "+where;
            log.info("_"+transid+"_sql tbdk: "+sqlselect);
            ResultSet rs = null;
            PreparedStatement stmt = null;
            Connection conn = null;
            ArrayList<String> data=new ArrayList<String>();
            try {
                conn = BaseDAO.getInstance("main").getConnection();
                stmt = conn.prepareStatement(sqlselect);
                rs = stmt.executeQuery();
                while (rs.next()) {
                    String msisdn=rs.getString("msisdn");
                    String neifResult=msisdn.substring(2)+",ID";
                    data.add(neifResult);
                }
                if(data.size()>0) {
                    if (new File(folderPath).exists() == false) {
                        new File(folderPath).mkdirs();
                    }
                    String fileName = "iwing_"+name+"_TBDK_" + ngayhomqua+".txt";
                    String pathfile = folderPath + fileName;
                    BaseUtils.writeFile(fileName, data, false);
                    new File(fileName).renameTo(new File(pathfile));
                    baseDAO.execSql(transid,sqlupdate);
                }
            }
            catch (Exception ex){
                return ex.toString();
            }
            finally {
                baseDAO.releaseAll(rs,  stmt, conn);
            }
        }
        //CDR cộng tiền
        String where=" where a.report_time>='20210617' and a.status_exporttopup=0 and a.category='ikv3' and b.status=2 and a.created_time>='"+DateToStringByFormatConfig(beginctkm,"yyyyMMdd")+"' and a.created_time<='"+DateToStringByFormatConfig(endctkm,"yyyyMMdd")+"' + interval 1 day";
        String sqlselect="select a.msisdn,b.topup_fee,b.topup_time,b.mt from stats_kmtq a join kmtq b on a.kmtq_id=b.id "+where;
        String sqlupdate="update stats_kmtq a join kmtq b on a.kmtq_id=b.id set a.status_exporttopup=2 "+where;
        log.info("_"+transid+"_sql topup: "+sqlselect);
        ResultSet rs = null;
        PreparedStatement stmt = null;
        Connection conn = null;
        ArrayList<String> data=new ArrayList<String>();
        try {
            conn = BaseDAO.getInstance("main").getConnection();
            stmt = conn.prepareStatement(sqlselect);
            rs = stmt.executeQuery();
            while (rs.next()) {
                String msisdn=rs.getString("msisdn");
                Date topup_time=rs.getTimestamp("topup_time");
                int topup_fee=rs.getInt("topup_fee");
                String mt=rs.getString("mt");
                String neifResult=msisdn.substring(2)+";congtien;"+topup_fee+";"+DateToStringByFormatConfig(topup_time,"yyyyMMddHHmmss")+";"+DateToStringByFormatConfig(beginctkm,"yyyyMMdd")+";"+DateToStringByFormatConfig(endctkm,"yyyyMMdd")+";"+mt;
                data.add(neifResult);
            }
            if(data.size()>0) {
                if (new File(folderPath).exists() == false) {
                    new File(folderPath).mkdirs();
                }
                String fileName = "iwing_"+name+"_" + ngayhomqua+".txt";
                String pathfile = folderPath + fileName;
                BaseUtils.writeFile(fileName, data, false);
                new File(fileName).renameTo(new File(pathfile));
                baseDAO.execSql(transid,sqlupdate);
            }
        }
        catch (Exception ex){
            return ex.toString();
        }
        finally {
            baseDAO.releaseAll(rs,  stmt, conn);
        }
        return name;
    }

    private static String dangkyngayquatraotay(String transid, Date dayhomqua,String folderPath, String ngayhomqua, Logger log, BaseDAO baseDAO){
        //Đoán giá miễn phí – Nhận quà giá trị Từ 00h00 ngày 05/06/2020 đến hết 23h59:59 ngày 05/09/2020
        String name="dangkyngayquatraotay";
        //CDR thuê bao đăng ký
        Date beginctkm=null;
        Date endctkm=null;
        beginctkm=StringToDateByFormatConfig("20200605","yyyyMMdd");
        endctkm=StringToDateByFormatConfig("20200905","yyyyMMdd");
        if(beginctkm.getTime()<=dayhomqua.getTime()&&dayhomqua.getTime()<=endctkm.getTime()){
            String where=" where report_time>='20200605' and status_exportcdr=0 and category='ikv3' and created_time>='"+DateToStringByFormatConfig(beginctkm,"yyyyMMdd")+"' and created_time<='"+DateToStringByFormatConfig(endctkm,"yyyyMMdd")+"' + interval 1 day";
            String sqlselect="select msisdn from stats_kmtq "+where;
            String sqlupdate="update stats_kmtq set status_exportcdr=2 "+where;
            log.info("_"+transid+"_sql tbdk: "+sqlselect);
            ResultSet rs = null;
            PreparedStatement stmt = null;
            Connection conn = null;
            ArrayList<String> data=new ArrayList<String>();
            try {
                conn = BaseDAO.getInstance("main").getConnection();
                stmt = conn.prepareStatement(sqlselect);
                rs = stmt.executeQuery();
                while (rs.next()) {
                    String msisdn=rs.getString("msisdn");
                    String neifResult=msisdn.substring(2)+",ID";
                    data.add(neifResult);
                }
                if(data.size()>0) {
                    if (new File(folderPath).exists() == false) {
                        new File(folderPath).mkdirs();
                    }
                    String fileName = "iwing_"+name+"_TBDK_" + ngayhomqua+".txt";
                    String pathfile = folderPath + fileName;
                    BaseUtils.writeFile(fileName, data, false);
                    new File(fileName).renameTo(new File(pathfile));
                    baseDAO.execSql(transid,sqlupdate);
                }
            }
            catch (Exception ex){
                return ex.toString();
            }
            finally {
                baseDAO.releaseAll(rs,  stmt, conn);
            }
        }
        //CDR cộng tiền
        String where=" where a.report_time>='20200309' and a.status_exporttopup=0 and a.category='ikv3' and b.status=2 and a.created_time>='"+DateToStringByFormatConfig(beginctkm,"yyyyMMdd")+"' and a.created_time<='"+DateToStringByFormatConfig(endctkm,"yyyyMMdd")+"' + interval 1 day";
        String sqlselect="select a.msisdn,b.topup_fee,b.topup_time,b.mt from stats_kmtq a join kmtq b on a.kmtq_id=b.id "+where;
        String sqlupdate="update stats_kmtq a join kmtq b on a.kmtq_id=b.id set a.status_exporttopup=2 "+where;
        log.info("_"+transid+"_sql topup: "+sqlselect);
        ResultSet rs = null;
        PreparedStatement stmt = null;
        Connection conn = null;
        ArrayList<String> data=new ArrayList<String>();
        try {
            conn = BaseDAO.getInstance("main").getConnection();
            stmt = conn.prepareStatement(sqlselect);
            rs = stmt.executeQuery();
            while (rs.next()) {
                String msisdn=rs.getString("msisdn");
                Date topup_time=rs.getTimestamp("topup_time");
                int topup_fee=rs.getInt("topup_fee");
                String mt=rs.getString("mt");
                String neifResult=msisdn.substring(2)+";congtien;"+topup_fee+";"+DateToStringByFormatConfig(topup_time,"yyyyMMddHHmmss")+";"+DateToStringByFormatConfig(beginctkm,"yyyyMMdd")+";"+DateToStringByFormatConfig(endctkm,"yyyyMMdd")+";"+mt;
                data.add(neifResult);
            }
            if(data.size()>0) {
                if (new File(folderPath).exists() == false) {
                    new File(folderPath).mkdirs();
                }
                String fileName = "iwing_"+name+"_" + ngayhomqua+".txt";
                String pathfile = folderPath + fileName;
                BaseUtils.writeFile(fileName, data, false);
                new File(fileName).renameTo(new File(pathfile));
                baseDAO.execSql(transid,sqlupdate);
            }
        }
        catch (Exception ex){
            return ex.toString();
        }
        finally {
            baseDAO.releaseAll(rs,  stmt, conn);
        }
        return name;
    }

    private static String doangiamienphinhanquagiatri(String transid, Date dayhomqua,String folderPath, String ngayhomqua, Logger log, BaseDAO baseDAO){
        //Đoán giá miễn phí – Nhận quà giá trị Từ 00h00 ngày 07/06/2020 đến hết 23h59:59 ngày 04/09/2020
        String name="doangiamienphinhanquagiatri";
        //CDR thuê bao đăng ký
        Date beginctkm=null;
        Date endctkm=null;
        beginctkm=StringToDateByFormatConfig("20200607","yyyyMMdd");
        endctkm=StringToDateByFormatConfig("20200904","yyyyMMdd");
        if(beginctkm.getTime()<=dayhomqua.getTime()&&dayhomqua.getTime()<=endctkm.getTime()){
            String where=" where report_time>='20200309' and status_exportcdr=0 and category='5th10k' and created_time>='"+DateToStringByFormatConfig(beginctkm,"yyyyMMdd")+"' and created_time<='"+DateToStringByFormatConfig(endctkm,"yyyyMMdd")+"' + interval 1 day";
            String sqlselect="select msisdn from stats_kmtq "+where;
            String sqlupdate="update stats_kmtq set status_exportcdr=2 "+where;
            log.info("_"+transid+"_sql tbdk: "+sqlselect);
            ResultSet rs = null;
            PreparedStatement stmt = null;
            Connection conn = null;
            ArrayList<String> data=new ArrayList<String>();
            try {
                conn = BaseDAO.getInstance("main").getConnection();
                stmt = conn.prepareStatement(sqlselect);
                rs = stmt.executeQuery();
                while (rs.next()) {
                    String msisdn=rs.getString("msisdn");
                    String neifResult=msisdn.substring(2)+",DG";
                    data.add(neifResult);
                }
                if(data.size()>0) {
                    if (new File(folderPath).exists() == false) {
                        new File(folderPath).mkdirs();
                    }
                    String fileName = "haychongiadung_"+name+"_TBDK_" + ngayhomqua+".txt";
                    String pathfile = folderPath + fileName;
                    BaseUtils.writeFile(fileName, data, false);
                    new File(fileName).renameTo(new File(pathfile));
                    baseDAO.execSql(transid,sqlupdate);
                }
            }
            catch (Exception ex){
                return ex.toString();
            }
            finally {
                baseDAO.releaseAll(rs,  stmt, conn);
            }
        }
        //CDR cộng tiền
        String where=" where a.report_time>='20200309' and a.status_exporttopup=0 and a.category='5th10k' and b.status=2 and a.created_time>='"+DateToStringByFormatConfig(beginctkm,"yyyyMMdd")+"' and a.created_time<='"+DateToStringByFormatConfig(endctkm,"yyyyMMdd")+"' + interval 1 day";
        String sqlselect="select a.msisdn,b.topup_fee,b.topup_time,b.mt from stats_kmtq a join kmtq b on a.kmtq_id=b.id "+where;
        String sqlupdate="update stats_kmtq a join kmtq b on a.kmtq_id=b.id set a.status_exporttopup=2 "+where;
        log.info("_"+transid+"_sql topup: "+sqlselect);
        ResultSet rs = null;
        PreparedStatement stmt = null;
        Connection conn = null;
        ArrayList<String> data=new ArrayList<String>();
        try {
            conn = BaseDAO.getInstance("main").getConnection();
            stmt = conn.prepareStatement(sqlselect);
            rs = stmt.executeQuery();
            while (rs.next()) {
                String msisdn=rs.getString("msisdn");
                Date topup_time=rs.getTimestamp("topup_time");
                int topup_fee=rs.getInt("topup_fee");
                String mt=rs.getString("mt");
                String neifResult=msisdn.substring(2)+";congtien;"+topup_fee+";"+DateToStringByFormatConfig(topup_time,"yyyyMMddHHmmss")+";"+DateToStringByFormatConfig(beginctkm,"yyyyMMdd")+";"+DateToStringByFormatConfig(endctkm,"yyyyMMdd")+";"+mt;
                data.add(neifResult);
            }
            if(data.size()>0) {
                if (new File(folderPath).exists() == false) {
                    new File(folderPath).mkdirs();
                }
                String fileName = "haychongiadung_"+name+"_" + ngayhomqua+".txt";
                String pathfile = folderPath + fileName;
                BaseUtils.writeFile(fileName, data, false);
                new File(fileName).renameTo(new File(pathfile));
                baseDAO.execSql(transid,sqlupdate);
            }
        }
        catch (Exception ex){
            return ex.toString();
        }
        finally {
            baseDAO.releaseAll(rs,  stmt, conn);
        }
        return name;
    }



    private static String chinhphucdoangiakhamphapattaya(String transid, Date dayhomqua,String folderPath, String ngayhomqua, Logger log, BaseDAO baseDAO){
        //Chinh phục đoán giá – Khám phá PATTAYA 20200309-20200606
        String name="chinhphucdoangiakhamphapattaya";
        //CDR thuê bao đăng ký
        Date beginctkm=null;
        Date endctkm=null;
        beginctkm=StringToDateByFormatConfig("20200309","yyyyMMdd");
        endctkm=StringToDateByFormatConfig("20200606","yyyyMMdd");
        if(beginctkm.getTime()<=dayhomqua.getTime()&&dayhomqua.getTime()<=endctkm.getTime()){
            String where=" where report_time>='20200309' and status_exportcdr=0 and category='fourth10k' and created_time>='"+DateToStringByFormatConfig(beginctkm,"yyyyMMdd")+"' and created_time<='"+DateToStringByFormatConfig(endctkm,"yyyyMMdd")+"' + interval 1 day";
            String sqlselect="select msisdn from stats_kmtq "+where;
            String sqlupdate="update stats_kmtq set status_exportcdr=2 "+where;
            log.info("_"+transid+"_sql tbdk: "+sqlselect);
            ResultSet rs = null;
            PreparedStatement stmt = null;
            Connection conn = null;
            ArrayList<String> data=new ArrayList<String>();
            try {
                conn = BaseDAO.getInstance("main").getConnection();
                stmt = conn.prepareStatement(sqlselect);
                rs = stmt.executeQuery();
                while (rs.next()) {
                    String msisdn=rs.getString("msisdn");
                    String neifResult=msisdn.substring(2)+",ID";
                    data.add(neifResult);
                }
                if(data.size()>0) {
                    if (new File(folderPath).exists() == false) {
                        new File(folderPath).mkdirs();
                    }
                    String fileName = "haychongiadung_"+name+"_TBDK_" + ngayhomqua+".txt";
                    String pathfile = folderPath + fileName;
                    BaseUtils.writeFile(fileName, data, false);
                    new File(fileName).renameTo(new File(pathfile));
                    baseDAO.execSql(transid,sqlupdate);
                }
            }
            catch (Exception ex){
                return ex.toString();
            }
            finally {
                baseDAO.releaseAll(rs,  stmt, conn);
            }
        }
        //CDR cộng tiền
        String where=" where a.report_time>='20200309' and a.status_exporttopup=0 and a.category='fourth10k' and b.status=2 and a.created_time>='"+DateToStringByFormatConfig(beginctkm,"yyyyMMdd")+"' and a.created_time<='"+DateToStringByFormatConfig(endctkm,"yyyyMMdd")+"' + interval 1 day";
        String sqlselect="select a.msisdn,b.topup_fee,b.topup_time,b.mt from stats_kmtq a join kmtq b on a.kmtq_id=b.id "+where;
        String sqlupdate="update stats_kmtq a join kmtq b on a.kmtq_id=b.id set a.status_exporttopup=2 "+where;
        log.info("_"+transid+"_sql topup: "+sqlselect);
        ResultSet rs = null;
        PreparedStatement stmt = null;
        Connection conn = null;
        ArrayList<String> data=new ArrayList<String>();
        try {
            conn = BaseDAO.getInstance("main").getConnection();
            stmt = conn.prepareStatement(sqlselect);
            rs = stmt.executeQuery();
            while (rs.next()) {
                String msisdn=rs.getString("msisdn");
                Date topup_time=rs.getTimestamp("topup_time");
                int topup_fee=rs.getInt("topup_fee");
                String mt=rs.getString("mt");
                String neifResult=msisdn.substring(2)+";congtien;"+topup_fee+";"+DateToStringByFormatConfig(topup_time,"yyyyMMddHHmmss")+";"+DateToStringByFormatConfig(beginctkm,"yyyyMMdd")+";"+DateToStringByFormatConfig(endctkm,"yyyyMMdd")+";"+mt;
                data.add(neifResult);
            }
            if(data.size()>0) {
                if (new File(folderPath).exists() == false) {
                    new File(folderPath).mkdirs();
                }
                String fileName = "haychongiadung_"+name+"_" + ngayhomqua+".txt";
                String pathfile = folderPath + fileName;
                BaseUtils.writeFile(fileName, data, false);
                new File(fileName).renameTo(new File(pathfile));
                baseDAO.execSql(transid,sqlupdate);
            }
        }
        catch (Exception ex){
            return ex.toString();
        }
        finally {
            baseDAO.releaseAll(rs,  stmt, conn);
        }
        return name;
    }

    private static String doangiahetminhvivuhetbuon(String transid, Date dayhomqua,String folderPath, String ngayhomqua, Logger log, BaseDAO baseDAO){
        //Đoán giá hết mình – Vi vu hết buồn 20191210 đến 20200308
        String name="doangiahetminhvivuhetbuon";
        //CDR thuê bao đăng ký
        Date beginctkm=null;
        Date endctkm=null;
        beginctkm=StringToDateByFormatConfig("20191210","yyyyMMdd");
        endctkm=StringToDateByFormatConfig("20200308","yyyyMMdd");
        if(beginctkm.getTime()<=dayhomqua.getTime()&&dayhomqua.getTime()<=endctkm.getTime()){
            String where=" where report_time>='20191210' and status_exportcdr=0 and category='third10k' and created_time>='"+DateToStringByFormatConfig(beginctkm,"yyyyMMdd")+"' and created_time<='"+DateToStringByFormatConfig(endctkm,"yyyyMMdd")+"' + interval 1 day";
            String sqlselect="select msisdn from stats_kmtq "+where;
            String sqlupdate="update stats_kmtq set status_exportcdr=2 "+where;
            log.info("_"+transid+"_sql tbdk: "+sqlselect);
            ResultSet rs = null;
            PreparedStatement stmt = null;
            Connection conn = null;
            ArrayList<String> data=new ArrayList<String>();
            try {
                conn = BaseDAO.getInstance("main").getConnection();
                stmt = conn.prepareStatement(sqlselect);
                rs = stmt.executeQuery();
                while (rs.next()) {
                    String msisdn=rs.getString("msisdn");
                    String neifResult=msisdn.substring(2)+",DG";
                    data.add(neifResult);
                }
                if(data.size()>0) {
                    if (new File(folderPath).exists() == false) {
                        new File(folderPath).mkdirs();
                    }
                    String fileName = "haychongiadung_"+name+"_TBDK_" + ngayhomqua+".txt";
                    String pathfile = folderPath + fileName;
                    BaseUtils.writeFile(fileName, data, false);
                    new File(fileName).renameTo(new File(pathfile));
                    baseDAO.execSql(transid,sqlupdate);
                }
            }
            catch (Exception ex){
                return ex.toString();
            }
            finally {
                baseDAO.releaseAll(rs,  stmt, conn);
            }
        }
        //CDR cộng tiền
        String where=" where a.report_time>='20191210' and a.status_exporttopup=0 and a.category='third10k' and b.status=2 and a.created_time>='"+DateToStringByFormatConfig(beginctkm,"yyyyMMdd")+"' and a.created_time<='"+DateToStringByFormatConfig(endctkm,"yyyyMMdd")+"' + interval 1 day";
        String sqlselect="select a.msisdn,b.topup_fee,b.topup_time,b.mt from stats_kmtq a join kmtq b on a.kmtq_id=b.id "+where;
        String sqlupdate="update stats_kmtq a join kmtq b on a.kmtq_id=b.id set a.status_exporttopup=2 "+where;
        log.info("_"+transid+"_sql topup: "+sqlselect);
        ResultSet rs = null;
        PreparedStatement stmt = null;
        Connection conn = null;
        ArrayList<String> data=new ArrayList<String>();
        try {
            conn = BaseDAO.getInstance("main").getConnection();
            stmt = conn.prepareStatement(sqlselect);
            rs = stmt.executeQuery();
            while (rs.next()) {
                String msisdn=rs.getString("msisdn");
                Date topup_time=rs.getTimestamp("topup_time");
                int topup_fee=rs.getInt("topup_fee");
                String mt=rs.getString("mt");
                String neifResult=msisdn.substring(2)+";congtien;"+topup_fee+";"+DateToStringByFormatConfig(topup_time,"yyyyMMddHHmmss")+";"+DateToStringByFormatConfig(beginctkm,"yyyyMMdd")+";"+DateToStringByFormatConfig(endctkm,"yyyyMMdd")+";"+mt;
                data.add(neifResult);
            }
            if(data.size()>0) {
                if (new File(folderPath).exists() == false) {
                    new File(folderPath).mkdirs();
                }
                String fileName = "haychongiadung_"+name+"_" + ngayhomqua+".txt";
                String pathfile = folderPath + fileName;
                BaseUtils.writeFile(fileName, data, false);
                new File(fileName).renameTo(new File(pathfile));
                baseDAO.execSql(transid,sqlupdate);
            }
        }
        catch (Exception ex){
            return ex.toString();
        }
        finally {
            baseDAO.releaseAll(rs,  stmt, conn);
        }
        return name;
    }


    private static String doangiadungtrungquakhung(String transid, Date dayhomqua,String folderPath, String ngayhomqua, Logger log, BaseDAO baseDAO){
        //Đoán giá đúng – Trúng giải khủng 20190911 đến 20191209
        //CDR thuê bao đăng ký
        Date beginctkm=null;
        Date endctkm=null;
        beginctkm=StringToDateByFormatConfig("20190911","yyyyMMdd");
        endctkm=StringToDateByFormatConfig("20191209","yyyyMMdd");
        if(beginctkm.getTime()<=dayhomqua.getTime()&&dayhomqua.getTime()<=endctkm.getTime()){
            String where=" where report_time>='20190911' and status_exportcdr=0 and category='second5k' and created_time>='"+DateToStringByFormatConfig(beginctkm,"yyyyMMdd")+"' and created_time<='"+DateToStringByFormatConfig(endctkm,"yyyyMMdd")+"' + interval 1 day";
            String sqlselect="select msisdn from stats_kmtq "+where;
            String sqlupdate="update stats_kmtq set status_exportcdr=2 "+where;
            log.info("_"+transid+"_sql tbdk: "+sqlselect);
            ResultSet rs = null;
            PreparedStatement stmt = null;
            Connection conn = null;
            ArrayList<String> data=new ArrayList<String>();
            try {
                conn = BaseDAO.getInstance("main").getConnection();
                stmt = conn.prepareStatement(sqlselect);
                rs = stmt.executeQuery();
                while (rs.next()) {
                    String msisdn=rs.getString("msisdn");
                    String neifResult=msisdn.substring(2)+",DG";
                    data.add(neifResult);
                }
                if(data.size()>0) {
                    if (new File(folderPath).exists() == false) {
                        new File(folderPath).mkdirs();
                    }
                    String fileName = "haychongiadung_doangiadungtrungquakhung_TBDK_" + ngayhomqua+".txt";
                    String pathfile = folderPath + fileName;
                    BaseUtils.writeFile(fileName, data, false);
                    new File(fileName).renameTo(new File(pathfile));
                    baseDAO.execSql(transid,sqlupdate);
                }
            }
            catch (Exception ex){
                return ex.toString();
            }
            finally {
                baseDAO.releaseAll(rs,  stmt, conn);
            }
        }
        //CDR cộng tiền
        String where=" where a.report_time>='20190911' and a.status_exporttopup=0 and a.category='second5k' and b.status=2 and a.created_time>='"+DateToStringByFormatConfig(beginctkm,"yyyyMMdd")+"' and a.created_time<='"+DateToStringByFormatConfig(endctkm,"yyyyMMdd")+"' + interval 1 day";
        String sqlselect="select a.msisdn,b.topup_fee,b.topup_time,b.mt from stats_kmtq a join kmtq b on a.kmtq_id=b.id "+where;
        String sqlupdate="update stats_kmtq a join kmtq b on a.kmtq_id=b.id set a.status_exporttopup=2 "+where;
        log.info("_"+transid+"_sql topup: "+sqlselect);
        ResultSet rs = null;
        PreparedStatement stmt = null;
        Connection conn = null;
        ArrayList<String> data=new ArrayList<String>();
        try {
            conn = BaseDAO.getInstance("main").getConnection();
            stmt = conn.prepareStatement(sqlselect);
            rs = stmt.executeQuery();
            while (rs.next()) {
                String msisdn=rs.getString("msisdn");
                Date topup_time=rs.getTimestamp("topup_time");
                int topup_fee=rs.getInt("topup_fee");
                String mt=rs.getString("mt");
                String neifResult=msisdn.substring(2)+";congtien;"+topup_fee+";"+DateToStringByFormatConfig(topup_time,"yyyyMMddHHmmss")+";"+DateToStringByFormatConfig(beginctkm,"yyyyMMdd")+";"+DateToStringByFormatConfig(endctkm,"yyyyMMdd")+";"+mt;
                data.add(neifResult);
            }
            if(data.size()>0) {
                if (new File(folderPath).exists() == false) {
                    new File(folderPath).mkdirs();
                }
                String fileName = "haychongiadung_doangiadungtrungquakhung_" + ngayhomqua+".txt";
                String pathfile = folderPath + fileName;
                BaseUtils.writeFile(fileName, data, false);
                new File(fileName).renameTo(new File(pathfile));
                baseDAO.execSql(transid,sqlupdate);
            }
        }
        catch (Exception ex){
            return ex.toString();
        }
        finally {
            baseDAO.releaseAll(rs,  stmt, conn);
        }
        return "doangiadungtrungquakhung";
    }


    private static String doangiangayqualientay(String transid, Date dayhomqua,String folderPath, String ngayhomqua, Logger log, BaseDAO baseDAO){
        //Đoán giá ngay – Quà liền tay - Cộng 5k vào TKC - 2019-06-10 đến 2019-09-07
        //CDR thuê bao đăng ký
        Date beginctkm=null;
        Date endctkm=null;
        beginctkm=StringToDateByFormatConfig("20190610","yyyyMMdd");
        endctkm=StringToDateByFormatConfig("20190907","yyyyMMdd");
        if(beginctkm.getTime()<=dayhomqua.getTime()&&dayhomqua.getTime()<=endctkm.getTime()){
            String where=" where report_time>='20190501' and status_exportcdr=0 and category='first5k' and created_time>='"+DateToStringByFormatConfig(beginctkm,"yyyyMMdd")+"' and created_time<='"+DateToStringByFormatConfig(endctkm,"yyyyMMdd")+"' + interval 1 day";
            String sqlselect="select msisdn from stats_kmtq "+where;
            String sqlupdate="update stats_kmtq set status_exportcdr=2 "+where;
            log.info("_"+transid+"_sql tbdk: "+sqlselect);
            ResultSet rs = null;
            PreparedStatement stmt = null;
            Connection conn = null;
            ArrayList<String> data=new ArrayList<String>();
            try {
                conn = BaseDAO.getInstance("main").getConnection();
                stmt = conn.prepareStatement(sqlselect);
                rs = stmt.executeQuery();
                while (rs.next()) {
                    String msisdn=rs.getString("msisdn");
                    String neifResult=msisdn.substring(2)+",DG";
                    data.add(neifResult);
                }
                if(data.size()>0) {
                    if (new File(folderPath).exists() == false) {
                        new File(folderPath).mkdirs();
                    }
                    String fileName = "haychongiadung_doangiangayqualientay_TBDK_" + ngayhomqua+".txt";
                    String pathfile = folderPath + fileName;
                    BaseUtils.writeFile(fileName, data, false);
                    new File(fileName).renameTo(new File(pathfile));
                    baseDAO.execSql(transid,sqlupdate);
                }
            }
            catch (Exception ex){
                return ex.toString();
            }
            finally {
                baseDAO.releaseAll(rs,  stmt, conn);
            }
        }
        //CDR cộng tiền
        String where=" where a.report_time>='20190615' and a.status_exporttopup=0 and a.category='first5k' and b.status=2 and a.created_time>='"+DateToStringByFormatConfig(beginctkm,"yyyyMMdd")+"' and a.created_time<='"+DateToStringByFormatConfig(endctkm,"yyyyMMdd")+"' + interval 1 day";
        String sqlselect="select a.msisdn,b.topup_fee,b.topup_time,b.mt from stats_kmtq a join kmtq b on a.kmtq_id=b.id "+where;
        String sqlupdate="update stats_kmtq a join kmtq b on a.kmtq_id=b.id set a.status_exporttopup=2 "+where;
        log.info("_"+transid+"_sql topup: "+sqlselect);
        ResultSet rs = null;
        PreparedStatement stmt = null;
        Connection conn = null;
        ArrayList<String> data=new ArrayList<String>();
        try {
            conn = BaseDAO.getInstance("main").getConnection();
            stmt = conn.prepareStatement(sqlselect);
            rs = stmt.executeQuery();
            while (rs.next()) {
                String msisdn=rs.getString("msisdn");
                Date topup_time=rs.getTimestamp("topup_time");
                int topup_fee=rs.getInt("topup_fee");
                String mt=rs.getString("mt");
                String neifResult=msisdn.substring(2)+";congtien;"+topup_fee+";"+DateToStringByFormatConfig(topup_time,"yyyyMMddHHmmss")+";"+DateToStringByFormatConfig(beginctkm,"yyyyMMdd")+";"+DateToStringByFormatConfig(endctkm,"yyyyMMdd")+";"+mt;
                data.add(neifResult);
            }
            if(data.size()>0) {
                if (new File(folderPath).exists() == false) {
                    new File(folderPath).mkdirs();
                }
                String fileName = "haychongiadung_doangiangayqualientay_" + ngayhomqua+".txt";
                String pathfile = folderPath + fileName;
                BaseUtils.writeFile(fileName, data, false);
                new File(fileName).renameTo(new File(pathfile));
                baseDAO.execSql(transid,sqlupdate);
            }
        }
        catch (Exception ex){
            return ex.toString();
        }
        finally {
            baseDAO.releaseAll(rs,  stmt, conn);
        }
        return "doangiangayqualientay";
    }

    public static String DateToStringByFormatConfig(Date date, String format){

        try {
            DateFormat df = new SimpleDateFormat(format);
            String reportDate = df.format(date);
            return reportDate;
        }
        catch (Exception ex){
            return null;
        }
    }
    public static Date StringToDateByFormatConfig(String dateString,String format){
        try {
            SimpleDateFormat formatter = new SimpleDateFormat(format);
            Date date = formatter.parse(dateString);
            return date;
        }
        catch (Exception ex){
            return null;
        }
    }
%>