package com.ligerdev.cmms.component.dashboard;

import java.util.ArrayList;

import org.w3c.dom.Element;

import com.ligerdev.appbase.utils.BaseUtils;
import com.ligerdev.appbase.utils.textbase.ConfigsReader;

public class DashConfigReader extends ConfigsReader {

	public static ArrayList<CfgDashCate> dashboardCfg = null; 
	public static void initStatic(){};
	
	static {
		dashboardCfg = new ArrayList<CfgDashCate>();
		String filename = BaseUtils.getMyDir() + "./config/dashboard.xml";
		ConfigsReader.init(DashConfigReader.class, filename);
	}

	@Override
	public void readPropeties() throws Exception { 
		ArrayList<CfgDashCate> dashboardCfgTemp = new ArrayList<CfgDashCate>();
		ArrayList<Element> listMenuE = getListElement("menu");
		
		for(int i = 0 ; i < listMenuE.size(); i ++){
			Element element = listMenuE.get(i);
			
			String name = getStringAttr(element, "name");
			String location = getStringAttr(element, "location");
			String value = getStringAttr(element, "value");
			
			ArrayList<Element> listItemE = getListElement(element, "item");
			ArrayList<CfgDashItem> listItem = new ArrayList<CfgDashItem>();
			
			for (int j = 0; j < listItemE.size(); j++) {
				Element element2 = listItemE.get(j);
				CfgDashItem cfgDashboard = getCfgDashboard(element2);
				listItem.add(cfgDashboard);
			}
			CfgDashCate cate = new CfgDashCate(name, location, value);
			cate.setItems(listItem);
			dashboardCfgTemp.add(cate);
		}
		dashboardCfg = dashboardCfgTemp;
	}
	
	private CfgDashItem getCfgDashboard(Element element){
		String name = element.getAttribute("name");
		String icon = element.getAttribute("icon");
		String value = element.getAttribute("value");
		String page = element.getAttribute("page");
		String tooltip = element.getAttribute("tooltip");
		
		CfgDashItem cfgDashboard = new CfgDashItem(name, icon, value, page, tooltip);
		return cfgDashboard;
	}
}
