package com.ligerdev.cmms.component;

public interface IMenuClickListener {

	public boolean menuItemEnable(IMenuCfg menuCfg);
	public <T>void menuClicked(T selectedItem, String cmd);
}
