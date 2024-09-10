<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<style>
body {
  pointer-events:none;
}
.sticky-panel{		
	color: #02829d; 
	text-align : right;
	top: 6.6rem;
    display: block;   
	font-size: 1.3vw;
	font-weight: 600;

}
.sticky-bar1 {
    margin-top: 130px!important;
}
.sticky-bar {
    position: fixed;
    margin-top: -116px !important;
    max-width: 100%;
    margin-right: 15px;
    width: -webkit-fill-available;
    z-index: 999;
}
.dot-line{
    padding-bottom: 13px;
}

</style>
<div class="row">
    <div id="main" role="main" class="pd-btm-title-bar">
        <#-- <@sectionFrameHeader title="Find Accounts" /> -->
        <#assign extraLeft='
        <a id=task1" title="Task1" href="#" class="btn btn-primary btn-xs" onclick="#"> Task 1</a>
        <a id="task2" title="Task2" href="#" class="btn btn-primary btn-xs" onclick="#">Task 2</a>
        <a id="task3" title="Task3" href="#" class="btn btn-primary btn-xs" onclick="#">Task 3</a>
        <a id="task4" title="Task4" href="#" class="btn btn-primary btn-xs" onclick="#">Task 4</a>
        <a id="task5" title="Task5" href="#" class="btn btn-primary btn-xs" onclick="#">Task 5</a>
        ' />
        <#-- <@sectionFrameHeader title="Recently Viewed:" extraLeft=extraLeft /> -->
        <div class="col-lg-12 col-md-12 col-sm-12">
            <#assign addActivities='
            <div class="dropdown-menu" aria-labelledby="E10007">
                <h4>Add Activities</h4>
                <a class="dropdown-item" href="/account-portal/control/createTaskActivity?partyId=${partyId?if_exists}&domainEntityType=${domainEntityType!}&domainEntityId=${domainEntityId!}&externalLoginKey=${requestAttributes.externalLoginKey!}" target="_blank"><i class="fa fa-clipboard" aria-hidden="true"></i> Task</a> 
			    <a class="dropdown-item" href="/account-portal/control/createPhoneCallActivity?partyId=${partyId?if_exists}&domainEntityType=${domainEntityType!}&domainEntityId=${domainEntityId!}&externalLoginKey=${requestAttributes.externalLoginKey!}" target="_blank"><i class="fa fa-phone" aria-hidden="true"></i> Phone Call</a>
			    <a class="dropdown-item" href="/account-portal/control/addEmail?partyId=${partyId?if_exists}&domainEntityType=${domainEntityType!}&domainEntityId=${domainEntityId!}&externalLoginKey=${requestAttributes.externalLoginKey!}" target="_blank"><i class="fa fa-envelope" aria-hidden="true"></i> Email</a>
			    <a class="dropdown-item" href="/account-portal/control/createAppointmentActivity?partyId=${partyId?if_exists}&domainEntityType=${domainEntityType!}&domainEntityId=${domainEntityId!}&externalLoginKey=${requestAttributes.externalLoginKey!}" target="_blank"><i class="fa fa-calendar" aria-hidden="true"></i> Appointment</a>
            </div>
            ' />
            <#-- <a class="dropdown-item" href="/sales-portal/control/addOthers?partyId=${partyId?if_exists}&domainEntityType=${domainEntityType!}&domainEntityId=${domainEntityId!}&externalLoginKey=${requestAttributes.externalLoginKey!}" target="_blank"><i class="fa fa-plus-square" aria-hidden="true"></i> Others</a> -->
                <#assign toggleDropDownData={"E10007":addActivities!} />
                <div class="card-head margin-adj mt-2  sticky-bar" id="view-detail">
                    <div class="col-lg-12 col-md-12 dot-line">
                        <div class="row">
                            <div class="col-lg-6 col-md-6">
                                <h3 class="float-left mr-2 mb-0 header-title" style="padding-top:12px;">Account Details</h3>
                                <span class="sticky-panel">
                                    <#if partyExternalId?has_content><strong>Source ID: ${partyExternalId?if_exists}</strong></#if>
                                </span>
                            </div>
                            <div class="col-lg-6 col-md-6" style="padding-bottom: 6px">
                                <a href="findAccount" class="btn btn-xs btn-primary float-right text-right"><i class="fa fa-chevron-circle-left"> Back</i></a>
								<#if isBoldBIReportEnabled?has_content && isBoldBIReportEnabled =="Y">
									<a href="/admin-portal/control/transactionHomePage?partyId=${partyId!}&externalLoginKey=${requestAttributes.externalLoginKey!}" class="btn btn-xs btn-primary float-right text-right" target="_blank"> View Dashboard</a>
								</#if>
                            </div>
                        </div>
                    </div>
                    <@AppBar
                        appBarId="ACTION_APP_BAR"
                        appBarTypeId="ACTION" 
                        id="appbar1" extra=extra! 
                        toggleDropDownData=toggleDropDownData! 
                        isEnableUserPreference=true 
                    />
                </div>
                <#assign partyId='${requestParameters.partyId!}'>
                <@inputHidden name="partyId" id="partyId" value="${partyId!}" />
                <div class="card-head margin-adj mt-2 sticky-bar1">
                    <#-- <h3 class="float-left mr-2 mb-0 header-title view-title">Kpi Metrics</h3> -->
                        <div class="row	">
                            <div class="col-lg-12 col-md-12">
                            <h3 class="float-left mr-2 mb-0 header-title view-title">KPI Metrics
                                <#if isEnableDashboardButton?has_content && isEnableDashboardButton =="Y"> 
                                <a title="view" href="/dashboard-portal/control/viewPartyDashboard?partyId=${partyId!}&requestUri=${request.getRequestURI()!}&externalLoginkey=${requestAttributes.externalLoginKey!}" target="_blank" class="btn btn-primary btn-xs ml-2 float-right"> View Dashboard </a>
                                </#if>
                            </h3>
                            </div>
                        </div>
                        <@AppBar
                            appBarId="ACCOUNT_KPI_BAR" 
                            appBarTypeId="KPI" 
                            id="kpi-metrics" 
                            isEnableUserPreference=true 
                        />
                </div>
                    <#if isEnableBasicBar?has_content && "Y"==isEnableBasicBar!>
                        <#-- Basic information -->
                            <div class="basic-info mt-3" id="cp">
                                <#-- <h3 class="float-left mr-2 mb-0 header-title view-title">Basic Information</h3> -->
                                    <div class="row	">
                                        <div class="col-lg-12 col-md-12">
                                            <h3 class="float-left mr-2 mb-0 header-title view-title">Basic Information</h3>
                                        </div>
                                    </div>
                                    <@dynaScreen
                                        instanceId="ACCT_BASIC_INFO" 
                                        modeOfAction="VIEW" 
                                    />
                            </div>
                    </#if>
                    <#assign tabToLoad = parameters.tabIdToLoad!'a-details' />
					<@navTab
						instanceId="VIEW_ACCOUNT"
						activeTabId=tabToLoad!''
						/>
                   <#-- <@navTab
                        instanceId="VIEW_ACCOUNT" 
                        activeTabId="a-details" 
                    />-->
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
        if(elementData !=null && elementData != "" && elementData != 'undefined'){
            $("#emailForm input[name=toEmail]").val(elementData);
        } else{
        }
    });
    
    $("#phoneCall").on("show.bs.modal", function(e) {
        var elementData = $(e.relatedTarget).data('element-data');
        var modal = $(this);
        if(elementData !=null && elementData != "" && elementData != 'undefined'){
            //$("#contactNumber").attr("href", "skype:"+elementData+"?call");
        } else{
        }
    });  
    $(document).ready(() => {
	  $('body').css('pointer-events', 'all') //activate all pointer-events on body
	})
    $(document).ready(function() {
    	
    	if($("#note-search-form #isImportant").length)
	    	$("#note-search-form #isImportant").val("");
	    	
	    if($('#srOpen').length){
	    	$('#srOpen').prop('checked', false);
	    	$("#sr-search-form #open").val("");
	    }
	    
	if($('#openchk').length) {
		$('#openchk').prop('checked', true);
		$("#activity-search-form #open").val($('#openchk').val());
		$("#act-search-btn").click();
	}
	    
	    if($('#oppoOpen').length){
	    	$('#oppoOpen').prop('checked', false);
	    	$("#opportunity-search-form #statusOpen").val("");
	    }
	    
    	var partyId  = $("#partyId").val();
   		
   		var link = document.getElementById("E10010");
   		link.setAttribute('href', "/account-portal/control/addEmail?partyId="+partyId+"&domainEntityType=LEAD&domainEntityId="+partyId);
   		
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
	location.hash = "#a-notes";
	loadTabContent("VIEW_ACCOUNT", "a-notes", function() {
		$("#note-search-form #isImportant").val("Y");
		$('.nav-tabs a[href="#a-notes"]').tab('show');
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
	location.hash = "#a-activities";
	$('.nav-tabs a[href="#a-activities"]').tab('show');
	loadTabContent("VIEW_ACCOUNT", "a-activities", function() {
		$('#openchk').prop('checked', true);
		var openStatus = $('input[name="openchk"]:checked').val();
		$("#activity-search-form #open").val(openStatus);
		$("#act-search-btn").click();
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
	location.hash = "#a-opportunities";
	$('.nav-tabs a[href="#a-opportunities"]').tab('show');
	loadTabContent("VIEW_ACCOUNT", "a-opportunities", function() {
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
	location.hash = "#a-sr";
	$('.nav-tabs a[href="#a-sr"]').tab('show');
	loadTabContent("VIEW_ACCOUNT", "a-sr", function() {
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