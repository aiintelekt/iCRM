<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<div class="row">
    <div id="main" role="main"> 
        <div class="col-lg-12 col-md-12 col-sm-12 dash-panel">
            <@sectionFrameHeader title="${uiLabelMap.RoleAndSecurityAssociation!}" />
            <form method="post" action="createRoleSecurityAssocService" id="createRoleSecurityAssocForm" name="createRoleSecurityAssocForm" data-toggle="validator" >
                <div class="row">
                    <div class="col-md-12 col-lg-6 col-sm-12 ">
                        <@dropdownCell
	                        label="${uiLabelMap.Roles!}"
	                        id="roleTypeId"  
	                        placeholder="Select role"
	                        options=roleList!
	                        value="${requestParameters.roleTypeId!''}"
	                        required=true
	                        dataError="Please select role"
	                        />
                        <@dropdownCell
	                        label="${uiLabelMap.SecurityGroup!}"
	                        id="groupId"  
	                        placeholder="Select security group"
	                        options=securityGroupList!
	                        value="${requestParameters.groupId!''}"
	                        required=true
	                        dataError="Please select security group"
	                        isMultiple="Y"
	                        />
                    </div>
                </div>
                <div class="form-group offset-2">
                    <div class="text-left ml-1">
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
            <div>
                <@AgGrid
	                gridheadertitle=uiLabelMap.List
	                gridheaderid="role-security-assoc"
	                insertBtn=false
	                updateBtn=false
	                userid="${userLogin.userLoginId}" 
	                shownotifications="true" 
	                instanceid="ROLE_SECURITY_ASSOC" 
	                autosizeallcol="true"
	                debug="false"
	                />			
                <script type="text/javascript" src="/admin-portal-resource/js/ag-grid/security/security-role-assoc.js"></script>												
            </div>
        </div>
    </div>
</div>
<script>
    /*
    $("input").keypress(function(event) {
        if (event.which == 13) {
            event.preventDefault();
            $("form").submit();
        }
    }); */       
</script>