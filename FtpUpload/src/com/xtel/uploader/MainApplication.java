package com.xtel.uploader;

import java.io.File;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

import com.ligerdev.appbase.utils.db.XBaseDAO;

public class MainApplication {
	
	private static Logger logger;
	public static String VERSION = "v2018.06";

	public static void main(String[] args) {
		
		DOMConfigurator.configureAndWatch("./config/log/log4j.efsc.xml");
		logger = Logger.getLogger(MainApplication.class);
		EFSCRunner runner = new EFSCRunner("./config/core/efsc.properties");
		runner.start();

		new Thread(new Runnable() {
			public void run() {
				for (;;) {
					MainApplication.logger.info("Version= 20150921.2, MemInfo: " + MainApplication.getCurrentMemInfo());
					try {
						Thread.sleep(60000 * 20);
					} catch (InterruptedException localInterruptedException) {
					}
				}
			}
		}).start();
		logger.info("************************ START EFSC SUCCESS " + VERSION + "  ***************************");
		logger.info("================== " + VERSION + " =====================");
		try {
			if(new File("config/database.cfg").exists()) {
				XBaseDAO b = XBaseDAO.getInstance("main");
				String sql = "insert into xcdr_uploader (path) values('start_process')";
				b.execSql("", sql);
			}
		} catch (Exception e) {
			//e.printStackTrace();
			logger.info("Exception: " + e.getMessage(), e);
		}
	}

	public static String getCurrentMemInfo() {
		int mb = 1048576;
		Runtime runtime = Runtime.getRuntime();

		int use = Math.round((float) (runtime.totalMemory() - runtime.freeMemory()) / mb);
		int free = Math.round((float) runtime.freeMemory() / mb);
		int max = Math.round((float) runtime.maxMemory() / mb);

		return use + "/" + free + "/" + max;
	}
}
