<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<@pageSectionHeader title="Campaign Details" />
<#assign requestURII = request.getRequestURI()/>
<#assign requestt = requestURII+"?" />
<@inputHidden id="requestt" value="${requestt!}" />
<div class="clearfix"> </div> 	
<div class="table-responsive">
	<div id="campaignGrid" style="width: 100%;" class="ag-theme-balham"></div>                        	    					
</div>
<script type="text/javascript" src="/crm-resource/js/ag-grid/campaign/campaign.js"></script>

<script type="text/javascript">
function viewNote(campaignNoteId){
	$('#campaignNoteId').val( campaignNoteId );
}
	
jQuery(document).ready(function() {
	$("a[href='#campaignDetails']").on('shown.bs.tab', function(e) {
		 loadCampaignAgGrid();
	});	 
});
</script>

		
