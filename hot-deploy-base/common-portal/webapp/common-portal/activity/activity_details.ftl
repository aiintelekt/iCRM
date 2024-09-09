<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
 <#assign workEffortId= request.getParameter("workEffortId")! />
 <#if workEffortId?has_content>
	<#assign CommunicationEventWorkEff = (Static["org.ofbiz.entity.util.EntityQuery"].use(delegator).select("communicationEventId").from("CommunicationEventWorkEff").where("workEffortId",workEffortId).queryFirst())?if_exists />
</#if>
<#if CommunicationEventWorkEff?has_content>
	<#assign communicationEventId = "${CommunicationEventWorkEff.communicationEventId?if_exists}">
</#if>
<#if communicationEventId?has_content>
	<#assign CommunicationEvent = delegator.findOne("CommunicationEvent", {"communicationEventId" : communicationEventId}, true) />
</#if>

 
 <div class="page-header border-b pt-2">
   <@headerH2
    title="Activity Details"
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
	     label="Team Name"
	     id="emplTeam"
	   />
	  	
	  <@displayCell
	     label="Phone Number"
	     id="test6"
	   />
	   
	    <@displayCell
	     label="Once and Done "
	     id="test9"
	   />
   	</div>
   
  </div>  
  
  <#if communicationEventId?has_content>
	  <div class="row">
	  	<div class="col-md-12 col-lg-12 col-sm-12">
	      <@inputArea
	          inputColSize="col-sm-12"
	          id="content"
	          label=uiLabelMap.Content
	          rows="10"
	          placeholder = uiLabelMap.Description
	          value = CommunicationEvent?if_exists.content?if_exists
	        />
	   	</div>
	<#else> 
	  	<div class="row">
			<div class="col-md-12 col-lg-12 col-sm-12">
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
   	</#if>
  </div>
				