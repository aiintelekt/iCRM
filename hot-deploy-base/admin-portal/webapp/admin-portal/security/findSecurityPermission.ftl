<#include "component://bootstrap/lib/ofbizFormMacros.ftl"/>
<div class="row">
    <div id="main" role="main" class="pd-btm-title-bar">
        <#-- <@sectionFrameHeader title="${uiLabelMap.FindSecurityPermissions!}"  />  -->
        <div class="col-lg-12 col-md-12 col-sm-12 dash-panel">
            <div>
                <#-- <div class="row">
                    <@arrowDownToggle />
                </div>  -->
                <@sectionFrameHeader title="${uiLabelMap.FindSecurityPermissions!}"  />
                <form method="post" name="searchForm" id="searchForm" data-toggle="validator">
                    <input type="hidden" name="externalLoginKey" value="${requestAttributes.externalLoginKey!}"/>
                    <input type="hidden" name="userId" value="${userLogin.userLoginId?if_exists}" />
                    <div class="">
                        <div class="row">
                            <div class="col-lg-6 col-md-6 col-sm-12">
                                <@inputCell 
                                id="permissionId"
                                placeholder="Permission ID"
                                maxlength=60
                                value="${requestParameters.permissionId?if_exists}"
                                />
                            </div>
                            <div class="col-lg-6 col-md-6 col-sm-12">
                                <@button 
                                label="${uiLabelMap.Search}"
                                id="main-search-btn"
                                />
                            </div>
                        </div>
                    </div>
                </form>
            </div>
            <div class="clearfix"></div>
            </div>   
			<div class="clearfix"></div>
			
			<div class="col-lg-12 col-md-12 col-sm-12 dash-panel">
            <#assign rightContent='<a title="Create" href="createSecurityPermission" class="btn btn-primary btn-xs ml-2" target="_blank"> <i class="fa fa-plus" aria-hidden="true"></i> Create </a>' />
            <#--
            <@AgGrid
	            gridheadertitle=uiLabelMap.ListofSecurityPermissions!
	            gridheaderid="security-permission-grid-action-container"
	            insertBtn=false
	            updateBtn=false
	            removeBtn=false
	            headerextra=rightContent!
	            userid="${userLogin.userLoginId}" 
	            shownotifications="true" 
	            instanceid="SECURITY_PERMISSION" 
	            autosizeallcol="false"
	            debug="false"
	            />
	         	
            <script type="text/javascript" src="/admin-portal-resource/js/ag-grid/security/find-security-permission.js"></script>
            -->
            <@fioGrid
				id="security-permission"
				instanceId="SECURITY_PERMISSION"
				jsLoc="/admin-portal-resource/js/ag-grid/security/find-security-permission.js"
				headerLabel=uiLabelMap.ListofSecurityPermissions!
				headerExtra=rightContent!
				headerBarClass="grid-header-no-bar"
				headerId="security-permission-tle"
				savePrefBtnId="security-permission-save-pref"
				clearFilterBtnId="security-permission-clear-pref"
				subFltrClearId="security-permission-clear-sub-ftr"
				serversidepaginate=false
				statusBar=false
				exportBtn=true
				exportBtnId="security-permission-list-export-btn"
				savePrefBtn=false
				clearFilterBtn=false
				subFltrClearBtn=false				
				/>
            </div>
        </div>
    </div>
</div>
<script>  </script>