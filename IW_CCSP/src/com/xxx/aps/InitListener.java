package com.xxx.aps;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.Logger;

import com.ligerdev.appbase.utils.BaseUtils;
import com.ligerdev.appbase.utils.db.BaseDAO;
import com.ligerdev.appbase.utils.db.XBaseDAO;
import com.ligerdev.appbase.utils.http.HttpClientUtils;
import com.ligerdev.appbase.utils.quartz.QuartzUtils;
import com.ligerdev.appbase.utils.textbase.ConfigsReader;
import com.ligerdev.appbase.utils.textbase.Log4jLoader;
import com.xxx.aps.processor.CreateTables;
import com.xxx.aps.processor.ExecuteSQL;
import com.xxx.aps.processor.LoadBroadcast;
import com.xxx.aps.processor.MoveCdr2SubFolder;
import com.xxx.aps.processor.SaveActionHis;
import com.xxx.aps.processor.ScanCdrCCSP;
import com.xxx.aps.processor.StatisticActive;
import com.xxx.aps.processor.UpdateStatsRsCode;

/**
 * Application Lifecycle Listener implementation class InitListener
 *
 */
public class InitListener implements ServletContextListener {

    /**
     * Default constructor. 
     */
    public InitListener() {
        // TODO Auto-generated constructor stub
    }

	/**
     * @see ServletContextListener#contextDestroyed(ServletContextEvent)
     */
    public void contextDestroyed(ServletContextEvent arg0)  { 
         // TODO Auto-generated method stub
    }

	/**
     * @see ServletContextListener#contextInitialized(ServletContextEvent)
     */
    private QuartzUtils quartzUtils = QuartzUtils.getInstance(); 
    private XBaseDAO xbaseDAO = XBaseDAO.getInstance(BaseDAO.POOL_NAME_MAIN);
    protected static Logger logger = Log4jLoader.getLogger();
    
    public void contextInitialized(ServletContextEvent arg0)  { 
    	Log4jLoader.init();
    	ConfigsReader.init(XmlConfigs.class);
    	System.out.println(1);
    	
    	new ExecuteSQL().start();
    	new SaveActionHis().start();
    	System.out.println(2);
    	
    	CreateTables.getInstance().start();
    	new ScanCdrCCSP().start();
    	
    	System.out.println(3);
    	new UpdateStatsRsCode().start();
    	
    	System.out.println(4);
    	new LoadBroadcast().start(); 
    	new MoveCdr2SubFolder().start();
    	quartzUtils.put("59 59 23 * * ?", StatisticActive.class);  
    	try {
			quartzUtils.start();
		} catch (Exception e) {
		}
    	System.out.println(5);
    	
    	if(xbaseDAO.isValidSql("check", "select noteint5 from subscriber limit 1") == false) { 
			logger.info("################ plz add cols noteint5... to table subscriber");
			System.exit(-9);
			return;
		}
    	new Thread() {
    		public void run() {
    			BaseUtils.sleep(5000); 
    			String s = null;
    			System.out.println(5);
    			
    			s = HttpClientUtils.getDefault("initjsp", "http://localhost:8888/ccsp/buildconfirm.jsp?compile", 6000);
    			Log4jLoader.getLogger().info("initjsp buildconfirm: " + s);
    			
    			s = HttpClientUtils.getDefault("initjsp", "http://localhost:8888/ccsp/checksub.jsp?compile", 6000); 
    			Log4jLoader.getLogger().info("initjsp checksub: " + s);
    			
    			s = HttpClientUtils.getDefault("initjsp", "http://localhost:8888/ccsp/forwardMessage.jsp?compile", 6000);
    			Log4jLoader.getLogger().info("initjsp forwardMessage: " + s);
    			
    			s = HttpClientUtils.getDefault("initjsp", "http://localhost:8888/ccsp/headers.jsp?compile", 6000);
    			Log4jLoader.getLogger().info("initjsp headers: " + s);
    			
    			s = HttpClientUtils.getDefault("initjsp", "http://localhost:8888/ccsp/sendsms.jsp?compile", 6000);
    			Log4jLoader.getLogger().info("initjsp sendsms: " + s);
    			
    			s = HttpClientUtils.getDefault("initjsp", "http://localhost:8888/ccsp/sendSmsBox.jsp?compile", 6000);
    			Log4jLoader.getLogger().info("initjsp sendSmsBox: " + s);
    			
    			s = HttpClientUtils.getDefault("initjsp", "http://localhost:8888/ccsp/unregister.jsp?compile", 6000);
    			Log4jLoader.getLogger().info("initjsp unregister: " + s);
    			
    			s = HttpClientUtils.getDefault("initjsp", "http://localhost:8888/ccsp/updateMT3Day.jsp?compile", 6000);
    			Log4jLoader.getLogger().info("initjsp updateMT3Day: " + s);
    			
    			s = HttpClientUtils.getDefault("initjsp", "http://localhost:8888/ccsp/updatePackage.jsp?compile", 6000);
    			Log4jLoader.getLogger().info("initjsp updatePackage: " + s);
    			
    			s = HttpClientUtils.getDefault("initjsp", "http://localhost:8888/ccsp/xlogin.jsp?compile", 6000);
    			Log4jLoader.getLogger().info("initjsp xlogin: " + s);
    		}; 
    	}.start(); 
    	
    	System.out.println(6);
    	Log4jLoader.getLogger().info("================== v1.20200619 started ==================="); 
    }
	
}
