<#if request.getRequestURI().contains("writeEmail")>
  <div class="page-header border-b">
    <h1 class="float-left">Write Email</h1>
  </div>
<#else>
  <div id="writeEmailModal" class="modal fade" role="dialog">
  <div class="modal-dialog modal-lg">
  <!-- Modal content-->
  <div class="modal-content">
  <div class="modal-header">
    <h4 class="modal-title">Write Email</h4>
    <button type="reset" class="close" data-dismiss="modal">&times;</button>
  </div>
  <div class="modal-body">
  <div class="">
</#if>
<!-- Content -->
<script type="text/javascript">
    var attachmentCount = 1 ;
    function addAttachment() {
          var divId = 'attachment_' + attachmentCount ;
          var html = '<div class="form-group row" id="' + divId + '"><label for="inputEmail3" class="col-sm-4 col-form-label">Attachments</label>';
          html = html+'<div class="col-sm-7">';
          html = html+'<span class="btn btn-primary btn-sm fileinput-button">';
          html = html+'<i class="glyphicon glyphicon-plus"></i>';
          html = html+'<span> Select files...</span>';
          html = html+'<input id="fileupload" type="file" name="uploadedFile_' + attachmentCount + '" >';
          html = html+'</span>';
          html = html+'<a onClick=removeAttachment("attachment_' + attachmentCount + '")>';
          html = html+'<span class="glyphicon glyphicon-remove btn btn-xs btn-danger ml-2"></span>';
          html = html+'</a></div>';
          jQuery('#addAttachmentButtonRemove').append(html);
          attachmentCount++ ;
    }
    function removeAttachment( toRemove ) {
        jQuery("#"+toRemove).remove();
    }
    function getTemplate() {
      var toEmailEl = document.getElementById('toEmail');
      var toEmail = toEmailEl ? toEmailEl.value : null;
      var templateEl = document.getElementById('templateId');
      var templateId = templateEl ? templateEl.options[templateEl.selectedIndex].value : null;
      if (! templateId) return false;
  
      var context = {"templateId" : templateId };
      if (toEmail) context['toEmail'] = toEmail;
  
      jQuery.ajax({
        url: 'getTemplateMasterForEmailJSON',
        async: true,
        type: 'POST',
        data: context,
        success: function(data) {
        $('#content').summernote('code', data.textContent);
        }
      });
    }
</script>
<form method="post" action="removeEmailAttachment" name="removeEmailAttachmentForm" >
  <input type="hidden" name="workEffortId" value=""/>
  <input type="hidden" name="fromDate" value=""/>
  <input type="hidden" name="contentId" value=""/>
  <input type="hidden" name="dataResourceId" value=""/>
  <input type="hidden" name="communicationEventId" value=""/>
</form>
  
<form method="post" action="sendActivityEmail" id="writeEmailForm" class="form-horizontal" name="writeEmailForm" novalidate="novalidate" data-toggle="validator" enctype="multipart/form-data">
  <#assign requestURI = "viewContact"/> 
  <#if request.getRequestURI().contains("viewLead")>
    <#assign requestURI = "viewLead"/>
  <#elseif request.getRequestURI().contains("viewAccount")>
    <#assign requestURI = "viewAccount"/>
  <#elseif request.getRequestURI().contains("viewCustomer")>
    <#assign requestURI = "viewCustomer"/>
  </#if>
  <input type="hidden" name="action" value="${parameters.action?if_exists}"/>
  <input type="hidden" name="contactMechIdTo" value="${parameters.contactMechIdTo?if_exists}"/>
  <input type="hidden" name="orderId" value="${parameters.orderId?if_exists}"/>
  <input type="hidden" name="contentMimeTypeId" value="${parameters.contentMimeTypeId?default("text/html")}"/>
  <input type="hidden" name="partyId" value="${partyId?if_exists}"/>
  <input type="hidden" name="donePage" value="${requestURI?if_exists}"/>
  <#-- <input type="hidden" name="datetimeStarted" value=getLocalizedDate(Static["org.ofbiz.base.util.UtilDateTime"].nowTimestamp())/>-->
  <input type="hidden" name="communicationEventId" value="${parameters.communicationEventId?if_exists}"/>
  <input type="hidden" name="workEffortId" value="${parameters.workEffortId?if_exists}"/>
  <#if parameters.contactListId?exists>
  <input type="hidden" name="contactListId" value="${parameters.contactListId?if_exists}"/>
  </#if>
  <#-- for switching HTML/TEXT and keeping the "from" mail selection -->
  <input type="hidden" name="fromEmail" value="${parameters.fromEmail?if_exists}"/>
  <#-- original CommunicationEvent, used to relate emails when replying or forwarding -->
  <input type="hidden" name="origCommEventId" value="${parameters.origCommEventId?if_exists}"/>
  <div class="row padding-r">
    <div class="col-md-6 col-sm-6 ">
      <div class="form-group row has-error">
        <label  class="col-sm-4 col-form-label">From*</label>
        <div class="col-sm-7">
          <select name="contactMechIdFrom" id="contactMechIdFrom" class="ui dropdown search form-control input-sm" >
            <#if userEmailAddresses?exists && userEmailAddresses?has_content>
              <#list userEmailAddresses as email>
              	<#if email.infoString?has_content>
                <option value="${email.contactMechId}" 
                	<#if contactMech?exists> <#if email?if_exists.infoString?contains(contactMech?if_exists.infoString?if_exists)> selected </#if></#if>
                >
                ${email.infoString!}
                </option>
                </#if>
              </#list>
            </#if>
          </select>
        </div>
      </div>
      <div class="form-group row">
        <label for="inputEmail3" class="col-sm-4 col-form-label">CC</label>
        <div class="col-sm-7">
          <div class="input-group">
            <input type="text" class="form-control input-sm" placeholder="CC" name="ccEmail" id="ccEmail" value="<#if ccAddresses?exists>${ccAddresses}<#else>${parameters?if_exists.ccEmail?if_exists}</#if>">
            <#--<span class="input-group-addon">
            <span class="glyphicon glyphicon-list-alt
              " data-toggle="modal" data-target="#myModal">
            </span>
            </span>-->
          </div>
        </div>
      </div>
      <#--<div class="form-group row">
        <label for="inputEmail3" class="col-sm-4 col-form-label">Ticket</label>
        <div class="col-sm-7">
          <div class="input-group">
            <input type="text" class="form-control input-sm" placeholder="Ticket" name="custRequestId" id="custRequestId" value="${parameters?if_exists.custRequestId?if_exists}" onblur="getCaseSubject(this);">
            <span class="input-group-addon">
            <span class="glyphicon glyphicon-list-alt
              " data-toggle="modal" data-target="#myModal">
            </span>
            </span>
          </div>
        </div>
      </div>-->
      
      <div class="form-group row">
        <label  class="col-sm-4 col-form-label">Template</label>
        <div class="col-sm-7">
          <select class="ui dropdown search form-control input-sm" name="templateId" id="templateId" onchange="getTemplate()">
            <option value=""></option>
            <#if templates?exists && templates?has_content>
            <#list templates as template>
            <option value="${template.templateId}" <#if template.templateId == parameters.templateId?default("")>selected="selected"</#if>>${template.templateName?if_exists}</option>
            </#list>
            </#if>
          </select>
        </div>
      </div>
      
      <div class="form-group row has-error">
        <label  class="col-sm-4 col-form-label">Subject*</label>
        <div class="col-sm-7">
          <input type="text" class="form-control input-sm" id="subject" name="subject" placeholder="Subject" value="${parameters.subject?if_exists}" required>
          <div class="help-block with-errors"></div>
        </div>
      </div>
    </div>
    <div class="col-md-6 col-sm-6">
      <div class="form-group row has-error">
        <label for="inputEmail3" class="col-sm-4 col-form-label">To*</label>
        <div class="col-sm-7">
          <div class="input-group">
            <input type="text" class="form-control input-sm" placeholder="To" name="toEmail" id="toEmail" value="<#if toAddresses?exists>${toAddresses}<#elseif parameters?if_exists.toEmail?exists>${parameters.toEmail?if_exists}<#else>${emailTo?if_exists}</#if>" required>
            <#--<span class="input-group-addon">
            <span class="glyphicon glyphicon-list-alt
              " data-toggle="modal" data-target="#myModal">
            </span>
            </span>-->
          </div>
        </div>
      </div>
      <div class="form-group row">
        <label for="inputEmail3" class="col-sm-4 col-form-label">BCC</label>
        <div class="col-sm-7">
          <div class="input-group">
            <input type="text" class="form-control input-sm" placeholder="BCC" name="bccEmail" id="bccEmail" value="<#if bccAddresses?exists>${bccAddresses}<#else>${parameters?if_exists.bccEmail?if_exists}</#if>">
            <#--<span class="input-group-addon">
            <span class="glyphicon glyphicon-list-alt
              " data-toggle="modal" data-target="#myModal">
            </span>
            </span>-->
          </div>
        </div>
      </div>
      <div class="form-group row" id="attachment_0">
        <label for="inputEmail3" class="col-sm-4 col-form-label">Attachments</label>
        <div class="col-sm-7">
          <span class="btn btn-primary btn-sm fileinput-button">
            <i class="glyphicon glyphicon-plus"></i>
            <span>Select files...</span>
            <!-- The file input field used as target for the file upload widget -->
            <input id="fileupload" type="file" name="uploadedFile_0" >
          </span>
          <a onClick="removeAttachment('attachment_0')">
            <span class="glyphicon glyphicon-remove btn btn-xs btn-danger"></span>
          </a>
        </div>
      </div>
      
      <div id="addAttachmentButtonRemove"></div>
      <div class="form-group row" id="addAttachmentButton">
      <label for="inputEmail3" class="col-sm-4 col-form-label"></label>
        <div class="col-sm-7">
          
          <a onClick="addAttachment()"><span class="btn btn-primary btn-sm">
           <i class="glyphicon glyphicon-plus">Add</i></span>
          </a>
          
         </div>
       </div>
     </div>
  </div>
  <div class=" has-error">
    <label  class="col-form-label col-sm-2">Message*</label>
    <div class="col-sm-12">
      <textarea id="content" name="content"></textarea>
      <script>
        $('#content').summernote({
          tabsize: 2,
          height: 100
        });
      </script>
    </div>
  </div>
  <div class="col-md-12 col-sm-12">
    <div class="form-group row">
      <div class="offset-sm-0 col-sm-9">
        <input type="submit" class="btn btn-sm btn-primary navbar-dark mt" value="Submit"/>
        <#--<button type="submit" class="btn btn-sm btn-primary navbar-dark mt" data-dismiss="modal">Clear</button>-->
      </div>
    </div>
  </div>
</form>
<#if request.getRequestURI().contains("writeEmail")>

<#else>
  </div>
  </div>
  <div class="modal-footer">
    <button type="sbmit" class="btn btn-sm btn-primary navbar-dark" data-dismiss="modal">Close</button>
  </div>
  </div>
  </div>
  </div>
</#if>

    <!-- /.container -->
    <div id="myModal" class="modal fade" role="dialog">
		<div class="modal-dialog"> 
        <!-- Modal content-->
        <div class="modal-content">
          <div class="modal-header">
		  <h4 class="modal-title">Modal Header</h4>
            <button type="reset" class="close" data-dismiss="modal">&times;</button>
            
          </div>
          <div class="modal-body">
            <p>Some text in the modal.</p>
          </div>
          <div class="modal-footer">
            <button type="reset" class="btn btn-sm btn-primary navbar-dark" data-dismiss="modal">Close</button>
          </div>
        </div>
      </div>
    </div>