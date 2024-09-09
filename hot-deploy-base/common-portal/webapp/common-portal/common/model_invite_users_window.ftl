<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<style>
.primaryContactStyle {
	width: 200px;
}
#inviteUser .ui.search.dropdown .menu{
	max-height:70px;
}
</style>
<#macro inviteUserModal instanceId path fromAction="">
<div id="${instanceId!}" class="modal fade" >
  <div class="modal-dialog modal-lg">
    <!-- Modal content-->
    <div class="modal-content">
      <div class="modal-header">
        <h3 class="modal-title">Invite User</h4>
        <button type="button" class="close" data-dismiss="modal">&times;</button>
      </div>
      <div class="modal-body" id="inviteUser">
                   <form name="invite-user" id="invite-user" action=""  enctype="multipart/form-data" method="post">
	                   		    <input type="hidden" id="activeTab" name="activeTab" value="a-onboarding">
	                   		    <input type="hidden" id="partyId" name="partyId" value="${partyId?if_exists}"/>
	                   		    <input type="hidden" name="externalLoginKey" value="${requestAttributes.externalLoginKey!}"/>
								<input type="hidden" name="domainEntityType" value="${domainEntityType!}">
								<input type="hidden" name="domainEntityId" value="${domainEntityId!}">
								<div class="row padding">
									<div class="col-md-4 col-sm-4 ">
					                  <div class="form-group row text-danger">
					                     <label  class="col-sm-12 value-text">Select User *</label>
					                	</div>
					                  </div>
									<div class="form-group row">
		                     		   <div class="col-sm-7">	 
								 		<select name="primaryContactId" id = "primaryContactId" class="ui dropdown search form-control primaryContactStyle"> 
					                      <#list inputContext.primaryContactsList as partyContactAssoc>
					                        <option value="${(partyContactAssoc.partyId)!}">${(partyContactAssoc.name)!} <#if partyContactAssoc.isPrimary?has_content && partyContactAssoc.isPrimary == "Y" >(P)<#else></#if></option>
					                      </#list>
					                    </select>
				                   		  </div>
				                   		</div>
				                 	  </div><br>
                    	<div class="modal-footer">
                    	<#--<input type="submit" class="btn btn-sm btn-primary navbar-dark"   value="Invite"/>-->
                    		<@button class="btn btn-sm btn-primary navbar-dark" id="invite-user-form-submit" label="Invite"/>
                    		<@reset label="${uiLabelMap.Clear}"	/>
                    	</div>
                  </form>
    	 </div>
    </div>
  </div>
</div>

<script>
$(document).ready(function() {
$('#invite-user-form-submit').on('click', function (e) {

			var partyId= $("#inviteUser #partyId").val();
			var primaryContactId= $("#inviteUser #primaryContactId").val();
			$.ajax({
            type: "POST",
            url: "createUserLoginForContact",
            data: { 
		            "domainEntityType":$("#domainEntityType").val(),
		            "partyId": partyId,
		            "primaryContactId":primaryContactId,
            },
            sync: true,
            success: function(data) {
                $('#${instanceId!}').modal('hide');
                 var message = data.errMsg;
              	  showAlert ("success", message);
              	  location.reload();
             	   getInviteUserRowData(); 
					
            }

        });
		e.preventDefault();
	});
});
function disableLogin(invitePartyId) {
	$.ajax({
		type: "POST",
     	url: "/account-portal/control/disableLoginForContact",
        data: {"invitePartyId": invitePartyId,"partyId": "${partyId!}", "domainEntityType": "${domainEntityType!}", "domainEntityId": "${domainEntityId!}", "externalLoginKey": "${requestAttributes.externalLoginKey!}"},
        async: true,
        success: function (data) {  
         var message = data.Error_Message; 
           	 $.notify({
                       message : '<p>Successfully Disabled Login</p>',
                  });
                    location.reload();
           
        },
        error: function(data) {
        var message = data.Error_Message; 
                   showAlert("error", message);
                   location.reload();
               }
        
	}); 
}

function enableLogin(invitePartyId) {
	$.ajax({
		type: "POST",
     	url: "/account-portal/control/enableLoginForContact",
        data: {"invitePartyId": invitePartyId,"partyId": "${partyId!}", "domainEntityType": "${domainEntityType!}", "domainEntityId": "${domainEntityId!}", "externalLoginKey": "${requestAttributes.externalLoginKey!}"},
        async: true,
        success: function (data) {  
         var message = data.Error_Message; 
          $.notify({
                  message : '<p>Successfully Enabled Login</p>',
                  });
         location.reload();
           
        },
        error: function(data) {
        var message = data.Error_Message; 
                   showAlert("error", message);
                   
               }
        
	}); 
}
function resetPasswordForContact(invitePartyId) {
	$.ajax({
		type: "POST",
     	url: "/account-portal/control/resetPasswordForContact",
        data: {"invitePartyId": invitePartyId,"partyId": "${partyId!}", "domainEntityType": "${domainEntityType!}", "domainEntityId": "${domainEntityId!}", "externalLoginKey": "${requestAttributes.externalLoginKey!}"},
        async: true,
        success: function (data) {  
         var message = data.Error_Message; 
           $.notify({
	                	message : '<p>'+message+'</p>'
	              	});
    				setTimeout(location.reload.bind(location),1000);
        },
        error: function(data) {
        var message = data.Error_Message; 
                   showAlert("error", message);
               }
        
	}); 
}

</script> 

</#macro>


 