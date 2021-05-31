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
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.concurrent.LinkedBlockingQueue;
import org.jconfig.Configuration;
import org.jconfig.ConfigurationManager;

/**
 *
 * @author Administrator
 */
public class DatabaseActions_simu implements DBInterface{

    private LogInterface logger;

    public DatabaseActions_simu(LogInterface logger) {
        this.logger = logger;
       
    }

    public Report getInput() {
        Report result = new Report();
        List<ReportEntity> list = new ArrayList<ReportEntity>();
      
        String stable = getTblName("TBL_DAILY_REPORT");
        Random ran = new Random();
        try {
            
            for(int i=0; i< 10; i++)
            {
                ReportEntity entity = new ReportEntity();
                entity.setTime("0" + i + "/05/2013");
                entity.setTotal_active(ran.nextInt(1000000));
                entity.setTotal_recharge(ran.nextInt(1000000));
                entity.setTotal_recharge_PSC(ran.nextInt(1000000));
                entity.setTotal_recharge_PSC_percent(ran.nextInt(1000000));
                entity.setTotal_register(ran.nextInt(1000000));
                entity.setTotal_register_PSC(ran.nextInt(1000000));
                entity.setTotal_register_PSC_percent(ran.nextInt(1000000));
                entity.setTotal_revenue(ran.nextInt(1000000));
                entity.setTotal_unregister_by_sub(ran.nextInt(1000000));
                entity.setTotal_unregister_by_sys(ran.nextInt(1000000));
                list.add(entity);
            }
            result.setList(list);
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            logger.error(ex);
        } finally {
           
        }
        return result;
    }

    private String getTblName(String tblName) {
        String result = tblName;
        Configuration config = ConfigurationManager.getConfiguration(CommonConstants.SYS_CONFIG_NAME);
        String schemaName = config.getProperty("ORA_SCHEMA", "", "JDBC");
        result = (schemaName.equals("")) ? result : schemaName + "." + result;
        return result;
    }

    public String getAddress() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public List<HashMap<String, String>> getDataFromQuery(String query) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public List<HashMap<String, String>> getDataFromStore(String query) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<HashMap<String, String>> getDataFromStoreMySQL(String query) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean runSummary(Date report_date) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
