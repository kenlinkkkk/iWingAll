/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.elcom.vinamailreport;

import com.elcom.mylogger.LogInterface;
import com.elcom.vinamailreport.common.CommonConstants;
import com.elcom.vinamailreport.common.ThreadBase;
import com.elcom.vinamailreport.common.interfaces.MailInterface;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import org.jconfig.Configuration;
import org.jconfig.ConfigurationManager;

/**
 *
 * @author Administrator
 */
public class SendMailThread extends ThreadBase {

    private String name;
    private LogInterface logger;
    private MailInterface mail;
    private LastSentDateMan lastSentMan;

    public SendMailThread(String name, MailInterface mail, LogInterface logger, LastSentDateMan lastSentMan) {
        super(name);
        this.name = name;
        this.logger = logger;
        this.mail = mail;
        this.lastSentMan = lastSentMan;
    }

    @Override
    protected void onExecuting() throws Exception {
        logger.infor("=======Started " + name + "========");
    }

    @Override
    protected void onKilling() {
        logger.infor("=======Stopped " + name + "========");
    }

    @Override
    protected void onException(Exception e) {
        logger.error(e);
    }

    @Override
    protected long sleeptime() throws Exception {
        return 1 * 60 * 1000;
    }

    @Override
    protected void action() throws Exception {
        try {
           
//            if(day_2_send == -1 || getCurrentDateInMonth() == day_2_send)
//            {
//                if (getCurHour() == getHourConfig()) {
                    if(checkCanSend())
                    {
                        String current = getCurrentDate();
                        if (!current.equals(lastSentMan.getLast_sent())) {
                            if(mail.send(null))
                            {
                                lastSentMan.writeBackup(current);
                            }
                        }
                    }
//                }
//            }
        } catch (Exception ex) {
            logger.infor("PROCESS SEND MAIN FAIL. ");
            logger.infor("PROCESS SEND MAIN FAIL. " + ex.getMessage(), ex);
        }
    }

    private int getCurHour() {
        return Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
    }

    private int getHourConfig() {
        Configuration config = ConfigurationManager.getConfiguration(CommonConstants.SYS_CONFIG_NAME);
        int hour = Integer.parseInt(config.getProperty("TIME_2_SEND", "7", "MAIL"));
        return hour;
    }
    
   
    private boolean checkCanSend() {
        Configuration config = ConfigurationManager.getConfiguration(CommonConstants.SYS_CONFIG_NAME);
        int day_send_type = Integer.parseInt(config.getProperty("DAY_2_SEND_TYPE", "1", "MAIL"));
        int hour = Integer.parseInt(config.getProperty("TIME_2_SEND", "7", "MAIL"));
        
        if(getCurHour() != hour)
        {
            return false;
        }
        if(day_send_type == 1)
        {
            return true;
        } 
        else 
        {
            int day = Integer.parseInt(config.getProperty("DAY_2_SEND", "1", "MAIL"));
            
            if(day_send_type == 2)
            {
                if(day == Calendar.getInstance().get(Calendar.getInstance().DAY_OF_WEEK))
                {
//                    if (getCurHour() == getHourConfig()) {
//                        return true;
//                    }
                    return true;
                }
            }
            else
            {
                if(day == Calendar.getInstance().get(Calendar.getInstance().DAY_OF_MONTH))
                {
//                    if (getCurHour() == getHourConfig()) {
//                        return true;
//                    }
                    return true;
                }
            }
        }
        return false;
    }

    private String getCurrentDate() {
        SimpleDateFormat formatter;

        Date date = Calendar.getInstance().getTime();

        formatter = new SimpleDateFormat("dd/MM/yyyy");


        return formatter.format(date);
    }
    
    private int getCurrentDateInMonth() {
       
        return Calendar.getInstance().DAY_OF_MONTH;

    }
    
    public static void main(String[] args)
    {
        System.out.println(Calendar.getInstance().get(Calendar.getInstance().DAY_OF_MONTH));
        System.out.println(Calendar.getInstance().get(Calendar.getInstance().DAY_OF_WEEK));
    }
}
