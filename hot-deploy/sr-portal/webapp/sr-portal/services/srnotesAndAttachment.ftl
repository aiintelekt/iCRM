<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<div class="page-header border-b pt-2"> 
	<#assign extraLeft='
    	<a href="" class="text-dark" data-toggle="modal" data-target="#addNotes"><i class="fa fa-plus fa-1 right-icones ml-2" aria-hidden="true" style="font-size: 18px;"></i></a>'
     	extra='  <a href="#" class="text-dark"><i class="fa fa-refresh fa-1" aria-hidden="true"></i></a>  <small>2019/03/26 01:10:14</small>'/>
       	<@sectionFrameHeader  title="Notes and Attachments" extraLeft=extraLeft   />                             
</div>
                                                     
<div class="table-responsive">
	<div id="NotesGrid" style="height:380px; width: 100%;" class="ag-theme-balham"></div>
    	<script type="text/javascript" src="/sr-portal-resource/js/ag-grid/services/srnotes-attach.js"></script>
    </div>
    <div id="addNotes" class="modal fade" role="dialog">
   		<div class="modal-dialog modal-lg">
    		<div class="modal-content">
            	<div class="modal-header">
                	<h4 class="modal-title">Add Notes / Attachments</h4>
                	<button type="reset" class="close" data-dismiss="modal">&times;</button>
            	</div>
            	<div class="modal-body">
            		<form method="post" action="createSrNotesAndAttachment?srNumber=${requestParameters.srNumber?if_exists}" id="createSrNotesAndAttachment" name="createSrNotesAndAttachment" data-toggle="validator" enctype="multipart/form-data">
                		<div class="row p-1">
	                   		<div class="col-md-12 col-lg-12 col-sm-12 ">
		                    	<@inputRow id="noteName" label="Note Title" placeholder="Note Title" required=true/>
		                    	<@inputHidden name="srNumber" id="srNumber" value="${requestParameters.srNumber?if_exists}"/>
		                     	<#assign srNumber = '${requestParameters.srNumber?if_exists}'>
		                    	<#assign custRequestSrSummaryDetails = (Static["org.ofbiz.entity.util.EntityQuery"].use(delegator).select("custRequestId","cinNumber","customerName").from("CustRequestSrSummary").where("custRequestId",srNumber).queryOne())?if_exists />
	                    		<#assign customerId = "">
	                    		<#if custRequestSrSummaryDetails?has_content>
	                    			<#assign cinNumber = "${custRequestSrSummaryDetails.cinNumber?if_exists}"> 
	                    			<#assign customerName = "${custRequestSrSummaryDetails.customerName?if_exists}"> 
	                    			
	                    			<#assign partyList = Static["org.ofbiz.entity.util.EntityQuery"].use(delegator).select("partyId","idValue").from("PartyIdentification").where("partyIdentificationTypeId","CIN","idValue",cinNumber).queryList()?if_exists />    
	                    			<#list partyList as eachParty>
	            						<#assign customerId = "${eachParty.partyId?if_exists}"> 
	            					</#list>
	                    		</#if>
		                    	<@inputHidden name="customerId" id="customerId" value="${customerId?if_exists}"/> 
		                    	
		                      	<@inputArea
						   			id="noteInfo"
						   			label="Note Description"
						   			rows="3"
						   			placeholder = "Description"
						   			value = ""
						   			required=true
						   			maxlength="255"
						   		/>
						   		<#assign fileSourceList = Static["org.fio.admin.portal.util.DataUtil"].toLinkedMap("Notes","Notes","Filenet","Filenet") />
		                        <@dropdownCell
		                    		id="fileSource"
		                  			label="File Source"
		                  			options=fileSourceList
		                  			placeholder = "Please Select"
		                    	/>
	                    		<div id= "upload">
	                    			<@inputRow id="uploadFile" type="file" label="Upload" />
	                    		</div>
	                    	</div>
						</div>          
                    	<div class="modal-footer">
                    		<@submit class="btn btn-sm btn-primary navbar-dark" label="${uiLabelMap.Save}"/>
                    	</div>
                	</form>
            	</div>
        	</div>
		</div>
	</div>


<script>
	$("#fileSource").change(function() {
		var fileSource  = $("#fileSource").val();
		var upload = document.getElementById("upload");
		if(fileSource != null && fileSource != "" && fileSource == "Notes"){
			upload.style.display = "none";
		}else{
			upload.style.display = "block";
		}
	});
		
	$('#uploadFile').on('change', function() { 
		const size = (this.files[0].size / 1024 / 1024).toFixed(2);
        var fileInput = document.getElementById('uploadFile');
    	var filePath = fileInput.value;
        var ext = filePath.substring(filePath.lastIndexOf('.') + 1);
        if(ext == "exe"){
        	alert("The selected attachment is not Allowed to upload"); 
            $("#uploadFile").val('');
      	}  
        if (size > 5) { 
        	alert("The attachment is too large, preferred size is less than 5 MB"); 
            $("#uploadFile").val('');
      	}
	});
		
	$("#createSrNotesAndAttachment").submit(function(e){
		$("#createSrNotesAndAttachment").attr("action", $("#createSrNotesAndAttachment").attr("action") + "&noteInfo=" + $("#noteInfo").val() + "&customerId=" + $("#customerId").val() + "&noteName=" + $("#noteName").val() + "&fileSource=" + $("#fileSource").val());	
	});
	
</script>
                       


