<#include "component://homeapps/webapp/homeapps/lib/ofbizFormMacros.ftl"/>

<div class="page-header border-b">
	<h1>${uiLabelMap.Update} ${uiLabelMap.MicroServiceConfig}</h1>
</div>

<div class="row padding-r">
	<div class="col-md-6 col-sm-6">
		
		<div class="portlet-body form">
			<form role="form" class="form-horizontal" action="<@ofbizUrl>updateMicroServiceConfig</@ofbizUrl>" encType="multipart/form-data" method="post" data-toggle="validator">
				
			<input type="hidden" name="configId" value="${configId!}" />																			
																																																							
			<div class="form-body">
			
			<@generalInput 
				id="microUrl"
				label=uiLabelMap.url
				placeholder=uiLabelMap.url
				value=microService.microUrl
				required=true
				/>	
				
			<@generalInput 
				id="authKey"
				label=uiLabelMap.authKey
				placeholder=uiLabelMap.authKey
				value=microService.authKey
				required=true
				/>	
				
			<@generalInput 
				id="serviceName"
				label=uiLabelMap.serviceName
				placeholder=uiLabelMap.serviceName
				value=microService.serviceName
				required=false
				/>										
			
			<@dropdownInput 
				id="isEnabled"
				label=uiLabelMap.isEnabled
				options=yesNoOptions
				required=true
				value=microService.isEnabled
				allowEmpty=true
				/>	
						
			<@generalInput 
				id="microSeqNum"
				label=uiLabelMap.sequence
				placeholder=uiLabelMap.sequence
				value=microService.microSeqNum
				inputType="number"
				required=false
				min=1
				/>
																																																																																																																																																																																																																																																	
			</div>
			
			<@fromCommonAction showCancelBtn=true cancelUrl="microServiceConfig"/>
			
		</form>			
							
		</div>
			
	</div>
	
</div>
