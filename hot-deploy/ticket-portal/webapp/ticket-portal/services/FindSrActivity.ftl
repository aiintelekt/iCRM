<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>

<div class="row">
	<div id="main" role="main" style="padding-bottom: 0px;">
    	<@sectionFrameHeader title="${uiLabelMap.FindActivities!}" />
        <div class="col-lg-12 col-md-12 col-sm-12 profil-sec-padding pt-0">
        	<div id="accordion" class="ui-accordion ui-widget ui-helper-reset" role="tablist">
        		<div class="iconek ui-accordion-header ui-corner-top ui-state-default ui-accordion-header-active ui-state-active ui-accordion-icons" role="tab" id="ui-id-1" aria-controls="ui-id-2" aria-selected="true" aria-expanded="true" tabindex="0">
            		<span class="ui-accordion-header-icon ui-icon ui-icon-triangle-1-s"></span>
                	<div class="arrow-down" onclick="this.classList.toggle('active')"></div>
            	</div>
       			<div style="height: 315.133px;" class="ui-accordion-content ui-corner-bottom ui-helper-reset ui-widget-content ui-accordion-content-active" id="ui-id-2" aria-labelledby="ui-id-1" role="tabpanel" aria-hidden="false">
            		<div class="card-header margin-adj-accordian pad-top">     
                		<form action="#" method="post" id="Activities" name="Activities">
               				<@inputHidden id="searchCriteria" />
              				<div class="row p-2">
	                     		<div class="col-lg-4 col-md-6 col-sm-12">
	                          		<@inputCell
	                            		id="workEffortId"
	                             		placeholder="Activity Number"
	                             		maxlength=60
	                            	/>
	                           		<@inputCell
	                           			name="businessUnitName"
	                            		id="businessUnitName"
	                             		placeholder="Owner"
	                             		maxlength=60
	                            	/>
	                         		<@inputDate
		                        		id="actualStartDate"
		                        		type="date"
		                        		value="${requestParameters.actualStartDate?if_exists}"
		                        		placeholder="Start Date"
	                        		/>
	                       		</div>
                     
	                     		<div class="col-lg-4 col-md-6 col-sm-12">
	                       			<#assign activityType = delegator.findByAnd("WorkEffortAssocTriplet", {"entityName" : "Activity" , "type" : "Type", "active","Y"}, null, false)>
	                    			<#assign activityTypeList = Static["org.fio.admin.portal.util.DataHelper"].getDropDownOptions(activityType, "code","value")?if_exists />
	                         		<@dropdownCell
	                            		id="workEffortServiceType"
	                            		name="workEffortServiceType"
	                            		placeholder="Select Activity Type"
	                             		options=activityTypeList!
	                           			value="${requestParameters.activityType?if_exists}" 
	                            	/>
	                          		<@inputCell
	                            		id="createdByUserLogin"
	                            		placeholder="Created By"
	                            		value=""
	                            	/>
	                              	<@inputDate
		                        		id="actualEndDate"
		                        		type="date"
		                        		value="${requestParameters.actualEndDate?if_exists}"
		                        		placeholder="End Date"
	                           		/>
	                    		</div>
                    		
	                    		<div class="col-lg-4 col-md-6 col-sm-12">
	                   				<#assign activitySubTypes = delegator.findByAnd("WorkEffortAssocTriplet", {"type" : "SubType"}, null, false)>
	                    			<#assign activitySubTypeList = Static["org.fio.admin.portal.util.DataHelper"].getDropDownOptions(activitySubTypes, "code","value")?if_exists />
	                           		<@dropdownCell
	                           	 		id="workEffortSubServiceType"
	                            		placeholder="Select Activity Sub-Type"
	                            		options=activitySubTypeList!
	                            		value="${requestParameters.workEffortSubServiceType?if_exists}"
	                            		name="workEffortSubServiceType"
	                            	/>
	                    			<#assign  statusList = (Static["org.ofbiz.entity.util.EntityQuery"].use(delegator).from("StatusItem").where("statusTypeId", "IA_STATUS_ID").queryList())?if_exists>
	                    			<#assign statusd = (Static["org.fio.admin.portal.util.DataHelper"].getDropDownOptions( statusList, "statusId","description"))?if_exists />
	                           		<@dropdownCell
	                            		id="currentStatusId"
	                            		placeholder="Select Status"
	                           			options=statusd!
	                           			value="${requestParameters.description?if_exists}" 
	                            	/> 
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
	                            			id="doSearch"
				                            label="${uiLabelMap.Search}"
				                            onclick="loadActivitiesHomegrid()"
	                            		/>
	                            		<@reset
	                            			id="reset"
	                            			label="${uiLabelMap.Reset}"
	                            		/>
	                    		</div>
                			</div>
               			</form>
            		</div>
            	</div>
        	</div>
    	</div>
	</div>
</div>

<div class="col-lg-12 col-md-12 col-sm-12">
	<div class="page-header border-b pt-2">
    	<h2 class="float-left">
        	Search Results 
      	</h2>
        <a id="export_to_excel_icon" title="" href="#" class="btn btn-primary btn-xs ml-2" onclick="onBtExport()" data-original-title="Export CSV">
        	<i class="fa fa-file-excel-o" aria-hidden="true"></i> Export
       	</a>   
        
        <a id="export_to_excel_icon" title="Save Preference" href="#" class="btn btn-primary btn-xs">
        	<i class="fa fa-save " aria-hidden="true"></i> Save Preference
        </a> 
        
      	<a id="reassign" title="Reassign" href="#" class="btn btn-primary btn-xs" onclick="getSelectedRows()">
        	<i class="fa fa-retweet " aria-hidden="true"></i> Reassign
       	</a>
             
        <div class="clearfix"></div>
        <div class="clearfix"></div>
  	</div>
    <div id="homegrid" style="width: 100%;" class="ag-theme-balham">
    	<script type="text/javascript" src="/ticket-portal-resource/js/services/activityhome.js"></script>
   	</div>
</div>

<div id="reassignModal" class="modal fade" role="dialog">
	<div class="modal-dialog" style="width:60%;">
    	<div class="modal-content">
        	<div class="modal-header">
            	<h4 class="modal-title">Assign To</h4>
                <button type="reset" class="close" data-dismiss="modal">&times;</button>
            </div>
            <div class="modal-body">
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
                 	</div>
            	</form>
        	</div>
    	</div>
	</div>
</div>

<script>
	$("#reassign").click(function () {
  		var workEffortId;
  		var rowdata = getSelectedRows();
        if(rowdata!=null && rowdata!=""){
        	if(rowdata.length==1){
            	rowdata.forEach(element => {
              		var csrloginId = element.primOwnerId;     
              		var emplTeamId = element.emplTeamId;                     
              		var businessUnitName = element.businessUnitName;
              		var businessUnitId = element.businessUnitId;
             
              		workEffortId=element.workEffortId;
                 
              		if (emplTeamId != "" && businessUnitId != "") {
                   		loaduserteam(emplTeamId , businessUnitId,workEffortId);
                    	$("#reassignModal").modal();
              		} 
           		});
      		}else{
            	$.notify({
                	message : '<p>Please select only one record from the list</p>',
              	});
         	}
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
	                    userOption += '<option value="'+category.userLoginId+'">'+category.partyName+'</option>';                     
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