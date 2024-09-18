package org.groupfio.etl.process.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.groupfio.etl.process.scheduler.FileDownloadScheduler;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilValidate;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.ChannelSftp.LsEntry;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpATTRS;

public class SftpUtility {
	
	private static String MODULE = FileDownloadScheduler.class.getName();

	private String sftpHost;
	private int sftpPort;
	private String sftpUser;
	private String sftpPassword;
	private String sftpDir;
	private boolean isCreateDir;

	public SftpUtility(String sftpHost, int sftpPort, String sftpUser, String sftpPassword, String sftpDir) {
		super();
		this.sftpHost = sftpHost;
		this.sftpPort = sftpPort;
		this.sftpUser = sftpUser;
		this.sftpPassword = sftpPassword;
		this.sftpDir = sftpDir;
	}

	public String getSftpHost() {
		return sftpHost;
	}

	public void setSftpHost(String sftpHost) {
		this.sftpHost = sftpHost;
	}

	public int getSftpPort() {
		return sftpPort;
	}

	public void setSftpPort(int sftpPort) {
		this.sftpPort = sftpPort;
	}

	public String getSftpUser() {
		return sftpUser;
	}

	public void setSftpUser(String sftpUser) {
		this.sftpUser = sftpUser;
	}

	public String getSftpPassword() {
		return sftpPassword;
	}

	public void setSftpPassword(String sftpPassword) {
		this.sftpPassword = sftpPassword;
	}

	public String getSftpDir() {
		return sftpDir;
	}

	public void setSftpDir(String sftpDir) {
		this.sftpDir = sftpDir;
	}
	
	public boolean isCreateDir() {
		return isCreateDir;
	}

	public void setCreateDir(boolean isCreateDir) {
		this.isCreateDir = isCreateDir;
	}

	public boolean moveFileToDir(String localFilePath) {
		return moveFileToDir(localFilePath, null, null, true);
	}

	public boolean moveFileToDir(String localFilePath, String remoteDirPath) {
		return moveFileToDir(localFilePath, remoteDirPath, null, true);
	}

	public boolean moveFileToDir(String localFilePath, String remoteDirPath, String remoteFileName) {
		return moveFileToDir(localFilePath, remoteDirPath, remoteFileName, true);
	}

	public boolean copyFileToDir(String localFilePath) {
		return moveFileToDir(localFilePath, null, null, false);
	}

	public boolean copyFileToDir(String localFilePath, String remoteDirPath) {
		return moveFileToDir(localFilePath, remoteDirPath, null, false);
	}

	public boolean copyFileToDir(String localFilePath, String remoteDirPath, String remoteFileName) {
		return moveFileToDir(localFilePath, remoteDirPath, remoteFileName, false);
	}

	public boolean moveFileToDir(String localFilePath, String remoteDirPath, String remoteFileName, boolean isDelete) {
		boolean returnResult = false;
		boolean deleteSuccess = false;
		Session session = null;
		Channel channel = null;
		ChannelSftp channelSftp = null;

		try {
			JSch jsch = new JSch();
			session = jsch.getSession(this.sftpUser, this.sftpHost, this.sftpPort);
			session.setPassword(this.sftpPassword);
			java.util.Properties config = new java.util.Properties();
			config.put("StrictHostKeyChecking", "no");
			session.setConfig(config);
			session.connect();
			channel = session.openChannel("sftp");
			channel.connect();
			channelSftp = (ChannelSftp) channel;
			if (null != remoteDirPath)
				channelSftp.cd(remoteDirPath);
			else
				channelSftp.cd(this.sftpDir);

			File f = new File(localFilePath);
			String fileName = f.getName();
			if (null != remoteFileName && remoteFileName.length() > 0)
				fileName = remoteFileName;

			channelSftp.put(new FileInputStream(f), fileName);
			// Disconnecting the channel
			channel.disconnect();
			// Disconnecting the session
			session.disconnect();
			if (isDelete) {
				deleteSuccess = f.delete();
			} else {
				deleteSuccess = true;
			}
			returnResult = deleteSuccess;
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return returnResult;
	}

	public List readFileFromDir(String remoteDirPath, String localDir) {
		List fileList = new ArrayList();
		boolean returnResult = true;

		boolean deleteSuccess = false;
		Session session = null;
		Channel channel = null;
		ChannelSftp channelSftp = null;

		try {
			JSch jsch = new JSch();
			session = jsch.getSession(sftpUser, sftpHost, sftpPort);

			session.setPassword(sftpPassword);
			java.util.Properties config = new java.util.Properties();
			config.put("StrictHostKeyChecking", "no");
			session.setConfig(config);
			session.connect();
			channel = session.openChannel("sftp");
			channel.connect();
			channelSftp = (ChannelSftp) channel;

			if (UtilValidate.isEmpty(remoteDirPath)) {
				remoteDirPath = sftpDir;
			}
			
			if (isCreateDir()) {
				try {
					SftpATTRS attrs = channelSftp.lstat(remoteDirPath);
				} catch (Exception ex) {
					ex.printStackTrace();
					Debug.logInfo("Remote dir not exists> "+remoteDirPath, MODULE);
					System.out.println("Creating dir "+remoteDirPath);
				    channelSftp.mkdir(remoteDirPath);
				    return new ArrayList<>();
				}
			}
			
			channelSftp.cd(remoteDirPath);
			
			Vector filelist = channelSftp.ls(remoteDirPath);
			for (int i = 0; i < filelist.size(); i++) {
				// System.out.println(filelist.get(i).toString());
				try {

					LsEntry entry = (LsEntry) filelist.get(i);

					String localFileName = localDir + File.separator + entry.getFilename();
					String remoteFileName = entry.getFilename();

					if (remoteFileName == null || remoteFileName == "" || remoteFileName.length() <= 2) {
						continue;
					}
					
					Debug.logInfo(entry.getFilename(), MODULE);
					
					fileList.add(remoteFileName);
					File localFile = new File(localFileName);
					FileOutputStream fos = new FileOutputStream(localFile);
					channelSftp.get(remoteFileName, fos);

					channelSftp.rm(remoteFileName);

				} catch (Exception e) {
					e.printStackTrace();
				}

			}

			// Disconnecting the channel
			channel.disconnect();
			// Disconnecting the session
			session.disconnect();
			returnResult = deleteSuccess;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return fileList;

	}

}