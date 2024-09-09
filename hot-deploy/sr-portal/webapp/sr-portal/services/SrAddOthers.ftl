<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<#include "component://sr-portal/webapp/sr-portal/services/findCustomerModal.ftl"/>
<#include "component://sales-portal/webapp/sales-portal/activities-home/modalForActivity.ftl">
<script type="text/javascript" src="/sr-portal-resource/js/services/addSrActivity.js"></script>
<div class="top-band bg-white mb-0">
</div>

<div class="row">
	<div id="main" role="main">
    	<#assign workEfforts = (Static["org.ofbiz.entity.util.EntityQuery"].use(delegator).select("workEffortId").from("WorkEffort").where("workEffortTypeId","85028").maxRows(5).orderBy("-estimatedStartDate").queryList())?if_exists />
        <#assign workEffortIds = Static["org.ofbiz.entity.util.EntityUtil"].getFieldListFromEntityList(workEfforts, "workEffortId", true)>
        <#assign extraLeftActivity=''/>
        <#list workEffortIds as workEffortId>
        	<#assign extraLeftActivity= '${extraLeftActivity}' + '<a id=task1 title="${workEffortId}" href="#" class="btn btn-primary btn-xs" onclick="#"> ${workEffortId}</a>'/>
     	</#list>
        <div class="top-band bg-light">
			<div class="col-lg-12 col-md-12 col-sm-12">
				<div class="row">
					<div class="col-lg-12 col-md-12 col-sm-12">
						<h1 class="float-left mr-2 mb-0">Add Others</h1>
						<div class="text-left ml-3">
							<a id="findcustomerSr" title="Find Customer" href="#"
								class="btn btn-primary btn-xs" data-toggle="modal"
								data-target="#findcustomer"><i
								class="fa fa-search"></i> Find Customer</a> <a id="createProspect"
								title="Create Prospect" href="#new-prospect"
								class="btn btn-primary btn-xs"><i class="fa fa-user-plus"></i>
								Create Prospect</a> <a id="createNonCrm" title="Create Non CRM"
								href="#" class="btn btn-primary btn-xs" data-toggle="modal"
								data-target="#createNonCrmCustomer" onclick="#"><i
								class="fa fa-user-times"></i> Create Non CRM</a>
						</div>
					</div>
				</div>
			</div>
		</div>
		<div class="card-head margin-adj mt-0 d-none">
            ${screens.render("component://sr-portal/widget/services/ServicesScreens.xml#CustomerForAddServiceRequest")}
        </div>
        <div class="col-lg-12 col-md-12 col-sm-12">
           <@pageSectionHeader title="Activity Details"/>
        </div>

      	<form method="post" action="<#if (parameters.srNumber)?has_content>addSRothersEvent<#else>srAddothersEvent</#if>" id="SrOthers" class="form-horizontal" name="srothers" novalidate="novalidate" data-toggle="validator">
        	<input type="hidden" name="custRequestId" value="${(parameters.srNumber)!}"/>
        	<div class="col-lg-12 col-md-12 col-sm-12">
            	<div class="row p-2">
                	<div class="col-md-12 col-lg-6 col-sm-12 ">
                    	<@inputHidden  id="cNo" value=""/>
                    	<#assign srType = EntityQuery.use(delegator).from("WorkEffortAssocTriplet").where("entityName", "Activity", "type", "Type", "value", "Others", "active", "Y").queryFirst()! />
                    	<@inputHidden id="srTypeId" value="${(srType.code)!}"/>
                    	<@inputHidden id="workEffortTypeId" value="${(srType.value)!}"/>
                    	<@displayCell
                    		label="Type"
                    		value="${(srType.value)!}"
                    	/>
                        <@dropdownCell
                        	id="srSubTypeId"
                            label="Sub Type"
                            placeholder="Sub Type"
                            required=false
                            allowEmpty=true
                    	/>
                        <#assign nowTimestamp = Static["org.ofbiz.base.util.UtilDateTime"].nowTimestamp()>
                        <#assign today = Static["org.ofbiz.base.util.UtilDateTime"].toDateString(nowTimestamp?if_exists, "dd/MM/yyyy")?if_exists/>
                        <@inputDate id="taskDate" label="Date" placeholder="Date" value=today required=false/>
                        <@inputRow id="subject" name="subject" label="Subject" placeholder="Subject"/>
                        <@inputRowAddOn
                        	id="location"
                           	name="location"
                           	label="Location"
                           	placeholder="Location"
                           	addOnTarget="location"
                           	required=false
                           	glyphiconClass="glyphicon-search"
                       	/>
                       <@inputRow id="duration" label="Duration" placeholder="Duration"/>
                    </div>
                    <div class="col-md-12 col-lg-6 col-sm-12 ">
            			<#assign products = delegator.findByAnd("Product", {"productTypeId" : "SERVICE_PRODUCT"}, null, false)!>
          				<#assign productsList = Static["org.fio.admin.portal.util.DataHelper"].getDropDownOptions(products, "productId","productName")?if_exists />
                       	<@dropdownCell
                        	id="productId"
                            label="Product Name"
                            options=productsList!
                            required=false
                            allowEmpty=true
                            placeholder="Please Select"
                       	/>

                        <@inputRow id="account" label="Account #" placeholder="Account No"/>
                        <@inputRow id="accountProduct" label="Account Product" placeholder="Account Product"/>
                        <@inputRow 
                        	id="linkedFrom" 
                        	label="Linked From" 
                        	placeholder="Linked From"
                            value="${(parameters.srNumber)!}"
                      	/>

                    	<@inputRow id="resolution" label="Resolution" placeholder="Resolution"/>
                        <@checkbox  id="onceDone" label="Once and Done" value="Y"/>
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
        	</div>
    	</form>
	</div>
</div>
    
<script>
	$(function() {
		$("#findcustomerSr").click(function() {
			loadAgGrid();
		});
	});
	function formSubmission(){
    	var valid = true;
    	if($('#cNo').val() == ""){
	 		showAlert('error','Please select Customer');
	 		valid = false;
	 	}
	 	return valid;
	 }
</script>