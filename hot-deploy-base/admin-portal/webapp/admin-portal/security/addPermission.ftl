<#include "component://ab-ag-grid-support/webapp/ab-ag-grid-support/lib/ag_grid_header.ftl"/>
<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<div class="row">
   <div id="main" role="main">
      <#assign extra='<a href="viewSecurityGroup?groupId=${requestParameters.groupId?if_exists}" class="btn btn-xs btn-primary">
      <i class="fa fa-chevron-circle-left" aria-hidden="true"></i> Back</a>'/>
      <@sectionFrameHeader
      title="${uiLabelMap.AddSecurityPermissions!}"
      extra=extra?if_exists
      />
      <div class="alert alert-danger" role="alert" id="modal-error" style="display:none;">
         <strong>Error : </strong> Please select atleast one security group
      </div>
      
      <div class="page-header border-b pt-2">
        <div class="float-right" id="main-grid-action-container">
        </div>
        <div class="clearfix"></div>
    </div>
      
      <@AgGrid
		userid="${userLogin.userLoginId}" 
		instanceid="SGRP02" 
		autosizeallcol="true" 
		gridoptions='{"pagination": true, "paginationPageSize": 10 }'
		requestbody='{"groupId":"${securityGroup.groupId!uiLabelMap.defaultValue}"}'
		insertBtn=false
    	removeBtn=false
    	updateBtn=false
    	serversidepaginate=false
		/>
      
       <script type="text/javascript" src="/admin-portal-resource/js/ag-grid/security/add-security-permission.js"></script>
        	<@inputHidden id="groupId" name="groupId" value="${securityGroup.groupId!uiLabelMap.defaultValue}"/>
      
      <#-- 
      <div class="table-responsive">
         <div class="loader text-center" id="loader" sytle="display:none;">
            <span></span>
            <span></span>
            <span></span>
         </div>
         <div id="grid" style="width: 100%;" class="ag-theme-balham"></div>
         <script type="text/javascript" src="/admin-portal-resource/js/ag-grid/security/add-security-permission.js"></script>
        	<@inputHidden id="groupId" name="groupId" value="${securityGroup.groupId!uiLabelMap.defaultValue}"/>
               
      </div>
       -->
       
      <form method="post" action="addPermissionToSecurityGroup" id="addPermissionForm" name="addPermissionForm" novalidate="true" data-toggle="validator">
                    <@inputHidden
                        id="modalId"
                        value="addPermission"
                        />
                    <@inputHidden
                        id="groupId"
                        value="${securityGroup.groupId!}"
                        />
                    <@inputHidden
                        id="permissionId"
                        value=""
                        />
                    <@inputHidden
                        id="permissionIds"
                        value=""
                        />
                </form>
   </div>
</div>
</div>