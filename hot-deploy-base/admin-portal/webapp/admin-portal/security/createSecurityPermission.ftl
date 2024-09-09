<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<div class="row" id="create-sec-perm">
    <div id="main" role="main" class="pd-btm-title-bar">  
        <div class="col-lg-12 col-md-12 col-sm-12 dash-panel">
        <@sectionFrameHeader title="${uiLabelMap.CreateSecurityPermissions!}"  />
            <form method="post" action="createSecurityPermissionService" id="securityPermissionForm" name="securityPermissionForm" data-toggle="validator">
                <div class="row">
                    <div class="col-md-12 col-lg-6 col-sm-12 ">
                        <@inputRow
                            label="${uiLabelMap.PermissionId}"
                            id="permissionId"
                            placeholder="Permission ID"
                            value="${requestParameters.permissionId!}"
                            required=true
                            />
                        <@inputArea
                            label="${uiLabelMap.Description}"
                            id="description"
                            placeholder="Description"
                            value="${requestParameters.description!}"
                            maxlength=60
                            required=true
                            dataError = "Please enter description"
                            />
                    </div>
                </div>
                <div class="form-group offset-2">
                    <div class="text-left ml-1 pad-10">
                        <@submit
                            label="${uiLabelMap.Save!}"
                            />
                        <@reset
		                   label="${uiLabelMap.Clear!}"
		                   />
                    </div>
                </div>
            </form>
            <div class="clearfix"></div>
        </div>
    </div>
</div>