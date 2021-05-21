package com.ligerdev.cmms.component;

import java.util.ArrayList;

import javax.servlet.http.Cookie;

import com.ligerdev.appbase.utils.BaseUtils;
import com.ligerdev.appbase.utils.cache.CacheSyncFile;
import com.ligerdev.appbase.utils.db.BaseDAO;
import com.ligerdev.appbase.utils.db.XBaseDAO;
import com.ligerdev.appbase.utils.entities.PairIntString;
import com.ligerdev.appbase.utils.entities.PairStringInt;
import com.ligerdev.appbase.utils.entities.PairStringObject;
import com.ligerdev.appbase.utils.entities.PairStringString;
import com.ligerdev.cmms.component.dialog.CheckboxConverter;
import com.vaadin.server.ThemeResource;
import com.vaadin.server.VaadinService;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.DateField;
import com.vaadin.ui.Label;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.xtel.cms.MainApplication;

public class AppCmmsUtils {

	private static BaseDAO baseDAO = BaseDAO.getInstance(BaseDAO.POOL_NAME_MAIN);
	private static XBaseDAO xbaseDAO = XBaseDAO.getInstance(BaseDAO.POOL_NAME_MAIN);
	private static String ATTR_PAGESIZE = "PageSize";
	private static int DEFAULT_PAGESIZE = 20;
	public static CacheSyncFile cache = CacheSyncFile.getInstance(1000000);
	
	public static void setDefaultPageSize(int i) {
		DEFAULT_PAGESIZE = i;
	}
	
	public static Button createButtonImage(String pathImage, String caption) { // 32/Close_folder.png
		ThemeResource icon = new ThemeResource("icons/" + pathImage);
		Button btn = new Button();
		btn.setStyleName("link");
		btn.setIcon(icon);
		//btn.setWidth("17px");
		//btn.setHeight("17px");
		btn.setCaption(caption); 
		btn.setCaptionAsHtml(true); 
		btn.addStyleName("btnLink"); // bỏ gạch chân
		return btn;
	}
	
	public static Cookie getCookie(String name) {
		try {
			Cookie[] cookies = VaadinService.getCurrentRequest().getCookies();
			for (Cookie cookie : cookies) {
				if (name.equals(cookie.getName())) {
					return cookie;
				}
			}
		} catch (Exception e) {
		}
		return null;
	}

	public static void addCookie(String name, String value, int seconds) {
		try {
			Cookie myCookie = new Cookie(name, value);
			myCookie.setMaxAge(seconds);
			myCookie.setPath(VaadinService.getCurrentRequest().getContextPath());
			VaadinService.getCurrentResponse().addCookie(myCookie);
		} catch (Exception e) {
		}
	}
	
	public static int getPageSize(){
		Integer rs = (Integer) UI.getCurrent().getSession().getAttribute(ATTR_PAGESIZE);
		if(rs != null && rs > 0){
			return rs;
		}
		Cookie cookie = getCookie(ATTR_PAGESIZE);
		if(cookie == null){
			return DEFAULT_PAGESIZE;
		}
		return BaseUtils.parseInt(cookie.getValue(), DEFAULT_PAGESIZE); 
	}
	
	public static void savePageSize(int value){
		 UI.getCurrent().getSession().setAttribute(ATTR_PAGESIZE, value);
		addCookie(ATTR_PAGESIZE, String.valueOf(value), 60 * 60 * 24 * 30 * 6); // 6 months 
	}
	
	public static String getFilterRestrictionSql(String filterText, String alias, String ... fields){
		if(BaseUtils.isBlank(filterText)){
			return "(1 = 1)";
		}
		if(fields == null || fields.length == 0){
			return "(1 = 1)";
		}
		String sqlRestriction = "";
		filterText = filterText.replace("%", "").replace("'", "")
				.replace("\"", "").toLowerCase().replaceAll(" +", " ").trim();
		
		for(String s : fields){
			sqlRestriction += " or lower(" + (alias == null ? "" : alias + ".")  + s + ") like '%" + filterText + "%'";
		}
		sqlRestriction = sqlRestriction.replaceFirst(" or", "");
		sqlRestriction = "(" + sqlRestriction + ")";
		return sqlRestriction;
	}
	
	public static int countRecord(String sql) {
		return countRecord(sql, xbaseDAO);
	}
	
	public static int countRecord(String sql, XBaseDAO xbaseDAO) {
		String newSql = "select count(*) from (" + sql + ") tmpTbl";
		return xbaseDAO.getFirstCell("CountRs", newSql, Integer.class);
	}
	
	public static ComboBox createComboBox(String caption, ArrayList<PairStringInt> listE, boolean isRequire){
		return createComboBox(caption, listE, isRequire, null);
	}
	
	public static ComboBox createComboBox(String caption, ArrayList<PairStringInt> listE, boolean isRequire, String width){
		ComboBox cb = new ComboBox(caption);
		cb.setFilteringMode(FilteringMode.CONTAINS);
		
		if(listE != null && listE.size() > 0){
			for(PairStringInt m: listE){
				cb.addItem(m.getValue());
				cb.setItemCaption(m.getValue(), m.getName()); 
			}
		}
		// ipRange.select(listE.get(0).getKey());
		// ipRange.setImmediate(true);
		if(isRequire){
			cb.setRequired(true);
			cb.setNullSelectionAllowed(false);
			
			MainApplication mainApp = (MainApplication) MainApplication.getCurrent();
			if(mainApp != null && mainApp.lang_vi) {
				cb.setRequiredError("Trường [" + caption + "] không hợp lệ");
			} else {
				cb.setRequiredError("Field [" + caption + "] is invalid");
			}
		}
		if(BaseUtils.isNotBlank(width)){ 
			cb.setWidth(width);
		}
		cb.setTextInputAllowed(false); 
		// ipRange.setNullSelectionItemId(listE.get(0).getKey());
		return cb;
	}
	
	public static ComboBox createComboBox2(String caption, ArrayList<PairStringString> listE, boolean isRequire){
		return createComboBox2(caption, listE, isRequire, null);
	}
	
	public static ComboBox createComboBox2(String caption, ArrayList<PairStringString> listE, boolean isRequire, String width){
		ComboBox cb = new ComboBox(caption);
		cb.setFilteringMode(FilteringMode.CONTAINS);
		
		if(listE != null && listE.size() > 0){
			for(PairStringString m: listE){
				cb.addItem(m.getValue());
				cb.setItemCaption(m.getValue(), m.getName()); 
			}
		}
		// ipRange.select(listE.get(0).getKey());
		// ipRange.setImmediate(true);
		if(isRequire){
			cb.setRequired(true);
			cb.setNullSelectionAllowed(false);
			
			MainApplication mainApp = (MainApplication) MainApplication.getCurrent();
			if(mainApp != null && mainApp.lang_vi) {
				cb.setRequiredError("Trường [" + caption + "] không hợp lệ");
			} else {
				cb.setRequiredError("Field [" + caption + "] is invalid");
			}
		}
		if(BaseUtils.isNotBlank(width)){ 
			cb.setWidth(width);
		}
		cb.setTextInputAllowed(false); 
		// ipRange.setNullSelectionItemId(listE.get(0).getKey());
		return cb;
	}
	
	public static ComboBox createComboBox3(String caption, ArrayList<PairStringObject> listE, boolean isRequire){
		return createComboBox3(caption, listE, isRequire, null);
	}
	
	public static ComboBox createComboBox3(String caption, ArrayList<PairStringObject> listE, boolean isRequire, String width){
		ComboBox cb = new ComboBox(caption);
		cb.setFilteringMode(FilteringMode.CONTAINS);
		
		if(listE != null && listE.size() > 0){
			for(PairStringObject m: listE){
				cb.addItem(m.getValue());
				cb.setItemCaption(m.getValue(), m.getName()); 
			}
		}
		// ipRange.select(listE.get(0).getKey());
		// ipRange.setImmediate(true);
		if(isRequire){
			cb.setRequired(true);
			cb.setNullSelectionAllowed(false);
			
			MainApplication mainApp = (MainApplication) MainApplication.getCurrent();
			if(mainApp != null && mainApp.lang_vi) {
				cb.setRequiredError("Trường [" + caption + "] không hợp lệ");
			} else {
				cb.setRequiredError("Field [" + caption + "] is invalid");
			}
		}
		if(BaseUtils.isNotBlank(width)){ 
			cb.setWidth(width);
		}
		cb.setTextInputAllowed(false); 
		// ipRange.setNullSelectionItemId(listE.get(0).getKey());
		return cb;
	}
	
	public static TextField createTextField(String caption, String value, boolean isRequire){
		return createTextField(caption, value, isRequire, null);
	}
	
	public static TextField createTextField(String caption, String value, boolean isRequire, String width){
		TextField t = new TextField(caption, value);
		// t.setInvalidAllowed(false);
		// t.setInvalidCommitted(false);
		if(BaseUtils.isNotBlank(width)){ 
			t.setWidth(width);
		}
		t.setRequired(isRequire);
		t.setNullRepresentation("");
		
		if(isRequire){
			MainApplication mainApp = (MainApplication) MainApplication.getCurrent();
			if(mainApp != null && mainApp.lang_vi) {
				t.setRequiredError("Trường [" + caption + "] không hợp lệ");
			} else {
				t.setRequiredError("Field [" + caption + "] is invalid");
			}
		}
		return t;
	}
	
	public static TextArea createTextArea(String caption, String value, boolean isRequire){
		return createTextArea(caption, value, isRequire, null, null); 
	}
	
	public static TextArea createTextArea(String caption, String value, boolean isRequire, String width, String height){
		TextArea t = new TextArea(caption, value);
		// t.setInvalidAllowed(false);
		// t.setInvalidCommitted(false);
		if(BaseUtils.isNotBlank(width)){ 
			t.setWidth(width);
		}
		if(BaseUtils.isNotBlank(height)){ 
			t.setHeight(height); 
		}
		t.setRequired(isRequire);
		t.setNullRepresentation("");
		if(isRequire){
			MainApplication mainApp = (MainApplication) MainApplication.getCurrent();
			if(mainApp != null && mainApp.lang_vi) {
				t.setRequiredError("Trường [" + caption + "] không hợp lệ");
			} else {
				t.setRequiredError("Field [" + caption + "] is invalid");
			}
		}
		return t;
	}
	
	public static PasswordField createPassField(String caption, String value, boolean isRequire){
		return createPassField(caption, value, isRequire, null);
	}
	
	public static PasswordField createPassField(String caption, String value, boolean isRequire, String width){
		PasswordField t = new PasswordField(caption, value);
		// t.setInvalidAllowed(false);
		// t.setInvalidCommitted(false);
		if(BaseUtils.isNotBlank(width)){ 
			t.setWidth(width);
		}
		t.setRequired(isRequire);
		t.setNullRepresentation("");
		if(isRequire){
			MainApplication mainApp = (MainApplication) MainApplication.getCurrent();
			if(mainApp != null && mainApp.lang_vi) {
				t.setRequiredError("Trường [" + caption + "] không hợp lệ");
			} else {
				t.setRequiredError("Field [" + caption + "] is invalid");
			}
		}
		return t;
	}
	
	public static Label createLabel(String caption, String w){
		Label l = new Label(caption);
		l.setWidth(w);
		return l;
	}
	
	public static Label createLabelH(String caption, String h){
		Label l = new Label(caption);
		l.setHeight(h); 
		return l;
	}
	
	public static Label createLabel_html(String caption, String w){
		Label l = new Label(caption);
		l.setContentMode(ContentMode.HTML);
		l.setWidth(w);
		return l;
	}
	
	public static Label createLabelH_html(String caption, String h){
		Label l = new Label(caption);
		l.setContentMode(ContentMode.HTML);
		l.setHeight(h); 
		return l;
	}
	
	public static DateField createDateField(String caption, boolean isRequire){
		return createDateField(caption, isRequire, Resolution.MINUTE);
	}
	
	public static DateField createDateField(String caption, boolean isRequire, Resolution resolution){
		DateField t = new DateField(caption);
		t.setRequired(isRequire);
		// t.setInvalidAllowed(false);
		// t.setInvalidCommitted(false);
		if(resolution != null){
			t.setResolution(resolution);
			
			if(resolution == Resolution.DAY) {
				t.setDateFormat("yyyy/MM/dd");
			}
		} else {
			t.setDateFormat("yyyy/MM/dd HH:mm:ss");
		}
		MainApplication mainApp = (MainApplication) MainApplication.getCurrent();
		if(mainApp != null && mainApp.lang_vi) {
			t.setParseErrorMessage("Trường [" + caption + "] bị sai format");
		} else {
			t.setParseErrorMessage("Field [" + caption + "] is invalid format");
		}
		if(isRequire){
			if(mainApp != null && mainApp.lang_vi) {
				t.setRequiredError("Trường [" + caption + "] không hợp lệ");
			} else {
				t.setRequiredError("Field [" + caption + "] is invalid");
			}
		}
		return t;
	}

	public static CheckBox createCheckBox(String caption, boolean value) {
		CheckBox t = new CheckBox(caption, value);
		t.setCaptionAsHtml(true); 
		t.setConverter(new CheckboxConverter<Integer>(new Integer(1), new Integer(0))); 
		return t;
	}

	/*public static OptionGroup createOptionGroup(String caption, ArrayList<PairStringObject> listData, boolean isRequire) {
		OptionGroup cb = new OptionGroup(caption);
		if(listData != null && listData.size() > 0){
			for(PairStringObject m: listData){
				cb.addItem(m.getValue());
				cb.setItemCaption(m.getValue(), m.getName()); 
			}
		}
		if(isRequire){
			cb.setRequired(true);
			cb.setNullSelectionAllowed(false);
		}
		cb.setRequiredError("Field [" + caption + "] is invalid");
		//cb.setNullSelectionItemId(listData.get(0).getValue());
		return cb;
	}*/
	
	public static OptionGroup createOptionGroup(String caption, ArrayList<PairStringInt> listData, boolean isRequire) {
		OptionGroup cb = new OptionGroup(caption);
		if(listData != null && listData.size() > 0){
			for(PairStringInt m: listData){
				cb.addItem(m.getValue());
				cb.setItemCaption(m.getValue(), m.getName()); 
			}
		}
		if(isRequire){
			cb.setRequired(true);
			cb.setNullSelectionAllowed(false);
			
			MainApplication mainApp = (MainApplication) MainApplication.getCurrent();
			if(mainApp != null && mainApp.lang_vi) {
				cb.setRequiredError("Trường [" + caption + "] không hợp lệ");
			} else {
				cb.setRequiredError("Field [" + caption + "] is invalid");
			}
		}
		//cb.setNullSelectionItemId(listData.get(0).getValue());
		return cb;
	}
	
	public static OptionGroup createOptionGroup2(String caption, ArrayList<PairStringString> listData, boolean isRequire) {
		OptionGroup cb = new OptionGroup(caption);
		if(listData != null && listData.size() > 0){
			for(PairStringString m: listData){
				cb.addItem(m.getValue());
				cb.setItemCaption(m.getValue(), m.getName()); 
			}
		}
		if(isRequire){
			cb.setRequired(true);
			cb.setNullSelectionAllowed(false);
			
			MainApplication mainApp = (MainApplication) MainApplication.getCurrent();
			if(mainApp != null && mainApp.lang_vi) {
				cb.setRequiredError("Trường [" + caption + "] không hợp lệ");
			} else {
				cb.setRequiredError("Field [" + caption + "] is invalid");
			}
		}
		//cb.setNullSelectionItemId(listData.get(0).getValue());
		return cb;
	}
}
