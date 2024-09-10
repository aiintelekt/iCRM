<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<#assign readOnlyPermission = Static["org.fio.homeapps.util.DataUtil"].hasPermissionWoFullPerm(request, "READ_ONLY_PERM")?if_exists />
<#if readOnlyPermission!>
	<script>
		$(document).ready(function(){
	        $('a').each(function(){ 
	        	var elementId = $(this).attr('id')
	        	if(elementId === "link_contactId"){
	        		$(this).addClass("view-link");
	        		$(this).attr("href", "#"); // Set herf value
	            	$(this).attr("target","");	
	        	}
	        });
	        
	        $("a.view-link").click(function () {
	            $("#accessDenied").modal("show");
	            return false;
	        });
	    });
	</script>
</#if>
<#assign dynaSuffix = ''>
<#assign isSRActivity = 'Y' />
<#if domainEntityType?has_content && domainEntityType == 'REBATE'>
	<#assign dynaSuffix = '_RBT'>
	<#assign isSRActivity = 'N' />
<#elseif domainEntityType?has_content && domainEntityType == 'ACCOUNT'>
	<#assign dynaSuffix = '_ACCT'>
	<#assign isSRActivity = 'N' />
<#elseif domainEntityType?has_content && domainEntityType == 'OPPORTUNITY'>
	<#assign dynaSuffix = '_OPPO'>
	<#assign isSRActivity = 'N' />
<#elseif domainEntityType?has_content && domainEntityType == 'CUSTOMER'>
	<#assign dynaSuffix = '_CUST'>
	<#assign isSRActivity = 'N' />	
<#elseif isProgAct?has_content && isProgAct=="Y">
	<#assign dynaSuffix = '_PROG'>
	<#assign isSRActivity = 'N' />	
<#elseif domainEntityType?has_content && domainEntityType == 'CONTACT'>
	<#assign dynaSuffix = '_CONT'>
	<#assign isSRActivity = 'N' />		
</#if>
<script src="/bootstrap/js/ckeditor/ckeditor.js" type="text/javascript"></script>
<div class="row">
    <input type='hidden' id="workEffortId" name="workEffortId" value="${requestParameters.workEffortId?if_exists}" />
    <input type='hidden' id="primOwnerId" name="primOwnerId" value="${requestParameters.primOwnerId?if_exists}" />
    <input type='hidden' id="emplTeamId" name="workEffortId" value="${requestParameters.emplTeamId?if_exists}" />
    <input type='hidden' id="businessUnitName" name="businessUnitName" value="${requestParameters.businessUnitName?if_exists}" />
    <input type='hidden' id="businessUnitId" name="businessUnitId" value="${requestParameters.businessUnitId?if_exists}" />
    <div id="main" role="main">
        <div class="col-lg-12 col-md-12 col-sm-12 dash-panel">
            <div>
                <div class="row">
                    <div class="col-lg-12 col-md-12 col-sm-12">
                        <#assign workEfforts=(Static["org.ofbiz.entity.util.EntityQuery"].use(delegator).select("seqId").from("UserLoginHistory").where("entity","WorkEffortCallSummary","userLoginId",userLogin.userLoginId).maxRows(5).orderBy("-fromDate").distinct().queryList())?if_exists />
                        <#assign workEffortIds=Static["org.ofbiz.entity.util.EntityUtil"].getFieldListFromEntityList(workEfforts, "seqId" , true)>
                            <div class="text-left float-left">
                                <h3 class="float-left"> View Activity : ${inputContext?if_exists.workEffortName?if_exists} [${requestParameters.workEffortId?if_exists}]</h3>
                            </div>
                    </div>
                    <div class="text-right position-absolute" style="right:20px;">
                    </div>
                </div>
            </div>
        </div>
        <div class="col-lg-12 col-md-12 col-sm-12 dash-panel">
            <div class="row">
                <div class="col-md-4">
                    <h6>Activity : Information</h6>
                    <@headerH3 id="test3" title="" />
                </div>
                <div class="col-md-8 right-details">
                    <div class="bd-callout">
                        <small>Activity Type</small>
                        <span class="text-danger"></span>
                        <h5 id="workEffortServiceTypeDescription2">${inputContext.type!}</h5>
                    </div>
                    <div class="bd-callout">
                        <small>Priority</small>
                        <span class="text-danger"></span>
                        <h5 id="priority">${inputContext.priorityDesc!}</h5>
                    </div>
                </div>
            </div>
        </div>
        <#if !isWorkflowActivity?has_content>
            <#assign isWorkflowActivity="N">
        </#if>
        <#if "Y"==isEnableBasicBar>
	        <#assign domainEntityType='${domainEntityType!inputContext.domainEntityTypeId!}' />
	        <#if domainEntityType=="">
	            <#assign domainEntityType=request.getParameter("domainEntityType")! />
	        </#if>
	        <#if domainEntityType?if_exists=="ACCOUNT">
	            <div class="col-lg-12 col-md-12 col-sm-12 dash-panel" id="cp">
	                <@dynaScreen 
	                    instanceId="ACCT_BASIC_INFO" 
	                    modeOfAction="VIEW" 
	                />
	            </div>
	        </#if>
	        <#if domainEntityType?if_exists=="LEAD">
	            <div class="col-lg-12 col-md-12 col-sm-12 dash-panel" id="cp">
	                <@dynaScreen 
	                    instanceId="LEAD_BASIC_INFO" 
	                    modeOfAction="VIEW" 
	                />
	            </div>
	        </#if>
	        <#if domainEntityType?if_exists=="CONTACT">
	            <div class="col-lg-12 col-md-12 col-sm-12 dash-panel" id="cp">
	                <@dynaScreen 
	                    instanceId="CONT_BASIC_INFO" 
	                    modeOfAction="VIEW" 
	                />
	            </div>
	        </#if>
	        <#if domainEntityType?if_exists=="OPPORTUNITY">
	            <div class="col-lg-12 col-md-12 col-sm-12 dash-panel" id="cp">
	                <@dynaScreen 
	                    instanceId="OPPORTUNITY_BASIC" 
	                    modeOfAction="VIEW" 
	                />
	            </div>
	        </#if>
	    </#if>
        <div class="clearfix"></div>
        <div class="col-lg-12 col-md-12 col-sm-12">
            <#if communicationEventTypeId?exists && communicationEventTypeId?has_content && communicationEventTypeId=="SMS_COMMUNICATION">
                <@navTab instanceId="VIEW_UNASSIGNED_SMS_ACTIVITY" activeTabId="activityDetails" />
	            <#else>
	            <#if inputContext.workEffortTypeId?has_content && inputContext.workEffortTypeId=="TASK">
	                <#if domainEntityType?has_content && isSRActivity?has_content && "Y"==isSRActivity && (!isProgAct?has_content || isProgAct=="N" )>
	                    <@navTab instanceId="VIEW_TASK_ACTIVITY" activeTabId="activityDetails" />
	                    <#else>
	                        <@navTab 
	                            instanceId="VIEW_ACTIVITY" 
	                            activeTabId="activityDetails" 
	                        />
	                </#if>
	                <#else>
	                    <@navTab 
	                        instanceId="VIEW_ACTIVITY" 
	                        activeTabId="activityDetails" 
	                    />
	            </#if>
	        </#if>
            <#-- <#if isEnableRebateModule?has_content && isEnableRebateModule=="Y">
                <div id="rebate" class="tab-pane fade">
                    ${screens.render("component://common-portal/widget/rebate/RebateScreens.xml#ListRebate")}
                </div>
                </#if>
                -->
                <script>
                    $(document).ready(function() {
                        $('.attr-collapse').removeClass('hide').addClass('show');
                        $('#attr-collapseAll-btn i').removeClass('fa-arrow-circle-down').addClass('fa-arrow-circle-up');
                        $('#attr-collapseAll-btn span').text('Collapse All');
                    });
                </script>
        </div>
<div id="assignModal" class="modal fade" role="dialog">
    <div class="modal-dialog" style="width:60%;">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title">Assign To</h4>
                <button type="button" class="close" data-dismiss="modal" id="close">&times;</button>
            </div>
            <div class="modal-body">
                <form method="post" action="#" id="assignModal" class="form-horizontal" name="" novalidate="novalidate" data-toggle="validator">
                    <div class="row p-1">
                        <div class="col-md-12 col-lg-12 col-sm-12 ">
                            &nbsp;User &nbsp;<input type="radio" id="user" name="emp" value="user">
                            Team &nbsp;<input type="radio" id="team" name="emp" value="team">
                            <div class="textboxUser" id="textboxUser">
                                <@dropdownCell
                                label="User/Team"
                                required=true
                                id="userText"
                                value=""
                                placeholder="Select User"
                                />
                            </div>
                            <div class="textboxteam" id="textboxteam" style="display: none;">
                                <@dropdownCell
                                label="Team"
                                required=true
                                id="teamText"
                                value=""
                                placeholder="Select Team"
                                />
                            </div>
                        </div>
                    </div>
                </form>
                <#-- added -->
                <div class="modal-footer">
                    <@submit class="btn btn-sm btn-primary navbar-dark" id="saveModal" label="${uiLabelMap.Save}" />
                    <button type="button" class="btn btn-default btn-primary navbar-dark" id="btnclose" data-dismiss="modal">Close</button>
                    <@reset id="reset" label="${uiLabelMap.Reset}" />
                </div>
            </div>
        </div>
    </div>
</div>

<div id="accessDenied" class="modal fade " tabindex="-1" role="dialog">
  <div class="modal-dialog" role="document">
    <div class="modal-content alert alert-danger">
      <div class="modal-header">
        <h5 class="modal-title">Alert!</h5>
        <button type="button" class="close" data-dismiss="modal" aria-label="Close">
          <span aria-hidden="true">&times;</span>
        </button>
      </div>
      <div class="modal-body">
        <p><h1>You do not have the permission.</h1></p>
      </div>
    </div>
  </div>
</div>
<script>
$(document).ready(function () {
    document.getElementById("detailsId").click();
    //loadActivity();
    //$('#messages').append('${messages!}');
    $('#assignModal #close').click(function () {
        $('#assignModal input[type=reset]').click();
    });
    $('#assignModal #btnclose').click(function () {
        $('#assignModal input[type=reset]').click();
    });
    $('#workEffortTypeId').val('${inputContext.srType!}');

    //var htmlContent='${inputContext.content!}';

    var url = document.URL;
    var hash = url.substring(url.indexOf('#'));
    $(".nav-tabs").find("li a").each(function (key, val) {
        if (hash == $(val).attr('href')) {
            $(val).click();
        }

        $(val).click(function (ky, vl) {
            location.hash = $(this).attr('href');
        });
    });
    
<#if !activeTab?has_content>
	<#assign activeTab = requestParameters.activeTab!>
</#if>
<#if activeTab?has_content && activeTab == "customFields">
	$('.nav-tabs a[href="#customFields"]').tab('show');
<#else>
	$('.nav-tabs a[href="#activityDetails"]').tab('show');	
</#if>

});
$("#assign").click(function () {
    var workEffortId = $('#workEffortId').val();
    var emplTeamId = $('#emplTeamId').val();
    var businessUnitId = $('#businessUnitId').val();
    var businessUnitName = $('#businessUnitName').val();
    var primOwnerId = $('#primOwnerId').val();
    if (workEffortId != null && workEffortId != "") {
        if (emplTeamId != "" && businessUnitId != "") {
            loaduserteam(emplTeamId, businessUnitId, workEffortId);
            $("#assignModal").modal();
            $("input[type='reset']").hide();
        } else {
            $.notify({
                message: '<p>There is no User or Team to assign</p>',
            });
        }
    } else {
        $.notify({
            message: '<p>There is no activity to assign</p>',
        });
    }
});

function loaduserteam(emplTeamId, businessUnitId, workEffortId) {
    var nonSelectContent;
    var userOption;
    var teamOptions;
    var dataSet = {};
    $("input[name$='emp']").click(function () {
        var test = $(this).val();
        if (test == "user") {
            $("div.textboxteam").hide();
            $("div.textboxUser").show();
            document.getElementById("userText").innerHTML = null;
            dataSet = {
                "emplTeamId": emplTeamId,
                "businessUnitId": ""
            };
        }
        if (test == "team") {
            $("div.textboxteam").show();
            $("div.textboxUser").hide();
            document.getElementById("userText").innerHTML = null;
            dataSet = {
                "emplTeamId": "",
                "businessUnitId": businessUnitId
            };
        }

        $.ajax({
            type: "POST",
            url: "getOwnerTeam",
            data: dataSet,
            async: false,
            success: function (data) {
                nonSelectContent = "<span class='nonselect'>Please Select</span>";
                userOption = '<option value="" data-content="' + nonSelectContent + '" selected="">Please Select</option>';
                teamOptions = '<option value="" data-content="' + nonSelectContent + '" selected="">Please Select</option>';
                var sourceDesc = data.results;
                for (var i = 0; i < data.length; i++) {
                    var category = data[i];
                    userOption += '<option value="' + category.partyId + '">' + category.userLoginId + '</option>';
                    teamOptions += '<option value="' + category.emplTeamId + '">' + category.teamName + '</option>';
                }
            }
        });

        $("#userText").html(userOption);
        $("#teamText").html(teamOptions);
    });
    var result;
    document.getElementById('saveModal').onclick = () => {
        var primOwnerId = $('#userText').val();
        var emp = $('#teamText').val();
        var dataSets = {
            "workEffortId": workEffortId,
            "primOwnerId": primOwnerId,
            "emplTeamId": emp
        };
        $.ajax({
            type: "POST",
            url: "UpdateReasignActivity",
            data: dataSets,
            async: false,
            success: function (data) {
                $("#assignModal").modal('hide');
                $.notify({
                    message: '<p>Reassigned Successfully for</p>' + workEffortId,
                });
            }
        });
    }
}

$("#doSave").click(function (event) {
    event.preventDefault();
    savedetails();
});
$("#doCancel").click(function (event) {
    event.preventDefault();
    var valid = true; 
    <#if (inputContext.workEffortTypeId?has_content && inputContext.workEffortTypeId == "TASK") && (inputContext.isSchedulingRequired?has_content && inputContext.isSchedulingRequired == "Y") >
        $.ajax({
            type: "POST",
            url: "/common-portal/control/getActivityTimeEntryCount",
            data: {
                "workEffortId": '${requestParameters.workEffortId!}',
                "externalLoginKey": "${requestAttributes.externalLoginKey!}"
            },
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
    </#if>

    if (valid) {
        canceldetails();
    }

});

function savedetails() {
    var workEffortId = $('#workEffortId').val();
    var currentStatusId = $('#currentStatusId').val();
    $.ajax({
        url: 'updateServiceActivityDetails',
        data: {
            "workEffortId": workEffortId,
            "currentStatusId": currentStatusId
        },
        type: "post",
        success: function (data) {
            showAlert("success", "Saved Successfully");
            loadActivity();
            location.reload();
            return data;
        },
        error: function (data) {
            return data;
        }
    });
}

function canceldetails() {
    var workEffortId = $('#workEffortId').val();
    var currentStatusId = $('#currentStatusId').val();
    $.ajax({
        url: 'closedServiceActivityDetails',
        data: {
            "workEffortId": workEffortId,
            "currentStatusId": currentStatusId
        },
        type: "post",
        success: function (data) {
            showAlert("success", " Activity Closed Successfully");
            loadActivity();
            location.reload();
            return data;
        },
        error: function (data) {
            console.log("dataerror====", data);
            return data;
        }
    });
}

function loadActivity() {
    var workEffortId = $("#workEffortId").val();
    dataSet = {
        "workEffortId": workEffortId
    };
    $.ajax({
        url: 'getActivityDetails',
        data: dataSet,
        type: "post",
        success: function (data) {

            var workEffortId1 = data[0].workEffortId;

            var workEffortServiceTypeDescription = data[0].workEffortServiceTypeDescription;
            var workEffortSubServiceTypeDescription = data[0].workEffortSubServiceTypeDescription;
            var priority = data[0].priority;
            var currentStatusId = data[0].currentStatusId;

            if (workEffortId1 != null) {
                document.getElementById("test3").innerHTML = workEffortId1;
            } else {
                document.getElementById("test3").innerHTML = "--";
            }


            if (priority != null) {
                document.getElementById("priority").innerHTML = priority;
            } else {
                document.getElementById("priority").innerHTML = "--";
            }


            if (workEffortServiceTypeDescription != null) {
                document.getElementById("workEffortServiceTypeDescription2").innerHTML = workEffortServiceTypeDescription;
            } else {
                document.getElementById("workEffortServiceTypeDescription2").innerHTML = "--";
            }
            if (workEffortSubServiceTypeDescription != null) {
                document.getElementById("workEffortSubServiceTypeDescription2").innerHTML = workEffortSubServiceTypeDescription;
            } else {
                document.getElementById("workEffortSubServiceTypeDescription2").innerHTML = "--";
            }
            if (currentStatusId != null) {
                $(".currentStatusId [data-value='" + currentStatusId + "']").click();
            }

            if (currentStatusId == "IA_MCOMPLETED") {
                $("#currentStatusId").empty();
                document.getElementById('assign').setAttribute('disabled', true);
                document.getElementById('doSave').setAttribute('disabled', true);
                document.getElementById('doCancel').setAttribute('disabled', true);
            }

        }
    });
}
</script>
<style>
    div#contactId>a{
		font-weight: bold;
	    font-family: sans-serif;
    }
    div#domainEntityId1>a{
		font-weight: bold;
	    font-family: sans-serif;
    }
    div#domainEntityId>a{
		font-weight: bold;
	    font-family: sans-serif;
    }
</style>