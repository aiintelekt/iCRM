<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<script src="/bootstrap/js/ckeditor/ckeditor.js" type="text/javascript"></script>
<script src="/bootstrap/js/ckeditor/ck-custom-functions.js" type="text/javascript"></script>
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
                        <#assign workEfforts = (Static["org.ofbiz.entity.util.EntityQuery"].use(delegator).select("seqId").from("UserLoginHistory").where("entity","WorkEffortCallSummary","userLoginId",userLogin.userLoginId).maxRows(5).orderBy("-fromDate").distinct().queryList())?if_exists />
                        <#assign workEffortIds = Static["org.ofbiz.entity.util.EntityUtil"].getFieldListFromEntityList(workEfforts, "seqId", true)>
                        <div class="text-left float-left">
                            <h3 class="float-left"> View Activity :</h3>
                            <!--<#list workEffortIds as workEffortId>
                                <a href="<@ofbizUrl>viewActivity?workEffortId=${workEffortId!}&seqId=${workEffortId!}&entity=WorkEffortCallSummary</@ofbizUrl>" class="btn btn-xs btn-primary">${workEffortId!}</a>
                                </#list>-->
                        </div>
                    </div>
                    <div class="text-right position-absolute" style="right:20px;">
                        <#--<button type="button" class="btn btn-primary btn-xs mt-1" id="assign"><i class="fa fa-save" aria-hidden="true"></i>Assign</button>-->
                        <#--<button type="button" class="btn btn-primary btn-xs mt-1" id="doSave"><i class="fa fa-save" aria-hidden="true"></i>&nbsp;&nbsp;Save</button>-->
                        <#if inputContext.statusId == "IA_OPEN">
                        <button type="button" class="btn btn-primary btn-xs mt-1" id="doCancel"><i class="fa fa-save" aria-hidden="true"></i>&nbsp;&nbsp;Mark Complete</button>
                        </#if>
                    </div>
                </div>
			</div>
		</div>
       
    	
    	
        <div class="card-head margin-adj mt-2">
             <div class="row">
             <div class="col-md-4">
             <h6>Activity : Information</h6>
             <@headerH3 id="test3" title="" />
        </div>
        
        <div class="col-md-8 right-details">
        	<!--<div class="bd-callout" style="width:150px;">
            	<#assign roleEnum = Static["org.ofbiz.entity.util.EntityQuery"].use(delegator).select("statusId","description").from("StatusItem").where("statusTypeId","IA_STATUS_ID").orderBy("sequenceId").queryList()?if_exists />    
	            <#assign roleTypeList = Static["org.fio.admin.portal.util.DataUtil"].getMapFromGeneric(roleEnum,"statusId","description",false)?if_exists />  
            	<small>Activity Status</small>
            	<@dropdownCell
	              	id="currentStatusId"
	              	label=""
	              	placeholder=""
	              	options=roleTypeList! 
              	/>
            </div>-->
            
         	<!--<div class="bd-callout">
            	<small>Sub Type</small>
               	<span class="text-danger">*</span>
                <@headerH5 title="" id="workEffortSubServiceTypeDescription2" />
          	</div>-->
          	
            <div class="bd-callout">
            	<small>Activity Type</small>
                <span class="text-danger">*</span>
                <@headerH5 title="" id="workEffortServiceTypeDescription2" />
            </div>
             
            <div class="bd-callout">
            	<small>Priority</small>
               	<span class="text-danger">*</span>
                <@headerH5 title="" id="priority" />
            </div>
        </div>
	</div>
</div>
              
         
         
        <#assign domainEntityType = '${domainEntityType!}' />
        <#if domainEntityType == "">
            <#assign domainEntityType = request.getParameter("domainEntityType")!/>
        </#if>
         
        <#if domainEntityType?if_exists == "ACCOUNT">
	        <div class="col-lg-12 col-md-12 col-sm-12 dash-panel" id="cp">
		        <@dynaScreen 
		            instanceId="ACCT_BASIC_INFO"
		            modeOfAction="VIEW"
		        />
	        </div>  
        </#if>
        
        <#if domainEntityType?if_exists == "LEAD">
        	<div class="col-lg-12 col-md-12 col-sm-12 dash-panel" id="cp">
		        <@dynaScreen 
		            instanceId="LEAD_BASIC_INFO"
		            modeOfAction="VIEW"
		        />
        	</div>
        </#if>
        
        <#if domainEntityType?if_exists == "CONTACT">
        	<div class="col-lg-12 col-md-12 col-sm-12 dash-panel" id="cp">
		        <@dynaScreen 
		            instanceId="CONT_BASIC_INFO"
		            modeOfAction="VIEW"
		        />
        	</div>
        </#if>
        
        <#if domainEntityType?if_exists == "OPPORTUNITY">
        	<div class="col-lg-12 col-md-12 col-sm-12 dash-panel" id="cp">
		        <@dynaScreen 
		            instanceId="OPPORTUNITY_BASIC"
		            modeOfAction="VIEW"
		        />
        	</div>
        </#if>
        
    	<div class="clearfix"></div>
        <div class="col-lg-12 col-md-12 col-sm-12">       
        	<form>
       			<ul class="nav nav-tabs mt-2">
                    <li class="nav-item"><a data-toggle="tab" class="nav-link" id="detailsId" href="#activityDetails">Activity Details </a></li>
                    <#--  <li class="nav-item"><a data-toggle="tab" class="nav-link" id="admId" href="#adm">Administration </a></li>-->
        		</ul>
        		<div class="tab-content">
	        		<div id="activityDetails" class="tab-pane fade">
	        				<#if (inputContext.workEffortTypeId?has_content && (inputContext.workEffortTypeId !="31703" && inputContext.workEffortTypeId!="31709"))&&(inputContext.currentStatusId!="IA_MCOMPLETED" && inputContext.currentStatusId !="IA_CLOSED")>
	        					<ul class="flot-icone">
							         <li class="mt-0">
							          <a href="updateActivity?workEffortId=${inputContext.workEffortId!}&domainEntityType=${domainEntityType!}&domainEntityId=${domainEntityId!}&${domainEntityFieldId!}=${domainEntityId!}&externalLoginKey=${requestAttributes.externalLoginKey!}" class="btn btn-xs btn-primary"><i class="fa fa-edit" aria-hidden="true"></i> Update</a>
							         </li>
							      </ul>
						    </#if>
	        				<#if  inputContext.workEffortTypeId?has_content>
        						<#if  inputContext.workEffortTypeId =="TASK">
        						<@dynaScreen 
									instanceId="TASK_ACT_GEN"
									modeOfAction="VIEW"
									/>
									
								<#elseif inputContext.workEffortTypeId =="EMAIL">
								<@dynaScreen 
									instanceId="EMAIL_ACT_GEN"
									modeOfAction="VIEW"
									/>
								<#elseif inputContext.workEffortTypeId =="PHONE">
								<@dynaScreen 
									instanceId="PHONE_ACT_GEN"
									modeOfAction="VIEW"
									/>
								<#elseif inputContext.workEffortTypeId =="APPOINTMENT">
								<@dynaScreen 
									instanceId="APNT_ACT_GEN"
									modeOfAction="VIEW"
									/>
								</#if>
								<#else>
								<@dynaScreen 
									instanceId="TASK_ACT_GEN"
									modeOfAction="VIEW"
									/>
								</#if>
								
								<#if inputContext.workEffortTypeId =="EMAIL">
								<div class="row padding-r">
			                    	<div class="col-md-6 col-sm-6">
			                    	<@displayRowFileContent 
									id="attachment"
									label="Attachments"
									activityId="${requestParameters.workEffortId!}"
									/>
			                    	</div>
			                    </div>
			                    </#if>
								
								<#if inputContext.workEffortTypeId?has_content && inputContext.workEffortTypeId =="EMAIL">
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
								        CKEDITOR.replace( 'emailContent', {	
								        	customConfig : '/bootstrap/js/ckeditor/ck-custom-config.js',
								        });
								    </script>
			                        </div>
			                        <#else>
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
			                        
		                        </#if>
		  					</div>
								
	        			<#-- ${screens.render("component://sales-portal/widget/activities-home/activitieshomeScreens.xml#ActivityDetails")}  
	        			${screens.render("component://common-portal/widget/common/CommonScreens.xml#ActivityDetails")}-->
	        		</div>
	         		<#--  <div id="adm" class="tab-pane fade in">
	        			<#-- ${screens.render("component://sales-portal/widget/activities-home/activitieshomeScreens.xml#administration1")}  
	        			${screens.render("component://common-portal/widget/common/CommonScreens.xml#AdministrationDetails")}
	        		</div>-->
	        	</div>  
     		</form>
        </div>
    </div>
</div>
      	<div id="assignModal" class="modal fade" role="dialog">
   			<div class="modal-dialog" style="width:60%;">
      			<div class="modal-content">
            		<div class="modal-header">
                		<h4 class="modal-title">Assign To</h4>
                		<button type="button" class="close" data-dismiss="modal" id="close">&times;</button>
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
               			</form><#-- added -->
             		
             			<div class="modal-footer">
                 			<@submit class="btn btn-sm btn-primary navbar-dark" id="saveModal" label="${uiLabelMap.Save}" />
                 			<button type="button" class="btn btn-default btn-primary navbar-dark" id="btnclose" data-dismiss="modal">Close</button>
                 			<@reset id="reset" label="${uiLabelMap.Reset}" />
                 		</div>
               		</div>
            	</div>
      		</div>
     	</div>
    
<script>
$(document).ready(function(){
	document.getElementById("detailsId").click();
        //loadActivity();
    //$('#messages').append('${messages!}');
    $('#assignModal #close').click(function(){
			$('#assignModal input[type=reset]').click();
	});
	$('#assignModal #btnclose').click(function(){
		$('#assignModal input[type=reset]').click();
	});
	$('#workEffortTypeId').val('${inputContext.srType!}');
	
	//var htmlContent='${inputContext.content!}';
    	var url = document.URL;
		var hash = url.substring(url.indexOf('#'));
		$(".nav-tabs").find("li a").each(function(key, val) {
		    if (hash == $(val).attr('href')) {
		        $(val).click();
		    }
		    
		    $(val).click(function(ky, vl) {
		        location.hash = $(this).attr('href');
		    });
		});
	
});
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
            $("input[type='reset']").hide();  
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
		                userOption += '<option value="'+category.partyId+'">'+category.userLoginId+'</option>';                     
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
			showAlert("success", "Saved Successfully");
			loadActivity();
			location.reload();
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
	 		showAlert("success", " Activity Closed Successfully");
	 		loadActivity();
	 		location.reload();
     		return data;
		},
		error:function(data){
			console.log("dataerror====",data);
			return data;
		}
	}); 
}

function loadActivity(){
	var workEffortId =$("#workEffortId").val();
    dataSet = {"workEffortId":workEffortId};
    $.ajax({
	    url:'getActivityDetails',
	    data:dataSet,
	    type:"post",
	    success:function(data){
		   
		    var workEffortId1=data[0].workEffortId;
		   
		    var workEffortServiceTypeDescription=data[0].workEffortServiceTypeDescription;
		    var workEffortSubServiceTypeDescription=data[0].workEffortSubServiceTypeDescription;
		    var priority=data[0].priority;
		    var currentStatusId=data[0].currentStatusId;
		   
		    if(workEffortId1!=null){
		  	 	document.getElementById("test3").innerHTML=workEffortId1;
		   	}else{
		   		document.getElementById("test3").innerHTML="--";
		   	}
		    
		    
		   	if(priority!=null){
		  	 	document.getElementById("priority").innerHTML=priority;
		   	}else{
		   		document.getElementById("priority").innerHTML="--";
		   	}
		   	
		   	
		   	if(workEffortServiceTypeDescription!=null){
		  	 	document.getElementById("workEffortServiceTypeDescription2").innerHTML=workEffortServiceTypeDescription;
		   	}else{		   		
		   		document.getElementById("workEffortServiceTypeDescription2").innerHTML="--";		   		
		   	}
		   	if(workEffortSubServiceTypeDescription!=null){
		  	 	document.getElementById("workEffortSubServiceTypeDescription2").innerHTML=workEffortSubServiceTypeDescription;
		   	}else{
		   		document.getElementById("workEffortSubServiceTypeDescription2").innerHTML="--";
		   	}
		   	if(currentStatusId!=null){
		  	 	$(".currentStatusId [data-value='" + currentStatusId +"']").click();
		   	}
		  
		   	if(currentStatusId=="IA_MCOMPLETED"){
		   	$("#currentStatusId").empty();
		   	document.getElementById('assign').setAttribute('disabled', true);
	   	    document.getElementById('doSave').setAttribute('disabled', true);
	   	    document.getElementById('doCancel').setAttribute('disabled', true);
		   	}
		   		    
		}
  	});
}
</script>
