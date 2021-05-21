package com.xxx.aps.utils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.log4j.Logger;

import com.ligerdev.appbase.utils.BaseUtils;
import com.ligerdev.appbase.utils.db.XBaseDAO;
import com.ligerdev.appbase.utils.textbase.Log4jLoader;
import com.ligerdev.appbase.utils.textbase.StringToolUtils;
import com.xxx.aps.XmlConfigs;
import com.xxx.aps.logic.db.orm.MoHis;
import com.xxx.aps.logic.db.orm.MtHis;

public class AppUtils {
	
	private static CloseableHttpClient httpclient; 

	static {
		try {
			httpclient = com.nip.net.http.HttpUtils.getInstance().getClient(30000);
			// Log4jLoader.getLogger().info("======> create http client");
		} catch (Exception e) {
			Log4jLoader.getLogger().info("Exception: " + e.getMessage());
		}
	}
	
	public static class ActionName {
		public static final String FirstREG = "FirstREG";
		public static final String ReREG = "ReREG";
		public static final String UNREG = "UNREG";
		public static final String RENEW = "RENEW";
		public static final String DELETE = "DELETE";
		public static final String CONTENT = "CONTENT";
		
		// thong ke
		public static final String ACTIVE = "ACTIVE";   // 23h59'59 day N
		public static final String ACTIVE2 = "ACTIVE2"; // 0h day N + 1
		public static final String DTNGAY = "DTNGAY";	// doanh thu ngày N
		public static final String DTTHANG = "DTTHANG"; // doanh thu tháng này   (từ ngày 1 tới hết ngày N)
		public static final String CKLUYKE = "CKLUYKE"; // doanh thu tháng trước (từ ngày 1 tới hết ngày N của tháng trước)
		public static final String CKTUAN = "CKTUAN";  	// cùng kỳ tuần (doanh thu ngày này tuần trước)
		public static final String CKTHANG = "CKTHANG"; // cùng kỳ tháng (doanh thu ngày này tháng trước)
		public static final String TLGH = "TLGH"; 		// tỷ lệ GH
	}
	//---------------------
	
	public static boolean subnote23Increase1() {
		return "cattiensa".equalsIgnoreCase(XmlConfigs.servicename);
	}
	
	public static final int sendSMS1(String transid, MtHis mt) {	
		// save DB và reurn int
		String temp = sendSMS0(transid, mt.getMsisdn(), mt.getContent(), mt.getShortcode());
		int rs = String.valueOf(temp).contains("<return>OK</return>") ? 0 : 1;
		mt.setResult(rs);
		insertMT(transid, mt);
		return rs;
	}
	
	public static final void sendSMS1_delay(final String transid, final MtHis mt, final int seconds) {	 
		// save DB và reurn int
		new Thread(){
			public void run(){
				BaseUtils.sleep(seconds);
				
				String temp = sendSMS0(transid, mt.getMsisdn(), mt.getContent(), mt.getShortcode());
				int rs = String.valueOf(temp).contains("<return>OK</return>") ? 0 : 1;
				mt.setResult(rs);
				insertMT(transid, mt);
			}
		}.start();
	}
	
	public static final String sendSMS2(String transid, MtHis mt) {
		// save db và return nguyên bản String từ CCSP
		String temp = sendSMS0(transid, mt.getMsisdn(), mt.getContent(), mt.getShortcode());
		int rs = String.valueOf(temp).contains("<return>OK</return>") ? 0 : 1;
		mt.setResult(rs);
		insertMT(transid, mt);
		return temp;
	}
	
	public static final String sendSMS0(String transid, String msisdn, String content) {
		return sendSMS0(transid, msisdn, content, null);
	}

	public static final String sendSMS0(String transid, String msisdn, String content, String shortcode) {
	   long l1 = System.currentTimeMillis();
	   String xml = 
			   "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:obj=\"http://object.app.telsoft/\">\n" +
		        "   <soapenv:Header/>\n" +
		        "   <soapenv:Body>\n" +
		        "      <obj:sendMessage>\n" + 
		        "         <ServiceCode>" + XmlConfigs.shortcode +"</ServiceCode>\n" +
		        "         <ISDN>" + msisdn + "</ISDN>\n" +
		        "         <Content>" + content + "</Content>\n" +
		        "         <UseBrandname>" + ("brandname".equalsIgnoreCase(shortcode) ? "1" : "0") + "</UseBrandname>\n" + 
		        "         <User>"+ XmlConfigs.username +"</User>\n" +
		        "         <Password>" + XmlConfigs.password +"</Password>\n" + 
		        "      </obj:sendMessage>\n" +
		        "   </soapenv:Body>\n" +
		        "</soapenv:Envelope>"; 
		        
		Logger logger = Log4jLoader.getLogger();
        HttpPost post = new HttpPost(XmlConfigs.url_send_mt);
        CloseableHttpResponse resp = null;
        try {
            ByteArrayEntity entity = new ByteArrayEntity(xml.getBytes("UTF-8"));
            post.setEntity(entity);
            post.addHeader("Content-Type","text/xml; charset=utf-8");
            post.addHeader("Connection","close");
            post.addHeader("x-ibm-client-id", XmlConfigs.x_ibm_client_id);
            logger.info(transid + ", xmlSendMT: " + xml.replace("\n", " ").replace("\t", " ").replaceAll(" +", " ")); 
            long l2 = System.currentTimeMillis();
            
            resp = httpclient.execute(post);
            String str = BaseUtils.readInputStream(resp.getEntity().getContent()) + ""; 
            long l3 = System.currentTimeMillis();
            
            logger.info(transid + ", xmlRespMT: " + str.replace("\n", " ").replace("\t", " ").replaceAll(" +", " ")
            		+ ", time1: " + BaseUtils.getDurations(l2, l1)
            		+ ", time2: " + BaseUtils.getDurations(l3, l2)
            ); 
            return str; 
            
        } catch (Exception e) {
        	logger.error(transid + ", Send SMS ("+ msisdn +", "+ StringToolUtils.unicode2ASII(content) +") -> CCSP error",e);
        }finally {
            if(resp != null) { 
            	try {
            		resp.close();
            	} catch (Exception e) {}
            }
            post.releaseConnection(); 
        }
        return null;
    }
	
	public static int insertMT(String transid, MtHis mt){ 
		XBaseDAO xbaseDAO = XBaseDAO.getInstance("main");
		Date time = mt.getCreatedTime();
		if(time == null) {
			time = new Date();
		}
		if(XmlConfigs.short_sms_table){
			String table = "mt_" + new SimpleDateFormat("yyyy").format(time);
			return xbaseDAO.insertBean(transid, table, mt);
		} 
		String table = "mt_" + new SimpleDateFormat("yyyyMM").format(time);
		return xbaseDAO.insertBean(transid, table, mt);
	}
	
	public static int insertMO(String transid, MoHis mo){
		XBaseDAO xbaseDAO = XBaseDAO.getInstance("main");
		if(XmlConfigs.short_sms_table){
			String table = "mo_" + new SimpleDateFormat("yyyy").format(new Date());
			return xbaseDAO.insertBean(transid, table, mo);
		} 
		return xbaseDAO.insertBean(transid, mo);
	}
	
	public static String getMondayOfWeek(Date dayOfWeek, String format){ // return yyyyMMdd
		Calendar cal = Calendar.getInstance();
		cal.setTime(dayOfWeek);
		
		for(int i = 0; i < 10; i ++){
			if(cal.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY){
				String yyyyMMdd = new SimpleDateFormat(format).format(cal.getTime());
				return yyyyMMdd;
			}
			cal.add(Calendar.DATE, -1); // lùi 1 ngày
		}
		return "0"; // return để compile ok, thực tế ko bao giờ return 0
	}
	
	public static String getSundayOfWeek(Date dayOfWeek, String format){ // return yyyyMMdd
		Calendar cal = Calendar.getInstance();
		cal.setTime(dayOfWeek);
		
		for(int i = 0; i < 10; i ++){
			if(cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY){
				String yyyyMMdd = new SimpleDateFormat(format).format(cal.getTime());
				return yyyyMMdd;
			}
			cal.add(Calendar.DATE, 1); // tiến 1 ngày
		}
		return "0"; // return để compile ok, thực tế ko bao giờ return 0
	}

	public static boolean isEnableNode0() {
		// > /opt/is_master.cmd
		return new File("/opt/is_master.cmd").exists() || new File("is_master.cmd").exists();
	}
}
