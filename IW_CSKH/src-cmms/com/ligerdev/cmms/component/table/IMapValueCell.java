package com.ligerdev.cmms.component.table;

import com.ligerdev.cmms.component.entity.XDataIMapValueCell;
import com.vaadin.data.util.BeanItem;

public interface IMapValueCell {
	public Object mapValueCol(Object columnId, Object columnValue, BeanItem<?> rowItem, TableHeader columnHeader, XDataIMapValueCell xdata); 
}
