<%@ page import="com.google.gson.Gson" %>
<%@ page import="com.ligerdev.appbase.utils.BaseUtils" %>
<%@ page import="com.ligerdev.appbase.utils.db.BaseDAO" %>
<%@ page import="com.ligerdev.appbase.utils.textbase.StringGenerator" %>
<%@ page import="org.apache.log4j.Logger" %>
<%@ page import="java.io.BufferedReader" %>
<%@ page import="java.sql.Connection" %>
<%@ page import="java.sql.PreparedStatement" %>
<%@ page import="java.sql.SQLException" %>
<%@ page import="java.sql.Timestamp" %>
<%@ page import="java.util.ArrayList" %>
<%@ page trimDirectiveWhitespaces="true" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    /*
    /opt/kns/bin/api/root/ctkm/kmtqservice/update_topup.jsp
    */
    String rs = "400|Bad Request";
    BufferedReader reader = request.getReader();
    Gson gson = new Gson();
    KMTQData kmtqData = (KMTQData) gson.fromJson(reader, KMTQData.class);

    if (kmtqData != null) {
        ArrayList<Kmtq> datas = kmtqData.getDatas();
        if (datas != null) {
            for (Kmtq kmtq : datas) {
                if (kmtq != null) {
                    if (BaseUtils.isNotBlank(kmtq.getMsisdn()) && BaseUtils.isNotBlank(kmtq.getMt())
                            && (kmtq.getId() > 0) && (kmtq.getStatus() > 0) && (kmtq.getTopupTime() > 0)) {
                        String transid = "KMTQ-" + kmtq.getMsisdn() + "-" + StringGenerator.randomCharacters(6);
                        rs = updateListTopup(transid, kmtq);
                    }
                }
            }
        }
    }
%>
<%!
    Logger logger = Logger.getLogger("updateTopupJSP");

    public String updateListTopup(String transid, Kmtq kmtq) {
        String rs = "-1|Error" + transid;
        Connection conn = null;
        PreparedStatement stm = null;
        BaseDAO baseDAO = BaseDAO.getInstance("main");
        try {

            if(kmtq.getMt().length()<=20) {
                String sqlUpdateTopup = "UPDATE `kmtq` SET `status` = ?, `topup_time` = ?, `mt` = ?, `type` = ?, `error_code` = ?, `tracenumber` = ?, `transid_mobi` = ?, `error_description` = ? WHERE `id` = ?";
                conn = baseDAO.getConnection();
                stm = conn.prepareStatement(sqlUpdateTopup);
                stm.setInt(1, kmtq.getStatus());
                stm.setTimestamp(2, new Timestamp(kmtq.getTopupTime()));
                stm.setString(3, kmtq.getMt());
                stm.setInt(4, kmtq.getType());
                stm.setInt(5, kmtq.getError_code());
                stm.setString(6, kmtq.getTracenumber());
                stm.setString(7, kmtq.getTransid_mobi());
                stm.setString(8, kmtq.getError_description());
                stm.setInt(9, kmtq.getId());
                rs = stm.executeUpdate() > 0 ? "0|Success" : "-1|Error1" + "|" + transid;
                kmtq.setUpdate_result(rs);
            }
            else{
                String sqlUpdateTopup = "UPDATE `kmtq` SET `status` = ?, `topup_time` = ?, `type` = ?, `error_code` = ?, `tracenumber` = ?, `transid_mobi` = ?, `error_description` = ? WHERE `id` = ?";
                conn = baseDAO.getConnection();
                stm = conn.prepareStatement(sqlUpdateTopup);
                stm.setInt(1, kmtq.getStatus());
                stm.setTimestamp(2, new Timestamp(kmtq.getTopupTime()));
                stm.setInt(3, kmtq.getType());
                stm.setInt(4, kmtq.getError_code());
                stm.setString(5, kmtq.getTracenumber());
                stm.setString(6, kmtq.getTransid_mobi());
                stm.setString(7, kmtq.getError_description());
                stm.setInt(8, kmtq.getId());
                rs = stm.executeUpdate() > 0 ? "0|Success" : "-1|Error1" + "|" + transid;
                kmtq.setUpdate_result(rs);
            }
        } catch (SQLException e) {
            rs = "Exception: " + transid + "| " + e.getMessage();
            logger.debug("[updateListTopup]: " + transid + "| " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            rs = "Exception: " + transid + "| " + e.getMessage();
            logger.debug("[updateListTopup]: " + transid + "| " + e.getMessage());
            e.printStackTrace();
        } finally {
            baseDAO.releaseAll(rs, stm, conn);
        }
        return rs;
    }

    public static class KMTQData {
        private ArrayList<Kmtq> datas;

        public KMTQData() {
        }

        public ArrayList<Kmtq> getDatas() {
            return datas;
        }

        public void setDatas(ArrayList<Kmtq> datas) {
            this.datas = datas;
        }
    }

    public static class Kmtq {

        private int id;
        private String msisdn;
        private int status;
        private int topupFee;
        private long topupTime;
        private String mt;
        private int type;
        private int error_code;
        private String tracenumber;
        private String update_result;
        private String transid_mobi;
        private String error_description;

        public Kmtq() {
        }

        public Kmtq(int id, String msisdn, int status, int topupFee, long topupTime, String mt, int type, int error_code, String tracenumber, String update_result, String transid_mobi) {
            this.id = id;
            this.msisdn = msisdn;
            this.status = status;
            this.topupFee = topupFee;
            this.topupTime = topupTime;
            this.mt = mt;
            this.type = type;
            this.error_code = error_code;
            this.tracenumber = tracenumber;
            this.update_result = update_result;
            this.transid_mobi = transid_mobi;
        }

        public String getError_description() {
            return error_description;
        }

        public void setError_description(String error_description) {
            this.error_description = error_description;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getId() {
            return this.id;
        }

        public void setMsisdn(String msisdn) {
            this.msisdn = msisdn;
        }

        public String getMsisdn() {
            return this.msisdn;
        }


        public void setStatus(int status) {
            this.status = status;
        }

        public int getStatus() {
            return this.status;
        }

        public void setTopupFee(int topupFee) {
            this.topupFee = topupFee;
        }

        public int getTopupFee() {
            return this.topupFee;
        }

        public void setTopupTime(long topupTime) {
            this.topupTime = topupTime;
        }

        public long getTopupTime() {
            return this.topupTime;
        }

        public void setMt(String mt) {
            this.mt = mt;
        }

        public String getMt() {
            return this.mt;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public int getError_code() {
            return error_code;
        }

        public String getTracenumber() {
            return tracenumber;
        }

        public void setTracenumber(String tracenumber) {
            this.tracenumber = tracenumber;
        }

        public void setError_code(int error_code) {
            this.error_code = error_code;
        }

        public String getTransid_mobi() {
            return transid_mobi;
        }

        public void setTransid_mobi(String transid_mobi) {
            this.transid_mobi = transid_mobi;
        }

        public String getUpdate_result() {
            return update_result;
        }

        public void setUpdate_result(String update_result) {
            this.update_result = update_result;
        }

        public String toString() {
            return "["
                    + "id=" + id
                    + ", msisdn=" + msisdn
                    + ", status=" + status
                    + ", topupFee=" + topupFee
                    + ", topupTime=" + topupTime
                    + ", mt=" + mt
                    + ", tracenumber=" + tracenumber
                    + "]";
        }

    }
%>

<%=rs%>