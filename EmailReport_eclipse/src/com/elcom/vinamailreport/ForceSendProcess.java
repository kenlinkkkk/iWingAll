package com.elcom.vinamailreport;

import java.io.File;
import java.util.Date;

public class ForceSendProcess implements Runnable {

    private static final ForceSendProcess instance = new ForceSendProcess();
    private boolean isRun = false;
    private final File file = new File("sendMail.cmd");
    private final File fileBackup = new File("sendMail.cmd.bk");

    public static ForceSendProcess getInstance() {
        return instance;
    }

    private ForceSendProcess() {
        isRun = true;
    }

    @Override
    public void run() {
        while (isRun) {
            try {
                if (file.exists()) {
                    if (file.renameTo(fileBackup)) {
                        // send mail o day
                        Main.getInstance().logger.infor("=============Force send mail now " + new Date() + "=============");

                        if (Main.getInstance().m != null)
                            if(Main.getInstance().m.send(null)){
                                Main.getInstance().logger.infor("Sent mail report successfully");
                            }else{
                                Main.getInstance().logger.infor("Sent mail report failed");
                            }
                    }
                }
            } catch (Exception e) {
                Main.getInstance().logger.error("Force send mail error " + e.toString(), e);
            }finally {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void stop(){
        this.isRun = false;
    }
}
