<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>

<script type="text/javascript" src="/common-portal-resource/js/ag-grid/common/common-utils.js"></script>
<#assign exportBtn=true>
<#assign isShowHelpUrl="Y">
<#if Static["org.fio.homeapps.util.DataUtil"].isPhoneCampaignEnabled(delegator)?if_exists=="Y">
<#assign exportBtn=false>
<#assign isShowHelpUrl="N">
</#if>
<@sectionFrameHeaderTab title="RFM Metrics" tabId="RfmMetric" isShowHelpUrl=isShowHelpUrl!/> 

<#assign partyId= request.getParameter("partyId")! />
<form method="post" id="find-rfm-metric-form" class="form-horizontal" novalidate="novalidate" data-toggle="validator">
	
	<input type="hidden" name="domainEntityType" value="${domainEntityType!}">
	<input type="hidden" name="domainEntityId" value="${domainEntityId!}">		
	<input type="hidden" name="partyId" value="${partyId?if_exists}"/>
	<input type="hidden" name="roleTypeId" value="${roleTypeId?if_exists}"/>
	<input type="hidden" name="externalLoginKey" value="${requestAttributes.externalLoginKey!}"/>
	
    <div class="row">
    	
      <div class="col-lg-2 col-md-2 col-sm-2">
        <@dropdownCell 
			id="segmentType"
			name="segmentType"
			options=rfmGroupList
			required=false
			allowEmpty=true
			placeholder = "Segment Type"
			dataLiveSearch=true
			/>
      </div>
      <div class="col-lg-2 col-md-2 col-sm-2">
        <@dropdownCell 
			id="subGroup"
			name="subGroup"
			required=false
			allowEmpty=true
			placeholder = "Sub Group"
			dataLiveSearch=true
			/>
      </div>
      
      <div class="col-lg-2 col-md-2 col-sm-2">
      	<@button
        id="find-rfm-metric-btn"
        label="${uiLabelMap.Find}"
        />	
      	
      </div>
	    	  
	</div>	
	</form>
			<#-- <@AgGrid
				gridheaderid="rfm-metric"
				savePrefBtn=true
				clearFilterBtn=true
				exportBtn=exportBtn
				insertBtn=false
				updateBtn=false
				removeBtn=false
			    userid="${userLogin.userLoginId}" 
			    shownotifications="true" 
			    instanceid="RFM_METRIC_LIST" 
			    autosizeallcol="true"
			    debug="false"
			    refreshPrefBtnId="rfm-refresh-pref-btn"
			    savePrefBtnId="rfm-save-pref-btn"
			    clearFilterBtnId="rfm-clear-filter-btn"
			    exportBtnId="rfm-export-btn"
			    />    

			<script type="text/javascript" src="/common-portal-resource/js/ag-grid/custom-field/rfm-metric.js"></script>-->
<input type="hidden" id="userId" value="${userLogin.userLoginId!}"/>
<input type="hidden" id="externalLoginKey" value="${requestAttributes.externalLoginKey!}"/>

		<@fioGrid 
			id="rfm-metric"
			instanceId="RFM_METRIC_LIST"
			jsLoc="/common-portal-resource/js/ag-grid/custom-field/rfm-metric.js"
			headerLabel=""
			headerId="rfm-metrics"
			savePrefBtn=true
			clearFilterBtn=true
			subFltrClearBtn=true
			exportBtn=true
			headerBarClass="grid-header-no-bar"
			subFltrClearId="subFltrClearId-rfm"
			clearFilterBtnId="clearFilterBtnId-rfm"
			savePrefBtnId="rfm-save-pref-btn"
			exportBtnId ="exportBtnId-rfm"
			/>
	
<script>

jQuery(document).ready(function() {
/*
$(".segmentType-input").one( "click",function(){
	CMMUTIL.loadCustomFieldGroup("SEGMENTATION", "${partyRoleTypeId!}", "segmentType", null, "${requestAttributes.externalLoginKey!}");
});
*/

var start = 2014;
var currentTime = new Date()
var yearParam = currentTime.getFullYear()
var end = new Date().getFullYear();

var options = "<option>" + "" + "</option>";
for (var year = end; year >= start; year--) {
    options += "<option value='" + year + "'>" + year + "</option>";
}
document.getElementById("subGroup").innerHTML = options;
var rfmyear = "${rfmYear?if_exists}";
if (rfmyear != null || rfmyear != undefined || rfmyear != "") {
    $("#subGroup").val(rfmyear);
} else {
    $("#subGroup").val(yearParam);
}

});



</script>
                     