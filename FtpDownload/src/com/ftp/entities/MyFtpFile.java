package com.ftp.entities;

import org.apache.commons.net.ftp.FTPFile;

public class MyFtpFile {

	private int type;
	private String name;
	
	// ext
	private FTPFile ftpFile;

	public MyFtpFile() {
		// TODO Auto-generated constructor stub
	}
	
	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public FTPFile getFtpFile() {
		return ftpFile;
	}

	public void setFtpFile(FTPFile ftpFile) {
		this.ftpFile = ftpFile;
	}

}
