<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<#include "component://common-portal/webapp/common-portal/lib/picker_macro.ftl"/>
<#include "component://uiadv-portal/webapp/uiadv-portal/lib/mobi_scroll_mobile_macros.ftl"/>

<script type="text/javascript" src="/common-portal-resource/js/ag-grid/activity/validation-activity.js"></script>
<script type="text/javascript" src="/common-portal-resource/js/ag-grid/activity/activity-utils.js"></script>
<script src="/bootstrap/js/ckeditor/ckeditor.js" type="text/javascript"></script>
<script src="/bootstrap/js/ckeditor/ck-custom-functions.js" type="text/javascript"></script>
<script>
    CKEDITOR.env.isCompatible = true;
</script>

<div class="row">
<div id="main" role="main" class="pd-btm-title-bar">
	  	
<#-- <@sectionFrameHeader title="Find Accounts"/> -->
<#assign extraLeft='
	<a id=task1" title="Task1" href="#" class="btn btn-primary btn-xs" onclick="#"> Task 1</a>
   	<a id="task2" title="Task2" href="#" class="btn btn-primary btn-xs" onclick="#">Task 2</a>
    <a id="task3" title="Task3" href="#" class="btn btn-primary btn-xs" onclick="#">Task 3</a>
    <a id="task4" title="Task4" href="#" class="btn btn-primary btn-xs" onclick="#">Task 4</a>
    <a id="task5" title="Task5" href="#" class="btn btn-primary btn-xs" onclick="#">Task 5</a>
' />
 <#--<@sectionFrameHeader  title="Recently Viewed:"  extraLeft=extraLeft  />-->

<#assign addActivities = '
        	<div class="dropdown-menu" aria-labelledby="E10007">
                <h4>Add Activities</h4>
                <a class="dropdown-item" href="/opportunity-portal/control/createTaskActivityOpportunity?partyId=${partyId?if_exists}&domainEntityType=${domainEntityType!}&domainEntityId=${domainEntityId!}&externalLoginKey=${requestAttributes.externalLoginKey!}&salesOpportunityId=${salesOpportunityId?if_exists}" target="_blank"><i class="fa fa-clipboard" aria-hidden="true"></i> Task</a> 
			    <a class="dropdown-item" href="/opportunity-portal/control/createPhoneCallActivityOpportunity?partyId=${partyId?if_exists}&domainEntityType=${domainEntityType!}&domainEntityId=${domainEntityId!}&externalLoginKey=${requestAttributes.externalLoginKey!}&salesOpportunityId=${salesOpportunityId?if_exists}" target="_blank"><i class="fa fa-phone" aria-hidden="true"></i> Phone Call</a>
			    <a class="dropdown-item" href="/opportunity-portal/control/addEmail?partyId=${partyId?if_exists}&domainEntityType=${domainEntityType!}&domainEntityId=${domainEntityId!}&externalLoginKey=${requestAttributes.externalLoginKey!}" target="_blank"><i class="fa fa-envelope" aria-hidden="true"></i> Email</a>
			    <a class="dropdown-item" href="/opportunity-portal/control/createAppointmentActivityOpportunity?partyId=${partyId?if_exists}&domainEntityType=${domainEntityType!}&domainEntityId=${domainEntityId!}&externalLoginKey=${requestAttributes.externalLoginKey!}&salesOpportunityId=${salesOpportunityId?if_exists}" target="_blank"><i class="fa fa-calendar" aria-hidden="true"></i> Appointment</a>
            </div>
        ' />
	    <#-- <a class="dropdown-item" href="/sales-portal/control/addOthers?partyId=${partyId?if_exists}&domainEntityType=${domainEntityType!}&domainEntityId=${domainEntityId!}&externalLoginKey=${requestAttributes.externalLoginKey!}" target="_blank"><i class="fa fa-plus-square" aria-hidden="true"></i> Others</a>  -->
        
        <#assign toggleDropDownData = {"E10007":addActivities!} />
<form id="mainFrom" name="createTaskActivity" method="post" action="<@ofbizUrl>createTaskActivityAction</@ofbizUrl>" data-toggle="validator">
<div class="col-lg-12 col-md-12 col-sm-12">
<#assign extra = '<span id="book-appointment" class="btn btn-xs btn-primary"><i class="fa fa-edit" aria-hidden="true"></i> Resource</span>'>
<@sectionFrameHeader   title="${uiLabelMap.createTaskActivity!}" extra=extra/>
	<input type="hidden" name="domainEntityType" value="${(parameters.domainEntityType)!}"/>
	<input type="hidden" name="domainEntityId" value="${(parameters.domainEntityId)!}"/>
	
					<#assign cifNo = '${requestParameters.partyId!}' >
                	<#assign custRequestId = '${requestParameters.custRequestId!}' >
	
					<@inputHidden name="ownerBookedCalSlots" id="ownerBookedCalSlots"/>
                    <@inputHidden name="cNo" id="cNo" value = "${cifNo!}"/>
                    <@inputHidden name="custRequestId" id="custRequestId" value = "${custRequestId!}"/>
                    <@inputHidden name="ownerBu" id="ownerBu" />
                    <@inputHidden name="inspActWorkTypeIds" id="inspActWorkTypeIds" value = "${inspActWorkTypeIds!}"/>
                    
                    <#assign srType = EntityQuery.use(delegator).from("WorkEffortAssocTriplet").where("entityName", "Activity", "type", "Type", "value", "Task", "active", "Y").queryFirst()! />
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
		                instanceId="CREATE_TASK_ACTIVITY"
		                modeOfAction="CREATE"
		             />
		             
	           
	            <div class="col-md-12 col-lg-12 col-sm-12 activity-desc">
         			<@textareaLarge  label="Description" id="messages" rows="4"/>
         			<script>
=					    CKEDITOR.replace( 'messages',{
					    	customConfig : '/bootstrap/js/ckeditor/ck-custom-config.js',
							autoGrow_minHeight : CKEditorUtil.autoGrowMinHeight,
							removePlugins : CKEditorUtil.removePlugins
					    });
					</script>
      			</div>
      			<div class="offset-md-2 col-sm-10 p-2">
	           		<@formButton
	                     btn1type="submit"
	                     btn1id=""
	                     btn1label="${uiLabelMap.Save}"
	                     btn2=true
	                     btn2onclick = "resetForm()"
	                     btn2type="reset"
	                     btn2label="${uiLabelMap.Clear}"
	                />
	            </div>
	             </div>

</form>
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

	var workEffortPurposeTypeId = $("#workEffortPurposeTypeId").val();
	if(workEffortPurposeTypeId == "TEST_WORK_TYPE" || workEffortPurposeTypeId == "TEST_WORK_TYPE_001"){
    	$("#statusId option[value='IA_CANCEL']").remove();
    } else{
    }
	$("#statusId").dropdown('refresh');
	
    onLoadDefaultElementsBehaviour();
    $("#owner").change(function() {
        var owner = $("#owner").val();
        if (owner != undefined && owner != null)
            getBusinessUnit(owner);
    });

    $('#onceDone').val("N");
    $('#onceDone').checked = true;
    $('#type').val($('#workEffortTypeId').val());
    $('#linkedFrom').val($('#salesOpportunityId').val());
    $('#type').attr('readonly', 'readonly');
    $('#ownerBuDesc').attr('readonly', 'readonly');

    var typeId = $("#srTypeId").val();
    if (typeId != "") {
        loadSubTypes(typeId);
    }

    $("#contactId").on("change", function() {
        getPartyTimeZonesList($("#contactId").val());
    });

    var userName = $("#userName").val();
    var loggedInUserId = $("#loggedInUserId").val();
    if (loggedInUserId != undefined && loggedInUserId != null)
        getBusinessUnit(loggedInUserId);
    
    var cNo = $("#cNo").val();
    if (cNo == null || cNo == undefined || cNo == "") {
        $("#cNo").val($("#partyId_val").val());
        cNo = $("#partyId_val").val();
    }
    if (cNo != null && cNo != undefined && cNo != "") {
        loadContacts();
    }
    loadTechArrivalWindow();

    prepareActivityDateInput($("#statusId").val());
    $("#statusId").change(function() {
        prepareActivityDateInput($(this).val());
    });
    
    $("#workEffortPurposeTypeId").on("change", function() {
        console.log('workEffortPurposeTypeId: '+$(this).val());
        if ($("#inspActWorkTypeIds").val()) {
        	var inspActWorkTypeIds = $("#inspActWorkTypeIds").val().split(',');
	        if (inspActWorkTypeIds.includes($(this).val())) {
	        	console.log('tech inspector work type');
	        	var context = new Map();
				context.set('roleTypeIds', 'TECH_INSPECTOR');
				$('#tech-cal-search-from input[name="isResourceType"]').val( 'TECH_INSPECTOR' );
				ACTUTIL.loadOwners('TASK', null, context, "${requestAttributes.externalLoginKey!}");
				$('#calendar_type_title').html("Inspectors");
	        } else {
	        	$('#tech-cal-search-from input[name="isResourceType"]').val( '' );
	        	ACTUTIL.loadOwners('TASK', null, null, "${requestAttributes.externalLoginKey!}");
	        	$('#calendar_type_title').html("Technicians");
	        } 
        } else{
        	ACTUTIL.loadOwners('TASK', null, null, "${requestAttributes.externalLoginKey!}");
        }
        
        var typeId = $(this).val();
        var scheduleTaskType = "${scheduleTaskTypes!'SCHEDULE_TASK'}";
        if(scheduleTaskType != null && scheduleTaskType !="" && scheduleTaskType !="undefined")
        	scheduleTaskType = scheduleTaskType.split(',');
        if(scheduleTaskType.includes(typeId)){
        	$('#statusId').dropdown('set selected', "IA_OPEN");
        	$('#statusId').dropdown('refresh');
        	prepareActivityDateInput("IA_OPEN");
        	var techType = "${thridPartyContractor!''}";
        	var context = new Map();
			context.set('techType', techType);
        	ACTUTIL.loadOwners('TASK', null, context, "${requestAttributes.externalLoginKey!}");
        }
        
        if(typeId == "TEST_WORK_TYPE" || typeId == "TEST_WORK_TYPE_001"){
        	$("#statusId option[value='IA_CANCEL']").remove();
        } else{
        	$("#statusId option[value='IA_CANCEL']").remove();
        	$('#statusId').append($('<option>', {
			    value: 'IA_CANCEL',
			    text: 'Cancelled'
			}));
        }
        
        if(typeId == "WEPT_INV"){
        	$('#statusId').dropdown('set selected', "IA_OTHER");
        	var owner = $("#owner").val();
			owner.push('APDEP');
			$('#owner').val(owner); 
        }
        
        $("#statusId").dropdown('refresh');
        
        let workEffortPurposeTypeId = $('#workEffortPurposeTypeId').val();
	    if (workEffortPurposeTypeId && (workEffortPurposeTypeId=='TEST_WORK_TYPE' || workEffortPurposeTypeId=='TEST_WORK_TYPE_001')) {
	    	$("#estimatedStartDate_time").prop("disabled", false);
	    	$("#estimatedCompletionDate_time").prop("disabled", false);
	    	$("input[name=isSchedulingRequired]").trigger('change');
	    } else {
	    	$("#estimatedStartDate_time").prop("disabled", true);
	    	$("#estimatedCompletionDate_time").prop("disabled", true);
	    	
	    	$('#estimatedStartDate_time').val('0:00');
	    	$('#estimatedCompletionDate_time').timepicker('setTime', '0:00');
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

    ACTUTIL.loadSrAssocParties('${(parameters.domainEntityId)!}', 'contactId', null, "${requestAttributes.externalLoginKey!}");
    ACTUTIL.loadOwners('TASK', null, null, "${requestAttributes.externalLoginKey!}");
    
    $('#mainFrom').validator().on('submit', function (e) {
		if (!e.isDefaultPrevented()) {
			//e.preventDefault();
	  		var valid = true;
	  		
	  		if ($('#statusId').val() === 'IA_MSCHEDULED' && !$('#ownerBookedCalSlots').val() && $("input[name=isSchedulingRequired]:checked").val()=='Y') {
	  			showAlert('error', 'Please select booking slot from "Schedule Task"!');
	  			valid = false;
	  			return valid;
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
	  		<#if verifyFsrOrderAssoc?has_content && verifyFsrOrderAssoc == "Y">
	  		$.ajax({
				type: "POST",
				url: "/common-portal/control/getSrOrderCount",
				data: {"srNumber": '${(parameters.domainEntityId)!}', "externalLoginKey": "${requestAttributes.externalLoginKey!}"},
				async: false,
				success: function (data) {   
					if (data.code == 200) {
						if (data.orderLineCount === 0) {
							if (!confirm('There are no Orders associated with this SR. Are you sure you want to proceed?')) {
								valid = false;
							}
						}
					}
				}
			});
			</#if>	 
	  		if (!valid) {
	  			e.preventDefault();
	  		}
	  		
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
		    if(submitForm){
		    	var $form = $(this);
		
			    if ($form.data('submitted') === true) {
			      event.preventDefault();
			    } else {
			      $form.data('submitted', true);
			    }
		    }
	  	} else{
	  		var $form = $(this);
			$form.data('submitted', false);
	  	}
	});    

});

function loadTechArrivalWindow() {
    var arrivalWindowVal = "${techArrivalWindows!}";
    var arrivalWindow = "${requestParameters.arrivalWindow!}";
    var arrivalList = arrivalWindowVal.split(",");
    var options = "<option value=''></option>";
    if (arrivalList != null && arrivalList != "" && arrivalList != "undefined") {
        $.each(arrivalList, function(i) {
        	var hrLabel = ' hr';
        	if (arrivalList[i] > 1) {
        		hrLabel = ' hrs';
        	}
            if (arrivalWindow != null && arrivalWindow != "" && arrivalWindow != "undefined" && arrivalWindow == arrivalList[i]) {
                options += '<option value="' + arrivalList[i] + '" selected>' + arrivalList[i] + hrLabel+'</option>';
            } else
                options += '<option value="' + arrivalList[i] + '">' + arrivalList[i] + hrLabel+'</option>';
        });
        $("#arrivalWindow").html(options);
        $("#arrivalWindow").dropdown('refresh');
    }
}

function onLoadDefaultElementsBehaviour() {

    var today = new Date();
    var dd = String(today.getDate()).padStart(2, '0');
    var mm = String(today.getMonth() + 1).padStart(2, '0');
    var yyyy = today.getFullYear();
    var hh = today.getHours();
    var m = today.getSeconds();
    today = mm + '/' + dd + '/' + yyyy + " " + hh + ":" + m;
    $('#taskDate').val(today.toLocaleString([], {
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
        success: function(data) {
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
</script>