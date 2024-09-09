<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<#include "component://homeapps/webapp/homeapps/lib/modal.ftl"/>
<div class="row">
	<div id="main" role="main">
		<@sectionFrameHeader title="${uiLabelMap.LeadBatchErrorLog!}" />
		<div class="col-md-12 col-lg-12 col-sm-12 ">
			<div id="accordion">
			  	<div class="row">
                    <@arrowDownToggle />
                </div>
                <div>
                	<div>
   						<form id="searchForm" name="searchForm" method="post" class="form-horizontal" data-toggle="validator">
   							<div class="border rounded bg-light margin-adj-accordian pad-top">
   								<input type="hidden" name="importStatusId" value="DATAIMP_ERROR" />	   		
      							<div class="row p-2">
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
							      		<@inputDate
							      			id="datetimepicker10"
											name="fromDate"
											placeholder=uiLabelMap.fromDate
											/>
							         </div>
							         <div class="col-md-3 col-sm-3">
							      		<@inputDate 
							      			id="datetimepicker11"
											name="thruDate"
											placeholder=uiLabelMap.thruDate
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
											id="errorCodeId"
											options=errorCodeList
											required=false
											value=filterLeadBatch.errorCodeId
											allowEmpty=true
											tooltip = uiLabelMap.errorCodeId
											placeholder = uiLabelMap.errorCodeId
											dataLiveSearch=true
											/>
							         </div>
							      	 <div class="col-md-12 col-sm-12">
							      	 	<div class="text-right">
							         	<@submit id="doSearch" label="Find"/>
							         	</div>
							         </div>
							      </div>
						      </div>
						  </form>
			     		</div>
		  			</div>
	  			</div>
	  			<div class="page-header border-b pt-2">
					<@headerH2 title="Lead Batch Error Log List" class="float-left"/>
					<div class="pr-3" >
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
							<input type="hidden" name="uploadedDate" />
							<input type="hidden" name="companyName" />
							<input type="hidden" name="leadId" />
							<input type="hidden" name="companyAddress" />
							<input type="hidden" name="phoneNumber1" />
						</form>
						<a href="javascript:callexportLeadErrorLog();" class="btn btn-xs btn-primary" id="exportLeadErrorLog">${uiLabelMap.export}</a>
					</div>
					<div class="clearfix"></div>
				</div>
		  		<div class="clearfix"> </div>
	  			<div class="table-responsive">  	
					<div id="myGrid" style="width: 100%;" class="ag-theme-balham"></div>     
				</div>
				<script type="text/javascript" src="/crm-resource/js/ag-grid/lead/leadBatchErrorLog.js"></script>
			</div>
		<div class="clearfix"></div>
	</div><#-- End main-->
</div><#-- End row-->
<script>
$("#doSearch").click(function(event) {
	event.preventDefault(); 
	loadAgGrid();
});       
</script>

<@auditLogModal id="auditModalDetailView" isShowAuditType=true/>
</div>
</div>
</div>
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
        "pageLength": "15",
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
	loadAgGrid();
}

function findLeadByUploadedBy(uploadedByUserLoginId) {
	$('#uploadedByUserLoginId').val( uploadedByUserLoginId );
	loadAgGrid();
}

function callexportLeadErrorLog() {
	
	$('#exportLeadErrorLogForm input[name="exportType"]').val( "EXCEL" );
	
	$('#exportLeadErrorLogForm input[name="fromDate"]').val( $('#fromDate').val() );
	$('#exportLeadErrorLogForm input[name="thruDate"]').val( $('#thruDate').val() );
	$('#exportLeadErrorLogForm input[name="batchId"]').val( $('#batchId').val() );
	$('#exportLeadErrorLogForm input[name="uploadedByUserLoginId"]').val( $('#uploadedByUserLoginId').val() );
	$('#exportLeadErrorLogForm input[name="firstName"]').val( $('#firstName').val() );
	$('#exportLeadErrorLogForm input[name="lastName"]').val( $('#lastName').val() );
	$('#exportLeadErrorLogForm input[name="leadId"]').val( $('#leadId').val() );
	$('#exportLeadErrorLogForm input[name="errorCodeId"]').val( $('#errorCodeId').val() );
	$('#exportLeadErrorLogForm input[name="uploadedDate"]').val( $('#createdStamp').val() );
	$('#exportLeadErrorLogForm input[name="companyName"]').val( $('#companyName').val() );
	$('#exportLeadErrorLogForm input[name="leadId"]').val( $('#leadId').val() );
	$('#exportLeadErrorLogForm input[name="companyAddress"]').val( $('#address1').val() );
	$('#exportLeadErrorLogForm input[name="phoneNumber1"]').val( $('#primaryPhoneNumber').val() ); 

	$('#exportLeadErrorLogForm').submit();
}

</script>

