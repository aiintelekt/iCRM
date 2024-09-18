<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
 
 <@sectionFrameHeaderTab title="Merged Parties" tabId="MergeParty"/> 

<#if sectionName?has_content && sectionName == "lead-portal" >
			<#assign name = "Lead"/>
	 <#elseif sectionName?has_content && sectionName == "account-portal" >
		   <#assign name = "Account"/>
	 <#elseif sectionName ?has_content && sectionName == "contact-portal" >
       	 	<#assign name = "Contact"/>
     <#elseif sectionName ?has_content && sectionName == "contact-portal" >
       	 	<#assign name = "Customer"/>
 </#if>
<#-- Get Successor list of this party -->
  <#if successorList?has_content && successorList?size!=0>
		 <#list successorList as scName>
		 			 <#assign party = (delegator.findOne("Party", {"partyId" : scName.partyIdTo}, false))?if_exists/>
				              <#assign partyRoleId = party.roleTypeId/>
				         <#if partyRoleId?has_content && partyRoleId == "LEAD" >
				    			<#assign link = "viewLead"/>
								<#assign succcessorSectionName = "lead-portal"/>
						 <#elseif partyRoleId?has_content && partyRoleId == "ACCOUNT" >
					     	   <#assign link = "viewAccount"/>
							   <#assign succcessorSectionName = "account-portal"/>
						 <#elseif partyRoleId?has_content && partyRoleId == "CONTACT" >
						   		<#assign link = "viewContact"/>
						   	 	<#assign succcessorSectionName = "contact-portal"/>
						  <#elseif partyRoleId?has_content && partyRoleId == "CUSTOMER" >
						   		<#assign link = "viewCustomer"/>
						   	 	<#assign succcessorSectionName = "customer-portal"/>
						  </#if>
		 
		 <a href="/${succcessorSectionName}/control/${link}?partyId=${scName.partyIdTo}" target="_blank">${scName.partyIdTo}</a>
		 <b>${Static["org.ofbiz.party.party.PartyHelper"].getPartyName(delegator, scName.partyIdTo, false)}</b>
		  is the successor to this ${name?if_exists}.
		 </#list>
	</#if>
 
 			<#-- Get predecessorList  of this party -->			 
		   <#if predecessorList?has_content && predecessorList?size!=0>
				  <div class="table-responsive">
							<table  id="dtableGrid" class="table table-striped">
						        <thead>
						           <tr>
						              <th>Party Id</th>
						              <th>Party Name</th>
						              <th>Merged Date</th>
					              	</tr>
						        </thead>
							        <tbody>
					      <#list predecessorList as pdName>
					       		  <#assign party = (delegator.findOne("Party", {"partyId" : pdName.partyIdFrom}, false))?if_exists/>
					              <#assign partyRoleId = party.roleTypeId/>
					         <#if partyRoleId?has_content && partyRoleId == "LEAD" >
					    			<#assign link = "viewLead"/>
									<#assign predecessorSectionName = "lead-portal"/>
							 <#elseif partyRoleId?has_content && partyRoleId == "ACCOUNT" >
						     	   <#assign link = "viewAccount"/>
								   <#assign predecessorSectionName = "account-portal"/>
							 <#elseif partyRoleId?has_content && partyRoleId == "CONTACT" >
							   		<#assign link = "viewContact"/>
							   	 	<#assign predecessorSectionName = "contact-portal"/>
							 <#elseif partyRoleId?has_content && partyRoleId == "CUSTOMER" >
							   		<#assign link = "viewCustomer"/>
							   	 	<#assign predecessorSectionName = "customer-portal"/>
							  </#if>
			         	   <tr>
			             	<td><a href="/${predecessorSectionName}/control/${link}?partyId=${pdName.partyIdFrom}"  target="_blank">${pdName.partyIdFrom}</a></td>
			             	<td>${Static["org.ofbiz.party.party.PartyHelper"].getPartyName(delegator, pdName.partyIdFrom, false)}</td>								
			             	<td>
			             		<#if pdName.fromDate?has_content> 
			             			<b>${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(pdName.fromDate!, "yyyy-MM-dd")}</b>
			             		</#if>
			             	</td>
			              </tr>
		              </#list>
		         </tbody>
		      </table>
		</div>
	<#else>
         No predecessors .
    </#if>














