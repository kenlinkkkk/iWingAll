package com.xtel.cms.ccsp.form.subs_info;

import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.ibm.icu.util.Calendar;
import com.ligerdev.appbase.utils.BaseUtils;
import com.ligerdev.appbase.utils.CounterLongUtils;
import com.ligerdev.appbase.utils.db.BaseDAO;
import com.ligerdev.appbase.utils.db.XBaseDAO;
import com.ligerdev.appbase.utils.entities.PairStringLong;
import com.ligerdev.appbase.utils.textbase.Log4jLoader;
import com.ligerdev.cmms.component.AppCmmsUtils;
import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.DateField;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TabSheet.SelectedTabChangeEvent;
import com.vaadin.ui.TabSheet.SelectedTabChangeListener;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.xtel.cms.MainApplication;
import com.xtel.cms.ccsp.db.orm.CCSPActionHis;
import com.xtel.cms.ccsp.db.orm.CCSPMoHis;
import com.xtel.cms.ccsp.db.orm.CCSPMtHis;
import com.xtel.cms.ccsp.db.orm.CCSPPkgPolicy;
import com.xtel.cms.ccsp.db.orm.CCSPSms;
import com.xtel.cms.ccsp.db.orm.CCSPSubscriber;
import com.xtel.cms.ccsp.utils.excel.CCSP_ExcelCreateSheet_ActionHis;
import com.xtel.cms.ccsp.utils.excel.CCSP_ExcelCreateSheet_SMS;
import com.xtel.cms.ccsp.utils.excel.CCSP_ExcelCreateSheet_SubInfo;
import com.xtel.cms.utils.AppUtils;
import com.xtel.cms.utils.XDataBean;

public class FormCCSPLayoutSubsInfo extends VerticalLayout {

	protected static Logger logger = Log4jLoader.getLogger();
	protected MainApplication mainApp = (MainApplication) MainApplication.getCurrent();
	
	public TextField msisdnBox;
	public TextField keyword;
	public DateField from;
	public DateField to;
	
	protected BaseDAO baseDAO = null;
	protected XBaseDAO xbaseDAO = null;
	private String serviceName = null;
	private String urlUnregTemplate = null;
	private XDataBean xdata;
	
	private FormCCSPLayoutSubsInfo() {
	}
	
	public FormCCSPLayoutSubsInfo(XDataBean xdata) {
		logger.info(mainApp.getTransid() + ", handler class: " + this.getClass().getName() + ", serviceName: " + xdata.serviceName); 
		setSizeUndefined();
		//setSizeFull();
		//setImmediate(true);
		
		this.xdata = xdata;
		this.baseDAO = xdata.baseDAO;
		this.xbaseDAO = xdata.xbaseDAO;
		this.serviceName = xdata.serviceName;
		this.urlUnregTemplate = xdata.urlUnregTemplate;
		
		// -------------- pannel time, msisdn, button search ...
		//from = AppCmmsUtils.createDateField("From", false, Resolution.DAY);
		//from.setValue(new Date()); 
		
		//to = AppCmmsUtils.createDateField("To", false, Resolution.DAY);
		//to.setValue(new Date()); 
		
		//msisdnBox = AppCmmsUtils.createTextField("Msisdn", "909090529", true);
		HorizontalLayout h1 = new HorizontalLayout();
		h1.setSpacing(true);
		//h1.setSizeUndefined(); 
		//h1.setHeightUndefined();
		//h1.setImmediate(true); 
		//h1.setHeight("2cm");
		
		h1.addComponent(AppCmmsUtils.createLabel("", "1cm"));
		// Label labelService = new Label("<h2>Tra cứu dịch vụ " + serviceName.split("-")[0].trim().toUpperCase() + "</h2>", ContentMode.HTML);
		Label labelService = new Label("Tra cứu dv " + serviceName.toUpperCase());
		labelService.addStyleName("titlePage");
		labelService.setSizeUndefined();
		h1.addComponent(labelService);
		h1.setComponentAlignment(labelService, Alignment.MIDDLE_CENTER); 
		
		h1.addComponent(AppCmmsUtils.createLabel("", "0.5cm"));
		Label lb = new Label("Số đt:");
		h1.addComponent(lb); 
		h1.setComponentAlignment(lb, Alignment.MIDDLE_CENTER); 
		
		//h1.addComponent(from);
		//h1.addComponent(AppCmmsUtils.createLabel("", "1cm")); 
		//h1.addComponent(to);
		
		msisdnBox = AppCmmsUtils.createTextField("", "", false);
		msisdnBox.setWidth("2.8cm");
		h1.addComponent(msisdnBox);
		
		h1.addComponent(AppCmmsUtils.createLabel("", "0.1cm"));
		Button button = new Button("Submit");
		h1.addComponent(button);
		h1.setComponentAlignment(button, Alignment.MIDDLE_RIGHT); 
		
		h1.addComponent(AppCmmsUtils.createLabel("", "0.1cm"));
		final Button buttonExport = new Button("Export");
		h1.addComponent(buttonExport);
		h1.setComponentAlignment(buttonExport, Alignment.MIDDLE_RIGHT); 
		buttonExport.setEnabled(false); 
		
		h1.addComponent(AppCmmsUtils.createLabel("", "1.1cm"));
		Label lbfilter = new Label("Từ ngày: ");
		h1.addComponent(lbfilter); 
		h1.setComponentAlignment(lbfilter, Alignment.MIDDLE_CENTER); 
		
		from = AppCmmsUtils.createDateField("", false, Resolution.DAY);
		from.setDateFormat("yyyy-MM-dd"); 
		h1.addComponent(from); 
		
		h1.addComponent(AppCmmsUtils.createLabel("", "0.1cm"));
		Label lbfilter2 = new Label("đến: ");
		h1.addComponent(lbfilter2); 
		h1.setComponentAlignment(lbfilter2, Alignment.MIDDLE_CENTER); 
		
		to = AppCmmsUtils.createDateField("", false, Resolution.DAY);
		to.setDateFormat("yyyy-MM-dd"); 
		h1.addComponent(AppCmmsUtils.createLabel("", "0.1cm"));
		h1.addComponent(to); 
		
		h1.addComponent(AppCmmsUtils.createLabel("", "0.3cm"));
		Label lbfilter3 = new Label("từ khóa: ");
		h1.addComponent(lbfilter3); 
		h1.setComponentAlignment(lbfilter3, Alignment.MIDDLE_CENTER); 
		keyword = AppCmmsUtils.createTextField("", "", false);
		keyword.setWidth("3cm"); 
		h1.addComponent(keyword);
		
		h1.addComponent(AppCmmsUtils.createLabel("", "0.1cm"));
		Button filter = new Button(" Lọc ");
		h1.addComponent(filter);
		h1.setComponentAlignment(filter, Alignment.MIDDLE_RIGHT); 
		
		h1.setHeight("1.5cm"); 
		addComponent(h1); 
		//setComponentAlignment(h1, Alignment.MIDDLE_CENTER); 
		//setExpandRatio(h1, 1F);
		
		//Label l = new Label("");
		//l.setHeight("2cm");
		//addComponent(l);  
		
		// ----------------
		buttonExport.addClickListener(new Button.ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				clickExport();
			}
		});
		
		// --------------- pannel table
		final TabSheet tabsheet = new TabSheet();
		tabsheet.setSizeFull();
		
		final FormCCSPTableSubscriber formTableSubs = new FormCCSPTableSubscriber(xdata);
		formTableSubs.setFormMultiComponent(this); 
		tabsheet.addTab(formTableSubs, "Tra cứu thuê bao"); 
		
		final FormCCSPTableSMS formTableSMS = new FormCCSPTableSMS(xdata, this);
		tabsheet.addTab(formTableSMS, "Lịch sử SMS"); 

		final FormCCSPTableAction formTableAction = new FormCCSPTableAction(xdata, this);
		tabsheet.addTab(formTableAction, "Lịch sử thuê bao"); 
		
		addComponent(tabsheet);
		final ArrayList<String> loadedTab = new ArrayList<String>();
		
		ClickListener listener = new Button.ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				// String tempMsisdn = msisdnBox.getValue();
				loadedTab.clear();
				try {
					logger.info(mainApp.getTransid() + ", button: " + event.getButton().getCaption()
							+ ", dateFilterFrom: " + from.getValue() + ", dateFilterTo: " + to.getValue());
					
					if(event.getButton().getCaption().equalsIgnoreCase("Submit")) {
						from.setValue(null);
						to.setValue(null);
						keyword.setValue(""); 
						
						if(tabsheet.getSelectedTab() instanceof FormCCSPTableSubscriber == false) {
							tabsheet.setSelectedTab(formTableSubs); // khi click search thì nhảy về tab 1
						}
						formTableSubs.getPage(1, true, null); // get data sub
						if(formTableSubs.getTotalFound() > 0) {
							buttonExport.setEnabled(true);
						} else {
							buttonExport.setEnabled(false);
						}
					} else { // button filter
						if(tabsheet.getSelectedTab() instanceof FormCCSPTableSubscriber) {
							formTableSubs.getPage(1, true, null); // get data sub
						}
						else if(tabsheet.getSelectedTab() instanceof FormCCSPTableSMS) {
							formTableSMS.getPage(1, true, null); 
						}
						else if(tabsheet.getSelectedTab() instanceof FormCCSPTableAction) {
							formTableAction.getPage(1, true, null); 
						}
					}
				} catch (Exception e) {
					logger.info(mainApp.getTransid() + ", Exception: " + e.getMessage(), e);
				}
			}
		};
		button.addClickListener(listener);
		filter.addClickListener(listener);
		
		tabsheet.addSelectedTabChangeListener(new SelectedTabChangeListener() {
			@Override
			public void selectedTabChange(SelectedTabChangeEvent event) {
				logger.info(mainApp.getTransid() + ", tab changed: " + tabsheet.getSelectedTab().getClass().getName());
				
				try {
					// chuyển tab 2, 3 ... thì bắt đầu mới load data
					if(tabsheet.getSelectedTab() instanceof FormCCSPTableSMS) {
						if(loadedTab.contains("SMS") == false) {
							loadedTab.add("SMS");
							logger.info(mainApp.getTransid() + ", load data TAB FormCCSPTableSMS");
							FormCCSPTableSMS f = (FormCCSPTableSMS) tabsheet.getSelectedTab();
							f.getPage(1, true, null);
						}
					}
					if(tabsheet.getSelectedTab() instanceof FormCCSPTableAction) {
						if(loadedTab.contains("ACTION") == false) {
							loadedTab.add("ACTION");
							logger.info(mainApp.getTransid() + ", load data TAB FormCCSPTableAction");
							FormCCSPTableAction f = (FormCCSPTableAction) tabsheet.getSelectedTab();
							f.getPage(1, true, null);
						}
					} 
				} catch (Exception e) {
					logger.info(mainApp.getTransid() + ", Exception: " + e.getMessage(), e);
				}
			}
		});
	} 
	
	private void clickExport() {  
		try {
			String msisdn = msisdnBox.getValue();
			msisdn = BaseUtils.formatMsisdn_84(msisdn);
			String newFormat = BaseUtils.getOtherFormat(msisdn);
			logger.info(mainApp.getTransid() + ", ######## click export: " + msisdn);
			
			String sql = "select * from subscriber where msisdn in ('" + msisdn + "', '" + newFormat + "') order by created_time"; 
			ArrayList<CCSPSubscriber> listSub = xbaseDAO.getListBySql(mainApp.getTransid(), CCSPSubscriber.class, sql, null, null);
			
			if(listSub == null || listSub.size() == 0) {
				Notification.show("Không có dữ liệu", Type.ERROR_MESSAGE);
				return;
			}
			Notification.show("Loading....", Type.WARNING_MESSAGE);
			
			sql = "select * from pkg_policy where status = 1";
			ArrayList<CCSPPkgPolicy> listPolicy = xbaseDAO.getListBySql(mainApp.getTransid(), CCSPPkgPolicy.class, sql, null, null);
			
			Date from = BaseUtils.truncDate(listSub.get(0).getCreatedTime());
			Date to = BaseUtils.addTime(new Date(), Calendar.DATE, 1);
			
			// -------------------------------------------------------------- get SMS
			ArrayList<CCSPSms> listSMS = new ArrayList<CCSPSms>();
			sql = FormCCSPTableSMS.buildSQL_MO(msisdn, from, to, null);
			ArrayList<CCSPMoHis> listMO = xbaseDAO.getListBySql(mainApp.getTransid(), CCSPMoHis.class, sql, null, null);
			
			sql = FormCCSPTableSMS.buildSQL_MT(msisdn, from, to, null);
			ArrayList<CCSPMtHis> listMT = xbaseDAO.getListBySql(mainApp.getTransid(), CCSPMtHis.class, sql, null, null);
			
			if(listMO != null) {
				for(CCSPMoHis bean: listMO) {
					CCSPSms sms = new CCSPSms(0, "MO", bean.getMsisdn(), 
							bean.getCreatedTime(), bean.getCommand(), bean.getContent(), bean.getTransId());
					listSMS.add(sms);
				}
				logger.info(mainApp.getTransid() + ", add MO: " + listSMS.size());
			}
			if(listMT != null) {
				for(CCSPMtHis bean: listMT) {
					CCSPSms sms = new CCSPSms(0, "MT", bean.getMsisdn(), 
							bean.getCreatedTime(), bean.getCommand(), bean.getContent(), bean.getTransId());
					listSMS.add(sms);
				}
				logger.info(mainApp.getTransid() + ", add MT: " + listSMS.size());
			}
			Collections.sort(listSMS);
			logger.info(mainApp.getTransid() + ", sort SMS: " + listSMS.size());
			
			// ------------------------------------------------------------- get action
			ArrayList<String> listMonth = AppUtils.getListMonth(from, to);
			sql = FormCCSPTableAction.buildSQL_Action(listMonth, null, null, msisdn, null);
			ArrayList<CCSPActionHis> listAction = xbaseDAO.getListBySql(mainApp.getTransid(), CCSPActionHis.class, sql, null, null);
			
			// ------------------------------------------------------------- create file
			ArrayList<PairStringLong> totalFeeByPack = null;
			long totalFee = 0;
			
			if(listAction != null) {
				CounterLongUtils counter = new CounterLongUtils();
				
				for(CCSPActionHis a : listAction) {
					if(a.getResult() == 0 && a.getFee() > 0) {
						totalFee += a.getFee();
						counter.put(a.getPkgCode(), a.getFee()); 
					}
				}
				totalFeeByPack = counter.getArrayList();
			}
			Workbook workbook = new XSSFWorkbook();
			CCSP_ExcelCreateSheet_SubInfo excel1 = new CCSP_ExcelCreateSheet_SubInfo(workbook, listSub, listPolicy, serviceName, totalFee, totalFeeByPack);
			excel1.execute();
			
			CCSP_ExcelCreateSheet_SMS excel2 = new CCSP_ExcelCreateSheet_SMS(mainApp.getTransid(), workbook, listSMS);
			excel2.execute();
			
			CCSP_ExcelCreateSheet_ActionHis excel3 = new CCSP_ExcelCreateSheet_ActionHis(workbook, listAction);
			excel3.execute();

			FileOutputStream fileOut = null;
			try {
				String fileName = serviceName + "." + msisdn + "." + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + ".xlsx";
				String thispath = FormCCSPLayoutSubsInfo.class.getClassLoader().getResource("").getPath();
				// /opt/kns/bin/tomcatAllCms/webapps/CmsAVG/WEB-INF/classes
				String fullPath = thispath.split("/WEB-INF/")[0] + "/VAADIN/" + fileName;
				logger.info(mainApp.getTransid() + ", fullPath: " + fullPath);
				
				String moduleName = thispath.split("/WEB-INF/")[0].split("/webapps/")[1];
				if(moduleName.endsWith("ROOT")) {
					moduleName = ""; 
				} else {
					moduleName = "/" + moduleName;
				}
				fileOut = new FileOutputStream(fullPath);
				workbook.write(fileOut);
				getUI().getPage().open(moduleName + "/VAADIN/" + fileName, "_blank"); //_blank
				
			} catch (Exception e1) {
				logger.info(mainApp.getTransid() + ", Exception export1: " + e1.getMessage(), e1);
			}  finally {
				try { fileOut.close(); } catch (Exception e) {}
				try { workbook.close(); } catch (Exception e) {}
			}
		} catch (Exception e) {
			logger.info(mainApp.getTransid() + ", Exception export2: " + e.getMessage(), e);
		}
	}
}
