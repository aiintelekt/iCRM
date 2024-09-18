<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<div class="row">
   <div id="main" role="main" class="pd-btm-title-bar">
      <#assign extra='<a href="viewSecurityGroup?groupId=${requestParameters.groupId?if_exists}" class="btn btn-xs btn-primary"><i class="fa fa-chevron-circle-left" aria-hidden="true"></i> Back</a>'/>
            <#assign extraLeft=''/>
          
      <div class="col-lg-12 col-md-12 col-sm-12 dash-panel">
       <@sectionFrameHeaderTab title="${uiLabelMap.UpdateSecurityGroup!}" tabId="UpdateSecurityGroup" extra=extra?if_exists/> 
         <form method="post" action="updateSecurityGroupService" id="securityGroupForm" name="securityGroupForm" novalidate="true" data-toggle="validator">
            <div class="row">
               <div class="col-md-12 col-lg-6 col-sm-12 ">
                  <@inputRow
                    id="groupId"
                    label="${uiLabelMap.GroupId}"
                    value="${securityGroup?if_exists.groupId?if_exists}"
                    placeholder="${uiLabelMap.GroupId}"
                    disabled=true
                    />
                  <@dropdownCell
                    label="${uiLabelMap.SecurityType!}"
                    id="securityTypeId"  
                    placeholder="Select security type"      
                    allowEmpty=true        
                    options=securityTypes!
                    value="${securityGroup?if_exists.securityTypeId?if_exists}"
                    required=true
                    allowEmpty=false
                    dataError="Please select security type"
                    />  
                <@inputArea 
                    id="description"
                    label="${uiLabelMap.Description}"
                    value="${securityGroup?if_exists.description?if_exists}"
                    placeholder="${uiLabelMap.Description}"
                    required=true
                    dataError="Please enter description"
                    maxlength=100
                    />
               </div>
            </div>
            <div class="form-group offset-2">
                <div class="text-left ml-1 pad-10">
                    <@submit
                        label="${uiLabelMap.Update}"
                        />
                    <@cancel
                        label="${uiLabelMap.Cancel}"
                        onclick="viewSecurityGroup?groupId=${securityGroup?if_exists.groupId?if_exists}"
                        />
                </div>
               
            </div>
         </form>
         <div class="clearfix"></div>
      </div>
   </div>
</div>
<script>
$("#securityTypeId").change(function() {
     $("#securityTypeId_error").empty();
     if($(this).val() == null || $(this).val() == "") {
        $("#securityTypeId_error").css('display','block');
        $("#securityTypeId_error").append('<ul class="list-unstyled"><li>Please select security type</li></ul>');
     } else {
        $("#securityTypeId_error").css('display','none');
     }
});
</script>