/**
 * 
 */
package org.fio.sr.portal.connector;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.ofbiz.entity.Delegator;

/**
 * @author Sharif
 *
 */
public abstract class Connector {
	
	public abstract Connection getConnection();
	public abstract boolean closeConnection();
	public abstract Connection getConnectionByLocation(Delegator delegator, String wwLocationId);
	public abstract boolean closeConnection(Connection connection);

	protected String driverName;
	protected String connectionUrl;
	protected String userName;
	protected String password;
	protected String dbName;
	
	protected String orderTransViewName;
	
	/*public Connector (String driverName, String connectionUrl, String userName, String password) {
		this.driverName = driverName;
		this.connectionUrl = connectionUrl;
		this.userName = userName;
		this.password = password;
	}*/
	
	public int getSize(ResultSet rs) {
		try {
			/*if (rs.getType() == rs.TYPE_FORWARD_ONLY) {
				return -1;
			}*/
			rs.last();
			int total = rs.getRow();
			rs.beforeFirst();
			return total;
		} catch (SQLException sqle) {
			return -1;
		}
		// JDBC 1 driver error
		catch (AbstractMethodError ame) {
			return -1;
		}
	}
	
	public String getOrderTransViewName() {
		return orderTransViewName;
	}
}
