<div class="row">
    <div id="main" role="main">
		<#include "component://crm/webapp/crm/common/modalNoteCreate.ftl">
		<#assign leadId = request.getParameter("partyId")!>
		<#assign leadNew = request.getParameter("leadNew")!/>
		<#assign msg = request.getParameter("msg")!/>
		<div class="top-band bg-light">
		   <p class="float-right mr-2 pb-2">		   
		   	  <#if (importAuditCount?exists && importAuditCount > 0 )>
		   	  <a href="#" class="btn btn-xs btn-primary tooltips view-audit-message" data-leadId="${dataImportLeadId!}" data-auditType="VAT_LEAD_IMPORT" data-original-title="${importAuditLogTitle!}"><strong>${importAuditCount!}</strong></a>
		   	  </#if>	
		   	  <#if (dedupAuditCount?exists && dedupAuditCount > 0 )>
		   	  <a href="#" class="btn btn-xs btn-primary tooltips view-dedup-message" data-leadId="${dataImportLeadId!}" data-auditType="VAT_LEAD_DEDUP" data-original-title="${dedupAuditLogTitle!}"><strong>${dedupAuditCount!}</strong></a>	
		   	  </#if>	
		   	  
		      <#if notesList?has_content>
		      <#list notesList as note>
		      <#if note.isImportant?if_exists = 'Y'>
		      <span class="fa fa-sticky-note btn btn-xs btn-danger tooltips" data-toggle="modal" data-target="#noteUpdate" data-original-title="${uiLabelMap.importantNote}"></span>
		      </#if>
		      </#list>
		      </#if>
		      <span class="fa fa-sticky-note btn btn-xs btn-primary tooltips" data-toggle="modal" data-target="#noteCreateUpdate" data-original-title="${uiLabelMap.createNote}"></span>
		      <span class="glyphicon glyphicon-comment btn btn-xs btn-primary tooltips" data-toggle="modal" data-target="#callLogModal" onclick=setActiveTab("details") data-original-title="${uiLabelMap.logCall}"></span>
		      <#--<span class="glyphicon glyphicon-earphone btn btn-xs btn-primary tooltips" data-toggle="modal" data-target="#myModal" data-original-title="${uiLabelMap.writeEmail}"></span>-->
		      <span class="glyphicon glyphicon-envelope btn btn-xs btn-primary tooltips" data-toggle="modal" data-target="#writeEmailModal" data-original-title="${uiLabelMap.writeEmail}"></span>
		   </p>
		   <div class="ml-2">
		      <h1 class="float-left">${uiLabelMap.viewLead} - ${leadName!} (${leadId!})</h1>
		      &nbsp;
		      <#if primaryContactInformation?exists && primaryContactInformation?if_exists.PrimaryPhone?has_content>
		      <a href="#" class="btn btn-xs <#if primaryContactInformation?exists && primaryContactInformation?if_exists.dndStatus?has_content && primaryContactInformation.dndStatus=="Y">btn-danger<#else>btn-success</#if> ">
		      <span class="glyphicon glyphicon-earphone">&nbsp;${primaryContactInformation?if_exists.PrimaryPhone?if_exists}
		      <#if primaryContactInformation?exists && primaryContactInformation?if_exists.dndStatus?has_content && primaryContactInformation.dndStatus=="Y">(DND)</#if></span>
		      </a>
		      </#if>
		      <#if primaryContactInformation?exists && primaryContactInformation?if_exists.EmailAddress?has_content><a href="#" class="btn btn-xs btn-success "><span class="glyphicon glyphicon-envelope">&nbsp;${primaryContactInformation.EmailAddress?if_exists?lower_case}</span></a></#if>
		      <span class="text-dark mt-1 px-2 small"><a href="#" class="bg-light text-warning small-btn px-2"><i class="fa fa-bullseye" aria-hidden="true"></i> ${leadData?if_exists.statusId?if_exists} </a> </span>
		      <span class="text-dark mt-1 small">RM 1 Bank ID : ${assignToPartyId?if_exists}</span> 
		   </div>
		</div>
		<div class="nav-tabs mx-2">
			<ul class="nav nav-tabs">
				<li class="nav-item"><a data-toggle="tab" class="nav-link" href="#overview">Overview</a></li>
			   <li class="nav-item"><a data-toggle="tab" class="nav-link" href="#details">${uiLabelMap.details}</a></li>
			   <li class="nav-item"><a data-toggle="tab" class="nav-link" href="#contactInfo">${uiLabelMap.contactInformation}</a></li>
			   <li class="nav-item"><a data-toggle="tab" class="nav-link" href="#customFields">Attributes</a></li>
			   <li class="nav-item"><a data-toggle="tab" class="nav-link" href="#contacts">${uiLabelMap.Contacts}</a></li>
			   <li class="nav-item"><a data-toggle="tab" class="nav-link" href="#opportunites">${uiLabelMap.opportunities}</a></li>
			   <#--<li class="nav-item"><a data-toggle="tab" class="nav-link" href="#notes">${uiLabelMap.notes}</a></li>
			   <li class="nav-item"><a data-toggle="tab" class="nav-link" href="#logCall">${uiLabelMap.logCall}</a></li>-->
			   <li class="nav-item"><a data-toggle="tab" class="nav-link" href="#campaignDetails">${uiLabelMap.campaignDetails}</a></li>
			   <#--<li class="nav-item"><a data-toggle="tab" class="nav-link" href="#formValues">${uiLabelMap.formValues}</a></li>-->
			   <li class="nav-item"><a data-toggle="tab" class="nav-link" href="#segmentation">${uiLabelMap.segmentation}</a></li>
			   <li class="nav-item"><a data-toggle="tab" href="#economicsMetrics" class="nav-link">Economic Metrics</a></li>
			</ul>
		</div>
		<div class="tab-content mx-2">
		   <div id="overview" class="tab-pane fade">
		      <#include "component://crm/webapp/crm/leads/viewLead/lead-overview.ftl" />
		   </div>
		   <div id="details" class="tab-pane fade">
		   	  <#include "component://crm/webapp/crm/leads/viewLead/lead-details.ftl"/>
		   </div>
		   <div id="contactInfo" class="tab-pane fade">
		      <#include "component://crm/webapp/crm/common/contactInfo.ftl" />
		   </div>
		   <div id="customFields" class="tab-pane fade">
		      <#include "component://crm/webapp/crm/common/customFields.ftl" />
		   </div>
		   <div id="contacts" class="tab-pane fade">
		   		<#include "component://crm/webapp/crm/leads/viewLead/lead-contacts.ftl"/>		      
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
		   <div id="formValues" class="tab-pane fade">
		      <#include "component://crm/webapp/crm/account/viewaccount/formValue.ftl" />
		   </div>
		   <div id="segmentation" class="tab-pane fade">
		       ${screens.render("component://crm/webapp/widget/crm/screens/account/AccountScreens.xml#segmentationDetails")}
		   </div>
		   <div id="economicsMetrics" class="tab-pane fade">
		       ${screens.render("component://crm/webapp/widget/crm/screens/account/AccountScreens.xml#EconomicsMetrics")}
		   </div>
		</div>
		<#include "component://crm/webapp/crm/leads/viewLead/attempt-call.ftl"/>
	</div><#-- End main-->
</div><#-- End row-->
<script>
function viewTab(val){
	$('.nav-tabs a[href="#' + val + '"]').tab('show');	
}
$(document).ready(function() {
	var activeTab = "overview";
	<#if parameters.msg?has_content>
        activeTab = ${"'"+parameters.msg+"'"};
    </#if>
    viewTab(activeTab);
    <#-- <#if activeTab?has_content && activeTab == "overview">
        $('.nav-tabs a[href="#overview"]').tab('show');
    <#elseif activeTab?has_content && activeTab == "details">
        $('.nav-tabs a[href="#details"]').tab('show');
    <#elseif activeTab?has_content && activeTab == "contactInfo">
        $('.nav-tabs a[href="#contactInfo"]').tab('show');
    <#elseif activeTab?has_content && activeTab == "customFields">
        $('.nav-tabs a[href="#customFields"]').tab('show');
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
    <#elseif activeTab?has_content && activeTab == "economicsMetrics">
        $('.nav-tabs a[href="#economicsMetrics"]').tab('show');
    <#else>
        $('.nav-tabs a[href="#overview"]').tab('show');	
    </#if> -->
 
resetCommonEvents();
$('#auditModalDetailView').on('shown.bs.modal', function (e) {
  	findValidationAuditLogs(pkCombinedValueText, validationAuditType);	
});    
   <#if leadNew?exists && "Y"==leadNew>
       $("#AssignedModal").show();
       var leadNew = '${leadNew!}' ;
       if('Y' == leadNew){
          $('#leadAssigned').fadeIn().show().css("display", "block");
          $('#shadowBox1').addClass('modal-backdrop fade show');;
      }
    </#if>
    
    var msg = '${msg!}';
	if(msg=='success'){	
		$('#leadSuccess').fadeIn().show().css("display", "block");
		$('#shadowBox').addClass('modal-backdrop fade show');;
	}
});

function findValidationAuditLogs(pkCombinedValueText, validationAuditType) {
	
   	var url = "searchValidationAuditLogs?pkCombinedValueText="+pkCombinedValueText+"&validationAuditType="+validationAuditType;
   
	$('#auditModalDetailView .error-logs').DataTable( {
	    "processing": true,
	    "serverSide": true,
	    "destroy": true,
	    "searching": false,
	    "ajax": {
            "url": url,
            "type": "POST",
            "async": true
        },
        "pageLength": 15,
        "stateSave": false,
        "order": [[ 4, "desc" ]],
        /*
        "columnDefs": [ 
        	{
				"targets": 14,
				"orderable": false,
				"className": "longtext"
			} 
		],
		*/	      
        "columns": [
			{ "data": "oldValueText" },
			{ "data": "newValueText" },
			{ "data": "changedFieldName" },
			{ "data": "changedByInfo" },
            { "data": "createdStamp" },
            { "data": "comments",
	          "render": function(data, type, row, meta){
	          	data = "";
	            if(type === 'display'){
	            	var comments = row.comments; 
	            	if (comments && comments.length > 300) {
	            		comments = comments.substring(0, 300)+'...';
	            	}
	                data = '<div class="ml-1">'+comments+'</div>';
	            }
	            return data;
	         }
	      	}
            
        ],
        "fnDrawCallback": function(settings, json) {
		    resetDefaultEvents();
		}
	});
	
}

function resetCommonEvents() {
	$('.view-audit-message').unbind( "click" );
	$('.view-audit-message').bind( "click", function( event ) {
	
		event.preventDefault(); 
		$('#auditModalDetailView').modal("show");
		
		pkCombinedValueText = $(this).attr("data-leadId");
		validationAuditType = $(this).attr("data-auditType");
		
		$('#auditModalDetailView .modal-title').html( 'Import ${uiLabelMap.auditMessage} for [ ${leadId!} ]' );
																										
	});
	
	$('.view-dedup-message').unbind( "click" );
	$('.view-dedup-message').bind( "click", function( event ) {
	
		event.preventDefault(); 
		$('#auditModalDetailView').modal("show");
		
		pkCombinedValueText = $(this).attr("data-leadId");
		validationAuditType = $(this).attr("data-auditType");
		
		$('#auditModalDetailView .modal-title').html( '${uiLabelMap.dedupMessage} for [ ${leadId!} ]' );
																										
	});
}

</script>
<#include "component://crm/webapp/crm/leads/lead-assigned.ftl"/>
<#include "component://crm/webapp/crm/common/modalPop.ftl">
<#include "component://crm/webapp/crm/common/writeEmail.ftl" />
<#include "component://crm/webapp/crm/common/findTeamMembersModal.ftl" />
<#include "component://crm/webapp/crm/common/createLogCall.ftl" />
<#include "component://crm/webapp/crm/leads/lead-success.ftl"/>

<@auditLogModal id="auditModalDetailView" />

