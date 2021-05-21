package com.xtel.uploader;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ligerdev.appbase.utils.db.XBaseDAO;


public class EFSCRunner {

	private static  int DISCONNECT = 0;
	private static  int CONNECT = 1;
	private static  int LOGIN = 2;
	  
	private int status;
	private FtpClientWrp client;
	private EFSCConfig cfg;
	private Log logger = LogFactory.getLog(EFSCRunner.class);
	
	public static String FTP_PASSIVE_MODE = "passive";
	public static String FTP_ACTIVE_MODE = "active";

	public EFSCRunner(String cfgPath) {
		this.cfg = new EFSCConfig(cfgPath);
		this.client = new FtpClientWrp(this.cfg);
		
		client.setBufferSize(1024000);
		client.setDataTimeout(1000 * 60 * 5);
		client.setDefaultTimeout(1000 * 60 * 5); 
		try {
			client.setSoTimeout(1000 * 60 * 5);
		} catch (Exception e) {
		}
	}

	public void start() {
		try {
			Thread.sleep(10 * 1000);
		} catch (Exception e) {
		}
		while(true){
			try {
				tryUploadFile();
			} catch (Exception e) {
				this.logger.warn(MainApplication.VERSION + ", START EFSCRUNNER FAIL!", e);
			}
			try {
				logger.info(MainApplication.VERSION + ", sleep: " + cfg.getFTP_SYNC_INTERVAL_TIME() + " seconds"); 
				Thread.sleep(cfg.getFTP_SYNC_INTERVAL_TIME() * 1000);
			} catch (Exception e) {
			}
		}
	}

	private boolean tryUploadFile() throws Exception {
		try {
			if (this.status == DISCONNECT) {
				if (this.logger.isInfoEnabled()) {
					this.logger.info(MainApplication.VERSION + ", Connect to server at " 
							+ this.cfg.getFTP_DESTINATION_ADDRESS() + ":" +  this.cfg.getFTP_DESTINATION_PORT());
				}
				this.client.connect(this.cfg.getFTP_DESTINATION_ADDRESS(), this.cfg.getFTP_DESTINATION_PORT());
				this.client.setDefaultTimeout(90 * 1000);
				this.status = CONNECT;
			}
			boolean bool1;
			if (this.status == CONNECT) {
				boolean bRet = this.client.login( this.cfg.getFTP_DESTINATION_USER(), this.cfg.getFTP_DESTINATION_PASSWORD());
				
				if (this.logger.isInfoEnabled()) {
					this.logger.info(MainApplication.VERSION + ", LOGIN into server");
				}
				if (bRet) {
					this.status = LOGIN;
					
					if(FTP_ACTIVE_MODE.equalsIgnoreCase(this.cfg.getLOCAL_MODE())){
						client.enterLocalActiveMode();
			    	}
					else if(FTP_PASSIVE_MODE.equalsIgnoreCase(this.cfg.getLOCAL_MODE())){
			    		client.enterLocalPassiveMode();
			    	}
			    	//if(FTP_ACTIVE_MODE.equalsIgnoreCase(this.cfg.getREMOTE_MODE())){
			    		// client.enterRemoteActiveMode(...);
			    	//}
			    	if(FTP_PASSIVE_MODE.equalsIgnoreCase(this.cfg.getREMOTE_MODE())){
			    		try {
							client.enterRemotePassiveMode();
						} catch (IOException e) {
							e.printStackTrace();
						}
			    	}
				} else {
					this.logger.error(MainApplication.VERSION + ", ERROR! can login into server " + this.cfg.getFTP_DESTINATION_ADDRESS());
					bool1 = bRet;
					return bool1;
				}
			}
			if (this.status == LOGIN) {
				if (this.cfg.getFTP_DESTINATION_FOLDER().equalsIgnoreCase("")) {
					this.logger.error(MainApplication.VERSION + ", Remote dirs is null");
					boolean bRet1 = false;
					bool1 = bRet1;
					return bool1;
				}
				if (this.cfg.getFTP_SOURCE_FOLDER().equalsIgnoreCase("")) {
					this.logger.error(MainApplication.VERSION + ", Local dirs is null");
					boolean bRet1 = false;
					bool1 = bRet1;
					return bool1;
				}
				String remoteDir = this.cfg.getFTP_DESTINATION_FOLDER().trim();
				String localDir = this.cfg.getFTP_SOURCE_FOLDER().trim();
				String backupDir = this.cfg.getFTP_SOURCE_BACKUP_FOLDER().trim();
				
				if (!remoteDir.equalsIgnoreCase("")) {
					if (this.logger.isInfoEnabled()) {
						this.logger.info(MainApplication.VERSION + ", Change working directory to " + remoteDir);
					}
					boolean bRet1 = this.client.changeWorkingDirectory(remoteDir);
					if (!bRet1) {
						if (!this.client.makeDirectory(remoteDir)) {
							this.logger.error(MainApplication.VERSION + ", ERROR can't make directory " + remoteDir);

							bool1 = bRet1;
							return bool1;
						}
						this.client.changeWorkingDirectory(remoteDir);
					}
				} else {
					this.logger.error(MainApplication.VERSION + ", Remote dir not config");
					boolean bRet1 = false;
					bool1 = bRet1;
					return bool1;
				}
				if ((this.cfg.getFTP_SOURCE_BACKUP_OPTION() == 1) && (!backupDir.equalsIgnoreCase(""))) {
					if (this.logger.isInfoEnabled()) {
						this.logger.info(MainApplication.VERSION + ", Set backup directory to " + backupDir);
					}
					File bk_folder = new File(backupDir);
					if (!bk_folder.exists()) {
						bk_folder.mkdir();
					}
				} else {
					this.logger.warn(MainApplication.VERSION + ", Back up dir not config");
				}
				String[] strFilterpp = null;
				// nếu config là % hoặc # hoặc * thì quét all file
				if("#".equalsIgnoreCase(this.cfg.getFTP_SOURCE_FILE_TYPE()) == false 
					&& "%".equalsIgnoreCase(this.cfg.getFTP_SOURCE_FILE_TYPE()) == false
					&& "*".equalsIgnoreCase(this.cfg.getFTP_SOURCE_FILE_TYPE()) == false
				){
					if (!this.cfg.getFTP_SOURCE_FILE_TYPE().equals("")) {
						strFilterpp = this.cfg.getFTP_SOURCE_FILE_TYPE().split(";");
					}
					if (strFilterpp == null) {
						this.logger.warn(MainApplication.VERSION + ", Back up file type not config");
						return false;
					}
				}
				this.logger.info(MainApplication.VERSION + ", ######## Scan to upload");
				UploadFileFilter filter = new UploadFileFilter(strFilterpp);
				uploadTask(localDir, remoteDir, backupDir, filter);
			}
		} catch (Exception e) {
			this.logger.error("", e);
		} finally {
			if (this.client.isConnected()) {
				this.client.disconnect();
				this.status = DISCONNECT;
			}
		}
		return false;
	}

	private void uploadTask(String localDir, String remoteDir, String backupDir, UploadFileFilter filter) throws Exception {
		File fp = new File(localDir);
		if (fp.exists()) {
			doUploadFile(fp, localDir, remoteDir, backupDir, filter);

			File[] lstFile = fp.listFiles();
			if (lstFile != null) {
				Arrays.sort(lstFile, new FileComparator());
				
				for (File file : lstFile) {
					if (file.isDirectory()) {
						String newRemoteDir = remoteDir + File.separator + file.getName();
						String newLocalDir = localDir + File.separator + file.getName();
						String newBackupDir = "";
						
						if ((this.cfg.getFTP_SOURCE_BACKUP_OPTION() == 1) && (!backupDir.equals(""))) {
							newBackupDir = backupDir + File.separator + file.getName();
						}
						uploadTask(newLocalDir, newRemoteDir, newBackupDir, filter);
					}
				}
			}
		} else {
			this.logger.error(MainApplication.VERSION + ", Local dir not exist: " + localDir);
		}
	}

	private void doUploadFile(File file, String newLocalDir, String newRemoteDir, String newBackupDir, UploadFileFilter filter) throws Exception {
		File[] listfile = file.listFiles(filter);
		
		if ((listfile == null) || (listfile.length == 0)) {
			if (this.logger.isInfoEnabled()) {
				//this.logger.info(MainApplication.VERSION + ", Local dir: " + newLocalDir + " is empty file: " + filter.toString());
			}
			return;
		}
		boolean bRet = this.client.changeWorkingDirectory(newRemoteDir);
		if (!bRet) {
			this.logger.error(MainApplication.VERSION + ", ERROR can't change remote dir to: " + newRemoteDir);
			if (!this.client.makeDirectory(newRemoteDir)) {
				this.logger.error(MainApplication.VERSION + ", ERROR can't make directory " + newRemoteDir);

				return;
			}
			this.client.changeWorkingDirectory(newRemoteDir);
		} else {
			this.logger.info(MainApplication.VERSION + ", Change remote dir to: " + newRemoteDir);
		}
		Arrays.sort(listfile, new FileComparator());
		boolean uploaded = false;

		for (int i = 0; i < listfile.length; i++) {
				if (listfile[i].isFile() == false) {
					continue;
				}
				if (this.logger.isInfoEnabled()) {
					this.logger.info(MainApplication.VERSION + ", Uploading--------" + listfile[i].getName());
				}
				try {
					String[] remoteList = this.client.listNames();

					boolean fileExist = false;
					if (remoteList != null) {
						for (String existName : remoteList) {
							if (listfile[i].getName().equals(existName)) {
								fileExist = true;
								break;
							}
						}
					}
					if (fileExist) {
						try {
							if ((this.cfg.getFTP_SOURCE_BACKUP_OPTION() == 1) && (!newBackupDir.equals(""))) {
								File bk_folder = new File(newBackupDir);
								
								if (!bk_folder.exists()) {
									bk_folder.mkdir();
								}
								if (bk_folder.exists()) {
									boolean success = listfile[i] .renameTo(new File(newBackupDir, listfile[i].getName()));
									if (!success) {
										this.logger.error(MainApplication.VERSION + ", Move error file " + listfile[i].getName()
												+ " /from: " + newLocalDir + " /to: " + newBackupDir);
									}
								} else {
									this.logger.error(MainApplication.VERSION + ", Backup dir not found " + newBackupDir);
								}
							}
						} catch (Exception ex) {
							this.logger.error( MainApplication.VERSION + ", Move error file " + listfile[i].getName() + " fail!", ex);
						}
						this.logger.warn(MainApplication.VERSION + ", Uploading error file " + listfile[i].getName() + ", file exist!");
						continue;
					}
					FileInputStream fptream = new FileInputStream(listfile[i]);
					// String file_temp = listfile[i].getName() + ".temp";
					String file_temp = listfile[i].getName();
					
					if (!this.client.storeFile(file_temp, fptream)) {
						this.logger.error(MainApplication.VERSION + ", Upload Error");
						uploaded = false;
					} else {
						if (this.logger.isInfoEnabled()) {
							this.logger.info(MainApplication.VERSION + ", Uploaded OK tempfile===>" + file_temp);
							
							try {
								if(new File("config/database.cfg").exists()) {
									XBaseDAO b = XBaseDAO.getInstance("main");
									String sql = "insert into xcdr_uploader (path) values('" + listfile[i].getAbsolutePath() + "')";
									b.execSql("", sql);
								}
							} catch (Exception e) {
							}
						}
						//this.client.rename(file_temp, listfile[i].getName());
						//if (this.logger.isInfoEnabled()) {
						//	this.logger.info("Rename tempfile===>" + file_temp + "===>" + listfile[i].getName());
						//}
						uploaded = true;
					}
					fptream.close();
					
				} catch (Exception e) {
					uploaded = false;
					this.logger.error( MainApplication.VERSION + ", Upload error file " + listfile[i].getName() + " " + e.getMessage(), e);
				}
				if ((uploaded) && (this.cfg.getFTP_SOURCE_BACKUP_OPTION() == 1) && (!newBackupDir.equals(""))) {
					File bk_folder = new File(newBackupDir);
					if (!bk_folder.exists()) {
						bk_folder.mkdir();
					}
					boolean success = listfile[i].renameTo(new File(newBackupDir, listfile[i].getName()));
					if (!success) {
						this.logger.error(MainApplication.VERSION + ", Move error file " + listfile[i].getName() + " /from: " + newLocalDir + " /to: " + newBackupDir);
					}
				}
		}
		Thread.sleep(this.cfg.getFTP_SYNC_SLEEP_TIME() * 1000);
	}

	public class UploadFileFilter implements FileFilter {
		private String[] okFileExtensions = null;

		public String toString() {
			if(okFileExtensions == null) {
				return "null";
			}
			String ext = "";
			for (String ex : this.okFileExtensions) {
				ext = ext + ";" + ex;
			}
			return ext;
		}

		public UploadFileFilter(String[] paramArrayOfString) {
			this.okFileExtensions = paramArrayOfString;
		}

		public UploadFileFilter() {
		}

		public boolean accept(File file) {
			boolean fileNameCheck = false;
			if (this.okFileExtensions != null) {
				for (String extension : this.okFileExtensions) {
					if (file.getName().toLowerCase().endsWith(extension)) {
						fileNameCheck = true;
						break;
					}
					fileNameCheck = false;
				}
			} else {
				fileNameCheck = true;
			}
			return fileNameCheck;
		}
	}
}
