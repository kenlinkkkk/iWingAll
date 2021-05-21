package com.xtel.cms.ccsp.form.report;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.apache.log4j.Logger;

import com.ibm.icu.util.Calendar;
import com.ligerdev.appbase.utils.BaseUtils;
import com.ligerdev.appbase.utils.db.BaseDAO;
import com.ligerdev.appbase.utils.db.XBaseDAO;
import com.ligerdev.appbase.utils.textbase.Log4jLoader;
import com.ligerdev.cmms.component.AppCmmsUtils;
import com.ligerdev.cmms.component.tree.ITreeListener;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.DateField;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Label;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.VerticalLayout;
import com.xtel.cms.MainApplication;
import com.xtel.cms.utils.XBean;
import com.xtel.cms.utils.XDataBean;
import com.xtel.cms.utils.XmlConfigs.CCSPCfg;

public class FormCCSPLayoutReportAVG extends HorizontalSplitPanel implements ITreeListener{
	
	protected static Logger logger = Log4jLoader.getLogger();
	protected MainApplication mainApp = (MainApplication) MainApplication.getCurrent();
	public OptionGroup options = null;
	
	public FormCCSPLayoutReportAVG() {
		//setSizeUndefined();
		setSizeFull();
		logger.info(mainApp.getTransid() + ", handler class: " + this.getClass().getName()); 
		
		setSplitPosition(17, Unit.PERCENTAGE);
		VerticalLayout h = new VerticalLayout();
		h.addComponent(new Label("Số liệu vài ngày gần nhất"));
		h.addComponent(AppCmmsUtils.createLabelH("_____________________", "1cm")); 
		options = new OptionGroup("Loại báo cáo");
		
		XBean firstBean = new XBean("fee1", "Doanh thu theo ngày");
		options.addItem(firstBean);
		options.addItem(new XBean("pttb", "Phát triển thuê bao"));
		options.setValue(firstBean);
		h.addComponent(options);
		
		h.addComponent(AppCmmsUtils.createLabelH("", "0.5cm")); 
		h.setMargin(new MarginInfo(true, true, true, true)); 
		Button btn = new Button("Submit");
		h.addComponent(btn); 
		setFirstComponent(h); 
		
		// auto click firstReq
		btn.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				// TODO Auto-generated method stub
				XBean xbean = (XBean) options.getValue();
				Date dateFrom = null;
				Date dateTo = new Date();
				
				if(Calendar.getInstance().get(Calendar.DAY_OF_MONTH) >= 4){
					dateFrom = BaseUtils.addTime(new Date(), Calendar.DATE, -2); 
				} else {
					try {
						String s = new SimpleDateFormat("yyyyMM").format(new Date());
						dateFrom = new SimpleDateFormat("yyyyMMdd").parse(s + "01");
					} catch (ParseException e) {
					}
				}
				if(xbean.getValue().equalsIgnoreCase("fee1")) {
					ArrayList<MyThread1> listThread = new ArrayList<MyThread1>();
					long l1 = System.currentTimeMillis();
					{// AMOBI
						BaseDAO baseDAO = BaseDAO.getInstance("main");
						XBaseDAO xbaseDAO = XBaseDAO.getInstance("main");
						XDataBean xdata = new XDataBean(baseDAO, xbaseDAO, "AMOBI - ", CCSPCfg.link_unreg, false);
						final FormCCSPTableReportFee formTable = new FormCCSPTableReportFee(null, xdata, dateFrom, dateTo); 
						MyThread1 t = new MyThread1(formTable, "AMB");
						listThread.add(t);
						t.start();
					}
					{// MOBION
						BaseDAO baseDAO = BaseDAO.getInstance("mobion");
						XBaseDAO xbaseDAO = XBaseDAO.getInstance("mobion");
						XDataBean xdata = new XDataBean(baseDAO, xbaseDAO, "MOBION - ", CCSPCfg.link_unreg_mobion, false);
						final FormCCSPTableReportFee formTable = new FormCCSPTableReportFee(null, xdata, dateFrom, dateTo); 
						MyThread1 t = new MyThread1(formTable, "MBO");
						listThread.add(t);
						t.start();
					}
					{// ANXH
						BaseDAO baseDAO = BaseDAO.getInstance("anxh");
						XBaseDAO xbaseDAO = XBaseDAO.getInstance("anxh");
						XDataBean xdata = new XDataBean(baseDAO, xbaseDAO, "ANXH - ", CCSPCfg.link_unreg_anxh, false);
						final FormCCSPTableReportFee formTable = new FormCCSPTableReportFee(null, xdata, dateFrom, dateTo); 
						MyThread1 t = new MyThread1(formTable, "ANX");
						listThread.add(t);
						t.start();
					}
					{// VHPD
						BaseDAO baseDAO = BaseDAO.getInstance("vhpd");
						XBaseDAO xbaseDAO = XBaseDAO.getInstance("vhpd");
						XDataBean xdata = new XDataBean(baseDAO, xbaseDAO, "VHPD - ", CCSPCfg.link_unreg_vhpd, false);
						final FormCCSPTableReportFee formTable = new FormCCSPTableReportFee(null, xdata, dateFrom, dateTo); 
						MyThread1 t = new MyThread1(formTable, "VHP");
						listThread.add(t);
						t.start();
					}
					{// POKI
						BaseDAO baseDAO = BaseDAO.getInstance("poki");
						XBaseDAO xbaseDAO = XBaseDAO.getInstance("poki");
						XDataBean xdata = new XDataBean(baseDAO, xbaseDAO, "POKI - ", CCSPCfg.link_unreg_poki, true);
						final FormCCSPTableReportFee formTable = new FormCCSPTableReportFee(null, xdata, dateFrom, dateTo); 
						MyThread1 t = new MyThread1(formTable, "PKI");
						listThread.add(t);
						t.start();
					}
					VerticalLayout v = new VerticalLayout();
					for(MyThread1 t: listThread) {
						try {
							t.join();
							v.addComponent(t.form);  
							v.addComponent(AppCmmsUtils.createLabelH("", "1cm")); 
						} catch (Exception e) { 
						}
					}
					long l2 = System.currentTimeMillis();
					logger.info(mainApp.getTransid() + ", totalViewTime1: " + (l2 - l1));
					FormCCSPLayoutReportAVG.this.setSecondComponent(v); 
					return;
				}
				if(xbean.getValue().equalsIgnoreCase("pttb")) {
					ArrayList<MyThread2> listThread = new ArrayList<MyThread2>();
					long l1 = System.currentTimeMillis();
					{// AMOBI
						BaseDAO baseDAO = BaseDAO.getInstance("main");
						XBaseDAO xbaseDAO = XBaseDAO.getInstance("main");
						XDataBean xdata = new XDataBean(baseDAO, xbaseDAO, "AMOBI - ", CCSPCfg.link_unreg, false);
						final FormCCSPTableReportREG formTable = new FormCCSPTableReportREG(null, xdata, dateFrom, dateTo); 
						MyThread2 t = new MyThread2(formTable, "AMB");
						listThread.add(t);
						t.start();
					}
					{// MOBION
						BaseDAO baseDAO = BaseDAO.getInstance("mobion");
						XBaseDAO xbaseDAO = XBaseDAO.getInstance("mobion");
						XDataBean xdata = new XDataBean(baseDAO, xbaseDAO, "MOBION - ", CCSPCfg.link_unreg_mobion, false);
						final FormCCSPTableReportREG formTable = new FormCCSPTableReportREG(null, xdata, dateFrom, dateTo); 
						MyThread2 t = new MyThread2(formTable, "MBO");
						listThread.add(t);
						t.start();
					}
					{// ANXH
						BaseDAO baseDAO = BaseDAO.getInstance("anxh");
						XBaseDAO xbaseDAO = XBaseDAO.getInstance("anxh");
						XDataBean xdata = new XDataBean(baseDAO, xbaseDAO, "ANXH - ", CCSPCfg.link_unreg_anxh, false);
						FormCCSPTableReportREG formTable = new FormCCSPTableReportREG(null, xdata, dateFrom, dateTo); 
						MyThread2 t = new MyThread2(formTable, "ANX");
						listThread.add(t);
						t.start();
					}
					{// VHPD
						BaseDAO baseDAO = BaseDAO.getInstance("vhpd");
						XBaseDAO xbaseDAO = XBaseDAO.getInstance("vhpd");
						XDataBean xdata = new XDataBean(baseDAO, xbaseDAO, "VHPD - ", CCSPCfg.link_unreg_vhpd, false);
						final FormCCSPTableReportREG formTable = new FormCCSPTableReportREG(null, xdata, dateFrom, dateTo); 
						MyThread2 t = new MyThread2(formTable, "VHP");
						listThread.add(t);
						t.start();
					}
					{// POKI
						BaseDAO baseDAO = BaseDAO.getInstance("poki");
						XBaseDAO xbaseDAO = XBaseDAO.getInstance("poki");
						XDataBean xdata = new XDataBean(baseDAO, xbaseDAO, "POKI - ", CCSPCfg.link_unreg_poki, true);
						final FormCCSPTableReportREG formTable = new FormCCSPTableReportREG(null, xdata, dateFrom, dateTo); 
						MyThread2 t = new MyThread2(formTable, "PKI");
						listThread.add(t);
						t.start();
					}
					VerticalLayout v = new VerticalLayout();
					for(MyThread2 t: listThread) {
						try {
							t.join();
							v.addComponent(t.form);  
							v.addComponent(AppCmmsUtils.createLabelH("", "1cm")); 
						} catch (Exception e) { 
						}
					}
					long l2 = System.currentTimeMillis();
					logger.info(mainApp.getTransid() + ", totalViewTime2: " + (l2 - l1));
					FormCCSPLayoutReportAVG.this.setSecondComponent(v); 
					return;
				}
			}
		});
		btn.click();
	}
	
	class MyThread2 extends Thread {
		public FormCCSPTableReportREG form;
		public String name;
		
		public MyThread2(FormCCSPTableReportREG form, String name) {
			this.form = form;
			this.name = name;
		}
		public void run() {
			try {
				form.getPage(1, true, null);
				logger.info(mainApp.getTransid() + ", finish getPage: " + name);
			} catch (Exception e) {
				logger.info(mainApp.getTransid() + ", finish Exception getPage: "
						+ name + ", msg: " + e.getMessage(), e);
			}
		};
	}
	
	class MyThread1 extends Thread {
		public FormCCSPTableReportFee form;
		public String name;
		
		public MyThread1(FormCCSPTableReportFee form, String name) {
			this.form = form;
			this.name = name;
		}
		public void run() {
			try {
				form.getPage(1, true, null);
				logger.info(mainApp.getTransid() + ", finish getPage: " + name);
			} catch (Exception e) {
				logger.info(mainApp.getTransid() + ", finish Exception getPage: "
						+ name + ", msg: " + e.getMessage(), e);
			}
		};
	}

	@Override
	public void filterTree(Object paramObject) {
	}

	@Override
	public void treeValueChanged(Object paramObject) {
	} 
}
