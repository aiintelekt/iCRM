<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>

<div class="row">
	<div id="main" role="main" class="pd-btm-title-bar">
    	<input type='hidden' id="userlogin" name="userlogin" value="${userLogin.userLoginId?if_exists}"/>
        <@sectionFrameHeader title="${uiLabelMap.telesales}" />
    	<div class="col-lg-12 col-md-12 col-sm-12 profil-sec-padding pt-0">
    		<div id="accordion">
            	<div class="row">
                	<@arrowDownToggle />
                </div>
            <div class="border rounded bg-light margin-adj-accordian pad-top">
            	<form method="post" action="teleSalesForm" id="teleSalesForm" class="form-horizontal" name="teleSalesForm" novalidate="novalidate" data-toggle="validator">
                	<div class="row p-2">
                    	<div class="col-lg-3 col-md-6 col-sm-12">
                        	<#assign components = (Static["org.fio.admin.portal.util.DataHelper"].getDropDownOptions(marketingCampaignList, "marketingCampaignId","campaignName"))?if_exists />
                           	<@dropdownCell
                            	id="marketingCampaignName"
                              	placeholder="Select Campaign Name"
                              	options=components!
                              	value="${requestParameters.campaignName?if_exists}"
                           	/>
                            <@inputDate
                            	id="callBackDate"
                              	placeholder="Call Back Date"
                              	type="date"
                              	inputColSize="col-lg-8 col-md-6 col-sm-12"
                            />
                      	</div>
                      	<div class="col-lg-3 col-md-6 col-sm-12">
                        	<#assign componentList = (Static["org.ofbiz.entity.util.EntityQuery"].use(delegator).from("Enumeration").where("enumTypeId", "CALL_OUT_COME").queryList())?if_exists>
                           	<#assign components = (Static["org.fio.admin.portal.util.DataHelper"].getDropDownOptions(componentList, "enumId","description"))?if_exists />
                            <@dropdownCell
                            	id="callOutCome"
                                placeholder="Select Outcome"
                                options=components!
                                value="${requestParameters.callOutCome?if_exists}"
                            />
                            <@dropdownCell
                                id="totalCallsByCamp"
                                placeholder="Select # Attempts"
                                options=totalCallsByCamp!
                                value=totalCallsByCamp
                                allowEmpty=true
                            />
                      	</div>
                      	<div class="col-lg-3 col-md-6 col-sm-12" >
                        	<#assign componentList = (Static["org.ofbiz.entity.util.EntityQuery"].use(delegator).from("Enumeration").where("enumTypeId", "OPP_RESPONSE_TYPE").queryList())?if_exists>
                           	<#assign components = (Static["org.fio.admin.portal.util.DataHelper"].getDropDownOptions(componentList, "enumId","description"))?if_exists />
                       		<@dropdownCell
                            	id="responseType"
                                placeholder="Select Response Type"
                                options=components!
                                value="${requestParameters.responseType?if_exists}"
                                allowEmpty=true
                            />
	                      	<@inputCell
	                        	id="lastContactDays"
	                            type="text"
	                            name="lastContactDays"
	                            placeholder="Select # Days Since Last Call"
	                        />
                        </div>
                      	<div class="col-lg-3 col-md-6 col-sm-12">
                       		<@inputCell
                            	id="customerCIN"
                              	name="customerCIN"
                              	placeholder="CIN/CIF ID"
                              	type="text"
                            />
                      	</div>
                      	<div class="col-lg-12 col-md-12 col-sm-12" >
                        	<div class="text-right">
                        		<div class="form-check form-check-inline">
                              		<input id="statusNew" name="statusNew" type="checkbox" class="form-check-input" value="60024" checked>
                                		<label class="form-check-label">
                                      		New
                                		</label>
                       			</div>
	                        	<div class="form-check form-check-inline">
	                            	<input id="statusCallBack" name="statusCallBack" type="checkbox" class="form-check-input" value="60017">
	                                	<label class="form-check-label">
	                                    	CallBack
	                                	</label>
	                         	</div>
	                         	<div class="form-check form-check-inline">
	                            	<input id="statusOpen" name="statusOpen" type="checkbox" class="form-check-input" value="60043">
	                            		<label class="form-check-label ">
	                           				Open 
	                            		</label>
	                         	</div>
	                         	<div class="form-check form-check-inline">
	                            	<input id="statusPending" name="statusPending" type="checkbox" class="form-check-input" value="60028">
	                                	<label class="form-check-label">
	                                    	Pending
	                                	</label>
	                            </div>
	                            <div class="form-check form-check-inline">
	                            	<input id="statusWon" name="statusWon" type="checkbox" class="form-check-input" value="SOSTG_WON">
	                                	<label class="form-check-label">
	                                    	Won
	                                	</label>
	                            </div>
	                            <div class="form-check form-check-inline">
	                            	<input id="statusLost" name="statusLost" type="checkbox" class="form-check-input" value="SOSTG_LOST">
	                                	<label class="form-check-label">
	                                    	Lost
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
                       <div class="clearfix"></div>
                  	</div>
            	</form>
            </div>
         	</div>
      	</div>
    </div>
    <div class="col-lg-12 col-md-12 col-sm-12 mt-4">
    	<div class="row">
    		<div class="clearfix"></div>
            	<div class="col-xl-3 col-lg-6 col-md-10 col-sm-12">
                	<div class="small-box border rounded bg-o" ">
                    	<div class="inner float-left mr-4">
                       		<h3  id="myCall"></h3>
                   				<p class="mb-0">My Calls</p>
                    	</div>
                  		<div class="icon float-left">
                    		<i class="fa fa-user text-light"></i>
                  		</div>
                	</div>
             	</div>
              	<div class="col-xl-3 col-lg-6 col-md-10 col-sm-12">
                	<div class="small-box border rounded bg-s" >
                    	<div class="inner float-left mr-4">
                        	<h3  id="ownerTeamCount"></h3>
                   				<p class="mb-0">My Team's Calls</p>
                    	</div>
                    	<div class="icon float-left">
                        	<i class="fa fa-users text-light"></i>
                    	</div>
                	</div>
              	</div>
              	<div class="col-xl-3 col-lg-6 col-md-10 col-sm-12">
                	<div class="small-box border rounded bg-i" >
                  		<div class="inner float-left mr-4">
                        	<h3  id="ownerCountToday"></h3>
                   				<p class="mb-0">My Calls For Today</p>
                  		</div>
                  		<div class="icon float-left">
                    		<i class="fa fa-user text-light"></i>
                  		</div>
                 	</div>
              	</div>
              	<div class="col-xl-3 col-lg-6 col-md-6 col-sm-12">
                	<div class="small-box border rounded bg-w" >
                  		<div class="inner float-left mr-4">
                  			<h3  id="teamCountToday"></h3>
                   				<p class="mb-0">My Team's Calls Today</p>
                  		</div>
                  		<div class="icon float-left">
                    		<i class="fa fa-users text-light"></i>
                  		</div>
                 	</div>
              	</div>
        </div>
    </div>
    <div class="col-lg-12 col-md-12 col-sm-12 profil-sec-padding pt-0">
    <div class="clearfix"></div>
    	<div class="page-header border-b pt-2">
        	<h2 class="float-left">My Calls <a class="text-dark" href="" id="dropdown05" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false"><i class="fa fa-angle-down" aria-hidden="true"></i></a>
            	<div class="dropdown-menu" aria-labelledby="dropdown05">
		            <div class="clearfix"></div>
		            	<@headerH4
		            		title="System Views"
		                />
		                <a id="MyOpenActivities"  class="dropdown-item" href="#" >My Open Activities </a>                                 
		                <a id="MyTeamsActivities" class="dropdown-item" href="#" >My Team's Activities </a>
	            </div>
            </h2>
           	<a id="csr_icon" title="Add Call" href="/sales-portal/control/addPhoneCall" class="text-dark"><i class="fa fa-plus fa-1 right-icones ml-2" aria-hidden="true" style="font-size: 18px;"></i></a><a id="export_to_excel_icon" title="Export to Csv" href="#" class="btn btn-primary btn-xs" onclick="onBtExport()"><i class="fa fa-file-excel-o" aria-hidden="true"></i> Export</a>   <a id="export_to_excel_icon" title="Save Preference" href="#" class="btn btn-primary btn-xs"><i class="fa fa-save " aria-hidden="true"></i> Save Preference</a> 
            <a href="" id="reassign" aria-hidden="true" class="btn btn-primary btn-xs" data-toggle="modal"><i class="fa fa-retweet" aria-hidden="true"></i> Reassign</a>
            <div class="clearfix"></div>
        </div>
        <div class="table-responsive">
        	<div id="myCellGrid" style="height:380px; width: 100%;" class="ag-theme-balham"></div>
            <script type="text/javascript" src="/sales-portal-resource/js/ag-grid/my-calls.js"></script>
        </div>
    </div>
    </div>
</div>
                
<div id="reassignModal" class="modal fade" role="dialog">
	<div class="modal-dialog modal-md">
    	<div class="modal-content">
        	<div class="modal-header">
            	<h4 class="modal-title">Assign To</h4>
                <button type="button" class="close" data-dismiss="modal" id="close">&times;</button>
            </div>
            <div class="modal-body">
            	<form method="post" action="#" id="reassignModal" class="form-horizontal" name="" novalidate="novalidate" data-toggle="validator">
                	<div class="row p-1">
                   		<div class="col-md-12 col-lg-12 col-sm-12 ">
                    		<input type='hidden' id="workEffortId" name="workEffortId" />
                     			User<input type="radio" id="user" name="emp" value="user">
                     			Team<input type="radio" id="team" name="emp" value="team">
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
                    	<@submit class="btn btn-sm btn-primary navbar-dark" id="saveModal"  label="${uiLabelMap.Save}"/>  
                        <button type="button" class="btn btn-default btn-primary navbar-dark" id="btnclose" data-dismiss="modal">Close</button>
                        <@reset id="reset" label="${uiLabelMap.Reset}" />
                    </div>    
                </form>
            </div>
        </div>
    </div>
</div>
                     
<script>    
	$(document).ready(function() {
    	$("#doSearch").click(function(event) {
        	event.preventDefault();
            loadAgGridMyCell();
        });
    });
    $("#reassign").click(function () {
		var workEffortId;
		var salesOppId;
        var rowdata = getSelectedRows();
        if(rowdata!=null && rowdata!=""){
        	rowdata.forEach(element => {
	            var emplTeamId = element.emplTeamId;                           
	            var businessUnitName = element.businessUnitName;
	            var businessUnitId = element.businessUnitId;
	            workEffortId=element.workEffortId;
	            salesOppId = element.salesOpportunityId;
	            document.getElementById("workEffortId").value = workEffortId;
	            $("#reassignModal").modal();
	            $("input[type='reset']").hide();     
	            if (emplTeamId != ""&& businessUnitId != "") {
	      			loaduserteam(emplTeamId , businessUnitId,workEffortId,salesOppId);
	 			}   
 			}); 
 		} else {
              $.notify({
                message : '<p>Please select one record in the list</p>',
              });
        }
    });
 			  
 			  
	function loaduserteam(emplTeamId,businessUnitId,workEffortId,salesOppId) {
    	var nonSelectContent;
        var oppoResponseReasonOptions;        
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
        	 		oppoResponseReasonOptions = '<option value="" data-content="'+nonSelectContent+'" selected="">Please Select</option>';        
        	 		teamOptions = '<option value="" data-content="'+nonSelectContent+'" selected="">Please Select</option>';
           
                   	var sourceDesc = data.results;
                   	for (var i = 0; i < data.length; i++) {
                    	var category = data[i];
                      	oppoResponseReasonOptions += '<option value="'+category.partyId+'">'+category.partyName+'</option>';                      
                      	teamOptions += '<option value="'+category.emplTeamId+'">'+category.teamName+'</option>';
                    }
            	}
        	});
        
          $("#userText").html(oppoResponseReasonOptions);
          $("#teamText").html(teamOptions);
       
    	});
              
       	document.getElementById('saveModal').onclick = () => {
       		var csr=  $('#userText').val();         
       		var emp=  $('#teamText').val();
       		var dataSets =  { "salesOppId": salesOppId , "ownerUserLoginId": csr, "emplTeamId":emp };
        	$.ajax({
	            type: "POST",
	            url: "UpdateReasignForOpportunity",
	            data:dataSets,
	            async: false,
	            success: function(data) {
	            	$("#reassignModal").modal('hide');   
	               	$.notify({
	                	message : '<p>Reassigned Successfully</p>',
	              	});
              		loadAgGridMyCell()  
           		 }
        	});
       	}
	}
</script>


