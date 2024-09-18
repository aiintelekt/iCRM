/**
 * 
 */
package org.fio.homeapps.util;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.fio.homeapps.constants.GlobalConstants;
import org.fio.homeapps.constants.GlobalConstants.QueryType;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericDataSourceException;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.datasource.GenericHelper;
import org.ofbiz.entity.datasource.GenericHelperInfo;
import org.ofbiz.entity.jdbc.SQLProcessor;
import org.ofbiz.entity.model.DynamicViewEntity;
import org.ofbiz.entity.model.ModelField;
import org.ofbiz.entity.model.ModelViewEntity;
import org.ofbiz.entity.util.EntityFindOptions;

/**
 * @author Sharif
 *
 */
public class QueryUtil {

	/**
	 * @deprecated causing Vulnerability issue (leading to SQL injection) Use runSqlQuery(PreparedStatement ps) 
	 */
	@Deprecated
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
	
	public static List<Map<String, Object>> runSqlQuery(PreparedStatement ps) {

		ResultSet rs = null;
		List<Map<String, Object>> records = new ArrayList<Map<String, Object>>();
		if (UtilValidate.isNotEmpty(ps)) {
			try {
				rs = ps.executeQuery();
				if (UtilValidate.isNotEmpty(rs)) {
					List<String> columns = new ArrayList<String>();
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
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					if (rs != null) {
						rs.close();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return records;
	}
	
	public static List<EntityCondition> makeCondition(List<EntityCondition> conditionList, Object filterValue) {
        try {
            if (UtilValidate.isNotEmpty(filterValue)) {
            	if (filterValue instanceof Map) {
            		Map<String, Object> filters = (Map<String, Object>) filterValue;
            		for (String key : filters.keySet()) {
            			Object value = filters.get(key);
            			value = value.equals(null) ? null : value;
						conditionList.add(EntityCondition.makeCondition(key, EntityOperator.EQUALS, value));
					}
            	} else if (filterValue instanceof List) {
            		makeCondition(conditionList, (List) filterValue);
            	}
            }
        } catch (Exception e) {}

        return conditionList;
    }
	
	public static List<EntityCondition> makeCondition(List<EntityCondition> conditionList, List<Map> filterValue) {
        try {
            if (UtilValidate.isNotEmpty(filterValue)) {
            	for (Map filter : filterValue) {
            		String fieldName = (String) filter.get("field_name");
            		String operation = (String) filter.get("operation");
            		Object fieldValue = filter.get("field_value");
            		
            		fieldValue = fieldValue.equals(null) ? null : fieldValue;
            		
            		Object fv = filter.get("filter_value");
            		
            		conditionList.add(EntityCondition.makeCondition(fieldName, GlobalConstants.ENTITY_OPERATOR_BY_NAME.get(operation), fieldValue));
            		if (UtilValidate.isNotEmpty(fv)) {
            			makeCondition(conditionList, fv);
            		}
            	} 
            }
        } catch (Exception e) {
        	e.printStackTrace();
        }
        
        return conditionList;
	}
	
	public static String getFilterByDateExpr(QueryType queryType) {
		return getFilterByDateExpr(queryType, null, "fromDate", "thruDate", "");
	}
	
	public static String getFilterByDateExpr(QueryType queryType, String fromDateName, String thruDateName, String alias) {
		return getFilterByDateExpr(queryType, null, fromDateName, thruDateName, alias);
	}
	
	public static String getFilterByDateExpr(QueryType queryType, String fromDateName, String thruDateName) {
		return getFilterByDateExpr(queryType, null, fromDateName, thruDateName, "");
	}
	
	public static String getFilterByDateExpr(QueryType queryType, String alias) {
		return getFilterByDateExpr(queryType, null, "fromDate", "thruDate", alias);
	}
	
	public static String getFilterByDateExpr(QueryType queryType, Timestamp date) {
		return getFilterByDateExpr(queryType, date, "fromDate", "thruDate", "");
	}
	
	public static String getFilterByDateExpr(QueryType queryType, Timestamp date, String fromDateName, String thruDateName, String alias) {
		String query = "";
		try {
			
			if (UtilValidate.isNotEmpty(queryType) && UtilValidate.isNotEmpty(fromDateName) && UtilValidate.isNotEmpty(thruDateName)) {
				
				if (UtilValidate.isEmpty(date)) {
					date = UtilDateTime.nowTimestamp();
				}
				alias = UtilValidate.isEmpty(alias) ? "" : alias+".";
				
				switch (queryType) {
				case NATIVE:
					fromDateName = DataHelper.javaPropToSqlProp(fromDateName);
					thruDateName = DataHelper.javaPropToSqlProp(thruDateName);
					query = " (("+alias+thruDateName+" IS NULL OR "+alias+thruDateName+" > '"+date+"') AND ("+alias+fromDateName+" IS NULL OR "+alias+fromDateName+" <= '"+date+"')) ";
					break;
				case HQL:
					query = " (("+alias+thruDateName+" IS NULL OR "+alias+thruDateName+" > '"+date+"') AND ("+alias+fromDateName+" IS NULL OR "+alias+fromDateName+" <= '"+date+"')) ";
					break;
				default:
					break;
				}
				
			}
			
		} catch (Exception e) {
		}
		return query;
	}
	
	public static String getFilterByDateExpr(QueryType queryType, Timestamp fromDate, Timestamp thruDate, String fromDateName, String thruDateName) {
		return getFilterByDateExpr(queryType, fromDate, thruDate, fromDateName, thruDateName, "");
	}
	public static String getFilterByDateExpr(QueryType queryType, Timestamp fromDate, Timestamp thruDate) {
		return getFilterByDateExpr(queryType, fromDate, thruDate, "fromDate", "thruDate", "");
	}
	public static String getFilterByDateExpr(QueryType queryType, Timestamp fromDate, Timestamp thruDate, String fromDateName, String thruDateName, String alias) {
		String query = "";
		try {
			
			if (UtilValidate.isNotEmpty(queryType) && UtilValidate.isNotEmpty(fromDateName) && UtilValidate.isNotEmpty(thruDateName)) {
				
				alias = UtilValidate.isEmpty(alias) ? "" : alias+".";
				
				switch (queryType) {
				case NATIVE:
					if (UtilValidate.isNotEmpty(fromDate)) {
						fromDateName = DataHelper.javaPropToSqlProp(fromDateName);
						query += " AND ("+alias+fromDateName+" IS NULL OR "+alias+fromDateName+" >= '"+fromDate+"') ";
					}
					if (UtilValidate.isNotEmpty(thruDate)) {
						thruDateName = DataHelper.javaPropToSqlProp(thruDateName);
						query += " AND ("+alias+thruDateName+" IS NULL OR "+alias+thruDateName+" < '"+thruDate+"') ";
					}
					break;
				case HQL:
					if (UtilValidate.isNotEmpty(fromDate)) {
						query += " AND ("+alias+fromDateName+" IS NULL OR "+alias+fromDateName+" >= '"+fromDate+"') ";
					}
					if (UtilValidate.isNotEmpty(thruDate)) {
						query += " AND ("+alias+thruDateName+" IS NULL OR "+alias+thruDateName+" < '"+thruDate+"') ";
					}
					break;
				default:
					break;
				}
				
			}
			
		} catch (Exception e) {
		}
		return query;
	}
	
	public static long findCountByCondition(Delegator delegator, DynamicViewEntity dynamicViewEntity, EntityCondition whereEntityCondition,
            EntityCondition havingEntityCondition, List<ModelField> selectFields, EntityFindOptions findOptions) {
		return findCountByCondition(delegator, dynamicViewEntity, whereEntityCondition, havingEntityCondition, selectFields, findOptions, null);
	}
	
	public static long findCountByCondition(Delegator delegator, DynamicViewEntity dynamicViewEntity, EntityCondition whereEntityCondition,
            EntityCondition havingEntityCondition, List<ModelField> selectFields, EntityFindOptions findOptions, Map<String, Object> context) {
		long resultListSize = 0;
		try {
			int fioGridFetch = UtilValidate.isNotEmpty(context) && UtilValidate.isNotEmpty(context.get("fioGridFetch")) ? (int) context.get("fioGridFetch") : 0;
			int totalCount = UtilValidate.isNotEmpty(context) && UtilValidate.isNotEmpty(context.get("totalCount")) ? (int) context.get("totalCount") : 0;
			
			boolean isFetchSize = true;
			if (UtilValidate.isNotEmpty(context) && UtilValidate.isNotEmpty(context.get("totalCount")) && UtilValidate.isNotEmpty(context.get("fioGridFetch"))) {
				if (totalCount <= fioGridFetch) {
					isFetchSize = false;
					resultListSize = totalCount;
				}
			}
			
			if (isFetchSize) {
				ModelViewEntity modelViewEntity = dynamicViewEntity.makeModelViewEntity(delegator);
				GenericHelper helper = delegator.getEntityHelper(dynamicViewEntity.getOneRealEntityName());
				resultListSize = helper.findCountByCondition(delegator, modelViewEntity, whereEntityCondition, havingEntityCondition, selectFields, findOptions, context);
			}
		} catch (Exception e) {
			UtilMessage.getPrintStackTrace(e);
		}
        return resultListSize;
	}
	
	public static void setParameterList(PreparedStatement ps, List<Object> values) {
		
		try {
			for (int i = 0; i < values.size(); i++) {
				ps.setObject(i + 1, values.get(i));
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static ResultSet getResultSet(String sqlQuery, List<Object> values, Delegator delegator) {
		SQLProcessor sqlProcessor = null;
		try {
			sqlProcessor = new SQLProcessor(delegator, delegator.getGroupHelperInfo("org.ofbiz"));
			PreparedStatement ps = sqlProcessor.getConnection().prepareStatement(sqlQuery);
			setParameterList(ps, values);
			return ps.executeQuery();
		}catch(Exception e) {
			e.printStackTrace();
		}finally {
			if (sqlProcessor!=null) {
				try {
					sqlProcessor.close();
				} catch (GenericDataSourceException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return null;
	}
}
