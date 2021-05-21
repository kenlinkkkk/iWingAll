package com.xxx.aps.processor;

import com.ligerdev.appbase.utils.queues.MsgQueue;
import com.ligerdev.appbase.utils.threads.AbsProcessor;
import com.xxx.aps.logic.db.orm.Subscriber;

public class UpdateRenew extends AbsProcessor {

	// public static MsgQueue queue = new MsgQueue("updaterenew");
	
	@Override
	public void execute() throws Exception {
		// TODO Auto-generated method stub
		// Subscriber subs = (Subscriber) queue.take();
	}

	@Override
	public int sleep() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void exception(Throwable e) {
	}

}
