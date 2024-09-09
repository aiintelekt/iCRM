<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>

<div class="row">
	
  	<div class="col-lg-12 col-md-12 col-sm-12">
	
	<div class="page-header border-b pt-2">
        <@headerH2 title="${uiLabelMap.List} ${uiLabelMap.batchDataStatus!}" class="float-left"/>
        <div class="float-right">
        </div>
        <div class="clearfix"></div>
    </div>    	
	  	  	  	
  	<table class="table table-striped" id="batch-job-list">
	<thead>
	<tr>
		<th></th>
		<th>${uiLabelMap.jobExecutionId!}</th>
		<th>${uiLabelMap.jobName!}</th>
		<th>${uiLabelMap.createTime!}</th>
		<th>${uiLabelMap.startTime!}</th>
		<th>${uiLabelMap.endTime!}</th>
		<th>${uiLabelMap.status!}</th>
		<th>${uiLabelMap.exitCode!}</th>
		<th>${uiLabelMap.lastUpdated!}</th>
		<th class="">${uiLabelMap.errorMessage!}</th>
	</tr>
	</thead>
	<tbody>
		
	</tbody>
	</table>
</div>

<div id="modalDetailView" class="modal fade" >
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
					
					<@radioInputCell
			        id="exportFileType"
			        name="exportFileType"
			        options=exportFileTypes
			        inputColSize="col-sm-12"
			        value="ERROR_FILE"
			        />
					
					<@dropdownCell 
						id="exportType" 
						options=exportTypeList
						required=false 
						allowEmpty=true 
						dataLiveSearch=true 
						placeholder=uiLabelMap.selectExportType
						style="min-width: 150px"
						/>
						
					<div class="float-right pr-3">
						<a href="javascript:  callExportBatchError();"
							class="btn btn-xs btn-primary">${uiLabelMap.export}</a>
					</div>
					
				</div>
			</div>
			<div class="clearfix"></div>
			
			<table class="table table-striped error-logs">
			<thead>
			<tr>
				<th>${uiLabelMap.id!}</th>
				<th>${uiLabelMap.createTime!}</th>
				<th class="longtext-nowrap">${uiLabelMap.errorMessage!}</th>
			</tr>
			</thead>
			<tbody>
				
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

<form method="post" action="exportBatchError" id="exportBatchErrorFrom" name="exportBatchErrorFrom" class="form-horizontal"  novalidate="novalidate" data-toggle="validator">
	<input type="hidden" name="exportType" />
	<input type="hidden" name="exportFileType" />
	<input type="hidden" name="executionId" />
	<input type="hidden" name="exitType" />
</form>

<script type="text/javascript">

var jobExecutionGrid;
var executionId, exitType;

jQuery(document).ready(function() {	

	findBatchJobs();
	
	$('#modalDetailView').on('shown.bs.modal', function (e) {
	
		if (exitType == "job") {
			$('input[name=exportFileType]').attr("disabled",true);
			$("input[name=exportFileType][value='ERROR_FILE']").prop('checked', true);
		} else {
			$('input[name=exportFileType]').removeAttr("disabled");
			$("input[name=exportFileType][value='IMPORT_FILE']").prop('checked', true);
		}
	
	  	findErrorLogs(executionId, exitType);	
	});
	
	$('#modalDetailView').on('hidden.bs.modal', function (e) {
  		$('#modalDetailView .error-logs tbody').html("");
	});
		
});	

function resetJobExecutionEvents() {
	
	$('#batch-job-list td.details-control').unbind( "click" );
	$('#batch-job-list td.details-control').bind( "click", function( event ) {
		
        var tr = $(this).closest('tr');
        var row = jobExecutionGrid.row( tr );
     
        if ( row.child.isShown() ) {
            // This row is already open - close it
            row.child.hide();
            tr.removeClass('shown');
        }
        else {
            // Open this row
            var jobExecutionId = row.data()['jobExecutionId'];
            var subtable_id = "subtable-"+jobExecutionId;
            row.child(prepareStepExecutionGrid(jobExecutionId, subtable_id)).show(); /* HERE I format the new table */
            tr.addClass('shown');
            findBatchSteps(jobExecutionId, subtable_id); /*HERE I was expecting to load data*/
        }
    });
    
    $('.view-exit-message').unbind( "click" );
	$('.view-exit-message').bind( "click", function( event ) {
	
		event.preventDefault(); 
		$('#modalDetailView').modal("show");
		
		executionId = $(this).attr("data-executionId");
		exitType = $(this).attr("data-exitType");
		
		$('#modalDetailView .modal-title').html( '${uiLabelMap.errorMessage} for [ '+exitType+" - "+executionId+' ]' );
																										
	});
	
	$('.refresh-steps').unbind( "click" );
	$('.refresh-steps').bind( "click", function( event ) {
	
		event.preventDefault(); 
		
		jobExecutionId = $(this).attr("data-jobExecutionId");
		tableId = $(this).attr("data-tableId");
		
		findBatchSteps(jobExecutionId, tableId);
																										
	});
    
}

function findErrorLogs(executionId, exitType) {
	
   	var url = "searchBatchStepErrorLogs?executionId="+executionId+"&exitType="+exitType;
   
	$('#modalDetailView .error-logs').DataTable( {
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
			{ "data": "batchStepErrorLogId" },
            { "data": "createdStamp" },
            { "data": "errorMessage",
	          "render": function(data, type, row, meta){
	          	data = "";
	            if(type === 'display'){
	            	var errorMessage = row.errorMessage; 
	            	if (errorMessage && errorMessage.length > 300) {
	            		errorMessage = errorMessage.substring(0, 300)+'...';
	            	}
	                data = '<div class="ml-1 longtext-nowrap">'+errorMessage+'</div>';
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

function findBatchJobs() {
	
	//var searchPartyId = $("#partyId").val();
	
	var fromDate = $('#findJobExecutionForm input[name="fromDate"]').val();
	var thruDate = $('#findJobExecutionForm input[name="thruDate"]').val();
   	
   	//var url = "searchHdaCaAccouts?searchPartyId="+searchPartyId+"&fromDate="+fromDate+"&thruDate="+thruDate;
   	var url = "searchBatchJobs?statusType=BATCH_DATA&fromDate="+fromDate+"&thruDate="+thruDate;
   
	jobExecutionGrid = $('#batch-job-list').DataTable( {
	    "processing": true,
	    "serverSide": true,
	    "destroy": true,
	    "searching": false,
	    "ajax": {
            "url": url,
            "type": "POST"
        },
        "pageLength": 15,
        "stateSave": false,
        /*
        "columnDefs": [ 
        	{
				"targets": 8,
				"orderable": false,
				"className": "longtext"
			} 
		],
		*/	      
		"order": [[ 1, "desc" ]],
        "columns": [
			{
                "className":      'details-control',
                "orderable":      false,
                "data":           null,
                "defaultContent": ''
            },	        	
            { "data": "jobExecutionId" },
            { "data": "jobName" },
            { "data": "createTime" },
            { "data": "startTime" },
            { "data": "endTime" },
            { "data": "status" },
            { "data": "exitCode" },
            { "data": "lastUpdated" },
            //{ "data": "exitMessage" },
            
            { "data": "exitMessage",
	          "render": function(data, type, row, meta){
	          	data = "";
	            if(type === 'display') {
	            	var errorTitle = "View Error Messages";
	            	if (row.errorCount == 0) {
	            		errorTitle = "No Error";
	            	}
	            	var exitMessage = "";
	            	exitMessage = '<a href="#" class="btn btn-xs btn-primary tooltips view-exit-message pt-0 pb-0 m-0" data-executionId="'+row.jobExecutionId+'" data-exitType="job" title="'+errorTitle+'"><strong>'+row.errorCount+'</strong></a>';
	                data = '<div class="ml-1">'+exitMessage+'</div>';
	            }
	            return data;
	         }
	      	}
            
        ],
        "fnDrawCallback": function(settings, json) {
		    resetDefaultEvents();
		    resetJobExecutionEvents();
		}
	});
	
}

function findBatchSteps(jobExecutionId, subTableId) {
	
   	var url = "searchBatchSteps?jobExecutionId="+jobExecutionId;
   
	$('#'+subTableId).DataTable( {
	    "processing": true,
	    "serverSide": true,
	    "destroy": true,
	    "searching": false,
	    "ajax": {
            "url": url,
            "type": "POST"
        },
        "pageLength": 15,
        "stateSave": false,
        /*
        "columnDefs": [ 
        	{
				"targets": 14,
				"orderable": false,
				"className": "longtext"
			} 
		],
		*/	  
		"order": [[ 0, "asc" ]],    
        "columns": [
			{ "data": "stepExecutionId" },
            { "data": "stepName" },
            { "data": "startTime" },
            { "data": "endTime" },
            { "data": "status" },
            { "data": "commitCount" },
            { "data": "readCount" },
            { "data": "filterCount" },
            { "data": "actualWriteCount" },
            { "data": "readSkipCount" },
            { "data": "processSkipCount" },
            { "data": "rollbackCount" },
            { "data": "duplicateCount" },
            { "data": "exitCode" },
            { "data": "lastUpdated" },
            
            { "data": "exitMessage",
	          "render": function(data, type, row, meta){
	          	data = "";
	            if(type === 'display'){
	            	var exitMessage = "";
	            	var errorTitle = "View Error Messages";
	            	if (row.errorCount == 0) {
	            		errorTitle = "No Error";
	            	}
	            	exitMessage = '<a href="#" class="btn btn-xs btn-primary m5 tooltips view-exit-message" data-executionId="'+row.stepExecutionId+'" data-exitType="step" title="'+errorTitle+'"><strong>'+row.errorCount+'</strong></a>';
	                data = '<div class="ml-1">'+exitMessage+'</div>';
	            }
	            return data;
	         }
	      	}
            
	      	
        ],
        "fnDrawCallback": function(settings, json) {
		    resetDefaultEvents();
		    resetJobExecutionEvents();
		}
	});
	
}

function prepareStepExecutionGrid ( jobExecutionId, table_id ) {
    // `d` is the original data object for the row
    return '<div class="page-header ml-4 mr-4"><h2 class="float-left display-4">Job Execution Steps</h2><div class="float-right"><a href="#" class="btn btn-xs btn-primary m5 refresh-steps tooltips" data-jobExecutionId="'+jobExecutionId+'" data-tableId="'+table_id+'" title="Refresh"><i class="fa fa-refresh" aria-hidden="true"></i></a></div></div>' + 
    '<table id="'+table_id+'" class="table table-striped">'+
    '<thead>'+
    '<th>${uiLabelMap.stepExecutionId!}</th>'+
    '<th>${uiLabelMap.stepName!}</th>'+
    '<th>${uiLabelMap.startTime!}</th>'+
    '<th>${uiLabelMap.endTime!}</th>'+
    '<th>${uiLabelMap.status!}</th>'+
    '<th>${uiLabelMap.commitCount!}</th>'+
    '<th>${uiLabelMap.readCount!}</th>'+
    '<th>${uiLabelMap.filterCount!}</th>'+
    '<th>${uiLabelMap.writeCount!}</th>'+
    '<th>${uiLabelMap.readSkipCount!}</th>'+
    '<th>${uiLabelMap.processSkipCount!}</th>'+
    '<th>${uiLabelMap.rollbackCount!}</th>'+
    '<th>${uiLabelMap.duplicateCount!}</th>'+
    '<th>${uiLabelMap.exitCode!}</th>'+
    '<th>${uiLabelMap.lastUpdated!}</th>'+
    '<th class="">${uiLabelMap.errorMessage!}</th>'+
    '</thead>'+
    '</table>';
}

function callExportBatchError() {

	if ( $('#exportType').val() ) {
		$('#exportBatchErrorFrom input[name="exportType"]').val( $('#exportType').val() );
		$('#exportBatchErrorFrom input[name="exportFileType"]').val( $("input[name='exportFileType']:checked").val() );

		$('#exportBatchErrorFrom input[name="executionId"]').val( executionId );
		$('#exportBatchErrorFrom input[name="exitType"]').val( exitType );
		
		$('#exportBatchErrorFrom').submit();
	} else {
		showAlert ("error", "Please select export type");
	}
		
}
	
</script>