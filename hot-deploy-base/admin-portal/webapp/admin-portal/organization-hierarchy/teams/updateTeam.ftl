<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<div class="row">
	<div id="main" role="main">
	<#assign extra=' <a href="viewTeam?emplTeamId=${emplTeamId!}" class="btn btn-xs btn-primary"><i class="fa fa-chevron-circle-left" aria-hidden="true"></i> Back</a>'/>
		<div class="clearfix"></div>
		<form method="post" action="<@ofbizUrl>teamUpdation</@ofbizUrl>" class="form-horizontal" name="updateTeam" id="updateTeam">
			<div class="col-md-12 col-lg-12 col-sm-12 dash-panel">
			<@sectionFrameHeader title="${uiLabelMap.UpdateTeam!}" extra=extra />
				<div class="row">
					<div class="col-md-12 col-lg-6 col-sm-12 ">
						<@dynaScreen
							instanceId="UPDATE_TEAMS"
							modeOfAction="UPDATE"
							/>
					<!--<div class="form-group row">
							<label class="col-sm-4 col-form-label">Team ID</label>
							<div class="col-sm-7">
								<input type="text" class="form-control" value="T10000">
							</div>
						</div>-->
					</div>
					<div class="offset-sm-2 col-sm-10 pad-10">
						<input type="submit" class="btn btn-sm btn-primary" onclick="return formSubmission();" value="Update">
						<a href="viewTeam?emplTeamId=${emplTeamId!}"class="btn btn-sm btn-secondary"> Cancel</a>
					</div>
				</div>
			</div>
			<div class="clearfix"></div>
		<form>
		<div class="clearfix"></div>
	</div>
</div>
<script>
	function formCancel(){
		var url = "viewTeam?emplTeamId=${emplTeamId!}";
		window.location(url);
	}
	function formSubmission(){
		var teamName =  $("#teamName").val();
		var status =  $("#status").val();
		if(teamName !='' && status!=''){
			return true;
		}else{
			if(teamName == "") {
				$("#teamName_error").html('');
				$("#teamName_error").append('<ul class="list-unstyled text-danger"><li id="teamName_err">Please enter Team Name</li></ul>');
			}
			if(status == "") {
				$("#status_error").html('');
				$("#status_error").append('<ul class="list-unstyled text-danger"><li id="status_err">Please select Status</li></ul>');
			}
			return false;
		}
	}
	$("#teamName").keyup(function() {
		var teamName = $("#teamName").val();
		$("#teamName_error").empty();
	});
	$("#status").change(function() {
		var status = $("#status").val();
		$("#status_error").empty();
	});
</script>
