<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<link rel="stylesheet" href="/crm-resource/css/custom.css" type="text/css"/>
<div class="row">
	<div id="main" role="main">
		<@sectionFrameHeader title=uiLabelMap.FindLeadBatch />
		<div class="col-md-12 col-lg-12 col-sm-12 ">
			<div id="accordion">
			  	<div class="row">
                    <@arrowDownToggle />
                </div>
                <div>
                	<div>
   						<form action="findLeadBatch" id="searchForm" name="searchForm" method="post" class="form-horizontal" data-toggle="validator">
   							<div class="border rounded bg-light margin-adj-accordian pad-top">
      							<div class="row p-2">
							      	<div class="col-md-3 col-sm-3">
							         	<@inputCell 
											id="leadId"
											placeholder=uiLabelMap.sequenceOrPartyId
											value=filterLeadBatch.leadId
											required=false
											maxlength=20
											/>
						         	</div>
							      	<div class="col-md-3 col-sm-3">
							         	<@inputCell 
											id="firstName"
											placeholder=uiLabelMap.firstName
											value=filterLeadBatch.firstName
											required=false
											maxlength=100
											/>
						         	</div>   	
							      	<div class="col-md-3 col-sm-3">
							         	<@inputCell 
											id="lastName"
											placeholder=uiLabelMap.lastName
											value=filterLeadBatch.lastName
											required=false
											maxlength=100
											/>
							     	</div>    
							      	<div class="col-md-3 col-sm-3">
							         	<@dropdownCell
											id="importStatusId"
											options=importStatusList
											required=false
											value=filterLeadBatch.importStatusId
											allowEmpty=true
											tooltip = uiLabelMap.status
											placeholder = uiLabelMap.status
											dataLiveSearch=true
											/>
							     	</div>      
							      	<div class="col-md-3 col-sm-3">
							      		<@inputDate 
											id="fromDate"
											placeholder="From Date"
											/>
							         </div>
							         <div class="col-md-3 col-sm-3">
							      		<@inputDate 
											id="thruDate"
											placeholder="Thru Date"
											/>
							         </div>
							         <div class="col-md-3 col-sm-3">
							         	<@inputCell
											id="batchId"
											placeholder=uiLabelMap.batchId
											value=filterLeadBatch.batchId
											required=false
											maxlength=20
											/>
							     	</div>
									<div class="col-md-3 col-sm-3">
							         	<@inputCell 
											id="uploadedByUserLoginId"
											placeholder=uiLabelMap.uploadedBy
											value=filterLeadBatch.uploadedBy
											required=false
											maxlength=100
											/>
							         </div>  
							         <div class="col-md-12 col-sm-12">
							         	<div class="text-right">
							         		<@button id="doSearch" label="Find"/>
							         	</div>
							         </div>
							      </div>
						      </div>
   							</form>
			     		</div>
		  			</div>
		  		</div>
				<div class="clearfix"></div>
		  		<div class="page-header border-b pt-2">
					<div class="float-right">
						<#if isApprover?has_content && isApprover>
						<input class="btn btn-xs btn-danger tooltips" title="Reject Selected Leads" id="rejected-selected-lead-button" value="Reject" type="button">
						<input class="btn btn-xs btn-primary tooltips" title="Approve Selected Leads" id="approved-selected-lead-button" value="Approve" type="button">
						</#if>
					</div>
					<@headerH2 title="Lead Batch List" class=""/>
		  			<div class="clearfix"> </div>
				</div>
		  		<div class="clearfix"> </div>
	  			<div class="table-responsive">  	
					<div id="myGrid" style="width: 100%;" class="ag-theme-balham"></div>     
				</div>
				<script type="text/javascript" src="/crm-resource/js/ag-grid/lead/leadBatch.js"></script>
			</div>
		<div class="clearfix"></div>
	</div><#-- End main-->
</div><#-- End row-->

<script type="text/javascript">
$("#doSearch").click(function(event) {
	event.preventDefault(); 
	loadAgGrid();
});       
var leadBatchProcessorGrid;

jQuery(document).ready(function() {	

$("#add-select-all").change(function(){  
    var status = this.checked; 
    $('input[name="leads"]').each(function(){ 
        this.checked = status; 
    });
});

$('#approved-selected-lead-button').on('click', function(){
	
	var rowsSelected = [];
			
	$('input[name="leads"]:checked').each(function() {
		//alert(this.value);
   		console.log(this.value);
   		
   		rowsSelected.push(this.value);
   		
	});
	
	if (rowsSelected.length == 0) {
		showAlert ("error", "Please select leads to be approved");
		return;
	}	
		
	//var customFieldId = $('#customFieldId').val();
	
	$.ajax({
		      
		type: "POST",
     	url: "approvedSelectedLead",
        data:  {"rowsSelected": rowsSelected},
        success: function (data) {   
            
            if (data.code == 200) {
				//showAlert ("success", "success count: "+data.successCount+", already exists count: "+data.alreadyExistsCount);
				showAlert ("success", data.successCount + " leads successfully approved!");
				findBatchLeadProcessors();
			} else {
				showAlert ("error", data.message);
			}  
			    	
        }
        
	});
		
});

$('#rejected-selected-lead-button').on('click', function(){
	
	var rowsSelected = [];
			
	$('input[name="leads"]:checked').each(function() {
		//alert(this.value);
   		console.log(this.value);
   		
   		rowsSelected.push(this.value);
   		
	});
	
	if (rowsSelected.length == 0) {
		showAlert ("error", "Please select leads to be approved");
		return;
	}	
		
	//var customFieldId = $('#customFieldId').val();
	
	$.ajax({
		      
		type: "POST",
     	url: "rejectedSelectedLead",
        data:  {"rowsSelected": rowsSelected},
        success: function (data) {   
            
            if (data.code == 200) {
				//showAlert ("success", "success count: "+data.successCount+", already exists count: "+data.alreadyExistsCount);
				showAlert ("success", data.successCount + " leads successfully rejected!");
				findBatchLeadProcessors();
			} else {
				showAlert ("error", data.message);
			}  
			    	
        }
        
	});
		
});

$(".form_datetime").datetimepicker({
    //autoclose: true,
    //isRTL: BootStrapInit.isRTL(),
    //format: "dd MM yyyy - hh:ii",
    //pickerPosition: (BootStrapInit.isRTL() ? "bottom-right" : "bottom-left")
});

});

function resetBatchLeadProcessorEvents() {
	
	$('#list-lead-batch-processor td.details-control').unbind( "click" );
	$('#list-lead-batch-processor td.details-control').bind( "click", function( event ) {
		
        var tr = $(this).closest('tr');
        var row = leadBatchProcessorGrid.row( tr );
     
        if ( row.child.isShown() ) {
            // This row is already open - close it
            row.child.hide();
            tr.removeClass('shown');
        }
        else {
            // Open this row
            var batchId = row.data()['batchId'];
            var subtable_id = "subtable-"+batchId;
            row.child(prepareBatchLeadGrid(subtable_id)).show(); /* HERE I format the new table */
            tr.addClass('shown');
            findBatchLeads(batchId, subtable_id); /*HERE I was expecting to load data*/
        }
    });
    
}

function prepareBatchLeadGrid ( table_id ) {
    // `d` is the original data object for the row
    return '<div class="page-header ml-4 mr-4"><h2 class="float-left display-4">Lead List</h2></div>' + 
    '<table id="'+table_id+'" class="table table-striped">'+
    '<thead>'+
    '<th>${uiLabelMap.sequenceId!}</th>'+
    '<th>${uiLabelMap.partyId!}</th>'+
    '<th>${uiLabelMap.firstName!}</th>'+
    '<th>${uiLabelMap.lastName!}</th>'+
    '<th>${uiLabelMap.address!}</th>'+
    '<th>${uiLabelMap.phoneNumber1!}</th>'+
    '<th>${uiLabelMap.status!}</th>'+
    '<th class="text-center">Action</th>'+
    '</thead>'+
    '</table>';
}

function findBatchLeads(batchId, subTableId) {
	
	var importStatusId = $("#importStatusId").val();
	var leadId = $('#leadId').val();
	//var batchId = $("#batchId").val();
	var firstName = $("#firstName").val();
	var lastName = $("#lastName").val();
	var uploadedByUserLoginId = $("#uploadedByUserLoginId").val();
	
	var fromDate = $('#findLeadBatchForm input[name="fromDate"]').val();
	var thruDate = $('#findLeadBatchForm input[name="thruDate"]').val();
   	
   	var url = "searchBatchLeads?importStatusId="+importStatusId+"&fromDate="+fromDate+"&thruDate="+thruDate+"&leadId="+leadId+"&batchId="+batchId+"&firstName="+firstName+"&lastName="+lastName+"&uploadedByUserLoginId="+uploadedByUserLoginId;
   
   	var actionColumnIndex = 4;
   	
	$('#'+subTableId).DataTable({
	    "processing": true,
	    "serverSide": true,
	    "destroy": true,
	    "ajax": {
            "url": url,
            "type": "POST"
        },
        "pageLength": 10,
        "stateSave": false,
        
        "columnDefs": [ 
        	{
				"targets": 6,
				"orderable": false
			} 
		],
				      
        "columns": [
			
			{ "data": "leadId" },
			{ "data": "primaryPartyId" },
            { "data": "firstName" },
            { "data": "lastName" },
            { "data": "address1" },
            { "data": "primaryPhoneNumber" },
            { "data": "importStatusName" },	       
            
            { "data": "leadId",
	          "render": function(data, type, row, meta){
	          	var data = '<div class="text-center ml-1" >';
	            if(type === 'display' && (row.importStatusId && (row.importStatusId == 'DATAIMP_ERROR' || row.importStatusId == 'DATAIMP_FAILED' || row.importStatusId == 'DATAIMP_NOT_APPROVED'))){
	                data += '<a class="btn btn-xs btn-primary tooltips" href="editDataImpLead?leadId='+row.leadId+'&backUrl=findLeadBatch" data-original-title="Edit" ><i class="fa fa-pencil info"></i></a>';
	            }
	            if(type === 'display' && (row.isDisalbed)){
	            	var disableReason = row.disableReason;
	            	disableReason += ". Want to Enable?";
	                data += '<a class="btn btn-xs btn-primary tooltips" href="javascript: enableLead(\''+row.primaryPartyId+'\', \''+batchId+'\', \''+subTableId+'\')" data-original-title="'+disableReason+'" ><i class="fa fa-eye info"></i></a>';
	            }
	            data += "</div>";
	            return data;
	          }
	         },     
            
        ],
        "fnDrawCallback": function( oSettings ) {
      		resetDefaultEvents();
    	}
	});
	
	$('#add-select-all').prop('checked', false);	
				
}

function findLeadByUploadedBy(uploadedByUserLoginId) {
	$('#uploadedByUserLoginId').val( uploadedByUserLoginId );
	loadAgGrid();
}

function enableLead (leadId, batchId, subTableId) {
	
	//alert(roleConfigId);

	$.ajax({
			      
		type: "POST",
     	url: "enableParty",
        data:  {"partyId": leadId},
        success: function (data) {   
            if (data.code == 200) {
				
				showAlert ("success", data.message)
				
				findBatchLeads(batchId, subTableId);
				
			} else {
				showAlert ("error", data.message)
			}
			    	
        }
        
	});    
	
}

</script>

