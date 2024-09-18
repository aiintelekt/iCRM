<#include "component://homeapps/webapp/homeapps/lib/ofbizFormMacros.ftl"/>
<#assign requestURI = ""/>
<#assign requestURI = request.getParameter("marketingCampaignId")! />
<script type="text/javascript" language="javascript" src="/bootstrap/js/jquery.dataTables.min.js"></script>

<div class="row">
<div id="main" role="main" class="pd-btm-title-bar">
	<div class="col-lg-12 col-md-12 col-sm-12 dash-panel">
		<div class="">
			<div class="col-lg-12 col-md-12 col-sm-12">
				<div class="row">
					<#if requestURI?has_content>
					<div class="col-lg-8 col-md-12 col-sm-12">
						<h1 class="float-left mr-2 mb-0 header-title">${uiLabelMap.Update} ${uiLabelMap.SegmentValue} <#if customFieldGroup.groupName?has_content>for [ ${customFieldGroup.groupName} <i class="fa fa-arrow-right" aria-hidden="true"></i> ${customField.customFieldName} ]</#if> </h1>
					</div>
					<div class="col-lg-4 col-md-6 col-sm-12">
						<div class="text-right" id="seg-back">
							<a href="/campaign/control/viewMarketingCampaign?marketingCampaignId=${marketingCampaignId!}&activeTab=drip" class="btn btn-xs btn-primary m5"><i class="fa fa-chevron-circle-left"></i> Back </a>
						</div>
					</div>
					<#else>
					<div class="col-lg-6 col-md-12 col-sm-12">
						<h1 class="float-left mr-2 mb-0 header-title">${uiLabelMap.Update} ${uiLabelMap.SegmentValue} <#if customFieldGroup.groupName?has_content>for [ ${customFieldGroup.groupName} <i class="fa fa-arrow-right" aria-hidden="true"></i> ${customField.customFieldName} ]</#if> </h1>
					</div>
					<div class="col-lg-6 col-md-6 col-sm-12">
						<div class="text-right" id="extra-header-right-container">
							<a href="/campaign/control/viewMarketingCampaign?marketingCampaignId=${marketingCampaignId!}&activeTab=drip" class="btn btn-xs btn-primary m5"><i class="fa fa-chevron-circle-left"></i> Back </a>
						</div>
					</div>
					</#if>
				</div>
			</div>
		</div>
		<#-- 
		<div class="page-header border-b">
			<h1 class="float-left">${uiLabelMap.Update} ${uiLabelMap.SegmentValue} <#if customFieldGroup.groupName?has_content>for [ ${customFieldGroup.groupName} <i class="fa fa-arrow-right" aria-hidden="true"></i> ${customField.customFieldName} ]</#if> </h1>
			<div class="float-right">
				<a href="/campaign/control/viewMarketingCampaign?marketingCampaignId=${marketingCampaignId!}&activeTab=drip" class="btn btn-xs btn-primary m5"> Back</a>
			</div>
		</div>
		-->
		<div class="row padding-r">
			<div class="col-md-6 col-sm-6">
				<div class="portlet-body form">
					</form>			
				</div>
			</div>
		</div>
	</div>
</div>
<script>
jQuery(document).ready(function(){

});
</script>
