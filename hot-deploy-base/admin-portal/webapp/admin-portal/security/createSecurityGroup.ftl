<#include "component://bootstrap/lib/ofbizFormMacros.ftl"/>
<div class="row" id="create-sec-group">
    <div id="main" role="main" class="pd-btm-title-bar">
            <#assign extra='<a href="findSecurityGroups" class="btn btn-xs btn-primary"><i class="fa fa-chevron-circle-left" aria-hidden="true"></i> Back</a>'/>
            <#assign extraLeft=''/>

        <div class="col-lg-12 col-md-12 col-sm-12 dash-panel">
            <@sectionFrameHeaderTab title="${uiLabelMap.CreateSecurityGroup!}" tabId="CreateSecurityGroup" extra=extra?if_exists/> 
            <form method="post" action="createSecurityGroupServive" id="securityGroupForm" name="securityGroupForm" data-toggle="validator" onsubmit="javascript:return validateForm();">
                <div class="row">
                    <div class="col-md-12 col-lg-6 col-sm-12 ">
                        <@inputRow
                            id="groupId"
                            label="${uiLabelMap.GroupId}"
                            value="${requestParameters.groupId?if_exists}"
                            placeholder="${uiLabelMap.GroupId}"
                            required=true
                            dataError="Please enter group id"
                            maxlength=60
                            onblur="validateForm();"
                            />
                        <@dropdownCell
                            label="${uiLabelMap.SecurityType!}"
                            id="securityTypeId"  
                            placeholder="Select security type"
                            options=securityTypes!
                            value="${requestParameters.securityTypeId!'OPS_SECURITY'}"
                            required=true
                            dataError="Please select security type"
                            />  
                        <@inputArea 
                            id="description"
                            label="${uiLabelMap.Description}"
                            value="${requestParameters.description?if_exists}"
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
                            label="${uiLabelMap.Save}"
                            />
                        <@reset
                            label="${uiLabelMap.Clear}"
                            />
                    </div>
                </div>
            </form>
            <div class="clearfix"></div>
        </div>
    </div>
</div>
<script>
function validateForm(){
    $('#groupId_error').html("");
    var result = validateUniqueId("validateUniqueId","SecurityGroup","groupId","groupId");
    if("Y" == result){
        $('#groupId_error').html("Security Group Id already exists");
        return false;
    } else if("N" == result){
        $('#groupId_error').html("");
    } 
    var groupId =$("#groupId").val();
    if(groupId == null || groupId == "" || groupId =='undefined') {
        $('#groupId_error').html("Please enter group id");
    }
}
$("#securityTypeId").change(function() {
     $("#securityTypeId_error").empty();
     if($(this).val() == null || $(this).val() == "") {
        $("#securityTypeId_error").css('display','block');
        $("#securityTypeId_error").append('<ul class="list-unstyled"><li>Please select security type</li></ul>');
     } else {
        $("#securityTypeId_error").css('display','none');
     }
}); 
/*
$("input").keypress(function(event) {
    if (event.which == 13) {
        event.preventDefault();
        $("form").submit();
    }
}); */       
</script>