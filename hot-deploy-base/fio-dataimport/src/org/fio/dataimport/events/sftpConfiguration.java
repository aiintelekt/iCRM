package org.fio.dataimport.events;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityQuery;

public class sftpConfiguration {

	public static String sftpConfig(HttpServletRequest request, HttpServletResponse response) 
	{
		Delegator delegator 	= (Delegator) request.getAttribute("delegator");
		String userName = request.getParameter("userName");
		String password = request.getParameter("password");
		String port = request.getParameter("port");
		String host = request.getParameter("host");
		String seqId = request.getParameter("sequenceId");
		try
		{
			GenericValue sftpConfiguration=null;
			if(UtilValidate.isNotEmpty(userName) && UtilValidate.isEmpty(seqId))
			{
				sftpConfiguration = delegator.makeValue("SftpConfiguration");
				seqId = delegator.getNextSeqId("SftpConfiguration");
				sftpConfiguration.put("seqId", seqId);
				sftpConfiguration.put("userName", userName);
				sftpConfiguration.put("password", password);
				sftpConfiguration.put("port", port);
				sftpConfiguration.put("host", host);
				sftpConfiguration.create();
			}
			else if(UtilValidate.isNotEmpty(seqId))
			{

				GenericValue sftConfig = EntityQuery.use(delegator).from("SftpConfiguration").where("seqId", seqId).queryOne();
				sftConfig.set("userName", userName);
				sftConfig.set("password", password);
				sftConfig.set("port", port);
				sftConfig.set("host", host);	
				sftConfig.store();
				
			}

		}
		catch(Exception e)
		{
			request.setAttribute("_ERROR_MESSAGE_", "Error occurred while creating sftpConfiguration , error is :"+e.getMessage());
			return "error";
		}
		request.setAttribute("_EVENT_MESSAGE_", "sftp configuration created successfully.");
		return "success";
	}
}
