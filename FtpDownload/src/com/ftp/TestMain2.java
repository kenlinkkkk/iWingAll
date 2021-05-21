package com.ftp;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.apache.commons.lang.time.FastDateFormat;

public class TestMain2 {

	public static void main(String[] args) {
		// 
		System.out.println("20190111_214557.txt".matches(".*.txt$"));
	}
	
	public static void main2(String[] args) throws ParseException {
		new SimpleDateFormat("yyyyMMddHHmmss");
		FastDateFormat.getInstance("yyyyMMddHHmmss");
		{
			SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
			long l1 = System.currentTimeMillis();
			for(int i = 0 ; i < 10000; i ++){
				new SimpleDateFormat("yyyyMMddHHmmss").format(System.currentTimeMillis());
				new SimpleDateFormat("yyyyMMddHHmmss").parse("20151111123000");
			}
			long l2 = System.currentTimeMillis();
			System.out.println(l2 - l1);
		}
		{
			FastDateFormat df = FastDateFormat.getInstance("yyyyMMddHHmmss");
			long l1 = System.currentTimeMillis();
			for(int i = 0 ; i < 10000; i ++){
				FastDateFormat.getInstance("yyyyMMddHHmmss").format(System.currentTimeMillis());
				FastDateFormat.getInstance("yyyyMMddHHmmss").parseObject("20151111123000");
			}
			long l2 = System.currentTimeMillis();
			System.out.println(l2 - l1);
		}
	}
}
