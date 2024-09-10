<#include "component://homeapps/webapp/homeapps/lib/ofbizFormMacros.ftl"/>

<div class="page-header border-b">
	<h1>${uiLabelMap.Create} ${uiLabelMap.ValueConfig}</h1>
</div>


<div class="row padding-r">
	<div class="col-md-6 col-sm-6">
		
		<div class="portlet-body form">
			<form role="form" class="form-horizontal" action="<@ofbizUrl>createValueConfig</@ofbizUrl>" encType="multipart/form-data" method="post" data-toggle="validator">
				
			<div class="form-body">
			
			<@dropdownInput 
				id="groupId"
				label=uiLabelMap.segmentCode
				options=groupList
				required=true
				value=valueConfig.groupId
				allowEmpty=true
				dataLiveSearch=true
				/>
				
			<@dropdownInput 
				id="customFieldId"
				label=uiLabelMap.segmentValue
				options=fieldList
				required=true
				value=valueConfig.customFieldId
				allowEmpty=true
				dataLiveSearch=true
				/>
				
			<@dropdownInput 
				id="valueCapture"
				label=uiLabelMap.valueCapture
				options=valueCaptureList
				required=true
				value=valueConfig.valueCapture
				allowEmpty=true
				/>			
			
			<@generalInput 
				id="valueSeqNum"
				label=uiLabelMap.sequence
				placeholder=uiLabelMap.sequence
				value=valueConfig.valueSeqNum
				inputType="number"
				required=false
				min=1
				/>				
				
			<@generalInput 
				id="valueMin"
				label=uiLabelMap.valueMin
				placeholder=uiLabelMap.valueMin
				value=valueConfig.valueMin
				required=false
				maxlength=20
				/>	
				
			<@generalInput 
				id="valueMax"
				label=uiLabelMap.valueMax
				placeholder=uiLabelMap.valueMax
				value=valueConfig.valueMax
				required=false
				maxlength=20
				/>
				
			<@generalInput 
				id="valueData"
				label=uiLabelMap.valueData
				placeholder=uiLabelMap.valueData
				value=valueConfig.valueData
				required=false
				/>			
															
			</div>
			
			<@fromCommonAction showCancelBtn=false showClearBtn=true/>
			
		</form>			
							
		</div>
			
	</div>
	
</div>
