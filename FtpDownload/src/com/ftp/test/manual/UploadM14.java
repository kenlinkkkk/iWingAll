package com.ftp.test.manual;

import java.io.File;
import java.text.ParseException;

import org.apache.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.SchedulerException;

import com.ftp.connection.SftpClientImpl;
import com.ftp.entities.FtpServerInfo;
import com.ftp.processor.FTPIntergration;
import com.ftp.utils.XmlConfigs;
import com.ligerdev.appbase.utils.BaseUtils;
import com.ligerdev.appbase.utils.quartz.AbsJob;
import com.ligerdev.appbase.utils.quartz.QuartzUtils;
import com.ligerdev.appbase.utils.textbase.ConfigsReader;
import com.ligerdev.appbase.utils.textbase.Log4jLoader;
import com.ligerdev.appbase.utils.textbase.ReqCountUtils;

public class UploadM14 extends AbsJob {

	private static Logger logger = Logger.getLogger(UploadM14.class);
	private static ReqCountUtils countUtils = ReqCountUtils.getInstance("UploadM14", "M");
	
	private static FTPIntergration ftpClient = null;
	private static FtpServerInfo ftpCfg = null;
	
	public static void main(String[] args) throws SchedulerException, ParseException {
		Log4jLoader.init();
		ConfigsReader.init(XmlConfigs.class);
		
		ftpCfg = XmlConfigs.LIST_CONNECTIONS.get(0);
		ftpClient = new FTPIntergration(ftpCfg.getProtocol());
		
		QuartzUtils quartzUtils = QuartzUtils.getInstance();
		quartzUtils.put(ftpCfg.getTimeExpression(), UploadM14.class);
		quartzUtils.start(); 
	}
	
	@Override
	protected void execute0(JobExecutionContext ctx) {
		String transid = countUtils.countLongStr();
		logger.info(transid + ", ========, scan: " + ftpCfg.getItemMaps().get(0).getLocalPath());

		if (new File(ftpCfg.getItemMaps().get(0).getLocalPath()).exists() == false) {
			logger.info(transid + ", folder local not exist => ignore to upload");
			return;
		}
		if (new File(ftpCfg.getItemMaps().get(0).getLocalPath()).isFile()) {
			logger.info(transid + ", localPath is not folder => ignore to upload");
			return;
		}
		boolean login = ftpClient.connect(
				ftpCfg.getConnectTimeout(), transid,
				ftpCfg.getIp(), ftpCfg.getPort(), ftpCfg.getUser(),
				ftpCfg.getPass(), ftpCfg.getLocalMode(),
				ftpCfg.getRemoteMode(), ftpCfg.getConnectTimeout(),
				ftpCfg.getBufferSize());
		if (!login) {
			logger.info(transid + ", login failed => ignore to upload");
			return;
		}
		try {
			SftpClientImpl client = (SftpClientImpl) ftpClient.getFtpClient();
			File files[] = new File(ftpCfg.getItemMaps().get(0).getLocalPath()).listFiles();

			for (File f : files) {
				if (f.isDirectory()) {
					continue;
				}
				try {
					logger.info(transid + ", prepare upload: " + f.getAbsolutePath());
					String dest = ftpCfg.getItemMaps().get(0).getServerPath() + "/" + f.getName();
					client.upload(transid, 20 * 1000, f.getAbsolutePath(), dest);
					logger.info(transid + ", upload success file: " + f.getName());
				} catch (Exception e) {
					logger.info(transid + ", Exception: " + e.getMessage());
				}
				f.delete();
			}
		} catch (Exception e) {
			logger.info(transid + ", Exception: " + e.getMessage());
		} finally  {
			ftpClient.disconnect(10000, transid);
		}
	}

	@Override
	protected boolean isSingleInstance() {
		return true;
	}
}
