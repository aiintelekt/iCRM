/*
 * Copyright (c) Open Source Strategies, Inc.
 *
 * Opentaps is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Opentaps is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Opentaps.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.fio.dataimport;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.fio.dataimport.util.DataUtil;
import org.fio.homeapps.util.ParamUtil;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityQuery;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.LocalDispatcher;

import javolution.util.FastList;

/**
 * @author Sharif
 *
 */
public class ProductSupplementaryDataDecoder implements ImportDecoder {
	
    private static final String module = ProductSupplementaryDataDecoder.class.getName();
    
    protected GenericValue userLogin;
    
    public ProductSupplementaryDataDecoder(Map<String, ?> context) throws GeneralException {
        this.userLogin = (GenericValue) context.get("userLogin");
    }

    public List<GenericValue> decode(GenericValue entry, Timestamp importTimestamp, Delegator delegator, LocalDispatcher dispatcher, Object... args) throws Exception {
    	List<String> matchPartyList = FastList.newInstance();
    	List<GenericValue> toBeStored = FastList.newInstance();
    	List<EntityCondition> conditions = new ArrayList<EntityCondition>();
    	GenericValue productSupplementaryData = null;
    	String productId = entry.getString("productId");
		conditions.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId));
		conditions.add(EntityCondition.makeCondition("dataType", EntityOperator.EQUALS, entry.getString("dataType")));
    	EntityCondition mainConditon = EntityCondition.makeCondition(conditions, EntityOperator.AND);
    	productSupplementaryData = EntityQuery.use(delegator).from("ProductSupplementaryData").where(mainConditon).queryFirst();
			if (UtilValidate.isNotEmpty(productSupplementaryData)) {
				// update basic product supplementary data
				prepareproductSupplementaryData(productSupplementaryData, entry);
			}else {
				productSupplementaryData = delegator.makeValue("ProductSupplementaryData", UtilMisc.toMap("productId",
						entry.getString("productId"), "dataType", entry.getString("dataType")));
				prepareproductSupplementaryData(productSupplementaryData, entry);
			}
			toBeStored.add(productSupplementaryData);
		return toBeStored;
    }
    
    private static void prepareproductSupplementaryData(GenericValue productSupplementaryData, GenericValue entry) {
    	productSupplementaryData.put("suppilerPartyId", entry.getString("suppilerPartyId"));
		productSupplementaryData.put("suppilerName", entry.getString("suppilerName"));
		productSupplementaryData.put("suppilerAddress", entry.getString("suppilerAddress"));
		productSupplementaryData.put("suppilerRating", entry.getString("suppilerRating"));
		productSupplementaryData.put("suppilerLogo", entry.getString("suppilerLogo"));
		productSupplementaryData.put("productType", entry.getString("productType"));
		productSupplementaryData.put("productTitle", entry.getString("productTitle"));
		productSupplementaryData.put("productStatus", entry.getString("productStatus"));
		productSupplementaryData.put("productRating", entry.getString("productRating"));
		productSupplementaryData.put("productMetric", entry.getString("productMetric"));
		productSupplementaryData.put("productValue", entry.getString("productValue"));
		productSupplementaryData.put("productCurrency", entry.getString("productCurrency"));
		productSupplementaryData.put("productDescription", entry.getString("productDescription"));
		productSupplementaryData.put("offerDate", entry.getTimestamp("offerDate"));
		productSupplementaryData.put("expirationDate", entry.getTimestamp("expirationDate"));
		productSupplementaryData.put("saleCommPerc", entry.getString("saleCommPerc"));
		productSupplementaryData.put("clickCommPerc", entry.getString("clickCommPerc"));
		productSupplementaryData.put("leadCommPerc", entry.getString("leadCommPerc"));
		productSupplementaryData.put("promotionType", entry.getString("promotionType"));
		productSupplementaryData.put("eventInfo", entry.getString("eventInfo"));
		productSupplementaryData.put("couponCode", entry.getString("couponCode"));
		productSupplementaryData.put("distance", entry.getString("distance"));
		productSupplementaryData.put("productCity", entry.getString("productCity"));
		productSupplementaryData.put("productState", entry.getString("productState"));
		productSupplementaryData.put("productCountry", entry.getString("productCountry"));
		productSupplementaryData.put("productZip", entry.getString("productZip"));
		productSupplementaryData.put("productGeoLat", entry.getString("productGeoLat"));
		productSupplementaryData.put("productGeoLng", entry.getString("productGeoLng"));
		productSupplementaryData.put("productSource", entry.getString("productSource"));
		productSupplementaryData.put("productLink", entry.getString("productLink"));
		productSupplementaryData.put("productContentId", entry.getString("productContentId"));
		productSupplementaryData.put("productImageUrl", entry.getString("productImageUrl"));
		productSupplementaryData.put("productImageResolutionOne", entry.getString("productImageResolutionOne"));
		productSupplementaryData.put("productImageResolutionTwo", entry.getString("productImageResolutionTwo"));
		productSupplementaryData.put("productImageResolutionThree", entry.getString("productImageResolutionThree"));
		productSupplementaryData.put("productImageResolutionFour", entry.getString("productImageResolutionFour"));
		productSupplementaryData.put("productImageResolutionFive", entry.getString("productImageResolutionFive"));
		productSupplementaryData.put("productCategoryIdOne", entry.getString("productCategoryIdOne"));
		productSupplementaryData.put("productCategoryIdTwo", entry.getString("productCategoryIdTwo"));
		productSupplementaryData.put("productCategoryIdThree", entry.getString("productCategoryIdThree"));
		productSupplementaryData.put("productCategoryNameOne", entry.getString("productCategoryNameOne"));
		productSupplementaryData.put("productCategoryNameTwo", entry.getString("productCategoryNameTwo"));
		productSupplementaryData.put("productCategoryNameThree", entry.getString("productCategoryNameThree"));
    }
}
