import org.ofbiz.base.util.UtilDateTime
import org.ofbiz.base.util.UtilValidate
import org.ofbiz.entity.GenericValue
import org.ofbiz.entity.util.EntityUtil

delegator = request.getAttribute("delegator");

loggedUserPartyId = userLogin.getString("partyId");
loggedUserName = org.fio.homeapps.util.PartyHelper.getPartyName(delegator, loggedUserPartyId, false);
context.put("loggedUserPartyName", loggedUserName);
context.put("loggedUserId", userLogin.getString("userLoginId"));
context.put("loggedUserPartyId", loggedUserPartyId);

String globalDateFormat = org.fio.homeapps.util.DataHelper.getGlobalDateFormat(delegator);
String globalDateTimeFormat = org.fio.homeapps.util.DataHelper.getGlobalDateTimeFormat(delegator);
	
inputContext = new LinkedHashMap<String, Object>();

productStoreId = request.getParameter("productStoreId");
context.put("productStoreId", productStoreId);
// promo summary data
List storeReceiptSummary = from("StoreAttribute").where("productStoreId", productStoreId).queryList();

inputContext.put("productStoreId",productStoreId);
if(UtilValidate.isNotEmpty(storeReceiptSummary)){
	for (int i=0;i<storeReceiptSummary.size();i++) {
		storeSummary = storeReceiptSummary.get(i);
		inputContext.put(storeSummary.get("attrName"),storeSummary.get("attrValue"));
		inputContext.put("productStoreId_desc",storeSummary.get("productStoreId"));
		inputContext.put("productStoreId_val",storeSummary.get("productStoreId"));
		storeHtml ="";
		if (UtilValidate.isNotEmpty(storeSummary.get("attrName")) && storeSummary.get("attrName").equalsIgnoreCase("storeHtml")) {
			storeHtml = storeSummary.get("attrValue");
			if (UtilValidate.isNotEmpty(storeHtml)) {
				storeHtml = org.ofbiz.base.util.Base64.base64Decode(storeHtml);
			}
			inputContext.put("storeHTML",storeHtml);
		}
		storeImage_path ="";
		storeImage_name ="";
		storeImage_link ="";
		if (UtilValidate.isNotEmpty(storeSummary.get("attrName")) && storeSummary.get("attrName").equalsIgnoreCase("storeImage")) {
			storeImage_path = storeSummary.get("attrValue");
			if (UtilValidate.isNotEmpty(storeImage_path)) {
				File file = new File(storeImage_path);
				if (file.isFile()) {
					storeImage_name = file.getName();
				}
				storeImage_link ="/admin-portal-resource/image/storeImages/"+storeImage_name;
				println"storeImage_link--"+storeImage_link;
				//storeImage_link = '<a target="_blank" href="/webapp/admin-portal-resource/image/storeImages/"'+storeImage_link+'">View Image</a>';
				inputContext.put("storeImage",storeImage_name);
				inputContext.put("storeImage_link",storeImage_link);
			}
			
		}else if (UtilValidate.isNotEmpty(storeSummary.get("attrName")) && storeSummary.get("attrName").equalsIgnoreCase("storeImageURL")) {
			storeImageURL_link = storeSummary.get("attrValue");
			inputContext.put("storeImageURL_link",storeImageURL_link);
		}else if (UtilValidate.isNotEmpty(storeSummary.get("attrName")) && storeSummary.get("attrName").equalsIgnoreCase("url1")) {
			url1_link = storeSummary.get("attrValue");
			inputContext.put("url1_link",url1_link);
		}else if (UtilValidate.isNotEmpty(storeSummary.get("attrName")) && storeSummary.get("attrName").equalsIgnoreCase("url2")) {
			url2_link = storeSummary.get("attrValue");
			inputContext.put("url2_link",url2_link);
		}else if (UtilValidate.isNotEmpty(storeSummary.get("attrName")) && storeSummary.get("attrName").equalsIgnoreCase("url3")) {
			url3_link = storeSummary.get("attrValue");
			inputContext.put("url3_link",url3_link);
		}else if (UtilValidate.isNotEmpty(storeSummary.get("attrName")) && storeSummary.get("attrName").equalsIgnoreCase("url4")) {
			url4_link = storeSummary.get("attrValue");
			inputContext.put("url4_link",url4_link);
		}
	}
}
println("inputContext----> "+inputContext);
context.put("inputContext", inputContext);




