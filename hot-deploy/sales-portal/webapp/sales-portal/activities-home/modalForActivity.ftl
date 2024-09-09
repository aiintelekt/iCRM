<!-- create non crm customer -->
<div id="createNonCrmCustomer" class="modal fade" role="dialog">
	<div class="modal-dialog modal-lg">
    	<!-- Modal content-->
    	<div class="modal-content">
        	<div class="modal-header">
                <h4 class="modal-title">${uiLabelMap.createNonCrmCustomer!}</h4>
                <button type="reset" class="close" data-dismiss="modal">&times;</button>
            </div>
            <div class="modal-body">
            	<form method="post" action="#" id="createNonCrmCustomer" class="form-horizontal" name="createNonCrmCustomer" novalidate="novalidate" data-toggle="validator">
                	<div class="row p-2">
                    	<div class="col-md-12 col-lg-6 col-sm-12 ">
                        	<@inputRow 
                        		id="" 
                        		label=uiLabelMap.firstName 
                        		placeholder="Enter First Name" 
                        		required=true
                        	/>
                      		<@inputRow 
                      			id="" 
                      			label=uiLabelMap.uniqueIdentifier 
                      			placeholder="Enter Unique Identifier"
                      		/>

                     		<@dropdownCell 
                     			id="srTypeIdM"
		                  		label=uiLabelMap.emailType
		                  		placeholder="Please Select"
		                    	required=false
		                    	allowEmpty=true
                    		/>

    						<@dropdownCell
                   				id="srTypeIdM"
                  				label=uiLabelMap.phoneType
                  				placeholder="Please Select"
                    			required=false
                    			allowEmpty=true
                   			 />                 
                		</div>
                 		
                 		<div class="col-md-12 col-lg-6 col-sm-12 ">
                        	<@inputRow 
                        		id="" 
                        		label=uiLabelMap.lastName 
                        		placeholder="Enter Last Name" 
                        		required=true
                        	/>

                     		<@inputRow 
                     			id="" 
                     			label=uiLabelMap.chineseName 
                     			placeholder="Enter Chinese Name" 
                     			required=false
                     		/>

                     		<@inputRow 
                     			id="" 
                     			label=uiLabelMap.email 
                     			placeholder="Enter Email"
                     		/>

                        	<@inputRow 
                        		id="" 
                        		label=uiLabelMap.phone 
                        		placeholder="Enter Phone"
                        	/>
                		</div>
         			</div>
         			<div class="modal-footer" >
	               		<@submit class="btn btn-sm btn-primary navbar-dark" label="${uiLabelMap.save}"/>
	               		<button type="sbmit" class="btn btn-sm btn-primary navbar-dark" data-dismiss="modal">Close</button>
	            	</div>
      			</form>
   			</div>
   		</div> 
  	</div> 		
</div>