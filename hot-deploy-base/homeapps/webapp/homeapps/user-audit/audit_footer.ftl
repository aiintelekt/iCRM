<#-- isPerformUserAudit: ${isPerformUserAudit!}, isValidAction: ${isValidAction!} -->
<script>
<#if isPerformUserAudit?has_content && isPerformUserAudit == "Y" && isValidAction?has_content && isValidAction == "N">

$("#mainFrom").submit(false);
$("#mainFrom :input").attr("disabled", true);
$('.ui.dropdown').addClass("disabled");

</#if>
</script>

<#if userAuditRequestId?has_content && isValidAction?has_content && isValidAction == "Y">

<#-- <a href="viewServiceRequestType?custRequestTypeId=${custRequestTypeId!}" class="btn btn-xs btn-primary"><i class="fa fa-chevron-circle-left" aria-hidden="true"></i> Back</a> -->

<script>

jQuery(document).ready(function() {

$('#modalAuditValueChangedView').on('shown.bs.modal', function (e) {
  	
  	$.ajax({
			      
		type: "POST",
     	url: "prepareAuditValueCompare",
        data:  {"userAuditRequestId": '${userAuditRequestId!}'},
        async: false,
        success: function (data) {   
            
            if (data.code == 200) {
				
				var compareHtml = "";                        
            	for (var i = 0; i < data.compareList.length; i++) {
            		var compare = data.compareList[i];
            		
            		var changedClass = "";
            		if (compare.isChanged) {
            			changedClass = "bg-light";
            		}
            		
            		compareHtml += '<tr class="'+changedClass+'"><td>'+compare.propLabel+'</td><td>'+compare.oldValue+'</td><td>'+compare.newValue+'</td></tr>';
            		
            	}
            	
            	$("#valueCompareList").html(compareHtml);
            }
			    	
        }
        
	});    
  	
});

});

var userAuditRequestId = '${userAuditRequestId!}';
var totalChanged = '${totalChanged!}';
var auditServiceTypeDesc = '${auditServiceTypeDesc!}';

var auditApproveBtn = '<span class="btn btn-xs btn-success audit-approve-btn"><i class="fa fa-check" aria-hidden="true"></i> Approve</span>';
var auditRejectBtn = '<span class="btn btn-xs btn-danger audit-reject-btn"><i class="fa fa-times" aria-hidden="true"></i> Reject</span>';
var auditCountBtn = '<span class="btn btn-xs btn-primary tooltips audit-count-btn" title="View Value Compare"><strong> '+totalChanged+'</strong></span>';

$("#extra-header-right-container").append(auditApproveBtn);
$("#extra-header-right-container").append(auditRejectBtn);
$("#extra-header-right-container").append(auditCountBtn);

$('.audit-approve-btn').bind( "click", function( event ) {
	
	event.preventDefault(); 
	
	var message = "Are you sure?";
	bootbox.confirm(message, function(result) {
	   if (result) {
		makerCheckerAction("APPROVED", userAuditRequestId, '');
	   }
    });
																									
});

$('.audit-reject-btn').bind( "click", function( event ) {
	
	event.preventDefault(); 
	
	var message = "Are you sure?";
	bootbox.confirm(message, function(result) {
	   if (result) {
		makerCheckerAction("REJECTED", userAuditRequestId, '');
	   }
    });
																									
});

$('.audit-count-btn').bind( "click", function( event ) {
	
	event.preventDefault(); 
	
	$('#modalAuditValueChangedView').modal("show");
		
	$('#modalAuditValueChangedView .modal-title').html( '${uiLabelMap.valueChanged} for [ '+auditServiceTypeDesc+ ' ]' );
	$('#modalAuditValueChangedView .total-changed').html( totalChanged );
																								
});

function makerCheckerAction(approveType, userAuditRequestId, remarks) { 
    var url = "processUserAuditRequest?approveType="+approveType+"&userAuditRequestId="+userAuditRequestId+"&remarks="+remarks;
    
    $.ajax({
      	url: url,
      	type: 'POST',
      	success : function(data) {
        	
        	if (data.code == 200) {
        		window.location = '<@ofbizUrl>viewPendingRequests</@ofbizUrl>';
        	}
        	
      	}
    });
	
 }

</script>

</#if>

<div id="modalAuditValueChangedView" class="modal fade" >
  <div class="modal-dialog modal-lg">
    <!-- Modal content-->
    <div class="modal-content">
      <div class="modal-header">
        <h2 class="modal-title"></h2>
        <button type="reset" class="close" data-dismiss="modal">&times;</button>
      </div>
      <div class="modal-body">
        
        <div class="table-responsive">
        
        	<div class="float-right" id="exportLead">
				<div class="row">
					
					<div class="float-right pr-3">
						Total Changed: <strong class="total-changed"></strong>
					</div>
				</div>
			</div>
			<div class="clearfix"></div>
        	
			<table class="table">
			<thead>
			<tr>
				<th>${uiLabelMap.propertyName!}</th>
				<th>${uiLabelMap.oldValue!}</th>
				<th>${uiLabelMap.newValue!}</th>
			</tr>
			</thead>
			<tbody id="valueCompareList">
				
			</tbody>
			</table>
		</div>
		                
      </div>
      <div class="modal-footer">
        <button type="sbmit" class="btn btn-sm btn-primary navbar-dark" data-dismiss="modal">Close</button>
      </div>
    </div>
  </div>
</div>
</div>