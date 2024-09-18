package org.fio.homeapps.util;
/**
 * 
 */

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.RandomStringUtils;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityQuery;

import net.sf.json.JSONObject;

/**
 * @author sharif
 *
 */
public class CommonUtils {
	
	private static final String MODULE = CommonUtils.class.getName();

	public static String getRandomString (int length) {
		//String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789~`!@#$%^&*()-_=+[{]}\\|;:\'\",<.>/?";
		String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$&-_";
		String pwd = RandomStringUtils.random( length, characters );
		
		return pwd;
	}
	
	public static List<String> getAssessibleDomainEntityTypes(Delegator delegator, String userLoginId) {
		List<String> domainEntityTypes = new ArrayList<String>();
		try {
			String compPermsMap = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "COMP_PERMS_MAP");
			if (UtilValidate.isNotEmpty(compPermsMap)) {
				JSONObject accessMatrix = JSONObject.fromObject(compPermsMap);
				accessMatrix.keySet().forEach(type -> {
			        String permissionId = (String) accessMatrix.get(type);
			        //System.out.println("key: "+ type + " value: " + permissionId);
			        if (DataUtil.validatePermission(delegator, userLoginId, permissionId)) {
			        	domainEntityTypes.add(type.toString());
			        }
			    });
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return domainEntityTypes;
	}
	
	public static String getCatalogNameByCatalogIds(Delegator delegator, String catalogIds) {
		String categoryNames = "";
		try {
			if (UtilValidate.isNotEmpty(catalogIds)) {
				for (String catalogId : catalogIds.split(",")) {
					GenericValue entity = EntityQuery.use(delegator).select("catalogName").from("ProdCatalog").where("prodCatalogId", catalogId).queryFirst();
					if (UtilValidate.isNotEmpty(entity)) {
						categoryNames += entity.getString("catalogName") + ", ";
					}
				}
				if (UtilValidate.isNotEmpty(categoryNames)) {
					categoryNames = categoryNames.substring(0, categoryNames.length()-2);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			Debug.logError(e.getMessage(), MODULE);
		}
		return categoryNames;
	}
	
	public static String getCategoryNameByCategoryIds(Delegator delegator, String categoryIds) {
		String categoryNames = "";
		try {
			if (UtilValidate.isNotEmpty(categoryIds)) {
				for (String categoryId : categoryIds.split(",")) {
					GenericValue entity = EntityQuery.use(delegator).select("categoryName").from("ProductCategory").where("productCategoryId", categoryId).queryFirst();
					if (UtilValidate.isNotEmpty(entity)) {
						categoryNames += entity.getString("categoryName") + ", ";
					}
				}
				if (UtilValidate.isNotEmpty(categoryNames)) {
					categoryNames = categoryNames.substring(0, categoryNames.length()-2);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			Debug.logError(e.getMessage(), MODULE);
		}
		return categoryNames;
	}
	public static String getSecureRandomString(int length) {
		String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$&-_";
		SecureRandom secureRandom = new SecureRandom();
		StringBuilder sb = new StringBuilder(length);
		for (int i = 0; i < length; i++) {
			int index = secureRandom.nextInt(characters.length());
			sb.append(characters.charAt(index));
		}
		return sb.toString();
	}
}
