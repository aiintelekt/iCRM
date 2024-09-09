<#include "component://homeapps/webapp/homeapps/lib/ofbizFormMacros.ftl"/>
         <div class="modal-header">
            <h4 class="modal-title">${uiLabelMap.CommonLead!}</h4>
            <button type="reset" class="close" data-dismiss="modal">&times;</button>
         </div>
         <div class="modal-body">
            <div class="card-header">
				<form method="post" action="findLeads" id="findLead" class="form-horizontal" name="findLead" novalidate="novalidate" data-toggle="validator">
					<div class="row padding-r">
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
				<div class="form-group row RM">
					<label class="col-sm-4 col-form-label " for="source">${uiLabelMap.leadStatus}</label>
					  <div class="col-sm-7 input-group"> 
						<select name="leadStatus" id="leadStatus"  class="ui dropdown search form-control input-sm leadStatus" multiple data-selected-text-format="count > 0">
	                      <#if leadStatusList?exists && leadStatusList?has_content>
	                        <#list leadStatusList as li>
	                           <option value="${li.enumCode!}">${li.description!}</option>
	                        </#list>
	                      </#if>
	                    </select>
				      </div>
				   </div>  
		           <div class="form-group row RM">
					  		<label class="col-sm-4 col-form-label " for="source">Lead Sub-Status</label>
						  <div class="col-sm-7 input-group"> 
							<select name="leadSubStatus" id="leadSubStatus"  class="ui dropdown search form-control input-sm leadSubStatus" multiple data-selected-text-format="count > 0">
		                      <#if leadSubStatusList?exists && leadSubStatusList?has_content>
		                        <#list leadSubStatusList as li>
		                           <option value="${li.enumId!}">${li.description!}</option>
		                        </#list>
		                      </#if>
		                    </select>
					      </div>
					   </div> 
					</div>
				</div>			
			</div>
			<div class="col-md-4 col-sm-4">			
				<div class="portlet-body form">					
					<div class="form-body">	
					<div class="form-group row RM">
					  		<label class="col-sm-4 col-form-label " for="source">Lead Assigned To</label>
						  <div class="col-sm-7 input-group"> 
							<select name="leadAssignTo" id="leadAssignTo"  class="ui dropdown search form-control input-sm leadAssignTo" multiple data-selected-text-format="count > 0">
		                      <#if leadAssignedToList?exists && leadAssignedToList?has_content>
		                        <#list leadAssignedToList as li>
		                           <option value="${li.partyId!}">${li.firstName!} ${li.lastName!}</option>
		                        </#list>
		                      </#if>
		                    </select>
					      </div>
					   </div>
		           <div class="form-group row RM">
					  		<label class="col-sm-4 col-form-label " for="source">${uiLabelMap.city}</label>
						  <div class="col-sm-7 input-group"> 
							<select name="location" id="location"  class="ui dropdown search form-control input-sm location" multiple data-selected-text-format="count > 0">
		                      <#if cityList?exists && cityList?has_content>
		                        <#list cityList as li>
		                           <option value="${li.geoCode!}">${li.geoName!}</option>
		                        </#list>
		                      </#if>
		                    </select>
					      </div>
					   </div>
					<@generalInput 
			           id="leadId"
			           label=uiLabelMap.leadId
			           placeholder=uiLabelMap.leadId
			           required=false
			           maxlength=20
			           />  
					</div>
				</div>			
			</div>
			<div class="col-md-4 col-sm-4">			
				<div class="portlet-body form">					
					<div class="form-body">	
						<div class="form-group row RM">
					  		<label class="col-sm-4 col-form-label " for="source">Users' Manager</label>
						  <div class="col-sm-7 input-group"> 
							<select name="userManager" id="userManager"  class="ui dropdown search form-control input-sm userManager" multiple data-selected-text-format="count > 0">
		                      <#if userManagerList?exists && userManagerList?has_content>
		                        <#list userManagerList as li>
		                           <option value="${li.partyId!}">${li.firstName!} ${li.lastName!}</option>
		                        </#list>
		                      </#if>
		                    </select>
					      </div>
					   </div>
					<div class="form-group row RM">
					  		<label class="col-sm-4 col-form-label " for="source">${uiLabelMap.leadSource}</label>
						  <div class="col-sm-7 input-group"> 
							<select name="source" id="source"  class="ui dropdown search form-control input-sm source" multiple data-selected-text-format="count > 0">
		                      <#if leadSourceList?exists && leadSourceList?has_content>
		                        <#list leadSourceList as li>
		                           <option value="${li.partyIdentificationTypeId!}">(${li.partyIdentificationTypeId!}) ${li.description!}</option>
		                        </#list>
		                      </#if>
		                    </select>
					      </div>
					   </div>
					</div>
				</div>			
			</div>
		</form> 
	</div>
	<div class="float-right" style="margin-right: 26px;">
	    <@fromSimpleAction id="find-lead-button" showCancelBtn=false isSubmitAction=false submitLabel="Find"/>
	</div>
	<div class="float-right">
		<@fromSimpleAction id="resetFindForm" showCancelBtn=false isSubmitAction=false submitLabel="Reset"/>
	</div>

	<div class="clearfix"> </div>
	<div class="page-header">
	   <h2 class="float-left">${uiLabelMap.leadList}</h2>
	</div> 
	
	<div class="table-responsive"  style="overflow-x: scroll!important;">
		<div class="clearfix"></div>
	   <table id="findLeadsTable" class="table table-striped">
	      <thead>
	         <tr>
	            <th>${uiLabelMap.leadId}</th>
	            <th>${uiLabelMap.companyName}</th>
	            <th>${uiLabelMap.leadStatus}</th>
	            <th>Lead Sub-Status</th>
	            <th># of Attempts</th>
	            <th>${uiLabelMap.leadSource}</th>
	            <th>Lead Assigned From</th>
	            <th>${uiLabelMap.leadAssignTo}</th>
	            <th>Last Called Date</th>
	            <th>Last Call Log Updated Date</th>
	            <th>Manager Name</th>
	            <th>Last Meeting Date</th>
	            <th>Last Meeting Log Updated Date</th>
	            <th>Lead Assigned to City</th>
	            <th>Days in Queue</th>
	            <th>Lead Classification</th>
	            </tr>
	      </thead>
	   </table>
	</div>
</div>
<style>
div.dataTables_processing {
    top: 20px!important;
}
</style>

<script>
$(document).ready(function() {
	loadFindLeads();
});
function loadFindLeads() {
	$("#find-lead-button").prop('disabled', true);
	var partyId = $("#leadId").val();
    var companyName = $("#companyName").val();
    var location = $("#location").val();
    var leadStatus = $("#leadStatus").val();
    var leadSubStatus = $("#leadSubStatus").val();
    var leadAssignTo = $("#leadAssignTo").val();
    var userManager = $("#userManager").val();
    var source = $("#source").val();
    var findLeadsUrl = "getLeadDetailsExt?partyId="+partyId+"&companyName="+companyName+"&location="+location+"&leadStatus="+leadStatus;
    findLeadsUrl += "&leadSubStatus="+leadSubStatus+"&leadAssignTo="+leadAssignTo+"&userManager="+userManager+"&source="+source;

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
        "pageLength": 100,
        "bAutoWidth": false,
        "stateSave": false,
        "columns": [
            {
                "data": "partyId",
                "render": function(data, type, row, meta) {
                    var partyId = row.partyId;
                    if (partyId != null && partyId != "" && partyId != undefined) {
                        data = '<a href="#" id="assignLead'+row.partyId+'" onclick="assignContactToLead('+"'"+row.partyId+"'"+')">' + row.partyId + '</a>';
                    }
                    return data;
                }
            },
            {
                "data": "companyName", 
                /*"render": function(data, type, row, meta) {
                    var companyName = row.companyName;
                    if (companyName != null && companyName != "" && companyName != undefined) {
                    	if(companyName.length > 25) 
                    		companyName = companyName.substring(0,25);
                		data = "<span title='" + row.companyName + "'>" + companyName + "</span>";
                    }
                    return data;
                }*/          
            },
            { "data": "leadStatus" },
            { "data": "leadSubStatus", 
                /*"render": function(data, type, row, meta) {
                    var leadSubStatus = row.leadSubStatus;
                    if (leadSubStatus != null && leadSubStatus != "" && leadSubStatus != undefined) {
                    	if(leadSubStatus.length > 15) 
                    		leadSubStatus = leadSubStatus.substring(0,15);
                		data = "<span title='" + row.leadSubStatus + "'>" + leadSubStatus + "</span>";
                    }
                    return data;
                }*/ },
            { "data": "noOfAttempt",
                "render": function(data, type, row, meta) {
                   var noOfAttempt = "";
                   if(row.noOfAttempt != null && row.noOfAttempt != "")
                   	noOfAttempt = row.noOfAttempt;
                   else
                   noOfAttempt = 0;
                   return noOfAttempt;
                 } },
            { "data": "dataSourceDesc",
            	/*"render": function(data, type, row, meta) {
                    var dataSourceDesc = row.dataSourceDesc;
                    if (dataSourceDesc != null && dataSourceDesc != "" && dataSourceDesc != undefined) {
                    	if(dataSourceDesc.length > 6) 
                    		dataSourceDesc = dataSourceDesc.substring(0,6);
                		data = "<nobr><span title='" + row.dataSourceDesc + "'>" + dataSourceDesc + "</span></nobr>";
                    }
                    return data;
                }*/  },
            { "data": "leadAssignFromName" },
            { "data": "leadAssignToName" },
            { "data": "lastCalledDate", 
            	"render": function(data, type, row, meta) {
                   var lastCalledDate = "";
                   if(row.lastCalledDate != null && row.lastCalledDate != ""){
                   	  var parts =row.lastCalledDate.substring(0,10).split('-');
                   	  lastCalledDate = parts[2] + '/' + parts[1] + '/' + parts[0];
               	   }
                   return lastCalledDate;
                 } },
            { "data": "lastCallLogUpdatedDate", 
            	"render": function(data, type, row, meta) {
                   var lastCallLogUpdatedDate = "";
                   if(row.lastCallLogUpdatedDate != null && row.lastCallLogUpdatedDate != ""){
                   	  var parts =row.lastCallLogUpdatedDate.substring(0,10).split('-');
                   	  lastCallLogUpdatedDate = parts[2] + '/' + parts[1] + '/' + parts[0];
               	   }
                   return lastCallLogUpdatedDate;
                 } },
            { "data": "managerName"  },// "personResponsible"
            { "data": "lastMeetingDate", 
            	"render": function(data, type, row, meta) {
                   var lastMeetingDate = "";
                   if(row.lastMeetingDate != null && row.lastMeetingDate != ""){
                   	  var parts =row.lastMeetingDate.substring(0,10).split('-');
                   	  lastMeetingDate = parts[2] + '/' + parts[1] + '/' + parts[0];
               	   }
                   return lastMeetingDate;
                 } },
            { "data": "lastMeetingLogUpdateDate", 
            	"render": function(data, type, row, meta) {
                   var lastMeetingLogUpdateDate = "";
                   if(row.lastMeetingLogUpdateDate != null && row.lastMeetingLogUpdateDate != ""){
                   	  var parts =row.lastMeetingLogUpdateDate.substring(0,10).split('-');
                   	  lastMeetingLogUpdateDate = parts[2] + '/' + parts[1] + '/' + parts[0];
               	   }
                   return lastMeetingLogUpdateDate;
                 } },
            { "data": "cityName" },
            { "data": "daysInQueue" },
            { "data": "leadClassification" }
        ],
        "initComplete": function(settings, json) {
		    $("#find-lead-button").prop('disabled', false);
		  }
    });
}

$("#resetFindForm").click(function(){
   $(".ui.dropdown.search.location > a.ui.label.transition.visible").remove();
   $(".ui.dropdown.search.leadStatus > a.ui.label.transition.visible").remove();
   $(".ui.dropdown.search.leadSubStatus > a.ui.label.transition.visible").remove();
   $(".ui.dropdown.search.leadAssignTo > a.ui.label.transition.visible").remove();
   $(".ui.dropdown.search.userManager > a.ui.label.transition.visible").remove();
   $(".ui.dropdown.search.source > a.ui.label.transition.visible").remove();
   $(".ui.search.dropdown .menu > div").removeClass("active selected");
   $('#findLead').trigger("reset");
});

</script>
