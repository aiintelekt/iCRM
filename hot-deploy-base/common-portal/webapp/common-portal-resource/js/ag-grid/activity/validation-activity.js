function submitActivityForm() {

    var submitForm = false;
    var typeId = $("#srTypeId").val();
    if (typeId && typeId != null && typeId != '' && (typeId === "31701" || typeId === "APPOINTMENT")) {
        var valid = validate();
        if (valid) {
            submitForm = true;
        } else {
            alert("Please Select Actual/Scheduled End Date as greater than Start Date");
        }

        var actualStartDate = $("#actualStartDate_date").val();
        var actualCompletionDate = $("#actualCompletionDate_date").val();
        if (actualStartDate == '' && actualCompletionDate != '') {
            submitForm = false;
            alert("Please Select Actual Start Date")
        }

    } else {
        submitForm = true;
    }

    return submitForm;
}

function validate() {

    var valid = true;

    var actualStartDate_date = $("#actualStartDate_date").val();
    var actualCompletionDate_date = $("#actualCompletionDate_date").val();

    var actualStartDate_time = $("#actualStartDate_time").val();
    var actualCompletionDate_time = $("#actualCompletionDate_time").val();

    var estimatedStartDate_date = $("#estimatedStartDate_date").val();
    var estimatedCompletionDate_date = $("#estimatedCompletionDate_date").val();

    var estimatedStartDate_time = $("#estimatedStartDate_time").val();
    var estimatedCompletionDate_time = $("#estimatedCompletionDate_time").val();

    var actualStartDate = actualStartDate_date + " " + actualStartDate_time;
    var actualCompletionDate = actualCompletionDate_date + " " + actualCompletionDate_time;

    var estimatedStartDate = estimatedStartDate_date + " " + estimatedStartDate_time;
    var estimatedCompletionDate = estimatedCompletionDate_date + " " + estimatedCompletionDate_time;

    estStartDate = new Date(estimatedStartDate);
    estEndDate = new Date(estimatedCompletionDate);

    startDate = new Date(actualStartDate);
    endDate = new Date(actualCompletionDate);

    if ((startDate - endDate) == 0) {
        valid = true;
    } else if (startDate > endDate) {
        valid = false;
    } else {}

    if ((estStartDate - estEndDate) == 0) {
        valid = true;
    } else if (estStartDate > estEndDate) {
        valid = false;
    } else {}

    return valid;
}

$(function() {
    $('input[type=radio][name=direction]').change(function() {
        if (this.value == 'IN') {
            $(':button[type="submit"]').html('Save');
            if ($('#domainEntityType').val() === 'CUSTOMER') {
            	console.log('calling mailOptionsTrigger');
            	mailOptionsTrigger(this.value);
            } else {
            	onChangeEmailDirection(this.value, $('#contactId').val(), $('#externalLoginKey').val());
            }
        } else if (this.value == 'OUT') {
            $(':button[type="submit"]').html('Send');
            if ($('#domainEntityType').val() === 'CUSTOMER') {
            	console.log('calling mailOptionsTrigger');
            	mailOptionsTrigger(this.value);
            } else {
            	onChangeEmailDirection(this.value, $('#contactId').val(), $('#externalLoginKey').val());
            }
        }
    });
    
    /*$("#nto").on("change", function() {
        var direction = $('input:radio[name=direction]:checked').val();
        if (direction && direction == "OUT") {
            var selectedEmailAddress = $("#nto").val();
            populatePrimaryContact(direction, selectedEmailAddress);
        }
    });

    $("#nsender").on("change", function() {
        var direction = $('input:radio[name=direction]:checked').val();
        if (direction && direction == "IN") {
            var selectedEmailAddress = $("#nsender").val();
            populatePrimaryContact(direction, selectedEmailAddress);
        }
    });*/
    
    $("#requiredAttendees").on("change", function() {
        var reqAttendeties = $("#requiredAttendees").val();
        var optionalVal = $("#optionalAttendees").val();
        var userOptionList = '<option value="">Please Select</option>';
        $("#optionalAttendees").html(DOMPurify.sanitize(userOptionList));
        $.ajax({
            type: "GET",
            url: '/common-portal/control/getAttendeeList',
            data: {
                "partyId": $("#partyId").val(),
                "externalLoginKey": "${requestAttributes.externalLoginKey!}"
            },
            async: false,
            success: function(data) {
                if (data) {
                    if (data.responseMessage == "success") {
                        for (var i = 0; i < data.attendeesList.length; i++) {
                            var entry = data.attendeesList[i];
                            if (entry != null) {

                                var valid = true;
                                if (reqAttendeties != '') {
                                    $(reqAttendeties).each(function(index, element) {
                                        var req = element;
                                        if (entry.partyId == req) {
                                            valid = false;
                                        } else {

                                        }

                                    });
                                    if (valid) {
                                        userOptionList += '<option value="' + entry.partyId + '">' + entry.userName + '</option>';
                                    }
                                }
                            }
                        }
                    }
                }
            }
        });

        $("#optionalAttendees").html(DOMPurify.sanitize(userOptionList));
    });
});

function mailOptionsTrigger(direction) {
    console.log('mailOptionsTrigger calling');
    var mailOptionsList = "";
    var partyId = $("#partyId").val();
    var defaultFrom = $('#loginEmail').val();
    var domain = $('#domainEntityType').val();
    
    var defaultMailOptionsList = '<option value="' + defaultFrom + '" selected="selected">' + defaultFrom + '</option>';

    var primContactId = "";

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
                    for (var i = 0; i < data.toMapList.length; i++) {
                        var entry = data.toMapList[i];
                        if (entry != null) {
                            if (entry.selected != null) {
                                mailOptionsList += '<option value="' + entry.EmailAddress + '" selected>' + entry.EmailAddress + ' (' + entry.partyName + ')' + '</option>';
                                $('#contactId').val(entry.primaryContactId);
                            } else {
                                if (i == 0) {
                                    $('#contactId').val(entry.primaryContactId);
                                }
                                mailOptionsList += '<option value="' + entry.EmailAddress + '">' + entry.EmailAddress + ' (' + entry.partyName + ')' + '</option>';
                            }
                        }
                    }
                }
            }
        }
    });

    if ("IN" == direction) {
        if (domain != null && domain != '' && domain == "CUSTOMER") {
            var emailVal = $('#primaryEmailId').val();
            var nOptions = "";
            var nOptions = '<option value="' + emailVal + '" selected>' + emailVal + '</option>';
            $("#nsender").html(DOMPurify.sanitize(nOptions));
            $("#nsender").dropdown('refresh');

            $("#nto").html(DOMPurify.sanitize(defaultMailOptionsList));
            $("#nto").dropdown('refresh');
        } else {
            $("#nsender").html(DOMPurify.sanitize(mailOptionsList));
            $("#nsender").dropdown('refresh');

            $("#nto").html(DOMPurify.sanitize(defaultMailOptionsList));
            $("#nto").dropdown('refresh');
        }
    }

    if ("OUT" == direction) {
        if (domain != null && domain != '' && domain == "CUSTOMER") {
            var emailVal = $('#primaryEmailId').val();
            var nOptions = "";
            var nOptions = '<option value="' + emailVal + '" selected>' + emailVal + '</option>';
            $("#nsender").html(DOMPurify.sanitize(defaultMailOptionsList));
            $("#nsender").dropdown('refresh');

            $("#nto").html(DOMPurify.sanitize(nOptions));
            $("#nto").dropdown('refresh');
        } else {
            $("#nto").html(DOMPurify.sanitize(mailOptionsList));
            $("#nto").dropdown('refresh');

            $("#nsender").html(DOMPurify.sanitize(defaultMailOptionsList));
            $("#nsender").dropdown('refresh');
        }
    }
}

function populatePrimaryContact(direction, selectedEmailAddress) {

    var partyId = $("#partyId").val();
    var primContactId = "";

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
                    for (var i = 0; i < data.toMapList.length; i++) {
                        var entry = data.toMapList[i];
                        if (entry != null) {
                            if (entry.EmailAddress != null && entry.EmailAddress == selectedEmailAddress) {
                                primContactId = entry.primaryContactId;
                            }
                        }
                    }
                }
            }
        }
    });
    $('#contactId').val(primContactId);
}

function submitEmailActivityForm() {
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
    return true;
}

function loadCcContacts() {

    var dataSourceOptions = "";
    var ntoOptions = "";
    var partyId = $("#partyId").val();
    $.ajax({
        type: "POST",
        url: "/common-portal/control/getCcContactEmailIds",
        data: {
            "partyId": partyId,
            "ccEmailD": "Y",
            "externalLoginKey": "${requestAttributes.externalLoginKey!}"
        },
        async: false,
        success: function(data) {

            if (data) {

                if (data.responseMessage == "success") {
                    for (var i = 0; i < data.partyRelContacts.length; i++) {
                        var entry = data.partyRelContacts[i];
                        dataSourceOptions += '<option value="' + entry.emailId + '">' + entry.emailId + '</option>';

                    }

                }
            }
        }

    });

    $("#ncc").html(DOMPurify.sanitize(dataSourceOptions));
    $("#ncc").dropdown('refresh');
}

const prepareActivityDateInput = (statusId) => {
    if (statusId === 'IA_OPEN') {
    	$('input[name=isSchedulingRequired]').prop("disabled", true);
    	$('#isSchedulingRequired_0').prop("checked", true);
    	
        $('#estimatedStartDate_date').val('');
        $('#estimatedCompletionDate_date').val('');
        $("#estimatedStartDate_date").prop("disabled", true);
        $("#estimatedStartDate_time").prop("disabled", true);
        $("#estimatedCompletionDate_date").prop("disabled", true);
        $("#estimatedCompletionDate_time").prop("disabled", true);

        $('#actualStartDate_date').val('');
        $('#actualCompletionDate_date').val('');
        $("#actualStartDate_date").prop("disabled", true);
        $("#actualStartDate_time").prop("disabled", true);
        $("#actualCompletionDate_date").prop("disabled", true);
        $("#actualCompletionDate_time").prop("disabled", true);
        
        $('#duration').dropdown('clear');
        $('#arrivalWindow').dropdown('clear');
        $(".duration").addClass("disabled");
    	$(".arrivalWindow").addClass("disabled");
    } else {
    	$(".duration").removeClass("disabled");
    	$(".arrivalWindow").removeClass("disabled");
    	$('input[name=isSchedulingRequired]').prop("disabled", false);
    	
        $("#estimatedStartDate_date").prop("disabled", false);
        $("#estimatedStartDate_time").prop("disabled", false);
        $("#estimatedCompletionDate_date").prop("disabled", false);
        $("#estimatedCompletionDate_time").prop("disabled", false);

        $("#actualStartDate_date").prop("disabled", false);
        $("#actualStartDate_time").prop("disabled", false);
        $("#actualCompletionDate_date").prop("disabled", false);
        $("#actualCompletionDate_time").prop("disabled", false);
    }
    
    if ($('input[name=isSchedulingRequired]:checked').val()=='N') {
    	$('#ownerBookedCalSlots').val('');
    	
    	$('#estimatedStartDate_time').timepicker('setTime', '0:00');
    	$('#estimatedCompletionDate_time').timepicker('setTime', '0:00');
    	$("#estimatedStartDate_time").prop("disabled", true);
    	$("#estimatedCompletionDate_time").prop("disabled", true);
    	
    	$('#actualStartDate_date').val('');
        $('#actualCompletionDate_date').val('');
        $("#actualStartDate_date").prop("disabled", true);
        $("#actualStartDate_time").prop("disabled", true);
        $("#actualCompletionDate_date").prop("disabled", true);
        $("#actualCompletionDate_time").prop("disabled", true);
        
        $('#duration').dropdown('clear');
        $('#arrivalWindow').dropdown('clear');
        $(".duration").addClass("disabled");
    	$(".arrivalWindow").addClass("disabled");
    }
}

const onChangeEmailDirection = (direction, partyId, externalLoginKey) => {
	$('#nsender').dropdown('clear');
    $('#nto').dropdown('clear');
    
    var defaultLoggedInUserEmail = '<option value=""></option>';
    if ($('#loginEmail').val()) {
        defaultLoggedInUserEmail = '<option value="' + $('#loginEmail').val() + '" selected>' + $('#loginEmail').val() + '</option>';
    }
    
	if ("IN" === direction) {
		$("#nto").html(DOMPurify.sanitize(defaultLoggedInUserEmail));
        $("#nto").dropdown('refresh');
        
		CMMUTIL.getPartyEmailList(`${partyId}`, 'nsender', null, `${externalLoginKey}`);
	} else if ("OUT" === direction) {
		$("#nsender").html(DOMPurify.sanitize(defaultLoggedInUserEmail));
        $("#nsender").dropdown('refresh');
        
		CMMUTIL.getPartyEmailList(`${partyId}`, 'nto', null, `${externalLoginKey}`);
	}
}