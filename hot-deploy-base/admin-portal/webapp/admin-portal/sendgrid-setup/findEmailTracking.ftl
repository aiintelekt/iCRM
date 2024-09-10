<#include "component://bootstrap/lib/ofbizFormMacros.ftl"/>
<#include "component://common-portal/webapp/common-portal/lib/picker_macro.ftl"/>
<div class="row">
	<div id="main" role="main" class="pd-btm-title-bar">
		<div class="col-lg-12 col-md-12 col-sm-12 ">
			<div class="row" style="width:100%">
				<div class="col-lg-12 col-md-12 col-sm-12 dash-panel" >
					<div><@sectionFrameHeader title="${uiLabelMap.findEmailTracking}"/></div>
					<div class="panel-group" id="accordionMenu" role="tablist" aria-multiselectable="true">
						<div class="panel panel-default">
							<div class="panel-heading" role="tab" id="headingTwo">
								<h4 class="panel-title">
									<a role="button" data-toggle="collapse" data-parent="#accordionMenu"
										href="#accordionDynaBase" aria-expanded="false"
										aria-controls="collapseOne">${uiLabelMap.MainFilter}</a>
								</h4>
							</div>
							<div id="accordionDynaBase" class="panel-collapse collapse" role="tabpanel" aria-labelledby="headingOne">
								<form method="post" id="emailTracking" name="emailTrackingForm" class="form-horizontal" novalidate="novalidate" data-toggle="validator">
									<div class="panel-body">
										<@dynaScreen 
											instanceId="FIND_EMAIL_TRACKING"
											modeOfAction="CREATE"
											/>
										<div class="row find-srbottom">
											<div class="col-lg-12 col-md-12 col-sm-12">
												<div class="text-right pd-cbx-lbls" style="padding-top: 0px;">
													<@button
														id="email-track-main-search-btn"
														label="${uiLabelMap.Find}"
														/>
													<@reset
														id="email-track-reset-btn"
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
<@campaignPicker
	instanceId="campaignPicker"
	/>
</div>
<script>
	$(document).ready(function() {
	$("#email-track-reset-btn").click(function() {
			$('#campaignId_val').val("");
		});
	}); 
</script>