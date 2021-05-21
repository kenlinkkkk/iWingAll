/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.elcom.vinamailreport.common.mail;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.Reader;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.jconfig.Configuration;
import org.jconfig.ConfigurationManager;

import com.elcom.mylogger.LogInterface;
import com.elcom.vinamailreport.Main;
import com.elcom.vinamailreport.common.CommonConstants;
import com.elcom.vinamailreport.common.CoreConfig;
import com.elcom.vinamailreport.common.TableDataEntity;
import com.elcom.vinamailreport.common.database.DatabaseActions;
import com.elcom.vinamailreport.common.interfaces.DBInterface;
import com.elcom.vinamailreport.common.interfaces.MailInterface;

/**
 * @author Administrator
 */
public class MailImplement implements MailInterface {

    private LogInterface logger;
    private String hostName;
    private String port;
    private String srcAddress;
    private String subject;
    //    private String header;
    private String user;
    private String pass;
    private DBInterface db;
    private String tls = "false";
    private String servlet = "";

    public MailImplement(LogInterface logger) {
        this.logger = logger;
        db = new DatabaseActions(logger);
//        db = new DatabaseActions_simu(logger);
    }
    
    public boolean send(String tos) throws Exception {
        init(); 
        Properties props = new Properties();
        logger.infor("account: " + user + "/" +  pass + ", host: " + hostName + ", port: " + port);
        
        props.put("mail.smtp.user", user);
        props.put("mail.smtp.password", pass);
        props.put("mail.smtp.protocol", "smtp");
        props.put("mail.smtp.host", hostName);
        props.put("mail.smtp.port", port);
        
        props.put("mail.smtp.starttls.enable", tls);
        props.put("mail.smtp.ssl.enable", "false");
        // props.put("mail.smtp.starttls.enable", "true");
        // props.put("mail.smtp.ssl.enable", "true");
        
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.socketFactory.port", port);
        props.put("mail.smtp.socketFactory.class", "");
        props.put("mail.smtp.socketFactory.fallback", "false");
        props.put("mail.debug", "true");
        props.setProperty("mail.debug.auth", "true");
        
        // props.put("mail.smtp.ssl.trust", hostName);
        props.put("mail.smtp.ssl.trust", "*");
        
        // add
        props.put("mail.smtp.ssl.checkserveridentity", "false");
        props.put("mail.transport.protocol", "smtp");
        
        /* 
        } else {
            props.put("mail.transport.protocol", "smtp");
            props.put("mail.smtp.host", host);
            props.put("mail.smtp.port", Integer.valueOf(port));
            if (connectionSecurity == 1) {
                props.put("mail.smtp.starttls.enable", "true");
            }
            if (connectionSecurity == 2) {
                props.put("mail.smtp.starttls.required", "true");
            }
            if (connectionSecurity == 3) {
                props.put("mail.smtp.starttls.enable", "true");
                props.put("mail.smtp.ssl.enable", "true");
            }
        }
        props.put("mail.smtp.ssl.checkserveridentity", "false");
        props.put("mail.smtp.ssl.trust", "*");*/

        String contend = ""; 
        //Authenticator au = new SMTPAuthenticator();
        // Session session = Session.getDefaultInstance(props, au);
        // session.setDebug(true);
        // Message msg = new MimeMessage(session);
        // SmtpAuthenticator authentication = new SmtpAuthenticator(user, pass);
        // javax.mail.Message msg = new MimeMessage(session.getDefaultInstance(props, authentication));
        Session sess = null; 
        try {
            sess = Session.getDefaultInstance(props, new Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(user, pass);
                }
            });
        } catch (SecurityException e) {
            sess = Session.getInstance(props, new Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(user, pass);
                }
            });
        }
        new File ("error.txt").delete();
        PrintStream out = new PrintStream(new File ("error.txt"));
        sess.setDebug(true);
        sess.setDebugOut(out);
        MimeMessage msg = new MimeMessage(sess);
        
        InternetAddress addressFrom = new InternetAddress(srcAddress);
        msg.setFrom(addressFrom);

        if (tos == null || tos.isEmpty()) {
            //======================================================================
            InternetAddress[] address = getAddresses();
            if (address != null && address.length > 0) {
                msg.setRecipients(Message.RecipientType.TO, address);
            }
            //======================================================================
            InternetAddress[] ccAddress = getCCAddresses();
            if (ccAddress != null && ccAddress.length > 0) {
                msg.setRecipients(Message.RecipientType.CC, ccAddress);
            }
            //======================================================================
            InternetAddress[] bccAddress = getBCCAddresses();
            if (bccAddress != null && bccAddress.length > 0) {
                msg.setRecipients(Message.RecipientType.BCC, bccAddress);
            }
            if ((address == null || address.length == 0) && (ccAddress == null || ccAddress.length == 0) && (bccAddress == null || bccAddress.length == 0)) {
                logger.infor("NOT SENT! TOs, BCCs & CCs are empty!");
                return false;
            }
        } else {
            InternetAddress[] address = getAddresses(tos);
            if (address != null && address.length > 0) {
                logger.infor("sendMail to fixed: " + tos);
                msg.setRecipients(Message.RecipientType.TO, address);
            }
        }
        msg.setSubject(subject);

        //======================================================================
        contend = getContend();
        //======================================================================
        if (contend == null) {
            return false;
        }
        msg.setContent(contend, "text/html; charset=utf-8");
        logger.infor("=========Start sending DailyReport=======");
        try {
            Transport.send(msg);
        }catch (Exception e){
            logger.error(" send mail error: " + e.toString(),e);
        }
        logger.infor("=========DailyReport has been sent=======");
        try {
        	out.close();
		} catch (Exception e) {
		}
        return true;
    }

    public static final void main(String[] args) {
//        MailImplement.getContend();
    }

    public String getContend() {
        String result = "";
//        Report report = db.getInput();
//
//        if (report.getList() == null) {
//            return null;
//        }

        File contentFile = new File("./config/content.html");
        if (contentFile.exists()) {
            return getContend("content.html", "dataReport.properties");
        } else {
            result = "<html><head>"
                    + "<meta charset=\"utf-8\">"
                    + "<style>td{padding-left: 2px;}</style></head>"
                    + "<body>";
        }
        CoreConfig config = new CoreConfig("config/dataReport.properties");
        boolean is_data_valid = true;

        for (TableDataEntity entity : config.getLstData()) {
            //==================================================================
            //HEADER OF TABLE
            result += "<h2 align=center style='text-align:center'><span style='color:" + entity.getStyle().get(0) + "'>" + entity.getHeader() + "</span></h2>";
            Reader in = null;
            BufferedReader br = null;
            try {
                String s = "";

                in = new InputStreamReader(new FileInputStream("config/" + entity.getData_header()), "UTF-8");

                br = new BufferedReader(in);
                StringBuilder content = new StringBuilder(1024);
                while ((s = br.readLine()) != null) {
                    content.append(s);
                }
                result += content;

            } catch (Exception ex) {
                ex.printStackTrace();
            } finally {
                try {
                    if (br != null) {
                        br.close();
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                try {
                    if (in != null) {
                        in.close();
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

            }
            //GET CONTENT OF TABLE
            int i = 0;
            String[] col_list = entity.getData_list().split("\\|");
            String[] footer_list = entity.getFooter_list().split("\\|");

            if (entity.getFooter_type() != TableDataEntity.DATA_FOOTER_TYPE.TYPE_NONE && col_list.length != footer_list.length) {
                logger.warn("DATA TABLE " + entity.getHeader() + " CONFIG FAIL: COL LENGTH: " + col_list.length + " /FOOTER LENGTH: " + footer_list.length);
                is_data_valid = false;
                break;
            }
            List<HashMap<String, String>> data = null;

            if (entity.getData_query_type() == TableDataEntity.DATA_QUERY_TYPE.TYPE_QUERY) {
                data = db.getDataFromQuery(entity.getData_query());
            } else {
                if (entity.getDriver_type() == 1)
                    data = db.getDataFromStoreMySQL(entity.getData_query());
                else
                    data = db.getDataFromStore(entity.getData_query());
            }

            if (data == null || data.isEmpty() || data.size() <= 0) {
                logger.warn("DATA TABLE " + entity.getHeader() + " DATA NULL!");
                is_data_valid = false;
                break;
            }

            for (HashMap<String, String> row : data) {
                if (i % 2 == 0) {
//                    result += "<tr style='background-color: " + entity.getStyle().get(2) + ";height: 25px;'>";
                    result += "<tr style='height: 25px;'>";
                } else {
//                    result += "<tr style='background-color: #FFFFFF;height: 25px;'>";
                    result += "<tr style='height: 25px;'>";
                }

                for (String col : col_list) {
                    if (col == null || col.equals("") || col.equals("#")) {
                        result += "<td><span style='padding-right: 1pt;'></span></td>";
                        continue;
                    }

                    String[] col_special = col.split(";");
                    String align = "right";
                    int format = 0;
                    String color = "black";
                    if (col_special.length >= 2) {
                        align = col_special[1];

                        if (col_special.length >= 3) {
                            try {
                                format = Integer.parseInt(col_special[2]);
                            } catch (Exception ex) {
                            }
                        }
                        if (col_special.length >= 4) {
                            try {
                                color = col_special[3];
                            } catch (Exception ex) {
                            }
                        }
                    }

                    String value = row.get(col_special[0]);

                    if (format != 0) {
                        result += "<td align='" + align + "'><span style='padding-right: 1pt; color: " + color + ";'>" + priceWithDecimal(Double.valueOf(value)) + "</span></td>";
                    } else {
                        result += "<td align='" + align + "'><span style='padding-right: 1pt; color: " + color + ";'>" + value + "</span></td>";
                    }
                }

                result += "</tr>";
                //==============================================================
                i++;
            }

            switch (entity.getFooter_type()) {
                case TableDataEntity.DATA_FOOTER_TYPE.TYPE_SUM:
                    HashMap<String, Double> sum_data = new HashMap<String, Double>();
                    for (HashMap<String, String> row : data) {
                        for (String col : footer_list) {
                            if (col == null || col.equals("")) {

                                continue;
                            }
                            String[] col_special = col.split(";");

                            String value = row.get(col_special[0]);

                            if (value != null) {

                                double oldData = 0;

                                if (sum_data.get(col_special[0]) != null) {
                                    oldData = sum_data.get(col_special[0]);
                                }

                                sum_data.put(col_special[0], oldData + Double.valueOf(value));
                            }
                        }
                    }

                    result += "<tr style='background-color: " + entity.getStyle().get(1) + ";height: 30px;'>";
                    for (String col : footer_list) {

                        if (col == null || col.equals("")) {
                            result += "<td></td>";
                        } else {
                            String[] col_special = col.split(";");
                            String align = "right";
                            int format = 0;
                            String color = "black";
                            if (col_special.length >= 2) {
                                align = col_special[1];

                                if (col_special.length == 3) {
                                    try {
                                        format = Integer.parseInt(col_special[2]);
                                    } catch (Exception ex) {
                                    }
                                }

                                if (col_special.length == 4) {
                                    try {
                                        format = Integer.parseInt(col_special[2]);
                                        color = col_special[3];
                                    } catch (Exception ex) {
                                    }
                                }
                            }

                            if (col.startsWith("#")) {
                                result += "<td align='" + align + "'><span style='padding-right: 1pt;font-weight: bold; color: " + color + ";'>" + col_special[0].substring(1) + "</span></td>";
                            } else {

                                double sum = 0;
                                try {
                                    sum = sum_data.get(col_special[0]);
                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                }
                                if (format != 0) {
                                    result += "<td align='" + align + "'><span style='padding-right: 1pt;font-weight: bold; color: " + color + ";'>" + priceWithDecimal(sum) + "</span></td>";
                                } else {
                                    result += "<td align='" + align + "'><span style='padding-right: 1pt;font-weight: bold; color: " + color + ";'>" + sum + "</span></td>";
                                }
                            }
                        }
                    }

                    result += "</tr>";
                    break;
                case TableDataEntity.DATA_FOOTER_TYPE.TYPE_QUERY:
                case TableDataEntity.DATA_FOOTER_TYPE.TYPE_STORE:
                    List<HashMap<String, String>> footer_data = null;

                    if (entity.getFooter_type() == TableDataEntity.DATA_FOOTER_TYPE.TYPE_QUERY) {
                        footer_data = db.getDataFromQuery(entity.getFooter_query());
                    } else {
                        if (entity.getDriver_type() == 1)
                            footer_data = db.getDataFromStoreMySQL(entity.getFooter_query());
                        else
                            footer_data = db.getDataFromStore(entity.getFooter_query());
                    }

                    if (footer_data == null) {
                        logger.warn("DATA TABLE " + entity.getHeader() + " DATA FOOTER NULL!");
                        //is_data_valid = false;
                        continue;
                    }

                    for (HashMap<String, String> row : footer_data) {
                        result += "<tr style='background-color: #6699CC;height: 30px;'>";
                        for (String col : footer_list) {

                            if (col == null || col.equals("")) {
                                result += "<td></td>";
                            } else {
                                String[] col_special = col.split(";");
                                String align = "right";
                                int format = 0;
                                String color = "black";
                                if (col_special.length >= 2) {
                                    align = col_special[1];

                                    if (col_special.length == 3) {
                                        try {
                                            format = Integer.parseInt(col_special[2]);
                                        } catch (Exception ex) {
                                        }
                                    }
                                    if (col_special.length == 4) {
                                        try {
                                            color = col_special[3];
                                        } catch (Exception ex) {
                                        }
                                    }
                                }

                                if (col.startsWith("#")) {
                                    result += "<td align='" + align + "'><span style='padding-right: 1pt;font-weight: bold; color: " + color + ";'>" + col_special[0].substring(1) + "</span></td>";
                                } else {
                                    String value = row.get(col_special[0]);

                                    if (format != 0) {
                                        result += "<td align='" + align + "'><span style='padding-right: 1pt;font-weight: bold; color: " + color + ";'>" + priceWithDecimal(Double.valueOf(value)) + "</span></td>";
                                    } else {
                                        result += "<td align='" + align + "'><span style='padding-right: 1pt;font-weight: bold; color: " + color + ";'>" + value + "</span></td>";
                                    }
                                }
                            }
                        }

                        result += "</tr>";
                        //==============================================================
                        i++;
                    }
                    break;
                default:
                    break;
            }

            result += "</table> <br/><br/>";

        }

        result += "</body></html>";

        if (is_data_valid == false) {
            result = null;
            logger.warn("DATA NOT VALID AT ALL! DON'T SEND EMAIL!");
        }

//        logger.debug(result);

        config = null;

//        
//        //======================================================================
//        List<ReportEntity> list = report.getList();
//        int i = 0;
//        double total_revenue = 0;
//        for (ReportEntity item : list) {
//            if (i % 2 == 0) {
//                result += "<tr style='background-color: #EAF5FF;height: 25px;'>";
//            } else {
//                result += "<tr style='background-color: #FFFFFF;height: 25px;'>";
//            }
//            result += "<td>" + item.getTime() + "</td>";
//            result += "<td align='right'><span style='padding-right: 1pt;'>" + item.getTotal_active() + "</span></td>";
//            result += "<td align='right'><span style='padding-right: 1pt;'>" + item.getTotal_unregister_by_sub() + "</span></td>";
//            result += "<td align='right'><span style='padding-right: 1pt;'>" + item.getTotal_unregister_by_sys() + "</span></td>";
//            result += "<td align='right'><span style='padding-right: 1pt;'>" + item.getTotal_register() + "</span></td>";
//            result += "<td align='right'><span style='padding-right: 1pt;'>" + item.getTotal_register_PSC() + "</span></td>";
//            result += "<td align='right'><span style='padding-right: 1pt;'>" + item.getTotal_register_PSC_percent() + "</span></td>";
//            result += "<td align='right'><span style='padding-right: 1pt;'>" + item.getTotal_recharge() + "</span></td>";
//            result += "<td align='right'><span style='padding-right: 1pt;'>" + item.getTotal_recharge_PSC() + "</span></td>";
//            result += "<td align='right'><span style='padding-right: 1pt;'>" + item.getTotal_recharge_PSC_percent() + "</span></td>";
//            result += "<td align='right'><span style='padding-right: 1pt;'>" + priceWithDecimal(item.getTotal_revenue()) + "</span></td>";
//            result += "<td align='right'><span style='padding-right: 1pt;'>" + priceWithDecimal(item.getTotal_to_date()) + "</span></td>";
//            result += "<td align='right'><span style='padding-right: 1pt;'>" + priceWithDecimal(item.getTotal_of_last_month()) + "</span></td>";
//            result += "</tr>";
//            i++;
//            total_revenue += item.getTotal_revenue();
//
//        }
//
//        result += "<tr style='height:18.75pt; color: white;'>";
//        result += "<td style='width:150.0pt;background:#6699CC;padding:0in 0in 0in 0in;height:18.75pt' colspan='10'>";
//        result += "<p class='MsoNormal' align=center style='text-align:right; margin-right: 8px;'><b><span style='font-size:10.0pt;font-family:'Arial','sans-serif';color:white'>" + "T&#7893;ng" + "</span></b><o:p></o:p></p></td>";
//        result += "<td width=200 align='right' style='width:150.0pt;background:#6699CC;padding:0in 0in 0in 0in;height:18.75pt'>";
//        result += "<p class='MsoNormal' align=right style='text-align:right; margin-left: 4px;'><b><span style='font-size:10.0pt;font-family:'Arial','sans-serif';color:white'>" + priceWithDecimal(total_revenue) + "</span></b><o:p></o:p></p></td>";
//        result += "<td style='width:150.0pt;background:#6699CC;padding:0in 0in 0in 0in;height:18.75pt' colspan='2'>";
//        result += "</tr>";
//        result += "</table>";
//        result += "<p class='MsoNormal'>Report is created at " + curFTime() + " <o:p></o:p></p><p class='MsoNormal'><br><br></body></html>";
        return result;
    }

    // private String getContend2(File contentFile) {
    public String getContend(String contentFile, String sqlFile) {
    	
        String result = "";
        BufferedReader reader = null;
        try {
        	//reader = new BufferedReader(new FileReader(contentFile));
            reader = new BufferedReader(new FileReader("config/" + contentFile));
            String line = null;
            while ((line = reader.readLine()) != null) {
                result += line;
            }
            logger.infor("Read Content.html value: " + result);
        } catch (Exception e) {
            logger.error("Get conent.html error: " + e.toString(), e);
            result = "<html><head>"
                    + "<meta charset=\"utf-8\">"
                    + "<style>td{padding-left: 2px;}</style></head>"
                    + "<body>";
        } finally {
            if (reader != null)
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
        CoreConfig config = new CoreConfig("config/" + sqlFile);
        // CoreConfig config = new CoreConfig("config/dataReport.properties");
        boolean is_data_valid = true;

        for (TableDataEntity entity : config.getLstData()) {
            //==================================================================
            //HEADER OF TABLE
            result += "<h2><span>" + entity.getHeader() + "</span></h2>";
            Reader in = null;
            BufferedReader br = null;
            try {
                String s = "";
                in = new InputStreamReader(new FileInputStream("config/" + entity.getData_header()), "UTF-8");

                br = new BufferedReader(in);
                StringBuilder content = new StringBuilder(1024);
                while ((s = br.readLine()) != null) {
                    content.append(s);
                }
                result += content;

            } catch (Exception ex) {
                ex.printStackTrace();
            } finally {
                try {
                    if (br != null) {
                        br.close();
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                try {
                    if (in != null) {
                        in.close();
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

            }
            //GET CONTENT OF TABLE

            int i = 0;

            String[] col_list = entity.getData_list().split("\\|");
            String[] footer_list = entity.getFooter_list().split("\\|");

            if (entity.getFooter_type() != TableDataEntity.DATA_FOOTER_TYPE.TYPE_NONE && col_list.length != footer_list.length) {
                logger.warn("DATA TABLE " + entity.getHeader() + " CONFIG FAIL: COL LENGTH: " + col_list.length + " /FOOTER LENGTH: " + footer_list.length);
                is_data_valid = false;
                break;
            }

            List<HashMap<String, String>> data = null;

            if (entity.getData_query_type() == TableDataEntity.DATA_QUERY_TYPE.TYPE_QUERY) {
                data = db.getDataFromQuery(entity.getData_query());
            } else {
                data = db.getDataFromStore(entity.getData_query());
            }
            if (data == null || data.isEmpty() || data.size() <= 0) {
                logger.warn("DATA TABLE " + entity.getHeader() + " DATA NULL!");
                is_data_valid = false;
                break;
            }
            for (HashMap<String, String> row : data) {
                if (i % 2 == 0) {
                    result += "<tr class=\"tr_00\">";
                } else {
                    result += "<tr class=\"tr_01\">";
                }

                for (String col : col_list) {
                    if (col == null || col.equals("") || col.equals("#")) {
                        result += "<td></td>";
                        continue;
                    }

                    String[] col_special = col.split(";");
                    String align = "right";
                    int format = 0;
                    String color = "black";
                    if (col_special.length >= 2) {
                        align = col_special[1];

                        if (col_special.length >= 3) {
                            try {
                                format = Integer.parseInt(col_special[2]);
                            } catch (Exception ex) {
                            }
                        }
                        if (col_special.length >= 4) {
                            try {
                                color = col_special[3];
                            } catch (Exception ex) {
                            }
                        }
                    }

                    String value = row.get(col_special[0]);

                    if (format != 0) {
                        result += "<td align='" + align + "'><span>" + priceWithDecimal(Double.valueOf(value)) + "</span></td>";
                    } else {
                        result += "<td align='" + align + "'><span>" + value + "</span></td>";
                    }
                }
                result += "</tr>";
                //==============================================================
                i++;
            }
            switch (entity.getFooter_type()) {
                case TableDataEntity.DATA_FOOTER_TYPE.TYPE_SUM:
                    HashMap<String, Double> sum_data = new HashMap<String, Double>();
                    for (HashMap<String, String> row : data) {
                        for (String col : footer_list) {
                            if (col == null || col.equals("")) {
                                continue;
                            }
                            String[] col_special = col.split(";");
                            String value = row.get(col_special[0]);

                            if (value != null) {

                                double oldData = 0;

                                if (sum_data.get(col_special[0]) != null) {
                                    oldData = sum_data.get(col_special[0]);
                                }
                                sum_data.put(col_special[0], oldData + Double.valueOf(value));
                            }
                        }
                    }

                    result += "<tr style='background-color: " + entity.getStyle().get(1) + ";height: 30px;'>";
                    for (String col : footer_list) {

                        if (col == null || col.equals("")) {
                            result += "<td></td>";
                        } else {
                            String[] col_special = col.split(";");
                            String align = "right";
                            int format = 0;
                            String color = "black";
                            if (col_special.length >= 2) {
                                align = col_special[1];

                                if (col_special.length == 3) {
                                    try {
                                        format = Integer.parseInt(col_special[2]);
                                    } catch (Exception ex) {
                                    }
                                }

                                if (col_special.length == 4) {
                                    try {
                                        format = Integer.parseInt(col_special[2]);
                                        color = col_special[3];
                                    } catch (Exception ex) {
                                    }
                                }
                            }
                            if (col.startsWith("#")) {
                                result += "<td align='" + align + "'><span style='padding-right: 1pt;font-weight: bold; color: " + color + ";'>" + col_special[0].substring(1) + "</span></td>";
                            } else {

                                double sum = 0;
                                try {
                                    sum = sum_data.get(col_special[0]);
                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                }
                                if (format != 0) {
                                    result += "<td align='" + align + "'><span style='padding-right: 1pt;font-weight: bold; color: " + color + ";'>" + priceWithDecimal(sum) + "</span></td>";
                                } else {
                                    result += "<td align='" + align + "'><span style='padding-right: 1pt;font-weight: bold; color: " + color + ";'>" + sum + "</span></td>";
                                }
                            }
                        }
                    }

                    result += "</tr>";
                    break;
                case TableDataEntity.DATA_FOOTER_TYPE.TYPE_QUERY:
                case TableDataEntity.DATA_FOOTER_TYPE.TYPE_STORE:
                    List<HashMap<String, String>> footer_data = null;

                    if (entity.getFooter_type() == TableDataEntity.DATA_FOOTER_TYPE.TYPE_QUERY) {
                        footer_data = db.getDataFromQuery(entity.getFooter_query());
                    } else {
                        footer_data = db.getDataFromStore(entity.getFooter_query());
                    }

                    if (footer_data == null) {
                        logger.warn("DATA TABLE " + entity.getHeader() + " DATA FOOTER NULL!");
                        //is_data_valid = false;
                        continue;
                    }

                    for (HashMap<String, String> row : footer_data) {
                        result += "<tr style='background-color: #6699CC;height: 30px;'>";
                        for (String col : footer_list) {

                            if (col == null || col.equals("")) {
                                result += "<td></td>";
                            } else {
                                String[] col_special = col.split(";");
                                String align = "right";
                                int format = 0;
                                String color = "black";
                                if (col_special.length >= 2) {
                                    align = col_special[1];

                                    if (col_special.length == 3) {
                                        try {
                                            format = Integer.parseInt(col_special[2]);
                                        } catch (Exception ex) {
                                        }
                                    }
                                    if (col_special.length == 4) {
                                        try {
                                            color = col_special[3];
                                        } catch (Exception ex) {
                                        }
                                    }
                                }
                                if (col.startsWith("#")) {
                                    result += "<td align='" + align + "'><span style='padding-right: 1pt;font-weight: bold; color: " + color + ";'>" + col_special[0].substring(1) + "</span></td>";
                                } else {
                                    String value = row.get(col_special[0]);

                                    if (format != 0) {
                                        result += "<td align='" + align + "'><span style='padding-right: 1pt;font-weight: bold; color: " + color + ";'>" + priceWithDecimal(Double.valueOf(value)) + "</span></td>";
                                    } else {
                                        result += "<td align='" + align + "'><span style='padding-right: 1pt;font-weight: bold; color: " + color + ";'>" + value + "</span></td>";
                                    }
                                }
                            }
                        }
                        result += "</tr>";
                        //==============================================================
                        i++;
                    }
                    break;
                default:
                    break;
            }
            result += "</table> <br/><br/>";
        }
        result += "</body></html>";

        if (is_data_valid == false) {
            result = null;
            logger.warn("DATA NOT VALID AT ALL! DON'T SEND EMAIL!");
        }

//        logger.debug(result);
        config = null;
        return result;
    }

    public void init() {
        Configuration config = ConfigurationManager.getConfiguration(CommonConstants.SYS_CONFIG_NAME);
        hostName = config.getProperty("HOST", "", "MAIL");
        port = config.getProperty("PORT", "", "MAIL");
        srcAddress = config.getProperty("FROM", "", "MAIL");
        subject = config.getProperty("SUBJECT", "", "MAIL");
        //  header = config.getProperty("HEAD_TITLE", "", "DAILY REPORT");
        user = config.getProperty("USER", "", "MAIL");
        pass = config.getProperty("PASS", "", "MAIL");
        tls = config.getProperty("TLS", "false", "MAIL");
        servlet = config.getProperty("SERVLET", "", "MAIL");
        Main.logger.infor("Load config: " + user + " | " + servlet); 
         
    }

    private InternetAddress[] getAddresses() throws Exception {
        Configuration config = ConfigurationManager.getConfiguration(CommonConstants.SYS_CONFIG_NAME);
        String addrr = config.getProperty("TOs", "", "MAIL");
        logger.infor("sendMail to: " + addrr);
        String[] adrresses = addrr.split(",");
        InternetAddress[] result = new InternetAddress[adrresses.length];
        for (int i = 0; i < adrresses.length; i++) {
            result[i] = new InternetAddress(adrresses[i]);
        }
        return result;
    }

    private InternetAddress[] getAddresses(String tos) throws Exception {
        String[] adrresses = tos.split(",");
        InternetAddress[] result = new InternetAddress[adrresses.length];
        for (int i = 0; i < adrresses.length; i++) {
            result[i] = new InternetAddress(adrresses[i]);
        }
        return result;
    }

    private InternetAddress[] getBCCAddresses() throws Exception {
        Configuration config = ConfigurationManager.getConfiguration(CommonConstants.SYS_CONFIG_NAME);
        String addrr = config.getProperty("BCCs", "", "MAIL");
        logger.infor("Bccs to: " + addrr);
        if (addrr == null || addrr.isEmpty())
            return null;
        String[] adrresses = addrr.split(",");
        InternetAddress[] result = new InternetAddress[adrresses.length];
        for (int i = 0; i < adrresses.length; i++) {
            result[i] = new InternetAddress(adrresses[i]);
        }
        return result;
    }

    private InternetAddress[] getCCAddresses() throws Exception {
        Configuration config = ConfigurationManager.getConfiguration(CommonConstants.SYS_CONFIG_NAME);
        String addrr = config.getProperty("CCs", "", "MAIL");
        logger.infor("CCs to: " + addrr);
        if (addrr == null || addrr.isEmpty())
            return null;
        String[] adrresses = addrr.split(",");
        InternetAddress[] result = new InternetAddress[adrresses.length];
        for (int i = 0; i < adrresses.length; i++) {
            result[i] = new InternetAddress(adrresses[i]);
        }
        return result;
    }


    private String curFTime() {
        String result = "";
        String year = String.valueOf(Calendar.getInstance().get(Calendar.YEAR));
        int intMonth = Calendar.getInstance().get(Calendar.MONTH) + 1;
        String month = (intMonth < 10) ? "0" + intMonth : String.valueOf(intMonth);
        int intDay = Calendar.getInstance().get(Calendar.DATE);
        String day = (intDay < 10) ? "0" + intDay : String.valueOf(intDay);
        int intHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        String hh = (intHour < 10) ? "0" + intHour : String.valueOf(intHour);
        int intMinute = Calendar.getInstance().get(Calendar.MINUTE);
        String mm = (intMinute < 10) ? "0" + intMinute : String.valueOf(intMinute);
        int intSecond = Calendar.getInstance().get(Calendar.SECOND);
        String ss = (intSecond < 10) ? "0" + intSecond : String.valueOf(intSecond);
        result = day + "-" + month + "-" + year + " " + hh + ":" + mm + ":" + ss;
        return result;
    }

    private String curTime() {
        String result = "";
        String year = String.valueOf(Calendar.getInstance().get(Calendar.YEAR));
        int intMonth = Calendar.getInstance().get(Calendar.MONTH) + 1;
        String month = (intMonth < 10) ? "0" + intMonth : String.valueOf(intMonth);
        int intDay = Calendar.getInstance().get(Calendar.DATE);
        String day = (intDay < 10) ? "0" + intDay : String.valueOf(intDay);
        result += year + "-" + month + "-" + day;
        return result;
    }

    private class SMTPAuthenticator extends javax.mail.Authenticator {
        @Override
        public PasswordAuthentication getPasswordAuthentication() {
            final String username = user;
            final String password = pass;
            return new PasswordAuthentication(username, password);
        }
    }

    public static String priceWithDecimal(double revenue) {
        DecimalFormat formatter = new DecimalFormat("###,###,###,###");
        return formatter.format(revenue).replace(",", ".");
    }

    public static String priceWithDecimal(String revenue) {
        DecimalFormat formatter = new DecimalFormat("###.###.###.###");
        return formatter.format(revenue);
    }

	public String getServlet() {
		return servlet;
	}

	public void setServlet(String servlet) {
		this.servlet = servlet;
	}
}
