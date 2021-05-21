package com.xtel.cms.ccsp.form.subs_info;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
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
import com.xtel.cms.ccsp.db.orm.CCSPActionHis;
import com.xtel.cms.ccsp.db.orm.CCSPPkgPolicy;
import com.xtel.cms.utils.AppUtils;
import com.xtel.cms.utils.XDataBean;
import com.xtel.cms.utils.XmlConfigs;

public class FormCCSPTableAction extends LDTableSimple<CCSPActionHis>{
	
	private FormCCSPLayoutSubsInfo formMultiComponent;
	private String serviceName;
	private XDataBean xdata;
	
	public FormCCSPTableAction(XDataBean xdata, FormCCSPLayoutSubsInfo formMultiComponent) {
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
		// tính from
		/*Date from = formMultiComponent.from.getValue();
		Date to = formMultiComponent.to.getValue();
		if(from == null) {
			from = BaseUtils.parseTime("yyyyMMdd", "20180901");
		}
		if(to == null) {
			to = new Date();
		}
		to = BaseUtils.addTime(to, Calendar.DATE, 1);
		*/
		Date from = formMultiComponent.from.getValue() == null ? XmlConfigs.time_launching : formMultiComponent.from.getValue();
		Date to = formMultiComponent.to.getValue() == null ? new Date() : formMultiComponent.to.getValue();
		ArrayList<String> listMonth = AppUtils.getListMonth(from, to);
		String sql = buildSQL_Action(listMonth, from, to, msisdn, filter);
		String key = "action." + sql + serviceName + this.getClass().getName();
		ArrayList<CCSPActionHis> listAction = (ArrayList<CCSPActionHis>) MainApplication.cache.getObject(key);
		
		if(listAction == null) {
			listAction = xbaseDAO.getListBySql(mainApp.getTransid(), CCSPActionHis.class, sql, null, null);
			
			if(listAction != null) { 
				sql = "select * from pkg_policy where status = 1";
				ArrayList<CCSPPkgPolicy> listCCSPPkgPolicy = xbaseDAO.getListBySql(mainApp.getTransid(), CCSPPkgPolicy.class, sql, null, null);
				
				// làm giàu dữ liệu cho listAction (ko thích join bảng)
				for(CCSPActionHis bean: listAction) {
					String pkgName = null;
					
					for(CCSPPkgPolicy p: listCCSPPkgPolicy) {
						if(p.getCode().equalsIgnoreCase(bean.getPkgCode())) {
							pkgName = p.getName();
							break;
						}
					}
					bean.setPkgName(pkgName);
					
					if("CONTENT".equalsIgnoreCase(bean.getAction())) {
						bean.setAction("Mua content"); 
					} 
					else if("RENEW".equalsIgnoreCase(bean.getAction())) {
						bean.setAction("Gia Hạn"); 
					} 
					else if("FirstREG".equalsIgnoreCase(bean.getAction())) {
						bean.setAction("Đăng ký lần đầu"); 
					} 
					else if("ReREG".equalsIgnoreCase(bean.getAction())) {
						bean.setAction("Đăng ký lại"); 
					} 
					else if("UNREG".equalsIgnoreCase(bean.getAction()) 
							|| "DELETE".equalsIgnoreCase(bean.getAction())) {
						bean.setAction("Hủy gói"); 
					}
				}
				MainApplication.cache.put(key, listAction, 30 * 1);
			}
		}
		if(listAction == null || listAction.size() == 0) {
			String str = " - Không tìm thấy bản ghi nào."; 
			Label title = new Label("<h3 style='color: blue'>" + str + "</h3>", ContentMode.HTML);
			setContent(title);
			return;
		}
		int totalFee = 0;
		if(listAction != null) {
			for(CCSPActionHis bean: listAction) {
				totalFee += bean.getFee();
			}
		}
		int totalFound = listAction.size();
		logger.info(mainApp.getTransid() + ", totalFound: " + totalFound);
		ArrayList<CCSPActionHis> list = null;
		try {
			logger.info(mainApp.getTransid() + ", 1. index: " + index + ", to: " + (index + AppCmmsUtils.getPageSize()));
			list = new ArrayList<CCSPActionHis>(listAction.subList(index, index + AppCmmsUtils.getPageSize()));
		} catch (Exception e) {
			logger.info(mainApp.getTransid() + ", " + e.getMessage() + " | 2. index: " + index + ", to: " + (listAction.size()));
			list = new ArrayList<CCSPActionHis> (listAction.subList(index, listAction.size()));
		}
		BeanItemContainer<CCSPActionHis> data = new BeanItemContainer<CCSPActionHis>(CCSPActionHis.class, list);
		tableContainer = new XGUI_TableLayout(showNotify, pageIndex, totalFound, serviceName + " Lịch sử giao dịch", data, this, this, filter, true, null);
		/*
		private int id;
		private String msisdn;
		private String action;
		private Date createdTime = new Date();
		private int fee;
		private String transId;
		private String note;
		private String channel;
		private int result;
		private String pkgCode;
		private String cpid;
		private Date expireTime;
		private Integer regdate;
		
		private int expireint;
		private int activeint;
		*/
		// manual set visible columns 
		tableContainer.getTable().addVisibleColumn(
				new TableHeader("STT", "STT")
				, new TableHeader("createdTime", "Thời gian")
				, new TableHeader("msisdn", "Số ĐT")
				, new TableHeader("action", "Giao dịch")
				, new TableHeader("pkgCode", "Mã Gói")
				, new TableHeader("pkgName", "Tên Gói")
				, new TableHeader("fee", "Giá tiền")
				, new TableHeader("channel", "Kênh")
						.addMapValue("WCC", "CSKH")
						.addMapValue("SYS", "SYSTEM")
			//	, new TableHeader("cpid", "Mã ĐK")
				, new TableHeader("note", "Note")
				, new TableHeader("transId", "TransID")
		);
		// manual remove some visible columns 
		tableContainer.getTable().removeVisibleColumn("password");
		tableContainer.addComponent(AppCmmsUtils.createLabel("+", "1cm"));
		
		NumberFormat nf = new DecimalFormat("###,###,###,###");
		String str = " - Tổng số cước đã trừ: " + nf.format(totalFee) + " VNĐ"; 
		
		Label title = new Label("<h3 style='color: blue'>" + str + "</h3>", ContentMode.HTML);
		tableContainer.addComponent(title);
		
		setContent(tableContainer); 
	}
	
	public static String buildSQL_Action(ArrayList<String> listMonth, Date from, Date to, String msisdn, String filter) {
		String sql = "";
		MainApplication mainApp = (MainApplication) MainApplication.getCurrent();
		logger.info(mainApp.getTransid() + ", buildSQL: from=" + from + ", to=" + to + ", filter=" + filter);
		String filterSQL = AppCmmsUtils.getFilterRestrictionSql(filter, null, "channel", "action", "pkg_code");
		
		for(int i = 0; i < listMonth.size(); i ++) {
			String newFormat = BaseUtils.getOtherFormat(msisdn);
			
			String temp = "select * from his_" + listMonth.get(i) 
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
		//return TableActions.ROLE_FILTER;
		return null;
	}

	@Override
	public void exportTable() {
	}

	@Override
	public void showEditDialog(CCSPActionHis selectedItem, Object colID) {
	}

	@Override
	public void showCopyDialog(CCSPActionHis selectedItem) {
	}

	@Override
	public int deleteSelectedItem(ArrayList<CCSPActionHis> listSelected) {
		return 0;
	}

	@Override
	public DialogSubmitResult dialogSubmit_(CCSPActionHis item, LDInputDialog<CCSPActionHis> dialog) {
		return null;
	} 
}
