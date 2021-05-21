package com.xtel.cms.utils;

import java.util.ArrayList;
import java.util.Date;

import org.w3c.dom.Element;

import com.ibm.icu.text.SimpleDateFormat;
import com.ligerdev.appbase.utils.BaseUtils;
import com.ligerdev.appbase.utils.entities.PairStringString;
import com.ligerdev.appbase.utils.textbase.ConfigsReader;
import com.xtel.cms.base.db.orm.UserCms;

public class XmlConfigs extends ConfigsReader {

	public static String link_clearcache;
	public static String vasbase_type = ""; // ccsp, normal , <vasbase_type>ccsp</vasbase_type>
	public static ArrayList<String> sms_shorttable;
	public static String service_name = "";
	public static String link_alarm_sms; 
	public static ArrayList<UserCms> list_acc_config;
	public static ArrayList<String> list_alarm_msisdn;
	public static Date time_launching;
	
	public static String mobion_ctkm201_cpid;
	public static String mobion_ctkm201_from;
	public static String mobion_ctkm201_to;
	public static ArrayList<CtkmDuration> mobion_ctkm201_durations;
	
	public static String funtv_ctkm201_cpid;
	public static String funtv_ctkm201_from;
	public static String funtv_ctkm201_to;
	public static ArrayList<CtkmDuration> funtv_ctkm201_durations;

	// phân hệ cskh vas ccsp
	public static class CCSPCfg {
		public static String link_unreg;
		public static String link_unreg_mobion;
		public static String link_unreg_vhpd;
		public static String link_unreg_anxh;
		public static String link_unreg_poki; 
		//public static boolean login_url_without_pass = false;
		public static ArrayList<PairStringString> report_by_cpid;
	}
	
	@Override
	public void readPropeties() throws Exception { 
		if(isExistElement("link_clearcache")) {
			link_clearcache = getString("link_clearcache");
		}
		if(isExistElement("mobion_ctkm201")) {
			mobion_ctkm201_cpid = getStringAttr("mobion_ctkm201|cpid");
			mobion_ctkm201_from = getStringAttr("mobion_ctkm201|from");
			mobion_ctkm201_to = getStringAttr("mobion_ctkm201|to");
			mobion_ctkm201_durations = new ArrayList<CtkmDuration>();
			ArrayList<Element> listE = getListElement("mobion_ctkm201|item");
			if(listE != null) {
				for(Element e: listE) {
					String type = getStringAttr(e, "type");
					String from = getStringAttr(e, "from");
					String to = getStringAttr(e, "to");
					String name = getStringAttr(e, "name");
					mobion_ctkm201_durations.add(new CtkmDuration(type, -1, name, from, to));
				}
			}
		}
		if(isExistElement("funtv_ctkm201")) {
			funtv_ctkm201_cpid = getStringAttr("funtv_ctkm201|cpid");
			funtv_ctkm201_from = getStringAttr("funtv_ctkm201|from");
			funtv_ctkm201_to = getStringAttr("funtv_ctkm201|to");
			funtv_ctkm201_durations = new ArrayList<CtkmDuration>();
			ArrayList<Element> listE = getListElement("funtv_ctkm201|item");
			if(listE != null) {
				for(Element e: listE) {
					String type = getStringAttr(e, "type");
					String from = getStringAttr(e, "from");
					String to = getStringAttr(e, "to");
					String name = getStringAttr(e, "name");
					funtv_ctkm201_durations.add(new CtkmDuration(type, -1, name, from, to));
				}
			}
		}
		if(isExistElement("time_launching")) {
			// <time_launching>202012</time_launching>
			time_launching = new SimpleDateFormat("yyyyMM").parse(getString("time_launching")); 
		}
		if(isExistElement("list_alarm_msisdn")) {
			list_alarm_msisdn = getList("list_alarm_msisdn");
		}
		if(isExistElement("service_name")) {
			// <service_name>iwing</service_name>
			service_name = getString("service_name");
		}
		if(isExistElement("sms_shorttable")) {
			sms_shorttable = getList("sms_shorttable");
		}
		if(isExistElement("vasbase_type")) {
			vasbase_type = getString("vasbase_type");  // <vasbase_type>true</vasbase_type>
		}
		if(isExistElement("link_alarm_sms")) {
			link_alarm_sms = getString("link_alarm_sms"); 
		}
		if(isExistElement("acc_config")) {
			ArrayList<Element> listE = getListElement("acc_config");
			list_acc_config = new ArrayList<UserCms>();
			
			for(Element e: listE) {
				UserCms u = new UserCms();
				u.setUsername(e.getAttribute("user"));
				u.setFullname(e.getAttribute("fullname"));
				u.setRolename(e.getAttribute("role"));
				u.setPassword(e.getAttribute("pass")); 
				u.setStatus(1);
				u.setUsertype(1); 
				u.setMaxUnreg(BaseUtils.parseInt(e.getAttribute("maxunreg"), null)); 
				list_acc_config.add(u);
			}
		}
		if(isExistElement("ccsp_cms")) {
			CCSPCfg.link_unreg = getString("ccsp_cms|link_unreg");
			
			if(isExistElement("ccsp_cms|link_unreg_mobion")) {
				CCSPCfg.link_unreg_mobion = getString("ccsp_cms|link_unreg_mobion");
			}
			if(isExistElement("ccsp_cms|link_unreg_vhpd")) {
				CCSPCfg.link_unreg_vhpd = getString("ccsp_cms|link_unreg_vhpd");
			}
			if(isExistElement("ccsp_cms|link_unreg_anxh")) {
				CCSPCfg.link_unreg_anxh = getString("ccsp_cms|link_unreg_anxh");
			}
			if(isExistElement("ccsp_cms|link_unreg_poki")) {
				CCSPCfg.link_unreg_poki = getString("ccsp_cms|link_unreg_poki");
			} 
			//if(isExistElement("ccsp_cms|login_url_without_pass")) {
			//	CCSPCfg.login_url_without_pass = getBoolean("ccsp_cms|login_url_without_pass");
			//}
			if(isExistElement("ccsp_cms|report_by_cpid")) { // <report_by_cpid name="cats"> and cpid = 'cats1'</report_by_cpid>
				ArrayList<Element> listE = getListElement("ccsp_cms|report_by_cpid");
				CCSPCfg.report_by_cpid = new ArrayList<PairStringString>();
				for(Element e: listE) {
					CCSPCfg.report_by_cpid.add(new PairStringString(e.getAttribute("name"), e.getTextContent()));
				}
			}
		}  
	}
	
	public static void main(String[] args) {
		XmlConfigs.init(XmlConfigs.class);
	}
}
