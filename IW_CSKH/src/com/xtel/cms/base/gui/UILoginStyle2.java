package com.xtel.cms.base.gui;

import org.apache.log4j.Logger;

import com.ligerdev.appbase.utils.textbase.Log4jLoader;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.Reindeer;
import com.xtel.cms.MainApplication;

public class UILoginStyle2 extends VerticalLayout {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L; 
	protected static Logger logger = Log4jLoader.getLogger();
	private MainApplication mainApp = (MainApplication) MainApplication.getCurrent();
	// private static BaseDAO baseDAO = BaseDAO.getInstance("main");

	@SuppressWarnings("serial")
	public UILoginStyle2() {

		final TextField username = new TextField();
		final PasswordField password = new PasswordField();
		final Button btnLogin = new Button();
		final Button btnClear = new Button();
		
		setSizeFull();
		addComponent(username);
		addComponent(password);
		
		username.focus();
		username.setCaption("Username");
		username.addStyleName(Reindeer.TEXTFIELD_SMALL);
		username.setMaxLength(35);
		setComponentAlignment(username, Alignment.BOTTOM_CENTER);

		password.addStyleName(Reindeer.TEXTFIELD_SMALL);
		password.setMaxLength(35);
		password.setCaption("Password");
		setComponentAlignment(password, Alignment.BOTTOM_CENTER);

		HorizontalLayout horizontalLayout = new HorizontalLayout();
		horizontalLayout.addComponent(btnLogin);
		horizontalLayout.setSpacing(true);
		horizontalLayout.addComponent(btnClear);
		
		btnLogin.addStyleName(Reindeer.BUTTON_SMALL);
		btnLogin.setCaption("Submit");
		btnLogin.setWidth("1in");
		
		btnClear.addStyleName(Reindeer.BUTTON_SMALL);
		btnClear.setCaption("Clear");
		btnClear.setWidth("1in");
		
		addComponent(horizontalLayout);
		setComponentAlignment(horizontalLayout, Alignment.MIDDLE_CENTER);
		
		btnLogin.addClickListener(new ClickListener() {
			public void buttonClick(Button.ClickEvent event) {
				
				String user = username.getValue().toString().trim();
				String pass = (String) password.getValue(); 
				boolean login = mainApp.login(user, pass);
				
				if(login){
					mainApp.removeWindow(mainApp.getWindows().iterator().next());
				}
			}
		});
		
		btnClear.addClickListener(new ClickListener() {
			public void buttonClick(Button.ClickEvent event) {
				logger.info(mainApp.getTransid() + ", clear form login");
				username.clear();
				password.clear();
				username.focus();
			}
		});
	}
}
