package com.xxx.aps;

import java.io.File;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

import com.ligerdev.appbase.utils.db.XBaseDAO;
import com.ligerdev.appbase.utils.encrypt.AES;

public class TestMain {
	
	public static void main(String[] args) {
//		XBaseDAO baseDB = XBaseDAO.getInstance("main");
//		String sql = "select 'ABC' service, now()  time ,b.* from fr_sub_log b where...";
//		
//		ArrayList<String> l = null;
//		baseDB.insertList("", l);
		
		String s = URLDecoder.decode("4tK5J4%2FetZ07Wo1hkObHMQ%3D%3D");
		System.out.println(s); 
		System.out.println(new AES("Vny5fgACqu6lGsbq").decrypt(s));
	}
	
	public static void main3(String[] args) {
		
		String folderCdr = "D:\\GGDrivePull\\workspace\\CCSPBase\\WebContent\\META-INF";
		File files[] = new File(folderCdr).listFiles();
		
		final Comparator<File> compare1 = new Comparator<File>() {
			// @Override
			public int compare(File o1, File o2) {
				return o1.getName().compareTo(o2.getName());
			}
		};
	 	Arrays.sort(files, compare1);
	 	
	 	for(File f: files){
			System.out.println(f.getName());
 	 	}
	}

	public static void main2(String[] args) {
//		String s = "{\"traceNum\":\"vano_1540182873622\",\"amount\":330,\"epayCode\":044,\"code\":1,\"descs\":\"Thành công.\"}";
//		String epaycode = String.valueOf(s).split("\"epayCode\":")[1].split(",")[0].trim();
//		System.out.println(epaycode);
//		
//		String key = "RWuoxsZlODP8SUoD";
//		System.out.println("?id=accc?link=ccc".replace("?", "").split("link=")[0]);   
		
		String s = "http://ketbanhulaa.vn/home";
		s = new AES("ANAYvJtiwyMxECKK").encrypt(s);
		s = new AES("ANAYvJtiwyMxECKK").decrypt("hMAXlTCoQtaQndOwdzE4zA=="); 
		// String url = "http://free.mobifone.vn/isdn?sp=5799&link=" + s;
		// System.out.println(s);
		
		System.out.println("share|0|success|32".contains("share|0|")); 
		
		/*
			http://free.mobifone.vn/isdn?sp=5799&link=mESR2KCtx6AzxkiWwES9krit1I3ND3+fKjGiHwKp+0Q=
			1. mESR2KCtx6AzxkiWwES9krit1I3ND3+fKjGiHwKp+0Q=     =>        http://ketbanhulaa.vn/home
			2. get Msisdn, encrypt X :  http://ketbanhulaa.vn/home?link=abcajscbaqswcbqw (abcajscbaqswcbqw = x)
			3. hulaa: get link, decrypt => msisdn, add session
		*/
		
		
		String str = "SMEDIA_DK_GT_SUCC_FREE: (DK) Chúc mừng bạn";
		System.out.println(str.contains(": (DK)")); 
		
		String msg = "(DK)" + str.split("\\: \\(DK\\)")[1];
		System.out.println(msg); 
		
	}
}
