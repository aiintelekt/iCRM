<#include "component://homeapps/webapp/homeapps/lib/ofbizFormMacros.ftl"/>

<div class="page-header border-b">
	<h1>${uiLabelMap.Update} ${uiLabelMap.ValueConfig}</h1>
</div>

<div class="row padding-r">
	<div class="col-md-6 col-sm-6">
		
		<div class="portlet-body form">
			<form role="form" class="form-horizontal" action="<@ofbizUrl>updateValueConfig</@ofbizUrl>" encType="multipart/form-data" method="post" data-toggle="validator">
			
			<div class="form-body">
			
			<@readonlyInput 
				id="groupId"
				label=uiLabelMap.segmentCode
				value=valueConfig.groupId
				isHiddenInput=true
				/>
				
			<@readonlyInput 
				id="customFieldId"
				label=uiLabelMap.segmentValue
				value=valueConfig.customFieldId
				isHiddenInput=true
				/>
				
			<@readonlyInput 
				id="valueCapture"
				label=uiLabelMap.valueCapture
				value=valueConfig.valueCapture
				isHiddenInput=true
				/>
			
			<@readonlyInput 
				id="valueSeqNum"
				label=uiLabelMap.sequence
				value=valueConfig.valueSeqNum
				isHiddenInput=true
				/>		
				
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
			
			<@fromCommonAction showCancelBtn=true cancelUrl="valueConfig"/>
			
		</form>			
							
		</div>
			
	</div>
	
</div>
