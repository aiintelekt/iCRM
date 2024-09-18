package org.fio.dataimport;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.ChannelSftp.LsEntry;

public class Test {

	public static void main(String[] args) {/*
		// TODO Auto-generated method stub
		System.out.print("HI");
		
		try{
			
			Session session = null;
			Channel channel = null;
			ChannelSftp channelSftp = null;
			
			JSch jsch = new JSch();
			
			String sftpUsername = "groupfio";
			String sftpPassword = "fio@!123!";
			String sftpPort = "21";
			String sftpHost = "fioftp.groupfio.com";

				int port = 21;

				try{
					port = Integer.parseInt(sftpPort);
				}catch(Exception e){
					e.printStackTrace();
				}
				session = jsch.getSession(sftpUsername, sftpHost, sftpPort);

			session.setPassword(sftpPassword);
			java.util.Properties config = new java.util.Properties();
			config.put("StrictHostKeyChecking", "no");
			session.setConfig(config);
			session.connect();
			channel = session.openChannel("sftp");
			channel.connect();
			channelSftp = (ChannelSftp) channel;
			
		}catch(Exception e){
			e.printStackTrace();
		}
	*/}

}
