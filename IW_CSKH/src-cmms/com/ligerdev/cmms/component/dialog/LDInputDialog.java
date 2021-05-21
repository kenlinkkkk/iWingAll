package com.ligerdev.cmms.component.dialog;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;

import org.apache.log4j.Logger;

import com.ligerdev.appbase.utils.BaseUtils;
import com.ligerdev.appbase.utils.db.AntColumn;
import com.ligerdev.appbase.utils.textbase.Log4jLoader;
import com.ligerdev.cmms.component.table.DialogSubmitResult;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.data.util.BeanItem;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.Field;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.xtel.cms.MainApplication;

public abstract class LDInputDialog<T> extends Window {

	public static Logger logger = Log4jLoader.getLogger();
	public MainApplication mainApp = (MainApplication) MainApplication.getCurrent();
	
	public FormLayout formLayout = new FormLayout();
	public HorizontalLayout horizontalLayoutButtons = new HorizontalLayout();
	public HorizontalLayout horizontalLayoutValidateMsg = new HorizontalLayout();
    public ArrayList<Component> listComAdded = new ArrayList<Component>();
    public VerticalLayout verticalLayout = new VerticalLayout();
    
    public Button btnSubmit = new Button();
    public Button btnClose = new Button();
    public FieldGroup binder = null;
    public IInputDialogListener<T> submitListener;
    public DialogCaption dCaption = null;
    public final static String cmd_refreshed = "$refreshed";
    
	public LDInputDialog(final T item, String w, String h, DialogCaption dCaption, IInputDialogListener<T> submitListener) {
		execute(item, w, h, dCaption, submitListener);
	} 
	
	private void execute(final T item, String w, String h, DialogCaption dCaption, IInputDialogListener<T> submitListener) {
		setWidth(w);
		setHeight(h);
		center();
		setModal(true);
		this.dCaption = dCaption;
		this.submitListener = submitListener;
		
		if(item != null){
			BeanItem<T> beanitem = new BeanItem<T>(item);
			binder = new FieldGroup(beanitem);
		}
		formLayout.setSpacing(true);
		formLayout.setSizeUndefined();
		
		//verticalLayout.setSizeFull();
		verticalLayout.setSpacing(true);
		verticalLayout.setHeightUndefined();
		
		final Label labelMsg = new Label();
		labelMsg.setSizeUndefined();
		labelMsg.setStyleName("validateMsg");
		// verticalLayout.addComponent(labelMsg);
		// verticalLayout.setComponentAlignment(labelMsg, Alignment.TOP_CENTER);
		
		verticalLayout.addComponent(formLayout);
		verticalLayout.setComponentAlignment(formLayout, Alignment.TOP_CENTER);
		btnSubmit.setCaption("Submit");
		//btnSubmit.setStyleName("small");
		
		btnSubmit.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				try {
					logger.info(mainApp.getTransid() + ", btnSubmit is clicked");
					if(binder != null){
						logger.info(mainApp.getTransid() + ", binder commit");
						binder.commit();
					}
				} catch (Exception e) {
					logger.info(mainApp.getTransid() + ", Exception: " + e.getMessage(), e);
				} 
				DialogSubmitResult<T> rs = LDInputDialog.this.submit000(item);
				
				if(rs == null || BaseUtils.isBlank(rs.getErrorMsg())){
					logger.info(mainApp.getTransid() + ", close dialog1");
					LDInputDialog.this.close();
				} else { 
					if(cmd_refreshed.equalsIgnoreCase(rs.getErrorMsg())) {
						logger.info(mainApp.getTransid() + ", close dialog2");
						LDInputDialog.this.close(); 
					}
					else {
						horizontalLayoutValidateMsg.setVisible(true);
						logger.info(mainApp.getTransid() + ", error msg dialog: " + rs.getErrorMsg());
						labelMsg.setValue("* " + rs.getErrorMsg());
					}
				}
			}
		});
		
		btnClose.setCaption("Close");
		//btnClose.setStyleName("small");
		btnClose.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				if(binder != null){
					binder.discard();
				}
				LDInputDialog.this.close();
			}
		});
		horizontalLayoutValidateMsg.setSizeUndefined();
		horizontalLayoutValidateMsg.setVisible(false);
		horizontalLayoutValidateMsg.addComponent(labelMsg);
		formLayout.addComponent(horizontalLayoutValidateMsg);
		
		horizontalLayoutButtons.setSizeUndefined();
		horizontalLayoutButtons.setSpacing(true);
		horizontalLayoutButtons.addComponent(btnSubmit);
		horizontalLayoutButtons.addComponent(btnClose);
		formLayout.addComponent(horizontalLayoutButtons); 
		
		setContent(verticalLayout);
	}
	
	public void addComponent(Component com){
		formLayout.removeComponent(horizontalLayoutValidateMsg);
		formLayout.removeComponent(horizontalLayoutButtons);
		formLayout.addComponent(com);
		// listComAdded.add((Field<?>) com);
		listComAdded.add(com);
		formLayout.addComponent(horizontalLayoutValidateMsg);
		formLayout.addComponent(horizontalLayoutButtons);
	}
	
	private DialogSubmitResult<T> submit000(T item){
		if(listComAdded.size() == 0){
			logger.info(mainApp.getTransid() + ", call dialogSubmit to submitListener1");
			return submitListener.dialogSubmit000(item, this);
		}
		for(Component ff : listComAdded){
			try {
				if(ff instanceof Field<?>) {
					try {
						((Field<?>) ff).validate();
					} catch (Exception e) {
						String msg = ((Field<?>) ff).getRequiredError();
						if(BaseUtils.isEmpty(msg)) {
							msg = "Some fields is can not empty!";
						}
						throw new Exception(msg);
					}
					if(((Field<?>) ff).isValid() == false){
						logger.info(mainApp.getTransid() + ", Exception Ssome fields is invalid");
						throw new Exception("Some fields is invalid");
					}
				}
			} catch (Exception e) {
				// e.printStackTrace();
				logger.info(mainApp.getTransid() + ", Exception: " + e.getMessage());
				return new DialogSubmitResult<T>(e.getMessage()); 
			}
		}
		logger.info(mainApp.getTransid() + ", call dialogSubmit to submitListener2");
		return submitListener.dialogSubmit000(item, this);
	}
	
	public static void genFields(Class<?> clazz){
		System.out.println(); 
		ArrayList<String> listAddCom = new ArrayList<String>();
		
		for(Method method : clazz.getDeclaredMethods()){
			if(!method.getName().startsWith("get")){
				continue;
			}
			String fieldName = method.getName().replaceFirst("get", "");
			fieldName = fieldName.substring(0, 1).toLowerCase() + fieldName.substring(1);
			String addLine = "\taddComponent(" + fieldName + ");";
			AntColumn antColumn = method.getAnnotation(AntColumn.class);
			
			if(antColumn != null && BaseUtils.isNotBlank(antColumn.tbl_icon())){ 
				System.out.println("\tprotected CheckBox " + fieldName 
						+ " = AppCmmsUtils.createCheckBox(\"" + fieldName + "\", false);");
				listAddCom.add(addLine);
				continue;
			}
			if(	method.getReturnType() == boolean.class
					|| method.getReturnType() == Boolean.class
			){
				System.out.println("\tprotected TextField " + fieldName 
						+ " = AppCmmsUtils.createTextField(\"" + fieldName + "\", \"\", true);");
				listAddCom.add(addLine);
				continue;
			}
			if(method.getReturnType() == Date.class){
				System.out.println("\tprotected DateField " + fieldName 
						+ " = AppCmmsUtils.createDateField(\"" + fieldName + "\", true);");
				listAddCom.add(addLine);
				continue;
			}
			System.out.println("\tprotected TextField " + fieldName 
					+ " = AppCmmsUtils.createTextField(\"" + fieldName + "\", \"\", true);");
			listAddCom.add(addLine);
		}
		System.out.println("\n");
		
		for(String s : listAddCom){
			System.out.println(s);
		}
	} 
	
	public DialogCaption getDCaption() {
		return dCaption;
	}

	public void setDCaption(DialogCaption dCaption) {
		this.dCaption = dCaption;
	}

	public void showDialog(){
		mainApp.addWindow(this);
	}

	public IInputDialogListener<T> getSubmitListener() {
		return submitListener;
	}

	public void setSubmitListener(IInputDialogListener<T> submitListener) {
		this.submitListener = submitListener;
	}

	public VerticalLayout getVerticalLayout() {
		return verticalLayout;
	}

	public void setVerticalLayout(VerticalLayout verticalLayout) {
		this.verticalLayout = verticalLayout;
	}
}
