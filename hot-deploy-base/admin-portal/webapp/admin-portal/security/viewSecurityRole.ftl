<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<div class="row">
   <div id="main" role="main">
      <#assign extra='<a href="updateSecurityRole?roleTypeId=${requestParameters.roleTypeId!}" class="btn btn-xs btn-primary"><i class="fa fa-edit" aria-hidden="true"></i> Update</a> 
      <a href="findSecurityRole" class="btn btn-xs btn-primary"><i class="fa fa-chevron-circle-left" aria-hidden="true"></i> Back</a>'/>
      <@sectionFrameHeader 
        title="${uiLabelMap.ViewSecurityRole!}"
        extra=extra?if_exists
        />
      <#assign roleType = (Static["org.ofbiz.entity.util.EntityQuery"].use(delegator).select("roleTypeId","description").from("RoleType").where("roleTypeId",requestParameters.roleTypeId!).queryOne())?if_exists />
      <div class="col-md-12 col-lg-12 col-sm-12 ">
         <div class="row">
            <div class="col-md-12 col-lg-6 col-sm-12 ">
                <@displayCell
                    label="${uiLabelMap.Role}"
                    value="${roleType.roleTypeId!uiLabelMap.defaultValue}"
                    />
                <@displayCell
                    label="${uiLabelMap.Description}"
                    value="${roleType.description!uiLabelMap.defaultValue}"
                    />
            </div>
         </div>
         
         <div class="clearfix"></div>
         <div class="page-header border-b pt-2">
            <h2 class="float-left">${uiLabelMap.ListofSecurityGroups}: ${roleType?if_exists.description!}</h2>
             <a href="addSecurityGroup?roleTypeId=${requestParameters.roleTypeId!}" class="btn btn-primary btn-xs ml-2"> <i class="fa fa-plus fa-1"></i>${uiLabelMap.Add}</a> 
             <a title="Remove" href="#" class="btn btn-primary btn-xs" onclick="removeSubmit()"><i class="fa fa-times" aria-hidden="true"></i> Remove </a>  
            <div class="clearfix"></div>
         </div>
         <div class="clearfix"></div>
         <div class="table-responsive">
         <div class="loader text-center" id="loader" sytle="display:none;">
                  <span></span>
                  <span></span>
                  <span></span>
                </div>
               <div id="grid" style="width: 100%;" class="ag-theme-balham"></div>
               
                <@inputHidden id="roleTypeId" name="roleTypeId" value="${requestParameters.roleTypeId!}"/>
               
            <#-- <@AgGrid
                userid="${userLogin.userLoginId}" 
                instanceid="SR03" 
                styledimensions='{"width":"100%","height":"auto"}'
                autosave="false"
                autosizeallcol="true" 
                debug="true"
                requestbody='{"roleTypeId":"${requestParameters.roleTypeId!}"}'
                buttonbarbuttons='[{"label":"Remove", "clickEventId":"remove-selected", "styleClass":"btn btn-primary btn-xs ml-2"}]'
                /> -->
                <script type="text/javascript" src="/admin-portal-resource/js/ag-grid/security/view-security-role.js"></script>
         </div>
         <div class="clearfix"></div>
         
         <div class="page-header border-b pt-2">
            <h2>${uiLabelMap.AuditHistory!}</h2> 
            <div class="clearfix"></div>
         </div>
         <div class="clearfix"></div>
         <#--
         <div class="table-responsive">
            <@AgGrid
                userid="${userLogin.userLoginId}" 
                instanceid="SGRP04" 
                styledimensions='{"width":"100%","height":"auto"}'
                autosave="false"
                autosizeallcol="true" 
                debug="true"
                />
         </div>
         <div class="clearfix"></div>
         -->
      </div>
   </div>
</div>
<form method="post" action="removeRoleSecurityAssoc" name="removeSecurityForm" id="removeSecurityForm">
    <@inputHidden
        id="roleTypeId"
        value="${requestParameters.roleTypeId!}"
        />
    <@inputHidden
        id="groupIds"
        />
</form>
<#-- <script type="text/javascript" src="/bootstrap/js/fio-ag-grid.js"></script>
<script type="text/javascript">
 var gridInstance1 = document.getElementById('SR03');
 gridInstance1.addEventListener("buttonBarClickEvent", function(evt){
    switch(evt.detail.clickEventId){
        case "remove-selected":
            var selectedRows = fag["SR03"].getInstanceApi().getSelectedRows();
            if(selectedRows !=null && selectedRows != "" && selectedRows != 'undefined'){
                $('#removeSecurityForm input[name=groupIds]').val(JSON.stringify(selectedRows));
                $('#removeSecurityForm').submit();
            } else{
                showAlert("error","Please select atleast one row");
            }
            break;
    }
 });
</script> -->
<script type="text/javascript">
 var gridInstance1 = document.getElementById('SR04');
 gridInstance1.addEventListener("buttonBarClickEvent", function(evt){
    console.log("[gridInstance1] button bar click ----> ", evt.detail);
    switch(evt.detail.clickEventId){
        case "add-selected":
            var selectedRows = fag["SR04"].getInstanceApi().getSelectedRows();
            if(selectedRows !=null && selectedRows != "" && selectedRows != 'undefined'){
                $('#modal-error').hide();
                $('#addSecurityGroupForm input[name=groupIds]').val(JSON.stringify(selectedRows));
                console.log("groupIds--->"+JSON.stringify(selectedRows));    
                $('#addSecurityGroupForm').submit();
            } else{
                $('#modal-error').show();
            }
            break;
        case "clear-selected":
            fag["SR04"].getInstanceApi().deselectAll();
            break;
    }
 });
</script>
