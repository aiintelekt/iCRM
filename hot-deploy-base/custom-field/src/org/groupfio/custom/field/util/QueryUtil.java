/**
 * 
 */
package org.groupfio.custom.field.util;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.datasource.GenericHelperInfo;
import org.ofbiz.entity.jdbc.SQLProcessor;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;

/**
 * @author Sharif
 *
 */
public class QueryUtil {

	public static List<Map<String, Object>> runSqlQuery(Delegator delegator, String query) {

		ResultSet rs = null;
		List<Map<String, Object>> records = new ArrayList<Map<String, Object>>();
		String selGroup = "org.ofbiz";

		if (UtilValidate.isNotEmpty(query)) {

			//String helperName = delegator.getGroupHelperName(selGroup);
			GenericHelperInfo ghi = delegator.getGroupHelperInfo(selGroup);
			SQLProcessor dumpSeq = new SQLProcessor(delegator, ghi);

			try {
				if (query.toUpperCase().startsWith("SELECT")) {

					rs = dumpSeq.executeQuery(query);

					if (UtilValidate.isNotEmpty(rs)) {
						
						List<String> columns = new ArrayList<String>();
						
		                ResultSetMetaData rsmd = rs.getMetaData();
		                int numberOfColumns = rsmd.getColumnCount();
		                for (int i = 1; i <= numberOfColumns; i++) {
		                    columns.add(rsmd.getColumnLabel(i));
		                }
		                
		                //boolean rowLimitReached = false;
		                
		                while (rs.next()) {
		                	
		                    /*if (records.size() >= rowLimit) {
		                        resultMessage = "Returned top $rowLimit rows.";
		                        rowLimitReached = true;
		                        break;
		                    }*/
		                	
		                	Map<String, Object> record = new HashMap<String, Object>();
		                    
		                    for (int i = 1; i <= numberOfColumns; i++) {
		                    	record.put(rsmd.getColumnLabel(i), rs.getObject(i));
		                    }
		                    records.add(record);
		                }
		            }
					
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					if (rs != null) {
						rs.close();
					}
				} catch (Exception e) {
				}
			}
		}
		return records;
	}

}
