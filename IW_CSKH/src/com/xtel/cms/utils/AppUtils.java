package com.xtel.cms.utils;

import java.lang.reflect.Method;
import java.net.URLEncoder;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.httpclient.NameValuePair;
import org.apache.log4j.Logger;

import com.ibm.icu.text.SimpleDateFormat;
import com.ibm.icu.util.Calendar;
import com.ligerdev.appbase.utils.BaseUtils;
import com.ligerdev.appbase.utils.cache.CacheSyncFile;
import com.ligerdev.appbase.utils.entities.PairStringString;
import com.ligerdev.appbase.utils.http.HttpClientUtils;
import com.ligerdev.appbase.utils.textbase.Log4jLoader;
import com.vaadin.server.VaadinRequest;

public class AppUtils {
	
	protected static Logger logger = Log4jLoader.getLogger();
	protected static CacheSyncFile cache = CacheSyncFile.getInstance(1000000);

	public static String normalizeString(String str) {
		return str == null ? null : Normalizer.normalize(str, Normalizer.Form.NFC);
	}
	
	public static ArrayList<NameValuePair> getHeaders(String transid, VaadinRequest req, boolean log){
		ArrayList<NameValuePair> headers = new ArrayList<NameValuePair>();
		Enumeration<String> enums =  req.getHeaderNames();
		
		while(enums.hasMoreElements()){
			String key = enums.nextElement();
			Enumeration<String> valuesEnums = req.getHeaders(key);
			
			while(valuesEnums.hasMoreElements()){
				String value = valuesEnums.nextElement();
				if(log) {
					logger.info(transid + ", Header: " + key + " = " + value); 
				}
				headers.add(new NameValuePair(key, value)); 
			}
		}
		return headers;
	}
	
	public static ArrayList<NameValuePair> getParameters(String transid, VaadinRequest req, boolean log){
		Map<String, String[]> map = req.getParameterMap();
		Set<Entry<String, String[]>> set = map.entrySet();
		Iterator<Entry<String, String[]>> iter = set.iterator();
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		
		while(iter.hasNext()){
			Entry<String, String[]> e = (Entry<String, String[]>) iter.next();
			String key = e.getKey();
			String value[] = e.getValue();
			
			for(int i = 0 ; value != null && i < value.length; i ++){
				if(log) {
					logger.info(transid + ", Parameter: " + key + " = " + value[i]); 
				}
				params.add(new NameValuePair(key, value[i])); 
			}
		}
		return params;
	}
	
	public static String formatNameProduct(String title) {
		try {
			title = title.trim().replace("\r\n", " ").replace("\n", " ").replace("\t", " ")
					.replace("'", " ").replace("\"", " ").replaceAll(" +", " ");
		} catch (Exception e) {
		}
		try {
			title = normalizeString(title); 
		} catch (Exception e) {
		}
		try {
			title = removeSundryChars(title, "");
		} catch (Exception e) {
		}
		return title;
	}

	public static String removeSundryChars(String s, String addStr){ // so với StringToolUtils thì thêm tham số addStr
		if(s == null){
			return s;
		}
		String uniChars = "àáảãạâầấẩẫậăằắẳẵặèéẻẽẹêềếểễệđ" 
								+ "ìíỉĩịòóỏõọôồốổỗộơờớởỡợùúủũụưừứửữựỳýỷỹỵÀÁẢÃẠÂẦẤẨẪẬĂẰẮẲ"
								+ "ẴẶÈÉẺẼẸÊỀẾỂỄỆĐÌÍỈĨỊÒÓỎÕỌÔỒỐỔỖỘƠỜỚỞỠỢÙÚỦŨỤƯỪỨỬỮỰỲÝỶỸỴÂĂĐÔƠƯ"
								+ " 0123456789().\\,/;:'[]{}!@#$%*qwertyuioplkjhgfdsazxcvbnmQWERTYUIOPLKJHGFDSAZXCVBNM_+=-\n\t<>?" + addStr;
		char ch[] = s.toCharArray();
		String result = "";
		for(char c : ch){
			if(uniChars.contains(String.valueOf(c))){
				result += c;
			}
		}
		return result;
	}
	
	public static ArrayList<PairStringString> convertListPair1(ArrayList<NameValuePair> listIn){
		if(listIn == null) {
			return null;
		}
		ArrayList<PairStringString> listOut = new ArrayList<PairStringString>();
		for(NameValuePair i : listIn) {
			listOut.add(new PairStringString(i.getName(), i.getValue()));
		}
		return listOut;
	}
	
	public static ArrayList<NameValuePair> convertListPair2(ArrayList<PairStringString> listIn){
		if(listIn == null) {
			return null;
		}
		ArrayList<NameValuePair> listOut = new ArrayList<NameValuePair>();
		for(PairStringString i : listIn) {
			listOut.add(new NameValuePair(i.getName(), i.getValue()));
		}
		return listOut;
	} 
	
	public static boolean isAmobService() {
		return "amob".equalsIgnoreCase(XmlConfigs.service_name);
	}
	
	public static boolean isCatTienSaPlayService() {
		return "CatTienSaPlay".equalsIgnoreCase(XmlConfigs.service_name);
	}
	
	public static boolean isShopeeCare() {
		return "ShopeeCare".equalsIgnoreCase(XmlConfigs.service_name);
	}
	
	public static Date truncMonth(Date d) { // 01/MM/yyyy
		try {
			String thisMonth = new SimpleDateFormat("yyyyMM").format(d);
			return new SimpleDateFormat("yyyyMMdd").parse(thisMonth + "01");
			 
		} catch (Exception e) {
			return null;
		}
	}
	
	public static int getMaxDayOfMonth1(Date d) { // 01/MM/yyyy 00:00:00
		Calendar c = Calendar.getInstance();
		c.setTime(d);
		return c.getActualMaximum(Calendar.DAY_OF_MONTH);
	}
	
	public static Date getMaxDayOfMonth2(Date d) { // 31/MM/yyyy 00:00:00
		try {
			String thisMonth = new SimpleDateFormat("yyyyMM").format(d);
			return new SimpleDateFormat("yyyyMMdd").parse(thisMonth + getMaxDayOfMonth1(d)); 

		} catch (Exception e) {
			return null;
		}
	}
	
	public static Date getMaxDayOfMonth3(Date d) { // 31/MM/yyyy 23:59:59
		try {
			String thisMonth = new SimpleDateFormat("yyyyMM").format(d);
			return new SimpleDateFormat("yyyyMMdd HHmmss").parse(thisMonth + getMaxDayOfMonth1(d) + " 235959"); 

		} catch (Exception e) {
			return null;
		}
	}
	
	public static void realoadCore(){
		new Thread(){
			public void run() {
				HttpClientUtils.getDefault("", XmlConfigs.link_clearcache, 3000);
			};
		}.start();
	}
	
	public static String getMondayOfWeek(Date dayOfWeek, String format){ // return yyyyMMdd
		Calendar cal = Calendar.getInstance();
		cal.setTime(dayOfWeek);
		
		for(int i = 0; i < 10; i ++){
			if(cal.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY){
				String yyyyMMdd = new SimpleDateFormat(format).format(cal.getTime());
				return yyyyMMdd;
			}
			cal.add(Calendar.DATE, -1); // lùi 1 ngày
		}
		return "0"; // return để compile ok, thực tế ko bao giờ return 0
	}
	
	public static String getSundayOfWeek(Date dayOfWeek, String format){ // return yyyyMMdd
		Calendar cal = Calendar.getInstance();
		cal.setTime(dayOfWeek);
		
		for(int i = 0; i < 10; i ++){
			if(cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY){
				String yyyyMMdd = new SimpleDateFormat(format).format(cal.getTime());
				return yyyyMMdd;
			}
			cal.add(Calendar.DATE, 1); // tiến 1 ngày
		}
		return "0"; // return để compile ok, thực tế ko bao giờ return 0
	}
	
	public static ArrayList<String> getListMonth(Date a, Date b){ 
		Date from, to;
		
		if(a.compareTo(b) < 0) {
			from = a;
			to = b;
		} else { 
			from = b;
			to = a;
		}
		ArrayList<String> listMonth = new ArrayList<String>();
		from = BaseUtils.truncDate(from);
		to = BaseUtils.truncDate(to);
		
		while(from.compareTo(to) <= 0) {
			String month = new SimpleDateFormat("yyyyMM").format(from);
			
			if(listMonth.contains(month) == false) {
				listMonth.add(month);
			}
			from = BaseUtils.addTime(from, Calendar.DATE, 1);
		}
		return listMonth;
	}
	
	public static void main2 (String[] args) {
		Date from = BaseUtils.parseTime("yyyyMMdd", "20170810");
		Date to = BaseUtils.parseTime("yyyyMMdd", "20180930");
		
		//ArrayList<String> listMonth = getListMonth( b ,a);
		//System.out.println(listMonth.toString()); 
		
		if(from == null) {
			from = BaseUtils.parseTime("yyyyMMdd", "20180901");
		}
		if(to == null) {
			to = new Date();
		}
		to = BaseUtils.addTime(to, Calendar.DATE, 1);
		String msisdn = "84909090529";
		
		ArrayList<String> listMonth = AppUtils.getListMonth(from, to);
		String sql = "";
		
		for(int i = 0; i < listMonth.size(); i ++) {
			String temp = "select * from mo_" + listMonth.get(i) + " where result = 0 and msisdn = '" + msisdn + "'";
			
			if(i == 0) {
				temp += " and created_time > " + new SimpleDateFormat("yyyyMMdd").format(from);
			}
			if(i + 1 >= listMonth.size()) {
				// line cuối
				temp += " and created_time < " + new SimpleDateFormat("yyyyMMdd").format(to);
			}
			sql += " union all " + temp + "\n";
		}
		sql = sql.replaceFirst(" union all ", "");
		System.out.println(sql);
 	}

	public static void sendAlarm(String transid, String content) {
		if(BaseUtils.isBlank(XmlConfigs.link_alarm_sms)
				|| XmlConfigs.list_alarm_msisdn == null 
				|| XmlConfigs.list_alarm_msisdn.size() == 0) {
			return;
		}
		for(String msisdn : XmlConfigs.list_alarm_msisdn) {
			if(BaseUtils.isValidMsisdnVn(msisdn) == false) {
				continue;
			}
			try {
				String url = XmlConfigs.link_alarm_sms.replace("@msisdn", msisdn).replace("@content", URLEncoder.encode(content, "UTF-8"));
				
				if(cache.getObject(url) != null) {
					logger.info(transid + ", duplicate alarm: " + url + ", content: " + content + ", ==> ignore");
					return;
				}
				String rs = HttpClientUtils.getDefault(transid, url, 6000);
				logger.info(transid + ", sendAlarm: " + url + ", content: " + content + ", result: " + rs);
				cache.put(url, "1", 60 * 5);
				
			} catch (Exception e) {
			}
		}
	}

	public static <V> V getItemTotal(ArrayList<V> list, Class <V> itemObjType, ArrayList<String> listFields) { 
		if(list == null || list.size() == 0) {
			try {
				return (V) itemObjType.getConstructor().newInstance();
			} catch (Exception e) {
				return null;
			}
		}
		try {
			V itemTotal = (V) list.get(0).getClass().getConstructor().newInstance();
			Method[] methods = itemTotal.getClass().getMethods();
			
			ArrayList<String> listFields_getMethod = new ArrayList<String>();
			if(listFields != null) {
				for(String s : listFields) {
					listFields_getMethod.add(BaseUtils.getGetterName(s)); 
				}
			}
			for(Method m: methods) {
				long totalLong = 0;
				Method setMethod = null;
				boolean ignoreSet = false;
				
				if(listFields_getMethod.contains(m.getName()) == false || m.getParameterCount() > 0) { 
					continue;
				}
				try {
					setMethod = itemTotal.getClass().getMethod(m.getName().replaceFirst("get", "set"), m.getReturnType());
					if(setMethod == null || setMethod.getParameterCount() != 1 || setMethod.getParameters()[0].getType() != m.getReturnType()) {
						continue;
					}
				} catch (Exception e) {
					continue;
				}
				for(Object item: list) {
					if(m.getReturnType() == String.class ) {
						String value = (String) m.invoke(item);
						try {
							totalLong += BaseUtils.isBlank(value) ? 0 : Long.parseLong(value);  // .replace("+", "")
						} catch (Exception e) {
							ignoreSet = true; // bỏ cột này
							break;
						}
						continue;
					}
					if(m.getReturnType() == int.class || m.getReturnType() == Integer.class) {
						Integer value = (Integer) m.invoke(item);
						totalLong += BaseUtils.parseLong(value + "", 0L); 
						continue;
					}
					if(m.getReturnType() == long.class || m.getReturnType() == Long.class) {
						Long value = (Long) m.invoke(item);
						totalLong += BaseUtils.parseLong(value + "", 0L); 
						continue;
					}
				}
				if(ignoreSet) {
					continue; // bỏ qua cột này
				}
				if(m.getReturnType() == String.class ) {
					setMethod.invoke(itemTotal, totalLong + "");
				}
				else if(m.getReturnType() == int.class || m.getReturnType() == Integer.class) {
					setMethod.invoke(itemTotal, Integer.valueOf(totalLong + ""));
				} 
				else if(m.getReturnType() == long.class || m.getReturnType() == Long.class) {
					setMethod.invoke(itemTotal, totalLong);
				}
			}
			//list.add(itemTotal);
			return (V) itemTotal;
			
		} catch (Exception e) {
		}
		return null;
	}
	
	public static <V> ConcurrentHashMap<String, V> convertList2Map_distinctKey(String transid, ArrayList<V> listData, String keyField) {
		ConcurrentHashMap<String, V> rs = new ConcurrentHashMap<String, V>();
		if(listData == null) {
			return rs;
		}
		for(V o: listData) {
			try {
				String getter = BaseUtils.getGetterName(keyField);
				Method m = o.getClass().getMethod(getter);
				String key = "" + m.invoke(o);
				rs.put(key, o);
			} catch (Exception e) {
			}
		}
		return rs;
	}
	
}
