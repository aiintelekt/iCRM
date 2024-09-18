<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<#include "component://sr-portal/webapp/sr-portal/services/findCustomerModal.ftl"/>
<#include "component://sales-portal/webapp/sales-portal/activities-home/modalForActivity.ftl">
<#include "component://common-portal/webapp/common-portal/lib/picker_macro.ftl"/>
<script src="/bootstrap/js/ckeditor/ckeditor.js" type="text/javascript"></script>
<#if inputContext.workEffortTypeId =="62821">
<script type="text/javascript" src="/account-portal-resource/js/emailActivity.js"></script>
<#else>
<script type="text/javascript" src="/sr-portal-resource/js/services/addSrActivity.js"></script>
</#if>
<div class="row">
	<div id="main" role="main">
		<div class="col-lg-12 col-md-12 col-sm-12">
        	<@pageSectionHeader title="Activity Details"/>
       	</div>

      	<form method="post" action="<#if (parameters.srNumber)?has_content>updateActivityEvent<#else>updateActivityEvent</#if>" id="SrTask" class="form-horizontal" name="SrTask" novalidate="novalidate" data-toggle="validator">
       		<input type="hidden" name="custRequestId" value="${(parameters.srNumber)!}"/>
                	<div class="col-md-12 col-md-6 col-sm-12">
                    	<#assign cifNo = '${requestParameters.partyId!}' >
                    	<@inputHidden name="cNo" id="cNo" value = "${cifNo!}"/>
                    	<#if  inputContext.workEffortTypeId =="TASK">
                    	<#assign srType = EntityQuery.use(delegator).from("WorkEffortAssocTriplet").where("entityName", "Activity", "type", "Type", "value", "Task", "active", "Y").queryFirst()! />
                    	<#elseif inputContext.workEffortTypeId =="62821">
                    	<#assign srType = EntityQuery.use(delegator).from("WorkEffortAssocTriplet").where("entityName", "Activity", "type", "Type", "value", "E-mail", "active", "Y").queryFirst()! />
                    	<#elseif inputContext.workEffortTypeId =="62820">
                    	<#assign srType = EntityQuery.use(delegator).from("WorkEffortAssocTriplet").where("entityName", "Activity", "type", "Type", "value", "Phone Call", "active", "Y").queryFirst()! />
                    	<#elseif inputContext.workEffortTypeId =="62823">
                    	<#assign srType = EntityQuery.use(delegator).from("WorkEffortAssocTriplet").where("entityName", "Activity", "type", "Type", "value", "Appointment", "active", "Y").queryFirst()! />
                    	</#if>
                        <@inputHidden id="srTypeId" value="${(srType.code)!}"/>
                        <@inputHidden id="workEffortTypeId" value="${(srType.value)!}"/>
                        <@inputHidden id="loggedInUserId" value="${userLogin.userLoginId?if_exists}" />
	                    <#assign userName = userLogin.userLoginId>
	                    <#assign findMap = Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId", userLogin.partyId)>
	                    <#assign person = delegator.findOne("Person", findMap, true)!>
	                    <#if person?has_content>
	                    	<#assign userName = (person.firstName!) + " " + (person.middleName!) + " " + person.lastName!>
	                    	<@inputHidden id="userName" value="${userName!}"/>
	                    </#if>
		               		<#if  inputContext.workEffortTypeId =="TASK">
        						<@dynaScreen 
									instanceId="CREATE_TASK_ACTIVITY"
									modeOfAction="UPDATE"
									/>
									
								<#elseif inputContext.workEffortTypeId =="62821">
								<@dynaScreen 
									instanceId="CREATE_EMAIL_ACTIVITY"
									modeOfAction="UPDATE"
									/>
								<#elseif inputContext.workEffortTypeId =="62820">
								<@dynaScreen 
									instanceId="CREATE_PHONE_ACTIVITY"
									modeOfAction="UPDATE"
									/>
								<#elseif inputContext.workEffortTypeId =="62823">
								<@dynaScreen 
									instanceId="CREATE_APPOINTMENT_ACTIVITY"
									modeOfAction="UPDATE"
									/>
								<#else>
								<@dynaScreen 
									instanceId="CREATE_TASK_ACTIVITY"
									modeOfAction="UPDATE"
									/>
								</#if>
					</div>
					<#if inputContext.workEffortTypeId =="62821">	
 						<div class="row p-2">
		                	<div class="col-md-12 col-lg-12 col-sm-12" id="emailContent">
		                    	<textarea id="emlContent" class="ckeditor">${inputContext.emailContent!}</textarea>
		                    </div>
		               	</div>
 						<#else>
 					 <div class="col-md-12 col-lg-12 col-sm-12 ">
     					<@textareaLarge  label="Description" id="messages" rows="3"/>
  					</div>
				</#if>
                <div class="offset-md-2 col-sm-10">
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
    	</form>
	</div>
</div>
<@partyPicker 
	instanceId="partyPicker"
	/> 
	
<script>
$(document).ready(function() {

		$("span.picker-window-erase").css("display", "none");
		$("span.picker-window").css("display", "none");
		onLoadDefaultElementsBehaviour();
		 var owner  = $("#owner").val();
		 if(owner != undefined && owner != null)
		 	getBusinessUnit(owner);
		 	
		$('#type').val($('#workEffortTypeId').val());
		$('#type').attr('readonly','readonly');
		$('#ownerBuDesc').attr('readonly','readonly');
		var userName  = $("#userName").val();
	    var loggedInUserId  = $("#loggedInUserId").val();
	    getUsers(loggedInUserId,userName);	
	    
	    $("#partyId_desc").on("change", function() {
	});
	    
	});
	$(function() {
		$("#owner").change(function() {
			var owner  = $("#owner").val(); 
		   	if(owner != undefined && owner != null)
		   		getBusinessUnit(owner);	    
		});
	});
	
	function getBusinessUnit(owner) {
		var owner = owner;
	    	$.ajax({
		    	type: "POST",
		        url: "getBusinessUnitName",
		        async: false,
		        data: { "owner": owner },
		        success: function(data) {
		        	result=data;
		            if(result && result[0] != undefined && result[0].businessunitName != undefined)
		            	$("#ownerBuDesc").val(result[0].businessunitName);
		            	$("#ownerBu").val(result[0].businessId);
		            else
		            	$("#ownerBu").val("");
		        },error: function(data) {
		        	result=data;
					showAlert("error", "Error occured while fetching Business Unit");
				}
		    });
	}
	function onLoadDefaultElementsBehaviour(){
        
        var today = new Date();
		var dd = String(today.getDate()).padStart(2, '0');
		var mm = String(today.getMonth() + 1).padStart(2, '0');
		var yyyy = today.getFullYear();
		
		//today = dd + '/' + mm + '/' + yyyy;
		//$('#taskDate').val(today.toLocaleString([],{hour12: false}).replace(",",""));
		
		
		var dataSourceOptions = '';
		var partyId = $("#partyId_val").val();
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
	function getUsers(loggedInUserId,userName) {
        var userOptionList = '<option value="'+loggedInUserId+'">'+userName+'</option>';
        $.ajax({
            type: "GET",
            url:'/common-portal/control/getUsersList',
            async: false,
            success: function(data) {
            	for (var i = 0; i < data.length; i++) {
                    var type = data[i];
                    userOptionList += '<option value="'+type.userLoginId+'">'+type.userName+'</option>';
                }
            }
        });
       $("#owner").html(userOptionList);
	}
	
	function formSubmission(){
    	var valid = true;
    	if($('#partyId_val').val() == ""){
	 		showAlert('error','Please select Customer');
	 		valid = false;
	 	}else{
	 		$('#cNo').val($('#partyId_val').val());
	 	}
	 	return valid;
	 }

</script>
