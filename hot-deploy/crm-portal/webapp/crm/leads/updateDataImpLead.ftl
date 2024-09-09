<#include "component://homeapps/webapp/homeapps/lib/ofbizFormMacros.ftl"/>
<div class="page-header border-b">
	<h1 class="float-left">
		<#if actionType?has_content && (actionType == "UPDATE" || actionType == "STAGING")>
			${uiLabelMap.Edit}
		<#else>
			${uiLabelMap.Create}	
		</#if>
		<#if actionType?has_content && (actionType == "UPDATE" || actionType == "CREATE")>
			${uiLabelMap.Lead}
		<#else>
			${uiLabelMap.DataImportLead!}
		</#if>
		<#if leadName?has_content>
		- ${leadName}
		</#if>
	</h1>
	<div class="float-right">
	
		<#if actionType?has_content && (actionType == "UPDATE" || actionType == "STAGING") && (importAuditCount?exists && importAuditCount > 0 )>
		<a href="#" class="btn btn-xs btn-primary tooltips view-audit-message" data-leadId="${dataImportLead?if_exists.leadId!}" data-auditType="VAT_LEAD_IMPORT" data-original-title="${importAuditLogTitle!}"><strong>${importAuditCount!}</strong></a>
		</#if>
		
	    <#if dedupAuditCount?exists>
          	<a href="#" class="btn btn-xs btn-primary tooltips view-dedup-message" data-leadId="${dataImportLead?if_exists.leadId!}" data-auditType="VAT_LEAD_DEDUP" data-original-title="${dedupAuditLogTitle!}"><strong>${dedupAuditCount!}</strong></a>
	    </#if>
	
		<#if actionType?has_content && (actionType == "UPDATE" || actionType == "STAGING" || actionType == "CREATE")>
		<strong>Source: </strong>${dataImportLead?if_exists.source!} 
		</#if>
		<#if codeList?has_content >
		, <strong>Error Codes: </strong>
  			<#list codeList.entrySet() as entry>
  				<span class="tooltips" data-html="true" data-original-title="${entry.value}">[${entry.key}]</span>
  			</#list>
  		</#if>
	</div>
</div>
<#assign hasEnumPermission = request.getAttribute("security").hasEntityPermission("CRMSFA_ENUM", "_CREATE", userLogin)>
<form role="form" class="form-horizontal" action="<@ofbizUrl>updateDataImpLead</@ofbizUrl>" encType="multipart/form-data" method="post" data-toggle="validator">

<input type="hidden" name="leadId" value="${leadId!}">
<input type="hidden" name="actionType" value="${actionType!}">
<input type="hidden" name="backUrl" value="${backUrl!}">

<#-- <input type="hidden" id="virtualTeamId" name="virtualTeamId" value="${loggedUserVirtualTeamId!}"> -->	

<div class="page-header">
	<h2 class="float-left">Basic Company Details</h2>
	<div class="float-right">
		<div class="help-block with-errors">
			<ul class="list-unstyled">
				<li>First Name OR Key Contact Person are mandatory.</li>
				<li>Phone Number1 OR Address OR Email Address are mandatory.</li>
			</ul>
		</div>
	</div>
</div>

<div class="row padding-r">

	<div class="col-md-4 col-sm-4">
				
		<div class="portlet-body form">
						
			<div class="form-body">
			
			<@generalInput 
				id="companyName"
				label=uiLabelMap.companyName
				placeholder=uiLabelMap.companyName
				value=dataImportLead?if_exists.companyName
				required=true
				maxlength=255
				pattern="^[ A-Za-z0-9'@.!&:*()+-]*$"
				dataError="Please enter valid name"
				/>
				
			<div class="form-group row">
			   <label class="col-sm-4 col-form-label " for="parentCoDetails">${uiLabelMap.parentCoDetails!}</label>
			   <div class="col-sm-7">
			      <div class="input-group ">
			         <input class="form-control input-sm  " autocomplete="off"  value="${dataImportLead?if_exists.parentCoDetails!}" id="parentCoDetails" name="parentCoDetails" placeholder="${uiLabelMap.parentCoDetails!}" maxlength="60" type="text">
			         <span class="input-group-addon">
                    	<span class="glyphicon glyphicon-list-alt" data-toggle="modal" data-target="#parentAccountModal" id="findAccount">
                    	</span>
                    </span>
			      </div>
			   </div>
			</div>
			
			<@generalInput 
				id="salesTurnover"
				label=uiLabelMap.salesTurnover
				placeholder=uiLabelMap.salesTurnover
				value=dataImportLead?if_exists.salesTurnover?if_exists
				required=false
				dataError="Please enter only number or decimal"
				pattern="^[0-9]{0,}(\\.[0-9]{0,})?$"
				<#--dataError="Should be numbers and above lakhs"
				pattern="^[0-9]{0,}(\\.[0-9]{0,})?$"
				pattern="^[0-9]+$"
				delimiter =","
				grouplength = "3"-->
				
				/>		
			<#assign constitutionParameters = { "type-id": "constitution","name":"Constitution"}>
			<@dropdownInput 
				id="constitution"
				label=uiLabelMap.constitution
				options=constitutionList
				required=false
				value=dataImportLead?if_exists.constitution?if_exists
				allowEmpty=true
				dataLiveSearch=true
				lookup="Y"
				lookupTarget = "enumerationLookup"
				hasPermission=hasEnumPermission
				lookupParams=constitutionParameters
				/>		
			
			<@dropdownInput 
				id="source"
				label=uiLabelMap.source
				options=leadSourceList
				required=true
				value=dataImportLead?if_exists.source?if_exists
				allowEmpty=true
				dataLiveSearch=true
				/>
			<#-- 
			<#if actionType?has_content && (actionType == "CREATE")>
				<@dropdownInput 
				id="source"
				label=uiLabelMap.source
				options=leadSourceList
				required=true
				value=dataImportLead?if_exists.source?if_exists
				allowEmpty=true
				dataLiveSearch=true
				/>
			<#else>
				<@readonlyInput 
				id="source"
				label=uiLabelMap.source
				value=dataImportLead?if_exists.source?if_exists
				isHiddenInput=false
				displayEntityName="PartyIdentificationType" 
				keyField="partyIdentificationTypeId" 
				desField="description"
				/>
			</#if>		
			 -->
			<#assign constitutionParameters = { "type-id": "incorporationPlace","name":"Place Of Incorporation"}>
			<@dropdownInput 
				id="placeOfIncorporation"
				label=uiLabelMap.placeOfIncorporation
				options=incorporationPlaceList
				required=false
				value=dataImportLead?if_exists.placeOfIncorporation?if_exists
				allowEmpty=true
				dataLiveSearch=true
				lookup="Y"
				lookupTarget = "enumerationLookup"
				hasPermission=hasEnumPermission
				lookupParams=constitutionParameters
				/>																														
				
			</div>
			
		</div>
						
	</div>
	
	<div class="col-md-4 col-sm-4">
				
		<div class="portlet-body form">
						
			<div class="form-body">
			
			<@generalInput 
				id="firstName"
				label=uiLabelMap.firstName
				placeholder=uiLabelMap.firstName
				value=dataImportLead?if_exists.firstName?if_exists
				required=false
				maxlength=100
				pattern="^([^0-9]*)$"
				dataError="Please enter valid name"
				/>	
			<#assign salutation=""/>
				<#if dataImportLead?if_exists.title?if_exists?contains(".")>
				  <#assign salutation="${dataImportLead?if_exists.title?if_exists}"/>
				<#else>
				  <#assign enumeration = Static["org.ofbiz.entity.util.EntityUtil"].getFirst(delegator.findByAnd("Enumeration", {"enumCode" : "${dataImportLead?if_exists.title?if_exists}"}, [], false))?if_exists/>
				  <#assign salutation="${enumeration.enumId!}"/>
				</#if>
				
			<#assign salutationParameters = { "type-id": "salutation","name":"Salutation"}>
			<@dropdownInput 
				id="title"
				label=uiLabelMap.salutation
				options=titleList
				required=false
				value="${salutation!}"
				allowEmpty=true
				dataLiveSearch=true
				lookup="Y"
				lookupTarget = "enumerationLookup"
				hasPermission=hasEnumPermission
				lookupParams=salutationParameters
				/>			
			
			<#assign industryCategoryParameters = { "type-id": "industryCat","name":"Industry Category"}>
			<@dropdownInput 
				id="industryCat"
				label=uiLabelMap.industryCat
				options=industryCatList
				required=false
				value=dataImportLead?if_exists.industryCat?if_exists
				allowEmpty=true
				dataLiveSearch=true
				lookup="Y"
				lookupTarget = "enumerationLookup"
				hasPermission=hasEnumPermission
				lookupParams=industryCategoryParameters
				/>	
				
			<#assign importExportParameters = { "type-id": "customerTradingType","name":"Import/Export Customer"}>
			<@dropdownInput 
				id="customerTradingType"
				label=uiLabelMap.importExportCustomer
				options=customerTradingTypeList
				required=false
				value=dataImportLead?if_exists.customerTradingType?if_exists
				allowEmpty=true
				dataLiveSearch=true
				lookup="Y"
				lookupTarget = "enumerationLookup"
				hasPermission=hasEnumPermission
				lookupParams=importExportParameters
				/>		
				
			<#assign tcpNameParameters = { "type-id": "tcpName","name":"TCP Name"}>
			<@dropdownInput 
				id="tcpName"
				label=uiLabelMap.tcpName
				options=tcpNameList
				required=false
				value=dataImportLead?if_exists.tcpName?if_exists
				allowEmpty=true
				dataLiveSearch=true
				lookup="Y"
				lookupTarget = "enumerationLookup"
				hasPermission=hasEnumPermission
				lookupParams=tcpNameParameters
				/>
				
			<@generalInput 
				id="finacleId"
				label=uiLabelMap.finacleId
				placeholder=uiLabelMap.finacleId
				value=dataImportLead?if_exists.finacleId?if_exists
				required=false
				dataError="Please enter only number"
				pattern="^[0-9]{0,}$"
				/>		
				
			<@dropdownInput 
				id="leadScore"
				label=uiLabelMap.leadScore
				options=leadScoreList
				required=false
				value=dataImportLead?if_exists.leadScore?if_exists
				allowEmpty=true
				dataLiveSearch=true
				/>					
			
			</div>
			
		</div>
						
	</div>
	
	<div class="col-md-4 col-sm-4">
				
		<div class="portlet-body form">
						
			<div class="form-body">
			
			<@generalInput 
				id="lastName"
				label=uiLabelMap.lastName
				placeholder=uiLabelMap.lastName
				value=dataImportLead?if_exists.lastName?if_exists
				required=false
				maxlength=100
				pattern="^([^0-9]*)$"
				dataError="Please enter valid name"
				/>	
				
			<div class="form-group row">
			   <label class="col-sm-4 col-form-label " for="title">${uiLabelMap.dateOfIncorporation!}</label>
			   <div class="col-sm-7">
			      <@simpleDateInput 
					name="dateOfIncorporation"
					value=dataImportLead?if_exists.dateOfIncorporation?if_exists
					dateFormat="DD-MM-YYYY"
					/>
			   </div>
			</div>			
					
			<#assign industryParameters = { "type-id": "industry","name":"Industry"}>
			<@dropdownInput 
				id="industry"
				label=uiLabelMap.industry
				options=industryList
				required=false
				value=dataImportLead?if_exists.industry?if_exists
				allowEmpty=true
				dataLiveSearch=true
				lookup="Y"
				lookupTarget = "enumerationLookup"
				hasPermission=hasEnumPermission
				lookupParams=industryParameters
				/>				
			<#assign tallyUserTypeParameters = { "type-id": "tallyUserType","name":"Tally User Type"}>
			<@dropdownInput 
				id="tallyUserType"
				label=uiLabelMap.tallyUserType
				options=tallyUserTypeList
				required=false
				value=dataImportLead?if_exists.tallyUserType?if_exists
				allowEmpty=true
				dataLiveSearch=true
				lookup="Y"
				lookupTarget = "enumerationLookup"
				hasPermission=hasEnumPermission
				lookupParams=tallyUserTypeParameters
				/>	
				
			<@generalInput 
				id="noOfEmployees"
				label=uiLabelMap.noOfEmployees
				placeholder=uiLabelMap.noOfEmployees
				value=dataImportLead?if_exists.noOfEmployees?if_exists
				required=false
				dataError="Please enter only number"
				pattern="^[0-9]{0,}$"
				/>	
			
			<#assign designationParameters = { "type-id": "leadDesignation","name":"Designation"}>	
			<@dropdownInput 
				id="designation"
				label=uiLabelMap.designation
				options=designationList
				required=false
				value=dataImportLead?if_exists.designation?if_exists
				allowEmpty=true
				dataLiveSearch=true
				lookup="Y"
				lookupTarget = "enumerationLookup"
				hasPermission=hasEnumPermission
				lookupParams=designationParameters
				/>	
			
			<@dropdownInput 
				id="virtualTeamId"
				label=uiLabelMap.virtualTeam
				options=virtualTeamList
				required=false
				value=dataImportLead?if_exists.virtualTeamId?if_exists
				allowEmpty=true
				dataLiveSearch=true
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
			
			<@generalInput 
				id="keyContactPerson1"
				label=uiLabelMap.keyContactPerson1
				placeholder=uiLabelMap.keyContactPerson1
				value=dataImportLead?if_exists.keyContactPerson1?if_exists
				required=false
				maxlength=20
				pattern="^([^0-9]*)$"
				dataError="Please enter valid name"
				/>	
				
			<#-- <@generalInput 
				id="secondaryPhoneNumber"
				label=uiLabelMap.phoneNumber2
				placeholder=uiLabelMap.phoneNumber2
				value=dataImportLead?if_exists.secondaryPhoneNumber?if_exists
				required=false
				maxlength=60
				pattern="^[0-9]{0,}$"
				dataError="Please enter valid name"
				/>	-->
			<@generalInputSplitCol
			    colId1="secondaryPhoneCountryCode"
			    colId2="secondaryPhoneNumber"
				label=uiLabelMap.phoneNumber2
				colTooltip1 = uiLabelMap.countryCode2
				colTooltip2 = uiLabelMap.phoneNumber2
				value1=dataImportLead?if_exists.secondaryPhoneCountryCode?if_exists
				value2=dataImportLead?if_exists.secondaryPhoneNumber?if_exists
				required=false
				maxlength1=3
				maxlength2=10
				pattern1="^[+]?[0-9]*$"
				pattern2="^[0-9]{0,}$"
				dataError2="Enter valid phone number"
				dataError1="Enter valid country code"
				errorId = "secondaryPhone"
				onkeyup = "clearErrorMsg();"
			    /> 
			
			<@generalInput 
				id="postalCode"
				label=uiLabelMap.pinCode
				placeholder=uiLabelMap.pinCode
				value=dataImportLead?if_exists.postalCode?if_exists
				required=false
				dataError="Should accept 6 digits and numbers only"
				pattern="^\\d{6}$"
				/>	
						
			<@generalInput 
				id="address2"
				label=uiLabelMap.address2
				placeholder=uiLabelMap.address2
				value=dataImportLead?if_exists.address2?if_exists
				required=false
				/>		
				
			</div>
			
		</div>
						
	</div>
	
	<div class="col-md-4 col-sm-4">
				
		<div class="portlet-body form">
						
			<div class="form-body">
			
			<@generalInput 
				id="keyContactPerson2"
				label=uiLabelMap.keyContactPerson2
				placeholder=uiLabelMap.keyContactPerson2
				value=dataImportLead?if_exists.keyContactPerson2?if_exists
				required=false
				maxlength=20
				pattern="^([^0-9]*)$"
				dataError="Please enter valid name"
				/>	
			<#--pattern="^([a-zA-Z0-9._-]+)+@([a-zA-Z0-9_]+)+.([a-zA-Z]{2,3})$"-->	
			<@generalInput 
				id="emailAddress"
				label=uiLabelMap.emailAddress
				placeholder=uiLabelMap.emailAddress
				value=dataImportLead?if_exists.emailAddress?if_exists
				required=false
				maxlength=255
				inputType="email"
				dataError="Please enter valid email address"
				/>	
			
			<@dropdownInput 
				id="city"
				label=uiLabelMap.city
				required=true
				value=dataImportLead?if_exists.city?if_exists
				allowEmpty=true
				dataLiveSearch=true
				/>		
			
			<#--  
			<@generalInput 
				id="city"
				label=uiLabelMap.city
				placeholder=uiLabelMap.city
				value=dataImportLead?if_exists.city?if_exists
				required=true
				maxlength=100
				/>					
				
			<@generalInput 
				id="webAddress"
				label=uiLabelMap.webAddress
				placeholder=uiLabelMap.webAddress
				value=dataImportLead?if_exists.webAddress?if_exists
				required=false
				maxlength=255
				inputType="url"
				/>
				-->
				<@textareaInput 
				id="note"
				label=uiLabelMap.note
				placeholder=uiLabelMap.note
				rows="3"
				required=false
				value=dataImportLead?if_exists.note?if_exists
				/>		
			
			</div>
			
		</div>
						
	</div>
	
	<div class="col-md-4 col-sm-4">
				
		<div class="portlet-body form">
						
			<div class="form-body">
			
			<#-- <@generalInput 
				id="primaryPhoneNumber"
				label=uiLabelMap.phoneNumber1
				placeholder=uiLabelMap.phoneNumber1
				value=dataImportLead?if_exists.primaryPhoneNumber?if_exists
				required=false
				maxlength=60
				pattern="^[0-9]{0,}$"
				dataError="Enter a valid Mobile Number"
				/> -->
			<@generalInputSplitCol
			    colId1="primaryPhoneCountryCode"
			    colId2="primaryPhoneNumber"
				label=uiLabelMap.phoneNumber1
				colTooltip1 = uiLabelMap.countryCode1
				colTooltip2 = uiLabelMap.phoneNumber1
				value1=dataImportLead?if_exists.primaryPhoneCountryCode?if_exists
				value2=dataImportLead?if_exists.primaryPhoneNumber?if_exists
				required=false
				maxlength1=3
				maxlength2=10
				pattern1="^[+]?[0-9]*$"
				pattern2="^[0-9]{0,}$"
				dataError2="Enter valid phone number"
				dataError1="Enter valid country code"
				errorId = "primaryPhone"
				onkeyup = "clearErrorMsg();"
			    />
			<@generalInput 
				id="address1"
				label=uiLabelMap.address
				placeholder=uiLabelMap.address
				value=dataImportLead?if_exists.address1?if_exists
				required=false
				<#--pattern="^[a-zA-Z0-9 ]+$"
				dataError="Please enter character and numeric only" -->
				/>
			
			<@dropdownInput 
				id="stateProvinceGeoId"
				label=uiLabelMap.state
				options=indiaStateList
				required=true
				value=dataImportLead?if_exists.stateProvinceGeoId?if_exists
				allowEmpty=true
				dataLiveSearch=true
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
			
			<@generalInput 
				id="permanentAcccountNumber"
				label=uiLabelMap.permanentAcccountNumber
				placeholder=uiLabelMap.permanentAcccountNumber
				value=dataImportLead?if_exists.permanentAcccountNumber?if_exists
				required=false
				maxlength=10
				pattern="^(?=.*[0-9])(?=.*[a-zA-Z])([a-zA-Z0-9]){10}$"
				dataError="Should accept 10 characters and Alphanumeric only"
				/>
				
			<@generalInput 
				id="otherBankBalance"
				label=uiLabelMap.otherBankBalance
				placeholder=uiLabelMap.inINRLakhs
				value=dataImportLead?if_exists.otherBankBalance?if_exists
				required=false
				dataError="Should be numbers and above lakhs"
				pattern="^[0-9]{6,}(\\.[0-9]{0,})?$"
				/>		
				
			<@generalInput 
				id="productsValueInOthBank"
				label=uiLabelMap.productsValueInOthBank
				placeholder=uiLabelMap.inINRLakhs
				value=dataImportLead?if_exists.productsValueInOthBank?if_exists
				required=false
				dataError="Should be numbers and above lakhs"
				pattern="^[0-9]{6,}(\\.[0-9]{0,})?$"
				/>		
				
			</div>
			
		</div>
						
	</div>
	
	<div class="col-md-4 col-sm-4">
				
		<div class="portlet-body form">
						
			<div class="form-body">
			
			<@generalInput 
				id="businessRegNo"
				label=uiLabelMap.businessRegNo
				placeholder=uiLabelMap.businessRegNo
				value=dataImportLead?if_exists.businessRegNo?if_exists
				required=false
				maxlength=250
				/>	
				
			<#assign productsHeldInOthBankParameters = { "type-id": "prodPhob","name":"Products held in Other Banks"}>
			<@dropdownInput 
				id="productsHeldInOthBank"
				label=uiLabelMap.productsHeldInOthBank
				options=prodPhobList
				required=false
				value=dataImportLead?if_exists.productsHeldInOthBank?if_exists
				allowEmpty=true
				dataLiveSearch=true
				lookup="Y"
				lookupTarget = "enumerationLookup"
				hasPermission=hasEnumPermission
				lookupParams=productsHeldInOthBankParameters
				/>	
			
			<@generalInput 
				id="paidupCapital"
				label=uiLabelMap.paidupCapital
				placeholder=uiLabelMap.inINRLakhs
				value=dataImportLead?if_exists.paidupCapital?if_exists
				required=false
				dataError="Should be numbers and above lakhs"
				pattern="^[0-9]{6,}(\\.[0-9]{0,})?$"
				/>	
			
			</div>
			
		</div>
						
	</div>
	
	<div class="col-md-4 col-sm-4">
				
		<div class="portlet-body form">
						
			<div class="form-body">
			<#assign otherBankNameParameters = { "type-id": "existingBank","name":"Other Banks"}>
			<@dropdownInput 
				id="otherBankName"
				label=uiLabelMap.otherBankName
				options=existingBankList
				required=false
				value=dataImportLead?if_exists.otherBankName?if_exists
				allowEmpty=true
				dataLiveSearch=true
				lookup="Y"
				lookupTarget = "enumerationLookup"
				hasPermission=hasEnumPermission
				lookupParams=otherBankNameParameters
				/>	
			
			<@generalInput 
				id="authorisedCap"
				label=uiLabelMap.authorisedCap
				placeholder=uiLabelMap.inINRLakhs
				value=dataImportLead?if_exists.authorisedCap?if_exists
				required=false
				dataError="Should be numbers and above lakhs"
				pattern="^[0-9]{6,}(\\.[0-9]{0,})?$"
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
			
			<@dropdownInput 
				id="segment"
				label=uiLabelMap.segment
				options=segmentList
				required=true
				value=dataImportLead?if_exists.segment?if_exists
				allowEmpty=true
				dataLiveSearch=true
				/>
				<#assign leadAssignTo = "${dataImportLead?if_exists.leadAssignTo?if_exists}">
				<#if !leadAssignTo?has_content> 
                  <#assign leadAssignTo = "${userLogin?if_exists.partyId!}">
                </#if>
				<#assign isOneBankIdExists = delegator.findOne("UserLogin", {"userLoginId" : leadAssignTo}, true)!>
                <#if isOneBankIdExists?exists && isOneBankIdExists?has_content>
                    <#assign leadAssignTo = "${isOneBankIdExists.partyId}" >
                </#if>
			<@dropdownInput 
				id="leadAssignTo"
				label=uiLabelMap.leadAssignTo
				options=rmList
				required=false
				value=leadAssignTo
				allowEmpty=true
				dataLiveSearch=true
				/>		
				
			</div>
			
		</div>
						
	</div>
	
	<div class="col-md-4 col-sm-4">
				
		<div class="portlet-body form">
						
			<div class="form-body">
			
			<#assign liabOrAssetParameters = { "type-id": "liabOrAsset","name":"Asset / Liability Lead"}>
			<@dropdownInput 
				id="liabOrAsset"
				label=uiLabelMap.liabOrAsset
				options=liabOrAssetList
				required=true
				value=dataImportLead?if_exists.liabOrAsset?if_exists
				allowEmpty=true
				dataLiveSearch=true
				lookup="Y"
				lookupTarget = "enumerationLookup"
				hasPermission=hasEnumPermission
				lookupParams=liabOrAssetParameters
				/>	
						
		  <#if !actionType?has_content || actionType != "CREATE">
			<@checkboxInput 	
			    id="isNotDuplicate"
				label=uiLabelMap.isNotDuplicate
				value=""
			/>
		 </#if>		
			</div>
			
		</div>
						
	</div>
	
	<div class="col-md-4 col-sm-4">
				
		<div class="portlet-body form">
						
			<div class="form-body">
			<#assign jobFamilyParameters = { "type-id": "jobFamily","name":"Job Family"}>
			<@dropdownInput 
				id="jobFamily"
				label=uiLabelMap.jobFamily
				options=jobFamilyList
				required=true
				value=dataImportLead?if_exists.jobFamily?if_exists
				allowEmpty=true
				dataLiveSearch=true
				lookup="Y"
				lookupTarget = "enumerationLookup"
				hasPermission=hasEnumPermission
				lookupParams=jobFamilyParameters
				/>	
			
			</div>
			
		</div>
						
	</div>
	
</div>

<#-- 
<div class="row padding-r">

	<div class="col-md-4 col-sm-4">
				
		<div class="portlet-body form">
						
			<div class="form-body">
			
			
				<div class="form-group row">
			   	<label class="col-md-4 col-form-label" for="leadAssignTo">${uiLabelMap.leadAssignTo!}</label>
			   	<div class="col-sm-7">
			      	<div class="input-group ">
			         	<input class="form-control input-sm  " autocomplete="off"  value="${dataImportLead?if_exists.leadAssignTo!}" id="leadAssignTo" name="leadAssignTo" placeholder="${uiLabelMap.leadAssignTo!}" maxlength="60" type="text">
			         	<span class="input-group-addon">
                    		<span class="glyphicon glyphicon-list-alt" data-toggle="modal" data-target="#teamMemberModal" id="findAccount">
                    		</span>
                    	</span>
			      	</div>
			   		</div>
			   		
			   		
				</div>
				
			</div>
			
		</div>
						
	</div>
	
</div>	
 -->

<#if !actionType?has_content || actionType != "CREATE">

<div class="page-header">
	<h2 class="float-left">Tracking Details</h2>
</div>

<div class="row padding-r">

	<div class="col-md-4 col-sm-4">
				
		<div class="portlet-body form">
						
			<div class="form-body">
			
			<#if (loggedUserPositionType?has_content && loggedUserPositionType == "DBS_TC") || otherUserPositionType>	
			
			<#assign teleCallingStatusParameters = { "type-id": "teleCallStatus","name":"Telecaller Status"}>
			<@dropdownInput 
				id="teleCallingStatus"
				label=uiLabelMap.teleCallingStatus
				options=teleCallStatusList
				required=false
				value=dataImportLead?if_exists.teleCallingStatus?if_exists
				allowEmpty=true
				dataLiveSearch=true
				lookup="Y"
				lookupTarget = "enumerationLookup"
				hasPermission=hasEnumPermission
				lookupParams=teleCallingStatusParameters
				/>	
			
			<#assign teleCallingSubStatusParameters = { "type-id": "teleCallSubStatus","name":"Telecaller Status"}>
			<@dropdownInput 
				id="teleCallingSubStatus"
				label=uiLabelMap.teleCallingSubStatus
				required=false
				value=dataImportLead?if_exists.teleCallingSubStatus?if_exists
				allowEmpty=true
				dataLiveSearch=true
				lookup="Y"
				lookupTarget = "enumerationLookup"
				hasPermission=hasEnumPermission
				lookupParams=teleCallingSubStatusParameters
				/>	
			</#if>			
			
			<#if (loggedUserPositionType?has_content && (loggedUserPositionType == "DBS_RM" || loggedUserPositionType == "DBS_PM" || loggedUserPositionType == "DBS_LBRM" || loggedUserPositionType == "DBS_ARM" || loggedUserPositionType == "DBS_CL")) || otherUserPositionType>		
			<@dropdownInput 
				id="rmCallingStatus"
				label=uiLabelMap.rmCallingStatus
				required=false
				value=dataImportLead?if_exists.rmCallingStatus?if_exists
				allowEmpty=true
				dataLiveSearch=true
				/>	
				
			<@dropdownInput 
				id="rmCallingSubStatus"
				label=uiLabelMap.rmCallingSubStatus
				required=false
				value=dataImportLead?if_exists.rmCallingSubStatus?if_exists
				allowEmpty=true
				dataLiveSearch=true
				/>	
			</#if>				
			
			</div>
			
		</div>
						
	</div>
	
	<div class="col-md-4 col-sm-4">
				
		<div class="portlet-body form">
						
			<div class="form-body">
			
			<#if (loggedUserPositionType?has_content && loggedUserPositionType == "DBS_TC") || otherUserPositionType>	
			<@textareaInput 
				id="teleCallingRemarks"
				label=uiLabelMap.teleCallingRemarks
				placeholder=uiLabelMap.teleCallingRemarks
				rows="3"
				required=false
				value=dataImportLead?if_exists.teleCallingRemarks?if_exists
				/>
			</#if>		
			
			<#if (loggedUserPositionType?has_content && (loggedUserPositionType == "DBS_RM" || loggedUserPositionType == "DBS_PM" || loggedUserPositionType == "DBS_LBRM" || loggedUserPositionType == "DBS_ARM" || loggedUserPositionType == "DBS_CL")) || otherUserPositionType>		
			<@textareaInput 
				id="rmCallingRemarks"
				label=uiLabelMap.rmCallingRemarks
				placeholder=uiLabelMap.rmCallingRemarks
				rows="3"
				required=false
				value=dataImportLead?if_exists.rmCallingRemarks?if_exists
				/>
			</#if>			
				
			</div>
			
		</div>
						
	</div>
	
	<div class="col-md-4 col-sm-4">
				
		<div class="portlet-body form">
						
			<div class="form-body">
			
			<@generalInput 
				id="noOfAttempt"
				label=uiLabelMap.noOfAttempt
				placeholder=uiLabelMap.noOfAttempt
				value=dataImportLead?if_exists.noOfAttempt?if_exists
				required=false
				dataError="Please enter only number"
				pattern="^[0-9]{0,}$"
				/>	
			
			</div>
			
		</div>
						
	</div>
	
</div>
</#if>

<div class="row padding-r">

	<div class="col-md-12 col-sm-12">
		<#if actionType?has_content && (actionType == "UPDATE" || actionType == "STAGING")>
			<@fromCommonAction showCancelBtn=true showClearBtn=false submitLabel="Update" cancelUrl="${backUrl!}" cancelLabel="Back" onclick="return onSubmitValidate(this);"/>
		<#else>
			<@fromCommonAction showCancelBtn=false showClearBtn=true submitLabel="Create" onclick="javascript:return onSubmitValidate(this);"/>
		</#if>
	</div>

</div>

</form>		

<#include "component://crm/webapp/crm/leads/leadModels.ftl">
<#-- <#include "component://crm/webapp/crm/common/teamMembersModal.ftl" /> -->

<#include "component://crm/webapp/crm/common/enumerationLookup.ftl">

<script src='https://s3-us-west-2.amazonaws.com/s.cdpn.io/3/creditablecardtype.js'></script>
<script src='https://s3-us-west-2.amazonaws.com/s.cdpn.io/3/politespace.js'></script>
<script src="/bootstrap/js/index.js"></script>
<script>

var selectedCity;

function clearErrorMsg(){
  var numbers = /^[0-9]+$/;
  var primaryPhoneNumber =  $("#primaryPhoneNumber").val();
  var secondaryPhoneNumber =  $("#secondaryPhoneNumber").val();
  var primaryPhone_error = $("#primary_error").val();
  var secondaryPhone_error = $("#secondary_error").val();
  if(primaryPhoneNumber!='' && primaryPhone_error ==0){
    if(primaryPhoneNumber.match(numbers)){  $("#primaryPhone_error").html('');}
  }
  if(secondaryPhoneNumber!='' && secondaryPhone_error ==0){
    if(secondaryPhoneNumber.match(numbers)){  $("#secondaryPhone_error").html('');}
  }
  
}
function onSubmitValidate(){
   var primaryPhoneCountryCode =  $("#primaryPhoneCountryCode").val();
   var secondaryPhoneCountryCode =  $("#secondaryPhoneCountryCode").val();
   var primaryPhoneNumber =  $("#primaryPhoneNumber").val();
   var secondaryPhoneNumber =  $("#secondaryPhoneNumber").val();
   var primaryFlag="N";
   var secondaryFlag="N";
   if(primaryPhoneCountryCode !='' || secondaryPhoneCountryCode !=''){
     if(primaryPhoneCountryCode !='' && primaryPhoneNumber ==""){
       $("#primaryPhone_error").html('');
       $("#primaryPhone_error").append('<ul class="list-unstyled text-danger"><li id="primary_error">Please enter phone number</li></ul>');
       primaryFlag="Y";
     }else{
       $("#primaryPhone_error").html('');
       $("#primaryPhone_error").attr("data-error", "");
       primaryFlag="N";
     }
     if(secondaryPhoneCountryCode !='' && secondaryPhoneNumber ==""){
       $("#secondaryPhone_error").html('');
       $("#secondaryPhone_error").append('<ul class="list-unstyled text-danger"><li id="secondary_error">Please enter phone number</li></ul>');
       secondaryFlag="Y";
     }else{
       $("#secondaryPhone_error").html('');
       $("#primaryPhone_error").attr("data-error", "");
       secondaryFlag="N";
     }
     if( primaryFlag=="Y" || secondaryFlag=="Y"){ 
       return false;
     }else{
       $("#primaryPhone_error").html('');
       $("#secondaryPhone_error").html('');
       //return true;
     }
   }
}
jQuery(document).ready(function() {	

resetCommonEvents();
$('#auditModalDetailView').on('shown.bs.modal', function (e) {
  	findValidationAuditLogs(pkCombinedValueText, validationAuditType);	
});

loadIndustryList();
$("#industryCat").change(function() {
	loadIndustryList();
});

loadTeleCallingSubStatusList();
$("#teleCallingStatus").change(function() {
	loadTeleCallingSubStatusList();
});

loadRmCallingStatusList();
$("#liabOrAsset").change(function() {
	loadRmCallingStatusList();
});

loadRmCallingSubStatusList();
$("#rmCallingStatus").change(function() {
	loadRmCallingSubStatusList();
});

$("#jobFamily").change(function() {
	loadResponsibleForPartyList();
});
$("#city").change(function() {
	loadResponsibleForPartyList();
});
$("#leadScore").change(function() {
	loadResponsibleForPartyList();
});
$("#virtualTeamId").change(function() {
	loadResponsibleForPartyList();
});

loadCityList();
$("#stateProvinceGeoId").change(function() {
	loadCityList();
});

$("#city").change(function() {
	checkPostalCodeValidaty();
});

$('#postalCode').bind( "blur keyup", function( event ) {
	
	if ($('#postalCode').val().length == 0) {
		$("#postalCode_error").html('');
		loadResponsibleForPartyList();
	}
	
	if ($('#postalCode').val().length == 6) {
		$.ajax({
			      
			type: "POST",
	     	url: "getPostalCodeDetail",
	        data:  {"postalCode": $("#postalCode").val()},
	        async: false,
	        success: function (data) {   
	            if (data.code == 200) {
	            	if (data.postalCodeDetail.postalCode) {
	            		$("#postalCode_error").html('');
	            		selectedCity = data.postalCodeDetail.city;
	            		$("#stateProvinceGeoId").val(data.postalCodeDetail.stateProvinceGeoId).change();
	            	} else {
	            		//showAlert ("error", "Invalid PIN Code");
	            		$("#postalCode_error").html('<ul class="list-unstyled"><li>Invalid PIN Code</li></ul>'); 
	            		$("#postalCode_error").closest('.form-group').addClass("has-error has-danger");
							            		
						$('#stateProvinceGeoId').dropdown("clear");	 
						$("#city").html( "" );    
						$('#city').dropdown('clear');  
						selectedCity = null;	
											   
	            	}
	            }
	        }
	        
		});    
	}

});

loadResponsibleForPartyList();

});

function findValidationAuditLogs(pkCombinedValueText, validationAuditType) {
	
   	var url = "searchValidationAuditLogs?pkCombinedValueText="+pkCombinedValueText+"&validationAuditType="+validationAuditType;
   
	$('#auditModalDetailView .error-logs').DataTable( {
	    "processing": true,
	    "serverSide": true,
	    "destroy": true,
	    "searching": false,
	    "ajax": {
            "url": url,
            "type": "POST",
            "async": true
        },
        "pageLength": 15,
        "stateSave": false,
        "order": [[ 4, "desc" ]],
        /*
        "columnDefs": [ 
        	{
				"targets": 14,
				"orderable": false,
				"className": "longtext"
			} 
		],
		*/	      
        "columns": [
			{ "data": "oldValueText" },
			{ "data": "newValueText" },
			{ "data": "changedFieldName" },
			{ "data": "changedByInfo" },
            { "data": "createdStamp" },
            { "data": "comments",
	          "render": function(data, type, row, meta){
	          	data = "";
	            if(type === 'display'){
	            	var comments = row.comments; 
	            	if (comments && comments.length > 300) {
	            		comments = comments.substring(0, 300)+'...';
	            	}
	                data = '<div class="ml-1">'+comments+'</div>';
	            }
	            return data;
	         }
	      	}
            
        ],
        "fnDrawCallback": function(settings, json) {
		    resetDefaultEvents();
		}
	});
	
}

function resetCommonEvents() {
	$('.view-audit-message').unbind( "click" );
	$('.view-audit-message').bind( "click", function( event ) {
	
		event.preventDefault(); 
		$('#auditModalDetailView').modal("show");
		
		pkCombinedValueText = $(this).attr("data-leadId");
		validationAuditType = $(this).attr("data-auditType");
		
		$('#auditModalDetailView .modal-title').html( 'Import ${uiLabelMap.auditMessage} for [ ${leadId!} ]' );
																										
	});
	
	$('.view-dedup-message').unbind( "click" );
	$('.view-dedup-message').bind( "click", function( event ) {
	
		event.preventDefault(); 
		$('#auditModalDetailView').modal("show");
		
		pkCombinedValueText = $(this).attr("data-leadId");
		validationAuditType = $(this).attr("data-auditType");
		
		$('#auditModalDetailView .modal-title').html( '${uiLabelMap.dedupMessage} for [ ${leadId!} ]' );
																										
	});
}

function loadCityList() {
	
	var groupNameOptions = '';
	
	if ( $("#stateProvinceGeoId").val() ) {
		$('#city').dropdown('clear');
		$.ajax({
			      
			type: "POST",
	     	url: "getGeoAssocList",
	        data:  {"geoId": $("#stateProvinceGeoId").val(), "geoAssocTypeId": "COUNTY_CITY"},
	        async: false,
	        success: function (data) {   
	            
	            if (data.code == 200) {
	            
	            	for (var i = 0; i < data.results.length; i++) {
	            		var result = data.results[i];
	            		groupNameOptions += '<option value="'+result.geoIdTo+'">'+result.geoName+'</option>';
	            	}
	            	
	            }
				    	
	        }
	        
		});    
		
		$("#city").html( groupNameOptions );
		
		if (selectedCity) {
			$("#city").val( selectedCity ).change();
		} else if ("${dataImportLead?if_exists.stateProvinceGeoId!}" == $("#stateProvinceGeoId").val()) {
			$("#city").val( "${dataImportLead?if_exists.city!}" ).change();
		} else {
			$('#city').dropdown('clear');
		}
		
		//$('#city').dropdown('refresh');
		selectedCity = null;	
		
	}
		
}

function checkPostalCodeValidaty() {
	if ($('#postalCode').val() && $('#city').val()) {
		$.ajax({
			      
			type: "POST",
	     	url: "getPostalCodeDetail",
	        data:  {"postalCode": $("#postalCode").val(), "geoId": $("#city").val()},
	        async: false,
	        success: function (data) {   
	            if (data.code == 200) {
	            	if (data.postalCodeDetail.postalCode) {
	            		
	            	} else {
						$('#postalCode').val("");
						$('#postalCode_error').html("");
	            	}
	            }
	        }
	        
		});    
	}
}

function loadIndustryList() {
	var groupNameOptions = '';		
		
	if ( $("#industryCat").val() ) {
		
		$.ajax({
			      
			type: "POST",
	     	url: "getIndustryList",
	        data:  {"industryCatId": $("#industryCat").val()},
	        async: false,
	        success: function (data) {   
	            
	            if (data.code == 200) {
	            
	            	for (var i = 0; i < data.industrys.length; i++) {
	            		var industry = data.industrys[i];
	            		groupNameOptions += '<option value="'+industry.enumId+'">('+industry.enumCode+') '+industry.description+'</option>';
	            	}
	            	
	            }
				    	
	        }
	        
		});    
		
		$("#industry").html( groupNameOptions );
		
		if ("${dataImportLead?if_exists.industryCat!}" == $("#industryCat").val()) {
			$("#industry").val( "${dataImportLead?if_exists.industry!}" );
		} else {
			$('#industry').dropdown('clear');
		}
		
		$('#industry').dropdown('refresh');
	}
		
}

function loadTeleCallingSubStatusList() {
	var groupNameOptions = '';		
		
	if ( $("#teleCallingStatus").val() ) {
		
		$.ajax({
			      
			type: "POST",
	     	url: "getEnumList",
	        data:  {"parentEnumId": $("#teleCallingStatus").val(), "enumTypeId": "DBS_TELE_SUB_STATUS"},
	        async: false,
	        success: function (data) {   
	            
	            if (data.code == 200) {
	            
	            	for (var i = 0; i < data.enums.length; i++) {
	            		var enumObj = data.enums[i];
	            		groupNameOptions += '<option value="'+enumObj.enumId+'">'+enumObj.description+'</option>';
	            	}
	            	
	            }
				    	
	        }
	        
		});    
		
		$("#teleCallingSubStatus").html( groupNameOptions );
		
		if ("${dataImportLead?if_exists.teleCallingStatus!}" == $("#teleCallingStatus").val()) {
			$("#teleCallingSubStatus").val( "${dataImportLead?if_exists.teleCallingSubStatus!}" );
		} else {
			$('#teleCallingSubStatus').dropdown('clear');
		}
		
		$('#teleCallingSubStatus').dropdown('refresh');
	}
		
}

function loadRmCallingStatusList() {
	var groupNameOptions = '';		
		
	if ( $("#liabOrAsset").val() ) {
		
		$.ajax({
			      
			type: "POST",
	     	url: "getEnumList",
	        data:  {"parentEnumId": $("#liabOrAsset").val(), "enumTypeId": "DBS_RM_CALL_STATUS"},
	        async: false,
	        success: function (data) {   
	            
	            if (data.code == 200) {
	            
	            	for (var i = 0; i < data.enums.length; i++) {
	            		var enumObj = data.enums[i];
	            		groupNameOptions += '<option value="'+enumObj.enumId+'">'+enumObj.description+'</option>';
	            	}
	            	
	            }
				    	
	        }
	        
		});    
		
		$("#rmCallingStatus").html( groupNameOptions );
		
		if ("${dataImportLead?if_exists.liabOrAsset!}" == $("#liabOrAsset").val()) {
			$("#rmCallingStatus").val( "${dataImportLead?if_exists.rmCallingStatus!}" );
		} else {
			$('#rmCallingStatus').dropdown('clear');
		}
		
		$('#rmCallingStatus').dropdown('refresh');
	}
		
}

function loadRmCallingSubStatusList() {
	var groupNameOptions = '';		
		
	if ( $("#rmCallingStatus").val() ) {
		
		$.ajax({
			      
			type: "POST",
	     	url: "getEnumList",
	        data:  {"parentEnumId": $("#rmCallingStatus").val(), "enumTypeId": "DBS_RM_SUB_STATUS"},
	        async: false,
	        success: function (data) {   
	            
	            if (data.code == 200) {
	            
	            	for (var i = 0; i < data.enums.length; i++) {
	            		var enumObj = data.enums[i];
	            		groupNameOptions += '<option value="'+enumObj.enumId+'">'+enumObj.description+'</option>';
	            	}
	            	
	            }
				    	
	        }
	        
		});    
		
		$("#rmCallingSubStatus").html( groupNameOptions );
		
		if ("${dataImportLead?if_exists.rmCallingStatus!}" == $("#rmCallingStatus").val()) {
			$("#rmCallingSubStatus").val( "${dataImportLead?if_exists.rmCallingSubStatus!}" );
		} else {
			$('#rmCallingSubStatus').dropdown('clear');
		}
		
		$('#rmCallingSubStatus').dropdown('refresh');
	}
		
}

function loadResponsibleForPartyList() {
	var groupNameOptions = '';		
	//$('#leadAssignTo').dropdown('clear');	
	//if ( ($("#jobFamily").val() || $("#leadScore").val() || $("#virtualTeamId").val()) && $("#city").val() ) {
	//if ( $("#city").val() ) {
		/*
		$.ajax({
			      
			type: "POST",
	     	url: "getResponsibleForPartyList",
	        data:  {"jobFamily": $("#jobFamily").val(), "countryGeoId": "${userLogin.countryGeoId!}", "city": $("#city").val(), "postalCode": $("#postalCode").val(), "leadScore": $("#leadScore").val(), "virtualTeamId": $("#virtualTeamId").val()},
	        async: false,
	        success: function (data) {   
	            
	            if (data.code == 200) {
	            
	            	for (var i = 0; i < data.results.length; i++) {
	            		var result = data.results[i];
	            		groupNameOptions += '<option value="'+result.partyId+'">('+result.partyId+') '+result.partyName+'</option>';
	            	}
	            	
	            }
				    	
	        }
	        
		});    
		
		$("#leadAssignTo").html( groupNameOptions );
		
		if ("${dataImportLead?if_exists.leadScore!}" == $("#leadScore").val()) {
			$("#leadAssignTo").val( "${dataImportLead?if_exists.leadAssignTo!}" );
		} else if ("${dataImportLead?if_exists.jobFamily!}" == $("#jobFamily").val()) {
			$("#leadAssignTo").val( "${dataImportLead?if_exists.leadAssignTo!}" );
		} else {
			$('#leadAssignTo').dropdown('clear');
		}
		
		$('#leadAssignTo').dropdown('refresh');*/
	//}
		
}
	
</script>

<@auditLogModal id="auditModalDetailView" />
