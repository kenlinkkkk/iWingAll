package com.xxx.manual.test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

public class _HttpPostTest {
	
	static Logger logger = Logger.getLogger("LOG");
	
	public static void main(String[] args) {
		HashMap<String, String> postDataParams = new HashMap<String, String>();
		postDataParams.put("content", "TEST2222");
		postDataParams.put("isdn", "84909090529");
		postDataParams.put("request_id", "123123123");
		
		String s = performPostCall("", "http://45.121.26.232/ccsp/forwardMessage.jsp?", postDataParams);
		System.out.println(s); 
	}

	public static String  performPostCall(String transid, String requestURL, HashMap<String, String> postDataParams) {
	    URL url;
	    String response = "";
	    try {
	        url = new URL(requestURL);

	        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	        conn.setReadTimeout(15000);
	        conn.setConnectTimeout(15000);
	        conn.setRequestMethod("POST");
	        conn.setDoInput(true);
	        conn.setDoOutput(true);

	        OutputStream os = conn.getOutputStream();
	        BufferedWriter writer = new BufferedWriter(
	                new OutputStreamWriter(os, "UTF-8"));
	        writer.write(getPostDataString(postDataParams));

	        writer.flush();
	        writer.close();
	        os.close();
	        int responseCode=conn.getResponseCode();

	        if (responseCode == HttpURLConnection.HTTP_OK) {
	            String line;
	            BufferedReader br=new BufferedReader(new InputStreamReader(conn.getInputStream()));
	            while ((line=br.readLine()) != null) {
	                response+=line;
	            }
	        }
	        else {
	            response="";
	        }
	    } catch (Exception e) {
	        logger.info(transid + ", Exception: " + e.getMessage(), e);
	    }
	    return response;
	}

	private static String getPostDataString(HashMap<String, String> params) throws UnsupportedEncodingException{
	    StringBuilder result = new StringBuilder();
	    boolean first = true;
	    for(Map.Entry<String, String> entry : params.entrySet()){
	        if (first)
	            first = false;
	        else
	            result.append("&");

	        result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
	        result.append("=");
	        result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
	    }
	    return result.toString();
	}
}
