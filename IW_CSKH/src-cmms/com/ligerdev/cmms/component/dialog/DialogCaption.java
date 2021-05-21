package com.ligerdev.cmms.component.dialog;

public class DialogCaption implements Cloneable {
	
	public static DialogCaption ADD = new DialogCaption("add", "Add new item");
	public static DialogCaption EDIT = new DialogCaption("edit", "Edit item");
	public static DialogCaption EDIT_LIST = new DialogCaption("edit_list", "Edit item");
	public static DialogCaption COPY = new DialogCaption("copy", "Copy this item");
	public static DialogCaption SEARCH = new DialogCaption("search", "Search");

	private String id;
	private String caption;

	public DialogCaption(String id, String caption) {
		this.id = id;
		this.caption = caption;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCaption() {
		return caption;
	}

	public void setCaption(String caption) {
		this.caption = caption;
	}
	
	@Override
	public boolean equals(Object obj) {
		try {
			if(obj instanceof DialogCaption){
				DialogCaption o = (DialogCaption) obj;
				return id == o.id;
			}
			return id.equals(obj);
			
		} catch (Exception e) {
		}
		return false;
	}
	
	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}
}
