package com.xtel.cms.ccsp.form.others;


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
import com.xtel.cms.ccsp.db.orm.others.ThongKeDanhGiaHieuQuaCTKMBean;

public class FormCMSThongKeDanhGiaHieuQuaCTKM extends LDTableSimple<Object> {
    static {
        // System.out.println("###########################");
        // BaseUtils.setMyDir("/media/Data/UWorkspace/Hitech/VaadinCms7/");
    }

    public FormCMSThongKeDanhGiaHieuQuaCTKM() {
        // TODO Auto-generated constructor stub
    }

    private FormCMSLayoutThongKeDanhGiaHieuQuaCTKM formMultiComponent;
   	boolean isFirstLoad = false;
    
    @Override
    public void getPage(int pageIndex, boolean showNotify, String filter) {
    	isFirstLoad = true;
    	
        int index = (pageIndex - 1) * AppCmmsUtils.getPageSize();
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
        String key = this.getClass().getName()  + "." + from.toString() + to.toString();
        ArrayList<ThongKeDanhGiaHieuQuaCTKMBean> listHis = (ArrayList<ThongKeDanhGiaHieuQuaCTKMBean>) MainApplication.cache.getObject(key);

        if (listHis == null) {
            listHis = new ArrayList<ThongKeDanhGiaHieuQuaCTKMBean>();

            String sql = 
            		"select " +
                    "x.id,x.name,x.begin_time,x.end_time,x.noidung, " +
                    "concat(DATE_FORMAT(x.begin_time, '%d/%m/%Y'),' đến ',DATE_FORMAT(x.end_time, '%d/%m/%Y')) time_ctkm, " +
                    "x.count_dk, " +
                    "x.count_huy, " +
                    "x.count_active, " +
                    "x.sum_topupfee, " +
                    "y.count_psc, " +
                    "z.sum_fee_kybaocao, " +
                    "z.sum_fee_luyke, " +
                    "0 chenhlech " +
                    "from " +
                    "(select  " +
                    "a.id, " +
                    "sum(1) count_dk, " +
                    "sum(case when b.deactive24h_time is not null and 1=1 then 1 else 0 end) count_huy, " +
                    "sum(case when b.deactive24h_time is null and 1=1 then 1 else 0 end) count_active, " +
                    "sum(case when (b.deactive24h_time is null or b.deactive24h_time>b.created_time+interval 1 day) and (b.renew24h_time is not null) and b.kmtq_id is not null and c.status=2 then c.topup_fee else 0 end) sum_topupfee, " +
                    "a.name,a.begin_time,a.end_time,a.noidung  " +
                    "from dm_ctkm a join stats_kmtq b  " +
                    "on a.category=b.category " +
                    "left join kmtq c  " +
                    "on b.created_time=c.created_time and b.msisdn=c.msisdn " +
                    "where a.status=1 " +
                    "and b.created_time<'" + new SimpleDateFormat("yyyy-MM-dd").format(to) + "' + interval 1 day " +
                    "and b.created_time>='" + new SimpleDateFormat("yyyy-MM-dd").format(from) + "' " +
                    "group by a.id) x " +
                    "join " +
                    "(select c.id,count(c.msisdn) count_psc " +
                    "from " +
                    "(select  " +
                    "distinct a.id,b.msisdn " +
                    "from dm_ctkm a  " +
                    "join his_renew_kmtq b " +
                    "on a.category=b.category " +
                    "where a.status=1 " +
                    "and b.active_time<'" + new SimpleDateFormat("yyyy-MM-dd").format(to) + "' + interval 1 day " +
                    "and b.active_time>='" + new SimpleDateFormat("yyyy-MM-dd").format(from) + "' " +
                    "and b.created_time<'" + new SimpleDateFormat("yyyy-MM-dd").format(to) + "' + interval 1 day " +
                    "and b.created_time>='" + new SimpleDateFormat("yyyy-MM-dd").format(from) + "' " +
                    ")c " +
                    "group by c.id) y " +
                    "on x.id=y.id " +
                    "join " +
                    "(select  " +
                    "a.id, " +
                    //hỏi xem có lấy renew của bọn đăng ký trước kỳ. nếu không lấy thì thêm active>= fromdate
                    "sum(case when b.created_time>='" + new SimpleDateFormat("yyyy-MM-dd").format(from) + "' then fee else 0 end) sum_fee_kybaocao, " +
                    "sum(case when 1=1 then fee else 0 end) sum_fee_luyke " +
                    "from dm_ctkm a  " +
                    "join his_renew_kmtq b " +
                    "on a.category=b.category " +
                    "where a.status=1  " +
                    "and b.active_time<'" + new SimpleDateFormat("yyyy-MM-dd").format(to) + "' + interval 1 day " +
                    "and b.created_time<'" + new SimpleDateFormat("yyyy-MM-dd").format(to) + "' + interval 1 day " +
                    "group by a.id) z " +
                    "on y.id=z.id ";

            listHis = xbaseDAO.getListBySql(null, ThongKeDanhGiaHieuQuaCTKMBean.class, sql, null, null);
            MainApplication.cache.put(key, listHis, 60 * 1);
        }
        if (listHis == null || listHis.size() == 0) {
            logger.info(mainApp.getTransid() + ", not found");
            notifShow("Không tìm thấy bản ghi nào", 2000);
            return;
        }
        DecimalFormat nf = new DecimalFormat("###,###,###,###");

        for (ThongKeDanhGiaHieuQuaCTKMBean listHi : listHis) {
            listHi.setCountActive(format(listHi.getCountActive()));
            listHi.setCountDk(format(listHi.getCountDk()));
            listHi.setCountHuy(format(listHi.getCountHuy()));
            listHi.setSumFeeKybaocao(format(listHi.getSumFeeKybaocao()));
            listHi.setCountPsc(format(listHi.getCountPsc()));
            listHi.setSumFeeLuyke(format(listHi.getSumFeeLuyke()));
            listHi.setSumTopupfee(format(listHi.getSumTopupfee()));
        }

        int totalFound = listHis.size();
        logger.info(mainApp.getTransid() + ", totalFound: " + totalFound);
        ArrayList<ThongKeDanhGiaHieuQuaCTKMBean> list = null;
        try {
            logger.info(mainApp.getTransid() + ", 1. index: " + index + ", to: " + (index + AppCmmsUtils.getPageSize()));
            list = new ArrayList<ThongKeDanhGiaHieuQuaCTKMBean>(listHis.subList(index, index + AppCmmsUtils.getPageSize()));
        } catch (Exception e) {
            logger.info(mainApp.getTransid() + ", " + e.getMessage() + " | 2. index: " + index + ", to: " + (listHis.size()));
            list = new ArrayList<ThongKeDanhGiaHieuQuaCTKMBean>(listHis.subList(index, listHis.size()));
        }

        BeanItemContainer<ThongKeDanhGiaHieuQuaCTKMBean> data = new BeanItemContainer<ThongKeDanhGiaHieuQuaCTKMBean>(ThongKeDanhGiaHieuQuaCTKMBean.class, list);
        tableContainer = new XGUI_TableLayout(showNotify, pageIndex, totalFound, "Thống kê đánh giá hiệu quả CTKM", data, this, this, filter, true, null);

        tableContainer.getTable().addVisibleColumn(
                new TableHeader("STT", "STT")
                , new TableHeader("name", "Tên CTKM")
                , new TableHeader("beginTime", "Thời gian triển khai CTKM")
                , new TableHeader("noidung", "Hình thức cộng thưởng")
                , new TableHeader("countDk", "TB đăng ký CTKM")
                , new TableHeader("countHuy", "TB hủy")
                , new TableHeader("countActive", "TB active")
                , new TableHeader("countPsc", "TB PSC")
                , new TableHeader("sumFeeKybaocao", "Doanh thu kỳ báo cáo")
                , new TableHeader("sumFeeLuyke", "Doanh thu lũy kế")
                , new TableHeader("sumTopupfee", "Chi phí khuyến mãi")
                , new TableHeader("chenhlech", "Chênh lệch")
        );
        // manual remove some visible columns
        tableContainer.getTable().removeVisibleColumn("password");
        setContent(tableContainer);
    }


    private String format(String countActive) {
    	try {
    		DecimalFormat nf = new DecimalFormat("###,###,###,###");
    		return nf.format(Long.parseLong(countActive)); 
		} catch (Exception e) {
		}
		return countActive;
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
        String sql = "select " +
                "x.id,x.name,x.begin_time,x.end_time,x.noidung, " +
                "concat(DATE_FORMAT(x.begin_time, '%d/%m/%Y'),' đến ',DATE_FORMAT(x.end_time, '%d/%m/%Y')) time_ctkm, " +
                "x.count_dk, " +
                "x.count_huy, " +
                "x.count_active, " +
                "x.sum_topupfee, " +
                "y.count_psc, " +
                "z.sum_fee_kybaocao, " +
                "z.sum_fee_luyke, " +
                "0 chenhlech " +
                "from " +
                "(select  " +
                "a.id, " +
                "sum(1) count_dk, " +
                "sum(case when b.deactive24h_time is not null and 1=1 then 1 else 0 end) count_huy, " +
                "sum(case when b.deactive24h_time is null and 1=1 then 1 else 0 end) count_active, " +
                "sum(case when (b.deactive24h_time is null or b.deactive24h_time>b.created_time+interval 1 day) and (b.renew24h_time is not null) and b.kmtq_id is not null and c.status=2 then c.topup_fee else 0 end) sum_topupfee, " +
                "a.name,a.begin_time,a.end_time,a.noidung  " +
                "from dm_ctkm a join stats_kmtq b  " +
                "on a.category=b.category " +
                "left join kmtq c  " +
                "on b.created_time=c.created_time and b.msisdn=c.msisdn " +
                "where a.status=1 " +
                "and b.created_time<'" + new SimpleDateFormat("yyyy-MM-dd").format(formMultiComponent.to.getValue()) + "' + interval 1 day " +
                "and b.created_time>='" + new SimpleDateFormat("yyyy-MM-dd").format(formMultiComponent.from.getValue()) + "' " +
                "group by a.id) x " +
                "join " +
                "(select c.id,count(c.msisdn) count_psc " +
                "from " +
                "(select  " +
                "distinct a.id,b.msisdn " +
                "from dm_ctkm a  " +
                "join his_renew_kmtq b " +
                "on a.category=b.category " +
                "where a.status=1 " +
                "and b.active_time<'" + new SimpleDateFormat("yyyy-MM-dd").format(formMultiComponent.to.getValue()) + "' + interval 1 day " +
                "and b.active_time>='" + new SimpleDateFormat("yyyy-MM-dd").format(formMultiComponent.from.getValue()) + "' " +
                "and b.created_time<'" + new SimpleDateFormat("yyyy-MM-dd").format(formMultiComponent.to.getValue()) + "' + interval 1 day " +
                "and b.created_time>='" + new SimpleDateFormat("yyyy-MM-dd").format(formMultiComponent.from.getValue()) + "' " +
                ")c " +
                "group by c.id) y " +
                "on x.id=y.id " +
                "join " +
                "(select  " +
                "a.id, " +
                //hỏi xem có lấy renew của bọn đăng ký trước kỳ. nếu không lấy thì thêm active>= fromdate
                "sum(case when b.created_time>='" + new SimpleDateFormat("yyyy-MM-dd").format(formMultiComponent.from.getValue()) + "' then fee else 0 end) sum_fee_kybaocao, " +
                "sum(case when 1=1 then fee else 0 end) sum_fee_luyke " +
                "from dm_ctkm a  " +
                "join his_renew_kmtq b " +
                "on a.category=b.category " +
                "where a.status=1  " +
                "and b.active_time<'" + new SimpleDateFormat("yyyy-MM-dd").format(formMultiComponent.to.getValue()) + "' + interval 1 day " +
                "and b.created_time<'" + new SimpleDateFormat("yyyy-MM-dd").format(formMultiComponent.to.getValue()) + "' + interval 1 day " +
                "group by a.id) z " +
                "on y.id=z.id ";
        SimpleDateFormat sf = new SimpleDateFormat("yyyyMMdd");
        try {
            // sql = URLEncoder.encode(new BASE64Encoder().encode(sql.getBytes()), "utf-8");
        	sql = URLEncoder.encode(XBase64.encode(sql), "UTF-8"); 
        } catch (Exception e) {
            e.printStackTrace();
        }
        logger.info("[EXPORT] sql encode: " + sql);
        String expUrl = "/api/get/exportQuery.jsp?" + sql;
        getUI().getPage().open(expUrl, "_blank");
    }

    @Override
    public DialogSubmitResult dialogSubmit_(Object item, LDInputDialog<Object> dialog) {
        if (DialogCaption.ADD.equals(dialog.getDCaption()) || DialogCaption.COPY.equals(dialog.getDCaption())) {
            return null;
        }
        if (DialogCaption.EDIT.equals(dialog.getDCaption())) {
        }
        return null;
    }

    public FormCMSLayoutThongKeDanhGiaHieuQuaCTKM getFormMultiComponent() {
        return formMultiComponent;
    }

    public void setFormMultiComponent(FormCMSLayoutThongKeDanhGiaHieuQuaCTKM formMultiComponent) {
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
