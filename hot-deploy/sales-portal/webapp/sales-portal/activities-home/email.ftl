<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<#include "component://sales-portal/webapp/sales-portal/activities-home/modalForActivity.ftl">
<#include "component://sr-portal/webapp/sr-portal/services/findCustomerModal.ftl"/>
<script src="/bootstrap/js/ckeditor/ckeditor.js" type="text/javascript"></script>
<script src="/bootstrap/js/ckeditor/ck-custom-functions.js" type="text/javascript"></script>
<script type="text/javascript" src="/account-portal-resource/js/emailActivity.js"></script>

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
            <@sectionFrameHeader   title="${uiLabelMap.addEmail}"  extraLeft=extraLeft  />
            
            <#assign addActivities = '
            <div class="dropdown-menu" aria-labelledby="E10007">
                <h4>Add Activities</h4>
                <a class="dropdown-item" href="/sales-portal/control/addTask?partyId=${partyId?if_exists}&domainEntityType=${domainEntityType!}&domainEntityId=${domainEntityId!}&externalLoginKey=${requestAttributes.externalLoginKey!}" target="_blank"><i class="fa fa-clipboard" aria-hidden="true"></i> Task</a> 
			    <a class="dropdown-item" href="/sales-portal/control/addPhoneCall?partyId=${partyId?if_exists}&domainEntityType=${domainEntityType!}&domainEntityId=${domainEntityId!}&externalLoginKey=${requestAttributes.externalLoginKey!}" target="_blank"><i class="fa fa-phone" aria-hidden="true"></i> Phone Call</a>
			    <a class="dropdown-item" href="addEmail?partyId=${partyId?if_exists}&domainEntityType=${domainEntityType!}&domainEntityId=${domainEntityId!}&externalLoginKey=${requestAttributes.externalLoginKey!}" target="_blank"><i class="fa fa-envelope" aria-hidden="true"></i> Email</a>
			    <a class="dropdown-item" href="/sales-portal/control/addAppointment?partyId=${partyId?if_exists}&domainEntityType=${domainEntityType!}&domainEntityId=${domainEntityId!}&externalLoginKey=${requestAttributes.externalLoginKey!}" target="_blank"><i class="fa fa-calendar" aria-hidden="true"></i> Appointment</a>
			    <a class="dropdown-item" href="/sales-portal/control/addOthers?partyId=${partyId?if_exists}&domainEntityType=${domainEntityType!}&domainEntityId=${domainEntityId!}&externalLoginKey=${requestAttributes.externalLoginKey!}" target="_blank"><i class="fa fa-plus-square" aria-hidden="true"></i> Others</a>
            </div>
            ' />
            <#assign toggleDropDownData = {"E10007":addActivities!} />
            <div class="card-head margin-adj mt-2">
                <@AppBar  
	                appBarId="ACTION_APP_BAR"
	                appBarTypeId="ACTION"
	                id="appbar1"
	                extra=extra!
	                toggleDropDownData=toggleDropDownData!
	                isEnableUserPreference=true
	                />
            </div>
            <div class="card-head margin-adj mt-2">
                <@AppBar
	                appBarId="ACCOUNT_KPI_BAR"
	                appBarTypeId="KPI"
	                id="appbar2"
	                isEnableUserPreference=true
	                />
            </div>
            <#-- Basic information -->
            <div class="card-header mt-3" >
           		<@dynaScreen 
	                instanceId="ACCT_BASIC_INFO"
	                modeOfAction="VIEW"
	            />
           </div>
            <div class="col-lg-12 col-md-12 col-sm-12">
               <@pageSectionHeader title="Activity Details"/>
            </div>

            <form method="post" action="<#if (parameters.salesOpportunityId)?has_content>addSalesEmailEvent<#else>addEmailEvent</#if>" id="SrEmail" class="form-horizontal" name="phone" novalidate="novalidate" data-toggle="validator" enctype="multipart/form-data">
                <input type="hidden" name="salesOpportunityId" value="${(parameters.salesOpportunityId)!}"/>
                <input type="hidden" name="domainEntityType" value="${(parameters.domainEntityType)!}"/>
        		<input type="hidden" name="domainEntityId" value="${(parameters.domainEntityId)!}"/>
                <div class="col-lg-12 col-md-12 col-sm-12">
                    <div class="row p-2">
                        <div class="col-md-12 col-lg-6 col-sm-12 ">
                            <@inputHidden name="cNo" id="cNo" value = "${cifNo!}"/>
                            <@inputHidden name="ownerBu" id="ownerBu" />
                            <#assign srType = EntityQuery.use(delegator).from("WorkEffortAssocTriplet").where("entityName", "Activity", "type", "Type", "value", "E-mail", "active", "Y").queryFirst()! />
                            <@inputHidden id="srTypeId" value="${(srType.code)!}"/>
                            <@inputHidden id="workEffortTypeId" value="${(srType.value)!}"/>
                            <@displayCell
                                label="Type"
                                value="Email"
                            />
                            <@dropdownCell
                                id="srSubTypeId"
                                label="Sub Type"
                                required=true
                                allowEmpty=true
                                placeholder="Please Select"
                                value="${requestParameters.srSubTypeId!}"
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
                            <@inputDate id="date" label="Sent / Received Date" placeholder="Date" required=false value="${requestParameters.date!}"/>
                            <@inputRow id="subject" name="subject" label="Subject" placeholder="Subject" value="${requestParameters.subject!}"/>
                            
                            <#assign templates = delegator.findByAnd("TemplateMaster", {"templateType" : "EMAIL_BLAST"}, null, false)>
                            <#assign templatesList = Static["org.fio.admin.portal.util.DataHelper"].getDropDownOptions(templates, "templateId","templateName")?if_exists />
                            <@dropdownCell
                                id="emailTemplate"
                                label="Template"
                                placeholder="Please Select"
                                required=false
                                allowEmpty=true
                                options=templatesList!
                             />
                            <@checkbox id="onceDone" label="Once and Done" value="Y"/> 
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
				                label="Owner BU"
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
								<#assign senderEmailId=""/>
								<#assign pretailParam = EntityQuery.use(delegator).from("SystemProperty").where("systemResourceId", "NOTIFICATION", "systemPropertyId", "from").queryOne()! />
								<#if pretailParam?exists && pretailParam?has_content>
									<#assign senderEmailId = pretailParam.systemPropertyValue!>
								</#if>
								<@inputRow id="nsender" label="From" value="${senderEmailId!}"/>

                               <@inputRow id="nto" label="To" value=""/>
                               <@inputRow id="ncc" label="Cc" placeholder="Cc"/>
                               <@inputRow id="nbcc" label="Bcc" placeholder="Bcc"/>

                        </div>
                    </div>

                    <div class="row p-2">
                        <div class="col-md-12 col-lg-12 col-sm-12" id="emlContent">
                        <@textareaLarge
			               id="emailContent"
			               groupId = "htmlDisplay"
			               label=uiLabelMap.html
			               rows="3"
			               value = template
			               required = false
			               txareaClass = "ckeditor"
			               />
                        	<script>
							    CKEDITOR.replace( 'emailContent',{
							    	customConfig : '/bootstrap/js/ckeditor/ck-custom-config.js',
									autoGrow_minHeight : CKEditorUtil.autoGrowMinHeight,
									removePlugins : CKEditorUtil.removePlugins
						        });
							</script>
                        </div>
                    </div>
                    
                    <div class="row padding-r">
                    	<div class="col-md-6 col-sm-6">
                    	<@inputRowFilePicker 
						id="attachment"
						label="Attachments"
						placeholder="Select Attachment"
						/>
                    	</div>
                    </div>

                    <div class="row">
                        <div class="form-group 1">
                           <div class="text-left ml-3">
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