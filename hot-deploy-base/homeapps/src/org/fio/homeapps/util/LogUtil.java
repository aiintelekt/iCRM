package org.fio.homeapps.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * @author Sharif Ul Islam
 *
 */
public class LogUtil {
	
	private static final String MODULE = LogUtil.class.getName();
	static Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

	public static void saveLogInfo(String massage, String fileName) {
		
		try {
			String destFolderPath = System.getProperty("ofbiz.home")+"/runtime/logs/"+fileName+".txt";
			FileHandler fileTxt = new FileHandler(destFolderPath, true);
			SimpleFormatter formatterTxt = new SimpleFormatter();
			fileTxt.setFormatter(formatterTxt);
			logger.addHandler(fileTxt);
			logger.log(Level.INFO, massage);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void saveLogError(String massage, String fileName) {
		
		try {
			String destFolderPath = System.getProperty("ofbiz.home")+"/runtime/logs/"+fileName+".txt";
			FileHandler fileTxt = new FileHandler(destFolderPath, true);
			SimpleFormatter formatterTxt = new SimpleFormatter();
			fileTxt.setFormatter(formatterTxt);
			logger.addHandler(fileTxt);
			logger.log(Level.SEVERE, massage);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void saveLogWarn(String massage, String fileName) {
		
		try {
			String destFolderPath = System.getProperty("ofbiz.home")+"/runtime/logs/"+fileName+".txt";
			FileHandler fileTxt = new FileHandler(destFolderPath, true);
			SimpleFormatter formatterTxt = new SimpleFormatter();
			fileTxt.setFormatter(formatterTxt);
			logger.addHandler(fileTxt);
			logger.log(Level.WARNING, massage);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static String getPrintStackTrace(Exception e) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		e.printStackTrace(pw);
		return sw.toString();
	}
}
