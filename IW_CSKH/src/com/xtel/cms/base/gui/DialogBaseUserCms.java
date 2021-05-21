package com.xtel.cms.base.gui;

import com.ligerdev.cmms.component.AppCmmsUtils;
import com.ligerdev.cmms.component.dialog.DialogCaption;
import com.ligerdev.cmms.component.dialog.IInputDialogListener;
import com.ligerdev.cmms.component.dialog.LDInputDialog;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.TextField;
import com.xtel.cms.base.db.orm.UserCms;

public class DialogBaseUserCms extends LDInputDialog<UserCms> {

	// protected TextField userId = AppCmmsUtils.createTextField("userId", "", true, "1cm");
	// protected TextField userType = AppCmmsUtils.createTextField("userType", "", true, "2cm");
	protected TextField username = AppCmmsUtils.createTextField("username", "", true);
	protected TextField password = AppCmmsUtils.createTextField("password", "", true);
	protected CheckBox status = AppCmmsUtils.createCheckBox("status", true);
	protected TextField phone = AppCmmsUtils.createTextField("phone", "", true);
	protected TextField email = AppCmmsUtils.createTextField("email", "", true);
	// protected TextField password = AppCmmsUtils.createTextField("password", "", true);
	protected TextField fullname = AppCmmsUtils.createTextField("fullname", "", true);
	protected TextField address = AppCmmsUtils.createTextField("address", "", true);
	 
	public DialogBaseUserCms(UserCms item, DialogCaption dCaption, IInputDialogListener<UserCms> submitListener) {
		super(item, "16cm", "10cm", dCaption, submitListener);
		setCaption(dCaption.getCaption()); 
		
		// addComponent(userId);
		// addComponent(userType);
		addComponent(username);
		addComponent(password);
		addComponent(status);
		addComponent(fullname);
		addComponent(phone);
		addComponent(email);
		addComponent(address);
		
        binder.bindMemberFields(this);
	}
	
	public static void main(String[] args) {
		LDInputDialog.genFields(UserCms.class);
		System.exit(-9);
	}
}
