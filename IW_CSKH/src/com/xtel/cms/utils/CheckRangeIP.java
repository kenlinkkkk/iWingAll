/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xtel.cms.utils;

import java.io.Serializable;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Hashtable;

import org.apache.log4j.Logger;

import com.ligerdev.appbase.utils.BaseUtils;
import com.ligerdev.appbase.utils.cache.CacheSyncFile;
import com.ligerdev.appbase.utils.textbase.Log4jLoader;

/**
 * 
 */
public class CheckRangeIP implements Serializable {

	private String ip_ranges;
	private String ips[];
	
	private static Logger logger = Log4jLoader.getLogger();
	private static CacheSyncFile cache = CacheSyncFile.getInstance(1000000);
	
	public synchronized static CheckRangeIP getInstance(String transid, String fileName){
		String key = "listIp_" + fileName;
		CheckRangeIP instance = (CheckRangeIP) cache.getObject(key);
		if(instance == null){
			instance = new CheckRangeIP(transid, fileName);
			cache.put(key, instance, 60 * 3);
		}
		return instance;
	}
	
	private CheckRangeIP(String transid, String fileName) {
		// TODO Auto-generated constructor stub
		ip_ranges = BaseUtils.readFile(fileName).replace("\r", "").replace(" ", ""); 
		ips = ip_ranges.split("\n");
		logger.info(transid + ", reload ip " + fileName + ", size = " + ips.length);
	}

	public static void main2(String[] args) {
		// BaseUtils.writeFile("config/ip.mobi", ip_ranges.replace(";", "\n"), false);
	}

	public static void main(String args[]) {
		CheckRangeIP p = new CheckRangeIP("", "config/ip.mobi");
		 try {
			String ip = "10.195.172.56";
			p.isMatchIP(ip);
			System.out.println("ok"); 
			
		} catch (Exception ex) {
			ex.printStackTrace();
		} 
	}

	public boolean isMatchIP(String ip) {
		Hashtable<String, String> notmatch = new Hashtable<String, String>();
		for (String ip_list : ips) {
			
			if(ip_list.startsWith("#")){
				continue;
			}
			if(notmatch.containsKey(ip_list)){
				continue;
			}
			if (netMatch(ip_list, ip)) {
				return true;
			}
			notmatch.put(ip_list, "");
		}
		return false;
	}

	public static boolean netMatch(String addr, String addr1) {
		String[] parts = addr.split("/");
		String ip = parts[0];
		int prefix;

		if (parts.length < 2) {
			prefix = 0;
		} else {
			prefix = Integer.parseInt(parts[1]);
		}
		Inet4Address a = null;
		Inet4Address a1 = null;
		try {
			a = (Inet4Address) InetAddress.getByName(ip);
			a1 = (Inet4Address) InetAddress.getByName(addr1);
		} catch (UnknownHostException e) {
		}

		byte[] b = a.getAddress();
		int ipInt = ((b[0] & 0xFF) << 24) | ((b[1] & 0xFF) << 16) | ((b[2] & 0xFF) << 8) | ((b[3] & 0xFF) << 0);

		byte[] b1 = a1.getAddress();
		int ipInt1 = ((b1[0] & 0xFF) << 24) | ((b1[1] & 0xFF) << 16) | ((b1[2] & 0xFF) << 8) | ((b1[3] & 0xFF) << 0);

		int mask = ~((1 << (32 - prefix)) - 1);

		if ((ipInt & mask) == (ipInt1 & mask)) {
			return true;
		} else {
			return false;
		}
	}
}
