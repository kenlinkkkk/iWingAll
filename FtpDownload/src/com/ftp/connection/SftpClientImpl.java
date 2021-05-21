package com.ftp.connection;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Vector;

import org.apache.log4j.Logger;

import com.ftp.entities.MyFtpFile;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.ligerdev.appbase.utils.BaseUtils;
import com.ligerdev.appbase.utils.ITimoutHandler;

public class SftpClientImpl implements IFtpClient {

	private static Logger logger = Logger.getLogger(SftpClientImpl.class);
	
	private int defautzTimeout = 20000;
	private Session session = null;
	private String ip = null;
	private int port;
	private ChannelSftp sftpClient = null;
	
	public SftpClientImpl() {
	} 
	
	public boolean makeDirectory(final String transid, int timeout, final String pathname) throws Exception {
		return BaseUtils.invokeWithTimeout(transid, true, timeout, new ITimoutHandler<Boolean>() {
			@Override 
			public Boolean execute() throws Exception {
				try {
					 sftpClient.mkdir(pathname);
					 return true;
				} catch (Exception e) {
					return false;
				}
			}
		});
	}
	
	public boolean changeWorkingDirectory(final String transid, int timeout, final String pathname) throws Exception {
		return BaseUtils.invokeWithTimeout(transid, true, timeout, new ITimoutHandler<Boolean>() {
			@Override 
			public Boolean execute() throws Exception {
				 try {
					 sftpClient.cd(pathname);
					 return true;
				} catch (Exception e) {
					 return false;
				}
			}
		});
	}
	
	public void setDefaultTimeout(final String transid, int timeout, final int connectTimeout) throws Exception {
		this.defautzTimeout = connectTimeout;
	}

	public void connect(final String transid, int timeout, final String ip, final int port) throws Exception { 
		this.ip = ip;
		this.port = port;
	}
	
	public void disconnect(final String transid, int timeout) throws Exception { 
		BaseUtils.invokeWithTimeout(transid, true, timeout, new ITimoutHandler<Boolean>() {
			@Override
			public Boolean execute() throws Exception {
				try {
					sftpClient.disconnect();
				} catch (Exception e) {
				}
				try {
					session.disconnect();
				} catch (Exception e) {
				}
			    return true;
			}
		});
	}

	public boolean login(final String transid, int timeout, final String user, final String pass) throws Exception { 
		return BaseUtils.invokeWithTimeout(transid, true, timeout, new ITimoutHandler<Boolean>() {
			@Override
			public Boolean execute() throws Exception {
				try {
					JSch ssh = new JSch();
					session = ssh.getSession(user, ip, port);
					
					java.util.Properties config = new java.util.Properties();
					config.put("StrictHostKeyChecking", "no");
					session.setConfig(config);
					session.setPassword(pass);
					session.connect(defautzTimeout);
					logger.info(transid + ", connect ssh successfully");
					
				} catch (Exception e) {
					logger.info(transid + ", connect ssh fail, msg = " + e.getMessage());
					return false;
				}
				Channel channel = null;
				try {
						channel = session.openChannel("sftp");
						channel.connect(defautzTimeout);
						sftpClient = (ChannelSftp) channel;
						logger.info(transid + ", connect sftp successfully");
						
						return true;
				} catch (Exception e) {
					logger.info(transid + ", connect fstp fail, msg = " + e.getMessage());
				}
				return false;
			}
		});
	}

	public boolean logout(final String transid, int timeout) throws Exception { 
		return BaseUtils.invokeWithTimeout(transid, true, timeout, new ITimoutHandler<Boolean>() {
			@Override
			public Boolean execute() throws Exception {
				try {
					sftpClient.disconnect();
				} catch (Exception e) {
				}
				try {
					session.disconnect();
				} catch (Exception e) {
				}
			    return true;
			}
		});
	}

	public void enterLocalActiveMode(final String transid, int timeout) throws Exception {
	}

	public void enterLocalPassiveMode(final String transid, int timeout) throws Exception { 
	}

	public void enterRemotePassiveMode(final String transid, int timeout) throws Exception { 
	}

	public boolean isAvailable(final String transid, int timeout) throws Exception { 
		return sftpClient != null && sftpClient.isConnected();
	}

	public boolean isConnected(final String transid, int timeout) throws Exception { 
		return sftpClient != null && sftpClient.isConnected();
	}

	public MyFtpFile[] listFiles(final String transid, int timeout, final String remotePath) throws Exception { 
		Vector<ChannelSftp.LsEntry> v = null;
		try {
			v = sftpClient.ls(remotePath);
		} catch (Exception e) {
			return null;
		}
		if(v == null){
			return null;
		}
		if(v.size() == 0){
			return new MyFtpFile[0];
		}
		ArrayList<MyFtpFile> newFiles = new ArrayList<MyFtpFile>();
		for(ChannelSftp.LsEntry entry: v){
			if(entry.getFilename().startsWith(".")){
				continue;
			}
			MyFtpFile bean = new MyFtpFile();
			bean.setName(entry.getFilename());
			bean.setType(entry.getFilename().contains(".") ? 0 : 1);
			newFiles.add(bean);
		}
		return newFiles.toArray(new MyFtpFile[newFiles.size()]);  
	}

	public boolean retrieveFile(final String transid, int timeout, final String fullRemotePath, final FileOutputStream out)  {
		try {
			return BaseUtils.invokeWithTimeout(transid, true, timeout, new ITimoutHandler<Boolean>() {
				@Override
				public Boolean execute() throws Exception {
					try {
						sftpClient.get(fullRemotePath, out);
						return true;
					} catch (Exception e) {
						return false;
					}
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
				try {
					sftpClient.rm(fullRemotePath);
					return true;
				} catch (Exception e) {
					return false;
				}
			}
		});
	}

	public boolean rename(final String transid, int timeout, final String fullRemotePath, final String moveTo) throws Exception {
		return BaseUtils.invokeWithTimeout(transid, true, timeout, new ITimoutHandler<Boolean>() {
			@Override
			public Boolean execute() throws Exception {
				try {
					sftpClient.rename(fullRemotePath, moveTo); 
					return true;
				} catch (Exception e) {
					return false;
				}
			}
		});
	}
	
	public boolean upload(final String transid, int timeout, final String src, final String dst) throws Exception {
		return BaseUtils.invokeWithTimeout(transid, true, timeout, new ITimoutHandler<Boolean>() {
			@Override
			public Boolean execute() throws Exception {
				try {
					sftpClient.put(src, dst);
					return true;
				} catch (Exception e) {
					return false;
				}
			}
		});
	} 

	@Override
	public void setBufferSize(String transid, int timeout, int bufSize) throws Exception {
	}
}
