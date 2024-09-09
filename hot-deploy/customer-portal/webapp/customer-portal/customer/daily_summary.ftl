<#include "component://bootstrap/lib/ofbizFormMacros.ftl"/>
<div class="row">
	<div id="main" role="main" class="pd-btm-title-bar">
		<div class="col-lg-12 col-md-12 col-sm-12 ">
			<div class="row" style="width:100%">
				<div class="col-lg-12 col-md-12 col-sm-12 dash-panel" >
					<div><@sectionFrameHeader title="Phone Call Report - Daily Summary"/></div>
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
								<form method="post" id="dailySummaryReport" name="searchForm" class="form-horizontal" data-toggle="validator">
									<div class="panel-body">
										<@dynaScreen 
											instanceId="DAILY_SUMMARY_REPORT"
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
														id="daily-summary-reset"
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
	</div>
</div>
<div class="row" style="width:100%" >
	<div class="col-lg-12 col-md-12 col-sm-12 dash-panel">
		<#-- <@AgGrid
			gridheadertitle="Daily Summary Report List"
			gridheaderid="${instanceId!}-grid-action-container"
			savePrefBtn=false
			clearFilterBtn=false
			exportBtn=true
			insertBtn=false
			updateBtn=false
			removeBtn=false
			refreshPrefBtn=false
			savePrefBtn=false
			subFltrClearBtn = false
			userid="${userLogin.userLoginId}"
			exportBtnId="summary-export-btn"
			shownotifications="true"
			instanceid="DAILY_SUMMARY_REPORT"
			autosizeallcol="true"
			debug="false"
			statusBar=true
			/>
		<script type="text/javascript" src="/contact-portal-resource/js/ag-grid/daily_summary.js"></script>-->
	<input type="hidden" id="userId" value="${userLogin.userLoginId}"/>
  	<input type="hidden" id="externalLoginKey" value="${requestAttributes.externalLoginKey!}"/>

	 				<@fioGrid 
						id="dailySummaryGrid"
						instanceId="PERSON_RESPOSIBLE_FOR_REPORT"
						jsLoc="/contact-portal-resource/js/ag-grid/daily_summary.js"
						headerLabel="Daily Summary Report List"
						headerId="${instanceId!}-grid-action-container"
						savePrefBtn=false
						clearFilterBtn=false
						subFltrClearBtn=false
						exportBtn=true
						headerBarClass="grid-header-no-bar"
						clearFilterBtnId="dailySummary-clear-pref-btn"
						subFltrClearId="dailySummary-sub-filter-clear-btn"
						savePrefBtnId="dailySummary-save-filter-btn"
						exportBtnId="summary-export-btn"
						/>
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
					personList+='<option value="${personGV.partyId?if_exists}" >${personGV.firstName?if_exists} ${personGV.lastName?if_exists}</option>';
				</#list>
			</#if>
			<#if marketingCampaignList?has_content>
				<#list marketingCampaignList as marketingCampaign>
					campaignList+='<option value="${marketingCampaign.marketingCampaignId?if_exists}" >${marketingCampaign.campaignName?if_exists}</option>';
				</#list>
			</#if>
			$("#csrPartyId").html(personList);
			$("#csrPartyId").dropdown('refresh');
			$("#callNumber").html(callNumberList);
			$("#callNumber").dropdown('refresh');
			$("#marketingCampaignId").html(campaignList);
			$("#marketingCampaignId").dropdown('refresh');
		});
</script>