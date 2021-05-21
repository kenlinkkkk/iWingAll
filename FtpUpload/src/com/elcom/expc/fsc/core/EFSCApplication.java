package com.elcom.expc.fsc.core;

import java.io.File;
import java.util.Enumeration;

import org.apache.log4j.Appender;
import org.apache.log4j.AsyncAppender;
import org.apache.log4j.DailyRollingFileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;

import com.elcom.utils.debug.DebugUtil;
import com.elcom.utils.serviceman.ApplicationBase;
import com.elcom.utils.shellcmd.CmdLine;
import com.elcom.utils.shellcmd.CommandConsole;
import com.ligerdev.appbase.utils.db.XBaseDAO;
import com.xtel.uploader.EFSCConfig;
import com.xtel.uploader.EFSCRunner;
import com.xtel.uploader.MainApplication;

public class EFSCApplication extends ApplicationBase {
	
	// main cũ, nếu deploy mới thì dùng main mới: MainApplication, nếu update cho module cũ thì dùng main cũ đỡ phái sửa file shell
	protected void registerCommandLines(CommandConsole shell) {
		super.registerCommandLines(shell);
		try {
			shell.registerCommand("reload", new CmdLine() {
				public String getHelpString() {
					return "reload [config]";
				}

				public void invoke(String name, String param) throws Exception {
					String[] params = param.split(" ");
					for (int i = 0; i < params.length; i++) {
						if (params[i].equalsIgnoreCase("CONFIG")) {
							EFSCApplication.this.getConfig().load();
							DebugUtil.show("Reload config done!");
							if (EFSCApplication.this.logger.isWarnEnabled()) {
								EFSCApplication.this.logger
										.warn("Reload config done!");
							}
						} else {
							DebugUtil.show("Invalid reload item " + params[i]);
							if (EFSCApplication.this.logger.isWarnEnabled()) {
								EFSCApplication.this.logger
										.warn("Invalid reload item "
												+ params[i]);
							}
						}
					}
				}
			});
		} catch (Exception e) {
			DebugUtil.printStackTrace(e);
		}
		try {
			shell.registerCommand("setlog", new CmdLine() {
				public String getHelpString() {
					return "setlog <level> : Set log level";
				}

				public void invoke(String name, String param) throws Exception {
					String[] params = param.split(" ");
					if (params.length == 1) {
						String level = params[0];

						EFSCApplication.this.setLog(level);
					} else {
						DebugUtil.show("Invalid command");
					}
				}
			});
		} catch (Exception e) {
			DebugUtil.printStackTrace(e);
		}
		try {
			shell.registerCommand("showstatus", new CmdLine() {
				public String getHelpString() {
					return "showstatus <true|false> : Show status";
				}

				public void invoke(String name, String param) throws Exception {
					String[] params = param.split(" ");
					if (params.length == 1) {
						boolean detail = Boolean.valueOf(params[0])
								.booleanValue();

						EFSCApplication.this.showStatus(detail);
					} else {
						DebugUtil.show("Invalid command");
					}
				}
			});
		} catch (Exception e) {
			DebugUtil.printStackTrace(e);
		}
	}

	protected static EFSCApplication theinstance = null;
	private final EFSCConfig config;
	private EFSCRunner runner;

	protected void exit() {
		if (this.logger.isInfoEnabled()) {
			this.logger.info("EFSCApplication.exit(): Enter");
		}
		if (this.logger.isInfoEnabled()) {
			this.logger.info("Kill timer...");
		}
		killTimer();

		super.exit();
	}

	public static final synchronized EFSCApplication getInstance() {
		if (theinstance == null) {
			new EFSCApplication("./config/core/efsc.properties");
		}
		return theinstance;
	}

	public static final synchronized EFSCApplication getInstance(
			String configfile) {
		if (theinstance == null) {
			new EFSCApplication(configfile);
		}
		return theinstance;
	}

	public static void main(String[] args) {
		System.out.println("EFSCApplication.main(): call mainHelper ...");
		System.out.println("Working dir:" + System.getProperty("user.dir"));
		mainHelper(getInstance(), "./config/log/log4j.efsc.xml");
		System.out.println("EFSCApplication.main(): exit ....");
	}

	protected int init() throws Exception {
		super.init();
		this.runner = new EFSCRunner("./config/core/efsc.properties");
		this.runner.start();
		this.logger .info("************************ START EFSC SUCCESS " + MainApplication.VERSION + " ***************************");
		
		new Thread(new Runnable() {
			public void run() {
				for (;;) {
					logger.info("Version= 20150921.2, MemInfo: " + MainApplication.getCurrentMemInfo());
					try {
						Thread.sleep(60000 * 20);
					} catch (InterruptedException localInterruptedException) {
					}
				}
			}
		}).start();
		logger.info("================== " + MainApplication.VERSION + " =====================");
		return 0;
	}

	public EFSCApplication(String configfile) {
		this.config = new EFSCConfig(configfile);
		theinstance = this;
		
		try {
			if(new File("config/database.cfg").exists()) {
				XBaseDAO b = XBaseDAO.getInstance("main");
				String sql = "insert into xcdr_uploader (path) values('start_process')";
				b.execSql("", sql);
			}
		} catch (Exception e) {
			e.printStackTrace();
			//logger.info("Exception: " + e.getMessage(), e);
		}
	}

	protected void showStatus(boolean details) {
		super.showStatus(details);
	}

	public boolean setLog(String loglevel) {
		AsyncAppender asa = null;
		DailyRollingFileAppender dfa = null;
		Enumeration e = LogManager.getRootLogger().getAllAppenders();
		
		while (e.hasMoreElements()) {
			Appender currAppender = (Appender) e.nextElement();
			if ((currAppender instanceof AsyncAppender)) {
				asa = (AsyncAppender) currAppender;
			}
		}
		if (asa != null) {
			e = asa.getAllAppenders();
			while (e.hasMoreElements()) {
				Appender currAppender = (Appender) e.nextElement();
				if ((currAppender instanceof DailyRollingFileAppender)) {
					dfa = (DailyRollingFileAppender) currAppender;
				}
			}
			if (dfa != null) {
				if ((loglevel != null) && (!loglevel.equalsIgnoreCase(""))) {
					Level level = Level.toLevel(loglevel);
					dfa.setThreshold(level);
				}
				return true;
			}
			return false;
		}
		return false;
	}

	public EFSCConfig getConfig() {
		return this.config;
	}

	public void reloadConfig() {
		getConfig().load();
	}
}
