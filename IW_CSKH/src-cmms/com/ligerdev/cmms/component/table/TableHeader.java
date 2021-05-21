package com.ligerdev.cmms.component.table;

import java.io.Serializable;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.httpclient.NameValuePair;

import com.ligerdev.cmms.component.entity.XClickListener;
import com.vaadin.shared.ui.label.ContentMode;

public class TableHeader implements Serializable {

	private Object key;
	private Object value;
	
	private Object tooltip;
	private XClickListener clickListener;
	private IMapValueCell mapValue;
	private ContentMode contentMode;
	
	// GUI
	private Boolean alignLeft; 	// căn lề trái
	private boolean isButton;	// là nút button  
	private boolean formatNumber;		// tiền mặt => format
	private Object viewOtherCol;
	
	private boolean showIconX;	// khác V thì là X, chứ k phải là rỗng
	private Object icon;		// show icon V, X
	
	private String imageW; 		// dùng cho ảnh
	private String imageH;		// dùng cho ảnh
	
	private Hashtable <Object, Object> mapValueStr;  // map giá trị sang giá trị hiển thị khác 
	private Hashtable <Object, NameValuePair> mapValueIcon; // map giá trị sang giá trị hiển thị khác 
	
	private String dateFormat; // kiểu date
	private String reFormatTime_old;	// kiểu string
	private String reFormatTime_new;	// kiểu string
	private Integer fixWidth = null;
	//private boolean wordwrap = false;
	private boolean bold = false; 
	
	public TableHeader() {
		// TODO Auto-generated constructor stub
	}
	
	public TableHeader(Object key, Object value){
		this.key = key;
		this.value = value;
	}
	
	public TableHeader(Object key, Object value, String dateFormat){
		this(key, value);
		this.dateFormat = dateFormat;
	}

	public TableHeader(Object key, Object value, String dateFormat, Object icon) {
		this(key, value, dateFormat);
		this.setIcon(icon);
	} 
	
	// ------------------------------------ init ----------------------
	public static TableHeader init(Object key, Object value){
		return new TableHeader(key, value);
	}
	
	public static TableHeader initFormat(Object key, Object value, String dateFormat){
		TableHeader temp = new TableHeader(key, value);
		temp.setDateFormat(dateFormat);
		return temp;
	}

	public static TableHeader initFormatIcon(Object key, Object value, String dateFormat, Object icon) {
		TableHeader temp = new TableHeader(key, value, dateFormat);
		temp.setIcon(icon);
		return temp;
	} 
	
	public static TableHeader initFormatIcon(Object key, Object value, Object icon) {
		TableHeader temp = new TableHeader(key, value);
		temp.setIcon(icon);
		return temp;
	} 
	
	public static TableHeader initTooltip(Object key, Object value, Object tooltip) {
		TableHeader temp = new TableHeader(key, value);
		temp.setTooltip(tooltip);
		return temp;
	} 
	
	public static TableHeader initButton(Object key, Object value, Object tooltip, XClickListener clickListener) {
		TableHeader temp = new TableHeader(key, value);
		temp.setTooltip(tooltip);
		temp.setButton(true);
		temp.setClickListener(clickListener);
		return temp;
	} 
	// ----------------------------------------------------------------------------------------
	
	public Object getKey() {
		return key;
	}

	public TableHeader setKey(Object key) {
		this.key = key;
		return this;
	}

	public Object getValue() {
		return value;
	}

	public TableHeader setValue(Object value) {
		this.value = value;
		return this;
	}

	public String getDateFormat() {
		return dateFormat;
	}
	
	public <X, Y> TableHeader addMapValue(LinkedHashMap<X, Y> data) {
		if(mapValueStr == null) {
			mapValueStr = new Hashtable<Object, Object>();
		}
		if(data != null && data.size() > 0) {
			Set<Entry<X, Y>> set = data.entrySet();
			Iterator<Entry<X, Y>> iter = set.iterator();
			
			while(iter.hasNext()) {
				Entry<X, Y> entry = iter.next();
				mapValueStr.put(entry.getKey(), entry.getValue());
			}
		}
		return this;
	}

	public TableHeader addMapValue(Object value, Object newValue) {
		if(mapValueStr == null) {
			mapValueStr = new Hashtable<Object, Object>();
		}
		mapValueStr.put(value, newValue);
		return this;
	}
	
	public TableHeader addMapValueOther(Object newValue) {
		if(mapValueStr == null) {
			mapValueStr = new Hashtable<Object, Object>();
		}
		mapValueStr.put("$other$", newValue);
		return this;
	}
	
	public TableHeader reFormatTime(String oldFormat, String newFormat) {
		setReFormatTime_old(oldFormat);
		setReFormatTime_new(newFormat);
		return this;
	}
	
	// ======================== ICON ==============================
	// iconPath = icons/16/x.png
	public TableHeader addMapIcon(Object valueColumn, String iconPath, String title) {
		if(mapValueIcon == null) {
			mapValueIcon = new Hashtable<Object, NameValuePair>();
		} 
		mapValueIcon.put(valueColumn, new NameValuePair(iconPath, title));
		return this; 
	}
	
	public TableHeader addMapIconOther(String iconPath, String title) {
		if(mapValueIcon == null) {
			mapValueIcon = new Hashtable<Object, NameValuePair>();
		}
		mapValueIcon.put("$other$", new NameValuePair(iconPath, title));
		return this;
	}
	//=============================== end icon =================================
	
	public TableHeader setDateFormat(String dateFormat) {
		this.dateFormat = dateFormat;
		return this;
	}

	public Object getIcon() {
		return icon;
	}

	public TableHeader setIcon(Object icon) {
		this.icon = icon;
		return this;
	}

	public Object getTooltip() {
		return tooltip;
	}

	public TableHeader setTooltip(Object tooltip) {
		this.tooltip = tooltip;
		return this;
	}

	public Boolean isAlignLeft() {
		return alignLeft;
	}

	public TableHeader setAlignLeft(Boolean alignLeft) {
		this.alignLeft = alignLeft;
		return this;
	} 

	public boolean isButton() {
		return isButton;
	}

	public TableHeader setButton(boolean isButton) {
		this.isButton = isButton;
		return this;
	}

	public XClickListener getClickListener() {
		return clickListener;
	}

	public TableHeader setClickListener(XClickListener clickListener) {
		this.clickListener = clickListener;
		return this;
	}

	public boolean isShowIconX() {
		return showIconX;
	}

	public TableHeader setShowIconX(boolean showIconX) {
		this.showIconX = showIconX;
		return this;
	}
	
	public boolean getFormatNumber() {
		return formatNumber;
	}

	public TableHeader setFormatNumber(boolean formatNumber) {
		this.formatNumber = formatNumber;
		return this;
	}

	public TableHeader setImage(String w, String h) {
		this.imageW = w;
		this.imageH = h;
		return this;
	}
	
	public TableHeader setWidth(Integer pixel) {
		this.fixWidth = pixel;
		return this;
	}  
	
	public Integer getWidth() {
		return fixWidth;
	} 
	
	
	/* public boolean isWordwrap() {
		return wordwrap;
	}

	public TableHeader setWordwrap(boolean wordwrap) {
		this.wordwrap = wordwrap;
		return this;
	} */
	
	public String getImageW() {
		return imageW;
	}

	public String getImageH() {
		return imageH;
	}

	public Hashtable<Object, Object> getMapValueStr() {
		return mapValueStr;
	}

	public TableHeader setMapValueStr(Hashtable<Object, Object> mapValueStr) {
		this.mapValueStr = mapValueStr;
		return this;
	}

	public Hashtable<Object, NameValuePair> getMapValueIcon() {
		return mapValueIcon;
	}

	public TableHeader setMapValueIcon(Hashtable<Object, NameValuePair> mapValueIcon) {
		this.mapValueIcon = mapValueIcon;
		return this;
	}

	public String getReFormatTime_new() {
		return reFormatTime_new;
	}

	public TableHeader setReFormatTime_new(String reFormatTime_new) {
		this.reFormatTime_new = reFormatTime_new;
		return this;
	}

	public String getReFormatTime_old() {
		return reFormatTime_old;
	}

	public TableHeader setReFormatTime_old(String reFormatTime_old) {
		this.reFormatTime_old = reFormatTime_old;
		return this;
	}

	public IMapValueCell getMapValue() {
		return mapValue;
	}

	public TableHeader setMapValue(IMapValueCell mapValue) {
		this.mapValue = mapValue;
		return this;
	}

	public ContentMode getContentMode() {
		return contentMode;
	} 
	
	public TableHeader setContentMode(ContentMode contentMode) {
		this.contentMode = contentMode;
		return this;
	}

	public Object getViewOtherCol() {
		return viewOtherCol;
	}

	public TableHeader setViewOtherCol(Object viewOtherCol) {
		this.viewOtherCol = viewOtherCol;
		return this;
	}

	public boolean isBold() {
		return bold;
	}

	public TableHeader setBold(boolean bold) {
		this.bold = bold;
		return this;
	}
}
