<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<script type="text/javascript" language="javascript" src="/bootstrap/js/jquery.dataTables.min.js"></script>
<div class="row">
    <div id="main" role="main">
    <div class="col-lg-12 col-md-12 col-sm-12 dash-panel">
	<@sectionFrameHeaderTab title="${uiLabelMap.Update} ${uiLabelMap.CustomFieldGroup}" tabId="EditCustomFieldGroup"/> 

	<div class="row p-2">	
	
	<div class="col-md-6 col-sm-6" style="padding:0px">		
		<div class="portlet-body form">
			<form id="mainFrom" role="form" class="form-horizontal" action="<@ofbizUrl>updateCustomFieldGroup</@ofbizUrl>" encType="multipart/form-data" method="post" data-toggle="validator">
																					
			<div class="form-body">
			
			<@inputRow 
				id="groupId"
				label=uiLabelMap.groupId
				value=customFieldGroup.groupId
				readonly=true
				/>
			
			<@inputRow 
				id="groupName"
				label=uiLabelMap.groupName
				placeholder=uiLabelMap.groupName
				value=customFieldGroup.groupName
				required=true
				maxlength=255
				dataError="Please Enter Group Name"
				/>
				
			<@dropdownCell 
				id="groupingCode"
				label=uiLabelMap.groupingCode
				options=groupingCodeList
				required=false
				value=customFieldGroup.groupingCode
				allowEmpty=true				
				dataLiveSearch=true
				required=true
				placeholder=uiLabelMap.pleaseSelect
				isMultiple="Y"
				/>	
			
			<@inputRow 
				id="sequence"
				label=uiLabelMap.sequence
				placeholder=uiLabelMap.sequence
				value=customFieldGroup.sequence
				type="number"
				required=false
				min=1
				/>	
				
			<@dropdownCell 
				id="hide"
				label=uiLabelMap.hide
				options=yesNoOptions
				required=false
				value=customFieldGroup.hide
				allowEmpty=true
				/>		
																																																																																																																																																																																																																																																					
			</div>
			
			<div class="form-group row" style="margin-left: 135px;">
                    <div class="offset-sm-2 col-sm-9">
                       <@submit label=uiLabelMap.submit/>
                    	<@cancel label=uiLabelMap.cancel/>
                    </div>
                </div>
			
		</form>			
							
		</div>
		</div>	
	</div>
	
	<div class="col-md-6 col-sm-6" style="padding:0px">
	
		<@pageSectionHeader title="${uiLabelMap.AssignRole!}"/>		
		<div class="portlet-body form">
			<form id="createRoleConfigForm" role="form" class="form-horizontal" action="<@ofbizUrl>createRoleConfig</@ofbizUrl>" encType="multipart/form-data" method="post" data-toggle="validator">
			
			<input type="hidden" name="groupId" value="${customFieldGroup.groupId!}" />
			<input type="hidden" name="isCompleteReset" value="N" />
						
			<div class="form-body">
			
			<@dropdownCell 
				id="roleTypeId"
				label=uiLabelMap.roleTypeId
				options=roleTypeList
				required=true
				value=roleConfig?if_exists
				allowEmpty=true
				dataLiveSearch=true		
				dataError="Please Select Role Type"
				placeholder=uiLabelMap.pleaseSelect
				/>	
				
			</div>
			
			<div class="clearfix"> </div>
			<div class="col-md-12 col-sm-12">
				<div class="form-group row">
					<div class="offset-sm-4 col-sm-1">
						<@button label="Assign" id="roleConfigBtn" onclick="javascript:return onSubmitRoleValidation(this);"/>
					</div>
				</div>
			</div>
			
			</form>			
							
		</div>	
		
		<div class="clearfix"></div>
		<h2 class="float-left">${uiLabelMap.List} ${uiLabelMap.Role}</h2>
		<div class="clearfix"></div>
		<div class="">
		</div>
		<div class="table-responsive">
			<table id="role-config-list" class="table table-striped">
				<thead>
					<tr>
						<th>${uiLabelMap.roleTypeId!}</th>
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
</div>

<script>

jQuery(document).ready(function() {

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

findRoleConfigs(); 

});

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

function findRoleConfigs(){

$('#role-config-list').DataTable( {
    "processing": true,
    "destroy": true,
    "ajax": {
        "url": "/custom-field/control/getRoleConfigs?groupId=${customFieldGroup.groupId!}",
        "type": "POST"
    },
    "pageLength": 10,
    "order": [[ 1, "asc" ]],
    "columns": [
        { "data": "roleType" },
        //{ "data": "sequenceNumber" },
        { "data": "roleTypeId",
          "render": function(data, type, row, meta){
            if(type === 'display'){
                data = 
                '<div class="text-center">' +
                	'<a class="btn btn-xs btn-danger tooltips remove-role-config" href="javascript:removeRoleConfig('+row.roleConfigId+')" data-original-title="Remove" data-config-id="'+row.roleConfigId+'"><i class="fa fa-times red"></i></a>' +
                '</div>';
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

</script>
