<#include "component://homeapps/webapp/homeapps/lib/ofbizFormMacros_dbs.ftl"/>
<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>

<#-- <#assign isValidAction = Static["org.fio.homeapps.util.UtilUserAudit"].isValidAction(delegator, userLogin, "", "", activeApp, pageId)> -->
<#assign isValidAction = true/>
<div class="row">
    <div id="main" role="main">
        <div class="col-lg-12 col-md-12 col-sm-12 dash-panel">
            <@sectionFrameHeader title="${uiLabelMap.profileSecurityConfiguration!}" />
            <form id="createProfileSecurityConfig" role="form" method="post" data-toggle="validator">
                <div class="row pb-2">
                    <div class="col-md-4 col-lg-4 col-sm-12">
                        <@simpleDropdownInput id="screenProfile" options=screenProfileList required=false value=profileSecurityConfig.screenProfile allowEmpty=true />
                    </div>
                    <div class="col-md-4 col-lg-4 col-sm-12">
                        <@simpleDropdownInput id="securityGroupId" options=securityGroupList required=false value=profileSecurityConfig.type allowEmpty=true />
                    </div>
                    <div class="col-md-4 col-lg-4 col-sm-12">
                        <button type="button" class="btn btn-sm btn-primary" id="load-profilesSecurityConfig-button">Load</button>
                    </div>
                </div>
            </form>
        </div>
        <div class="clearfix"> </div>
        <div class="col-md-12 col-lg-12 col-sm-12 dash-panel">
            <div class="page-header">
                <h2 class="float-left">Configuration</h2>
            </div>
            <div class="table-responsive">
                <table class="table table-hover" id="list_profileSecurityConfig">
                    <thead>
                        <tr>
                            <th></th>
                            <th>${uiLabelMap.screenProfiles!}</th>
                            <th>${uiLabelMap.securityGroup!}</th>
                            <th>Create<div class="form-check-inline"> <label> <input type="checkbox" value="createPermissionChecked" class="selectAll"> </label></div>
                            </th>
                            <th>View<div class="form-check-inline"> <label> <input type="checkbox" value="viewPermissionChecked" class="selectAll"> </label></div>
                            </th>
                            <th>Edit<div class="form-check-inline"> <label> <input type="checkbox" value="editPermissionChecked" class="selectAll"> </label></div>
                            </th>
                            <th>M/C<div class="form-check-inline"> <label> <input type="checkbox" value="auditPermissionChecked" class="selectAll"> </label></div>
                            </th>
                        </tr>
                    </thead>
                    <tbody>
                    </tbody>
                </table>
            </div>
            <div class="clearfix"> </div>
            <div class="form-group row">
                <div class="col-md-12 col-sm-12">
                    <div class="text-right">
                        <#if isValidAction>
                            <button type="submit" class="btn btn-sm btn-primary">Save</button>
                        </#if>
                    </div>
                    <div class="clearfix"> </div>
                </div>
            </div>
        </div>
    </div>
</div>
<script type="text/javascript">

jQuery(document).ready(function() {	
	
<#if !isValidAction>
   $("#createProfileSecurityConfig").submit(false);
</#if>
$('#load-profilesSecurityConfig-button').on('click', function(){
	loadProfileSecurityConfig();
});

$('#createProfileSecurityConfig').validator().on('submit', function (e) {
    
    e.preventDefault();
	var screenProfile = $("#screenProfile").val();
	var securityGroupId = $("#securityGroupId").val();
	if((screenProfile == null || screenProfile == "" || screenProfile == undefined) && (securityGroupId == null || securityGroupId == "" || securityGroupId == undefined)) {
		showAlert ("error", "Select Screen Profiles or Screen Profiles fields");
		return;
	}
	
  	$.post('updateUserAuditConfiguration', $('#createProfileSecurityConfig').serialize(), function(returnedData) {
	
		if (returnedData.code == 200) {
			showAlert ("success", returnedData.message);
		} else {
			showAlert ("error", returnedData.message);
		}
	});
	// everything looks good!

});
		
$(".selectAll").change(function(){  
      var status = this.checked;
      var value = this.value;
      if (status) {
         $('.'+value).each(function(){ 
            this.checked = true; 
         });
      } else {
         $('.'+value).each(function(){ 
            this.checked = false;
         });
      }
 });	
     
loadProfileSecurityConfig();
function loadProfileSecurityConfig() {
	
	var screenProfile = $("#screenProfile").val();
	var securityGroupId = $("#securityGroupId").val();
	   	
   	var url = "getUserAuditConfigurations";
   
	$('#list_profileSecurityConfig').DataTable({
	    "processing": true,
	    "destroy": true,
	    "ordering": false,
	    "searching": false,
	    "paging": false,
	    "info": false,
	    "ajax": {
            "url": url,
            "type": "POST",
            "data": {
            	"screenProfile": screenProfile,
            	"securityGroupId": securityGroupId
            }
        },
        "columns": [
        	{ "data": "permissionId",
	          "render": function(data, type, row, meta){
	           data = '<input type="hidden" id="permissionId" name="permissionId" value="'+row.permissionId+'"/>';
               data = data+'<input type="hidden" id="groupId" name="groupId" value="'+row.groupId+'"/>';
	           data = data+'<td><div class="checkbox"> <label><input type="checkbox" value="'+row.id+'" onchange="javascript:selectCheckBox(this);"/></label> </div> </td>';
	           return data;
	         }
	      	},
            { "data": "description" },
            { "data": "groupId" },
            { "data": "createPermissionId",
	          "render": function(data, type, row, meta){
	           var createPermissionId = row.createPermissionId;
	           var createPermissionChecked = row.createPermissionChecked;
               if (createPermissionId != null && createPermissionId != "" && createPermissionId != undefined) {
	           	  data = '<td><div class="checkbox"> <label>';
	           	  if(createPermissionChecked != null && createPermissionChecked != "" && createPermissionChecked != undefined && createPermissionChecked == "Y") {
	           	    data = data + '<input type="checkbox" class="createPermissionChecked" id="createPermissionChecked_'+row.id+'" name="createPermissionChecked_'+row.id+'" value="Y" checked="checked">';
	           	  } else {
	              	data = data + '<input type="checkbox" class="createPermissionChecked" id="createPermissionChecked_'+row.id+'" name="createPermissionChecked_'+row.id+'" value="Y">';
	              }
	              data = data + '<input type="hidden" id="createPermissionId_'+row.id+'" name="createPermissionId_'+row.id+'" value="'+createPermissionId+'">';
	              data = data + '</label> </div> </td>';
	           }
	           return data;
	         }
	      	},
	      	{ "data": "viewPermissionId",
	          "render": function(data, type, row, meta){
	           var viewPermissionId = row.viewPermissionId;
	           var viewPermissionChecked = row.viewPermissionChecked;
               if (viewPermissionId != null && viewPermissionId != "" && viewPermissionId != undefined) {
	              data = '<td><div class="checkbox"> <label>';
	              if(viewPermissionChecked != null && viewPermissionChecked != "" && viewPermissionChecked != undefined && viewPermissionChecked == "Y") {
	           	    data = data + '<input type="checkbox" class="viewPermissionChecked" id="viewPermissionChecked_'+row.id+'" name="viewPermissionChecked_'+row.id+'" value="Y" checked="checked">';
	           	  } else {
	              	data = data + '<input type="checkbox" class="viewPermissionChecked" id="viewPermissionChecked_'+row.id+'" name="viewPermissionChecked_'+row.id+'" value="Y">';
	              }
	              data = data + '<input type="hidden" id="viewPermissionId_'+row.id+'" name="viewPermissionId_'+row.id+'" value="'+viewPermissionId+'">';
	              data = data + '</label> </div> </td>';
	           }
	           return data;
	         }
	      	},
	      	{ "data": "editPermissionId",
	          "render": function(data, type, row, meta){
	           var editPermissionId = row.editPermissionId;
	           var editPermissionChecked = row.editPermissionChecked;
               if (editPermissionId != null && editPermissionId != "" && editPermissionId != undefined) {
	           	  data = '<td><div class="checkbox"> <label>';
	           	  if(editPermissionChecked != null && editPermissionChecked != "" && editPermissionChecked != undefined && editPermissionChecked == "Y") {
	           	    data = data + '<input type="checkbox" class="editPermissionChecked" id="editPermissionChecked_'+row.id+'" name="editPermissionChecked_'+row.id+'" value="Y" checked="checked">';
	           	  } else {
	              	data = data + '<input type="checkbox" class="editPermissionChecked" id="editPermissionChecked_'+row.id+'" name="editPermissionChecked_'+row.id+'" value="Y">';
	              }
	              
	              data = data + '<input type="hidden" id="editPermissionId_'+row.id+'" name="editPermissionId_'+row.id+'" value="'+editPermissionId+'">';
	              data = data + '</label> </div> </td>';
	           }
	           return data;
	         }
	      	},
            { "data": "auditPermissionId",
	          "render": function(data, type, row, meta){
	           var auditPermissionId = row.auditPermissionId;
	           var auditPermissionChecked = row.auditPermissionChecked;
               if (auditPermissionId != null && auditPermissionId != "" && auditPermissionId != undefined) {
	           	  data = '<td><div class="checkbox"> <label>';
	           	  if(auditPermissionChecked != null && auditPermissionChecked != "" && auditPermissionChecked != undefined && auditPermissionChecked == "Y") {
	           	    data = data + '<input type="checkbox" class="auditPermissionChecked" id="auditPermissionChecked_'+row.id+'" name="auditPermissionChecked_'+row.id+'" value="Y" checked="checked">';
	           	  } else {
	              	data = data + '<input type="checkbox" class="auditPermissionChecked" id="auditPermissionChecked_'+row.id+'" name="auditPermissionChecked_'+row.id+'" value="Y">';
	              }
	              data = data + '<input type="hidden" id="auditPermissionId_'+row.id+'" name="auditPermissionId_'+row.id+'" value="'+auditPermissionId+'">';
	              data = data + '</label> </div> </td>';
	           }
	           return data;
	         }
	      	},
        ]
	});
}     
     
});

function selectCheckBox(row) {
	var status = row.checked;
    var value = row.value;
    if (status) {
       $('#createPermissionChecked_'+value).prop("checked", true); 
       $('#viewPermissionChecked_'+value).prop("checked", true); 
       $('#editPermissionChecked_'+value).prop("checked", true); 
       $('#auditPermissionChecked_'+value).prop("checked", true); 
    } else {
       $('#createPermissionChecked_'+value).prop("checked", false); 
       $('#viewPermissionChecked_'+value).prop("checked", false); 
       $('#editPermissionChecked_'+value).prop("checked", false); 
       $('#auditPermissionChecked_'+value).prop("checked", false); 
    }
}
</script>