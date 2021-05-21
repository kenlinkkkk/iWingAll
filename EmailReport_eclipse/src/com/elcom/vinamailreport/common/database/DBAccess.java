/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.elcom.vinamailreport.common.database;

import com.elcom.mylogger.LogInterface;
import com.elcom.vinamailreport.common.CommonConstants;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import org.jconfig.Configuration;
import org.jconfig.ConfigurationManager;

/**
 *
 * @author Administrator
 */
public class DBAccess {

    private static DBAccess instance;
    private LogInterface logger;

    public DBAccess(LogInterface logger) {
        this.logger = logger;
    }

    public static DBAccess getInstance(LogInterface logger) {
        logger.infor("=========Instance of DatabaseAccess========");
        if (instance == null) {
            return new DBAccess(logger);
        } else {
            return instance;
        }
    }

    private Connection getConnection(String classDriverName, String url, String user, String pass) {
        Connection con = null;
        try {
            Class.forName(classDriverName).newInstance();
            con = DriverManager.getConnection(url, user, pass);
            con.setAutoCommit(false);
            logger.infor("===Connection DB: Connection has been connected!!!===");
        } catch (ClassNotFoundException ex) {
            logger.error("Error",ex);
        } catch (SQLException ex) {
            logger.error("===Connection DB: Connection Errored!!!===");
            logger.error("Error",ex);
        } catch (Exception ex)
        {
            logger.error("===Connection DB: other Errored!!!===");
            logger.error("Error",ex);
        }
        return con;
    }

    public Connection getConnection() {
        Connection conn = null;
        Configuration config = ConfigurationManager.getConfiguration(CommonConstants.SYS_CONFIG_NAME);
        String url = config.getProperty("ORA_URL", "jdbc:oracle:thin:@192.168.11.99:1521:vasdb", "JDBC");
        String userName = config.getProperty("ORA_USER", "vasdb", "JDBC");
        String password = config.getProperty("ORA_PWD", "vasdb", "JDBC");
        String classDriverName = config.getProperty("ORA_DRIVER", "oracle.jdbc.driver.OracleDriver", "JDBC");
        logger.infor("URL: " + url);
        logger.infor("USER: " + userName);
//        logger.infor("PASS: " + password);
        logger.infor("DRIVER: " + classDriverName);
        conn = getConnection(classDriverName, url, userName, password);
        return conn;
    }

    public void closeConnection(Connection cn) {
        if (cn != null) {
            try {
                if (!cn.isClosed()) {
                    cn.close();
                    logger.infor("===Closed Connection===");
                }
            } catch (SQLException e) {
                logger.error(e);
            }
        }
    }

    public void commitTransaction(Connection cn) {
        try {
            cn.commit();
            logger.infor("===Committed Transaction===");
        } catch (SQLException e) {
            logger.error(e);
        }
    }

    public void rollBackTransaction(Connection cn) {
        try {
            cn.rollback();
            logger.infor("===RollBack Transaction===");
        } catch (SQLException e) {
            logger.error(e);
        }
    }
}
