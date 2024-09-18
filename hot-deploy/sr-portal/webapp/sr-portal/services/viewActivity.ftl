<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<script src="/bootstrap/js/ckeditor/ckeditor.js" type="text/javascript"></script>
<script src="/bootstrap/js/ckeditor/ck-custom-functions.js" type="text/javascript"></script>
<#if inputContext.workEffortTypeId =="62821">
<script type="text/javascript" src="/account-portal-resource/js/emailActivity.js"></script>
<#else>
<script type="text/javascript" src="/sr-portal-resource/js/services/addSrActivity.js"></script>
</#if>
<div class="top-band bg-white mb-0">
	<div class="col-lg-12 col-md-12 col-sm-12">
    	<div class="row">
        	<marquee behavior="scroll" direction="left" class="text-danger">"System maintenance scheduled for 03-09-2019 from 8 AM SGT to 10 AM SGT. During this time, users may experience unavailability of services"</marquee>
      	</div>
 	</div>
</div>
<div class="row">
	<input type='hidden' id="workEffortId" name="workEffortId" value="${requestParameters.workEffortId?if_exists}" />
    <input type='hidden' id="primOwnerId" name="primOwnerId" value="${requestParameters.primOwnerId?if_exists}" />
    <input type='hidden' id="emplTeamId" name="workEffortId" value="${requestParameters.emplTeamId?if_exists}" />
    <input type='hidden' id="businessUnitName" name="businessUnitName" value="${requestParameters.businessUnitName?if_exists}" />
    <input type='hidden' id="businessUnitId" name="businessUnitId" value="${requestParameters.businessUnitId?if_exists}" />
    <div id="main" role="main">
    	<div class="top-band bg-light">
        	<div class="col-lg-12 col-md-12 col-sm-12">
            	<div class="row">
                	<div class="col-lg-12 col-md-12 col-sm-12">
                   		<#assign workEfforts = (Static["org.ofbiz.entity.util.EntityQuery"].use(delegator).select("workEffortId").from("WorkEffortCallSummary").maxRows(5).orderBy("-createdStamp").queryList())?if_exists />
                        <#assign workEffortIds = Static["org.ofbiz.entity.util.EntityUtil"].getFieldListFromEntityList(workEfforts, "workEffortId", true)>
                        <#assign extraLeft=''/>
                        <div class="text-left float-left"><h3 class="float-left">Recently Viewed: </h3>
                        	<#list workEffortIds as workEffortIdsData>
                                <a href="<@ofbizUrl>viewActivity?workEffortId=${workEffortIdsData}</@ofbizUrl>" class="btn btn-xs btn-primary">${workEffortIdsData}</a>
                            </#list>
                      	</div>   
                 	</div>
                    <div class="text-right position-absolute" style="right:20px;">
                    	<a title="Assign" id="assign" href="#" class="btn btn-primary btn-xs mt-1" onclick=""><i class="fa fa-save" aria-hidden="true"></i>Assign</a>
                        <a title="Save" href="#" id="doSave" class="btn btn-primary btn-xs mt-1"><i class="fa fa-save" aria-hidden="true"></i>Save</a>
                        <a title="CLose Activity" href="#" id="doCancel" class="btn btn-primary btn-xs mt-1" ><i class="fa fa-window-close-o" aria-hidden="true"></i>Close</a>
       				</div>
             	</div>
			</div>
		</div>
        <div class="col-lg-12 col-md-12 col-sm-12 mid">
	        <div class="card-head margin-adj mt-2">          
	        	${screens.render("component://sr-portal/widget/services/ServiceScreens.xml#CusNameCommon1")}
	       	</div
    	  	<div class="clearfix"></div>
          	<div class="card-head margin-adj mt-2">
            	<div class="row">
             		<div class="col-md-4">
             			<h6>Activity : Information</h6>
             			<@headerH3 id="test3" title="${inputContext.workEffortId!}" ></@headerH3>
          			</div>
          			<div class="col-md-8 right-details">
            			<div class="bd-callout" style="width:150px;">
                 			<#assign roleEnum = Static["org.ofbiz.entity.util.EntityQuery"].use(delegator).select("statusId","description").from("StatusItem").where("statusTypeId","IA_STATUS_ID").orderBy("sequenceId").queryList()?if_exists />    
	             			<#assign statusesList = Static["org.fio.admin.portal.util.DataUtil"].getMapFromGeneric(roleEnum,"statusId","description",false)?if_exists />  
            				<small>Activity Status</small>
            				<@dropdownCell
              					id="currentStatusId"
              					label=""
              					placeholder=""
              					options=statusesList! 
              				/>
              			</div>
              			<div class="bd-callout">
               				<small>Sub Type</small>
               				<span class="text-danger">*</span>
                			<@headerH5 title="${inputContext.srSubTypeId!}" id="workEffortSubServiceTypeDescription2" />
              			</div>
              			<div class="bd-callout">
                			<small>Activity Type</small>
                			<span class="text-danger">*</span>
                			<@headerH5 title="${inputContext.type!}" id="workEffortServiceTypeDescription2" />
              			</div>             
              			<div class="bd-callout">
               				<small>Priority</small>
               				<span class="text-danger">*</span>
                			<@headerH5 title="${inputContext.priority!}" id="priority" />
              			</div>
              		</div>
              	</div>
              </div>
          		<div class="card-head margin-adj mt-2" id="cp">
          			${screens.render("component://sr-portal/widget/services/ServicesScreens.xml#customerRowCommon1")}
        		</div>  
        		<div class="col-lg-12 col-md-12 col-sm-12">       
            		<form>
       					<ul class="nav nav-tabs mt-2">
				        	<li class="nav-item"><a data-toggle="tab" class="nav-link" id="detailsId" href="#details">Activity Details </a></li>
				         	<li class="nav-item"><a data-toggle="tab" class="nav-link" id="admId" href="#adm">Administration </a></li>
        				</ul>
        				<div class="tab-content">
         					<div id="details" class="tab-pane fade">
         						
        						<#if  inputContext.workEffortTypeId?has_content>
        						<ul class="flot-icone">
							         <li class="mt-0">
							            <a href="/sr-portal/control/updateActivity?workEffortId=${inputContext.workEffortId!}" class="btn btn-xs btn-primary"><i class="fa fa-edit" aria-hidden="true"></i> Update</a>
							         </li>
							      </ul>
        						
        						<#if  inputContext.workEffortTypeId =="TASK">
        						<@dynaScreen 
									instanceId="CREATE_TASK_ACTIVITY"
									modeOfAction="VIEW"
									/>
									
								<#elseif inputContext.workEffortTypeId =="62821">
								<@dynaScreen 
									instanceId="CREATE_EMAIL_ACTIVITY"
									modeOfAction="VIEW"
									/>
								<#elseif inputContext.workEffortTypeId =="62820">
								<@dynaScreen 
									instanceId="CREATE_PHONE_ACTIVITY"
									modeOfAction="VIEW"
									/>
								<#elseif inputContext.workEffortTypeId =="62823">
								<@dynaScreen 
									instanceId="CREATE_APPOINTMENT_ACTIVITY"
									modeOfAction="VIEW"
									/>
								</#if>
								<#else>
								<@dynaScreen 
									instanceId="CREATE_TASK_ACTIVITY"
									modeOfAction="VIEW"
									/>
								</#if>
        					</div>
         					<div id="adm" class="tab-pane fade in">
         					
         						<@dynaScreen 
									instanceId="ACTIVITY_ADMIN_INFO"
									modeOfAction="VIEW"
									/>
        					</div>
        					<@inputArea
						          inputColSize="col-sm-12"
						          id="messages"
						          label=uiLabelMap.Description
						          maxlength=100
						          rows="10"
						          disabled=true
						          placeholder = uiLabelMap.Description
						          value = inputContext.messages?if_exists
						        />
						        <#if inputContext.workEffortTypeId =="62821">
						        <div class="col-md-12 col-lg-12 col-sm-12" id="emlContent">
		                        <@textareaLarge
					               id="emailContent"
					               groupId = "htmlDisplay"
					               label=uiLabelMap.html
					               rows="3"
					               value = template
					               required = false
					               txareaClass = "ckeditor"
					               />
		                        <script>
							        CKEDITOR.replace( 'emailContent',{
							        	customConfig : '/bootstrap/js/ckeditor/ck-custom-config.js',
										autoGrow_minHeight : CKEditorUtil.autoGrowMinHeight,
										removePlugins : CKEditorUtil.removePlugins
							        });
							    </script>
		                        </div>
		                        </#if>
        				</div>  
     				</form>
        		</div>
    	</div>
	</div>
</div>
     
<div id="assignModal" class="modal fade" role="dialog">
	<div class="modal-dialog" style="width:60%;">
    	<div class="modal-content">
        	<div class="modal-header">
            	<h4 class="modal-title">Assign To</h4>
               	<button type="reset" class="close" data-dismiss="modal">&times;</button>
            </div>
            <div class="modal-body">
            	<form method="post" action="#" id="assignModal" class="form-horizontal" name="" novalidate="novalidate" data-toggle="validator">
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

	$(document).ready(function(){
    	document.getElementById("detailsId").click();
     	//loadActivity();
     	loadData();
     	
  	});
    function loadData(){
    
    var partyId = '${inputContext.partyId!}';
		$.ajax({
			type: "POST",
	     	url: "/common-portal/control/getPrimaryContacts",
	        data: {"partyId": partyId, "externalLoginKey": "${requestAttributes.externalLoginKey!}"},
	        async: false,
	        success: function (data) {   
	            if (data) {
	            if(data.responseMessage=="success"){
	            	for (var i = 0; i < data.partyRelContacts.length; i++) {
	            		var entry = data.partyRelContacts[i];
	            		if(entry.selected!=null){
	            		dataSourceOptions += '<option value="'+entry.contactId+'" selected>'+entry.name+'</option>';
	            		}else{
	            		dataSourceOptions += '<option value="'+entry.contactId+'">'+entry.name+'</option>';
	            		}
	            	}
	            	}else{
	            	for (var i = 0; i < data.length; i++) {
	            		var entry = data[i];
	            		dataSourceOptions += '<option value="'+entry.contactId+'">'+entry.name+'</option>';
	            	}
	            	
	            	}
	            }
	        }
	        
		});    
		
		$("#contactId").html( dataSourceOptions );
				
		$("#contactId").dropdown('refresh');
    
    
    }
    $("#assign").click(function () {
    	var workEffortId = $('#workEffortId').val();
        var emplTeamId = $('#emplTeamId').val();
        var businessUnitId = $('#businessUnitId').val();
        var businessUnitName= $('#businessUnitName').val();
        var primOwnerId = $('#primOwnerId').val();
        if(workEffortId!=null && workEffortId!=""){
        	if (emplTeamId != "" && businessUnitId != "") {
            	loaduserteam(emplTeamId , businessUnitId,workEffortId);
                $("#assignModal").modal();
          	} else{
            	$.notify({
                	message : '<p>There is no User or Team to assign</p>',
             	});
         	}        
     	} else{
        	$.notify({
            	message : '<p>There is no activity to assign</p>',
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
	            	$("#assignModal").modal('hide');   
	               	$.notify({
	                	message : '<p>Reassigned Successfully for</p>'+workEffortId,
	              	});
	              	window.location.reload();
	            }
	        });   
    	}
	}
	
	$("#doSave").click(function(event) {
		event.preventDefault(); 
	   	savedetails();
	});
	
	$("#doCancel").click(function(event) {
	    event.preventDefault(); 
	   canceldetails();
	});
	
	function savedetails(){
    	var workEffortId = $('#workEffortId').val();
     	var currentStatusId = $('#currentStatusId').val();
    	$.ajax({
			url:'updateServiceActivityDetails',
			data:{"workEffortId":workEffortId,"currentStatusId":currentStatusId},
			type:"post",
			success:function(data){
				var workEffortId = data[0].workEffortId;
				document.getElementById("workEffortId").value = workEffortId; 
				var currentStatusId = data[0].currentStatusId;
				document.getElementById("currentStatusId").value = currentStatusId; 
	 			showAlert("success", "Saved Successfully");
				return data;
			},
			error:function(data){
				return data;
			}
		}); 
	}
	
	function canceldetails(){
    	var workEffortId = $('#workEffortId').val();
    	var currentStatusId = $('#currentStatusId').val();
      	$.ajax({
	    	url:'closedServiceActivityDetails',
		    data:{"workEffortId":workEffortId,"currentStatusId":currentStatusId},
		    type:"post",
		    success:function(data){
			    var workEffortId = data[0].workEffortId;
			    document.getElementById("workEffortId").value = workEffortId; 
			    var currentStatusId = data[0].currentStatusId;
			    document.getElementById("currentStatusId").value = currentStatusId; 
			    showAlert("success", " Activity Closed Successfully");
		        return data;
			   window.location.reload();
	  		},
	 		error:function(data){
				return data;
	 		}
		}); 
	}
	
	function loadActivity(){
    	var workEffortId =$("#workEffortId").val();
    	dataSet = {"workEffortId":workEffortId};
   	 	$.ajax({
		    url:'viewServiceActivityDetails',
		    data:dataSet,
		    type:"post",
		    success:function(data){
			    var workEffortServiceType1=data[0].workEffortServiceType;
			    var workEffortName1=data[0].workEffortName;
			    var workEffortId1=data[0].workEffortId;
			    var direction1=data[0].direction;
			    var phoneNumber1=data[0].phoneNumber;
			    var description1=data[0].description;
			    var accountNumber1=data[0].accountNumber;
			    var wfOnceDone1=data[0].wfOnceDone;
			    var currentStatusId1=data[0].currentStatusId;
			    var businessUnitName1=data[0].businessUnitName;
			    var primOwnerId1=data[0].primOwnerName;
			    var partyId1=data[0].partyId;
			    var duration=data[0].duration;
			    var externalReferenceTypeId1=data[0].externalReferenceTypeId;
			    var regardingId1=data[0].regardingId;
			    var csrPartyId1=data[0].csrPartyId;
			    var callStartTime1=data[0].callStartTime;
			    var callEndTime1=data[0].callEndTime;
			    var callDuration1=data[0].callDuration;
			    var estimatedStartDate=data[0].estimatedStartDate;
			    var nationalId1=data[0].nationalId;
			    var workEffortServiceTypeDescription=data[0].workEffortServiceTypeDescription;
			    var workEffortSubServiceTypeDescription=data[0].workEffortSubServiceTypeDescription;
			    var priority=data[0].priority;
			    var currentStatusId=data[0].currentStatusId;    
			    var emplTeamId=data[0].emplTeamId;
			    var businessUnitName=data[0].businessUnitName;
			    var businessUnitId=data[0].businessUnitId;
			    if(workEffortServiceTypeDescription!=null){
			  	 	document.getElementById("workEffortServiceTypeDescription1").innerHTML=workEffortServiceTypeDescription;
			   }else{
			   		document.getElementById("workEffortServiceTypeDescription1").innerHTML="--";
			   }
			   if(workEffortServiceTypeDescription!=null){
			  	 	document.getElementById("workEffortServiceTypeDescription2").innerHTML=workEffortServiceTypeDescription;
			   }else{
			   		document.getElementById("workEffortServiceTypeDescription2").innerHTML="--";
			   }
			   if(workEffortSubServiceTypeDescription!=null){
			  	 	document.getElementById("workEffortSubServiceTypeDescription1").innerHTML=workEffortSubServiceTypeDescription;
			   }else{
			   		document.getElementById("workEffortSubServiceTypeDescription1").innerHTML="--";
			   }
			   if(workEffortSubServiceTypeDescription!=null){
			  	 	document.getElementById("workEffortSubServiceTypeDescription2").innerHTML=workEffortSubServiceTypeDescription;
			   }else{
			   		document.getElementById("workEffortSubServiceTypeDescription2").innerHTML="--";
			   }
			    if(workEffortName1!=null){
			  	 	document.getElementById("test2").innerHTML=workEffortName1;
			   }else{
			   		document.getElementById("test2").innerHTML="--";
			   }
			    if(workEffortId1!=null){
			  	 	document.getElementById("test3").innerHTML=workEffortId1;
			   }else{
			   		document.getElementById("test3").innerHTML="--";
			   }
			    if(direction1!=null){
			  	 	document.getElementById("test5").innerHTML=direction1;
			   }else{
			   		document.getElementById("test5").innerHTML="--";
			   }
			    if(phoneNumber1!=null){
			  	 	document.getElementById("test6").innerHTML=phoneNumber1;
			   }else{
			   		document.getElementById("test6").innerHTML="--";
			   }
			   if(description1!=null){
			  	 	document.getElementById("test7").innerHTML=description1;
			   }else{
			   		document.getElementById("test7").innerHTML="--";
			   }
			   if(description1!=null){
			  	 	document.getElementById("description").innerHTML=description1;
			   }else{
			   		document.getElementById("description").innerHTML="--";
			   }
			   if(accountNumber1!=null){
			  	 	document.getElementById("test8").innerHTML=accountNumber1;
			   }else{
			   		document.getElementById("test8").innerHTML="--";
			   }
			   if(wfOnceDone1!=null){
			  	 	document.getElementById("test9").innerHTML=wfOnceDone1;
			   }else{
			   		document.getElementById("test9").innerHTML="--";
			   }
			   if(partyId1!=null){
			  	 	document.getElementById("testT2").innerHTML=partyId1;
			   }else{
			   		document.getElementById("testT2").innerHTML="--";
			   }
			    if(estimatedStartDate!=null){
			  	 	document.getElementById("estimatedStartDate").innerHTML=estimatedStartDate;
			   }else{
			   		document.getElementById("estimatedStartDate").innerHTML="--";
			   }
			    if(duration!=null){
			  	 	document.getElementById("duration").innerHTML=duration;
			   }else{
			   		document.getElementById("duration").innerHTML="--";
			   }
			   if(workEffortId1!=null){
			  	 	document.getElementById("test3").innerHTML=workEffortId1;
			   }else{
			   		document.getElementById("test3").innerHTML="--";
			   }
			    if(primOwnerId1!=null){
			  	 	document.getElementById("test12").innerHTML=primOwnerId1;
			   }else{
			   		document.getElementById("test12").innerHTML="--";
			   }
			    if(businessUnitName1!=null){
			  	 	document.getElementById("test11").innerHTML=businessUnitName1;
			   }else{
			   		document.getElementById("test11").innerHTML="--";
			   }
			    if(csrPartyId1!=null){
			  	 	document.getElementById("csrPartyId2").innerHTML=csrPartyId1;
			   }else{
			   		document.getElementById("csrPartyId2").innerHTML="--";
			   }
			    if(regardingId1!=null){
			  	 	document.getElementById("test15").innerHTML=regardingId1;
			   }else{
			   		document.getElementById("test15").innerHTML="--";
			   }
			   if(priority!=null){
			  	 	document.getElementById("priority").innerHTML=priority;
			   }else{
			   		document.getElementById("priority").innerHTML="--";
			   }
			   if(currentStatusId!=null){
			  	 	$(".currentStatusId [data-value='" + currentStatusId +"']").click();
			   }
			     if(emplTeamId!=null){
			  	 	$("#emplTeamId").val(emplTeamId);
			   } 
			    if(businessUnitId!=null){
			  	 	$("#businessUnitId").val(businessUnitId);
			   } 
			   if(businessUnitName!=null){
			  	 	$("#businessUnitName").val(businessUnitName);
			   } 
			    if(primOwnerId1!=null){
			  	 	$("#primOwnerId").val(primOwnerId1);
			   } 
			   
			   if(partyId1!=null){
			  	 	$("#partyId").val(partyId);
			   } 
 		}
	});
}

</script>
