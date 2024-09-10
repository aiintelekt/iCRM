<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<#include "component://common-portal/webapp/common-portal/custom-field/modal_window.ftl"/>

<script type="text/javascript" src="/common-portal-resource/js/ag-grid/common/common-utils.js"></script>

<#assign partyId= request.getParameter("partyId")! />

<div class="pt-2 align-lists">

<div class="col-lg-12 col-md-12 col-sm-12 check-list">
	<h2 class="right-icones">Add Economic Metric</h2>
	
</div>

</div>

<form method="post" id="economic-metric-add-form" class="form-horizontal " novalidate="novalidate" data-toggle="validator">
	
	<input type="hidden" name="domainEntityType" value="${domainEntityType!}">
	<input type="hidden" name="domainEntityId" value="${domainEntityId!}">
	
	<input type="hidden" name="partyId" value="${partyId?if_exists}"/>
	<input type="hidden" name="externalLoginKey" value="${requestAttributes.externalLoginKey!}"/>
	
    <div class="row p-2">
    	<#-- 
    	<div class="col-lg-2 col-md-2 col-sm-2">
         <@dropdownCell 
			id="add_economicmetric_groupingCode"
			name="groupingCode"
			options=economicGroupingCodeList
			required=false
			allowEmpty=true
			tooltip = uiLabelMap.groupingCode
			placeholder = uiLabelMap.groupingCode
			dataLiveSearch=true
			/>
      </div> -->
      <div class="col-lg-3 col-md-2 col-sm-2">
        <@dropdownCell 
			id="add_economicCodeId"
			name="groupId"
			required=false
			allowEmpty=true
			tooltip = uiLabelMap.economicCode
			placeholder = uiLabelMap.economicCode
			dataLiveSearch=true
			/>
      </div>
      <div class="col-lg-2 col-md-2 col-sm-2">
        <@dropdownCell 
			id="add_economicMetricId"
			name="customFieldId"
			required=false
			allowEmpty=true
			tooltip = uiLabelMap.economicMetric
			placeholder = uiLabelMap.economicMetric
			dataLiveSearch=true
			/>
      </div>
      <div class="col-lg-2 col-md-2 col-sm-2">
      <@inputRow
           id="propertyName"
           name="propertyName"
           placeholder=uiLabelMap.propertyName
           inputColSize="col-sm-12"
           />
      </div>
      <div class="col-lg-2 col-md-2 col-sm-2">
      <@inputRow
           id="propertyValue"
           name="propertyValue"
           placeholder=uiLabelMap.propertyValue
           inputColSize="col-sm-12"
           />
      </div>
      <div class="col-lg-2 col-md-2 col-sm-2">
      	<@button
        id="add-economicmetric"
        label="${uiLabelMap.Add}"
        />	
      	
      </div>
	    	  
	</div>	
	
</form>

<script>

jQuery(document).ready(function() {

$(".add_economicCodeId-input").one( "click",function(){
	CMMUTIL.loadCustomFieldGroup("ECONOMIC_METRIC", "${partyRoleTypeId!}", "add_economicCodeId", null, "${requestAttributes.externalLoginKey!}");
});

$("#add_economicmetric_groupingCode").change(function() {
	loadEconomicCodeList("add_economicmetric_groupingCode", "add_economicCodeId");
});

$("#add_economicCodeId").change(function() {
	loadEconomicMetricList("add_economicCodeId", "add_economicMetricId");
});

});

</script>