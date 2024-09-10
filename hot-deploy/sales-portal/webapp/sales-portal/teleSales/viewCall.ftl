<div id="viewCall" class="modal fade" role="dialog">
   <div class="modal-dialog modal-lg">
      <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title">${uiLabelMap.createlogCall!}</h4>
                <button type="reset" class="close" data-dismiss="modal">&times;</button>
            </div>
            <div class="modal-body">
                <form method="post" action="#" id="viewCall" class="form-horizontal" name="" novalidate="novalidate" data-toggle="validator">
                    <div class="row p-1">
                   <div class="col-md-12 col-lg-12 col-sm-12 ">
                     <@dropdownCell
                    id=""
                  label=uiLabelMap.inboundoutbound
                    required=false
                    allowEmpty=true
                    />
                    <@inputRow id="" label=uiLabelMap.customerparty placeholder="customerparty"/>
                    <@inputRow id="" label=uiLabelMap.rmcsr placeholder="rmcsr" required=true/>
                    <@inputRow id="" label=uiLabelMap.opportunity placeholder="opportunity"/>
                    <@inputRow id="" label=uiLabelMap.serviceRequest placeholder="serviceRequest"/>
                     <@inputRow id="" label=uiLabelMap.subject placeholder="subject"/>
	                              <div class="form-group row">
	                                  <label class="col-sm-4 col-form-label">Message</label>
	                                         <div class="col-sm-7">
	                                               <textarea class="form-control ta-phone-modal" rows="4"></textarea>
	                                         </div>
	                                    </div>
                                    </div>
                                 </div>
                             </div>
                             <div class="modal-footer">
                                  <@submit class="btn btn-sm btn-primary navbar-dark" label="${uiLabelMap.create}"/>
                                </form>
                              </div>
                           </div>
                        </div>
                     </div>