<#include "component://homeapps/webapp/homeapps/lib/ofbizFormMacros.ftl"/>

<div class="page-head">
	<div class="page-title">
		<h1>${uiLabelMap.Create} <small>${uiLabelMap.ApplicationRegistry}</small></h1>
	</div>
</div>

<div class="row">
	<div class="col-md-12">
		
		<div class="portlet light">
		<div class="portlet-title">
			<div class="caption font-green-haze">
				<i class="fa fa-building font-green-haze"></i>
				<span class="caption-subject bold uppercase"> ${uiLabelMap.AppRegistry}</span>
			</div>
			<div class="tools">
				
				<#-- 
				<#if !lockboxBatch.importStatusId?exists || lockboxBatch.importStatusId != "LBIMP_IMPORTED">
				<button class="btn red-pink" data-toggle="confirmation" id="btn_discard_batch" data-popout="true" data-original-title="" title="Are you sure to Discard Batch ?">
				<i class="fa fa-times"></i>&nbsp;
				Discard Batch
				</button>
				</#if>
				 -->
				 
				<a href="javascript:;" class="collapse">
				</a>
				<a href="javascript:;" class="fullscreen">
				</a>
			</div>
		</div>
		
		<div class="portlet-body form">
			<form role="form" class="form-horizontal" action="<@ofbizUrl>createAppRegistry</@ofbizUrl>" encType="multipart/form-data" method="post" data-toggle="validator">
				
			<input type="hidden" name="sourceInvoked" value="PORTAL" />					
																
			<div class="form-body">
			
			<@generalInput 
				id="clientName"
				label=uiLabelMap.clientName
				placeholder=uiLabelMap.clientName
				value=applicationRegistry.clientName
				tooltip = uiLabelMap.clientName
				required=true
				/>
			
			<@generalInput 
				id="clientDomain"
				label=uiLabelMap.clientDomain
				placeholder=uiLabelMap.clientDomain
				value=applicationRegistry.clientDomain
				tooltip = uiLabelMap.clientDomain
				required=true
				/>
				
			<@textareaInput 
				id="comments"
				label=uiLabelMap.comments
				placeholder=uiLabelMap.comments
				rows="3"
				value=applicationRegistry.comments
				/>	
			
			<@dropdownInput 
				id="appStatusId"
				label=uiLabelMap.appStatusId
				options=appStatusList
				required=true
				value=applicationRegistry.appStatusId
				allowEmpty=true
				tooltip = uiLabelMap.appStatusId
				/>
						
			<@dateInput 
				id="fromDate"
				label=uiLabelMap.CommonFromDate
				value=applicationRegistry.fromDate
				disablePastDate="Y"
				/>
				
			<@dateInput 
				id="thruDate"
				label=uiLabelMap.CommonThruDate
				value=applicationRegistry.thruDate
				disablePastDate="Y"
				/>			
							
			<@generalInput 
				id="externalReferenceId"
				label=uiLabelMap.externalReferenceId
				placeholder=uiLabelMap.externalReferenceId
				value=applicationRegistry.externalReferenceId
				tooltip = uiLabelMap.externalReferenceId
				/>
																																																																																				
			</div>
			
			<@fromCommonAction iconClass="fa fa-check" showCancelBtn=false/>
			
		</form>			
							
		</div>
		</div>
			
	</div>
	
</div>
