/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.elcom.vinamailreport;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.xml.DOMConfigurator;

import com.elcom.mylogger.LogAction;
import com.elcom.mylogger.LogConst;
import com.elcom.mylogger.LogInterface;
import com.elcom.vinamailreport.common.CommonConstants;
import com.elcom.vinamailreport.common.ForceSendTestProcess;
import com.elcom.vinamailreport.common.mail.MailImplement;
import com.ligerdev.appbase.utils.BaseUtils;
import com.ligerdev.appbase.utils.http.HttpServerUtils;

/**
 *
 * @author Administrator
 */
public class Main {

    private static Main app = null;

    public static Main getInstance() {
        return app = (app == null) ? new Main() : app;
    }
    public static LogInterface logger = null;

    public static LogInterface getLogger(String name, byte level) {
        String logPath = CommonConstants.BASE_FOLDER + "log" + File.separator + "mailReport";
        File f = new File(logPath);
        if (!f.exists()) {
            f.mkdirs();
        }
        LogInterface result = new LogAction(logPath, name, level);
        return result;
    }

    private SendMailThread mail;
    public MailImplement m;

    public static void main(String[] args) {
        try {
        	DOMConfigurator.configure("config/log4j.xml"); 
            app = getInstance();
            logger = getLogger("dailyReport", LogConst.LEVEL.ALL);
            logger.infor("Hello, i am Sending Email DailyReport. VVVVV 1.0");
            app.start();
            
            if(BaseUtils.isNotBlank(app.m.getServlet())) { //listen servlet, thay vì sendmail thì sẽ print html khi dc call
            	BaseUtils.sleep(500);
            	logger.infor("start servlet: " + app.m.getServlet()); 
                HttpServerUtils servers = new HttpServerUtils();
                servers.addServlet(app.m.getServlet(), new ServletContent());
                servers.startServer();
            }
            Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
                public void run() {
                    logger.infor("Asked to stop!");
                    app.stop();
                    logger.infor("Goodbye.");
                }
            }, "HOOK    "));
            
        } catch (Exception ex) {
            logger.error(ex);
            app.stop();
        }
    }

    private void start() {
        ReloadConfig rl = new ReloadConfig(logger);
        rl.reloadConfig();
        
        m = new MailImplement(logger);
        m.init();
        
        LastSentDateMan lastSentMan = new LastSentDateMan(logger);
        mail = new SendMailThread("SendDailyReport", m, logger, lastSentMan);
        mail.execute();

        ExecutorService service = Executors.newCachedThreadPool();
        service.submit(ForceSendProcess.getInstance());
        service.submit(ForceSendTestProcess.getInstance());
    }

    private void stop() {
        ForceSendProcess.getInstance().stop();
        mail.kill();
    }
}
