<#include "component://bootstrap/lib/ofbizFormMacros.ftl"/>

<div class="row">

<div id="main" role="main" class="pd-btm-title-bar">
	
<div class="col-lg-12 col-md-12 col-sm-12 dash-panel">
<@sectionFrameHeader title="${uiLabelMap.FindNonWorkingDays!}" />
<form method="post" id="searchForm" class="form-horizontal" novalidate="novalidate" data-toggle="validator">

<div class="">
	<div class="row">
		<div class="col-lg-4 col-md-6 col-sm-12">
			<@inputDate
            id="nonWorkingDate"
            type="date"
            placeholder="Non-Working Date"
            value="${requestParameters.nonWorkingDate?if_exists}"
            dateFormat="YYYY-MM-DD"
            /> 
		</div>
		<div class="col-lg-4 col-md-6 col-sm-12">
			<#assign statusesList = Static["org.ofbiz.base.util.UtilMisc"].toMap("ACTIVE" , "Active","IN_ACTIVE", "In Active")> 
            <@dropdownCell 
                id="status"
                placeholder="Select Status"
                options=statusesList!
                allowEmpty=true
                value="${requestParameters.status?if_exists}"
                /> 
		</div>
		
		<div class="col-md-2 col-sm-2">
	     	<@button
	        id="main-search-btn"
	        label="${uiLabelMap.Find}"
	        />
	     	<@reset
			label="${uiLabelMap.Reset}"
			/>
     	</div>
		
	</div>
</div>

</form>
	
</div>


<div class="clearfix"></div>
	
<div class="col-lg-12 col-md-12 col-sm-12 dash-panel">

<#assign rightContent='<a title="Create" href="/admin-portal/control/createHolidayConfig" class="btn btn-primary btn-xs ml-2" target="_blank"> <i class="fa fa-plus" aria-hidden="true"></i> Create </a>' />   
<#-- <@AgGrid
    gridheadertitle=uiLabelMap.ListOfNonWorkingDays
    gridheaderid="cal-holiday-grid-action-container"
    insertBtn=false
    updateBtn=false
    removeBtn=false
    headerextra=rightContent
    userid="${userLogin.userLoginId}" 
    shownotifications="true" 
    instanceid="PARAM_CAL_NW_DAY" 
    autosizeallcol="false"
    debug="false"
    />  	    
         
<script type="text/javascript" src="/admin-portal-resource/js/ag-grid/param-unit/calender-setup.js"></script>-->
    <input type="hidden" id="userId" value="${userLogin.userLoginId!}"/>
	<input type="hidden" id="externalLoginKey" value="${requestAttributes.externalLoginKey!}"/>

		<@fioGrid 
			id="ListOfNonWorkingDays-Grid"
			instanceId="PARAM_CAL_NW_DAY"
			jsLoc="/admin-portal-resource/js/ag-grid/param-unit/calender-setup.js"
			headerLabel=uiLabelMap.ListOfNonWorkingDays!
			headerId="cal-holiday-grid-action-container"
			subFltrClearId="cal-holiday-sub-filter-clear-btn"
			savePrefBtnId="cal-holiday-save-pref-btn"
			clearFilterBtnId="cal-holiday-clear-filter-btn"
			headerBarClass="grid-header-no-bar"
			savePrefBtn=false
			clearFilterBtn=false
			exportBtn=true
			subFltrClearBtn=false
			headerExtra=rightContent!
			exportBtnId="cal-holiday-list-export-btn"
			/>
</div>
  	
</div>
</div>

<script>     
$(document).ready(function() {

});
</script>
