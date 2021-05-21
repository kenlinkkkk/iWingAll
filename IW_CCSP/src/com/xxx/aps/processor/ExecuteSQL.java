package com.xxx.aps.processor;

import com.ligerdev.appbase.utils.db.BaseDAO;
import com.ligerdev.appbase.utils.db.XBaseDAO;
import com.ligerdev.appbase.utils.queues.MsgQueue;
import com.ligerdev.appbase.utils.threads.AbsProcessor;
import com.xxx.aps.logic.entity.SqlBean;

public class ExecuteSQL extends AbsProcessor {

	public static MsgQueue queue = new MsgQueue("executesql");
	private XBaseDAO xbaseDAO = XBaseDAO.getInstance(BaseDAO.POOL_NAME_MAIN);
	
	public ExecuteSQL() {
	}

	@Override
	public void execute() throws Exception {
		SqlBean bean = (SqlBean) queue.take();
		xbaseDAO.execSql(bean.getTransid(), bean.getSql());
	}

	@Override
	public int sleep() {
		return 0;
	}

	@Override
	public void exception(Throwable e) {
	}
}
