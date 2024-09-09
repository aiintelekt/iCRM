package org.groupfio.common.portal.util;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.jdbc.SQLProcessor;

/**
 * @author Sharif
 *
 */
public class UtilTemplate {

	private static final String MODULE = UtilTemplate.class.getName();
	
	public static List<Map<String, Object>> getInnerTemplateTags(Delegator delegator, Map<String, Object> filter) {
		List<Map<String, Object>> dataList = new ArrayList<>();
		try {
			String templateId = (String) filter.get("templateId");
			String templateCategoryId = (String) filter.get("templateCategoryId");
			
			ResultSet rs = null;
			SQLProcessor sqlProcessor = new SQLProcessor(delegator, delegator.getGroupHelperInfo("org.ofbiz"));
			
			String queryStr = "SELECT tt.TAG_ID, dtc.data_tag_value FROM template_tag tt"; 
			queryStr += " INNER JOIN data_tag_configuration dtc ON dtc.TAG_ID=tt.TAG_ID";
			queryStr += " INNER JOIN template_to_category ttc ON ttc.TEMPLATE_ID=dtc.data_tag_value";
			queryStr += " WHERE 1=1";
			queryStr += " AND dtc.TEMPLATE_TAG_TYPE_ID='INNER_TPL'";
			
			if (UtilValidate.isNotEmpty(templateId)) {
				queryStr += " AND tt.TEMPLATE_ID='"+templateId+"'";
			}
			if (UtilValidate.isNotEmpty(templateCategoryId)) {
				queryStr += " AND ttc.TEMPLATE_CATEGORY_ID='"+templateCategoryId+"'";
			}
			queryStr += " GROUP BY tt.TAG_ID, dtc.data_tag_value";
			
			rs = sqlProcessor.executeQuery(queryStr);
			if (rs != null) {
				while (rs.next()) {
					String tagId = rs.getString("tt.TAG_ID");
					String innerTemplateId = rs.getString("dtc.data_tag_value");
					
					Map<String, Object> data = new LinkedHashMap<>();
					data.put("tagId", tagId);
					data.put("innerTemplateId", innerTemplateId);
					dataList.add(data);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dataList;
	}
	
}
