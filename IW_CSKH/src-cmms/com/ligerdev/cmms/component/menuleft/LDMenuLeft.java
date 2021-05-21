package com.ligerdev.cmms.component.menuleft;

import org.apache.log4j.Logger;

import com.ligerdev.appbase.utils.BaseUtils;
import com.ligerdev.appbase.utils.textbase.Log4jLoader;
import com.ligerdev.cmms.component.IMenuCfg;
import com.ligerdev.cmms.component.IMenuClickListener;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.MenuBar;
import com.xtel.cms.MainApplication;

public class LDMenuLeft extends MenuBar {

	protected static Logger logger = Log4jLoader.getLogger();
	private IMenuClickListener menuLeftListener = null;
	protected MainApplication mainApp = (MainApplication) MainApplication.getCurrent();
	
	public LDMenuLeft(IMenuClickListener menuLeftListener) {
		this(menuLeftListener, null);
	}

	public LDMenuLeft(IMenuClickListener menuLeftListener, String fileXml) {
		setSizeFull();
		setAutoOpen(true);
		this.menuLeftListener = menuLeftListener;
		LeftMenuConfigReader menuLeftReader = new LeftMenuConfigReader(fileXml);

		for (int i = 0; i < menuLeftReader.getMenuLeft().length; i++) {
			initMenus(menuLeftReader.getMenuLeft()[i], null);
		}
	}

	private void initMenus(final CfgMenu menu, MenuItem itemRoot) {
		if (menu == null) {
			return;
		}
		if (itemRoot == null) {
			final boolean enable = menuLeftListener.menuItemEnable(menu);
			if(enable == false) {
				return;
			}
			MenuBar.Command command = null;
			if(enable && BaseUtils.isNotBlank(menu.getPage())) {
				command = new MenuBar.Command() {
					@Override
					public void menuSelected(MenuItem selectedItem) {
						logger.info(mainApp.getTransid() + ", clicked: " + menu.getName() + ", cmd: " + menu.getPage() + ", enable: " + enable);
						if (BaseUtils.isBlank(menu.getPage()) || enable == false) {
							return;
						}
						menuLeftListener.menuClicked(selectedItem, menu.getPage());
					}
				};
			}
			itemRoot = addItem(menu.getName(), command);
			itemRoot.setEnabled(enable);
			
			if(BaseUtils.isNotBlank(menu.getIcon())){ 
				itemRoot.setIcon(new ThemeResource(menu.getIcon())); 
			}
		}
		if (menu.getItems() != null) {
			for (IMenuCfg iMenuCfg : menu.getItems()) {
				
				if(iMenuCfg instanceof CfgMenuItem){
					CfgMenuItem item = (CfgMenuItem) iMenuCfg;
					
					if("#".equals(item.getName())){
						itemRoot.addSeparator();
					} else {
						boolean enable = menuLeftListener.menuItemEnable(item);
						if(enable == false) {
							continue;
						}
						MenuItem itemTemp = addItem(itemRoot, item.getName(), item.getPage(), enable);
						itemTemp.setEnabled(enable);
						
						if(BaseUtils.isNotBlank(item.getIcon())){ 
							itemTemp.setIcon(new ThemeResource(item.getIcon())); 
						}
					}
				} else {
					// has child item
					CfgMenu childMenu = (CfgMenu) iMenuCfg;
					boolean enable = menuLeftListener.menuItemEnable(childMenu);
					if(enable == false) {
						continue;
					}
					MenuItem childItem = addItem(itemRoot, childMenu.getName(), childMenu.getPage(), enable); 
					childItem.setEnabled(enable);
					
					if(BaseUtils.isNotBlank(childMenu.getIcon())){
						childItem.setIcon(new ThemeResource(childMenu.getIcon())); 
					}
					initMenus(childMenu, childItem);
				}
			}
		} 
	}

	@SuppressWarnings("serial")
	private MenuItem addItem(MenuItem itemParent, final String title, final String cmd, final boolean enable) {
		logger.info(mainApp.getTransid() + ", addmenu: " + title + ", cmd: " + cmd + ", enable: " + enable);
		MenuBar.Command command = null;
		
		if(enable && BaseUtils.isNotBlank(cmd)) {
			command = new MenuBar.Command() {
				@Override
				public void menuSelected(MenuItem selectedItem) {
					logger.info(mainApp.getTransid() + ", clicked: " + title + ", cmd: " + cmd + ", enable: " + enable);
					if (BaseUtils.isBlank(cmd) || enable == false) {
						return;
					}
					menuLeftListener.menuClicked(selectedItem, cmd);
				}
			};
		}
		return itemParent.addItem(title, command);
	}
}
