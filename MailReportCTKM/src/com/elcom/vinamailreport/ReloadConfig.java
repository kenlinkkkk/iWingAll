/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.elcom.vinamailreport;

import com.elcom.mylogger.LogInterface;
import com.elcom.vinamailreport.common.CommonConstants;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import org.jconfig.ConfigurationManager;
import org.jconfig.ConfigurationManagerException;
import org.jconfig.handler.XMLFileHandler;

/**
 *
 * @author Administrator
 */
public class ReloadConfig {

    private LogInterface logger;

    public ReloadConfig(LogInterface logger) {
        this.logger = logger;
    }

    public synchronized void reloadConfig() {
        ConfigurationManager cm = ConfigurationManager.getInstance();
        try {
            String fileStr = CommonConstants.BASE_FOLDER + "config" + File.separator;
            File file = new File(fileStr);
            if (!file.exists()) {
                file.mkdirs();
            }
            fileStr += "dailyReportConfig.xml";
            file = new File(fileStr);
            if (!file.exists()) {
                createDefaultConfigFile(file);
            }
            XMLFileHandler fileHandler = new XMLFileHandler();
            fileHandler.setFile(file);
            cm.load(fileHandler, CommonConstants.SYS_CONFIG_NAME);
            
        } catch (ConfigurationManagerException cme1) {
            logger.error(cme1);
        }
    }

    private void createDefaultConfigFile(File fout) {
        BufferedWriter w;
        try {
            w = new BufferedWriter(new FileWriter(fout));
            w.write("<?xml version=\"1.0\" ?>");
            w.newLine();
            w.write("\t<properties>");
            w.newLine();
            w.write("\t\t<category name=\"JDBC\">");
            w.newLine();
            w.write("\t\t\t<property name=\"ORA_URL\" value=\"jdbc:oracle:thin:@10.8.13.31:1521:expc\"/>");
            w.newLine();
            w.write("\t\t\t<property name=\"ORA_USER\" value=\"expc\"/>");
            w.newLine();
            w.write("\t\t\t<property name=\"ORA_PWD\" value=\"expc\"/>");
            w.newLine();
            w.write("\t\t\t<property name=\"ORA_SCHEMA\" value=\"owner_expc\"/>");
            w.newLine();
            w.write("\t\t\t<property name=\"ORA_DRIVER\" value=\"oracle.jdbc.driver.OracleDriver\"/>");
            w.newLine();
            w.write("\t\t\t<property name=\"NUM_COMMIT_DB\" value=\"500\"/>");
            w.newLine();
            w.write("\t\t</category>");
            w.newLine();
            w.write("\t\t<category name=\"MAIL\">");
            w.newLine();
            w.write("\t\t\t<property name=\"SERVLET\" value=\"http://0.0.0.0:4999/emailcontent\"/>");
            w.newLine();
            w.write("\t\t\t<property name=\"TLS\" value=\"true\"/>");
            w.newLine();
            w.write("\t\t\t<property name=\"TIME_2_SEND\" value=\"5\"/>");
            w.newLine();
            w.write("\t\t\t<property name=\"HOST\" value=\"\"/>");
            w.newLine();
            w.write("\t\t\t<property name=\"PORT\" value=\"\"/>");
            w.newLine();
            w.write("\t\t\t<property name=\"FROM\" value=\"\"/>");
            w.newLine();
            w.write("\t\t\t<property name=\"SUBJECT\" value=\"\"/>");
            w.newLine();
            w.write("\t\t\t<property name=\"USER\" value=\"\"/>");
            w.newLine();
            w.write("\t\t\t<property name=\"PASS\" value=\"\"/>");
            w.newLine();
            w.write("\t\t\t<property name=\"TOs\" value=\"hungnq@elcom.com.vn,hieunx@elcom.com.vn\"/>");
            w.newLine();
            w.write("\t\t</category>");
            w.newLine();
            w.write("\t</properties>");
            w.flush();
            w.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
