<#include "component://homeapps/webapp/homeapps/lib/ofbizFormMacros_dbs.ftl"/>

<style>
table input[type="radio"]:checked:after {
	left: auto !important;
	top: -0.10px;
	width: 14px;
	height: 15px;
}
</style>
<div class="col-lg-12 col-md-12 col-sm-12 dash-panel">
<@sectionFrameHeader title="${uiLabelMap.ListOf} ${uiLabelMap.pendingRequests}"/>
<div class="table-responsive">
	<table class="table table-hover" id="list_pending_audit_requests">
	<thead>
	<tr>
		<th>${uiLabelMap.serviceType!}</th>
		<#-- <th>${uiLabelMap.detail!}</th> -->
		<th>${uiLabelMap.maker!}</th>
		<th>${uiLabelMap.cheker!}</th>
		<th>${uiLabelMap.modeOfAction!}</th>
		<th>${uiLabelMap.status!}</th>
		<th>${uiLabelMap.remarks!}</th>
		<th class="longtext-nowrap">${uiLabelMap.valueChanged!}</th>
		<#-- <th>${uiLabelMap.mandatoryFields!}</th> -->  
		<th>${uiLabelMap.createdDate!}</th>
		<th class="text-center">Action</th>
	</tr>
	
	</thead>
	<tbody>
	
	</tbody>
	</table>
</div>
</div>
</div>
</div>

<div id="pendingRequestFormModalView" class="modal fade" role="dialog" data-parentForm="" data-keyboard="false" data-backdrop="static">
	<div class="modal-dialog modal-lg">
		<!-- Modal content-->
		<div class="modal-content">
			<div class="modal-header">
				<h4 class="modal-title">Are you sure? </h4>
				<button type="button" class="close" data-dismiss="modal">&times;</button>
			</div>
			<div class="modal-body">
				<form id="pendingRequestForm" role="form" class="form-horizontal" method="post" data-toggle="validator">
				<input type="hidden" name="approveType" id="approveType"/>
				<input type="hidden" name="userAuditRequestId" id="userAuditRequestId"/>
				<div class="row padding-r">
					
					<div class="col-md-8 col-sm-8 form-horizontal">
						<@generalInput 
						id="remarks"
						label="Remarks"
						placeholder="Remarks"
						required=true
						/>
					</div>
					
				</div>
				<div class="clearfix"></div>
				<div class="modal-footer">
					<button type="button" class="btn btn-default" id="pendingRequestFormModal-cancel-btn">Cancel</button>
					<button type="submit" class="btn btn-primary" id="pendingRequestFormModal-apply-btn">OK</button>
				</div>
				</form>
			</div>
		</div>
	</div>
</div>

<div id="mandatoryFieldJsonData" class="modal fade" role="dialog" data-parentForm="" data-keyboard="false" data-backdrop="static">
	<div class="modal-dialog modal-xs">
		<!-- Modal content-->
		<div class="modal-content">
			<div class="modal-header">
				<h4 class="modal-title">Mandatory Field</h4>
				<button type="button" class="close" data-dismiss="modal">&times;</button>
			</div>
			<div class="modal-body">
				<form id="mandatoryFieldJsonData" role="form" class="form-horizontal" method="" data-toggle="validator">
				
				<div class="row padding-r">
					<div class="col-md-8 col-sm-8 form-horizontal">
						<@readonlyInput 
						id="mandatoryField"
						label="Value"
						placeholder="Mandatory Field Value"
						value=""
						required=false
						/>
					</div>
					
				</div>
				<div class="clearfix"></div>
				<div class="modal-footer">
					<button type="button" class="btn btn-default" data-dismiss="modal">Cancel</button>
					<button type="submit" class="btn btn-primary" id="mandatoryFieldJsonData-apply-btn">OK</button>
				</div>
				</form>
			</div>
		</div>
	</div>
</div>

<input type="hidden" id="userAuditRequestId" value="">

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

<script type="text/javascript">

function mandatoryField(mandatoryField){
	
    var mandatoryFieldVal = mandatoryField.value;
    if(mandatoryFieldVal != "" && mandatoryFieldVal != undefined) {
    	$('#mandatoryField').html(mandatoryFieldVal);
		$('#mandatoryFieldJsonData').modal("show");
	} else {
		showAlert("error", "Please select mandatory field");
	}
	
}

jQuery(document).ready(function() {

$('#modalAuditValueChangedView').on('shown.bs.modal', function (e) {
  	
  	$.ajax({
			      
		type: "POST",
     	url: "prepareAuditValueCompare",
        data:  {"userAuditRequestId": $("#userAuditRequestId").val()},
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



$('#find-pendingRequest-button').on('click', function(){

	findPendingRequests();

});

$('#pendingRequestFormModal-cancel-btn').on('click', function(e) {
	e.preventDefault();
	var userAuditRequestId = $('#userAuditRequestId').val();
	$('#list_pending_audit_requests input[name="'+userAuditRequestId+'"]').prop("checked", false);
	$('#pendingRequestFormModalView').modal("hide");
});
		
$('#mandatoryFieldJsonData-apply-btn').on('click', function(e) {
	e.preventDefault();
	$('#mandatoryFieldJsonData').modal("hide");
});
	
$('#maker-checker-audit').on('click', function(){
	findPendingRequests("maker_checker_audit");
});

$('#maker-inbox').on('click', function(){
	findPendingRequests("maker_inbox");
});
	
$('#pendingRequestForm').validator().on('submit', function (e) {
		
	if (e.isDefaultPrevented()) {
    	// handle the invalid form...
  	} else {
  		e.preventDefault();
  		makerCheckerAction($("#approveType").val(), $("#userAuditRequestId").val(), $("#remarks").val());
  	}	
});

findPendingRequests();

});	

function resetRequestEvents() {
	$('.view-exit-message').unbind( "click" );
	$('.view-exit-message').bind( "click", function( event ) {
	
		event.preventDefault(); 
		$('#modalAuditValueChangedView').modal("show");
		
		serviceType = $(this).attr("data-serviceType");
		totalChanged = $(this).attr("data-totalChanged");
		
		userAuditRequestId = $(this).attr("data-userAuditRequestId");
				
		$("#userAuditRequestId").val(userAuditRequestId);
		
		$('#modalAuditValueChangedView .modal-title').html( '${uiLabelMap.valueChanged} for [ '+serviceType+ ' ]' );
		$('#modalAuditValueChangedView .total-changed').html( totalChanged );
																										
	});
}

function findPendingRequests() {
	
	var statusId = $("#statusId").val();
	var modeOfAction = $("#modeOfAction").val();
	var makerPartyId = $("#makerPartyId").val();
	var chekerPartyId = $("#chekerPartyId").val();
	var fromDate = $("#fromDate").val();
	var thruDate = $("#thruDate").val();
	var requestType = $("#requestType").val();
	   	
   	var url = "searchPendingRequests";
   
	$('#list_pending_audit_requests').DataTable({
	
		    "processing": true,
		    "serverSide": true,
		    "searching": false,
		    "destroy": true,
		    "ajax": {
	            "url": url,
	            "type": "POST",
	            "data": {
                   "statusId": statusId,
                   "modeOfAction": modeOfAction,
                   "makerPartyId": makerPartyId,
                   "chekerPartyId": chekerPartyId,
                   "fromDate": fromDate,
                   "thruDate": thruDate,
                   "requestType": requestType,
                }
	        },
	        
	        "pageLength": 20,
	        "stateSave": false,
	        	      
	        "columns": [
					        	
	            { "data": "serviceRequestType",
	            	"orderable": false,
	              "render": function(data, type, row, meta) {
	                 return row.description;
	              }
	            },
				/*	            
	            { "data": "requestUri", 
	            	"orderable": false,
	               "render": function(data, type, row, meta) {
                        var requestUri = row.requestUri;
                        var modeOfAction = row.modeOfAction;
                        var approvePermission = row.approvePermission;
                        
                        data = '<a href="'+row.requestUri+'&userAuditRequestId='+row.userAuditRequestId+'">Review</a>';
                        
                        return data;
                    }
						            
	            },
	            */
	            { "data": "makerPartyId", 
	            	"orderable": false
	             },
	             
	             { "data": "chekerPartyId", 
	            	"orderable": false
	             },
	             { "data": "modeOfAction", 
	            	"orderable": false
	             },
	             { "data": "statusId", 
	            	"orderable": false
	             },
	             
	            { "data": "remarks",
	            	"orderable": false
	              },
	              
	              { "data": "totalChanged",
		          "render": function(data, type, row, meta){
		          	data = "";
		            if(type === 'display') {
		            	var title = "View Value Compare";
		            	if (row.totalChanged == 0) {
		            		title = "No Changes";
		            	}
		            	
		            	if (row.modeOfAction == 'CREATE' ) {
		            		title = "Please refer the Review screen";
		            	}
		            	
		            	var exitMessage = "";
		            	exitMessage = '<a href="#" class="btn btn-xs btn-primary tooltips view-exit-message pt-0 pb-0 m-0" data-userAuditRequestId="'+row.userAuditRequestId+'" data-serviceType="'+row.description+'" data-totalChanged="'+row.totalChanged+'" title="'+title+'"><strong>'+row.totalChanged+'</strong></a>';
		                data = '<div class="ml-1">'+exitMessage+'</div>';
		            }
		            return data;
		         }
		      	},
	             
	            { "data": "createdStamp",
	            	"orderable": false
	              },
	            
	            { "data": "userAuditRequestId",
	            	"orderable": false,
		          "render": function(data, type, row, meta){
		          	var data = '<div class="text-center ml-1">';
		          	var approvePermission = row.approvePermission;
			          	
		          	if (approvePermission =='Y') {
			            if(type === 'display') {
			            	
			            	data += '<div class="form-group row approveClass">';
			                data = data + '<a href="#" class="btn btn-xs btn-success tooltips m-1" title="Approve" onclick="javascript: triggerApprove(\''+row.userAuditRequestId+'\', \'APPROVED\');"> <i class="fa fa-check" aria-hidden="true"> </i> </a>';
			            	data = data + '<a href="#" class="btn btn-xs btn-danger tooltips m-1" title="Reject" onclick="javascript: triggerApprove(\''+row.userAuditRequestId+'\', \'REJECTED\');"> <i class="fa fa-times" aria-hidden="true"></i> </a>';
			            	
			            	if (row.makerPartyId == "${userLoginId}") {
			            		data = data + '<a href="#" class="btn btn-xs btn-warning tooltips m-1" title="Hold" onclick="javascript: triggerApprove(\''+row.userAuditRequestId+'\', \'IGNORED\');"> <i class="fa fa-hand-paper-o " aria-hidden="true"></i> </a>';
			            	}
										            	
			            	data = data + '<a href="'+row.requestUri+'&userAuditRequestId='+row.userAuditRequestId+'" class="btn btn-xs btn-primary tooltips m-1" title="Review"> <i class="fa fa-eye-slash" aria-hidden="true"></i> </a>';
			            	data = data + '</div>';
										            	
			            }
		            } else {
		            	data += '<div class="form-group row approveClass">';
			            	data = data + '<a href="#" class="btn btn-xs btn-warning tooltips m-1" title="Hold" onclick="javascript: triggerApprove(\''+row.userAuditRequestId+'\', \'IGNORED\');"> <i class="fa fa-hand-paper-o " aria-hidden="true"></i> </a>';
			            	data = data + '<a href="'+row.requestUri+'&userAuditRequestId='+row.userAuditRequestId+'" class="btn btn-xs btn-primary tooltips m-1" title="Review"> <i class="fa fa-eye-slash" aria-hidden="true"></i> </a>';
			            	data = data + '</div>';
		            }
		            
		            data += "</div>";
		            return data;
		          }
		         },   
		         
	        ],
	        "fnDrawCallback": function( oSettings ) {
	      		resetDefaultEvents();
	      		resetRequestEvents();
	    	}
		});
}	

function triggerApprove(userAuditRequestId, approveType) {
    var message = "Are you sure?";
    if("APPROVED" == approveType){
       bootbox.confirm(message, function(result) {
		   if (result) {
				makerCheckerAction(approveType, userAuditRequestId, '');
		   } else {
		   	    $('#list_pending_audit_requests input[name="'+userAuditRequestId+'"]').prop("checked", false);
		   }
	    });
    } else if("REJECTED" == approveType){
       $("#approveType").val(approveType);
       $("#userAuditRequestId").val(userAuditRequestId);
       $('#pendingRequestFormModalView').modal("show");
    } else if("IGNORED" == approveType){
       $("#approveType").val(approveType);
       $("#userAuditRequestId").val(userAuditRequestId);
       $('#pendingRequestFormModalView').modal("show");
    }
    return true;
}

function makerCheckerAction(approveType, userAuditRequestId, remarks) { 
    var url = "processUserAuditRequest?approveType="+approveType+"&userAuditRequestId="+userAuditRequestId+"&remarks="+remarks;
    
    $.ajax({
      url: url,
      type: 'POST',
      success : function(data) {
		        
        if (data.code == 200) {
        	$('#pendingRequestFormModalView').modal("hide");
        	findPendingRequests();
        }
        
      }
    });
	
 }
 	
</script>