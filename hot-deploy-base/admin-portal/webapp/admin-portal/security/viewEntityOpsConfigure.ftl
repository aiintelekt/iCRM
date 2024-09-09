<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<div class="row">
    <div id="main" role="main" class="pd-btm-title-bar">
        <#assign extra='<a href="entityOpsConfigure?entityName=${requestParameters.entityName!}" class="btn btn-xs btn-primary"><i class="fa fa-edit" aria-hidden="true"></i> Update</a><a href="entityOpsConfigure" class="btn btn-xs btn-primary"><i class="fa fa-chevron-circle-left" aria-hidden="true"></i> Back</a>'/>
        
        <div class="col-lg-12 col-md-12 col-sm-12 dash-panel">
        <@sectionFrameHeader 
        title="${uiLabelMap.ViewEntityOperations}"
        extra=extra?if_exists
        />
            <form method="post" action="updateEntityOperations" id="entityConfigureForm" name="entityConfigureForm" data-toggle="validator" >
                <div class="row">
                    <div class="col-md-12 col-lg-6 col-sm-12 ">
                        <@displayCell
                            label="${uiLabelMap.Entity!}"
                            value="${entityOpsConfig?if_exists.entityName!uiLabelMap.defaultValue}"
                            />
                        <@displayCell
                            label="${uiLabelMap.Alias!}"
                            value="${entityOpsConfig?if_exists.entityAliasName!uiLabelMap.defaultValue}"
                            />
                        <@displayCell
                            label="${uiLabelMap.EntityType!}"
                            value="${entityTypeList.get(entityOpsConfig?if_exists.entityType!)!uiLabelMap.defaultValue}"
                            />
                        <@displayCell
                            label="${uiLabelMap.RoleType!}"
                            value="${roleTypeList.get(entityOpsConfig?if_exists.roleTypeId!)!uiLabelMap.defaultValue}"
                            />
                            
                        
                        <#assign entityOpsEnum = Static["org.fio.admin.portal.util.EnumUtil"].getEnums(delegator, "ENTITY_OPERATIONS")?if_exists />
                        <#assign entityOpsList = Static["org.fio.admin.portal.util.DataHelper"].getDropDownOptions(entityOpsEnum, "enumId","description")?if_exists />  
                      
                        <@inputCheckBox
                            id="operations"
                            label="${uiLabelMap.Operations}"
                            optionList=entityOpsList!
                            optionValues=entityOpsConfig?if_exists.operations!
                            disabled=true
                            />
                    </div>
                </div>
            </form>
            <div class="clearfix"></div>
		    <form action="" method="post" id="searchForm" name="searchForm">
		    	<@inputHidden 
		    		id="entityName"
		    		value="${requestParameters.entityName!}"
		    		/>
		    	<@inputHidden 
		    		id="roleTypeId"
		    		value="${requestParameters.roleTypeId!}"
		    		/>
		    </form>
            <@AgGrid
				gridheadertitle=uiLabelMap.ListofSecurityPermission!
				gridheaderid=""
				savePrefBtn=true
				clearFilterBtn=true
				exportBtn=true
				insertBtn=false
				updateBtn=false
				removeBtn=false
				
			    userid="${userLogin.userLoginId}" 
			    shownotifications="true" 
			    instanceid="ENTITY_OPS_PERM" 
			    autosizeallcol="true"
			    debug="false"
			    statusBar=true
			    serversidepaginate=false
			    refreshPrefBtnId="refresh-pref-btn"
			    savePrefBtnId="save-pref-btn"
			    clearFilterBtnId="clear-filter-btn"
			    exportBtnId="export-btn"
			    />
			<script type="text/javascript" src="/admin-portal-resource/js/ag-grid/security/entity-ops-permisstion.js"></script>

            
        </div>
    </div>
    <#-- main end -->
</div>
<#-- row end -->