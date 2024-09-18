<#include "component://sr-mob-portal/webapp/sr-mob-portal/lib/mobiFormMacros.ftl" />


<#macro makePaymentModal instanceId path fromAction="">
<div id="${instanceId!}" class="modal fade" >
  <div class="modal-dialog modal-lg">
    <!-- Modal content-->
    <div class="modal-content">
      <div class="modal-header">
        <h3 class="modal-title">Make Payment</h4>
        <button type="button" class="close" data-dismiss="modal">&times;</button>
      </div>
      <div class="modal-body" id="makePayment">
                   <form name="make-payment" id="make-payment" action=""  enctype="multipart/form-data" method="post">
	                   		    <input type="hidden" id="partyId" name="partyId" value="${partyId?if_exists}"/>
	                   		    <input type="hidden" name="externalLoginKey" value="${requestAttributes.externalLoginKey!}"/>
								<input type="hidden" name="domainEntityType" value="${domainEntityType!}">
								<input type="hidden" name="domainEntityId" value="${domainEntityId!}">
								<div class="row padding">
									<div class="col-md-4 col-sm-4 ">
					                  <div class="form-group row text-danger">
					                     <label  class="col-sm-12 value-text">Card Details*</label>
					                	</div>
					                  </div>
									<div class="form-group row">
		                     		   <div class="col-sm-7">	 
								 		<input id="ccn" type="tel" inputmode="numeric" pattern="[0-9\s]{13,19}" autocomplete="cc-number" maxlength="19" placeholder="xxxx xxxx xxxx xxxx">
				                   		  </div>
				                   		</div>
				                 	  </div>
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

</script> 

</#macro>


 