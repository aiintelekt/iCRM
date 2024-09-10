<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<#assign readOnlyPermission = Static["org.fio.homeapps.util.DataUtil"].hasPermissionWoFullPerm(request, "READ_ONLY_PERM")?if_exists />
<#assign rightContent=''/>
<#if readOnlyPermission!>
<#else>
	<#if activityStatus?exists && activityStatus?has_content && (activityStatus == "IA_MIN_PROGRESS" || activityStatus == "IA_MCOMPLETED")>
		<#assign hasPermission = Static["org.fio.homeapps.util.DataUtil"].hasPermission(request, "SR_CREATE_TIME_ENTRY")?if_exists />
		<#if hasPermission>
			<#assign rightContent='<span data-toggle="modal" data-target="#create-time-entry" title="Create Time Entry" class="btn btn-xs btn-primary"><i class="fa fa-edit" aria-hidden="true"></i> Create</span>'/>
		</#if>
	</#if>
</#if>

<style>
	.bootbox-confirm .modal-dialog {
		top:22% !important;
		max-width:25% !important;
	}
</style>

<div class="row">
  	<div class="col-lg-12 col-md-12 col-sm-12">
  	<#-- <@AgGrid
		gridheadertitle="Time Entries"
		gridheaderid="time-entry-container"
		insertBtn=false
		updateBtn=false
		removeBtn=false
		headerextra=rightContent!
		refreshPrefBtnId="time-entry-refresh-pref-btn"
		savePrefBtnId="time-entry-save-pref-btn"
		clearFilterBtnId="time-entry-clear-filter-btn"
		exportBtnId="time-entry-export-btn"
	    userid="${userLogin.userLoginId!}" 
	    shownotifications="true" 
	    instanceid="TIME_ENTRY_LOGS" 
	    autosizeallcol="true"
	    debug="false"
	    /> 
  	<script type="text/javascript" src="/common-portal-resource/js/ag-grid/activity/time-entry-logs.js"></script>-->
  	<form id="timeEntriesForm">
	  	<input type="hidden" id="workEffortId" value="${workEffortId!}"/>
	</form>
	<input type="hidden" id="userId" value="${userLogin.userLoginId!}"/>
  	<input type="hidden" id="externalLoginKey" value="${requestAttributes.externalLoginKey!}"/>

		<@fioGrid 
			id="time-entry-grid"
			instanceId="TIME_ENTRY_LOGS"
			jsLoc="/common-portal-resource/js/ag-grid/activity/time-entry-logs.js"
			headerLabel="Time Entries"
			headerId="time-entry-grid-action-container"
			headerBarClass="grid-header-no-bar"
			savePrefBtn=false
			clearFilterBtn=false
			exportBtn=true
			subFltrClearBtn = false
			exportBtnId="time-entry-grid-export-btn"
			savePrefBtnId ="time-entry-save-pref-btn"
			clearFilterBtnId ="time-entry-clear-filter-btn"
			subFltrClearId="time-entry-sub-filter-clear-btn"
			headerExtra=rightContent!
			/>
  	</div>
</div>

<div id="create-time-entry" class="modal fade" >
    <div class="modal-dialog">
        <!-- Modal content-->
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title">Create Time Entry</h4>
                <button type="button" class="close" data-dismiss="modal">&times;</button>
            </div>
            <div class="modal-body">
            
            	<form method="post" action="#" name="createTimeEntryFrom" id="createTimeEntryFrom" class="form-horizontal" data-toggle="validator">
				    <div class="row">
				        <div class="col-md-12 col-md-12 form-horizontal">
				        	<div class="form-group row error" id="hour_error_row" style="display:none;">
							        <label class="col-sm-3 field-text" id=""></label>
							    <div class=" col-sm-9 left">
							       <div class="help-block with-errors" id="hourminute_error"></div>
							    </div>
							</div>
							<@inputHidden id="activeTab" value="timeEntry" />
							<@inputHidden id="workEffortId" value="${requestParameters.workEffortId!}" />
							<@dropdownCell 
						   		id="partyId"
						   		label="${uiLabelMap.Technician}"
						   	  	placeholder="${uiLabelMap.Technician}"
						   	  	options=technicianList!
						   	  	allowEmpty=true
						   	  	labelColSize="col-sm-3"
					            inputColSize="col-sm-9"
					            required=true
						   	  	/>
						   	
						   	<@dropdownCell 
						   		id="rateTypeId"
						   		label="${uiLabelMap.Purpose}"
						   	  	placeholder="${uiLabelMap.Purpose}"
						   	  	options=rateTypeList!
						   	  	allowEmpty=true
						   	  	labelColSize="col-sm-3"
					            inputColSize="col-sm-9"
					            required=true
						   	  	/>
						   	  	
						   	<@inputDate 
								id="timeEntryDate"
								label="Date of Service"
					            placeholder="Date of Service"
					            dateFormat="MM/DD/YYYY"
					            labelColSize="col-sm-3"
					            inputColSize="col-sm-9"
					            minDate="${actualStartDate?if_exists?string('MM/dd/yyyy')}"
					            maxDate="${actualCompletionDate?if_exists?string('MM/dd/yyyy')}"
					            value="${.now?string('MM/dd/yyyy')}"
								/>
						   	
				        	
				        	<div id="hoursMinutes" style="display:none;">
					        	<@inputRow 
					               id="hours"
					               label="Hours"
					               type="number"
					               placeholder="Hours"
					               max="10"
					               min="0"
					               pattern="[0-9]+"
					               labelColSize="col-sm-3"
					               inputColSize="col-sm-9"
					               onkeypress="return event.charCode > 47 && event.charCode < 58;"
					               />
					            <@inputRow 
					               id="minutes"
					               label="Minutes"
					               type="number"
					               placeholder="Minutes"
					               step=15
					               max="60"
					               min="0"
					               pattern="[0-9]+"
					               labelColSize="col-sm-3"
					               inputColSize="col-sm-9"
					               onkeypress="return event.charCode > 47 && event.charCode < 58;"
					               />
				            </div>
				            
				            <div id="costField" style="display:none;">
				            	<@inputRow 
					               id="cost"
					               label="Cost"
					               placeholder="Cost"
					               min="0"
					               pattern="[0-9]*\\.?[0-9]{2}"
					               labelColSize="col-sm-3"
					               inputColSize="col-sm-9"
					               />
				            </div>
				        	
				        	<@inputArea
				        		id="comments"
								label="Comments"
								labelColSize="col-sm-3"
					            inputColSize="col-sm-9"
				        		/>
				        </div>
				        
				        <div class="col-md-12 col-md-12 form-horizontal">
				        	<div class="form-group row">
					        	<div class="col-sm-3"></div>
					        	<div class="col-sm-9">
									<div class="text-left ml-1">
									   <button id="create-time-entry-btn" type="button" class="btn btn-sm btn-primary"> Save</button>
									   <button id="reset-btn" type="reset" class="btn btn-sm btn-secondary"> Clear</button>
									</div>
								</div>
							</div>
	                    </div>
				    </div>
				</form>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-sm btn-primary navbar-dark" data-dismiss="modal">Close</button>
            </div>
        </div>
    </div>
</div>


<div id="update-time-entry" class="modal fade" >
    <div class="modal-dialog">
        <!-- Modal content-->
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title">Update Time Entry</h4>
                <button type="button" class="close" data-dismiss="modal">&times;</button>
            </div>
            <div class="modal-body">
            
            	<form method="post" action="#" name="updateTimeEntryFrom" id="updateTimeEntryFrom" class="form-horizontal" novalidate="true" data-toggle="validator">
				    <@inputHidden id="timeEntryId" />
				    <@inputHidden id="partyId" />
				    <#-- <@inputHidden id="rateTypeId" /> -->
				    <div class="row">
				        <div class="col-md-12 col-md-12 form-horizontal">
				        	<div class="form-group row error" id="hour_error_row" style="display:none;">
							        <label class="col-sm-3 field-text" id=""></label>
							    <div class=" col-sm-9 left">
							       <div class="help-block with-errors" id="hourminute_error"></div>
							    </div>
							</div>
							<@displayCell 
								id="techName"
						   		label="${uiLabelMap.Technician}"
						   	  	labelColSize="col-sm-3"
					            inputColSize="col-sm-9"
						   	  	/>
						   	
						   	<@dropdownCell 
						   		id="rateTypeId"
						   		label="${uiLabelMap.Purpose}"
						   	  	placeholder="${uiLabelMap.Purpose}"
						   	  	options=rateTypeList!
						   	  	allowEmpty=true
						   	  	labelColSize="col-sm-3"
					            inputColSize="col-sm-9"
					            required=true
						   	  	/>
						   	  	
						   	<@inputDate 
								id="timeEntryDate"
								label="Date of Service"
					            placeholder="Date of Service"
					            dateFormat="MM/DD/YYYY"
					            labelColSize="col-sm-3"
					            inputColSize="col-sm-9"
					            minDate="${actualStartDate?if_exists?string('MM/dd/yyyy')}"
					            maxDate="${actualCompletionDate?if_exists?string('MM/dd/yyyy')}"
					            value="${.now?string('MM/dd/yyyy')}"
								/>
				        	<div id="hoursMinutes">
				        	<@inputRow 
				               id="hours"
				               label="Hours"
				               type="number"
				               placeholder="Hours"
				               required=true
				               max="10"
				               min="0"
				               pattern="[0-9]+"
				               labelColSize="col-sm-3"
				               inputColSize="col-sm-9"
				               onkeypress="return event.charCode > 47 && event.charCode < 58;"
				               />
				            <@inputRow 
				               id="minutes"
				               label="Minutes"
				               type="number"
				               placeholder="Minutes"
				               step=15
				               max="60"
				               min="0"
				               pattern="[0-9]+"
				               labelColSize="col-sm-3"
				               inputColSize="col-sm-9"
				               onkeypress="return event.charCode > 47 && event.charCode < 58;"
				               />
				            </div>
				            
				            <div id="costField" style="display:none;">
				            	<@inputRow 
					               id="cost"
					               label="Cost"
					               placeholder="Cost"
					               required=true
					               min="0"
					               pattern="[0-9]*\\.?[0-9]{2}"
					               labelColSize="col-sm-3"
					               inputColSize="col-sm-9"
					               />
				            </div>
				            <@inputArea
				        		id="comments"
								label="Comments"
								labelColSize="col-sm-3"
					            inputColSize="col-sm-9"
				        		/>
				            <#-- 
				            <div class="form-group row hours" id="hours_row" style="">
				                <label class="col-sm-4 field-text" id="hours_label">Hours<span class="text-danger"> *</span></label>
				                <div class=" col-sm-6">
				                    <input type="text" class="form-control input-sm" value="" id="hours" name="hours" placeholder="Hours" autocomplete="off" required minlength="" maxlength="" step="">
				                    <div class="help-block with-errors" id="hours_error"></div>
				                </div>
				            </div> -->
				        </div>
				        
				        <div class="col-md-12 col-md-12 form-horizontal">
				        	<div class="form-group row">
					        	<div class="col-sm-3"></div>
					        	<div class="col-sm-9">
									<div class="text-left ml-1">
									   <button id="update-time-entry-btn" type="button" class="btn btn-sm btn-primary"> Save</button>
									   <button id="reset-btn" type="reset" class="btn btn-sm btn-secondary"> Clear</button>
									</div>
								</div>
							</div>
	                    </div>
				    </div>
				</form>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-sm btn-primary navbar-dark" data-dismiss="modal">Close</button>
            </div>
        </div>
    </div>
</div>

<script>

	$("#create-time-entry").on("show.bs.modal", function(e) {
		$("#create-sr-time-entry").hide();
		$("#createTimeEntryFrom #hoursMinutes").hide();
		$("#createTimeEntryFrom #costField").hide();
		$('#createTimeEntryFrom #workEffortId').dropdown('clear');
		$("#createTimeEntryFrom #rateTypeId").dropdown('clear');
		$("#createTimeEntryFrom #partyId").dropdown('clear');		
		$('#createTimeEntryFrom')[0].reset();
   	});
   	
	$("#createTimeEntryFrom #rateTypeId").change(function(){
		
		$("#createTimeEntryFrom #hours").val('');
		$("#createTimeEntryFrom #minutes").val('');
		$("#createTimeEntryFrom #cost").val('');
		
		var rateTypeId = this.value; 
		if("TOLL_CHARGE"== rateTypeId || "ANCILLARY_COST" == rateTypeId || "LEGACY_TOLL_CHARGE"== rateTypeId || "LEGACY_ANCILLARY_COST" == rateTypeId){
			$("#hoursMinutes").hide();
    		$("#costField").show();
		} else{
			$("#hoursMinutes").show();
    		$("#costField").hide();
		}
		if(!rateTypeId) $("#createTimeEntryFrom #rateTypeId_error").html("Please select purpose");
		else $("#createTimeEntryFrom #rateTypeId_error").html("");
	});
	
	$("#createTimeEntryFrom #hours").keyup(function(){
		if(this.value) $("#createTimeEntryFrom #hours_error").html("");
	});
	$("#createTimeEntryFrom #minutes").keyup(function(){
		if(this.value) $("#createTimeEntryFrom #minutes_error").html("");
	});
	$("#createTimeEntryFrom #cost").keyup(function(){
		if(this.value) $("#createTimeEntryFrom #cost_error").html("");
	});
	
	$("#createTimeEntryFrom #partyId").change(function(){
		var partyId = this.value;
		if(!partyId) $("#createTimeEntryFrom #partyId_error").html("Please select technician");
		else $("#createTimeEntryFrom #partyId_error").html("");
	});
	
	$("#updateTimeEntryFrom #rateTypeId").change(function(){
		var rateType = this.value;
		if(!rateType){
			$("#updateTimeEntryFrom #hours").val('');
			$("#updateTimeEntryFrom #minutes").val('');
			$("#updateTimeEntryFrom #cost").val('');
		}
	});
	
	function updateTimeEntry(timeEntryId){
		var rateTypeId = "";
		$.ajax({
		  async: false,
		  url:'getTimeEntry',
		  type:"POST",
		  data: {"timeEntryId":timeEntryId},
		  success: function(data){
			if (data.code == 200) {
				$("#updateTimeEntryFrom #hours").val(data.logHour);
				$("#updateTimeEntryFrom #minutes").val(data.logMinute);
				$("#updateTimeEntryFrom #timeEntryId").val(data.timeEntryId);
				$("#updateTimeEntryFrom #partyId").val(data.partyId);
				//$("#updateTimeEntryFrom #rateTypeId").val(data.rateTypeId);
				$("#updateTimeEntryFrom #techName").html(data.technicianName);
				$("#updateTimeEntryFrom #timeEntryDate").val(data.timeEntredDate);
				$("#updateTimeEntryFrom #comments").val(data.comments);
				
				rateTypeId = data.rateTypeId;
				$('#updateTimeEntryFrom #rateTypeId').dropdown('set selected', rateTypeId);
				$('#updateTimeEntryFrom #rateTypeId').dropdown('refresh');
				
				if("TOLL_CHARGE"== rateTypeId || "ANCILLARY_COST" == rateTypeId || "LEGACY_TOLL_CHARGE"== rateTypeId || "LEGACY_ANCILLARY_COST" == rateTypeId){
					$("#updateTimeEntryFrom #cost").val(data.cost);	
				}
			}
		  }
		});
		
		
		if("TOLL_CHARGE"== rateTypeId || "ANCILLARY_COST" == rateTypeId || "LEGACY_TOLL_CHARGE"== rateTypeId || "LEGACY_ANCILLARY_COST" == rateTypeId){
			$("#updateTimeEntryFrom #hoursMinutes").hide();
    		$("#updateTimeEntryFrom #costField").show();
		} else{
			$("#updateTimeEntryFrom #hoursMinutes").show();
    		$("#updateTimeEntryFrom #costField").hide();
		}
		
		$('#update-time-entry').modal('show');
    }
</script>

<script>
</script>