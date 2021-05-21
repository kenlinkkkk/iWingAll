package com.ligerdev.cmms.component.dashboard;

import java.util.ArrayList;

import org.apache.log4j.Logger;

import com.ligerdev.appbase.utils.BaseUtils;
import com.ligerdev.appbase.utils.textbase.Log4jLoader;
import com.ligerdev.cmms.component.IMenuClickListener;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.Reindeer;

public class LDDashboard extends VerticalLayout {
	
	protected static Logger logger = Log4jLoader.getLogger();
	private IMenuClickListener menuClickListener = null;
	private CustomLayout customLayout = null;
	
	public LDDashboard(IMenuClickListener menuClickListener) {
		this(menuClickListener, null);
	}
	
	public LDDashboard(IMenuClickListener menuClickListener, String htmlFile) {
		
		if(htmlFile == null) {
			htmlFile = "dashboard";
		}
		customLayout = new CustomLayout(htmlFile);
		this.menuClickListener = menuClickListener;
		setSizeFull();
		// customLayout.addComponent(layout, "smstest");
		
		for(CfgDashCate cate: DashConfigReader.dashboardCfg){
			VerticalLayout layout = getDashBoardCate(cate.getName(), cate.getItems()); 
			layout.setEnabled(menuClickListener.menuItemEnable(cate)); 
			
			if(layout.isEnabled() == false) {
				continue;
			}
			customLayout.addComponent(layout, cate.getLocation());
		}
		addComponent(customLayout); 
	}
	
	public VerticalLayout getDashBoardCate(String cateName, ArrayList<CfgDashItem> listField){
		if(listField == null || listField.size() == 0){
			return getDashBoardCate(cateName);
		}
		CfgDashItem[] temp = new CfgDashItem[listField.size()];
		return getDashBoardCate(cateName, listField.toArray(temp)); 
	}
	
	@SuppressWarnings("serial")
	public VerticalLayout getDashBoardCate(String cateName, CfgDashItem ... listField){
		VerticalLayout vLayout = new VerticalLayout();
		
		Label cateNameLabel = new Label(cateName);
		cateNameLabel.setStyleName("cateDashboard");
		vLayout.addComponent(cateNameLabel);
		
		for (int i = 0; listField != null && i < listField.length; i ++) {
			
			final CfgDashItem item = listField[i];
			Button btn = new Button(item.getName());
			btn.setDescription(item.getTooltip());
			btn.setStyleName(Reindeer.BUTTON_LINK);
			btn.addStyleName("btnLink");
			
			if(BaseUtils.isNotBlank(item.getIcon())){ 
				ThemeResource icon = new ThemeResource(item.getIcon());
				btn.setIcon(icon);
			}
			btn.addClickListener(new Button.ClickListener() {
				public void buttonClick(ClickEvent event) {
					menuClickListener.menuClicked(event, item.getPage());
				}
			});
			btn.setEnabled(menuClickListener.menuItemEnable(item)); 
			vLayout.addComponent(btn);
		}
		return vLayout;
	}

	public CustomLayout getCustomLayout() {
		return customLayout;
	}

	public void setCustomLayout(CustomLayout customLayout) {
		this.customLayout = customLayout;
	}
}
