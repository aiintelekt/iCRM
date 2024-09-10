<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<#include "component://common-portal/webapp/common-portal/lib/picker_macro.ftl"/>
<#assign requestURI = ""/>
<#if request.getRequestURI().contains("main")>
<#assign helpUrl = Static["org.groupfio.common.portal.util.DataHelper"].getTabHelpUrl(delegator, request.getRequestURI(),"main")/>
</#if>
<div class="row">
	<div id="main" role="main" class="pd-btm-title-bar">
		<div class="col-lg-12 col-md-12 col-sm-12 dash-panel">
			<@sectionFrameHeader title="Find Contacts" extra=helpUrl!/>
			<div class="panel-group" id="accordionMenu" role="tablist" aria-multiselectable="true">
				<div class="panel panel-default">
					<div class="panel-heading" role="tab" id="headingTwo">
						<h4 class="panel-title">
							<a role="button" data-toggle="collapse" data-parent="#accordionMenu"
								href="#accordionDynaBase" aria-expanded="true"
								aria-controls="collapseOne">${uiLabelMap.MainFilter}</a>
						</h4>
					</div>
					<div id="accordionDynaBase" class="panel-collapse collapse show" role="tabpanel" aria-labelledby="headingOne">
						<form method="post" id="searchForm" class="form-horizontal" novalidate="novalidate" data-toggle="validator">
							<div class="panel-body">
								<@inputHidden id="externalLoginKey" name="externalLoginKey" value="${requestAttributes.externalLoginKey!}"/>
								<@dynaScreen
									instanceId="FIND_CONTACT"
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
<@responsiblePickerAccount 
	instanceId="partyResponsible" isExecutePartyAssoc="N"
	/>
<script>
$(document).ready(function(){

/*
$("#find-contact").click(function(event){
	event.preventDefault(); 
	getContactRowData();
});
*/
});
</script>