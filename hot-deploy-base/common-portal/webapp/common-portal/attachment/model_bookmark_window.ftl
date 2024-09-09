<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<#assign partyId = '${requestParameters.partyId!}' >

<#macro createBookmarkModal instanceId path fromAction="">

<div id="${instanceId!}" class="modal fade" >
  <div class="modal-dialog modal-lg">
    <!-- Modal content-->
    <div class="modal-content">
      <div class="modal-header">
        <h4 class="modal-title">Bookmark URL</h4>
        <button type="button" class="close" data-dismiss="modal">&times;</button>
      </div>
      <div class="modal-body" id="bookmark">
                  <!-- <form name="add-bookmark-form" id="add-bookmark-form" action="" enctype="multipart/form-data" method="post"> -->
                      <div class="row p-1">
	                   		<div class="col-md-12 col-lg-12 col-sm-12 ">
	                   		<form name="add-bookmark-form" id="add-bookmark-form" action="" enctype="multipart/form-data" method="post">
	                   		    <input type="hidden" id="path" name="path" value="${path!}">
	                   		    <input type="hidden" id="activeTab" name="activeTab" value="attachments">
	                   		    <input type="hidden" id="partyId" name="partyId" value="${partyId?if_exists}"/>
	                   		    <input type="hidden" id="salesOpportunityId1" name="salesOpportunityId1" value="${salesOppId!}">
	                   		    <input type="hidden" id="custRequestId1" name="custRequestId1" value="${custRequestIdd!}">
	                   		    <input type="hidden" name="externalLoginKey" value="${requestAttributes.externalLoginKey!}"/>
								<input type="hidden" name="domainEntityType" value="${domainEntityType!}">
								<input type="hidden" name="domainEntityId" value="${domainEntityId!}">
					          	<#assign entities = delegator.findByAnd("Enumeration", Static["org.ofbiz.base.util.UtilMisc"].toMap("enumTypeId","CONTENT_CLASS","isEnabled","Y"),  Static["org.ofbiz.base.util.UtilMisc"].toList("sequenceId"), false)>
                          		 <#assign entityList = Static["org.fio.admin.portal.util.DataHelper"].getDropDownOptions(entities, "enumId","description")?if_exists />
	                           <@dropdownCell 
	                              id="classificationEnumId1"
	                              name="classificationEnumId1"
	                              placeholder=uiLabelMap.Classification
	                              options=entityList!
	                              label= "Classification"
	                              value="${requestParameters.classificationEnumId?if_exists}"
	                              allowEmpty=false
	                              /> 
		                      	<@inputArea
						   			id="attachmentDescription1"
						   			label="Description"
						   			rows="1"
						   			placeholder = "Description"
						   			value = ""
						   			required=false
						   			maxlength="255"
						   		/>
		                      
	                    		<div id= "upload">
	                    			<@inputRow id="url" type="text" label="URL" required =true placeholder = "http://www.groupfio.com" />
	                    		</div>
	                    	</form>	
	                    	</div>
						</div>          
                    	<div class="modal-footer">
                    		<@button class="btn btn-sm btn-primary navbar-dark" id="add-bookmark-form-submit" label="${uiLabelMap.Save}"/>
                    		<@button id="bookmark-reset-btn" label="${uiLabelMap.Reset}"	/>
                    	</div>
                  
     </div>
     
    </div>
  </div>
</div>

<script>
$(document).ready(function() {
document.getElementById('url_error').innerHTML = "";
$("#add-bookmark-form").validate({
        rules:{
            url:{
                required:true   
            }
        }
    });
    $("#url").blur(function(){
    	document.getElementById('url_error').innerHTML = "";
       // $("#url").focus();
    });


$('#bookmark-reset-btn').on('click', function (e) {
	$('#classificationEnumId1').dropdown("clear");
  	$("#add-bookmark-form #attachmentDescription1").val("");
  	$('#url').val("");
});

$('#${instanceId!}').on('hidden.bs.modal', function (e) {
  	$('#classificationEnumId1').dropdown("clear");
  	$("#add-bookmark-form #attachmentDescription1").val("");
  	$('#url').val("");
});

});

</script> 

</#macro>


 