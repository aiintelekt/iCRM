<#include "component://homeapps/webapp/homeapps/lib/ofbizFormMacros.ftl"/>
<script type="text/javascript" language="javascript" src="/bootstrap/js/jquery.dataTables.min.js"></script>
<div class="clearfix"> </div>
<div id="multiple-value-config" class="row padding-r" style="padding-top: 20px; display: none">

	<div class="col-md-6 col-sm-6">
				
		<div class="panel panel-default">
			<div class="panel-heading" role="tab" id="multiValue-heading">
				<h4 class="panel-title">
					<a role="button" class="collapsed" data-toggle="collapse" data-parent="#accordionMenu"
						href="#accordion-multiValue" aria-expanded="false"
						aria-controls="collapseOne"> ${uiLabelMap.MultiValue} </a>
				</h4>
			</div>
			<div id="accordion-multiValue" class="panel-collapse collapse"
				role="tabpanel" aria-labelledby="multiValue-heading">
				<div class="panel-body">
				
					<div class="page-header">
						<h2 class="float-left">${uiLabelMap.Create} ${uiLabelMap.MultiValue}</h2>
					</div>
					
					<div class="portlet-body form">
					
						<div class="card-header mt-2">
						   	<form id="createCustomFieldMultiValueForm" method="post" action="<@ofbizUrl>createCustomFieldMultiValue</@ofbizUrl>"
								class="form-horizontal" name="searchContant" novalidate="novalidate" data-toggle="validator">
								
								<input type="hidden" id="mvCustomFieldId" name="mvCustomFieldId" value="${customFieldId!}" />
								
								<div class="row">
									<div class="col-md-2 col-sm-2">
										<div class="form-group row mr">
											<input type="text" class="form-control input-sm"
												name="fieldValue" id="fieldValue"
												placeholder="Field Value" required>
										</div>
									</div>
									<div class="col-md-2 col-sm-2">
										<div class="form-group row mr">
											<input type="text" class="form-control input-sm"
												name="description" id="description" placeholder="Description" maxlength="255" required >
										</div>
									</div>
									<div class="col-md-2 col-sm-2">
										<div class="form-group row mr">
											<select class="ui dropdown form-control input-sm" id="multi-value-hide" name="hide" >
												<option value="" data-content="<span class='nonselect'>Select ${uiLabelMap.hide!}</span>" selected>Select ${uiLabelMap.hide!}</option>
												<option value="Y">${uiLabelMap.yes!}</option>
												<option value="N">${uiLabelMap.no!}</option>
											</select>
										</div>
									</div>
									<div class="col-md-2 col-sm-2">
										<div class="form-group row mr">
											<input type="number" class="form-control input-md " value="" id="mvSequenceNumber" name="mvSequenceNumber" placeholder="${uiLabelMap.sequence!}" min="1" required >
										</div>
									</div>
									<div class="col-md-1 col-sm-1">
										<input type="button" class="btn btn-sm btn-primary"
											id="add-multivalue-button" value="Add" />
									</div>
								</div>
							</form>
							<div class="clearfix"></div>
						</div>
													
					</div>
					
					<div class="clearfix"></div>
					<div class="page-header">
						<h2 class="float-left">${uiLabelMap.List} ${uiLabelMap.MultiValue}</h2>
						<div class="float-right">
					  		<input class="btn btn-xs btn-danger mt-2 mr-1" id="remove-selected-value-button" value="Remove Selected Values" type="button">	
						</div>
					</div>
					<div class="table-responsive">
						<table id="multi-value-list" class="table table-striped">
							<thead>
								<tr>
									<th>${uiLabelMap.fieldValue!}</th>
									<th>${uiLabelMap.description!}</th>
									<th>${uiLabelMap.hide!}</th>
									<th>${uiLabelMap.sequence!}</th>
									<th class="text-center">Action</th>
									<th><div class="ml-1"><input id="remove-multivalue-select-all" type="checkbox"></div></th>
								</tr>
							</thead>
							<tbody>
							</tbody>
						</table>
					</div>
					
				</div>
			</div>
		</div>
		
	</div>
</div>	

<script>

jQuery(document).ready(function() { 

$('#remove-selected-value-button').on('click', function(){
		
	var rowsSelected = [];
			
	$('input[name="selected-multivalues"]:checked').each(function() {
		//alert(this.value);
   		console.log(this.value);
   		
   		rowsSelected.push(this.value);
   		
	});
		
	var customFieldId = $('#mvCustomFieldId').val();
	var groupId = $('#groupId').val();
		
	$.ajax({
		      
		type: "POST",
     	url: "removeSelectedSegmentValueMultiValues",
        data:  {"groupId": groupId, "customFieldId": customFieldId, "rowsSelected": rowsSelected},
        success: function (data) {   
        
			if (data.code == 200) {
				showAlert ("success", "remove count: "+data.successCount);
            	findMultiValues();
			} else {
				showAlert ("error", data.message);
			}           
						    	
        }
        
	});
	
});

$('#add-multivalue-button').on('click', function(){
	
	if ($('#fieldValue').val() && $('#description').val() && $('#mvSequenceNumber').val()) {
		$.post('createSegmentValueMultiValue', $('#createCustomFieldMultiValueForm').serialize(), function(returnedData) {
	
			if (returnedData.code == 200) {
				
				showAlert ("success", returnedData.message)
				
				$('#createCustomFieldMultiValueForm')[0].reset();
				findMultiValues();
				
			} else {
				showAlert ("error", returnedData.message)
			}
			
		});
	} else {
		showAlert ("error", "fill up all the values");
	}
	
});

$("#remove-multivalue-select-all").change(function(){  
    var status = this.checked; 
    $('input[name="selected-multivalues"]').each(function(){ 
        this.checked = status; 
    });
});

$('#multiValue-heading').click(function () {

	findMultiValues();

}); 

});

function removeMultiValue (valueSeqNum) {
	
	var customFieldId = $('input[name=customFieldId]').val();

	$.ajax({
			      
		type: "POST",
     	url: "removeSegmentValueMultiValue",
        data:  {"customFieldId": customFieldId, "valueSeqNum": valueSeqNum},
        success: function (data) {   
            
            findMultiValues();
			    	
        }
        
	});    
	
}

//findMultiValues();
function findMultiValues() {
	
	var customFieldId = $('input[name=customFieldId]').val();
	var groupId = $('#groupId').val();
	
   	var url = "getSegmentValueMultiValues?customFieldId="+customFieldId+"&groupId="+groupId;
   
	$('#multi-value-list').DataTable( {
		    "processing": true,
		    "destroy": true,
		    "ajax": {
	            "url": url,
	            "type": "POST"
	        },
	        "pageLength": 10,
	        "order": [[ 4, "asc" ]],
	        
	        "columnDefs": [ 
	        	{
					"targets": 5,
					"orderable": false
				} 
			],
					      
	        "columns": [
	        	
	            { "data": "fieldValue" },
	            { "data": "description" },
	            { "data": "hide" },
	            { "data": "sequenceNumber" },
	            { "data": "sequenceNumber",
		          "render": function(data, type, row, meta){
		            if(type === 'display'){
		                data = '<div class="text-center ml-1" ><a class="btn btn-xs btn-danger tooltips remove-role-config" href="javascript:removeMultiValue('+row.sequenceNumber+')" data-original-title="Remove" data-config-id="'+row.sequenceNumber+'"><i class="fa fa-times red"></i></a></div>';
		            }
		            return data;
		          }
		         },
		         
		         { "data": "sequenceNumber",
		          "render": function(data, type, row, meta){
		            if(type === 'display'){
		                data = '<div class="ml-1"><input type="checkbox" name="selected-multivalues" value="' + row.sequenceNumber + '"></div>';
		            }
		            return data;
		         }
		      	},
	            
	        ],
	        "fnDrawCallback": function( oSettings ) {
	      		resetDefaultEvents();
	    	}
		});
		
	$("#remove-multivalue-select-all").prop('checked', false);
			
}

</script>
