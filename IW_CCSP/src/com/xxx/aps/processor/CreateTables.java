package com.xxx.aps.processor;

import java.util.Calendar;
import java.util.Date;

import org.apache.log4j.Logger;

import com.ligerdev.appbase.utils.BaseUtils;
import com.ligerdev.appbase.utils.db.BaseDAO;
import com.ligerdev.appbase.utils.threads.AbsProcessor;
import com.xxx.aps.XmlConfigs;
import com.xxx.aps.utils.AppUtils;

public class CreateTables extends AbsProcessor {

	private BaseDAO baseDAO = BaseDAO.getInstance(BaseDAO.POOL_NAME_MAIN, "nolog");
	private static CreateTables instance = null;
	// public static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger("LOG");
	
	private CreateTables() {
		baseDAO.setEnableLogger(false);
	}
	
	public synchronized static CreateTables getInstance(){
		if(instance == null){
			instance = new CreateTables();
		}
		return instance;
	}
	
	@Override
	public void execute() throws Exception { 
		BaseUtils.sleep(1000 * 10);
		if(AppUtils.isEnableNode0() == false || baseDAO == null){
			return;
		}
		execute0();
	}
	
	private void execute0() throws Exception { 
		String thisTable = BaseUtils.formatTime("yyyyMM", System.currentTimeMillis());
		String nextTable = BaseUtils.formatTime("yyyyMM", BaseUtils.addTime(new Date(), Calendar.MONTH, 1)); 
		{
			String sqlCheck = "select 1 from his_" + nextTable;
			boolean checkExist = baseDAO.isValidSql("CheckTbl", sqlCheck);
			
			if(checkExist == false){
				String sqlCreate = "create table his_" + nextTable + " like his_" + thisTable;
				baseDAO.execSql("CreateTbl", sqlCreate);
				logger.info("################ " + sqlCreate);
			}
		}
		if(XmlConfigs.short_sms_table){
			thisTable = BaseUtils.formatTime("yyyy", System.currentTimeMillis());
			nextTable = BaseUtils.formatTime("yyyy", BaseUtils.addTime(new Date(), Calendar.YEAR, 1)); 
		}
		{
			String sqlCheck = "select 1 from mt_" + nextTable;
			boolean checkExist = baseDAO.isValidSql("CheckTbl", sqlCheck);
			
			if(checkExist == false){
				String sqlCreate = "create table mt_" + nextTable + " like mt_" + thisTable;
				baseDAO.execSql("CreateTbl", sqlCreate);
				logger.info("################ " + sqlCreate);
			}
		}
		{
			String sqlCheck = "select 1 from mo_" + nextTable;
			boolean checkExist = baseDAO.isValidSql("CheckTbl", sqlCheck);
			
			if(checkExist == false){
				String sqlCreate = "create table mo_" + nextTable + " like mo_" + thisTable;
				baseDAO.execSql("CreateTbl", sqlCreate);
				logger.info("################ " + sqlCreate);
			}
		}
		
	}

	@Override
	public int sleep() {
		return 1000 * 60 * 60 * 12;
	}

	@Override
	public void exception(Throwable e) {
	}
}
