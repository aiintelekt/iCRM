<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<#include "component://common-portal/webapp/common-portal/lib/picker_macro.ftl"/>
         <div class="row">
        <div id="main" role="main">
       <form id="createAndUpdateSmtpValues" method="post" action="<@ofbizUrl>createAndUpdateSmtpValues</@ofbizUrl>" data-toggle="validator">   
		        
                <div class="col-lg-12 col-md-12 col-sm-12 dash-panel">
         		<@sectionFrameHeader title="Set SMTP Values"  />
            	<@dynaScreen 
					instanceId="SMTP_CREATE"
					modeOfAction="CREATE"
					/>
					
					<div class="offset-md-2 col-sm-10 pad-10">
                 		<@submit class="btn btn-sm btn-primary navbar-dark" id="saveModal" label="${uiLabelMap.Save}" />
                 	</div>
                 
	            </div>
                 
                 
  			</form>
  			
 		</div>
 		</div>
   
<script>
$(document).ready(function() {
});
$("#mailEngine").change(function() {

	var mailEngine = document.getElementById("mailEngine").value;
	var isEnabled = ''; var relayhost = '';var authUser= '';
	var authPassword=''; var smtpPort= ''; var tlsEnableFlag = '';
	var factoryPort =''; var isRequire ='';
	$.ajax({
		type: "POST",
     	url:'/admin-portal/control/getEmailEngineValues',
        data: {"mailEngine": mailEngine},
        async: false,
        success: function(data) {
        	for (var i = 0; i < data.length; i++) {
                var type = data[i];
              
                if(type.systemPropertyId === "mail.notifications.enabled"){
		        	isEnabled = type.systemPropertyValue;
		        		}
		        	if(type.systemPropertyId === "mail.smtp.relay.host"){
		        	relayhost = type.systemPropertyValue;
		        		}
		        		if(type.systemPropertyId === "mail.smtp.auth.user"){
		        	authUser = type.systemPropertyValue;
		        		}
		        		if(type.systemPropertyId === "mail.smtp.auth.password"){
		        		
		        	authPassword = type.systemPropertyValue;
		        		}
		        		if(type.systemPropertyId === "mail.smtp.port"){
		        	smtpPort = type.systemPropertyValue;
		        		}
		        		if(type.systemPropertyId === "mail.smtp.starttls.enable"){
		        	tlsEnableFlag = type.systemPropertyValue;
		        		}
		        		if(type.systemPropertyId === "mail.smtp.socketFactory.port"){
		        	factoryPort = type.systemPropertyValue;
		        		}
		        		if(type.systemPropertyId === "mail.smtp.auth.require"){
		        	isRequire = type.systemPropertyValue;
		        		}
            	}
      	  }
        
		}); 
		
			document.getElementById("mail.notifications.enabled").value="Y";
			document.getElementById("mail.smtp.relay.host").value=relayhost;
			document.getElementById("mail.smtp.auth.user").value=authUser;
			document.getElementById("mail.smtp.auth.password").value=authPassword;
			document.getElementById("mail.smtp.port").value="25";
			document.getElementById("mail.smtp.starttls.enable").value="true";
			document.getElementById("mail.smtp.socketFactory.port").value="25";
			document.getElementById("mail.smtp.auth.require").value="N";
			//$("#mail.notifications.enabled").val('');
			//$("#mail.smtp.relay.host").val('');
	
	
	});

</script>	



 

	
	


