package com.ftp.processor;

import java.util.Calendar;
import java.util.Date;

import org.apache.log4j.Logger;
import org.quartz.JobExecutionContext;

import com.ftp.MainApplication;
import com.ftp.entities.FtpServerInfo;
import com.ftp.entities.ItemMap;
import com.ftp.utils.XmlConfigs;
import com.ligerdev.appbase.utils.BaseUtils;
import com.ligerdev.appbase.utils.quartz.AbsJob;
import com.ligerdev.appbase.utils.textbase.Log4jLoader;
import com.ligerdev.appbase.utils.textbase.ReqCountUtils;

public class JobScheduler extends AbsJob {

	private static Logger logger = Logger.getLogger(JobScheduler.class);
	private static ReqCountUtils reqCountUtils = ReqCountUtils.getInstance("ScanSchedule", "A");
	
	@Override
	protected void execute0(JobExecutionContext ctx) {
		try {
			int index = (Integer) ctx.getMergedJobDataMap().get("index");
			FtpServerInfo con = XmlConfigs.LIST_CONNECTIONS.get(index);
			
			MainApplication mainApp = (MainApplication) ctx.getMergedJobDataMap().get("mainApp");
			FTPIntergration ftpClientUtils = (FTPIntergration) ctx.getMergedJobDataMap().get("ftpClientUtils");
			process(con, mainApp, ftpClientUtils);
			
		} catch (Throwable e) {
			logger.info("Exception: " + e.getMessage(), e);
		}
	}
	
	private void process(FtpServerInfo ftpCfg, MainApplication mainApp, FTPIntergration ftpClientUtils) throws Exception { 
		String transid = reqCountUtils.countLongStr();
		logger.info(transid + ", ######## JobExp = " + ftpCfg.getTimeExpression() + " is executed ...");
		
		if(ftpCfg.getItemMaps() == null && ftpCfg.getItemMaps().size() == 0){
			logger.info(transid + ", itemMap size = 0, ignore to download");
			return;
		}
		if(!MainApplication.CONTINUES){
			logger.info(transid + ", application is shutting down => ignore to download");
			return;
		}
		if(!ftpClientUtils.connectionOK(1000, transid)){
			boolean login = ftpClientUtils.connect(ftpCfg.getConnectTimeout(), transid, ftpCfg.getIp(), ftpCfg.getPort(), ftpCfg.getUser(),
					ftpCfg.getPass(), ftpCfg.getLocalMode(), ftpCfg.getRemoteMode(), ftpCfg.getConnectTimeout(), ftpCfg.getBufferSize());
			if(!login){
				logger.info(transid + ", login failed => ignore to download");
				return;
			}  
		}  else {
			logger.info(transid + ", KEEP CONNECTION,  ip = " + ftpCfg.getIp() + ", port = " + ftpCfg.getPort()
					+ ", user = " + ftpCfg.getUser() + ", pass = " + ftpCfg.getPass());
		}
		int downloadedCount = 0;
		try {
			for(int i = 0 ; i < ftpCfg.getItemMaps().size() ; i ++){
				ItemMap itemMap = ftpCfg.getItemMaps().get(i);
				Date today = new Date();
				
				for(int k = itemMap.getCheckPrevDate(); k >= 0; k-- ){
					// scan lùi x ngày tới ngày hiện tại => tránh trường hợp client đã nhảy ngày mà server vẫn là ngày cũ
					
					Date datePrev = BaseUtils.addTime(today, Calendar.DATE, -k);
					String dateFolder = BaseUtils.formatTime(itemMap.getFolderDateFormat(), datePrev); 
					dateFolder = dateFolder == null ? "" : dateFolder;
					String transidTmp = transid + "-" + itemMap.getLogName() + "-" + i + "-" + k;
					downloadedCount += ftpClientUtils.download(transidTmp, itemMap, dateFolder, ftpCfg);
				}
			}
		} catch (Exception e) {
			logger.info(transid + ", Exception: " + e.getMessage(), e);
			BaseUtils.sleep(1000 * 10);
		} finally {
			if(ftpCfg.isKeepConnection() == false){
				ftpClientUtils.disconnect(10000, transid);
			}
		}
		logger.info(transid + ", JobScheduler process done. downloaded count = " + downloadedCount + "\n");
	}

	@Override
	protected boolean isSingleInstance() {
		return true;
	}
}
