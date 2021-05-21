package com.xxx.aps.processor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Calendar;
import java.util.Date;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.log4j.Logger;

import com.ligerdev.appbase.utils.BaseUtils;
import com.ligerdev.appbase.utils.db.BaseDAO;
import com.ligerdev.appbase.utils.db.XBaseDAO;
import com.ligerdev.appbase.utils.textbase.Log4jLoader;
import com.ligerdev.appbase.utils.textbase.ReqCountUtils;
import com.ligerdev.appbase.utils.textbase.StringGenerator;
import com.ligerdev.appbase.utils.textbase.StringToolUtils;
import com.ligerdev.appbase.utils.threads.AbsProcessor;
import com.ligerdev.appbase.utils.traffic.SendTraffic;
import com.xxx.aps.XmlConfigs;
import com.xxx.aps.logic.db.orm.MtHis;
import com.xxx.aps.utils.AppUtils;

public class LoadBroadcast extends AbsProcessor {

	private static Logger logger = Logger.getLogger(LoadBroadcast.class);
	private static BaseDAO baseDao = BaseDAO.getInstance(BaseDAO.POOL_NAME_MAIN);
	private static ReqCountUtils reqCountUtils = ReqCountUtils.getInstance("LoadSMS", "BRC");
	private static int sleep = 10000;
	
	@Override
	public int sleep() {
		return sleep;
	}

	@Override
	public void execute() throws Exception {
		if(AppUtils.isEnableNode0() == false || baseDao == null){
			return;
		}
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String sql = null;
		
		if(baseDao.getDbType() == BaseDAO.DB_TYPE_MYSQL){
			sql = "select r.* from mt_broadcast2 r where r.status >= 0 and r.send_time <= now()";
		} else {
			sql = "select r.* from mt_broadcast2 r where r.status >= 0 and r.send_time <= sysdate";
		}
		try {
			conn = baseDao.getConnection();
			stmt = conn.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
			rs = stmt.executeQuery();
			
			if(Calendar.getInstance().get(Calendar.SECOND) / (int) (sleep/1000) == 0){
				logger.info("Exec SQL: " + sql);
			}
			int count = 0;
			
			while(rs.first()){
				String msisdn = rs.getString("msisdn");
				msisdn = BaseUtils.formatMsisdn(msisdn, "84", "84"); 
				String content = rs.getString("message");
				// int status = rs.getInt("status"); 
				
				String command = rs.getString("command");
				if(BaseUtils.isBlank(command)){
					command = "MTBRC";
				}  
				String transid = rs.getString("transid");
				if(BaseUtils.isBlank(transid)){
					transid = "Mtbrc" + StringGenerator.randomCharacters(8);
				}
				String shortcode = rs.getString("shortcode");
				if(BaseUtils.isBlank(shortcode)){
					shortcode = String.valueOf(XmlConfigs.shortcode);
				}
				/*int mtType = MtHis.TYPE_TEXT;  
				String transMT = "BRC21";
				try {
					mtType = rs.getInt("mt_type");
					if(mtType == 2) {
						 transMT = "BRC22";
					}
				} catch (Exception e) {
				}*/
				
				MtHis mt = new MtHis(0, msisdn, content, 0, transid, command, new Date(), "SMS");
				mt.setShortcode(shortcode); 
				int rsint = AppUtils.sendSMS1(transid, mt); 
				
				SendTraffic.getInstance("BRC").limitTraffic(5);  
				rs.deleteRow();
				// rs.updateString("MSISDN", "ABC");
				count ++;
			}
			if(count > 0){
				logger.info("Select * from table mt_broadcast2, Found " + count + " rows to send SMS");
			}
		} catch (Exception e) {
			logger.info("Exception: " + e.getMessage() + ", SQL = " + sql, e);
		} finally {
			baseDao.releaseAll(rs, stmt, conn);
		}
	}

	@Override
	public void exception(Throwable e) {
	} 
}
