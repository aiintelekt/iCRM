<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<div class="row">
          <div class="col-md-6 col-lg-3 col-sm-12">              
               <#assign salesOpportunityId = '${requestParameters.salesOpportunityId?if_exists}'> 
               <#if salesOpportunityId?has_content>
                   <#assign customerList = Static["org.ofbiz.entity.util.EntityQuery"].use(delegator).select("partyId").from("SalesOpportunityRole").where("salesOpportunityId",salesOpportunityId, "roleTypeId","CUSTOMER").queryList()?if_exists />    
				   <#assign customerId = "">
				   <#assign customerName = "">
				   <#if customerList?has_content>
				   		<#list customerList as eachParty>
				   			<#assign customerId = "${eachParty.partyId?if_exists}">
				   		</#list>
				   </#if>
                   <#if customerId?has_content>  
                   		<#assign personList = (Static["org.ofbiz.entity.util.EntityQuery"].use(delegator).select("firstName","middleName","lastName","nationalId").from("Person").where("partyId",customerId).queryOne())?if_exists />
						<#assign nonCrmList = (Static["org.ofbiz.entity.util.EntityQuery"].use(delegator).select("idValue").from("PartyIdentification").where("partyId",customerId,"partyIdentificationTypeId","NON_CRM").queryOne())?if_exists />
						<#assign prospectList = (Static["org.ofbiz.entity.util.EntityQuery"].use(delegator).select("idValue").from("PartyIdentification").where("partyId",customerId,"partyIdentificationTypeId","PROSPECT").queryOne())?if_exists />
						<#assign cinList = (Static["org.ofbiz.entity.util.EntityQuery"].use(delegator).select("idValue").from("PartyIdentification").where("partyId",customerId,"partyIdentificationTypeId","CIN").queryOne())?if_exists />
						<#if personList?has_content>
							<#assign nationalId = "${personList.nationalId?if_exists}">  
							<#assign customerName = "${personList.firstName?if_exists} ${personList.middleName?if_exists} ${personList.lastName?if_exists}">   
						</#if>
						<#if nonCrmList?has_content> 
						 	<#assign vPlusId = "${nonCrmList.idValue?if_exists}">
						 	<#assign nonCrmName = "${customerName?if_exists}">
						</#if>
						<#if prospectList?has_content>  
							<#assign prospectId = "${prospectList.idValue?if_exists}">
							<#assign prospectName = "${customerName?if_exists}">
						</#if>
						<#if cinList?has_content>  
							<#assign customerCin = "${cinList.idValue?if_exists}">
							<#assign cinName = "${customerName?if_exists}">
						</#if>
                   </#if>     
               </#if>
         <@displayCell
          label="Customer"
          value="${cinName?if_exists}"
          id="testT3"
          />
         
           <@displayCell
              label="CIN/CIF ID"
             value="${customerCin?if_exists}"
             id="test14"
            />
              
            </div>
            
          <div class="col-md-6 col-lg-3 col-sm-12">
          <@displayCell
              label="Prospect"
              value="${prospectName?if_exists}"
            />
            <@displayCell
              label="Prospect ID"
             value="${prospectId?if_exists}"
            />
              
            </div>
          
          <div class="col-md-6 col-lg-3 col-sm-12">
          <@displayCell
              label="Non CRM"
              value="${nonCrmName?if_exists}"
            />
            <@displayCell
              label="V+ ID"
              value="${vPlusId?if_exists}"
            />  
            </div>
          
          <div class="col-md-6 col-lg-3 col-sm-12">
          <@displayCell
              label="National ID"
              value="${nationalId?if_exists}"
              id="test20"
            />             
          </div>
         </div>
         
     