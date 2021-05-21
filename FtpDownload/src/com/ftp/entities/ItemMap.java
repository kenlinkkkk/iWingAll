package com.ftp.entities;

import java.io.Serializable;

public class ItemMap implements Serializable {

	private String localPrefix;
	private String serverPath;
	private String localPath;
	private String copyLocalTo;
	private int checkPrevDate;
	private String folderDateFormat;
	private String logName;
	private int timeoutDownload;
	private String filePattern;
	private String actionOnServer;
	private String moveOnServer;
	private String fileNotMatch;
	private boolean extract;
	private String extractType;
	private String extractPass;
	
	public ItemMap() {
		// TODO Auto-generated constructor stub
	}

	public ItemMap(String serverPath, String localPath,  String copyLocalTo, int checkPrevDate, 
			String folderDateFormat, String logName, int timeoutDownload, String filePattern,
			String actionOnServer, String moveOnServer, String fileNotMatch, boolean extract,
			String extractType, String extractPass, String localPrefix) {
		super();
		this.serverPath = serverPath;
		this.localPath = localPath;
		this.copyLocalTo = copyLocalTo;
		this.checkPrevDate = checkPrevDate;
		this.folderDateFormat = folderDateFormat;
		this.logName = logName;
		this.timeoutDownload = timeoutDownload;
		this.filePattern = filePattern;
		this.setActionOnServer(actionOnServer);
		this.moveOnServer = moveOnServer;
		this.fileNotMatch = fileNotMatch;
		this.extract = extract;
		this.extractType = extractType;
		this.extractPass = extractPass;
		this.setLocalPrefix(localPrefix);
	}

	public String getServerPath() {
		return serverPath;
	}

	public void setServerPath(String serverPath) {
		this.serverPath = serverPath;
	}

	public String getLocalPath() {
		return localPath;
	}

	public void setLocalPath(String localPath) {
		this.localPath = localPath;
	} 
	public String getCopyLocalTo() {
		return copyLocalTo;
	}

	public void setCopyLocalTo(String copyLocalTo) {
		this.copyLocalTo = copyLocalTo;
	}

	public int getCheckPrevDate() {
		return checkPrevDate;
	}

	public void setCheckPrevDate(int checkPrevDate) {
		this.checkPrevDate = checkPrevDate;
	}

	public String getFolderDateFormat() {
		return folderDateFormat;
	}

	public void setFolderDateFormat(String folderDateFormat) {
		this.folderDateFormat = folderDateFormat;
	}

	public String getLogName() {
		return logName;
	}

	public void setLogName(String name) {
		this.logName = name;
	}

	public int getTimeoutDownload() {
		return timeoutDownload;
	}

	public void setTimeoutDownload(int timeoutDownload) {
		this.timeoutDownload = timeoutDownload;
	}

	public String getFilePattern() {
		return filePattern;
	}

	public void setFilePattern(String filePattern) {
		this.filePattern = filePattern;
	}

	public String getMoveOnServer() {
		return moveOnServer;
	}

	public void setMoveOnServer(String moveOnServer) {
		this.moveOnServer = moveOnServer;
	}

	public String getActionOnServer() {
		return actionOnServer;
	}

	public void setActionOnServer(String actionOnServer) {
		this.actionOnServer = actionOnServer;
	}

	public String getFileNotMatch() {
		return fileNotMatch;
	}

	public void setFileNotMatch(String fileNotMatch) {
		this.fileNotMatch = fileNotMatch;
	}

	public String getExtractPass() {
		return extractPass;
	}

	public void setExtractPass(String extractPass) {
		this.extractPass = extractPass;
	}

	public String getExtractType() {
		return extractType;
	}

	public void setExtractType(String extractType) {
		this.extractType = extractType;
	}

	public boolean isExtract() {
		return extract;
	}

	public void setExtract(boolean extract) {
		this.extract = extract;
	}

	public String getLocalPrefix() {
		return localPrefix;
	}

	public void setLocalPrefix(String localPrefix) {
		this.localPrefix = localPrefix;
	}
}
