package com.xtel.cms;

import com.vaadin.server.VaadinSession;
import java.util.Enumeration;
import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

// com.xtel.cms.SessionExpireListener
@WebListener
public class SessionExpireListener implements HttpSessionListener {

	@Override
	public void sessionCreated(HttpSessionEvent hse) {
		System.out.println("Session created");
	}

	@Override
	public void sessionDestroyed(HttpSessionEvent hse) {
		System.out.println("Session destroyed");
//		Enumeration e = hse.getSession().getAttributeNames();
//		while (e.hasMoreElements()) {
//			Object o = hse.getSession().getAttribute((String) e.nextElement());
//			if (o instanceof VaadinSession) {
//				VaadinSession vs = (VaadinSession) o;
//			}
//		}
	}
}

/*

 	<listener>
        <listener-class>com.xtel.cms.SessionExpireListener</listener-class>
    </listener>
    
*/ 
