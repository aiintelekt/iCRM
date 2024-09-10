<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<#include "component://common-portal/webapp/common-portal/custom-field/modal_window.ftl"/>

<script type="text/javascript" src="/common-portal-resource/js/ag-grid/common/common-utils.js"></script>

<#assign partyId= request.getParameter("partyId")! />

<div class="pt-2 align-lists">

<div class="col-lg-12 col-md-12 col-sm-12 check-list">
	<h2 class="right-icones">List Segment Values</h2>
	
</div>

</div>

<form method="post" id="segmentation-search-form" class="form-horizontal list-seg" novalidate="novalidate" data-toggle="validator">
	
	<input type="hidden" name="domainEntityType" value="${domainEntityType!}">
	<input type="hidden" name="domainEntityId" value="${domainEntityId!}">
	
	<input type="hidden" name="groupType" value="SEGMENTATION"/>
	<input type="hidden" name="filterGroupCode" id="filterGroupCode" value="Y"/>
	<input type="hidden" name="partyId" value="${partyId?if_exists}"/>
	<input type="hidden" name="externalLoginKey" value="${requestAttributes.externalLoginKey!}"/>
	
    <div class="row p-2">
    	<#-- 
    	<div class="col-lg-4 col-md-4 col-sm-4">
         <@dropdownCell 
			id="filter_segment_groupingCode"
			name="groupingCode"
			options=segmentGroupingCodeList
			required=false
			allowEmpty=true
			tooltip = uiLabelMap.groupingCode
			placeholder = uiLabelMap.groupingCode
			dataLiveSearch=true
			/>
      </div> -->
      <div class="col-lg-3 col-md-4 col-sm-4">
        <@dropdownCell 
			id="filter_segmentCodeId"
			name="groupId"
			required=false
			allowEmpty=true
			tooltip = uiLabelMap.segmentCode
			placeholder = uiLabelMap.segmentCode
			dataLiveSearch=true
			/>
      </div>
      
      <div class="col-lg-3 col-md-4 col-sm-4">
      	<@button
        id="find-segmentation"
        label="${uiLabelMap.Find}"
        />	
      	
      </div>
	    	  
	</div>	
	
</form>

<script>

jQuery(document).ready(function() {

$(".filter_segmentCodeId-input").one( "click",function(){
	CMMUTIL.loadCustomFieldGroup("SEGMENTATION", "${partyRoleTypeId!}", "filter_segmentCodeId", null, "${requestAttributes.externalLoginKey!}");
});

$("#filter_segment_groupingCode").change(function() {
	loadSegmentCodeList("filter_segment_groupingCode", "filter_segmentCodeId");
});

});

</script>