/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.elcom.vinamailreport.common.database;

import com.elcom.mylogger.LogInterface;
import com.elcom.vinamailreport.common.CommonConstants;
import com.elcom.vinamailreport.common.interfaces.DBInterface;
import com.elcom.vinamailreport.common.messages.Report;
import com.elcom.vinamailreport.common.messages.ReportEntity;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import oracle.jdbc.OracleTypes;
import org.jconfig.Configuration;
import org.jconfig.ConfigurationManager;

/**
 *
 * @author Administrator
 */
public class DatabaseActions implements DBInterface {

    private LogInterface logger;
    private DBAccess db;
    private String tblDailyReport = "TBL_EMAIL_REPORT";
    private Connection connection = null;

    public DatabaseActions(LogInterface logger) {
        this.logger = logger;
        db = DBAccess.getInstance(logger);
        //connection = getConnection();
    }

    public List<HashMap<String, String>> getDataFromQuery(String query) {
        List<HashMap<String, String>> allData = new ArrayList<HashMap<String, String>>();
        Statement st = null;
        Connection con = null;
        try {
            con = getConnection();
            logger.debug("query string: " + query);
            if (con == null) {
                logger.fantal("Connection to DB died for  !!!");
                return null;
            }

            st = con.createStatement();
            ResultSet rsData = st.executeQuery(query);

            ResultSetMetaData rsmd = rsData.getMetaData();

            int column_count = rsmd.getColumnCount();

            List<String> lstFeild = new ArrayList<String>();

            if (column_count > 0) {
                for (int i = 0; i < column_count; i++) {
                    String name = rsmd.getColumnName(i + 1);

                    lstFeild.add(name.toUpperCase());
                }

            }

            while (rsData.next()) {
                HashMap<String, String> row = new HashMap<String, String>();

                for (String col : lstFeild) {
//                    logger.infor("Column: " + col + " data: " + rsData.getString(col));
                    row.put(col, rsData.getString(col));
                }

                allData.add(row);
            }

        } catch (Exception ex) {
            logger.error(ex.getMessage());
            logger.error(ex);
        } finally {
            try {
                if (st != null) {
                    st.close();
                }
                if (con != null && !con.isClosed()) {
                    con.close();
                }
            } catch (SQLException ex) {
                logger.error(ex);
            }
        }

        return allData;
    }

    public List<HashMap<String, String>> getDataFromStore(String query) {
        List<HashMap<String, String>> allData = new ArrayList<HashMap<String, String>>();
        CallableStatement st = null;
        Connection con = null;
        try {
            con = getConnection();
            if (con == null) {
                logger.fantal("Connection to DB died for  !!!");
                return null;
            }

            st = con.prepareCall(query); //

            st.registerOutParameter(1, OracleTypes.CURSOR);

            //===========================================================
            st.execute();

            ResultSet rsData = (ResultSet) st.getObject(1);

            ResultSetMetaData rsmd = rsData.getMetaData();

            int column_count = rsmd.getColumnCount();

            List<String> lstFeild = new ArrayList<String>();

            if (column_count > 0) {
                for (int i = 0; i < column_count; i++) {
                    String name = rsmd.getColumnName(i + 1);

                    lstFeild.add(name.toUpperCase());
                }

            }

            while (rsData.next()) {
                HashMap<String, String> row = new HashMap<String, String>();

                for (String col : lstFeild) {
                    row.put(col, rsData.getString(col));
                }

                allData.add(row);
            }

        } catch (Exception ex) {
            logger.error(ex.getMessage());
            logger.error(ex);
        } finally {
            try {
                if (st != null) {
                    st.close();
                }
                if (con != null && !con.isClosed()) {
                    con.close();
                }
            } catch (SQLException ex) {
                logger.error(ex);
            }
        }

        return allData;
    }

    public List<HashMap<String, String>> getDataFromStoreMySQL(String query) {
        List<HashMap<String, String>> allData = new ArrayList<HashMap<String, String>>();
        CallableStatement st = null;
        Connection con = null;
        try {
            con = getConnection();
            if (con == null) {
                logger.fantal("Connection to DB died for  !!!");
                return null;
            }

            st = con.prepareCall(query); //

            //===========================================================
            st.execute();

            ResultSet rsData = st.getResultSet();

            ResultSetMetaData rsmd = rsData.getMetaData();

            int column_count = rsmd.getColumnCount();

            List<String> lstFeild = new ArrayList<String>();

            if (column_count > 0) {
                for (int i = 0; i < column_count; i++) {
                    String name = rsmd.getColumnName(i + 1);

                    lstFeild.add(name.toUpperCase());
                }

            }

            while (rsData.next()) {
                HashMap<String, String> row = new HashMap<String, String>();

                for (String col : lstFeild) {
                    row.put(col, rsData.getString(col));
                }

                allData.add(row);
            }

        } catch (Exception ex) {
            logger.error(ex.getMessage());
            logger.error(ex);
        } finally {
            try {
                if (st != null) {
                    st.close();
                }
                if (con != null && !con.isClosed()) {
                    con.close();
                }
            } catch (SQLException ex) {
                logger.error(ex);
            }
        }

        return allData;
    }
    
    public boolean runSummary(Date sumary_date) {
       
        CallableStatement st = null;
        Connection con = null;
        try {
            con = getConnection();
            if (con == null) {
                logger.fantal("Connection to DB died for  !!!");
                return false;
            }

            st = con.prepareCall("{call DAILY_SUMMARY_REPORT(?)}"); //
            //java.util.Date utilDate = new java.util.Date();
            java.sql.Date sqlDate = new java.sql.Date(sumary_date.getTime());
            st.setDate(1, sqlDate);
            //===========================================================
            st.execute();

        } catch (Exception ex) {
            logger.error(ex.getMessage());
            logger.error(ex);
        } finally {
            try {
                if (st != null) {
                    st.close();
                }
                if (con != null && !con.isClosed()) {
                    con.close();
                }
            } catch (SQLException ex) {
                logger.error(ex);
            }
        }

        return true;
    }

    public Report getInput() {
        Report result = new Report();
        List<ReportEntity> list = new ArrayList<ReportEntity>();
        Connection con = getConnection();
        if (con == null) {
            logger.fantal("Connection to DB died for  !!!");
            return null;
        }
        PreparedStatement ps = null;
        String tblName = this.tblDailyReport;//getTblName(this.tblDailyReport);
        Configuration config = ConfigurationManager.getConfiguration(CommonConstants.SYS_CONFIG_NAME);
        int date_before1 = Integer.parseInt(config.getProperty("DATE_1_BEFORE_CUR", "", "MAIL"));
        int date_before2 = Integer.parseInt(config.getProperty("DATE_2_BEFORE_CUR", "", "MAIL"));

        if (date_before2 < date_before1) {
            int temp = date_before2;
            date_before2 = date_before1;
            date_before1 = temp;
        }
        try {
            String sqlCheck = "select count(*) from " + tblName + " where to_char(REPORT_DATE, 'dd/mm/yyyy') = to_char(SYSDATE-" + date_before1 + ", 'dd/mm/yyyy')";
            logger.infor("CHECK DATA: " + sqlCheck);
            Statement st = con.createStatement();
            ResultSet rsCheck = st.executeQuery(sqlCheck);
            boolean needSend = false;
            while (rsCheck.next()) {
                int isUptoDate = rsCheck.getInt(1);

                if (isUptoDate >= 1) {
                    needSend = true;
                    break;
                }
            }

            logger.infor("DATE UP TO DATE: " + needSend);

            if (needSend) {
                String sql = "SELECT " + tblName + ".*, to_char(REPORT_DATE,'dd/MM/yyyy') as REPORT_DATE_S  FROM " + tblName + " where REPORT_DATE <= SYSDATE - " + date_before1 + " AND REPORT_DATE >= SYSDATE-" + (date_before1 + date_before2) + " order by REPORT_DATE ASC";

                logger.infor(sql);

                ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    ReportEntity entity = new ReportEntity();
                    entity.setTime(rs.getString("REPORT_DATE_S"));
                    entity.setTotal_active(rs.getInt("TOTAL_ACTIVE"));
                    entity.setTotal_recharge(rs.getInt("TOTAL_RECHARGE"));
                    entity.setTotal_recharge_PSC(rs.getInt("TOTAL_RECHARGE_PSC"));
                    entity.setTotal_recharge_PSC_percent(rs.getDouble("TOTAL_RECHARGE_PSC_PERCENT"));
                    entity.setTotal_register(rs.getInt("TOTAL_REGISTER"));
                    entity.setTotal_register_PSC(rs.getInt("TOTAL_REGISTER_PSC"));
                    entity.setTotal_register_PSC_percent(rs.getDouble("TOTAL_REGISTER_PSC_PERCENT"));
                    entity.setTotal_revenue(rs.getDouble("TOTAL_REVENUE"));


                    entity.setTotal_unregister_by_sub(rs.getInt("TOTAL_UNREGISTER_BY_SUB"));
                    entity.setTotal_unregister_by_sys(rs.getInt("TOTAL_UNREGISTER_BY_SYS"));
                    entity.setTotal_to_date(rs.getDouble("cumulative_mon"));
                    entity.setTotal_of_last_month(rs.getDouble("cumulative_last_mon"));
                    logger.infor(entity.toString());

                    list.add(entity);
                }
                result.setList(list);
            }
//            sql = "";
//            ps = con.prepareStatement(sql);
//            rs = ps.executeQuery();
//            int active = 0;
//            int deactive = 0;
//            int totalActive = 0;
//            int totalDeactive = 0;
//            String time = "";
//            while (rs.next()) {
//                active = rs.getInt("");
//                deactive = rs.getInt("");
//                totalActive = rs.getInt("");
//                totalDeactive = rs.getInt("");
//                time = rs.getString("");
//            }
//            result.setActive(active);
//            result.setDeactive(deactive);
//            result.setTime(time);
//            result.setTotalActive(totalActive);
//            result.setTotalDeactive(totalDeactive);
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            logger.error(ex);
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
                if (con != null && !con.isClosed()) {
                    con.close();
                }
            } catch (SQLException ex) {
                logger.error(ex);
            }
        }
        return result;
    }

    public String getAddress() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    private synchronized Connection getConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                return connection;
            }
            int retry = 0;
            connection = null;
            while ((connection == null || connection.isClosed()) && retry < 3) {
                connection = db.getConnection();
                
                if (connection != null) {
                    logger.debug("Get new connection done!");
                    break;
                }
                retry++;
                logger.infor("=============System start retry connection: " + retry + "============");
                try {
                    Thread.sleep(5000); // Tam nghi 5s
                } catch (InterruptedException e) {
                    logger.error("InterruptedException when waiting connection");
                }
            }
        } catch (Exception ex) {
            logger.error(ex);
        }
        return connection;
    }

    private String getTblName(String tblName) {
        String result = tblName;
        Configuration config = ConfigurationManager.getConfiguration(CommonConstants.SYS_CONFIG_NAME);
        String schemaName = config.getProperty("ORA_SCHEMA", "", "JDBC");
        result = (schemaName.equals("")) ? result : schemaName + "." + result;
        return result;
    }
}
