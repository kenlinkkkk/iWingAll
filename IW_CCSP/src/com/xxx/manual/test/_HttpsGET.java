package com.xxx.manual.test;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.log4j.Logger;

import com.ligerdev.appbase.utils.BaseUtils;
import com.ligerdev.appbase.utils.textbase.Log4jLoader;
import com.xxx.aps.XmlConfigs;

public class _HttpsGET {

	private static CloseableHttpClient httpclient; 

	static {
		try {
			httpclient = com.nip.net.http.HttpUtils.getInstance().getClient(null);
			// Log4jLoader.getLogger().info("======> create http client");
		} catch (Exception e) {
			Log4jLoader.getLogger().info("Exception: " + e.getMessage());
		}
	}
	
	public static void main(String[] args) {
		String url = "https://www.programcreek.com/java-api-examples/?code=NCSC-NL/PEF/PEF-master/src/main/java/nl/minvenj/pef/stream/PcapSniffer.java";
		String s = get("", url);
		System.out.println(s); 
	}
	
	public static final String get(String transid, String url) {
		Logger logger = Log4jLoader.getLogger();
        HttpGet get = new HttpGet(url); 
        CloseableHttpResponse resp = null;
        try {
            logger.info(transid + ", get: " + url); 
            resp = httpclient.execute(get);
            String str = BaseUtils.readInputStream(resp.getEntity().getContent()) + ""; 
            logger.info(transid + ", get: " + str.replace("\n", " ").replace("\t", " ").replaceAll(" +", " ")); 
            return str; 
            
        } catch (Exception e) {
        	logger.error(transid + ", Exception : " + e.getMessage(),e);
        } finally {
            if(resp != null) { 
            	try {
            		resp.close();
            	} catch (Exception e) {}
            }
            get.releaseConnection(); 
        }
        return null;
    }
	
	public static final String post(String transid, String url, String xml) {
		Logger logger = Log4jLoader.getLogger();
        HttpPost post = new HttpPost(url); 
        CloseableHttpResponse resp = null;
        try {
            ByteArrayEntity entity = new ByteArrayEntity(xml.getBytes("UTF-8"));
            post.setEntity(entity);
            post.addHeader("Content-Type","text/xml; charset=utf-8");
            post.addHeader("Connection","close");
            post.addHeader("x-ibm-client-id", XmlConfigs.x_ibm_client_id);
            logger.info(transid + ", forward2ccspREQ: " + xml.replace("\n", " ").replace("\t", " ").replaceAll(" +", " ")); 
            
            resp = httpclient.execute(post);
            String str = BaseUtils.readInputStream(resp.getEntity().getContent()) + ""; 
            logger.info(transid + ", post: " + str.replace("\n", " ").replace("\t", " ").replaceAll(" +", " ")); 
            return str; 
            
        } catch (Exception e) {
        	logger.error(transid + ", Exception : " + e.getMessage(),e);
        } finally {
            if(resp != null) { 
            	try {
            		resp.close();
            	} catch (Exception e) {}
            }
            post.releaseConnection(); 
        }
        return null;
    }
}
