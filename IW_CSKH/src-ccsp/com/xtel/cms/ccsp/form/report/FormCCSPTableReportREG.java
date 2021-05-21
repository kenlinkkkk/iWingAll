package com.xtel.cms.ccsp.form.report;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import com.ibm.icu.text.SimpleDateFormat;
import com.ligerdev.appbase.utils.BaseUtils;
import com.ligerdev.cmms.component.dialog.LDInputDialog;
import com.ligerdev.cmms.component.table.DialogSubmitResult;
import com.ligerdev.cmms.component.table.LDTableSimple;
import com.ligerdev.cmms.component.table.XGUI_TableLayout;
import com.ligerdev.cmms.component.table.XOptions;
import com.ligerdev.cmms.component.table.TableHeader;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Label;
import com.xtel.cms.ccsp.db.orm.others.CCSPReportChannel;
import com.xtel.cms.ccsp.db.orm.others.CCSPReportREG;
import com.xtel.cms.utils.AppUtils;
import com.xtel.cms.utils.XDataBean;

public class FormCCSPTableReportREG extends LDTableSimple<CCSPReportREG>{
	
	private Date dateFrom;
	private Date dateTo;
	private String serviceName;
	private XDataBean xdata;
	private ComboBox cpids;

	public FormCCSPTableReportREG(ComboBox cpids, XDataBean xdata, Date dateFrom, Date dateTo) {
		this.baseDAO = xdata.baseDAO;
		this.xbaseDAO = xdata.xbaseDAO;
		this.serviceName = xdata.serviceName;
		this.dateFrom = dateFrom;
		this.dateTo = dateTo;
		this.xdata = xdata;
		this.cpids = cpids;
		logger.info(mainApp.getTransid() + ", handler class: " + this.getClass().getName() + ", serviceName: " + serviceName); 
		setSizeUndefined();
	}

	@Override
	public void getPage(final int pageIndex, final boolean showNotify, final String filter) {
		logger.info(mainApp.getTransid() + ", =====> pageIndex: " + pageIndex + ", showNtf: " + showNotify + ", filter: " + filter);
		// int index = (pageIndex - 1) * AppCmmsUtils.getPageSize();
		String sql = buildSQL_ccsp(dateFrom, dateTo);
		if(xdata.isCCSPService == false) {
			sql = buildSQL_normal(dateFrom, dateTo);
		}
		ArrayList<CCSPReportREG> list = xbaseDAO.getListBySql(mainApp.getTransid(), CCSPReportREG.class, sql, null, null);
		int totalFound = list.size();
		long total = 0;
		
		if(list != null) {
			Collections.sort(list, new Comparator<CCSPReportREG>() {
				@Override
				public int compare(CCSPReportREG o1, CCSPReportREG o2) {
					try {
						//SimpleDateFormat nf = new SimpleDateFormat("dd/MM/yyyy");
						return o2.getDateReport().compareTo(o1.getDateReport());
					} catch (Exception e) {
					}
					return 0;
				}
			});
			for(CCSPReportREG b: list) {
				long temp = Long.parseLong(b.getDkTotal()) - Long.parseLong(b.getHuyTotal());
				total += temp;
				b.setChangeSubs(temp > 0 ? "+" + temp :  "" + temp); 
				
				Long huyOther = Long.parseLong(b.getHuyTotal())
						- Long.parseLong(b.getHuySms())
						- Long.parseLong(b.getHuyCskh())
						- Long.parseLong(b.getHuyHt());
				b.setHuyOther(huyOther + ""); 
			}
		}
		// -------add row total tự động
		CCSPReportREG rowTotal = null;
		ArrayList<String> listFields = new ArrayList<String>();
		try {
			listFields.add("dkSms");
			listFields.add("dkWap");
			listFields.add("dkVbc");
			listFields.add("dkTotal");
			listFields.add("huySms");
			listFields.add("huyCskh");
			listFields.add("huyHt");
			listFields.add("huyOther");
			listFields.add("huyTotal");
			listFields.add("huyToday");
			listFields.add("changeSubs");
			rowTotal = AppUtils.getItemTotal(list, CCSPReportREG.class, listFields);
			rowTotal.setDateReport("<b>Tổng:</b>");
			list.add(rowTotal);
		} catch (Exception e) {
		}
		XOptions option = new XOptions();
		option.rowTotal_title = "<b>Tổng trang:</b>";
		option.rowTotal_title_colID = "dateReport";
		option.rowTotal_object = rowTotal;
		option.rowTotal_listTotalCol = listFields;
		
		BeanItemContainer<CCSPReportREG> data = new BeanItemContainer<CCSPReportREG>(CCSPReportREG.class, list);
		String cpidStr = cpids != null ? " (cpid = " + cpids.getItemCaption(cpids.getValue()).toString() + ")" : "";
		String titleTable = serviceName + cpidStr + " - Phát triển thuê bao: "  + new SimpleDateFormat("dd-MM-yyyy").format(dateFrom) + " đến " + new SimpleDateFormat("dd-MM-yyyy").format(dateTo);
		tableContainer = new XGUI_TableLayout(false, pageIndex, totalFound, titleTable, data, this, this, filter, false, option);
		/*
		 	private String dateReport;
			private String dkSms;
			private String dkWap;
			private String dkCtkm;
			private String dkIvr;
			private String dkVbc;
			private String dkTotal;
			private String huySms;
			private String huyCskh;
			private String huyTotal;
		*/
		// manual set visible columns 
		tableContainer.getTable().addVisibleColumn(
				 new TableHeader("dateReport", "Ngày")
				, new TableHeader("dkSms", "ĐK SMS")
				, new TableHeader("dkWap", "ĐK WAP")
			//	, new TableHeader("dkCtkm", "ĐK CTKM")
				, new TableHeader("dkVbc", "ĐK AVB")
				, new TableHeader("dkTotal", "Tổng ĐK")
				, new TableHeader("huySms", "Hủy SMS")
				, new TableHeader("huyCskh", "Hủy CSKH")
				, new TableHeader("huyHt", "Hủy 30d")
				, new TableHeader("huyOther", "Hủy Khác")
				, new TableHeader("huyTotal", "Tổng Hủy")
				, new TableHeader("huyToday", "(Hủy trong ngày)")
				, new TableHeader("changeSubs", "Thay đổi")
		);
		// manual remove some visible columns 
		tableContainer.getTable().removeVisibleColumn("password");
		NumberFormat nf = new DecimalFormat("###,###,###,###");
		String str = " - Tổng sub thay đổi: " + (total > 0 ? "+" : "") +  nf.format(total); 
		
		Label title = new Label("<h3 style='color: blue'>" + str + "</h3>", ContentMode.HTML);
		tableContainer.addComponent(title);
		setContent(tableContainer); 
	}
	
	private String buildSQL_normal(Date from, Date to) {
		ArrayList <String> listMonth = BaseUtils.getListMonthBetween(from, to, "yyyyMM");
		String dateFromStr = new SimpleDateFormat("yyyyMMdd").format(dateFrom);
		String dateToStr = new SimpleDateFormat("yyyyMMdd").format(dateTo);
		String sql = "";
		
		String cpidStr = "";
		if(cpids != null) {
			cpidStr = (String) cpids.getValue();
		}
		for(int i = 0; i < listMonth.size(); i ++) {
			String table = "his_" + listMonth.get(i);
			
			String tempTime = "";  
			if(i == 0) { // đầu
				tempTime += " and hisnote3 >= " + dateFromStr;
			}
			if(i == listMonth.size() - 1) { // cuối
				tempTime += " and hisnote3 <= " + dateToStr;
			}
			String temp = 
					  		" select DATE_FORMAT(STR_TO_DATE(hisnote3, '%Y%m%d'), '%Y-%m-%d') date_report, " + 
							" COALESCE(sum(case when action in ('FirstREG', 'ReREG') and result = 0  and channel = 'SMS' then 1 else 0 end), 0) dk_sms," + 
							" COALESCE(sum(case when action in ('FirstREG', 'ReREG') and result = 0  and channel = 'WAP' then 1 else 0 end), 0) dk_wap," + 
							" COALESCE(sum(case when action in ('FirstREG', 'ReREG') and result = 0  and channel in ('VBC', 'AVB') then 1 else 0 end), 0) dk_vbc," + 
							" COALESCE(sum(case when action in ('FirstREG', 'ReREG') and result = 0 then 1 else 0 end), 0) dk_total," + 
							
							" COALESCE(sum(case when action in ('UNREG') and result = 0 and channel = 'SMS' then 1 else 0 end), 0) huy_sms," + 
							" COALESCE(sum(case when action in ('UNREG') and result = 0 and channel in ('WCC', 'CSKH') then 1 else 0 end), 0) huy_cskh," + 
							" COALESCE(sum(case when action in ('DELETE') and result = 0 and expireint >= 29 then 1 else 0 end), 0) huy_ht," + 
							" COALESCE(sum(case when action in ('UNREG', 'DELETE') and result = 0 then 1 else 0 end), 0) huy_total, " + 
							" COALESCE(sum(case when action in ('UNREG') and result = 0 and regdate = hisnote3 then 1 else 0 end), 0) huy_today " + 
					" from " + table + " where result = 0 " + tempTime + cpidStr + " group by hisnote3" ;
			
			sql += " union all " + temp;
		}
		sql = sql.replaceFirst(" union all ", "");
		return sql;
	}
	
	
	private String buildSQL_ccsp(Date from, Date to) {
		ArrayList <String> listMonth = BaseUtils.getListMonthBetween(from, to, "yyyyMM");
		logger.info(mainApp.getTransid() + ", buildSQL: from=" + String.valueOf(from) + ", to=" + String.valueOf(to) +", listMonth=" + listMonth);
		String dateFromStr = new SimpleDateFormat("yyyyMMdd").format(from);
		String dateToStr = new SimpleDateFormat("yyyyMMdd").format(to);
		String cpidStr = "";
		if(cpids != null) {
			cpidStr = (String) cpids.getValue();
		}
		String sql = "";
		
		for(int i = 0; i < listMonth.size(); i ++) {
			String table = "his_" + listMonth.get(i);
			
			String tempTime = "";  
			if(i == 0) { // đầu
				tempTime += " and hisnote1 >= " + dateFromStr;
			}
			if(i == listMonth.size() - 1) { // cuối
				tempTime += " and hisnote1 <= " + dateToStr;
			}
			String temp = 
					" select DATE_FORMAT(STR_TO_DATE(hisnote1, '%Y%m%d'), '%Y-%m-%d') date_report, " + 
							" COALESCE(sum(case when action in ('FirstREG', 'ReREG') and channel = 'SMS' then 1 else 0 end), 0) dk_sms," + 
							" COALESCE(sum(case when action in ('FirstREG', 'ReREG') and channel = 'WAP' then 1 else 0 end), 0) dk_wap," + 
							" COALESCE(sum(case when action in ('FirstREG', 'ReREG') and channel = 'VASGATE' then 1 else 0 end), 0) dk_vbc," + 
							" COALESCE(sum(case when action in ('FirstREG', 'ReREG') then 1 else 0 end), 0) dk_total," + 
							
							" COALESCE(sum(case when action in ('UNREG') and channel = 'SMS' then 1 else 0 end), 0) huy_sms," + 
							" COALESCE(sum(case when action in ('UNREG') and channel in ('CP', 'SELFCARE') then 1 else 0 end), 0) huy_cskh," + 
							" COALESCE(sum(case when action in ('UNREG') and channel = 'VASP' and expireint >= 30 then 1 else 0 end), 0) huy_ht," + 
							" COALESCE(sum(case when action in ('UNREG', 'DELETE') then 1 else 0 end), 0) huy_total, " + 
							" COALESCE(sum(case when action in ('UNREG') and regdate = hisnote1 then 1 else 0 end), 0) huy_today " + 
					" from " + table + " where result = 0 " + tempTime + cpidStr + " group by hisnote1" ;
			
			sql += " union all " + temp;
		}
		sql = sql.replaceFirst(" union all ", "");
		return sql; 
	}
	
	@Override
	public void showAddDialog() {
		// TODO Auto-generated method stub
	}

	@Override
	public void showSearchDialog() {
		// TODO Auto-generated method stub
	}

	@Override
	public String getPermission() {
		return null;
	}

	@Override
	public void exportTable() {
		// TODO Auto-generated method stub
	}

	@Override
	public void showEditDialog(CCSPReportREG selectedItem, Object colID) {
		// TODO Auto-generated method stub
	}

	@Override
	public void showCopyDialog(CCSPReportREG selectedItem) {
		// TODO Auto-generated method stub
	}

	@Override
	public int deleteSelectedItem(ArrayList<CCSPReportREG> listSelected) {
		return 0;
	}

	@Override
	public DialogSubmitResult dialogSubmit_(CCSPReportREG item, LDInputDialog<CCSPReportREG> dialog) {
		return null;
	} 
}





