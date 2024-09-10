<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<#include "component://ticket-portal/webapp/ticket-portal/services/findCustomerModal.ftl"/>
<#include "component://sales-portal/webapp/sales-portal/activities-home/modalForActivity.ftl">
<#include "component://common-portal/webapp/common-portal/lib/picker_macro.ftl"/>
<script type="text/javascript" src="/ticket-portal-resource/js/services/addSrActivity.js"></script>
<div class="row">
	<div id="main" role="main">
    	<#assign workEfforts = (Static["org.ofbiz.entity.util.EntityQuery"].use(delegator).select("workEffortId").from("WorkEffort").where("workEffortTypeId","85001").maxRows(5).orderBy("-estimatedStartDate").queryList())?if_exists />
        <#assign workEffortIds = Static["org.ofbiz.entity.util.EntityUtil"].getFieldListFromEntityList(workEfforts, "workEffortId", true)>
        <#assign extraLeftActivity=''/>
        <#list workEffortIds as workEffortId>
        	<#assign extraLeftActivity= '${extraLeftActivity}' + '<a id=task1 title="${workEffortId}" href="#" class="btn btn-primary btn-xs" onclick="#"> ${workEffortId}</a>'/>
       	</#list>
       
		<div class="card-head margin-adj mt-0 d-none">
            ${screens.render("component://ticket-portal/widget/services/ServicesScreens.xml#CustomerForAddServiceRequest")}
        </div>
        <div class="col-lg-12 col-md-12 col-sm-12">
           <@pageSectionHeader title="Activity Details"/>
        </div>

        <form method="post" action="<#if (parameters.srNumber)?has_content>addSRAppointmentEvent<#else>srAddAppointmentEvent</#if>" id="SrAppointment" class="form-horizontal" name="phone" novalidate="novalidate" data-toggle="validator">
        	<input type="hidden" name="custRequestId" value="${(parameters.srNumber)!}"/>
        	<input type="hidden" name="domainEntityType" value="${(parameters.domainEntityType)!}"/>
        		<input type="hidden" name="domainEntityId" value="${(parameters.domainEntityId)!}"/>
                	<div class="col-md-12 col-md-6 col-sm-12 ">
	                	<#assign cifNo = '${requestParameters.partyId!}' >
	                    <@inputHidden name="cNo" id="cNo" value = "${cifNo!}"/>
                        <#assign srType = EntityQuery.use(delegator).from("WorkEffortAssocTriplet").where("entityName", "Activity", "type", "Type", "value", "Appointment", "active", "Y").queryFirst()! />
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
	                    
		                <@dynaScreen 
			                instanceId="CREATE_APPOINTMENT_ACTIVITY"
			                modeOfAction="CREATE"
			             />
                    </div>
                    <div class="col-md-12 col-lg-12 col-sm-12 ">
     					<@textareaLarge  label="Description" id="messages" rows="3"/>
  					</div>
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
<@partyPicker 
	instanceId="partyPicker"
	/>     
<script>
$(document).ready(function() {
	onLoadDefaultElementsBehaviour();
		 var owner  = $("#owner").val();
		 if(owner != undefined && owner != null)
		 	getBusinessUnit(owner);
		 	
		 getAttendees();	
		 $('#type').val($('#workEffortTypeId').val());
		$('#type').attr('readonly','readonly');
		$('#ownerBuDesc').attr('readonly','readonly');
		var userName  = $("#userName").val();
	    var loggedInUserId  = $("#loggedInUserId").val();
	    if(loggedInUserId != undefined && loggedInUserId != null)
			   	getBusinessUnit(loggedInUserId);
	    getUsers(loggedInUserId,userName);		
	});
	
	$(function() {
		$("#owner").change(function() {
			var owner  = $("#owner").val(); 
		   	if(owner != undefined && owner != null)
		   		getBusinessUnit(owner);	    
		});
		
		 $("#partyId_desc").on("change", function() {

		var nonSelectContent = "<span class='nonselect'>Select Source</span>";
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
		getAttendees();
	});
		
		$("#findcustomerSr").click(function() {
			loadAgGrid();
		});
	});
	function resetFormToReload(){
    	window.location.href=window.location.href;
    }
	function onLoadDefaultElementsBehaviour(){
        
         var today = new Date();
		var dd = String(today.getDate()).padStart(2, '0');
		var mm = String(today.getMonth() + 1).padStart(2, '0');
		var yyyy = today.getFullYear();
		var hh= today.getHours();
		var m= today.getSeconds();
		today = mm + '/' + dd + '/' + yyyy +" "+hh+":"+m;
		$('#actualStartDate').val(today.toLocaleString([],{hour12: false,dateStyle:"short",timeStyle:"short"}).replace(",",""));
		$('#estimatedStartDate').val(today.toLocaleString([],{hour12: false,dateStyle:"short",timeStyle:"short"}).replace(",",""));
    }
	function getBusinessUnit(owner) {
		var owner = owner;
	    	$.ajax({
		    	type: "POST",
		        url: "getBusinessUnitName",
		        async: false,
		        data: { "owner": owner },
		        success: function(data) {
		        	result=data;
		            if(result && result[0] != undefined && result[0].businessunitName != undefined){
		            	$("#ownerBu").val(result[0].businessId);
		            	$("#ownerBuDesc").val(result[0].businessunitName);
		            	}
		            else
		            	$("#ownerBu").val("");
		        },error: function(data) {
		        	result=data;
					showAlert("error", "Error occured while fetching Business Unit");
				}
		    });
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
	function getAttendees() {
        var userOptionList = '<option value="">Please Select</option>';
        $.ajax({
            type: "GET",
            url:'/common-portal/control/getAttendeeList',
            data: {"partyId": $("#partyId_val").val(), "externalLoginKey": "${requestAttributes.externalLoginKey!}"},
            async: false,
            success: function(data) {
            	if (data) {
	            if(data.responseMessage=="success"){
	            	for (var i = 0; i < data.attendeesList.length; i++) {
	            		var entry = data.attendeesList[i];
	            		if(entry!=null){
	            		userOptionList += '<option value="'+entry.partyId+'">'+entry.userName+'</option>';
	            		}
	            	}
	            }
	            }
	            }
        });
       $("#requiredAttendees").html(userOptionList);
       $("#optionalAttendees").html(userOptionList);
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