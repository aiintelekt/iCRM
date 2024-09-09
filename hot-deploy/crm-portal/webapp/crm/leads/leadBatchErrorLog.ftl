<#include "component://homeapps/webapp/homeapps/lib/ofbizFormMacros.ftl"/>

<div class="page-header">
	<h1 class="float-left">${uiLabelMap.LeadBatchErrorLog!}</h1>
	<div class="float-right">
		
	</div>
</div>

<div class="card-header mt-2 mb-3">
   <form id="findLeadBatchForm" method="post" class="form-horizontal" data-toggle="validator">
   		
   		<input type="hidden" name="activeTab" value="economicsMetrics" />	
   		
      <div class="row">
      
      	<div class="col-md-2 col-sm-2">
      		<@simpleDateInput 
				name="fromDate"
				tooltip="From Date"
				/>
         </div>
         <div class="col-md-2 col-sm-2">
      		<@simpleDateInput 
				name="thruDate"
				tooltip="Thru Date"
				/>
         </div>
         <div class="col-md-2 col-sm-2">
         	<@simpleInput 
				id="batchId"
				placeholder=uiLabelMap.batchId
				value=filterLeadBatch.batchId
				required=false
				maxlength=20
				/>
         </div>
		<div class="col-md-2 col-sm-2">
         	<@simpleInput 
				id="uploadedByUserLoginId"
				placeholder=uiLabelMap.uploadedBy
				value=filterLeadBatch.uploadedBy
				required=false
				maxlength=100
				/>
         </div>  
         <div class="col-md-2 col-sm-2">
         	<@simpleInput 
				id="firstName"
				placeholder=uiLabelMap.firstName
				value=filterLeadBatch.firstName
				required=false
				maxlength=100
				/>
         </div>    
         <div class="col-md-2 col-sm-2">
         	<@simpleInput 
				id="lastName"
				placeholder=uiLabelMap.lastName
				value=filterLeadBatch.lastName
				required=false
				maxlength=100
				/>
         </div>   
         <div class="col-md-2 col-sm-2">
         	<@simpleInput 
				id="leadId"
				placeholder=uiLabelMap.sequenceOrPartyId
				value=filterLeadBatch.leadId
				required=false
				maxlength=20
				/>
         </div>
         <div class="col-md-2 col-sm-2">
         	<@simpleDropdownInput 
				id="errorCodeId"
				options=errorCodeList
				required=false
				value=filterLeadBatch.errorCodeId
				allowEmpty=true
				tooltip = uiLabelMap.errorCodeId
				emptyText = uiLabelMap.errorCodeId
				dataLiveSearch=true
				/>
         </div>
      		 
         <@fromSimpleAction id="find-lead-button" showCancelBtn=false isSubmitAction=false submitLabel="Find"/>
        	
      </div>
   </form>
   <div class="clearfix"> </div>
</div>

<form method="post" action="exportLeadErrorLog" id="exportLeadErrorLogForm" class="form-horizontal" name="exportLeadErrorLogForm" novalidate="novalidate" data-toggle="validator">
	
	<input type="hidden" name="exportType" />
	
	<input type="hidden" name="batchId" />
	<input type="hidden" name="uploadedByUserLoginId" />
	<input type="hidden" name="firstName" />
	<input type="hidden" name="lastName" />
	<input type="hidden" name="fromDate" />
	<input type="hidden" name="thruDate" />
	<input type="hidden" name="leadId" />
	<input type="hidden" name="errorCodeId" />
</form>



<div class="clearfix"> </div>
<div class="page-header mt-2 mb-2 nav-tabs">
	<h2 class="float-left ml-1">Lead Batch Error Log List </h2>
	<div class="float-right">
  
	</div>
	
</div>

<div class="table-responsive">
   <div class="row ">
   <div class="col-md-1 offset-sm-10">
	 <@simpleDropdownInput 
		id="exportType"
		options=exportTypeList
		required=false
		allowEmpty=false
		dataLiveSearch=true
	/>
	</div>
	  <div class="col-md-1">
	<div class="float-right pr-3" >
		<a href="javascript:callexportLeadErrorLog();" class="btn btn-xs btn-primary" id="exportLeadErrorLog">${uiLabelMap.export}</a>
	</div>
	</div>
	</div>
	
	<div class="clearfix"> </div>
	<table class="table table-hover" id="list-lead-batch">
	<thead>
	<tr>
		<th>${uiLabelMap.batchId!}</th>
		<th>${uiLabelMap.uploadedDate!}</th>
		<th>${uiLabelMap.uploadedBy!}</th>
		<th>${uiLabelMap.sequenceId!}</th>
		<th>${uiLabelMap.partyId!}</th>
		<th>${uiLabelMap.firstName!}</th>
		<th>${uiLabelMap.lastName!}</th>
		<th>${uiLabelMap.address!}</th>
		<th>${uiLabelMap.phoneNumber1!}</th>
		<th>${uiLabelMap.errorCode!}</th>
		<th>${uiLabelMap.status!}</th>
		<th class="text-center">Action</th>
	</tr>
	</thead>
	<tbody>
		
	</tbody>
	</table>
</div>

<@auditLogModal id="auditModalDetailView" isShowAuditType=true/>

<script type="text/javascript">

var pkCombinedValueText;

jQuery(document).ready(function() {	

$('#auditModalDetailView').on('shown.bs.modal', function (e) {
  	findValidationAuditLogs(pkCombinedValueText);	
});

$('#find-lead-button').on('click', function(){
	findLeadBatchs();
});

$(".form_datetime").datetimepicker({
    //autoclose: true,
    //isRTL: BootStrapInit.isRTL(),
    //format: "dd MM yyyy - hh:ii",
    //pickerPosition: (BootStrapInit.isRTL() ? "bottom-right" : "bottom-left")
});

});

function findValidationAuditLogs(pkCombinedValueText) {
	
   	var url = "searchValidationAuditLogs?pkCombinedValueText="+pkCombinedValueText;
   
	$('#auditModalDetailView .error-logs').DataTable( {
	    "processing": true,
	    "serverSide": true,
	    "destroy": true,
	    "searching": false,
	    "ajax": {
            "url": url,
            "type": "POST",
            "async": true
        },
        "pageLength": 15,
        "stateSave": false,
        "order": [[ 4, "desc" ]],
        /*
        "columnDefs": [ 
        	{
				"targets": 14,
				"orderable": false,
				"className": "longtext"
			} 
		],
		*/	      
        "columns": [
			{ "data": "validationAuditType",
	          "render": function(data, type, row, meta){
	          	data = "";
	            if(type === 'display'){
	            	if (row.validationAuditType == 'VAT_LEAD_IMPORT') {
	            		data = "Auto Correction";
	            	} else if (row.validationAuditType == 'VAT_LEAD_DEDUP') {
	            		data = "Dedup";
	            	}
	            }
	            return data;
	         }
	      	},
			{ "data": "oldValueText" },
			{ "data": "newValueText" },
			{ "data": "changedFieldName" },
			{ "data": "changedByInfo" },
            { "data": "createdStamp" },
            { "data": "comments",
	          "render": function(data, type, row, meta){
	          	data = "";
	            if(type === 'display'){
	            	var comments = row.comments; 
	            	if (comments && comments.length > 300) {
	            		comments = comments.substring(0, 300)+'...';
	            	}
	                data = '<div class="ml-1">'+comments+'</div>';
	            }
	            return data;
	         }
	      	}
            
        ],
        "fnDrawCallback": function(settings, json) {
		    resetDefaultEvents();
		}
	});
	
}

function resetCommonEvents() {
	$('.view-audit-message').unbind( "click" );
	$('.view-audit-message').bind( "click", function( event ) {
	
		event.preventDefault(); 
		$('#auditModalDetailView').modal("show");
		
		pkCombinedValueText = $(this).attr("data-leadId");
		
		$('#auditModalDetailView .modal-title').html( '${uiLabelMap.auditMessage} for [ '+pkCombinedValueText+' ]' );
																										
	});
}

findLeadBatchs();
function findLeadBatchs() {
	
	var importStatusId = 'DATAIMP_ERROR';
	var leadId = $('#leadId').val();
	var batchId = $("#batchId").val();
	var firstName = $("#firstName").val();
	var lastName = $("#lastName").val();
	var uploadedByUserLoginId = $("#uploadedByUserLoginId").val();
	var errorCodeId = $("#errorCodeId").val();
	
	var fromDate = $('#findLeadBatchForm input[name="fromDate"]').val();
	var thruDate = $('#findLeadBatchForm input[name="thruDate"]').val();
   	
   	var url = "searchBatchLeads?importStatusId="+importStatusId+"&fromDate="+fromDate+"&thruDate="+thruDate+"&leadId="+leadId+"&batchId="+batchId+"&firstName="+firstName+"&lastName="+lastName+"&uploadedByUserLoginId="+uploadedByUserLoginId+"&errorCodeId="+errorCodeId;
   
	$('#list-lead-batch').DataTable({
	    "processing": true,
	    "serverSide": true,
	    "destroy": true,
	    "ajax": {
            "url": url,
            "type": "POST"
        },
        "pageLength": 20,
        "stateSave": true,
        
        "columnDefs": [ 
        	{
				"targets": 5,
				"orderable": false
			} 
		],
		"order": [[ 1, "desc" ]],		      
        "columns": [
				 
			{ "data": "leadId",
	          "render": function(data, type, row, meta){
	          	var data = '';
	            if(type === 'display'){
	                data = '<div class="text-left ml-1" ><a href="javascript: findLeadByBatchId(\''+row.batchId+'\')" class="btn btn-xs btn-primary m5 tooltips"><strong>'+row.batchId+'</strong></a></div>';
	            }
	            return data;
	          }
	         }, 
	         { "data": "createdStamp" },
	        { "data": "leadId",
	          "render": function(data, type, row, meta){
	          	var data = '';
	            if(type === 'display'){
	                data = '<div class="text-left ml-1" ><a href="javascript: findLeadByUploadedBy(\''+row.uploadedByUserLoginId+'\')" class="btn btn-xs btn-primary m5 tooltips"><strong>'+row.uploadedByUserLoginId+'</strong></a></div>';
	            }
	            return data;
	          }
	         },   	        	
				        	       	       	
            { "data": "leadId" },
            { "data": "primaryPartyId" },
            { "data": "firstName" },
            { "data": "lastName" },
            //{ "data": "leadName" },
            { "data": "address1" },
            { "data": "primaryPhoneNumber" },
            
            //{ "data": "errorCodes" },
            { "data": "leadId",
	          "render": function(data, type, row, meta){
	          	var data = '';
	            if(type === 'display'){
	            	for (var key in row.codeList) {
	            		data += '<span class="tooltips" data-html="true" data-original-title="'+row.codeList[key]+'">['+key+']</span> ';
	            	}		
	            }
	            return data;
	          }
	         },  
            
            { "data": "importStatusName" },	     
			            
            { "data": "leadId",
	          "render": function(data, type, row, meta){
	          	var data = '<div class="text-center ml-1" >';
	            if(type === 'display') {
	            	var auditTitle = "View Audit Messages";
	            	if (row.auditCount == 0) {
	            		auditTitle = "No Audit Log";
	            	}
	            	var exitMessage = "";
	            	exitMessage = '<a href="#" class="btn btn-xs btn-primary tooltips view-audit-message" data-leadId="'+row.leadId+'" title="'+auditTitle+'"><strong>'+row.auditCount+'</strong></a>';
	                data += exitMessage;
	            }
	            if(type === 'display' && (row.importStatusId && (row.importStatusId == 'DATAIMP_ERROR' || row.importStatusId == 'DATAIMP_FAILED' || row.importStatusId == 'DATAIMP_NOT_APPROVED'))){
	                data += '<a class="btn btn-xs btn-primary tooltips" href="editDataImpLead?leadId='+row.leadId+'&backUrl=leadBatchErrorLog" data-original-title="Edit" ><i class="fa fa-pencil info"></i></a>';
	            }
	            if(type === 'display' && (row.isDisalbed)){
	            	var disableReason = row.disableReason;
	            	disableReason += ". Want to Enable?";
	                data += '<a class="btn btn-xs btn-primary tooltips" href="javascript: enableLead(\''+row.primaryPartyId+'\')" data-original-title="'+disableReason+'" ><i class="fa fa-eye info"></i></a>';
	            }
	            
	            data += "</div>";
	            return data;
	          }
	         },     
			            
        ],
        "fnDrawCallback": function( oSettings ) {
      		resetDefaultEvents();
      		resetCommonEvents();
    	}
	});
	
	$('#add-select-all').prop('checked', false);	
				
}

function enableLead (leadId) {
	
	//alert(roleConfigId);

	$.ajax({
			      
		type: "POST",
     	url: "enableParty",
        data:  {"partyId": leadId, "isExecuteImport": "Y"},
        success: function (data) {   
            if (data.code == 200) {
				
				showAlert ("success", data.message)
				
				findLeadBatchs();
				
			} else {
				showAlert ("error", data.message)
			}
			    	
        }
        
	});    
	
}

function findLeadByBatchId(batchId) {
	$('#batchId').val( batchId );
	findLeadBatchs();
}

function findLeadByUploadedBy(uploadedByUserLoginId) {
	$('#uploadedByUserLoginId').val( uploadedByUserLoginId );
	findLeadBatchs();
}

function callexportLeadErrorLog() {

	$('#exportLeadErrorLogForm input[name="exportType"]').val( $('#exportType').val() );
	$('#exportLeadErrorLogForm input[name="fromDate"]').val( $('#fromDate').val() );
	$('#exportLeadErrorLogForm input[name="thruDate"]').val( $('#thruDate').val() );
	$('#exportLeadErrorLogForm input[name="batchId"]').val( $('#batchId').val() );
	$('#exportLeadErrorLogForm input[name="uploadedByUserLoginId"]').val( $('#uploadedByUserLoginId').val() );
	$('#exportLeadErrorLogForm input[name="firstName"]').val( $('#firstName').val() );
	$('#exportLeadErrorLogForm input[name="lastName"]').val( $('#lastName').val() );
	$('#exportLeadErrorLogForm input[name="leadId"]').val( $('#leadId').val() );
	$('#exportLeadErrorLogForm input[name="errorCodeId"]').val( $('#errorCodeId').val() );
	

	$('#exportLeadErrorLogForm').submit();
}

</script>

