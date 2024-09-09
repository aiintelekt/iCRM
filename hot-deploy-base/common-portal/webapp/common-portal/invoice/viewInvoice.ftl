<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<#include "component://common-portal/webapp/common-portal/lib/picker_macro.ftl"/>

    <div class="row">
        <div id="main" role="main">
         <div class="col-lg-12 col-md-12 col-sm-12 dash-panel">
        
         <#if inputContext.statusId?has_content && (inputContext.statusId != "INVOICE_CANCELLED")&& ( inputContext.statusId != "INVOICE_PAID") && ( inputContext.statusId != "INVOICE_VOID")>
           <#assign extra='
			        <a href="/sr-portal/control/viewServiceRequest?srNumber=${inputContext.srNumber!}&externalLoginKey=${requestAttributes.externalLoginKey!}" class="btn btn-xs btn-primary text-right">
			            <i class="fa fa-chevron-circle-left" aria-hidden="true"></i> Back
			        </a>
			        
			        
			        <a href="/common-portal/control/invoice.pdf?invoiceId=${inputContext.invoiceId!}" target="_blank" class="btn btn-xs btn-primary text-right">
			            <i class="fa fa-edit" aria-hidden="true"></i> Download Invoice PDF
			        </a>' />
            	  <#-- <#assign extra='
			        <a href="/common-portal/control/createInvoice?externalLoginKey=${requestAttributes.externalLoginKey!}" class="btn btn-xs btn-primary text-right">
			            <i class="fa fa-chevron-circle-left" aria-hidden="true"></i> Back
			        </a>
			        
			        <a href="/common-portal/control/updateInvoice?invoiceId=${inputContext.invoiceId!}" target="_blank" class="btn btn-xs btn-primary text-right">
			            <i class="fa fa-edit" aria-hidden="true"></i> Update
			        </a>
			         <a href="/common-portal/control/invoiceApplyPayments?invoiceId=${inputContext.invoiceId!}" target="_blank" class="btn btn-xs btn-primary text-right">
			            <i class="fa fa-money-check" aria-hidden="true"></i> Apply payments
			        </a>
			        
			         <a href="/common-portal/control/sendInvoicePdf?invoiceId=${inputContext.invoiceId!}"  class="btn btn-xs btn-primary text-right">
			            <i class="fa fa-edit" aria-hidden="true"></i> Send Invoice PDF
			        </a>
			        <a href="/common-portal/control/invoice.pdf?invoiceId=${inputContext.invoiceId!}" target="_blank" class="btn btn-xs btn-primary text-right">
			            <i class="fa fa-edit" aria-hidden="true"></i> Download Invoice PDF
			        </a>' />-->
			        <#else>
			        <#assign extra='
			        <a href="/sr-portal/control/viewServiceRequest?srNumber=${inputContext.srNumber!}&externalLoginKey=${requestAttributes.externalLoginKey!}" class="btn btn-xs btn-primary text-right">
			            <i class="fa fa-chevron-circle-left" aria-hidden="true"></i> Back
			        </a>
			        
			         <a href="/common-portal/control/sendInvoicePdf?invoiceId=${inputContext.invoiceId!}"  class="btn btn-xs btn-primary text-right">
			            <i class="fa fa-edit" aria-hidden="true"></i> Send Invoice PDF
			        </a>
			        <a href="/common-portal/control/invoice.pdf?invoiceId=${inputContext.invoiceId!}" target="_blank" class="btn btn-xs btn-primary text-right">
			            <i class="fa fa-edit" aria-hidden="true"></i> Download Invoice PDF
			        </a>' />
			        </#if>
            	  <@sectionFrameHeaderTab title="View Invoice" extra=extra/>
	      		<form method="post" id="searchForm" class="form-horizontal" novalidate="novalidate" data-toggle="validator">
							<@dynaScreen 
								instanceId="VIEW_INVOICE"
							    modeOfAction="VIEW"
								      /> 
			              <@inputHidden
			        id="invoiceId"
			        value="${inputContext.invoiceId!}"
			        />  
			        <@inputHidden
			        id="statusId"
			        value="${inputContext.statusId!}"
			        />   
			        <@inputHidden
			        id="externalLoginKey"
			        value="${externalLoginKey!}"
			        />  
        	  </form>      
                 </div>  
		     	
		    	<div class="col-lg-12 col-md-12 col-sm-12 dash-panel">
		    	<#include "component://common-portal/webapp/common-portal/invoice/tab_menu.ftl"/> 
		    		<div class="tab-content">
					
					<div id="a-details" class="tab-pane fade active show">
						${screens.render("component://common-portal/widget/invoice/InvoiceScreens.xml#InvoiceItemsList")}
					</div>
					<div id="a-contactInfo" class="tab-pane fade">
						${screens.render("component://common-portal/widget/invoice/InvoiceScreens.xml#ContactInfo")}	
					</div>
					<div id="a-association" class="tab-pane fade">
					 	${screens.render("component://common-portal/widget/invoice/InvoiceScreens.xml#InvoiceAssociation")}
					</div>
					<div id="a-payment" class="tab-pane fade">
					 <#-- 	${screens.render("component://common-portal/widget/invoice/InvoiceScreens.xml#MakePayment")}-->
					</div>
					<div id="a-appliedPayments" class="tab-pane fade">
						${screens.render("component://common-portal/widget/invoice/InvoiceScreens.xml#AppliedPayments")}
					</div>
					<div id="a-paymentInfo" class="tab-pane fade">
						<#-- ${screens.render("component://common-portal/widget/invoice/InvoiceScreens.xml#PaymentInfo")} -->
					</div>
					<div id="a-communication" class="tab-pane fade">
					<#-- 	${screens.render("component://common-portal/widget/invoice/InvoiceScreens.xml#CommunicationHistory")} -->
					</div>
					<div id="a-status" class="tab-pane fade">
						${screens.render("component://common-portal/widget/invoice/InvoiceScreens.xml#InvoiceStatus")}
					</div>
						
				
		    	
               	
               	 </div>
            </div>
      
<script>     
   $(document).ready(function() {

	<#if !activeTab?has_content>
    	<#assign activeTab = requestParameters.activeTab!>
    </#if>
    
    <#if activeTab?has_content && activeTab == "a-details">
    	$('.nav-tabs a[href="#a-details"]').tab('show');
    <#elseif activeTab?has_content && activeTab == "a-contactInfo">
    	$('.nav-tabs a[href="#a-contactInfo"]').tab('show');	
    <#elseif activeTab?has_content && activeTab == "a-terms">
    	$('.nav-tabs a[href="#a-terms"]').tab('show');
    <#elseif activeTab?has_content && activeTab == "a-appliedPayments">
    	$('.nav-tabs a[href="#a-appliedPayments"]').tab('show');
    <#elseif activeTab?has_content && activeTab == "a-roles">
    	$('.nav-tabs a[href="#a-roles"]').tab('show');
    <#elseif activeTab?has_content && activeTab == "a-sendPerMail">
    	$('.nav-tabs a[href="#a-sendPerMail"]').tab('show');
    <#elseif activeTab?has_content && activeTab == "a-status">
    	$('.nav-tabs a[href="#a-status"]').tab('show');
    <#else>
    
    	$('.nav-tabs a[href="#a-details"]').tab('show');	
    </#if>
    
});

</script>   
 
