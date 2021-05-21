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
import com.xtel.cms.ccsp.db.orm.others.ThueBaoDangKyCTKMBean;
import com.xtel.cms.utils.AppUtils;



public class FormCMSThueBaoDangKyCTKM extends LDTableSimple<Object> {
    static {
        // System.out.println("###########################");
        // BaseUtils.setMyDir("/media/Data/UWorkspace/Hitech/VaadinCms7/");
    }

    public FormCMSThueBaoDangKyCTKM() {
        // TODO Auto-generated constructor stub
    }

    private FormCMSLayoutThongKeDanhGiaHieuQuaCTKM formMultiComponent;
    boolean isFirstLoad = false;
    
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
        String key = this.getClass().getName()  + "." + from.toString() + to.toString();
        ArrayList<ThueBaoDangKyCTKMBean> listHis = (ArrayList<ThueBaoDangKyCTKMBean>) MainApplication.cache.getObject(key);

        if (listHis == null) {
            listHis = new ArrayList<ThueBaoDangKyCTKMBean>();
            ArrayList<String> listMonth = AppUtils.getListMonth(from, to);

            String sql = "select " +
                    "a.msisdn, " +
                    "a.created_time, " +
                    "case when (a.deactive24h_time is null or a.deactive24h_time>a.created_time+interval 1 day) and (a.renew24h_time is not null) then 'x' else '' end dudk, " +
                    "case when (a.deactive24h_time is null or a.deactive24h_time>a.created_time+interval 1 day) and (a.renew24h_time is null) then 'x' else '' end chuadapung, " +
                    "case when a.deactive24h_time<a.created_time+interval 1 day then 'x' else '' end huytruoc24h, " +
                    "b.topup_time,c.topup_fee, " +
                    "case when b.status=2 then 'x' else '' end resultok, " +
                    "case when b.status=3 then 'x' else '' end resultnok " +
                    "from  " +
                    "dm_ctkm c " +
                    "join stats_kmtq a  " +
                    "on c.category=a.category " +
                    "left join kmtq b  " +
                    "on a.kmtq_id=b.id " +
                    "where c.status=1 " +
                    "and a.created_time>='" + new SimpleDateFormat("yyyy-MM-dd").format(from) + "' and a.created_time<'" + new SimpleDateFormat("yyyy-MM-dd").format(to) + "' + interval 1 day ";

            listHis = xbaseDAO.getListBySql(null, ThueBaoDangKyCTKMBean.class, sql, null, null);
            MainApplication.cache.put(key, listHis, 60 * 1);
        }
        if (listHis == null || listHis.size() == 0) {
            logger.info(mainApp.getTransid() + ", not found");
            notifShow("Không tìm thấy bản ghi nào", 2000);
            return;
        }
        int totalFound = listHis.size();
        logger.info(mainApp.getTransid() + ", totalFound: " + totalFound);

        ArrayList<ThueBaoDangKyCTKMBean> list = null;
        try {
            logger.info(mainApp.getTransid() + ", 1. index: " + index + ", to: " + (index + AppCmmsUtils.getPageSize()));
            list = new ArrayList<ThueBaoDangKyCTKMBean>(listHis.subList(index, index + AppCmmsUtils.getPageSize()));
        } catch (Exception e) {
            logger.info(mainApp.getTransid() + ", " + e.getMessage() + " | 2. index: " + index + ", to: " + (listHis.size()));
            list = new ArrayList<ThueBaoDangKyCTKMBean>(listHis.subList(index, listHis.size()));
        }
        BeanItemContainer<ThueBaoDangKyCTKMBean> data = new BeanItemContainer<ThueBaoDangKyCTKMBean>(ThueBaoDangKyCTKMBean.class, list);
        tableContainer = new XGUI_TableLayout(showNotify, pageIndex, totalFound, "Thuê bao đăng ký CTKM", data, this, this, filter, true, null);

        tableContainer.getTable().addVisibleColumn(
                new TableHeader("STT", "STT")
                , new TableHeader("msisdn", "msisdn")
                , new TableHeader("createdTime", "Ngày đăng ký")
                , new TableHeader("dudk", "Thỏa mãn được hưởng khuyến mãi").addMapIcon("x", "icons/16/v.png", "")
                , new TableHeader("chuadapung", "TB chưa đáp ứng").addMapIcon("x", "icons/16/v.png", "")
                , new TableHeader("huytruoc24h", "TB chưa đáp ứng do hủy trước 24h").addMapIcon("x", "icons/16/v.png", "")
                , new TableHeader("topupFee", "Giá trị KM")
                , new TableHeader("topupTime", "Thời gian cộng thưởng KM")
                , new TableHeader("resultok", "Kết quả cộng thưởng OK")
                , new TableHeader("resultnok", "Kết quả cộng thưởng NOK")
        );
        // manual remove some visible columns
        tableContainer.getTable().removeVisibleColumn("password");
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
        String sql = "select " +
                "a.msisdn, " +
                "a.created_time, " +
                "case when (a.deactive24h_time is null or a.deactive24h_time>a.created_time+interval 1 day) and (a.renew24h_time is not null) then 'x' else '' end dudk, " +
                "case when (a.deactive24h_time is null or a.deactive24h_time>a.created_time+interval 1 day) and (a.renew24h_time is null) then 'x' else '' end chuadapung, " +
                "case when a.deactive24h_time<a.created_time+interval 1 day then 'x' else '' end huytruoc24h, " +
                "b.topup_time,c.topup_fee, " +
                "case when b.status=2 then 'x' else '' end resultok, " +
                "case when b.status=3 then 'x' else '' end resultnok " +
                "from  " +
                "dm_ctkm c " +
                "join stats_kmtq a  " +
                "on c.category=a.category " +
                "left join kmtq b  " +
                "on a.kmtq_id=b.id " +
                "where c.status=1 " +
                "and a.created_time>='" + new SimpleDateFormat("yyyy-MM-dd").format(formMultiComponent.from.getValue()) + "' and a.created_time<'" + new SimpleDateFormat("yyyy-MM-dd").format(formMultiComponent.to.getValue()) + "' + interval 1 day ";

        SimpleDateFormat sf = new SimpleDateFormat("yyyyMMdd");
        try {
            // sql = URLEncoder.encode(new BASE64Encoder().encode(sql.getBytes()), "utf-8");
        	sql = URLEncoder.encode(XBase64.encode(sql), "UTF-8"); 
        } catch (Exception e) {
            e.printStackTrace();
        }
        logger.info("[EXPORT] sql encode: " + sql);
        String expUrl = "/api/get/exportQuery.jsp?sql=" + sql;
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
