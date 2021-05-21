package com.ligerdev.cmms.component.dialog;

import com.ligerdev.cmms.component.table.XOptions;
import com.vaadin.server.ThemeResource;
import com.xtel.cms.MainApplication;

@SuppressWarnings("serial")
public class LDDelConfirmDialog extends LDOptionDialog {
	
	public IOptionDialogListener listener;
	protected MainApplication mainApp = (MainApplication) MainApplication.getCurrent();
	
	public LDDelConfirmDialog(XOptions xoption, IOptionDialogListener listener) {
		super("ConfirmDialog", "9cm", "2.8cm");
		if(mainApp != null && mainApp.lang_vi) {
			setMessage("Bạn có chắc chắn muốn xóa?", "OK", "Cancel"); 
		} else {
			setMessage("Are you sure you want to delete?", "OK", "Cancel"); 
		}
		this.listener = listener;
		setIcon(new ThemeResource("icons/16/question.png"));
		
		setResizable(false);
		setClosable(false);
	}
	
	public LDDelConfirmDialog(XOptions xoption, IOptionDialogListener listener, String width, String message) {
		super("ConfirmDialog", width, "2.8cm");
		setMessage(message, "OK", "Cancel"); 
		this.listener = listener;
		setIcon(new ThemeResource("icons/16/question.png"));
		
		setResizable(false);
		setClosable(false);
	}

	@Override
	public void dialogButtonClicked(String buttonName, int buttonIndex) {
		if(buttonIndex == 0){
			listener.dialogOptionClicked(buttonName, buttonIndex);
		}
		this.close();
	}
}
