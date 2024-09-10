<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<#include "component://sr-portal/webapp/sr-portal/services/findCustomerModal.ftl"/>
<#include "component://sales-portal/webapp/sales-portal/activities-home/modalForActivity.ftl">
<#include "component://common-portal/webapp/common-portal/lib/picker_macro.ftl"/>
<#include "component://uiadv-portal/webapp/uiadv-portal/lib/mobi_scroll_mobile_macros.ftl"/>
<script src="/bootstrap/js/ckeditor/ckeditor.js" type="text/javascript"></script>
<script src="/bootstrap/js/ckeditor/ck-custom-functions.js" type="text/javascript"></script>
<script type="text/javascript" src="/common-portal-resource/js/ag-grid/activity/validation-activity.js"></script>
<script type="text/javascript" src="/common-portal-resource/js/ag-grid/activity/activity-utils.js"></script>

<#if inputContext.workEffortTypeId! =="EMAIL">
<script type="text/javascript" src="/account-portal-resource/js/emailActivity.js"></script>
</#if>

<div class="row">
	<div id="main" role="main">
		<div id="" class="col-lg-12 col-md-12 col-sm-12 dash-panel">
		
		<#assign extra="">
		<#if ownerBookedCalSlot?has_content>
		<#assign extra="<span style='color: red'>Release appointment to enable edit for schedule date</span>">
		</#if>
        	<@pageSectionHeader title="Activity Details" extra=extra/>
     	
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
       <#elseif screen_Name?if_exists == "SERVICE_REQUEST">
     		<#assign screenRedirect = "sr-portal">
       <#elseif screen_Name?if_exists == "REBATE">
     		<#assign screenRedirect = "rebate-portal">
       <#elseif screen_Name?if_exists == "CUSTOMER">
     		<#assign screenRedirect = "customer-portal">				
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
	         <#if inputContext.workEffortTypeId =="TASK">
	         	<#if domainEntityType?has_content && (domainEntityType == 'REBATE' || domainEntityType == 'ACCOUNT' || domainEntityType == 'OPPORTUNITY')>
	         	<#else>
	         	<span id="book-appointment" class="btn btn-xs btn-primary"><i class="fa fa-edit" aria-hidden="true"></i> Schedule Technician</span>
	         	<span id="release-appointment" class="btn btn-xs btn-primary" data-toggle="confirmation" title="Are you sure to release calendar booking ?"><i class="fa fa-edit" aria-hidden="true"></i> Release Technician</span>
	         	</#if>
	         </#if>
	         <#if inputContext.workEffortTypeId =="TASK">
	          <span id="save-appointment" class="btn btn-xs btn-primary" onclick="javascript:return mainformSubmit();"><i class="fa fa-save" aria-hidden="true"></i> Save</span>
	         </#if>
	         <a href="/${screenRedirect!}/control/viewActivity?workEffortId=${inputContext.activityId!}&domainEntityType=${screen_Name!}" class="btn btn-xs btn-primary">
	         <i class="fa fa-chevron-circle-left" aria-hidden="true"></i> Back
	         </a>
	   	  </div>
    	</#if>
  <#--end-->
      	<form method="post" action="<#if (parameters.srNumber)?has_content>updateActivityEvent<#else>updateActivityEvent</#if>" class="form-horizontal" id="mainFromId" name="mainFrom" novalidate="novalidate" data-toggle="validator" onsubmit="return submitActivityForm();">
       		<input type="hidden" name="custRequestId" value="${(parameters.srNumber)!}"/>
                	<div class="col-md-12 col-md-6 col-sm-12">
                		<@inputHidden name="ownerBookedCalSlots" id="ownerBookedCalSlots" value="${ownerBookedCalSlot!}"/>
                    	<#assign cifNo = '${requestParameters.partyId!}' >
                    	<@inputHidden name="cNo" id="cNo" value = "${cifNo!}"/>
                    	<@inputHidden name="inspActWorkTypeIds" id="inspActWorkTypeIds" value = "${inspActWorkTypeIds!}"/>
                    	<#assign salesOpportunityId = '${requestParameters.salesOpportunityId!}' >
                    	<@inputHidden name="salesOpportunityId" id="salesOpportunityId" value = "${salesOpportunityId!}"/>
                    	<@inputHidden  id="selectedOwnerId" value="${selectedOwnerId?if_exists}" />
                    	<@inputHidden  id="domainEntityType" value="${(parameters.domainEntityType)!}" />
                    	<@inputHidden  id="domainEntityId" value="${(parameters.domainEntityId)!}" />
                    	<#if inputContext.workEffortTypeId =="TASK">
                    	<#assign srType = EntityQuery.use(delegator).from("WorkEffortAssocTriplet").where("entityName", "Activity", "type", "Type", "value", "Task", "active", "Y").queryFirst()! />
                    	<#elseif inputContext.workEffortTypeId =="EMAIL">
                    	<#assign srType = EntityQuery.use(delegator).from("WorkEffortAssocTriplet").where("entityName", "Activity", "type", "Type", "value", "E-mail", "active", "Y").queryFirst()! />
                    	<#elseif inputContext.workEffortTypeId =="PHONE">
                    	<#assign srType = EntityQuery.use(delegator).from("WorkEffortAssocTriplet").where("entityName", "Activity", "type", "Type", "value", "Phone Call", "active", "Y").queryFirst()! />
                    	<#elseif inputContext.workEffortTypeId =="APPOINTMENT">
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
	                    
	                    	<#assign dynaSuffix = ''>
				            <#if domainEntityType?has_content && domainEntityType == 'REBATE'>
				            	<#assign dynaSuffix = '_RBT'>
				            <#elseif domainEntityType?has_content && domainEntityType == 'ACCOUNT'>
				            	<#assign dynaSuffix = '_ACCT'>
				            <#elseif domainEntityType?has_content && domainEntityType == 'OPPORTUNITY'>
				            	<#assign dynaSuffix = '_OPPO'>
				            <#elseif domainEntityType?has_content && domainEntityType == 'CUSTOMER'>
				            	<#assign dynaSuffix = '_CUST'>	
				            <#elseif domainEntityType?has_content && domainEntityType == 'LEAD'>
								<#assign dynaSuffix = '_LEAD'>
							</#if>
	                    
		               		<#if inputContext.workEffortTypeId =="TASK">
        						<@dynaScreen 
									instanceId="CREATE_TASK_ACTIVITY${dynaSuffix}"
									modeOfAction="UPDATE"
									/>
									
								<#elseif inputContext.workEffortTypeId =="EMAIL">
								<@dynaScreen 
									instanceId="CREATE_EMAIL_ACTIVITY${dynaSuffix}"
									modeOfAction="UPDATE"
									/>
								<#elseif inputContext.workEffortTypeId =="PHONE">
								<@dynaScreen 
									instanceId="CREATE_PHONE_ACTIVITY${dynaSuffix}"
									modeOfAction="UPDATE"
									/>
								<#elseif inputContext.workEffortTypeId =="APPOINTMENT">
								<@dynaScreen 
									instanceId="CREATE_APPOINTMENT_ACTIVITY${dynaSuffix}"
									modeOfAction="UPDATE"
									/>
								<#else>
								<@dynaScreen 
									instanceId="CREATE_TASK_ACTIVITY${dynaSuffix}"
									modeOfAction="UPDATE"
									/>
								</#if>
					</div>
					<#if inputContext?has_content && inputContext.workEffortTypeId =="EMAIL">	
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
								CKEDITOR.replace( 'emailContent',{
									customConfig : '/bootstrap/js/ckeditor/ck-custom-config.js'
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
									customConfig : '/bootstrap/js/ckeditor/ck-custom-config.js'
								});
							</script>
						</#if>
  					</div>
				</#if>
                <div class="offset-md-2 col-sm-10 p-2">
	           		<@formButton
	                     btn1type="submit"
	                     btn1label="${uiLabelMap.Save}"
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
	
<@resourcePickerFromCal 
	instanceId="avlTechnicianPicker"
	calInstanceId="TECHNICIAN_01"
	searchFromId="tech-cal-search-from"
	isActivateLegend="Y"
/>	
	
<script>

$(document).ready(function() {
	
	loadTechArrivalWindow();
	$('select[name="srSubTypeId"]').val('${selectedSubTypeId!}');
	var durationOptionsList = '<option value="">Please Select</option>';
	var typeId = $("#srTypeId").val();
	if (typeId != "") {
	    loadSubTypes(typeId);
	}
	$("#srTypeId").change(function() {
	    var typeId = $("#srTypeId").val();
	    if (typeId != "") {
	        loadSubTypes(typeId);
	    }
	});
	var loggedInUserId = $('#loggedInUserId').val();
	
	$("span.picker-window-erase").css("display", "none");
	$("span.picker-window").css("display", "none");
	    
	//onLoadDefaultElementsBehaviour();
	var optionListforLogUser = "Y";
	var selectedOwnerId = $("#selectedOwnerId").val();
	//alert(selectedOwnerId)
	if (selectedOwnerId === loggedInUserId) {
	    selectedOwnerId = loggedInUserId;
	    optionListforLogUser = "N";
	}
	
	getBusinessUnit(selectedOwnerId);
	getAttendees();
	
	$('#type').val($('#workEffortTypeId').val());
	$('#type').attr('readonly', 'readonly');
	var htmlContent = '${inputContext.content!}';
	if (htmlContent) {
	    var editor = CKEDITOR.instances['emailContent'];
	    editor.setData('${inputContext.content!}');
	}
	$('#ownerBuDesc').val('${inputContext.ownerBuDesc!}');
	$('#ownerBuDesc').attr('readonly', 'readonly');
	var userName = $("#userName").val();
	$("#norganizer").val(userName);
	$('#norganizer').attr('readonly', 'readonly');
	$("#partyId_desc").on("change", function() {
	});
    
	$('#mainFromId').validator().on('submit', function (e) {
		if (!e.isDefaultPrevented()) {
			//e.preventDefault();
			console.log('owner: '+$('#owner').val());
	  		var valid = true;
	  		var workEffortTypeId = "${inputContext.workEffortTypeId!}";
	  		var domainEntityType = "${domainEntityType!}";
			console.log("workEffortTypeId--"+workEffortTypeId+"--domainEntityType---->"+domainEntityType);
			 //(domainEntityType != "REBATE" || domainEntityType != "ACCOUNT" || domainEntityType != "OPPORTUNITY")
			if( workEffortTypeId === "TASK" && domainEntityType === "SERVICE_REQUEST"){
				console.log("workEffortTypeId-111111-"+workEffortTypeId+"--domainEntityType--11111-->"+domainEntityType);
				if ($('#statusId').val() === 'IA_MSCHEDULED' && !$('#ownerBookedCalSlots').val() && $("input[name=isSchedulingRequired]:checked").val()=='Y') {
		  			showAlert('error', 'Please select booking slot from "Schedule Task"!');
		  			valid = false;
		  			return false;
		  		}
		  		
		  		if ($('#statusId').val() === 'IA_MSCHEDULED') {
		  			if (!$('#estimatedStartDate_date').val()) {
			  			showAlert('error', 'Please fill schedule start date!');
			  			valid = false;
			  			return valid;
		  			}
		  			if (!$('#estimatedCompletionDate_date').val()) {
			  			showAlert('error', 'Please fill schedule end date!');
			  			valid = false;
			  			return valid;
		  			}
		  		}
		  		
		  		if ($('#statusId').val() === 'IA_MCOMPLETED' && $("input[name=isSchedulingRequired]:checked").val()=='Y') {
		  			$.ajax({
						type: "POST",
						url: "/common-portal/control/getActivityTimeEntryCount",
						data: {"workEffortId": '${requestParameters.workEffortId!}', "externalLoginKey": "${requestAttributes.externalLoginKey!}"},
						async: false,
						success: function (data) {   
							if (data.code == 200) {
								if (data.timeEntryCount === 0) {
									if (!confirm('Time Entry Missing. Proceed with Ending Activity?')) {
										valid = false;
									}
								}
							}
						}
					});	
		  		}
				  		
		  		$.ajax({
					type: "POST",
					url: "/common-portal/control/checkTechActivityStatus",
					data: {"workEffortId": '${requestParameters.workEffortId!}', "assignedTechLoginIds": getAssignedTechLoginIds(), "externalLoginKey": "${requestAttributes.externalLoginKey!}"},
					async: false,
					success: function (data) {   
						if (data.code == 200) {
							if (data.message) {
								var message = data.message;
								message += " Proceed with Editing Activity ?";
								if (!confirm(message)) {
									valid = false;
								}
							}
						}
					}
				});	
			} else if(domainEntityType === "REBATE" || domainEntityType === "ACCOUNT" || domainEntityType === "OPPORTUNITY") {
				
			}
	  		
	  		if (!valid) {
	  			e.preventDefault();
	  		}
	  	}
	});    

	<#if inputContext.workEffortTypeId =="TASK">

		<#if !ownerBookedCalSlot?has_content>
		prepareActivityDateInput('${inputContext.statusId!}');
		$("#statusId").change(function() {
			prepareActivityDateInput($(this).val());
		});
		</#if>
		
		$('#tech-cal-search-from input[name="estimatedStartDate_date"]').val( $('#estimatedStartDate_date').val() );
		$('#tech-cal-search-from input[name="estimatedCompletionDate_date"]').val( $('#estimatedCompletionDate_date').val() );
		$('#tech-cal-search-from input[name="estimatedStartDate_time"]').val( $('#estimatedStartDate_time').val() );
		$('#tech-cal-search-from input[name="estimatedCompletionDate_time"]').val( $('#estimatedCompletionDate_time').val() );
	
		$("#workEffortPurposeTypeId").on("change", function() {
		    console.log('workEffortPurposeTypeId: '+$(this).val());
		    if ($("#inspActWorkTypeIds").val()) {
		    	var inspActWorkTypeIds = $("#inspActWorkTypeIds").val().split(',');
		        if (inspActWorkTypeIds.includes($(this).val())) {
		        	console.log('tech inspector work type');
		        	var context = new Map();
					context.set('roleTypeIds', 'TECH_INSPECTOR');
					$('#tech-cal-search-from input[name="isResourceType"]').val( 'TECH_INSPECTOR' );
					ACTUTIL.loadOwners('${inputContext.workEffortTypeId!}', selectedOwnerId, context, "${requestAttributes.externalLoginKey!}");
					$('#calendar_type_title').html("Inspectors");
		        } else {
		        	$('#tech-cal-search-from input[name="isResourceType"]').val( '' );
		        	ACTUTIL.loadOwners('${inputContext.workEffortTypeId!}', selectedOwnerId, null, "${requestAttributes.externalLoginKey!}");
		        	$('#calendar_type_title').html("Technicians");
		        }
		    }
		    if($(this).val() == "WEPT_INV"){
		    	$('#statusId').dropdown('set selected', "IA_OTHER");
		    }
		});
	
		$("input[name=isSchedulingRequired]").on("change", function() {
		    console.log('isSchedulingRequired: '+$(this).val());
		    prepareActivityDateInput($("#statusId").val());
		    if ($(this).val()=='Y') {
		    	<#if workStartTime?has_content>
				if ($('#estimatedStartDate_time').val() == '0:00') {
					$('#estimatedStartDate_time').timepicker('setTime', '${StringUtil.wrapString(workStartTime)}');
				}
				</#if>
				<#if workEndTime?has_content>
				if ($('#estimatedCompletionDate_time').val() == '0:00') {
					$('#estimatedCompletionDate_time').timepicker('setTime', '${StringUtil.wrapString(workEndTime)}');
				}
				</#if>
		    }
		});
	
		<#if inputContext.currentStatusId =="IA_MIN_PROGRESS" || inputContext.currentStatusId =="IA_MCOMPLETED">
		$('input[name=isSchedulingRequired]').prop("disabled", true);
		</#if>

	</#if>
	
	<#if domainEntityType?has_content && domainEntityType == 'SERVICE_REQUEST'>
	ACTUTIL.loadSrAssocParties('${requestParameters.domainEntityId!}', 'contactId', '${inputContext.contactId!}', "${requestAttributes.externalLoginKey!}");
	<#else>
		let inputData = new Map();
	    <#if mainAssocPartyRoleTypId?has_content && mainAssocPartyRoleTypId=='CUSTOMER'>
		inputData.set('isIncludeMainParty', 'Y');
		</#if>
		ACTUTIL.loadContacts('${requestParameters.partyId!}', '${inputContext.contactId!}', 'contactId', inputData, "${requestAttributes.externalLoginKey!}");
	</#if>
	
	ACTUTIL.loadOwners('${inputContext.workEffortTypeId!}', selectedOwnerId, null, "${requestAttributes.externalLoginKey!}");

});
function mainformSubmit(){
	$('#mainFromId').submit();
}
function loadTechArrivalWindow(){
	var arrivalWindowVal = "${techArrivalWindows!}";
	var arrivalWindow = "${requestParameters.arrivalWindow!inputContext.arrivalWindow!}";
	var arrivalList = arrivalWindowVal.split(",");
	var options="<option value=''></option>";
	if(arrivalList != null && arrivalList != "" && arrivalList != "undefined"){
		$.each(arrivalList,function(i){
			var hrLabel = ' hr';
        	if (arrivalList[i] > 1) {
        		hrLabel = ' hrs';
        	}
			if(arrivalWindow != null && arrivalWindow != "" && arrivalWindow != "undefined" && arrivalWindow==arrivalList[i]){
				options += '<option value="'+arrivalList[i]+'" selected>'+arrivalList[i]+hrLabel+'</option>';
			} else
		   		options += '<option value="'+arrivalList[i]+'">'+arrivalList[i]+hrLabel+'</option>';
		});
		$("#arrivalWindow").html( options );
		$("#arrivalWindow").dropdown('refresh');
	}
}

$(function() {
    $("#owner").change(function() {
        var owner = $("#owner").val();
        if (owner != undefined && owner != null)
            getBusinessUnit(owner);
    });
});

function getBusinessUnit(owner) {
    var owner = owner;
    $.ajax({
        type: "POST",
        url: "getBusinessUnitName",
        async: false,
        data: {
            "owner": owner
        },
        success: function(data) {
            result = data;
            if (result && result[0] != undefined && result[0].businessunitName != undefined) {
                $("#ownerBuDesc").val(result[0].businessunitName);
                $("#ownerBu").val(result[0].businessId);
            } else
                $("#ownerBuDesc").val("");
        },
        error: function(data) {
            result = data;
            showAlert("error", "Error occured while fetching Business Unit");
        }
    });
}

function resetFormToReload() {
    window.location.href = window.location.href;
}

function onLoadDefaultElementsBehaviour() {

    var today = new Date();
    var dd = String(today.getDate()).padStart(2, '0');
    var mm = String(today.getMonth() + 1).padStart(2, '0');
    var yyyy = today.getFullYear();

    //today = dd + '/' + mm + '/' + yyyy;
    //$('#taskDate').val(today.toLocaleString([],{hour12: false}).replace(",",""));


    var dataSourceOptions = '';
   // var partyId = $("#partyId_val").val();
     var partyId = $("#partyId").val();
    $.ajax({
        type: "POST",
        url: "/common-portal/control/getPrimaryContacts",
        data: {
            "partyId": partyId,
            "externalLoginKey": "${requestAttributes.externalLoginKey!}"
        },
        async: false,
        success: function(data) {
            if (data) {
                if (data.responseMessage == "success") {
                    for (var i = 0; i < data.partyRelContacts.length; i++) {
                        var entry = data.partyRelContacts[i];
                        if (entry.selected != null) {
                            if (entry.contactId === '${selectedContactId!}') {
                                dataSourceOptions += '<option value="' + entry.contactId + '" selected>' + entry.name + '</option>';
                            } else {
                                dataSourceOptions += '<option value="' + entry.contactId + '">' + entry.name + '</option>';
                            }
                        } else {
                            if (entry.contactId === '${selectedContactId!}') {
                                dataSourceOptions += '<option value="' + entry.contactId + '" selected>' + entry.name + '</option>';
                            } else {
                                dataSourceOptions += '<option value="' + entry.contactId + '">' + entry.name + '</option>';
                            }
                        }
                    }
                } else {
                    for (var i = 0; i < data.length; i++) {
                        var entry = data[i];
                        dataSourceOptions += '<option value="' + entry.contactId + '">' + entry.name + '</option>';
                    }

                }
            }
        }

    });

    $("#contactId").html(dataSourceOptions);
    $("#contactId").dropdown('refresh');

}

function getUsers(selectedOwnerId) {
    var userOptionList = '<option value=""></option>';
    
    var ownerArr = selectedOwnerId.split(',');
    console.log('ownerArr: '+ownerArr);
    for(var i = 0; i < ownerArr.length; i++) {
		ownerArr[i] = ownerArr[i].replace(/^\s*/, "").replace(/\s*$/, "");
	}
    $.ajax({
        type: "GET",
        url: '/common-portal/control/getUsersList?roleTypeId=ACT_OWNER&isIncludeLoggedInUser=Y&externalLoginKey=${requestAttributes.externalLoginKey!}',
        async: false,
        success: function(data) {
            for (var i = 0; i < data.length; i++) {
                var type = data[i];
                
                if (ownerArr.includes(type.userLoginId)) {
                    userOptionList += '<option value="' + type.userLoginId + '" selected>' + type.userName + ' (' + type.roleDesc + ') </option>';
                } else {
                    userOptionList += '<option value="' + type.userLoginId + '">' + type.userName + ' (' + type.roleDesc + ') </option>';
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
        data: {
            "iaTypeId": typeId
        },
        async: false,
        success: function(data) {
            var sourceDesc = data.results;
            console.log("--result-----" + data);
            for (var i = 0; i < data.length; i++) {
                var type = data[i];
                if (type.subTypeId === '${selectedSubTypeId!}') {
                    subTypes += '<option value="' + type.subTypeId + '" selected>' + type.subTypeDesc + '</option>';
                } else {
                    subTypes += '<option value="' + type.subTypeId + '">' + type.subTypeDesc + '</option>';
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
        url: '/common-portal/control/getAttendeeList',
        data: {
            "partyId": $("#cNo").val(),
            "workEffortId": "${inputContext.activityId!}",
            "externalLoginKey": "${requestAttributes.externalLoginKey!}"
        },
        async: false,
        success: function(data) {
            if (data) {
                if (data.responseMessage == "success") {

                    for (var i = 0; i < data.attendeesList.length; i++) {
                        var entry = data.attendeesList[i];
                        if (entry != null) {

                            if (entry.selected == "opt") {
                                optOptionList += '<option value="' + entry.partyId + '" selected>' + entry.userName + '</option>';
                            } else if (entry.selected == "req") {
                                reqOptionList += '<option value="' + entry.partyId + '" selected>' + entry.userName + '</option>';
                            } else {
                                userOptionList += '<option value="' + entry.partyId + '">' + entry.userName + '</option>';
                            }
                        }
                    }
                    reqOptionList += userOptionList;
                    optOptionList += userOptionList;
                }
            }
        }
    });
    $("#requiredAttendees").html(reqOptionList);
    $("#optionalAttendees").html(optOptionList);
}

function formSubmission() {
    var valid = true;
    if ($('#partyId_val').val() == "") {
        showAlert('error', 'Please select Customer');
        valid = false;
    } else {
        $('#cNo').val($('#partyId_val').val());
    }
    return valid;
}
</script>