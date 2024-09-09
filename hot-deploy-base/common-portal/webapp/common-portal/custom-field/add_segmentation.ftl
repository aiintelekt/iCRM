<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<#include "component://common-portal/webapp/common-portal/custom-field/modal_window.ftl"/>

<script type="text/javascript" src="/common-portal-resource/js/ag-grid/common/common-utils.js"></script>

<#assign partyId= request.getParameter("partyId")! />

<div class="pt-2 align-lists">

<div class="col-lg-12 col-md-12 col-sm-12 check-list">
	<h2 class="right-icones">Add Segment</h2>
	
</div>

</div>

<form method="post" id="segmentation-add-form" class="form-horizontal add-seg" novalidate="novalidate" data-toggle="validator">
	
	<input type="hidden" name="domainEntityType" value="${domainEntityType!}">
	<input type="hidden" name="domainEntityId" value="${domainEntityId!}">
	
	<input type="hidden" name="partyId" value="${partyId?if_exists}"/>
	<input type="hidden" name="externalLoginKey" value="${requestAttributes.externalLoginKey!}"/>
	
    <div class="row p-2">
    	<#-- 
    	<div class="col-lg-2 col-md-2 col-sm-2">
         <@dropdownCell 
			id="add_segment_groupingCode"
			name="groupingCode"
			options=segmentGroupingCodeList
			required=false
			allowEmpty=true
			tooltip = uiLabelMap.groupingCode
			placeholder = uiLabelMap.groupingCode
			dataLiveSearch=true
			/>
      </div> -->
      <div class="col-lg-3 col-md-2 col-sm-2">
        <@dropdownCell 
			id="add_segmentCodeId"
			name="groupId"
			required=false
			allowEmpty=true
			tooltip = uiLabelMap.segmentCode
			placeholder = uiLabelMap.segmentCode
			dataLiveSearch=true
			/>
      </div>
      <div class="col-lg-3 col-md-2 col-sm-2">
        <@dropdownCell 
			id="add_segmentValueId"
			name="customFieldId"
			required=false
			allowEmpty=true
			tooltip = uiLabelMap.segmentValue
			placeholder = uiLabelMap.segmentValue
			dataLiveSearch=true
			/>
      </div>
      <div class="col-lg-3 col-md-2 col-sm-2">
      <@inputRow
           id="actualValue"
           name="actualValue"
           placeholder=uiLabelMap.actualValue
           inputColSize="col-sm-12"
           />
      </div>
      <div class="col-lg-3 col-md-2 col-sm-2">
      	<@button
        id="add-segmentation"
        label="${uiLabelMap.Add}"
        />	
      	
      </div>
	    	  
	</div>	
	
</form>

<script>

jQuery(document).ready(function() {

$(".add_segmentCodeId-input").one( "click",function(){
	CMMUTIL.loadCustomFieldGroup("SEGMENTATION", "${partyRoleTypeId!}", "add_segmentCodeId", null, "${requestAttributes.externalLoginKey!}");
});

$("#add_segment_groupingCode").change(function() {
	loadSegmentCodeList("add_segment_groupingCode", "add_segmentCodeId");
});

$("#add_segmentCodeId").change(function() {
	loadSegmentValueList("add_segmentCodeId", "add_segmentValueId");
});

});

</script>