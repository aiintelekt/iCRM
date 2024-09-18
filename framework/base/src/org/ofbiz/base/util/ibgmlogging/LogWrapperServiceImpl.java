package org.ofbiz.base.util.ibgmlogging;

import java.net.InetAddress;
import java.net.UnknownHostException;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.SimpleMessage;
import org.apache.logging.log4j.spi.ExtendedLogger;
import org.apache.logging.log4j.spi.ExtendedLoggerWrapper;

public class LogWrapperServiceImpl implements I3beLogService {

	private static final String FQCN = LogWrapperServiceImpl.class.getName();
	private ExtendedLoggerWrapper log;
	private static String serverIp = null;

	public LogWrapperServiceImpl(Class<?> clazz) {
		Logger logger = LogManager.getLogger(clazz);
		log = new ExtendedLoggerWrapper((ExtendedLogger) logger, logger.getName(), logger.getMessageFactory());
		getserverIp();
	}

	public boolean isInfoEnabled() {
		return log.isInfoEnabled();
	}
	
	public void info(String loggerMessage){
		info(null, null, null, loggerMessage,
				null, null);
	}
	
	public void info(String loggerMessage,String loggerValue){
		info(null, null, null, loggerMessage,
				loggerValue, null);	
	}

	public void info(String loggerMessage,Object loggerValue){
		info(null, null, null, loggerMessage,
				loggerValue, null);	
	}

	
	public void info(String userId, String mode, String walletAcctId, String loggerMessage,
			String loggerValue, Exception e) {
		
		String customMsg = customSimpleMessage(userId, mode,  walletAcctId, loggerMessage,
				 loggerValue,  e);
		SimpleMessage message = new SimpleMessage(customMsg);
		log.logIfEnabled(FQCN, Level.INFO, null, message.getFormattedMessage());
	}
	
	public void info(String userId, String mode, String walletAcctId, String loggerMessage,
			Object loggerValue, Exception e) {
		
		String value = "";
		if(null != loggerValue)
			value = loggerValue.toString();
		
		String customMsg = customSimpleMessage(userId, mode,  walletAcctId, loggerMessage,
				value,  e);
		SimpleMessage message = new SimpleMessage(customMsg);
		log.logIfEnabled(FQCN, Level.INFO, null, message.getFormattedMessage());
	}
	
	
	public void debug(String loggerMessage){
		info(null, null, null, loggerMessage,
				null, null);
	}

	public void debug(String loggerMessage,String loggerValue){
		debug(null, null, null, loggerMessage,
				loggerValue, null);	
	}
	
	public void debug(String userId, String mode, String walletAcctId, String loggerMessage,
			String loggerValue, Exception e) {
		
		String customMsg = customSimpleMessage(userId, mode,  walletAcctId, loggerMessage,
				 loggerValue,  e);
		SimpleMessage message = new SimpleMessage(customMsg);
		log.logIfEnabled(FQCN, Level.DEBUG, null, message.getFormattedMessage());
		
	}


	
	public void error(String loggerMessage){
		error(null, null, null, loggerMessage,
				null, null);
	}
	
	public void error(String loggerMessage,Exception e){
		error(null, null, null, loggerMessage,
				null, e);
	}
	
	public void error(String loggerMessage,String loggerValue){
		error(null, null, null, loggerMessage,
				loggerValue, null);	
	}

	public void error(String userId, String mode, String walletAcctId, String loggerMessage,
			String loggerValue, Exception e) {
		
		String customMsg = customSimpleMessage(userId, mode,  walletAcctId, loggerMessage,
				 loggerValue);
		SimpleMessage message = new SimpleMessage(customMsg);
		log.logIfEnabled(FQCN, Level.ERROR , null, message.getFormattedMessage());
		if(e != null){
			log.error(e);
			//log.error(e.printStackTrace());
		}
	}
	
	public static void getserverIp() {
		InetAddress ip = null;
		try {
			if (serverIp == null) {
				ip = InetAddress.getLocalHost();
				serverIp = ip.getHostAddress();
			}

		} catch (UnknownHostException e) {

		}
	}

	public String customSimpleMessage(String userId, String mode, String walletAcctId, String loggerMessage,
			String loggerValue, Exception e) {

		StringBuilder customMsg = new StringBuilder();

		customMsg.append("Application : " + "Iwallet");
		customMsg.append(" - ServerIP : " + serverIp);
		customMsg.append(" - User ID : " + userId);
		customMsg.append(" - Mode : " + mode);

		if (walletAcctId != null)
			customMsg.append(" - WalletAcctId : " + walletAcctId);

		if (loggerMessage != null)
			customMsg.append(" - LoggerMessage : " + loggerMessage);

		if (loggerValue != null)
			customMsg.append(" - LoggerMessage : " + loggerValue);

		if (e != null)
			customMsg.append(" - Exception : " + e);

		return customMsg.toString();
	}

	public String customSimpleMessage(String userId, String mode, String walletAcctId, String loggerMessage,
			String loggerValue) {

		StringBuilder customMsg = new StringBuilder();

		customMsg.append("Application : " + "IWallet");
		customMsg.append(" - ServerIP : " + serverIp);
		customMsg.append(" - User ID : " + userId);
		customMsg.append(" - Mode : " + mode);

		if (walletAcctId != null)
			customMsg.append(" - WalletAcctId : " + walletAcctId);

		if (loggerMessage != null)
			customMsg.append(" - LoggerMessage : " + loggerMessage);

		if (loggerValue != null)
			customMsg.append(" - LoggerMessage : " + loggerValue);

		return customMsg.toString();
	}

	@Override
	public void error(Exception e) {
		if(e != null){
			log.error(e);
			//log.error(e.printStackTrace());
		}
		
	}

}
