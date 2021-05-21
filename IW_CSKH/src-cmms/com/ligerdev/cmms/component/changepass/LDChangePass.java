package com.ligerdev.cmms.component.changepass;

import com.ligerdev.cmms.component.dialog.IInputDialogListener;
import com.ligerdev.cmms.component.dialog.LDInputDialog;
import com.ligerdev.cmms.component.table.DialogSubmitResult;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.PasswordField;

@SuppressWarnings("serial")
public class LDChangePass extends LDInputDialog<Object> implements IInputDialogListener<Object> {

	private PasswordField oldPass = new PasswordField("Mật khẩu cũ");
	private PasswordField newPass1 = new PasswordField("Mật khẩu mới");
	private PasswordField newPass2 = new PasswordField("Xác nhận mật khẩu mới");
	{
		oldPass.setRequired(true);
		oldPass.setRequiredError("Please enter old password");
		
		newPass1.setRequired(true);
		newPass1.setRequiredError("Please enter new password");
		
		newPass2.setRequired(true);
		newPass2.setRequiredError("Please re-enter new password");
	}
	
	private IChangePassListener listener = null;

	public LDChangePass(IChangePassListener listener) {
		super(null, "13cm", "5.8cm", null, null);
		setSubmitListener(this);
		
		setCaption("Đổi mật khẩu");
		this.listener = listener;
		
		setIcon(new ThemeResource("icons/16/user.png"));
		addComponent(oldPass);
		addComponent(newPass1);
		addComponent(newPass2);
	}

	@Override
	public DialogSubmitResult dialogSubmit000(Object item, LDInputDialog<Object> dialog) {
		String oldPassStr = oldPass.getValue();
		String newPassStr1 = newPass1.getValue();
		String newPassStr2 = newPass2.getValue();
		
		//		if(	BaseUtils.isBlank(oldPassStr)
		//				|| BaseUtils.isBlank(newPassStr1)
		//				|| BaseUtils.isBlank(newPassStr2)
		//				|| !String.valueOf(newPassStr1).equals(newPassStr2)){
		//			
		//			return "Invalid data, please reenter...";
		//		}
		String str = listener.change(oldPassStr, newPassStr1);
		return new DialogSubmitResult(str);
	}
}
