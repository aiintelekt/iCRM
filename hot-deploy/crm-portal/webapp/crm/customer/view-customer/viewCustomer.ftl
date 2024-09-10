<#include "component://crm/webapp/crm/common/modalNoteCreate.ftl">
<#include "component://crm/webapp/crm/common/writeEmail.ftl" />
<#include "component://crm/webapp/crm/common/findTeamMembersModal.ftl" />
<#include "component://crm/webapp/crm/common/createLogCall.ftl" />
<div class="row">
	<div id="main" role="main">
        <div class="top-band bg-light">
          <p class="float-right mr-2 pb-2">
          	<#if notesList?has_content>
              <#list notesList as note>
                  <#if note.isImportant?if_exists = 'Y'>
                      <span class="fa fa-sticky-note-o btn btn-xs btn-danger tooltips" data-toggle="modal" data-target="#noteUpdate" data-original-title="${uiLabelMap.importantNote}"></span>
                  </#if>
              </#list>
           	</#if>
            <#-- <span class="fa fa-sticky-note btn btn-xs btn-primary tooltips" data-toggle="modal" data-target="#noteCreateUpdate" data-original-title="${uiLabelMap.createNote}"></span> -->
            <#--<span class="glyphicon glyphicon-comment btn btn-xs btn-primary" data-toggle="modal" data-target="#myModal"></span>-->
            <span class="glyphicon glyphicon-earphone btn btn-xs btn-primary tooltips" data-toggle="modal" data-target="#callLogModal" onclick=setActiveTab("details") data-original-title="${uiLabelMap.logCall}"></span>
            <span class="glyphicon glyphicon-envelope btn btn-xs btn-primary tooltips" data-toggle="modal" data-target="#writeEmailModal" data-original-title="${uiLabelMap.writeEmail}"></span>
          </p>
   		  <div class="ml-2"> 
               <#if partyPrimaryPhone?exists && partyPrimaryPhone?has_content><a href="#" class="btn btn-xs btn-success"><span class="glyphicon glyphicon-earphone"> ${partyPrimaryPhone?if_exists}</span></a></#if>
               <#if partyEmailAddress?exists && partyEmailAddress?has_content><a href="#" class="btn btn-xs btn-success"> <span class="glyphicon glyphicon-envelope"></span> ${partyEmailAddress?if_exists}</a></#if>
               <h1 class="float-left">View Customer -  ${partySummary.firstName?if_exists} ${partySummary.lastName?if_exists} </h1>&nbsp;
           </div>
        </div>
        <div class="col-md-12 col-lg-12 col-sm-12 ">
        <div class="nav-tabs">
		    <ul class="nav nav-tabs">
		      <li class="nav-item"><a data-toggle="tab" class="nav-link" href="#details">Details</a></li>
		      <li class="nav-item"><a data-toggle="tab" class="nav-link" href="#contactInfo">Contact Information</a></li>
		      <li class="nav-item"><a data-toggle="tab" class="nav-link" href="#opportunites">Opportunities</a></li>
		      <li class="nav-item"><a data-toggle="tab" class="nav-link" href="#campaignDetails">Campaign Details</a></li>
		      <li class="nav-item"><a data-toggle="tab" class="nav-link" href="#customFields">Attributes</a></li>
		      <li class="nav-item"><a data-toggle="tab" class="nav-link" href="#segmentation">Segmentation</a></li>
		      <li class="nav-item"><a data-toggle="tab" class="nav-link" href="#lead">Lead</a></li>
		      <#--<li class="nav-item"><a data-toggle="tab" class="nav-link" href="#formValues">Form Values</a></li>-->
		    </ul>
	    </div>
	    <div class="tab-content">
	    <div id="details" class="tab-pane fade">
	      <#include "component://crm/webapp/crm/customer/view-customer/viewCustomerDetails.ftl" />
	    </div>
	    <div id="contactInfo" class="tab-pane fade">
	      <#include "component://crm/webapp/crm/common/contactInfo.ftl" />
	    </div>
	    <div id="opportunites" class="tab-pane fade">
	      <#include "component://crm/webapp/crm/common/opportunites.ftl" />
	    </div>
	    <div id="notes" class="tab-pane fade">
	      ${screens.render("component://crm/webapp/widget/crm/screens/account/AccountScreens.xml#note")}
	    </div>
	    <div id="logCall" class="tab-pane fade">
	      ${screens.render("component://crm/webapp/widget/crm/screens/common/CommonScreens.xml#callDetails")}
	    </div>
	    <div id="campaignDetails" class="tab-pane fade">
	      <#include "component://crm/webapp/crm/common/campaignDetails.ftl" />
	    </div>
	    <div id="customFields" class="tab-pane fade">
	      <#include "component://crm/webapp/crm/common/customFields.ftl" />
	    </div>
	    <div id="segmentation" class="tab-pane fade">
	    ${screens.render("component://crm/webapp/widget/crm/screens/account/AccountScreens.xml#segmentationDetails")}
	    </div>
	    <div id="lead" class="tab-pane fade">
	      ${screens.render("component://crm/webapp/widget/crm/screens/contacts/ContactScreens.xml#viewContactLead")}
	   </div>
	    <div id="formValues" class="tab-pane fade">
	      <#include "component://crm/webapp/crm/account/viewaccount/formValue.ftl" />
	    </div>
        <#include "component://crm/webapp/crm/common/modalPop.ftl">
        <#include "component://crm/webapp/crm/common/writeEmail.ftl" />
        <#include "component://crm/webapp/crm/common/findTeamMembersModal.ftl" />
        <#include "component://crm/webapp/crm/common/createLogCall.ftl" />
    </div>
    </div>
   </div>
</div> 
    <script>
       $(document).ready(function() {
       <#if !activeTab?has_content>
          <#assign activeTab = requestParameters.activeTab!>
       </#if>
    
       <#if activeTab?has_content && activeTab == "details">
          $('.nav-tabs a[href="#details"]').tab('show');
       <#elseif activeTab?has_content && activeTab == "contactInfo">
          $('.nav-tabs a[href="#contactInfo"]').tab('show');
       <#elseif activeTab?has_content && activeTab == "customFields">
          $('.nav-tabs a[href="#customFields"]').tab('show');
       <#elseif activeTab?has_content && activeTab == "account">
          $('.nav-tabs a[href="#account"]').tab('show');
       <#elseif activeTab?has_content && activeTab == "opportunites">
          $('.nav-tabs a[href="#opportunites"]').tab('show');
       <#elseif activeTab?has_content && activeTab == "notes">
          $('.nav-tabs a[href="#notes"]').tab('show');
       <#elseif activeTab?has_content && activeTab == "logCall">
          $('.nav-tabs a[href="#logCall"]').tab('show');
       <#elseif activeTab?has_content && activeTab == "campaignDetails">
          $('.nav-tabs a[href="#campaignDetails"]').tab('show');
       <#elseif activeTab?has_content && activeTab == "segmentation">
          $('.nav-tabs a[href="#segmentation"]').tab('show');
       <#elseif activeTab?has_content && activeTab == "lead">
          $('.nav-tabs a[href="#lead"]').tab('show');
       <#else>
          $('.nav-tabs a[href="#details"]').tab('show');	
       </#if>
    
});
</script>

  </div>

       