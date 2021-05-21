package com.ligerdev.cmms.component.table;

import java.util.ArrayList;

public class XOptions {
	
	public Integer pageSize = null;
	public boolean hideActionBar = false;
	
	// cap on top table
	public String button_cap_refresh = null;
	public String button_cap_refresh_tooltip = null;
	public String button_cap_add = null;
	public String button_cap_add_tooltip = null;
	public String button_cap_del = null;
	public String button_cap_del_tooltip = null;
	public String button_cap_edit = null;
	public String button_cap_edit_tooltip = null;
	public String button_cap_search = null;
	public String button_cap_search_tooltip = null;
	public String button_cap_copy = null;
	public String button_cap_copy_tooltip = null;
	public String button_cap_export = null;
	public String button_cap_export_tooltip = null;
	public String button_cap_filter = null;
	public String button_cap_filter_tooltip = null;
	
	// ---------------
	// định nghĩa row total, cần bold
	public String rowTotal_title = null;
	public String rowTotal_title_colID = null;
	public Object rowTotal_object = null;
	public ArrayList<String> rowTotal_listTotalCol;
}
