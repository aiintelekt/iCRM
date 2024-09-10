<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<#include "component://sales-portal/webapp/sales-portal/activities-home/modalForActivity.ftl">
<#include "component://sr-portal/webapp/sr-portal/services/findCustomerModal.ftl"/>
<script src="/bootstrap/js/ckeditor/ckeditor.js" type="text/javascript"></script>
<script src="/bootstrap/js/ckeditor/ck-custom-functions.js" type="text/javascript"></script>
<#include "component://common-portal/webapp/common-portal/lib/picker_macro.ftl"/>
<script type="text/javascript" src="/account-portal-resource/js/emailActivity.js"></script>
<script type="text/javascript" src="/common-portal-resource/js/ag-grid/activity/validation-activity.js"></script>
<style>
.create-email .header-title{
	padding-bottom: 0px;
}
.create-email .dash-panel{
	padding: 0px 0px 0px 0px !important;
}
</style>
    <div class="row">
        <div id="main" role="main">
			<#assign salesOpportunityId = '${requestParameters.salesOpportunityId!}' >
			<#assign workEffortId = '${requestParameters.workEffortId!}' >
			<#assign cifNo = '${requestParameters.partyId!}' >
			<#if salesOpportunityId?has_content>
				<#assign roleList = (Static["org.ofbiz.entity.util.EntityQuery"].use(delegator).select("partyId","roleTypeId").from("SalesOpportunityRole").where("salesOpportunityId",salesOpportunityId).queryFirst())?if_exists />
				<#if roleList?has_content>
					<#assign partyId = "${roleList.partyId?if_exists}">
					<#assign partyIdtnList = (Static["org.ofbiz.entity.util.EntityQuery"].use(delegator).select("idValue").from("PartyIdentification").where("partyId",partyId).queryFirst())?if_exists />
					<#if partyIdtnList?has_content>
						<#assign cifNo = "${partyIdtnList.idValue?if_exists}" >	
					</#if>
				</#if>
        		<#assign extraLeft='
                            <a id="createProspect" title="Create Prospect" href="#" class="btn btn-primary btn-xs" onclick="#"><i class="fa fa-user-plus"></i> Create Prospect</a>
                            <a id="createNonCrm" title="Create Non CRM" href="#" class="btn btn-primary btn-xs" onclick="#"><i class="fa fa-user-times"></i>  Create Non CRM</a>
                           ' />
        	<#elseif workEffortId?has_content>
				<#assign roleList = (Static["org.ofbiz.entity.util.EntityQuery"].use(delegator).select("partyId","roleTypeId").from("WorkEffortPartyAssignment").where("workEffortId",workEffortId).queryFirst())?if_exists />
				<#if roleList?has_content>
					<#assign partyId = "${roleList.partyId?if_exists}">
					<#assign partyIdtnList = (Static["org.ofbiz.entity.util.EntityQuery"].use(delegator).select("idValue").from("PartyIdentification").where("partyId",partyId).queryFirst())?if_exists />
					<#if partyIdtnList?has_content>
						<#assign cifNo = "${partyIdtnList.idValue?if_exists}" >	
					</#if>
				</#if>
				<#assign extraLeft='
            					<a id="createProspect" title="Create Prospect" href="#" class="btn btn-primary btn-xs" onclick="#"><i class="fa fa-user-plus"></i> Create Prospect</a>
                            	<a id="createNonCrm" title="Create Non CRM" href="#" class="btn btn-primary btn-xs" onclick="#"><i class="fa fa-user-times"></i>  Create Non CRM</a>
                           	   ' 
            	/> 
        	<#else>
        		<#assign extraLeft='
                           	<a id="findcustomerSr" title="Find Customer" href="#" class="btn btn-primary btn-xs" data-toggle="modal" data-target="#findcustomer" ><i class="fa fa-search"></i> Find Customer</a>
                            <a id="createProspect" title="Create Prospect" href="#" class="btn btn-primary btn-xs" onclick="#"><i class="fa fa-user-plus"></i> Create Prospect</a>
                            <a id="createNonCrm" title="Create Non CRM" href="#" class="btn btn-primary btn-xs" onclick="#"><i class="fa fa-user-times"></i>  Create Non CRM</a>
                           ' />
        	</#if>
            
            <#assign addActivities = '
            <div class="dropdown-menu" aria-labelledby="E10007">
                <h4>Add Activities</h4>
                <a class="dropdown-item" href="/account-portal/control/addTask?partyId=${partyId?if_exists}&domainEntityType=${domainEntityType!}&domainEntityId=${domainEntityId!}&externalLoginKey=${requestAttributes.externalLoginKey!}" target="_blank"><i class="fa fa-clipboard" aria-hidden="true"></i> Task</a> 
			    <a class="dropdown-item" href="/account-portal/control/addPhoneCall?partyId=${partyId?if_exists}&domainEntityType=${domainEntityType!}&domainEntityId=${domainEntityId!}&externalLoginKey=${requestAttributes.externalLoginKey!}" target="_blank"><i class="fa fa-phone" aria-hidden="true"></i> Phone Call</a>
			    <a class="dropdown-item" href="/account-portal/control/addEmail?partyId=${partyId?if_exists}&domainEntityType=${domainEntityType!}&domainEntityId=${domainEntityId!}&externalLoginKey=${requestAttributes.externalLoginKey!}" target="_blank"><i class="fa fa-envelope" aria-hidden="true"></i> Email</a>
			    <a class="dropdown-item" href="/account-portal/control/addAppointment?partyId=${partyId?if_exists}&domainEntityType=${domainEntityType!}&domainEntityId=${domainEntityId!}&externalLoginKey=${requestAttributes.externalLoginKey!}" target="_blank"><i class="fa fa-calendar" aria-hidden="true"></i> Appointment</a>
			    <a class="dropdown-item" href="/account-portal/control/addOthers?partyId=${partyId?if_exists}&domainEntityType=${domainEntityType!}&domainEntityId=${domainEntityId!}&externalLoginKey=${requestAttributes.externalLoginKey!}" target="_blank"><i class="fa fa-plus-square" aria-hidden="true"></i> Others</a>
            </div>
            ' />
            <#assign toggleDropDownData = {"E10007":addActivities!} />
           <#--  <div class="card-head margin-adj mt-2">
                <@AppBar  
	                appBarId="ACTION_APP_BAR"
	                appBarTypeId="ACTION"
	                id="appbar1"
	                extra=extra!
	                toggleDropDownData=toggleDropDownData!
	                isEnableUserPreference=true
	                />
            </div>
            <div class="card-header mt-3" id="cp"> 
           		<@dynaScreen 
	                instanceId="ACCT_BASIC_INFO"
	                modeOfAction="VIEW"
	            />
           </div> -->
           <div class="col-lg-12 col-md-12 col-sm-12 dash-panel create-email">
        	 <@sectionFrameHeader   title="${uiLabelMap.createMailActivity!}" />
            

            <form method="post" action="<#if (parameters.salesOpportunityId)?has_content>addSalesEmailEvent<#else>addEmailEvent</#if>" id="SrEmail" class="form-horizontal" name="phone" novalidate="novalidate" data-toggle="validator" onsubmit="return submitEmailActivityForm();" enctype="multipart/form-data">
                <#assign partyId = '${requestParameters.partyId!}' >
                <@inputHidden name="partyId" id="partyId" value = "${partyId!}"/>
                <@inputHidden name="ccEmailIds" id="ccEmailIds" value = ""/>

                <#assign primaryEmail=""/>
                <#assign PrimaryContact=Static["org.groupfio.common.portal.util.PartyPrimaryContactMechWorker"].getPartyPrimaryContactMechValueMaps(delegator, partyId, Static["org.ofbiz.base.util.UtilMisc"].toMap("isRetriveEmail", true),true)!>
        		<#if PrimaryContact?has_content> 
				<#assign primaryEmail=PrimaryContact.get("EmailAddress")!/>
				 <@inputHidden name="primaryEmail" id="primaryEmail" value="${primaryEmail!}" />
				</#if>
        		<input type="hidden" id="domainEntityType" name="domainEntityType" value="${(parameters.domainEntityType)!}"/>
        		<input type="hidden" id="domainEntityId"  name="domainEntityId" value="${(parameters.domainEntityId)!}"/>
        		<input type="hidden" id="primaryEmailId"   value="${actionBarContext.primaryEmail!}"/>
        		<div>
                        <div>
                            <@inputHidden name="cNo" id="cNo" value = "${cifNo!}"/>
                            <@inputHidden name="ownerBu" id="ownerBu" />
                            <#assign srType = EntityQuery.use(delegator).from("WorkEffortAssocTriplet").where("entityName", "Activity", "type", "Type", "value", "E-mail", "active", "Y").queryFirst()! />
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
	                    <#assign userLoginEmail=""/>		                     				
	    				<#assign userloginContact=Static["org.groupfio.common.portal.util.PartyPrimaryContactMechWorker"].getPartyPrimaryContactMechValueMaps(delegator, userLogin.partyId, Static["org.ofbiz.base.util.UtilMisc"].toMap("isRetriveEmail", true),true)!>
	            		<#if userloginContact?has_content> 
	    					<#assign userLoginEmail=userloginContact.get("EmailAddress")!/>
	    					<@inputHidden name="loginEmail" id="loginEmail" value="${userLoginEmail!}" />
	    				</#if>
	    				<@inputHidden name="contactId" id="contactId" value="" />
		                <@dynaScreen 
			                instanceId="CREATE_EMAIL_ACTIVITY_CUST"
			                modeOfAction="CREATE"
			             />
					       	
                   	 </div>

                    <div class="row p-2">
                        <div class="col-md-12 col-lg-12 col-sm-12" id="emlContent">
                        <@textareaLarge
			               id="emailContent"
			               groupId = "htmlDisplay"
			               label=uiLabelMap.HTML
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
                    
                    <div class="row padding-r">
                    	<div class="col-md-6 col-sm-6">
                    	<@inputRowFilePicker 
						id="attachment"
						label="Attachments"
						placeholder="Select Attachment"
						/>
                    	</div>
                    </div>

                    <div class="row">
                         <div class="offset-md-2 col-sm-10 pb-3">
                           <div class="text-left ml-1">
                              <@formButton
                                 btn1type="submit"
                                 btn1label="Send"
                                 btn1onclick="return formSubmission();"
                                 btn2=true
                                 btn2onclick = "resetFormToReload()"
                                 btn2type="reset"
                                 btn2label="${uiLabelMap.Clear}"
                               />
                           </div>
                        </div>
                    </div>
                </div>
            </form>
        </div>
    </div>
    </div>
    <@partyPicker 
	instanceId="partyPicker"
	/>
	<@templatePicker 
	instanceId="templatePicker"
	/>
	
<script>
$(document).ready(function() {
	CKEDITOR.config.height = 400;
	var party = $("#partyId").val();
	if (party != "" && party != 'undefined') {
		$("#partyId_row").find("span.picker-window-erase").css("display", "none");
		$("#partyId_row").find("span.picker-window").css("display", "none");
	}
	onLoadDefaultElementsBehaviour();
	$('#type').val($('#workEffortTypeId').val());
	$('#type').attr('readonly', 'readonly');
	$('#ownerBuDesc').attr('readonly', 'readonly');
	//		$("span.picker-window-erase").css("display", "none");
	//		$("span.picker-window").css("display", "none");
	var cNo = $("#cNo").val();
	if (cNo != null && cNo != undefined && cNo != "") {
		loadContacts();
		loadCcContacts();
	}
	var defaultFrom = $('#loginEmail').val();
	if (defaultFrom != null && defaultFrom != "" & defaultFrom != undefined) {
		var defaultLoggedInUserEmail = '<option value="' + defaultFrom + '" selected>' + defaultFrom + '</option>';
		$("#nsender").html(defaultLoggedInUserEmail);
		$("#nsender").dropdown('refresh');
	}

	$("#partyId_desc").val('${actionBarContext.name!}');
	$("#partyId_Val").val($("#partyId").val());
	$("#owner").change(function() {
		var owner = $("#owner").val();
		if (owner != undefined && owner != null)
			getBusinessUnit(owner);
	});
	var userName = $("#userName").val();
	var loggedInUserId = $("#loggedInUserId").val();
	if (loggedInUserId != undefined && loggedInUserId != null)
		getBusinessUnit(loggedInUserId);
	getUsers(loggedInUserId, userName);
	$("#partyId_desc").on("change", function() {

		var nonSelectContent = "<span class='nonselect'>Select Contact</span>";
		var dataSourceOptions = '';
		var ntoOptions = '';
		var partyId = $("#partyId_val").val();
		$.ajax({
			type: "POST",
			url: "/common-portal/control/getPrimaryContacts",
			data: {
				"partyId": partyId,
				"toEmailDD": "Y",
				"externalLoginKey": "${requestAttributes.externalLoginKey!}"
			},
			async: false,
			success: function(data) {
				if (data) {
					if (data.responseMessage == "success") {
						for (var i = 0; i < data.partyRelContacts.length; i++) {
							var entry = data.partyRelContacts[i];
							if (entry.selected != null) {
								dataSourceOptions += '<option value="' + entry.contactId + '" selected>' + entry.name + '</option>';
							} else {
								dataSourceOptions += '<option value="' + entry.contactId + '">' + entry.name + '</option>';
							}
						}
						for (var i = 0; i < data.toMapList.length; i++) {
							var entry = data.toMapList[i];
							if (entry != null) {
								ntoOptions += '<option value="' + entry.EmailAddress + '">' + entry.EmailAddress + '</option>';
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

		var custNtoOptions = "";
		var custNtoOptions = '<option value="' + '${actionBarContext.primaryEmail!}' + '" selected>' + '${actionBarContext.primaryEmail!}' + '</option>';
		$("#nto").html(custNtoOptions);

		$("#nto").dropdown('refresh');
	});
});

$(function() {
	var partyId = $("#partyId").val();
	var link = document.getElementById("E10010");
	link.setAttribute('href', "/customer-portal/control/addEmail?partyId=" + partyId + "&domainEntityType=CUSTOMER&domainEntityId=" + partyId);
	var userName = $("#userName").val();
});

function onLoadDefaultElementsBehaviour() {
	var today = new Date();
	var dd = String(today.getDate()).padStart(2, '0');
	var mm = String(today.getMonth() + 1).padStart(2, '0');
	var yyyy = today.getFullYear();
	var hh = today.getHours();
	var m = today.getSeconds();
	today = mm + '/' + dd + '/' + yyyy + " " + hh + ":" + m;
	$('#startTime').val(today.toLocaleString([], {
		hour12: false,
		dateStyle: "short",
		timeStyle: "short"
	}).replace(",", ""));
}

function getUsers(loggedInUserId, userName) {
	var userOptionList = '<option value="' + loggedInUserId + '">' + userName + '</option>';
	$.ajax({
		type: "GET",
		url: '/common-portal/control/getUsersList',
		async: false,
		success: function(data) {
			for (var i = 0; i < data.length; i++) {
				var type = data[i];
				userOptionList += '<option value="' + type.userLoginId + '">' + type.userName + '</option>';
			}
		}
	});
	$("#owner").html(userOptionList);
}

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
			if (result && result[0] != undefined && result[0].businessId != undefined) {
				$("#ownerBu").val(result[0].businessId);
				$("#ownerBuDesc").val(result[0].businessunitName);
			} else {
				$("#ownerBu").val("");
				$("#ownerBuDesc").val("");
			}
		},
		error: function(data) {
			result = data;
			showAlert("error", "Error occured while fetching Business Unit");
		}
	});
}

function loadContacts() {
	var dataSourceOptions = "";
	var ntoOptions = "";
	var partyId = $("#partyId_val").val();
	$.ajax({
		type: "POST",
		url: "/common-portal/control/getPrimaryContacts",
		data: {
			"partyId": partyId,
			"toEmailDD": "Y",
			"externalLoginKey": "${requestAttributes.externalLoginKey!}"
		},
		async: false,
		success: function(data) {
			if (data) {
				if (data.responseMessage == "success") {
					for (var i = 0; i < data.partyRelContacts.length; i++) {
						var entry = data.partyRelContacts[i];
						if (entry.selected != null) {
							dataSourceOptions += '<option value="' + entry.contactId + '" selected>' + entry.name + '</option>';
						} else {
							dataSourceOptions += '<option value="' + entry.contactId + '">' + entry.name + '</option>';
						}
					}
					for (var i = 0; i < data.toMapList.length; i++) {
						var entry = data.toMapList[i];
						if (entry != null) {
							if (entry.selected != null) {
								ntoOptions += '<option value="' + entry.EmailAddress + '" selected>' + entry.EmailAddress + '</option>';
								$('#contactId').val(entry.primaryContactId);
							} else {
								ntoOptions += '<option value="' + entry.EmailAddress + '">' + entry.EmailAddress + '</option>';
								if (i == 0) {
									$('#contactId').val(entry.primaryContactId);
								}
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

	var custNtoOptions = "";
	var custNtoOptions = '<option value="' + '${actionBarContext.primaryEmail!}' + '" selected>' + '${actionBarContext.primaryEmail!}' + '</option>';
	$("#nto").html(custNtoOptions);

	$("#nto").dropdown('refresh');
}

function resetFormToReload() {
	window.location.href = window.location.href;
}

function formSubmission() {
	var valid = true;
	if ($('#partyId_val').val() == "") {
		showAlert('error', 'Please select Customer');
		valid = false;
	} else {
		//$('#cNo').val($('#partyId_val').val());
	}
	var htmlContent = CKEDITOR.instances["emailContent"].getData();
	$('#emailContent').val(htmlContent);
	
	var cc = "";
    var ccEmailArray = $("#ncc").val();
    var type = $("#ncc").attr("type");
    if (type && type=="text"){
    	if (ccEmailArray){
        	ccEmailArray=ccEmailArray.split(",");
        }
    }
    for (var i = 0; i < ccEmailArray.length; i++) {
        var email = ccEmailArray[i];
        if (cc == "") {
            cc = email;
        } else {
            cc = cc + "," + email;
        }
    }
    $("#ccEmailIds").val(cc);
	
	return valid;
}
</script>
