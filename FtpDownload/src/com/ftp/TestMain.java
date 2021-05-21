package com.ftp;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.SocketException;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

import com.ftp.processor.FTPIntergration;
import com.ftp.utils.XmlConfigs;

public class TestMain {

	static {
		DOMConfigurator.configure(new File("./config/", "log4j.xml").getPath());
		XmlConfigs.init(XmlConfigs.class);
	}
	
	private static Logger logger = Logger.getLogger(FTPIntergration.class);
	private static Logger loggerCDR = Logger.getLogger("CDR_EXPORT");
	
	public static void main(String[] args) throws SocketException, IOException {
		FTPClient ftpClient = new FTPClient();
		ftpClient.setDefaultTimeout(10000);
		ftpClient.connect("192.168.6.107", 21);
		ftpClient.login("ducnv", "'");
		ftpClient.enterLocalPassiveMode();
		System.out.println("============== START ============="); 
		while(true){
			FTPFile files[] = ftpClient.listFiles("/home/ducnv/Exchange/Test");
			boolean downloaded = false;
			for (int i = 0; files != null &&  i < files.length; i++) {
				File f = new File("/home/ducnv/Desktop/test.txt");
				if(f.exists()){
					continue;
				}
				System.out.println("Prepare download file = " + files[i].getName()); 
				long l1 = System.currentTimeMillis();
				FileOutputStream out = new FileOutputStream(f);
				boolean res = ftpClient.retrieveFile("/home/ducnv/Exchange/Test/" + files[i].getName(), out);
				out.close();
				long l2 =  System.currentTimeMillis();
				downloaded = true;
				System.out.println("Downloaded: " + files[i].getName() + ", time = " + (l2 - l1)); 
			}
			if(downloaded){
				System.out.println("===============================\n"); 
			}
			try {
				Thread.sleep(100);
			} catch (Exception e) {
			}
		}
	} 
}
