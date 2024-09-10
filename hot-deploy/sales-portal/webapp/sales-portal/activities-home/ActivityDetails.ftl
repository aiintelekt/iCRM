<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
 <div class="page-header border-b pt-2">
   <@headerH2
    title="ActivityDetails"
    />
 </div>
<div class="row">
      <div class="col-md-12 col-lg-6 col-sm-12">
   <@displayCell
     label="Type"
     id="workEffortServiceTypeDescription1"
   />
   <@displayCell
     label="Sub Type"
    id="workEffortSubServiceTypeDescription1"
   />
   <@displayCell
     label="Call Date /Time"
    id="estimatedStartDateVal"
   />
   <@displayCell
     label="Duration"
    id="duration"
   />
    <@displayCell
     label="Subject"
     id="test2"
   />
  <@displayCell
     label="Direction"
          id="test5"
   />
    <@displayCell
     label="Call To"
    <#--  -- value="${partyId!}"-->
     id="testT2"
    />
    <@displayCell
     label="Call From"
      id="csrPartyId2"
   />
   <@displayCell
     label="Phone Number"
     id="test6"
   />
   <@displayCell
     label="Instruction / Comments"
     id="test7"
   />
   </div>
   <div class="col-md-12 col-lg-6 col-sm-12">
   <@displayCell
     label="Owner"
     id="test12"
   />
   <@displayCell
     label="Owner BU"
     id="test11"
   />
   <@displayCell
     label="Product Name"
     value="--"
   />
    <@displayCell
     label="Account #"
      id="test8"	
   />
    <@displayCell
     label="Account Product"
     value="--"
   />
   <@displayCell
     label="Regarding"
     id="test15"
   />
  
   <@displayCell
     label="Resolution"
     value="--"
   />
    <@displayCell
     label="Once and Done "
     id="test9"
   />
   </div>
   <div class="table-responsive">
      <@inputArea
          inputColSize="col-sm-12"
          id="description"
          label=uiLabelMap.Description
          maxlength=100
          rows="10"
          placeholder = uiLabelMap.Description
          value = responseObj?if_exists.description?if_exists
        />
   </div>
  </div>
				