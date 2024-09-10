<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<#assign srNumber = '${requestParameters.srNumber?if_exists}'>
<#assign roleTypeDescription = "">
<#assign cifId = "">
<#assign prospectId = "">
<#assign vPlusId = "">
<#assign nationalId = "">
<#if srNumber?has_content>
	<#assign custReqDetail = (Static["org.ofbiz.entity.util.EntityQuery"].use(delegator).select("fromPartyId").from("CustRequest").where("custRequestId",requestParameters.srNumber!).queryOne())?if_exists />
	<#if custReqDetail?has_content && custReqDetail.get("fromPartyId")?has_content>
		<#assign fromPartyId = "${custReqDetail.fromPartyId?if_exists}">
		<#if fromPartyId?has_content>
			<#assign partyRoleDetail = (Static["org.ofbiz.entity.util.EntityQuery"].use(delegator).select("roleTypeId").from("PartyRole").where("partyId",fromPartyId).queryFirst())?if_exists />
			<#if partyRoleDetail?has_content && partyRoleDetail.get("roleTypeId")?has_content>
				<#assign roleTypeId = "${partyRoleDetail.roleTypeId?if_exists}">
				<#assign roleTypeAndPartyDetail = Static["org.ofbiz.entity.util.EntityQuery"].use(delegator).select("description").from("RoleTypeAndParty").where("partyId",fromPartyId?if_exists,"roleTypeId",roleTypeId).queryOne()?if_exists />
				<#if roleTypeAndPartyDetail?has_content && roleTypeAndPartyDetail.get("description")?has_content>
					<#assign roleTypeDescription = "${roleTypeAndPartyDetail.description?if_exists}">
				</#if>
			</#if>
			<#assign partyIdentificationDetails = Static["org.ofbiz.entity.util.EntityUtil"].getFirst(delegator.findByAnd("PartyIdentification", {"partyId" :  fromPartyId}, null, false))/>
			<#if partyIdentificationDetails?has_content && partyIdentificationDetails.get("idValue")?has_content> 
				<#if partyIdentificationDetails.get("partyIdentificationTypeId")?has_content>
					<#assign partyIdentificationTypeId = "${partyIdentificationDetails.partyIdentificationTypeId?if_exists}">
					<#if "CIF" == partyIdentificationTypeId>
						<#assign cifId = "${partyIdentificationDetails.idValue?if_exists}">
					<#elseif "PROSPECT" == partyIdentificationTypeId>
						<#assign prospectId = "${partyIdentificationDetails.idValue?if_exists}">
					<#elseif "NON_CRM" == partyIdentificationTypeId>
						<#assign vPlusId = "${partyIdentificationDetails.idValue?if_exists}">
					</#if>
				</#if>
			</#if>
			<#assign personDetail = (Static["org.ofbiz.entity.util.EntityQuery"].use(delegator).select("partyId").from("Person").where("partyId",fromPartyId!).queryOne())?if_exists />
			<#if personDetail?has_content && personDetail.get("nationalId")?has_content>
				<#assign nationalId = "${personDetail.nationalId?if_exists}">
			</#if>
		</#if>
	</#if>
</#if>

<div class="row">
	<div class="col-md-6 col-lg-3 col-sm-12">
		<@displayCell
        	label=uiLabelMap.customerType
            value='${roleTypeDescription?if_exists}'
            labelColSize="col-sm-5"
        />
	</div>
	<div class="col-md-6 col-lg-3 col-sm-12">
		<@displayCell
        	label="CIN/CIF ID"
            value='${cifId?if_exists}'
        />
		<@displayCell
        	label=uiLabelMap.prospectId
            value='${prospectId?if_exists}'
        />
	</div>
	<div class="col-md-6 col-lg-3 col-sm-12">
		<@displayCell
        	label=uiLabelMap.nationalId
            value='${nationalId?if_exists}'
        />
		<@displayCell
        	label=uiLabelMap.vId
            value='${vPlusId?if_exists}'
        />
	</div>
</div>