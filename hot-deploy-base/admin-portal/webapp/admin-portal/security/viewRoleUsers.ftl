<#include "component://bootstrap/lib/ofbizFormMacros.ftl"/>
<div class="row">
    <div id="main" role="main">
       <#assign extra = '<a href="findSecurityRole" class="btn btn-xs btn-primary"><i class="fa fa-chevron-circle-left" aria-hidden="true"></i> Back</a>' />

       <#assign roleType = (Static["org.ofbiz.entity.util.EntityQuery"].use(delegator).select("description").from("RoleType").where("roleTypeId",requestParameters.roleTypeId!).queryOne())?if_exists />     
       <div class="col-lg-12 col-md-12 col-sm-12 dash-panel">
       <@sectionFrameHeaderTab  title="${uiLabelMap.ViewRoleUsers!}"
       extra=extra! tabId="ViewRoleUsers"/> 
          <div class="clearfix"></div>
          
          <div class="">
	        <@headerH2 title="${uiLabelMap.ListofRoleUsers} : ${roleType?if_exists.description!}" class="float-left"/>
	      </div>
        	<form action="" method="post" id="searchForm" name="searchForm">
        		<input type="hidden" name="externalLoginKey" value="${requestAttributes.externalLoginKey!}"/>
				<input type="hidden" name="userId" value="${userLogin.userLoginId?if_exists}" />
		    	<@inputHidden 
		    		id="roleTypeId"
		    		value="${requestParameters.roleTypeId!}"
		    		/>
		    </form>	 		
			<#--<@AgGrid
			gridheadertitle=""
			gridheaderid="role-user-grid"
			savePrefBtn=true
			clearFilterBtn=true
			exportBtn=true
			insertBtn=false
			updateBtn=false
			removeBtn=false
						
		    userid="${userLogin.userLoginId}" 
		    shownotifications="true" 
		    instanceid="ROLE_USERS" 
		    autosizeallcol="true"
		    debug="false"
		    />		
			<script type="text/javascript" src="/admin-portal-resource/js/ag-grid/security/role-users.js"></script>	
          -->
        	<@fioGrid
				id="role-user"
				instanceId="ROLE_USERS"
				jsLoc="/admin-portal-resource/js/ag-grid/security/role-users.js"
				headerLabel=""
				headerExtra=rightContent!
				headerBarClass="grid-header-no-bar"
				headerId="role-user-tle"
				savePrefBtnId="role-user-save-pref"
				clearFilterBtnId="role-user-clear-pref"
				subFltrClearId="role-user-clear-sub-ftr"
				serversidepaginate=false
				statusBar=false
				exportBtn=true
				exportBtnId="role-user-list-export-btn"
				savePrefBtn=false
				clearFilterBtn=false
				subFltrClearBtn=false
				/>

       </div>
    </div>
</div>