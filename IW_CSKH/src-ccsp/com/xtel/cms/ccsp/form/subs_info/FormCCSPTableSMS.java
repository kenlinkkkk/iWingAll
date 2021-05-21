package com.xtel.cms.ccsp.form.subs_info;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import com.ibm.icu.text.SimpleDateFormat;
import com.ligerdev.appbase.utils.BaseUtils;
import com.ligerdev.cmms.component.AppCmmsUtils;
import com.ligerdev.cmms.component.dialog.LDInputDialog;
import com.ligerdev.cmms.component.table.DialogSubmitResult;
import com.ligerdev.cmms.component.table.LDTableSimple;
import com.ligerdev.cmms.component.table.XGUI_TableLayout;
import com.ligerdev.cmms.component.table.TableHeader;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Label;
import com.xtel.cms.MainApplication;
import com.xtel.cms.ccsp.db.orm.CCSPMoHis;
import com.xtel.cms.ccsp.db.orm.CCSPMtHis;
import com.xtel.cms.ccsp.db.orm.CCSPSms;
import com.xtel.cms.ccsp.db.orm.CCSPSubscriber;
import com.xtel.cms.utils.AppUtils;
import com.xtel.cms.utils.XDataBean;
import com.xtel.cms.utils.XmlConfigs;

public class FormCCSPTableSMS extends LDTableSimple<CCSPSubscriber>{
	
	private FormCCSPLayoutSubsInfo formMultiComponent;
	private String serviceName;
	private XDataBean xdata;
	
	public FormCCSPTableSMS(XDataBean xdata, FormCCSPLayoutSubsInfo formMultiComponent) {
		this.baseDAO = xdata.baseDAO;
		this.xbaseDAO = xdata.xbaseDAO;
		this.serviceName = xdata.serviceName;
		this.xdata = xdata;
		this.formMultiComponent = formMultiComponent;
		logger.info(mainApp.getTransid() + ", handler class: " + this.getClass().getName() + ", serviceName: " + serviceName); 
	}
	
	@Override
	public void getPage(final int pageIndex, boolean showNotify, String filter) {
		filter = formMultiComponent.keyword.getValue();
		logger.info(mainApp.getTransid() + ", =====> pageIndex: " + pageIndex + ", showNtf: " + showNotify + ", filter: " + filter);
		
		int index = (pageIndex - 1) * AppCmmsUtils.getPageSize();
		String msisdn = formMultiComponent.msisdnBox.getValue();
		
		if(msisdn != null) {
			msisdn = msisdn.replace(" ", "").replace("'", "").replace("\"", "");
			msisdn = BaseUtils.formatMsisdn(msisdn, "84", "84");
		}
		Date from = formMultiComponent.from.getValue() == null ? XmlConfigs.time_launching : formMultiComponent.from.getValue();
		Date to = formMultiComponent.to.getValue() == null ? new Date() : formMultiComponent.to.getValue();
		String sql = buildSQL_MO(msisdn, from, to, filter);
		String key = "sms." + sql + serviceName + this.getClass().getName();
		ArrayList<CCSPSms> listSMS = (ArrayList<CCSPSms>) MainApplication.cache.getObject(key);
		
		if(listSMS == null) {
			listSMS = new ArrayList<CCSPSms>();
			ArrayList<CCSPMoHis> listMO = xbaseDAO.getListBySql(mainApp.getTransid(), CCSPMoHis.class, sql, null, null);
			
			sql = buildSQL_MT(msisdn, from, to, filter);
			ArrayList<CCSPMtHis> listMT = xbaseDAO.getListBySql(mainApp.getTransid(), CCSPMtHis.class, sql, null, null);
			
			if(listMO != null) {
				for(CCSPMoHis bean: listMO) {
					CCSPSms sms = new CCSPSms(0, "MO", bean.getMsisdn(), 
							bean.getCreatedTime(), bean.getCommand(), bean.getContent(), bean.getTransId());
					listSMS.add(sms);
				}
			}
			if(listMT != null) {
				for(CCSPMtHis bean: listMT) {
					CCSPSms sms = new CCSPSms(0, "MT", bean.getMsisdn(), 
							bean.getCreatedTime(), bean.getCommand(), bean.getContent(), bean.getTransId());
					listSMS.add(sms);
				}
			}
			Collections.sort(listSMS); 
			MainApplication.cache.put(key, listSMS, 30 * 1);
		}
		if(listSMS == null || listSMS.size() == 0) {
			logger.info(mainApp.getTransid() + ", not found");
			
			String str = " - Không tìm thấy bản ghi nào."; 
			Label title = new Label("<h3 style='color: blue'>" + str + "</h3>", ContentMode.HTML);
			setContent(title);
			return;
		}
		int totalFound = listSMS.size();
		logger.info(mainApp.getTransid() + ", totalFound: " + totalFound);
		
		ArrayList<CCSPSms> list = null;
		try {
			logger.info(mainApp.getTransid() + ", 1. index: " + index + ", to: " + (index + AppCmmsUtils.getPageSize()));
			list = new ArrayList<CCSPSms>(listSMS.subList(index, index + AppCmmsUtils.getPageSize()));
			
		} catch (Exception e) {
			logger.info(mainApp.getTransid() + ", " + e.getMessage() + " | 2. index: " + index + ", to: " + (listSMS.size()));
			list = new ArrayList<CCSPSms> (listSMS.subList(index, listSMS.size()));
		}
		BeanItemContainer<CCSPSms> data = new BeanItemContainer<CCSPSms>(CCSPSms.class, list);
		tableContainer = new XGUI_TableLayout(showNotify, pageIndex, totalFound, serviceName + " Lịch sử SMS", data, this, this, filter, true, null);
		/*
		private int id;
		private String type;
		private String msisdn;
		private Date createdTime;
		private String command;
		private String content;
		*/
		// manual set visible columns 
		tableContainer.getTable().addVisibleColumn(
				new TableHeader("STT", "STT")
				, new TableHeader("createdTime", "Thời gian")
				, new TableHeader("msisdn", "Số ĐT")
				, new TableHeader("type", "MO/MT")
				, new TableHeader("command", "Command")
				, new TableHeader("transid", "TransID")
				, new TableHeader("content", "Nội dung SMS").setWidth(1100)//.setWordwrap(true) 
		);
		tableContainer.getTable().setColumnWidth("content", 1150);
		setContent(tableContainer);
	} 
	
	private static String insteadOf(String tableMonth) {
		if(XmlConfigs.sms_shorttable == null) {
			return tableMonth;
		}
		for(String year : XmlConfigs.sms_shorttable) {
			if(tableMonth.startsWith(year)) {
				return year;
			}
		}
		return tableMonth;
	}
	
	public static String buildSQL_MO(String msisdn, Date from, Date to, String filter) {
		MainApplication mainApp = (MainApplication) MainApplication.getCurrent();
		logger.info(mainApp.getTransid() + ", buildSQL: from=" + from + ", to=" + to + ", filter=" + filter);
		
		ArrayList<String> listMonth = AppUtils.getListMonth(from, to);
		String filterSQL = AppCmmsUtils.getFilterRestrictionSql(filter, null, "content", "command");
		String sql = "";
		
		if(XmlConfigs.sms_shorttable != null && XmlConfigs.sms_shorttable.size() > 0) {
			ArrayList<String> temp = new ArrayList<String>();
			
			for(String tableMonth: listMonth) {
				String otherFormat = insteadOf(tableMonth);
				
				if(temp.contains(otherFormat) == false) {
					temp.add(otherFormat);
				}
			} 
			listMonth = temp;
		}
		for(int i = 0; i < listMonth.size(); i ++) {
			String newFormat = BaseUtils.getOtherFormat(msisdn);
			String temp = "select * from mo_" + listMonth.get(i) 
						+ " where msisdn in ('" + msisdn + "', '" + newFormat + "') and " + filterSQL;
			
			if(i == 0 && from != null) {
				temp += " and created_time >= " + new SimpleDateFormat("yyyyMMdd").format(from);
			}
			if(i == listMonth.size() - 1 && to != null) {
				temp += " and created_time <= '" + new SimpleDateFormat("yyyy-MM-dd").format(to) + " 23:59:59'";
			}
			sql += " union all " + temp;
		}
		sql = sql.replaceFirst(" union all ", "");
		return sql;
	}
	
	public static String buildSQL_MT(String msisdn, Date from, Date to, String filter) {
		MainApplication mainApp = (MainApplication) MainApplication.getCurrent();
		logger.info(mainApp.getTransid() + ", buildSQL: from=" + from + ", to=" + to + ", filter=" + filter);
		
		ArrayList<String> listMonth = AppUtils.getListMonth(from, to);
		String filterSQL = AppCmmsUtils.getFilterRestrictionSql(filter, null, "content", "command");
		String sql = "";
		
		if(XmlConfigs.sms_shorttable != null && XmlConfigs.sms_shorttable.size() > 0) {
			ArrayList<String> temp = new ArrayList<String>();
			
			for(String tableMonth: listMonth) {
				String otherFormat = insteadOf(tableMonth);
				
				if(temp.contains(otherFormat) == false) {
					temp.add(otherFormat);
				}
			}
			listMonth = temp;
		}
		for(int i = 0; i < listMonth.size(); i ++) {
			String newFormat = BaseUtils.getOtherFormat(msisdn);
			String temp = "select * from mt_" + listMonth.get(i)
						+ " where result = 0 and msisdn in ('" + msisdn + "', '" + newFormat + "') and " + filterSQL;
			
			if(i == 0 && from != null) {
				temp += " and created_time >= " + new SimpleDateFormat("yyyyMMdd").format(from);
			}
			if(i == listMonth.size() - 1 && to != null) {
				temp += " and created_time <= '" + new SimpleDateFormat("yyyy-MM-dd").format(to) + " 23:59:59'";
			}
			sql += " union all " + temp;
		}
		sql = sql.replaceFirst(" union all ", "");
		return sql;
	}

	@Override
	public void showAddDialog() {
	}

	@Override
	public void showSearchDialog() {
	}

	@Override
	public String getPermission() {
		// return TableActions.ROLE_FILTER;
		return null;
	}

	@Override
	public void exportTable() {
	}

	@Override
	public void showEditDialog(CCSPSubscriber selectedItem, Object colID) {
	}

	@Override
	public void showCopyDialog(CCSPSubscriber selectedItem) {
	}

	@Override
	public int deleteSelectedItem(ArrayList<CCSPSubscriber> listSelected) {
		return 0;
	}

	@Override
	public DialogSubmitResult dialogSubmit_(CCSPSubscriber item, LDInputDialog<CCSPSubscriber> dialog) {
		return null;
	}
 
}
