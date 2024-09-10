<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>

<script type="text/javascript" src="/common-portal-resource/js/ag-grid/activity/activity-utils.js"></script>

<#include "component://common-portal/webapp/common-portal/lib/picker_macro.ftl"/>
<div class="row">
	<div id="main" role="main" class="pd-btm-title-bar">
        
	<#-- <#assign extraLeft='
			<a id="findcustomerSr" title="Find Customer" href="#" class="btn btn-primary btn-xs" data-toggle="modal" data-target="#findcustomer" ><i class="fa fa-search"></i> Find Customer</a>
    		<a id="createProspect" title="Create Prospect" href="#" class="btn btn-primary btn-xs" onclick="#"><i class="fa fa-user-plus"></i> Create Prospect</a>
    		<a id="createNonCrm" title="Create Non CRM" href="#" class="btn btn-primary btn-xs" onclick="#"><i class="fa fa-user-times"></i>  Create Non CRM</a>
   	   '/> 
            
        <@sectionFrameHeader  title="Add Phone Calls "  extraLeft=extraLeft  />  -->
        
         <#assign extraLeft='
	        <a id=task1" title="Task1" href="#" class="btn btn-primary btn-xs" onclick="#"> Task 1</a>
	        <a id="task2" title="Task2" href="#" class="btn btn-primary btn-xs" onclick="#">Task 2</a>
	        <a id="task3" title="Task3" href="#" class="btn btn-primary btn-xs" onclick="#">Task 3</a>
	        <a id="task4" title="Task4" href="#" class="btn btn-primary btn-xs" onclick="#">Task 4</a>
	        <a id="task5" title="Task5" href="#" class="btn btn-primary btn-xs" onclick="#">Task 5</a>
	        ' />
       <#--  <@sectionFrameHeader  title="Recently Viewed:"  extraLeft=extraLeft  /> -->
        
        <#assign addActivities = '
        	<div class="dropdown-menu" aria-labelledby="E10007">
                <h4>Add Activities</h4>
                <a class="dropdown-item" href="/lead-portal/control/createTaskActivity?partyId=${partyId?if_exists}&domainEntityType=${domainEntityType!}&domainEntityId=${domainEntityId!}&externalLoginKey=${requestAttributes.externalLoginKey!}" target="_blank"><i class="fa fa-clipboard" aria-hidden="true"></i> Task</a> 
			    <a class="dropdown-item" href="/lead-portal/control/createPhoneCallActivity?partyId=${partyId?if_exists}&domainEntityType=${domainEntityType!}&domainEntityId=${domainEntityId!}&externalLoginKey=${requestAttributes.externalLoginKey!}" target="_blank"><i class="fa fa-phone" aria-hidden="true"></i> Phone Call</a>
			    <a class="dropdown-item" href="/sales-portal/control/addEmail?partyId=${partyId?if_exists}&domainEntityType=${domainEntityType!}&domainEntityId=${domainEntityId!}&externalLoginKey=${requestAttributes.externalLoginKey!}" target="_blank"><i class="fa fa-envelope" aria-hidden="true"></i> Email</a>
			    <a class="dropdown-item" href="/lead-portal/control/createAppointmentActivity?partyId=${partyId?if_exists}&domainEntityType=${domainEntityType!}&domainEntityId=${domainEntityId!}&externalLoginKey=${requestAttributes.externalLoginKey!}" target="_blank"><i class="fa fa-calendar" aria-hidden="true"></i> Appointment</a>
            </div>
        ' />
	   <#-- <a class="dropdown-item" href="/sales-portal/control/addOthers?partyId=${partyId?if_exists}&domainEntityType=${domainEntityType!}&domainEntityId=${domainEntityId!}&externalLoginKey=${requestAttributes.externalLoginKey!}" target="_blank"><i class="fa fa-plus-square" aria-hidden="true"></i> Others</a>  -->
        
        <#assign toggleDropDownData = {"E10007":addActivities!} />
        
        <div class="col-lg-12 col-md-12 col-sm-12 dash-panel">
        	<div class="card-head margin-adj mt-2" style = "display: none">
                <@AppBar  
	                appBarId="ACTION_APP_BAR"
	                appBarTypeId="ACTION"
	                id="appbar1"
	                extra=extra!
	                toggleDropDownData=toggleDropDownData!
	                isEnableUserPreference=true
	                />
            </div>
           
           <#-- Basic information -->
           <div class="card-header mt-3" id="cp" style = "display: none">
           		<@dynaScreen 
	                instanceId="LEAD_BASIC_INFO"
	                modeOfAction="VIEW"
	            />
           </div>
            
           <#-- <div class="col-lg-12 col-md-12 col-sm-12">
        		<@pageSectionHeader title="Activity Details"/>
        	</div>  --> 
             
            <form id="createPhoneActivity" method="post" action="<@ofbizUrl>createPhoneCallActivityAction</@ofbizUrl>" data-toggle="validator"> 
            	<#assign partyId = '${requestParameters.partyId!}' >
        		<@inputHidden name="partyId" id="partyId" value = "${partyId!}"/>
        		<input type="hidden" name="domainEntityType" value="${(parameters.domainEntityType)!}"/>
        		<input type="hidden" name="domainEntityId" value="${(parameters.domainEntityId)!}"/>
	        	<div>
	        		
        			<@sectionFrameHeader   title="${uiLabelMap.createPhoneCallActivity!}" />
	        		
                	<#assign cifNo = '${requestParameters.partyId!}' >
                    <@inputHidden name="cNo" id="cNo" value = "${cifNo!}"/>
                    <@inputHidden name="ownerBu" id="ownerBu" />
                    <#assign srType = EntityQuery.use(delegator).from("WorkEffortAssocTriplet").where("entityName", "Activity", "type", "Type", "value", "Phone Call", "active", "Y").queryFirst()! />
                    <@inputHidden id="srTypeId" value="${(srType.code)!}"/>
                    <@inputHidden id="workEffortTypeId" value="${(srType.value)!}"/>
                    <@inputHidden id="isPhoneCall" value="Y"/>
                    <@inputHidden id="loggedInUserId" value="${userLogin.userLoginId?if_exists}" />
                    <#assign userName = userLogin.userLoginId>
                    <#assign findMap = Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId", userLogin.partyId)>
                    <#assign person = delegator.findOne("Person", findMap, true)!>
                    <#if person?has_content>
                    	<#assign userName = (person.firstName!) + " " + (person.middleName!) + " " + person.lastName!>
                    	<@inputHidden id="userName" value="${userName!}"/>
                    </#if>
                    
	                <@dynaScreen 
		                instanceId="CREATE_PHONE_ACTIVITY"
		                modeOfAction="CREATE"
		             />
	            </div>
	            
	             <div class="col-md-12 col-lg-12 col-sm-12 activity-desc ">
         			<@textareaLarge  label="Description" id="messages" rows="4"/>
      			</div>
      			
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
<script>
$(document).ready(function() {

    var userName = $("#userName").val();
    $("#callFrom").remove();
    $("#callFrom_error").html('<i class="fa fa-user fa-1" aria-hidden="true"></i> ' + '<b>' + userName + '</b>').css('color', 'blue');

    onLoadDefaultElementsBehaviour();
    $('#extension').attr('readonly', 'readonly');
    var cNo = $("#cNo").val();
    
    $("#owner").change(function() {
        var owner = $("#owner").val();
        if (owner != undefined && owner != null) {
            ACTUTIL.loadBusinessUnit(owner, 'ownerBu', 'ownerBuDesc', null, "${requestAttributes.externalLoginKey!}");
       	}     
    });

    $('#type').val($('#workEffortTypeId').val());
    $('#type').attr('readonly', 'readonly');
    $('#ownerBuDesc').attr('readonly', 'readonly');

    var typeId = $("#srTypeId").val();
    if (typeId != "") {
        loadSubTypes(typeId);
    }
    
    var loggedInUserId = $("#loggedInUserId").val();
    if (loggedInUserId != undefined && loggedInUserId != null) {
       ACTUTIL.loadBusinessUnit(loggedInUserId, 'ownerBu', 'ownerBuDesc', null, "${requestAttributes.externalLoginKey!}");
   	}
   	
   	var direction = $("#direction").val();
    if (direction != undefined && direction != null && direction != "") {
        loadCallToAndFrom(direction, loggedInUserId, userName);
    }
   	     
    $("#direction").on("change", function () {
        var direction = $("#direction").val();
        if (direction != undefined && direction != null && direction != "") {
            loadCallToAndFrom(direction, loggedInUserId, userName);
        }
    });

    $("#norganizer").on("change", function () {
        var direction = $("#direction").val();
        var norganizer = $("#norganizer").val();
        if (direction == "62438") {
            var norganizer = $("#norganizer").val();
            populatePhoneNumber(direction, norganizer);
        }
    });

    $("#nrecepient").on("change", function () {
        var direction = $("#direction").val();
        var norganizer = $("#norganizer").val();
        if (direction == "62439") {
            var nrecepient = $("#nrecepient").val();
            populatePhoneNumber(direction, nrecepient);
        }
    });
    
    $("#contactId").on("change", function () {
        ACTUTIL.loadPartyTimeZones($(this).val(), 'timeZoneDesc', null, "${requestAttributes.externalLoginKey!}");
    });
	
    ACTUTIL.loadOwners(null, loggedInUserId, null, "${requestAttributes.externalLoginKey!}");
    ACTUTIL.loadContacts('${requestParameters.partyId!}', null, 'contactId', null, "${requestAttributes.externalLoginKey!}");
    
    if ($("#contactId").val()) {
        ACTUTIL.loadPartyTimeZones($("#contactId").val(), 'timeZoneDesc', null, "${requestAttributes.externalLoginKey!}");
    }
    
    $("#phoneNumber").on("change", function () {
		getPhoneNumberExtension();
	});
initDateRangeWithForm("createPhoneActivity","callDateTime_picker", "callBackDate_picker", $('#callDateTime'), $('#callBackDate'));
	function initDateRangeWithForm(formId,fromDatePanelId, thruDatePanelId, fromDateValue, thruDateValue) {
		if (fromDateValue) {
			$('#'+formId+' #'+thruDatePanelId).data("DateTimePicker").minDate(moment(fromDateValue).format('MM-DD-YYYY'));
		}
		if (thruDateValue) {
			$('#'+formId+' #'+fromDatePanelId).data("DateTimePicker").maxDate(moment(thruDateValue).format('MM-DD-YYYY'));
		}
		$('#'+formId+' #'+fromDatePanelId).on("dp.change", function (e) {
			$('#'+formId+' #'+thruDatePanelId).data("DateTimePicker").minDate(e.date);
		});
		$('#'+formId+' #'+thruDatePanelId).on("dp.change", function (e) {
			$('#'+formId+' #'+fromDatePanelId).data("DateTimePicker").maxDate(e.date);
		});
	}
});

function resetFormToReload() {
    window.location.href = window.location.href;
}

function onLoadDefaultElementsBehaviour() {

    var nonSelectContent = "<span class='nonselect'>Please Select</span>";
    var today = new Date();
    var dd = String(today.getDate()).padStart(2, '0');
    var mm = String(today.getMonth() + 1).padStart(2, '0');
    var yyyy = today.getFullYear();
    var hh = today.getHours();
    var m = today.getSeconds();
    //today = mm + '/' + dd + '/' + yyyy +" "+hh+":"+m;
    today = mm + '/' + dd + '/' + yyyy;
    $('#callDateTime').val(today.toLocaleString([], {
        hour12: false,
        dateStyle: "short",
        timeStyle: "short"
    }).replace(",", ""));
}

function loadSubTypes(typeId) {
    var nonSelectContent = "<span class='nonselect'>Please Select</span>";
    var subTypes = '<option value="" data-content="' + nonSelectContent + '" selected="">Please Select</option>';

    $.ajax({
        type: "POST",
        url: "getIASubTypes",
        data: {
            "iaTypeId": typeId
        },
        async: false,
        success: function(data) {
            var sourceDesc = data.results;
            for (var i = 0; i < data.length; i++) {
                var type = data[i];
                subTypes += '<option value="' + type.subTypeId + '">' + type.subTypeDesc + '</option>';
            }
        }
    });
    $("#srSubTypeId").html(subTypes);
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

function loadCallToAndFrom(direction, loggedInUserId, userName) {

    $('#nrecepient').dropdown('clear');
    $('#norganizer').dropdown('clear');

    var userOptionList = '<option value=""></option>';
    $.ajax({
        type: "GET",
        url: '/common-portal/control/getUsersList?roleTypeId=SALES_REP&isIncludeLoggedInUser=Y&externalLoginKey=${requestAttributes.externalLoginKey!}',
        async: false,
        success: function (data) {
            for (var i = 0; i < data.length; i++) {
                var type = data[i];
                var selected = loggedInUserId && loggedInUserId === type.userLoginId ? 'selected' : '';
                //userOptionList += '<option value="'+type.userLoginId+'">'+type.userName+' ('+ type.roleDesc +')</option>';
                userOptionList += '<option value="' + type.userLoginId + '" ' + selected + '>' + type.userName + '</option>';
            }
        }
    });

    if ("62438" == direction) {
        $("#nrecepient").html(userOptionList);
        $("#nrecepient").dropdown('refresh');

        ACTUTIL.loadContacts('${requestParameters.partyId!}', null, 'norganizer', null, "${requestAttributes.externalLoginKey!}");
        var contactPartyId = $("#norganizer").val();
        populatePhoneNumber(direction, contactPartyId);
    }

    if ("62439" == direction) {
        $("#norganizer").html(userOptionList);
        $("#norganizer").dropdown('refresh');

        ACTUTIL.loadContacts('${requestParameters.partyId!}', null, 'nrecepient', null, "${requestAttributes.externalLoginKey!}");
        var contactPartyId = $("#nrecepient").val();
        populatePhoneNumber(direction, contactPartyId);
    }

}

function populatePhoneNumber(direction, contactPartyId) {
    var nonSelectContent = "<span class='nonselect'>Please Select</span>";
    var telecomOptionsList = '';
    $('#phoneNumber').empty();
    $('#phoneNumber').dropdown('clear');
    $("form#phonenumberForm").html('');
    $.ajax({
        type: "POST",
        url: "/common-portal/control/getPartyTelecomNumbers",
        data: {
            "partyId": contactPartyId,
            "externalLoginKey": "${requestAttributes.externalLoginKey!}"
        },
        async: false,
        success: function (data) {
            for (var i = 0; i < data.length; i++) {
                var type = data[i];
                var isprimary = type.isPrimary;

                if ("Y" === isprimary) {
                    telecomOptionsList += '<option class="phoneclass" value="' + type.contactNumber + '" selected="selected">' + type.contactNumber + '</option>';
                    $("div.ui.dropdown.search.form-control.fluid.show-tick.phoneNumber.selection > i").addClass("clear");
                } else {
                    if (i == 0) {
                        telecomOptionsList = '<option class="phoneclass" value="" data-content="' + nonSelectContent + '" selected="selected">Please Select</option>';
                    }
                    telecomOptionsList += '<option class="phoneclass" value="' + type.contactNumber + '">' + type.contactNumber + '</option>';
                }

                $("form#phonenumberForm").append('<input id="' + type.contactNumber + '" type="hidden" name="' + type.contactNumber + '" value="' + type.contactMechId + '" />');
            }
        }
    });

    $("#phoneNumber").html(telecomOptionsList);
    $("#phoneNumber").dropdown('refresh');

    getPhoneNumberExtension();
}

function getPhoneNumberExtension() {
    var selectedPhoneNum = $('#createPhoneActivity #phoneNumber').val();
    var contactMechId = $("#" + selectedPhoneNum).val();
    var partyId = $("#nrecepient").val();
    $.ajax({
        type: "GET",
        url: '/common-portal/control/getPhoneNumberExtension',
        data: {
            "contactMechId": contactMechId,
            "partyId": partyId,
            "externalLoginKey": "${requestAttributes.externalLoginKey!}"
        },
        async: false,
        success: function (data) {
            $('#extension').val(data.extension);
        }
    });
}
</script>