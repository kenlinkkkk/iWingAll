package com.xtel.cms.ccsp.form.xemlien;


import com.ligerdev.appbase.utils.db.BaseDAO;
import com.ligerdev.appbase.utils.textbase.Log4jLoader;
import com.ligerdev.cmms.component.AppCmmsUtils;
import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.ui.*;
import com.xtel.cms.MainApplication;
import org.apache.log4j.Logger;

import java.util.Calendar;
import java.util.Date;

public class FormTranhTaiShowbizLayout extends VerticalLayout {
    protected static Logger logger = Log4jLoader.getLogger();
    protected static BaseDAO baseDAO = BaseDAO.getInstance(BaseDAO.POOL_NAME_MAIN);
    protected MainApplication mainApp = (MainApplication) MainApplication.getCurrent();

    public DateField from;
    public DateField to;

    public FormTranhTaiShowbizLayout() {
    	//setSizeUndefined();
    	setSizeFull();
        setImmediate(true);

        // -------------- pannel time, msisdn, button search ...
        from = AppCmmsUtils.createDateField("From", false, Resolution.DAY);
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -7);
        from.setValue(calendar.getTime());
        from.setDateFormat("dd/MM/yyyy"); 
        
        to = AppCmmsUtils.createDateField("To", false, Resolution.DAY);
        to.setValue(new Date());
        to.setDateFormat("dd/MM/yyyy"); 
        Button button = new Button("Submit");

        HorizontalLayout h1 = new HorizontalLayout();
        h1.setSpacing(true);
        h1.addComponent(AppCmmsUtils.createLabel("", "1cm"));
        h1.setImmediate(true);

        h1.addComponent(from);
        h1.addComponent(AppCmmsUtils.createLabel("", "1cm"));
        h1.addComponent(to);
        h1.addComponent(AppCmmsUtils.createLabel("", "1cm"));
        h1.addComponent(button);
        h1.setComponentAlignment(button, Alignment.MIDDLE_RIGHT);
        h1.setHeight("1.5cm");
        addComponent(h1);

        final TabSheet tabsheet = new TabSheet(); 
        tabsheet.setSizeFull();
        
        final FormTranhTaiShowbizTable formTableAction = new FormTranhTaiShowbizTable();
        formTableAction.setFormMultiComponent(this);
        tabsheet.addTab(formTableAction, "Th???ng k?? b??nh ch???n g??i Tranh t??i Showbiz");
        addComponent(tabsheet);

        // --------------- button listener
        button.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                
                if (tabsheet.getSelectedTab() instanceof FormTranhTaiShowbizTable) {
                    logger.info(mainApp.getTransid() + ", tab FormCMSThueBaoDangKyCTKM");
                    FormTranhTaiShowbizTable f = (FormTranhTaiShowbizTable) tabsheet.getSelectedTab();
                    f.getPage(1, true, null);
                }
            }
        });
        tabsheet.addSelectedTabChangeListener(new TabSheet.SelectedTabChangeListener() {
            @Override
            public void selectedTabChange(TabSheet.SelectedTabChangeEvent event) {
                logger.info(mainApp.getTransid() + ", tab changed: " + tabsheet.getSelectedTab().getClass().getName());
                if (tabsheet.getSelectedTab() instanceof FormTranhTaiShowbizTable) {
                    logger.info(mainApp.getTransid() + ", tab FormCMSThueBaoDangKyCTKM");
                    FormTranhTaiShowbizTable f = (FormTranhTaiShowbizTable) tabsheet.getSelectedTab();
                    if(f.isFirstLoad == false)
                    	f.getPage(1, true, null);
                }
            }
        });
    }
}