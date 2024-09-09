<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<#include "component://common-portal/webapp/common-portal/custom-field/modal_window.ftl"/>

<script type="text/javascript" src="/common-portal-resource/js/ag-grid/common/common-utils.js"></script>

<#assign partyId= request.getParameter("partyId")! />

<div class="pt-2">

<div class="col-lg-12 col-md-12 col-sm-12 check-list">
	<h2 class="right-icones">List Economic Metrics</h2>
	
</div>

</div>


<form method="post" id="economic-metric-search-form" class="form-horizontal" novalidate="novalidate" data-toggle="validator">
	
	<input type="hidden" name="domainEntityType" value="${domainEntityType!}">
	<input type="hidden" name="domainEntityId" value="${domainEntityId!}">
	
	<input type="hidden" name="groupType" value="ECONOMIC_METRIC"/>
	
	<input type="hidden" name="partyId" value="${partyId?if_exists}"/>
	<input type="hidden" name="externalLoginKey" value="${requestAttributes.externalLoginKey!}"/>
	
    <div class="row">
    	<#-- 
    	<div class="col-lg-4 col-md-4 col-sm-4">
         <@dropdownCell 
			id="filter_economic_groupingCode"
			name="groupingCode"
			options=economicGroupingCodeList
			required=false
			allowEmpty=true
			tooltip = uiLabelMap.groupingCode
			placeholder = uiLabelMap.groupingCode
			dataLiveSearch=true
			/>
      </div> -->
      <div class="col-lg-4 col-md-4 col-sm-4">
        <@dropdownCell 
			id="filter_economicCodeId"
			name="groupId"
			required=false
			allowEmpty=true
			tooltip = uiLabelMap.economicCode
			placeholder = uiLabelMap.economicCode
			dataLiveSearch=true
			/>
      </div>
      
      <div class="col-lg-4 col-md-4 col-sm-4">
      	<@button
        id="find-economic-metric"
        label="${uiLabelMap.Find}"
        />	
      	
      </div>
	    	  
	</div>	
	
</form>

<script>

jQuery(document).ready(function() {

$(".filter_economicCodeId-input").one( "click",function(){
	CMMUTIL.loadCustomFieldGroup("ECONOMIC_METRIC", "${partyRoleTypeId!}", "filter_economicCodeId", null, "${requestAttributes.externalLoginKey!}");
});

$("#filter_economic_groupingCode").change(function() {
	loadEconomicCodeList("filter_economic_groupingCode", "filter_economicCodeId");
});

});

</script>