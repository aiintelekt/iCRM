<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<#include "component://common-portal/webapp/common-portal/lib/picker_macro.ftl"/>

<script>
   function onClickMergeReq(fromPartyId, toPartyId){
   
       if(fromPartyId.value == null || fromPartyId.value =="undefined" || fromPartyId.value =="" )
   		{
		  	   	$('#messageModalForEmpParty').modal();
		      			return false;
		      		}
      if(toPartyId.value == null || toPartyId.value =="undefined" || toPartyId.value =="")
		{
		      	$('#messageModalForEmpParty').modal();
		      			return false;
		      		}
      if(fromPartyId.value === toPartyId.value )
		{
		  $('#messageModalForParty').modal();
		    // window.alert("Select different From and To PartyId");
		        return false;
		  }
       $('#submitModal').modal();
   }
</script> 

<!-- confirm dialog box and alert messages in box--> 
<div class="row">
    <div id="main" role="main">
        <div id="submitModal" class="modal fade">
		    <div class="modal-dialog">
		      <div class="modal-content">
            	<div class="modal-header">
	               <h4 class="modal-title">Are you sure you want to merge?</h4>
	               <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
	            	</div>
		            <div>
		            </div>
            		<div class="modal-footer">
		              <input type="submit" class="btn btn-sm btn-primary navbar-dark"  value="Yes" onclick="mainForm.submit();">
		              <input type="button" class="btn btn-sm btn-primary navbar-dark" data-dismiss="modal" value="No" onclick="return false;">
		        </div>
		      </div>
		   </div>
		</div> 
     
         <div id="messageModalForParty" class="modal fade">
		    <div class="modal-dialog">
		      <div class="modal-content">
           		 <div class="modal-header">
	               <h4 class="modal-title">Please select different from and to party Id</h4>
	              	<input type="button" class="btn btn-sm btn-primary navbar-dark" data-dismiss="modal" value="Ok" onclick="return false;">
           		   </div>
		     	 </div>
			   </div>
			</div> 
			
		 <div id="messageModalForEmpParty" class="modal fade">
		    <div class="modal-dialog">
		      <div class="modal-content">
           		 <div class="modal-header">
	               <h4 class="modal-title">From and To Party Id should not be Empty!</h4>
	              	<input type="button" class="btn btn-sm btn-primary navbar-dark" data-dismiss="modal" value="Ok" onclick="return false;">
           		   </div>
		     	 </div>
			   </div>
			</div> 

 <!-- ended -->
 		
   <#-- <#if sectionName ?has_content && sectionName == "lead-portal" >

              <#assign formTarget = "mergeLeadAction"/>
               <#assign extra='<a href="/lead-portal/control/findLead" class="btn btn-xs btn-primary">
             <i class="fa fa-chevron-circle-left" aria-hidden="true"></i> Back</a> ' />
             <@sectionFrameHeader title="Merge Lead" extra=extra />
         	
         	  <#elseif sectionName ?has_content && sectionName == "account-portal" >
               <#assign formTarget = "mergeAccountAction"/>
              <#assign extra='<a href="/account-portal/control/findAccount" class="btn btn-xs btn-primary">
             <i class="fa fa-chevron-circle-left" aria-hidden="true"></i> Back</a> ' />
              <@sectionFrameHeader title="Merge Account" extra=extra />
          	
          	  <#elseif sectionName ?has_content && sectionName == "contact-portal" >
               <#assign formTarget = "mergeContactAction"/>
               <#assign extra='<a href="/contact-portal/control/findContact" class="btn btn-xs btn-primary">
          	 <i class="fa fa-chevron-circle-left" aria-hidden="true"></i> Back</a> ' />              
               <@sectionFrameHeader title="Merge Contact" extra=extra />
           	  
           	  <#elseif sectionName ?has_content && sectionName == "customer-portal" >
               <#assign formTarget = "mergeCustomerAction"/>
               <#assign extra='<a href="/customer-portal/control/findCustomer" class="btn btn-xs btn-primary">
         	  <i class="fa fa-chevron-circle-left" aria-hidden="true"></i> Back</a> ' />
              <@sectionFrameHeader title="Merge Customer" extra=extra />
 	    </#if>   -->

 <!-- merge form for Lead and Account -->
        <#if sectionName ?has_content && sectionName == "lead-portal" || sectionName ?has_content && sectionName == "account-portal" >
              	     	<#if sectionName ?has_content && sectionName == "lead-portal" >
		              <#assign formTarget = "mergeLeadAction"/>
		               <#assign extra='<a href="/lead-portal/control/findLead" class="btn btn-xs btn-primary back-btn">
		             <i class="fa fa-chevron-circle-left" aria-hidden="true"></i> Back</a> ' />
		              <#assign secTitle = "Merge Lead"/>
		             <#elseif sectionName ?has_content && sectionName == "account-portal" >
		               <#assign formTarget = "mergeAccountAction"/>
		              <#assign extra='<a href="/account-portal/control/findAccount" class="btn btn-xs btn-primary back-btn">
		             <i class="fa fa-chevron-circle-left" aria-hidden="true"></i> Back</a> ' />
		             <#assign secTitle = "Merge Account"/>
          </#if>
           <form id="mainForm" formName="mainForm" method="post" action="<@ofbizUrl>${formTarget}</@ofbizUrl>" data-toggle="validator">
    		<input type="hidden" id="partyRoleTypeId" name="partyRoleTypeId" value="${screenName}"/>
      	     <div class="col-lg-12 col-md-12 col-sm-12 dash-panel">
		             <@sectionFrameHeader title=secTitle extra=extra />
		      	    <@dynaScreen 
				      instanceId="MERGE_PARTY"
				      modeOfAction="CREATE"/>
		      
		        <div class="clearfix"></div>
            <div class="offset-md-2 col-sm-10 p-2">
                <input type="button" class="btn btn-sm btn-primary navbar-dark"  onclick="return onClickMergeReq(mainForm.fromPartyId,mainForm.toPartyId);" value="Merge Party"/>
            </div> </div>
           </form>
        	  <@partyPicker 
		         instanceId="partyPicker"/> 
		         
	<!-- ended -->
	
	<!-- merge form for Contact -->	         
		 <#elseif sectionName ?has_content && sectionName == "contact-portal" >         
		   <#if sectionName ?has_content && sectionName == "contact-portal" >
               	<#assign formTarget = "mergeContactAction"/>
               	<#assign extra='<a href="/contact-portal/control/findContact" class="btn btn-xs btn-primary back-btn">
          	 	<i class="fa fa-chevron-circle-left" aria-hidden="true"></i> Back</a> ' />              
               	
           	  	</#if>
		     <form id="mainForm" formName="mainForm" method="post" action="<@ofbizUrl>${formTarget}</@ofbizUrl>" data-toggle="validator">
    		<input type="hidden" id="partyRoleTypeId" name="partyRoleTypeId" value="${screenName}"/>
      	    <div class="col-lg-12 col-md-12 col-sm-12 dash-panel">
      	    <@sectionFrameHeader title="Merge Contact" extra=extra />
      	    	
              
      	      <@dynaScreen 
		      instanceId="MERGE_CONTACT"
		      modeOfAction="CREATE"/>
		      
		        <div class="clearfix"></div>
          <div class="offset-md-2 col-sm-10 p-2">
                <input type="button" class="btn btn-sm btn-primary navbar-dark"  onclick="return onClickMergeReq(mainForm.fromPartyId,mainForm.toPartyId);" value="Merge Party"/>
            </div></div>
        </form>
       <@contactPicker 
		         instanceId="contactPicker"/>    
		         
	<!-- ended for Contact -->
	<!-- merge form for Customer -->	         
		 <#elseif sectionName ?has_content && sectionName == "customer-portal" >  
		 <#if sectionName ?has_content && sectionName == "customer-portal" > 
		 	<#assign extra='<a href="/customer-portal/control/findCustomer" class="btn btn-xs btn-primary back-btn">
         	  	<i class="fa fa-chevron-circle-left" aria-hidden="true"></i> Back</a> ' />
              
              	</#if>
		 <#assign formTarget = "mergeCustomerAction"/>      
		    <form id="mainForm" formName="mainForm" method="post" action="<@ofbizUrl>${formTarget}</@ofbizUrl>" data-toggle="validator">
    		<input type="hidden" id="partyRoleTypeId" name="partyRoleTypeId" value="${screenName}"/>
      	    <div class="col-lg-12 col-md-12 col-sm-12 dash-panel">
      	    	<@sectionFrameHeader title="Merge Customer" extra=extra />
              
      	      <@dynaScreen 
		      instanceId="MERGE_CUSTOMER"
		      modeOfAction="CREATE"/>
		      
		        <div class="clearfix"></div>
          <div class="offset-md-2 col-sm-10 p-2">
                <input type="button" class="btn btn-sm btn-primary navbar-dark"  onclick="return onClickMergeReq(mainForm.fromPartyId,mainForm.toPartyId);" value="Merge Party"/>
            </div>
            </div>
        </form>
       <@customerPicker 
		    instanceId="customerPicker"/>  	  
		  <!-- ended for Customer -->             
		
		</#if>         
    
     
         
     
   </div>
</div>


