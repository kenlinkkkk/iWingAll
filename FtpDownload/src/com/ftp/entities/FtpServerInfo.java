package com.ftp.entities;

import java.io.Serializable;
import java.util.ArrayList;

public class FtpServerInfo implements Serializable {

	private String ip;
	private int port;
	private String user;
	private String pass;
	private String localMode;
	private String remoteMode;
	private ArrayList<String>  timeExpression;
	private int connectTimeout;
	private ArrayList<ItemMap> itemMaps;
	private String separator;
	private boolean keepConnection;
	private int bufferSize;
	private String protocol;

	public FtpServerInfo() {
		// TODO Auto-generated constructor stub
	}
	
	public FtpServerInfo(String ip, int port, String user, String pass, ArrayList<String>  timeExpression, 
			String localMode,  String remoteMode, ArrayList<ItemMap> itemMaps,
			int connectTimeout, String separator, boolean keepConnection, int bufferSize, String protocol) {
		super();
		this.ip = ip;
		this.port = port;
		this.user = user;
		this.pass = pass;
		this.localMode = localMode;
		this.remoteMode = remoteMode;
		this.timeExpression = timeExpression;
		this.itemMaps = itemMaps;
		this.connectTimeout = connectTimeout;
		this.separator = separator;
		this.keepConnection = keepConnection;
		this.setBufferSize(bufferSize);
		this.protocol = protocol;
	}
	
	public int getBufferSize() {
		return bufferSize;
	}

	public void setBufferSize(int bufferSize) {
		this.bufferSize = bufferSize;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPass() {
		return pass;
	}

	public void setPass(String pass) {
		this.pass = pass;
	}

	public ArrayList<String> getTimeExpression() {
		return timeExpression;
	}

	public void setTimeExpression(ArrayList<String> time_expression) {
		this.timeExpression = time_expression;
	}

	public ArrayList<ItemMap> getItemMaps() {
		return itemMaps;
	}

	public void setItemMaps(ArrayList<ItemMap> itemMaps) {
		this.itemMaps = itemMaps;
	}

	public String getLocalMode() {
		return localMode;
	}

	public void setLocalMode(String localMode) {
		this.localMode = localMode;
	}

	public String getRemoteMode() {
		return remoteMode;
	}

	public void setRemoteMode(String remoteMode) {
		this.remoteMode = remoteMode;
	}

	public int getConnectTimeout() {
		return connectTimeout;
	}

	public void setConnectTimeout(int connectTimeout) {
		this.connectTimeout = connectTimeout;
	} 

	public String getSeparator() {
		return separator;
	}

	public void setSeparator(String separator) {
		this.separator = separator;
	}

	public boolean isKeepConnection() {
		return keepConnection;
	}

	public void setKeepConnection(boolean keepConnection) {
		this.keepConnection = keepConnection;
	}

	public String getProtocol() {
		return protocol;
	}

	public void setProtocol(String protocol) {
		this.protocol = protocol;
	} 
}
