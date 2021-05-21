package com.xtel.cms.ccsp.dialog;

import com.ligerdev.cmms.component.AppCmmsUtils;
import com.ligerdev.cmms.component.dialog.DialogCaption;
import com.ligerdev.cmms.component.dialog.IInputDialogListener;
import com.ligerdev.cmms.component.dialog.LDInputDialog;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.TextField;
import com.xtel.cms.base.db.orm.UserCms;

public class DialogCCSPUsersCms extends LDInputDialog<UserCms> {

	// protected TextField userId = AppCmmsUtils.createTextField("userId", "", true, "1cm");
	// protected TextField userType = AppCmmsUtils.createTextField("userType", "", true, "2cm");
	protected TextField username = AppCmmsUtils.createTextField("Username", "", true);
	protected CheckBox status = AppCmmsUtils.createCheckBox("Status", true);
	protected TextField phone = AppCmmsUtils.createTextField("Phone", "", false);
	protected TextField email = AppCmmsUtils.createTextField("Email", "", false);
	// protected TextField password = AppCmmsUtils.createTextField("password", "", true);
	protected TextField fullname = AppCmmsUtils.createTextField("Họ Tên", "", true);
	protected TextField address = AppCmmsUtils.createTextField("Địa chỉ", "", false);
	protected ComboBox comboboxType = null;
	
	public DialogCCSPUsersCms(UserCms item, DialogCaption dCaption, IInputDialogListener<UserCms> submitListener) {
		super(item, "16cm", "10cm", dCaption, submitListener);
		setCaption(dCaption.getCaption()); 
		status.setValue(true);
		
		// addComponent(userId);
		// addComponent(userType);
		addComponent(username);
		addComponent(status);
		addComponent(fullname);
		addComponent(phone);
		addComponent(email);
		// addComponent(password);
		addComponent(address);
		
		comboboxType =  AppCmmsUtils.createComboBox2("Phân quyền", null, true);
		comboboxType.addItem(UserCms.CCSP_USERTYPE_ADMIN);
		comboboxType.setItemCaption(UserCms.CCSP_USERTYPE_ADMIN, "ADMIN"); 
		
		comboboxType.addItem(UserCms.CCSP_USERTYPE_NORMAL);
		comboboxType.setItemCaption(UserCms.CCSP_USERTYPE_NORMAL, "Staff"); 
		
		comboboxType.select(UserCms.CCSP_USERTYPE_NORMAL); 
		addComponent(comboboxType);
        binder.bindMemberFields(this);
	} 
	
	public static void main(String[] args) {
		LDInputDialog.genFields(UserCms.class);
		System.exit(-9);
	}

	public ComboBox getComboboxType() {
		return comboboxType;
	}

	public void setComboboxType(ComboBox comboboxType) {
		this.comboboxType = comboboxType;
	}
	
	
}
