<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<#-- <#include "component://ticket-portal/webapp/ticket-portal/services/viewActModal.ftl"/> -->
<#if srNumber?has_content>
<#assign srNumberUrlParam = srNumber!>
<#else>
<#assign srNumberUrlParam = requestParameters.srNumber!>
</#if>

<#assign requestFrom = requestParameters.requestFrom! />
<#assign readOnlyPermission = Static["org.fio.homeapps.util.DataUtil"].hasPermissionWoFullPerm(request, "READ_ONLY_PERM")?if_exists />


<style>
html {
      overflow-y: overlay !important;
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
    margin-top: 112px!important;
    max-width: 99.4%;
}
.sticky-bar {
    position: fixed;
    margin-top: -98px !important;
    max-width: 100%;
    margin-right: 38px;
    width: -webkit-fill-available; /* For WebKit-based browsers */
    width: -moz-available; /* For Mozilla-based browsers */
    z-index: 999;
}
.nav-tabs { 
	max-width: 99.4%; 
}
.tab-content {
	max-width: 99.4%; 
}
</style>
<div class="row">
    <div id="main" role="main">
        <input type="hidden" name="custRequestId" id="custRequestId" value="${srNumberUrlParam!}" />
        <div class="top-band bg-light" style="display:none">
            ${screens.render("component://ticket-portal/widget/services/ServicesScreens.xml#RecentlyViewed")}
        </div>
        <div class="clearfix"></div>
        <div class="col-lg-12 col-md-12 col-sm-12 mid">
            <input type="hidden" name="partyId" id ="partyId" value="${inputContext.cNo?if_exists}"/>
            <input type="hidden" name="partyType" id ="partyType" value="${inputContext.partyType?if_exists}"/>
            <input type="hidden" name="cNo_link" id ="cNo_link" value="${inputContext.partyType?if_exists}"/>
            <#assign srStatus = inputContext.srStatusId ?if_exists/>
            <#if inputContext.srStatusId ?if_exists != "SR_CLOSED" || inputContext.srStatusId ?if_exists != "SR_CANCELLED">
            <#assign addActivities = '
            <div class="dropdown-menu" aria-labelledby="10183" display:none>
                <h4>Add Activities</h4>
                <a class="dropdown-item" href="/ticket-portal/control/createTaskActivity?partyId=${partyId?if_exists}&domainEntityType=${domainEntityType!}&domainEntityId=${domainEntityId!}&custRequestId=${srNumberUrlParam!}&externalLoginKey=${requestAttributes.externalLoginKey!}" target="_blank"><i class="fa fa-clipboard" aria-hidden="true"></i> Task/Schedule</a> 
                <a class="dropdown-item" href="/ticket-portal/control/createPhoneCallActivity?partyId=${partyId?if_exists}&domainEntityType=${domainEntityType!}&domainEntityId=${domainEntityId!}&custRequestId=${srNumberUrlParam!}&externalLoginKey=${requestAttributes.externalLoginKey!}" target="_blank"><i class="fa fa-phone" aria-hidden="true"></i> Phone Call</a>
                <a class="dropdown-item" href="/ticket-portal/control/createEmailActivity?partyId=${partyId?if_exists}&domainEntityType=${domainEntityType!}&domainEntityId=${domainEntityId!}&custRequestId=${srNumberUrlParam!}&externalLoginKey=${requestAttributes.externalLoginKey!}" target="_blank"><i class="fa fa-envelope" aria-hidden="true"></i> Email</a>
            </div>
            ' />
            <#-- <a class="dropdown-item" href="/ticket-portal/control/createAppointmentActivity?partyId=${partyId?if_exists}&domainEntityType=${domainEntityType!}&domainEntityId=${domainEntityId!}&custRequestId=${srNumberUrlParam!}&externalLoginKey=${requestAttributes.externalLoginKey!}" target="_blank"><i class="fa fa-calendar" aria-hidden="true"></i> Appointment</a> -->
            <#assign toggleDropDownData = {"10183":addActivities!} />
            </#if>
            <#assign addActivities = '
            <div class="dropdown-menu" aria-labelledby="10183">
                <h4>Add Activities</h4>
                <a class="dropdown-item" href="/ticket-portal/control/createTaskActivity?partyId=${partyId?if_exists}&domainEntityType=${domainEntityType!}&domainEntityId=${domainEntityId!}&custRequestId=${srNumberUrlParam!}&externalLoginKey=${requestAttributes.externalLoginKey!}" target="_blank"><i class="fa fa-clipboard" aria-hidden="true"></i> Task/Schedule</a> 
                <a class="dropdown-item" href="/ticket-portal/control/createPhoneCallActivity?partyId=${partyId?if_exists}&domainEntityType=${domainEntityType!}&domainEntityId=${domainEntityId!}&custRequestId=${srNumberUrlParam!}&externalLoginKey=${requestAttributes.externalLoginKey!}" target="_blank"><i class="fa fa-phone" aria-hidden="true"></i> Phone Call</a>
                <a class="dropdown-item" href="/ticket-portal/control/createEmailActivity?partyId=${partyId?if_exists}&domainEntityType=${domainEntityType!}&domainEntityId=${domainEntityId!}&custRequestId=${srNumberUrlParam!}&externalLoginKey=${requestAttributes.externalLoginKey!}" target="_blank"><i class="fa fa-envelope" aria-hidden="true"></i> Email</a>
            </div>
            ' />
            <#-- <a class="dropdown-item" href="/ticket-portal/control/createAppointmentActivity?partyId=${partyId?if_exists}&domainEntityType=${domainEntityType!}&domainEntityId=${domainEntityId!}&custRequestId=${srNumberUrlParam!}&externalLoginKey=${requestAttributes.externalLoginKey!}" target="_blank"><i class="fa fa-calendar" aria-hidden="true"></i> Appointment</a> -->
            <div class="card-head margin-adj mt-0  sticky-bar" id="view-detail">
                <div class="col-lg-12 col-md-12 dot-line">
                    <div class="row">
                        <div class="col-lg-6 col-md-6">
                            <h3 class="float-left mr-2 mb-0 header-title">View Service Request</h3>                           
            				<span class="sticky-panel">${srNumberUrlParam!}&nbsp;<#if relatedOldSrId?has_content>(${relatedOldSrId!})</#if></span>
                        </div>
                        <div class="col-lg-6 col-md-6" style="padding-bottom: 6px">
                        	
                            <a href="main" class="btn btn-xs btn-primary float-right text-right">Back</a>
                        </div>
                    </div>
                </div>
                <@AppBar  
                appBarId="SR_ACTION_BAR_G"
                appBarTypeId="ACTION"
                id="appbar1"
                extra=extra!
                toggleDropDownData=toggleDropDownData!
                isEnableUserPreference=true
                />
            </div>
            <div class="card-head margin-adj mt-2 sticky-bar1">
                <#-- <h3 class="float-left mr-2 mb-0 header-title view-title">KPI Metrics</h3> -->
                <div class="row	">
                    <div class="col-lg-12 col-md-12">
                     <h3 class="float-left mr-2 mb-0 header-title view-title">KPI Metrics
                      <#if isEnableDashboardButton?has_content && isEnableDashboardButton =="Y"> <a title="view" href="/dashboard-portal/control/viewPartyDashboard?partyId=${inputContext.cNo?if_exists}&requestUri=${request.getRequestURI()!}&externalLoginkey=${requestAttributes.externalLoginKey!}" target="_blank" class="btn btn-primary btn-xs ml-2 float-right"> View Dashboard </a></#if>
                     </h3>
                    </div>
                </div>
                <@AppBar
                appBarId="SR_KPI_BAR_G"
                appBarTypeId="KPI"
                id="kpi-metrics"
                isEnableUserPreference=true
                />
            </div>
            <#include "component://ticket-portal/webapp/ticket-portal/services/sr_view_tab_menu.ftl"/>
            <#-- <@navTab
                instanceId="VIEW_SER_REQUEST"
                activeTabId="sr-details"
                />-->
            <#assign tabToLoad = parameters.tabIdToLoad!'sr-details' />
				<@navTab
					instanceId="VIEW_SER_REQUEST"
					activeTabId=tabToLoad!''
					/>
        </div>
    </div>
</div>

<script>
$(document).ready(function() {
	<#if readOnlyPermission!>
    	$(".addActivity").hide();
    	$('.name').attr("href","#");
    	$('.name').attr("target","");
    	$('.name').addClass("view-link");
    	
    	$('.primaryEmail').attr("href","#");
    	$('.primaryEmail').attr("target","");
    	$('.primaryEmail').addClass("view-link");
    	
    	$('.primaryPhone').attr("href","#");
    	$('.primaryPhone').attr("target","");
    	$('.primaryPhone').addClass("view-link");
    	
    	$("a.view-link").click(function () {
            $("#accessDenied").modal("show");
            return false;
        });
    </#if>
    
	var status='${inputContext.srStatusId!}';
	if(status === "SR_CLOSED" || status === "SR_CANCELLED" ){
	 	var partyId  = $("#partyId").val();
		$("#appbar1").children().attr("disabled",true);
		//$('#appbar1 a').click(function(){ return false });
		$("#appbar1 a:not('#10180')").click(function () {
			return false;
		});
		$("#statusId").prop("disabled", true);
		//$("#appbar1").children().find('10180').attr("disabled",false);	
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
	
	if($("#note-search-form #isImportant").length)
	    $("#note-search-form #isImportant").val("");
	
	if($('#openchk').length) {
		$('#openchk').prop('checked', true);
		$("#activity-search-form #open").val($('#openchk').val());
		$("#act-search-btn").click();
	}
	
});

	$('.ab-im-notes').click(function(){
    	location.hash = "#sr-notes";
    	$("#note-search-form #isImportant").val("Y");
    	
    	$('.nav-tabs a[href="#sr-notes"]').tab('show');
    	$("#sr-notes").click();
    	$("#note-search-btn").click();
    	
    	var target = $('#nav-tab-focus');
	    if (target.length) {
	        $('html,body').animate({
	            scrollTop: target.offset().top
	        }, 1000);
	        return false;
	    }
	    
    });  
    
    $('.ab-activity').click(function(){
    	location.hash = "#sr-activities";
    	$('#openchk').prop('checked', true);
    	var openStatus = $('input[name="openchk"]:checked').val();
    	$("#activity-search-form #open").val(openStatus);
    	
    	$('.nav-tabs a[href="#sr-activities"]').tab('show');
    	$("#act-search-btn").click();
    	
    	var target = $('#nav-tab-focus');
	    if (target.length) {
	        $('html,body').animate({
	            scrollTop: target.offset().top
	        }, 1000);
	        return false;
	    }
	    
    });
    
</script>
          
          