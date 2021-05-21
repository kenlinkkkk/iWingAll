package com.ftp.connection;

import java.io.FileOutputStream;

import com.ftp.entities.MyFtpFile;

public interface IFtpClient {

	public boolean makeDirectory(final String transid, int timeout, final String pathname) throws Exception;
	public boolean changeWorkingDirectory(final String transid, int timeout, final String pathname) throws Exception;
	public void setDefaultTimeout(final String transid, int timeout, final int connectTimeout) throws Exception;
	public void connect(final String transid, int timeout, final String ip, final int port) throws Exception;
	public void setBufferSize(final String transid, int timeout, final int bufSize) throws Exception ;
	public void disconnect(final String transid, int timeout) throws Exception ;
	public boolean login(final String transid, int timeout, final String user, final String pass) throws Exception ;
	public boolean logout(final String transid, int timeout) throws Exception ;
	public void enterLocalActiveMode(final String transid, int timeout) throws Exception;
	public void enterLocalPassiveMode(final String transid, int timeout) throws Exception ;
	public void enterRemotePassiveMode(final String transid, int timeout) throws Exception;
	public boolean isAvailable(final String transid, int timeout) throws Exception ;
	public boolean isConnected(final String transid, int timeout) throws Exception;
	public MyFtpFile[] listFiles(final String transid, int timeout, final String remotePath) throws Exception;
	public boolean retrieveFile(final String transid, int timeout, final String fullRemotePath, final FileOutputStream out) ;
	public boolean deleteFile(final String transid, int timeout, final String fullRemotePath) throws Exception ;
	public boolean rename(final String transid, int timeout, final String fullRemotePath, final String moveTo) throws Exception;
}
