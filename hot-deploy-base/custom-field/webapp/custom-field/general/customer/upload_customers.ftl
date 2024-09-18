<#include "component://homeapps/webapp/homeapps/lib/ofbizFormMacros.ftl"/>

<div class="page-header border-b">
	<h1>${uiLabelMap.Upload} ${uiLabelMap.SegmentValue}</h1>
</div>

<div class="row padding-r">
	<div class="col-md-6 col-sm-6">
		
		<div class="portlet-body form">
			<form role="form" class="form-horizontal" action="<@ofbizUrl>createSegmentValue</@ofbizUrl>" encType="multipart/form-data" method="post" data-toggle="validator">
				
			<div class="form-body">
			
			<#-- <@dropdownInput 
				id="roleTypeId"
				label=uiLabelMap.roleTypeId
				options=roleTypeList
				required=true
				value=customField.roleTypeId
				allowEmpty=false
				tooltip = uiLabelMap.roleTypeId
				/> -->
			
			<#if groupId?has_content>						
			<@readonlyInput 
				id="groupId"
				label=uiLabelMap.segmentCode
				value=customField.groupId
				isHiddenInput=true
				/>
			<#else>	
			<@dropdownInput 
				id="groupId"
				label=uiLabelMap.segmentCode
				options=groupList
				required=true
				value=customField.groupId
				allowEmpty=false
				/>
			</#if>
					
			<@generalInput 
				id="customFieldName"
				label=uiLabelMap.segmentValueName
				placeholder=uiLabelMap.segmentValueName
				value=customField.customFieldName
				required=true
				/>	
				
			<#-- <@dropdownInput 
				id="customFieldFormat"
				label=uiLabelMap.customFieldFormat
				options=fieldFormatList
				required=true
				value=customField.customFieldFormat
				allowEmpty=true
				/> -->	
				
			<#-- <@dropdownInput 
				id="customFieldLength"
				label=uiLabelMap.fieldLength
				options=fieldLengthList
				required=true
				value=customField.customFieldLength
				allowEmpty=true
				/>		 -->
				
			<@generalInput 
				id="sequenceNumber"
				label=uiLabelMap.sequence
				placeholder=uiLabelMap.sequence
				value=customField.sequenceNumber
				inputType="number"
				required=true
				min=1
				/>						
			
			<@dropdownInput 
				id="isEnabled"
				label=uiLabelMap.isEnabled
				options=yesNoOptions
				required=fales
				value=customField.isEnabled
				allowEmpty=true
				/>		
			
			<div class="page-header">
				<h2>${uiLabelMap.Configuration} ${uiLabelMap.SegmentValue}</h2>
			</div>
			
			<input type="hidden" name="valueCapture" value="${customFieldGroup.valueCapture!}" />
			<input type="hidden" name="valueSeqNum" value="1" />		
			<#-- 						
			<@dropdownInput 
				id="valueCapture"
				label=uiLabelMap.valueCapture
				options=valueCaptureList
				required=true
				value=valueConfig.valueCapture
				allowEmpty=false
				/>			
			 -->
			<@generalInput 
				id="valueMin"
				label=uiLabelMap.valueMin
				placeholder=uiLabelMap.valueMin
				value=valueConfig.valueMin
				required=false
				/>	
				
			<@generalInput 
				id="valueMax"
				label=uiLabelMap.valueMax
				placeholder=uiLabelMap.valueMax
				value=valueConfig.valueMax
				required=false
				/>
				
			<@generalInput 
				id="valueData"
				label=uiLabelMap.valueData
				placeholder=uiLabelMap.valueData
				value=valueConfig.valueData
				required=false
				/>																					
																																				
			</div>
			
			<@fromCommonAction showCancelBtn=false/>
			
		</form>			
							
		</div>
			
	</div>
	
</div>
