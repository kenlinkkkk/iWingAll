package com.ligerdev.cmms.component.table;

public interface IPagingListener {
	public void getPage(int pageIndex, boolean showNotify, String filter);
}
