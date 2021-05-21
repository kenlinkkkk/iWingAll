package com.xtel.cms.base.gui;

import org.apache.log4j.Logger;

import com.fss.util.StringUtil;
import com.ligerdev.appbase.utils.BaseUtils;
import com.ligerdev.appbase.utils.db.BaseDAO;
import com.ligerdev.appbase.utils.db.XBaseDAO;
import com.ligerdev.appbase.utils.textbase.Log4jLoader;
import com.ligerdev.cmms.component.IMenuCfg;
import com.ligerdev.cmms.component.IMenuClickListener;
import com.ligerdev.cmms.component.changepass.IChangePassListener;
import com.ligerdev.cmms.component.changepass.LDChangePass;
import com.ligerdev.cmms.component.dashboard.LDDashboard;
import com.ligerdev.cmms.component.menuleft.LDMenuLeft;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.xtel.cms.MainApplication;
import com.xtel.cms.base.db.orm.UserCms;
import com.xtel.cms.ccsp.form.others.FormCCSPTableUserCms;
import com.xtel.cms.ccsp.form.others.FormCMSLayoutThongKeDanhGiaHieuQuaCTKM;
import com.xtel.cms.ccsp.form.report.FormCCSPLayoutReport;
import com.xtel.cms.ccsp.form.subs_info.FormCCSPLayoutSubsInfo;
import com.xtel.cms.utils.AppUtils;
import com.xtel.cms.utils.XDataBean;
import com.xtel.cms.utils.XmlConfigs;
import com.xtel.cms.utils.XmlConfigs.CCSPCfg;

public class UIHomePage extends VerticalLayout implements IMenuClickListener, IChangePassListener {
	
	private static Logger logger = Log4jLoader.getLogger();
	private MainApplication mainApp = (MainApplication) MainApplication.getCurrent();
	
	private static BaseDAO baseDAO = BaseDAO.getInstance(BaseDAO.POOL_NAME_MAIN);
	private static XBaseDAO xbaseDAO = XBaseDAO.getInstance(BaseDAO.POOL_NAME_MAIN);
	
	private Panel pnlMainDisplay = null;
	private LDDashboard dashboard = null;
	
	static {
		baseDAO.setIgnoreMissingCol(true);
		xbaseDAO.setIgnoreMissingCol(true);
	}
	
	public static class LDCommand { 
		// base
		public static final String CHANGE_PASS = "dmk";
		public static final String GO_HOME = "tc";
		public static final String EXIT = "exit";
		
		// ------- ccsp/vasbase
		public static final String CCSP_ACC = "ccsp_acc";
		public static final String CCSP_CSKH = "ccsp_cskh";
		public static final String CCSP_REPORT = "ccsp_report";
		public static final String VASBASE_CTKM = "vasbase_ctkm";
	}
	
	public String defaultScreen;
	
	public UIHomePage() {
	}
	
	public UIHomePage(String defaultScreen) {
		this.defaultScreen = defaultScreen;
	}
	
	public void buildUI() {
		// setMargin(false);
		 setSizeFull();
		// setHeight("100%");
		// setHeightUndefined();

		 // init banner
		CssLayout banner = new CssLayout();
		banner.setStyleName("toolbar");
		banner.setWidth("100%");
		banner.setHeight("76px");
		CustomLayout custom = new CustomLayout("banner");
		// custom.setSizeFull();
		banner.addComponent(custom);
		addComponent(banner);
		
		// init menu left
		LDMenuLeft menubarLeft = new LDMenuLeft(this);
		menubarLeft.setHeight("6mm");
		
		// init menu right
		MenuBar menubarRight = new MenuBar();
		menubarRight.setStyleName("menubarRight");
		menubarRight.addItem("Xin chào: " + mainApp.getFullname(), null);
		menubarRight.setSizeFull();
		menubarRight.setHeight("6mm");
		
		// init menubar
		HorizontalLayout hLayout = new HorizontalLayout();
		hLayout.setWidth("100%");
		hLayout.setHeight("6mm");
		hLayout.addComponent(menubarLeft);
		hLayout.addComponent(menubarRight);
		// hLayout.setExpandRatio(menubarRight, 1.0F);
		    
		addComponent(hLayout);
		
		// init panel dashboard
		pnlMainDisplay = new Panel();
		pnlMainDisplay.setSizeFull();
		// pnlMainDisplay.setHeight("15cm");
		// pnlMainDisplay.setHeightUndefined();
		addComponent(pnlMainDisplay);
		// setComponentAlignment(pnlMainDisplay, Alignment.TOP_CENTER);
		setExpandRatio(pnlMainDisplay, 1.0F);
		dashboard = new LDDashboard(this);
		pnlMainDisplay.setContent(dashboard);
		
		HorizontalLayout layoutBottom = new HorizontalLayout();
		layoutBottom.setSizeFull();
		layoutBottom.setHeight("4mm");
		
		CustomLayout copyRight = new CustomLayout("footer"); 
		copyRight.setSizeUndefined();
		layoutBottom.addComponent(copyRight);
		layoutBottom.setComponentAlignment(copyRight, Alignment.MIDDLE_CENTER);
		
		addComponent(layoutBottom);
		setComponentAlignment(layoutBottom, Alignment.BOTTOM_CENTER);
		
		if(defaultScreen != null) { // giả lập click để vào luôn cate cần vào
			menuClicked(null, defaultScreen); 
		}
	}
	
	@Override
	public boolean menuItemEnable(IMenuCfg menuCfg) {
		// hàm này sẽ quyết định acc hiện tại: mỗi menu/dashboad có quyền hay không 
		if(BaseUtils.isBlank(menuCfg.getValue())){
			return true;
		}
		String cfg = menuCfg.getValue().toLowerCase().replace(",", ";").replace(" ", "");
		String temp[] = cfg.split(";");
		//logger.info(mainApp.getTransid() + ", menuItemEnable = " + mainApp.getUserBean().getRolename() + ", cfg: " + cfg + ", menuCfg: " + menuCfg.getPage());
				
		for(String s: temp) {
			if(BaseUtils.isBlank(s)) {
				continue;
			}
			if(mainApp.getUserBean().getRolename().toLowerCase().contains(";" + s + ";")
				|| mainApp.getUserBean().getRolename().equalsIgnoreCase(s) 
			) {
				return true;
			}
		}
		return false;
	}

	@Override
	public <T>void menuClicked(T selectedItem, String eventName) {
		logger.info(mainApp.getTransid() + ", click menu/dashboard eventName = " + eventName
			+ ", eventClass = " + (selectedItem == null ? null : selectedItem.getClass().getName())
			+ ", acc: " + mainApp.getUserBean().getUsername());
		mainApp.getSession().setAttribute("screen", eventName);
		
		try {
			if(LDCommand.GO_HOME.equals(eventName)){
				pnlMainDisplay.setContent(dashboard);
				return;
			}
			if(LDCommand.CHANGE_PASS.equals(eventName)){
				mainApp.addWindow(new LDChangePass(this));
				return;
			}
			if(LDCommand.EXIT.equals(eventName)){
				mainApp.setUserBean(null);
				removeAllComponents();
				mainApp.showBoxLogin2(null);
				return;
			}
			if(LDCommand.CCSP_ACC.equals(eventName)){
				FormCCSPTableUserCms p = new FormCCSPTableUserCms();
				p.getPage(1, true, null); 
				pnlMainDisplay.setContent(p); 
				return;
			} 
			if(LDCommand.VASBASE_CTKM.equals(eventName)){
				FormCMSLayoutThongKeDanhGiaHieuQuaCTKM p = new FormCMSLayoutThongKeDanhGiaHieuQuaCTKM();
				pnlMainDisplay.setContent(p); 
				return;
			} 
			if(LDCommand.CCSP_CSKH.equals(eventName)){
				String serviceName = AppUtils.isAmobService() ? "AMOBI" : XmlConfigs.service_name.toUpperCase();
				boolean isCCsp = "ccsp".equalsIgnoreCase(XmlConfigs.vasbase_type);
				XDataBean xdata = new XDataBean(baseDAO, xbaseDAO, serviceName, CCSPCfg.link_unreg, isCCsp);
				xdata.timeLaunching = XmlConfigs.time_launching;
				FormCCSPLayoutSubsInfo form = new FormCCSPLayoutSubsInfo(xdata);
				pnlMainDisplay.setContent(form);
				return; 
			} 
			if(LDCommand.CCSP_REPORT.equals(eventName)){
				String serviceName = AppUtils.isAmobService() ? "AMOBI" : XmlConfigs.service_name.toUpperCase();
				boolean isCCsp = "ccsp".equalsIgnoreCase(XmlConfigs.vasbase_type);
				XDataBean xdata = new XDataBean(baseDAO, xbaseDAO, serviceName, CCSPCfg.link_unreg, isCCsp);
				xdata.timeLaunching = XmlConfigs.time_launching;
				pnlMainDisplay.setContent(new FormCCSPLayoutReport(xdata));
				return; 
			} 
		} catch (Exception e) {
			logger.info(mainApp.getTransid() + ", Exception: " + e.getMessage(), e); 
		}
		mainApp.getSession().setAttribute("screen", ""); 
		logger.info(mainApp.getTransid() + ", wrong pass ...."); 
	}

	@Override
	public String change(String oldPass, String newPass) {
		String sql = "select * from user_cms where status = 1 and username = ? and (password = ? or password = ?)";
		UserCms userBean = null;
		String oldPass2 = oldPass;
		try { 
			oldPass2 = StringUtil.encrypt(oldPass2, "SHA");
		} catch (Exception e) {
		} 
		try {
			userBean = baseDAO.getBeanBySql(mainApp.getTransid(), UserCms.class, sql, mainApp.getUserBean().getUsername(), oldPass, oldPass2);
		} catch (Exception e) {
		}
		if(userBean == null){
			return "Old password is wrong! please try again!";
		}
		if(newPass == null || newPass.length() == 0){
			return "New password is not blank, please reenter!";
		}
		sql = "update user_cms set password = ? where id = " + mainApp.getUserBean().getId();
		if(XmlConfigs.CCSPCfg.link_unreg != null) {
			// CMS cho dv vas chạy trên CCSP => mã hóa theo kiểu data cũ
			try { 
				newPass = StringUtil.encrypt(newPass, "SHA");
			} catch (Exception e) {
			} 
		} 
		try {
			baseDAO.execSql(mainApp.getTransid(), sql, newPass);
			mainApp.getUserBean().setPassword(newPass);
			return "OK";
			
		} catch (Exception e) {
			return "Update fail, please try again later!";
		}
	}
 
}
