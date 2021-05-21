package com.ligerdev.cmms.component.dialog;

import com.vaadin.event.FieldEvents.BlurEvent;
import com.vaadin.event.FieldEvents.BlurListener;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.xtel.cms.MainApplication;

public abstract class LDOptionDialog extends Window {
	
	public MainApplication mainApp = (MainApplication) MainApplication.getCurrent();

	public LDOptionDialog(String caption, String w, String h) {
		setModal(true);
		center();
		setCaption(caption);
		
		if(w != null){
			setWidth(w);
		} else {
			setWidthUndefined();
		}
		if(h != null){
			setHeight(h);
		} else {
			setHeightUndefined();
		}
		//addBlurListener(new BlurListener() {
        //    @Override
        //    public void blur(BlurEvent event) {
        //    	LDOptionDialog.this.close();
        //    }
        //});
	}
	
	@SuppressWarnings("serial")
	public void setMessage(String message, final String ... buttonsName){
		
		VerticalLayout verticalLayout = new VerticalLayout();
		verticalLayout.setSizeFull();
		// verticalLayout.setSpacing(true);
		
		Label label = new Label(message);
		label.setSizeUndefined();
		verticalLayout.addComponent(label);
		verticalLayout.setComponentAlignment(label, Alignment.MIDDLE_CENTER);
		
		HorizontalLayout horizontalLayout = new HorizontalLayout();
		horizontalLayout.setSizeUndefined();
		horizontalLayout.setSpacing(true);
		
		for(int i = 0 ; i < buttonsName.length; i ++){
			final int k = i;
			final Button b = new Button(buttonsName[i]);
			horizontalLayout.addComponent(b);
			b.setStyleName("small");
			b.setWidth("2cm");
			
			b.addClickListener(new ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					dialogButtonClicked(buttonsName[k], k);
				}
			});
		}
		verticalLayout.addComponent(horizontalLayout);
		verticalLayout.setComponentAlignment(horizontalLayout, Alignment.TOP_CENTER);
		
		/*
		Label labelSpace = new Label();
		labelSpace.setHeight("0.1mm");
		verticalLayout.addComponent(labelSpace);
		*/
		setContent(verticalLayout);
	}
	
	public abstract void dialogButtonClicked(String buttonName, int buttonIndex);
	
	public void showDialog(){
		mainApp.addWindow(this);
	}
}
