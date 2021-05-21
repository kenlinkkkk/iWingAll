package com.ftp.processor;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import com.ftp.connection.FtpClientImpl;
import com.ftp.connection.IFtpClient;
import com.ftp.connection.SftpClientImpl;
import com.ftp.entities.FtpServerInfo;
import com.ftp.entities.ItemMap;
import com.ftp.entities.MyFtpFile;
import com.ftp.utils.ExtractFileUtils;
import com.ftp.utils.FTPFileComparator;
import com.ftp.utils.XmlConfigs;
import com.ligerdev.appbase.utils.BaseUtils;
import com.ligerdev.appbase.utils.textbase.Log4jLoader;
import com.ligerdev.appbase.utils.textbase.ReqCountUtils;

public class FTPIntergration {

	private IFtpClient ftpClient = null;
	private static Logger logger = Logger.getLogger(FTPIntergration.class);
	private static Logger loggerCDR = Logger.getLogger("CDR_EXPORT");
	private static ReqCountUtils countUtils = ReqCountUtils.getInstance("FTPIntergration", "F");
	private String protocol;
	
	public FTPIntergration(String protocol) {
		if("sftp".equalsIgnoreCase(protocol)){
			logger.info("**************** Init SFTP protocol *****************");
			ftpClient = new SftpClientImpl();
		} else {
			logger.info("**************** Init FTP protocol *****************");
			ftpClient = new FtpClientImpl();
		}
		this.protocol = protocol;
	}
	
	public boolean connect(int timeout, String transid, String ip, int port, String user, String pass, 
			String localMode, String remoteMode, int connectTimeout, int bufferSize) {
		boolean connected = false;
        try {
        	logger.info(transid + ", ip = " + ip + ", port = " + port + ", user = " 
        			+ user + ", pass = " + pass + ", localMode = " + localMode + ", bufferSize = " + bufferSize
        			+ ", remoteMode = " + remoteMode + ", timeout =  " + connectTimeout);
        	
        	// ftpClient.setConnectTimeout(connectTimeout);
        	// ftpClient.setDataTimeout(connectTimeout);
        	ftpClient.setDefaultTimeout(transid, timeout, connectTimeout);
        	if(bufferSize > 0){
        		ftpClient.setBufferSize(transid, timeout, bufferSize);
        	}
        	// ftpClient.setControlKeepAliveReplyTimeout(connectTimeout);
        	// ftpClient.setSoTimeout(connectTimeout);
        	// ftpClient.setControlKeepAliveTimeout(connectTimeout);
            try {
            	logger.info(transid + ", call connect method, protocol = " + protocol);
            	ftpClient.connect(transid, timeout, ip, port);
			} catch (Exception e) {
				try {
					ftpClient.disconnect(transid, timeout);
				} catch (Exception e2) {
				}
				throw e;
			}
            try {
            	logger.info(transid + ", call login method...");
            	connected = ftpClient.login(transid, timeout, user, pass);
            	if(!connected){
            		throw new Exception("login failure exception");
            	}
			} catch (Exception e) {
				try {
					ftpClient.logout(transid, timeout);
				} catch (Exception e2) {
				}
				try {
					ftpClient.disconnect(transid, timeout);
				} catch (Exception e2) {
				}
				throw e;
			}
        	if(FTP_ACTIVE_MODE.equalsIgnoreCase(localMode)){
        		ftpClient.enterLocalActiveMode(transid, timeout);
        	}
        	if(FTP_PASSIVE_MODE.equalsIgnoreCase(localMode)){
        		ftpClient.enterLocalPassiveMode(transid, timeout);
        	}
        	if(FTP_ACTIVE_MODE.equalsIgnoreCase(remoteMode)){
        		// ftpClient.enterRemoteActiveMode(...);
        	}
        	if(FTP_PASSIVE_MODE.equalsIgnoreCase(remoteMode)){
        		ftpClient.enterRemotePassiveMode(transid, timeout);
        	}
            logger.info(transid + ", connected = " + connected);
        } catch (Exception e) {
        	logger.info(transid + ", Exception: " + e.getMessage(), e);
        }
        return connected;
    }
	
	public boolean connectionOK(int timeout, String transid) throws Exception{
		return ftpClient.isAvailable(transid, timeout) && ftpClient.isConnected(transid, timeout); 
	}
    
	public void disconnect(int timeout, String transid) {
		try {
			logger.info(transid + ", call logout method...");
			ftpClient.logout(transid, timeout);
			logger.info(transid + ", logout successfully");
			
		} catch (Exception e) {
			logger.info(transid + ", msg: " + e.getMessage());
		}
        try {
        	logger.info(transid + ", call disconnect method...");
            ftpClient.disconnect(transid, timeout);
            logger.info(transid + ", disconnect successfully");
            
        } catch (Exception e) {
        	logger.info(transid + ", msg: " + e.getMessage());
        }
    }
	
	public static String FTP_PASSIVE_MODE = "passive";
	public static String FTP_ACTIVE_MODE = "active";
	
	public static String ACTION_DELETE = "delete";
	public static String ACTION_BACKUP = "backup";
	
	private void createFoldersIfNotExist(String transid, String folderPath){
		if(BaseUtils.isBlank(folderPath) || folderPath.startsWith("#")){
			return;
		}
		File fileLocal = new File(folderPath);
		if(!fileLocal.exists()){
			boolean temp = fileLocal.mkdirs();
			logger.info(transid + ", folderPath = " + folderPath + " does not exist => mkdir dirs, result = " + temp);
		}
	}
	
	public static void main(String[] args) throws Exception {
		Log4jLoader.init();
		XmlConfigs.init(XmlConfigs.class);
		FTPIntergration f = new FTPIntergration("sftp");
		logger.warn("==========================");
		
		FtpServerInfo ftpCfg = XmlConfigs.LIST_CONNECTIONS.get(0);
		f.connect(ftpCfg.getConnectTimeout(), "Test", ftpCfg.getIp(), ftpCfg.getPort(), ftpCfg.getUser(),
				ftpCfg.getPass(), ftpCfg.getLocalMode(), ftpCfg.getRemoteMode(), 
				ftpCfg.getConnectTimeout(), 1024);
		String date = BaseUtils.formatTime("yyyyMM", new Date());
		
		f.download("test", ftpCfg.getItemMaps().get(0), date, ftpCfg);
		f.disconnect(2000, "test");
	}
	
	public int download(String transid, ItemMap itemMap, String dateFolder, FtpServerInfo ftpCfg) throws Exception{ 
		
		String remotePathDate = itemMap.getServerPath().replace("$time", dateFolder);
		String localPathDate = itemMap.getLocalPath().replace("$time", dateFolder);
		createFoldersIfNotExist(transid, localPathDate);
		createFoldersIfNotExist(transid, itemMap.getCopyLocalTo());
		 
		logger.info(transid + ", ---------------------------- \n");
		int downloadedCount = 0;
		MyFtpFile files[] = ftpClient.listFiles(transid, itemMap.getTimeoutDownload(), remotePathDate); 
		
		if(files == null || files.length == 0){
			if(ftpClient.changeWorkingDirectory(transid, itemMap.getTimeoutDownload(), remotePathDate) == false){
				logger.warn(transid + ", !!!!!!!!!!!!!!!!!!!!!!!!!!! path does not exist, remotePath = " + remotePathDate);
			} else {
				logger.info(transid + ", list files: remotePath = " + remotePathDate + ", localPath = " + localPathDate + ", listSize is blank");
			}
			return 0;
		}
		Arrays.sort(files, new FTPFileComparator());
		logger.info(transid + ", list files: remotePath = " + remotePathDate + ", localPath = " + localPathDate + ", listSize = " + files.length);
		
		int totalFoundFile = 0;
		String lastFileName = null, firstFileName = null;
		boolean showFileInfo = new File("showFileInfo.cmd").exists();
		
		for (int i = 0; files != null &&  i < files.length; i++) {
			long l1 = System.currentTimeMillis();
			
			if(showFileInfo){
				if(files[i].getFtpFile() == null){ // nếu là sftp sẽ = null 
					logger.info(transid + ", " + i + ": " +  files[i].getName() + ", type: " + files[i].getType());
				} else {
					logger.info(transid + ", " + i + ": " +  files[i].getName() 
							+ ", size: " + files[i].getFtpFile().getSize()
							+ ", isDir: " + files[i].getFtpFile().isDirectory());
				}
			}
			if(files[i].getType() != 0){ // not a file
				logger.info(transid + ", ignore folder name: " + files[i].getName());
				continue;
			}  
			if(lastFileName == null || lastFileName.compareTo(files[i].getName()) < 0){
				lastFileName = files[i].getName();
			}
			if(firstFileName == null || firstFileName.compareTo(files[i].getName()) > 0){
				firstFileName = files[i].getName();
			} 
			boolean isMatchFileName = true;
			if(BaseUtils.isNotBlank(itemMap.getFilePattern())){
				// Pattern r = Pattern.compile(itemMap.getFilePattern());
			    // Matcher m = r.matcher(files[i].getName());
			    
			    if(BaseUtils.isBlank(itemMap.getFileNotMatch())){
			    	if(files[i].getName().matches(itemMap.getFilePattern()) == false){
			    		logger.debug(transid + ", !!!!!!!!!!!!!! ignore file: " + files[i].getName() + ", it's not match to config regex");
						continue;
				    }
			    } else {
			    	if(files[i].getName().matches(itemMap.getFilePattern()) == false){
			    		localPathDate = itemMap.getFileNotMatch().replace("$time", dateFolder);
			    		createFoldersIfNotExist(localPathDate, localPathDate);
			    		isMatchFileName = false;
			    		logger.debug(transid + ", !!!!!!!!!!!!!!!! file name: " + files[i].getName() + " is not match => download to: " + localPathDate);
				    }
			    }
			} 
			totalFoundFile ++;
			String fullRemotePath = remotePathDate + files[i].getName();
			String fullLocalPath = localPathDate + itemMap.getLocalPrefix() + files[i].getName();
			
			if(	new File(fullLocalPath).exists()					// tồn tại ở local file giống hệt (file trên server có thể là gz hoặc ko)
					|| new File(fullLocalPath + ".gz").exists()		// tồn tại ở local file cùng tên nhưng đã zip thành gz (server ko gz)
					|| (fullLocalPath.endsWith(".gz") && new File(fullLocalPath.replace(".gz", "")).exists()) // ở server có gz, ở local thì ko gz 
			){
				// các TH trong if đều là đã download file 
				// logger.info(transid + ", file = "  + files[i].getName() + " already exist on local");
				continue;
			}
			transid = transid.split("@")[0] + "@" + countUtils.countLongStr(); 
			Date today = new Date();
			
			if(BaseUtils.isNotBlank(dateFolder) && dateFolder.equals(BaseUtils.formatTime(itemMap.getFolderDateFormat(), today))){
				// nếu đang scan ngày hiện tại, kiểm tra trên local của ngày hôm qua xem có file hay không
				// nếu có sẽ ko download (vì 1 số server bị export file nhầm ngày ở thời điểm giao ngày)
				String yesterdayFolder = BaseUtils.formatTime(itemMap.getFolderDateFormat(), BaseUtils.addTime(today, Calendar.DATE, -1)); 
				
				String localPathDateYesterday = itemMap.getLocalPath().replace("$time", yesterdayFolder)
						+ ftpCfg.getSeparator() + itemMap.getLocalPrefix() + files[i].getName();
				
				if(new File(localPathDateYesterday).exists()){
					logger.info(transid + ", found new file on server = " + fullRemotePath 
							+ " | but it's already exist on local on: " + localPathDateYesterday + " => ignore download");
					continue;
				}
			}
			logger.info(transid + ", found a new file on server, file = " + fullRemotePath);
			try {	
				String writingExtension = ".writing";
				File fileTemp = new File(fullLocalPath + writingExtension);
				
				if(fileTemp.exists()){
					logger.info(transid + ", already exist file: " + (fullLocalPath + writingExtension) + " => delete to re-download");
					fileTemp.delete();
				}
				FileOutputStream out = null;
				boolean res = false;
				try {
					out = new FileOutputStream(fullLocalPath + writingExtension);
					res = ftpClient.retrieveFile(transid, itemMap.getTimeoutDownload(), fullRemotePath, out);
					
				} catch (Exception e) {
					logger.info(transid + ", Exception: " + e.getMessage());
				} finally {
					try {
						out.close();
					} catch (Exception e2) {
					}
				}
				if(res == false){
					logger.warn(transid + ", !!!!!!!!!!!!!!! download file fail, file = "  + files[i].getName());
					continue;
				}
				File fileOutput = new File(fullLocalPath + writingExtension);
				if(fileOutput.exists() == false){
					// download ko thành công
					logger.warn(transid + ", !!!!!!!!!!!!!!! save file error, file = "  + files[i].getName());
					continue;
				}
				String newName = fileOutput.getAbsolutePath().replace(writingExtension, "");
				boolean rs = fileOutput.renameTo(new File(newName));

				if(rs == false){
					logger.warn(transid + ", !!!!!!!!!!!!!! can not move from writing file to normal, file = " + fileOutput.getAbsolutePath());
					continue;
				}
				logger.info(transid + ", download success, file = "  + files[i].getName());
				
				if(isMatchFileName){
					File downloadedFile = new File(newName);
					File folderDest = new File(itemMap.getCopyLocalTo());
					// FileUtils.copyFile(newFile, dest); 
					copy2ReadyFolder(transid, downloadedFile, folderDest, itemMap);
					logCDR(transid, downloadedFile.length(), fullRemotePath, fullLocalPath, System.currentTimeMillis() - l1);
				}
				downloadedCount ++;
				
				// xóa file trên server nếu cấu hình del (và acc phải có quyền)
				if(ACTION_DELETE.equalsIgnoreCase(itemMap.getActionOnServer())){
					boolean del = ftpClient.deleteFile(transid, itemMap.getTimeoutDownload(), fullRemotePath);
					logger.info(transid + ", delete file on server, delResult = " + del + ", delete from: " + fullRemotePath);
					
				} else if(ACTION_BACKUP.equalsIgnoreCase(itemMap.getActionOnServer())){
					String backupFolder = itemMap.getMoveOnServer().replace("$time", dateFolder);
					
					if(ftpClient.changeWorkingDirectory(transid, itemMap.getTimeoutDownload(), backupFolder) == false){
						boolean mkdir = ftpClient.makeDirectory(transid, itemMap.getTimeoutDownload(), backupFolder);
						if(!mkdir) {
							logger.warn(transid + ", !!!!!!!!!!!!!!!!! folder backup does not exist, backupFolder = " + backupFolder);
							continue;
						}
					}
					String backupPath = backupFolder + files[i].getName();
					boolean move = ftpClient.rename(transid, itemMap.getTimeoutDownload(), fullRemotePath, backupPath);
					logger.info(transid + ", move file on server, moveResult = " + move 
							+ ", move from: " + fullRemotePath + ", to: " + backupPath);
				}
			} catch (Exception e) {
				logger.info(transid + ", Exception: " + e.getMessage(), e);
			}  
		}
		logger.info(transid
				+ ", firstFile = " + firstFileName 
				+ ", lastFile = " + lastFileName 
				+ ", totalFound = " + totalFoundFile
				+ ", totalDownloaded = " + downloadedCount
		);
		return downloadedCount;
	}
	
	private void copy2ReadyFolder(String transid, File fileSource, File folderDest, ItemMap itemMap) throws Exception{ 

		String type = null;
		if(itemMap.isExtract()){
			
			String types[] = {"gz", "tar", "rar", "zip"};
			for(String tmp : types){
				if(tmp.equals(itemMap.getExtractType())){
					type = tmp;
					break;
				}
			}
			if(type == null){
				for(String tmp : types){
					if(fileSource.getName().endsWith("." + tmp)){
						type = tmp;
						break;
					}
				}
			}
		}
		if(type == null){
			File copyingFile = new File(folderDest.getAbsolutePath() + File.separator + fileSource.getName() + ".copying");
			FileUtils.copyFile(fileSource, copyingFile);
			copyingFile.renameTo(new File(copyingFile.getAbsolutePath().replace(".copying", "")));  
			return;
		}
		String folderTmp = "EXT_" + BaseUtils.nomalizeString(itemMap.getLogName()) + "_" + System.currentTimeMillis();
		File folder = new File(folderTmp);
		folder.mkdirs();
		try {
			if("gz".equals(type)){
				 try {
					  ExtractFileUtils.unGZip(fileSource.getAbsolutePath(), folderTmp);
					   logger.info(transid + ", extract file success, fileName: " + fileSource.getName());
					} catch (Exception e) {
						throw new Exception("Can not extract file: " + fileSource.getName() + ", invalid file");
					}
			} else if("zip".equals(type)){
				if(BaseUtils.isBlank(itemMap.getExtractPass())){
					  try {
						 	ExtractFileUtils.unZip(fileSource.getAbsolutePath(), folderTmp);
							logger.info(transid + ", extract file success, fileName: " + fileSource.getName());
						} catch (Exception e) {
							throw new Exception("Can not extract file: " + fileSource.getName() + ", invalid file");
						}
				} else {
					try {
						ExtractFileUtils.unZip(fileSource.getAbsolutePath(), folderTmp, itemMap.getExtractPass());
						logger.info(transid + ", extract file success, fileName: " + fileSource.getName());
					} catch (Exception e) {
						throw new Exception("Can not extract file: " + fileSource.getName() + ", wrong password or invalid file");
					}
				}
			} else if("tar".equals(type)){
				try {
					ExtractFileUtils.unTar(fileSource.getAbsolutePath(), folderTmp);
					 logger.info(transid + ", extract file success, fileName: " + fileSource.getName());
				} catch (Exception e) {
					throw new Exception("Can not extract file: " + fileSource.getName() + ", invalid file");
				}
			} else if("rar".equals(type)){
				if(BaseUtils.isBlank(itemMap.getExtractPass())){
					try {
						 ExtractFileUtils.unRar(fileSource.getAbsolutePath(), folderTmp);
						 logger.info(transid + ", extract file success, fileName: " + fileSource.getName());
					} catch (Exception e) {
						throw new Exception("Can not extract file: " + fileSource.getName() + ", invalid file");
					}
				} else {
					try {
						ExtractFileUtils.unRar(fileSource.getAbsolutePath(), folderTmp, itemMap.getExtractPass());
						logger.info(transid + ", extract file success, fileName: " + fileSource.getName());
					} catch (Exception e) {
						throw new Exception("Can not extract file: " + fileSource.getName() + ", wrong password or invalid file");
					}
				}
			}  
			File files[] = folder.listFiles();
			
			if(files != null && files.length > 0){
				for(File file : files){
					if(file.isFile()){
						File copyingFile = new File(folderDest.getAbsolutePath() + File.separator + file.getName() + ".copying");
						FileUtils.copyFile(file, copyingFile);
						copyingFile.renameTo(new File(copyingFile.getAbsolutePath().replace(".copying", "")));  
					} else {
						FileUtils.copyDirectory(file, new File(folderDest.getAbsolutePath() + File.separator + file.getName()));
					}
				}
			}
		} catch (Exception e) {
			throw e;
		} finally {
			FileUtils.deleteDirectory(folder);
		}
	}
	
	public void logCDR(String transid, long length, String serverPath, String localPath, long time) {
		try {
			String str = BaseUtils.formatTime("yyyy-MM-dd HH:mm:ss.SSS", System.currentTimeMillis()) 
					+ "|transid="  + transid + "|length=" + length + "|server=" + serverPath 
					+ "|local=" + localPath + "|time=" + time;
			loggerCDR.info(str);
			
		} catch (Exception e) {
			logger.info("Exception: " + e.getMessage());
		}
	}

	public IFtpClient getFtpClient() {
		return ftpClient;
	} 
}
