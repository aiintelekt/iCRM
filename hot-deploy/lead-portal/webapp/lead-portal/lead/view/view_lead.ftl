<#include "component://bootstrap/lib/ofbizFormMacros.ftl"/>
<div class="row">
<div id="main" role="main" class="pd-btm-title-bar">
	  
<#-- <@sectionFrameHeader title="Find Leads"/> -->
<#assign extraLeft='
	<a id=task1" title="Task1" href="#" class="btn btn-primary btn-xs" onclick="#"> Task 1</a>
   	<a id="task2" title="Task2" href="#" class="btn btn-primary btn-xs" onclick="#">Task 2</a>
    <a id="task3" title="Task3" href="#" class="btn btn-primary btn-xs" onclick="#">Task 3</a>
    <a id="task4" title="Task4" href="#" class="btn btn-primary btn-xs" onclick="#">Task 4</a>
    <a id="task5" title="Task5" href="#" class="btn btn-primary btn-xs" onclick="#">Task 5</a>
' />

<#-- <@sectionFrameHeader  title="Recently Viewed:"  extraLeft=extraLeft  /> -->
  	
<div class="col-lg-12 col-md-12 col-sm-12">
	
	<#assign addActivities = '
			<div class="dropdown-menu" aria-labelledby="E10007">
    		<h4>Add Activities</h4>
            	<a class="dropdown-item" href="/lead-portal/control/createTaskActivity?partyId=${partyId?if_exists}&domainEntityType=${domainEntityType!}&domainEntityId=${domainEntityId!}&externalLoginKey=${requestAttributes.externalLoginKey!}" target="_blank"><i class="fa fa-clipboard" aria-hidden="true"></i> Task</a> 
			    <a class="dropdown-item" href="/lead-portal/control/createPhoneCallActivity?partyId=${partyId?if_exists}&domainEntityType=${domainEntityType!}&domainEntityId=${domainEntityId!}&externalLoginKey=${requestAttributes.externalLoginKey!}" target="_blank"><i class="fa fa-phone" aria-hidden="true"></i> Phone Call</a>
			    <a class="dropdown-item" href="/lead-portal/control/addEmail?partyId=${partyId?if_exists}&domainEntityType=${domainEntityType!}&domainEntityId=${domainEntityId!}&externalLoginKey=${requestAttributes.externalLoginKey!}" target="_blank"><i class="fa fa-envelope" aria-hidden="true"></i> Email</a>
			    <a class="dropdown-item" href="/lead-portal/control/createAppointmentActivity?partyId=${partyId?if_exists}&domainEntityType=${domainEntityType!}&domainEntityId=${domainEntityId!}&externalLoginKey=${requestAttributes.externalLoginKey!}" target="_blank"><i class="fa fa-calendar" aria-hidden="true"></i> Appointment</a>
            </div>
            ' />
       <#-- <a class="dropdown-item" href="/sales-portal/control/addOthers?partyId=${partyId?if_exists}&domainEntityType=${domainEntityType!}&domainEntityId=${domainEntityId!}&externalLoginKey=${requestAttributes.externalLoginKey!}" target="_blank"><i class="fa fa-plus-square" aria-hidden="true"></i> Others</a> -->
	<#assign toggleDropDownData = {"E10007":addActivities!} />
	   
    <#if leadStatusId == "LEAD_QUALIFIED">
    	<div class="card-head margin-adj mt-2" id="view-detail">
    	
    	<div class="col-lg-12 col-md-12 dot-line">
	    	<div class="row">
		      <div class="col-lg-6 col-md-6">
		            <h3 class="float-left mr-2 mb-0 header-title view-title">Lead Details</h3>
		          </div>
		      <div class="col-lg-6 col-md-6">
		            <a href="findLead" class="btn btn-xs btn-primary float-right text-right"><i class="fa fa-chevron-circle-left"> Back</i></a>
		      </div>
	    	</div>
	    </div>
    	
    	<@AppBar  
            appBarId="ACTION_APP_BAR"
            appBarTypeId="ACTION"
            id="appbar1"
            extra=extra!
            toggleDropDownData=toggleDropDownData!
            isEnableUserPreference=true
            />
    	</div>
    <#else>	
	    <div class="card-head margin-adj mt-2" id="view-detail">
	    
	    <div class="col-lg-12 col-md-12 dot-line">
	    	<div class="row">
		      <div class="col-lg-6 col-md-6">
		            <h3 class="float-left mr-2 mb-0 header-title view-title">Lead Details</h3>
		          </div>
		      <div class="col-lg-6 col-md-6">
		            <a href="findLead" class="btn btn-xs btn-primary float-right text-right"><i class="fa fa-chevron-circle-left"></i> Back</a>
		      </div>
	    	</div>
	    </div>
	    
		<@AppBar  
	        appBarId="LEAD_ACTION_BAR"
	        appBarTypeId="ACTION"
	        id="appbar1"
	        extra=extra!
	        toggleDropDownData=toggleDropDownData!
	        isEnableUserPreference=true
	        />
		</div>
    </#if>  
    <#assign partyId = '${requestParameters.partyId!}' >
    <@inputHidden name="partyId" id="partyId" value = "${partyId!}"/>
	<div class="card-head margin-adj mt-2">
		<h3 class="float-left mr-2 mb-0 header-title view-title">KPI Metrics
			<#if isEnableDashboardButton?has_content && isEnableDashboardButton =="Y">
				<a title="view" href="/dashboard-portal/control/viewPartyDashboard?partyId=${partyId!}&requestUri=${request.getRequestURI()!}&externalLoginkey=${requestAttributes.externalLoginKey!}" target="_blank" class="btn btn-primary btn-xs ml-2 float-right"> View Dashboard </a>
			</#if>
    	</h3>
    	<@AppBar
			appBarId="LEAD_KPI_BAR"
	        appBarTypeId="KPI"
	        id="kpi-metrics"
	        isEnableUserPreference=true
	        />
	</div>
        
		<#if isEnableBasicBar?has_content && "Y" == isEnableBasicBar!>
		<#-- Basic information -->
		<div class="basic-info mt-3" id="cp">
			
			
	        <h3 class="float-left mr-2 mb-0 header-title view-title">Basic Information</h3>
	        
			<@dynaScreen 
				instanceId="LEAD_BASIC_INFO"
				modeOfAction="VIEW"
				/>
			
		</div>
		</#if>
		<#assign tabsToHide = 'opportunities' />
		<#if leadStatusId == "LEAD_QUALIFIED">
			<#assign tabsToHide = '' />
		</#if>
		<#-- <@navTab
			instanceId="VIEW_LEAD"
			activeTabId="lead-details"
			hideTabIds=tabsToHide!
			/>-->
		<#assign tabToLoad = parameters.tabIdToLoad!'lead-details' />
			<@navTab
				instanceId="VIEW_LEAD"
				activeTabId=tabToLoad!''
				hideTabIds=tabsToHide!
				/>

${screens.render("component://common-portal/widget/common/CommonScreens.xml#AddCommonFeatures")}	

<script>
	$(document).ready(function() {
		var status='${inputContext.leadStatus!}';
		if(status === "Disable"){
	 		$("#appbar1").children().attr("disabled",true);
	 		$('#appbar1 a').click(function(){ return false });
	 		$("#statusId").prop("disabled", true);
	 	}		
		var leadStatusId='${context.leadStatusId!}';	
		if(leadStatusId === "LEAD_QUALIFIED"){
	 		$("#E10005").show(); 		
	 		$("#E10006").show();
	 	}else{
	 		$("#E10005").hide(); 		
	 		$("#E10006").hide();
	 	}
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
	   		link.setAttribute('href', "/lead-portal/control/createPhoneCallActivity?partyId="+partyId+"&domainEntityType=LEAD&domainEntityId="+partyId);
		}
	   var emaillink = document.getElementById("E10010");
	   if(typeof(emaillink) != 'undefined' && emaillink != null){
	   		emaillink.setAttribute('href', "/lead-portal/control/addEmail?partyId="+partyId+"&domainEntityType=LEAD&domainEntityId="+partyId);
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
	location.hash = "#lead-notes";
	$('.nav-tabs a[href="#lead-notes"]').tab('show');
	loadTabContent("VIEW_LEAD", "lead-notes", function() {
		$("#note-search-form #isImportant").val("Y");
		$("#lead-notes").click();
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
	location.hash = "#lead-activities";
	$('.nav-tabs a[href="#lead-activities"]').tab('show');
	loadTabContent("VIEW_LEAD", "lead-activities", function() {
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
	location.hash = "#opportunities";
	$('.nav-tabs a[href="#opportunities"]').tab('show');
	loadTabContent("VIEW_LEAD", "opportunities", function() {
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
	location.hash = "#lead-sr";
	$('.nav-tabs a[href="#lead-sr"]').tab('show');
	loadTabContent("VIEW_LEAD", "lead-sr", function() {
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