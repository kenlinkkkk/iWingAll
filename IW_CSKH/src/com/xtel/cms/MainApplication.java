package com.xtel.cms;

import java.util.Date;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.log4j.Logger;

import com.fss.util.StringUtil;
import com.ligerdev.appbase.utils.BaseUtils;
import com.ligerdev.appbase.utils.cache.CacheSyncFile;
import com.ligerdev.appbase.utils.cache.CacheXLite;
import com.ligerdev.appbase.utils.db.BaseDAO;
import com.ligerdev.appbase.utils.db.XBaseDAO;
import com.ligerdev.appbase.utils.encrypt.Encrypter;
import com.ligerdev.appbase.utils.textbase.Log4jLoader;
import com.ligerdev.appbase.utils.textbase.StringGenerator;
import com.ligerdev.appbase.utils.textbase.StringToolUtils;
import com.vaadin.annotations.Theme;
import com.vaadin.server.CustomizedSystemMessages;
import com.vaadin.server.Page;
import com.vaadin.server.SystemMessages;
import com.vaadin.server.SystemMessagesInfo;
import com.vaadin.server.SystemMessagesProvider;
import com.vaadin.server.ThemeResource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinService;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.UI;
import com.vaadin.ui.Window;
import com.xtel.cms.base.db.orm.UserCms;
import com.xtel.cms.base.gui.UIHomePage;
import com.xtel.cms.base.gui.UILoginStyle1;
import com.xtel.cms.base.gui.UILoginStyle2;
import com.xtel.cms.utils.AppUtils;
import com.xtel.cms.utils.XAuthenUtils;
import com.xtel.cms.utils.XmlConfigs;

//@Title("CMMS")
//@Theme("base")
//@Theme("chameleon")
//@Theme("liferay")
//@Theme("reindeer")
//@Theme("runo")
//@Theme("valo")
@Theme("liger")

public class MainApplication extends UI {
	
	static {
		 // BaseUtils.setMyDir("/media/Data/UWorkspace/Hitech/VaadinCms7/");
	}
	public static Logger logger = Log4jLoader.getLogger();
	public static BaseDAO baseDAO = BaseDAO.getInstance(BaseDAO.POOL_NAME_MAIN);
	public static BaseDAO baseDAO_nolog = BaseDAO.getInstance(BaseDAO.POOL_NAME_MAIN, "nolog");
	public static XBaseDAO xbaseDAO = XBaseDAO.getInstance(BaseDAO.POOL_NAME_MAIN);
	public static XBaseDAO xbaseDAO_nolog = XBaseDAO.getInstance(BaseDAO.POOL_NAME_MAIN, "nolog");
	public static CacheSyncFile cache =  CacheSyncFile.getInstance(1000000);
	
	static {
		baseDAO.setIgnoreMissingCol(true);
		baseDAO.setInsertListIgnore(true);
		baseDAO.setUpdateListIgnore(true); 
		
		baseDAO_nolog.setIgnoreMissingCol(true);
		baseDAO_nolog.setInsertListIgnore(true);
		baseDAO_nolog.setUpdateListIgnore(true); 
		
		xbaseDAO.setIgnoreMissingCol(true);
		xbaseDAO.setInsertListIgnore(true);
		xbaseDAO.setUpdateListIgnore(true); 
		
		xbaseDAO_nolog.setIgnoreMissingCol(true);
		xbaseDAO_nolog.setInsertListIgnore(true);
		xbaseDAO_nolog.setUpdateListIgnore(true); 
	}
	//private static ReqCountUtils reqCountUtils = ReqCountUtils.getInstance("AbsWebApplication", "A");
	private String transid = StringGenerator.randomCharacters(8); 
	private UserCms userBean = null;
	
	public boolean lang_vi = false;
	public VaadinRequest request;
	public MainApplication prevUIHome; // ext

	public MainApplication() {
	}
	
	@Override
	protected void init(VaadinRequest request) {
		// setTheme("liger");
		this.request = request;
		String aath = request.getHeader("aath");
		
		if(AppUtils.isShopeeCare()) {
			lang_vi = true;
		}
		if(aath != null) {
			getSession().setAttribute("aath", aath);
		} else {
			getSession().setAttribute("aath", "");
		}
		if(XmlConfigs.service_name != null) {
			Page.getCurrent().setTitle(XmlConfigs.service_name);
		}
		/*VaadinSession.getCurrent().addRequestHandler(new RequestHandler() {
		    @Override
		    public boolean handleRequest(VaadinSession session,
		                                 VaadinRequest request,
		                                 VaadinResponse response)  throws IOException {
		    	
		    	String transid = MainApplication.this.getTransid();
		    	AppUtils.getHeaders(transid, request, true);
		    	AppUtils.getParameters(transid, request, true);
		    	MainApplication.this.request = request;
		    	return false;
		    }
		});*/
		//Page.getCurrent().setLocation("");
		VaadinService.getCurrent().setSystemMessagesProvider(new SystemMessagesProvider() {
            @Override
            public SystemMessages getSystemMessages(SystemMessagesInfo systemMessagesInfo) {
                CustomizedSystemMessages messages = new CustomizedSystemMessages();
                messages.setSessionExpiredURL("/");
                messages.setSessionExpiredMessage("Phiên hết hạn, vui lòng đăng nhập lại.");
                messages.setSessionExpiredCaption("");
                messages.setSessionExpiredNotificationEnabled(true);
                
                //messages.setCommunicationErrorCaption("");
                //messages.setCommunicationErrorMessage("Mất kết nối với máy chủ, vui lòng đăng nhập lại.");
                //messages.setCommunicationErrorNotificationEnabled(true);
             
                //messages.setCommunicationErrorURL("/");
                //messages.setOutOfSyncURL("/");
                //messages.setAuthenticationErrorURL("/");
                return messages; 
            }
        });
		setLocale(new Locale("vi", "VN")); 
		setSizeFull();
		// getLoadingIndicatorConfiguration().setSecondDelay(30000); 
		logger.info(transid + " ########: user request, IP = " + request.getRemoteHost()); 
		
		if(AppUtils.isShopeeCare()) {
			String sw = request.getParameter("sw");
			if(sw != null && cache.contains(sw)) { // switch user
				UserCms user = (UserCms) cache.getObject(sw);
				setUserBean(user); 
				UIHomePage p = new UIHomePage();
				prevUIHome = (MainApplication) CacheXLite.getObject(sw); // set UI cũ vào, để bên trong class biết để hiện nút back
				p.buildUI();
				setContent(p);
				return;
			}
			String bk = request.getParameter("bk");
			if(bk != null && CacheXLite.contains(bk)) { // back GUI 2 admin
				MainApplication main = (MainApplication) CacheXLite.getObject(bk);
				setUserBean(main.getUserBean()); 
				
				UIHomePage p = new UIHomePage();
				p.buildUI();
				setContent(p);
				return;
			}
		}
		// --------------------------------------------------- tự động login với VAS, tích hợp CSKH
		String userVasMBF = getUserOnURL();
		if(userVasMBF != null && XmlConfigs.CCSPCfg.link_unreg != null) { // cms vas
			String passVasMBF =  getPassOnURL();  
			logger.info(this.getTransid() + ", auto login VAS MBF. CSKHTT, user: " + userVasMBF + ", pass: " + passVasMBF);  
			boolean loginResult = false;
			
			if(BaseUtils.isNotEmpty(passVasMBF) && BaseUtils.isNotEmpty(passVasMBF)) {
				loginResult = this.login(userVasMBF, passVasMBF); 
			}
			if(loginResult){
				//this.removeWindow(this.getWindows().iterator().next());
				return;
			}
		}
		// ------------------------------------------------- login url với xremote
		String userAuthen = XAuthenUtils.getUserAuthen(transid, request);  // ex: http://45.122.253.185:2011/?xuser=vinhpt&screen=shopee_xhome
		if(request.getParameter("xuser") != null && request.getParameter("screen") != null && BaseUtils.isNotBlank(userAuthen)) {
			// login tập trung OK => theo rule của tham số xuser
			String sql = "select * from user_cms where status = 1 and username = ?";
			userBean = xbaseDAO.getBeanBySql(transid, UserCms.class, sql, request.getParameter("xuser"));
			
			if(userBean != null){
				UIHomePage p = new UIHomePage(request.getParameter("screen"));
				p.buildUI();
				setContent(p);
				// this.removeWindow(this.getWindows().iterator().next());
				return;
			}
		}
		//----------------------------------------------------- login thường
		userBean = getUserBean();
		
		if(userBean != null) {
			String screen = (String) getSession().getAttribute("screen");
			UIHomePage homepage = null;
			
			if(screen != null && screen.length() > 0) {
				homepage = new UIHomePage(screen); 
			} else {
				homepage = new UIHomePage(); 
			}
			homepage.buildUI();
			setContent(homepage);
			return;
		}
		showBoxLogin2(request);
	}

	private String getPassOnURL() {
		if( this.getRequest().getParameterMap() == null ||  this.getRequest().getParameterMap().size() == 0) {
			return null;
		}
		try {
			Set<Entry<String, String[]>> set = this.getRequest().getParameterMap().entrySet();
			Iterator<Entry<String, String[]>> i = set.iterator();
			
			while(i.hasNext()) {
				Entry<String, String[]> e = i.next();
				
				if(e.getKey().toLowerCase().equalsIgnoreCase("pass") 
						|| e.getKey().toLowerCase().equalsIgnoreCase("password") 
						|| e.getKey().toLowerCase().equalsIgnoreCase("code")
						|| e.getKey().toLowerCase().equalsIgnoreCase("key")
						|| e.getKey().toLowerCase().equalsIgnoreCase("mk")
						|| e.getKey().toLowerCase().equalsIgnoreCase("p")
				){
					return e.getValue()[0];
				}
			}
		} catch (Exception e) {
		}
		return null;
	}
	
	private String getUserOnURL() {
		if( this.getRequest().getParameterMap() == null ||  this.getRequest().getParameterMap().size() == 0) {
			return null;
		}
		try {
			Set<Entry<String, String[]>> set = this.getRequest().getParameterMap().entrySet();
			Iterator<Entry<String, String[]>> i = set.iterator();
			
			while(i.hasNext()) {
				Entry<String, String[]> e = i.next();
				logger.info(this.getTransid() + ", params: " + e.getKey() + "=" + e.getValue()[0]); 
				
				if(e.getKey().toLowerCase().equalsIgnoreCase("user") 
						|| e.getKey().toLowerCase().equalsIgnoreCase("username")
						|| e.getKey().toLowerCase().equalsIgnoreCase("acc")
						|| e.getKey().toLowerCase().equalsIgnoreCase("account")
						|| e.getKey().toLowerCase().equalsIgnoreCase("tk")
						|| e.getKey().toLowerCase().equalsIgnoreCase("name")
						|| e.getKey().toLowerCase().equalsIgnoreCase("u")
				){
					return e.getValue()[0];
				}
			}
		} catch (Exception e) {
		}
		return null;
	}
 

	public void showBoxLogin1(VaadinRequest request) {
		UILoginStyle1 frmLogin = new UILoginStyle1();
		setContent(frmLogin);
	}
	
	public void showBoxLogin2(VaadinRequest request) {
		UILoginStyle2 frmLogin = new UILoginStyle2();
		Window mainWindow = new Window("Login", frmLogin);
		// mainWindow.setSizeFull();
		mainWindow.setDraggable(true);
		mainWindow.setClosable(false);
		//mainWindow.setPositionX(400); 
		//mainWindow.setPositionY(100); 
		//mainWindow.center();
		
		mainWindow.setModal(true);
		mainWindow.setIcon(new ThemeResource("icons/16/user.png"));
		mainWindow.setWidth("11cm");
		mainWindow.setHeight("5cm");
		addWindow(mainWindow);
	}
	
	public static void main(String[] args) throws Exception {
		// 9f12ed29689a12c9be0285a07caac4aa = 3123fb2j3f		hien.cskh/3123fb2j3f
		// 60e8f4fa803018f8363ce1c5ea2e50a2 = 124vg2131			vinh.pham/124vg2131
		// fcc067720baecc2d58ee149b3f4202c8 = 123123aA@			duc/123123aA@	
		// cbc7b5527a2ff7caa141faaa18ddad3f = 23t23523d			phuong.cskh/23t23523d
		// 852dff71d2958e27252e4fbedf70ac8b = 12u4g1u25			hien/12u4g1u25
		// 852dff71d2958e27252e4fbedf70ac8b = 12u4g1u25			minh.nguyen/12u4g1u25
		// 8f8464d8e03bf86788eb07c16e8dd42f = 12u4g1u25			hoa/13tr124s2
		// c42fcab509de5243346e6ffdab9d1ddd = thuyan@123		thuyan/thuyan@123
	}
	
	public boolean login(String user, String pass) {
		/*if(BaseUtils.isBlank(user) && new java.io.File("test.autologin").exists()){ 
			user = "admin";
			pass = "Conmacon3110";
		}*/
		logger.info(transid + ", User submited account: " + user + "/" + pass);
		
		if(XmlConfigs.list_acc_config != null && XmlConfigs.list_acc_config.size() > 0) {
			for(UserCms bean : XmlConfigs.list_acc_config) {
				String passMd5 = Encrypter.encodeMD5_F32("CMS", pass);
				
				if(bean.getPassword().equalsIgnoreCase(passMd5) && bean.getUsername().equalsIgnoreCase(user)) {
					userBean = bean;
					break;
				}
				if(bean.getPassword().equals(pass) && bean.getUsername().equalsIgnoreCase(user)) {
					// chấp nhận config ko mã hóa
					userBean = bean;
					break;
				} 
			}
		} else {
			String pass2 = pass;
			try { pass2 = StringUtil.encrypt(pass2, "SHA"); } catch (Exception e) {}  
			String sql = "select * from user_cms where status = 1 and username = ? and (password = ? or password = ?)";
			userBean = xbaseDAO.getBeanBySql(transid, UserCms.class, sql, user, pass, pass2);
			
			if (userBean != null) {
				sql = "update user_cms set lastlogin = now() where username = '" + user + "'";
				xbaseDAO.execSql(pass2, sql);
				userBean.setLastlogin(new Date()); 
			}
		}
		if (userBean != null) {
			logger.info(transid + ", login success: " + String.valueOf(userBean));
			setUserBean(userBean); 
			UIHomePage homepage = new UIHomePage();
			homepage.buildUI();
			setContent(homepage);
			return true;
		} else {
			Notification.show("Username or password is incorrect", Type.WARNING_MESSAGE);
		}
		logger.info(transid + ", login fail");
		return false;
	}
	
	private Thread thread = Thread.currentThread();
	
	public synchronized String getTransid(){
		if(thread == Thread.currentThread()) {
			return transid;
		}
		// khác thread
		transid = getUserStr() + "@" + StringToolUtils.randomCharacters(5);
		thread = Thread.currentThread();
		logger.info(transid + ", #################### IP: " + request.getRemoteAddr()); 
		return transid;
	}
	
	public void test(){
	}
	
	public void setUserBean(UserCms userBean) {
		this.userBean = userBean;
		if(AppUtils.isShopeeCare()) { 
			// refresh URL là bắt login lại, do dv này muốn login dc nhiều tk song song trên cùng 1 trình duyệt => ko sử dụng userbean trong session
			return; 
		}
		this.getSession().setAttribute("vaadinUser", userBean); 
	}
	
	public UserCms getUserBean() {
		if(userBean != null) {
			return userBean;
		}
		if(AppUtils.isShopeeCare()) { 
			// refresh URL là bắt login lại, do dv này muốn login dc nhiều tk song song trên cùng 1 trình duyệt => ko sử dụng userbean trong session
			return null; 
		}
		return (UserCms) getSession().getAttribute("vaadinUser");
		
	}
	
	public String getUserStr() {
		return userBean == null ? "?" : userBean.getUsername();
	}
	
	public String getFullname() {
		return userBean == null ? "?" : userBean.getFullname();
	}

	public VaadinRequest getRequest() {
		return request;
	}

	public void setRequest(VaadinRequest request) {
		this.request = request;
	}
	
}
