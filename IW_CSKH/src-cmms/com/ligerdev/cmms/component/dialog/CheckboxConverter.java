package com.ligerdev.cmms.component.dialog;

import java.util.Locale;

import com.vaadin.data.util.converter.Converter;

@SuppressWarnings("serial")
public class CheckboxConverter<M> implements Converter<Boolean, M> {

	public M trueValue;
	public M falseValue;
	
	public CheckboxConverter(M trueValue, M falseValue) {
		this.trueValue = trueValue;
		this.falseValue = falseValue;
	}

	@Override
	public M convertToModel(Boolean value, Class<? extends M> targetType, Locale locale) 
			throws com.vaadin.data.util.converter.Converter.ConversionException {
		if(value != null && value){
			return trueValue;
		}
		return falseValue;
	}

	@Override
	public Boolean convertToPresentation(M value, Class<? extends Boolean> targetType, Locale locale)
			throws com.vaadin.data.util.converter.Converter.ConversionException {
		if(value == null){
			return false;
		}
		if(value.equals(trueValue)){
			return true;
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Class<M> getModelType() {
		return (Class<M>) trueValue.getClass();
	}

	@Override
	public Class<Boolean> getPresentationType() {
		return Boolean.class;
	}
}
