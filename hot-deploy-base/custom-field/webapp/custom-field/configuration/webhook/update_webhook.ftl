<#include "component://homeapps/webapp/homeapps/lib/ofbizFormMacros.ftl"/>

<div class="page-header border-b">
	<h1>${uiLabelMap.Update} ${uiLabelMap.WebhookConfig}</h1>
</div>

<div class="row padding-r">
	<div class="col-md-6 col-sm-6">
		
		<div class="portlet-body form">
			<form role="form" class="form-horizontal" action="<@ofbizUrl>updateWebhookConfig</@ofbizUrl>" encType="multipart/form-data" method="post" data-toggle="validator">
				
			<input type="hidden" name="configId" value="${configId!}" />																			
																																																							
			<div class="form-body">
			
			<@generalInput 
				id="webhookUrl"
				label=uiLabelMap.url
				placeholder=uiLabelMap.url
				value=webhook.webhookUrl
				required=true
				/>	
				
			<@generalInput 
				id="authKey"
				label=uiLabelMap.authKey
				placeholder=uiLabelMap.authKey
				value=webhook.authKey
				required=true
				/>	
				
			<@generalInput 
				id="serviceName"
				label=uiLabelMap.serviceName
				placeholder=uiLabelMap.serviceName
				value=webhook.serviceName
				required=false
				/>										
			
			<@dropdownInput 
				id="isEnabled"
				label=uiLabelMap.isEnabled
				options=yesNoOptions
				required=true
				value=webhook.isEnabled
				allowEmpty=true
				/>	
						
			<@generalInput 
				id="webhookSeqNum"
				label=uiLabelMap.sequence
				placeholder=uiLabelMap.sequence
				value=webhook.webhookSeqNum
				inputType="number"
				required=false
				min=1
				/>		
																																																																																																																																																																																																																																																	
			</div>
			
			<@fromCommonAction showCancelBtn=true cancelUrl="webhookConfig"/>
			
		</form>			
							
		</div>
			
	</div>
	
</div>
