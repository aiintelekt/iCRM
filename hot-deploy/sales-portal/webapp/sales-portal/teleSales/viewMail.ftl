<script>     
function sendMail() {
var sendFrom=$("#sendForm").val();
var sendTo=$("#sendTo").val();
	if(sendFrom !=""&&sendTo!="")
	    $("#sendMailForm").submit();
    else
	    return false;
}
</script>    
<div id="viewMail" class="modal fade" role="dialog">
   <div class="modal-dialog modal-lg">
      <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title">${uiLabelMap.viewMail!}</h4>
                <button type="reset" class="close" data-dismiss="modal">&times;</button>
            </div>
            <div class="modal-body">
                <form method="post" action="sendSrEmail" id="sendMailForm" class="form-horizontal" name="sendMailForm" >
                    <#assign srNumberUrlParam = requestParameters.srNumber!>

					<input type="hidden" name="srNumber" id="srNumber" value="${srNumberUrlParam!}" /> 
                    <div class="row p-1">
                   <div class="col-md-12 col-lg-12 col-sm-12 ">
                     <@dropdownCell
                    id="sendFrom"
                  	label=uiLabelMap.from
                    required=true
                    allowEmpty=true
                    />
                    <@inputRow 
                    id="sendTo" 
                    label=uiLabelMap.to
                    placeholder="to"
                    required=true/>
                    <@inputRow 
                    id="sendCC" 
                    label=uiLabelMap.cc 
                    placeholder="cc" />
                    <@inputRow 
                    id="sendBCC" 
                    label=uiLabelMap.bcc 
                    placeholder="bcc"/>
                    <@inputRow 
                    id="sendSr" 
                    label=uiLabelMap.serviceRequest 
                    placeholder="Service Request"/>
	               <#assign templates = delegator.findByAnd("TemplateMaster", {"templateType" : "EMAIL_BLAST"}, null, false)>
                            <#assign templatesList = Static["org.fio.admin.portal.util.DataHelper"].getDropDownOptions(templates, "templateId","templateName")?if_exists />

                            <@dropdownCell
                                id="template"
                                 label=uiLabelMap.template
                                placeholder="Please Select"
                                required=false
                                allowEmpty=true
                                options=templatesList!
                                />

                     <@inputRow 
                     id="subject" 
                     label=uiLabelMap.subject 
                     placeholder="subject" />
                     <div class="form-group row">
	                     <label class="col-sm-4 col-form-label float-labels">${uiLabelMap.attachments}</label>
	                            <div class="col-sm-5">
	                                <div class="custom-file">
	                                    <input type="file" class="custom-file-input" id="inputGroupFile01" aria-describedby="inputGroupFileAddon01">
	                                          <label class="custom-file-label rounded-0" for="inputGroupFile01">Choose file</label>
	                                  </div>
	                                 </div>
	                                 <div class="col-sm-2">
	                                    <a href="" class="btn btn-primary btn-sm"> <i class="fa fa-plus fa-1"></i> Add</a>
	                                  </div>
	                              </div>
	                              <div class="form-group row">
	                                  <label class="col-sm-4 col-form-label">${uiLabelMap.message}</label>
	                                         <div class="col-sm-7">
	                                               <textarea  id="message" class="form-control ta-phone-modal" rows="4"></textarea>
	                                         </div>
	                                    </div>
                                    </div>
                                 </div>
                             <div class="modal-footer">
                                  
                              </div>
                              <@button 
                                label="${uiLabelMap.send}"
                                onclick="javascript: return sendMail();"
                                />
                                   <@cancel class="btn btn-sm btn-secondary navbar-dark" label="${uiLabelMap.cancel}"/>
                              </form>
                              </div>
                           </div>
                        </div>
                     </div>
                     
                     
                     
                      