<#macro dynaScreen instanceId modeOfAction userId="" layoutType="" securityGroupId="" defaultMessage="" isConfigScreen="">
	   	
   	<#local screenRenderDetail = dispatcher.runSync("dynaScreen.getDynaScreenRenderDetail", {
                "dynaConfigId" : instanceId,
                "requestContext" : Static["org.ofbiz.base.util.UtilMisc"].toMap("layoutType", layoutType, "securityGroupId", securityGroupId, "modeOfAction", modeOfAction, "isCheckSecurityGroup", "Y", "isConfigScreen", isConfigScreen, "inputContext", inputContext!),
                "userLogin" : userLogin 
            })>
            
    <#if screenRenderDetail.screenConfig?has_content>
    <#local isDisplayDyanName = Static["org.fio.homeapps.util.DataUtil"].getGlobalValue(delegator, "DISPLAY_DYNA_NAME", "Y") />
	<#if "Y" == isDisplayDyanName>
	<div class="row">
		<div class="col-sm-8">
			<h4 class="float-left sub-txt">(Dyna Name : ${screenRenderDetail.screenConfig.screenDisplayName!})</h4>
		</div>
		
		<div class="col-sm-4 text-right">
			<#if security.hasPermission("DYNA_CONFIG", userLogin)>
			<a class="settings" title="Dyna configuration" target="_blank" href="/dyna-screen/control/updateDynaScreen?dynaConfigId=${instanceId!}&externalLoginKey=${requestAttributes.externalLoginKey!}"><i class="fa fa-cog" aria-hidden="true"></i></a>
			</#if>
		</div>	
	</div>
	</#if>
    </#if>        
            
  	<div class="row padding-r">          
   	
   	<#if screenRenderDetail.screenConfig?has_content>
		   	
   		<#local screenConfig = screenRenderDetail.screenConfig>
   		<#local screenComputation = screenConfig.screenComputation>
   		
   		<#if screenRenderDetail.screenConfigFieldList?has_content>
   			
   			<#local inputColSize = 6>
   			<#local layoutColumn = screenComputation.layoutColumn>
   			<#local isFullscreen = screenConfig.isFullscreen!>
   			
   			<#if layoutColumn==1 && isFullscreen?has_content && isFullscreen=='Y'>
   				<#local inputColSize = 12>
   			<#else>
   				<#local isFullscreen = 'N'>		
   			</#if>
   			
   			<#local labelColSize = 'col-sm-4'/>
			<#local defaultInputColumnSize = 'col-sm-8'/>
			<#if isFullscreen=='Y'>
				<#local labelColSize='col-sm-2'>
				<#local defaultInputColumnSize='col-sm-9'>
			</#if>
			<#if screenConfig.labelColSize?has_content><#local labelColSize = '${screenConfig.labelColSize}'/></#if>
			<#if screenConfig.inputColSize?has_content><#local defaultInputColumnSize = '${screenConfig.inputColSize}'/></#if>
   			
   			<#if (layoutColumn >= 3) >
   				<#local inputColSize = ((6/layoutColumn)+2)>
   			</#if>
   			
   			<#local fieldCount = screenComputation.fieldCount>
   			<#local colFieldRemainCount = screenComputation.colFieldRemainCount >
   			<#local colFieldCount = screenComputation.colFieldCount >
   			<#local tmpColFieldCount = screenComputation.colFieldCount + colFieldRemainCount >
   			<#local colCount = 0 >
   			
   			<#-- inputColSize: ${inputColSize}, layoutColumn: ${layoutColumn}, fieldCount: ${fieldCount}, colFieldCount: ${colFieldCount}, colFieldRemainCount: ${colFieldRemainCount} -->
			   			
   			<#list screenRenderDetail.screenConfigFieldList as field>
   				
   				<#if (colCount == tmpColFieldCount)>
   					<#local colCount = 0 >
   					<#local tmpColFieldCount = colFieldCount >
   				</#if>
   				
   				<#local inputId = field.dynaFieldId>
				<#local fieldName = field.fieldName!>
				<#local fieldAttrList = field.fieldAttrList/>
				
				<#local value = "">
				<#if inputContext?exists && inputContext.get(inputId)?has_content>
					<#local value = inputContext.get(inputId)>
				<#elseif modeOfAction=="CREATE">
					<#local value = field.defaultValue!>	
				</#if>
				   				
   				<#if field.fieldType?has_content && field.fieldType=="HIDDEN">
   					<@inputHidden id="${inputId!}" value="${value!}" />
   				<#else>	
   				
   				<#if colCount == 0>
   					<div class="col-md-${inputColSize} col-sm-${inputColSize}">
   					<#-- tmpColFieldCount: ${tmpColFieldCount!}, fieldCount: ${fieldCount!}, colFieldCount: ${colFieldCount!} -->
   				</#if>
								
				<#if !fieldName?has_content>
					<#local inputColumnSize = 'col-sm-11'/>
				<#else>
					<#local inputColumnSize = defaultInputColumnSize/>	
				</#if>
				
				<#if field.fieldType?has_content && field.fieldType=="DATE">
					<#local value=Static["org.groupfio.common.portal.util.DataHelper"].formatDate(delegator, value, field.fieldDataPattern!)>
				</#if>
								
				<#if modeOfAction == "CREATE" || modeOfAction == "UPDATE">
										
					<#if (modeOfAction == "CREATE" && fieldAttrList.isCreateDisplay?has_content && fieldAttrList.isCreateDisplay=="Y")
						|| (modeOfAction == "UPDATE" && fieldAttrList.isEditDisplay?has_content && fieldAttrList.isEditDisplay=="Y")
						>
						<@inputHidden id="${inputId!}" name="${inputId!}" value="${value!}" /> 
						<#local displayValue = value!>      
						<#if field.fieldType?has_content && (field.lookupTypeId?has_content && field.lookupTypeId != "CUSTOM") >
							<#local fieldDataList = field.fieldDataList>
							<#local displayValue = fieldDataList.get(value)!>
						</#if>
						
						<#local desValue = "">
						<#if inputContext?exists && inputContext.get(inputId+'_desc')?has_content>
							<#local desValue = inputContext.get(inputId+'_desc') />
						</#if>
						<#local linkValue = "">
						<#if inputContext?exists && inputContext.get(inputId+'_link')?has_content>
							<#local linkValue = inputContext.get(inputId+'_link') />
						</#if>
						<#local linkData = "" />
						<#if inputContext?exists && inputContext.get(inputId+'_link_data')?has_content>
							<#local linkData = inputContext.get(inputId+'_link_data') />
						</#if>
												
						<@displayCell 
						id="${inputId}"
					    label="${uiLabelMap.get(field.fieldName!)}"
					    value="${displayValue!}"
					    isLink="${fieldAttrList.isLinkDisplay!}"
					    isPhoneNumber="${fieldAttrList.isPhoneNumber!}"
	                    desValue="${desValue!}"
	                    linkValue="${linkValue!}"
	                    linkMap="${linkData!}"
	                    labelColSize="${labelColSize!}" 
					    inputColSize="${inputColumnSize!}"
					    />	
					<#else>
						<@dynaScreenInput 
						field=field
						inputId=inputId
						modeOfAction=modeOfAction
						value=value
						labelColSize="${labelColSize!}" 
					    inputColSize="${inputColumnSize!}"
						/>		
					</#if>
				<#elseif modeOfAction == "VIEW">
					
					<#if field.fieldType?has_content && (field.fieldType=="DROPDOWN" || field.fieldType=="RADIO") && value?has_content 
						&& (field.lookupTypeId?has_content && field.lookupTypeId != "CUSTOM") >
						<#local fieldDataList = field.fieldDataList>
						<#local value = fieldDataList.get(value)!>
					</#if>
					<#-- Added to fix the multi select dropdown field -->
					<#if field.fieldType?has_content && field.fieldType=="DROPDOWN"&& fieldAttrList?has_content && (fieldAttrList.isMultiple?has_content && fieldAttrList.isMultiple == "Y") && !value?has_content && (field.lookupTypeId?has_content && field.lookupTypeId != "CUSTOM")>
						<#local value = inputContext.get(inputId)!>
					</#if>
					
					<#local desValue = "">
					<#if inputContext?exists && inputContext.get(inputId+'_desc')?has_content>
						<#local desValue = inputContext.get(inputId+'_desc') />
					</#if>
					<#local linkValue = "">
					<#if inputContext?exists && inputContext.get(inputId+'_link')?has_content>
						<#local linkValue = inputContext.get(inputId+'_link') />
					</#if>
					<#local linkData = "" />
					<#if inputContext?exists && inputContext.get(inputId+'_link_data')?has_content>
						<#local linkData = inputContext.get(inputId+'_link_data') />
					</#if>
					
					<#if field.fieldType?has_content && field.fieldType=="CURRENCY">
						<@displayCurrencyRow 
						  id="${inputId}"		
	                      label="${uiLabelMap.get(field.fieldName!)}"
	                      value="${value!}"
	                      labelColSize="${labelColSize!}" 
						  inputColSize="${inputColumnSize!}"
	                  	/>	
					<#else>
						<@displayCell 
						  id="${inputId}"	
	                      label="${uiLabelMap.get(field.fieldName!)}"
	                      value="${value!}"
	                      isLink="${fieldAttrList.isLinkDisplay!}"
	                      isPhoneNumber="${fieldAttrList.isPhoneNumber!}"
	                      desValue="${desValue!}"
	                      linkValue="${linkValue!}"
	                      linkMap="${linkData!}"
	                      labelColSize="${labelColSize!}" 
						  inputColSize="${inputColumnSize!}"
	                  	/>	
					</#if>
					
				</#if>		
						
				<#local colCount = colCount + 1 >
				<#if (colCount == tmpColFieldCount)>
   					</div>
   				</#if>
				   									
   				</#if>
				
			</#list>
   		
   		<#else>
   			<div class="col-md-6 col-sm-6">
   			
   				<div class="alert alert-warning">
 				<strong>Warning!</strong>
	   			<#if defaultMessage?has_content>
	   				${uiLabelMap.get(defaultMessage)!}
	   			<#elseif screenConfig.defaultMessage?has_content>
	   				${uiLabelMap.get(screenConfig.defaultMessage)!}	
	   			<#else>
	   				No fields been configured, Please check with administrator
	   			</#if>
	   			</div>
	   		</div>			
   		</#if>
   	
   	<#elseif screenRenderDetail.responseCode?has_content && screenRenderDetail.responseCode == "E3010" >	
   		<div class="col-md-6 col-sm-6">
   			<div class="alert alert-warning">
 				<strong>Warning!</strong> Access Denied!
 			</div>	
   		</div>		
   	<#else>
   		<div class="col-md-6 col-sm-6">
   			<div class="alert alert-warning">
 				<strong>Warning!</strong> Configuration not found, Please check with administrator
 			</div>	
   		</div>		
   	</#if>   
   	
   	</div>		
  
</#macro>

<#macro dynaScreenInput field inputId modeOfAction value labelColSize inputColSize>
	
	<#local ddOptions = field.fieldDataList/>
	<#local fieldAttrList = field.fieldAttrList/>
	
	<#if field.isRequired?has_content && field.isRequired=="Y">
		<#local isRequired = true>
	<#else>
		<#local isRequired = false>
	</#if>
	
	<#if field.isDisabled?has_content && field.isDisabled=="Y">
		<#local isDisabled = true>
	<#else>
		<#local isDisabled = false>
	</#if>
	
	<#local isMakerChange = false>
	<#if auditTrackList?exists && auditTrackList.get(inputId)?has_content>
		<#local isMakerChange = true>
	</#if>
		
	<#if field.fieldType?has_content && (field.fieldType=="TEXT" || field.fieldType=="PASSWORD")>
		<#local type="text" />
		<#if field.fieldType=="PASSWORD">
			<#local type="password" />
		</#if>
		<@inputRow 
		id="${inputId}"
		type=type!'text'
		label="${uiLabelMap.get(field.fieldName!)}"
		placeholder=fieldAttrList.placeholder!"${uiLabelMap.get(field.fieldName!)}"
		value="${value!}"
		maxlength=field.maxLength!
		pattern="${field.fieldDataPattern!}"
		labelColSize="${labelColSize!}" 
		inputColSize="${inputColSize!}"
		isMakerChange=isMakerChange
		required=isRequired
		disabled=isDisabled
		/>
	<#elseif field.fieldType?has_content && field.fieldType=="DATE">
		<@inputDate 
		id="${inputId}"
		value="${value!}"
		label="${uiLabelMap.get(field.fieldName!)}"
		disablePastDate="${fieldAttrList.disablePastDate!'N'}"
		disableFutureDate="${fieldAttrList.disableFutureDate!'N'}"
		dateFormat="${StringUtil.wrapString(field.fieldDataPattern?if_exists)}"
		labelColSize="${labelColSize!}" 
		inputColSize="${inputColSize!}"
		isMakerChange=isMakerChange
		required=isRequired
		disabled=isDisabled
		/>
	<#elseif field.fieldType?has_content && field.fieldType=="CUSTOM_DATE">
		<@customDatePicker 
		id="${inputId}"
		value="${value!}"
		label="${uiLabelMap.get(field.fieldName!)}"
		labelColSize="${labelColSize!}" 
		inputColSize="${inputColSize!}"
		isMakerChange=isMakerChange
		yearAdd="${fieldAttrList.yearAdd!}"
		required=isRequired
		disabled=isDisabled
		/>
	<#elseif field.fieldType?has_content && field.fieldType=="TIME">
		<@inputTime 
		id="${inputId}"
		value=value!
		label="${uiLabelMap.get(field.fieldName!)}"
		isMeridianTimeMode="${fieldAttrList.isMeridianTimeMode!'N'}"
		isCurrentDefaultTime="${fieldAttrList.isCurrentDefaultTime!'Y'}"
		isShowSeconds="${fieldAttrList.isShowSeconds!'N'}"
		minuteStep="${fieldAttrList.minuteStep!'15'}"
		secondStep="${fieldAttrList.secondStep!'15'}"
		labelColSize="${labelColSize!}" 
		inputColSize="${inputColSize!}"
		isMakerChange=isMakerChange
		required=isRequired
		disabled=isDisabled
		/>
	<#elseif field.fieldType?has_content && field.fieldType=="DATE_TIME">
		<@inputDateTime 
		id="${inputId}"
		value=value!
		label="${uiLabelMap.get(field.fieldName!)}"
		disablePastDate="${fieldAttrList.disablePastDate!'N'}"
		disableFutureDate="${fieldAttrList.disableFutureDate!'N'}"
		dateFormat="${StringUtil.wrapString(field.fieldDataPattern?if_exists)}"
		isMeridianTimeMode="${fieldAttrList.isMeridianTimeMode!'N'}"
		isCurrentDefaultTime="${fieldAttrList.isCurrentDefaultTime!'N'}"
		isShowSeconds="${fieldAttrList.isShowSeconds!'N'}"
		minuteStep="${fieldAttrList.minuteStep!'15'}"
		secondStep="${fieldAttrList.secondStep!'15'}"
		labelColSize="${labelColSize!}" 
		inputColSize="${inputColSize!}"
		isMakerChange=isMakerChange
		required=isRequired
		disabled=isDisabled
		/>		
	<#elseif field.fieldType?has_content && field.fieldType=="DATE_RANGE">
		<#local valueFrom = inputContext.get(inputId+'_from')! >
		<#local valueTo = inputContext.get(inputId+'_to')! >
		
		<@inputDateRange 
		id="${inputId}"
		idFrom="${inputId}_from"
		valueFrom="${valueFrom!}"
		idTo="${inputId}_to"
		valueTo="${valueTo!}"
		label="${uiLabelMap.get(field.fieldName!)}"
		dateFormat="${StringUtil.wrapString(field.fieldDataPattern?if_exists)}"
		disablePastDate="${fieldAttrList.disablePastDate!'N'}"
		disableFutureDate="${fieldAttrList.disableFutureDate!'N'}"
		labelColSize="${labelColSize!}" 
		inputColSize="${inputColSize!}"
		isMakerChange=isMakerChange
		required=isRequired
		disabled=isDisabled
        />  
	<#elseif field.fieldType?has_content && field.fieldType=="DROPDOWN">
	
		<#local defValue = field.defaultValue!>
		<#-- <#if defValue?has_content && (field.lookupTypeId?has_content && field.lookupTypeId == "CUSTOM") > -->
		<#if defValue?has_content>
			<input type="hidden" id="${inputId}_defValue" value="${defValue!}"/> 
		</#if>
		
		<@dropdownCell 
		id="${inputId}"
		label="${uiLabelMap.get(field.fieldName!)}"
		placeholder=fieldAttrList.placeholder!"${uiLabelMap.get(field.fieldName!)}"
		options=ddOptions
		allowEmpty=true
		value="${value!}"
		isMultiple="${fieldAttrList.isMultiple!}"
		labelColSize="${labelColSize!}" 
		inputColSize="${inputColSize!}"
		isMakerChange=isMakerChange
		required=isRequired
		disabled=isDisabled
		/>	
	<#elseif field.fieldType?has_content && field.fieldType=="PICKER">
		<#local desValue = "">
		<#if inputContext?exists && inputContext.get(inputId+'_desc')?has_content>
			<#local desValue = inputContext.get(inputId+'_desc') />
		</#if>
		<@inputRowPicker 
        inputColSize="col-sm-7"
        glyphiconClass= "fa fa-id-card"
        pickerWindow="${field.pickerWindowId!}"
        label="${uiLabelMap.get(field.fieldName!)}"
        value="${value!}"
        desValue="${desValue!}"
        isMakerChange=isMakerChange
        required=isRequired
        id="${inputId}"  
        name="${inputId}" 
        placeholder="${uiLabelMap.get(field.fieldName!)}"
        labelColSize="${labelColSize!}" 
		inputColSize="${inputColSize!}"
		
		isAutoCompleteEnable="${fieldAttrList.isAutoCompleteEnable!'N'}"
		isTriggerChangeEvent="${fieldAttrList.isTriggerChangeEvent!'Y'}"
		autoCompleteMinLength="${fieldAttrList.autoCompleteMinLength!'3'}"
		autoCompleteUrl="${StringUtil.wrapString(fieldAttrList.autoCompleteUrl?if_exists)}"
		autoCompleteLabelFieldId="${fieldAttrList.autoCompleteLabelFieldId!}"
		autoCompleteValFieldId="${fieldAttrList.autoCompleteValFieldId!}"
		autoCompleteFormId="${fieldAttrList.autoCompleteFormId!}"
        />
	<#elseif field.fieldType?has_content && field.fieldType=="NUMBER">
		<@inputRow 
		id="${inputId}"
		label="${uiLabelMap.get(field.fieldName!)}"
		placeholder=fieldAttrList.placeholder!"${uiLabelMap.get(field.fieldName!)}"
		type="number"
		value="${value!}"
		maxlength=field.maxLength!
		pattern="${field.fieldDataPattern!}"
		min="${fieldAttrList.minValue!}"
		max="${fieldAttrList.maxValue!}"
		labelColSize="${labelColSize!}" 
		inputColSize="${inputColSize!}"
		isMakerChange=isMakerChange
		required=isRequired
		disabled=isDisabled
		/>
	<#elseif field.fieldType?has_content && field.fieldType=="RADIO">
		<@radioInputCell
        id="${inputId}"
        name="${inputId}"
        label="${uiLabelMap.get(field.fieldName!)}"
        options=ddOptions
        value="${value!}"
        labelColSize="${labelColSize!}" 
		inputColSize="${inputColSize!}"
        isMakerChange=isMakerChange
		required=isRequired
		disabled=isDisabled
        />	
    <#elseif field.fieldType?has_content && field.fieldType=="TEXT_AREA">
		<@inputArea
        id="${inputId}"
        inputColSize="col-sm-7"
        label="${uiLabelMap.get(field.fieldName!)}"
        labelColSize="col-sm-4"
        rows="3"
        placeholder=fieldAttrList.placeholder!"${uiLabelMap.get(field.fieldName!)}"
        value="${value!}"
        maxlength=field.maxLength!
        pattern="${field.fieldDataPattern!}"
        labelColSize="${labelColSize!}" 
		inputColSize="${inputColSize!}"
        isMakerChange=isMakerChange
		required=isRequired
		disabled=isDisabled
        />  
   	<#elseif field.fieldType?has_content && (field.fieldType=="FULL_RICH_TEXT" || field.fieldType=="LITE_RICH_TEXT")>
		<@inputRichTextAreaRow
        id="${inputId}"
        inputColSize="col-sm-7"
        label="${uiLabelMap.get(field.fieldName!)}"
        labelColSize="col-sm-4"
        height="200"
        placeholder=fieldAttrList.placeholder!"${uiLabelMap.get(field.fieldName!)}"
        value="${value!}"
        editorType="${field.fieldType!}"
        pattern="${field.fieldDataPattern!}"
        labelColSize="${labelColSize!}" 
		inputColSize="${inputColSize!}"
        isMakerChange=isMakerChange
		required=isRequired
		disabled=isDisabled
        />       
    <#elseif field.fieldType?has_content && field.fieldType=="CHECK_BOX">
    	<@checkbox
        name="${inputId}"
        id="${inputId}"
        label="${uiLabelMap.get(field.fieldName!)}"
        checked=false
        value="${value!}"
        labelColSize="${labelColSize!}" 
		inputColSize="${inputColSize!}"
        isMakerChange=isMakerChange
		required=isRequired
		disabled=isDisabled
        />   
  	<#elseif field.fieldType?has_content && field.fieldType=="HIDDEN">
		<@inputHidden id="${inputId!}" value="${value!}" />       
	<#elseif field.fieldType?has_content && field.fieldType=="DISPLAY">
		<@inputHidden name="${inputId!}" value="${value!}" /> 
		<#local displayValue = value!>      
		<#if field.fieldType?has_content && (field.lookupTypeId?has_content && field.lookupTypeId != "CUSTOM") >
			<#local fieldDataList = field.fieldDataList>
			<#local displayValue = fieldDataList.get(value)!>
		</#if>
		<@displayCell 
		id="${inputId}"
	    label="${uiLabelMap.get(field.fieldName!)}"
	    value="${displayValue!}"
	    labelColSize="${labelColSize!}" 
		inputColSize="${inputColSize!}"
	    />	
	<#else>
		<@inputRow 
		id="${inputId}"
		label="${uiLabelMap.get(field.fieldName!)}"
		placeholder=fieldAttrList.placeholder!"${uiLabelMap.get(field.fieldName!)}"
		value="${value!}"
		maxlength=field.maxLength!
		pattern="${field.fieldDataPattern!}"
		labelColSize="${labelColSize!}" 
		inputColSize="${inputColSize!}"
		isMakerChange=isMakerChange
		required=isRequired
		disabled=isDisabled
		/>
	</#if>
</#macro>

<#-- temporary added, will be remove after completely integrate fio AG grid -->
<#assign requestURI = request.getRequestURI()/>
<#if !requestURI?has_content || (requestURI?has_content && requestURI.contains("screenRender"))>
<#assign requestURI=request.getParameter("requestUri")!>
</#if>
<#if !context.isInitiateFioAgGrid?exists>
		<#if requestURI?has_content && requestURI.contains("addservicerequest") || requestURI.contains("updateServiceRequest") || requestURI.contains("findServiceRequests") || requestURI.contains("viewServiceRequest")|| requestURI.contains("createTaskActivity") || requestURI.contains("updateActivity") || requestURI.contains("viewActivity") 
		|| requestURI.contains("/sr-portal/control/main") || requestURI.contains("serviceRequestHome") || requestURI.contains("createSrOrderAssoc") || requestURI.contains("updateSrOrderAssoc") || requestURI.contains("findActivity") || requestURI.contains("findResAvail")
		|| requestURI.contains("/sr-portal/control/activityHome") 
		|| requestURI.contains("/client-portal/control/findServices")
		|| requestURI.contains("/client-portal/control/servicesHome")
		|| requestURI.contains("viewRebate")
		|| requestURI.contains("viewCustomer") || requestURI.contains("viewAccount") || requestURI.contains("viewOpportunity") || requestURI.contains("viewContact")
		|| requestURI.contains("findCustomer") || requestURI.contains("customerHome")
		|| requestURI.contains("viewTemplate")
		|| requestURI.contains("account-portal") || requestURI.contains("opportunity-portal") || requestURI.contains("findTemplates")
		|| requestURI.contains("rebate-portal") || requestURI.contains("service-portal") || requestURI.contains("ticket-portal") || requestURI.contains("ticketc-portal")|| requestURI.contains("viewLead") || requestURI.contains("contract-portal")
		|| requestURI.contains("customer-portal") || requestURI.contains("admin-portal")
		>
	<#else>
	<script src="/bootstrap/js/ag-grid-community.min.js"></script>
    <link rel="stylesheet" href="/bootstrap/css/ag-grid.css">
    <link rel="stylesheet" href="/bootstrap/css/ag-theme-balham.css">
	${setContextField("isInitiateFioAgGrid", "N")}
	</#if>
</#if>

