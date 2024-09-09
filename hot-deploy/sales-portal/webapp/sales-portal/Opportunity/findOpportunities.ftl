<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>

<div id="reassignModal" class="modal fade" role="dialog">
	<div class="modal-dialog" style="width:60%;">
    	<div class="modal-content">
        	<div class="modal-header">
            	<h4 class="modal-title">Assign To</h4>
                <button type="button" class="close" data-dismiss="modal" id="close">&times;</button>
            </div>
            <div class="modal-body">
            	<input type='hidden' id="salesOppId" name="salesOppId" />
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

 <div class="row">
 	<div id="main" role="main" style="padding-bottom: 0px;">
		<@sectionFrameHeader title="${uiLabelMap.FindOpportunities!}" />
	    	<div class="col-lg-12 col-md-12 col-sm-12 profil-sec-padding pt-0">
	       		<div id="accordion">
                	<div class="row">
                    	<@arrowDownToggle />
                	</div>
                	<div>
                	<div class="card-header margin-adj-accordian pad-top">
                        <form method="post" action="findOpportunity" name="findOpportunity" id="findOpportunity" data-toggle="validator">
                                <div class="row p-2">
                                    <div class="col-lg-4 col-md-6 col-sm-12">
                        				<@dropdownCell
					                    	id="marketingCampaignId"
					                    	required=false
					                    	value="${requestParameters.marketingCampaignId?if_exists}"
					                    	allowEmpty=false
					                    	placeholder="Select Campaign Name"
					                    	glyphiconClass="fa fa-user-o"
	                    				/>  
                                        <@inputRowAddOn2
				                        	id="salesOpportunityId"
				                            name="salesOpportunityId"
				                            placeholder="Opportunity Number"
				                            required=false
				                            addOnTarget="findOpportunity"
				                            glyphiconClass="glyphicon-user"
				                            inputColSize="col-sm-12"
                              			/>
                                        <@inputRowAddOn2
			                               	id="salesEmailAddress"
			                               	name="salesEmailAddress"
			                               	placeholder="Email"
			                               	required=false
			                               	addOnTarget="findOpportunity"
			                               	glyphiconClass="glyphicon-envelope"
			                               	inputColSize="col-sm-12"
                               			/>
                                        <#assign usersList = delegator.findByAnd("UserLoginPerson", null, null, false)>
                      					<#assign usersOptionList = Static["org.fio.admin.portal.util.DataHelper"].getDropDownOptions(usersList, "userLoginId","firstName")?if_exists />
                        				<@dropdownCell
					                        id="assignedUserLoginId"  
					                        placeholder="Owner"      
					                        allowEmpty=true  
					                        options=usersOptionList!        
					                        value="${requestParameters.assignedUserLoginId?if_exists}"     
                        				/> 
                              		</div>
                              		<div class="col-lg-4 col-md-6 col-sm-12">
                              			<#assign callOutcomeData = Static["org.fio.admin.portal.util.EnumUtil"].getEnums(delegator, "CALL_OUT_COME")?if_exists />
                   						<#assign callOutcomeList = Static["org.fio.admin.portal.util.DataHelper"].getDropDownOptions(callOutcomeData, "enumId","description")?if_exists /> 
                                   
							            <@dropdownCell
							            	id="callOutCome"
							              	name="callOutCome"
							              	options=callOutcomeList!
							              	label=""
							              	value=""
							              	allowEmpty=true
							              	placeholder="Select CallOutcome"
							            />
                                        <@inputRowAddOn2
					                       id="opportunityName"
					                       name="opportunityName"
					                       placeholder="Name"
					                       required=false
					                       addOnTarget="findOpportunity"
					                       glyphiconClass="glyphicon-user"
					                       inputColSize="col-sm-12"
			                       		/>
                                        <@inputRowAddOn2
			                               	id="salesPhone"
			                               	name="salesPhone"
			                               	placeholder="Phone"
			                               	required=false
			                               	addOnTarget="findOpportunity"
			                               	glyphiconClass="glyphicon-earphone"
			                               	inputColSize="col-sm-12"
                              			/>
                              		</div>
                              		<div class="col-lg-4 col-md-6 col-sm-12">
							            <@dropdownCell 
							            	id="responseTypeId"              
							               	value=""
							              	label=""              
							              	placeholder="Select Response Type"
							              	allowEmpty=true
							            />
                                        <@inputRowAddOn2
			                            	id="opportunitychname"
			                               	name="opportunitychname"
			                               	placeholder="Chinese Name"
			                               	required=false
			                               	addOnTarget="findOpportunity"
			                               	glyphiconClass="glyphicon-user"
			                               	inputColSize="col-sm-12"
                               			/>    
                                 		<#assign componentList = (Static["org.ofbiz.entity.util.EntityQuery"].use(delegator).from("Enumeration").where("enumTypeId", "SALES_CHANNEL_ID").queryList())?if_exists>
                                 		<#assign components = (Static["org.fio.admin.portal.util.DataHelper"].getDropDownOptions(componentList, "enumId","description"))?if_exists />
                                 		<@dropdownCell
                                        	id="salesChannelId"
                                            placeholder="Select Channel"
                                            allowEmpty=true
                                            options=components!
                                            value="${requestParameters.description?if_exists}" 
                                        />
                                	</div>
                                </div><#-- End row p-2-->
                                
                                <div class="row p-2">
	                                <div class="col-lg-12 col-md-12 col-sm-12">
										<div class="text-right pd-cbx-lbls" style="padding-top: 0px;">
										 	<div class="form-check form-check-inline">
		                          				<input class="form-check-input" name="statusOpen" id="statusOpen" value="SOSTG_OPEN" checked="" type="checkbox">
		                          				<label class="form-check-label">Open</label>
		                        			</div>
					                        <div class="form-check form-check-inline">
					                        	<input class="form-check-input" name="statusClosed" id="statusClosed" value="SOSTG_CLOSED" type="checkbox">
					                          	<label class="form-check-label" for="inlineRadio1">Closed</label>
					                        </div>
											<div class="form-check form-check-inline">
			                          			<input class="form-check-input" name="statusWon" id="statusWon" value="SOSTG_WON" type="checkbox">
			                          			<label class="form-check-label">Won</label>
			                        		</div>
											<div class="form-check form-check-inline">
					                        	<input class="form-check-input" name="statusNew" id="statusNew" value="SOSTG_NEW" type="checkbox">
					                          	<label class="form-check-label">New</label>
					                        </div>
											<div class="form-check form-check-inline">
					                        	<input class="form-check-input" name="statusLost" id="statusLost" value="SOSTG_LOST" type="checkbox">
					                          	<label class="form-check-label">Lost</label>
					                        </div>						
											<div class="form-check form-check-inline">
					                        	<input class="form-check-input" name="statusProgress" id="statusProgress" value="IN_PROGRESS" type="checkbox">
					                          	<label class="form-check-label">In Progress</label>
					                        </div>
					                        <div class="form-check form-check-inline">
					                        	<input class="form-check-input" name="statusContact" id="statusContact" value="SOSTG_CONTACT" type="checkbox">
					                          	<label class="form-check-label">Contacted</label>
					                        </div>
					                         <div class="form-check form-check-inline">
					                         	<input class="form-check-input" name="statusNotContact" id="statusNotContact" value="SOSTG_NOT_CONTACT" type="checkbox">
					                          	<label class="form-check-label">Not Contacted</label>
					                        </div>
		                                    <@button 
		                                		label="${uiLabelMap.Search}"
		                                		id="doSearch"
		                                	/>
		                               		<@reset
		                            			label="${uiLabelMap.Reset}"
		                            		/>
	                                	</div>
	                            	</div>
                            	</div><#-- End row p-2-->
                        </form>
                    </div>
                	</div>
            	</div> 
            
            	<div class="clearfix"></div>
            	<div class="page-header border-b pt-2">
                	<@headerH2 title="${uiLabelMap.searchResults}" class="float-left"/>
            		<a id="export_to_excel_icon" title="Export to CSV" href="#" class="btn btn-primary btn-xs" onclick="onBtExport()"><i class="fa fa-file-excel-o" aria-hidden="true"></i> Export</a>
            		<a id="export_to_excel_icon" title="Save Preference" href="#" class="btn btn-primary btn-xs"><i class="fa fa-save " aria-hidden="true"></i> Save Preference</a>
     				<a id="reassign" title="Reassign" href="#" class="btn btn-primary btn-xs" onclick="getSelectedRows()">
						<i class="fa fa-retweet " aria-hidden="true"></i> Reassign
					</a>
             		<div class="clearfix"></div>
           		</div>
            	<div class="clearfix"></div>
            	<div class="table-responsive">
               		<div id="opportunityGrid" style="width: 100%;" class="ag-theme-balham"></div>
               		<script type="text/javascript" src="/sales-portal-resource/find-opportunity.js"></script>
            	</div>
            	<div class="clearfix"></div>
        	</div>
    </div>
 </div> 
      
 <script>     
	
	$(document).ready(function() {
		$("#doSearch").click(function(event) {
	    event.preventDefault(); 
	    loadAgGrid();
	});
	loadOriginatingCampaigns();	
	});
	
	$("#callOutCome").change(function() {
   		var enumId  = $("#callOutCome").val();
   		$(".responseTypeId .clear").click();
    	if (enumId != "") {
        	loadResponseType(enumId);
    	}
	});
	
	 function loadResponseType(parentEnumId) {
      
        var nonSelectContent = "<span class='nonselect'>Please Select ResponseType</span>";
        var responseTypeOptions = '<option value="" data-content="'+nonSelectContent+'" selected="">Please Select</option>';
      
        $.ajax({
            type: "POST",
            url: "getOpportunityResponseType",
            data: { "enumId": parentEnumId },
            async: false,
            success: function(data) {
                   var sourceDesc = data.results;
                   for (var i = 0; i < data.length; i++) {
                        var responseType = data[i];
                        responseTypeOptions += '<option value="'+responseType.enumId+'">'+responseType.description+'</option>';
                    }
            }
        });
        $("#responseTypeId").html(responseTypeOptions);
	}

	function loadOriginatingCampaigns() {
        var nonSelectContent = "<span class='nonselect'>Please Select</span>";
        var campaignOptions = '<option value="" data-content="'+nonSelectContent+'" selected="">Please Select</option>';
        $.ajax({
            type: "POST",
            url: "findOppCampaignListsAjax",
            data: "data",
            async: false,
            success: function(data) {
            	var campaignDetails = data.data;
                for (var i = 0; i < campaignDetails.length; i++) {
                	var eachCampaign = campaignDetails[i];
                	var campaignName = eachCampaign.campaignName;
                	if(campaignName != null && campaignName != undefined && campaignName != ""){
                    	campaignOptions += '<option value="'+eachCampaign.campaignId+'">' + eachCampaign.campaignName + '(' +eachCampaign.campaignId + ')</option>';
                    }
                }
            }
        });
        $("#marketingCampaignId").html(campaignOptions);
	}
	
	var salesOppId;
	$("#reassign").click(function () {              
		var rowdata = getSelectedRows();  
	  	if(rowdata!=null && rowdata!=""){
	    	rowdata.forEach(element => {
	      		var assignedUserLoginId = element.ownerId;     
	      		var emplTeamId = element.emplTeamId;                     
	      		var ownerBuId = element.businessUnitId;
	      		salesOppId=element.salesOpportunityId;   
	     		document.getElementById("salesOppId").value = salesOppId;      
	      		if (emplTeamId != "" && ownerBuId != "") {
	           		loaduserteam(emplTeamId , ownerBuId, salesOppId);
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
	
	function loaduserteam(emplTeamId,ownerBuId,salesOppId) {
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
	         	dataSet =  { "emplTeamId": "" , "businessUnitId": ownerBuId };
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
	}
	
	document.getElementById('saveModal').onclick = () => {
		var assignedUserLoginId=  $('#userText').val();  
	   	var emp=  $('#teamText').val();            
	   	var dataSets =  { "salesOppId": salesOppId , "ownerUserLoginId": assignedUserLoginId, "emplTeamId":emp };
	  	if((assignedUserLoginId!="" && assignedUserLoginId!=null && assignedUserLoginId!=undefined)||(emp!="" && emp!=undefined && emp!=null)){
	  		$.ajax({
	   			type: "POST",
	        	url: "UpdateReasignForOpportunity",
	        	data:dataSets,
	        	async: false,
	        	success: function(data) {
	        		$("#reassignModal").modal('hide');   
	        		$.notify({
	            		message : '<p>Reassigned Successfully for</p>'+salesOppId,
	        		});
	        	}
	    	});
	  	}
	}
	
 </script>        
                              
