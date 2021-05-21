package com.xtel.cms.ccsp.form.xemlien;


import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

//import java.text.DecimalFormat;
//import java.text.SimpleDateFormat;
import com.ibm.icu.text.DecimalFormat;
import com.ibm.icu.text.SimpleDateFormat;
import com.ligerdev.appbase.utils.encrypt.XBase64;
import com.ligerdev.cmms.component.AppCmmsUtils;
import com.ligerdev.cmms.component.dialog.DialogCaption;
import com.ligerdev.cmms.component.dialog.LDInputDialog;
import com.ligerdev.cmms.component.table.DialogSubmitResult;
import com.ligerdev.cmms.component.table.LDTableSimple;
import com.ligerdev.cmms.component.table.XGUI_Actions;
import com.ligerdev.cmms.component.table.XGUI_TableLayout;
import com.ligerdev.cmms.component.table.TableHeader;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.Page;
import com.vaadin.ui.Notification;
import com.xtel.cms.MainApplication;
import com.xtel.cms.ccsp.db.orm.others.TranhTaiShowbizReportBean;



public class FormTranhTaiShowbizTable extends LDTableSimple<Object> {

    public FormTranhTaiShowbizTable() {
        // TODO Auto-generated constructor stub
    }

    private FormTranhTaiShowbizLayout formMultiComponent;
    boolean isFirstLoad = false;
    String sql = null;
    
    @Override
    public void getPage(int pageIndex, boolean showNotify, String filter) {
    	isFirstLoad = true;
    	
        int index = (pageIndex - 1) * AppCmmsUtils.getPageSize();
        DecimalFormat nf = new DecimalFormat("###,###,###,###");

        Date from = formMultiComponent.from.getValue();
        Date to = formMultiComponent.to.getValue();

        if (from == null) {
        	Calendar calFrom = Calendar.getInstance();
            calFrom.add(Calendar.DAY_OF_MONTH, -7);
            from = calFrom.getTime();
        }
        if (to == null) {
            to = new Date();
        } else {
	    	Calendar calTo = Calendar.getInstance();
	        calTo.setTime(to);
	        calTo.add(Calendar.DAY_OF_MONTH, +1);
            to = calTo.getTime();
        }
        String key = this.getClass().getName()  + "." + from.toString() + to.toString() + "." + from.toString() + "." + to.toString();
        ArrayList<TranhTaiShowbizReportBean> listHis = (ArrayList<TranhTaiShowbizReportBean>) MainApplication.cache.getObject(key);

        if (listHis == null) {
            listHis = new ArrayList<TranhTaiShowbizReportBean>();

            sql = 
            		"select time_report, registered, unregistered, \r\n" + 
            		"(select count(id) from ns_action where star_id=a.top_pl1 and xdate=a.xdate) top_pl1, \r\n" + 
            		"(select count(id) from ns_action where star_id=a.top_pl2 and xdate=a.xdate) top_pl2, \r\n" + 
            		"(select count(id) from ns_action where star_id=a.top_pl3 and xdate=a.xdate) top_pl3, \r\n" + 
            		"(select count(id) from ns_action where star_id=a.top_pl4 and xdate=a.xdate) top_pl4, \r\n" + 
            		"sub_active total_charge_sub, total_fee \r\n" + 
            		"from \r\n" + 
            		"(\r\n" + 
            		"   select b.*, DATE_FORMAT(time,'%d/%m/%Y') time_report, \r\n" + 
            		"       (case when pkg_code = 'XEM1' then 'XEM1 (Ngày)' when pkg_code = 'XEM7' then 'XEM7 (Tuần)'  when pkg_code = 'TS' then 'Tranh tài Showbiz (Ngày)' else '' end) lable01, \r\n" + 
            		"        sub_active, registered, reg_sms, reg_ivr, reg_wap, unregistered, total_call, total_call_fee, total_charge_sub, charge_sub_success, ratio, total_sub_fee, total_fee\r\n" + 
            		"   from statistic_daily_revenue_details a join ns_topstar_dayli b on b.xdate = DATE_FORMAT(time,'%Y%m%d') \r\n" + 
            		"   where pkg_code = 'TS' and b.xdate >= " + new SimpleDateFormat("yyyyMMdd").format(from) +  " and b.xdate <= " + new SimpleDateFormat("yyyyMMdd").format(to) + " \r\n" + 
            		"        order by xdate desc\r\n" + 
            		") a"; 

            listHis = xbaseDAO.getListBySql(null, TranhTaiShowbizReportBean.class, sql, null, null);
            MainApplication.cache.put(key, listHis, 60 * 1);
        }
        if (listHis == null || listHis.size() == 0) {
            logger.info(mainApp.getTransid() + ", not found");
            notifShow("Không tìm thấy bản ghi nào", 2000);
            return;
        }
        int totalFound = listHis.size();
        logger.info(mainApp.getTransid() + ", totalFound: " + totalFound);
        ArrayList<TranhTaiShowbizReportBean> list = null;
        try {
            logger.info(mainApp.getTransid() + ", 1. index: " + index + ", to: " + (index + AppCmmsUtils.getPageSize()));
            list = new ArrayList<TranhTaiShowbizReportBean>(listHis.subList(index, index + AppCmmsUtils.getPageSize()));
        } catch (Exception e) {
            logger.info(mainApp.getTransid() + ", " + e.getMessage() + " | 2. index: " + index + ", to: " + (listHis.size()));
            list = new ArrayList<TranhTaiShowbizReportBean>(listHis.subList(index, listHis.size()));
        }
        BeanItemContainer<TranhTaiShowbizReportBean> data = new BeanItemContainer<TranhTaiShowbizReportBean>(TranhTaiShowbizReportBean.class, list);
        tableContainer = new XGUI_TableLayout(showNotify, pageIndex, totalFound, "", data, this, this, filter, true, null);
        /*
        private String timeReport;
    	private int registered;
    	private int unregistered;
    	private Integer topPl1;
    	private Integer topPl2;
    	private Integer topPl3;
    	private Integer topPl4;
    	private int totalChargeSub;
    	private int totalFee;
    	*/
        tableContainer.getTable().addVisibleColumn(
                new TableHeader("STT", "STT")
                , new TableHeader("timeReport", "Ngày")
                , new TableHeader("registered", "Thuê bao đăng ký")
                , new TableHeader("topPl1", "A")
                , new TableHeader("topPl2", "B")
                , new TableHeader("topPl3", "C")
                , new TableHeader("topPl4", "D")
                , new TableHeader("totalChargeSub", "Thuê bao active")
                , new TableHeader("totalFee", "Doanh thu gói theo ngày")
        );
        setContent(tableContainer);
    }


    @Override
    public String getPermission() {
        return XGUI_Actions.ROLE_VIEW_SEARCH_FILETER_EXPORT;
    }

    @Override
    public int deleteSelectedItem(ArrayList<Object> listSelected) {
        return 0;
    }

    @Override
    public void showAddDialog() {
    }

    @Override
    public void showSearchDialog() {
    }

    @Override
    public void showEditDialog(Object selectedItem, Object colID) {
    }

    @Override
    public void showCopyDialog(Object selectedItem) {
    }

    @Override
    public void exportTable() {
    	if(sql == null) {
    		return;
    	}
        String sqlEncode = null;
        try {
        	sqlEncode = URLEncoder.encode(XBase64.encode(sql), "UTF-8"); 
        } catch (Exception e) {
        }
        logger.info("[EXPORT] sql encode: " + sqlEncode);
        String expUrl = "/api/get/exportQuery.jsp?sql=" + sqlEncode;
        getUI().getPage().open(expUrl, "_blank");
    }

    @Override
    public DialogSubmitResult dialogSubmit_(Object item, LDInputDialog<Object> dialog) {

        if (DialogCaption.ADD.equals(dialog.getDCaption()) || DialogCaption.COPY.equals(dialog.getDCaption())) {
			/*try {
				String sql = "select 1 from user_cms where status = 1 and username = ?";
				if(baseDAO.hasResult(mainApp.getTransid(), sql, item.getUsername())){
					return "Duplicate username, plz reenter!";
				}
				item.setPassword("123456");
				int id = baseDAO.insertBean(mainApp.getTransid(), item);
				item.setId(id);

			} catch (Exception e) {
			}*/
            return null;
        }
        if (DialogCaption.EDIT.equals(dialog.getDCaption())) {
			/*int rs = 0;
			try {
				String sql = "select 1 from user_cms where status = 1 and username = ? and id != " + item.getId();
				if(baseDAO.hasResult(mainApp.getTransid(), sql, item.getUsername())){
					return "Duplicate username, plz reenter!";
				}
				rs = baseDAO.updateBean(mainApp.getTransid(), item);
			} catch (Exception e) {
			}
			if(rs <= 0) {
				return "Can not update database ....";
			}
			return null;*/
        }
        return null;
    }

    public FormTranhTaiShowbizLayout getFormMultiComponent() {
        return formMultiComponent;
    }

    public void setFormMultiComponent(FormTranhTaiShowbizLayout formMultiComponent) {
        this.formMultiComponent = formMultiComponent;
    }

    public static void notifShow(String content, int timeShow) {
        Notification notif = new Notification(
                content,
                "",
                Notification.Type.WARNING_MESSAGE);
        notif.setDelayMsec(timeShow);
        notif.show(Page.getCurrent());
        return;
    }

    public static void notifShowError(String content, int timeShow) {
        Notification notif = new Notification(
                content,
                "",
                Notification.Type.ERROR_MESSAGE);
        notif.setDelayMsec(timeShow);
        notif.show(Page.getCurrent());
        return;
    }
}
