<%@ page import="com.google.gson.Gson" %>
<%@ page import="com.ligerdev.appbase.utils.BaseUtils" %>
<%@ page import="com.ligerdev.appbase.utils.db.BaseDAO" %>
<%@ page import="java.sql.Connection" %>
<%@ page import="java.sql.PreparedStatement" %>
<%@ page import="java.sql.ResultSet" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.Map" %>
<%@ page trimDirectiveWhitespaces="true" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    //    response.setHeader("Content-Type", "application/json;charset=utf-8");
    /*
    /opt/kns/bin/api/root/ctkm/kmtqservice/get_list_topup.jsp
    http://icall.vn/ctkm/kmtqservice/get_list_topup.jsp?pair=xtl
    http://icall.vn/ctkm/kmtqservice/get_list_topup.jsp?pair=timeout&state=99&partnerName=topupxmedia2
    */

    String pair = request.getParameter("pair");
    String state = request.getParameter("state");
    String partnerName=request.getParameter("partnerName");
    String rs = "-1";
    Map<String, ArrayList<ObjToup>> mapToup = new HashMap<String, ArrayList<ObjToup>>();
    if (BaseUtils.isNotBlank(pair) && pair.equalsIgnoreCase("xtl")) {
        ArrayList<ObjToup> objToups = getData(1,state,partnerName);
        mapToup.put("datas", objToups);
        Gson gson = new Gson();
        rs = gson.toJson(mapToup);
    }
    else if(BaseUtils.isNotBlank(pair) && pair.equalsIgnoreCase("timeout"))
    {
        ArrayList<ObjToup> objToups = getData(2,state,partnerName);
        mapToup.put("datas", objToups);
        Gson gson = new Gson();
        rs = gson.toJson(mapToup);
    }

%>

<%!
    public ArrayList<ObjToup> getData(int type, String state, String partnerName) throws Exception {
        ArrayList<ObjToup> listTopup = new ArrayList<ObjToup>();
        Connection conn = null;
        PreparedStatement stm = null;
        ResultSet rs = null;
        try {
            String sqlRetListTopup = "";
            if(type==1) {
                sqlRetListTopup = "SELECT * FROM kmtq WHERE status = 0 AND date(make_time) = date(now()) order by id desc";
                sqlRetListTopup = "SELECT * FROM kmtq WHERE status = 0 AND date(make_time) >= date(now()-interval 1 day) order by id desc limit 1000";
            }
            else if(type==2)
                sqlRetListTopup = "SELECT * FROM kmtq WHERE make_time>'20190610' and tracenumber like '"+partnerName+"_%' and status = 3 AND error_code in("+state+")";
            else if(type==5)
                sqlRetListTopup = "SELECT * FROM kmtq_mostcalltime WHERE status = 0 AND date(make_time) = date(now()) order by id desc";
            conn = BaseDAO.getInstance("main").getConnection();
            stm = conn.prepareStatement(sqlRetListTopup);
            rs = stm.executeQuery();
            while (rs.next()) {

                ObjToup objToup = new ObjToup();
                objToup.setId(rs.getInt("id"));
                objToup.setMsisdn(rs.getString("msisdn"));
                objToup.setTracenumber(rs.getString("tracenumber"));
                objToup.setTopup_fee(rs.getInt("topup_fee"));
                objToup.setMt(rs.getString("mt"));
                objToup.setPartner(rs.getInt("partner"));
                objToup.setDatasize(rs.getString("datasize"));
                listTopup.add(objToup);
            }
            String theId = "";
            for (int i = 0; i < listTopup.size(); i++) {
                int idf = listTopup.get(i).getId();
                if (i < listTopup.size() - 1) {
                    theId += idf + ",";
                    continue;
                }
                theId += idf;
            }
            if (theId.length() > 0) {

                String sqlUpdate = "UPDATE kmtq SET `status` = [STATUS], `request_time` = now() WHERE id IN ([THEID])".replace("[THEID]", theId).replace("[STATUS]",type==1?"1":"11");
                if(type==2)
                    sqlUpdate = "UPDATE kmtq SET `status` = [STATUS] WHERE id IN ([THEID])".replace("[THEID]", theId).replace("[STATUS]","11");
                if(type==5)
                    sqlUpdate = "UPDATE kmtq_mostcalltime SET `status` = [STATUS], `request_time` = now() WHERE id IN ([THEID])".replace("[THEID]", theId).replace("[STATUS]",type==5?"1":"11");
                BaseDAO.getInstance("main").execSql("updateStatusKMTQ", sqlUpdate);
            }
        } finally {
            BaseDAO.getInstance("main").releaseAll(rs, stm, conn);
        }
        return listTopup;

    }

    class ObjToup {
        private int id;
        private String msisdn;
        private String tracenumber;
        private int topup_fee;
        private String mt;
        private int partner;
        private String datasize;

        public String getDatasize() {
            return datasize;
        }

        public void setDatasize(String datasize) {
            this.datasize = datasize;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getMsisdn() {
            return msisdn;
        }

        public void setMsisdn(String msisdn) {
            this.msisdn = msisdn;
        }

        public String getTracenumber() {
            return tracenumber;
        }

        public void setTracenumber(String tracenumber) {
            this.tracenumber = tracenumber;
        }

        public int getTopup_fee() {
            return topup_fee;
        }

        public void setTopup_fee(int topup_fee) {
            this.topup_fee = topup_fee;
        }

        public String getMt() {
            return mt;
        }

        public void setMt(String mt) {
            this.mt = mt;
        }

        public int getPartner() {
            return partner;
        }

        public void setPartner(int partner) {
            this.partner = partner;
        }
    }
%>
<%=rs%>