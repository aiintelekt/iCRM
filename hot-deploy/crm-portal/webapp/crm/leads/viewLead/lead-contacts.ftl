<#include "component://lms-mobile/webapp/lms-mobile/lib/mobileMacros.ftl"/> 
<div class="col-form-label p-3" id="Contact-tab" role="tabpanel" aria-labelledby="nav-contact-tab" style="margin-top:-10px">
   <div class="bg-light1 border-0 row">
   		<@pageSectionHeader title="Key Decision Maker(s)" class ="col-12 px-0 border-b mb-2" />
    </div>
  	<div class="col-12 text-right" style="margin-top:-40px;margin-bottom:20px;">
  	<a href="createContactExt?leadId=${leadId}" class="btn btn-xs btn-primary"> Add contact</a> </div>

	<#list keycontacts as contact>
	  <div class="form-group row">
        <div class="col-10 text-dark p-0">
        <#if request.getRequestURI()?contains("crm")>
		<a href="viewContactExt?contactId=${contact.contactId}&leadId=${leadId}" class="text-dark">
		<#else>
		<a href="viewContact?contactId=${contact.contactId}&leadId=${leadId}" class="text-dark">
		</#if>
		<strong>${contact.firstName!}</strong>  <@nullChecked value=contact.lastName/><br/>
		<span class="text-secondary"><#if contact.designation??>${contact.designation}</#if></span><br/> <#if contact.phoneNumber??>${contact.phoneNumber}</#if> <br/><#if contact.email??> ${contact.email}</#if> <span class="text-secondary"><br/><#-- <a href="#">2 other(s)</a> --></span></div>
	  </a>
	 
	  <#if !contact.phoneNumber?has_content || contact.dnd?if_exists == 'Y'> 
	    <div class="col-2 text-secondary text-right"><i class="fa fa-phone display-2 text-secondary" aria-hidden="true"></i></div>
     <#else>
  		<div class="col-2 text-secondary text-right">
  		<i class="fa fa-phone display-2 text-success" aria-hidden="true"></i>
  		<#-- <a href="#" onclick="initiateCall(`${contact}`)" data-toggle="modal" data-target="#myModal">
  		<i class="fa fa-phone display-2 text-success" aria-hidden="true"></i></a> -->
  		</div>
  		
     </#if>
     </div>
   </#list>
  <#if leadContacts?size == 0>
    <div class="form-group row">
		<div class="col-9">
			<div class="mb-1">No contacts for this lead </div>
		</div>	
		
    </div>
  </#if>   
     
      
    
	 <div class="bg-light1 border-0 row">
	 	<@pageSectionHeader title="Associates" class ="col-12 px-0 border-b mb-2" />
    </div>
	<#list associates as contact>
	  <div class="form-group row">
        <div class="col-10 text-dark p-0">
        <#if request.getRequestURI()?contains("crm")>
		<a href="viewContactExt?contactId=${contact.contactId}&leadId=${leadId}" class="text-dark">
		<#else>
		<a href="viewContact?contactId=${contact.contactId}&leadId=${leadId}" class="text-dark">
		</#if>
		<strong>${contact.firstName}</strong>  <@nullChecked value=contact.lasttName/><br/>
		<span class="text-secondary"><#if contact.designation??>${contact.designation}</#if></span><br/> <#if contact.phoneNumber??>${contact.phoneNumber}</#if> <br/><#if contact.email??> ${contact.email}</#if> <span class="text-secondary"><br/><#-- <a href="#">2 other(s)</a> --></span></div>
	  </a>
	  <div class="col-2 text-secondary text-right">
	  <i class="fa fa-phone display-2 text-success" aria-hidden="true"></i>
	  <#-- <a href="#" onclick="initiateCall(`${contact}`)" data-toggle="modal" data-target="#myModal">
	  <i class="fa fa-phone display-2 text-success" aria-hidden="true"></i></a> -->
	  </div>
      </div>
   </#list>
	
   
  </div>
  <script>
  function initiateCall(contact) {
  
  var contactData = contact.replace('{','').replace('}','').split(', ').reduce(function(o,pair) {
   pair = pair.split('=');
   return o[pair[0]] = pair[1], o;
   },{});

   if(contactData.lastName == 'null') {
		contactData.lastName = '';
	}
  
	//$('#callNumber').attr('href','tel:'+ contactData.phoneNumber);
	
	
	$('#attemptContactMechId').val(contactData.teleMechId);
	$('#attemptContactId').val(contactData.contactId);
	
	var callerInfoHtml =  contactData.firstName + ' ' +contactData.lastName + '<br/>' + contactData.phoneNumber;	
	$('#callerInfo').html(callerInfoHtml);
	
	$('#callNumber').click(function(){
		$('#createCallLog').submit();
	});
	

  }
  </script>