<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<#include "component://crm/webapp/crm/account/accountModels.ftl">
<#assign tabId= request.getParameter("tabId")!>
<#assign accountId= request.getParameter("accountPartyId")!>
<#assign leadPartyId= request.getParameter("leadPartyId")!>
<div class="row">
    <div id="main" role="main">
    	<@sectionFrameHeader title="Create Contact" />        
		<div class="col-md-12 col-lg-12 col-sm-12 ">
        <#if tabId?exists && "${tabId}"=="account">
          <form method="post" action="assocAcctWithContact" id="createContactForm" class="form-horizontal" name="createContactForm" novalidate="novalidate" data-toggle="validator">
          <input type="hidden" name="tabId" value="${tabId?if_exists}"/>
          <input type="hidden" name="activeTab" value="contact" />
          <input type="hidden" name="donePage" value="viewAccount"/>
          <input type="hidden" name="leadPartyId" value="viewAccount"/>
        <#elseif tabId?exists && "${tabId}"=="lead">
       	  <form method="post" action="assocContactWithLead" id="createContactForm" class="form-horizontal" name="createContactForm" novalidate="novalidate" data-toggle="validator">
          <input type="hidden" name="tabId" value="${tabId?if_exists}"/>
          <input type="hidden" name="activeTab" value="contact" />
          <input type="hidden" name="donePage" value="viewLead"/>
          <input type="hidden" name="leadPartyId" value="${leadPartyId?if_exists}"/>
        <#else>
          <form method="post" action="storeContact" id="createContactForm" class="form-horizontal" name="createContactForm" novalidate="novalidate" data-toggle="validator">
        </#if>
		  <div class="row p-2">
          <div class="col-md-6 col-sm-6">
            <@inputRow 
			         id="firstName"
		             label=uiLabelMap.firstName
			         placeholder=uiLabelMap.firstName
			         value=""
			         required=true
			         maxlength="100"
			         />
			<#-- <@generalInput 
					id="personalTitle"
					label=uiLabelMap.salutation
					placeholder=uiLabelMap.salutation
					value=""
					required=false
					maxlength="100"
					/> -->
			<@dropdownCell 
				id = "personalTitle"
				label = uiLabelMap.salutation
				options = salutationList
				value = ""
				allowEmpty=false
				dataLiveSearch = true
				/>
			<#--<@generalInput 
					id="generalProfTitle"
					label=uiLabelMap.title
					placeholder=uiLabelMap.title
					value=""
					required=false
					maxlength="100"
					/>-->
			<@inputRow
	               id="departmentName"
					label=uiLabelMap.department
					placeholder=uiLabelMap.department
					value=""
					required=false	
					maxlength="100"
				/>
        <#-- <div class="form-group row row">
              <label  class="col-sm-4 col-form-label">${uiLabelMap.title}</label>
              <div class="col-sm-7">
                <input type="text" class="form-control input-sm" name ="generalProfTitle" id="generalProfTitle" placeholder="Title">
              </div>
            </div> -->
             
             <@dropdownCell 
				id = "gender"
				label = uiLabelMap.gender
				options = genderList
				value = ""
				allowEmpty=true
				dataLiveSearch = true
				/>
			
            <#--
            <div class="form-group row row">
              <label  class="col-sm-4 col-form-label">${uiLabelMap.gender}</label>
              <#assign classifications = delegator.findAll("PartyClassificationGroup",false)/>
               <div class="col-sm-7">
	              <select name="partyClassificationGroupId" id="partyClassificationGroupId" class="ui dropdown form-control input-sm" data-live-search="true" >
	                <option value="">Select Classifications</option>
	               <#list classifications as PartyClassificationGroup>
	                 <option value="${PartyClassificationGroup.partyClassificationGroupId?if_exists}"  <#if requestParameters.partyClassificationGroupId?if_exists = PartyClassificationGroup.partyClassificationGroupId>selected<#elseif partyClassification?exists && partyClassification?has_content> <#if partyClassification.partyClassificationGroupId==PartyClassificationGroup.partyClassificationGroupId>selected</#if> </#if> >${PartyClassificationGroup.description?if_exists}</option>
	               </#list>
	              </select>
               </div>
            </div> -->
            
             <#--<div class="form-group row row">
              <label  class="col-sm-4 col-form-label">Initial Account</label>
              <div class="col-sm-7">
               <#assign accParty = delegator.findByAnd("PartyRole", {"roleTypeId" : "ACCOUNT"}, [], false)>
               <select class="form-control input-sm" name="accountPartyId" id="accountPartyId">
                <option value="" disabled selected>Select Account</option>
                <#if !accParty?has_content>
                <option value="_NA_">N/A</option>
                <#else>
                <#list accParty as accParty>
                <#assign defaultStates = delegator.findOne("PartyGroup", {"partyId" : accParty?if_exists.partyId?if_exists}, true)>
                <option  value="${defaultStates?if_exists.partyId?if_exists}" <#if "${accountId?if_exists}"="${defaultStates?if_exists.partyId?if_exists}" >selected </#if> >${defaultStates?if_exists.groupName?if_exists}</option>
               </#list>
              </#if>
                </select>
             </div>
            </div>-->
            <#assign acctId = ""/>
            <#assign acctDisable = false/>
            <#if accountId?exists && accountId?has_content>
            <#assign defaultAcct = delegator.findOne("PartyGroup", {"partyId" : accountId?if_exists}, false)?if_exists>
            <#if defaultAcct?exists && defaultAcct?has_content>
                <#assign acctId = "${accountId?if_exists}"/>
                <#assign acctDisable = true/>
            </#if>
            </#if>
            <@inputRowAddOn 
               id="parentPartyId"
               name="accountPartyId"
               label="Initial Account"
               placeholder="Initial Account"
               value=acctId
               addOnTarget="parentAccountModal"
               required=false
               disabled=acctDisable
               glyphiconClass="glyphicon-search"
               />
            
            <@inputArea
			 		id="description"
			 		label=uiLabelMap.description
			 		rows="3"
			 		placeholder = "Description"
					value = ""
					required = false
			    />
           <#-- <div class="form-group row">
            <label for="${uiLabelMap.description}" class="col-sm-4 col-form-label">${uiLabelMap.description}</label>
            <div class="col-sm-7">
              <textarea name="description" id="description" rows="3" placeholder="Description" class="form-control" ></textarea>
            </div>
          </div>-->
          
          </div>
          <div class="col-md-6 col-sm-6">
           <@inputRow 
					id="lastName"
					label=uiLabelMap.lastName
					placeholder=uiLabelMap.lastName
					value=""
					required=false
					maxlength="100"
				/>
            <#--<div class="form-group row row has-error">
              <label for="inputEmail38" class="col-sm-4 col-form-label">${uiLabelMap.lastName}</label>
              <div class="col-sm-7">
                <input type="text" class="form-control input-sm" id="lastName" name="lastName"  placeholder="Last Name" required>
                <div class="help-block with-errors"></div>
              </div>
            </div> -->
            <div class="form-group row">
                  <label for="${uiLabelMap.birthDate}" class="col-sm-4 col-form-label">${uiLabelMap.birthDate}</label>
                  <div class="col-sm-7">
                     <div class="input-group date" id="datetimepicker7">
                        <input type='text' class="form-control input-sm" placeholder="YYYY-MM-DD" data-date-format="YYYY-MM-DD" id="birthDate" name="birthDate"/>
                        <span class="input-group-addon"><span class="glyphicon glyphicon-calendar"></span>
                        </span>
                     </div>
                  </div>
               </div>
            
            <#-- <div class="form-group row row">
              <label  class="col-sm-4 col-form-label">${uiLabelMap.department}</label>
              <div class="col-sm-7">
                <input type="text" class="form-control input-sm" name="departmentName" id="departmentName" placeholder="Department">
              </div>
            </div>-->
            
            <#-- <div class="form-group row">
                  <label for="${uiLabelMap.preferredCurrency}" class="col-sm-4 col-form-label">${uiLabelMap.preferredCurrency}</label>
                  <div class="col-sm-7">
                     <@inputCurrency  
                     name="currencyUomId"
                     value=""
                     defaultCourrencyUom=true
                     dataLiveSearch=true
                     required=false
                     />
                  </div>
            </div> -->
            
            <#--<div class="form-group row row">
              <label  class="col-sm-4 col-form-label">${uiLabelMap.preferredCurrency}</label>
              <div class="col-sm-7">
            <#assign currencies = delegator.findByAnd("Uom",{"uomTypeId","CURRENCY_MEASURE"},Static["org.ofbiz.base.util.UtilMisc"].toList("abbreviation"), false)?if_exists/>
            <#if isMultitenant?exists && isMultitenant = "Y">
              <#assign defaultCourrencyUom = delegator.findOne("TenantProperties",Static["org.ofbiz.base.util.UtilMisc"].toMap("resourceName","crmsfa","propertyName","defaultCurrencyUomId"), false)?if_exists/>
            <#else>
              <#assign defaultCourrencyUom = delegator.findOne("PartyAttribute",Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId","Company","attrName","defaultCurrencyUomId"), false)?if_exists/>
            </#if>
            <#if defaultCourrencyUom?has_content && isMultitenant?if_exists = "Y">
              <#assign defaultCourrencyUomId = defaultCourrencyUom.propertyValue?if_exists/>
            <#elseif defaultCourrencyUom?has_content && isMultitenant?if_exists = "N">
              <#assign defaultCourrencyUomId = defaultCourrencyUom.attrValue?if_exists/>
            <#else>
              <#assign defaultCourrencyUomId = (Static["org.ofbiz.base.util.UtilProperties"].getPropertyValue("crmsfa", "defaultCurrencyUomId"))?default("USD")/>
            </#if>
            <#assign requestURI = request.getRequestURI()/>
              <select class="ui dropdown form-control input-sm" data-live-search="true" id="preferredCurrencyUomId" name="preferredCurrencyUomId">
                <option value="">Select Preferred Currency</option>
                <#list currencies as currency>
                  <#if requestURI.contains("updateAccountForm")>
                    <option value="${currency.uomId}" <#if currencyUomId?if_exists == currency.uomId>selected</#if>>${currency.description}</option>
                  <#else>
                    <option value="${currency.uomId}" <#if requestParameters.currencyUomId?if_exists == currency.uomId>selected <#elseif defaultCourrencyUomId?if_exists == currency.uomId>selected</#if>>${currency.description}</option>
                  </#if>
                </#list>
              </select>
            </div>
            </div>-->
           <#-- <#assign marketingCampainList  = delegator.findByAnd("MarketingCampaign",Static["org.ofbiz.base.util.UtilMisc"].toMap("statusId","MKTG_CAMP_INPROGRESS"),marketingCampainList ,false)/>
            <div class="form-group row row">
              <label  class="col-sm-4 col-form-label">${uiLabelMap.marketingCampaign}</label>
              <div class="col-sm-7">
                <select class="ui dropdown form-control input-sm" data-live-search="true" name="marketingCampaignId" id="marketingCampaignId" >
                <option value="">Select Marketing Campaign</option>
                 <#if marketingCampainList?has_content>
                  <#list marketingCampainList as marketingCampain>
                     <option value="${marketingCampain.marketingCampaignId}" <#if requestParameters.marketingCampaignId?if_exists = marketingCampain.marketingCampaignId?if_exists>selected</#if>>${marketingCampain.campaignName}</option>
                  </#list>
                 </#if>
                </select>
              </div>
            </div> -->
            
            <@inputArea
			 		id="importantNote"
			 		label=uiLabelMap.note
			 		rows="3"
			 		placeholder = "Note"
					value = ""
					required = false
				/>
           <#--  <div class="form-group row row">
              <label  class="col-sm-4 col-form-label">${uiLabelMap.note}</label>
              <div class="col-sm-7">
                <textarea name="importantNote" id="importantNote" rows="3" placeholder="Note" class="form-control" ></textarea>
              </div>
            </div>-->
          </div>
          </div>
          <div class="clearfix"> </div>
          <@pageSectionHeader title=uiLabelMap.contactInformation class ="col-12 px-0 border-b mb-2" />
          <div class="row padding-r" style="display:none;" id="emailPhoneError">
          	 <div class="col-md-6 col-sm-6">
          		 <div class="form-group row">
				   <label class="col-sm-4 col-form-label"></label>
				   <div class="col-sm-7">
				 	 <div class="help-block with-errors list-unstyled" id="email_phone_error"></div>
				   </div>
			  	 </div>
			 </div>
		  </div>
		  <div class="row padding-r">
          <div class="col-md-6 col-sm-6">
          	  
	          <div class="form-group row">
				  <label class="col-sm-4 col-form-label" for="mobileNumber">${uiLabelMap.mobileNumber}</label>
				  <div class="col-sm-7 row">
				   	  <div class="col-sm-3">
				   	     <input type="tel" class="form-control tooltips"  id="primaryPhoneCountryCode" name="primaryPhoneCountryCode" pattern="([+]?\d{1,2})" data-error="Please enter a valid Country Code" data-original-title="Country Code" autocomplete="off"  value="" maxlength="3" />
				   	  </div>
				   	  -
				      <div class="col-sm-5">
				         <input type="tel" class="form-control tooltips" id="primaryPhoneNumber" name="primaryPhoneNumber" pattern="(\d*)" data-error="Enter a valid Mobile Number" data-original-title="Mobile Number" autocomplete="off"  value="" maxlength="10">
				      </div>
				      <div class="col-sm-8">
				      	<div class="help-block with-errors" id="phone_error"></div>
				      </div>
			      </div>
			  </div>
          
          <#--  <@generalInput
	                id="countryCode"
					label=uiLabelMap.phoneNumber
					placeholder="Counrty Code"
					value=""
					required=false	
					maxlength="3"
					inputType= "number"
				/>
			<@inputRow
	                id="primaryPhoneNumber"
					label=uiLabelMap.mobileNumber
					placeholder="Mobile Number"
					value=""
					required=false	
					maxlength="10"
					type= "number"
				/> -->
            <#-- <div class="form-group row row">
              <label  class="col-sm-4 col-form-label">${uiLabelMap.phoneNumber}</label>
              <div class="col-sm-7">
                <input name="primaryPhoneNumber" id="primaryPhoneNumber" placeholder="(000)000-0000" class="form-control input-sm" type="text" value="">
              </div>
            </div> -->
           <#assign emailPattern = "^[a-zA-Z0-9._-]+@[a-zA-Z0-9-_]+(?:.[a-zA-Z]{2,3})*$" />
           <@inputRow
	                id="primaryEmail"
					label=uiLabelMap.email
					placeholder="example@company.com"
					value=""
					type="email"
					required=false
					dataError="Please enter valid email address"
				/>
            <#-- <div class="form-group row row">
              <label  class="col-sm-4 col-form-label">${uiLabelMap.email}</label>
              <div class="col-sm-7">
                <input type="email" class="form-control input-sm" name="primaryEmail" id="primaryEmail" data-error="Please enter valid email" placeholder="example@company.com">
              <div class="help-block with-errors"></div>
              </div>
            </div> -->
          </div>
          <div class="col-md-6 col-sm-6">
          <@inputRow
	                id="primaryPhoneAskForName"
					label=uiLabelMap.personToAskFor
					placeholder=uiLabelMap.personToAskFor
					value=""
					required=false	
					maxlength="60"
				/>
            <#-- <div class="form-group row row">
              <label  class="col-sm-4 col-form-label">${uiLabelMap.personToAskFor}</label>
              <div class="col-sm-7">
                <input type="text" class="form-control input-sm" name ="primaryPhoneAskForName" id="primaryPhoneAskForName" placeholder="Person Name">
              </div>
            </div>-->
          <#--   <@generalInput
	                id="primaryWebUrl"
					label=uiLabelMap.webURL
					placeholder="http://domain.com"
					value=""
					required=false	
					maxlength="150"
				/>  -->
            <#-- <div class="form-group row row">
              <label  class="col-sm-4 col-form-label">${uiLabelMap.webURL}</label>
              <div class="col-sm-7">
                <input type="text" class="form-control input-sm" name="primaryWebUrl" id="primaryWebUrl" placeholder="http://domain.com">
              </div>
            </div>-->
          </div>
          </div>
          <div class="clearfix"> </div>
            <@pageSectionHeader title=uiLabelMap.primaryAddress class ="col-12 px-0 border-b mb-2" />
			<div class="row padding-r">
          <div class="col-md-6 col-sm-6">
           <@inputRow
	                id="generalToName"
					label=uiLabelMap.toName
					placeholder="To Name"
					value=""
					required=false	
					maxlength="100"
				/>
             <@inputRow
	                id="generalAddress1"
					label=uiLabelMap.address1
					placeholder="Address Line 1"
					value=""
					required=false	
					maxlength="255"
				/>
				<@inputRow
	                id="generalCity"
					label=uiLabelMap.city
					placeholder="City"
					value=""
					required=false	
					maxlength="100"
				/>
            <#--<div class="form-group row row has-error">
              <label  class="col-sm-4 col-form-label">${uiLabelMap.address1}</label>
              <div class="col-sm-7">
                <input type="text" class="form-control input-sm" name="generalAddress1" id="generalAddress1" placeholder="Address Line 1" required>
                <div class="help-block with-errors"></div>
              </div>
            </div>
            <div class="form-group row row has-error">
              <label  class="col-sm-4 col-form-label">${uiLabelMap.city}</label>
              <div class="col-sm-7">
                <input type="text" class="form-control input-sm" name="generalCity" id="generalCity" placeholder="City" required>
                <div class="help-block with-errors"></div>
              </div>
            </div>-->
            <@inputRow
	                id="generalPostalCode"
					label=uiLabelMap.postalCode
					placeholder=uiLabelMap.postalCode
					value=""
					required=false	
					maxlength="60"
				/>
            <#-- <div class="form-group row row has-error">
              <label  class="col-sm-4 col-form-label">${uiLabelMap.postalCode}</label>
              <div class="col-sm-7">
                <input type="text" class="form-control input-sm" name="generalPostalCode" id="generalPostalCode" placeholder="Postal Code" required>
                <div class="help-block with-errors"></div>
              </div>
            </div>-->
            <div class="form-group row row">
              <label  class="col-sm-4 col-form-label">${uiLabelMap.postalCodeExt}</label>
              <div class="col-sm-7">
                <input type="text" class="form-control input-sm" name="generalPostalCodeExt" id="generalPostalCodeExt" placeholder="Zip/Postal Code Extension">
              </div>
            </div>
          </div>
          <div class="col-md-6 col-sm-6">
            <div class="form-group row row">
              <label  class="col-sm-4 col-form-label">${uiLabelMap.attentionName}</label>
              <div class="col-sm-7">
                <input type="text" class="form-control input-sm" name="generalAttnName" id="generalAttnName" placeholder="Attention Name	">
              </div>
            </div>
            <div class="form-group row row">
              <label  class="col-sm-4 col-form-label">${uiLabelMap.address2}</label>
              <div class="col-sm-7">
                <input type="text" class="form-control input-sm" name="generalAddress2" id="generalAddress2" placeholder="Address Line 2">
              </div>
            </div>
            <div class="form-group row row">
              <label  class="col-sm-4 col-form-label">${uiLabelMap.country}</label>
              <div class="col-sm-7">
                <@inputCountry  
                     name="generalCountryGeoId"
                     defaultCountry=false
                     dataLiveSearch=true
                     required=false
                     />
             </div>
            </div>
            <div class="form-group row row">
              <label  class="col-sm-4 col-form-label">${uiLabelMap.state}</label>
              <div class="col-sm-7">
              <@inputState 
                     name="generalStateProvinceGeoId"
                     dataLiveSearch=true
                     required=false
                     />
                <div class="help-block with-errors"></div>
              </div>
            </div>
          </div>
          </div>
          <div class="col-md-12 col-sm-12">
            <div class="form-group row row">
              <div class="offset-sm-2 col-sm-9">
                <button type="button" class="btn btn-sm btn-primary mt-2 disabled" onclick="javascript:return onSubmitValidate(this);">Submit </button>
                <button type="reset" class="btn btn-sm btn-secondary mt-2">Clear</button>
              </div>
            </div>
          </div>
        </form>
        </div>
      </div>
    </div>
    </div><#-- End main-->
</div><#-- End row-->
<script>
	$( "#generalPostalCode" ).keyup(function() {
	       clearPostalCodeErr();
	   });
	$("#generalStateProvinceGeoId, #generalCountryGeoId").change(function(){
	   clearPostalCodeErr();
	});
	function clearPostalCodeErr(){
      var postalCodeErr = $("#generalPostalCode_error").val();
      var postalCode = $("#generalPostalCode").val();
      $("#postalCodeErr").html("");
      if(postalCode !=" " || postalCode !=null){
          var country = $('select#generalCountryGeoId option:selected').val();
          var state = $('select#generalStateProvinceGeoId option:selected').val();
          if(country != null && country != "" && state != null && state != "" ){
           if(country=="CAN" || country=="USA"){
           if(country=="CAN"){
            if(postalCode.length > 7 || postalCode.length < 7 ){
              $("#generalPostalCode_error").append('<ul class="list-unstyled"><li>Postal Code Length Should be 7</li></ul>');
              return false;
            }else{
              $("#generalPostalCode_error").html("");
            }
           }
           else if(country=="USA"){
             if(postalCode.length > 5 || postalCode.length < 5){
               var postalCodeErr = $( "div#generalPostalCode_error" ).text();
               if(postalCodeErr != "") {
               }else{
                 $("#generalPostalCode_error").append('<ul class="list-unstyled"><li>Postal Code Length Should be 5</li></ul>');
               }
               return false;
             }else{
              $("#generalPostalCode_error").html("");
            } 
           }
         }
         }
         if(postalCodeErr !="" || postalCodeErr !=null){
           $("#generalPostalCode_error").html("");
         }
      }
   }
   
   function onSubmitValidate() {
	 clearPostalCodeErr();
	 var phoneNumber = $('#primaryPhoneNumber').val();
	 var primaryEmail = $('#primaryEmail').val();
	//|| (primaryEmail != null && primaryEmail != "" && primaryEmail != "undefined")
	 if((phoneNumber != null && phoneNumber != "" && phoneNumber != "undefined") || (primaryEmail != null && primaryEmail != "" && primaryEmail != "undefined")) {
	 	 $('#email_phone_error').html("");
	 	 $('#emailPhoneError').hide();
	 	 $('#createContactForm').submit();
	 } else{
	 	$('#emailPhoneError').show();
	 	$('#email_phone_error').html("Please enter either phone number or email");
	 }
	
   }
</script>
   <!-- /.container -->
