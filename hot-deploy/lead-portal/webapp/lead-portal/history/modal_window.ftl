<#include "component://bootstrap/lib/ofbizFormMacros.ftl"/>
<#include "component://common-portal/webapp/common-portal/lib/picker_macro.ftl"/>

<#macro leadEventHistory instanceId leadName="">
<div id="${instanceId!}" class="modal fade" style="z-index: 99999;">
   <div class="modal-dialog modal-lg" style="max-width: 1200px;">
      <!-- Modal content-->
      <div class="modal-content" style="height:600px;">
         <div class="modal-header">
            <h4 class="modal-title">
               Lead Event History <#if leadName?has_content>[${leadName!}]</#if>
            </h4>
            <button type="button" class="close" data-dismiss="modal">&times;</button>
         </div>
         <p>
            <button id="${instanceId!}_event-ref-btn" type="button" class="btn btn-xs btn-primary m5 pull-right" style="margin-top:10px;margin-right:50px;"><i class="fa fa-refresh" aria-hidden="true"></i> Refresh</button>
         </p>
         <div class="modal-body" style="overflow-y: scroll;overflow-x: hidden;">
            <div id="event-history-body">
            </div>
         </div>
      </div>
   </div>
</div>

<script>
$(document).ready(function() {

$('#${instanceId!}').on('shown.bs.modal', function (e) {
	loadLeadEventHistory();
});

$('#${instanceId!}_event-ref-btn').on('click', function(e) {
    loadLeadEventHistory();
});

});

function loadLeadEventHistory() {
	var partyId = '${inputContext.partyId!}';
    $.ajax({
        type: "POST",
        url: "/lead-portal/control/getLeadEventHistory",
        data: {
            partyId: partyId
        },
        sync: true,
        success: function(data) {
            // alert(JSON.stringify(data));

            var content = "";
            var iconColor = ["#ff6666", "#80ffff", "#99ff99", "#b3ff1a", "#8080ff"];
            var eventColor = ["#80ffff", "#8080ff", "#ff6666", "#b3ff1a", "#99ff99"];
            if (data) {
                var dataList = data["dataList"];
                if (dataList) {
                    for (var i = 0; i < dataList.length; i++) {
                        var eventList = dataList[i];
                        var historyId = eventList.historyId;
                        var eventTypeId = eventList.eventTypeId;
                        var eventTypeDesc = eventList.eventTypeDesc;
                        var eventDate = eventList.eventDate;
                        var createdByUserLogin = eventList.createdByUserLogin;
                        var ipAddress = eventList.ipAddress;
                        var createdByEmailAddress = eventList.createdByEmailAddress;
                        var description = eventList.description;
                        var createdTxStamp = eventList.createdTxStamp;
                        var partyId = eventList.partyId;
                        var partyName = eventList.partyName;
                        //iColor=iconColor[i];
                        eColor = eventColor[i];
                        iColor = getRandomColor();
                        var favIcon = "fa fa-history";
                        var imgSrc = "";
                        var adverb = "";
                        var displayDetails = "Y";
                        if (eventTypeId) {
                            if (eventTypeId == "DOC_CREATED") {
                                favIcon = "fa fa-file-text fa-lg";
                                adverb = "By";
                                imgSrc = '<img src="https://fioicrm.groupfio.com/images/templateimages/document.png" class="nexit-blog2" style="width:19px;">';
                            } else if (eventTypeId == "DOC_EMAILED") {
                                favIcon = "fa fa-send fa-lg";
                                adverb = "By";
                                imgSrc = '<img src="https://fioicrm.groupfio.com/images/templateimages/email.png" class="nexit-blog2" style="width:19px;">';
                            } else if (eventTypeId == "DOC_EMAIL_VIEW") {
                                favIcon = "fa fa-envelope-open-o fa-lg";
                                adverb = "By";
                                createdByUserLogin = partyName
                                imgSrc = '<img src="https://fioicrm.groupfio.com/images/templateimages/document.png" class="nexit-blog2" style="width:19px;">';
                            } else if (eventTypeId == "DOC_E_SIGNED") {
                                favIcon = "fa fa-pencil fa-lg";
                                adverb = "By";
                                createdByUserLogin = partyName
                                imgSrc = '<img src="https://fioicrm.groupfio.com/images/templateimages/sign.png" class="nexit-blog2" style="width:19px;">';
                            } else if (eventTypeId == "DOC_COMPLETED") {
                                favIcon = "fa fa-check-circle fa-lg";
                                imgSrc = '<img src="https://fioicrm.groupfio.com/images/templateimages/completed.png" class="nexit-blog2" style="width:19px;">';
                                displayDetails = "N";
                            } else if (eventTypeId == "DOC_APPROVED") {
                                favIcon = "fa fa-check fa-lg";
                                displayDetails = "N";
                            } else if (eventTypeId == "DOC_REJECTED") {
                                adverb = "By";
                                favIcon = "fa fa-ban fa-lg";
                                displayDetails = "N";
                            } else if (eventTypeId == "DOC_CANCELLED") {
                                adverb = "By";
                                favIcon = "fa fa-times-circle fa-lg";
                            } else if (eventTypeId == "DOC_IN_PROCESS") {
                                favIcon = "fa fa-tasks fa-lg";
                                displayDetails = "N";
                            }
                        }
                        var eventInfo = "";
                        var timeInfo = "";

                        eventInfo += eventTypeDesc;
                        if (displayDetails && displayDetails == "Y") {

                            if (adverb) {
                                eventInfo += " " + adverb;
                            }
                            if (createdByUserLogin) {
                                eventInfo += " " + createdByUserLogin;
                            }
                            if (eventTypeId == "DOC_EMAILED") {
                                if (partyName) {
                                    eventInfo += " " + "To";
                                    eventInfo += " " + partyName;
                                }
                            }
                            if (createdByEmailAddress) {
                                eventInfo += " " + "(" + createdByEmailAddress + ")";
                            }
                        }
                        //eventInfo+=eventTypeDesc+" "+adverb+" "+createdByUserLogin+" "+"("+createdByEmailAddress+")";
                        timeInfo += eventDate;
                        if (ipAddress) {
                        	timeInfo +=" Ip Address: "+ipAddress;
                        }
                        content += '<div class="row">';
                        content += '<div class="event-hist icon-item">';
                        content += '<i class="' + favIcon + '" style="color:' + iColor + '"></i>';
                        //content+=imgSrc;
                        content += '</div>'
                        content += '<div>';
                        content += '<ul class="u-name">'
                        content += '<li class="event-hist li-name">'
                        content += '<h3>'
                        content += '' + eventInfo + '';
                        content += '</h3>'
                        content += '</li>'
                        content += '<li class="event-hist li-name">'
                        content += '' + timeInfo + '';
                        content += '</li>'
                        content += '</ul>'
                        content += '</div>'
                        content += '</div>'
                        content += '</div>'
                    }
                }
            }

            $("#event-history-body").html(content);
        }

    });
}

function getRandomColor() {
    var letters = '0123456789ABCDEF'.split('');
    var color = '#';
    for (var i = 0; i < 6; i++) {
        color += letters[Math.floor(Math.random() * 16)];
    }
    return color;
}
</script> 
</#macro>
