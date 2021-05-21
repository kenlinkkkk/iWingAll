package com.ftp.utils;

import java.util.ArrayList;

import org.w3c.dom.Element;

import com.ftp.entities.FtpServerInfo;
import com.ftp.entities.ItemMap;
import com.ligerdev.appbase.utils.BaseUtils;
import com.ligerdev.appbase.utils.textbase.ConfigsReader;

public class XmlConfigs extends ConfigsReader {

	public static int CHECK_INSTANCE_PORT;
	public static ArrayList<FtpServerInfo> LIST_CONNECTIONS;
	 
	@Override
	public void readPropeties() throws Exception {
		
		ArrayList<FtpServerInfo> listConnections = new ArrayList<FtpServerInfo>();
		ArrayList<Element> listFtpConnectionE = (ArrayList<Element>) getListElement("ftp_server_info");
		
		for (int i = 0; listFtpConnectionE != null && i < listFtpConnectionE.size() ; i++) {
			Element ftpConnectionE = listFtpConnectionE.get(i);
			
			ArrayList<String> time_expression = getList(ftpConnectionE, "time_expression|crond");
			String separator = getString(ftpConnectionE, "server_info|separator");
			String ip = getString(ftpConnectionE, "server_info|ip");
			String port = getString(ftpConnectionE, "server_info|port");
			String user = getString(ftpConnectionE, "server_info|user");
			String pass = getString(ftpConnectionE, "server_info|pass");
			String localMode = getString(ftpConnectionE, "server_info|local_mode");
			String remoteMode = getString(ftpConnectionE, "server_info|remote_mode");
			int connectTimeout = getInt(ftpConnectionE, "server_info|connect_timeout");
			boolean keepConnection = getBoolean(ftpConnectionE, "server_info|keep_connection");
			int bufferSize = getInt(ftpConnectionE, "server_info|buffer_size");
			String protocol = getStringAttr(ftpConnectionE, "server_info|protocol");
			
			ArrayList<ItemMap> itemMaps = new ArrayList<ItemMap>();
			ArrayList<Element> listFolderMapE = (ArrayList<Element>) getListElement(ftpConnectionE, "folder_mapping|item");
			
			for(int k = 0; listFolderMapE != null && k < listFolderMapE.size(); k ++){
				Element foderMapE = listFolderMapE.get(k);
				
				String local_path = getPath(foderMapE, "local_path", separator); 
				String remote_path = getPath(foderMapE, "server_path", separator); 
				String copyLocalTo = getPath(foderMapE, "copy_local_to", separator); 
				String actionOnServer = getString(foderMapE, "action_on_server"); 
				String moveOnServer = getPath(foderMapE, "move_on_server", separator); 
				int checkPrevDate = getInt(foderMapE, "scan_prev");
				String folderDateFormat = getString(foderMapE, "date_format");
				String file_pattern = getString(foderMapE, "file_pattern");
				String logName = getString(foderMapE, "log_name");
				int timeoutDownload = getInt(foderMapE, "timeout_download");
				String fileNotMatch = getPath(foderMapE, "file_not_match", separator); 
				boolean extract = getBoolean(foderMapE, "extract");
				String extractType = getString(foderMapE, "extract_type");
				String extractPass = getString(foderMapE, "extract_pass");
				String localPrefix = getString(foderMapE, "local_prefix");
				
				ItemMap itemMap = new ItemMap(remote_path, local_path, copyLocalTo,  checkPrevDate, 
						folderDateFormat, logName, timeoutDownload, file_pattern, actionOnServer, 
						moveOnServer, fileNotMatch, extract, extractType, extractPass, localPrefix);
				itemMaps.add(itemMap);
			}
			FtpServerInfo ftpConnectionObject = new FtpServerInfo(ip, Integer.parseInt(port), user, pass,  time_expression, 
					localMode, remoteMode, itemMaps, connectTimeout, separator, keepConnection, bufferSize, protocol); 
			listConnections.add(ftpConnectionObject);
		}
		LIST_CONNECTIONS = listConnections;
		CHECK_INSTANCE_PORT = getInt("check_instance_port");
	}
	
	private String getPath(Element e, String key, String separator) throws Exception{
		String path = getString(e, key);
		if(BaseUtils.isNotBlank(path) && !path.endsWith(separator)){
			path += separator;
		}
		return path; 
	}
}
