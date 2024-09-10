<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<div class="row">
    <div id="main" role="main">
        <@sectionFrameHeader title="${uiLabelMap.CreateCustomer!}" />        
        <div class="col-md-12 col-lg-12 col-sm-12 ">
            <form method="post" action="createCustomerService" id="createCustomerForm" class="form-horizontal" name="createCustomerForm" novalidate="novalidate" data-toggle="validator" onsubmit="javascript:return onSubmitValidate(this);">
                <@pageSectionHeader 
                title="${uiLabelMap.BasicInformation}"
                class="border-b mb-2"
                />
                <div class="row padding-r">
                    <div class="col-md-6 col-sm-6">
                        <@inputRow 
	                        id="firstName"
	                        label=uiLabelMap.firstName!
	                        placeholder=uiLabelMap.firstName!
	                        value="${requestParameters.firstName?if_exists}"
	                        dataError="Please enter first name"
	                        required=true
	                        maxlength="100"
	                        />
	              		<#assign salutationEnum = Static["org.ofbiz.entity.util.EntityQuery"].use(delegator).select("enumId","description").from("Enumeration").where("enumTypeId","SALUTATION").orderBy("sequenceId").queryList()?if_exists />    
	                    <#assign salutationList = Static["org.fio.admin.portal.util.DataUtil"].getMapFromGeneric(salutationEnum,"enumId","description",false)?if_exists />
                        <@dropdownCell 
	                        id = "personalTitle"
	                        label = uiLabelMap.salutation!
	                        placeholder = uiLabelMap.salutation!
	                        options = salutationList
	                        value = "${requestParameters.personalTitle?if_exists}"
	                        allowEmpty=false
	                        dataLiveSearch = true
	                        />
                        <@inputRow
	                        id="departmentName"
	                        label=uiLabelMap.department!
	                        placeholder=uiLabelMap.department!
	                        value="${requestParameters.departmentName?if_exists}"
	                        required=false	
	                        maxlength="100"
	                        />
	                    <#assign genderEnum = Static["org.ofbiz.entity.util.EntityQuery"].use(delegator).select("enumCode","description").from("Enumeration").where("enumTypeId","GENDER").orderBy("sequenceId").queryList()?if_exists />    
	                    <#assign genderList = Static["org.fio.admin.portal.util.DataUtil"].getMapFromGeneric(genderEnum,"enumCode","description",false)?if_exists />
                        <@dropdownCell 
	                        id = "gender"
	                        label = uiLabelMap.gender!
	                        placeholder= uiLabelMap.gender!
	                        options = genderList
	                        value = "${requestParameters.gender?if_exists}"
	                        allowEmpty=true
	                        dataLiveSearch = true
	                        />
                        <@inputArea
	                        id="description"
	                        label=uiLabelMap.description!
	                        rows="3"
	                        placeholder=uiLabelMap.description!
	                        value = "${requestParameters.description?if_exists}"
	                        required = false
	                        />
                    </div>
                    <div class="col-md-6 col-sm-6">
                        <@inputRow 
	                        id="lastName"
	                        label=uiLabelMap.lastName!
	                        placeholder=uiLabelMap.lastName!
	                        value="${requestParameters.lastName?if_exists}"
	                        required=false
	                        maxlength="100"
	                        />
                        <@inputDate
	                        id="birthDate"
	                        type="date"
	                        value="${requestParameters.birthDate?if_exists}"
	                        label=uiLabelMap.birthDate!
	                        placeholder=uiLabelMap.birthDate!
	                        />
                        <@inputArea
	                        id="importantNote"
	                        label=uiLabelMap.note!
	                        rows="3"
	                        placeholder=uiLabelMap.note!
	                        value = "${requestParameters.importantNote?if_exists}"
	                        required = false
	                        />
                    </div>
                </div>
                <div class="clearfix"> </div>
                <@pageSectionHeader 
	                title="${uiLabelMap.contactInformation}"
	                />
                <div class="row padding-r">
                    <div class="col-md-6 col-sm-6">
                        <@errorRow 
	                        id="email_phone"
	                        />
                        <@inputPhoneRow
	                        label=uiLabelMap.mobileNumber!
	                        />
	                    <@inputHidden 
                    		id="phonePurposeTypeId"
                    		value="PHONE_HOME"
                    		/>
                        <#assign emailPattern = "^[a-zA-Z0-9._-]+@[a-zA-Z0-9-_]+(?:.[a-zA-Z]{2,3})*$" />
                        <@inputRow
	                        id="primaryEmail"
	                        label=uiLabelMap.email!
	                        placeholder="example@company.com"
	                        value=""
	                        type="email"
	                        required=false
	                        dataError="Please enter valid email address"
	                        />
	                   <@inputHidden 
                    		id="emailPurposeTypeId"
                    		value="PRIMARY_EMAIL"
                    		/>
                    </div>
                    <div class="col-md-6 col-sm-6">
                        <@inputRow
	                        id="primaryPhoneAskForName"
	                        label=uiLabelMap.personToAskFor!
	                        placeholder=uiLabelMap.personToAskFor!
	                        value=""
	                        required=false	
	                        maxlength="60"
	                        />
                    </div>
                </div>
                <div class="clearfix"> </div>
                <@pageSectionHeader 
	                title="${uiLabelMap.primaryAddress}"
	                />
                <div class="clearfix"> </div>
                <div class="row padding-r">
                    <div class="col-md-6 col-sm-6">
                    	<@inputHidden 
                    		id="postalPurposeTypeId"
                    		value="PRIMARY_LOCATION"
                    		/>
                        <@inputRow
	                        id="generalToName"
	                        label=uiLabelMap.toName!
	                        placeholder=uiLabelMap.toName!
	                        value=""
	                        required=false	
	                        maxlength="100"
	                        />
                        <@inputRow
	                        id="generalAddress1"
	                        label=uiLabelMap.address1!
	                        placeholder=uiLabelMap.address1!
	                        value=""
	                        required=false	
	                        maxlength="255"
	                        />
                        <@inputRow
	                        id="generalCity"
	                        label=uiLabelMap.city!
	                        placeholder=uiLabelMap.city!
	                        value=""
	                        required=false	
	                        maxlength="100"
	                        />
                        <@inputRow
	                        id="generalPostalCode"
	                        label=uiLabelMap.postalCode!
	                        placeholder=uiLabelMap.postalCode!
	                        value=""
	                        required=false	
	                        maxlength="60"
	                        />
                        <@inputRow
	                        id="generalPostalCodeExt"
	                        label=uiLabelMap.postalCodeExt!
	                        placeholder=uiLabelMap.postalCodeExt!
	                        value=""
	                        required=false	
	                        maxlength="100"
	                        />
                    </div>
                    <div class="col-md-6 col-sm-6">
                        <@inputRow
	                        id="generalAttnName"
	                        label=uiLabelMap.attentionName!
	                        placeholder=uiLabelMap.attentionName!
	                        value=""
	                        required=false	
	                        maxlength="100"
	                        />
                        <@inputRow
	                        id="generalAddress2"
	                        label=uiLabelMap.address2!
	                        placeholder=uiLabelMap.address2!
	                        value=""
	                        required=false	
	                        maxlength="100"
	                        />
                        <@inputCountry
	                        name="generalCountryGeoId"
	                        defaultCountry=false
	                        label=uiLabelMap.CountryOrRegion!
	                        stateValue=""
	                        />
                        <@inputState
	                        name="generalStateProvinceGeoId"
	                        label=uiLabelMap.StateOrProvince!
	                        />
                    </div>
                </div>
                <div class="form-group row">
                    <div class="offset-sm-2 col-sm-9">
                       <@submit
                    		label="${uiLabelMap.Save!}"
                    		/>
                    	<@reset
                    		label="${uiLabelMap.Clear!}"
                    		/>
                    </div>
                </div>
            </form>
        </div>
    </div><#-- End main-->
</div><#-- End row-->
<script type="text/javascript" src="/crm-resource/js/customer/create-customer.js"></script>