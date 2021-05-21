package com.ligerdev.cmms.component.tree;

import com.vaadin.ui.Tree;

public class LDTree extends Tree {
	
	/*
	protected final ThemeResource ICON_ROOT = new ThemeResource("icons/16/home.png");
	protected final ThemeResource ICON_SERVICE = new ThemeResource("icons/16/service2.png");
	protected final ThemeResource ICON_ACTION = new ThemeResource("icons/16/mobile.png");
	protected final ThemeResource ICON_PACKAGE = new ThemeResource("icons/16/package.png");
	protected final ThemeResource ICON_PROMOTION = new ThemeResource("icons/16/promotion.png");
	protected final ThemeResource ICON_CHARGEBAND = new ThemeResource("icons/16/chargeband.png");
	protected final ThemeResource ICON_SUB_DETAIL_PARENT = new ThemeResource("icons/16/month_calendar.png");
	protected final ThemeResource ICON_SUB_DETAIL = new ThemeResource("icons/16/chart.png");
	protected final ThemeResource ICON_OTHERS = new ThemeResource("icons/16/money.png");

	protected final HashMap<Class<?>, ThemeResource> iconMapper = new HashMap<Class<?>, ThemeResource>();
	{
		// this.iconMapper.put(McaService.class, this.ICON_SERVICE);
		// this.iconMapper.put(McaAction.class, this.ICON_ACTION);
		// this.iconMapper.put(McaPackage.class, this.ICON_PACKAGE);
		// this.iconMapper.put(Promotion.class, this.ICON_PROMOTION);
		// this.iconMapper.put(ChargeBand.class, this.ICON_CHARGEBAND);
		this.iconMapper.put(String.class, this.ICON_SUB_DETAIL_PARENT);
		this.iconMapper.put(Object.class, this.ICON_OTHERS);
	}
	*/
	
	public LDTree() {
		setNullSelectionAllowed(false);
		setImmediate(true);
		
		// setCaption("TreeView");
		// setIcon(new ThemeResource("icons/16/tree.png"));
		
		/* 
		setItemStyleGenerator(new Tree.ItemStyleGenerator() {
			public String getStyle(Tree source, Object itemId) {
				return "no-children";
			}
		});
		*/
		
		/*
		setItemDescriptionGenerator(new ItemDescriptionGenerator() {
			public String generateDescription(Component source, Object itemId, Object propertyId) {
				return itemId.toString();
			}
		});
		*/
	}
}