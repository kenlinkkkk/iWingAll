package com.xtel.cms;

import java.net.UnknownHostException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import com.ligerdev.appbase.utils.BaseUtils;
import com.ligerdev.appbase.utils.encrypt.Encrypter;
import com.ligerdev.appbase.utils.entities.PairStringString;
import com.ligerdev.appbase.utils.http.HttpClientUtils2;

public class TestMain {
	/*
	static {
		Log4jLoader.init();
	}
	protected static Logger logger = Log4jLoader.getLogger();
	protected static BaseDAO baseDAO = BaseDAO.getInstance(BaseDAO.POOL_NAME_MAIN);
	*/
	
	public static void main(String[] args) throws ParseException {
//		Date d1 = new SimpleDateFormat("yyyyMMdd HH:mm:ss").parse("20191226 00:00:00");
//		Date d2 = new SimpleDateFormat("yyyyMMdd HH:mm:ss").parse("20200101 23:59:59");
//		ArrayList<String> list = getListMonthBetween(d1, d2, "yyyyMM");
//		System.out.println(list); 
//		System.out.println("AVC?cc".contains("?"));  
		
		
//		String url = "http://app.mobion.vn/apiv2/auth/credentials_sync/register";
//		String body = "{\n" + 
//						"    \"tel\": \"0909090529\",\n" + 
//						"    \"password\":\"123456\"\n" + 
//						"}";
		
//		
//		String url = "http://app.mobion.vn/apiv2/auth/credentials_sync/remove";
//		String body = "{\n" + 
//						"    \"tel\": \"0909090521\"" + 
//						"}";
//		

		String url = "http://app.mobion.vn/apiv2/auth/credentials_sync/update_password";
		String body = "{\n" + 
				"    \"tel\": \"0909090521\",\n" + 
				"    \"newPassword\":\"123456\"\n" + 
				"}";
		
		
		ArrayList<PairStringString> headers = new ArrayList<PairStringString>();
		headers.add(new PairStringString("Content-Type", "application/json"));
		String rs = HttpClientUtils2.postDefault("", 9000, url, body, headers); 
		System.out.println(rs); 
	}
	
	public static ArrayList<String> getListMonthBetween(Date d1, Date d2, String outputformat){
		ArrayList<String> rs = new ArrayList<String>();
		if(d1 == null || d2 == null) {
			return rs;
		}
		Date from = d1;
		Date to = d2;
		
		if(d1.compareTo(d2) > 0) {
			from = d2;
			to = d1;
		}
		from = BaseUtils.truncMonth(from);
		to = BaseUtils.truncMonth(to);
		// =>	 from <= to
		SimpleDateFormat df = new SimpleDateFormat(outputformat);
		
		while(from.compareTo(to) <= 0) {
			rs.add(df.format(from));
			from = BaseUtils.addTime(from, Calendar.MONTH, 1);
		}
		return rs; 
	} 
	
	public static void main2(String[] args) throws UnknownHostException { 
		//InetAddress giriAddress = java.net.InetAddress.getByName("bongdaso.live");
		//String address = giriAddress.getHostAddress();
		//System.out.println(address);
		/*
		ArrayList<CCSPTopupResult> listAction = new ArrayList<CCSPTopupResult>();
		listAction.add(new CCSPTopupResult()); 
		
		ArrayList<CCSPTopupResult> list = new ArrayList<CCSPTopupResult> (listAction.subList(0, 1));
		System.out.println(list.size());
		*/
		String pass2 = Encrypter.encodeMD5_F32("CMS", "thuyan@123"); 
		System.out.println(pass2); 
		
//		pass2 = Encrypter.encodeMD5_F32("CMS", "thuyan@123"); 
//		System.out.println(pass2); 
		
//		System.out.println("0|asc".startsWith("0|")); 
//		System.out.println(Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
		
//		String table = "his_" + new SimpleDateFormat("yyyyMM").format(new Date());
//		String sql = 
//				" select DATE_FORMAT(STR_TO_DATE(hisnote1, '%Y%m%d'), '%Y-%m-%d') date_report, \n" + 
//					" COALESCE(sum(case when channel in ('WAP') and result = 0 then fee else 0 end), 0) fee_wap,\n" + 
//					" COALESCE(sum(case when channel in ('SMS', 'VASP') and result = 0 then fee else 0 end), 0) fee_sms,\n" + 
//				//	" COALESCE(sum(case when channel in ('IVR') and result = 0 then fee else 0 end), 0) fee_ivr," + 
//				//	" COALESCE(sum(case when channel in ('CTKM') and result = 0 then fee else 0 end), 0) fee_ctkm," + 
//					" COALESCE(sum(case when channel in ('VASGATE') and result = 0 then fee else 0 end), 0) fee_avb,\n" + 
//					" COALESCE(sum(case when channel not in ('VASGATE', 'WAP', 'SMS') and result = 0 then fee else 0 end), 0) fee_other,\n" +  // = VASP
//					" COALESCE(sum(case when result = 0 and fee > 0 then fee else 0 end), 0) fee_total  \n" + 
//				" from " + table + " where result = 0 group by hisnote1 order by hisnote1 desc" ;
//		
//		System.out.println(sql); 
 
	}
}
