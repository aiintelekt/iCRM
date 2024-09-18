<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<#include "component://sr-portal/webapp/sr-portal/services/findCustomerModal.ftl"/>
<#include "component://sales-portal/webapp/sales-portal/activities-home/modalForActivity.ftl">
<#include "component://common-portal/webapp/common-portal/lib/picker_macro.ftl"/>
<script src="/bootstrap/js/ckeditor/ckeditor.js" type="text/javascript"></script>
<script src="/bootstrap/js/ckeditor/ck-custom-functions.js" type="text/javascript"></script>
<script type="text/javascript" src="/common-portal-resource/js/ag-grid/activity/validation-activity.js"></script>
<script src="/bootstrap/js/ckeditor/ckeditor.js" type="text/javascript"></script>
<script>
    CKEDITOR.env.isCompatible = true;
</script>

<#if inputContext.workEffortTypeId =="31703">
<script type="text/javascript" src="/account-portal-resource/js/emailActivity.js"></script>
</#if>

<div class="row">
	<div id="main" role="main">
		<div class="col-lg-12 col-md-12 col-sm-12 dash-panel">
        	<@pageSectionHeader title="Activity Details"/>
      
  <#--Changes made to add back button in update activitiy for LEAD,ACCOUNT,CONTACT,OPPOURNITY-->
       <#assign screen_Name = request.getParameter("domainEntityType")! >
       <#if  screen_Name?if_exists == "LEAD">
       		<#assign screenRedirect = "lead-portal">       
       <#elseif screen_Name?if_exists == "ACCOUNT">
       		<#assign screenRedirect = "account-portal">       
       <#elseif screen_Name?if_exists == "CONTACT">
       		<#assign screenRedirect = "contact-portal">       
       <#elseif screen_Name?if_exists == "OPPORTUNITY">
       		<#assign screenRedirect = "opportunity-portal">
       </#if>
       <#assign salesOppId= request.getParameter("salesOpportunityId")! />
		<#if salesOppId?has_content>
		<#assign salesOppRole=EntityQuery.use(delegator).from("SalesOpportunityRole").where("salesOpportunityId", salesOppId).queryFirst()! />
		<#if salesOppRole?has_content>
		<#assign partyId=salesOppRole.get("partyId")/>
		</#if>
		</#if>
   	   <#if  screen_Name?has_content> 
	       <div class="text-right" id="extra-header-right-container">
	         <a href="/${screenRedirect}/control/viewActivity?workEffortId=${inputContext.activityId!}&domainEntityType=${screen_Name!}" class="btn btn-xs btn-primary">
	         <i class="fa fa-chevron-circle-left" aria-hidden="true"></i> Back</a>
	   	  </div>
    	</#if>
  <#--end-->
      	<form method="post" action="<#if (parameters.srNumber)?has_content>updateActivityEvent<#else>updateActivityEvent</#if>" id="SrTask" class="form-horizontal" name="SrTask" novalidate="novalidate" data-toggle="validator" onsubmit="return submitActivityForm();">
       		<input type="hidden" name="custRequestId" value="${(parameters.srNumber)!}"/>
                	<div class="col-md-12 col-md-6 col-sm-12">
                    	<#assign cifNo = '${requestParameters.partyId!}' >
                    	<@inputHidden name="cNo" id="cNo" value = "${cifNo!}"/>
                    	<#assign salesOpportunityId = '${requestParameters.salesOpportunityId!}' >
                    	<@inputHidden name="salesOpportunityId" id="salesOpportunityId" value = "${salesOpportunityId!}"/>
                    	<@inputHidden  id="selectedOwnerId" value="${selectedOwnerId?if_exists}" />
                    	<@inputHidden  id="domainEntityType" value="${(parameters.domainEntityType)!}" />
                    	<@inputHidden  id="domainEntityId" value="${(parameters.domainEntityId)!}" />
                    	<#if  inputContext.workEffortTypeId =="TASK">
                    	<#assign srType = EntityQuery.use(delegator).from("WorkEffortAssocTriplet").where("entityName", "Activity", "type", "Type", "value", "Task", "active", "Y").queryFirst()! />
                    	<#elseif inputContext.workEffortTypeId =="31703">
                    	<#assign srType = EntityQuery.use(delegator).from("WorkEffortAssocTriplet").where("entityName", "Activity", "type", "Type", "value", "E-mail", "active", "Y").queryFirst()! />
                    	<#elseif inputContext.workEffortTypeId =="31709">
                    	<#assign srType = EntityQuery.use(delegator).from("WorkEffortAssocTriplet").where("entityName", "Activity", "type", "Type", "value", "Phone Call", "active", "Y").queryFirst()! />
                    	<#elseif inputContext.workEffortTypeId =="31705">
                    	<#assign srType = EntityQuery.use(delegator).from("WorkEffortAssocTriplet").where("entityName", "Activity", "type", "Type", "value", "Appointment", "active", "Y").queryFirst()! />
                    	</#if>
                        <@inputHidden id="srTypeId" value="${(srType.code)!}"/>
                        <@inputHidden id="workEffortTypeId" value="${(srType.value)!}"/>
                        <@inputHidden id="ownerBu" value="${inputContext.ownerBu!}"/>
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
									instanceId="TASK_ACT_GEN"
									modeOfAction="UPDATE"
									/>
									
								<#elseif inputContext.workEffortTypeId =="31703">
								<@dynaScreen 
									instanceId="EMAIL_ACT_GEN"
									modeOfAction="UPDATE"
									/>
								<#elseif inputContext.workEffortTypeId =="31709">
								<@dynaScreen 
									instanceId="PHONE_ACT_GEN"
									modeOfAction="UPDATE"
									/>
								<#elseif inputContext.workEffortTypeId =="31705">
								<@dynaScreen 
									instanceId="APNT_ACT_GEN"
									modeOfAction="UPDATE"
									/>
								<#else>
								<@dynaScreen 
									instanceId="TASK_ACT_GEN"
									modeOfAction="UPDATE"
									/>
								</#if>
					</div>
					<#if inputContext.workEffortTypeId =="31703">	
 						<div class="row p-2">
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
									autoGrow_minHeight : CKEditorUtil.autoGrowMinHeight,
									removePlugins : CKEditorUtil.removePlugins
								});
							</script>
                        </div>
		               	</div>
 						<#else>
 					 <div class="col-md-12 col-lg-12 col-sm-12 ">
     					<@inputArea
						          inputColSize="col-sm-12"
						          id="messages"
						          label=uiLabelMap.Description						         
						          rows="10"
						          disabled=false
						          placeholder = uiLabelMap.Description
						          value = inputContext.messages?if_exists
						        />
						<#if inputContext?has_content && inputContext.workEffortTypeId =="TASK">
							<script>          
								CKEDITOR.replace( 'messages',{
									customConfig : '/bootstrap/js/ckeditor/ck-custom-config.js',
									autoGrow_minHeight : CKEditorUtil.autoGrowMinHeight,
									removePlugins : CKEditorUtil.removePlugins
								});
							</script>
						</#if>
  					</div>
				</#if>
                <div class="offset-md-2 col-sm-10">
	           		<@formButton
	                     btn1type="submit"
	                     btn1label="${uiLabelMap.Save}"
	                     btn1onclick="return formSubmission();"
	                     btn2=true
	                     btn2onclick = "resetFormToReload()"
	                     btn2type="reset"
	                     btn2label="${uiLabelMap.Clear}"
	                />
	            </div>
    	</form>
	</div>
</div>
</div>		

<@partyPicker 
	instanceId="partyPicker"
	/> 
	
<script>
$(document).ready(function() {
		$('select[name="srSubTypeId"]').val('${selectedSubTypeId!}');
 		var durationOptionsList = '<option value="">Please Select</option>';
       var typeId  = $("#srTypeId").val();
	    if (typeId != "") {
	    	loadSubTypes(typeId);
	    }
		$("#srTypeId").change(function() {
	       var typeId  = $("#srTypeId").val();
	        if (typeId != "") {
	            loadSubTypes(typeId);
	        }
	    });
	    var loggedInUserId=$('#loggedInUserId').val();
		$("span.picker-window-erase").css("display", "none");
		$("span.picker-window").css("display", "none");
		onLoadDefaultElementsBehaviour();
		var optionListforLogUser="Y";
		 var selectedOwnerId  = $("#selectedOwnerId").val();
		 if(selectedOwnerId != undefined && selectedOwnerId != null){
		  if(selectedOwnerId === loggedInUserId){
		  selectedOwnerId=loggedInUserId;
		  optionListforLogUser="N";
		  }
		 	getUsers(selectedOwnerId,optionListforLogUser);
		 	getBusinessUnit(selectedOwnerId);
		 }
		 
		 getAttendees();
		
		$('#type').val($('#workEffortTypeId').val());
		$('#type').attr('readonly','readonly');
		var htmlContent='${inputContext.content!}';
		if(htmlContent){
			var editor = CKEDITOR.instances['emailContent'];
			if (editor){
				editor.setData('${inputContext.content!}');
			}
            
            }
		 $('#ownerBuDesc').val('${inputContext.ownerBuDesc!}');
		$('#ownerBuDesc').attr('readonly','readonly');
		var userName  = $("#userName").val();
	    $( "#norganizer" ).val(userName);
   		$('#norganizer').attr('readonly','readonly');
	    $("#partyId_desc").on("change", function() {
		
	});
		
		<#-- Focus the description in update -->
		if($("#messages").length > 0) {
		  $("#messages").focus(); 
		}
	    
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
		            {
		            	$("#ownerBuDesc").val(result[0].businessunitName);
		            	$("#ownerBu").val(result[0].businessId);
		            	}
		            else
		            	$("#ownerBuDesc").val("");
		        },error: function(data) {
		        	result=data;
					showAlert("error", "Error occured while fetching Business Unit");
				}
		    });
	}
	function resetFormToReload(){
    	window.location.href=window.location.href;
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
	            		if(entry.contactId==='${selectedContactId!}'){
	            			dataSourceOptions += '<option value="'+entry.contactId+'" selected>'+entry.name+'</option>';
	            		}else{
	            			dataSourceOptions += '<option value="'+entry.contactId+'">'+entry.name+'</option>';
	            		}
	            		}else{
	            		if(entry.contactId==='${selectedContactId!}'){
	            			dataSourceOptions += '<option value="'+entry.contactId+'" selected>'+entry.name+'</option>';
	            		}else{
	            			dataSourceOptions += '<option value="'+entry.contactId+'">'+entry.name+'</option>';
	            		}
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
	function getUsers(selectedOwnerId,optionListforLogUser) {
        var userOptionList = '';
        if(optionListforLogUser=="Y"){
        var loggedInUserId=$('#loggedInUserId').val();
        var userName=$('#userName').val();
        	userOptionList = '<option value="'+loggedInUserId+'">'+userName+'</option>';
        }
        $.ajax({
            type: "GET",
            url:'/common-portal/control/getUsersList',
            async: false,
            success: function(data) {
            	for (var i = 0; i < data.length; i++) {
                    var type = data[i];
                    if(selectedOwnerId && selectedOwnerId === type.userLoginId){
                		userOptionList += '<option value="'+type.userLoginId+'" selected>'+type.userName+'</option>';
	                }else{
	                	userOptionList += '<option value="'+type.userLoginId+'">'+type.userName+'</option>';
	                }            
                }
            }
        });
       $("#owner").html(userOptionList);
	}
	
	function loadSubTypes(typeId) {
        var nonSelectContent = "<span class='nonselect'>Please Select</span>";
        var subTypes = '<option value="" >Please Select</option>';
        console.log(typeId);
        $.ajax({
            type: "POST",
            url: "getIASubTypes",
            data: { "iaTypeId": typeId },
            async: false,
            success: function(data) {
                   var sourceDesc = data.results;
                   console.log("--result-----"+data);
                   for (var i = 0; i < data.length; i++) {
                        var type = data[i];
                        if(type.subTypeId==='${selectedSubTypeId!}'){
                        	subTypes += '<option value="'+type.subTypeId+'" selected>'+type.subTypeDesc+'</option>';
                        }else{
                        	subTypes += '<option value="'+type.subTypeId+'">'+type.subTypeDesc+'</option>';
                        }
                    }
            }
        });
        $("#srSubTypeId").html(subTypes);
}
function getAttendees() {
        var userOptionList = '<option value="">Please Select</option>';
        var reqOptionList = '';
         var optOptionList = '';
        
        
        $.ajax({
            type: "GET",
            url:'/common-portal/control/getAttendeeList',
            data: {"partyId": $("#cNo").val(), "workEffortId": "${inputContext.activityId!}", "externalLoginKey": "${requestAttributes.externalLoginKey!}"},
            async: false,
            success: function(data) {
            	if (data) {
	            if(data.responseMessage=="success"){
	            	
	            	for (var i = 0; i < data.attendeesList.length; i++) {
	            		var entry = data.attendeesList[i];
	            		if(entry!=null){
	            			
	            			if(entry.selected=="opt"){
	            				optOptionList += '<option value="'+entry.partyId+'" selected>'+entry.userName+'</option>';
	            			}else if(entry.selected=="req"){	            				
	            				reqOptionList += '<option value="'+entry.partyId+'" selected>'+entry.userName+'</option>';
	            			}          		
		            		else{
		            			userOptionList += '<option value="'+entry.partyId+'">'+entry.userName+'</option>';
		            	    }
	            	  }
	            	}
	            	reqOptionList+=userOptionList;
	            	optOptionList+=userOptionList;
	            	}}
	            	}
        });       
       $("#requiredAttendees").html(reqOptionList);
       $("#optionalAttendees").html(optOptionList);
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