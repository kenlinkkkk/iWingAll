package com.xxx.aps.processor;

import java.io.File;
import java.io.FilenameFilter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.ligerdev.appbase.utils.BaseUtils;
import com.ligerdev.appbase.utils.threads.AbsProcessor;

public class MoveCdr2SubFolder extends AbsProcessor {

	// module ftpclient download về backup, nhưng ko nên để quá nhiều file trong 1 folder (ảnh hưởng tới khi list file hoặc index của disk) => move những cdr cũ vào từng folder ngày
	
	@Override
	public void execute() throws Exception {
		// TODO Auto-generated method stub
		BaseUtils.sleep(5000); 
		moveBackupFile();
	}

	@Override
	public int sleep() {
		// TODO Auto-generated method stub
		return 60 * 1000 * 30;
	}

	@Override
	public void exception(Throwable e) {
		// TODO Auto-generated method stub
	}
	
	private void moveBackupFile() {
		try {
			String folder = "/opt/kns/cdr/ccsp/backup/"; // ko đổi
			File ff = new File(folder);
			
			if(ff.exists() == false) {
				return;
			}
			File f[] = ff.listFiles();
			String mileStone = new SimpleDateFormat("yyyyMMdd").format(BaseUtils.addTime(new Date(), Calendar.DATE, -20));
			
			for (File f2 : f) {
				if(f2.isDirectory()) {
					continue;
				}
				String name = f2.getName();
				if(name.endsWith(".txt") == false && name.endsWith(".gz") == false) {
					continue;
				}
				String dateFile = f2.getName().split("_")[0];
				if(dateFile.compareTo(mileStone) > 0) {
					continue;
				}
				String folderdate = folder + dateFile + "/";
				if(new File(folderdate).exists() == false) {
					new File(folderdate).mkdirs();
				} 
				boolean rs = f2.renameTo(new File(folderdate + f2.getName()));
				logger.info("MoveFile2SubFolder: " + f2.getName() + " = " + rs);
			}
		} catch (Exception e) {
			logger.info("Exception: " + e.getMessage(), e); 
		}
	}
}
