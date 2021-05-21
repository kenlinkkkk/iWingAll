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
import com.xtel.cms.ccsp.db.orm.others.CCSPReportFee;
import com.xtel.cms.utils.AppUtils;
import com.xtel.cms.utils.XDataBean;

public class FormCCSPTableReportFee extends LDTableSimple<CCSPReportFee>{
	
	private String serviceName;
	private Date dateFrom;
	private Date dateTo;
	private XDataBean xdata;
	private ComboBox cpids;
	
	public FormCCSPTableReportFee(ComboBox cpids, XDataBean xdata, Date dateFrom, Date dateTo) {
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
		String sql = buildSQL(dateFrom, dateTo);
		 
		if(xdata.isCCSPService == false) {
			sql =  sql.replace("hisnote1", "hisnote3");
		}
		ArrayList<CCSPReportFee> list = xbaseDAO.getListBySql(mainApp.getTransid(), CCSPReportFee.class, sql, null, null);
		int totalFound = list.size();
		long total = 0;
		
		if(list != null) {
			Collections.sort(list, new Comparator<CCSPReportFee>() {
				@Override
				public int compare(CCSPReportFee o1, CCSPReportFee o2) {
					try {
						//SimpleDateFormat nf = new SimpleDateFormat("dd/MM/yyyy");
						return o2.getDateReport().compareTo(o1.getDateReport());
					} catch (Exception e) {
					}
					return 0;
				}
			});
			for(CCSPReportFee b: list) {
				total += Long.parseLong(b.getFeeTotal());
			}
		}
		// -------add row total tự động
		CCSPReportFee rowTotal = null;
		ArrayList<String> listFields = new ArrayList<String>();
		try {
			listFields.add("feeDk");
			listFields.add("feeRenew");
			listFields.add("feeContent");
			listFields.add("feeTotal");
			rowTotal = AppUtils.getItemTotal(list, CCSPReportFee.class, listFields);
			list.add(rowTotal);
		} catch (Exception e) {
		}
		XOptions option = new XOptions();
		option.rowTotal_title = "<b>Tổng trang:</b>";
		option.rowTotal_title_colID = "dateReport";
		option.rowTotal_object = rowTotal;
		option.rowTotal_listTotalCol = listFields;
		
		BeanItemContainer<CCSPReportFee> data = new BeanItemContainer<CCSPReportFee>(CCSPReportFee.class, list);
		String cpidStr = cpids != null ? " (cpid = " + cpids.getItemCaption(cpids.getValue()).toString() + ")" : "";
		String titleTable = serviceName + cpidStr + " - Doanh thu theo ngày: "  + new SimpleDateFormat("dd-MM-yyyy").format(dateFrom) + " đến " + new SimpleDateFormat("dd-MM-yyyy").format(dateTo);
		tableContainer = new XGUI_TableLayout(false, pageIndex, totalFound, titleTable, data, this, this, filter, false, option);
		
		/*
		 	private String dateReport;
			private String feeDk;
			private String feeRenew;
			private String feeContent;
			private String feeTotal;
		 
		 */

		// manual set visible columns 
		tableContainer.getTable().addVisibleColumn(
				 new TableHeader("dateReport", "Ngày")
				, new TableHeader("feeDk", "D.Thu ĐK").setFormatNumber(true)
				, new TableHeader("feeRenew", "D.Thu gia hạn").setFormatNumber(true)
				, new TableHeader("feeContent", "D.Thu Content").setFormatNumber(true)
				, new TableHeader("feeTotal", "D.Thu Tổng").setFormatNumber(true)
		);
		// manual remove some visible columns 
		tableContainer.getTable().removeVisibleColumn("password");
		NumberFormat nf = new DecimalFormat("###,###,###,###");
		
		String str = " - Tổng doanh thu: " + nf.format(total); 
		Label title = new Label("<h3 style='color: blue'>" + str + "</h3>", ContentMode.HTML);
		tableContainer.addComponent(title);
		/*{
			Label label = new Label();
			label.setValue("This is test message for mailing list This is test message for mailing list . This is test message for mailing list This is test message for mailing list2");
			label.setWidth("90px");
			// label.setStyleName(ValoTheme.LAYOUT_HORIZONTAL_WRAPPING);
			label.addStyleName("wrapLine");
			tableContainer.addComponent(label);
		}*/
		setContent(tableContainer); 
	}

	private String buildSQL(Date from, Date to) {
		ArrayList <String> listMonth = BaseUtils.getListMonthBetween(from, to, "yyyyMM");
		logger.info(mainApp.getTransid() + ", buildSQL: from=" + String.valueOf(from) + ", to=" + String.valueOf(to) +", listMonth=" + listMonth);
		String dateFromStr = new SimpleDateFormat("yyyyMMdd").format(from);
		String dateToStr = new SimpleDateFormat("yyyyMMdd").format(to);
		String sql = "";
		
		String cpidStr = "";
		if(cpids != null) {
			cpidStr = (String) cpids.getValue();
		}
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
						" COALESCE(sum(case when action in ('FirstREG', 'ReREG') then fee else 0 end), 0) fee_dk," + 
						" COALESCE(sum(case when action in ('RENEW') then fee else 0 end), 0) fee_renew," + 
						" COALESCE(sum(case when action in ('CONTENT') then fee else 0 end), 0) fee_content, " + 
						" COALESCE(sum(fee), 0) fee_total  " + 
					" from " + table + " where result = 0 and fee > 0 " + tempTime + cpidStr + " group by hisnote1" ;
			
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
	public void showEditDialog(CCSPReportFee selectedItem, Object colID) {
		// TODO Auto-generated method stub
	}

	@Override
	public void showCopyDialog(CCSPReportFee selectedItem) {
		// TODO Auto-generated method stub
	}

	@Override
	public int deleteSelectedItem(ArrayList<CCSPReportFee> listSelected) {
		return 0;
	}

	@Override
	public DialogSubmitResult dialogSubmit_(CCSPReportFee item, LDInputDialog<CCSPReportFee> dialog) {
		return null;
	}  
	
}
