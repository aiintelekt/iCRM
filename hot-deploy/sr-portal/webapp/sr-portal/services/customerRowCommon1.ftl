<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<div class="row">
  <#assign workEffortId = '${requestParameters.workEffortId?if_exists}'>
 <input type='hidden' id="partyId" name="partyId" value="${requestParameters.partyId?if_exists}" />
 
 <input type='hidden' id="workEffortId" name="partyId" value="${requestParameters.workEffortId?if_exists}" />
 <script>
 var partyId= $('#partyId').val();
 var workEffortId= $('#workEffortId').val();
 console.log("partyId"+partyId);
 console.log("workEffortId"+workEffortId);
 </script>
  <#-- <#assign partyId = '${requestParameters.partyId?if_exists}'> -->
  <#assign cinNumber = "">
  <#assign customerName = "">
  
  <#assign prospectId = "">
   <#assign partyIdOne = (EntityQuery.use(delegator).select("partyId","roleTypeId").from("WorkEffortPartyAssignment").where("workEffortId",workEffortId).queryFirst())?if_exists />
        <#assign partyId = "">	
		<#assign roleTypeId = "">
		<#assign customerType = "">
			<#if partyIdOne?has_content>
					<#assign partyId = "${partyIdOne.partyId?if_exists}">
					<#assign roleTypeId = "${partyIdOne.roleTypeId?if_exists}">
		    </#if>
<#if partyId?has_content && roleTypeId?has_content>
   <#assign roleTypeAndPartyDetails = (EntityQuery.use(delegator).select("description").from("RoleTypeAndParty").where("partyId",partyId).queryFirst())?if_exists />
    <#if roleTypeAndPartyDetails?has_content>  
      <#assign customerType = "${roleTypeAndPartyDetails.description?if_exists}">
        <#assign personList = (Static["org.ofbiz.entity.util.EntityQuery"].use(delegator).select("firstName","middleName","lastName","nationalId").from("Person").where("partyId",partyId).queryOne())?if_exists />
		  <#if personList?has_content>
		    <#assign nationalId = "${personList.nationalId?if_exists}">  
		  </#if>
		 <#assign partyIdtnList = (Static["org.ofbiz.entity.util.EntityQuery"].use(delegator).select("idValue").from("PartyIdentification").where("partyId",partyId).queryFirst())?if_exists />
					<#if partyIdtnList?has_content>
						<#if roleTypeId == "NON_CRM">
							<#assign vPlusId = "${partyIdtnList.idValue?if_exists}" >
						<#elseif roleTypeId == "PROSPECT">	
							<#assign prospectId = "${partyIdtnList.idValue?if_exists}" >
						<#elseif roleTypeId == "CUSTOMER">	
							<#assign cifNo = "${partyIdtnList.idValue?if_exists}" >	
						</#if>
					</#if>
     </#if>     
  </#if>
        <div class="col-md-12 col-lg-3 col-sm-12">
        	<@displayCell label="Customer Type" value="${customerType?if_exists}"/>
    	</div>
    	<div class="col-md-12 col-lg-3 col-sm-12">
        	<@displayCell label="CIF ID" value="${cifNo?if_exists}" />
        	<@displayCell label="Prospect ID"value="${prospectId?if_exists}" />
    	</div>
   		<div class="col-md-12 col-lg-3 col-sm-12">
   		    <@displayCell label="National ID" value="${nationalId?if_exists}"/>
        	<@displayCell label="V+ ID" value="${vPlusId?if_exists}"/>
    	</div>
    	 <div class="col-md-12 col-lg-3 col-sm-12">
    	 </div>

 
         </div>