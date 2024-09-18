<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<div class="row">
   <div id="main" role="main" class="pd-btm-title-bar">
	   <#if parameters.select?exists && parameters.select == "Y">
		   <div class="top-band bg-light"> 
		   <h1>${uiLabelMap.findLeads}</h1>
		    <button type="reset" class="close" style="position:relative;top:-30px;" data-dismiss="modal">�</button>
			<script>
			$(document).ready(function() {
				$('.fixed-top').hide();
				$('.footer').hide();
			});
			</script>
			</div>					
		<#else>
			<@sectionFrameHeader title="${uiLabelMap.findLeads!}" />
		</#if>
		<div class="col-md-12 col-lg-12 col-sm-12 ">
			<div id="accordion">
				<div class="row">
					<@arrowDownToggle />
				</div>
				<div>
					<div>
						<form method="post" action="findLeads" id="searchForm" class="form-horizontal" name="searchForm" data-toggle="validator">
							<div class="border rounded bg-light margin-adj-accordian pad-top">
								<div class="row p-2">
									<div class="col-lg-4 col-md-4 col-sm-4">			                  
										<@inputRow 
											id="companyName"
											label=uiLabelMap.companyName
											placeholder=uiLabelMap.companyName
											required=false
											maxlength=255
											/>
									</div>
									<div class="col-lg-4 col-md-4 col-sm-4"> 
										<@multiSelectInput
											id="leadStatus"
											options=leadStatusList
											required=false
											allowEmpty=true
											emptyText = uiLabelMap.leadStatus
											label = uiLabelMap.leadStatus
											multiple=true
											dataLiveSearch=true
											/> 
									</div>
									<div class="col-lg-4 col-md-4 col-sm-4"> 
										<@multiSelectInput
											id="leadSubStatus"
											options=leadSubStatusList
											required=false
											allowEmpty=true
											emptyText = "Lead Sub-Status"
											label = "Lead Sub-Status"
											multiple=true
											dataLiveSearch=true
											/> 			
									</div>
									<div class="col-lg-4 col-md-4 col-sm-4">
										<@multiSelectInput
											id="leadAssignTo"
											options=leadAssignedToList
											required=false
											allowEmpty=true
											emptyText = "Lead Assigned To"
											label = "Lead Assigned To"
											multiple=true
											dataLiveSearch=true
											/>
									</div>
									<div class="col-lg-4 col-md-4 col-sm-4">
										<@multiSelectInput
											id="location"
											options=cityList
											required=false
											allowEmpty=true
											emptyText = uiLabelMap.city
											label = uiLabelMap.city
											multiple=true
											dataLiveSearch=true
											/>
									</div>
									<div class="col-lg-4 col-md-4 col-sm-4">
										<@inputRow 
											id="leadId"
											label=uiLabelMap.leadId
											placeholder=uiLabelMap.leadId
											required=false
											maxlength=20
										/>                       		
									</div>
									<div class="col-lg-4 col-md-4 col-sm-4">
										<@multiSelectInput
											id="userManager"
											options=userManagerList
											required=false
											allowEmpty=true
											emptyText = "Users' Manager"
											label = "Users' Manager"
											multiple=true
											dataLiveSearch=true
											/>
									</div>
									<div class="col-lg-4 col-md-4 col-sm-4">
										<@multiSelectInput
											id="source"
											options=leadSourceList
											required=false
											allowEmpty=true
											emptyText = uiLabelMap.leadSource
											label = uiLabelMap.leadSource
											multiple=true
											dataLiveSearch=true
											/>			
									</div>
									<div class="col-lg-12 col-md-12 col-sm-12">
										<div class="text-right" style="margin-right: 35px;">		
										 <@button id="doSearch" label="Search"/>
										 <@reset id="resetFindForm" label="Reset"/>
										</div>
									</div>			
								</div>
							</div>
						</form>
					</div>					
				</div>
			</div>
			<form method="post" action="exportLeadData" id="exportLeadFrom" class="form-horizontal" name="exportLeadFrom" novalidate="novalidate" data-toggle="validator">      
	            <@inputHidden id="exType" name="exportType" />
	            <@inputHidden id="exSelectedFields" name="selectedFields" />
	            <@inputHidden id="exLeadId" name="leadId" />
	            <@inputHidden id="exCompanyName" name="companyName" />
	            <@inputHidden id="exLeadStatus" name="leadStatus" />
	            <@inputHidden id="exLeadSubStatus" name="leadSubStatus" />
	            <@inputHidden id="exLeadSource" name="leadSource" />
	            <@inputHidden id="exLeadAssignedTo" name="leadAssignedTo" />
	            <@inputHidden id="exUserManager" name="userManager" />
	            <@inputHidden id="exLeadSource" name="leadSource" />      
	        </form>
         	<div class="clearfix"> </div>
         	<div class="border-b pt-2 mb-2">
	            <@headerH2 class="d-inline-block float-left" title=uiLabelMap.leadList />
		         	<#if !(parameters.select?exists && parameters.select == "Y")|| (displayFields?exists && displayFields =="Y")>
		            <div class="col-md-8" id="exportLead">
		               <div class="row"> 
		                  <div class="col-md-7">	   	 
		                     <@multiSelectInput 
		                        id="selectedFields"
		                        options=exportFieldList
		                        required=false
		                        allowEmpty=false
		                        dataLiveSearch=true
		                        multiple=true
		                        inputColSize="col-md-12"
		                     />	
		                  </div>     	
		                  <div class="col-md-2">       	
		                     <@dropdownCell 
		                        id="exportType"
		                        options=exportTypeList
		                        required=false
		                        allowEmpty=false
		                        dataLiveSearch=true
		                     />		
		                  </div>		
		                  <div class="col-md-3">	  	
		                     <div class="float-right pr-3" >
		                        <@cancel onclick="javascript:callExportLeadData();" class="btn btn-xs btn-primary" 
		                        	id="exportLeadList" label=uiLabelMap.export />
		                        <@button class="btn btn-xs btn-primary" id="rmReassignList" label=uiLabelMap.rmReassign />
		                     </div>
		                  </div>					
		               </div>
		            </div>
	            <#else>
		        	<div class="float-right" >
		               <@button class="btn btn-xs btn-primary" id="rmReassignList" label=uiLabelMap.rmReassign />
		            </div>
	            </#if>          
         	</div>      			         
			<div class="clearfix"> </div> 	
			<div class="table-responsive">
				<div id="myGrid" style="width: 100%;" class="ag-theme-balham"></div>                        	    					
			</div>
			<script type="text/javascript" src="/crm-resource/js/ag-grid/lead/lead.js"></script>
		</div>
	</div><#-- End main-->
</div><#-- End row-->

<div id="rmReassignModal" class="modal fade" role="dialog" data-keyboard="false" data-backdrop="static">
   <div class="modal-dialog modal-md">
      <!-- Modal content-->
      <div class="modal-content">
         <div class="modal-header">
            <h4 class="modal-title">${uiLabelMap.rmReassign!}</h4>
            <button type="reset" class="close" data-dismiss="modal">&times;</button>
         </div>
         <div class="modal-body">
            <div class="card-header">
               <form method="post" id="rmReassignFromLead" class="form-horizontal" name="rmReassignFromLead" novalidate="novalidate" data-toggle="validator">
               <input type="hidden" id="partyList" name="partyList" value="">
                 <div class="row padding-r">
                   <#if fullAdminAccess?default(false)>
                    <div class="col-md-12 col-sm-12">
                     <@dropdownCell 
                     id="teamId"
                     label=uiLabelMap.virtualTeam
                     options=virtualTeamMemberList
                     required=true
                     allowEmpty=true
                     dataLiveSearch=true
                     />
                    </div>
                   </#if>
                   <div class="col-md-12 col-sm-12">
                     <@dropdownCell
                     id="reAssignPartyId"
                     label=uiLabelMap.rmReassign
                     options=rmLists
                     required=true
                     allowEmpty=true
                     dataLiveSearch=true
                     />
                   </div>
                  </div>
               </form>
               <div class="row padding-r">
                  <div class="col-md-12 col-sm-12">
                     <@submit label="Save" onclick="rmReassignSubmit();"/>                     
                  </div>
               </div>
               <div class="clearfix"> </div>
            </div>
         </div>
         <div class="modal-footer">
            <button type="submit" class="btn btn-sm btn-primary" data-dismiss="modal">${uiLabelMap.close!}</button>
         </div>
      </div>
   </div>
</div>
<script>
$("#doSearch").click(function(event) {
	event.preventDefault(); 
	loadAgGrid();
});
$(document).ready(function() {

$("#reAssignPartyId").change(function() {
   if($(this).val() != null && $(this).val() != "") {
      $("#reAssignPartyId_error").empty();
      $("#reAssignPartyId_error").css('display','none');
   } else {
      $("#reAssignPartyId_error").css('display','block');
   }
});
<#if fullAdminAccess?default(false)>
$("#teamId").change(function() {
   if($(this).val() != null && $(this).val() != "") {
      $("#teamId_error").empty();
      $("#teamId_error").css('display','none');
   } else {
      $("#teamId_error").css('display','block');
   }
});
</#if>
$(".form_datetime").datetimepicker({
    //autoclose: true,
    //isRTL: BootStrapInit.isRTL(),
    //format: "dd MM yyyy - hh:ii",
    //pickerPosition: (BootStrapInit.isRTL() ? "bottom-right" : "bottom-left")
});
       
});

$("#rmReassignList").click(function () {
  if($('#partyList').val() != ""){
  	$('#rmReassignModal').modal('show');
  }else {
    $.notify({
      message : '<p>Please select at least one record in the list</p>'
    });
  }
});

<#if fullAdminAccess?default(false)>
$("#teamId").change(function() {
   var reAssignPartyOptions = $("#reAssignPartyId").empty();
     $('#reAssignPartyId').dropdown('clear');
     $.ajax({
         type: "POST",
         url: "getVirtualTeamRM",
         data: {
             "teamId": $(this).val()
         },
         async: false,
         success: function(data) {
             if (data.code == 200) {
                 $("#reAssignPartyId").empty();
                 reAssignPartyOptions.append("<option value=''>Please Select</option>");
                 for (var i = 0; i < data.results.length; i++) {
                     var result = data.results[i];
                     reAssignPartyOptions.append("<option  value =" + result.virtualTeamMemberId + ">" + result.virtualTeamMemberName + " </option>");
                 }
             }
         }
     });
     $('#reAssignPartyId').append(reAssignPartyOptions);
     $('#reAssignPartyId').dropdown('refresh');
});
</#if>

function rmReassignSubmit() {
    var teamValidation = "Y";
    <#if fullAdminAccess?default(false)>
       var teamId = $("#teamId").val();
       if (teamId == null || teamId == "") {
          teamValidation = "N";
          $("#teamId_error").empty();
          $("#teamId_error").append('<ul class="list-unstyled text-danger"><li>Please select an item in the list.</li></ul>');
          $("#teamId_error").css('display', 'block');
       }
    </#if>
    var reAssignPartyId = $("#reAssignPartyId").val();
    if (reAssignPartyId != null && reAssignPartyId != "" && teamValidation == "Y") {
        $("#reAssignPartyId_error").empty();
        $("#reAssignPartyId_error").css('display', 'none');
        $.ajax({
            url: "rmReassignFromLeadAjax",
            type: 'POST',
            data: $('#rmReassignFromLead').serialize(),
            success: function(data) {
                if (data.code == 200) {
                    showAlert("success", data.message);
                    //loadFindLeads();
                    $("#selectAll").prop("checked", false);
                    $('input[name="leadIdCheckBox"]').each(function(){ 
                       this.checked = false;
                    });
                    $('#reAssignPartyId').dropdown('refresh');
                    $('#rmReassignModal').modal('hide');
                } else {
                    showAlert("error", data.message);
                }
            }
        });
    } else {
        $("#reAssignPartyId_error").empty();
        $("#reAssignPartyId_error").append('<ul class="list-unstyled text-danger"><li>Please select an item in the list.</li></ul>');
        $("#reAssignPartyId_error").css('display', 'block');
    }
}
	
function callExportLeadData() {
    var selectedFields = $('#selectedFields').val();
    //var RMRoleList = $("#RMRoleList").val();
    
    console.log('selectedFields---'+selectedFields);
    
    $('#exportLeadFrom input[name="selectedFields"]').val( $('#selectedFields').val() );
	$('#exportLeadFrom input[name="exportType"]').val( $('#exportType').val() );
	$('#exportLeadFrom input[name="leadId"]').val( $('#leadId').val() );
	$('#exportLeadFrom input[name="companyName"]').val( $('#companyName').val() );
	$('#exportLeadFrom input[name="location"]').val( $('#location').val() );
	$('#exportLeadFrom input[name="leadSource"]').val( $('#source').val() );
	$('#exportLeadFrom input[name="leadStatus"]').val( $('#leadStatus').val() );
	$('#exportLeadFrom input[name="leadSubStatus"]').val( $('#leadSubStatus').val() );
	$('#exportLeadFrom input[name="userManager"]').val( $('#userManager').val() );
	$('#exportLeadFrom input[name="leadAssignedTo"]').val( $('#leadAssignTo').val() );
	
	$('#exportLeadFrom').submit();
	
}
$("#resetFindForm").click(function(){
   $(".ui.dropdown.search.location > a.ui.label.transition.visible").remove();
   $(".ui.dropdown.search.leadStatus > a.ui.label.transition.visible").remove();
   $(".ui.dropdown.search.leadSubStatus > a.ui.label.transition.visible").remove();
   $(".ui.dropdown.search.leadAssignTo > a.ui.label.transition.visible").remove();
   $(".ui.dropdown.search.userManager > a.ui.label.transition.visible").remove();
   $(".ui.dropdown.search.source > a.ui.label.transition.visible").remove();
   /*$('.ui.dropdown.search.form-control.leadSubStatus.selection > div.text').html("");
   $(".ui.dropdown.search.form-control.leadSubStatus.selection > i").removeClass("clear");*/
   $(".ui.search.dropdown .menu > div").removeClass("active selected");
   $('#doSearch').trigger("reset");
});
</script>