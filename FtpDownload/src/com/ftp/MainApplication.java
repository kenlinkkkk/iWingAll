package com.ftp;

import java.io.File;
import java.text.ParseException;

import org.apache.log4j.Logger;
import org.quartz.SchedulerException;

import com.ftp.entities.FtpServerInfo;
import com.ftp.processor.FTPIntergration;
import com.ftp.processor.JobScheduler;
import com.ftp.utils.XmlConfigs;
import com.ligerdev.appbase.utils.BaseUtils;
import com.ligerdev.appbase.utils.CheckAlivePort;
import com.ligerdev.appbase.utils.quartz.JobValues;
import com.ligerdev.appbase.utils.quartz.QuartzUtils;
import com.ligerdev.appbase.utils.textbase.ConfigsReader;
import com.ligerdev.appbase.utils.threads.AbsApplication;

public class MainApplication extends AbsApplication {
	
	protected MainApplication(Class<? extends ConfigsReader> clazzConfig) {
		super(clazzConfig);
	}

	private static Logger logger = Logger.getLogger(MainApplication.class);
	public static boolean CONTINUES = true;
	private QuartzUtils quartzUtils = QuartzUtils.getInstance();
	
	@Override
	protected void initProcess() throws Exception {
		new CheckAlivePort(XmlConfigs.CHECK_INSTANCE_PORT).start();
		
		for(int i = 0 ; i < XmlConfigs.LIST_CONNECTIONS.size(); i ++){
			FtpServerInfo con = XmlConfigs.LIST_CONNECTIONS.get(i);
			FTPIntergration ftpClientUtils = new FTPIntergration(con.getProtocol());
			
			quartzUtils.put(
					con.getTimeExpression(), JobScheduler.class, 
					new JobValues("index", i), 
					new JobValues("mainApp", this), 
					new JobValues("ftpClientUtils", ftpClientUtils));
		}
		quartzUtils.start();
		
		VERSION = "1.4";
		startThreadLog();
		checkToStopModule();
	}
	
	private void checkToStopModule() {
		Thread th = new Thread() {
			public void run() {
				while (CONTINUES) {
					
					BaseUtils.sleep(2000);
					 File file = new File("stop");
					 if(file.exists()){
						 
						 file.delete();
						 logger.info("prepare stop module ...");
						 endProcess();
						 try {
							quartzUtils.stop();
						} catch (Exception e) {
							logger.info("Excepion: " + e.getMessage());
						} 
						 System.exit(-9);
					 }
				}
			};
		};
		th.start();
	}
	
	private void startThreadLog() {
		Thread th = new Thread() {
			public void run() {
				while (CONTINUES) {
					logger.info("Version: " + VERSION + ", JvmMem: " + BaseUtils.getCurrentMemInfo());
					try {
						Thread.sleep(1000 * 60);
					} catch (Exception e) {
						logger.info("Excepion: " + e.getMessage());
					}
				}
			};
		};
		th.start();
	}
	
	public static void main(String[] args) {
		new MainApplication(XmlConfigs.class).start();
	}
	
	public void endProcess() {
		CONTINUES = false;
		
		while(quartzUtils.hasInstanceRunning()){
			String str = "Have instance running => wait some seconds to finish";
			logger.info(str);
			System.out.println(str);
			try {
				Thread.sleep(1000);
			} catch (Exception e) {
			}
		}
		String str = "======= Exited =======";
		System.out.println(str);
		logger.info(str);
	}

	public QuartzUtils getQuartzUtils() {
		return quartzUtils;
	}

	public void setQuartzUtils(QuartzUtils quartzUtils) {
		this.quartzUtils = quartzUtils;
	}
}
