package com.elcom.vinamailreport;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.httpclient.NameValuePair;
import org.apache.log4j.Logger;
import org.eclipse.jetty.server.Request;

import com.ligerdev.appbase.utils.BaseUtils;
import com.ligerdev.appbase.utils.textbase.Log4jLoader;
import com.ligerdev.appbase.utils.textbase.ReqCountUtils;

public class ServletContent extends HttpServlet {

	// com.ligerdev.appbase.utils.http._DemoHttpServer
	private static ReqCountUtils reqCount = ReqCountUtils.getInstance("LoggerListener", "DL");
	// private static Logger logger = Log4jLoader.getLogger();
	private static final long serialVersionUID = 1L;
	
	public ServletContent() {
	}
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doPost(req, resp);
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.setCharacterEncoding("UTF-8");
		resp.setContentType("text/plain");
		Main.logger.infor("######### getContentEmail: " + req.getQueryString() + " | ip = " + req.getRemoteAddr() + "/" + req.getHeader("X-Forwarded-For")); 
		
		String s = Main.getInstance().m.getContend();
		resp.getWriter().write(s);
	}
	 
}
