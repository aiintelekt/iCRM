<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<div class="row" id="update-sec-perm">
    <div id="main" role="main" class="pd-btm-title-bar">
        <#assign extra ='<a href="findSecurityPermissions" class="btn btn-xs btn-primary"><i class="fa fa-chevron-circle-left" aria-hidden="true"></i> Back</a>' />

        <div class="col-lg-12 col-md-12 col-sm-12 dash-panel">
        <@sectionFrameHeaderTab title="${uiLabelMap.UpdateSecurityPermissions!}" tabId="UpdateSecurityPermission"  extra=extra/> 
            <form method="post" action="updateSecurityPermissionService" id="securityPermissionForm" name="securityPermissionForm" data-toggle="validator">
                <div class="row">
                    <div class="col-md-12 col-lg-6 col-sm-12 ">
                        <@inputRow
                            label="${uiLabelMap.PermissionId}"
                            id="permissionId"
                            placeholder="Permission ID"
                            value="${securityPermission.permissionId!uiLabelMap.defaultValue}"
                            disabled=true
                            required=true
                            />
                        <@inputArea
                            label="${uiLabelMap.Description}"
                            id="description"
                            placeholder="Description"
                            value="${securityPermission.description!uiLabelMap.defaultValue}"
                            maxlength=60
                            required=true
                            dataError = "Please enter description"
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
                            onclick="findSecurityPermissions"
                            />
                    </div>
                </div>
            </form>
            <div class="clearfix"></div>
            <#-- 
            <div class="page-header border-b pt-4">
                <h2 class="float-left">Audit History</h2>
                <a id="export_to_excel_icon" title="Export to Excel" href="#" class="btn btn-primary btn-xs ml-2" onclick="onBtExport()"><i class="fa fa-file-excel-o" aria-hidden="true"></i> Export</a>   
                <a id="export_to_excel_icon" title="Save Preference" href="#" class="btn btn-primary btn-xs"><i class="fa fa-save " aria-hidden="true"></i> Save Preference</a> 
                <div class="clearfix"></div>
            </div>
            <div class="table-responsive">
                <div id="cbggridaudit" style="height: 300px; width: 100%;" class="ag-theme-balham"></div>
            </div>
            <div class="clearfix"></div>
            -->
        </div>
    </div>
</div>