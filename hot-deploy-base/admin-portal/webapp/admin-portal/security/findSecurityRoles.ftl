<#include "component://bootstrap/lib/ofbizFormMacros.ftl"/>
<div class="row">
   <div id="main" role="main" class="pd-btm-title-bar">
      
      <div class="col-lg-12 col-md-12 col-sm-12 dash-panel">
         <div>
            <#-- <div class="row">
                <@arrowDownToggle />
            </div>  -->
            <div>
               <div>
               	<@sectionFrameHeader 
            		title="${uiLabelMap.FindSecurityRoles}" 
            	/>
                <form method="post" action="findSecurityRole" name="searchForm" id="searchForm" data-toggle="validator">
                  <input type="hidden" name="externalLoginKey" value="${requestAttributes.externalLoginKey!}"/>
                  <input type="hidden" name="userId" value="${userLogin.userLoginId?if_exists}" />
                  <div class="">
                     <div class="row">
                        <div class="col-lg-4 col-md-4 col-sm-12">
                        <#assign roleList = (Static["org.ofbiz.entity.util.EntityQuery"].use(delegator).select("roleTypeId","description").from("RoleType").where("parentTypeId","DBS_ROLE").queryList())?if_exists />
                        <#assign roleMap = Static["org.fio.admin.portal.util.DataHelper"].getDropDownOptions(roleList, "roleTypeId","description")?if_exists /> 
                     <@dropdownCell 
                              id="role"
                              placeholder="${uiLabelMap.SelectRole}"
                              options=roles!
                              allowEmpty=true
                              value="${requestParameters.role!}"
                              dataError="Please select role"
                              /> 
                         
                        </div>
                        <div class="col-lg-4 col-md-4 col-sm-12">
                            <@button 
                                label="${uiLabelMap.Search}"
                                id="main-search-btn"
                                />
                        </div>
                     </div>
                  </div>
                </form>
               </div>
            </div>
         </div>
         </div>   
			<div class="clearfix"></div>
			
			<div class="col-lg-12 col-md-12 col-sm-12 dash-panel">
			<#assign rightContent='<a title="Create" href="/admin-portal/control/createSecurityRole" class="btn btn-primary btn-xs ml-2" target="_blank"> <i class="fa fa-plus" aria-hidden="true"></i> Create </a>' />   
						 		
			<#--
			<@AgGrid
			gridheadertitle=uiLabelMap.ListofSecurityRoles
			gridheaderid="security-role-grid-action-container"
			savePrefBtn=true
			clearFilterBtn=true
			exportBtn=true
			insertBtn=false
			updateBtn=false
			removeBtn=false
			headerextra=rightContent
			
		    userid="${userLogin.userLoginId}" 
		    shownotifications="true" 
		    instanceid="SECURITY_ROLE" 
		    autosizeallcol="true"
		    debug="false"
		    />			
			
			<script type="text/javascript" src="/admin-portal-resource/js/ag-grid/security/find-security-role.js"></script>-->
			<@fioGrid
				id="security-role"
				instanceId="SECURITY_ROLE"
				jsLoc="/admin-portal-resource/js/ag-grid/security/find-security-role.js"
				headerLabel=uiLabelMap.ListofSecurityRoles!
				headerExtra=rightContent!
				headerBarClass="grid-header-no-bar"
				headerId="security-role-tle"
				savePrefBtnId="security-role-save-pref"
				clearFilterBtnId="security-role-clear-pref"
				subFltrClearId="security-role-clear-sub-ftr"
				serversidepaginate=false
				statusBar=false
				exportBtn=true
				exportBtnId="security-role-list-export-btn"
				savePrefBtn=false
				clearFilterBtn=false
				subFltrClearBtn=false				
				/>
			
         	<form method="post" action="removeRole" name="removeRoleForm" id="removeRoleForm">
	            <@inputHidden
	                id="roleTypeId"
	                value="${requestParameters.roleTypeId!}"
	                />
        	</form>
         </div>
      </div>
   </div>
</div>
<script type="text/javascript">

</script>
