<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>

<div class="row">
	<div id="main" role="main">
    	<@sectionFrameHeader title="${uiLabelMap.Activities!}" extraLeft=extraLeft />
    	<form action="Activities" method="post" id="Activities" name="Activities">
      		<@inputHidden id="searchCriteria" />
			<@inputHidden id="open" value="y" />
			<@inputHidden id="searchParam1" value="${ownerUserLoginId?if_exists}" />	  
			<@inputHidden id="ownerUserLoginId" value="${ownerUserLoginId?if_exists}" />
			<@inputHidden id="sixMonthsDate" value="${sixMonthsDate}" />
			<@inputHidden id="systemViewFilter" value="loggedInUserOpenActivities" />
 
			<div class="col-lg-12 col-md-12 col-sm-12">
	     		<div class="row">
	         		<div class="col-xl-3 col-lg-6 col-md-12 col-sm-12">
	             		<div class="small-box border rounded bg-o">
	                 		<div class="inner float-left mr-4">
	                      		<h3><span id="myActivities"></span></h3>
	                      		<p class="mb-0">My Open Activities</p>
                     		</div>
	                  		<div class="icon float-left">
	                       		<i class="fa fa-calendar text-light"></i>
	              			</div>
                		</div>  
            		</div>
            		
            		<div class="col-xl-3 col-lg-6 col-md-12 col-sm-12">
               			<div class="small-box border rounded bg-s">
	                    	<div class="inner float-left mr-4">
	                        	<h3><span id="myTeamActivities"></span></h3>
	                         	<p class="mb-0">My Team's Open Activities</p>
                   			</div>
                   			<div class="icon float-left">
                        		<i class="fa fa-users text-light"></i>
                   			</div>
               			</div>
          			</div>
          			
          			<div class="col-xl-3 col-lg-6 col-md-12 col-sm-12">
              			<div class="small-box border rounded bg-i">
                  			<div class="inner float-left mr-4">
                      			<h3><span id="completedActivities"></span></h3>
                      			<p class="mb-0">My Completed Activities</p>
                			</div>
               				<div class="icon float-left">
                    			<i class="fa fa-list-alt text-light"></i>
           					</div>
       					</div>
  					</div>
  					
  					<div class="col-xl-3 col-lg-6 col-md-12 col-sm-12">
       					<div class="small-box border rounded bg-w">
          					<div class="inner float-left mr-4">
              					<h3><span id="overDueActivities"></span></h3>
              					<p class="mb-0">Overdue Activities</p>
          					</div>
           					<div class="icon float-left">
                  				<i class="fa fa-file-text-o text-light"></i>
             				</div>
        				</div>
    				</div>
   				</div>
			</div>

			<div class="col-lg-12 col-md-12 col-sm-12">
    			<div class="page-header border-b pt-2">
             		<h2 class="float-left"><span id="systemViewFilterLabel">My Open Activities</span>
						<a class="text-dark" href="" id="dropdown05" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
							<i class="fa fa-angle-down" aria-hidden="true"></i>
						</a>
                   		<div class="dropdown-menu" aria-labelledby="dropdown05" x-placement="bottom-start" style="position: absolute; transform: translate3d(220px, 38px, 0px); top: 0px; left: 0px; will-change: transform;">
				   			<h4>System Views</h4>
							<a class="dropdown-item" style="padding: .25rem 1.5rem!important;" id="loggedInUserActivities" href="">My Activities </a>
							<a class="dropdown-item" style="padding: .25rem 1.5rem!important;" id="loggedInUserOpenActivities" href="">My Open Activities </a>
							<a class="dropdown-item" style="padding: .25rem 1.5rem!important;" id="loggedInUserClosedActivities" href="">My Closed Activities </a>
							<a class="dropdown-item" style="padding: .25rem 1.5rem!important;" id="loggedInUserTeamActivities" href="">My Team's Activities</a>
							<a class="dropdown-item" style="padding: .25rem 1.5rem!important;" id="loggedInUserTeamOpenActivities" href="">My Team's Open Activities</a>
							<a class="dropdown-item" style="padding: .25rem 1.5rem!important;" id="loggedInUserTeamClosedActivities" href="">My Team's Completed Activities</a>
						</div>
                	</h2>
                	
                	<a id="csr_icon" title="Add Activities" href="<@ofbizUrl>addTask</@ofbizUrl>" class="text-dark ">
						<i class="fa fa-plus fa-1 right-icones ml-2" aria-hidden="true" style="font-size: 18px;"></i>
					</a>
					
					<a id="export_to_excel_icon" title="" href="#" class="btn btn-primary btn-xs ml-2" onclick="onBtExport()" data-original-title="Export to Excel">
						<i class="fa fa-file-excel-o" aria-hidden="true"></i> Export
					</a>
					
					<a id="reassign" title="Reassign" href="#" class="btn btn-primary btn-xs" onclick="getSelectedRows()">
						<i class="fa fa-retweet " aria-hidden="true"></i> Reassign
					</a>
					
					<a id="export_to_excel_icon" title="Search" href="<@ofbizUrl>findActivity</@ofbizUrl>" class="btn btn-primary btn-xs">
						<i class="fa fa-search"></i> &nbsp;Search
					</a>
					
					<div class="clearfix"></div>
				</div>
                <div id="homegrid" style="height: 600px; width: 100%;" class="ag-theme-balham">
                	<script type="text/javascript" src="/sr-portal-resource/js/services/activityhome.js"></script>
        	  	</div>
        	</div>
		</form>
	</div>
</div>

<div id="reassignModal" class="modal fade" role="dialog">
	<div class="modal-dialog" style="width:60%;">
    	<div class="modal-content">
        	<div class="modal-header">
            	<h4 class="modal-title">Assign To</h4>
                <button type="button" class="close" data-dismiss="modal" id="close">&times;</button>
            </div>
            <div class="modal-body">
            	<input type='hidden' id="externalId" name="externalId" />
                <form method="post" action="#" id="reassignModal" class="form-horizontal" name="" novalidate="novalidate" data-toggle="validator">
                	<div class="row p-1">
                    	<div class="col-md-12 col-lg-12 col-sm-12 ">
                    		&nbsp;User &nbsp;<input type="radio" id="user" name="emp" value="user">
                       		Team &nbsp;<input type="radio" id="team" name="emp" value="team">
                        	<div class="textboxUser" id="textboxUser">
                            	<@dropdownCell
                                	label="User/Team"
                                  	required=true
                                	id="userText"
                                	value=""
                                	placeholder="Select User"
                              	/>
                        	</div>
                        	<div class="textboxteam" id="textboxteam" style="display: none;">
                            	<@dropdownCell
                                	label="Team"
                                  	required=true
                                	id="teamText"
                                	value=""
                                	placeholder="Select Team"
                              	/>
                        	</div>
                    	</div>
               		</div>
             
             		<div class="modal-footer">
                 		<@submit class="btn btn-sm btn-primary navbar-dark" id="saveModal" label="${uiLabelMap.Save}" />
                 		<button type="button" class="btn btn-default btn-primary navbar-dark" id="btnclose" data-dismiss="modal">Close</button>
                 		<@reset id="reset" label="${uiLabelMap.Reset}" />
                 	</div>
            	</form>
        	</div>
    	</div>
	</div>
</div>
     
<script>

$(function() {
	getActivityCounts();
});
	
function getActivityCounts() {
    var result = null;
    $.ajax({
        type: "POST",
        url: "getActivityCounts",
        async: false,
        data: "",
        success: function(data) {
            result=data;
            $("#myActivities").html(data["myActivities"]);
			$("#myTeamActivities").html(data["myTeamActivities"]);
			$("#completedActivities").html(data["completedActivities"]);
			$("#overDueActivities").html(data["overDueActivities"]);
			
        },error: function(data) {
        	result=data;
			console.log('Error occured');
			showAlert("error", "Error occured while fetching Tiles Data!");
		}
    });
}
     
$("#reassign").click(function () {
	var workEffortId;
  	var rowdata = getSelectedRows();
              
    if(rowdata!=null && rowdata!=""){
    	rowdata.forEach(element => {
      		var csrloginId = element.primOwnerId;     
      		var emplTeamId = element.emplTeamId;
      		var businessUnitName = element.businessUnitName;
      		var businessUnitId = element.businessUnitId;
        	workEffortId=element.workEffortId;
	   		if (emplTeamId != "" && businessUnitId != "") {
	    		loaduserteam(emplTeamId , businessUnitId,workEffortId);
	        	$("#reassignModal").modal();
	        	$("input[type='reset']").hide();
	   		} 
    	});
      } else {
      	$.notify({
        	message : '<p>Please select one record in the list</p>',
        });
      }
 });
 
      
function loaduserteam(emplTeamId,businessUnitId,workEffortId) {
	var nonSelectContent;
    var userOption;       
    var teamOptions;
    var dataSet = {};
    $("input[name$='emp']").click(function() {
    	var test = $(this).val();
        if(test=="user"){      
        	$("div.textboxteam").hide();
            $("div.textboxUser").show();                          
            document.getElementById("userText").innerHTML = null;
            dataSet =  { "emplTeamId": emplTeamId , "businessUnitId": "" };
     	}
        if(test=="team"){        
        	$("div.textboxteam").show();
            $("div.textboxUser").hide();
            document.getElementById("userText").innerHTML = null;
            dataSet =  { "emplTeamId": "" , "businessUnitId": businessUnitId };
      	}
                 
      	$.ajax({
        	type: "POST",
            url: "getOwnerTeam",
            data:dataSet,
            async: false,
            success: function(data) {
            	nonSelectContent = "<span class='nonselect'>Please Select</span>";
            	userOption = '<option value="" data-content="'+nonSelectContent+'" selected="">Please Select</option>';       
            	teamOptions = '<option value="" data-content="'+nonSelectContent+'" selected="">Please Select</option>';
            	var sourceDesc = data.results;
            	for (var i = 0; i < data.length; i++) {
            		var category = data[i];
                	userOption += '<option value="'+category.partyId+'">'+category.partyName+'</option>';                     
                	teamOptions += '<option value="'+category.emplTeamId+'">'+category.teamName+'</option>';
            	}
            }
          });
          $("#userText").html(userOption);
          $("#teamText").html(teamOptions);
       });
           
       var result;       
       document.getElementById('saveModal').onclick = () => {
      		var primOwnerId=  $('#userText').val();        
       		var emp=  $('#teamText').val();    
       		var dataSets =  { "workEffortId": workEffortId , "primOwnerId": primOwnerId, "emplTeamId":emp };
      
       		$.ajax({
            	type: "POST",
            	url: "UpdateReasignActivity",
            	data:dataSets,
            	async: false,
            	success: function(data) { 
            		$("#reassignModal").modal('hide');   
              		$.notify({
                		message : '<p>Reassigned Successfully for</p>'+workEffortId,
              		});
              		loadActivitiesHomegrid();
            	}
        	});
    	}
	}
	
</script>

