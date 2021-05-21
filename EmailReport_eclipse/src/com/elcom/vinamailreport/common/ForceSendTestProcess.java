package com.elcom.vinamailreport.common;

import com.elcom.vinamailreport.Main;

import java.io.*;
import java.util.Date;

public class ForceSendTestProcess implements Runnable {

    private static final ForceSendTestProcess instance = new ForceSendTestProcess();
    private boolean isRun = false;
    private final File file = new File("sendMail.test.cmd");
    private final File fileBackup = new File("sendMail.test.cmd.bk");

    public static ForceSendTestProcess getInstance() {
        return instance;
    }

    private ForceSendTestProcess() {
        isRun = true;
    }

    @Override
    public void run() {
        while (isRun) {
            try {
                if (file.exists()) {
                    String tos = readLine(file);
                    
                    if (file.renameTo(fileBackup) && tos != null && !tos.isEmpty()) {
                        // send mail o day
                        Main.getInstance().logger.infor("=============Force send mail test now " + new Date() + "=============");

                        if (Main.getInstance().m != null)
                            if (Main.getInstance().m.send(tos)) {
                                Main.getInstance().logger.infor("Sent mail test report successfully");
                            } else {
                                Main.getInstance().logger.infor("Sent mail test report failed");
                            }
                    }
                }
            } catch (Exception e) {
                Main.getInstance().logger.error("Force send mail error " + e.toString(), e);
            } finally {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void stop() {
        this.isRun = false;
    }

    private String readLine(File file) throws IOException {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String line = reader.readLine();
            return line;
        } finally {
            if (reader != null)
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
    }
}
