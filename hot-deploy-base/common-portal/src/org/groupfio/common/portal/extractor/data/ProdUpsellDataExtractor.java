package org.groupfio.common.portal.extractor.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.fio.homeapps.util.ParamUtil;
import org.groupfio.common.portal.util.UtilAttribute;
import org.groupfio.common.portal.util.UtilProduct;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Sharif
 *
 */
public class ProdUpsellDataExtractor extends DataExtractor {
	
	private static final Logger log = LoggerFactory.getLogger(ProdUpsellDataExtractor.class);
	
	public ProdUpsellDataExtractor() {}
	
	public ProdUpsellDataExtractor(Data extractedData) {
		super(extractedData);
	}
	
	@Override
	public Map<String, Object> retrieve(Map<String, Object> context) {
		if (UtilValidate.isNotEmpty(extractedData)) {
			extractedData.retrieve(context);
		}
		return retrieveData(context);
	}

	public Map<String, Object> retrieveData(Map<String, Object> context) {
		Map<String, Object> response = new LinkedHashMap<String, Object>();
		try {
			if (UtilValidate.isNotEmpty(context)) {
				Delegator delegator = (Delegator) context.get("delegator"); 
				Map<String, Object> request = (Map<String, Object>) context.get("request");
				response = (Map<String, Object>) context.get("response");
				Map<String, Object> prodUpsellData = new LinkedHashMap<String, Object>();
				
				String partyId = ParamUtil.getString(request, "partyId");
				
				List<Map<String, Object>> prodUpsellTplTags = (List<Map<String, Object>>) request.get("prodUpsellTplTags");
				
				if (UtilValidate.isNotEmpty(partyId) && UtilValidate.isNotEmpty(prodUpsellTplTags)) {
					String roleTypeId = org.fio.homeapps.util.PartyHelper.getPartyRoleTypeId(partyId, delegator);;
					String upsellGroupId = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "PROD_UPSELL_GPID", "PROD_UPSELL");
					String defaultUpsellProdIds = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "PROD_UPSELL");
					
					Set<String> upsellProdIds = new LinkedHashSet<>();
					List<Map<String, Object>> prodList = new ArrayList<>();
					
					List<GenericValue> cfList = EntityQuery.use(delegator).select("customFieldId").from("CustomField").where("groupId", upsellGroupId).queryList();
					if (UtilValidate.isNotEmpty(cfList)) {
						for (GenericValue cf : cfList) {
							
							String fieldValue = UtilAttribute.getAttrFieldValue(delegator, UtilMisc.toMap("customFieldId", cf.getString("customFieldId"), "partyId", partyId, "domainEntityType", roleTypeId, "domainEntityId", partyId));
							if (UtilValidate.isNotEmpty(fieldValue)) {
								upsellProdIds.add(fieldValue);
							}
						}
					}
					
					if (UtilValidate.isEmpty(upsellProdIds) && UtilValidate.isNotEmpty(defaultUpsellProdIds)) {
						upsellProdIds = Arrays.asList(defaultUpsellProdIds.split(",")).stream().collect(Collectors.toSet());
					}
					
					if (UtilValidate.isNotEmpty(upsellProdIds)) {
						for (String productId : upsellProdIds) {
							Map<String, Object> prod = new LinkedHashMap<>();
							prod = UtilProduct.getProductDetails(delegator, productId);
							prod.put("productPrice", UtilProduct.getProductPrice(delegator, productId, "LIST_PRICE"));
							prodList.add(prod);
						}
					}
					
					for (Map<String, Object> templateTag : prodUpsellTplTags) {
						String tagId = (String) templateTag.get("tagId");
						String innerTemplateId = (String) templateTag.get("innerTemplateId");
						Map<String, Object> prodHtmlContext = new LinkedHashMap<String, Object>();
						prodHtmlContext.put("prodList", prodList);
						prodHtmlContext.put("erOrdhtmlRelprodTplId", innerTemplateId);
						String prodsHtml = UtilProduct.generateProdsHtml(delegator, prodHtmlContext);
						
						prodUpsellData.put(tagId, Objects.toString(prodsHtml, ""));
					}
					
					response.put("prodUpsellData", prodUpsellData);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage());
		}
		
		return response;
	}

}
