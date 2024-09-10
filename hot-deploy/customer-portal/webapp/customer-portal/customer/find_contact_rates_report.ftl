<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<div class="row">
	<div id="main" role="main" class="pd-btm-title-bar">
		<div class="col-lg-12 col-md-12 col-sm-12 dash-panel">
			<div><@sectionFrameHeader title="${uiLabelMap.findContactRatesReport!}" extra=helpUrl! /></div>
			<div class="panel-group" id="accordionMenu" role="tablist" aria-multiselectable="true">
				<div class="panel panel-default">
					<div class="panel-heading" role="tab" id="headingTwo">
						<h4 class="panel-title">
							<a role="button" data-toggle="collapse" data-parent="#accordionMenu"
								href="#accordionDynaBase" aria-expanded="true"
								aria-controls="collapseOne"> ${uiLabelMap.MainFilter!} </a>
						</h4>
					</div>
					<div id="accordionDynaBase" class="panel-collapse collapse show" role="tabpanel" aria-labelledby="headingOne">
						<form method="post" id="contactRatesReportForm" class="form-horizontal" novalidate="novalidate" data-toggle="validator">
							<div class="panel-body">
								<@dynaScreen 
									instanceId="CONTACT_RATES_REPORT"
									modeOfAction="CREATE"
									/>
								<div class="text-right pd-cbx-lbls pad-10" style="padding-top: 0px;">
									<@button
										id="find-contact-rates-btn"
										label="${uiLabelMap.Find}"
										/>
									<@reset
										id="contact-rates-reset"
										label="${uiLabelMap.Reset}"
										/>
								</div>
							</div>
						</form>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>
<script>
	$(document).ready(function() {
		initDateRange("fromDate_picker", "thruDate_picker", null, null);
	    $("#fromDate_picker").click(function() {
	            $("#fromDate_error").html('');
		});
	    $("#thruDate_picker").click(function() {
	            $("#thruDate_error").html('');
		});
		var campaignList = "<option value=''></option>";
		var csrList = "<option value=''></option>";
		<#if marketingCampaignList?has_content>
			<#list marketingCampaignList as marketingCampaign>
				campaignList+='<option value="${marketingCampaign.campaignName?if_exists}">${marketingCampaign.campaignName?if_exists}</option>';
			</#list>
		</#if>
		<#if rmMap?exists && rmMap?has_content>
			<#list rmMap.keySet() as csrListMap>
				csrList+='<option value="${rmMap[csrListMap]!}">${rmMap[csrListMap]!}</option>';
			</#list>
		</#if>
		$("#csrPartyId").html(csrList);
		$("#csrPartyId").dropdown('refresh');
		$("#campaignId").html(campaignList);
		$("#campaignId").dropdown('refresh');
	});
</script>