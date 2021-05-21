package com.xtel.cms.ccsp.form.report;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashMap;

import com.ibm.icu.text.SimpleDateFormat;
import com.ligerdev.appbase.utils.BaseUtils;
import com.ligerdev.appbase.utils.IRetrieveMap;
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
import com.xtel.cms.ccsp.db.orm.others.CCSPReportPriceGroup;
import com.xtel.cms.utils.AppUtils;
import com.xtel.cms.utils.XDataBean;

public class FormCCSPTableReportGroupPrice extends LDTableSimple<CCSPReportPriceGroup>{
	
	private Date dateFrom;
	private Date dateTo;
	private String serviceName;
	private XDataBean xdata;
	private ComboBox cpids;

	public FormCCSPTableReportGroupPrice(ComboBox cpids, XDataBean xdata, Date dateFrom, Date dateTo) {
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
		//xbaseDAO.setIgnoreMissingCol(false);
		ArrayList<CCSPReportPriceGroup> list = xbaseDAO.getListBySql(mainApp.getTransid(), CCSPReportPriceGroup.class, sql, null, null);
		//xbaseDAO.setIgnoreMissingCol(true);
		
		int totalFound = list.size();
		long totalFee = 0;
		long totalChargeTimes = 0;
		LinkedHashMap<String, Long> totalMonth = new LinkedHashMap<>(); 
		
		if(list != null && list.size() > 0) {
			logger.info(mainApp.getTransid() + ", firstObj: " + list.get(0).toString());
			Collections.sort(list, new Comparator<CCSPReportPriceGroup>() {
				@Override
				public int compare(CCSPReportPriceGroup o1, CCSPReportPriceGroup o2) {
					try {
						//SimpleDateFormat nf = new SimpleDateFormat("dd/MM/yyyy");
						return o2.getDateReport().compareTo(o1.getDateReport());
					} catch (Exception e) {
					}
					return 0;
				}
			});
			for(CCSPReportPriceGroup b: list) {
				totalFee += BaseUtils.parseInt(b.getFee(), 0); 
				totalChargeTimes += b.getCount();  
				totalMonth.put(b.getPriceGroup() + "", (totalMonth.get(b.getPriceGroup() + "") == null ? 0 : totalMonth.get(b.getPriceGroup() + "")) + b.getCount());  
			}
		}
		// -------add row total tự động
		CCSPReportPriceGroup rowTotal = null;
		ArrayList<String> listFields = new ArrayList<String>();
		try {
			listFields.add("count");
			listFields.add("fee");
			rowTotal = AppUtils.getItemTotal(list, CCSPReportPriceGroup.class, listFields);
			list.add(rowTotal);
		} catch (Exception e) {
		}
		XOptions option = new XOptions();
		option.rowTotal_title = "<b>Tổng trang:</b>";
		option.rowTotal_title_colID = "dateReport";
		option.rowTotal_object = rowTotal;
		option.rowTotal_listTotalCol = listFields;
		
		BeanItemContainer<CCSPReportPriceGroup> data = new BeanItemContainer<CCSPReportPriceGroup>(CCSPReportPriceGroup.class, list);
		String cpidStr = cpids != null ? " (cpid = " + cpids.getItemCaption(cpids.getValue()).toString() + ")" : "";
		String titleTable = serviceName + cpidStr + " - Doanh thu theo mệnh giá: "  + new SimpleDateFormat("dd-MM-yyyy").format(dateFrom) + " đến " + new SimpleDateFormat("dd-MM-yyyy").format(dateTo);
		tableContainer = new XGUI_TableLayout(false, pageIndex, totalFound, titleTable, data, this, this, filter, false, option);

		// manual set visible columns 
		tableContainer.getTable().addVisibleColumn(
				 new TableHeader("dateReport", "Ngày")
				, new TableHeader("priceGroup", "Mệnh Giá")
				, new TableHeader("count", "Lượt charge")
				, new TableHeader("fee", "Doanh thu").setFormatNumber(true) 
		);
		final NumberFormat nf = new DecimalFormat("###,###,###,###");
		/*final StringBuilder str = new StringBuilder(" - Tổng cước: " + new SimpleDateFormat("MM-yyyy")
				.format(layoutParent.month.getValue()) + ": " +  nf.format(total)); 
		try {
			BaseUtils.retrieveMap(totalMonth, new IRetrieveMap<String, Long>() {
				@Override
				public void found(String key, Long value) throws Exception {
					long fee = value * Integer.parseInt(key);
					str.append("<br/> - Mệnh giá " + key + " đ = " + value + " Lượt = " + nf.format(fee) + " đ"); 
				}
			});
		} catch (Exception e) {
		}
		Label title = new Label("<h3 style='color: blue'>" + str.toString() + "</h3>", ContentMode.HTML);*/
		
		final StringBuilder str = new StringBuilder("<table border=\"1\" width=\"40%\" style=\"border-collapse: collapse;\">");
		str.append("<br/><br/><th>Mệnh giá</th><th>lượt charge</th><th>doanh thu (vnđ)</th>");
		try {
			BaseUtils.retrieveMap(totalMonth, new IRetrieveMap<String, Long>() {
				@Override
				public void found(String key, Long value) throws Exception {
					long fee = value * Integer.parseInt(key);
					str.append("<tr><td>" + key + "</td><td>" + nf.format(value) + "</td><td>" + nf.format(fee) + "</td></tr>");  
				}
			});
		} catch (Exception e) { 
		}
		str.append("<tr><td><b>Tổng</b></td>   <td><b>" + nf.format(totalChargeTimes) + "</b></td>  <td><b>" + nf.format(totalFee) + "</b></td></tr></table><br/><br/><br/>"); 
		// Label title = new Label("<h3 style='color: blue'>" + str.toString() + "</h3>", ContentMode.HTML);
		Label title = new Label(str.toString(), ContentMode.HTML);
		
		tableContainer.addComponent(title);
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
					"select DATE_FORMAT(STR_TO_DATE(hisnote1, '%Y%m%d'), '%Y-%m-%d') date_report, fee price_group, count(fee) count, sum(fee) fee from "
							+ table + " where result = 0 " + tempTime + cpidStr + " and fee > 0 group by date_report, price_group" ;
			
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
		return null;
	}

	@Override
	public void exportTable() {
	}

	@Override
	public void showEditDialog(CCSPReportPriceGroup selectedItem, Object colID) {
	}

	@Override
	public void showCopyDialog(CCSPReportPriceGroup selectedItem) {
	}

	@Override
	public int deleteSelectedItem(ArrayList<CCSPReportPriceGroup> listSelected) {
		return 0;
	}

	@Override
	public DialogSubmitResult dialogSubmit_(CCSPReportPriceGroup item, LDInputDialog<CCSPReportPriceGroup> dialog) {
		return null;
	} 
}


