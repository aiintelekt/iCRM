package org.fio.admin.portal.util;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.datasource.GenericHelperInfo;
import org.ofbiz.entity.jdbc.SQLProcessor;

public class SqlUtil {

    public static List<Map<String, Object>> executeQuery(Delegator delegator, String query) {
        ResultSet rs = null;
        List<Map<String, Object>> records = new ArrayList<Map<String, Object>>();
        String selGroup = "org.ofbiz";
        if (UtilValidate.isNotEmpty(query)) {
            GenericHelperInfo ghi = delegator.getGroupHelperInfo(selGroup);
            SQLProcessor sqlProcessor = new SQLProcessor(delegator, ghi);
            try {
                if (query.toUpperCase().startsWith("SELECT")) {
                    rs = sqlProcessor.executeQuery(query);
                    if (UtilValidate.isNotEmpty(rs)) {
                        List < String > columns = new ArrayList < String > ();
                        ResultSetMetaData rsmd = rs.getMetaData();
                        int numberOfColumns = rsmd.getColumnCount();
                        for (int i = 1; i <= numberOfColumns; i++) {
                            columns.add(rsmd.getColumnLabel(i));
                        }
                        while (rs.next()) {
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
                    if (rs != null)
                        rs.close();
                    if (sqlProcessor != null)
                    	sqlProcessor.close();
                } catch (Exception e) {}
            }
        }
        return records;
    }
    
    public static String getSelectQueryTableName(String sqlQuery) {
		String tableName = "";
		try {
			if(UtilValidate.isNotEmpty(sqlQuery) && sqlQuery.toUpperCase().startsWith("SELECT")) {
				sqlQuery = sqlQuery.toUpperCase();
				tableName = sqlQuery.substring(sqlQuery.indexOf("FROM")+5, sqlQuery.contains("WHERE") ? (sqlQuery.indexOf("WHERE")-1) : sqlQuery.length());
			}
		} catch (Exception e) {
		}
		return tableName;
	}
    
    public static String extractTableName(String query) {
        // Regular expression pattern to match the table name after the "FROM" keyword
        Pattern pattern = Pattern.compile("FROM\\s+([\\w.]+)", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(query);

        if (matcher.find()) {
            String tableName = matcher.group(1);
            // Remove any alias names after the table name
            return tableName.split("\\s+")[0];
        } else {
            // Handle cases where table name extraction fails
            return "";
        }
    }
    
}