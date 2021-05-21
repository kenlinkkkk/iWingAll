/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.elcom.vinamailreport;

import com.elcom.mylogger.LogInterface;
import java.io.*;
import java.util.Properties;

/**
 *
 * @author tig3r4ev3r
 */
public class LastSentDateMan {

    private String last_sent = "01/01/1970";
//    private int minIdx = 0;
//    private int maxIdx = 9999;
    private String properties_key = "last_date";
//    private Log logger = LogFactory.getLog(this.getClass());
    private LogInterface logger;
    String fileDir = "./last_sent_date.ini";
    
    public LastSentDateMan(LogInterface logger) {

        this.logger = logger;
        //======================================================================


        File f = new File(fileDir);

        try {

            FileInputStream fis = new FileInputStream(f);

            Properties pInfo = new Properties();

            pInfo.load(fis);

            last_sent = pInfo.getProperty(properties_key);

            fis.close();

        } catch (FileNotFoundException ex) {
            createFileBackup(fileDir);

        } catch (IOException ex) {

            f.delete();
            createFileBackup(fileDir);
        } catch (Exception ex) {
        }
    }

    private void createFileBackup(String fileDir) {
        try {
            File f = new File(fileDir);

            FileOutputStream fout = new FileOutputStream(f);

            String value = properties_key + " = " + last_sent;

            fout.write(value.getBytes());

            fout.flush();

            fout.close();

        } catch (Exception ex) {

            logger.warn("CREATE FILE CDR_IDX FAIL, UN-HANDLE EXCEPTION.", ex);

        }
    }

    public void writeBackup(String last_sent) {
//        String fileDir = "./cdr_idx.ini";
        this.last_sent = last_sent;
        File f = new File(fileDir);

        try {

            FileInputStream fis = new FileInputStream(f);

            Properties pInfo = new Properties();

            pInfo.load(fis);

            pInfo.setProperty(properties_key, last_sent);

            FileOutputStream fout = new FileOutputStream(f);

            pInfo.store(fout, "");

            fis.close();


        } catch (FileNotFoundException ex) {

            logger.warn("FILE CDR_IDX BACKUP NOT FOUND, CREATE FILE.", ex);

            createFileBackup(fileDir);

        } catch (IOException ex) {
            f.delete();
            createFileBackup(fileDir);
        }
    }

    public String getLast_sent() {
        return last_sent;
    }

    public void setLast_sent(String last_sent) {
        this.last_sent = last_sent;
    }
    
}
