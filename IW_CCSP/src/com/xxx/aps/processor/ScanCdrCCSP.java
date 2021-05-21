package com.xxx.aps.processor;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.Logger;

import com.ligerdev.appbase.utils.BaseUtils;
import com.ligerdev.appbase.utils.IReadFile;
import com.ligerdev.appbase.utils.db.BaseDAO;
import com.ligerdev.appbase.utils.db.XBaseDAO;
import com.ligerdev.appbase.utils.textbase.StringGenerator;
import com.ligerdev.appbase.utils.threads.AbsProcessor;
import com.xxx.aps.logic.db.orm.ActionHis;
import com.xxx.aps.logic.db.orm.MtHis;
import com.xxx.aps.logic.db.orm.Subscriber;
import com.xxx.aps.logic.entity.SqlBean;
import com.xxx.aps.utils.AppUtils;

public class ScanCdrCCSP extends AbsProcessor {

	 	private static Logger logger = Logger.getLogger("LOG"); 
		private static BaseDAO baseDAO = BaseDAO.getInstance("main");
		private static XBaseDAO xbaseDAO = XBaseDAO.getInstance("main");
		
		@Override
		public void execute() throws Exception {
			// TODO Auto-generated method stub
			if(AppUtils.isEnableNode0() == false || xbaseDAO == null){
				return;
			}
			execute0();
		}
		
		private synchronized int execute0() throws IOException {
			BaseUtils.sleep(10000); 
			transid = "CR@" + StringGenerator.randomCharacters(4);  
			
			String folderCdr = "/opt/kns/cdr/ccsp/ready/";
			File files[] = new File(folderCdr).listFiles();
			
			if(files == null || files.length == 0){
				// response.getWriter().print("blank folder");
				return 0;
			}
			/*
				- gia han thanh cong
				939705958,9443,SMEDIA,GT,27/07/2017 13:42:39,27/07/2017 13:42:39,,28/07/2017 13:42:38,0,VASP,GH_SMEDIA GT,3000,SMEDIA_GH_SUCC: 
					
				- gia han fail
				939706764,9443,SMEDIA,GT,27/07/2017 13:42:40,27/07/2017 13:42:40,,28/07/2017 13:42:39,0,VASP,GH_SMEDIA GT,0,SMEDIA_GH_SUCC_RETRY:
				
				- dang ky
				905061776,9443,SMEDIA,GT,21/07/2017 17:31:24,21/07/2017 17:31:24,,24/07/2017 17:31:23,1,SMS,DK GT,0,SMEDIA_DK_GT_SUCC_FREE: (DK) Chúc mừng bạn trở thành thành viên ....
				
				- huy 
				1208942350,9443,SMEDIA,GT,21/07/2017 09:06:04,21/07/2017 09:06:04,21/07/2017 09:06:04,22/07/2017 09:06:03,3,SMS,HUY GT,0,SMEDIA_HUY_GT_SUCC: Quý khách đã hủy....
			*/
			
			final Comparator<File> compare1 = new Comparator<File>() {
				// @Override
				public int compare(File o1, File o2) {
					return o1.getName().compareTo(o2.getName());
				}
			};
		 	Arrays.sort(files, compare1);
			final AtomicInteger counter = new AtomicInteger();
			 
			for(File f: files){
				logger.info(transid + ", found: " + f.getAbsolutePath()); 
				
				if(f.getName().endsWith("txt")){
					
					BaseUtils.readFile(f, new IReadFile(){
						public boolean readLine(String line) throws Exception {
							try{
								String format = "dd/MM/yyyy HH:mm:ss";
								String str[] = line.split(",");
								String packageCode = str[3].toUpperCase();
								String msisdn = BaseUtils.formatMsisdn(str[0], "84", "84");
								String type = str[8];
								String channel = str[9];
								
								if("CP".equalsIgnoreCase(channel)){
							    	channel = "SMS";
							    }
								//String moContent = str[10];
								int fee = Integer.parseInt(str[11]);
								Date chargeDate = BaseUtils.parseTime(format, str[4]);
								Date expireTime = BaseUtils.parseTime(format, str[7]);
								String transidTmp = transid + "@" + StringGenerator.randomCharacters(4);
								
								int addValue = fee;
								if(AppUtils.subnote23Increase1()){
									addValue = 1;
								}
								if("0".equalsIgnoreCase(type)){
									// tạm thời chỉ xử lý các CDR gia hạn, các loại khác bổ sung sau
									if(fee <= 0){
										return true;
									}
									String dateTableHis = BaseUtils.formatTime("yyyyMM", chargeDate);
									//String chargedDateTrunc = BaseUtils.formatTime("yyyy-MM-dd", chargeDate);
									//String chargedDateNext = BaseUtils.formatTime("yyyy-MM-dd", BaseUtils.addTime(chargeDate, Calendar.DATE, 1));  
									//String note = "0";
									//String application = "CDR";
									//int result = 0;
									//String username = null;
									//String clientip = null;
									String expireStr = BaseUtils.formatTime("yyyy-MM-dd HH:mm:ss", expireTime);
									
									//String sql = "select 1 from his_" + dateTableHis 
									//		+ " where msisdn = '" + msisdn + "' and action = 'RENEW' and result = 0 and fee > 0 and pkg_code = '" + packageCode + "'"
									//		+ " and expire_time = '" + expireStr + "'";
									
									String sql = "select 1 from his_" + dateTableHis 
											+ " where msisdn = '" + msisdn + "' and action in ('RENEW', 'FirstREG', 'ReREG') and result = 0 and fee > 0 and pkg_code = '" + packageCode + "'"
											+ " and expire_time = '" + expireStr + "'";
														
									if(baseDAO.hasResult(transidTmp, sql) == false){
										// ko có trong db => có thể do lúc call api xịt => insert lại 
										Subscriber subs = getSubscriber(transidTmp, msisdn, packageCode);
									    if(subs != null) {
									    	int regdate = Integer.parseInt(new SimpleDateFormat("yyyyMMdd").format(subs.getActiveTime()));
											int expireint = BaseUtils.getDateDiff(subs.getExpireTime(), chargeDate);
											int activeint = BaseUtils.getDateDiff(subs.getActiveTime(), chargeDate);
											String hisnote1 = new SimpleDateFormat("yyyyMMdd").format(chargeDate); 
											String hisnote2 = null, hisnote3 = null;
											
											subs.setSubnote2("" + (BaseUtils.parseInt(subs.getSubnote2(), 0) + addValue));
											subs.setSubnote3("" + (BaseUtils.parseInt(subs.getSubnote3(), 0) + addValue));
											
											ActionHis actionHis = new ActionHis(0, msisdn, "RENEW", chargeDate, 
													fee, transidTmp, subs.getNote(), subs.getActiveChannel(), 0,
													packageCode, subs.getCpid(), expireTime, regdate, subs.getSubnote1(),
									   				subs.getSubnote2(), subs.getSubnote3(), subs.getSubnote4(), subs.getSubnote5(),
									   				expireint, activeint, hisnote1, hisnote2, hisnote3);
											// SaveActionHis.queue.put(actionHis);
											baseDAO.insertBean(transidTmp, "his_" +  new SimpleDateFormat("yyyyMM").format(chargeDate), actionHis);
											
											sql = 
												"update subscriber set " + 
													// "status = 1, " + 
													"last_renew = now(), " +
													"subnote2 = '" + subs.getSubnote2() + "', " + 
													"subnote3 = '" + subs.getSubnote3() + "', " + 
													"expire_time = '" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(expireTime) + "' " +
												 "where msisdn = '" + msisdn + "' and package_id = '" + packageCode + "'";
											ExecuteSQL.queue.put(new SqlBean(transidTmp, sql));
											
									    } else {
									    	logger.info(transidTmp + ", @@@@@@@@@@@@@@@@@ NOTE , renew but subs is not exist");
									    	String hisnote1 = new SimpleDateFormat("yyyyMMdd").format(chargeDate); 
									    	
									    	ActionHis actionHis = new ActionHis(0, msisdn, "RENEW", chargeDate, fee, transidTmp, null, channel, 0,
									   				packageCode, null, null, 0, null, 
									   				null, null, null, null,
									   				0, 0, hisnote1, null, null);
											// SaveActionHis.queue.put(actionHis);
									    	baseDAO.insertBean(transidTmp, "his_" +  new SimpleDateFormat("yyyyMM").format(chargeDate), actionHis);
											
											// trường hợp này là mất đồng bộ giữa CCSP và CP
											// => insert SUBS
											String pass = StringGenerator.randomDigits(5);
									    	String subnote1 = null, subnote2 = addValue + "", subnote3 = addValue + "", subnote4 = null, subnote5 = null;
									    	
											subs = new Subscriber(msisdn, chargeDate, expireTime, packageCode, chargeDate, null,
													null, channel, null, 1, "ReInsert", null, pass, 
													0, subnote1, subnote2, subnote3, subnote4, subnote5); 
											xbaseDAO.insertBean(transidTmp, subs); 
									    }
									} else {
										logger.info(transidTmp + ", transRenewOK: " + msisdn + "/" + expireStr + "/" + packageCode);
									}
									return true;
									
								} else if ("1".equalsIgnoreCase(type)) { // đăng ký
									String sql = "select status from subscriber where msisdn = '" + msisdn + "' and package_id = '" + packageCode + "'";
									Integer status = xbaseDAO.getFirstCell(transidTmp, sql, Integer.class);
									
									if(status == null) { // not exist DB
										logger.info(transidTmp + ", reinsert transaction register: " + msisdn + ", " + packageCode);  
										String subnote1 = null;
										String subnote2 = addValue + "";   // số tiền trừ dc của gói, ko reset khi hủy/dk lại
										String subnote3 = addValue + "";	  // số tiền trừ dc của gói, reset khi hủy/dk lại
										String subnote4 = null, subnote5 = null;
										String note = "ReInsert", cpid = null;
										String pass = StringGenerator.randomDigits(5);
										/*
										String pass = xbaseDAO.getFirstCell(transid, "select password from subscriber where msisdn = '" + msisdn + "'", String.class);
								    	if(pass == null){  
								    		pass = StringGenerator.randomDigits(5);
								    	}
								    	*/
										Subscriber subs = new Subscriber(msisdn, chargeDate, expireTime, packageCode, chargeDate, null,
												null, channel, null, 1, note, cpid, pass, 
												0, subnote1, subnote2, subnote3, subnote4, subnote5); 
										xbaseDAO.insertBean(transidTmp, subs);
										
										String actionType = fee > 0 ? "ReREG" : "FirstREG";
										int regdate = Integer.parseInt(new SimpleDateFormat("yyyyMMdd").format(chargeDate)); 
										int expireint = 0, activeint = 0;
										String hisnote1 = new SimpleDateFormat("yyyyMMdd").format(chargeDate); 
										String hisnote2 = null, hisnote3 = null;
										
										ActionHis actionHis = new ActionHis(0, msisdn, actionType, chargeDate, fee, transidTmp.split("@")[0], note, channel, 0,
								   				packageCode, cpid, expireTime, regdate, subnote1, subnote2, subnote3, subnote4, subnote5,
								   				expireint, activeint, hisnote1, hisnote2, hisnote3);
								   		// SaveActionHis.queue.put(actionHis);
								   		baseDAO.insertBean(transidTmp, "his_" +  new SimpleDateFormat("yyyyMM").format(chargeDate), actionHis);
								   		
								   		if(line.contains(": (DK)")) { 
								   			String msg = "(DK)" + line.split("\\: \\(DK\\)")[1];
								   			MtHis mt = new MtHis(0, msisdn, msg, 0, transidTmp.split("@")[0], "REG@" + packageCode, chargeDate, "SMS");
											// xbaseDAO.insertBean(transid, mt);
								   			AppUtils.insertMT(transidTmp, mt); 
								   		}
									}
								}
							} catch (Exception e){ 
								logger.info(transid + ", exception: "  + e.getMessage() + ", line: " + line);
							}
							return true;
						}
					}); 
				}
				// boolean b = f.renameTo(new File(folderBackup + f.getName()));
				boolean b = f.delete();
				logger.info(transid + ", delete file " + f.getName() + ", rs: " + b);
			}
			return counter.get();
		}
		
		public static Subscriber getSubscriber(String transid, String msisdn, String pkgCode){
			String sql = "select * from subscriber where msisdn = '" + msisdn + "' and package_id = '" + pkgCode + "'";
			return xbaseDAO.getBeanBySql(transid, Subscriber.class, sql);
		}

		@Override
		public int sleep() {
			return 10000;
		}

		@Override
		public void exception(Throwable e) {
		}
}
