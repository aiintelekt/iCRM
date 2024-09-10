<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
    <div class="row">
        <div id="main" role="main">
            <#assign extra='<a href="updateTeam?emplTeamId=${emplTeamId!}" class="btn btn-xs btn-primary text-right">
                <i class="fa fa-edit" aria-hidden="true"></i> Update</a>
                <a href="teams" class="btn btn-xs btn-primary">
                <i class="fa fa-chevron-circle-left" aria-hidden="true"></i> Back</a>' />

			<div class="col-lg-12 col-md-12 col-sm-12 dash-panel">
				<@sectionFrameHeaderTab title="${uiLabelMap.ViewTeam!}" tabId="ViewTeam" extra=extra />
				<div class="" id="cp">
					<@dynaScreen
						instanceId="VIEW_TEAM"
						modeOfAction="VIEW"
						/>
					<@inputHidden
						id="emplTeamId"
						name="emplTeamId"
						value="${emplTeamId!}"
						/>
				</div>
			</div>
			<div class="clearfix"></div>
			
                <#--
                <div class="page-header border-b pt-2">
			        <div class="float-right" id="main-grid-action-container">
			        <a href="addMemberToTeam?emplTeamId=${emplTeamId!}"  class="btn btn-primary btn-xs"> <i class="fa fa-plus fa-1"></i> Add Member </a>
                  	<a href="addLeaderToTeam?emplTeamId=${emplTeamId!}"  class="btn btn-primary btn-xs"> <i class="fa fa-plus fa-1"></i> Add Leader </a>
               		<span id="main-grid-remove-btn" title="Remove" class="btn btn-primary btn-xs" ><i class="fa fa-times" aria-hidden="true"></i> Remove </span>  
                 	<#-- <a title="Update Team Role" href="#" class="btn btn-primary btn-xs" onclick="updateRole()"><i class="fa fa-edit "></i> Update Team Role</a> -- 
			        </div>
			        <div class="clearfix"></div>
			    </div> -->
		    	
		    	<div class="col-lg-12 col-md-12 col-sm-12 dash-panel">
		    	<#assign extra = ' <a href="" data-toggle="modal" data-target="#addMembersToTeam"  class="btn btn-primary btn-xs add-member-team"> <i class="fa fa-plus fa-1"></i> Add Member </a>
                  	<a href="" data-toggle="modal" data-target="#addMembersToTeam" class="btn btn-primary btn-xs add-lead-team"> <i class="fa fa-plus fa-1"></i> Add Leader </a>
                  	<span class="btn btn-xs btn-primary" id="remove-btn" data-toggle="confirmation" title="" data-original-title="Are you sure to REMOVE ?"><i class="fa fa-remove" aria-hidden="true"></i> Remove</span>' />
		    	
		    	<#-- <@AgGrid
					userid="${userLogin.userLoginId}" 
					instanceid="TEAM_MEMBER" 
					autosizeallcol="false"
					shownotifications="true"
		            autosizeallcol="true"
		            debug="false"
		            gridheadertitle="List of Members"
			    	gridheaderid="listofMembersBtns_1"
			    	statusBar=true
			    	serversidepaginate=false
			    	headerextra=extra!
			    	insertBtn=false
			    	updateBtn=false
					/>
				<script type="text/javascript" src="/admin-portal-resource/js/ag-grid/org-Hierarchy/team-user.js"></script>-->
	<input type="hidden" id="userId" value="${userLogin.userLoginId!}"/>
  	<input type="hidden" id="externalLoginKey" value="${requestAttributes.externalLoginKey!}"/>

		<@fioGrid 
			id="listofMembersBtns_1-grid"
			instanceId="TEAM_MEMBER"
			jsLoc="/admin-portal-resource/js/ag-grid/org-Hierarchy/team-user.js"
			headerLabel="List of Members"
			headerId="listofMembersBtns_1-list-grid-container"
			headerBarClass="grid-header-no-bar"
			savePrefBtn=false
			clearFilterBtn=false
			exportBtn=true
			subFltrClearBtn = false
			savePrefBtnId ="team-member-save-pref-btn"
			clearFilterBtnId ="teams-member-clear-filter-btn"
			subFltrClearId="teams-member-sub-filter-clear-btn"
			headerExtra=extra!
			exportBtnId="teams-member-list-export-btn"
			/>
				<form method="post" action="searchForm" id="searchForm" name="searchForm" novalidate="true" data-toggle="validator">
					<@inputHidden
						id="emplTeamId"
						name="emplTeamId"
						value="${emplTeamId!}"
						/> 
				</form>
               	<#-- 
                <div class=" pt-2">
                  	<a href="addMemberToTeam?emplTeamId=${emplTeamId!}"  class="btn btn-primary btn-xs"> <i class="fa fa-plus fa-1"></i> Add Member </a>
                  	<a href="addLeaderToTeam?emplTeamId=${emplTeamId!}"  class="btn btn-primary btn-xs"> <i class="fa fa-plus fa-1"></i> Add Leader </a>
               		<a title="Remove" href="#" class="btn btn-primary btn-xs" onclick="removeSubmit()"><i class="fa fa-times" aria-hidden="true"></i> Remove </a>  
                 	<a title="Update Team Role" href="#" class="btn btn-primary btn-xs" onclick="updateRole()"><i class="fa fa-edit "></i> Update Team Role</a> 
                  
                    <div class="clearfix"></div>
                </div>
                <div class="clearfix"></div>-->
               <#-- 
                <div class="table-responsive">
                 	<div class="loader text-center" id="loader" sytle="display:none;">
                  		<span></span>
                  		<span></span>
                  		<span></span>
            		</div>
            		<div id="T002" style="width: 100%;" class="ag-theme-balham"></div>
            		<script type="text/javascript" src="/admin-portal-resource/js/ag-grid/org-Hierarchy/team-user.js"></script> 
                          
                </div>
               	 -->
               	 </div>
            </div>
        </div>
    </div>   
    
<form method="post" action="removeMember" name="removeMemberForm" id="removeMemberForm">
    <@inputHidden
        id="emplTeamId"
        value="${emplTeamId!}"
        />
    <@inputHidden
        id="newPartyIds"
        />
</form>

<#-- <script type="text/javascript" src="/bootstrap/js/fio-ag-grid.js"></script>
<script type="text/javascript">
 var gridInstance1 = document.getElementById('T002');
 gridInstance1.addEventListener("buttonBarClickEvent", function(evt){
    switch(evt.detail.clickEventId){
        case "remove-selected":
            var selectedRows = fag["T002"].getInstanceApi().getSelectedRows();
            if(selectedRows !=null && selectedRows != "" && selectedRows != 'undefined'){
                $('#newPartyIds').val(JSON.stringify(selectedRows));
                $('#removeMemberForm').submit();
            } else{
                showAlert("error","Please select atleast one row");
            }
            break;
    }
 });
 
 var gridInstance2 = document.getElementById('T002');
 gridInstance2.addEventListener("buttonBarClickEvent", function(evt){
    switch(evt.detail.clickEventId){
        case "update-selected":
            var selectedRows = fag["T002"].getInstanceApi().getSelectedRows();
            var rownumber = fag["T002"].getInstanceApi().getSelectedRows().length;
            if(selectedRows !=null && selectedRows != "" && selectedRows != 'undefined' && rownumber == 1){
                $('#partuser').val(JSON.stringify(selectedRows[0].newPartyId));
                var partyuser=JSON.stringify(selectedRows[0].newPartyId);
                
              
                
                var partyrole=JSON.stringify(selectedRows[0].teamRole);
                
                var partyname=JSON.stringify(selectedRows[0].userName);
                
                var teamLeadId=JSON.stringify(selectedRows[0].teamId);
                
                var userParty = partyuser.replace(/"/g,"");
                var role = partyrole.replace(/"/g,"");
                var name = partyname.replace(/"/g,"");
                var leadId = teamLeadId.replace(/"/g,"");
              
                $('input[name=roleofteam][value=' + leadId + ']').prop('checked',true);
               
                 $('#partyUser').val(userParty);
                 $('#roleLead').val(role);
                 $('#partyName').val(name);
                 $('#led').val(leadId);
               
                 
                $('#updateMember').modal('show');
               
            } else{
                showAlert("error","Please select one row");
            }
            break;
    }
 });
</script> -->
 <#-- <div id="addmember" class="modal fade mt-2 save-modal" role="dialog">
  <div class="modal-dialog modal-lg">
    <div class="modal-content">
      <div class="modal-header">
        <h3 class="modal-title">Add Members</h3>
        <button type="reset" class="close" data-dismiss="modal">&times;</button>
      </div>
      <div class="modal-body">
           <div class="table-responsive">
                   
                  <#--   <@AgGrid 
                    userid="${userLogin.userLoginId}" 
                    instanceid="T003" 
                    styledimensions='{"width":"100%","height":"80vh"}'
                    autosave="false"
                    autosizeallcol="true" 
                    debug="true"
                    buttonbarbuttons='[{"label":"Add", "clickEventId":"add-selected", "styleClass":"btn btn-primary btn-xs ml-2"}, {"label":"Clear Selected", "clickEventId":"clear-selected", "styleClass":"btn btn-primary btn-xs ml-2"}]'
                    requestbody='{"emplTeamId":"${emplTeamId!}"}'
                    endpoint="/admin-portal/control/getMembers"
                 />
                 <form method="post" action="addMembers" id="addMembersForm" name="addMembersForm">
                 <@inputHidden
                    id="modalId"
                    value="addMember"/>
                    
                 <@inputHidden
                    id="emplTeamId"
                    value="${emplTeamId!}"/>
                    
                 <@inputHidden
                    id="newPartyId"
                    value=""/>
                    
                  <@inputHidden
                    id="partyId"
                    value=""/>
                 </form>
              </div>
             <script type="text/javascript" src="/bootstrap/js/fio-ag-grid.js"></script>
      </div>
      <div class="modal-footer">
        </div>
    </div>
  </div>
</div>
<script type="text/javascript">
 var gridInstance1 = document.getElementById('T003');
 gridInstance1.addEventListener("buttonBarClickEvent", function(evt){
    console.log("[gridInstance1] button bar click ---- ", evt.detail);
    switch(evt.detail.clickEventId){
        case "add-selected":
            var selectedRows = fag["T003"].getInstanceApi().getSelectedRows();
            if(selectedRows !=null && selectedRows != "" && selectedRows != 'undefined'){
                $('#modal-error').hide();
                $('#newPartyId').val(JSON.stringify(selectedRows));
                console.log("newPartyIds---"+JSON.stringify(selectedRows));    
                $('#addMembersForm').submit();
            } else{
                $('#modal-error').show();
            }
            break;
        case "clear-selected":
            fag["T003"].getInstanceApi().deselectAll();
            break;
    }
 });
</script>-->

<div id="updateMember" class="modal fade mt-2 save-modal" role="dialog">
  <div class="modal-dialog modal-md">
    <!-- Modal content-->
    <div class="modal-content">
    
    <form method="post" action="updateTeamRole" class="form-horizontal" name="updateTeamRoleForm" id="updateTeamRoleForm" >
      <div class="modal-header">
        <h3 class="modal-title">Update Team Role</h3>
        <button type="reset" class="close" data-dismiss="modal">&times;</button>
      </div>
      <div class="modal-body">        
                   <@inputHidden    
                        id="partyUser"
                        name="partyUser"
                      />
                    <@inputHidden    
                        id="emplTeamId"
                        name="emplTeamId"
                        value="${emplTeamId!}"
                      />
                    <@inputRow    
                        label="User Name"
                        id="partyName"
                        name="partyName"
                        readonly=true
                    />  
                     <@inputHidden       
                        id="led"
                        name="led"
                    /> 
                  <#assign optionMap = Static["org.ofbiz.base.util.UtilMisc"].toMap("Y" , "Team Leader", "N", "Team Member")>
                      <@radioButtonInput 
                       name="roleofteam"
                       id="roleofteam"
                       label="Team Role"
                       options=optionMap!
                       value=""
                     />
      </div>
      <div class="modal-footer">
       <@formButton
                     btn1type="submit"
                     btn1label="${uiLabelMap.Update}"
                     btn1onclick="return formSubmission();"
                   />
        </div>
        </form>
        
    </div>
  </div>
</div>
<#-- <div id="addleader" class="modal fade mt-2 save-modal" role="dialog">
  <div class="modal-dialog modal-lg">
 
    <div class="modal-content">
      <div class="modal-header">
        <h3 class="modal-title">Add Leaders</h3>
        <button type="reset" class="close" data-dismiss="modal">&times;</button>
      </div>
      <div class="modal-body">
           <div class="table-responsive">
                   
                   <#--  <@AgGrid 
                    userid="${userLogin.userLoginId}" 
                    instanceid="T006" 
                    styledimensions='{"width":"100%","height":"80vh"}'
                    autosave="false"
                    autosizeallcol="true" 
                    debug="true"
                    buttonbarbuttons='[{"label":"Add", "clickEventId":"add-selected", "styleClass":"btn btn-primary btn-xs ml-2"}, {"label":"Clear Selected", "clickEventId":"clear-selected", "styleClass":"btn btn-primary btn-xs ml-2"}]'
                    requestbody='{"emplTeamId":"${emplTeamId!}"}'
                    endpoint="/admin-portal/control/getMembers"
                 />
                 <form method="post" action="addLeaders" id="addLeadersForm" name="addLeadersForm">
                 <@inputHidden
                    id="modalId"
                    value="addLeader"/>
                    
                 <@inputHidden
                    id="emplTeamId"
                    value="${emplTeamId!}"/>
                    
                 <@inputHidden
                    id="newPartyIdLeader"
                    value=""/>
                    
                  <@inputHidden
                    id="partyIdLeader"
                    value=""/>
                 </form>
              </div>
             <script type="text/javascript" src="/bootstrap/js/fio-ag-grid.js"></script>
      </div>
      <div class="modal-footer">
        </div>
    </div>
  </div>
</div>
<script type="text/javascript">
 var gridInstance1 = document.getElementById('T006');
 gridInstance1.addEventListener("buttonBarClickEvent", function(evt){
    console.log("[gridInstance1] button bar click -- ", evt.detail);
    switch(evt.detail.clickEventId){
        case "add-selected":
            var selectedRows = fag["T006"].getInstanceApi().getSelectedRows();
            if(selectedRows !=null && selectedRows != "" && selectedRows != 'undefined'){
                $('#modal-error').hide();
                $('#newPartyIdLeader').val(JSON.stringify(selectedRows));
                console.log("newPartyIds--"+JSON.stringify(selectedRows));    
                $('#addLeadersForm').submit();
            } else{
                $('#modal-error').show();
            }
            break;
        case "clear-selected":
            fag["T006"].getInstanceApi().deselectAll();
            break;
    }
 });
</script>-->
 
 <div  id="addMembersToTeam" class="modal fade bd-example-modal-lg" tabindex="-1" role="dialog" aria-labelledby="myLargeModalLabel" aria-hidden="true">
	<div class="modal-dialog modal-lg" style="max-width: 1000px;">
		<div class="modal-content">
            <div class="modal-body" style="padding-bottom: 8px;">
			
				<#assign rightContent = ' <input type="button" value="Add" class="btn btn-xs btn-primary" id="add-member-btn" />
                  	           <input type="button" value="Close" class="btn btn-xs btn-primary" data-dismiss="modal" /> ' /> 
    	<form method="post" id="addMembersTeamForm" name="addMembersTeamForm">
		    <input type="hidden" name="emplTeamId" id="emplTeamId" value="${emplTeamId!}"/>
		  	<input type="hidden" name="roleTypeId" id="roleTypeId" value="SALES_REP,CUST_SERVICE_REP"/>
		  	<input type="hidden" name="status" id="status" value="Y"/>
    	</form>
		    	<#-- <@AgGrid
					gridheadertitle="List of Members"
					gridheaderid="members-grid-action-container"
					refreshPrefBtn=false
					savePrefBtn=false
					clearFilterBtn=false
					exportBtn=false
					insertBtn=false
					updateBtn=false
					removeBtn=false
					headerextra=rightContent
				    userid="${userLogin.userLoginId}" 
				    shownotifications="true" 
				    instanceid="ORG_USERS_LIST" 
				    autosizeallcol="true"
				    debug="false"
				 />    
			         
				<script type="text/javascript" src="/admin-portal-resource/js/ag-grid/org-Hierarchy/members-list.js"></script>-->
	<input type="hidden" id="userId" value="${userLogin.userLoginId!}"/>
  	<input type="hidden" id="externalLoginKey" value="${requestAttributes.externalLoginKey!}"/>

		<@fioGrid 
			id="members-grid"
			instanceId="ORG_USERS_LIST"
			jsLoc="/admin-portal-resource/js/ag-grid/org-Hierarchy/members-list.js"
			headerLabel="List of Members"
			headerId="members-list-grid-container"
			headerBarClass="grid-header-no-bar"
			savePrefBtn=false
			clearFilterBtn=false
			exportBtn=true
			subFltrClearBtn = false
			savePrefBtnId ="members-save-pref-btn"
			clearFilterBtnId ="members-clear-filter-btn"
			subFltrClearId="members-sub-filter-clear-btn"
			headerExtra=rightContent!
			exportBtnId="members-list-export-btn"
			/>
				<form method="post" action="addMembersToTeam" id="addMembersToTeamForm" name="addMembersToTeamForm" novalidate="true" data-toggle="validator">
		            <@inputHidden
		                id="emplTeamId"
		                name="emplTeamId"
		                value="${requestParameters.emplTeamId!}"
		                /> 
		            <@inputHidden
		                id="selectedRows"
		                name="selectedRows"
		                value=""
		                />
		            <@inputHidden
		                id="teamLeadFlag"
		                name="teamLeadFlag"
		                value=""
		                />
		        </form>
			
      		</div>
	      	
    	</div>
  	</div>
</div>
 
<script>
/*
function removeMainGrid(fag, api) {
	
	var selectedData = api.getSelectedRows();
    if(selectedData !=null && selectedData != "" && selectedData != 'undefined' && selectedData.length == 1){
        $('#newPartyIds').val(JSON.stringify(selectedData));
        $('#removeMemberForm').submit();
    } else {
        showAlert("error","Please select one row");
    }
	
} */

function permissionFunction()
{
	showAlert("error","Sorry this function is not available for now");
}

$(document).ready(function() {
	
	$(".add-member-team").on('click', function(e) {
    	$("#teamLeadFlag").val("N");
    });
    
    $(".add-lead-team").on('click', function(e) {
    	$("#teamLeadFlag").val("Y");
    });
    
});

</script>