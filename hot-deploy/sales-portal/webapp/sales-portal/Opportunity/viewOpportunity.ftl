<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<div class="row">
	<div id="main" role="main">
    	<div class="col-lg-12 col-md-12 col-sm-12">
	    	<input type="hidden" id="salesOppId" name="salesOppId" value="${requestParameters.salesOpportunityId?if_exists}"/>
	        <#assign emplTeamId = "">
	        <#assign businessUnit = "">
	        <#assign salesOppId = "${requestParameters.salesOpportunityId?if_exists}">
	        <#if salesOppId?has_content>
	        	<#assign roleList = (Static["org.ofbiz.entity.util.EntityQuery"].use(delegator).select("ownerBu","emplTeamId","ownerId").from("SalesOpportunityRole").where("salesOpportunityId",salesOppId).queryFirst())?if_exists />
	 			<#if roleList?has_content>
	 				<#assign emplTeamId = "${roleList.emplTeamId?if_exists}"> 
	 				<#assign businessUnit = "${roleList.ownerBu?if_exists}"> 
	 			</#if>
	        </#if>
	        <div class="row">
	        	<div class="col-lg-12 col-md-12 col-sm-12">
	            	<#assign oppSummaryDetails = (Static["org.ofbiz.entity.util.EntityQuery"].use(delegator).select("seqId").from("UserLoginHistory").where("entity","SalesOpportunitySummary","userLoginId",userLogin.userLoginId).maxRows(5).orderBy("-fromDate").distinct().queryList())?if_exists />
					<#assign salesOpportunityIds = Static["org.ofbiz.entity.util.EntityUtil"].getFieldListFromEntityList(oppSummaryDetails, "seqId", true)>
	                <#assign extraLeft=''/>
	                <div class="text-left float-left"><h3 class="float-left">Recently Viewed: </h3>
		                <#list salesOpportunityIds as salesOpportunityId>
		                	<a href="<@ofbizUrl>viewOpportunity?salesOpportunityId=${salesOpportunityId!}&seqId=${salesOpportunityId!}&entity=SalesOpportunitySummary</@ofbizUrl>" class="btn btn-xs btn-primary">${salesOpportunityId!}</a>
		                </#list>
	                </div>
	            </div>
	            <div class="text-right position-absolute" style="right:20px;">
	            	 <#-- <a title="Reassign" href="#" id="reassign" class="btn btn-primary btn-xs mt-1"><i class="fa fa-retweet" aria-hidden="true"></i>Reassign</a>
	                <a title="Save" href="#" id="doSave" class="btn btn-primary btn-xs mt-1"><i class="fa fa-save" aria-hidden="true"></i>Save</a> -->
	                 <button type="button" class="btn btn-primary btn-xs mt-1" id="reassign"><i class="fa fa-save" aria-hidden="true"></i>Reassign</button>
	               <button type="button" class="btn btn-primary btn-xs mt-1" id="doSave"><i class="fa fa-save" aria-hidden="true"></i>Save</button>
	             
	                <a href="findOpportunity" class="btn btn-primary btn-xs mt-1"><i class="fa fa-chevron-circle-left" aria-hidden="true"></i> Back</a>
	            </div>
	        </div>
	    </div>
	    <div class="col-lg-12 col-md-12 col-sm-12 mid">
	    	<div class="card-head margin-adj mt-0">
	        	${screens.render("component://sales-portal/widget/opportunity/OpportunityScreens.xml#cusNameCommon")}
	        </div>
	        <div class="clearfix"></div>
	        <div class="card-head margin-adj mt-2">
	        	<div class="row">
	            	<div class="col-md-6 col-lg-6">
	               		<div class="bd-callout float-left" >
	                 		<h6>Opportunity : Information</h6>
	                 		<@headerH3
	                 			id="salesId"
	                 			title=""
	                 		/>
	              		</div>
	             		<div class="bd-callout float-left">
	               			<small>Campaign Code</small>
	                 		<@headerH5
	          					id="campaignCode"
	           					title=""/>
	             		</div>
	             		<div class="bd-callout float-left">
	               			<small>Campaign Name</small>
	                  		<@headerH5
	          				id="campaignName"
	           				title=""/>
	             		</div>
	             		<div class="bd-callout float-left">
	              			<small>Campaign End</small>
	               			<@headerH5
	          					id="endDate"
	           					title=""/>
	             		</div>
	          	</div>
	          	<div class="col-md-6 col-lg-6 right-details">
	           		<div class="bd-callout" style="width:130px;">
	            		<#assign roleEnum = Static["org.ofbiz.entity.util.EntityQuery"].use(delegator).select("opportunityStageId","description").from("SalesOpportunityStage").queryList()?if_exists />    
	                   	<#assign roleTypeList = Static["org.fio.admin.portal.util.DataUtil"].getMapFromGeneric(roleEnum,"opportunityStageId","description",false)?if_exists />    
	            		<small>Opportunity Status</small>
	            		<@dropdownCell
	              			id="opportunityStageId"<#-- opportunityStatusId -->
	              	 		options=roleTypeList!
	               			value=""
	               			readonly=true
	              			label=""
	              			placeholder=""
	              		/>
	          		</div>              
	         		<#assign statuses = delegator.findByAnd("Enumeration", {"enumTypeId" : "RESPONSE_REASON_ID"}, null, false)>
	         		<#assign statusList = Static["org.fio.admin.portal.util.DataHelper"].getDropDownOptions(statuses, "enumId","description")?if_exists />                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                 
	            	<div class="bd-callout" style="width:130px;">
	            		<small>Response Reason </small>
	            		<@dropdownCell
	              			id="responseReason"  
	              			value=""
	              			options=statusList!
	              			label=""
	              			allowEmpty= true
	              			placeholder=""
	              		/>	
	         		</div>                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                       
	                <#assign statuses = delegator.findByAnd("Enumeration", {"enumTypeId" : "OPP_RESPONSE_TYPE"}, null, false)>
	        		<#assign statusList = Static["org.fio.admin.portal.util.DataHelper"].getDropDownOptions(statuses, "enumId","description")?if_exists />                                                                                                                                                                                                                                                                                                                                                                                                                                                                                            
	            	<div class="bd-callout" style="width:130px;">
	            		<small>Response Type</small>
	            		<@dropdownCell 
	              			id="responseTypeId"              
	               			value=""
	              			label=""              
		          			options=statusList!
	              			placeholder=""
	              			allowEmpty= true
	              		/>
	          		</div>                                                                                                       
	          
	           		<div class="bd-callout" style="width:130px;">
	           			<#assign status = Static["org.fio.admin.portal.util.EnumUtil"].getEnums(delegator, "CALL_OUT_COME")?if_exists />
	                   	<#assign statusList1 = Static["org.fio.admin.portal.util.DataHelper"].getDropDownOptions(status, "enumId","description")?if_exists /> 
	            		<small data-toggle="modal" data-target="#Notinterested">Call Outcome</small>
	            		<@dropdownCell
	              			id="callOutCome"
	              			name="callOutCome"
				            options=statusList1!
				            label=""
				            value=""
				            placeholder=""
				            allowEmpty= true
	              		/>
	             	</div>
	              	<div class="bd-callout" style="width:357px;">
	               		<small># Days since last call</small>
	               		<@headerH5  id="lastCall" title="" />           
	               	</div>
	                <div class="bd-callout" >
	                	<small>Call Back Date</small>
	                   	<@inputDate
	                   		id="callBackDate"
	                   		value=""
	                  	/>
	               	</div>
	            </div>
	            </div>
	        </div> 
	        <div class="card-head margin-adj mt-2" id="cp">
	          ${screens.render("component://sales-portal/widget/opportunity/OpportunityScreens.xml#customerRowAddOpportunity")}
	        </div>  
	      	<ul class="nav nav-tabs mt-2">
		      	<li class="nav-item"><a data-toggle="tab" class="nav-link" id="detailsId" href="#details">Details </a></li>
		      	<li class="nav-item"><a data-toggle="tab" class="nav-link" id="curRepId" href="#curRep">Customer Response </a></li>
		      	<li class="nav-item"><a data-toggle="tab" class="nav-link" id="stpId" href="#stp">Straight Through Processing </a></li>
		      	<li class="nav-item"><a data-toggle="tab" class="nav-link" id="activityId" href="#activity">Activities </a></li> 
		      	<li class="nav-item"><a data-toggle="tab" class="nav-link" id="offerId" href="#offer">Offers </a></li>
		      	<li class="nav-item"><a data-toggle="tab" class="nav-link" id="noteId" href="#notes">Notes and Attachment </a></li> 
		      	<li class="nav-item"><a data-toggle="tab" class="nav-link" id="reloppId" href="#relopp">Related Opportunities </a></li>
		      	<li class="nav-item"><a data-toggle="tab" class="nav-link" id="admId" href="#adm">Administration </a></li>
	       	</ul>
	       
	     	<div class="tab-content">
	      
	       		<div id="details" class="tab-pane fade">
	        		${screens.render("component://sales-portal/widget/opportunity/OpportunityScreens.xml#opportunityDetails")}
	        	</div>
	        
	        	<div id="curRep" class="tab-pane fade in">
	        		${screens.render("component://sales-portal/widget/opportunity/OpportunityScreens.xml#customerResponse")}
	        	</div>
	        
	         	<div id="stp" class="tab-pane fade in">
	        		${screens.render("component://sales-portal/widget/opportunity/OpportunityScreens.xml#straightThroughProcessing")}
	        	</div>
	        
	        	<div id="activity" class="tab-pane fade in">
	        		${screens.render("component://sales-portal/widget/opportunity/OpportunityScreens.xml#tabActivity")}
	        	</div>
	        
	         	<div id="offer" class="tab-pane fade in">
	        		${screens.render("component://sales-portal/widget/opportunity/OpportunityScreens.xml#offer")}
	        	</div>
	        
	         	<div id="notes" class="tab-pane fade in">
	        		${screens.render("component://sales-portal/widget/opportunity/OpportunityScreens.xml#notesAndAttachment")}
	        	</div>
	        
	         	<div id="relopp" class="tab-pane fade in">
	        		${screens.render("component://sales-portal/widget/opportunity/OpportunityScreens.xml#relatedOpportunities")}
	        	</div>
	        
	         	<div id="adm" class="tab-pane fade in">
	        		${screens.render("component://sales-portal/widget/opportunity/OpportunityScreens.xml#administration")}
	        	</div>
	       	</div>
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
							<input type='hidden' id="custRequestId" name="custRequestId" />
                     			User
							<input type="radio" id="user" name="emp" value="user">
                     			Team
							<input type="radio" id="team" name="emp" value="team">
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
		
		$('#reassignModal #close').click(function(){
			$('#reassignModal input[type=reset]').click();
		});
		$('#reassignModal #btnclose').click(function(){
			$('#reassignModal input[type=reset]').click();
		});
	
		$("#userText_error").empty();
		$("#doSave").click(function(event) {
	    	event.preventDefault(); 
	   		savedetails();
		});

		function savedetails(){
			var responseTypeId = $('#responseTypeId').val();
			var opportunityStageId = $('#opportunityStageId').val();
			var callOutCome = $('#callOutCome').val();
			var responseReasonId = $('#responseReason').val();
			var salesOppId = $('#salesOppId').val();
			var callBackDate=$('#callBackDate').val();
	
			$.ajax({
				url:'updateSalesOpportunityDetails',
				data:{"salesOpportunityId":salesOppId,"responseReasonId":responseReasonId,"callOutcome":callOutCome,"opportunityStageId":opportunityStageId,"responseTypeId":responseTypeId,"callBackDate":callBackDate},
				type:"post",
				success:function(data){
		
					$.notify({
	                	message : '<p>Saved Successfully</p>',
	            	});
	            	 location.reload();
			}
		}); 
		
	}

	document.getElementById("detailsId").click();

 	$("#callOutCome").change(function() {
   		var enumId  = $("#callOutCome").val();
  		$(".responseTypeId .clear").click();
  		$(".responseReason .clear").click();
    	if (enumId != "") {
        	loadResponseType(enumId);
    	}
	});

	$('.nav-tabs a[href="#curRep"]').click(function(){
		emailGrid();
	});
	$('.nav-tabs a[href="#activity"]').click(function(){
   		loadActivityGrid();
	});
	$('.nav-tabs a[href="#notes"]').click(function(){
		noteAttachGrid();
	});
	$('.nav-tabs a[href="#relopp"]').click(function(){
		loadRelatedOpportunityGrid();
	});

	$("#responseTypeId").change(function() {
   		var responseTypeId  = $("#responseTypeId").val();
   		var callOutCome  = $("#callOutCome").val();
    	$(".responseReason .clear").click();
   		if (responseTypeId != ""&& callOutCome != "") {
       		loadoppoResponseReasonId(callOutCome , responseTypeId);
   		}else{
   	  		$("#responseReason").html('');
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



	function loadoppoResponseReasonId(oppocallOutCome,responseTypeId) {
    	var nonSelectContent = "<span class='nonselect'>Please Select</span>";
        var oppoResponseReasonOptions = '<option value="" data-content="'+nonSelectContent+'" selected="">Please Select</option>';
        $.ajax({
            type: "POST",
            url: "getOpportunityResponseReason",
            data: { "oppocallOutCome": oppocallOutCome , "responseTypeId": responseTypeId },
            async: false,
            success: function(data) {
                   var sourceDesc = data.results;
                   for (var i = 0; i < data.length; i++) {
                        var category = data[i];
                      oppoResponseReasonOptions += '<option value="'+category.enumId+'">'+category.description+'</option>';
                    }
            }
        });
       
        $("#responseReason").html(oppoResponseReasonOptions);
	}

	loaddata();

	function loaddata(){
		var salesOppId = $('#salesOppId').val();
		$.ajax({
			url:'getviewopp',
			data:{"salesOppId":salesOppId},
			type:"post",
			success:function(data){
				var salesOpportunityId = data[0].salesOpportunityId;
				document.getElementById("salesId").innerHTML = salesOpportunityId; 
				var campaignCode = data[0].campaignCode;
				document.getElementById("campaignCode").innerHTML = campaignCode; 
				var endDate = data[0].endDate;
				document.getElementById("endDate").innerHTML = endDate; 
				var campaignName = data[0].campaignName;
				document.getElementById("campaignName").innerHTML = campaignName; 
				var callBackDate = data[0].callBackDate;
	  			$("#callBackDate").val(callBackDate);
	  			var lastCall=data[0].lastCall;
	  	 		if(lastCall!=null){
	  	 			document.getElementById("lastCall").innerHTML = lastCall; 
	   			}
	  			var opportunityStageId=data[0].opportunityStageId;
	    	 	if(opportunityStageId!=null){
	  	 			$(".opportunityStageId [data-value='" + opportunityStageId +"']").click();
	   			}
				var callOutCome = data[0].callOutCome;
				if(callOutCome!=null){
	  	 			$(".callOutCome [data-value='" + callOutCome +"']").click();
	   			}
				var responseTypeId = data[0].responseTypeId;
		 		if(responseTypeId!=null){
	  	 			$(".responseTypeId [data-value='" + responseTypeId +"']").click();
	   			}
				var responseReason = data[0].responseReasonId;
		 		if(responseReason!=null){
	  	 			$(".responseReason [data-value='" + responseReason +"']").click();
	   			}
	   			if(opportunityStageId == "SOSTG_CLOSED"){
	   				$("#callOutCome").empty();
	   				$("#responseTypeId").empty();
	   				$("#responseReason").empty();
	   				$("#opportunityStageId").empty();
	   				document.getElementById('callBackDate').setAttribute('disabled', true);
	   				
	   				document.getElementById('reassign').setAttribute('disabled', true);
	   				 document.getElementById('doSave').setAttribute('disabled', true);
	   				 
	   			}
			}
		}); 
	}	
});

	$("#reassign").click(function () {
		var salesOppId;
        var csrloginId ='${userLogin.userLoginId!}';              
        var emplTeamId = '${emplTeamId!}';                           
        var businessUnitId ='${businessUnit!}';
        salesOppId=$("#salesOppId").val();
        if (emplTeamId != ""&& businessUnitId != "") {
      		loaduserteam(emplTeamId , businessUnitId,salesOppId);
 		}  
 		$("#reassignModal").modal();
 		$("input[type='reset']").hide();
    });
    
    function loaduserteam(emplTeamId,businessUnitId,salesOppId) {
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
                      userOption += '<option value="'+category.userLoginId+'">'+category.partyName+'</option>';                     
                      teamOptions += '<option value="'+category.emplTeamId+'">'+category.teamName+'</option>';
            }
            }
          });
       
          $("#userText").html(userOption);
          $("#teamText").html(teamOptions);
       		});  
        }
        
        document.getElementById('saveModal').onclick = () => {
	       var ownerId=  $('#userText').val();         
	       var emplTeamId=  $('#teamText').val();    
	       var salesOppId = $('#salesOppId').val();
	       var dataSets =  { "salesOppId": salesOppId , "ownerUserLoginId": ownerId, "emplTeamId":emplTeamId };
	       if((ownerId!="" && ownerId!=null && ownerId!=undefined)||(emplTeamId!="" && emplTeamId!=undefined && emplTeamId!=null)){
	        $("#userText_error").empty();
	       	$.ajax({
	            type: "POST",
	            url: "UpdateReasignForViewOpportunity",
	            data:dataSets,
	            async: false,
	            success: function(data) {
	              $("#reassignModal").modal('hide');   
	               $.notify({
	                message : '<p>Reassigned Successfully</p>',
	              });
	            },error: function(data) {
			        	result=data;
						console.log('Error occured');
						showAlert("error", "Error occured while fetching Tiles Data!");
					}
	        });
	       }else{
	      	 	$("#userText_error").append('<ul class="list-unstyled"><li>Please Select a Value</li></ul>');
	        }
       }	

</script>
          
          