/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.elcom.vinamailreport;

import com.elcom.mylogger.LogAction;
import com.elcom.mylogger.LogConst;
import com.elcom.mylogger.LogInterface;
import com.elcom.vinamailreport.common.CommonConstants;
import com.elcom.vinamailreport.common.database.DatabaseActions;
import com.elcom.vinamailreport.common.interfaces.DBInterface;
import com.elcom.vinamailreport.common.interfaces.MailInterface;
import com.elcom.vinamailreport.common.mail.MailImplement;
import java.io.File;
import java.util.Calendar;

/**
 *
 * @author Administrator
 */
public class MainSummary {

    private static MainSummary app = null;

    public static MainSummary getInstance() {
        return app = (app == null) ? new MainSummary() : app;
    }
    private LogInterface logger = null;

    public static LogInterface getLogger(String name, byte level) {
        String logPath = CommonConstants.BASE_FOLDER + "log" + File.separator + "mailReport";
        File f = new File(logPath);
        if (!f.exists()) {
            f.mkdirs();
        }
        LogInterface result = new LogAction(logPath, name, level);
        return result;
    }
    
    public static void main(String[] args) {
        try {

            app = getInstance();
            app.logger = getLogger("dailyReport", LogConst.LEVEL.ALL);
            app.logger.infor("Hello, i am Sending Email DailyReport - SUMMARY");
            ReloadConfig rl = new ReloadConfig(app.logger);
        rl.reloadConfig();
            DBInterface db = new DatabaseActions(app.logger);
            Calendar car = Calendar.getInstance();
            car.add(Calendar.DATE, -1);
            db.runSummary(car.getTime());
            app.logger.infor("Hello, i am Sending Email DailyReport - SUMMARY - DONE");
        } catch (Exception ex) {
            app.logger.error(ex);
          
        }finally{
            
        }
    }

}
