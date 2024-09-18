<#include "component://ab-ag-grid-support/webapp/ab-ag-grid-support/lib/ag_grid_header.ftl"/>
<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<div class="row">
    <div id="main" role="main" class="pd-btm-title-bar">
        <#-- <@sectionFrameHeader title="${uiLabelMap.FindSecurityGroup!}" />  -->
        <div class="col-lg-12 col-md-12 col-sm-12 dash-panel">
            <div>
                <#-- <div class="row">
                    <@arrowDownToggle />
                </div>  -->
                <div>
                    <div>
                    	<@sectionFrameHeader title="${uiLabelMap.FindSecurityGroup!}" />
                        <form method="post" name="searchForm" id="searchForm" data-toggle="validator">
                            <input type="hidden" name="externalLoginKey" value="${requestAttributes.externalLoginKey!}"/>
                            <input type="hidden" name="userId" value="${userLogin.userLoginId?if_exists}" />
                            <div class="">
                                <div class="row">
                                    <div class="col-lg-6 col-md-6 col-sm-12">
                                        <@inputCell 
                                            id="groupId"
                                            placeholder="Group ID"
                                            value="${requestParameters.groupId?if_exists}"
                                            maxlength=60
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
                </div>
            </div>
            </div>   
			<div class="clearfix"></div>
			
			<div class="col-lg-12 col-md-12 col-sm-12 dash-panel">
            <#assign rightContent='<a title="Create" href="createSecurityGroup" class="btn btn-primary btn-xs ml-2" target="_blank"> <i class="fa fa-plus" aria-hidden="true"></i> Create </a>' />   
						 		
			<#--
			<@AgGrid
				gridheadertitle=uiLabelMap.ListofSecurityGroups!
				gridheaderid="security-role-grid-action-container"
				insertBtn=false
				updateBtn=false
				removeBtn=false
				headerextra=rightContent
				
			    userid="${userLogin.userLoginId}" 
			    shownotifications="true" 
			    instanceid="SECURITY_GROUP" 
			    autosizeallcol="true"
			    debug="false"
			    />
			<script type="text/javascript" src="/admin-portal-resource/js/ag-grid/security/find-security-group.js"></script>
			-->
			<@fioGrid
				id="security-group"
				instanceId="SECURITY_GROUP"
				jsLoc="/admin-portal-resource/js/ag-grid/security/find-security-group.js"
				headerLabel=uiLabelMap.ListofSecurityGroups!
				headerExtra=rightContent!
				headerBarClass="grid-header-no-bar"
				headerId="security-group-tle"
				savePrefBtnId="security-group-save-pref"
				clearFilterBtnId="security-group-clear-pref"
				subFltrClearId="security-group-clear-sub-ftr"
				serversidepaginate=false
				statusBar=false
				exportBtn=true
				exportBtnId="security-group-list-export-btn"
				savePrefBtn=false
				clearFilterBtn=false
				subFltrClearBtn=false				
				/>

			 </div> 
        </div>
    </div>
</div>

<script>     

function loadMainGrid(fag) {
		
	$.ajax({
	  url:'/admin-portal/control/getSecurityGroups',
	  type:"POST",
	  data: JSON.parse(JSON.stringify($("#searchForm").serialize())),
	  success: function(data){
			// console.log("data: ", data)
		  fag.setRowData(data)
	  }
	})
	
}

$(document).ready(function() {

/*
	$("#doSearch").click(function(event) {
	    event.preventDefault(); 
	    loadAgGrid();
	});
	
*/

});
</script>