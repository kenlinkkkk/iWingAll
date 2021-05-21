package com.ligerdev.cmms.component.table;

import java.util.ArrayList;
import java.util.Set;

import org.apache.log4j.Logger;

import com.ligerdev.appbase.utils.BaseUtils;
import com.ligerdev.appbase.utils.db.BaseDAO;
import com.ligerdev.appbase.utils.db.XBaseDAO;
import com.ligerdev.appbase.utils.textbase.Log4jLoader;
import com.ligerdev.cmms.component.dialog.DialogCaption;
import com.ligerdev.cmms.component.dialog.IInputDialogListener;
import com.ligerdev.cmms.component.dialog.IOptionDialogListener;
import com.ligerdev.cmms.component.dialog.LDDelConfirmDialog;
import com.ligerdev.cmms.component.dialog.LDInputDialog;
import com.vaadin.ui.Panel;
import com.xtel.cms.MainApplication;

public abstract class LDTableSimple<T> extends Panel implements IPagingListener, ITableActionsListener<T> , IInputDialogListener<T>{

	protected static Logger logger = Log4jLoader.getLogger();
	protected static BaseDAO baseDAO = BaseDAO.getInstance(BaseDAO.POOL_NAME_MAIN);
	protected static XBaseDAO xbaseDAO = XBaseDAO.getInstance(BaseDAO.POOL_NAME_MAIN);
	protected MainApplication mainApp = (MainApplication) MainApplication.getCurrent();
	protected XGUI_TableLayout tableContainer = null;
	
	public LDTableSimple() {
		//setSizeFull();  // off 20210121
		setSizeUndefined();
	}
	
	@Override
	public void deleteSelectedItem(final Set<T> set, XOptions xOptions){
		new LDDelConfirmDialog(xOptions, new IOptionDialogListener() {
			@Override
			public void dialogOptionClicked(String buttonName, int buttonIndex) {
				//@SuppressWarnings("unchecked")
				//Set<T> set = (Set<T>) tableContainer.getTable().getValue();
				if(set == null){
					return;
				}
				ArrayList<T> listSelected = new ArrayList<T>(set);
				int delRows = deleteSelectedItem(listSelected); 
				if(delRows > 0){
					for(T t : listSelected){
						tableContainer.getTable().removeItem(t);
					}
				}
			}
		}).showDialog();
	}
	
	@Override
	public void showEditDialog(Set<T> set, Object colID) {
		//@SuppressWarnings("unchecked")
		//Set<T> set = (Set<T>) tableContainer.getTable().getValue();
		ArrayList<T> listSelected = null;
		if(set != null){
			listSelected = new ArrayList<T>(set);
			showEditDialog(listSelected.get(0), colID); 
		}
	}
	
	@Override
	public void showCopyDialog(Set<T> set) {
		//@SuppressWarnings("unchecked")
		//Set<T> set = (Set<T>) tableContainer.getTable().getValue();
		ArrayList<T> listSelected = null;
		if(set != null){
			listSelected = new ArrayList<T>(set);
			showCopyDialog(listSelected.get(0)); 
		}
	}
	
	@Override
	public void showEditListDialog(Set<T> set) {
		if(set == null){
			return;
		}
		ArrayList<T> listSelected = new ArrayList<T>(set);
		showEditListDialog(listSelected);
	}
	
	public abstract void showEditDialog(T selectedItem, Object colID);
	public void showEditListDialog(ArrayList<T> listSelected) {}
	public abstract void showCopyDialog(T selectedItem);
	public abstract int deleteSelectedItem(ArrayList<T> listSelected);
	public abstract DialogSubmitResult<T> dialogSubmit_(T item, LDInputDialog<T> dialog);
	
	@Override
	public DialogSubmitResult<T> dialogSubmit000(T item, LDInputDialog<T> dialog){
		int orgSize = tableContainer.getTable().getItemIds().size();
		DialogSubmitResult<T> rs = dialogSubmit_(item, dialog);
		
		if(rs == null || BaseUtils.isBlank(rs.getErrorMsg())){
			if(DialogCaption.EDIT_LIST.equals(dialog.getDCaption()) == false) {
				int add = 0;
				if(rs == null || rs.getListObj() == null) {
					tableContainer.getTable().addItem(item);
					add ++;
				} else {
					for(T t: rs.getListObj()) {
						tableContainer.getTable().addItem(t);
						add ++;
					}
				}
				if(add > 0 && orgSize != tableContainer.getTable().getItemIds().size()){
					tableContainer.getTable().setPageLength(tableContainer.getTable().getPageLength() + add);
				}
			}
			tableContainer.getTable().refreshRowCache();
		} 
		return rs;
	}
	
}
