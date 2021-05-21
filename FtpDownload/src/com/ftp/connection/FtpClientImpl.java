package com.ftp.connection;

import java.io.FileOutputStream;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.log4j.Logger;

import com.ftp.entities.MyFtpFile;
import com.ligerdev.appbase.utils.BaseUtils;
import com.ligerdev.appbase.utils.ITimoutHandler;

public class FtpClientImpl implements IFtpClient {

	private static Logger logger = Logger.getLogger(FtpClientImpl.class);
	private FTPClient ftpClient = new FTPClient();
	
	public FtpClientImpl() {
	} 
	
	public boolean makeDirectory(final String transid, int timeout, final String pathname) throws Exception {
		return BaseUtils.invokeWithTimeout(transid, true, timeout, new ITimoutHandler<Boolean>() {
			@Override 
			public Boolean execute() throws Exception {
				return  ftpClient.makeDirectory(pathname);
			}
		});
	}
	
	public boolean changeWorkingDirectory(final String transid, int timeout, final String pathname) throws Exception {
		return BaseUtils.invokeWithTimeout(transid, true, timeout, new ITimoutHandler<Boolean>() {
			@Override 
			public Boolean execute() throws Exception {
				if(BaseUtils.isBlank(pathname)){
					return true;
				}
				return  ftpClient.changeWorkingDirectory(pathname);
			}
		});
	}

	public void setDefaultTimeout(final String transid, int timeout, final int connectTimeout) throws Exception {
		BaseUtils.invokeWithTimeout(transid, true, timeout, new ITimoutHandler<Boolean>() {
			@Override 
			public Boolean execute() throws Exception {
				 ftpClient.setDefaultTimeout(connectTimeout);
				 return true;
			}
		});
	}

	public void connect(final String transid, int timeout, final String ip, final int port) throws Exception { 
		BaseUtils.invokeWithTimeout(transid, true, timeout, new ITimoutHandler<Boolean>() {
			@Override
			public Boolean execute() throws Exception {
				 ftpClient.connect(ip, port);
				 return true;
			}
		});
	}
	
	public void setBufferSize(final String transid, int timeout, final int bufSize) throws Exception { 
		BaseUtils.invokeWithTimeout(transid, true, timeout, new ITimoutHandler<Boolean>() {
			@Override
			public Boolean execute() throws Exception {
				 ftpClient.setBufferSize(bufSize);
				 return true;
			}
		});
	}

	public void disconnect(final String transid, int timeout) throws Exception { 
		BaseUtils.invokeWithTimeout(transid, true, timeout, new ITimoutHandler<Boolean>() {
			@Override
			public Boolean execute() throws Exception {
				 ftpClient.disconnect();
				 return true;
			}
		});
	}

	public boolean login(final String transid, int timeout, final String user, final String pass) throws Exception { 
		return BaseUtils.invokeWithTimeout(transid, true, timeout, new ITimoutHandler<Boolean>() {
			@Override
			public Boolean execute() throws Exception {
				return ftpClient.login(user, pass);
			}
		});
	}

	public boolean logout(final String transid, int timeout) throws Exception { 
		return BaseUtils.invokeWithTimeout(transid, true, timeout, new ITimoutHandler<Boolean>() {
			@Override
			public Boolean execute() throws Exception {
				 return ftpClient.logout();
			}
		});
	}

	public void enterLocalActiveMode(final String transid, int timeout) throws Exception {
		BaseUtils.invokeWithTimeout(transid, true, timeout, new ITimoutHandler<Boolean>() {
			@Override
			public Boolean execute() throws Exception {
				 ftpClient.enterLocalActiveMode();
				 return true;
			}
		});
	}

	public void enterLocalPassiveMode(final String transid, int timeout) throws Exception { 
		BaseUtils.invokeWithTimeout(transid, true, timeout, new ITimoutHandler<Boolean>() {
			@Override
			public Boolean execute() throws Exception {
				 ftpClient.enterLocalPassiveMode();
				 return true;
			}
		});
	}

	public void enterRemotePassiveMode(final String transid, int timeout) throws Exception { 
		BaseUtils.invokeWithTimeout(transid, true, timeout, new ITimoutHandler<Boolean>() {
			@Override
			public Boolean execute() throws Exception {
				 ftpClient.enterRemotePassiveMode();
				 return true;
			}
		});
	}

	public boolean isAvailable(final String transid, int timeout) throws Exception { 
		return BaseUtils.invokeWithTimeout(transid, true, timeout, new ITimoutHandler<Boolean>() {
			@Override
			public Boolean execute() throws Exception {
				return ftpClient.isAvailable();
			}
		});
	}

	public boolean isConnected(final String transid, int timeout) throws Exception { 
		return BaseUtils.invokeWithTimeout(transid, true, timeout, new ITimoutHandler<Boolean>() {
			@Override
			public Boolean execute() throws Exception {
				return ftpClient.isConnected();
			}
		});
	}

	public MyFtpFile[] listFiles(final String transid, int timeout, final String remotePath) throws Exception { 
		return BaseUtils.invokeWithTimeout(transid, true, timeout, new ITimoutHandler<MyFtpFile[]>() {
			@Override
			public MyFtpFile[] execute() throws Exception {
				FTPFile files[] = null;
				if(remotePath == null || "".equalsIgnoreCase(remotePath.trim())){ 
					files = ftpClient.listFiles();  
				} else {
					files = ftpClient.listFiles(remotePath); 
				}
				if(files == null){
					return null;
				}
				if(files.length == 0){
					return new MyFtpFile[0];
				}
				MyFtpFile newList[] = new MyFtpFile[files.length];
				for(int i = 0; i < files.length; i ++){
					newList[i] = new MyFtpFile();
					newList[i].setName(files[i].getName());
					newList[i].setType(files[i].getType());  
					newList[i].setFtpFile(files[i]); 
				}
				return newList;
			}
		});
	}

	public boolean retrieveFile(final String transid, int timeout, final String fullRemotePath, final FileOutputStream out)  {
		try {
			return BaseUtils.invokeWithTimeout(transid, true, timeout, new ITimoutHandler<Boolean>() {
				@Override
				public Boolean execute() throws Exception {
					return ftpClient.retrieveFile(fullRemotePath, out);
				}
			});
		} catch (Exception e) {
			logger.info(transid + ", Exception: " + e.getMessage(), e);
		}
		return false;
	}

	public boolean deleteFile(final String transid, int timeout, final String fullRemotePath) throws Exception {
		return BaseUtils.invokeWithTimeout(transid, true, timeout, new ITimoutHandler<Boolean>() {
			@Override
			public Boolean execute() throws Exception {
				return ftpClient.deleteFile(fullRemotePath);
			}
		});
	}

	public boolean rename(final String transid, int timeout, final String fullRemotePath, final String moveTo) throws Exception {
		return BaseUtils.invokeWithTimeout(transid, true, timeout, new ITimoutHandler<Boolean>() {
			@Override
			public Boolean execute() throws Exception {
				return ftpClient.rename(fullRemotePath, moveTo); 
			}
		});
	}
}
