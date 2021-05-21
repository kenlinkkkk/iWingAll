package com.xxx.hula.processor;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.ligerdev.appbase.utils.db.BaseDAO;
import com.ligerdev.appbase.utils.db.XBaseDAO;
import com.ligerdev.appbase.utils.queues.MsgQueue;
import com.ligerdev.appbase.utils.threads.AbsProcessor;
import com.xxx.aps.logic.db.orm.ActionHis;
import com.xxx.aps.utils.AppUtils;
import com.xxx.hula.logic.db.orm.Ctkm1His;
import com.xxx.hula.logic.db.orm.Ctkm1Score;

public class Ctkm1UpdateScore extends AbsProcessor {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static MsgQueue queue = new MsgQueue("Ctkm1UpdateScore"); // Ctkm1His
	public static XBaseDAO xbaseDAO = XBaseDAO.getInstance("main");
	public static BaseDAO baseDAO = BaseDAO.getInstance("main");

	public static void addScore(ActionHis bean) {
		try {
			if("FirstREG".equalsIgnoreCase(bean.getAction()) 
					|| "ReREG".equalsIgnoreCase(bean.getAction())
					|| "RENEW".equalsIgnoreCase(bean.getAction())
			) {
				int xmonth = getXMonth(bean.getTransId());
				
				if(xmonth >= 5) {
					return;
				}
				Date now = new Date();
				int xdate = Integer.parseInt(new SimpleDateFormat("yyyyMMdd").format(now)); 
				int xweek = Integer.parseInt(AppUtils.getMondayOfWeek(now, "yyyyMMdd")); 
				int scoreAdd = bean.getAction().equalsIgnoreCase("FirstREG") ? 1000 : 100;
				
				Ctkm1His score = new Ctkm1His(0, bean.getMsisdn(), bean.getAction(), scoreAdd, 
						bean.getTransId(), xdate, xweek, xmonth, bean.getPkgCode(), new Date(), null, null);
				Ctkm1UpdateScore.queue.put(score);
			} 
		} catch (Exception e) { 
			logger.info(bean.getTransId() + ", Exception: " + e.getMessage(), e); 
		}
	}
	
	public static int getXMonth(String transid) {
		int xmonth = 5;
		try {
			Date now = new Date();
			
			/*if(now.compareTo(new SimpleDateFormat("HH:mm:ss dd/MM/yyyy").parse("23:59:59 14/12/2018")) <= 0) {
				xmonth = 1;
			} else if(now.compareTo(new SimpleDateFormat("HH:mm:ss dd/MM/yyyy").parse("23:59:59 13/01/2019")) <= 0) {
				xmonth = 2;
			} else if(now.compareTo(new SimpleDateFormat("HH:mm:ss dd/MM/yyyy").parse("23:59:59 12/02/2019")) <= 0) {
				xmonth = 3;
			} else if(now.compareTo(new SimpleDateFormat("HH:mm:ss dd/MM/yyyy").parse("23:59:59 12/03/2019")) <= 0) {
				// chỉ kéo dài 3 tháng, tháng 4 thừa vài ngày
				xmonth = 4;
			}*/
			if(now.compareTo(new SimpleDateFormat("HH:mm:ss dd/MM/yyyy").parse("23:59:59 21/12/2018")) <= 0) {
				xmonth = 1;
			} else if(now.compareTo(new SimpleDateFormat("HH:mm:ss dd/MM/yyyy").parse("23:59:59 20/01/2019")) <= 0) {
				xmonth = 2;
			} else if(now.compareTo(new SimpleDateFormat("HH:mm:ss dd/MM/yyyy").parse("23:59:59 19/02/2019")) <= 0) {
				xmonth = 3;
			} else if(now.compareTo(new SimpleDateFormat("HH:mm:ss dd/MM/yyyy").parse("23:59:59 19/03/2019")) <= 0) {
				// chỉ kéo dài 3 tháng, tháng 4 thừa vài ngày
				xmonth = 4;
			}
		} catch (Exception e) {
			logger.info(transid + ", Exception: " + e.getMessage(), e); 
		}
		return xmonth;
	}
	
	
	@Override
	public void execute() throws Exception {
		// TODO Auto-generated method stub
		Ctkm1His bean = (Ctkm1His) queue.take();
		transid = bean.getTransid();
		
		xbaseDAO.insertBean(bean.getTransid(), bean);
		updateScore("D", bean, bean.getXdate());
		updateScore("W", bean, bean.getXweek());
		updateScore("M", bean, bean.getXmonth());
	}
	
	private void updateScore(String type, Ctkm1His bean, int xtime) {
		String sql =
				"update ctkm1_score set" 
				+ " score = score + " + bean.getScore()
				+ ", note1 = '" + bean.getNote1() + "'"
				+ ", transid = '" + bean.getTransid() + "'"
				+ ", lastupdate = now()" 
				+ " where msisdn = '" + bean.getMsisdn() + "'"
				+ " and type = '" + type + "'"
				+ " and xtime = " + xtime
			;
		int rs = xbaseDAO.execSql(bean.getTransid(), sql);
		
		if(rs <= 0) {
			Ctkm1Score score  = new Ctkm1Score(0, bean.getMsisdn(), type, bean.getNote1(), 
					bean.getScore(), transid, xtime, new Date(), new Date(), new Date());
			xbaseDAO.insertBean(bean.getTransid(), score);
		}
	}

	@Override
	public int sleep() {
		return 0;
	}

	@Override
	public void exception(Throwable e) {
	} 
}
