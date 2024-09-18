<#include "component://homeapps/webapp/homeapps/lib/ofbizFormMacros.ftl"/>

<div class="page-header row">
  <div class="col-md-3 col-sm-3 pl-0">
    <h2>${uiLabelMap.CommonLead}</h2>
  </div>
  <div class="col-md-9 col-sm-9 float-right mt-1">
    <div class="col-md-12 col-sm-12 form-horizontal">
    <div class="row">
    <div class="col-md-2 col-sm-2 pl-0 offset-md-5">
      <div class="form-group">        
        <select id="leadStatusId" name="leadStatusId" class="ui dropdown search form-control input-sm" onchange="javascript: leadStatusAjax(this.value);">
          <option value="placeHolder" data-content="<span class='nonselect'>${uiLabelMap.selectLeadStatus}</span>" selected>${uiLabelMap.selectLeadStatus}</option>
           <#list leadStatusHistoryList as leadStatus>
              <option value="${leadStatus.enumId}" <#if currentStatus?exists && currentStatus == leadStatus.enumId>selected</#if> >${leadStatus.description!}</option>
          </#list> 
        </select>
       </div>
      </div>
      <#-- <div class="col-md-2 col-sm-2 pl-0">
      <div class="form-group">
      		
	      <select id="callStatusForm" name="callStatusForm" class="ui dropdown form-control input-sm" data-live-search="true">
	         <option value=""> Select Call Status </option>
	         <#list callStatusList.entrySet() as entry>  
		  		<option value="${entry.key}"  >${entry.value!}</option>
		  	</#list>
	      </select>
      	 
      </div>
      </div>
      
      <div class="col-md-3 col-sm-3 pl-0">
       <div class="form-group">
          <div class="input-group date" id="datetimepicker11">
             <input type='text' class="form-control input-sm" name="callBackDateForm" id="callBackDateForm" data-date-format="DD-MM-YYYY" value="${callBackDateOCL?if_exists}" placeholder="DD-MM-YYYY" />
             <span class="input-group-addon"><span class="glyphicon glyphicon-calendar"></span>
             </span>
             <button type="reset" class="btn btn-xs btn-primary" id="updateCallStatus">Save</button>
          </div>
       </div>
    </div> -->
      
      <#-- a lead can be qualified it has already been assigned -->
      <#if partySummary.statusId?exists> 
      <form name="qualifyLeadForm" method="post" action="<@ofbizUrl>updateLeadContactStatus</@ofbizUrl>"/>
        <input type="hidden" name="leadPartyId" id="partyId" value="${partySummary?if_exists.partyId?if_exists}" />
        <input type="hidden" name="leadContactStatusId" id="statusId"  />
      </form>
      </#if>
      
      <div class="col-md-2 col-sm-2 pl-2 pr-0">
      <#if (partySummary.statusId?exists) && (partySummary.statusId != 'LEAD_QUALIFIED')>
        <a class="btn btn-xs btn-primary" href='javascript:document.qualifyLeadForm.submit()'>${uiLabelMap.qualifyLead}</a>
      </#if>
      
      <#-- a lead can only be converted if it has already been qualified -->
      <#if (partySummary.statusId?exists) && (partySummary.statusId == 'LEAD_QUALIFIED')>
       <a class="btn btn-xs btn-primary" href="convertLeadForm?partyId=${partySummary?if_exists.partyId?if_exists}">${uiLabelMap.convertLead}</a>
      </#if>
       
      <a href="updateLeadForm?partyId=${partySummary?if_exists.partyId?if_exists}">
        <span class="fa fa-edit btn btn-xs btn-primary" data-toggle="modal" data-target="" alt="${uiLabelMap.edit}" title="${uiLabelMap.edit}"></span>
      </a>
	</div>
	
	<div class="col-md-3 col-sm-3 pl-0 pr-1">		
	 <div class="form-group row"> 
	  <div class="col-md-11 col-sm-11 p-0">
 	  <select id="disableReason" name="disableReason" class="ui dropdown search form-control input-sm">
         <option value=""> Select Disable Reason </option>
         <#list disableReasonList.entrySet() as entry>  
	  		<option value="${entry.key}"  >${entry.value!}</option>
	  	</#list>
      </select>
	 </div>
	  <div class="col-md-1 col-sm-1 p-0">
      <a id="lead-disable-btn" class="btn btn-xs btn-secondary tooltips" href="#" data-original-title="${uiLabelMap.disabled}"><i class="fa fa-eye-slash gray"></i></a>      
      </div>
     </div> 	      
      </div>
      </div>
    </div>
  </div>
</div>

<form method="post" action="<@ofbizUrl>updateLeadContactStatus</@ofbizUrl>" id="updateLeadContactStatus" class="basic-form" name="updateLeadContactStatus">
	<input type="hidden" name="leadPartyId" value="${partySummary.partyId}">
    <input type="hidden" name="leadContactStatusId" value="" id="leadContactStatusId">
</form>

<form id="disabledLeadForm" name="disabledLeadForm" method="post" action="<@ofbizUrl>disabledLead</@ofbizUrl>"/>
	<input type="hidden" name="partyId" value="${partySummary.partyId}">
    <input type="hidden" name="statusId" value="PARTY_DISABLED" />
    <input type="hidden" name="disableReason" value="" />
</form>

<form name="callStatusUpdate" id="callStatusUpdate" method="post" action="<@ofbizUrl>callStatusUpdate</@ofbizUrl>"/>
  <input type="hidden" name="partyId" id="partyId" value="${partySummary?if_exists.partyId?if_exists}" />
  <input type="hidden" name="donePage" value="viewLead"/>
  <input type="hidden" name="callStatus" id="callStatus" value=""/>
  <input type="hidden" name="callBackDate" id="callBackDate" value=""/>
  <input type="hidden" name="marketingCampaignId" value="${defaultLeadMarketingCampaignId!}"/>
  <input type="hidden" name="contactListId" value="${defaultLeadContactListId!}"/>
</form>

<#-- view lead new layout [start] -->

<div class="row padding-r">

	<div class="col-md-4 col-sm-4">
				
		<div class="portlet-body form">
						
			<div class="form-body">
			
			<@readonlyInput 
				id="companyName"
				label=uiLabelMap.companyName
				value=dataImportLead.companyName
				isHiddenInput=false
				/>
			
			<div class="form-group row">
				<label class="col-md-4 col-form-label" for="parentCoDetails">Parent Co Details</label>
			   	<div class="col-md-7">
			   		<label class="col-form-label input-sm fw">
			   			<span id="parentCoDetails">
			         		<a href="viewAccount?partyId=${parentParty?if_exists.partyId?if_exists}">${parentParty?if_exists.groupName?if_exists} <#if parentParty?exists && parentParty?has_content>(${parentParty?if_exists.partyId?if_exists})</#if></a>
			         	</span>
			   		</label>	
			      	<div class="form-control-focus">
			      	</div>
			   	</div>
			</div>
			<#-- 
			<@readonlyInput 
				id="parentCoDetails"
				label=uiLabelMap.parentCoDetails
				value=dataImportLead.parentCoDetails
				isHiddenInput=false
				/>		
			 -->
			
			<div class="form-group row">
				<label class="col-md-4 col-form-label" for="salesTurnover">${uiLabelMap.salesTurnover!}</label>
			   	<div class="col-md-7">
			   		<label class="col-form-label input-sm fw">
			   			<span id="salesTurnover">
			         		<#if dataImportLead.salesTurnover?has_content>
			         			<#assign salesTurnover = Static["org.fio.crm.util.DataHelper"].numberFormat(dataImportLead.salesTurnover) />
			         			${salesTurnover!}
				         	<#else>
				         		-	
				         	</#if>	
			         	</span>
			   		</label>	
			      	<div class="form-control-focus">
			      	</div>
			   	</div>
			</div>
			<#-- 
			<@readonlyInput 
				id="salesTurnover"
				label=uiLabelMap.salesTurnover 
				value=dataImportLead.salesTurnover
				isHiddenInput=false
				/>	 -->	
			
			<@readonlyInput 
				id="constitution"
				label=uiLabelMap.constitution
				value=dataImportLead.constitution
				isHiddenInput=false
				displayEntityName="Enumeration" 
				keyField="enumId" 
				desField="description"
				/>		
						
			<@readonlyInput 
				id="source"
				label=uiLabelMap.source
				value=dataImportLead.source
				isHiddenInput=false
				displayEntityName="PartyIdentificationType" 
				keyField="partyIdentificationTypeId" 
				desField="description"
				/>
				
			<@readonlyInput 
				id="placeOfIncorporation"
				label=uiLabelMap.placeOfIncorporation
				value=dataImportLead.placeOfIncorporation
				isHiddenInput=false
				displayEntityName="Enumeration" 
				keyField="enumId" 
				desField="description"
				/>		
				
			</div>
			
		</div>
						
	</div>
	
	<div class="col-md-4 col-sm-4">
				
		<div class="portlet-body form">
						
			<div class="form-body">
			
			<@readonlyInput 
				id="firstName"
				label=uiLabelMap.firstName
				value=dataImportLead.partyFirstName
				isHiddenInput=false
				/>
				<#assign salutation=""/>
				<#if dataImportLead?if_exists.title?if_exists?contains(".")>
				  <#assign salutation="${dataImportLead?if_exists.title?if_exists}"/>
				<#else>
				  <#assign enumeration = Static["org.ofbiz.entity.util.EntityUtil"].getFirst(delegator.findByAnd("Enumeration", {"enumCode" : "${dataImportLead?if_exists.title?if_exists}"}, [], false))?if_exists/>
				  <#assign salutation="${enumeration.enumId!}"/>
				</#if>	
			<@readonlyInput 
				id="title"
				label=uiLabelMap.salutation
				value="${salutation!}"
				isHiddenInput=false
				/>		
			
			<@readonlyInput 
				id="industryCat"
				label=uiLabelMap.industryCat
				value=dataImportLead.industryCat
				isHiddenInput=false
				displayEntityName="Enumeration" 
				keyField="enumId" 
				desField="description"
				/>	
				
			<@readonlyInput 
				id="customerTradingType"
				label=uiLabelMap.importExportCustomer
				value=dataImportLead.customerTradingType
				isHiddenInput=false
				displayEntityName="Enumeration" 
				keyField="enumId" 
				desField="description"
				/>		
			
			<@readonlyInput 
				id="tcpName"
				label=uiLabelMap.tcpName
				value=dataImportLead.tcpName
				isHiddenInput=false
				displayEntityName="Enumeration" 
				keyField="enumId" 
				desField="description"
				/>	
				
			<@readonlyInput 
				id="finacleId"
				label=uiLabelMap.finacleId
				value=dataImportLead.finacleId
				isHiddenInput=false
				/>	
				
			<@readonlyInput 
				id="leadScore"
				label=uiLabelMap.leadScore
				value=dataImportLead.leadScore
				isHiddenInput=false
				displayEntityName="Enumeration" 
				keyField="enumId" 
				desField="description"
				/>				
				
			</div>
			
		</div>
						
	</div>
	
	<div class="col-md-4 col-sm-4">
				
		<div class="portlet-body form">
						
			<div class="form-body">
			
			<@readonlyInput 
				id="lastName"
				label=uiLabelMap.lastName
				value=dataImportLead.partyLastName
				isHiddenInput=false
				/>	
			
			<@readonlyInput 
				id="dateOfIncorporation"
				label=uiLabelMap.dateOfIncorporation
				value=dataImportLead.dateOfIncorporation
				isHiddenInput=false
				isDate=false
				/>		
				
			<@readonlyInput 
				id="industry"
				label=uiLabelMap.industry
				value=dataImportLead.industry
				isHiddenInput=false
				displayEntityName="Enumeration" 
				keyField="enumId" 
				desField="description"
				/>				
						
			<@readonlyInput 
				id="tallyUserType"
				label=uiLabelMap.tallyUserType
				value=dataImportLead.tallyUserType
				isHiddenInput=false
				displayEntityName="Enumeration" 
				keyField="enumId" 
				desField="description"
				/>	
				
			<@readonlyInput 
				id="noOfEmployees"
				label=uiLabelMap.noOfEmployees
				value=dataImportLead.noOfEmployees
				isHiddenInput=false
				/>	
				
			<@readonlyInput 
				id="designation"
				label=uiLabelMap.designation
				value=dataImportLead.designation
				isHiddenInput=false
				displayEntityName="Enumeration" 
				keyField="enumId" 
				desField="description"
				/>	
				
			<@readonlyInput 
				id="virtualTeamId"
				label=uiLabelMap.virtualTeam
				value=virtualTeamName
				isHiddenInput=false
				/>				
						
			</div>
			
		</div>
						
	</div>
	
</div>

<div class="page-header">
	<h2 class="float-left">Contact Details</h2>
</div>

<div class="row padding-r">

	<div class="col-md-4 col-sm-4">
				
		<div class="portlet-body form">
						
			<div class="form-body">
			
			<@readonlyInput 
				id="keyContactPerson1"
				label=uiLabelMap.keyContactPerson1
				value=dataImportLead.keyContactPerson1
				isHiddenInput=false
				/>
			<#assign secondaryPhoneCountryCode ="">
			<#assign secondaryPhoneNumber ="">
			<#if dataImportLead.secondaryPhoneNumber?has_content || dataImportLead.secondaryPhoneCountryCode?has_content>
			  <#if dataImportLead.secondaryPhoneCountryCode?has_content> 
			      <#assign secondaryPhoneCountryCode=dataImportLead.secondaryPhoneCountryCode/>
			  </#if>
			  <#if dataImportLead.secondaryPhoneNumber?has_content> 
			      <#assign secondaryPhoneNumber=dataImportLead.secondaryPhoneNumber/>
			  </#if>
			  <#assign secondaryPhonVal=secondaryPhoneCountryCode+" "+secondaryPhoneNumber/>
			  <#else> 
			  <#assign secondaryPhonVal ="">
			</#if>
			<@readonlyInput 
				id="secondaryPhoneNumber"
				label=uiLabelMap.phoneNumber2
				value=secondaryPhonVal
				isHiddenInput=false
				/>		
						
			<@readonlyInput 
				id="postalCode"
				label=uiLabelMap.pinCode
				value=dataImportLead.postalCode
				isHiddenInput=false
				/>	
			
			<@readonlyInput 
				id="address2"
				label=uiLabelMap.address2
				value=dataImportLead.address2
				isHiddenInput=false
				/>		
															
				
			</div>
			
		</div>
						
	</div>
	
	<div class="col-md-4 col-sm-4">
				
		<div class="portlet-body form">
						
			<div class="form-body">
			
			<@readonlyInput 
				id="keyContactPerson2"
				label=uiLabelMap.keyContactPerson2
				value=dataImportLead.keyContactPerson2
				isHiddenInput=false
				/>	
			
			<@readonlyInput 
				id="emailAddress"
				label=uiLabelMap.emailAddress
				value=dataImportLead.emailAddress
				isHiddenInput=false
				/>	
			
			<@readonlyInput 
				id="city"
				label=uiLabelMap.city
				value=dataImportLead.city
				isHiddenInput=false
				displayEntityName="Geo" 
				keyField="geoId" 
				desField="geoName"
				/>	
			<#--	
			<@readonlyInput 
				id="webAddress"
				label=uiLabelMap.webAddress
				value=dataImportLead.webAddress
				isHiddenInput=false
				/>												
				-->
				<@readonlyInput 
				id="note"
				label=uiLabelMap.note
				value=dataImportLead.note
				isHiddenInput=false
				/>	
			</div>
			
		</div>
						
	</div>
	
	<div class="col-md-4 col-sm-4">
				
		<div class="portlet-body form">
						
			<div class="form-body">
			
			<#assign primaryPhoneCountryCode ="">
			<#assign primaryPhoneNumber ="">
			<#assign primaryPhonVal ="">
			<#if dataImportLead.primaryPhoneNumber?has_content || dataImportLead.primaryPhoneCountryCode?has_content>
			  <#if dataImportLead.primaryPhoneCountryCode?has_content> 
			      <#assign primaryPhoneCountryCode=dataImportLead.primaryPhoneCountryCode/>
			  </#if>
			  <#if dataImportLead.primaryPhoneNumber?has_content> 
			      <#assign primaryPhoneNumber=dataImportLead.primaryPhoneNumber/>
			  </#if>
			  <#assign primaryPhonVal=primaryPhoneCountryCode+" "+primaryPhoneNumber/>
			  <#else> 
			  <#assign primaryPhonVal ="">
			</#if>
			<@readonlyInput 
				id="primaryPhoneNumber"
				label=uiLabelMap.phoneNumber1
				value=primaryPhonVal
				isHiddenInput=false
				/>	
			
			<@readonlyInput 
				id="address1"
				label=uiLabelMap.address
				value=dataImportLead.address1
				isHiddenInput=false
				/>	
			
			<@readonlyInput 
				id="stateProvinceGeoId"
				label=uiLabelMap.state
				value=dataImportLead.stateProvinceGeoId
				isHiddenInput=false
				displayEntityName="Geo" 
				keyField="geoId" 
				desField="geoName"
				/>
				
			</div>
			
		</div>
						
	</div>
	
</div>

<div class="page-header">
	<h2 class="float-left">Other Details</h2>
</div>

<div class="row padding-r">

	<div class="col-md-4 col-sm-4">
				
		<div class="portlet-body form">
						
			<div class="form-body">
			
			<@readonlyInput 
				id="permanentAcccountNumber"
				label=uiLabelMap.permanentAcccountNumber
				value=dataImportLead.permanentAcccountNumber
				isHiddenInput=false
				/>
				
			<@readonlyInput 
				id="otherBankBalance"
				label=uiLabelMap.otherBankBalance
				value=dataImportLead.otherBankBalance
				isHiddenInput=false
				/>		
				
			<@readonlyInput 
				id="productsValueInOthBank"
				label=uiLabelMap.productsValueInOthBank
				value=dataImportLead.productsValueInOthBank
				isHiddenInput=false
				/>		
				
			</div>
			
		</div>
						
	</div>
	
	<div class="col-md-4 col-sm-4">
				
		<div class="portlet-body form">
						
			<div class="form-body">
			
			<@readonlyInput 
				id="businessRegNo"
				label=uiLabelMap.businessRegNo
				value=dataImportLead.businessRegNo
				isHiddenInput=false
				/>	
			
			<@readonlyInput 
				id="productsHeldInOthBank"
				label=uiLabelMap.productsHeldInOthBank
				value=dataImportLead.productsHeldInOthBank
				isHiddenInput=false
				displayEntityName="Enumeration" 
				keyField="enumId" 
				desField="description"
				/>		
			
			<@readonlyInput 
				id="paidupCapital"
				label=uiLabelMap.paidupCapital
				value=dataImportLead.paidupCapital
				isHiddenInput=false
				/>	
			
			</div>
			
		</div>
						
	</div>
	
	<div class="col-md-4 col-sm-4">
				
		<div class="portlet-body form">
						
			<div class="form-body">
			
			<@readonlyInput 
				id="otherBankName"
				label=uiLabelMap.otherBankName
				value=dataImportLead.otherBankName
				isHiddenInput=false
				displayEntityName="Enumeration" 
				keyField="enumId" 
				desField="description"
				/>	
				
			<@readonlyInput 
				id="authorisedCap"
				label=uiLabelMap.authorisedCap
				value=dataImportLead.authorisedCap
				isHiddenInput=false
				/>			
			
			</div>
			
		</div>
						
	</div>
	
</div>

<div class="page-header">
	<h2 class="float-left">Lead Assignment</h2>
</div>

<div class="row padding-r">

	<div class="col-md-4 col-sm-4">
				
		<div class="portlet-body form">
						
			<div class="form-body">
			
			<div class="form-group row">
				<label class="col-md-4 col-form-label" for="leadAssignTo">${uiLabelMap.leadAssignTo!}</label>
			   	<div class="col-md-7">
			   		<label class="col-form-label input-sm fw">
			   			<span id="leadAssignTo">
			         		<#if personResponsible?exists && personResponsible?has_content>${personResponsible?if_exists}<#else>${uiLabelMap.CommonNone}</#if> 
			         	</span>
			   		</label>	
			      	<#-- <span class="fa fa-user-o btn btn-xs btn-primary" data-toggle="modal" data-target="#teamMemberModal" alt="Reassign" title="Reassign"></span> -->
			   	</div>
			</div>
			<#-- 
			<@readonlyInput 
				id="leadAssignTo"
				label=uiLabelMap.leadAssignTo
				value=personResponsible
				isHiddenInput=false
				/>
			 -->
			 
			<@readonlyInput 
				id="segment"
				label=uiLabelMap.segment
				value=dataImportLead.segment
				isHiddenInput=false
				/>
			
			</div>
			
		</div>
						
	</div>
	
	<div class="col-md-4 col-sm-4">
				
		<div class="portlet-body form">
						
			<div class="form-body">
			<#assign leadAssignBy = "${dataImportLead?if_exists.leadAssignBy?if_exists}">
			<#assign partyRow = delegator.findOne("Person", {"partyId" : leadAssignBy}, true)!>
			<#if personResponsibleAssignBy?has_content>
			<#else>
			  <#assign personResponsibleAssignBy = "${partyRow?if_exists.firstName!} ${partyRow?if_exists.lastName!}">
			  </#if>
			<@readonlyInput 
				id="leadAssignBy"
				label=uiLabelMap.leadAssignBy
				value=personResponsibleAssignBy
				isHiddenInput=false
				/>
			
			<@readonlyInput 
				id="liabOrAsset"
				label=uiLabelMap.liabOrAsset
				value=dataImportLead.liabOrAsset
				isHiddenInput=false
				displayEntityName="Enumeration" 
				keyField="enumId" 
				desField="description"
				/>			
			
			</div>
			
		</div>
						
	</div>
	
	<div class="col-md-4 col-sm-4">
				
		<div class="portlet-body form">
						
			<div class="form-body">
			
			<@readonlyInput 
				id="jobFamily"
				label=uiLabelMap.jobFamily
				value=dataImportLead.jobFamily
				isHiddenInput=false
				displayEntityName="Enumeration" 
				keyField="enumId" 
				desField="description"
				/>		
			
			</div>
			
		</div>
						
	</div>
	
</div>

<div class="page-header">
	<h2 class="float-left">Tracking Details</h2>
</div>

<div class="row padding-r">

	<div class="col-md-4 col-sm-4">
				
		<div class="portlet-body form">
						
			<div class="form-body">
			
			<#if (loggedUserPositionType?has_content && loggedUserPositionType == "DBS_TC") || otherUserPositionType>	
			<@readonlyInput 
				id="teleCallingStatus"
				label=uiLabelMap.teleCallingStatus
				value=dataImportLead.teleCallingStatus
				isHiddenInput=false
				displayEntityName="Enumeration" 
				keyField="enumId" 
				desField="description"
				/>	
				
			<@readonlyInput 
				id="teleCallingSubStatus"
				label=uiLabelMap.teleCallingSubStatus
				value=dataImportLead.teleCallingSubStatus
				isHiddenInput=false
				displayEntityName="Enumeration" 
				keyField="enumId" 
				desField="description"
				/>	
			</#if>			
			
			<#if (loggedUserPositionType?has_content && (loggedUserPositionType == "DBS_RM" || loggedUserPositionType == "DBS_PM" || loggedUserPositionType == "DBS_LBRM" || loggedUserPositionType == "DBS_ARM" || loggedUserPositionType == "DBS_CL")) || otherUserPositionType>		
			<@readonlyInput 
				id="rmCallingStatus"
				label=uiLabelMap.rmCallingStatus
				value=dataImportLead.rmCallingStatus
				isHiddenInput=false
				displayEntityName="Enumeration" 
				keyField="enumId" 
				desField="description"
				/>	
				
			<@readonlyInput 
				id="rmCallingSubStatus"
				label=uiLabelMap.rmCallingSubStatus
				value=dataImportLead.rmCallingSubStatus
				isHiddenInput=false
				displayEntityName="Enumeration" 
				keyField="enumId" 
				desField="description"
				/>	
			</#if>					
			
			</div>
			
		</div>
						
	</div>
	
	<div class="col-md-4 col-sm-4">
				
		<div class="portlet-body form">
						
			<div class="form-body">
			
			<#if (loggedUserPositionType?has_content && loggedUserPositionType == "DBS_TC") || otherUserPositionType>	
			<@readonlyInput 
				id="teleCallingRemarks"
				label=uiLabelMap.teleCallingRemarks
				value=dataImportLead.teleCallingRemarks
				isHiddenInput=false
				/>
			</#if>		
			
			<#if (loggedUserPositionType?has_content && (loggedUserPositionType == "DBS_RM" || loggedUserPositionType == "DBS_PM" || loggedUserPositionType == "DBS_LBRM" || loggedUserPositionType == "DBS_ARM" || loggedUserPositionType == "DBS_CL")) || otherUserPositionType>				
			<@readonlyInput 
				id="rmCallingRemarks"
				label=uiLabelMap.rmCallingRemarks
				value=dataImportLead.rmCallingRemarks
				isHiddenInput=false
				/>	
			</#if>		
				
			</div>
			
		</div>
						
	</div>
	
	<div class="col-md-4 col-sm-4">
				
		<div class="portlet-body form">
						
			<div class="form-body">
			
			<@readonlyInput 
				id="noOfAttempt"
				label=uiLabelMap.noOfAttempt
				value=dataImportLead.noOfAttempt
				isHiddenInput=false
				/>	
			
			</div>
			
		</div>
						
	</div>
	
</div>

<script>
$("#updateCallStatus").click(function () {
   var form = document.getElementById("callStatusUpdate");
   var callStatus = $("#callStatusForm").val();
   var callBackDate = $("#callBackDateForm").val();
   if((callStatus != null && callStatus != "") || (callBackDate != null && callBackDate != "")) {
      form.callStatus.value = callStatus;
      form.callBackDate.value = callBackDate;
      form.submit();
   } else {
      $.notify({
      message : '<p>Select Call Status or Call Back Date</p>',
   });
   }
});
</script>

<#-- view lead new layout [end] -->

<div class="clearfix"> </div>
<form method="post" action="createLead" id="createLeadForm" class="form-horizontal" name="createLeadForm" novalidate="novalidate" data-toggle="validator">
  <input type="hidden" name="duplicatingPartyId">
  <input type="hidden" name=" ">
  <input type="hidden" name="">
  <div class="panel-group" id="accordionMenu" role="tablist" aria-multiselectable="true">
  
  <#-- Call Status History -->
  <div class="panel panel-default">
   <div class="panel-heading" role="tab" id="headingTwo">
      <h4 class="panel-title">
         <a class="panel-collapse collapse show" role="button" data-toggle="collapse" data-parent="#accordionMenu" href="#callHistory" aria-expanded="false" aria-controls="headingTwo">
         Call Status History
         </a>
      </h4>
   </div>
   <div id="callHistory" class="panel-collapse collapse show" data-parent="#accordionMenu" aria-labelledby="CallHistory">
      <div class="panel-body">
         ${screens.render("component://crm/webapp/widget/crm/screens/leads/LeadScreens.xml#callHistory")}
      </div>
   </div>
  </div>

    <div class="panel panel-default">
      <div class="panel-heading" role="tab" id="headingTwo">
        <h4 class="panel-title">
          <a class="collapsed" role="button" data-toggle="collapse" data-parent="#accordionMenu" href="#PendingActivities" aria-expanded="false" aria-controls="headingTwo">
          Pending Activities
          </a>
        </h4>
      </div>
      <div id="PendingActivities" class="panel-collapse collapse" data-parent="#accordionMenu" aria-labelledby="PendingActivities">
        <div class="panel-body">
          <p class="float-right">   <button type="reset" class="btn btn-xs btn-primary mt"> New Event</button>
            <button type="reset" class="btn btn-xs btn-primary mt"> New Activity</button>
          </p>
          <div class="table-responsive">
            <table class="table table-striped">
              <thead>
                <tr>
                  <th>Activity</th>
                  <th>Sequence Number</th>
                  <th>Status</th>
                  <th>Resource</th>
                  <th>Scheduled Date</th>
                  <th>Due Date</th>
                  <th>Type</th>
                  <th>Purpose</th>
                </tr>
              </thead>
              <tbody>
                <tr>
                  <td colspan="8">No Records Available</td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </div>
    
    <#-- Attachements and Book mark History -->
    <#include "component://crm/webapp/crm/common/bookMark.ftl"/>

    <div class="panel panel-default">
      <div class="panel-heading" role="tab" id="headingTwo">
        <h4 class="panel-title">
          <a class="collapsed" role="button" data-toggle="collapse" data-parent="#accordionMenu" href="#LeadHistory" aria-expanded="false" aria-controls="headingTwo">
          Lead Status History
          </a>
        </h4>
      </div>
      <div id="LeadHistory" class="panel-collapse collapse" data-parent="#accordionMenu" aria-labelledby="PendingActivities">
        <div class="panel-body">
          <div class="table-responsive">
            <table class="table table-striped" id="leadHistoryTable">
              <thead>
                <tr>
                  <th>${uiLabelMap.createdStamp}</th>
                  <th>${uiLabelMap.fromState}</th>
                  <th>${uiLabelMap.toState}</th>
                  <th>${uiLabelMap.modifiedBy}</th>
                </tr>
              </thead>
              <tbody>
              <#if leadContactHistory?exists && leadContactHistory?has_content>
                <#list leadContactHistory as leadContactHistoryGV>
                  <tr>
                    <td>${leadContactHistoryGV.createdStamp?if_exists}</td>
                    <td>${leadContactHistoryGV.leadContactStageIdFrom?if_exists}</td>
                    <td>${leadContactHistoryGV.leadContactStageIdTo?if_exists}</td>
                    <td>${leadContactHistoryGV.modifiedBy?if_exists}</td>
                  </tr>
                </#list>
              </#if>
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </div>
     <div class="panel panel-default">
      <div class="panel-heading" role="tab" id="headingTwo">
        <h4 class="panel-title">
          <a class="collapsed" role="button" data-toggle="collapse" data-parent="#accordionMenu" href="#LeadReassignHistory" aria-expanded="false" aria-controls="headingTwo">
          Reassign Status History
          </a>
        </h4>
      </div>
      <div id="LeadReassignHistory" class="panel-collapse collapse" data-parent="#accordionMenu" aria-labelledby="LeadReassignHistoryMenu">
        <div class="panel-body">
          <div class="table-responsive">
            <table class="table table-striped" id="LeadReassignHistoryTD">
              <thead>
                <tr>
                  
                  <th>RM From</th>
                  <th>RM To</th>
                  <th>Modified By</th>
                  <th>Changed Date</th>
                </tr>
              </thead>
              <tbody>
              <#if rmHistoryList?exists && rmHistoryList?has_content>
                <#list rmHistoryList as rmHistoryListGV>
                  <tr>
                    <td>${rmHistoryListGV.rmFromName?if_exists}</td>
                    <td>${rmHistoryListGV.rmToName?if_exists}</td>
                    <td>${rmHistoryListGV.rmUserLoginName?if_exists}</td>
                    <td>${rmHistoryListGV.modifiedDate?if_exists}</td>
                  </tr>
                </#list>
              </#if>
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </div>
    
    
    
  </div>
</form>
<script type="text/javascript">
function leadStatusAjax(statusData){
    if(statusData !="placeHolder"){
      var leadStatusId = $('select#leadStatusId option:selected').val();
      $('#statusId').val(leadStatusId);
      document.qualifyLeadForm.submit();
    }
}
function leadContactStageAjax(status){
    var leadContactStatusId = document.getElementById("leadContactStatusId");
    leadContactStatusId.value = status;
    document.updateLeadContactStatus.submit();
}

$(document).ready(function() {

$('#leadHistoryTable').DataTable({
  	"pageLength": 3,
  	"order": [[ 0, "DESC" ]],
});

$('#lead-disable-btn').on('click', function(){
	var disableReason = $("#disableReason").val();
	if (!disableReason) {
		showAlert ("error", "Please select disable reason");
		return;
	} else {
		$('#disabledLeadForm input[name="disableReason"]').val( disableReason );
		$("#disabledLeadForm").submit();
	}
});        
    
});

$(function(){
       $(document).ready(function(){
           $("#LeadReassignHistoryTD").DataTable({
               "lengthMenu" : false,
               "filter" : false,
               "lengthChange" : false,
               "pageLength" : 5,
               //"ordering": false
               "order": [[ 3, "desc" ]]
           });
       });
   });
</script>

