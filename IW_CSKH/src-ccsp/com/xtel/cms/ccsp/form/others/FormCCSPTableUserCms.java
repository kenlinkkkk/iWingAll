package com.xtel.cms.ccsp.form.others;

import java.util.ArrayList;

import com.fss.util.StringUtil;
import com.ligerdev.cmms.component.AppCmmsUtils;
import com.ligerdev.cmms.component.dialog.DialogCaption;
import com.ligerdev.cmms.component.dialog.LDInputDialog;
import com.ligerdev.cmms.component.table.DialogSubmitResult;
import com.ligerdev.cmms.component.table.LDTableSimple;
import com.ligerdev.cmms.component.table.XGUI_Actions;
import com.ligerdev.cmms.component.table.XGUI_TableLayout;
import com.ligerdev.cmms.component.table.TableHeader;
import com.vaadin.data.util.BeanItemContainer;
import com.xtel.cms.base.db.orm.UserCms;
import com.xtel.cms.ccsp.dialog.DialogCCSPUsersCms;

public class FormCCSPTableUserCms extends LDTableSimple<UserCms>{

	static {
		 // System.out.println("###########################");
		 // BaseUtils.setMyDir("/media/Data/UWorkspace/Hitech/VaadinCms7/");
	}
	
	public FormCCSPTableUserCms() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void getPage(int pageIndex, boolean showNotify, String filter) {
		logger.info(mainApp.getTransid() + ", =====> pageIndex: " + pageIndex + ", showNtf: " + showNotify + ", filter: " + filter);
		
		int index = (pageIndex - 1) * AppCmmsUtils.getPageSize();
		String sql = "select * from user_cms where " + AppCmmsUtils.getFilterRestrictionSql(filter, null, "username", "fullname"); 
		
		int totalFound = 0;
		try {
			totalFound = AppCmmsUtils.countRecord(sql);
		} catch (Exception e) {
		}
		ArrayList<UserCms> list = null;
		try {
			list = baseDAO.getListBySql(mainApp.getTransid(), UserCms.class, sql, index, AppCmmsUtils.getPageSize());
			
			for(UserCms b: list) {
				tagRollName(b);
			}
		} catch (Exception e) {
		} 
		BeanItemContainer<UserCms> data = new BeanItemContainer<UserCms>(UserCms.class, list);
		tableContainer = new XGUI_TableLayout(showNotify, pageIndex, totalFound, "Quản lý user", data, this, this, filter, true, null);
		
		// manual set visible columns 
		tableContainer.getTable().addVisibleColumn(
				new TableHeader("STT", "STT")
				, new TableHeader("username", "Tên đăng nhập")
				, new TableHeader("fullname", "Họ tên")
				, new TableHeader("status", "Trạng thái", null, "1").setShowIconX(true)
				, new TableHeader("phone", "Số đt")
				// , new TableHeader("lastLogin", "Đăng nhập gần nhất", "yyyy/MM/dd HH:mm:ss")
				, new TableHeader("email", "Email")
				, new TableHeader("address", "Địa chỉ")
				, new TableHeader("userTypeStr", "Phân quyền")
		);
		// manual remove some visible columns 
		tableContainer.getTable().removeVisibleColumn("password");
		setContent(tableContainer);
	}

	@Override
	public String getPermission() {
		return XGUI_Actions.ROLE_GROUP_FULL;
	}

	@Override
	public int deleteSelectedItem(ArrayList<UserCms> listSelected) {
		int count = 0;
		logger.info(mainApp.getTransid() + ", call delete");
		try {
			logger.info(mainApp.getTransid() + ", " + String.valueOf(listSelected));
			for(UserCms u: listSelected){ 
				if(u.getUsertype() != 0){ // # root
					String sql = "update user_cms set status = 0 where id = " + u.getId();
					count += baseDAO.execSql(mainApp.getTransid(), sql);
				}
			}
		} catch (Exception e) {
			logger.info(mainApp.getTransid() + ", Exception: " + e.getMessage());
		}
		return count;
	}

	@Override
	public void showAddDialog() {
		logger.info(mainApp.getTransid() + ", call showDialog");
		UserCms user = new UserCms();
		DialogCCSPUsersCms dialog = new DialogCCSPUsersCms(user, DialogCaption.ADD, this);
		dialog.showDialog();
	}

	@Override
	public void showSearchDialog() {
		logger.info(mainApp.getTransid() + ", call showDialog");
		UserCms user = new UserCms();
		DialogCCSPUsersCms dialog = new DialogCCSPUsersCms(user, DialogCaption.SEARCH, this);
		dialog.showDialog();
	}

	@Override
	public void showEditDialog(UserCms selectedItem, Object colID) {
		logger.info(mainApp.getTransid() + ", call showDialog, selectedItem = " + String.valueOf(selectedItem)); 
		DialogCCSPUsersCms dialog = new DialogCCSPUsersCms(selectedItem, DialogCaption.EDIT, this);
		dialog.showDialog();
	}
 
	@Override
	public void showCopyDialog(UserCms selectedItem) {
		logger.info(mainApp.getTransid() + ", call showDialog, selectedItem = " + String.valueOf(selectedItem)); 
		DialogCCSPUsersCms secUsersDialog = new DialogCCSPUsersCms(selectedItem, DialogCaption.COPY, this);
		secUsersDialog.showDialog();
	}
	
	@Override
	public void exportTable() {
	}

	@Override
	public DialogSubmitResult dialogSubmit_(UserCms item, LDInputDialog<UserCms> dialog) {
		DialogCCSPUsersCms dialogX = (DialogCCSPUsersCms) dialog;
		item.setUsertype((Integer) dialogX.getComboboxType().getValue());
		logger.info(mainApp.getTransid() + ", usertype: " + item.getUsertype()); 
		tagRollName(item);
		
		if(DialogCaption.ADD.equals(dialog.getDCaption()) || DialogCaption.COPY.equals(dialog.getDCaption())){
			try {
				String sql = "select 1 from user_cms where status = 1 and username = ?";
				if(baseDAO.hasResult(mainApp.getTransid(), sql, item.getUsername())){
					return new DialogSubmitResult("Duplicate username, plz reenter!");
				}
				String pass = "123456Abc";
				try { 
					pass = StringUtil.encrypt(pass, "SHA");
				} catch (Exception e) {
				} 
				item.setPassword(pass); 
				int id = baseDAO.insertBean(mainApp.getTransid(), item);
				item.setId(id);
				
			} catch (Exception e) {
			}
			return null;
		}
		if(DialogCaption.EDIT.equals(dialog.getDCaption())){
			int rs = 0;
			try {
				String sql = "select 1 from user_cms where status = 1 and username = ? and id != " + item.getId();
				if(baseDAO.hasResult(mainApp.getTransid(), sql, item.getUsername())){
					return new DialogSubmitResult("Duplicate username, plz reenter!");
				}
				rs = baseDAO.updateBean(mainApp.getTransid(), item);
			} catch (Exception e) {
			}
			if(rs <= 0) {
				return new DialogSubmitResult("Can not update database ....");
			}
			return null;
		}
		return null;
	}
	
	private String tagRollName(UserCms item) {
		String str = "Normal";
		item.setRolename(";staff;");
		
		if(item.getUsertype() == UserCms.CCSP_USERTYPE_ADMIN) {
			str = "ADMIN";
			item.setRolename(";admin;");
		} else if(item.getUsertype() == UserCms.CCSP_USERTYPE_ROOT) {
			str = "ROOT";
			item.setRolename(";root;");
		} 
		item.setUserTypeStr(str); 
		return str;
	}
}
