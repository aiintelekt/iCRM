<#include "component://homeapps/webapp/homeapps/lib/ofbizFormMacros.ftl"/>

<div class="page-header border-b">
	<h1 class="float-left">${uiLabelMap.Create} ${uiLabelMap.CustomField} <#if customFieldGroup.groupName?has_content>for [ ${customFieldGroup.groupName} ]</#if> </h1>
	<div class="float-right">
		<#if customFieldGroup.groupId?has_content>
		<a href="findCustomField" class="btn btn-xs btn-primary m5 tooltips" title="Cancel" >Cancel</a>
		<a href="editCustomFieldGroup?groupId=${customFieldGroup.groupId}" class="btn btn-xs btn-primary m5 tooltips" title="Back to ${customFieldGroup.groupName!}" >Back</a>
		</#if>
	</div>
</div>

<#if customFieldGroup.groupId?has_content>
	<#assign actionUrl = "createCustomField"/>
<#else>
	<#assign actionUrl = "createCustomFieldIndividual"/>
</#if>

<div class="row padding-r">
	<div class="col-md-6 col-sm-6" style="padding:0px">
		
		<div class="portlet-body form">
			<form role="form" class="form-horizontal" action="<@ofbizUrl>${actionUrl}</@ofbizUrl>" encType="multipart/form-data" method="post" data-toggle="validator">
				
			<div class="form-body">
			
			<#if groupId?has_content>
			<input type="hidden" id="groupId" name="groupId" value="${customField.groupId!}" />						
			<#-- <@readonlyInput 
				id="groupId"
				label=uiLabelMap.customGroup
				value=customField.groupId
				isHiddenInput=true
				/> -->
			<#else>	
			<@dropdownInput 
				id="groupId"
				label=uiLabelMap.customGroup
				options=groupList
				required=false
				value=customField.groupId
				allowEmpty=true
				tooltip = uiLabelMap.customGroup
				dataLiveSearch=true
				/>
			</#if>
			<#-- 
			<@dropdownInput 
				id="roleTypeId"
				label=uiLabelMap.roleTypeId
				options=roleTypeList
				required=true
				value=customField.roleTypeId
				allowEmpty=true
				tooltip = uiLabelMap.roleTypeId
				dataLiveSearch=true
				/>
			 -->
			<@generalInput 
				id="customFieldName"
				label=uiLabelMap.customFieldName
				placeholder=uiLabelMap.customFieldName
				value=customField.customFieldName
				tooltip = uiLabelMap.customFieldName
				required=true
				maxlength=255
				/>
							
			<@dropdownInput 
				id="customFieldType"
				label=uiLabelMap.customFieldType
				options=fieldTypeList
				required=true
				value=customField.customFieldType
				allowEmpty=true
				tooltip = uiLabelMap.customFieldType
				/>	
				
			<@dropdownInput 
				id="customFieldFormat"
				label=uiLabelMap.customFieldFormat
				options=fieldFormatList
				required=true
				value=customField.customFieldFormat
				allowEmpty=true
				tooltip = uiLabelMap.customFieldFormat
				/>	
				
			<@dropdownInput 
				id="customFieldLength"
				label=uiLabelMap.fieldLength
				options=fieldLengthList
				required=false
				value=customField.customFieldLength
				allowEmpty=true
				tooltip = uiLabelMap.fieldLength
				/>
			<@dropdownInput 
				id="roleTypeId"
				label=uiLabelMap.roleTypeId
				options=roleTypeList
				required=true
				value=roleConfig?if_exists
				allowEmpty=true
				dataLiveSearch=true
				/>			
				
			<@generalInput 
				id="sequenceNumber"
				label=uiLabelMap.sequence
				placeholder=uiLabelMap.sequence
				value=customField.sequenceNumber
				tooltip = uiLabelMap.sequence
				inputType="number"
				required=false
				min=1
				/>	
				
			<@dropdownInput 
				id="hide"
				label=uiLabelMap.hide
				options=yesNoOptions
				required=false
				value=customField.hide
				allowEmpty=true
				tooltip = uiLabelMap.hide
				/>											
														
			</div>
			
			<@fromCommonAction showCancelBtn=false/>
			
		</form>			
							
		</div>
			
	</div>
	
</div>
