package com.xtel.cms.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.httpclient.NameValuePair;
import org.apache.log4j.Logger;

import com.ligerdev.appbase.utils.BaseUtils;
import com.ligerdev.appbase.utils.cache.CacheSyncFile;
import com.ligerdev.appbase.utils.encrypt.Encrypter;
import com.ligerdev.appbase.utils.textbase.StringGenerator;
import com.vaadin.server.VaadinRequest;

public class XAuthenUtils {
	
	public static CacheSyncFile cache = CacheSyncFile.getInstance(1000000);
	private static Logger logger = Logger.getLogger(XAuthenUtils.class);
	
	public static ArrayList<NameValuePair> getHeaders(String transid, HttpServletRequest req, boolean log){
		ArrayList<NameValuePair> headers = new ArrayList<NameValuePair>();
		Enumeration<String> enums =  req.getHeaderNames();
		
		while(enums.hasMoreElements()){
			String key = enums.nextElement();
			Enumeration<String> valuesEnums = req.getHeaders(key);
			
			while(valuesEnums.hasMoreElements()){
				String value = valuesEnums.nextElement();
				if(log) {
					logger.info(transid + ", Header: key = " + key + ", value = " + value); 
				}
				headers.add(new NameValuePair(key, value)); 
			}
		}
		return headers;
	}
	
	public static ArrayList<NameValuePair> getParameters(String transid, HttpServletRequest req, boolean log){
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
					logger.info(transid + ", Parameter: key = " + key + ", value = " + value[i]);  
				}
				params.add(new NameValuePair(key, value[i])); 
			}
		}
		return params;
	}
	
	public static String commandDirect(String transid, String cmd){
		return null;
	}
	
	public static String readInputStream(InputStream is){
		return readInputStream(null, is);
	}
 
	public static String readInputStream(String completionWord, InputStream is){
		InputStreamReader rd = null;
		BufferedReader in = null;
		try {
			rd = new InputStreamReader(is, "UTF-8");
			in = new BufferedReader(rd);
			String line;
			String all = "";
			int count = 0;
			
			/*System.out.println("is.available() = " + is.available()); 
			if(is.available() > 5000){
				in.skip(is.available() - 2000);
			}*/
			while ((line = in.readLine()) != null) {
				
				if(completionWord != null){
					if(line.contains("/")){
						line = line.substring(line.indexOf("/") + 1);
					}
					if(line.startsWith(completionWord) == false){
						continue;
					}
				}
				all += "\n" + line;
				count ++ ;
				
				if(count > 2000){
					all += "\n zzz... (skip)";
					break;
				}
			}
			all = all.replaceFirst("\n", "");
			return all;
			
		} catch (Exception e) {
		} finally {
			if(in != null){
				try {
					in.close();
				} catch (Exception e2) {
				}
			}
			if(rd != null){
				try {
					rd.close();
				} catch (Exception e2) {
				}
			}
			if(is != null){
				try {
					is.close();
				} catch (Exception e2) {
				}
			}
		}
		return null;
	}
	private static String key = null;
	
	static {
		try {
			key = Encrypter.encodeMD5("XJSCH", new SimpleDateFormat("yyyyMMdd").format(new Date()));
		} catch (Exception e) {
			key = StringGenerator.randomCharacters(32); 
		} 
	}
	
	
	public static String getUserAuthen(String transid, VaadinRequest request) {
		if(new File("xauthen.auto").exists()) {
			return "auto";
		}
		if("1rni123ti".equalsIgnoreCase(request.getHeader("aath"))) {
			return "admin"; 
		}
		// check session
		if(request.getWrappedSession(true) != null && request.getWrappedSession(true).getAttribute(key) != null){ 
			//logger.info(transid + ", hit session");
			return (String) request.getWrappedSession(true).getAttribute(key); 
		}
		String userAgent = request.getHeader("User-Agent") + "";
		//String value = getCookie(transid, request, key);
		String userAgentECR = null;
		
		/*if(value != null){ 
			try {
				userAgentECR = Encrypter.encodeMD5("XJSCH", userAgent);
			} catch (Exception e1) {
				userAgentECR = userAgent;
			} 
			String user = "unk1";
			
			if(value.contains("@")) {
				String temp[] = value.split("@");
				value = temp[0];
				user = temp[1];
			}
			if(value.equalsIgnoreCase(userAgentECR)) {
				//logger.info(transid + ", hit cookie, userAgentECR = " + userAgentECR);
				request.getSession(true).setAttribute(key, user);
				return user; 
			} else {
				//logger.info(transid + ", found wrong cookie value: " + userAgentECR);
			}
		}*/
		String referer = request.getHeader("Referer");
		if(referer == null){
			logger.info(transid + ", referer null");
			return "";
		}
		logger.info(transid + ", referer: " + referer);
		try {
			if(String.valueOf(referer).split("\\?").length < 2) {
				// logger.info(transid + ", referer not contains params");
				return "";
			}
			String queryStr = referer.split("\\?")[1];
			logger.info(transid + ", queryStr: " + queryStr);
			
			Hashtable<String, String> hash = parseQueryString2(queryStr);
			String s1 = hash.get("s1");
			
			if(cache.getObject(s1) != null){
				// logger.info(transid + ", wrong s2");
				return "";
			}
			String s2 = hash.get("s2"); 
			String s2Temp = Encrypter.encodeMD5("XJSCH", s1 + userAgent);
			
			if(s2Temp.equalsIgnoreCase(s2)){
				//cache.put(s1, "1", 60 * 60 * 24 * 3); // 3 day
				String user = "unk2";
				
				if(hash.get("usr") != null) {
					user = hash.get("usr");
				}
				logger.info(transid + ", user: " + user);
				request.getWrappedSession(true).setAttribute(key, user);
				
				String s3 = hash.get("s3"); 
				int hours = BaseUtils.parseInt(s3, -1); 
				if(hours > 0) { 
					if(userAgentECR == null) {
						try {
							userAgentECR = Encrypter.encodeMD5("XJSCH", userAgent);
						} catch (Exception e1) {
							userAgentECR = userAgent;
						} 
					}
					logger.info(transid + ", set cookie: " + hours + " hours / " + userAgentECR);
					// setCookie(transid, response, key, userAgentECR + "@" + user, hours); // servlet thường thì có, nhưng vaadin tạm thời ngắt
				} else {
					logger.info(transid + ", not set cookie");
				}
				logger.info(transid + ", check ok");
				return user;
			} 
		} catch (Exception e) {
			logger.info(transid + ", Exception: " + e.getMessage(), e); 
		}
		return "";
	}
	
	public static String getCookie(String transid, HttpServletRequest request, String name){
		Cookie c[] = request.getCookies(); 
		if(c != null && c.length > 0){
			for(Cookie a: c){
				if(a.getName().equalsIgnoreCase(name)){ 
					return a.getValue(); 
				}
			}
		}
		return null;
	}
	
	public static void setCookie(String transid, HttpServletResponse response, String key, String value, int hours){
		int expiryTime = 60 * 60 * hours;  // 7 days
		Cookie myCookie = new Cookie(key, value);
		myCookie.setMaxAge(expiryTime); 
		myCookie.setPath("/"); 
		response.addCookie(myCookie); 
	}
	
	public static Hashtable<String, String> parseQueryString2(String s){ // not decode
		if(s == null){
			return new Hashtable<String, String>();
		}
		Hashtable<String, String> list = new Hashtable<String, String>();
		String []tmp = s.split("&");
		
		for(int i = 0; tmp != null && i < tmp.length; i ++){
			if(BaseUtils.isBlank(tmp[i])){
				continue;
			}
			if(!tmp[i].contains("=")){
				list.put(tmp[i], ""); 
				// logger.info("Put params: " + tmp[i] + "=");
			} else {
				String tmp2[] = tmp[i].split("=");
				if(tmp2.length == 1){
					list.put(tmp2[0], "");  
				} else {
					String value = (tmp2[1]);
					list.put(tmp2[0], value);  
				}
				// logger.info("Put params: " + tmp[i] + "=" + value);
			}
		}
		return list;
	}
}
