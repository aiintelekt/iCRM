<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<script type="text/javascript" language="javascript" src="/bootstrap/js/jquery.dataTables.min.js"></script>
<div class="row">
	<div id="main" role="main" class="pd-btm-title-bar">
	<div class="col-md-12 col-lg-12 col-sm-12 dash-panel">
	<#assign extra='<a href="findContactField" class="btn btn-xs btn-primary m5 tooltips" title="Cancel" >Cancel</a>' + 
		'<a href="editContactFieldGroup?groupId=${customFieldGroup.groupId}" class="btn btn-xs btn-primary m5 tooltips" title="Back to ${customFieldGroup.groupName!}" >Back</a>' />
	<@sectionFrameHeaderTab title="${uiLabelMap.Update} ${uiLabelMap.ContactFieldGroup}" />

	<#-- <h1 class="float-left">${uiLabelMap.Update} ${uiLabelMap.CustomField} <#if customFieldGroup.groupName?has_content>for [ ${customFieldGroup.groupName} <i class="fa fa-arrow-right" aria-hidden="true"></i> ${customField.customFieldName} ]</#if> </h1> -->
		<div class="row p-2">
				<div class="col-md-6 col-sm-6" style="padding:0px">
				<form role="form" class="form-horizontal" 
					action="<@ofbizUrl>updateContactField</@ofbizUrl>" 
					encType="multipart/form-data" method="post" data-toggle="validator">
					<input type="hidden" id="customFieldId" name="customFieldId" value="${customFieldId!}" />																																																																									
					<#if groupId?has_content>
						<inputHidden" id="groupId" value="${customField.groupId!}" />									
					<#else>	
						<@dropdownCell 
							id="groupId"
							label=uiLabelMap.contactFieldGroup
							options=groupList
							required=true
							value=customField.groupId
							allowEmpty=true
							/>
					</#if>
					<@inputRow 
						id="customFieldName"
						label=uiLabelMap.contactFieldName
						placeholder=uiLabelMap.contactFieldName
						value=customField.customFieldName
						required=true
						maxlength=255
						/>
					<@dropdownCell 
						id="customFieldType"
						label=uiLabelMap.contactFieldType
						options=fieldTypeList
						required=true
						value=customField.customFieldType
						allowEmpty=true
						/>	
					<@dropdownCell 
						id="customFieldFormat"
						label=uiLabelMap.contactFieldFormat
						options=fieldFormatList
						required=true
						value=customField.customFieldFormat
						allowEmpty=true
						/>	
					<@dropdownCell 
						id="customFieldLength"
						label=uiLabelMap.fieldLength
						options=fieldLengthList
						required=false
						value="${customField.customFieldLength!}"
						allowEmpty=true
						/>					
					<@inputRow 
						id="sequenceNumber"
						label=uiLabelMap.sequence
						placeholder=uiLabelMap.sequence
						value=customField.sequenceNumber
						type="number"
						min=1
						/>					
					<@dropdownCell 
						id="hide"
						label=uiLabelMap.hide
						options=yesNoOptions
						required=false
						value=customField.hide
						allowEmpty=true
						/>																																																																																																																																																																																																																																															
				</div>
                <div class="clearfix"></div>
                <div class="col-md-12 col-sm-12">
                    <div class="form-group row">
                        <div class="offset-sm-2 col-sm-7">
                            <@submit label="Submit"/>  
                        </div>
                    </div>
                </div>
			</form>		
			</div>
		</div>
	</div>
</div>
</div>
<#-- <div class="clearfix"> </div>
<div class="row padding-r" style="padding-top: 20px">

	<#if customField.customFieldType?has_content && customField.customFieldType == "MULTIPLE">
	<div class="col-md-6 col-sm-6">
				
		<div class="panel panel-default">
			<div class="panel-heading" role="tab" id="multiValue-heading">
				<h4 class="panel-title">
					<a role="button" data-toggle="collapse" data-parent="#accordionMenu"
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
											<select class="ui dropdown form-control input-sm tooltips" data-original-title="Hide" id="multi-value-hide" name="hide" >
												<option value="" data-content="<span class='nonselect'>Select ${uiLabelMap.hide!}</span>" selected>Select ${uiLabelMap.hide!}</option>
												<option value="Y">${uiLabelMap.yes!}</option>
												<option value="N">${uiLabelMap.no!}</option>
											</select>
										</div>
									</div>
									<div class="col-md-2 col-sm-2">
										<div class="form-group row mr">
											<input type="number" class="form-control input-md tooltips " value="" id="mvSequenceNumber" name="mvSequenceNumber" placeholder="${uiLabelMap.sequence!}" min="1" >
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
						    <button value="submit" class="btn btn-primary btn-xs mt-2 mr-1" onclick="multiValueUpdateAll();">Update all</button>
					  		<input class="btn btn-xs btn-danger mt-2 mr-1" id="remove-selected-value-button" value="Remove Selected Values" type="button">
						</div>
					</div>
					<div class="table-responsive">
					   <form name="multiValueUpdate" id="multiValueUpdate" action="multiValueUpdate">
					    <input type="hidden" id="mvUpdateCustomFieldId" name="mvUpdateCustomFieldId" value="${customFieldId!}" />
					    <input type="hidden" id="currentFieldId" name="currentFieldId" value="" />
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
					   </form>
					</div>
					
				</div>
			</div>
		</div>
		
	</div>
	</#if>
</div>	-->
<script>

jQuery(document).ready(function() {  

$('#remove-selected-value-button').on('click', function(){
		
	var rowsSelected = [];
			
	$('input[name="selected-multivalues"]:checked').each(function() {
		//alert(this.value);
   		console.log(this.value);
   		
   		rowsSelected.push(this.value);
   		
	});
		
	var customFieldId = $('#customFieldId').val();
	var groupId = $('#groupId').val();
		
	$.ajax({
		      
		type: "POST",
     	url: "removeSelectedMultiValues",
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
	
	if ($('#fieldValue').val() && $('#description').val()) {
		$.post('createCustomFieldMultiValue', $('#createCustomFieldMultiValueForm').serialize(), function(returnedData) {
	
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

//////////////// Assign role [start] //////////////////////////

$('#roleConfigBtn').click(function () {

	if ($('#roleTypeId').val()) {
		$.post('createRoleConfig', $('#createRoleConfigForm').serialize(), function(returnedData) {
	
			if (returnedData.code == 200) {
				
				showAlert ("success", returnedData.message)
				
				$('#createRoleConfigForm')[0].reset();
				findRoleConfigs();
				
			} else {
				showAlert ("error", returnedData.message)
			}
			
		});
	} else {
		showAlert ("error", "fill up all the values");
	}
	
});

//////////////// Assign role [end] //////////////////////////

$('#multiValue-heading').click(function () {

	findMultiValues();

}); 

$('#multiValue-roleAssign').click(function () {

	findRoleConfigs();

}); 


});

function removeMultiValue (multiValueId) {
	
	var customFieldId = $('#customFieldId').val();

	$.ajax({
			      
		type: "POST",
     	url: "removeCustomFieldMultiValue",
        data:  {"customFieldId": customFieldId, "multiValueId": multiValueId},
        success: function (data) {   
            
            findMultiValues();
			    	
        }
        
	});    
	
}

function removeRoleConfig (roleConfigId) {
	
	//alert(roleConfigId);

	$.ajax({
			      
		type: "POST",
     	url: "removeRoleConfig",
        data:  {"roleConfigId": roleConfigId},
        success: function (data) {   
            
            findRoleConfigs();
			    	
        }
        
	});    
	
}

//findMultiValues();
function findMultiValues() {
	
	var customFieldId = $('#customFieldId').val();
	
   	var url = "getCustomFieldMultiValues?customFieldId="+customFieldId;
   
	$('#multi-value-list').DataTable( {
		    "processing": true,
		    "destroy": true,
		    "ajax": {
	            "url": url,
	            "type": "POST"
	        },
	        "pageLength": 10,
	        //"order": [[ 4, "asc" ]],
	        
	        "columnDefs": [ 
	        	{
					"targets": [4,5],
					"orderable": false
				},
				{ 
                    className: 'multiValueListSize', 
                    'targets': [ 1, 2, 3 ] 
                 }
			],
					      
	        "columns": [
	        	
	            { "data": "fieldValue" },
	             { "data": "description",
		          "render": function(data, type, row, meta){
		            data = '<input type="hidden" class="multiValueId" id="multiValueId" name="multiValueId_'+row.multiValueId+'" value="'+row.multiValueId+'"/>';
		            data = data + '<input type="text" class="form-control input-sm description" name="description_'+row.multiValueId+'" id="description_'+row.multiValueId+'" placeholder="Description" maxlength="255" value="'+row.description+'" autocomplete="off">';
		            return data;
		          }
		        },
		        { "data": "hide",
		          "render": function(data, type, row, meta){
		           data = '<select class="ui dropdown form-control input-sm" data-original-title="Hide" id="hide_'+row.multiValueId+'" name="hide_'+row.multiValueId+'">';
		           var hideValue = row.hide;
                   data = data + '<option value="">Select Hide</option>';
                   if(hideValue != null && hideValue != "" && hideValue == "Y") {
                      data = data + '<option value="Y" selected>Yes</option>';
                   } else {
                      data = data + '<option value="Y">Yes</option>';
                   }
                   if(hideValue != null && hideValue != "" && hideValue == "N") {
                      data = data + '<option value="N" selected>No</option>';
                   } else {
                      data = data + '<option value="N">No</option>';
                   }
                   data = data + '</select>';
		           return data;
		          }
		        },
		        { "data": "sequenceNumber",
		          "render": function(data, type, row, meta){
		            data = '<input type="number" class="form-control input-sm" name="mvSequenceNumber_'+row.multiValueId+'" id="mvSequenceNumber_'+row.multiValueId+'" placeholder="Sequence No" min="1" value="'+row.sequenceNumber+'" autocomplete="off">';
		            return data;
		          }
		        },
	            { "data": "multiValueId",
		          "render": function(data, type, row, meta){
		            if(type === 'display'){
		                data = '<div class="text-center ml-1" ><a class="btn btn-xs btn-primary tooltips" data-original-title="Update" href="javascript:updateMultiValue('+row.multiValueId+')"><i class="fa fa-check-square-o"></i></a> <a class="btn btn-xs btn-danger tooltips remove-role-config" href="javascript:removeMultiValue('+row.multiValueId+')" data-original-title="Remove" data-config-id="'+row.multiValueId+'"><i class="fa fa-times red"></i></a></div>';
		            }
		            return data;
		          }
		         },
		         
		         { "data": "multiValueId",
		          "render": function(data, type, row, meta){
		            if(type === 'display'){
		                data = '<div class="ml-1"><input type="checkbox" name="selected-multivalues" value="' + row.multiValueId + '"></div>';
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

findRoleConfigs();
function findRoleConfigs(){

	var url = "getRoleConfigs?customFieldId=${customField.customFieldId!}";
	$('#role-config-list').DataTable( {
		    "processing": true,
		    "destroy": true,
		    "ajax": {
	            "url": url,
	            "type": "POST"
	        },
	        "pageLength": 10,
	        "order": [[ 1, "asc" ]],
	        "columns": [
	            { "data": "roleType" },
	            { "data": "sequenceNumber" },
	            { "data": "roleTypeId",
		          "render": function(data, type, row, meta){
		            if(type === 'display'){
		                data = '<div class="text-center"><a class="btn btn-xs btn-danger tooltips remove-role-config" href="javascript: removeRoleConfig('+row.roleConfigId+')" data-original-title="Remove" data-config-id="'+row.roleConfigId+'"><i class="fa fa-times red"></i></a></div>';
		            }
		            return data;
		         }
		      	},
	        ],
	        "fnDrawCallback": function( oSettings ) {
	      		resetDefaultEvents();
	    	}
		});
	
	
}

function multiValueUpdate() {
   //jQuerry Ajax Request
   jQuery.ajax({
      url: "multiValueUpdate",
      type: 'POST',
      data: $('#multiValueUpdate').serialize(),
      error: function(msg) {
          showAlert ("error", msg);
      },
      success: function(msg) {
        showAlert ("success", "Updated successfully");
        //findMultiValues();
      }
   });
}

function multiValueUpdateAll(){
   document.multiValueUpdate.currentFieldId.value = "";
   var validateDesc = "Y";
   var multiValueList = [];
   $("#multi-value-list tr .description").each(function() {
      var value = $(this).val();
      if( value == null || value == "" ) {
         validateDesc = "N";
      }
   });
   $("#multi-value-list tr .multiValueId").each(function() {
      multiValueList.push(this.value);
   });
   if(multiValueList != null && multiValueList != "" ) {
      if(validateDesc != null && validateDesc != "" && validateDesc == "Y" ) {
         $("#currentFieldId").val(multiValueList.toString());
         multiValueUpdate();
      } else {
         showAlert ("error", "Description should not be empty");
      }
   } else {
      showAlert ("error", "No data available to update Multi Value");
   }
}

function updateMultiValue(multiValueId) {
   if(multiValueId != null && multiValueId != "" ) {
      var multiValueList = [];
      var desc = $("#description_"+multiValueId).val();
      if(desc != null && desc != "" ) {
         multiValueList.push(multiValueId);
         $("#currentFieldId").val(multiValueList.toString());
         multiValueUpdate();
      } else {
         showAlert ("error", "Description should not be empty");
      }
   }
}
</script>
