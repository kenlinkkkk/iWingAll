package com.xxx.aps.processor;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import org.quartz.JobExecutionContext;

import com.ligerdev.appbase.utils.BaseUtils;
import com.ligerdev.appbase.utils.db.BaseDAO;
import com.ligerdev.appbase.utils.db.XBaseDAO;
import com.ligerdev.appbase.utils.quartz.AbsJob;
import com.ligerdev.appbase.utils.textbase.StringGenerator;
import com.xxx.aps.logic.db.orm.ActionHis;
import com.xxx.aps.logic.db.orm.StatsRscode;
import com.xxx.aps.logic.entity.ActivePkgBean;
import com.xxx.aps.utils.AppUtils;
import com.xxx.aps.utils.AppUtils.ActionName;

public class StatisticActive extends AbsJob {

	protected static BaseDAO baseDAO = BaseDAO.getInstance(BaseDAO.POOL_NAME_MAIN);
	protected static XBaseDAO xbaseDAO = XBaseDAO.getInstance(BaseDAO.POOL_NAME_MAIN);
	
	@Override
	protected boolean isSingleInstance() {
		return true;
	}

	@Override
	protected void execute0(JobExecutionContext ctx) throws Exception {
		if(AppUtils.isEnableNode0() == false || xbaseDAO == null){
			return;
		}
		String transid = StringGenerator.randomCharacters(10);
		try {
			executeAct(transid, ctx.getFireTime()); 
		} catch (Exception e) {
		}
		try {
			executeDT(transid, ctx.getFireTime()); 
		} catch (Exception e) {
		}
	}
	
	public static void executeDT(String transid, Date date) { 
		String thisMonth = BaseUtils.formatTime("yyyyMM", date);
		int today = Integer.parseInt(BaseUtils.formatTime("yyyyMMdd", date));
		String sql = "delete from his_" + thisMonth + " where action in ('DTNGAY', 'DTTHANG', 'CKTUAN', 'CKTHANG', 'CKLUYKE') and hisnote1 = " + today;
		xbaseDAO.execSql(transid, sql);
		sql = "delete from stats_rscode  where reason in ('DTNGAY', 'DTTHANG', 'CKTUAN', 'CKTHANG', 'CKLUYKE') and created_time = " + today;
		xbaseDAO.execSql(transid, sql);
		
		// --------------------------------------------------------------------- doanh thu ngày hôm nay
		int date_yyyyMMdd = today;
		String month_yyyyMM = thisMonth;
		sql = "select sum(fee) from his_" + month_yyyyMM + " where result = 0 and fee >= 500 and hisnote1 = " + date_yyyyMMdd;
		long fee = xbaseDAO.getFirstCell(transid, sql, Long.class);
		{
			ActionHis his1 = new ActionHis(0, "#", ActionName.DTNGAY, date, 0, transid, fee + "", "SYS",
					0, date_yyyyMMdd + "", null, null, 0, null, null, null, null, null, 0, 0, today + "", null, null);
			xbaseDAO.insertBean(transid, "his_" + thisMonth, his1);

			StatsRscode stats1 = new StatsRscode(today, "#", 0, 0, date_yyyyMMdd + "", 0, "#", ActionName.DTNGAY, "#", "#", fee + ""); 
			UpdateStatsRsCode.queue.addLast(stats1);
		}
		// --------------------------------------------------------------------- doanh thu tháng này, từ 1 tới nay
		date_yyyyMMdd = today;
		month_yyyyMM = thisMonth;
		sql = "select sum(fee) from his_" + month_yyyyMM + " where result = 0 and fee >= 500 and hisnote1 <= " + date_yyyyMMdd;
		fee = xbaseDAO.getFirstCell(transid, sql, Long.class);
		{
			ActionHis his1 = new ActionHis(0, "#", ActionName.DTTHANG, date, 0, transid, fee + "", "SYS",
					0, date_yyyyMMdd + "", null, null, 0, null, null, null, null, null, 0, 0, today + "", null, null);
			xbaseDAO.insertBean(transid, "his_" + thisMonth, his1);

			StatsRscode stats1 = new StatsRscode(today, "#", 0, 0, date_yyyyMMdd + "", 0, "#", ActionName.DTTHANG, "#", "#", fee + ""); 
			UpdateStatsRsCode.queue.addLast(stats1);
		}
		// --------------------------------------------------------------------- tỷ lệ GH
		date_yyyyMMdd = today;
		month_yyyyMM = thisMonth;
		sql = "select count(msisdn) from subscriber where status = 1 and active_time < " + today;
		long loadGH = xbaseDAO.getFirstCell(transid, sql, Long.class);
		sql = "select sum(counter) c from stats_rscode where created_time = " + today + " and reason = 'RENEW' and rs_code = 'CPS-0000'";
		long renewOK = xbaseDAO.getFirstCell(transid, sql, Long.class);
		String rate = loadGH <= 0 ? "" : BaseUtils.formatFloat(renewOK * 100f/loadGH, 1);
		{
			ActionHis his1 = new ActionHis(0, "#", ActionName.TLGH, date, 0, transid, fee + "", "SYS",
					0, date_yyyyMMdd + "", null, null, 0, loadGH + "", renewOK + "", rate, null, null, 0, 0, today + "", null, null);
			xbaseDAO.insertBean(transid, "his_" + thisMonth, his1);

			StatsRscode stats1 = new StatsRscode(today, rate, 0, 0, date_yyyyMMdd + "", 0, "#", ActionName.TLGH, loadGH + "", renewOK + "", fee + ""); 
			UpdateStatsRsCode.queue.addLast(stats1);
		}
				
		// -------------------------------------------------------------------- doanh thu lũy kế tháng trước, từ ngày 1 tới ngày N tháng trước
		date_yyyyMMdd = Integer.parseInt(BaseUtils.formatTime("yyyyMMdd", BaseUtils.addTime(date, Calendar.MONTH, -1)));
		month_yyyyMM = BaseUtils.formatTime("yyyyMM", BaseUtils.addTime(date, Calendar.MONTH, -1));
		sql = "select sum(fee) from his_" + month_yyyyMM + " where result = 0 and fee >= 500 and hisnote1 <= " + date_yyyyMMdd;
		fee = xbaseDAO.getFirstCell(transid, sql, Long.class);
		{
			ActionHis his1 = new ActionHis(0, "#", ActionName.CKLUYKE, date, 0, transid, fee + "", "SYS",
					0, date_yyyyMMdd + "", null, null, 0, null, null, null, null, null, 0, 0, today + "", null, null);
			xbaseDAO.insertBean(transid, "his_" + thisMonth, his1);

			StatsRscode stats1 = new StatsRscode(today, "#", 0, 0, date_yyyyMMdd + "", 0, "#", ActionName.CKLUYKE, "#", "#", fee + ""); 
			UpdateStatsRsCode.queue.addLast(stats1);
		}
		// --------------------------------------------------------------------- cùng kỳ tuần (doanh thu 1 ngày, ngày này tuần trước)
		Date date7Obj = BaseUtils.addTime(date, Calendar.DATE, -7);
		month_yyyyMM = BaseUtils.formatTime("yyyyMM", date7Obj);
		date_yyyyMMdd = Integer.parseInt(BaseUtils.formatTime("yyyyMMdd", date7Obj));
		sql = "select sum(fee) from his_" + month_yyyyMM + " where result = 0 and fee >= 500 and hisnote1 = " + date_yyyyMMdd;
		fee = xbaseDAO.getFirstCell(transid, sql, Long.class);
		{
			ActionHis his1 = new ActionHis(0, "#", ActionName.CKTUAN, date, 0, transid, fee + "", "SYS",
					0, date_yyyyMMdd + "", null, null, 0, null, null, null, null, null, 0, 0, today + "", null, null);
			xbaseDAO.insertBean(transid, "his_" + thisMonth, his1);

			StatsRscode stats1 = new StatsRscode(today, "#", 0, 0, date_yyyyMMdd + "", 0, "#", ActionName.CKTUAN, "#", "#", fee + ""); 
			UpdateStatsRsCode.queue.addLast(stats1);
		}
		// -------------------------------------------------------------------- cùng kỳ tháng (doanh thu 1 ngày, ngày này tháng trước)
		Date date30Obj = BaseUtils.addTime(date, Calendar.MONTH, -1);
		month_yyyyMM = BaseUtils.formatTime("yyyyMM", date30Obj);
		date_yyyyMMdd = Integer.parseInt(BaseUtils.formatTime("yyyyMMdd", date30Obj));
		sql = "select sum(fee) from his_" + month_yyyyMM + " where result = 0 and fee >= 500 and hisnote1 = " + date_yyyyMMdd;
		fee = xbaseDAO.getFirstCell(transid, sql, Long.class);
		{
			ActionHis his1 = new ActionHis(0, "#", ActionName.CKTHANG, date, 0, transid, fee + "", "SYS",
					0, date_yyyyMMdd + "", null, null, 0, null, null, null, null, null, 0, 0, today + "", null, null);
			xbaseDAO.insertBean(transid, "his_" + thisMonth, his1);

			StatsRscode stats1 = new StatsRscode(today, "#", 0, 0, date_yyyyMMdd + "", 0, "#", ActionName.CKTHANG, "#", "#", fee + ""); 
			UpdateStatsRsCode.queue.addLast(stats1);
		}
	} 
	
	public static void executeAct(String transid, Date date) {
		Date nextDate = BaseUtils.addTime(date, Calendar.DATE, 1);
		nextDate = BaseUtils.truncDate(nextDate);
		//String thisMonth = BaseUtils.formatTime("yyyyMM", date);
		//String thisMonth2 = BaseUtils.formatTime("yyyyMM", nextDate);
		
		int today = Integer.parseInt(BaseUtils.formatTime("yyyyMMdd", date));
		int nextDay = Integer.parseInt(BaseUtils.formatTime("yyyyMMdd", nextDate)); 
		logger.info(transid + ", ########### execute active count: " + today); 
		
		String sql = "select count(msisdn) counter, active_channel, package_id, cpid from subscriber where status = 1 group by active_channel, package_id, cpid";
		ArrayList<ActivePkgBean> listBean = null;
		try {
			listBean = baseDAO.getListBySql(transid, ActivePkgBean.class, sql, null, null);
		} catch (Exception e) {
		}
		if(listBean == null || listBean.size() == 0) {
			logger.info(transid + ", listSize = 0");
			return;
		}
		logger.info(transid + ", listSize = " + listBean.size());
		
		for(ActivePkgBean a : listBean) { 
			/*StatsResultCharge stats = new StatsResultCharge(today, "SYS", -1, a.getActiveChannel(), transid, 0, a.getPackageId(), ActionName.ACTIVE);
			stats.setCounter(a.getCounter()); 
			UpdateStatsRsCode.queue.addLast(stats);*/
			
			StatsRscode stats1 = new StatsRscode(today, "SYS", a.getCounter(), -1, a.getActiveChannel(), 0,  a.getPackageId(), "ACTIVE", a.getCpid(), "#", "#");   
			UpdateStatsRsCode.queue.put(stats1);
			
			StatsRscode stats2 = new StatsRscode(nextDay, "SYS", a.getCounter(), -1, a.getActiveChannel(), 0,  a.getPackageId(), "ACTIVE2", a.getCpid(), "#", "#");   
			UpdateStatsRsCode.queue.put(stats2);
		}
	}
}
