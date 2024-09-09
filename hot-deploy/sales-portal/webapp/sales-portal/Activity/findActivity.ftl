<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<div class="row">
	<div id="main" role="main">
     <#assign extra='
    	<script>
       		$(document).ready(function(){
         		$("#accordion").click(function(){
             	$(".row").show();
         		});
       		});
        </script> 
    ' />                        
    <@sectionFrameHeader
    	title="${uiLabelMap.FindActivities}"
        extra=extra?if_exists
    />
    <div class="col-lg-12 col-md-12 col-sm-12" profil-sec-padding pt-0">
    	<div id="accordion">
        	<div class="row">
            	<@arrowDownToggle />
            </div>
            <div class="border rounded bg-light margin-adj-accordian pad-top">
            	<form name="findActivity" id="findActivity" action="#" method="post">  
                	<div class="border rounded bg-light mt-3">
                    	<div class="row p-2">
                        	<div class="col-lg-4 col-md-6 col-sm-12">
                            	<div class="form-group">
                                	<@inputCell
                                    	id="workEffortId"
                                        name="workEffortId"
                                        placeholder="Activity Number"
                                     />
                                </div> 
           						<div class="form-group">
              						<#assign activityType = delegator.findByAnd("WorkEffortAssocTriplet", {"entityName" : "Activity" , "type" : "Type", "active","Y"}, null, false)>
                    				<#assign activityTypeList = Static["org.fio.admin.portal.util.DataHelper"].getDropDownOptions(activityType, "code","value")?if_exists />
                    				<@dropdownCell
                                    	id="workEffortServiceType"
                                        name="workEffortServiceType"
                                        placeholder="Select Activity Type"
                                        options=activityTypeList!
	                                    value="${requestParameters.activityType?if_exists}" 
                                    />
                                </div>
                            </div>  
                            <div class="col-lg-4 col-md-6 col-sm-12">
                            	<div class="form-group">
                                	<@inputCell
                                    	id="primOwnerId"
                                        name="primOwnerId"
                                        placeholder="Owner"
                                     />
                                </div>
                                <div class="form-group">
	                       			<#assign activitySubTypes = delegator.findByAnd("WorkEffortAssocTriplet", {"type" : "SubType"}, null, false)>
	                          		<#assign activitySubTypeList = Static["org.fio.admin.portal.util.DataHelper"].getDropDownOptions(activitySubTypes, "code","value")?if_exists />
	                            	<@dropdownCell
	                                	id="workEffortSubServiceType"
	                                    name="workEffortSubServiceType"
	                                    placeholder="Select Activity Sub Type"
	                                    allowEmpty=true
	                                    options=activitySubTypeList!
	                                    value="${requestParameters.activitySubTypes?if_exists}" 
	                                />
                                </div>
                            </div>
                            <div class="col-lg-4 col-md-6 col-sm-12">                                          
                            	<div class="form-group">
                                	<@inputCell
                                    	id="createdByUserLogin"
                                        name="createdByUserLogin"
                                        placeholder="Created by"
                                    />
                                </div>
                                <div class="form-group">
                                	<#assign componentList = (Static["org.ofbiz.entity.util.EntityQuery"].use(delegator).from("StatusItem").where("statusTypeId", "IA_STATUS_ID").queryList())?if_exists>
                                	<#assign components = (Static["org.fio.admin.portal.util.DataHelper"].getDropDownOptions(componentList, "statusId","description"))?if_exists />
                                	<@dropdownCell
                                    	id="currentStatusId"
                                        name="currentStatusId"
                                        placeholder="Select Status"
                                        options=components!
	                                    value="${requestParameters.description?if_exists}" 
                                    />
                                </div>
                            </div>
                            <div class="col-lg-12 col-md-12 col-sm-12">     
                            	<div class="text-right">
                                	<div class="form-check-inline">
                                    	<label class="form-check-label flx-cbx-lbl">
                                        	<input type="checkbox" class="form-check-input" id="statusopen"
                                            	name="statusopen" value="IA_OPEN" checked>Open
                                        </label>
                                    </div>
                                    <div class="form-check-inline">
                                    	<label class="form-check-label flx-cbx-lbl">
                                    	    <input type="checkbox" class="form-check-input" id="statuscompleted"
                                            	name="statuscompleted" value="IA_MCOMPLETED" >Completed
                                        </label>
                                   	</div>
                                    <@button 
                                    	label="${uiLabelMap.search}"
                                        id="doSearch"
                                    />
                                    <@reset
                                    	label="${uiLabelMap.reset}"
                                    />
                          		</div>
                          	</div>
                </form>
            </div>
        </div>
    </div>
    </div>
</div>
<div class="col-lg-12 col-md-12 col-sm-12 profil-sec-padding pt-0">
	<div class="clearfix"></div>
    <div class="page-header border-b pt-2">
    	<@headerH2
    		title="${uiLabelMap.searchResults}"
        	class="float-left"
        />
        <a id="export_to_excel_icon" title="Export to CSV" href="#" class="btn btn-primary btn-xs" onclick="onBtExport()"> <i class="fa fa-file-excel-o" aria-hidden="true"></i> Export</a> 
        <a id="reassign" title="Reassign" href="#" class="btn btn-primary btn-xs" onclick=""><i class="fa fa-retweet " aria-hidden="true"></i>Reassign</a> 
        <div class="clearfix"></div>
    </div>
    <div class="clearfix"></div>
    	<div class="table-responsive">
        	<div id="activityGrid" style="height:380px; width: 100%;" class="ag-theme-balham"></div>
            	<script type="text/javascript" src="/sales-portal-resource/find-activity.js"></script>
        </div>
    </div>
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
	$("#reassign").click(function () {
    	var workEffortId;
        var rowdata = getSelectedRowsForReassign();
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
	            } else {
	            	$.notify({
	                	message : '<p>There is no USER or TEAM available to reassign</p>',
	                });
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
                dataSet =  { "emplTeamId": emplTeamId , "businessUnitId": "" };}
                if(test=="team")
                {        
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
	              loadActivityGrid();
	            }
	        });   
    	}
	} 
	$(document).ready(function() {
		$("#doSearch").click(function(event) {
	    	event.preventDefault(); 
	    	loadActivityGrid();
		});
	});
</script>        

