package com.xtel.uploader;

import com.elcom.utils.misc.Config;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class EFSCConfig extends Config {
	
	Log logger = LogFactory.getLog(EFSCConfig.class);
	private String LOCAL_MODE = "";
	private String REMOTE_MODE = "";
	
	private String FTP_DESTINATION_ADDRESS = "127.0.0.1";
	private int FTP_DESTINATION_PORT = 21;
	private String FTP_DESTINATION_USER = "ftpuser";
	private String FTP_DESTINATION_PASSWORD = "ftppassword";
	private String FTP_DESTINATION_FOLDER = "";
	private int FTP_SYNC_INTERVAL_TIME = 20;
	private int FTP_SYNC_SLEEP_TIME = 1;
	private int FTP_SOURCE_BACKUP_OPTION = 1;
	private String FTP_SOURCE_FOLDER = "";
	private String FTP_SOURCE_FILE_TYPE = "cdr";
	private String FTP_SOURCE_BACKUP_FOLDER = "";
	private long FTP_TIME_OUT = 300L;

	public EFSCConfig(String filename) {
		super(filename);
		load();
	}

	public void load() {
		super.load();
		scatter();
	}

	private void scatter() {
		this.FTP_SYNC_INTERVAL_TIME = getKeyInt(null, "FTP_SYNC_INTERVAL_TIME",
				this.FTP_SYNC_INTERVAL_TIME);
		this.FTP_SYNC_SLEEP_TIME = getKeyInt(null, "FTP_SYNC_SLEEP_TIME",
				this.FTP_SYNC_SLEEP_TIME);
		this.FTP_SOURCE_BACKUP_OPTION = getKeyInt(null, "FTP_SOURCE_BACKUP_OPTION", this.FTP_SOURCE_BACKUP_OPTION);
		this.FTP_DESTINATION_ADDRESS = getKey(null, "FTP_DESTINATION_ADDRESS", this.FTP_DESTINATION_ADDRESS);
		this.FTP_DESTINATION_PORT = getKeyInt(null, "FTP_DESTINATION_PORT", this.FTP_DESTINATION_PORT);
		this.LOCAL_MODE = getKey(null, "LOCAL_MODE", this.LOCAL_MODE);
		this.REMOTE_MODE = getKey(null, "REMOTE_MODE", this.REMOTE_MODE);
		
		this.FTP_DESTINATION_USER = getKey(null, "FTP_DESTINATION_USER",
				this.FTP_DESTINATION_USER);
		this.FTP_DESTINATION_PASSWORD = getKey(null,
				"FTP_DESTINATION_PASSWORD", this.FTP_DESTINATION_PASSWORD);
		this.FTP_DESTINATION_FOLDER = getKey(null, "FTP_DESTINATION_FOLDER",
				this.FTP_DESTINATION_FOLDER);
		this.FTP_SOURCE_FOLDER = getKey(null, "FTP_SOURCE_FOLDER",
				this.FTP_SOURCE_FOLDER);
		this.FTP_SOURCE_FILE_TYPE = getKey(null, "FTP_SOURCE_FILE_TYPE",
				this.FTP_SOURCE_FILE_TYPE);
		this.FTP_SOURCE_BACKUP_FOLDER = getKey(null,
				"FTP_SOURCE_BACKUP_FOLDER", this.FTP_SOURCE_BACKUP_FOLDER);
		this.FTP_TIME_OUT = getKeyLong(null, "FTP_TIME_OUT", this.FTP_TIME_OUT);
		if (!this.logger.isWarnEnabled()) {
			return;
		}
		this.logger.warn("FTP_SYNC_INTERVAL_TIME: "
				+ this.FTP_SYNC_INTERVAL_TIME);
		this.logger.warn("FTP_SYNC_SLEEP_TIME: " + this.FTP_SYNC_SLEEP_TIME);
		this.logger.warn("FTP_SOURCE_BACKUP_OPTION: "
				+ this.FTP_SOURCE_BACKUP_OPTION);
		this.logger.warn("FTP_DESTINATION_ADDRESS: " + this.FTP_DESTINATION_ADDRESS);
		this.logger.warn("FTP_DESTINATION_PORT: " + this.FTP_DESTINATION_PORT);
		this.logger.warn("LOCAL_MODE: " + this.LOCAL_MODE);
		this.logger.warn("REMOTE_MODE: " + this.REMOTE_MODE);
		
		this.logger.warn("FTP_DESTINATION_USER: " + this.FTP_DESTINATION_USER);
		this.logger.warn("FTP_DESTINATION_PASSWORD: "
				+ this.FTP_DESTINATION_PASSWORD);
		this.logger.warn("FTP_DESTINATION_FOLDER: "
				+ this.FTP_DESTINATION_FOLDER);
		this.logger.warn("FTP_SOURCE_FOLDER: " + this.FTP_SOURCE_FOLDER);
		this.logger.warn("FTP_SOURCE_FILE_TYPE: " + this.FTP_SOURCE_FILE_TYPE);
		this.logger.warn("FTP_SOURCE_BACKUP_FOLDER: "
				+ this.FTP_SOURCE_BACKUP_FOLDER);
		this.logger.warn("FTP_TIME_OUT: " + this.FTP_TIME_OUT);
	}

	public void store() {
		gather();
		super.store();
	}

	private void gather() {
		setKeyInt(null, "FTP_SYNC_INTERVAL_TIME", this.FTP_SYNC_INTERVAL_TIME);
		setKeyInt(null, "FTP_SYNC_SLEEP_TIME", this.FTP_SYNC_SLEEP_TIME);
		setKeyInt(null, "FTP_SOURCE_BACKUP_OPTION", this.FTP_SOURCE_BACKUP_OPTION);
		setKey(null, "FTP_DESTINATION_ADDRESS", this.FTP_DESTINATION_ADDRESS);
		setKey(null, "LOCAL_MODE", this.LOCAL_MODE);
		setKey(null, "REMOTE_MODE", this.REMOTE_MODE);
		setKeyInt(null, "FTP_DESTINATION_PORT", this.FTP_DESTINATION_PORT);
		setKey(null, "FTP_DESTINATION_USER", this.FTP_DESTINATION_USER);
		setKey(null, "FTP_DESTINATION_PASSWORD", this.FTP_DESTINATION_PASSWORD);
		setKey(null, "FTP_DESTINATION_FOLDER", this.FTP_DESTINATION_FOLDER);
		setKey(null, "FTP_SOURCE_FOLDER", this.FTP_SOURCE_FOLDER);
		setKey(null, "FTP_SOURCE_FILE_TYPE", this.FTP_SOURCE_FILE_TYPE);
		setKey(null, "FTP_SOURCE_BACKUP_FOLDER", this.FTP_SOURCE_BACKUP_FOLDER);
		setKeyLong(null, "FTP_TIME_OUT", this.FTP_TIME_OUT);
	}

	protected void setDefault() {
		super.setDefault();

		gather();
	}

	public String getFTP_DESTINATION_ADDRESS() {
		return this.FTP_DESTINATION_ADDRESS;
	}

	public String getFTP_DESTINATION_FOLDER() {
		return this.FTP_DESTINATION_FOLDER;
	}

	public String getFTP_DESTINATION_PASSWORD() {
		return this.FTP_DESTINATION_PASSWORD;
	}

	public String getFTP_DESTINATION_USER() {
		return this.FTP_DESTINATION_USER;
	}

	public String getFTP_SOURCE_BACKUP_FOLDER() {
		return this.FTP_SOURCE_BACKUP_FOLDER;
	}

	public String getFTP_SOURCE_FOLDER() {
		return this.FTP_SOURCE_FOLDER;
	}

	public int getFTP_SYNC_INTERVAL_TIME() {
		return this.FTP_SYNC_INTERVAL_TIME;
	}

	public String getFTP_SOURCE_FILE_TYPE() {
		return this.FTP_SOURCE_FILE_TYPE;
	}

	public int getFTP_SYNC_SLEEP_TIME() {
		return this.FTP_SYNC_SLEEP_TIME;
	}

	public int getFTP_SOURCE_BACKUP_OPTION() {
		return this.FTP_SOURCE_BACKUP_OPTION;
	}

	public long getFTP_TIME_OUT() {
		return this.FTP_TIME_OUT;
	}

	public int getFTP_DESTINATION_PORT() {
		return FTP_DESTINATION_PORT;
	}

	public void setFTP_DESTINATION_PORT(int fTP_DESTINATION_PORT) {
		FTP_DESTINATION_PORT = fTP_DESTINATION_PORT;
	}

	public String getLOCAL_MODE() {
		return LOCAL_MODE;
	}

	public void setLOCAL_MODE(String lOCAL_MODE) {
		LOCAL_MODE = lOCAL_MODE;
	}

	public String getREMOTE_MODE() {
		return REMOTE_MODE;
	}

	public void setREMOTE_MODE(String rEMOTE_MODE) {
		REMOTE_MODE = rEMOTE_MODE;
	}
	
	
}
