import org.ofbiz.base.util.*;
import org.ofbiz.entity.util.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.base.util.Debug;
import java.util.*;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityQuery;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.fio.admin.portal.util.DataHelper;
import org.ofbiz.base.util.UtilValidate;
import java.util.List;
import org.fio.admin.portal.util.DataHelper;
import org.fio.admin.portal.constant.AdminPortalConstant;

productStoreGroupId = null;
primaryParentGroupId = null;
status = null;
productStoreGroupTypeId = null;
phone = null;
phoneContactMechId = null;
telecomContactMechId = null;
emailContactMechId = null;
webContactMechId = null;
postalContactMechId = null;
address = null;
address1 = null;
address2 = null;
address3 = null;
city = null;
postalCode = null;
countryName = null;
stateName = null;
a1="";
a2="";
a3="";
a4="";
a5="";
a6="";
a7="";
inputContext = new LinkedHashMap<String, Object>();

productStoreGroupId=request.getParameter("productStoreGroupId");
GenericValue viewBusinessUnit = EntityQuery.use(delegator).from("ProductStoreGroup").where("productStoreGroupId", productStoreGroupId).queryOne();
if (UtilValidate.isNotEmpty(viewBusinessUnit))
{
	inputContext.put("productStoreGroupId",productStoreGroupId);
	inputContext.put("buId",viewBusinessUnit.getString("externalId"));
	inputContext.put("businessUnitName",viewBusinessUnit.getString("productStoreGroupName"));
	
	context.put("businessUnitName",viewBusinessUnit.getString("productStoreGroupName"));
	context.put("buId",viewBusinessUnit.getString("externalId"));
	context.put("productStoreGroupId",viewBusinessUnit.getString("productStoreGroupId"));
	context.put("postalId",viewBusinessUnit.getString("postalContactMechId"));
	context.put("mobileId",viewBusinessUnit.getString("telecomContactMechId"));
	context.put("phoneMechId",viewBusinessUnit.getString("phoneContactMechId"));
	context.put("websiteId",viewBusinessUnit.getString("websiteId"));
	context.put("emailId",viewBusinessUnit.getString("emailId"));
	context.put("url1",viewBusinessUnit.getString("url1"));
	context.put("url2",viewBusinessUnit.getString("url2"));
	context.put("url3",viewBusinessUnit.getString("url3"));
	context.put("url4",viewBusinessUnit.getString("url4"));
	context.put("url5",viewBusinessUnit.getString("url5"));
	primaryParentGroupId=viewBusinessUnit.getString("primaryParentGroupId");
	if (UtilValidate.isNotEmpty(primaryParentGroupId))
	{
		GenericValue getParentBu = EntityQuery.use(delegator).from("ProductStoreGroup").where("productStoreGroupId", primaryParentGroupId).queryOne();
		if (UtilValidate.isNotEmpty(getParentBu))
		{
			context.put("parentBu",getParentBu.getString("productStoreGroupName"));
			inputContext.put("parentBu",getParentBu.getString("productStoreGroupName"));
			
		}
	}
	productStoreGroupTypeId=viewBusinessUnit.getString("productStoreGroupTypeId");
	if (UtilValidate.isNotEmpty(productStoreGroupTypeId))
	{
		GenericValue getBuType = EntityQuery.use(delegator).from("ProductStoreGroupType").where("productStoreGroupTypeId", productStoreGroupTypeId).queryOne();
		if (UtilValidate.isNotEmpty(getBuType))
		{
			context.put("buType",getBuType.getString("description"));
			inputContext.put("buType",getBuType.getString("description"));
			
		}
	}
	status=viewBusinessUnit.getString("status");
	if (UtilValidate.isNotEmpty(status))
	{ 
		GenericValue getBuStatus = EntityUtil.getFirst(delegator.findByAnd("Enumeration", UtilMisc.toMap("enumId", status,"enumTypeId",AdminPortalConstant.BusinessUnitConstant.STATUS_ID), null, false));
		if (UtilValidate.isNotEmpty(getBuStatus))
		{
			context.put("buStatus",getBuStatus.getString("description"));
			inputContext.put("status",getBuStatus.getString("description"));
			
		}
	}
	/*context.put("seqNumber",viewBusinessUnit.getString("seqNum"));*/
	phoneContactMechId=viewBusinessUnit.getString("phoneContactMechId");
	if (UtilValidate.isNotEmpty(phoneContactMechId))
	{
		GenericValue getPhone = EntityQuery.use(delegator).from("TelecomNumber").where("contactMechId", phoneContactMechId).queryOne();
		if (UtilValidate.isNotEmpty(getPhone))
		{
			context.put("phone",getPhone.getString("contactNumber"));
		}
	}
	telecomContactMechId=viewBusinessUnit.getString("telecomContactMechId");
	if (UtilValidate.isNotEmpty(telecomContactMechId))
	{
		GenericValue getTelecom = EntityQuery.use(delegator).from("TelecomNumber").where("contactMechId", telecomContactMechId).queryOne();
		if (UtilValidate.isNotEmpty(getTelecom))
		{
			context.put("mobile",getTelecom.getString("contactNumber"));
		}
	}
	emailContactMechId=viewBusinessUnit.getString("emailId");
	if (UtilValidate.isNotEmpty(emailContactMechId))
	{
		GenericValue getEmail = EntityQuery.use(delegator).from("ContactMech").where("contactMechId", emailContactMechId).queryOne();
		if (UtilValidate.isNotEmpty(getEmail))
		{
			context.put("email",getEmail.getString("infoString"));
		}
	}
	webContactMechId=viewBusinessUnit.getString("websiteId");
	if (UtilValidate.isNotEmpty(webContactMechId))
	{
		GenericValue getWeb = EntityQuery.use(delegator).from("ContactMech").where("contactMechId", webContactMechId).queryOne();
		if (UtilValidate.isNotEmpty(getWeb))
		{
			context.put("web",getWeb.getString("infoString"));
		}
	}
	postalContactMechId=viewBusinessUnit.getString("postalContactMechId");
	if (UtilValidate.isNotEmpty(postalContactMechId))
	{ 
		GenericValue getPostal = EntityQuery.use(delegator).from("PostalAddress").where("contactMechId",postalContactMechId).queryOne(); 
		if (UtilValidate.isNotEmpty(getPostal))
		{
			address1 = getPostal.getString("address1"); 
			address2 = getPostal.getString("address2"); 
			address3 = getPostal.getString("address3");
			city = getPostal.getString("city");
			postalCode = getPostal.getString("postalCode");
			country = getPostal.getString("countryGeoId");
			if (UtilValidate.isNotEmpty(country))
			{
				context.put("country",country);
				GenericValue getCountry = EntityQuery.use(delegator).from("Geo").where("geoId",country).queryOne();
				if (UtilValidate.isNotEmpty(getCountry))
				{
					countryName = getCountry.getString("geoName");
				}
			}
			state = getPostal.getString("stateProvinceGeoId");
			if (UtilValidate.isNotEmpty(state))
			{
				context.put("state",state);
				GenericValue getState = EntityQuery.use(delegator).from("Geo").where("geoId",state).queryOne();
				if (UtilValidate.isNotEmpty(getState))
					{
						stateName = getState.getString("geoName");
					}
			}
			if(UtilValidate.isNotEmpty(address1)) {
				a1=address1 +",";
				context.put("address1",address1);
			}
			if(UtilValidate.isNotEmpty(address2)) {
				a2=address2 +",";
				context.put("address2",address2);
			}
			if(UtilValidate.isNotEmpty(address3)) {
				a3=address3 +",";
				context.put("address3",address3);
			}
			if(UtilValidate.isNotEmpty(city)) {
				a4=city +",";
				context.put("city",city);
			}
			if(UtilValidate.isNotEmpty(stateName)) {
				a5=stateName +",";
				context.put("stateName",stateName);
			}
			if(UtilValidate.isNotEmpty(countryName)) {
				a6=countryName +",";
				context.put("countryName",countryName);
			}
			
			if(UtilValidate.isNotEmpty(postalCode)) {
				a7=postalCode ;
				context.put("postalCode",postalCode);
			}
			address=a1+ a2 + a3 +a4 +a5 + a6 + a7;
			if(UtilValidate.isNotEmpty(address)) {
				context.put("address",address); 
			}
		} 
	}
}
context.put("inputContext",inputContext);