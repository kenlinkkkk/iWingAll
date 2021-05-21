package com.ftp.utils;

import java.util.Comparator;

import com.ftp.entities.MyFtpFile;

public class FTPFileComparator implements Comparator<MyFtpFile> {

	@Override
	public int compare(MyFtpFile o1, MyFtpFile o2) {
		try {
			return o1.getName().compareTo(o2.getName());
		} catch (Exception e) {
		}
		return 0;
	}
}
