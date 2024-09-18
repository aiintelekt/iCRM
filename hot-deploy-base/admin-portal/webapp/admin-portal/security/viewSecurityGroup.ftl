<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<div class="row">
    <div id="main" role="main">
        <#assign extra='<a href="updateSecurityGroup?groupId=${securityGroup.groupId!}" class="btn btn-xs btn-primary"><i class="fa fa-edit" aria-hidden="true"></i> Update</a><a href="findSecurityGroups" class="btn btn-xs btn-primary"><i class="fa fa-chevron-circle-left" aria-hidden="true"></i> Back</a>'/>
        <#assign extraLeft=''/>
        
        <div class="col-md-12 col-lg-12 col-sm-12 dash-panel">
       <@sectionFrameHeaderTab  title="${uiLabelMap.ViewSecurityGroup}"
            extra=extra?if_exists tabId="ViewSecurityGroup" extraLeft=extraLeft /> 
     
            <div class="col-md-12 col-lg-12 col-sm-12 ">
                <div class="row">
                    <div class="col-md-12 col-lg-6 col-sm-12 ">
                        <@displayCell
                            label="${uiLabelMap.GroupId}"
                            value="${securityGroup.groupId!uiLabelMap.defaultValue}"
                            />
                        <#assign securityTypeGv = (Static["org.ofbiz.entity.util.EntityQuery"].use(delegator).select("description").from("SecurityType").where("securityTypeId",securityGroup.securityTypeId?if_exists).queryFirst())?if_exists />
                        <@displayCell
                            label="${uiLabelMap.SecurityType}"
                            value="${securityTypeGv.description!uiLabelMap.defaultValue}"
                            />
                        <@displayCell
                            label="${uiLabelMap.Description}"
                            value="${securityGroup.description!uiLabelMap.defaultValue}"
                            />
                    </div>
                    <div class="col-md-12 col-lg-6 col-sm-12 "></div>
                </div>
            </div>
            <div class="clearfix"></div>
            <form method="post" action="findSecurityGroups" name="searchForm" id="searchForm" data-toggle="validator">
                <input type="hidden" name="externalLoginKey" value="${requestAttributes.externalLoginKey!}"/>
                <input type="hidden" name="userId" value="${userLogin.userLoginId?if_exists}" />
				<@inputHidden
					id="groupId"
                    value="${securityGroup.groupId!}"
                    />
			</form>
		         
            <form method="post" action="removeSercurityPermission" name="removeSecurityPermissionForm" id="removeSecurityPermissionForm">
                <@inputHidden
                    id="groupId"
                    value="${requestParameters.groupId!}"
                    />
                <@inputHidden
                    id="permissionIds"
                    />
            </form>
			<#assign rightContent = '<a href="" data-toggle="modal" data-target="#addPermission" class="btn btn-primary btn-xs ml-0"> <i class="fa fa-plus fa-1"></i>${uiLabelMap.AddPermissions!}</a>
			<span id="remove-per-btn" title="Remove" class="btn btn-primary btn-xs"><i class="fa fa-times" aria-hidden="true"></i>Remove</span>' />
				
         	<#--
         	<@AgGrid
	            gridheadertitle="${uiLabelMap.SecurityPermissionsforGroupId}: ${securityGroup.groupId!}"
	            gridheaderid=""
	            savePrefBtn=true
	            clearFilterBtn=true
	            exportBtn=true
	            insertBtn=false
	            updateBtn=false
	            removeBtn=false
	            headerextra=rightContent!
	            userid="${userLogin.userLoginId}" 
	            shownotifications="true" 
	            instanceid="SECURITY_GRP_PERM" 
	            autosizeallcol="true"
	            debug="false"
	            serversidepaginate=false
	            />    
            <script type="text/javascript" src="/admin-portal-resource/js/ag-grid/security/view-security-group.js"></script>
            -->
            <@fioGrid
				id="security-group-permissions"
				instanceId="SECURITY_GRP_PERM"
				jsLoc="/admin-portal-resource/js/ag-grid/security/view-security-group.js"
				headerLabel="${uiLabelMap.SecurityPermissionsforGroupId}: ${securityGroup.groupId!}"
				headerExtra=rightContent!
				headerBarClass="grid-header-no-bar"
				headerId="security-group-permissions-tle"
				savePrefBtnId="security-group-permissions-save-pref"
				clearFilterBtnId="security-group-permissions-clear-pref"
				subFltrClearId="security-group-permissions-clear-sub-ftr"
				serversidepaginate=false
				statusBar=false
				exportBtn=true
				exportBtnId="security-group-permissions-list-export-btn"
				savePrefBtn=false
				clearFilterBtn=false
				subFltrClearBtn=false
				/>

        </div>
    </div>
</div>

<div  id="addPermission" class="modal fade bd-example-modal-lg" tabindex="-1" role="dialog" aria-labelledby="addMembersToTeam" aria-hidden="true">
	<div class="modal-dialog modal-lg" style="max-width: 1000px;">
		<div class="modal-content">
            <div class="modal-body" style="padding-bottom: 8px;">
			
				<#assign rightContent = '<input type="button" value="Add" class="btn btn-xs btn-primary" id="add-security-perm-btn" />
                  	           			 <input type="button" value="Close" class="btn btn-xs btn-primary" data-dismiss="modal" /> ' /> 
    		 <div>
		    	<@AgGrid
		    		instanceid="ADD_SECURITY_PERMISSION" 
					gridheadertitle=uiLabelMap.AddSecurityPermissions!
					gridheaderid="grid-action-container"
					refreshPrefBtn=false
					savePrefBtn=false
					clearFilterBtn=false
					exportBtn=false
					insertBtn=false
					updateBtn=false
					removeBtn=false
					refreshPrefBtnId="add-refresh-pref-btn"
                    savePrefBtnId="add-save-pref-btn"
                    clearFilterBtnId="add-clear-filter-btn"
                    exportBtnId="add-main-search-btn"
					headerextra=rightContent!
				    userid="${userLogin.userLoginId}" 
				    shownotifications="true"
				    autosizeallcol="true"
				    debug="false"
				 />    
			    </div>    
				<script type="text/javascript" src="/admin-portal-resource/js/ag-grid/security/add-security-permission.js"></script> 
				
				<form method="post" action="addPermissionToSecurityGroup" id="addPermissionForm" name="addPermissionForm" novalidate="true" data-toggle="validator">
                    <@inputHidden
                        id="groupId"
                        value="${securityGroup.groupId!}"
                        />
                    <@inputHidden
                        id="selectedRows"
                        value=""
                        />
                </form>
      		</div>
    	</div>
  	</div>
</div>

<script>

function removeMainGrid(fag, api) {
	var selectedData = api.getSelectedRows();
	if(selectedData !=null && selectedData != "" && selectedData != 'undefined'){
        $('#removeSecurityPermissionForm input[name=permissionIds]').val(JSON.stringify(selectedData));
        $('#removeSecurityPermissionForm').submit();
    } else{
        showAlert("error","Please select atleast one row");
    }
}

</script>