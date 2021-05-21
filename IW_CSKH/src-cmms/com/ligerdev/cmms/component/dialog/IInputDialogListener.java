package com.ligerdev.cmms.component.dialog;

import com.ligerdev.cmms.component.table.DialogSubmitResult;

public interface IInputDialogListener<T> {
	public DialogSubmitResult<T> dialogSubmit000(T item, LDInputDialog<T> dialog);
}
