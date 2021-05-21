package com.xtel.cms.servlets;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.Logger;

import com.ligerdev.appbase.utils.BaseUtils;
import com.ligerdev.appbase.utils.db.BaseDAO;
import com.ligerdev.appbase.utils.db.XBaseDAO;
import com.ligerdev.appbase.utils.textbase.ConfigsReader;
import com.ligerdev.appbase.utils.textbase.Log4jLoader;
import com.ligerdev.cmms.component.dashboard.DashConfigReader;
import com.xtel.cms.utils.XmlConfigs;

public class ContextListener implements ServletContextListener {

	private boolean inited = false;
	private static Object locker = new Object();
	
	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		Log4jLoader.getLogger().info("contextDestroyed ....");
	}

	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		
		Log4jLoader.getLogger().info("contextInitialized ....");
		synchronized (locker) {
			
    		if(inited == false){
    			inited = true;
    			String path = arg0.getServletContext().getInitParameter("path");
            	BaseUtils.setMyDir(path);
            	initApp();
    		}
		}
	}
	
    
	private void initApp() {
		Log4jLoader.init();
		
		ConfigsReader.init(XmlConfigs.class);
		DashConfigReader.initStatic();
		// new UpdateReport().start();
		XBaseDAO xbaseDAO = XBaseDAO.getInstance(BaseDAO.POOL_NAME_MAIN);
		Logger logger = Log4jLoader.getLogger();
		logger.info("======== app started v1.0 =========");
		
		if("ccsp".equalsIgnoreCase(XmlConfigs.vasbase_type) 
				&& xbaseDAO.isValidSql("check", "select noteint5 from subscriber limit 1") == false) {
			
			logger.info("################ plz add cols noteint5... to table subscriber");
			System.exit(-9);
			return;
		}
	}
}
