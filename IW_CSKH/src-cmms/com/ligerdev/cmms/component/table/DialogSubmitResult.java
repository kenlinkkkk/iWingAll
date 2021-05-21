package com.ligerdev.cmms.component.table;

import java.util.ArrayList;

public class DialogSubmitResult<T> {
	
	private String errorMsg;
	private ArrayList<T> listObj = null; // nếu khác null sẽ add thêm số item vào bảng bằng số size của list

	public DialogSubmitResult() {
	}
	
	public DialogSubmitResult(String errorMsg) {
		this.errorMsg = errorMsg;
	}

	public DialogSubmitResult(String errorMsg, ArrayList<T> listObj) {
		this.errorMsg = errorMsg;
		this.listObj = listObj;
	}

	public DialogSubmitResult(String errorMsg, T... objs) {
		this.errorMsg = errorMsg;
		addObj(objs); 
	}
	
	public void addObj(T ...objs) {
		if(this.listObj == null) {
			this.listObj = new ArrayList<T>();
		}
		for (T t : objs) {
			this.listObj.add(t);
		}
	}
	
	public void clearListObj() {
		if(this.listObj == null) {
			this.listObj = new ArrayList<T>();
		} else {
			listObj.clear();
		}
	}
	
	public void initListObjIfNull() { // khác null thì table sẽ ko add item (1 số tình huống add nhưng xử lý như sửa chứ ko thêm item vào bảng)
		if(this.listObj == null) {
			this.listObj = new ArrayList<T>();
		}  
	}

	public String getErrorMsg() {
		return errorMsg;
	}

	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}

	public ArrayList<T> getListObj() {
		return listObj;
	}

	public void setListObj(ArrayList<T> listObj) {
		this.listObj = listObj;
	}
}
