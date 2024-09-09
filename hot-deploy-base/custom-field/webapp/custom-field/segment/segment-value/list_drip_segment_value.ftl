<#include "component://homeapps/webapp/homeapps/lib/ofbizFormMacros.ftl"/>

<#assign salesTriggerType=delegator.findByAnd( "MarketingCampaignContactList", { "marketingCampaignId": "${marketingCampaignId?if_exists}", "contactListId": "RMSales_Trigger_Monday_EveryMonth"},[], false)?if_exists />

<input type="hidden" name="salesTriggerTypeNew" value="${salesTriggerTypeNew?if_exists}" id="salesTriggerTypeNew" />
<input type="hidden" name="responseCountNew" value="${responseCountNew?if_exists}" id="responseCountNew" />
<input type="hidden" name="marketingCampaignIdVal" id="marketingCampaignIdVal" value="${marketingCampaignId?if_exists}" />
<input type="hidden" name="groupId" value="${groupId?if_exists}" id="groupId" />
<input type="hidden" name="userLoginId" value="${userLoginId?if_exists}" id="userLoginId" /> 

<div class="">
	<div id="myDripGrid" style="width: 100%;" class="ag-theme-balham"></div>
	<script type="text/javascript" src="/campaign-resource/js/ag-grid/campaign/list_drip_segment.js"></script>
</div>

<div id="modalMemberView" class="modal fade">
	<div class="modal-dialog modal-lg">
		<!-- Modal content-->
		<div class="modal-content">
			<div class="modal-header">
				<h2 class="modal-title">${uiLabelMap.List} ${uiLabelMap.Member} for [ <span id="segment-value-title"></span> ]</h2>
				<button type="reset" class="close" data-dismiss="modal">&times;</button>
			</div>
			<div class="modal-body">
				<div class="clearfix"></div>
				<div class="table-responsive">
					<div id="modalGrid" style="width: 100%;" class="ag-theme-balham"></div>
					<script type="text/javascript" src="/cf-resource/js/ag-grid/segment-value/drip-campaign-Accnt.js"></script>
				</div>
			</div>
			<div class="modal-footer">
				<button type="sbmit" class="btn btn-sm btn-primary navbar-dark" data-dismiss="modal">Close</button>
			</div>
		</div>
	</div>
</div>

<#include "component://campaign/webapp/campaign/campaigns/memberContactModel.ftl" />

<script type="text/javascript">

var customFieldId, groupId;
jQuery(document).ready(function() {
	loadAgGridDrip();
});

</script>