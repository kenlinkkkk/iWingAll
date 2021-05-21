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
import com.xtel.cms.ccsp.CCSPAppUtils;
import com.xtel.cms.ccsp.db.orm.CCSPPkgPolicy;
import com.xtel.cms.ccsp.db.orm.others.CCSPReportChannel;
import com.xtel.cms.ccsp.db.orm.others.CCSPReportPKG;
import com.xtel.cms.utils.AppUtils;
import com.xtel.cms.utils.XDataBean;

public class FormCCSPTableReportPkg extends LDTableSimple<CCSPReportPKG>{
	
	private Date dateFrom;
	private Date dateTo;
	private String serviceName;
	private XDataBean xdata;
	private ComboBox cpids;

	public FormCCSPTableReportPkg(ComboBox cpids, XDataBean xdata, Date dateFrom, Date dateTo) {
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
		ArrayList<CCSPReportPKG> list = xbaseDAO.getListBySql(mainApp.getTransid(), CCSPReportPKG.class, sql, null, null);
		int totalFound = list.size();
		long total = 0;
		
		sql = "select * from pkg_policy where status = 1";
		final ArrayList<CCSPPkgPolicy> listCCSPPkgPolicy = xbaseDAO.getListBySql(mainApp.getTransid(), CCSPPkgPolicy.class, sql, null, null);
		LinkedHashMap<String, Long> totalMonth = new LinkedHashMap<>(); 
		
		if(list != null) {
			Collections.sort(list, new Comparator<CCSPReportPKG>() {
				@Override
				public int compare(CCSPReportPKG o1, CCSPReportPKG o2) {
					try {
						//SimpleDateFormat nf = new SimpleDateFormat("dd/MM/yyyy");
						return o2.getDateReport().compareTo(o1.getDateReport());
					} catch (Exception e) {
					}
					return 0;
				}
			});
			for(CCSPReportPKG b: list) {
				total += BaseUtils.parseInt(b.getFee(), 0); 
				totalMonth.put(b.getPkgCode() + "", (totalMonth.get(b.getPkgCode()) == null ? 0 : totalMonth.get(b.getPkgCode())) + BaseUtils.parseLong(b.getFee(), 0L));  
				CCSPPkgPolicy p = CCSPAppUtils.getPkgPolicy(listCCSPPkgPolicy, b.getPkgCode());
				
				if(p == null) {
					logger.info(mainApp.getTransid() + ", not found PKG: " + b.getPkgCode()); 
					continue;
				}
				// enrich
				b.setNote(p.getNote1()); 
				b.setPkgDesc(p.getDesc());
				b.setFeeDesc(p.getFeeDesc()); 
			}
		}
		// -------add row total tự động
		CCSPReportPKG rowTotal = null;
		ArrayList<String> listFields = new ArrayList<String>();
		try {
			listFields.add("count");
			listFields.add("fee");
			rowTotal = AppUtils.getItemTotal(list, CCSPReportPKG.class, listFields);
			rowTotal.setDateReport("<b>Tổng:</b>");
			list.add(rowTotal);
		} catch (Exception e) {
		}
		XOptions option = new XOptions();
		option.rowTotal_title = "<b>Tổng trang:</b>";
		option.rowTotal_title_colID = "dateReport";
		option.rowTotal_object = rowTotal;
		option.rowTotal_listTotalCol = listFields;
		
		BeanItemContainer<CCSPReportPKG> data = new BeanItemContainer<CCSPReportPKG>(CCSPReportPKG.class, list);
		String cpidStr = cpids != null ? " (cpid = " + cpids.getItemCaption(cpids.getValue()).toString() + ")" : "";
		String titleTable = serviceName + cpidStr + " - Doanh thu theo gói: "  + new SimpleDateFormat("dd-MM-yyyy").format(dateFrom) + " đến " + new SimpleDateFormat("dd-MM-yyyy").format(dateTo);
		tableContainer = new XGUI_TableLayout(false, pageIndex, totalFound, titleTable, data, this, this, filter, false, option);

		// manual set visible columns 
		tableContainer.getTable().addVisibleColumn(
				 new TableHeader("dateReport", "Ngày")
				, new TableHeader("pkgCode", "Mã gói")
				, new TableHeader("pkgDesc", "Mô tả gói")
				, new TableHeader("note", "Note")
				, new TableHeader("feeDesc", "Giá gói")
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
					CCSPPkgPolicy p = CCSPAppUtils.getPkgPolicy(listCCSPPkgPolicy, key);
					long fee = value * p.getRenewFee();
					str.append("<br/> - Mệnh giá " + key + "đ: " + value + " Lượt = " + nf.format(fee) + " đ"); 
				}
			});
		} catch (Exception e) {
		}
		Label title = new Label("<h3 style='color: blue'>" + str.toString() + "</h3>", ContentMode.HTML);*/
		
		final StringBuilder str = new StringBuilder("<table border=\"1\" width=\"30%\" style=\"border-collapse: collapse;\">");
		str.append("<br/><br/><th>Gói cước</th><th>doanh thu (vnđ)</th>");
		try {
			BaseUtils.retrieveMap(totalMonth, new IRetrieveMap<String, Long>() {
				@Override
				public void found(String key, Long value) throws Exception {
					str.append("<tr><td>" + key + "</td><td>" + nf.format(value) + "</td></tr>");  
				}
			});
		} catch (Exception e) { 
		}
		str.append("<tr><td><b>Tổng</b></td>  <td><b>" + nf.format(total) + "</b></td></tr></table><br/><br/><br/>"); 
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
					"select DATE_FORMAT(STR_TO_DATE(hisnote1, '%Y%m%d'), '%Y-%m-%d') date_report, pkg_code, count(fee) count, sum(fee) fee from "
							+ table + " where result = 0 and fee > 0 " + tempTime + cpidStr + " group by date_report, pkg_code" ;
			
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
	public void showEditDialog(CCSPReportPKG selectedItem, Object colID) {
	}

	@Override
	public void showCopyDialog(CCSPReportPKG selectedItem) {
	}

	@Override
	public int deleteSelectedItem(ArrayList<CCSPReportPKG> listSelected) {
		return 0;
	}

	@Override
	public DialogSubmitResult dialogSubmit_(CCSPReportPKG item, LDInputDialog<CCSPReportPKG> dialog) {
		return null;
	} 
}


