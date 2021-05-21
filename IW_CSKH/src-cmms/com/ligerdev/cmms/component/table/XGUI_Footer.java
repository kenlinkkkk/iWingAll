package com.ligerdev.cmms.component.table;

import java.util.ArrayList;

import org.apache.log4j.Logger;

import com.ligerdev.appbase.utils.BaseUtils;
import com.ligerdev.appbase.utils.textbase.Log4jLoader;
import com.ligerdev.cmms.component.AppCmmsUtils;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.TextField;
import com.vaadin.ui.themes.Reindeer;
import com.xtel.cms.MainApplication;

public class XGUI_Footer<T> extends HorizontalLayout {
	
	private static final long serialVersionUID = 1L;
	private MainApplication mainApp = (MainApplication) MainApplication.getCurrent();
	private static Logger logger = Log4jLoader.getLogger();
	
	IPagingListener listener = null;
	TextField txtPageSize = new TextField();
	TextField txtPageIndex = new TextField();
	TextField txtFilter;
	XGUI_TableLayout<T> mainLayout;
	int curPage = 1;
	
	@SuppressWarnings("serial")
	public XGUI_Footer(XGUI_TableLayout<T> mainLayout, final int curPage, Integer totalItem, final int totalPage, 
				final IPagingListener listener, final int pageSize, XOptions xoption) {
		
		//addStyleName("pagingStyle");
		//setSizeFull();
		this.curPage = curPage;
		this.mainLayout = mainLayout;
		setSpacing(true);
		setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);
		this.listener = listener;
		this.txtFilter = mainLayout.txtFilter;
		
		ShortcutListener shortcutEnterTextField = new ShortcutListener("", 13, (int[]) null) {
			@Override
			public void handleAction(Object sender, Object target) {
				int tempIndex = BaseUtils.parseInt(txtPageIndex.getValue(), -1);
				if(tempIndex > totalPage){
					tempIndex = totalPage;
				}
				if(tempIndex <= 0){
					tempIndex = 1;
				}
				int tempSize = BaseUtils.parseInt(txtPageSize.getValue(), -1); 
				if(tempSize <= 0){
					tempSize = pageSize;
				}
				if(tempSize > 200){
					tempSize = 200;
				}
				txtPageIndex.setValue("" + tempIndex);
				txtPageSize.setValue("" + tempSize);
				
				AppCmmsUtils.savePageSize(tempSize); 
				listener.getPage(tempIndex, false, txtFilter.getValue());
			}
		};
		this.txtFilter.addShortcutListener(shortcutEnterTextField);
		
		if(totalItem != null){
			String str = null;
			if(mainApp != null && mainApp.lang_vi) {
				str = "Tìm thấy " + totalItem + " kết quả / " + totalPage + " trang";
				if(BaseUtils.isNotBlank(txtFilter.getValue())){
					str += " (lọc theo: '" + txtFilter.getValue() + "')";
				}
			} else {
				str = "Found " + totalItem + " items / " + totalPage + " pages";
				if(BaseUtils.isNotBlank(txtFilter.getValue())){
					str += " (filter: '" + txtFilter.getValue() + "')";
				}
			}
			Label label = new Label(str);
			label.addStyleName("blueLabel");
			this.addComponent(label);
			//this.setComponentAlignment(label, Alignment.MIDDLE_LEFT);
		}
		ArrayList<PageInfo> allPages = getDisplayingPages(curPage, totalPage);
		
		if(allPages != null && allPages.size() > 0){
			this.addComponent(AppCmmsUtils.createLabel_html("&nbsp;&nbsp; pagesize: ", null));
			
			txtPageSize.setMaxLength(3);
			txtPageSize.setWidth("11mm");
			txtPageSize.setHeight("5mm");
			txtPageSize.setValue(String.valueOf(pageSize));
			
			// rào vì khi enter nó lại ăn sang enter ở textbox filter, nghiên cứu sau
			/*textFieldPageSize.addShortcutListener(new ShortcutListener("Enter", ShortcutAction.KeyCode.ENTER, null) {
				@Override
				public void handleAction(Object sender, Object target) {
					logger.info(mainApp.getTransid() + ", enter textFieldPageSize: " + textFieldPageSize.getValue());
					int temp = BaseUtils.parseInt(textFieldPageSize.getValue(), -1);
					if(temp > 0){
						AppCmmsUtils.savePageSize(temp); 
						listener.getPage(curPage, false, txtFilter.getValue());
					}
				}
			});*/
			this.addComponent(txtPageSize);
			
			Button buttonRefresh = createButton(new ThemeResource("icons/16/refresh.png")); 
			buttonRefresh.setWidth("20px"); 
			buttonRefresh.addClickListener(new ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					logger.info(mainApp.getTransid() + ", click refreshFieldPageSize: " + txtPageSize.getValue());
					int temp = BaseUtils.parseInt(txtPageSize.getValue(), -1);
					if(temp > 0){
						AppCmmsUtils.savePageSize(temp); 
						listener.getPage(curPage, false, txtFilter.getValue());
					}
				}
			});
			this.addComponent(buttonRefresh);
			//Label labelSpace = new Label();
			//labelSpace.setWidth("0.2cm");
			//this.addComponent(labelSpace);
			
			ClickListener xlistener = new ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) { 
					PageInfo bean = (PageInfo) event.getButton().getData();
					String key = "checknotify." + Thread.currentThread().hashCode() + "." + mainApp.getTransid();
					AppCmmsUtils.cache.put(key, "1", 4);
					listener.getPage(Integer.parseInt(bean.getValue()), false, txtFilter.getValue());
				}
			};
			
			for(int i = 0; allPages != null && i < allPages.size(); i ++){
				final PageInfo bean = allPages.get(i);
				if(bean.isDisable()) {
					continue; // ẩn btn disable
				}
				Button button = null;
				if(bean.getLabel().contains(".") == false) {
					button = new Button("&nbsp;&nbsp;" + bean.getLabel() + "&nbsp;&nbsp;");
				} else if(bean.getLabel().endsWith(".")) {
					button = new Button("&nbsp;&nbsp;" + bean.getLabel());
				} else if(bean.getLabel().startsWith(".")) {
					button = new Button(bean.getLabel() + "&nbsp;&nbsp;");
				}
				//button.setWidth("25px");
				button.setStyleName(Reindeer.BUTTON_LINK);
				button.setCaptionAsHtml(true);
				if(bean.isDisable()){
					button.setEnabled(false);
				}
				if(bean.getIsCurrent()){
					button.addStyleName("btnLinkFocus");
				} else {
					button.addStyleName("btnLink");
					button.setData(bean);
					button.addClickListener(xlistener);
				}
				this.addComponent(button);
				//Label labelSpace = new Label();
				//labelSpace.setWidth("0.3mm");
				//this.addComponent(labelSpace);
			}
			if(allPages.size() > 5){
				this.addComponent(new Label("PageIndex:"));
				txtPageIndex.setMaxLength(3);
				txtPageIndex.setWidth("11mm");
				txtPageIndex.setHeight("5mm");
				txtPageIndex.setValue(curPage + "");
				txtPageIndex.addShortcutListener(shortcutEnterTextField);
				this.addComponent(txtPageIndex);
				
				Button goPage = createButton(new ThemeResource("icons/16/next.png")); 
				goPage.addClickListener(new ClickListener() {
					@Override
					public void buttonClick(ClickEvent event) {
						logger.info(mainApp.getTransid() + ", move to pageIndex " + txtPageIndex.getValue());
						handlerPageIndex(txtPageIndex.getValue(), totalPage);
					}
				});
				this.addComponent(goPage);
			}
			Label labelSpace = new Label();
			labelSpace.setWidth("1cm");
			this.addComponent(labelSpace);
		}
	}
	
	private Button createButton(ThemeResource icon) {
		Button btn = new Button();
		btn.setStyleName("link");
		btn.setIcon(icon);
		btn.setWidth("17px");
		btn.setHeight("17px");
		return btn;
	}
	
	private void handlerPageIndex(String pageStr, int totalPage){
		try {
			int page = Integer.parseInt(pageStr);
			if(page > totalPage){
				throw new Exception();
			} else {
				listener.getPage(page, false, txtFilter.getValue());
			}
		} catch (Exception e) {
			Notification.show("Not found page " + pageStr, Type.WARNING_MESSAGE);
		}
	}
	
	private ArrayList<PageInfo> getDisplayingPages(int current, int totalPages) {
		if(current < 1){
			current = 1;
		}
		if(totalPages < 1){
			totalPages = 1;
		}
		ArrayList<PageInfo> allPages = new ArrayList<PageInfo>();
		//if (totalPages == 1) {
		//	return allPages;
		//}
		if (totalPages <= 5) {
			for (int i = 1; i <= totalPages; i++) {
				allPages.add(new PageInfo("" + i, "" + i));
			}
		} else {
			int start = 0;
			int end = 0;
			if (current == 1 || current == 2) {
				start = 1;
				end = 5;
			} else if (current == totalPages - 1 || current == totalPages) {
				start = totalPages - 4;
				end = totalPages;
			} else {
				start = current - 2;
				end = current + 2;
			}
			if (start != 1) {
				if (start == 2) {
					allPages.add(new PageInfo("1", "1"));
				} else {
					allPages.add(new PageInfo("1...", "1"));
				}
			}
			for (int i = start; i <= end; i++) {
				allPages.add(new PageInfo("" + i, "" + i));
			}
			if (end != totalPages) {
				if (end == totalPages - 1) {
					allPages.add(new PageInfo("" + totalPages, "" + totalPages));
				} else {
					allPages.add(new PageInfo("..." + totalPages, "" + totalPages));
				}
			}  
		}
		for (int i = 0; i < allPages.size(); i++) {
			PageInfo page = allPages.get(i);
			if (page.getValue().equals("" + current) && Character.isDigit(page.getLabel().toCharArray()[0])) {
				page.setIsCurrent(true);
				break;
			}
		}
		return allPages;
	}
}
