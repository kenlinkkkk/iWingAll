package com.ligerdev.cmms.component.table;

import java.io.File;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;

import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.log4j.Logger;

import com.ligerdev.appbase.utils.BaseUtils;
import com.ligerdev.appbase.utils.textbase.Log4jLoader;
import com.vaadin.data.Container;
import com.vaadin.data.Property;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.FileResource;
import com.vaadin.server.ThemeResource;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.themes.ValoTheme;
import com.xtel.cms.MainApplication;

public class XGUI_Table<T> extends Table {

	private static final long serialVersionUID = 1L;
	public static final String INDEXED_COLUMN_CAPTION = "STT";
	private static Logger logger = Log4jLoader.getLogger();
	
	MainApplication mainApp = (MainApplication) MainApplication.getCurrent();
	Hashtable<String, TableHeader> listColumnHeader = new Hashtable<String, TableHeader>();
	Hashtable<String, Component> mapComponentGen = new Hashtable<String, Component>();
	int pageIndex = 1, pageSize = 20;
	XOptions xoption;
	XGUI_TableLayout<T> mainLayout;
	
	public XGUI_Table(XGUI_TableLayout<T> mainLayout, int pageIndex, Container data, int pageSize, XOptions xoption) {
		super(null, data);
		this.pageIndex = pageIndex;
		this.pageSize = pageSize;
		this.xoption = xoption;
		this.mainLayout = mainLayout;
		//setSizeFull();
		setSizeUndefined();

		// setFooterVisible(true);
		// setHeightUndefined();
		// addStyleName("striped");
		// setStyleName("table-header-center");
		
		//addRowTooltip();
		setColumnReorderingAllowed(true);
        setColumnCollapsingAllowed(true);
		setIndexedColumnVisible(true);
		
		setSelectable(true);
		setMultiSelect(true);
		setImmediate(true);
		setSortEnabled(true);
	}
	
	public void addVisibleColumn(ArrayList<TableHeader> arrayList){
		if(arrayList == null || arrayList.size() == 0){
			return;
		}
		addVisibleColumn(arrayList.toArray(new TableHeader[arrayList.size()]));  
	}
	
	public void addVisibleColumn(TableHeader ... entry){
		listColumnHeader.clear();
		mapComponentGen.clear();
		String headers[] = new String[entry.length];
		Object visibleColumns[] = new String[entry.length];
		//logger.info("0===> " + entry.length);
		
		for(int i = 0; entry != null && i < entry.length; i ++){
			// add các cột cần hiển thị, set từ ngoài, chỗ class build table
			headers[i] = String.valueOf(entry[i].getValue());
			visibleColumns[i] = String.valueOf(entry[i].getKey());
			listColumnHeader.put(String.valueOf(entry[i].getKey()), entry[i]);
			
			if(entry[i].getWidth() != null) {
				setColumnWidth(entry[i].getKey(), entry[i].getWidth());
			}
			// set các cột cần xử lý lại view
			if(entry[i].getValue() instanceof Component
					|| entry[i].isButton()
					|| entry[i].getImageW() != null
					|| entry[i].getIcon() != null
					|| entry[i].getMapValueIcon() != null
					|| entry[i].getMapValueStr() != null
					|| entry[i].getWidth() != null
			) {
				//logger.info("1===> " + entry[i].getKey());
				addGeneratedColumn(entry[i].getKey(), new IconColumnGenerator());
			} else {
				// @@test => thử cho vào hết class IconColumnGenerator 
				if(INDEXED_COLUMN_CAPTION.equalsIgnoreCase(String.valueOf(entry[i].getKey())) == false) {
					//logger.info("2===> " + entry[i].getKey());
					addGeneratedColumn(entry[i].getKey(), new IconColumnGenerator());
				}
			}
		}
		setVisibleColumns(visibleColumns);
		setColumnHeaders(headers);
		addRowTooltip();
	}
	
	public void removeVisibleColumn(String ... columnHeaders){
		if(columnHeaders == null || columnHeaders.length == 0){
			return;
		}
		for(String s : columnHeaders){
			removeVisibleColumn0(s);
		}
	}
	
	private void removeVisibleColumn0(String columnHeader){
		if(BaseUtils.isBlank(columnHeader)){
			return;
		}
		String headers[] = getColumnHeaders();
		Object visibleColumns[] = getVisibleColumns();
		
		/*TableHeader tableHeader = listColumnHeader.get(columnHeader);
		if(tableHeader == null){
			return;
		}
		int index = ArrayUtils.indexOf(headers, String.valueOf(tableHeader.getValue())); 
		*/
		
		int index = ArrayUtils.indexOf(visibleColumns, columnHeader);
		if(index < 0){
			return;
		}
		headers = ArrayUtils.remove(headers, index);
		visibleColumns = ArrayUtils.remove(visibleColumns, index);
		
		setVisibleColumns(visibleColumns);
		setColumnHeaders(headers);
	}
	
	@SuppressWarnings("serial")
	private void addRowTooltip() {
		setItemDescriptionGenerator(new ItemDescriptionGenerator() {
			public String generateDescription(Component source, Object itemId, Object columnId) {
				// source = TableData
				// itemId = object data
				// propertyId = column name (string: ex = @shop, productName, itemid...)
				
				//logger.info(mainApp + ", test generateDescription: " + (source == null ? null : source.getClass().getName()) + ", source: " + String.valueOf(source));
				//logger.info(mainApp + ", test generateDescription: " + (itemId == null ? null : itemId.getClass().getName()) + ", itemId: " + String.valueOf(itemId));
				//logger.info(mainApp + ", test generateDescription: " + (columnId == null ? null : columnId.getClass().getName()) + ", columnId: " + String.valueOf(columnId));
				
				//Property<?> pro = TableData.this.getContainerProperty(itemId, columnId);
				//if (pro == null) {
				//	return null;
				//}
				if(columnId == null) {
					return "";
				}
				String keyCompoentMap = (itemId == null ? null : itemId.hashCode()) + "." + (columnId == null ? null : columnId.hashCode()) ;
				Component com = mapComponentGen.get(keyCompoentMap);
				
				if(com != null && BaseUtils.isNotEmpty(com.getDescription())) {
					String str = String.valueOf(com.getDescription());
					str = str.replace("\n", "</br>").replace("\\n", "</br>");
					return str;
				}
				TableHeader columnHeader = listColumnHeader.get(columnId);
				// System.out.println("columnHeader = " + columnHeader);
				if(columnHeader == null) {
					return String.valueOf(columnId);
				}
				if(columnHeader.getTooltip() != null){
					//Property<?> pro2 = TableData.this.getContainerProperty(itemId, columnHeader.getTooltip());
					//if (pro2 != null) {
					//	String str = TableData.this.formatPropertyValue(itemId, columnHeader.getTooltip(), pro2);
					//	if(str != null){
					//		return str;
					//	}
					//}
					String str = String.valueOf(columnHeader.getTooltip());
					str = str.replace("\n", "</br>").replace("\\n", "</br>");
					return str;
				}
				if(columnHeader.getImageW() != null) { // ảnh
			        try {
			        	final BeanItemContainer<?> con = (BeanItemContainer<?>) getContainerDataSource();
						BeanItem<?> rowItem = con.getItem(itemId);
						Property<?> cellProperty = null;
						Object columnValue = null;
						
						if(columnHeader.getViewOtherCol() != null) { // giá trị cột A, nhưng show ra GUI lấy cột B
							columnId = columnHeader.getViewOtherCol();
						}
						try {
							cellProperty = rowItem.getItemProperty(columnId);
							columnValue = cellProperty.getValue();
						} catch (Exception e) {
						}
			        	//String colName = String.valueOf(columnId); 
			        	//String getter = "get" + String.valueOf(colName.charAt(0)).toUpperCase() + colName.substring(1);
						//Method m = itemId.getClass().getMethod(getter);
						//String value = m.invoke(itemId) + "";
						if(String.valueOf(columnValue).startsWith("http")) {
							return String.valueOf(columnValue);
						}
					} catch (Exception e) {
					}
				} 
				return columnHeader.getValue() + "";
			}
		});
	}

	public void setIndexedColumnVisible(boolean visible) {
		if (visible) {
			addGeneratedColumn(INDEXED_COLUMN_CAPTION, new IndexedColumnGenerator());
			setColumnWidth(INDEXED_COLUMN_CAPTION, 30);
			setColumnAlignment(INDEXED_COLUMN_CAPTION, Align.RIGHT);
		} else {
			// removeGeneratedColumn("STT");
		}
	}
	
	//@Override
	//protected String formatPropertyValue(Object rowId, Object colId, Property<?> cell) {
	//	// String pid = (String) colId;
	//	return getVaueStr(rowId, colId, cell.getValue());
	//}
	
	protected String getVaueStr(Object rowId, Object colId, Object cellValue, TableHeader columnHeader) {
		// truyền định nghĩa row total qua xoptions
		if(xoption != null && xoption.rowTotal_object != null && xoption.rowTotal_object == rowId) {
			if(xoption.rowTotal_title_colID != null && String.valueOf(colId).equalsIgnoreCase(xoption.rowTotal_title_colID)) {
				return xoption.rowTotal_title + ""; 
			}
			if(xoption.rowTotal_listTotalCol != null && xoption.rowTotal_listTotalCol.contains(colId + "")) {
				if(cellValue == null) {
					return "";
				}
				String s = getVaueStr0(rowId, colId, cellValue, columnHeader);
				return "<b>" + s + "</b>";
			}
			return "";
		}    
		if(cellValue == null) {
			return "";
		}
		String s = getVaueStr0(rowId, colId, cellValue, columnHeader);
		return s;
		/*
		String tmp = String.valueOf(rowId);
		boolean setBold = ( tmp.contains("=<b>Tổng") || tmp.contains("=<b>Total") ) 
				  && String.valueOf(cellValue).startsWith("<b") == false;
		 
		if(setBold) {
			if(String.valueOf(cellValue).equals("-2147483646")) { 
				return ""; // quy ước để show rỗng
			}
			String s = getVaueStr0(rowId, colId, cellValue, columnHeader);
			return "<b>" + s + "</b>";
		}
		String s = getVaueStr0(rowId, colId, cellValue, columnHeader);
		return s;
		*/
	}
	
	private String getVaueStr0(Object rowId, Object colId, Object cellValue, TableHeader columnHeader) {
		//TableHeader columnHeader = listColumnHeader.get(colId);
		//logger.info("===> test log: rowId=" + String.valueOf(rowId) + ", rowIdClass=" + rowId.getClass().getName() + ", colId=" + String.valueOf(colId)); 
		if(columnHeader != null && columnHeader.getFormatNumber()) {
			try {
				NumberFormat nf = new DecimalFormat("###,###,###,###");
				String s = String.valueOf(cellValue);
				cellValue = nf.format(Long.parseLong(s));  
				
				if(s.startsWith("+")) {
					return "+" + String.valueOf(cellValue); 
				}
				return String.valueOf(cellValue); 
				
			} catch (Exception e) {
				//logger.info(mainApp.getTransid() + ", Exception: " + e.getMessage());
				return String.valueOf(cellValue);
			} 
		}
		if(columnHeader != null && BaseUtils.isNotBlank(columnHeader.getReFormatTime_new())) {
			//logger.info(mainApp.getTransid() + ", columnHeader.getReFormatTime_new() " + columnHeader.getReFormatTime_new()); 
			return BaseUtils.reFormatTime(String.valueOf(cellValue), columnHeader.getReFormatTime_old(), columnHeader.getReFormatTime_new());
		}
		// System.out.println("pid = " + pid + ", val = " + val); 
		if (cellValue instanceof Number) {
			return String.valueOf(cellValue);
		}
		if (cellValue instanceof Date) {
			Date date = (Date) cellValue; 
			if(columnHeader != null && BaseUtils.isNotBlank(columnHeader.getDateFormat())){
				return BaseUtils.formatTime(columnHeader.getDateFormat(), date);
			}
			return BaseUtils.formatTime("yyyy-MM-dd HH:mm:ss", date);
		}
		return String.valueOf(cellValue);
	}

	@Override
	public Align getColumnAlignment(Object propertyId) {
		// xử lý căn lề
		try {
			// logger.info(mainApp.getTransid() + ", " + String.valueOf(propertyId)); 
			if(propertyId == null){
				return super.getColumnAlignment(propertyId);
			}
			Class<?> t = getContainerDataSource().getType(propertyId);
			if(t == null){
				return super.getColumnAlignment(propertyId);
			}
			// System.out.println("propertyId = " + propertyId + " => class = " + t.getName()); 
			TableHeader columnHeader = listColumnHeader.get(propertyId);
			
			if(columnHeader.isAlignLeft() != null) {
				if(columnHeader.isAlignLeft()){
					return Align.LEFT;
				} else {
					return Align.RIGHT;
				}
			}
			if(columnHeader.getIcon() != null) {
				return Align.CENTER;
			}
			if(columnHeader.getMapValueIcon() != null || columnHeader.getMapValueStr() != null) {
				return Align.LEFT;
			}
			if(columnHeader.getFormatNumber()){
				return Align.RIGHT; 
			}
			if (t == Date.class){
				return Align.CENTER;
			}
			if (t == String.class){
				return Align.LEFT;
			}
			if (t == Integer.TYPE
					|| t == Double.TYPE 
					|| t == Short.TYPE
					|| t == Float.TYPE 
					|| t == Byte.TYPE
					|| t == Long.TYPE 
					|| t.getSuperclass() == Number.class) {
				
				return Align.RIGHT;
			}
		} catch (Exception e) {
			logger.info(mainApp.getTransid() + ", Exception: " + e.getMessage());
		}
		return super.getColumnAlignment(propertyId);
	}

	@SuppressWarnings("serial")
	private class IndexedColumnGenerator implements Table.ColumnGenerator {
		// tự gen cột STT
		public Component generateCell(Table source, Object itemId, Object columnId) {
			int fromIndex = (pageIndex - 1) * pageSize;
			Container container = source.getContainerDataSource();
			
			if (container instanceof BeanItemContainer) {
				BeanItemContainer<?> con = (BeanItemContainer<?>) source.getContainerDataSource();
				int id = con.indexOfId(itemId);
				
				Label label = new Label("" + (id + 1 + fromIndex));
				label.setSizeUndefined();
				return label;
			}
			Label label = new Label("");
			label.setSizeUndefined();
			return label;
		}
	}
	
	public static void setProperty(Image image, TableHeader columnHeader) {
		image.setSizeUndefined();
		if(columnHeader.getImageW() != null) {
			image.setWidth(columnHeader.getImageW());
		} 
		else if (columnHeader.getWidth() != null) {
			image.setWidth(columnHeader.getWidth() + "px");
		}
		if(columnHeader.getImageH() != null) {
			image.setHeight(columnHeader.getImageH()); 
		}
	}
	
	public static void setProperty(Label label, TableHeader columnHeader) {
		if (label == null || columnHeader == null) {
			return;
		}
		label.setSizeUndefined();
		if (columnHeader.getWidth() != null) {
			label.setWidth(columnHeader.getWidth() + "px");
		}
		label.setStyleName(ValoTheme.LAYOUT_HORIZONTAL_WRAPPING);
		
		if(columnHeader.isBold()) {
			label.setContentMode(ContentMode.HTML);
			label.setValue("<b>" + label.getValue() + "</b>"); 
		} else {
			if (columnHeader.getContentMode() != null) {
				label.setContentMode(columnHeader.getContentMode());
			} else {
				label.setContentMode(ContentMode.HTML);
			}
		}
	}
	
	@SuppressWarnings("serial")
	private class IconColumnGenerator implements Table.ColumnGenerator {
		// xử lý return comment UI theo tùy thuộc tính của cột
		
		public Component generateCell(Table source, final Object itemId, Object columnId) {
			String keyCompoentMap = (itemId == null ? null : itemId.hashCode()) + "." + (columnId == null ? null : columnId.hashCode()) ;
			Component com = generateCell0(source, itemId, columnId);
			if(com != null) {
				mapComponentGen.put(keyCompoentMap, com);
			}
			return com;
		}
		
		public Component generateCell0(Table source, final Object itemId, Object columnId) {
			Container container = source.getContainerDataSource();
			final TableHeader columnHeader = listColumnHeader.get(columnId);
			// System.out.println("itemId = " + columnId + ", " + String.valueOf(columnHeader) + ", icon = " + columnHeader.getIcon()); 
			
			if (container instanceof BeanItemContainer) {
				final BeanItemContainer<?> con = (BeanItemContainer<?>) source.getContainerDataSource();
				BeanItem<?> rowItem = con.getItem(itemId);
				Property<?> cellProperty = null;
				Object columnValue = null;
				
				if(columnHeader.getViewOtherCol() != null) { // giá trị cột A, nhưng show ra GUI lấy cột B
					columnId = columnHeader.getViewOtherCol();
				}
				try {
					cellProperty = rowItem.getItemProperty(columnId);
					columnValue = cellProperty.getValue();
				} catch (Exception e) {
				}
				try {
					if(xoption != null && xoption.rowTotal_object == rowItem.getBean()) {
						String s = getVaueStr(itemId, columnId, columnValue, columnHeader);
						Label label = new Label(s); 
						setProperty(label, columnHeader);
						return label;
					}
					if(columnHeader.getMapValue() != null) { // map data qua interface
						Object newValue = columnHeader.getMapValue().mapValueCol(columnId, columnValue, rowItem, columnHeader, null); // 
						
						if(newValue instanceof Component) {
							return (Component) newValue;
						} else {
							String s = getVaueStr(itemId, columnId, newValue, columnHeader);
							Label label = new Label(s); 
							setProperty(label, columnHeader); 
							return label;
						}
					}
					if(columnValue != null && columnValue instanceof Component) {
						// component giao diện tùy biến, được set từ bên ngoài => hiển thị y nguyên
						return (Component) columnValue;
					}
					if(columnHeader.isButton()) {
						// logger.info("=====> itemId = " + columnId + ", " + String.valueOf(columnHeader) + ", icon = " + columnHeader.getIcon()); 
						Button button = new Button();
						button.setCaption(String.valueOf(columnHeader.getValue()));  
						button.addClickListener(new Button.ClickListener() {
							@Override
							public void buttonClick(ClickEvent event) {
								BeanItem<?> bean = con.getItem(itemId);
								columnHeader.getClickListener().click(bean); 
							}
						});
						return button;
					} 
					if(columnHeader.getIcon() != null) {
						if(String.valueOf(columnHeader.getIcon()).equals(String.valueOf(columnValue))){ 
							Image image = new Image("", new ThemeResource("icons/16/v.png")); 
							setProperty(image, columnHeader);
							return image; 
						}  
						else if(columnHeader.isShowIconX()) {
							Image image = new Image("", new ThemeResource("icons/16/x.png")); 
							setProperty(image, columnHeader);
							return image; 
						}
					}
					if(columnHeader.getMapValueIcon() != null) { 
						NameValuePair pair = columnHeader.getMapValueIcon().get(columnValue);
						if(pair == null) {
							 pair = columnHeader.getMapValueIcon().get(String.valueOf(columnValue)); 
						}
						// logger.info("### 1 =====> columnValue = " + columnValue + ", " + String.valueOf(pair)); 
						
						if(pair == null) {
							pair = columnHeader.getMapValueIcon().get("$other$");
							if(pair == null) {
								// hiển thị đúng giá trị nguyên bản
								Label label = new Label(String.valueOf(columnValue)); 
								setProperty(label, columnHeader);
								return label;
							}
						}
						String iconPath = pair.getName();
						String title = pair.getValue();
						
						if("".equalsIgnoreCase(iconPath)) { 
							Label label = new Label("");
							setProperty(label, columnHeader);
							return label;
						} 
						//Image resource = new Image(title, new ThemeResource(iconPath)); 
						//resource.setSizeUndefined();
						// return resource;
						// <img class="v-icon" src="http://45.122.253.185/xcms/VAADIN/themes/liger/icons/dashboard/iStar.png" alt="">
						Label label = new Label("<img src='VAADIN/themes/liger/" + iconPath + "'> " + title + "</img>");
						label.setSizeUndefined();
						label.setContentMode(ContentMode.HTML);
						setProperty(label, columnHeader); // ????? 2021-02
						return label;
					}
					if(columnHeader.getImageW() != null) {
						Image image = new Image(""); 
						image.setVisible(true);
						if(String.valueOf(columnValue).startsWith("http")) {
							image.setSource(new ExternalResource(String.valueOf(columnValue))); 
						} else {
							image.setSource(new FileResource(new File(String.valueOf(columnValue)))); 
						}
						setProperty(image, columnHeader);
				        return image;
					} 
					//if(columnHeader.getWidth() != null) {
					//	 Label label = new Label(String.valueOf(columnValue));
					//	 setProperty(label, columnHeader);
				    //    return label; 
					//} 
					if(columnHeader.getMapValueStr() != null) {
						Object newValue = columnHeader.getMapValueStr().get(columnValue);
						
						if(newValue == null) {
							// ko thấy giá trị map => thử tìm trong $other$
							newValue = columnHeader.getMapValueStr().get("$other$");
							
							if(newValue == null) {
								// hiển thị đúng giá trị nguyên bản
								Label label = new Label(String.valueOf(columnValue)); 
								setProperty(label, columnHeader);
								return label;
							}
						}
						if(newValue instanceof Component) {
							return (Component) newValue;
						} else {
							Label label = new Label(String.valueOf(newValue)); 
							setProperty(label, columnHeader);
							return label;
						}
					} 
					String s = getVaueStr(itemId, columnId, columnValue, columnHeader);
					Label label = new Label(s); 
					setProperty(label, columnHeader);
					return label;
					
				} catch (Exception e) {
					// return new Image("", new ThemeResource("icons/16/x.png"));
				}
			}
			Label label = new Label("");
			setProperty(label, columnHeader);
			return label;
		}
	} 
}
