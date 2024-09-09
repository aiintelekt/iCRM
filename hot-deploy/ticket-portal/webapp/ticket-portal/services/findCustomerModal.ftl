<script type="text/javascript" src="/ticket-portal-resource/js/ag-grid/services/findCustomer.js"></script>

<div  id="findcustomer" class="modal fade bd-example-modal-lg" tabindex="-1" role="dialog" aria-labelledby="myLargeModalLabel" aria-hidden="true">
	<div class="modal-dialog modal-lg" style="max-width: 1200px;">
		<div class="modal-content">
			<div class="modal-header">
                <h4 class="modal-title">${uiLabelMap.findCustomer!}</h4>
                <button type="button" class="close" data-dismiss="modal" id="close">&times;</button>
            </div>         
            <div class="modal-body" style="padding-bottom: 8px;">
            	<div class="card-header">
            		<form method="post" action="#" id="findCustomerForm" name="findCustomerForm" class="form-horizontal" novalidate="novalidate" data-toggle="validator">
						<div class="row">
							<div class="col-md-4 col-md-4 form-horizontal">
							      <@inputRowAddOn2
			                      	id="cin"
			                        name="cin"
			                        placeholder="CIN/CIF"
			                        required=false
			                        addOnTarget="findCustomerModal"
			                        glyphiconClass="glyphicon-user"
			                        inputColSize="col-sm-12"
			                        />
							      <@inputRowAddOn2
			                      	id="name"
			                       	name="name"
			                       	placeholder="Name"
			                       	required=false
			                      	addOnTarget="findCustomerModal"
			                       	glyphiconClass="glyphicon-user"
			                       	inputColSize="col-sm-12"
			                       />
							      <@inputDate id="dob" name="dob" placeholder="Date of Birth"/>
							      
							      <input type="radio"  name="roleTypeId" value="CUSTOMER" checked /> Customer
			                      <input type="radio"  name="roleTypeId" value="PROSPECT"/> Prospect
			                      <input type="radio"  name="roleTypeId" value="NON_CRM"/> Non CRM
			                      <input type="radio"  name="roleTypeId" value="ALL"/> All
						  </div>
						 
						  <div class="col-md-4 col-md-4 form-horizontal">
                              <@inputRowAddOn2
                              	id="uid"
                              	name="uid"
                              	placeholder="Unique Identifier"
                              	required=false
                              	addOnTarget="findCustomerModal"
                              	glyphiconClass="glyphicon-user"
                              	inputColSize="col-sm-12"
                              />

                              <@inputRowAddOn2
                              	id="cName"
                               	name="cName"
                               	placeholder="Chinese Name"
                               	required=false
                               	addOnTarget="findCustomerModal"
                               	glyphiconClass="glyphicon-user"
                               	inputColSize="col-sm-12"
                               	disabled=true
                               />

                              <@inputRowAddOn2
                              	id="email"
                               	name="email"
                               	placeholder="Email"
                               	required=false
                               	addOnTarget="findCustomerModal"
                               	glyphiconClass="glyphicon-envelope"
                               	inputColSize="col-sm-12"
                               	disabled=true
                               />
						  </div>
						  
						  <div class="col-md-4 col-md-4 form-horizontal text-right">
						      <@inputRowAddOn2
                              	id="account"
                              	name="account"
                              	placeholder="Account"
                              	required=false
                              	addOnTarget="findCustomerModal"
                              	glyphiconClass="glyphicon-list-alt"
                              	inputColSize="col-sm-12"
                              	disabled=true
                              />

                              <@inputRowAddOn2
                              	id="apNo"
                               	name="apNo"
                               	placeholder="AVALOQ Portfolio No"
                               	required=false
                               	addOnTarget="findCustomerModal"
                               	glyphiconClass="glyphicon-briefcase"
                               	inputColSize="col-sm-12"
                               	disabled=true
                               />
							  
                              <@inputRowAddOn2
                              	id="phone"
                               	name="phone"
                               	placeholder="Phone"
                               	required=false
                               	addOnTarget="findCustomerModal"
                               	glyphiconClass="glyphicon-earphone"
                               	inputColSize="col-sm-12"
                               	disabled=true
                              />
                              
	                           <@button
	                           	id="doSearch"
	                            label="${uiLabelMap.Search}"
	                            onclick="loadAgGrid()"
	                           />
	                            
	                           <@reset
	                            id="reset"
	                            label="${uiLabelMap.Reset}"
	                           />
                    	 </div>
					</div>
				</form>
			</div>
      	</div>
	      	<div class="modal-footer" style="border-top: 0px;padding-top: 0px;">
	             <div id="myGrid" style="width: 100%;" class="ag-theme-balham"></div>
	        </div>
    	</div>
  	</div>
</div>
