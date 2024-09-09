<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>

<#macro custGroupSummaryModal instanceId fromAction="">
<div id="${instanceId!}" class="modal fade" >
    <div class="modal-dialog modal-sm" style="min-width: 400px;">
        <!-- Modal content-->
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title">Batch Summary: <span id='batch-id'></span> &nbsp;&nbsp;&nbsp;</h4>
                <button type="button" class="close" data-dismiss="modal">&times;</button>
            </div>
            <div class="modal-body" id="batch-summary">
                
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-sm btn-primary navbar-dark" data-dismiss="modal">Close</button>
            </div>
        </div>
    </div>
</div>

<script>
$(document).ready(function() {
    
});
    
function showCustGroupSummary(seqId, batchId) {
	$('#${instanceId!}').modal('show');
	$('#batch-id').html(batchId);
	let summaryHtml = '<table class="table table-hover"><tbody>';
	$.ajax({
		type: "POST",
     	url: "/admin-portal/control/getCustGrpBatchSummary",
        data: {"seqId": seqId, "batchId": batchId, "externalLoginKey": "${requestAttributes.externalLoginKey!}"},
        async: false,
        success: function (result) {
            if (result.code == 200) {
            	let summary = result.summary;
            	summaryHtml += '<tr><th scope="row">Success Count</th><td>'+summary.successCount+'</td></tr>';
            	summaryHtml += '<tr><th scope="row">Failed Count</th><td>'+summary.failedCount+'</td></tr>';
            	summaryHtml += '<tr><th scope="row">Repost Count</th><td>'+summary.repostCount+'</td></tr>';
            	summaryHtml += '<tr><th scope="row">Curr Customer Group(s)</th><td style="word-wrap: break-word;min-width: 160px;max-width: 160px;">'+summary.groupId+'</td></tr>';
            	summaryHtml += '<tr><th scope="row">Old Customer Group(s)</th><td style="word-wrap: break-word;min-width: 160px;max-width: 160px;">'+summary.prvGroupId+'</td></tr>';
            }
        }
	}); 
	summaryHtml += '</tbody></table>';
	$('#batch-summary').html(summaryHtml);
}
</script> 
</#macro>