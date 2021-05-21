package com.ligerdev.cmms.component.table;

import java.util.Set;

public abstract interface ITableActionsListener <T> {
	public abstract void showEditDialog(Set<T> set, Object colID); // colID != null nếu click đúp vào row (ko click nút sửa trên action bar)
	public abstract void showEditListDialog(Set<T> set);
	public abstract void showCopyDialog(Set<T> set);
	public abstract void showAddDialog();
	public abstract void showSearchDialog();
	public abstract void deleteSelectedItem(Set<T> set, XOptions xOptions);
	public abstract String getPermission();
	public abstract void exportTable();
}
