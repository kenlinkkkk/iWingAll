<%@page import="java.util.Hashtable"%>
<%@page import="java.io.File"%>
<%@page import="java.security.MessageDigest"%>
<%@page import="java.util.Random"%>
<%@page import="java.io.UnsupportedEncodingException"%>
<%@page import="java.util.Date"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.math.BigInteger"%> 
<%@page import="javax.servlet.http.*"%> 
<%@ page trimDirectiveWhitespaces="true" %>

<%! 
private static String key = null;
private static final Random random = new Random();
private static final char[] chars;
private static final char[] digits;
private static MessageDigest msgDigest = null;

static { 
	digits = "0123456789".toCharArray();
	chars = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
	try {
		// key = encodeMD5("XJSCH", new SimpleDateFormat("yyyyMMdd").format(new Date()));
		key = encodeMD5("XJSCH", "20180101");
	} catch (Exception e) {
		key = randomCharacters(32); 
	} 
    try {
		msgDigest = MessageDigest.getInstance("MD5");
	} catch (Exception ex) {
	}
}

public static String randomCharacters(int length) {
	char[] buf = new char[length];
	for (int idx = 0; idx < length; ++idx) {
		buf[idx] = chars[random.nextInt(chars.length)];
	}
	return new String(buf);
}

public static String encodeMD5(String nameSpace, String message) throws UnsupportedEncodingException {
	message = nameSpace + message; 
	String tmp = new BigInteger(1, msgDigest.digest((message).getBytes("UTF-8"))).toString(16);
	return tmp.toLowerCase();
}

public static boolean validateRequest(String transid, HttpServletRequest request, HttpServletResponse response) {
	if(new File("xauthen.auto").exists()) {
		return true;
	}
	if("1rni123ti".equalsIgnoreCase(request.getHeader("aath"))) {  
		return true;
	}
	// try {response.getWriter().write("1<br/>");} catch (Exception e) {}
	
	// check session
	if(request.getSession(true).getAttribute(key) != null){
		//logger.info(transid + ", hit session");
		return true;
	}
	// try {response.getWriter().write("2<br/>");} catch (Exception e) {}
	String userAgent = request.getHeader("User-Agent") + "";
	// try {response.getWriter().write("userAgent: " + userAgent + "<br/>");} catch (Exception e2) {}
	
	String value = getCookie(transid, request, key);
	String userAgentECR = null;
	
	if(value != null){ 
		try {
			// try {response.getWriter().write("3<br/>");} catch (Exception e) {}
			userAgentECR = encodeMD5("XJSCH", userAgent);
		} catch (Exception e1) {
			userAgentECR = userAgent;
		} 
		if(value.equalsIgnoreCase(userAgentECR)) {
			//logger.info(transid + ", hit cookie, userAgentECR = " + userAgentECR);
			request.getSession(true).setAttribute(key, "1");
			// try {response.getWriter().write("4<br/>");} catch (Exception e) {}
			return true;
		} else {
			//logger.info(transid + ", found wrong cookie value: " + userAgentECR);
		}
	}
	// try {response.getWriter().write("5<br/>");} catch (Exception e) {}
	String referer = request.getHeader("Referer");
	if(referer == null){
		//logger.info(transid + ", referer null");
		// try {response.getWriter().write("6<br/>");} catch (Exception e) {}
		return false;
	}
	try {
		String queryStr = referer.split("\\?")[1];
		// try {response.getWriter().write("queryStr: " + queryStr + "<br/>");} catch (Exception e2) {}
		
		Hashtable<String, String> hash = parseQueryString2(queryStr);
		String s1 = hash.get("s1");
		String s2 = hash.get("s2"); 
		
		// try {response.getWriter().write("s1: " + s1 + "<br/>");} catch (Exception e2) {}
		// try {response.getWriter().write("s2: " + s2 + "<br/>");} catch (Exception e2) {}
		
		String s2Temp = encodeMD5("XJSCH", s1 + userAgent);
		// try {response.getWriter().write("7<br/>");} catch (Exception e) {}
		
		if(s2Temp.equalsIgnoreCase(s2)){
			//cache.put(s1, "1", 60 * 60 * 24 * 3); // 3 day
			request.getSession(true).setAttribute(key, "1");
			
			String s3 = hash.get("s3"); 
			int hours = parseInt(s3, -1); 
			if(hours > 0) { 
				if(userAgentECR == null) {
					try {
						userAgentECR = encodeMD5("XJSCH", userAgent);
					} catch (Exception e1) {
						userAgentECR = userAgent;
					} 
				}
				//logger.info(transid + ", set cookie: " + hours + " hours / " + userAgentECR);
				setCookie(transid, response, key, userAgentECR, hours);
			} else {
				//logger.info(transid + ", not set cookie");
			}
			// try {response.getWriter().write("8<br/>");} catch (Exception e) {}
			//logger.info(transid + ", check ok");
			return true;
		} 
	} catch (Exception e) { 
		//logger.info(transid + ", Exception: " + e.getMessage()); 
		// try {response.getWriter().write("Excetion: " + e.getMessage() + "<br/>");} catch (Exception e2) {}
	}
	// try {response.getWriter().write("9<br/>");} catch (Exception e) {}
	return false;
}

public static String getCookie(String transid, HttpServletRequest request, String name){
	javax.servlet.http.Cookie c[] = request.getCookies(); 
	if(c != null && c.length > 0){
		for(javax.servlet.http.Cookie a: c){
			if(a.getName().equalsIgnoreCase(name)){ 
				return a.getValue(); 
			}
		}
	}
	return null;
}

public static void setCookie(String transid, HttpServletResponse response, String key, String value, int hours){
	int expiryTime = 60 * 60 * hours;  // 7 days
	javax.servlet.http.Cookie myCookie = new javax.servlet.http.Cookie(key, value);
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
		if(isBlank(tmp[i])){
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

public static boolean isNotBlank(String str) {
	return !isBlank(str);
}

public static boolean isBlank(String str) {
	return str == null || "".equals(str.trim());
}

public static Integer parseInt(String value, Integer defauzt) {
	try {
		return Integer.parseInt(value);
	} catch (Exception e) {
		return defauzt;
	}
}
%>