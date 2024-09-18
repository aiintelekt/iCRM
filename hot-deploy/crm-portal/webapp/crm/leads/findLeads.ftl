<#include "component://homeapps/webapp/homeapps/lib/ofbizFormMacros.ftl"/>
<div class="page-header border-b">
   <h1>${uiLabelMap.findLeads}</h1>
</div>

<form method="post" action="findLeads" id="findLead" class="form-horizontal" name="findLead" novalidate="novalidate" data-toggle="validator">
<div class="row padding-r">

	<div class="col-md-4 col-sm-4">
				
		<div class="portlet-body form">
						
			<div class="form-body">
			
			<@generalInput 
	           id="partyId"
	           label=uiLabelMap.leadId
	           placeholder=uiLabelMap.leadId
	           required=false
	           maxlength=20
	           />
				
           <#-- <@generalInput 
	           id="location"
	           label=uiLabelMap.city
	           placeholder=uiLabelMap.city
	           required=false
	           maxlength=100
	           /> -->
	        <@dropdownInput 
				id="location"
				label=uiLabelMap.city
				options=cityList
				required=false
				allowEmpty=true
				dataLiveSearch=true
				/>      	
			
			<@dropdownInput 
				id="tallyUserType"
				label=uiLabelMap.tallyUserType
				options=tallyUserTypeList
				required=false
				allowEmpty=true
				dataLiveSearch=true
				/>
				
			<@dropdownInput 
				id="source"
				label=uiLabelMap.source
				options=leadSourceList
				required=false
				allowEmpty=true
				dataLiveSearch=true
				/>
				<#if displayFields?exists && "${displayFields}" =="Y">
				<div class="form-group row RM">
				  <label class="col-sm-4 col-form-label " for="source">RM/TC</label>
				  <div class="col-sm-7 input-group"> 
				    <select name="RMRoleList" id="RMRoleList"  class="ui dropdown search form-control input-sm" multiple data-selected-text-format="count > 0">
                      <#if RMRoleList?exists && RMRoleList?has_content>
                        <#list RMRoleList as li>
                           <option value="${li.partyId!}">${li.firstName!} ${li.lastName!}</option>
                        </#list>
                      </#if>
                    </select>
			      </div>
			   </div>
			   
			   <script>
				$('#RMRoleList').dropdown({
					useLabels: false
				
				});
				</script>
			   <#else>
			   <input type="hidden" name="RMRoleList" id="RMRoleList" />
			   </#if>
			</div>
		</div>
						
	</div>
	
	<div class="col-md-4 col-sm-4">
				
		<div class="portlet-body form">
						
			<div class="form-body">
			
			<@generalInput 
	           id="companyName"
	           label=uiLabelMap.companyName
	           placeholder=uiLabelMap.companyName
	           required=false
	           maxlength=255
	           />
			
			<div class="form-group row">
			   <label class="col-sm-4 col-form-label " for="title">${uiLabelMap.fromCallBackDate!}</label>
			   <div class="col-sm-7">
			      <@simpleDateInput 
			      	id="fromCallBackDate"
					name="fromCallBackDate"
					dateFormat="YYYY-MM-DD"
					/>
			   </div>
			</div>
			
			<@generalInput 
	           id="salesTurnoverFrom"
	           label=uiLabelMap.salesTurnoverFrom
	           placeholder=uiLabelMap.salesTurnoverFrom
	           required=false
	           inputType="number"
	           />	
	           
	        <@dropdownInput 
				id="leadStatus"
				label=uiLabelMap.leadStatus
				options=leadStatusList
				required=false
				allowEmpty=true
				dataLiveSearch=true
				/>     
			
			<@dropdownInput 
				id="virtualTeamId"
				label=uiLabelMap.virtualTeam
				options=virtualTeamList
				required=false
				allowEmpty=true
				dataLiveSearch=true
				/>		
			 
			</div>
			
		</div>
						
	</div>
	
	<div class="col-md-4 col-sm-4">
				
		<div class="portlet-body form">
						
			<div class="form-body">		
			
			<@generalInput 
	           id="firstName"
	           label=uiLabelMap.firstName
	           placeholder=uiLabelMap.firstName
	           required=false
	           maxlength=100
	           />
	           
	         <div class="form-group row">
			   <label class="col-sm-4 col-form-label " for="title">${uiLabelMap.toCallBackDate!}</label>
			   <div class="col-sm-7">
			      <@simpleDateInput 
			      	id="toCallBackDate"
					name="toCallBackDate"
					dateFormat="YYYY-MM-DD"
					/>
			   </div>
			</div>
			
			<@generalInput 
	           id="salesTurnoverTo"
	           label=uiLabelMap.salesTurnoverTo
	           placeholder=uiLabelMap.salesTurnoverTo
	           required=false
	           inputType="number"
	           />	
			<@dropdownInput 
               id="noOfDaysSinceLastCallOCL"
               name="noOfDaysSinceLastCall"
               options=numberCountOCL
               value="${noOfDaysSinceLastCallOCL?if_exists}"
               allowEmpty=true
               tooltip = uiLabelMap.status
               label="# Days Since Last Call"
               dataLiveSearch=true
            />
			</div>
			
		</div>
						
	</div>
	
</div>

</form>

<div class="float-right" style="margin-right: 26px;">
	<@fromSimpleAction id="find-lead-button" showCancelBtn=false isSubmitAction=false submitLabel="Find"/>
</div>

<form method="post" action="exportLeadData" id="exportLeadFrom" class="form-horizontal" name="exportLeadFrom" novalidate="novalidate" data-toggle="validator">
	
	<input type="hidden" name="exportType" />
	
	<input type="hidden" name="partyId" />
	<input type="hidden" name="companyName" />
	<input type="hidden" name="firstName" />
	<input type="hidden" name="city" />
	<input type="hidden" name="fromCallBackDate" />
	<input type="hidden" name="toCallBackDate" />
	<input type="hidden" name="tallyUserType" />
	<input type="hidden" name="salesTurnoverFrom" />
	<input type="hidden" name="salesTurnoverTo" />
	<input type="hidden" name="source" />
	<input type="hidden" name="leadStatus" />
	<input type="hidden" name="selectedFields" />
	<input type="hidden" name="RMRoleList" />
	<input type="hidden" name="virtualTeamId" />
	
</form>

<div class="clearfix"> </div>
<div class="page-header">
   <h2 class="float-left">${uiLabelMap.leadList}</h2>
</div> 
	
<div class="table-responsive">

<#if displayFields?exists && "${displayFields}" =="Y">
<div class="col-md-8 offset-md-4" id="exportLead">
	<div class="row"> 
   		<div class="col-md-8">
	   	 
		<@simpleDropdownInput 
			id="selectedFields"
			options=exportFieldList
			required=false
			allowEmpty=false
			dataLiveSearch=true
			isMultiple=true
		/>	
   	
   		</div>
   		
    	<div class="clearfix"></div>
    	
       	<div class="col-md-1">
       	
		<@simpleDropdownInput 
			id="exportType"
			options=exportTypeList
			required=false
			allowEmpty=false
			dataLiveSearch=true
		/>
		
		</div>
		
	  	<div class="col-md-3">
	  	
			<div class="float-right pr-3" >
				<a href="javascript:callExportLeadData();" class="btn btn-xs btn-primary" id="exportLeadList">${uiLabelMap.export}</a>
				<button type="reset" class="btn btn-xs btn-primary" id="rmReassignList">RM Reassign</button>
			</div>
		</div>	
				
	</div>
</div>
<#else>
<div class="float-right" >
   <button type="reset" class="btn btn-xs btn-primary" id="rmReassignList">RM Reassign</button>
</div>
</#if>

	<div class="clearfix"></div>
	     
   <table id="findLeadsTable" class="table table-striped">
      <thead>
         <tr>
            <th>${uiLabelMap.leadId}</th>
            <th>${uiLabelMap.companyName}</th>
            <th>${uiLabelMap.firstName}</th>
            <th>${uiLabelMap.tcStatus}</th>
            <th>${uiLabelMap.callBackDate}</th>
            <th>${uiLabelMap.source}</th>
            <th># Days Since Last Call</th>
            <th>${uiLabelMap.leadAssignBy}</th>
            <th>${uiLabelMap.segment}</th>
            <th>${uiLabelMap.liabOrAsset}</th>
            <th>${uiLabelMap.status}</th>
            <#-- <th>${uiLabelMap.createdDate}</th> -->
            <th>${uiLabelMap.rmReassign!} <input type="checkbox" id="selectAll" name="selectAll" value="1" /></th>
         </tr>
      </thead>
      
   </table>
</div>

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
                     <@dropdownInput 
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
                     <@dropdownInput 
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
                     <@fromCommonAction showCancelBtn=false showClearBtn=false submitLabel="Save" onclick="rmReassignSubmit();"/>
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
    
loadFindLeads();
    
$('#find-lead-button').on('click', function(){
	loadFindLeads();
});

$("#rmReassignList").click(function () {
  var form = document.getElementById("rmReassignFromLead");
  var favorite = [];
  if($('#findLeadsTable input[type="checkbox"]').is(':checked')){
    $.each($("input[name='leadIdCheckBox']:checked"), function(){ 
      favorite.push($(this).val());
    });
    $('#partyList').val(favorite.toString());
    $('#rmReassignModal').modal('show');
  } else {
    $.notify({
      message : '<p>Please select atleast one record in the list</p>'
    });
  }
});
       
$("#selectAll").change(function(){  
  var status = this.checked;
  if (status) {
    $('input[name="leadIdCheckBox"]').each(function(){ 
    this.checked = status; 
  });
  } else {
    $('input[name="leadIdCheckBox"]').each(function(){ 
      this.checked = false;
    });
  }
});
       
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

function loadFindLeads() {
		
	$("#find-lead-button").prop('disabled', true);

	var partyId = $("#partyId").val();
    var firstName = $("#firstName").val();
    var companyName = $("#companyName").val();
    //var emailAddress = $("#emailAddress").val();
    //var contactNumber = $("#contactNumber").val();
    var location = $("#location").val();
    var fromCallBackDate = $("#fromCallBackDate").val();
    var toCallBackDate = $("#toCallBackDate").val();
    var source = $("#source").val();
    var noOfDaysSinceLastCall = $("#noOfDaysSinceLastCallOCL").val();
    var salesTurnoverFrom = $("#salesTurnoverFrom").val();
    var salesTurnoverTo = $("#salesTurnoverTo").val();
    var tallyUserType = $("#tallyUserType").val();
    var leadStatus = $("#leadStatus").val();
    var virtualTeamId = $("#virtualTeamId").val();
    var RMRoleList = $("#RMRoleList").val();
    var findLeadsUrl = "getLeadDetails?partyId="+partyId+"&firstName="+firstName+
    "&companyName="+companyName+"&location="+location+"&fromCallBackDate="+fromCallBackDate+"&toCallBackDate="+toCallBackDate+"&source="+source+"&salesTurnoverFrom="+salesTurnoverFrom+"&salesTurnoverTo="+salesTurnoverTo+"&tallyUserType="+tallyUserType+"&leadStatus="+leadStatus+"&noOfDaysSinceLastCall="+noOfDaysSinceLastCall+"&RMRoleList="+RMRoleList+"&virtualTeamId="+virtualTeamId;

    oTable = $('#findLeadsTable').DataTable({
        "processing": true,
        "serverSide": true,
        "searching": false,
        "destroy": true,
        "ordering": true,
        "ajax": {
            "url": findLeadsUrl,
            "type": "POST",
            //"async": false
        },
        "Paginate": true,
        "order": [[ 11, "DESC" ]],
        "language": {
            "emptyTable": "No data available in table",
            "info": "Showing _START_ to _END_ of _TOTAL_ entries",
            "infoEmpty": "No entries found",
            "infoFiltered": "(filtered1 from _MAX_ total entries)",
            "lengthMenu": "Show _MENU_ entries",
            "zeroRecords": "No matching records found",
            "oPaginate": {
                "sNext": "Next",
                "sPrevious": "Previous"
            }
        },
        "columnDefs": [
            /*{
                "targets": [ 11 ],
                "visible": false,
                "searchable": false
            }*/
        ],

        "pageLength": 10,
        "bAutoWidth": false,
        "stateSave": false,
        "columns": [
            {
                "data": "partyId",
                "render": function(data, type, row, meta) {
                    var partyId = row.partyId;
                    if (partyId != null && partyId != "" && partyId != undefined) {
                        data = '<a href="/crm/control/viewLead?partyId=' + row.partyId + '&externalLoginKey=${requestAttributes.externalLoginKey!}">' + row.partyId + '</a>';
                    }
                    return data;
                }
            },
            {
                "data": "companyName"
            },
            {
                "data": "firstName"
            },
            {
                "data": "teleCallingStatus"
            },
            {
                "data": "lastCallBackDate",
                "render": function(data, type, row, meta) {
                   var lastCallBackDate = row.callBackDate;
                   return lastCallBackDate;
                 }
            },
            {
                "data": "dataSourceDesc"
            },
            {
               "data": "lastContactDate",
               "render": function(data, type, row, meta) {
                   var difDays = row.diffDays;
                   var lastContactDate = "";
                   if (difDays != null && difDays != "") {
                       lastContactDate = row.diffDays + " - " + row.noOfDateSinceLastCall;
                   }
                   return lastContactDate;
               }
            },
            {
                "data": "personResponsibleAssignBy"
            },
            {
                "data": "segment"
            },
            {
                "data": "liabOrAsset"
            },
            {
                "data": "leadStatus"
            },
            /*{
                "data": "createdStamp"
            },*/
            {
                "data": "checkBoxSelect",
                "orderable": false,
                "render": function(data, type, row, meta) {
                    var checkBoxSelect = '<input type="checkbox" class="leadIdCheckBox" name="leadIdCheckBox" id="leadIdCheckBox" value="'+row.partyId+'"/>';
                    return checkBoxSelect;
                }
            }
        ],
        "initComplete": function(settings, json) {
		    $("#find-lead-button").prop('disabled', false);
		  }
    });
}
	
function callExportLeadData() {

    var selectedFields = $('#selectedFields').val();
    var RMRoleList = $("#RMRoleList").val();
    
    console.log('selectedFields---'+selectedFields);
    
    $('#exportLeadFrom input[name="RMRoleList"]').val( $('#RMRoleList').val() );
    $('#exportLeadFrom input[name="selectedFields"]').val( $('#selectedFields').val() );
	$('#exportLeadFrom input[name="exportType"]').val( $('#exportType').val() );

	$('#exportLeadFrom input[name="partyId"]').val( $('#partyId').val() );
	$('#exportLeadFrom input[name="companyName"]').val( $('#companyName').val() );
	$('#exportLeadFrom input[name="firstName"]').val( $('#firstName').val() );
	$('#exportLeadFrom input[name="location"]').val( $('#location').val() );
	$('#exportLeadFrom input[name="fromCallBackDate"]').val( $('#fromCallBackDate').val() );
	$('#exportLeadFrom input[name="toCallBackDate"]').val( $('#toCallBackDate').val() );
	$('#exportLeadFrom input[name="tallyUserType"]').val( $('#tallyUserType').val() );
	$('#exportLeadFrom input[name="salesTurnoverFrom"]').val( $('#salesTurnoverFrom').val() );
	$('#exportLeadFrom input[name="salesTurnoverTo"]').val( $('#salesTurnoverTo').val() );
	$('#exportLeadFrom input[name="source"]').val( $('#source').val() );
	$('#exportLeadFrom input[name="leadStatus"]').val( $('#leadStatus').val() );
	$('#exportLeadFrom input[name="virtualTeamId"]').val( $('#virtualTeamId').val() );

	$('#exportLeadFrom').submit();
	
}

</script>