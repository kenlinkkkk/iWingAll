package com.ligerdev.cmms.component.table;

import java.util.Set;

import org.apache.log4j.Logger;

import com.ligerdev.appbase.utils.textbase.Log4jLoader;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.themes.Reindeer;
import com.xtel.cms.MainApplication;

public class XGUI_Actions<T> extends HorizontalLayout {
	
	// IDUFXT = full
	// IUFXT = trừ delete (vì chỉ muốn cho update disable)
	// UFI
	private static final long serialVersionUID = 1L;
	public static final String ROLE_CREATE = "I";
	public static final String ROLE_COPY = "C";
	public static final String ROLE_DELETE = "D";
	public static final String ROLE_UPDATE = "U";
	public static final String ROLE_SEARCH = "F";
	public static final String ROLE_EXPORT = "X";
	public static final String ROLE_FILTER = "T";
	public static final String ROLE_REFRESH = "R";
	
	public static final String ROLE_GROUP_FULL = ROLE_CREATE + ROLE_COPY + ROLE_DELETE + ROLE_UPDATE + ROLE_SEARCH + ROLE_EXPORT + ROLE_FILTER;
	public static final String ROLE_VIEW_ADD_DEL = ROLE_CREATE + ROLE_DELETE;
	public static final String ROLE_VIEW_ADD_DEL_FILETER = ROLE_CREATE + ROLE_DELETE + ROLE_FILTER;
	public static final String ROLE_VIEW_SEARCH_FILETER_EXPORT = ROLE_SEARCH  + ROLE_FILTER + ROLE_EXPORT;
	public static final String ROLE_VIEW_ADD_EDIT_DEL = ROLE_CREATE + ROLE_DELETE + ROLE_UPDATE;
	public static final String ROLE_VIEW_ADD_EDIT_DEL_FILTER = ROLE_CREATE + ROLE_DELETE + ROLE_UPDATE + ROLE_FILTER;
	public static final String ROLE_VIEW_ADD_EDIT_FILTER = ROLE_CREATE + ROLE_UPDATE + ROLE_FILTER;
	
	public static final String ROLE_VIEW_ADD_COPY_EDIT_DEL = ROLE_CREATE + ROLE_COPY + ROLE_DELETE + ROLE_UPDATE;
	public static final String ROLE_VIEW_ADD_COPY_EDIT_DEL_FILETER = ROLE_CREATE  + ROLE_COPY + ROLE_DELETE + ROLE_UPDATE + ROLE_FILTER;
	public static final String ROLE_VIEW_ADD_COPY_EDIT_DEL_SEARCH = ROLE_CREATE  + ROLE_COPY + ROLE_DELETE + ROLE_UPDATE + ROLE_SEARCH;
	public static final String ROLE_VIEW_ADD_COPY_EDIT_DEL_SEARCH_FILETER = ROLE_CREATE  + ROLE_COPY + ROLE_DELETE + ROLE_UPDATE + ROLE_SEARCH + ROLE_FILTER; 
	
	protected ITableActionsListener<T> tableActionInterface;
	protected Button btnRefresh;
	protected Button btnAdd;
	protected Button btnEdit;
	protected Button btnDelete;
	protected Button btnSearch;
	protected Button btnCopy;
	protected Button btnExport;
	protected String permision;
	protected TextField txtFilter;
	protected Button btnFilter;
	
	private static Logger logger = Log4jLoader.getLogger();
	private MainApplication mainApp = (MainApplication) MainApplication.getCurrent();
	protected boolean enable_edit_multi_row = false;
	private XGUI_TableLayout<T> mainLayout;
	
	@SuppressWarnings("serial")
	public XGUI_Actions(final XGUI_TableLayout<T> mainLayout, String permision, final IPagingListener iPagingListener, 
			ITableActionsListener<T> parent, final XOptions xoption) {
		
		this.mainLayout = mainLayout;
		this.txtFilter = mainLayout.txtFilter;
		this.tableActionInterface = parent;
		// setSizeFull();
		
		this.permision = permision; // this.tableActionInterface.getPermission();
		ThemeResource icon = null;
		
		if(this.permision == null){ 
			this.permision = "#";
		}
		//------------------------------------------------------------------------
		
		if(this.permision.contains(ROLE_REFRESH)) {
			icon = new ThemeResource("icons/16/refresh.png");
			if(mainApp != null && mainApp.lang_vi) {
				this.btnRefresh = createButton(icon, "tải lại");
				this.btnRefresh.setDescription("Tải lại trang");
			} else {
				this.btnRefresh = createButton(icon, "refresh");
				this.btnRefresh.setDescription("refresh current page");
			}
			if(xoption != null && xoption.button_cap_refresh != null) {
				this.btnRefresh.setCaption(xoption.button_cap_refresh); 
			}
			if(xoption != null && xoption.button_cap_refresh_tooltip != null) {
				this.btnRefresh.setDescription(xoption.button_cap_refresh_tooltip); 
			}
			this.btnRefresh.addClickListener(new Button.ClickListener() {
				public void buttonClick(Button.ClickEvent event) {
					int page = mainLayout.pagingLayout.curPage;
					iPagingListener.getPage(page, true, XGUI_Actions.this.txtFilter.getValue().trim());
				}
			});
		}
		//------------------------------------------------------------------------
		icon = new ThemeResource("icons/16/add.png");
		if(mainApp != null && mainApp.lang_vi) {
			this.btnAdd = createButton(icon, "thêm");
			this.btnAdd.setDescription("Thêm mới bản ghi");
		} else {
			this.btnAdd = createButton(icon, "add");
			this.btnAdd.setDescription("add new item");
		}
		if(xoption != null && xoption.button_cap_add != null) {
			this.btnAdd.setCaption(xoption.button_cap_add); 
		}
		if(xoption != null && xoption.button_cap_add_tooltip != null) {
			this.btnAdd.setDescription(xoption.button_cap_add_tooltip); 
		}
		this.btnAdd.addClickListener(new Button.ClickListener() {
			public void buttonClick(Button.ClickEvent event) {
				tableActionInterface.showAddDialog();
			}
		});
		
		//------------------------------------------------------------------------
		icon = new ThemeResource("icons/16/edit.png");
		if(mainApp != null && mainApp.lang_vi) {
			this.btnEdit = createButton(icon, "sửa");
			this.btnEdit.setDescription("sửa bản ghi đã chọn");
		} else {
			this.btnEdit = createButton(icon, "edit");
			this.btnEdit.setDescription("edit selected item");
		}
		if(xoption != null && xoption.button_cap_edit != null) {
			this.btnEdit.setCaption(xoption.button_cap_edit); 
		}
		if(xoption != null && xoption.button_cap_edit_tooltip != null) {
			this.btnEdit.setDescription(xoption.button_cap_edit_tooltip); 
		}
		this.btnEdit.addClickListener(new Button.ClickListener() {
			public void buttonClick(Button.ClickEvent event) {
				Set<T> set = (Set<T>) mainLayout.tableLayout.getValue();
				if(set == null || set.size() <= 0) {
					return;
				} 
				if(set.size() == 1) {
					tableActionInterface.showEditDialog(set, null);
				} else {
					tableActionInterface.showEditListDialog(set);
				}
			}
		});
		
		//------------------------------------------------------------------------
		icon = new ThemeResource("icons/16/delete.png");
		if(mainApp != null && mainApp.lang_vi) {
			this.btnDelete = createButton(icon, "xóa");
			this.btnDelete.setDescription("xóa bản ghi đã chọn");
		} else {
			this.btnDelete = createButton(icon, "delete");
			this.btnDelete.setDescription("delete selected item");
		}
		if(xoption != null && xoption.button_cap_del != null) {
			this.btnDelete.setCaption(xoption.button_cap_del); 
		}
		if(xoption != null && xoption.button_cap_del_tooltip != null) {
			this.btnDelete.setDescription(xoption.button_cap_del_tooltip); 
		}
		this.btnDelete.addClickListener(new Button.ClickListener() {
			public void buttonClick(Button.ClickEvent event) {
				Set<T> set = (Set<T>) mainLayout.tableLayout.getValue();
				tableActionInterface.deleteSelectedItem(set, xoption);
			}
		});
		
		//------------------------------------------------------------------------
		icon = new ThemeResource("icons/16/search.png");
		if(mainApp != null && mainApp.lang_vi) {
			this.btnSearch = createButton(icon, "tìm kiếm");
			this.btnSearch.setDescription("tìm kiếm theo tiêu chí");
		} else {
			this.btnSearch = createButton(icon, "search");
			this.btnSearch.setDescription("search");
		}
		if(xoption != null && xoption.button_cap_search != null) {
			this.btnSearch.setCaption(xoption.button_cap_search); 
		}
		if(xoption != null && xoption.button_cap_search_tooltip != null) {
			this.btnSearch.setDescription(xoption.button_cap_search_tooltip); 
		}
		this.btnSearch.addClickListener(new Button.ClickListener() {
			public void buttonClick(Button.ClickEvent event) {
				tableActionInterface.showSearchDialog();
			}
		});
		
		//------------------------------------------------------------------------
		icon = new ThemeResource("icons/16/add_copy.png");
		this.btnCopy = createButton(icon, "copy");
		if(mainApp != null && mainApp.lang_vi) {
			this.btnCopy.setDescription("copy bản ghi đã chọn");
		} else {
			this.btnCopy.setDescription("copy selected item");
		}
		if(xoption != null && xoption.button_cap_copy != null) {
			this.btnCopy.setCaption(xoption.button_cap_copy); 
		}
		if(xoption != null && xoption.button_cap_copy_tooltip != null) {
			this.btnCopy.setDescription(xoption.button_cap_copy_tooltip); 
		}
		this.btnCopy.addClickListener(new Button.ClickListener() {
			public void buttonClick(Button.ClickEvent event) {
				Set<T> set = (Set<T>) mainLayout.tableLayout.getValue();
				tableActionInterface.showCopyDialog(set);
			}
		});
		
		//------------------------------------------------------------------------
		icon = new ThemeResource("icons/16/export.png");
		if(mainApp != null && mainApp.lang_vi) {
			this.btnExport = createButton(icon, "xuất file");
			this.btnExport.setDescription("xuất bảng ra file");
		} else {
			this.btnExport = createButton(icon, "export");
			this.btnExport.setDescription("export table");
		}
		if(xoption != null && xoption.button_cap_export != null) {
			this.btnExport.setCaption(xoption.button_cap_export); 
		}
		if(xoption != null && xoption.button_cap_export_tooltip != null) {
			this.btnExport.setDescription(xoption.button_cap_export_tooltip); 
		}
		this.btnExport.addClickListener(new Button.ClickListener() {
			public void buttonClick(Button.ClickEvent event) {
				tableActionInterface.exportTable();
			}
		});
		
		//------------------------------------------------------------------------
		this.txtFilter.addStyleName(Reindeer.TEXTFIELD_SMALL);
		this.txtFilter.setHeight("5mm");
		this.txtFilter.addShortcutListener(new ShortcutListener("Enter", ShortcutAction.KeyCode.ENTER, null) {
			@Override
			public void handleAction(Object sender, Object target) {
				logger.info(mainApp.getTransid() + ", user click searchSimple: " + XGUI_Actions.this.txtFilter.getValue());
				iPagingListener.getPage(1, true, XGUI_Actions.this.txtFilter.getValue().trim());
			}
		});
		if(mainApp != null && mainApp.lang_vi) {
			this.btnFilter = new Button("Lọc &nbsp;&nbsp;");
		} else {
			this.btnFilter = new Button("Filter &nbsp;&nbsp;");
		}
		if(xoption != null && xoption.button_cap_filter != null) {
			this.btnFilter.setCaption(xoption.button_cap_filter); 
		}
		if(xoption != null && xoption.button_cap_filter_tooltip != null) {
			this.btnFilter.setDescription(xoption.button_cap_filter_tooltip); 
		}
		this.btnFilter.setCaptionAsHtml(true); 
		this.btnFilter.setHeight("4.5mm");
		this.btnFilter.setStyleName("link");
		this.btnFilter.addStyleName("btnLink2");
		
		this.btnFilter.addClickListener(new ClickListener() {
			public void buttonClick(ClickEvent event) {
				logger.info(mainApp.getTransid() + ", user click searchSimple: " + XGUI_Actions.this.txtFilter.getValue());
				iPagingListener.getPage(1, true, XGUI_Actions.this.txtFilter.getValue().trim());
			}
		}); 
		//------------------------------------------------------------------------
		setSpacing(true);
		setDefaultComponentAlignment(Alignment.MIDDLE_LEFT);
		
		if(this.permision.contains(ROLE_REFRESH) && this.btnRefresh != null) {
			addComponent(this.btnRefresh);
		}
		addComponent(this.btnAdd);
		addComponent(this.btnCopy);
		addComponent(this.btnEdit);
		addComponent(this.btnDelete);
		addComponent(this.btnExport);
		addComponent(this.btnSearch);
		addComponent(this.txtFilter);
		addComponent(this.btnFilter);
		checkPermission();
		setRowSelected(0);
	}


	public void checkPermission() {
		logger.info(mainApp.getTransid() + ", permision = " + permision);
		if(this.permision == null){
			return;
		}
		this.btnAdd.setEnabled(this.permision.contains(ROLE_CREATE));
		this.btnCopy.setEnabled(this.permision.contains(ROLE_COPY));
		
		this.btnEdit.setEnabled(this.permision.contains(ROLE_UPDATE));
		this.btnDelete.setEnabled(this.permision.contains(ROLE_DELETE));
		this.btnSearch.setEnabled(this.permision.contains(ROLE_SEARCH));
		this.btnExport.setEnabled(this.permision.contains(ROLE_EXPORT)); 
		
		this.btnFilter.setEnabled(this.permision.contains(ROLE_FILTER));
		this.txtFilter.setEnabled(this.permision.contains(ROLE_FILTER));
	}

	private Button createButton(ThemeResource icon, String caption) {
		Button btn = new Button();
		btn.setStyleName("link");
		btn.setIcon(icon);
		//btn.setWidth("17px");
		btn.setHeight("17px");
		btn.setCaption(caption + " &nbsp;&nbsp;"); 
		btn.setCaptionAsHtml(true); 
		btn.addStyleName("btnLink"); // bỏ gạch chân
		return btn;
	}

	public void setRowSelected(int selected) {
		if(selected <= 0){
			this.btnEdit.setEnabled(false);
			this.btnDelete.setEnabled(false);
			this.btnCopy.setEnabled(false);
			
		} else if(selected == 1){
			this.btnCopy.setEnabled(this.permision.contains(ROLE_COPY));
			this.btnEdit.setEnabled(this.permision.contains(ROLE_UPDATE));
			this.btnDelete.setEnabled(this.permision.contains(ROLE_DELETE));
			
		} else if(selected > 1){
			if(enable_edit_multi_row) {
				this.btnEdit.setEnabled(true);
			} else {
				this.btnEdit.setEnabled(false);
			}
			this.btnDelete.setEnabled(this.permision.contains(ROLE_DELETE));
			this.btnCopy.setEnabled(false);
		}
	}

	public void setEnableEditMultiRow(boolean b) {
		enable_edit_multi_row = b;
	}
}
