package com.ligerdev.cmms.component.menuleft;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.ligerdev.appbase.utils.BaseUtils;
import com.ligerdev.appbase.utils.textbase.DocumentUtils;

public class LeftMenuConfigReader {

	private CfgMenu[] menuLeft = null;
	
	public LeftMenuConfigReader() {
		this(null);
	}

	public LeftMenuConfigReader(String fileXml) {
		if(fileXml == null) {
			fileXml = "menu.xml";
		}
		String fileName = BaseUtils.getMyDir() + "./config/" + fileXml;
		Element rootE = DocumentUtils.parseStringToDoc(BaseUtils.readFile(fileName)).getDocumentElement();
		NodeList listE = rootE.getElementsByTagName("menu");

		this.menuLeft = new CfgMenu[listE.getLength()];
		for (int i = 0; i < listE.getLength(); i++) {
			Element rootMenuE = (Element) listE.item(i);
			this.menuLeft[i] = loadMenu(rootMenuE);
		}
	}

	private CfgMenu loadMenu(Element rootE) {
		String menuName = rootE.getAttribute("name");
		String menuIcon = rootE.getAttribute("icon");
		String menuValue = rootE.getAttribute("value");
		String menuPage = rootE.getAttribute("page");

		CfgMenu menu = new CfgMenu(menuName, menuIcon, menuValue, menuPage, null);
		NodeList listE = rootE.getChildNodes();

		if (listE != null && listE.getLength() > 0) {
			for (int i = 0; i < listE.getLength(); i++) {
				if ("#text".equals(listE.item(i).getNodeName())) {
					continue;
				}
				Element childE = (Element) listE.item(i);
				if (childE.getParentNode() != rootE) {
					continue;
				}
				if (listE.item(i).hasChildNodes()) {
					CfgMenu childMenu = loadMenu(childE);
					menu.addIMenuCfg(childMenu);

				} else {
					String name = childE.getAttribute("name");
					String value = childE.getAttribute("value");
					String page = childE.getAttribute("page");
					String icon = childE.getAttribute("icon");

					CfgMenuItem itemCfg = new CfgMenuItem(name, value, page, icon);
					menu.addIMenuCfg(itemCfg);
				}
			}
		}
		return menu;
	}

	public CfgMenu[] getMenuLeft() {
		return menuLeft;
	}

	public void setMenuLeft(CfgMenu[] menuLeft) {
		this.menuLeft = menuLeft;
	}
}
