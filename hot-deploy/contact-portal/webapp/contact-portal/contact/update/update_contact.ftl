<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<#include "component://common-portal/webapp/common-portal/lib/picker_macro.ftl"/>

<div class="row">
    <div id="main" role="main">
        <#assign extra='<a href="/contact-portal/control/viewContact?partyId=${inputContext.partyId!}" class="btn btn-xs btn-primary back-btn">
        <i class="fa fa-chevron-circle-left" aria-hidden="true"></i> Back</a>' />
        
        <div class="clearfix"></div>
        <form id="mainFrom" method="post" action="<@ofbizUrl>updateContactAction</@ofbizUrl>" data-toggle="validator"> 
        <#assign partyId = "${parameters.partyId?if_exists}"/>
        <@inputHidden id="partyId" value="${parameters.partyId?if_exists}"/>
        <#assign partySummaryDetailsView = (delegator.findOne("PartySummaryDetailsView", {"partyId" : partyId}, false))?if_exists/>     
        <@inputHidden id="postalCode" value="${partySummaryDetailsView.primaryPostalCode?if_exists}"/>
              
            <div class="col-lg-12 col-md-12 col-sm-12">
            <@sectionFrameHeader title="${uiLabelMap.UpdateContact!}" extra=extra />
            
            	<@dynaScreen 
					instanceId="UPDATE_CONT_BASE"
					modeOfAction="UPDATE"
					/>
            	
            </div>
            
             <div id="submitAddressModal" class="modal fade" role="dialog">
		            <div class="modal-dialog modal-md">
				     <div class="modal-dialog">
				      <div class="modal-content">
		            	<div class="modal-header">
		            	<span id="AD_message"></span>
			               <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
			            	</div>
				            <div>
				            </div>
		            		<div class="modal-footer">
				              <input type="submit" class="btn btn-sm btn-primary navbar-dark"  value="Update" onclick="mainForm.submit();">
				              <input type="button" class="btn btn-sm btn-primary navbar-dark" data-dismiss="modal" value="Cancel" onclick="return false;">
					        </div>
					      </div>
					      </div>
					   </div>
					</div> 
					
            <div class="clearfix"></div>
            <div class="offset-md-2 col-sm-10">
            
         	<@submit label="${uiLabelMap.Save}"onclick="return formSubmission();"/>
			<@cancel label="${uiLabelMap.Cancel}" onclick="/contact-portal/control/viewContact?partyId=${inputContext.partyId!}"/>
            <#--  <@formButton
                     btn1type="submit"
                     btn1label="${uiLabelMap.Save}"
                     btn2=false
                     btn2onclick = "resetForm()"
                     btn2type="reset"
                     btn2label="${uiLabelMap.Clear}"
                   />-->
            </div>
        </form>
    </div>
</div>

<@accountPicker 
	instanceId="parentAccountModal"
	/>

<script>
//added for duplicate records check paddress
function formSubmission() {
	var firstName = document.getElementById("firstName").value;
    var lastName = document.getElementById("lastName").value;
	var postalCode = this.mainFrom.postalCode.value;
	var partyId = this.mainFrom.partyId.value;
	var accType = "CONTACT";
	 $.ajax({
        type: "POST",
        url : "/common-portal/control/getDuplicateAddress",
        async: true,
         data: { "firstName": firstName,
         		 "lastName": lastName,
         		 "accType" : accType,
         		 "partyId" : partyId,
                 "postalCode": postalCode,
                 "screenType : "UPDATE"},
        success: function(data) {
        var message = data.Error_Message;
        loadActivity(message);
        }
      }); 
       return false;
    }
 function loadActivity(message){
     if(message === "NO_RECORDS"){
       this.mainFrom.submit();
        }
       else{
        $('#submitAddressModal').modal('show');
        $("#AD_message").html(message);
       }
  }



</script>