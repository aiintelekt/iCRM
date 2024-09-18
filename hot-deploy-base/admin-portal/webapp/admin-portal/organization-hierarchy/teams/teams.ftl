<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<div class="row">
	<div id="main" role="main">
		<#-- <@sectionFrameHeader title="${uiLabelMap.FindTeam!}" />  -->
		<div class="col-lg-12 col-md-12 col-sm-12 dash-panel">
			<div id="">
				<div class="row">
					<#-- 
					<div class="iconek">
						<div class="arrow-down" style="margin-bottom: 10px;" onclick="this.classList.toggle('active')"></div>
					</div>
					-->
				</div>
				<div>
					<div>
						<div class="">
							<form action="teams" method="post" id="searchForm" name="searchForm">
								<@sectionFrameHeader title="${uiLabelMap.FindTeam!}" />
								<@inputHidden
									id="searchCriteria"
									/>
								<@dynaScreen
									instanceId="FIND_TEAM"
									modeOfAction="CREATE"
									/>
								<div class="text-right mt-1 pad-10">
									<@button
										label="${uiLabelMap.Search}"
										id="main-search-btn"
										/>
								</div>
							</form>
						</div>
					</div>
				</div>
			</div>
		</div>
		<div class="clearfix"></div>
		<#-- 
		<div class="page-header border-b pt-2">
			<@headerH2 title="List Of Teams" class="float-left"/>
			<div class="float-right" id="main-grid-action-container">
				<a href="" data-toggle="modal" data-target="#addteam" class="btn btn-primary btn-xs ml-2"> <i class="fa fa-plus fa-1"></i> Create</a>
			</div>
			<div class="clearfix"></div>
		</div>
		-->
		<div class="col-lg-12 col-md-12 col-sm-12 dash-panel">
			<#assign extra ='<a href="" data-toggle="modal" data-target="#addteam" class="btn btn-primary btn-xs ml-2"> <i class="fa fa-plus fa-1"></i> Create</a>' />
			<#-- <@AgGrid
				userid="${userLogin.userLoginId}" 
				instanceid="ORG_TEAM"  
				shownotifications="true"
				autosizeallcol="true"
				debug="false"
				gridheadertitle="List of Teams"
				gridheaderid="listofTeamsBtns"
				statusBar=true
				serversidepaginate=false
				insertBtn=false
				removeBtn=false
				updateBtn=false
				headerextra=extra!
				/>
			<script type="text/javascript" src="/admin-portal-resource/js/ag-grid/org-Hierarchy/team.js"></script>-->
	<input type="hidden" id="userId" value="${userLogin.userLoginId!}"/>
  	<input type="hidden" id="externalLoginKey" value="${requestAttributes.externalLoginKey!}"/>

		<@fioGrid 
			id="listofTeamsBtns-grid"
			instanceId="ORG_TEAM"
			jsLoc="/admin-portal-resource/js/ag-grid/org-Hierarchy/team.js"
			headerLabel="List of Teams"
			headerId="listofTeamsBtns-list-grid-container"
			headerBarClass="grid-header-no-bar"
			savePrefBtn=true
			clearFilterBtn=true
			exportBtn=true
			subFltrClearBtn = true
			savePrefBtnId ="teams-save-pref-btn"
			clearFilterBtnId ="teams-clear-filter-btn"
			subFltrClearId="teams-sub-filter-clear-btn"
			headerExtra=extra!
			exportBtnId="teams-list-export-btn"
			/>
		</div>
	</div>
	<#-- 
	<div class="table-responsive">
		<div class="loader text-center" id="loader" sytle="display:none;">
			<span></span>
			<span></span>
			<span></span>
		</div>
		<div id="grid1" style="width: 100%;" class="ag-theme-balham"></div>
		<script type="text/javascript" src="/admin-portal-resource/js/ag-grid/org-Hierarchy/team.js"></script> 
	</div>
	-->
</div>
</div>
</div>
</div>
</div>
<#-- To create teams row-->
<style>
	#addteam .ui.search.dropdown .menu{
	max-height:119px;
	}
</style>
<div id="addteam" class="modal fade mt-2" role="dialog">
	<div class="modal-dialog modal-md" >
		<!-- Modal content-->
		<form method="post" action="<@ofbizUrl>teamCreation</@ofbizUrl>" class="form-horizontal" name="createTeam" id="createTeam" >
		<div class="modal-content">
			<div class="modal-header">
				<h3 class="modal-title">Create Team</h3>
				<button type="reset" class="close" data-dismiss="modal">&times;</button>
			</div>
				<div class="modal-body">
					<@dynaScreen
						instanceId="CREATE_TEAMS"
						modeOfAction="CREATE"
						/>
				</div>
			<div class="modal-footer">
				<#--<input type="submit" id="myWish" class="btn btn-sm btn-primary mt-2" value="Save">-->
				<@formButton
					btn1type="submit"
					btn1label="${uiLabelMap.Save}"
					btn1onclick="return formSubmission();"
					btn2=true
					btn2onclick = "resetForm()"
					btn2type="reset"
					btn2label="${uiLabelMap.Clear}"
					/>
			</div>
			<form>
		</div>
	</div>
</div>
<script>
	/*
    function loadMainGrid(fag) {
    		
    	$.ajax({
    	  url:'/admin-portal/control/getTeam',
    	  type:"POST",
    	  data: JSON.parse(JSON.stringify($("#searchForm").serialize())),
    	  success: function(data){
    			// console.log("data: ", data)
    		  fag.setRowData(data)
    	  }
    	})
    	
    }
    
    
    function prepareForm(){
        var teamName = $("#teamName").val();
        var buName = $("#buName").val();
        var teamStatus = $("#teamStatus").val();
        
        item = {}
        item ["teamName"] = teamName;
        item ["buName"] = buName;
        item ["teamStatus"] = teamStatus;
       
        jsonString = JSON.stringify(item);
        $("#searchCriteria").val(jsonString);
        $("#searchForm").submit();
        
    }
    */
    function resetForm(){
    $('[id*="_error"]').empty();
    }
    function formSubmission(){
        var teamId =  $("#teamId").val();
        var businessUnit =  $("#businessUnit").val();
        var status =  $("#status").val();
        if(teamId !='' && businessUnit !='' && status!=''){
            return true;
        }else{
            if(teamId == "") {
               $("#teamId_error").html('');
               $("#teamId_error").append('<ul class="list-unstyled text-danger"><li id="teamId_err">Please enter Team Name</li></ul>');
            }
            if(businessUnit == "") {
              $("#businessUnit_error").html('');
              $("#businessUnit_error").append('<ul class="list-unstyled text-danger"><li id="businessUnit_err">Please select Business Unit</li></ul>');
            }
            if(status == "") {
                $("#status_error").html('');
                $("#status_error").append('<ul class="list-unstyled text-danger"><li id="status_err">Please select Status</li></ul>');
            }
            return false;
        }
    }
    $("#teamId").keyup(function() {
       var teamId = $("#teamId").val();
       $("#teamId_error").empty();
    });
     $("#businessUnit").change(function() {
       var businessUnit = $("#businessUnit").val();
       $("#businessUnit_error").empty();
    });
     $("#status").change(function() {
       var status = $("#status").val();
       $("#status_error").empty();
    });
</script>