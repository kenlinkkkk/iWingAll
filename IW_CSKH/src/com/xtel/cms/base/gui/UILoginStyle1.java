package com.xtel.cms.base.gui;

import org.apache.log4j.Logger;

import com.ligerdev.appbase.utils.textbase.Log4jLoader;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.xtel.cms.MainApplication;

public class UILoginStyle1 extends VerticalLayout {

	private MainApplication mainApp = (MainApplication) MainApplication.getCurrent();
	protected static Logger logger = Log4jLoader.getLogger();

	@SuppressWarnings("serial")
	public UILoginStyle1() {

		setSizeFull();
		setStyleName("loginVLayoutStyle");
		
		CustomLayout custom = new CustomLayout("login");
		custom.setSizeFull();
		addComponent(custom);

		final TextField username = new TextField();
		username.focus();
		username.setMaxLength(50);
		username.setWidth("128");
		custom.addComponent(username, "username");

		final PasswordField password = new PasswordField();
		password.setMaxLength(50);
		password.setWidth("128");
		custom.addComponent(password, "password");
		
		Button btnLogin = new Button();
		btnLogin.setIcon(new ThemeResource("images/en_login_11.jpg"));
		btnLogin.setStyleName("link");
		btnLogin.setWidth("150px");
		btnLogin.setHeight("32px");
		custom.addComponent(btnLogin, "okbutton");

		btnLogin.addClickListener(new ClickListener() {
			public void buttonClick(Button.ClickEvent event) {
				
				String user = username.getValue().toString().trim();
				String pass = password.getValue();
				mainApp.login(user, pass);
			}
		});
	}
}
