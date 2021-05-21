package com.ftp;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

import com.ligerdev.appbase.utils.BaseUtils;

public class CopyConfigMediation {

	public static void main(String[] args) {
		
		// /u01/data/mobifone/01_Ericsson_CS40/FtpClient
		String parentScan = args[0];
		// java -cp :./lib/* com.ftp.CopyConfigMediation /u01/data/mobifone/ 32
		
		System.out.println("HomePath: " + parentScan + ", port prefix: " + args[1]); 
		File files[] = new File(args[0]).listFiles();
		
		for(File f : files){
			if(f.isFile()){
				continue;
			}
			if(f.getName().contains("_") == false){
				continue;
			}
			int number = BaseUtils.parseInt(f.getName().split("_")[0], -1);
			if(number < 0 ){
				continue;
			}
			System.out.println("process folder: " + f.getName()); 
			String copyTo = null;
			try {
				copyTo = parentScan + "/" + f.getName() + "/FtpClient/lib/ftp_client.jar";
				FileUtils.copyFile(new File("lib/ftp_client.jar"), new File(copyTo));
				System.out.println("Copy lib/ftp_client.jar to " + copyTo);
				
			} catch (IOException e) {
				System.out.println("Can NOT copy lib/ftp_client.jar to " + copyTo);
			}
			String xml = BaseUtils.readFile("template/config.xml");
			xml = xml.replace("@moduleName", f.getName());
			xml = xml.replace("@port", args[1] + f.getName().split("_")[0]); 
			BaseUtils.writeFile(parentScan + "/" + f.getName() +  "/FtpClient/config/config.xml", xml, false);
			System.out.println("Write config.xml to module " + f.getName()); 
			
			String sh = BaseUtils.readFile("template/shell.sh");
			sh = sh.replace("@grepName", "ftpclient_" + f.getName());
			BaseUtils.writeFile(parentScan + "/" + f.getName() + "/FtpClient/shell.sh", sh, false);
			
			System.out.println(); 
		}
	}
}
