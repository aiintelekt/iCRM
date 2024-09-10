<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<#if isBirthdayRemainderEnabled?has_content && isBirthdayRemainderEnabled?if_exists=="Y">
<#include "component://campaign/webapp/campaign/birthdayRemainder/birthdayModal.ftl"/>
</#if>
<#-- Temporary code, will be remove -->
<script>
function dateFieldComparator(date1, date2) {
	var date1Number = date1 && new Date(date1).getTime();
	var date2Number = date2 && new Date(date2).getTime();

	if (date1Number == null && date2Number == null) {
		return 0;
	}

	if (date1Number == null) {
		return -1;
	} else if (date2Number == null) {
	    return 1;
	}

	return date1Number - date2Number;
}
</script>

<div class="row">
    <div id="main" role="main" class="pd-btm-title-bar">
        <div class="col-lg-12 col-md-12 col-sm-12">
            <#assign addActivities = '
            <div class="dropdown-menu" aria-labelledby="E10007">
                <h4>Add Activities</h4>
                <a class="dropdown-item" href="/customer-portal/control/createTaskActivity?partyId=${partyId?if_exists}&domainEntityType=${domainEntityType!}&domainEntityId=${domainEntityId!}&externalLoginKey=${requestAttributes.externalLoginKey!}" target="_blank"><i class="fa fa-clipboard" aria-hidden="true"></i> Task</a> 
                <a class="dropdown-item" href="/customer-portal/control/createPhoneCallActivity?partyId=${partyId?if_exists}&domainEntityType=${domainEntityType!}&domainEntityId=${domainEntityId!}&externalLoginKey=${requestAttributes.externalLoginKey!}" target="_blank"><i class="fa fa-phone" aria-hidden="true"></i> Phone Call</a>
                <a class="dropdown-item" href="/customer-portal/control/addEmail?partyId=${partyId?if_exists}&domainEntityType=${domainEntityType!}&domainEntityId=${domainEntityId!}&externalLoginKey=${requestAttributes.externalLoginKey!}" target="_blank"><i class="fa fa-envelope" aria-hidden="true"></i> Email</a>
                <a class="dropdown-item" href="/customer-portal/control/createAppointmentActivity?partyId=${partyId?if_exists}&domainEntityType=${domainEntityType!}&domainEntityId=${domainEntityId!}&externalLoginKey=${requestAttributes.externalLoginKey!}" target="_blank"><i class="fa fa-calendar" aria-hidden="true"></i> Appointment</a>
            </div>
            ' />
            <#assign toggleDropDownData = {"E10007":addActivities!} />
            <#assign isShowHelpUrl="Y">
            <#if isPhoneCampaignEnabled?has_content && isPhoneCampaignEnabled?if_exists=="Y">
			<#assign isShowHelpUrl="N">
			</#if>
            <div class="card-head margin-adj mt-2" id="view-detail">
            	<#if contactLisId?has_content && contactLisId?exists && marketingCampaignId?has_content && marketingCampaignId?exists>
                        <#assign extras='<a href="viewCallListCustomer?partyId=${partyId!}&marketingCampaignId=${marketingCampaignId!}&contactLisId=${contactLisId!}" class="btn btn-xs btn-primary float-right text-right"><i class="fa fa-chevron-circle-left"></i> Back</a>'/>
                <#else>
                        <#assign extras='<a href="findCustomer" class="btn btn-xs btn-primary float-right text-right"><i class="fa fa-chevron-circle-left"></i> Back</a>'/>
                </#if>
                <@sectionFrameHeader title="Customer Details" extra=extras! isShowHelpUrl=isShowHelpUrl!/>
                
                <#if inputContext.statusId! ==  "PARTY_DISABLED">
	                <@AppBar  
		                appBarId="CUST_INACT_BAR"
		                appBarTypeId="ACTION"
		                id="appbar1"
		                extra=extra!
		                toggleDropDownData=toggleDropDownData!
		                isEnableUserPreference=true
		                />
                <#else>
	                <@AppBar  
		                appBarId="CUSTOMER_ACTION_BAR"
		                appBarTypeId="ACTION"
		                id="appbar1"
		                extra=extra!
		                toggleDropDownData=toggleDropDownData!
		                isEnableUserPreference=true
		                />
                </#if>
            </div>
            <#assign partyId = '${requestParameters.partyId!}' >
            <@inputHidden name="partyId" id="partyId" value = "${partyId!}"/>
            <div class="card-head margin-adj mt-2">
                 <div class="row">
                    <div class="col-lg-12 col-md-12">
                    <h3 class="float-left mr-2 mb-0 header-title view-title">KPI Metrics
                        <#if isEnableDashboardButton?has_content && isEnableDashboardButton =="Y"> <a title="view" href="/dashboard-portal/control/viewPartyDashboard?partyId=${partyId!}&requestUri=${request.getRequestURI()!}&externalLoginkey=${requestAttributes.externalLoginKey!}" target="_blank" class="btn btn-primary btn-xs ml-2 float-right"> View Dashboard </a></#if>
                    </h3>
                    </div>
                </div>
                <@AppBar
	                appBarId="CUST_KPI_BAR"
	                appBarTypeId="KPI"
	                id="kpi-metrics"
	                isEnableUserPreference=true
	                />
            </div>
            <#if isEnableBasicBar?has_content && "Y" == isEnableBasicBar!>
            <#-- Basic information -->
            <div class="basic-info mt-3" id="cp">
                <div class="row	">
                    <div class="col-lg-12 col-md-12">
                        <h3 class="float-left mr-2 mb-0 header-title view-title">Basic Information</h3>
                    </div>
                </div>
                <@dynaScreen 
	                instanceId="CUST_BASIC_INFO"
	                modeOfAction="VIEW"
	                />
            </div>
            </#if>
            <#if isPhoneCampaignEnabled?has_content && isPhoneCampaignEnabled?if_exists=="Y">
	         <#assign hideTabIds="c-activities,c-communicationHistory,c-invoice,c-mergedParties,c-opportunities,c-rebate,c-receipt,e-value,quotes,serviceRequests,c-contracts">
			<#else>
				<#assign hideTabIds="callStatusHistory">
			</#if>
			<#assign tabToLoad = parameters.tabIdToLoad!'c-details' />
			<@navTab
				instanceId="VIEW_CUSTOMER"
				activeTabId=tabToLoad!''
				hideTabIds=hideTabIds!
				/>
        </div>
    </div>
</div>
${screens.render("component://common-portal/widget/common/CommonScreens.xml#AddCommonFeatures")}

<div class="modal fade mt-5 save-modal" id="writeEmail" tabindex="-1" role="dialog" aria-labelledby="writeEmail" aria-hidden="true">
    <div class="modal-dialog modal-lg">
        <!-- Modal content-->
        <div class="modal-content">
            <div class="modal-header">
                <h3 class="modal-title">Write Email</h3>
                <button type="reset" class="close" data-dismiss="modal">&times;</button>
            </div>
            <div class="modal-body">
                <form id="emailForm" name="emailForm" method="post" data-toggle="validator">
                    <div class="form-group row">
                        <label for="staticEmail" class="col-sm-4 col-form-label text-danger">From</label>
                        <div class="col-sm-7">
                            <input class="form-control" name="fromEmail" id="fromEmail" value="" type="text">
                        </div>
                    </div>
                    <div class="form-group row">
                        <label class="col-sm-4 col-form-label text-danger">To</label>
                        <div class="col-sm-7">
                            <input class="form-control" name="toEmail" id="toEmail" value="" type="text">
                        </div>
                    </div>
                    <div class="form-group row">
                        <label class="col-sm-4 col-form-label">CC</label>
                        <div class="col-sm-7">
                            <input class="form-control" value="" type="text">
                        </div>
                    </div>
                    <div class="form-group row">
                        <label class="col-sm-4 col-form-label">BCC</label>
                        <div class="col-sm-7">
                            <input class="form-control" value="" type="text">
                        </div>
                    </div>
                    <div class="form-group row">
                        <label class="col-sm-4 col-form-label float-labels">Service Request</label>
                        <div class="col-sm-7">
                            <input class="form-control" value="" type="text">
                        </div>
                    </div>
                    <div class="form-group row">
                        <label class="col-sm-4 col-form-label float-labels">Template</label>
                        <div class="col-sm-7">
                            <select class="ui dropdown search form-control fluid show-tick" data-live-search="true">
                                <option value="notes">CASA Topup</option>
                                <option value="c3">Birth Day</option>
                                <option value="filenet"></option>
                            </select>
                        </div>
                    </div>
                    <div class="form-group row">
                        <label class="col-sm-4 col-form-label text-danger">Subject</label>
                        <div class="col-sm-7">
                            <input class="form-control" value="" type="text">
                        </div>
                    </div>
                    <div class="form-group row">
                        <label class="col-sm-4 col-form-label float-labels">Attachments</label>
                        <div class="col-sm-5">
                            <div class="custom-file">
                                <input type="file" class="custom-file-input" id="inputGroupFile01" aria-describedby="inputGroupFileAddon01">
                                <label class="custom-file-label rounded-0" for="inputGroupFile01">Choose file</label>
                            </div>
                        </div>
                        <div class="col-sm-2">
                            <a href="" class="btn btn-primary btn-sm"> <i class="fa fa-plus fa-1"></i> Add</a>
                        </div>
                    </div>
                    <div class="form-group row">
                        <label class="col-sm-4 col-form-label">Message</label>
                        <div class="col-sm-7">
                            <textarea class="form-control ta-phone-modal" rows="4"></textarea>
                        </div>
                    </div>
                </form>
            </div>
            <div class="modal-footer">
                <button type="submit" class="btn btn-sm btn-primary" data-dismiss="modal">Send</button>
                <button class="btn btn-sm btn-secondary" type="submit">Cancel</button>
            </div>
        </div>
    </div>
</div>
<div class="modal fade" id="phoneCall" tabindex="-1" role="dialog" aria-labelledby="skypeCall" aria-hidden="true">
    <div class="modal-dialog" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title" id="exampleModalLabel">Ring Central Call</h5>
                <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                <span aria-hidden="true">&times;</span>
                </button>
            </div>
            <div class="modal-body">
                Do you want to call?
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-dismiss="modal">Cancel</button>
                <a href="#" id="contactNumber" name="contactNumber" class="btn btn-primary">Call</a>
            </div>
        </div>
    </div>
</div>

<script>

$("#writeEmail").on("show.bs.modal", function(e) {
	var elementData = $(e.relatedTarget).data('element-data');
	var modal = $(this);
	if (elementData != null && elementData != "" && elementData != 'undefined') {
		$("#emailForm input[name=toEmail]").val(elementData);
	} else {}
});

$("#phoneCall").on("show.bs.modal", function(e) {
	var elementData = $(e.relatedTarget).data('element-data');
	var modal = $(this);
	if (elementData != null && elementData != "" && elementData != 'undefined') {
		//$("#contactNumber").attr("href", "skype:"+elementData+"?call");
	} else {}
});

$(document).ready(function() {

	if ($("#note-search-form #isImportant").length)
		$("#note-search-form #isImportant").val("");

	if ($('#srOpen').length) {
		$('#srOpen').prop('checked', false);
		$("#sr-search-form #open").val("");
	}
	
	if ($('#oppoOpen').length) {
		$('#oppoOpen').prop('checked', false);
		$("#opportunity-search-form #statusOpen").val("");
	}

	var partyId = $("#partyId").val();
	var link = document.getElementById("E10010");
	link.setAttribute('href', "/customer-portal/control/addEmail?partyId=" + partyId + "&domainEntityType=CUSTOMER&domainEntityId=" + partyId);

	var url = document.URL;
	var hash = url.substring(url.indexOf('#'));

	$(".nav-tabs").find("li a").each(function(key, val) {
		if (hash == $(val).attr('href')) {
			$(val).click();
		}

		$(val).click(function(ky, vl) {
			location.hash = $(this).attr('href');
		});
	});

});

$('.ab-im-notes').click(function() {
	location.hash = "#c-notes";
	$('.nav-tabs a[href="#c-notes"]').tab('show');
	loadTabContent("VIEW_CUSTOMER", "c-notes", function() {
		$("#note-search-form #isImportant").val("Y");
		$("#note-search-btn").click();
		var target = $('#nav-tab-focus');
		if (target.length) {
			$('html,body').animate({
				scrollTop: target.offset().top
			}, 1000);
			return false;
		}
	});
});

$('.ab-activity').click(function() {
	location.hash = "#c-activities";
	$('.nav-tabs a[href="#c-activities"]').tab('show');
	loadTabContent("VIEW_CUSTOMER", "c-activities", function() {
		$('#openchk').prop('checked', true);
		var openStatus = $('input[name="openchk"]:checked').val();
		$("#activity-search-form #open").val(openStatus);
		var target = $('#nav-tab-focus');
		if (target.length) {
			$('html,body').animate({
				scrollTop: target.offset().top
			}, 1000);
			return false;
		}
	});
});

$('.ab-oppo').click(function() {
	location.hash = "#c-opportunities";
	$('.nav-tabs a[href="#c-opportunities"]').tab('show');
	loadTabContent("VIEW_CUSTOMER", "c-opportunities", function() {
		$('#oppoOpen').prop('checked', true);
		var openStatus = $('input[name="oppoOpen"]:checked').val();
		if (openStatus != null && openStatus != "undefined" && openStatus != "")
			$("#opportunity-search-form #statusOpen").val(openStatus);
		$("#search-oppo-btn").click();
		var target = $('#nav-tab-focus');
		if (target.length) {
			$('html,body').animate({
				scrollTop: target.offset().top
			}, 1000);
			return false;
		}
	});
});

$('.ab-sr').click(function() {
	location.hash = "#serviceRequests";
	$('.nav-tabs a[href="#serviceRequests"]').tab('show');
	loadTabContent("VIEW_CUSTOMER", "serviceRequests", function() {
		$('#srOpen').prop('checked', true);
		var openStatus = $('input[name="srOpen"]:checked').val();
		$("#sr-search-form #open").val(openStatus);
		$("#service-req-btn").click();
		var target = $('#nav-tab-focus');
		if (target.length) {
			$('html,body').animate({
				scrollTop: target.offset().top
			}, 1000);
			return false;
		}
	});
});
</script>