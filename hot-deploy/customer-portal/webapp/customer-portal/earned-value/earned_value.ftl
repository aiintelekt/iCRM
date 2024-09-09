<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<div>
        <#assign helpUrl=Static["org.groupfio.common.portal.util.DataHelper"].getHelpUrl(delegator, request.getRequestURI()) />
        <#assign extras='' />
        <#assign extraLeft='' /> 
          <div>  <@pageSectionHeader title="Add Earned Value" extra=helpUrl?if_exists /></div> 
            <form id="editEarnedValue" name="editEarnedValueForm" action="<@ofbizUrl>addEarnedValue#e-value</@ofbizUrl>" method="post" onsubmit="return validateForm()" class="form-horizontal"  data-toggle="validator">
                         <@inputHidden id="channelId" value="${channelId!'MANUAL'}" />
				         <@inputHidden id="loyaltyType" value="${loyaltyType!'EARNED'}" />
				         <@inputHidden id="transactionType" value="${transactionType!'ADJUSTMENT'}" />
				         <@inputHidden id="partyId" value="${partyId!}" />
				         <@inputHidden id="domainEntityType" value="CUSTOMER" />
				         <@inputHidden id="domainEntityId" value="${partyId!}" />
				         <@inputHidden id="noteType" value="EV_TYPE" />
                         <@dynaScreen instanceId="ADD_EARNED_VALUE" modeOfAction="CREATE" />
                <div class="offset-md-2 col-sm-10">
                    <@submit label="Add EV" id="submit"/>
                    <@button label="Reset" id="resting_btn" class="btn btn-sm btn-secondary" />
                </div><br>
            </form>
        </div>
<script>
    $(document).ready(function() {
        $("#resting_btn").click(function() {
            $("#evPoints").val('');
            $("textarea#evNote").val('');
            $("#evPoints_error").html("");
            $("#reason_error").html("");
            $("#evNote_error").html("");
        });
    });

$("#reason").prop("disabled", true);

function validateForm() {

    var evPointsvalue = $("#evPoints").val();
    if (evPointsvalue != "") {
        let doubleValue = parseInt(evPointsvalue);
        if (doubleValue != evPointsvalue) {
            $("#evPoints_error").show();
            return false;
        } else if (doubleValue > 1000 || doubleValue < -1000) {
            console.log("doubleValue" + doubleValue);
            $("#evPoints_error").css("display", "inline");
            $("#evPoints_error").html("Number range should be -1000 to 1000");
            $("#evPoints_error").show();
            return false;
        }
    } else {
        $("#evPoints_error").show();
    }
}
$(document).ready(function() {
    $("#evPoints").change(function() {
        $('#evPoints_error').hide();
    });
    $("#evPoints").click(function() {
        $("#evPoints_error").hide();
    });
    $("#evPoints").keyup(function() {
        var evValue = this.id;
        var number = $("#evPoints").val();
        if (number != "") {
            let double = parseFloat(number);
            if (double != number) {
                $("#evPoints_error").html("");
                $("#evPoints_error").show();
            } else {
                if (double != 0) {
                    if (double > 1000 || double < -1000) {
                        $("#evPoints_error").html("");
                        $("#evPoints_error").html("Number range should be -1000 to 1000");
                        $("#evPoints_error").show();
                    } else {
                        $("#evPoints_error").hide();
                    }
                }
            }
        }

    });
}); </script>   