package com.ligerdev.cmms.component.table;

import java.util.ArrayList;
import java.util.Set;

import org.apache.log4j.Logger;

import com.ligerdev.appbase.utils.BaseUtils;
import com.ligerdev.appbase.utils.textbase.Log4jLoader;
import com.ligerdev.cmms.component.AppCmmsUtils;
import com.vaadin.data.Container;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Page;
import com.vaadin.shared.Position;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.xtel.cms.MainApplication;

public class XGUI_TableLayout<T> extends VerticalLayout {

	private static final long serialVersionUID = 1L; 
	private static Logger logger = Log4jLoader.getLogger();
	private MainApplication mainApp = (MainApplication) MainApplication.getCurrent();
	
	XGUI_Table<T> tableLayout = null;
	XGUI_Footer<T> pagingLayout = null;
	XGUI_Actions<T> actionLayout = null;
	TextField txtFilter = new TextField();
	
	// private HorizontalLayout pnControl;
	// private FormLayout pncombo;
	// private String[] filteredColumns = null;
	
	public XGUI_TableLayout() {
	}
	
	public XGUI_TableLayout(boolean showNotify, int pageIndex, Integer totalItem, String caption, Container data, IPagingListener pagingListener, 
			final ITableActionsListener<T> tableActionProvider, String textFilter, boolean paging, XOptions xoption) {
		build(showNotify, pageIndex, totalItem, caption, data, pagingListener, tableActionProvider, textFilter, paging, xoption);
	}
	
	public void build(boolean showNotify, int pageIndex, Integer totalItem, 
			String caption, Container data, IPagingListener pagingListener, 
			final ITableActionsListener<T> tableActionProvider, String textFilter, boolean paging, XOptions xoption) {
		
		txtFilter.setValue(BaseUtils.isBlank(textFilter) ? "" : textFilter);
		int pageSize = (xoption != null && xoption.pageSize != null) ? xoption.pageSize : AppCmmsUtils.getPageSize();
		this.tableLayout = new XGUI_Table(this, pageIndex, data, pageSize, xoption);
		String permision = tableActionProvider.getPermission();
		// this.tableData.setSizeFull();
		// this.tableData.setSizeUndefined();
		
		if(permision == null || (xoption != null && xoption.hideActionBar)) {
			//tableActions.setVisible(false);
		}  else {
			actionLayout = new XGUI_Actions(this, permision, pagingListener, tableActionProvider, xoption);
		}
		// setCaption(caption);
		// setSizeFull();
		setSizeUndefined();
		setMargin(false);
		//int addTableLength = xoption != null ? xoption.addTableLength : 0; // default 0
		
		if (totalItem != null && paging) {
			// tableData.setIcon(FontAwesome.COMMENT);
			// tableData.setCaption("Found " + total + " items");
			int totalPage = (int) Math.ceil((float) totalItem / pageSize);
			
			if(showNotify || totalItem == null || totalItem >= 0){
				String key = "checknotify." + Thread.currentThread().hashCode() + "." + mainApp.getTransid();
				
				if(AppCmmsUtils.cache.contains(key) == false) {
					String notifyText = "Found " + totalItem + " items / " + totalPage + " pages";
					if(mainApp != null && mainApp.lang_vi) {
						notifyText = "Tìm thấy " + totalItem + " bản ghi / " + totalPage + " trang";
					}
					Notification notification = new Notification(null, notifyText, Type.TRAY_NOTIFICATION);
					notification.setDelayMsec(2000);
					notification.setIcon(FontAwesome.COMMENT);
					notification.setPosition(Position.BOTTOM_RIGHT);
					notification.setStyleName("myNotifyStyle");
					notification.show(Page.getCurrent());
				}
			}
			/*
			if(totalPage > 1){
				int temp = Math.min(pageSize, data.size());
				tableData.setPageLength(temp);
				pagingLayout = new XGUI_Footer(pageIndex, totalItem, totalPage, pagingListener, txtFilter, pageSize, xoption);
			} else { // totalPage
				tableData.setPageLength(totalItem);
				pagingLayout = new XGUI_Footer(pageIndex, totalItem, totalPage, pagingListener, txtFilter, pageSize, xoption);
			}
			*/
			tableLayout.setPageLength(data.size());
			pagingLayout = new XGUI_Footer(this, pageIndex, totalItem, totalPage, pagingListener, pageSize, xoption);
			
		} else {
			if(paging == false && data != null) {
				tableLayout.setPageLength(data.size());
			} else {
				tableLayout.setPageLength(pageSize);
			}
		}
		// Label label = new Label("<a style=\"font-size: 30;font: bold;\">" +
		// caption + "</a>", ContentMode.HTML);
		Label label = new Label("&nbsp;" + caption);
		label.addStyleName("titlePage");
		label.setContentMode(ContentMode.HTML);
		//label.setSizeUndefined();
		//label.setHeight("20px"); 

		CustomLayout custom = new CustomLayout("contentview");
		custom.setSizeFull();
		custom.addComponent(label, "title");
		// buttonPanel.setSizeFull();
		if(actionLayout != null) {
			custom.addComponent(actionLayout, "actions");
		}
		custom.addComponent(tableLayout, "table");
		
		if(pagingLayout != null){
			custom.addComponent(pagingLayout, "paging");
			//addComponent(pagingLayout);
			//Alignment a = xoption != null && xoption.alignPaging != null  ? xoption.alignPaging : Alignment.MIDDLE_RIGHT;
			//setComponentAlignment(pagingLayout, a);
		}
		tableLayout.setSizeFull();
		
		addComponent(custom);
		//setExpandRatio(custom, 1F);
		/*
		table.addItemClickListener(new ItemClickListener() {
			@Override
			public void itemClick(ItemClickEvent event) {
				// Object id = table.getValue();
				// logger.info(mainApp.getTransid() + ", event = " + event.getItemId()); 
			}
		});
		table.addItemSetChangeListener(new Container.ItemSetChangeListener() {
			@Override
			public void containerItemSetChange(ItemSetChangeEvent event) {
				// Object id = table.getValue();
				// logger.info(mainApp.getTransid() + ", event = " + event.getContainer().getItemIds()); 
			}
		});
		*/
		
		if(actionLayout != null) {
			tableLayout.addValueChangeListener(new Property.ValueChangeListener() {
				@Override
				public void valueChange(ValueChangeEvent event) {
					// logger.info(mainApp.getTransid() + ", event = " + event.getProperty().getValue()); 
					// logger.info(mainApp.getTransid() + ", event = " + tableData.getValue()); 
					Set<T> set = (Set<T>) tableLayout.getValue();
					actionLayout.setRowSelected(set == null ? 0 : set.size());
				}
			});
			tableLayout.addItemClickListener(new ItemClickListener() {
				@Override
				public void itemClick(ItemClickEvent event) {
					 if (event.isDoubleClick()) {  
						//logger.info(mainApp.getTransid() + ", rowID: " + String.valueOf(event.getItemId()) + " | colId: " + String.valueOf(event.getPropertyId()));
						Set<T> set = (Set<T>) tableLayout.getValue();
						actionLayout.setRowSelected(set == null ? 0 : set.size());
						if(set != null && set.size() == 1 && actionLayout.btnEdit.isEnabled()) {
							tableActionProvider.showEditDialog(set, event.getPropertyId());
						}  
					 }
				}
			});
		}
		ArrayList<TableHeader> newVisibleColumn = new ArrayList<TableHeader>();
		newVisibleColumn.add(new TableHeader("STT", "STT")); 
		
		/*
		Object visibleColumn[] = table.getVisibleColumns();
		@SuppressWarnings("rawtypes")
		BeanItemContainer beanItemContainer = (BeanItemContainer) table.getContainerDataSource();
		for(int i = 0; i < visibleColumn.length; i ++){
			String propertyName = String.valueOf(visibleColumn[i]);
			if(propertyName.equals("STT")){
				continue;
			}
			try {
				String getter = BaseUtils.getGetterName(propertyName);
				@SuppressWarnings("unchecked")
				Method get = beanItemContainer.getBeanType().getMethod(getter);

				AntColumn antColumn = get.getAnnotation(AntColumn.class);
				if(antColumn.tbl_visible() == false){
					continue;
				}
				TableHeader tableColumnHeader = new TableHeader(propertyName, antColumn.label());
				if(BaseUtils.isNotBlank(antColumn.tbl_icon())){
					tableColumnHeader.setIcon(antColumn.tbl_icon());
				}
				if(BaseUtils.isNotBlank(antColumn.tbl_format())){
					tableColumnHeader.setDateFormat(antColumn.tbl_format());
				}
				newVisibleColumn.add(tableColumnHeader);
				
			} catch (Exception e) {
				newVisibleColumn.add(new TableHeader(propertyName, propertyName));
			}
		}
		*/
		getTable().addVisibleColumn(newVisibleColumn);
	}
	/*	
		public void addVisibleColumn(ArrayList<TableHeader> arrayList){
			table.addVisibleColumn(arrayList);
		}
		
		public void addVisibleColumn(TableHeader ... entry){
			table.addVisibleColumn(entry);
		}
	*/
	public XGUI_Table getTable() {
		return tableLayout;
	}
	
	public void setEnableEditMultiRow(boolean b) {
		if(actionLayout != null) {
			actionLayout.setEnableEditMultiRow(b);
		}
	}
	
	public void setRowClickCallback(ItemClickListener clickListenerCallBack) {
		if(tableLayout != null) {
			tableLayout.addItemClickListener(clickListenerCallBack); 
		}
	}
	
	public void addVisibleColumn(ArrayList<TableHeader> arrayList) {
		if(tableLayout != null) {
			tableLayout.addVisibleColumn(arrayList);
		}
	}
}