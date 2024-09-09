
$("#generalPostalCode").keyup(function () {
    validatePostalCode();
});

$("#generalStateProvinceGeoId, #generalCountryGeoId").change(function () {
    validatePostalCode();
});

function validatePostalCode() {
    var postalCodeErr = $("#generalPostalCode_error").val();
    var postalCode = $("#generalPostalCode").val();
    $("#postalCodeErr").html("");
    if (postalCode != "" || postalCode != null || postalCode != " ") {
        var country = $('#createCustomerForm select#generalCountryGeoId option:selected').val();
        var state = $('#createCustomerForm select#generalStateProvinceGeoId option:selected').val();
        if (country != null && country != "" && state != null && state != "") {
            if (country == "CAN" || country == "USA") {
                if (country == "CAN") {
                    if (postalCode.length > 7 || postalCode.length < 7) {
                        $("#generalPostalCode_error").append('<ul class="list-unstyled"><li>Postal Code Length Should be 7</li></ul>');
                        return false;
                    } else {
                        $("#generalPostalCode_error").html("");
                    }
                } else if (country == "USA") {
                    if (postalCode.length > 5 || postalCode.length < 5) {
                        var postalCodeErr = $("div#generalPostalCode_error").text();
                        if (postalCodeErr != "") {} else {
                            $("#generalPostalCode_error").append('<ul class="list-unstyled"><li>Postal Code Length Should be 5</li></ul>');
                        }
                        return false;
                    } else {
                        $("#generalPostalCode_error").html("");
                    }
                }
            }
        }
        if (postalCodeErr != "" || postalCodeErr != null) {
            $("#generalPostalCode_error").html("");
        }
    }
}

function onSubmitValidate() {
    validatePostalCode();
    var phoneNumber = $('#primaryPhoneNumber').val();
    var primaryEmail = $('#primaryEmail').val();
    if ((phoneNumber != null && phoneNumber != "" && phoneNumber != "undefined") || (primaryEmail != null && primaryEmail != "" && primaryEmail != "undefined")) {
        $('#email_phone_error').html("");
        $('#email_phone').hide();
        return true;
    } else {
        $('#email_phone').show();
        $('#email_phone_error').html("Please enter either phone number or email");
        return false;
    }
}