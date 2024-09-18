<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<div class="row">
   <div id="main" role="main" class="pd-btm-title-bar">
    
      <div class="col-lg-12 col-md-12 col-sm-12 dash-panel">
   <#if requestParameters.roleTypeId?exists>
   <#assign extra='<a href="findSecurityRole" class="btn btn-xs btn-primary"><i class="fa fa-chevron-circle-left" aria-hidden="true"></i> Back</a>'/>
      
     <@sectionFrameHeaderTab  title="${uiLabelMap.UpdateSecurityRole!}" extra=extra! tabId="CreateSecurityRole"/> 
       
   <#assign roleType = (Static["org.ofbiz.entity.util.EntityQuery"].use(delegator).select("roleTypeId","description").from("RoleType").where("roleTypeId",requestParameters.roleTypeId!).queryOne())?if_exists />
   <form method="post" action="updateRole" name="createSecurityRoleForm" id="createSecurityRoleForm" data-toggle="validator">
   <#else>
   <#assign extra='<a href="findSecurityRole" class="btn btn-xs btn-primary"><i class="fa fa-chevron-circle-left" aria-hidden="true"></i> Back</a>'/>
     
       <@sectionFrameHeader 
            title="${uiLabelMap.CreateSecurityRole!}"
            extra=extra! + helpUrl?if_exists 
            />
    <form method="post" action="createNewRole" name="createSecurityRoleForm" id="createSecurityRoleForm" data-toggle="validator" onsubmit="javascript:return validateForm();">
   </#if>
         <div class="row">
            <div class="col-md-12 col-lg-6 col-sm-12 ">
            <#if requestParameters.roleTypeId?exists>
               <@inputRow
                    id="roleTypeId"
                    label="${uiLabelMap.Role!}"
                    value=""
                    placeholder="${uiLabelMap.RoleTypeId}"
                    required=true
                    value="${roleType?if_exists.roleTypeId!}"
                    disabled=true
                    />
            <#else>
                <@inputRow
                    id="roleTypeId"
                    label="${uiLabelMap.Role!}"
                    value=""
                    placeholder="${uiLabelMap.RoleTypeId}"
                    required=true
                    value="${roleType?if_exists.roleTypeId!}"
                    maxlength=60
                    onblur="validateForm();"
                    />
            </#if>        
               <@inputArea
                    id="description"
                    label="${uiLabelMap.Description!}"
                    placeholder="${uiLabelMap.Description!}"
                    required=true
                    value="${roleType?if_exists.description!}"
                    maxlength=100
                    />
            </div>
         </div>
         <div class="form-group offset-2">
            <div class="text-left ml-1 pad-10">
            <#if requestParameters.roleTypeId?exists>
               <@submit 
                   label="${uiLabelMap.Update!}"
                   />
               <@cancel
                   label="${uiLabelMap.Cancel!}"
                   onclick="viewSecurityRole?roleTypeId=requestParameters.roleTypeId?exists"
                   />
            <#else>
               <@submit 
                   label="${uiLabelMap.Save!}"
                   />
               <@reset
                   label="${uiLabelMap.Clear!}"
                   />
            </#if> 
            </div>
         </div>
      </div>
    </form>
    </div>
</div>
<script>
function validateForm(){
    var result = validateUniqueId("validateUniqueId","RoleType","roleTypeId","roleTypeId");
    if("Y" == result){
        $('#roleTypeId_error').html("Role id already exists");
        return false;
    } else if("N" == result){
        $('#roleTypeId_error').html("");
    }
    
}
/*
$("input").keypress(function(event) {
    if (event.which == 13) {
        event.preventDefault();
        $("form").submit();
    }
}); */
</script>
