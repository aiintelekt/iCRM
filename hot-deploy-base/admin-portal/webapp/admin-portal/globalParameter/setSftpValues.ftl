<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<#include "component://common-portal/webapp/common-portal/lib/picker_macro.ftl"/>

         <div class="row">
        <div id="main" role="main">
       <form id="createAndUpdateSmtpValues" method="post" action="<@ofbizUrl>createAndUpdateSftpValues</@ofbizUrl>" data-toggle="validator">   
		        
                <div class="col-lg-12 col-md-12 col-sm-12 dash-panel">
         		<@sectionFrameHeader title="Set SFTP Values" />
            	<@dynaScreen 
					instanceId="SFTP_CREATE"
					modeOfAction="CREATE"
					/>
					
					<div class="offset-md-2 col-sm-10 pad-10">
                 <@formButton
                     btn1type="submit"
                     btn1label="${uiLabelMap.Save}"
                     btn1onclick="return formSubmission();"
                   />
                 	</div>
                 	
	            </div>
                         
  			</form>
  			
 		</div>
 		</div>
   
<script>
$(document).ready(function(){
	$('#password').attr('type', 'password');
});
$('#port').keyup(function(e){
	validatePortValues();
});

$('#user').keyup(function(e){
	validateUsers();
});

function validatePortValues(){
	var isInvalid = false;
	if($('#port').val() != ""){
		var re = new RegExp("^([0-9]{4})$");
		if (re.test($('#port').val())) {
	  		$('#port_error').html('');
	  	}else{
	  		$('#port_error').html('Please enter valid port number');
	  		isInvalid = true;
	  	}
  	}
  	else
  		$('#port_error').html('');
  	return isInvalid;
}

function validateUsers(){
	var isInvalid = false;
	if($('#user').val() != ""){
		var re = new RegExp(/^[A-Za-z]+$/);
		if (re.test($('#user').val())) {
	  		$('#user_error').html('');
	  	}else{
	  		$('#user_error').html('Please enter valid user');
	  		isInvalid = true;
	  	}
  	}
  	else
  		$('#user_error').html('');
  	return isInvalid;
}
function formSubmission(){

var valid = true;
  		
		if(validateUsers() || validatePortValues()){
	    	valid = false;
		}
		if (!valid) {
  			return false;
  		}

}
 

	
	
</script>

