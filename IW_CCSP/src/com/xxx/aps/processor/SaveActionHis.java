package com.xxx.aps.processor;

import java.util.ArrayList;

import com.ligerdev.appbase.utils.db.BaseDAO;
import com.ligerdev.appbase.utils.queues.MsgQueue;
import com.ligerdev.appbase.utils.threads.AbsBatchProcessor;
import com.xxx.aps.logic.db.orm.ActionHis;

public class SaveActionHis extends AbsBatchProcessor<ActionHis> {

	public static MsgQueue queue = new MsgQueue("actionhis");
	private BaseDAO baseDAO = BaseDAO.getInstance(BaseDAO.POOL_NAME_MAIN);
	
	public SaveActionHis() {
		super(queue);
	}
	
	@Override
	public void execute(String caller, ArrayList<ActionHis> list) throws Exception {
		execute2(caller, list);
	}
	
	public void execute2(String caller, ArrayList<ActionHis> list) throws Exception {
		String transid = list.get(0).getTransId() + "-" + list.get(list.size() - 1).getTransId();
		int rs = baseDAO.insertList(transid, list); 
		
		if(rs <= 0) {
			for(ActionHis bean : list){
				logger.info(transid + ", insertFail, bean = " + String.valueOf(bean));
			}
		}
	}

	@Override
	public int getTimeToUpdate() {
		return 3000;
	}


	@Override
	public int getSizeToUpdate() {
		return 60;
	}
	
	@Override
	public void exception(Throwable e) {
	}
}
