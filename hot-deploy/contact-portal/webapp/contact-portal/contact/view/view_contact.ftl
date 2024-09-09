<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
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
            <#-- Action bar -->
                <#-- <div class="card-head margin-adj">
                    <h2>
                        <a href="#"> ${inputContext.accountName!}</a>
                    </h2>

                    <a href="#" class="text-dark left-icones" title="Portfolio">
                        <i class="fa fa-briefcase custicons" aria-hidden="true"></i>
                    </a>
                    <a href="financial-dashboard1.php" class="text-dark left-icones" title="Dashboard">
                        <i class="fa fa-area-chart custicons" aria-hidden="true"></i>
                    </a>
                    <a href="financial-dashboard1.php" class="text-dark left-icones" data-toggle="modal" data-target="#box-blue" title="Alerts">
                        <i class="fa fa-bell-o custicons" aria-hidden="true"></i>
                    </a>
                    <a href="financial-dashboard1.php" class="text-dark left-icones" data-toggle="modal" data-target="#blue-box" title="Add Notes">
                        <img src="/bootstrap/images/add-customer-alert.png" class="cust-icon" width="21" height="22">
                    </a>
                    <a href="#" title="Add Service Request" class="text-dark left-icones" data-toggle="modal" data-target="#blue-box">
                        <i class="fa fa-plus-square custicons" aria-hidden="true"></i>
                    </a>
                    <a href="#" title="Add Opportunity" class="text-dark left-icones" data-toggle="modal" data-target="#blue-box">
                        <img src="/bootstrap/images/add-opportunities.png" class="cust-icon" width="21" height="22">
                    </a>
                    <a href="#" title="Add Activity" class="text-dark left-icones" data-toggle="modal" data-target="#blue-box">
                        <img src="/bootstrap/images/add-activity.png" class="cust-icon" width="20" height="20">
                    </a>

                    <ul class="text-right">
                        <#if email?has_content>
                            <li>
                                <a href="#" class="mr-2 text-dark" data-toggle="modal" data-target="#phonepopup">
                                    <i class="fa fa-envelope fa-1" aria-hidden="true"></i> ${inputContext.email!}
                                </a>
                            </li>
                        </#if>
                        <#if contactNumber?has_content>
                            <li class="mr-2 text-dark" aria-hidden="true" data-toggle="modal" data-target="#myModal2">
                                <i class="fa fa-phone fa-1" aria-hidden="true"></i> ${inputContext.contactNumber!}
                            </li>
                        </#if>
                        <li>
                            <div class="text-right">
                                <a href="customer-profile3.php" class="mr-2 text-dark">
                                    <i class="fa fa-chevron-left fa-1" aria-hidden="true"></i>
                                </a>
                                <a href="customer-profile.php" class="text-dark">
                                    <i class="fa fa-chevron-right fa-1" aria-hidden="true"></i>
                                </a>
                            </div>
                        </li>
                    </ul>
        </div>
        -->
        <#-- Key decision bar -->
            <#-- <div class="card-header p-1">
                <div class="row">
                    <div class="col-lg-6 col-md-12 col-sm-12">
                        <div class="bd-callout float-left">
                            <small>METRIC 1</small>
                            <h5>--</h5>
                        </div>
                        <div class="bd-callout float-left">
                            <small>METRIC 2 </small>
                            <h5>--</h5>
                        </div>
                        <div class="bd-callout float-left">
                            <small>METRIC 3</small>
                            <h5>--</h5>
                        </div>
                        <div class="bd-callout float-left">
                            <small>METRIC 4</small>
                            <h5>--</h5>
                        </div>
                        <div class="bd-callout float-left">
                            <small>METRIC 5</small>
                            <h5>--</h5>
                        </div>

                    </div>
                    <div class="col-lg-6 col-md-12 col-sm-12">
                        <div class="bd-callout">
                            <small>Last Open Email</small>
                            <h5>25/03/2019</h5>
                        </div>
                        <div class="bd-callout">
                            <small>Last Clicked Email</small>
                            <h5>25/03/2019</h5>
                        </div>
                        <div class="bd-callout">
                            <small>Most Recent Campaign</small>
                            <h5>DBS 03 Aug Campaign</h5>
                        </div>
                        <div class="bd-callout">
                            <small>Customer Since</small>
                            <h5>11/02/2015</h5>
                        </div>
                    </div>
                </div>
    </div> -->

    <#assign addActivities='
			<div class="dropdown-menu" aria-labelledby="E10007">
    		<h4>Add Activities</h4>
	            <a class="dropdown-item" href="/contact-portal/control/createTaskActivity?partyId=${partyId?if_exists}&domainEntityType=${domainEntityType!}&domainEntityId=${domainEntityId!}&externalLoginKey=${requestAttributes.externalLoginKey!}" target="_blank"><i class="fa fa-clipboard" aria-hidden="true"></i> Task</a> 
			    <a class="dropdown-item" href="/contact-portal/control/createPhoneCallActivity?partyId=${partyId?if_exists}&domainEntityType=${domainEntityType!}&domainEntityId=${domainEntityId!}&externalLoginKey=${requestAttributes.externalLoginKey!}" target="_blank"><i class="fa fa-phone" aria-hidden="true"></i> Phone Call</a>
			    <a class="dropdown-item" href="/contact-portal/control/addEmail?partyId=${partyId?if_exists}&domainEntityType=${domainEntityType!}&domainEntityId=${domainEntityId!}&externalLoginKey=${requestAttributes.externalLoginKey!}" target="_blank"><i class="fa fa-envelope" aria-hidden="true"></i> Email</a>
			    <a class="dropdown-item" href="/contact-portal/control/createAppointmentActivity?partyId=${partyId?if_exists}&domainEntityType=${domainEntityType!}&domainEntityId=${domainEntityId!}&externalLoginKey=${requestAttributes.externalLoginKey!}" target="_blank"><i class="fa fa-calendar" aria-hidden="true"></i> Appointment</a>
            </div>
            ' />
    <#-- <a class="dropdown-item" href="/sales-portal/control/addOthers?partyId=${partyId?if_exists}&domainEntityType=${domainEntityType!}&domainEntityId=${domainEntityId!}&externalLoginKey=${requestAttributes.externalLoginKey!}" target="_blank"><i class="fa fa-plus-square" aria-hidden="true"></i> Others</a> -->
        <#assign toggleDropDownData={"E10007":addActivities!} />
        <div class="card-head margin-adj mt-2" id="view-detail">
            <div class="col-lg-12 col-md-12 dot-line">
                <div class="row">
                    <div class="col-lg-6 col-md-6">
                        <h3 class="float-left mr-2 mb-0 header-title view-title">Contact Details</h3>
                    </div>
                    <div class="col-lg-6 col-md-6">
                        <a href="findContact" class="btn btn-xs btn-primary float-right text-right"><i class="fa fa-chevron-circle-left"></i> Back</a>
                    </div>
                </div>
            </div>
            <@AppBar
                appBarId="CON_ACTION_BAR"
                appBarTypeId="ACTION" 
                id="appbar1" extra=extra! 
                toggleDropDownData=toggleDropDownData! 
                isEnableUserPreference=true 
             />
        </div>
        <#assign partyId='${requestParameters.partyId!}'>
        <@inputHidden name="partyId" id="partyId" value="${partyId!}" />
        <div class="card-head margin-adj mt-2">
            <#-- <h3 class="float-left mr-2 mb-0 header-title view-title">Kpi Metrics</h3> -->
                <div class="row	">
                    <div class="col-lg-12 col-md-12">
                    <h3 class="float-left mr-2 mb-0 header-title view-title">KPI Metrics
                         <#if isEnableDashboardButton?has_content && isEnableDashboardButton =="Y"> <a title="view" href="/dashboard-portal/control/viewPartyDashboard?partyId=${partyId!}&requestUri=${request.getRequestURI()!}&externalLoginkey=${requestAttributes.externalLoginKey!}" target="_blank" class="btn btn-primary btn-xs ml-2 float-right"> View Dashboard </a></#if>
                    </h3>
                    </div>
                </div>
                <@AppBar 
                    appBarId="CONTACT_KPI_BAR" 
                    appBarTypeId="KPI" 
                    id="kpi-metrics" 
                    isEnableUserPreference=true 
                 />
        </div>
        <#if isEnableBasicBar?has_content && "Y"==isEnableBasicBar!>
            <#-- Basic information -->
                <div class="basic-info mt-3" id="cp">
                    <#-- <h3 class="float-left mr-2 mb-0 header-title view-title">Basic Information</h3>-->
                        <div class="row	">
                            <div class="col-lg-12 col-md-12">
                                <h3 class="float-left mr-2 mb-0 header-title view-title">Basic Information</h3>
                            </div>
                        </div>
                            <#assign nto='${requestParameters.nto!}'>
                                <@inputHidden name="nto" id="nto" value="${nto!}" />
                                <@dynaScreen 
                                    instanceId="CONT_BASIC_INFO" 
                                    modeOfAction="VIEW" 
                                />
                </div>
        </#if>
        <@navTab 
            instanceId="VIEW_CONTACT" 
            activeTabId="details" 
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
</script>
<script>     
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
   	var link = document.getElementById("E10011");
   	if(typeof(link) != 'undefined' && link != null){
   		link.setAttribute('href', "/contact-portal/control/createPhoneCallActivity?partyId="+partyId+"&domainEntityType=CONTACT&domainEntityId="+partyId);
   	}
	
   		var emaillink = document.getElementById("E10010");
   		if(typeof(emaillink) != 'undefined' && emaillink != null){
	   		emaillink.setAttribute('href', "/contact-portal/control/addEmail?partyId="+partyId+"&domainEntityType=CONTACT&domainEntityId="+partyId+"&nto="+$("#nto").val());
   		}
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
	location.hash = "#contact-notes";
	$('.nav-tabs a[href="#contact-notes"]').tab('show');
	loadTabContent("VIEW_CONTACT", "contact-notes", function() {
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
	location.hash = "#contact-activities";
	$('.nav-tabs a[href="#contact-activities"]').tab('show');
	loadTabContent("VIEW_CONTACT", "contact-activities", function() {
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
	location.hash = "#contact-opportunities";
	$('.nav-tabs a[href="#contact-opportunities"]').tab('show');
	loadTabContent("VIEW_CONTACT", "contact-opportunities", function() {
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
	location.hash = "#contact-sr";
	$('.nav-tabs a[href="#contact-sr"]').tab('show');
	loadTabContent("VIEW_CONTACT", "contact-sr", function() {
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