<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<#assign requestURI = ""/>
<#if request.getRequestURI().contains("main")>
<#assign helpUrl = Static["org.groupfio.common.portal.util.DataHelper"].getTabHelpUrl(delegator, request.getRequestURI(), "main") />
</#if>
<div class="row">
	<div id="main" role="main" class="pd-btm-title-bar">
		<div class="col-lg-12 col-md-12 col-sm-12 dash-panel">
			<@sectionFrameHeader title="Phone Call Report - Details By Day" extra=helpUrl! />
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
						<form method="post" id="searchDetailsByDayForm" class="form-horizontal" novalidate="novalidate" data-toggle="validator">
							<div class="panel-body">
								<@dynaScreen 
									instanceId="DETAILS_BY_DAY"
									modeOfAction="CREATE"
									/>
								<div class="row find-srbottom">
									<div class="col-lg-12 col-md-12 col-sm-12">
										<div class="text-right pd-cbx-lbls" style="padding-top: 0px;">
											<@button
												id="main-search-btn"
												label="${uiLabelMap.Find}"
												/>
											<@reset
												id="daily-by-day-reset"
												label="${uiLabelMap.Reset}"
												/>
										</div>
									</div>
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
	        $("#createdDate_picker").click(function() {
	             $("#createdDate_error").html('');
			});
			var callNumberList = "<option value=''>ALL</option>";
			var personList = "<option value=''>ALL</option>";
			var campaignList = "<option value=''>ALL</option>";
			<#if callNumberList?has_content>
				<#list callNumberList as callNumberGV>
					callNumberList+='<option value="${callNumberGV?if_exists}" >${callNumberGV?if_exists}</option>';
				</#list>
			</#if>
			<#if personList?has_content>
				<#list personList as personGV>
					personList+='<option value="${personGV.firstName?if_exists} ${personGV.lastName?if_exists}" >${personGV.firstName?if_exists} ${personGV.lastName?if_exists}</option>';
				</#list>
			</#if>
			<#if marketingCampaignList?has_content>
				<#list marketingCampaignList as marketingCampaign>
					campaignList+='<option value="${marketingCampaign.campaignName?if_exists}" >${marketingCampaign.campaignName?if_exists}</option>';
				</#list>
			</#if>
			$("#csrName").html(personList);
			$("#csrName").dropdown('refresh');
			$("#callNumber").html(callNumberList);
			$("#callNumber").dropdown('refresh');
			$("#campaignName").html(campaignList);
			$("#campaignName").dropdown('refresh');
		});
</script>