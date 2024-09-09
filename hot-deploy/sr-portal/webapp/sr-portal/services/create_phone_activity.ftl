<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<#include "component://common-portal/webapp/common-portal/lib/picker_macro.ftl"/>

<script type="text/javascript" src="/common-portal-resource/js/ag-grid/activity/activity-utils.js"></script>

<div class="row">
<div id="main" role="main" class="pd-btm-title-bar">
	
<div class="col-lg-12 col-md-12 col-sm-12 dash-panel">
		<form id="createPhoneActivity" name="createPhoneActivity" method="post" action="<@ofbizUrl>createPhoneCallActivityAction</@ofbizUrl>" data-toggle="validator">
			<input type="hidden" name="domainEntityType" value="${(parameters.domainEntityType)!}"/>
        	<input type="hidden" name="domainEntityId" value="${(parameters.domainEntityId)!}"/>
					        	
                	<#assign cifNo = '${requestParameters.partyId!}' >
                	<#assign custRequestId = '${srNumber!}' >
                    <@inputHidden name="cNo" id="cNo" value = "${cifNo!}"/>
                    <@inputHidden name="custRequestId" id="custRequestId" value = "${custRequestId!}"/>
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
                    <@sectionFrameHeader   title="${uiLabelMap.createPhoneCallActivity!}" />
	                <@dynaScreen 
		                instanceId="CREATE_PHONE_ACTIVITY"
		                modeOfAction="CREATE"
		             />
	           
	            
	            <div class="col-md-12 col-lg-12 col-sm-12 activity-desc">
         			<@textareaLarge  label="Description" id="messages" rows="4"/>
      			</div>
      			
	           	<div class="offset-md-2 col-sm-10 p-2">
	           		<@formButton
	                     btn1type="submit"
	                     btn1id=""
	                     btn1label="${uiLabelMap.Save}"
	                     btn2=true
	                     btn2onclick = "resetFormToReload()"
	                     btn2type="reset"
	                     btn2label="${uiLabelMap.Clear}"
	                />
	            </div>
	             </div>

	</form>
	</div>
</div>

<form id="phonenumberForm" name="phonenumberForm">
</form>

<div id="teleCalModal" class="modal fade" role="dialog">
	<div class="modal-dialog" style="width:50%;">
    	<div class="modal-content">
    		<div class="modal-header" style="text-align:center">
               <h4 class="modal-title" align="center">To make a phone call click on below link</h4>
               <button type="reset" class="close" data-dismiss="modal">&times;</button>
            </div>
            <div class="modal-body">
                <div style="text-align:center"><a class="tele_share_link" href=${telePhoneLink!}><i class="fa fa fa-phone fa-1 " aria-hidden="true"></i>${telePhoneNumber!}</a></div>
        	</div>
    	</div>
	</div>
</div>

<@partyPicker 
instanceId="partyPicker"
/>

<script>
    
$(document).ready(function () {
	
	$("#createPhoneActivity").validator().on('submit', function (event) {  
		if (event.isDefaultPrevented()) {
			var $form = $(this);
			$form.data('submitted', false);
			return false;
        } else{
        	var $form = $(this);
		    if ($form.data('submitted') === true) {
		      event.preventDefault();
		    } else {
		      $form.data('submitted', true);
		      //$("#createPhoneActivity").submit();
		    }	
        }
	});

    var userName = $("#userName").val();
    $("#callFrom").remove();
    $("#callFrom_error").html('<i class="fa fa-user fa-1" aria-hidden="true"></i> ' + '<b>' + userName + '</b>').css('color', 'blue');

    onLoadDefaultElementsBehaviour();

    // ${StringUtil.wrapString(telePhoneLink?if_exists)}
    //$("#teleCalModal").modal();
    <#if telePhoneLink ? has_content >
        location.href = "${StringUtil.wrapString(telePhoneLink?if_exists)}"; 
    <#else>
        showAlert("error", "Phone link is empty!"); 
    </#if>

    $("a.tele_share_link").on("click", function () {
        $(".close").click();
    });

    $("#owner").change(function () {
        var owner = $("#owner").val();
        if (owner != undefined && owner != null)
            getBusinessUnit(owner);
    });
    $('#onceDone').val("N");
    $('#onceDone').checked = true;
    $('#type').val($('#workEffortTypeId').val());
    $('#type').attr('readonly', 'readonly');
    $('#ownerBuDesc').attr('readonly', 'readonly');

    $('#extension').attr('readonly', 'readonly');

    var typeId = $("#srTypeId").val();
    if (typeId != "") {
        loadSubTypes(typeId);
    }

    var loggedInUserId = $("#loggedInUserId").val();
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
        getPartyTimeZonesList($("#contactId").val());
    });

    if (loggedInUserId != undefined && loggedInUserId != null)
        getBusinessUnit(loggedInUserId);

    getUsers(loggedInUserId, userName);
    $("span.picker-window-erase").css("display", "none");
    $("span.picker-window").css("display", "none");
    var cNo = $("#cNo").val();

    if (cNo == null || cNo == undefined || cNo == "") {
        $("#cNo").val($("#partyId_val").val());
        cNo = $("#partyId_val").val();
    }

    if (cNo != null && cNo != undefined && cNo != "") {
        loadContacts();
    }

    $("#partyId_desc").on("change", function () {

        var nonSelectContent = "<span class='nonselect'>Select Contact</span>";
        var dataSourceOptions = '';
        var partyId = $("#partyId_val").val();
        $.ajax({
            type: "POST",
            url: "/common-portal/control/getPrimaryContacts",
            data: {
                "partyId": partyId,
                "externalLoginKey": "${requestAttributes.externalLoginKey!}"
            },
            async: false,
            success: function (data) {
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

    });

    ACTUTIL.loadSrAssocParties('${(parameters.domainEntityId)!}', 'contactId', null, "${requestAttributes.externalLoginKey!}");
	
	$("#phoneNumber").on("change", function () {
		getPhoneNumberExtension();
	});
});

function resetFormToReload() {
    window.location.href = window.location.href;
}

function onLoadDefaultElementsBehaviour() {

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

function getBusinessUnit(owner) {
    var owner = owner;
    $.ajax({
        type: "POST",
        url: "getBusinessUnitName",
        async: false,
        data: {
            "owner": owner
        },
        success: function (data) {
            result = data;
            if (result && result[0] != undefined && result[0].businessId != undefined) {
                $("#ownerBu").val(result[0].businessId);
                $("#ownerBuDesc").val(result[0].businessunitName);
            } else {
                $("#ownerBu").val("");
                $("#ownerBuDesc").val("");
            }
        },
        error: function (data) {
            result = data;
            showAlert("error", "Error occured while fetching Business Unit");
        }
    });
}

function loadContacts() {
    var dataSourceOptions = "";
    var partyId = $("#partyId_val").val();
    $.ajax({
        type: "POST",
        url: "/common-portal/control/getPrimaryContacts",
        data: {
            "partyId": partyId,
            "externalLoginKey": "${requestAttributes.externalLoginKey!}"
        },
        async: false,
        success: function (data) {
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

    var populatedPrimContactId = $("#contactId").val();
    if (populatedPrimContactId != undefined && populatedPrimContactId != null && populatedPrimContactId != "") {
        getPartyTimeZonesList(populatedPrimContactId);
    }
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
        success: function (data) {
            var sourceDesc = data.results;
            for (var i = 0; i < data.length; i++) {
                var type = data[i];
                subTypes += '<option value="' + type.subTypeId + '">' + type.subTypeDesc + '</option>';
            }
        }
    });
    $("#srSubTypeId").html(subTypes);
}

function getUsers(loggedInUserId, userName) {
    var userOptionList = '<option value="' + loggedInUserId + '">' + userName + '</option>';
    $.ajax({
        type: "GET",
        url: '/common-portal/control/getUsersList',
        async: false,
        success: function (data) {
            for (var i = 0; i < data.length; i++) {
                var type = data[i];
                userOptionList += '<option value="' + type.userLoginId + '">' + type.userName + '</option>';
            }
        }
    });
    $("#owner").html(userOptionList);
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

        ACTUTIL.loadSrAssocParties('${(parameters.domainEntityId)!}', 'norganizer', null, "${requestAttributes.externalLoginKey!}");
        var contactPartyId = $("#norganizer").val();
        populatePhoneNumber(direction, contactPartyId);
    }

    if ("62439" == direction) {
        $("#norganizer").html(userOptionList);
        $("#norganizer").dropdown('refresh');

        ACTUTIL.loadSrAssocParties('${(parameters.domainEntityId)!}', 'nrecepient', null, "${requestAttributes.externalLoginKey!}");
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

function getPartyTimeZonesList(contactId) {
    $('#timeZoneDesc').dropdown('clear');
    var nonSelectContent = "<span class='nonselect'>Please Select</span>";
    var timeZonesOptionList = '<option value="" data-content="' + nonSelectContent + '" selected="">Please Select</option>';
    var selTimeZoneId = '';

    $.ajax({
        type: "GET",
        url: '/common-portal/control/getPartyTimeZonesList',
        data: {
            "partyId": contactId,
            "externalLoginKey": "${requestAttributes.externalLoginKey!}"
        },
        async: false,
        success: function (data) {
            for (var i = 0; i < data.length; i++) {
                var entry = data[i];
                if (entry.selected) {
                    selTimeZoneId = entry.timeZoneId;
                    timeZonesOptionList += '<option value="' + entry.timeZoneId + '" selected="selected" >' + entry.description + '</option>';
                } else {
                    if (selTimeZoneId != undefined && selTimeZoneId != null && selTimeZoneId != "" && i == 0) {

                    } else {
                        timeZonesOptionList += '<option value="' + entry.timeZoneId + '">' + entry.description + '</option>';
                    }
                }
            }
        }
    });
    $("#timeZoneDesc").html(timeZonesOptionList);
    $("#timeZoneDesc").dropdown('refresh');
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