package com.xxx.aps.processor;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.concurrent.ConcurrentHashMap;

import com.ligerdev.appbase.utils.BaseUtils;
import com.ligerdev.appbase.utils.CounterUtils;
import com.ligerdev.appbase.utils.db.BaseDAO;
import com.ligerdev.appbase.utils.queues.MsgQueue;
import com.ligerdev.appbase.utils.threads.AbsBatchProcessor;
import com.xxx.aps.logic.db.orm.StatsRscode;
import com.xxx.aps.utils.AppUtils;
 

public class UpdateStatsRsCode extends AbsBatchProcessor<StatsRscode> {

	public static MsgQueue queue = new MsgQueue("UpdateStatsRsCode");
	private static BaseDAO baseDAO = BaseDAO.getInstance("main");
	
	public UpdateStatsRsCode() {
		super(queue);
	}
	
	/*
	CREATE TABLE `stats_rscode` (
	  `created_time` INT NOT NULL,
	  `rs_code` VARCHAR(45) NOT NULL,
	  `counter` INT NOT NULL,
	  `load_number` INT NOT NULL,
	  `group_cpid` VARCHAR(45) NOT NULL);
	*/

	@Override
	public void execute(String caller, ArrayList<StatsRscode> list) throws Exception {
		if(AppUtils.isEnableNode0() == false || baseDAO == null){
			return;
		}
		String transidMap = list.get(0).getTransid() + "-" + list.get(list.size() - 1).getTransid();
		CounterUtils<String> counter = new CounterUtils<String>();
		
		for(StatsRscode b : list){
			String key = b.getCreatedTime() 	// idx 0
				+ "@@@" + b.getGroupCpid() 		// idx 1
				+ "@@@" + b.getLoadNumber() 	// idx 2
				+ "@@@" + b.getRsCode()			// idx 3
				+ "@@@" + b.getPkgCode()		// idx 4
				+ "@@@" + b.getFee()			// idx 5
				+ "@@@" + b.getReason()			// idx 6
				
				// add 201809
				+ "@@@" + b.getCpid()			// idx 7
				+ "@@@" + b.getApplication()	// idx 8
				+ "@@@" + b.getInfo()			// idx 9
			;
			counter.put(key, b.getCounter());
		}
		ConcurrentHashMap<String, Integer> hash = counter.get();
		Enumeration<String> enums = hash.keys();
		
		while(enums.hasMoreElements()){
			String key = enums.nextElement();
			String str[] = key.split("@@@");
			
			int date = Integer.parseInt(str[0]);
			String groupCpid = str[1];
			int loadNumber = Integer.parseInt(str[2]);
			String rsCode = str[3];
			String pkgCode = str[4];
			String fee = str[5];
			String reason = str[6];
			String cpid = str[7];
			String app = str[8];
			String info = str[9];
			
			if("null".equalsIgnoreCase(cpid)) {
				cpid = "#";
			}
			if("null".equalsIgnoreCase(app)) {
				app = "#";
			}
			if("null".equalsIgnoreCase(info)) {
				info = "#";
			}
			Integer count = hash.get(key);
			String sql = 
						"update stats_rscode set counter = counter + " + count + " where " +
							"created_time = " + date + 
							" and rs_code = '" + rsCode + "'" +
							" and load_number = " + loadNumber + 
							" and group_cpid = '" + groupCpid + "'" +
							" and pkg_code = '" + pkgCode + "'" +  
							" and fee = " + fee +  
							" and reason = '" + reason + "'" + 
							" and cpid = '" + cpid + "'" + 
							" and application = '" + app + "'" + 
							" and info = '" + info + "'"
			;
			int rsUpdate = baseDAO.execSql(transidMap, sql);
			if(rsUpdate <= 0){
				StatsRscode bean = new StatsRscode(date, rsCode, count, loadNumber,
					groupCpid, BaseUtils.parseInt(fee, 0), pkgCode, reason, cpid, app, info); 
				baseDAO.insertBean(transidMap, bean);
			}
		}
	}

	@Override
	public int getTimeToUpdate() {
		return 6000;
	}

	@Override
	public int getSizeToUpdate() {
		return 500;
	}

	@Override
	public void exception(Throwable e) {
	}
}
