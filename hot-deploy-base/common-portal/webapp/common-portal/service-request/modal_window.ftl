<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>

<#assign requestURI = "viewContact"/>
<#if request.getRequestURI().contains("viewLead")>
<#assign requestURI = "viewLead"/>
<#elseif request.getRequestURI().contains("viewAccount")>
<#assign requestURI = "viewAccount"/>
<#elseif request.getRequestURI().contains("viewCustomer")>
<#assign requestURI = "viewCustomer"/>
</#if>

<#macro createServiceRequestModal instanceId fromAction="">

<div id="${instanceId!}" class="modal fade" >
  <div class="modal-dialog modal-xl">
    <!-- Modal content-->
    <div class="modal-content">
      <div class="modal-header">
        <h2 class="modal-title">Create SR</h2>
        
        <button type="button" class="close" data-dismiss="modal">&times;</button>
		        
      </div>
      <div class="modal-body">
        
		<form method="post" action="<@ofbizUrl>createServiceRequest</@ofbizUrl>" data-toggle="validator">
		
		<input type="hidden" name="activeTab" value="serviceRequests" />
        <input type="hidden" name="donePage" value="${requestURI?if_exists}"/>
        <input type="hidden" name="partyId" value="${partyId?if_exists}" />
		
		<@dynaScreen 
			instanceId="CREATE_SR"
			modeOfAction="CREATE"
			isConfigScreen="N"
			/>
			
		<div class="form-group offset-2">
			<div class="text-left ml-3">
		      
		      <@formButton
			     btn1type="submit"
			     btn1label="${uiLabelMap.Save}"
			     btn2=true
			     btn2type="reset"
			     btn2label="${uiLabelMap.Clear}"
			   />
				 	
			</div>
		</div>	
				
		</form>
        
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-sm btn-primary navbar-dark" data-dismiss="modal">Close</button>
      </div>
    </div>
  </div>
</div>

</#macro>