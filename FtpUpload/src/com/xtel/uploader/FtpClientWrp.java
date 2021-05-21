package com.xtel.uploader;

import java.io.IOException;
import java.io.InputStream;
import java.net.SocketException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import org.apache.commons.net.ftp.FTPClient;

public class FtpClientWrp extends FTPClient {
	
	private ExecutorService executor = Executors.newCachedThreadPool();
	private final EFSCConfig cfg;

	public FtpClientWrp(EFSCConfig config) {
		this.cfg = config;
	}
	
	@Override
	public boolean rename(final String from, final String to) throws IOException {
		Callable<Boolean> call = new Callable<Boolean>() { 
			public Boolean call() throws Exception {
				return FtpClientWrp.this.m_rename(from, to);
			}
		};
		Future<Boolean> future = this.executor.submit(call);
		try {
			return  future.get(this.cfg.getFTP_TIME_OUT(), TimeUnit.SECONDS);
		} catch (Exception e) {
			throw new IOException(e.getMessage());
		}
	};
	
	private boolean m_rename(final String from, final String to) throws IOException {
		return super.rename(from, to);
	};
	
	@Override
	public boolean login(final String username, final String password) throws IOException {
		Callable<Boolean> call = new Callable<Boolean>() { 
			public Boolean call() throws Exception {
				return FtpClientWrp.this.m_login(username, password);
			}
		};
		Future<Boolean> future = this.executor.submit(call);
		try {
			return  future.get(this.cfg.getFTP_TIME_OUT(), TimeUnit.SECONDS);
		} catch (Exception e) {
			throw new IOException(e.getMessage());
		}
	};
	
	private boolean m_login(String username, String password) throws IOException {
		return super.login(username, password);
	};
	
	@Override
	public boolean makeDirectory(final String pathname) throws IOException {
		Callable<Boolean> call = new Callable<Boolean>() { 
			public Boolean call() throws Exception {
				return FtpClientWrp.this.m_makeDirectory(pathname);
			}
		};
		Future<Boolean> future = this.executor.submit(call);
		try {
			return  future.get(this.cfg.getFTP_TIME_OUT(), TimeUnit.SECONDS);
		} catch (Exception e) {
			throw new IOException(e.getMessage());
		}
	};
	
	private boolean m_makeDirectory(String pathname) throws IOException {
		return super.makeDirectory(pathname);
	};
	
	
	@Override
	public void connect(final String hostname) throws IOException {
		Callable<Boolean> call = new Callable<Boolean>() { 
			public Boolean call() throws Exception {
				 FtpClientWrp.this.m_connect(hostname);
				 return true;
			}
		};
		Future<Boolean> future = this.executor.submit(call);
		try {
			future.get(this.cfg.getFTP_TIME_OUT(), TimeUnit.SECONDS);
		} catch (Exception e) {
			throw new IOException(e.getMessage());
		}
	};
	
	private void m_connect(String hostname) throws IOException {
		 super.connect(hostname);
	};
	
	@Override
	public void disconnect() throws IOException {
		Callable<Boolean> call = new Callable<Boolean>() { 
			public Boolean call() throws Exception {
				 FtpClientWrp.this.m_disconnect();
				 return true;
			}
		};
		Future<Boolean> future = this.executor.submit(call);
		try {
			future.get(this.cfg.getFTP_TIME_OUT(), TimeUnit.SECONDS);
		} catch (Exception e) {
			throw new IOException(e.getMessage());
		}
	};
	
	private void m_disconnect() throws IOException {
		 super.disconnect();
	};
	
	@Override
	public boolean changeWorkingDirectory(final String pathname) throws IOException {
		Callable<Boolean> call = new Callable<Boolean>() { 
			public Boolean call() throws Exception {
				return FtpClientWrp.this.m_changeWorkingDirectory(pathname);
			}
		};
		Future<Boolean> future = this.executor.submit(call);
		try {
			return  future.get(this.cfg.getFTP_TIME_OUT(), TimeUnit.SECONDS);
		} catch (Exception e) {
			throw new IOException(e.getMessage());
		}
	};
	
	private boolean m_changeWorkingDirectory(String pathname) throws IOException {
		return super.changeWorkingDirectory(pathname);
	};

	@Override
	public String[] listNames() throws IOException {
		Callable<String[]> call = new Callable<String[]>() {
			public String[] call() throws Exception {
				return FtpClientWrp.this.m_listNames();
			}
		};
		Future<String[]> future = this.executor.submit(call);
		try {
			return (String[]) future.get(this.cfg.getFTP_TIME_OUT(), TimeUnit.SECONDS);
		} catch (Exception e) {
			throw new IOException(e.getMessage());
		}
	}

	private String[] m_listNames() throws IOException {
		return super.listNames();
	}

	@Override
	public boolean storeFile(final String remote, final InputStream local) throws IOException {
		
		Callable<Boolean> call = new Callable<Boolean>() { 
			public Boolean call() throws Exception {
				return FtpClientWrp.this.m_storeFile(remote, local);
			}
		};
		Future<Boolean> future = this.executor.submit(call);
		try {
			return  future.get(this.cfg.getFTP_TIME_OUT(), TimeUnit.SECONDS);
		} catch (Exception e) {
			throw new IOException(e.getMessage());
		}
	}

	private boolean m_storeFile(String remote, InputStream local) throws IOException {
		return super.storeFile(remote, local);
	}
}
