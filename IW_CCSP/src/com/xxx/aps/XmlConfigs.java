package com.xxx.aps;

import com.ligerdev.appbase.utils.cache.CacheSyncFile;
import com.ligerdev.appbase.utils.textbase.ConfigsReader;

public class XmlConfigs extends ConfigsReader {

	public static String servicename;
	//public static String servicecode;
	public static String shortcode;
	public static String username;
	public static String password;
	
	public static String url_send_mt;
	public static String url_mt_remind;
	public static String url_action_wcc;
	public static String url_charge_content;
	
	public static String x_ibm_client_id;
	public static String cskh_number;
	public static boolean short_sms_table;
	
	// fix giá trị charge response
	public static final int CHARGE_OK = 0;
	public static final int CHARGE_LOWBALANCE = 1;
	public static final int CHARGE_ERROR = 2;
	public static final CacheSyncFile cache = CacheSyncFile.getInstance("COMMONCACHE", 500000);
	
	@Override
	public void readPropeties() throws Exception { 
		//servicecode = getString("servicecode");
		username = getString("username");
		password = getString("password");
		url_send_mt = getString("url_send_mt");
		x_ibm_client_id = getString("x_ibm_client_id");
		url_mt_remind = getString("url_mt_remind");
		shortcode = getString("shortcode");
		servicename = getString("servicename");
		cskh_number = getString("cskh_number");
		url_action_wcc = getString("url_action_wcc");
		url_charge_content = getString("url_charge_content");
		
		if(isExistElement("short_sms_table")) {
			short_sms_table = getBoolean("short_sms_table");
		}
	}
	
	public static void main(String[] args) {
		ConfigsReader.init(XmlConfigs.class);
		System.out.println("........."); 
	}
	 
}
