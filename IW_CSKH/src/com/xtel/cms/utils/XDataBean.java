package com.xtel.cms.utils;

import java.util.Date;

import com.ligerdev.appbase.utils.db.BaseDAO;
import com.ligerdev.appbase.utils.db.XBaseDAO;

public class XDataBean {

	public BaseDAO baseDAO = null;
	public XBaseDAO xbaseDAO = null;
	public String serviceName = null;
	public String urlUnregTemplate = null;
	public boolean isCCSPService = false;
	public Date timeLaunching;
	
	public XDataBean(BaseDAO baseDAO, XBaseDAO xbaseDAO, String serviceName, String urlUnregTemplate, boolean isCCSPService) {
		this.baseDAO = baseDAO;
		this.xbaseDAO = xbaseDAO;
		this.serviceName = serviceName;
		this.urlUnregTemplate = urlUnregTemplate;
		this.isCCSPService = isCCSPService;
	}
 
	public XDataBean setCCSPService(boolean isCCSPService) {
		this.isCCSPService = isCCSPService;
		return this;
	}
	
	
}
