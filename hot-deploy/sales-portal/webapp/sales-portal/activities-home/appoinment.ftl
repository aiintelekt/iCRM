<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<#include "component://sr-portal/webapp/sr-portal/services/findCustomerModal.ftl"/>
<script type="text/javascript" src="/sales-portal-resource/js/ag-grid/addSalesActivity.js"></script>
<div class="row">
	<div id="main" role="main">
		 <#assign salesOpportunityId = '${requestParameters.salesOpportunityId!}' >
		 <#assign workEffortId = '${requestParameters.workEffortId!}' >
			<#assign cifNo = '${requestParameters.partyId!}' >
			<#if salesOpportunityId?has_content>
				<#assign roleList = (Static["org.ofbiz.entity.util.EntityQuery"].use(delegator).select("partyId","roleTypeId").from("SalesOpportunityRole").where("salesOpportunityId",salesOpportunityId).queryFirst())?if_exists />
				<#if roleList?has_content>
					<#assign partyId = "${roleList.partyId?if_exists}">
					<#assign partyIdtnList = (Static["org.ofbiz.entity.util.EntityQuery"].use(delegator).select("idValue").from("PartyIdentification").where("partyId",partyId).queryFirst())?if_exists />
					<#if partyIdtnList?has_content>
						<#assign cifNo = "${partyIdtnList.idValue?if_exists}" >	
					</#if>
				</#if>
        		<#assign extraLeft='
                            <a id="createProspect" title="Create Prospect" href="#" class="btn btn-primary btn-xs" onclick="#"><i class="fa fa-user-plus"></i> Create Prospect</a>
                            <a id="createNonCrm" title="Create Non CRM" href="#" class="btn btn-primary btn-xs" onclick="#"><i class="fa fa-user-times"></i>  Create Non CRM</a>
                           ' />
        	<#elseif workEffortId?has_content>
				<#assign roleList = (Static["org.ofbiz.entity.util.EntityQuery"].use(delegator).select("partyId","roleTypeId").from("WorkEffortPartyAssignment").where("workEffortId",workEffortId).queryFirst())?if_exists />
				<#if roleList?has_content>
					<#assign partyId = "${roleList.partyId?if_exists}">
					<#assign partyIdtnList = (Static["org.ofbiz.entity.util.EntityQuery"].use(delegator).select("idValue").from("PartyIdentification").where("partyId",partyId).queryFirst())?if_exists />
					<#if partyIdtnList?has_content>
						<#assign cifNo = "${partyIdtnList.idValue?if_exists}" >	
					</#if>
				</#if>
				<#assign extraLeft='
            					<a id="createProspect" title="Create Prospect" href="#" class="btn btn-primary btn-xs" onclick="#"><i class="fa fa-user-plus"></i> Create Prospect</a>
                            	<a id="createNonCrm" title="Create Non CRM" href="#" class="btn btn-primary btn-xs" onclick="#"><i class="fa fa-user-times"></i>  Create Non CRM</a>
                           	   ' 
            	/> 
        	<#else>
        		<#assign extraLeft='
                           	<a id="findcustomerSr" title="Find Customer" href="#" class="btn btn-primary btn-xs" data-toggle="modal" data-target="#findcustomer" ><i class="fa fa-search"></i> Find Customer</a>
                            <a id="createProspect" title="Create Prospect" href="#" class="btn btn-primary btn-xs" onclick="#"><i class="fa fa-user-plus"></i> Create Prospect</a>
                            <a id="createNonCrm" title="Create Non CRM" href="#" class="btn btn-primary btn-xs" onclick="#"><i class="fa fa-user-times"></i>  Create Non CRM</a>
                           ' />
        	</#if>
       		<@sectionFrameHeader  title="${uiLabelMap.addAppoinments}"  extraLeft=extraLeft  />
       		<div class="card-head margin-adj mt-2" id="cp">
                  ${screens.render("component://sales-portal/widget/opportunity/OpportunityScreens.xml#cusNameCommon")}
            </div> 
            <div class="card-head margin-adj mt-2">
                    ${screens.render("component://sales-portal/widget/opportunity/OpportunityScreens.xml#customerRowAddOpportunity")}
            </div>
       		<div class="col-lg-12 col-md-12 col-sm-12">
       			<@pageSectionHeader title="${uiLabelMap.activityDetails}"/>
       		</div>
       		<form method="post" action="<#if (parameters.salesOpportunityId)?has_content>addAppointmentEvent<#else>salesAddAppointmentEvent</#if>" id="SalesAppointment" class="form-horizontal" name="SalesAppointment" novalidate="novalidate" data-toggle="validator">
            	<input type="hidden" name="salesOpportunityId" value="${(parameters.salesOpportunityId)!}"/>
            	<input type="hidden" name="domainEntityType" value="${(parameters.domainEntityType)!}"/>
        		<input type="hidden" name="domainEntityId" value="${(parameters.domainEntityId)!}"/>
        		<div class="col-lg-12 col-md-12 col-sm-12">
            		<div class="row p-2">
                		<div class="col-md-12 col-lg-6 col-sm-12 ">
                	   		<@inputHidden name="cNo" id="cNo" value = "${cifNo!}"/>
                	   		<@inputHidden name="ownerBu" id="ownerBu" />
                            <#assign srType = EntityQuery.use(delegator).from("WorkEffortAssocTriplet").where("entityName", "Activity", "type", "Type", "value", "Appointment", "active", "Y").queryFirst()! />
                            <@inputHidden id="srTypeId" value="${(srType.code)!}"/>
                            <@inputHidden id="workEffortTypeId" value="${(srType.value)!}"/>
                            <@displayCell
                                label="Type"
                                value="${(srType.value)!}"
                            />
                            <@dropdownCell
                                id="srSubTypeId"
                                label="Sub Type"
                                required=true
                                allowEmpty=true
                                placeholder="Please Select"
                            />
                            <#assign activityPriorities = delegator.findByAnd("Enumeration", {"enumTypeId" : "PRIORITY_LEVEL","enumService","Activities","enumEntity","Activities"}, null, false)>
					        <#assign activityPrioritiesList = Static["org.fio.admin.portal.util.DataHelper"].getDropDownOptions(activityPriorities, "enumCode","description")?if_exists />
					        <@dropdownCell 
					        	id="priority"
					            label="Priority"
					            options=activityPrioritiesList!
					            allowEmpty=true
					            value="${requestParameters.priority?if_exists}"
					            placeholder = "Please Select"
					        />  
                            <#assign nowTimestamp = Static["org.ofbiz.base.util.UtilDateTime"].nowTimestamp()>
                            <#assign today = Static["org.ofbiz.base.util.UtilDateTime"].toDateString(nowTimestamp?if_exists, "dd/MM/yyyy")?if_exists/>
                            <@inputDate id="startTime" label="Start Date Time" placeholder="Date" type="datetime" required=true value=today/>
							<#assign durationList = Static["org.fio.admin.portal.util.DataUtil"].toLinkedMap("1H","1 Hour","2H","2 Hours") />
                            <@dropdownCell
                                id="duration"
                                placeholder="Please select"
                                label="Duration"
                                allowEmpty=true
                                options=durationList!
                                required=true
                                />
                            <@inputRow id="subject" name="subject" label="Subject" placeholder="Subject"/>
                            <@checkbox  id="onceDone" label="Once and Done" value="Y"/>    
                        </div>

                        <div class="col-md-12 col-lg-6 col-sm-12 ">
                			<#assign userDetails = Static["org.ofbiz.entity.util.EntityQuery"].use(delegator).select("userLoginId","firstName").from("UserLoginPerson").where("statusId","PARTY_ENABLED").queryList()?if_exists />    
                            <#assign usersOptionList = Static["org.fio.admin.portal.util.DataHelper"].getDropDownOptions(userDetails, "userLoginId", "firstName")?if_exists />
            
                        <@dropdownCell
                                id="owner"
                                label="Owner"
                                options=usersOptionList!
                                allowEmpty=true
                                placeholder="Please Select"
                                />
                               <@inputRow    
				                label="${uiLabelMap.OwnerBU}"
				                id="ownerBuDesc"
				                value=""
				                readonly=true
                                />
                             <#if salesOpportunityId?has_content>
				               		<@displayCell
                                		label="Linked From"
                                		value="${salesOpportunityId!}"
                            		/>
				               <#else>
				               		<@dropdownCell
                                		id="linkedFrom"
                                		label="Linked From"
                                		allowEmpty=true
                                		placeholder="Please Select"
                                	/>
				               </#if>
                           <@inputRow id="requiredAttendees" name="requiredAttendees" label="Required Attendees"/>
                       		<@inputRow id="optionalAttendees" name="optionalAttendees" label="Optional Attendees "/>
                        <@inputRow
                               id="location"
                               name="location"
                               label="Location"
                               placeholder="Location"
                               />
                        </div>
                        <div class="col-md-12 col-lg-12 col-sm-12 ">
         					<@textareaLarge  label="Description" id="messages" rows="3"/>
      					</div>
                        <div class="offset-md-2 col-sm-10">
                        	<@formButton
                            	btn1type="submit"
                                btn1label="${uiLabelMap.Save}"
                                btn1onclick="return formSubmission();"
                                btn2=true
                              	btn2onclick = "resetForm()"
                                btn2type="reset"
                                btn2label="${uiLabelMap.Clear}"
                            />
                        </div>
                      </div>
                 </div>
            </form>
         </div>
    </div>
<script>
	$(function() {
		$("#owner").change(function() {
			var owner  = $("#owner").val(); 
			if(owner != undefined && owner != null)
			   	getBusinessUnit(owner);	    
		});
		$("#findcustomerSr").click(function() {
			loadAgGrid();
		});
	});
		
	function getBusinessUnit(owner) {
		var owner = owner;
	    $.ajax({
				type: "POST",
		        url: "getBusinessUnitName",
		        async: false,
		         data: { "owner": owner },
		        success: function(data) {
		            result=data;
		            if(result && result[0] != undefined && result[0].businessId != undefined){
		            	$("#ownerBu").val(result[0].businessId);
		            	$("#ownerBuDesc").val(result[0].businessunitName);
		            }else{
		            	$("#ownerBu").val("");
		            	$("#ownerBuDesc").val("");
		            }
		        },error: function(data) {
		        	result=data;
					showAlert("error", "Error occured while fetching Business Unit");
				}
		});
	}
		
	function formSubmission(){
    	var valid = true;
    	if($('#cNo').val() == ""){
	 		showAlert('error','Please select Customer');
	 		valid = false;
	 	}
	 	return valid;
	}
	
</script>