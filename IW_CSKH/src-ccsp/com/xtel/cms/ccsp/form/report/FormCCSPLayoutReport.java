package com.xtel.cms.ccsp.form.report;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.apache.log4j.Logger;

import com.ligerdev.appbase.utils.db.BaseDAO;
import com.ligerdev.appbase.utils.db.XBaseDAO;
import com.ligerdev.appbase.utils.entities.PairStringString;
import com.ligerdev.appbase.utils.textbase.Log4jLoader;
import com.ligerdev.cmms.component.AppCmmsUtils;
import com.ligerdev.cmms.component.tree.ITreeListener;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.DateField;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.VerticalLayout;
import com.xtel.cms.MainApplication;
import com.xtel.cms.utils.AppUtils;
import com.xtel.cms.utils.XBean;
import com.xtel.cms.utils.XDataBean;
import com.xtel.cms.utils.XmlConfigs;
import com.xtel.cms.utils.XmlConfigs.CCSPCfg;

public class FormCCSPLayoutReport extends HorizontalSplitPanel implements ITreeListener{
	
	protected static Logger logger = Log4jLoader.getLogger();
	protected MainApplication mainApp = (MainApplication) MainApplication.getCurrent();
	
	public DateField dateFrom = null;
	public DateField dateTo = null;
	public OptionGroup options = null;
	public ComboBox cpids = null;
	
	protected BaseDAO baseDAO = null;
	protected XBaseDAO xbaseDAO = null;
	private String serviceName = null;
	
	private FormCCSPLayoutReport() {
	}
	
	public FormCCSPLayoutReport(final XDataBean xdata) {
		//setSizeUndefined();
		setSizeFull();
		this.baseDAO = xdata.baseDAO;
		this.xbaseDAO = xdata.xbaseDAO;
		this.serviceName = xdata.serviceName;
		logger.info(mainApp.getTransid() + ", handler class: " + this.getClass().getName() + ", serviceName: " + serviceName); 
		
		setSplitPosition(17, Unit.PERCENTAGE);
		VerticalLayout h = new VerticalLayout();
		// h.addComponent(new Label("Chọn nhóm báo cáo"));
		
		HorizontalLayout h2 = new HorizontalLayout();
		dateFrom = AppCmmsUtils.createDateField(serviceName + " From:", true, Resolution.DAY);
		dateFrom.setValue(AppUtils.truncMonth(new Date()));  
		dateFrom.setDateFormat("dd/MM/yyyy"); 
		h2.addComponent(dateFrom);
		h2.addComponent(AppCmmsUtils.createLabel("", "30px")); 
		
		dateTo = AppCmmsUtils.createDateField("To:", true, Resolution.DAY);
		dateTo.setValue(AppUtils.getMaxDayOfMonth3(new Date()));  
		dateTo.setDateFormat("dd/MM/yyyy"); 
		h2.addComponent(dateTo);
		h.addComponent(h2); 
		
		dateFrom.addValueChangeListener(new ValueChangeListener() {
			@Override
			public void valueChange(ValueChangeEvent event) {
				if("01".equals(new SimpleDateFormat("dd").format(dateFrom.getValue()))) {
					Date maxOfMonth = AppUtils.getMaxDayOfMonth2(dateFrom.getValue());
					dateTo.setValue(maxOfMonth); 
				}
			}
		});
		h.addComponent(AppCmmsUtils.createLabelH("", "0.5cm")); 
		options = new OptionGroup("Loại báo cáo:");
		ArrayList<PairStringString> listCpids = null;
		
		if(CCSPCfg.report_by_cpid != null) { // config 
			listCpids = (ArrayList<PairStringString>) CCSPCfg.report_by_cpid.clone();
		}
		// ---------- với amb ko theo config vì config đang ko riêng cho từng dv của amb
		if("MOBION".equalsIgnoreCase(this.serviceName)) {
			if(mainApp.getUserBean().getUsername().endsWith(".km201")) {
				// tk riêng => clear các cpid khác đi
				listCpids = new ArrayList<PairStringString>();
			} 
			listCpids.add(new PairStringString("KM201", " and cpid = 'KM201'"));
			listCpids.add(new PairStringString("KM202", " and cpid = 'KM202'"));
		}
		else if("AMOBI".equalsIgnoreCase(this.serviceName)) {
			if(mainApp.getUserBean().getUsername().endsWith(".km201")) {
				// tk riêng => clear ALL cpid, tạm thời chưa cho xem
				listCpids = new ArrayList<PairStringString>();
			}  
			listCpids.add(new PairStringString("KMFUN", " and cpid = 'KMFUN'"));
		}
		// ----------------------
		if(listCpids != null && listCpids.size() > 0) {
			cpids = AppCmmsUtils.createComboBox2("Chọn CPID", null, true);
			for(PairStringString item: listCpids) {
				cpids.addItem(item.getValue());
				cpids.setItemCaption(item.getValue(), item.getName()); 
			}
			cpids.setValue(listCpids.get(0).getValue()); 
			h.addComponent(cpids);
			h.addComponent(AppCmmsUtils.createLabelH("", "0.5cm")); 
		}
		XBean firstBean = new XBean("fee1", "Doanh thu theo ngày");
		options.addItem(firstBean);
		
		options.addItem(new XBean("pttb", "Phát triển thuê bao"));
		options.addItem(new XBean("fee2", "Doanh thu theo kênh"));
		options.addItem(new XBean("feePkg", "Doanh thu theo gói"));
		options.addItem(new XBean("feePrice", "Doanh thu theo mệnh giá"));
		
		if(String.valueOf(serviceName).toLowerCase().contains("amobi")) { 
			options.addItem(new XBean("feeService", "Doanh thu theo DV AMOBI"));
		}
		options.setValue(firstBean);
		h.addComponent(options);
		
		h.setMargin(new MarginInfo(true, true, true, true)); 
		// h.setDefaultComponentAlignment(Alignment.TOP_CENTER);
		h.addComponent(AppCmmsUtils.createLabelH("_____________________", "1cm")); 
		
		h.addComponent(AppCmmsUtils.createLabelH("", "0.5cm")); 
		Button btn = new Button("Submit");
		h.addComponent(btn); 
		setFirstComponent(h); 
		
		// first request => load
		//FormCCSPTableReportFee formTable = new FormCCSPTableReportFee();
		//formTable.setLayoutParent(FormCCSPLayoutReport.this); 
		//formTable.getPage(1, true, null);
		//FormCCSPLayoutReport.this.setSecondComponent(formTable);
		
		// auto click firstReq
		btn.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				// TODO Auto-generated method stub
				XBean xbean = (XBean) options.getValue();
				Date from = dateFrom.getValue();
				Date to = dateTo.getValue();
				
				if(from.compareTo(to) > 0) {
					from = dateTo.getValue();
					to = dateFrom.getValue();
				}
				if(xbean.getValue().equalsIgnoreCase("fee1")) {
					FormCCSPTableReportFee formTable = new FormCCSPTableReportFee(cpids, xdata, from, to);
					formTable.getPage(1, true, null);
					FormCCSPLayoutReport.this.setSecondComponent(formTable); 
					return;
				}
				if(xbean.getValue().equalsIgnoreCase("fee2")) {
					FormCCSPTableReportChannel formTable = new FormCCSPTableReportChannel(cpids, xdata, from, to);
					formTable.getPage(1, true, null);
					FormCCSPLayoutReport.this.setSecondComponent(formTable); 
					return;
				}
				if(xbean.getValue().equalsIgnoreCase("pttb")) {
					FormCCSPTableReportREG formTable = new FormCCSPTableReportREG(cpids, xdata, from, to);
					formTable.getPage(1, true, null);
					FormCCSPLayoutReport.this.setSecondComponent(formTable); 
					return;
				}
				if(xbean.getValue().equalsIgnoreCase("feePkg")) {
					FormCCSPTableReportPkg formTable = new FormCCSPTableReportPkg(cpids, xdata, from, to);
					formTable.getPage(1, true, null);
					FormCCSPLayoutReport.this.setSecondComponent(formTable); 
					return;
				}
				if(xbean.getValue().equalsIgnoreCase("feePrice")) {
					FormCCSPTableReportGroupPrice formTable = new FormCCSPTableReportGroupPrice(cpids, xdata, from, to);
					formTable.getPage(1, true, null);
					FormCCSPLayoutReport.this.setSecondComponent(formTable); 
					return;
				}
				if(xbean.getValue().equalsIgnoreCase("feeService")) {
					FormCCSPTableReportSubServiceAMOB formTable = new FormCCSPTableReportSubServiceAMOB(cpids, xdata, from, to);
					formTable.getPage(1, true, null);
					FormCCSPLayoutReport.this.setSecondComponent(formTable); 
					return;
				}
			}
		});
	}

	@Override
	public void filterTree(Object paramObject) {
	}

	@Override
	public void treeValueChanged(Object paramObject) {
	} 
}
