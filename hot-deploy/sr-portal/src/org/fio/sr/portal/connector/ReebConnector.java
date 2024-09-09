/**
 * 
 */
package org.fio.sr.portal.connector;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.fio.homeapps.util.CryptoUtil;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.util.EntityUtilProperties;

/**
 * @author Sharif
 *
 */
public class ReebConnector extends Connector {
	
	private static final String MODULE = ReebConnector.class.getName();

	public Connection connection;
	
	public ReebConnector(Delegator delegator) {
		driverName = EntityUtilProperties.getPropertyValue("reeb", "jdbc.driverName", delegator);
		connectionUrl = EntityUtilProperties.getPropertyValue("reeb", "jdbc.connectionUrl", delegator);
		userName = EntityUtilProperties.getPropertyValue("reeb", "jdbc.userName", delegator);
		password = EntityUtilProperties.getPropertyValue("reeb", "jdbc.password", delegator);
		
		try {
			CryptoUtil cryptoUtil = new CryptoUtil();
			password = cryptoUtil.decrypt(CryptoUtil.secretPass, password);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		orderTransViewName = EntityUtilProperties.getPropertyValue("reeb", "reeb.order.trans.viewName", delegator);
	}
	
	public Connection getConnection() {
		try {
			if (UtilValidate.isEmpty(connection)) {
				if (UtilValidate.isEmpty(driverName) || UtilValidate.isEmpty(connectionUrl) || UtilValidate.isEmpty(userName) || UtilValidate.isEmpty(password)) {
					Debug.logInfo("reeb jdbc: driverName, connectionUrl, userName, password cant be empty", MODULE);
					return null;
				}
				
				Debug.logInfo("reeb jdbc: start to open connection", MODULE);
				Class.forName(driverName);
				connection = DriverManager.getConnection(connectionUrl, userName, password);
				Debug.logInfo("reeb jdbc: success to open connection, schema: "+connection.getSchema(), MODULE);
			}
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
			Debug.logError("reeb jdbc: error open connection ClassNotFoundException: "+e1.getMessage(), MODULE);
		} catch (SQLException e1) {
			e1.printStackTrace();
			Debug.logError("reeb jdbc: error open connection SQLException: "+e1.getMessage(), MODULE);
		}
		return connection;
	}
	@Override
	public boolean closeConnection() {
		try {
			if (UtilValidate.isNotEmpty(connection)) {
				Debug.logInfo("reeb jdbc: start to close connection", MODULE);
				connection.close();
				Debug.logInfo("reeb jdbc: success to close connection, schema: "+connection.getSchema(), MODULE);
				return true;
			}
		} catch (Exception e1) {
			e1.printStackTrace();
			Debug.logError("reeb jdbc: error close connection error: "+e1.getMessage(), MODULE);
		}
		return false;
	}
	
	public Connection getConnectionByLocation(Delegator delegator, String wwLocationId) {
		Connection connection = null;
		try {
			if (UtilValidate.isNotEmpty(wwLocationId)) {
				String connectionUrl = EntityUtilProperties.getPropertyValue("reeb", "jdbc.connectionUrl."+wwLocationId, delegator);
				
				if (UtilValidate.isEmpty(driverName) || UtilValidate.isEmpty(connectionUrl) || UtilValidate.isEmpty(userName) || UtilValidate.isEmpty(password)) {
					Debug.logInfo("reeb jdbc: driverName, connectionUrl, userName, password cant be empty", MODULE);
					return null;
				}
				
				Debug.logInfo("reeb jdbc: start to open connection", MODULE);
				Class.forName(driverName);
				connection = DriverManager.getConnection(connectionUrl, userName, password);
				Debug.logInfo("reeb jdbc: success to open connection, schema: "+connection.getSchema(), MODULE);
			}
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
			Debug.logError("reeb jdbc: error open connection ClassNotFoundException: "+e1.getMessage(), MODULE);
		} catch (SQLException e1) {
			e1.printStackTrace();
			Debug.logError("reeb jdbc: error open connection SQLException: "+e1.getMessage(), MODULE);
		}
		return connection;
	}
	public boolean closeConnection(Connection connection) {
		try {
			if (UtilValidate.isNotEmpty(connection)) {
				Debug.logInfo("reeb jdbc: start to close connection", MODULE);
				connection.close();
				Debug.logInfo("reeb jdbc: success to close connection, schema: "+connection.getSchema(), MODULE);
				return true;
			}
		} catch (Exception e1) {
			e1.printStackTrace();
			Debug.logError("reeb jdbc: error close connection error: "+e1.getMessage(), MODULE);
		}
		return false;
	}
	
}
