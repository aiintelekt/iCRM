<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>

<#assign partyId= request.getParameter("partyId")!>
<#assign nowDateTime = Static["org.ofbiz.base.util.UtilDateTime"].nowTimestamp() />
<#-- 
<#if nowDateTime?has_content>
	<#assign nowDateTime = nowDateTime />
</#if>
-->

<#if partyId?exists && partyId?has_content>

<#assign requestUri = request.getParameter("requestUri")!request.getRequestURI()/>

<#if requestUri.contains("viewLead")>
<#assign requestURI = "viewLead"/>
<#elseif requestUri.contains("viewContact")>
<#assign requestURI = "viewContact"/>
<#elseif requestUri.contains("viewAccount")>
<#assign requestURI = "viewAccount"/>
<#elseif requestUri.contains("viewCustomer")>
<#assign requestURI = "viewCustomer"/>
</#if>

<!-- Email Address -->
<div id="createEMAILcontactInfo" class="modal fade" role="dialog">
   <div class="modal-dialog modal-lg">
      <!-- Modal content-->
      <form method="post" action="<@ofbizUrl>createEmailAddress#${tabIdForCurrentTab!}</@ofbizUrl>" id="createEmailAddress" class="form-horizontal" name="createEmailAddress" data-toggle="validator">
         <input type="hidden" name="activeTab" value="contactInfo" />
         <input type="hidden" name="donePage" value='${requestURI?if_exists}'/>
         <input type="hidden" name="contactMechTypeId" value="EMAIL_ADDRESS">
         <input type="hidden" name="partyId" value="${partyId?if_exists}">
         <div class="modal-content">
            <div class="modal-header">
               <h4 class="modal-title">${uiLabelMap.createEmail}</h4>
               <button type="reset" class="close" data-dismiss="modal">&times;</button>
            </div>
            <div class="modal-body poplabel-left">
            
               <#assign contactMechTPEmail = delegator.findByAnd("ContactMechTypePurpose", {"contactMechTypeId" : "EMAIL_ADDRESS"}, [], false)>
               <div class="form-group row">
                  <label  class="col-sm-4 field-text">${uiLabelMap.CommonPurpose} <span class="text-danger"> &#42;</span></label> 
                  <div class="col-sm-8">
                      <select name="contactMechPurposeTypeId" id="contactMechPurposeTypeId" class="ui dropdown form-control input-sm" >-->
                      <option value="" data-content="<span class='nonselect'>Please Select</span>" selected>Please Select</option>
                        <#if contactMechTPEmail?exists && contactMechTPEmail?has_content>
                        <#list contactMechTPEmail as contactMechTPEmailGV>
                           <#assign contactMechPTEmail = delegator.findOne("ContactMechPurposeType", {"contactMechPurposeTypeId" : contactMechTPEmailGV.contactMechPurposeTypeId}, false)>
                           <#if contactMechPTEmail?exists && contactMechPTEmail?has_content>
                             <option value="${contactMechPTEmail.contactMechPurposeTypeId}" <#if ("${contactMechPTEmail.contactMechPurposeTypeId}"="IDEAL_EMAIL_ADDRESS" || "${contactMechPTEmail.contactMechPurposeTypeId}"="AOS_EMAIL_ADDRESS")> disabled data-content="<span class='nonselect'>${contactMechPTEmail.description}</span>" </#if>>${contactMechPTEmail.description}</option>
                           </#if>
                        </#list>
                        </#if>
                     </select>
                     <div class="help-block with-errors" id="contactMechPurposeTypeId_error"></div>
                  </div>
               </div>
               
               <@inputRow 
               id="emailAddress"
               name="emailAddress"
               label=uiLabelMap.email
               placeholder=uiLabelMap.emailPlaceholder
               value=""
               required=true
               />
               <@dropdownCell 
               id="allowSolicitation"
               name="allowSolicitation"
               label=uiLabelMap.allowSolicitation
               options=yesNoOptions
               required=false
               allowEmpty=true
               dataLiveSearch=true
               />
               <@dropdownCell 
               id="emailValidInd"
               name="emailValidInd"
               label=uiLabelMap.deliverable
               options=yesNoOptions
               required=false
               allowEmpty=false
               dataLiveSearch=true
               />
               <div class="col-md-12 col-sm-12">
                  <div class="form-group row">
                     <div class="offset-sm-4 col-sm-9">
                        <input type="button" class="btn btn-sm btn-primary navbar-dark mt" value="${uiLabelMap.Save}" onclick ="formMailSubmission(createEmailAddress.contactMechPurposeTypeId,createEmailAddress.emailAddress);"/>
                        	<@reset
	                            id="reset"
	                            label="${uiLabelMap.Reset}"
	                           />
                     </div>
                  </div>
               </div>
            </div>
         </div>
      </form>
   </div>
</div>
<!-- Email Address End-->

<!--Postal Address [start]-->
<style>
 #scroll-1 {
  height: 600px;
  overflow-y: scroll;
  overflow-x: hidden;
}
</style>
<div id="createPOSTALcontactInfo" class="modal fade" role="dialog">
   <div class="modal-dialog modal-lg" >
      <form method="post" action="<@ofbizUrl>createPostalAddress#${tabIdForCurrentTab!}</@ofbizUrl>" id="createPostalAddress" 
      class="form-horizontal" name="createPostalAddress"  data-toggle="validator" onSubmit = "return formSubmissionCreate();">
         <input type="hidden" name="activeTab" value="contactInfo" />
         <input type="hidden" name="donePage" value='${requestURI?if_exists}'/>
         <input type="hidden" name="contactMechTypeId" value="POSTAL_ADDRESS">
         <input type="hidden" name="partyId" value="${partyId?if_exists}">
         <input type="hidden" id="groupName" name="groupName" value="${partySummary.groupName?if_exists}"/>
         <input type="hidden" id="firstName" name="firstName" value="${partySummary.firstName?if_exists}"/>
         <input type="hidden" id="lastName" name="lastName" value="${partySummary.lastName?if_exists}"/>
         <input type="hidden" id="accType" name="accType" value="${accType?if_exists}"/>
         
         <input type="hidden" name="isBusiness" value="">
         <input type="hidden" name="isVacant" value="">
         <input type="hidden" name="isUspsAddrVerified" value="">
         
         <div class="modal-content">
            <div class="modal-header">
               <h4 class="modal-title">${uiLabelMap.createPostalAddress}</h4>
               <button type="reset" class="close" data-dismiss="modal">&times;</button>
            </div>
            <div class="" id="scroll-1">
            <div class="modal-body poplabel-left">
               <!-- Modal content-->
               <#assign contactMechTPPostal = delegator.findByAnd("ContactMechTypePurpose", {"contactMechTypeId" : "POSTAL_ADDRESS"}, [], false)>
               <div class="form-group row">
               <label  class="col-sm-4 field-text">${uiLabelMap.CommonPurpose} <span class="text-danger"> &#42;</span></label> 
                  <div class="col-sm-8">
                     <select name="contactMechPurposeTypeId" id="contactMechPurposeTypeId" class="ui dropdown form-control input-sm" >
                         <option value="" data-content="<span class='nonselect'>Please Select</span>" selected>Please Select</option>
                        <#if contactMechTPPostal?exists && contactMechTPPostal?has_content>
                        <#list contactMechTPPostal as contactMechTPPostalGV>
                           <#assign contactMechPTPostal = delegator.findOne("ContactMechPurposeType", {"contactMechPurposeTypeId" : contactMechTPPostalGV.contactMechPurposeTypeId}, false)>
                           <#if contactMechPTPostal?exists && contactMechPTPostal?has_content>
                             <option value="${contactMechPTPostal.contactMechPurposeTypeId}" <#if "${contactMechPTPostal.contactMechPurposeTypeId}"="IDEAL_MAILING_ADDRES"> disabled data-content="<span class='nonselect'>${contactMechPTPostal.description}</span>" </#if> >${contactMechPTPostal.description}</option>
                           </#if>
                        </#list>
                        </#if>
                     </select>
                     <div class="help-block with-errors" id="contactMechPurposeTypeId_error"></div>
                  </div>
               </div>
               <@inputRow 
               id="address1"
               name="address1"
               label=uiLabelMap.address1
               placeholder=uiLabelMap.address1
               value=""
               required=false
               />
               <@inputRow 
               id="address2"
               name="address2"
               label=uiLabelMap.address2
               placeholder=uiLabelMap.address2
               value=""
               required=false
               />
               
             <#--  <div class="form-group row">
                    <label class="col-sm-4 field-text">${uiLabelMap.country}</label>
                  <div class="col-sm-7">-->
                     <@inputCountry  
                     label=uiLabelMap.country
                     name="countryGeoId"
                     defaultCountry=true
                     dataLiveSearch=true
                     tooltip=uiLabelMap.country
                     required= true
                     dataError = "Please fill out the field"
                     />
                    <#--  <div class="help-block with-errors"></div>
                  </div>
               </div> -->
               <div class="form-group row">
                  <label class="col-sm-4 field-text">${uiLabelMap.state}</label>
                  <div class="col-sm-8">
                     <@inputState 
                     name="stateProvinceGeoId"
                     dataLiveSearch=true
                     tooltip=uiLabelMap.State
                     required=false
                     />
                     <div class="help-block with-errors"></div>
                  </div>
               </div>
               <@inputRow 
               id="postalCode"
               name="postalCode"
               class = "createpostalCode"
               label=uiLabelMap.postalCode
               placeholder=uiLabelMap.postalCode
               value=""
               required=true
               />
               <@inputRow 
               id="postalCodeExt"
               name="postalCodeExt"
               label=uiLabelMap.postalCodeExt
               placeholder=uiLabelMap.postalCodeExt
               value=""
               required=false
               />
               
               <@inputAutoComplete
					id="city"
					label=uiLabelMap.city
					isAutoCompleteEnable="Y"
					onkeydown=true
					autoCompleteMinLength=0
					placeholder=uiLabelMap.city
					autoCompleteLabelFieldId="city"
					autoCompleteValFieldId="city"
					autoCompleteFormId="zipcodeassoc-search-form"
					autoCompleteUrl="/uiadv-portal/control/searchZipCodeAssocs"
				/>
               
               <#-- 
               <@dropdownCell 
               id="city"
               name="city"
               label=uiLabelMap.city
               placeholder=uiLabelMap.city
               required=false
               allowEmpty=true
               dataLiveSearch=true
               />
                -->
                
               <@dropdownCell 
               id="countyGeoId"
               name="county"
               label=uiLabelMap.county
               placeholder=uiLabelMap.county
               required=false
               allowEmpty=true
               dataLiveSearch=true
               />
               
               <#-- 
               <@inputRow 
               id="city"
               name="city"
               label=uiLabelMap.city
               placeholder=uiLabelMap.city
               value=""
               required=false
               />
                -->
               
               <@dropdownCell 
               id="allowSolicitation"
               name="allowSolicitation"
               label=uiLabelMap.allowSolicitation
               options=yesNoOptions
               required=false
               allowEmpty=true
               dataLiveSearch=true
               />
               <@dropdownCell 
               id="addressValidInd"
               name="addressValidInd"
               label=uiLabelMap.deliverable
               options=yesNoOptions
               required=false
               allowEmpty=false
               dataLiveSearch=true
               />
			   <@inputDateTime 
				   id="solicitChangeDt"
				   label="Solicitation Change Date"
				   value=""
				   dateFormat="MM/DD/YYYY"
				   />
               <@inputRow 
	               id="ip"
	               name="ip"
	               label=uiLabelMap.IP!
	               placeholder=uiLabelMap.IP!
	               value=""
	               required=false
	               />
               <@inputRow 
	               id="device"
	               name="device"
	               label=uiLabelMap.Device!
	               placeholder=uiLabelMap.Device!
	               value=""
	               required=false
	               />
               <div class="col-md-12 col-sm-12">
                  <div class="form-group row">
                     <div class="offset-sm-4 col-sm-9">
                       <#--  <@submit class="btn btn-sm btn-primary navbar-dark mt" label="${uiLabelMap.Save}"/>-->
                         <input type="button" class="btn btn-sm btn-primary navbar-dark mt" value="${uiLabelMap.Save}" onclick ="formPAddressSubmission(createPostalAddress.postalCode,createPostalAddress.contactMechPurposeTypeId);"/>
                        <@reset
	                            id="reset"
	                            label="${uiLabelMap.Reset}"
	                           />
	                    </div>        
                     </div>
                  </div>
               </div>
            </div>
         </div>
      </form>
   </div>
</div>
<!--Postal Address [End]-->

<!-- Telecom Number start-->
<div id="createTELECOMcontactInfo" class="modal fade" role="dialog">
   <div class="modal-dialog modal-lg">
      <form method="post" action="<@ofbizUrl>createTelecomNumber#${tabIdForCurrentTab!}</@ofbizUrl>" id="createTelecomNumber" class="form-horizontal" name="createTelecomNumber" novalidate="novalidate" data-toggle="validator">
         <input type="hidden" name="activeTab" value="contactInfo" />
         <input type="hidden" name="donePage" value='${requestURI?if_exists}'/>
         <input type="hidden" name="contactMechTypeId" value="TELECOM_NUMBER">
         <input type="hidden" name="partyId" value="${partyId?if_exists}">
         <div class="modal-content">
            <div class="modal-header">
               <h4 class="modal-title">${uiLabelMap.createPhoneNumber}</h4>
               <button type="reset" class="close" data-dismiss="modal">&times;</button>
            </div>
            <div class="modal-body poplabel-left">
            <#assign contactMechTPTelecom = delegator.findByAnd("ContactMechTypePurpose", {"contactMechTypeId" : "TELECOM_NUMBER"}, [], false)>
               <div class="form-group row">
                  <label  class="col-sm-4 field-text">${uiLabelMap.CommonPurpose} <span class="text-danger"> &#42;</span></label> 
                  <div class="col-sm-8">
                     <select name="contactMechPurposeTypeId" id="contactMechPurposeTypeId" class="ui dropdown form-control input-sm" >
                        <option value="" data-content="<span class='nonselect'>Please Select</span>" selected>Please Select</option>
                        <#if contactMechTPTelecom?exists && contactMechTPTelecom?has_content>
                        <#list contactMechTPTelecom as contactMechTPTelecomGV>
                           <#assign contactMechPTTelecom = delegator.findOne("ContactMechPurposeType", {"contactMechPurposeTypeId" : contactMechTPTelecomGV.contactMechPurposeTypeId}, false)>
                           <#if contactMechPTTelecom?exists && contactMechPTTelecom?has_content>
                              <option value="${contactMechPTTelecom.contactMechPurposeTypeId}" <#if ("${contactMechPTTelecom.contactMechPurposeTypeId}"="IDEAL_MOBILE_PHONE" || "${contactMechPTTelecom.contactMechPurposeTypeId}"="AOS_MOBILE_PHONE") || ("${contactMechPTTelecom.contactMechPurposeTypeId}" = "IDEAL_PRIMARY_PHONE") > disabled data-content="<span class='nonselect'>${contactMechPTTelecom.description}</span>" </#if> >
                                  ${contactMechPTTelecom.description} 
                              </option>
                           </#if>
                        </#list>
                        </#if>
                     </select>
                     <div class="help-block with-errors" id="contactMechPurposeTypeId_error"></div>
                  </div>
               </div>
               <#assign teleCode = Static["org.fio.homeapps.util.DataUtil"].getCountryTeleCode(delegator, "") />
               <#assign countryCode="">
               <#if teleCode?has_content>
                   <#assign countryCode=teleCode?if_exists>
               </#if>
               
               <@inputRow 
	               id="countryCode${numberCnt!}"
	               name="countryCode"
	               label='Country Code'
	               placeholder='Country Code'
           		   value="${countryCode?if_exists}"
	               pattern="^(\\d{1,4})$"
               	   maxlength=4
	              />
               <@inputRow 
               id="contactNumberss"
               name="contactNumber"
               label=uiLabelMap.phoneNumber
               placeholder=uiLabelMap.phoneNumber
               value=""
               required=true
               />
               <@inputRow 
               id="extension"
               name="extension"
               label=uiLabelMap.extension
               placeholder=uiLabelMap.extension
               value=""
               pattern="^[0-9]+$"
               maxlength=5
               />
               <@inputRow 
               id="askForName"
               name="askForName"
               label=uiLabelMap.personToAskFor
               placeholder=uiLabelMap.personToAskFor
               value=""
               required=false
               />
               <@dropdownCell 
               id="allowSolicitation"
               name="allowSolicitation"
               label=uiLabelMap.allowSolicitation
               options=yesNoOptions
               required=false
               allowEmpty=true
               dataLiveSearch=true
               />
               <@dropdownCell 
               id="phoneValidInd"
               name="phoneValidInd"
               label=uiLabelMap.deliverable
               options=yesNoOptions
               required=false
               allowEmpty=false
               dataLiveSearch=true
               />
               <div class="col-md-12 col-sm-12">
                  <div class="form-group row">
                     <div class="offset-sm-4 col-sm-9">
                       <input type="button" class="btn btn-sm btn-primary navbar-dark mt" value="${uiLabelMap.Save}" onclick ="formTelecomNumberSubmission(createTelecomNumber.contactMechPurposeTypeId,createTelecomNumber.contactNumberss);"/>
                        <@reset
	                            id="reset"
	                            label="${uiLabelMap.Reset}"
	                           />
                     </div>
                  </div>
               </div>
            </div>
         </div>
      </form>
   </div>
</div>
<!-- Telecom Number end-->

<!-- Web Address start -->
<div id="createWEBcontactInfo" class="modal fade" role="dialog">
   <div class="modal-dialog modal-lg">
      <form method="post" action="<@ofbizUrl>createContactMech#${tabIdForCurrentTab!}</@ofbizUrl>" id="createContactMech" class="form-horizontal" name="createContactMech" novalidate="novalidate" data-toggle="validator">
         <input type="hidden" name="activeTab" value="contactInfo" />
         <input type="hidden" name="donePage" value='${requestURI?if_exists}'/>
         <input type="hidden" name="contactMechTypeId" value="WEB_ADDRESS">
         <input type="hidden" name="partyId" value="${partyId?if_exists}">
         <div class="modal-content">
            <div class="modal-header">
               <h4 class="modal-title">${uiLabelMap.createWebAddress}</h4>
               <button type="reset" class="close" data-dismiss="modal">&times;</button>
            </div>
            <div class="modal-body poplabel-left">
               <#assign contactMechTPWeb = delegator.findByAnd("ContactMechTypePurpose", {"contactMechTypeId" : "WEB_ADDRESS"}, [], false)>
               <div class="form-group row">
                  <label  class="col-sm-4 field-text">${uiLabelMap.CommonPurpose} <span class="text-danger"> &#42;</span></label> 
                  <div class="col-sm-8">
                     <select name="contactMechPurposeTypeId" id="contactMechPurposeTypeId" class="ui dropdown form-control input-sm" >
                        <option value="" data-content="<span class='nonselect'>Please Select</span>" selected>Please Select</option>
                        <#if contactMechTPWeb?exists && contactMechTPWeb?has_content>
                        <#list contactMechTPWeb as contactMechTPWebGV>
                           <#assign contactMechPTWeb = delegator.findOne("ContactMechPurposeType", {"contactMechPurposeTypeId" : contactMechTPWebGV.contactMechPurposeTypeId}, false)>
                           <#if contactMechPTWeb?exists && contactMechPTWeb?has_content>
                              <option value="${contactMechPTWeb.contactMechPurposeTypeId}">${contactMechPTWeb.description}</option>
                           </#if>
                        </#list>
                        </#if>
                     </select>
                     <div class="help-block with-errors" id="contactMechPurposeTypeId_error"></div>
                  </div>
               </div>
               <@inputRow 
               id="infoString"
               name="infoString"
               label=uiLabelMap.webURL
               placeholder=uiLabelMap.webURL
               value=""
               required=true
               />
               <@dropdownCell 
               id="allowSolicitation"
               name="allowSolicitation"
               label=uiLabelMap.allowSolicitation
               options=yesNoOptions
               required=false
               allowEmpty=true
               dataLiveSearch=true
               />
               <div class="col-md-12 col-sm-12">
                  <div class="form-group row">
                     <div class="offset-sm-4 col-sm-9">
                        <@submit class="btn btn-sm btn-primary navbar-dark mt" label="${uiLabelMap.Save}" onclick="return formWeb();"/>
                        <@reset
	                            id="reset"
	                            label="${uiLabelMap.Reset}"
	                           />
                     </div>
                  </div> 
               </div>
            </div>
         </div>
      </form>
   </div>
</div>
<!-- Web Address end -->

<!-- Social Media Type start -->
<div id="createSocialMediacontactInfo" class="modal fade" role="dialog">
   <div class="modal-dialog modal-lg">
      <form method="post" action="<@ofbizUrl>createSocialMediaTypeWebm#${tabIdForCurrentTab!}</@ofbizUrl>" id="createSocialMediaTypeWebm" class="form-horizontal" name="createSocialMediaTypeWebm" novalidate="novalidate" data-toggle="validator">
         <input type="hidden" name="activeTab" value="contactInfo" />
         <input type="hidden" name="donePage" value='${requestURI?if_exists}'/>
         <input type="hidden" name="contactMechTypeId" value="SOCIAL_MEDIA_TYPE">
         <input type="hidden" name="partyId" value="${partyId?if_exists}">
         <div class="modal-content">
            <div class="modal-header">
               <h4 class="modal-title">${uiLabelMap.createSocialMediaType}</h4>
               <button type="reset" class="close" data-dismiss="modal">&times;</button>
            </div>
            <div class="modal-body poplabel-left">
               <#assign contactMechTPMediaType = delegator.findByAnd("ContactMechTypePurpose", {"contactMechTypeId" : "SOCIAL_MEDIA_TYPE"}, [], false)>
               <div class="form-group row">
                  <label  class="col-sm-4 field-text">${uiLabelMap.CommonPurpose}<span class="text-danger"> &#42;</span></label> 
                  <div class="col-sm-8">
                     <select name="contactMechPurposeTypeId" id="contactMechPurposeTypeId" class="ui dropdown form-control input-sm" >
                        <option value="" data-content="<span class='nonselect'>Please Select</span>" selected>Please Select</option>
                        <#if contactMechTPMediaType?exists && contactMechTPMediaType?has_content>
                        <#list contactMechTPMediaType as contactMechTPMediaTypeGV>
                           <#assign contactMechPTMediaType = delegator.findOne("ContactMechPurposeType", {"contactMechPurposeTypeId" : contactMechTPMediaTypeGV.contactMechPurposeTypeId}, false)>
                           <#if contactMechPTMediaType?exists && contactMechPTMediaType?has_content>
                              <option value="${contactMechPTMediaType.contactMechPurposeTypeId}">${contactMechPTMediaType.description}</option>
                           </#if>
                        </#list>
                        </#if>
                     </select>
                     <div class="help-block with-errors" id="contactMechPurposeTypeId_error"></div>
                  </div>
               </div>
               <@inputRow 
               id="socialMediaId"
               name="socialMediaId"
               label=uiLabelMap.socialMediaTypeId
               placeholder=uiLabelMap.socialMediaTypeId
               value=""
               required=true
               />
               <@dropdownCell 
               id="allowSolicitation"
               name="allowSolicitation"
               label=uiLabelMap.allowSolicitation
               options=yesNoOptions
               required=false
               allowEmpty=true
               dataLiveSearch=true
               />
               <div class="col-md-12 col-sm-12">
                  <div class="form-group row">
                     <div class="offset-sm-4 col-sm-9">
                        <input type="submit" class="btn btn-sm btn-primary navbar-dark mt" value="${uiLabelMap.Save}" onclick="return formSocialMedia();"/>
                        <@reset
	                            id="reset"
	                            label="${uiLabelMap.Reset}"
	                           />
                     </div>
                  </div>
               </div>
            </div>
         </div>
      </form>
   </div>
</div>
<!-- Social Media Type end -->

</#if>

				<div id="createEmailAddressModal" class="modal fade" role="dialog">
		            <div class="modal-dialog modal-md">
				     <div class="modal-dialog">
				      <div class="modal-content">
		            	<div class="modal-header">
		            	<span id="EDC_message"></span>
			               <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
			            	</div>
				            <div>
				            </div>
		            		<div class="modal-footer">
				              <input type="submit" class="btn btn-sm btn-primary navbar-dark"  value="Yes" onclick="createEmailAddress.submit();">
				              <input type="button" class="btn btn-sm btn-primary navbar-dark" data-dismiss="modal" value="No" onclick="return false;">
					        </div>
					      </div>
					      </div>
					   </div>
					</div>
					
				 <div id="createTelecomNumberModal" class="modal fade" role="dialog">
		            <div class="modal-dialog modal-md">
				     <div class="modal-dialog">
				      <div class="modal-content">
		            	<div class="modal-header">
		            	<span id="TNC_message"></span>
			               <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
			            	</div>
				            <div>
				            </div>
		            		<div class="modal-footer">
				              <input type="submit" class="btn btn-sm btn-primary navbar-dark"  value="Yes" onclick="createTelecomNumber.submit();">
				              <input type="button" class="btn btn-sm btn-primary navbar-dark" data-dismiss="modal" value="No" onclick="return false;">
					        </div>
					      </div>
					      </div>
					   </div>
					</div>
					
				<div id="createPostalAddressModal" class="modal fade" role="dialog">
		            <div class="modal-dialog modal-md">
				     <div class="modal-dialog">
				      <div class="modal-content">
		            	<div class="modal-header">
		            	<span id="ADC_message"></span>
			               <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
			            	</div>
				            <div>
				            </div>
		            		<div class="modal-footer">
				              <input type="submit" class="btn btn-sm btn-primary navbar-dark"  value="Yes" onclick="createPostalAddress.submit();">
				              <input type="button" class="btn btn-sm btn-primary navbar-dark" data-dismiss="modal" value="No" onclick="return false;">
					        </div>
					      </div>
					      </div>
					   </div>
					</div>  


<form id="zipcodeassoc-search-form" name="zipcodeassoc-search-form" method="post">	
	<input type="hidden" name="state" value="">
    <input type="hidden" name="zip" value="">
    <input type="hidden" name="searchField" value="city">
    <input type="hidden" name="externalLoginKey" value="${requestAttributes.externalLoginKey!}"/>
</form>

<script>
$(document).ready(function() {
    $('#createPOSTALcontactInfo').on('shown.bs.modal', function() {
        var countryGeoId = $('#generalCountryGeoId').val();
        console.log('countryGeoId', countryGeoId);
        if (countryGeoId != '') {
            regexJson = getServiceResult("getZipCodeRegex", 'countryGeoId', countryGeoId);
            regex = regexJson.regex;
        }
        console.log('countryGeoId', countryGeoId, regex);
    });
    $('#generalCountryGeoId').change(function(e, data) {
        $('#generalStateProvinceGeoId').dropdown("clear");
        regex = "";
        $('.createpostalCode').val('');
        $('#postalCodeExt').val('');
        var countryGeoId = $('#generalCountryGeoId').val();
        console.log('countryGeoId', countryGeoId);
        if (countryGeoId != '') {
            regexJson = getServiceResult("getZipCodeRegex", 'countryGeoId', countryGeoId);
            regex = regexJson.regex;
        } else {
            $('#generalStateProvinceGeoId').html('<option value="">Please Select</option>');
        }
        console.log('countryGeoId', countryGeoId, regex);
    });

    $('#createPostalAddress #postalCodeExt').keyup(function(e) {
        validatePostalCodeExt();
    });

    $('#createEmailAddress #emailAddress').keyup(function(e) {
        console.log("daba");
        validateEmailAddress();
    });

    $('#createTelecomNumber input#contactNumberss').keyup(function(e) {
        console.log("daba");
        validatePhoneNumber();
    });
    /*$('#createPostalAddress').validator().on('submit', function (e) {
    	if (e.isDefaultPrevented()) {
        	// handle the invalid form...
      	} else {
      	
      		var valid = true;
      		if ($("#generalCountryGeoId").val() && $("#generalCountryGeoId").val() == "USA") {
      			if ($("#createPostalAddress input[name=postalCode]").val().length != 5) {
      				valid = false;
      				showAlert("error", "Zip/Postal code length should be 5");
      			}  
      			if ($("#createPostalAddress input[name=postalCodeExt]").val() && $("#createPostalAddress input[name=postalCodeExt]").val().length != 4) {
      				valid = false;
      				showAlert("error", "Zip/Postal code extension length should be 4");
      			}  
      		}
      	
      		if (!valid){
      			e.preventDefault();
      		}
      		
      	}
    });*/

    createLoadZipCodeAssoc();
    $("#generalStateProvinceGeoId").change(function() {
        createLoadZipCodeAssoc();
    });
  
  //validation for emailid

  $('[id="createEmailAddress"] #contactMechPurposeTypeId').change(function() {
      console.log('gdfgdfgdf', $(this).val(), 'gdfgdfgd');
      if ($(this).val() == '')
          $('[id="createEmailAddress"] #contactMechPurposeTypeId_error').html('Please fill out the field');
      else
          $('[id="createEmailAddress"] #contactMechPurposeTypeId_error').html('');
  });
  
  //validation telecom number    
  $('[id="createTelecomNumber"] #contactMechPurposeTypeId').change(function() {
      if ($(this).val() == '')
          $('[id="createTelecomNumber"] #contactMechPurposeTypeId_error').html('Please fill out the field');
      else
          $('[id="createTelecomNumber"] #contactMechPurposeTypeId_error').html('');
  });
  
  $('#createWEBcontactInfo #contactMechPurposeTypeId').change(function() {
    if ($('#createWEBcontactInfo #contactMechPurposeTypeId').val() == "") {
        $('#createWEBcontactInfo #contactMechPurposeTypeId_error').html('Please fill out the field');
    } else
        $('#createWEBcontactInfo #contactMechPurposeTypeId_error').html('');
  });
  $('#createWEBcontactInfo #infoString').keyup(function() {
      if ($('#createWEBcontactInfo #infoString').val() == "") {
          $('#createWEBcontactInfo #infoString_error').html('Please fill out the field');
      } else
          $('#createWEBcontactInfo #infoString_error').html('');
  });


  //validation for PAddress
  $('#createPostalAddress #contactMechPurposeTypeId').change(function() {
      if ($('#createPostalAddress #contactMechPurposeTypeId').val() == "") {
          $('#createPostalAddress #contactMechPurposeTypeId_error').html('Please fill out the field');
      } else
          $('#createPostalAddress #contactMechPurposeTypeId_error').html('');
  });
$('[name="createPostalAddress"] #postalCode').keyup(function(e) {
        validatePostalCode();
        createLoadZipCodeAssoc();
    });
    function validatePostalCode() {
    var isInvalid = false;
    if (regex != '') {
        var re = new RegExp(regex);
        if (re.test($('.createpostalCode').val())) {
            console.log(regex, $('.createpostalCode').val(), 'regex1');
            $('[id="createPostalAddress"] #postalCode_error').html('');
        } else {
            console.log(regex, $('.createpostalCode').val(), 'regex2');
            $('[id="createPostalAddress"] #postalCode_error').html('Please enter the valid zip code');
            isInvalid = true;
        }
    }

    return isInvalid;
    }
    var re = new RegExp(regex);
    if (re.test($('.createpostalCode').val())) {
        $('[id="createPostalAddress"] #postalCode_error').html('');
    }
});

var regex = '';
function validatePostalCodeExt() {
    var isInvalid = false;
    if ($('[id="createPostalAddress"] #postalCodeExt').val() != "" && $('#generalCountryGeoId').val() == "USA") {
        var re = new RegExp("^([0-9]{4})$");
        console.log(regex, $('[id="createPostalAddress"] #postalCodeExt').val(), 'regex1');
        if (re.test($('[id="createPostalAddress"] #postalCodeExt').val())) {
            $('[id="createPostalAddress"] #postalCodeExt_error').html('');
        } else {
            $('[id="createPostalAddress"] #postalCodeExt_error').html('Please enter the valid zip code extension');
            isInvalid = true;
        }
    } else {
        if ($('[id="createPostalAddress"] #postalCodeExt_error') != undefined)
            $('[id="createPostalAddress"] #postalCodeExt_error').html('');
    }
    return isInvalid;
}

function validateEmailAddress() {
    var isInvalid = false;
    if ($('[id="createEmailAddress"] #emailAddress').val() != "") {
        var re = new RegExp("^[a-zA-Z0-9._-]+@[a-zA-Z0-9-_]+(?:.[a-zA-Z]{2,3})*$");
        console.log(re, $('[id="createEmailAddress"] #emailAddress').val(), 'regex1');
        if (re.test($('[id="createEmailAddress"] #emailAddress').val())) {
            $('[id="createEmailAddress"] #emailAddress_error').html('');
        } else {
            $('[id="createEmailAddress"] #emailAddress_error').html('Please enter valid email address');
            isInvalid = true;
        }
    } else {
        if ($('[id="createEmailAddress"] #emailAddress_error') != undefined)
            $('[id="createEmailAddress"] #emailAddress_error').html('');
    }
    return isInvalid;

}

function validatePhoneNumber() {
    var isInvalid = false;
    if ($('[id="createTelecomNumber"] #contactNumberss').val() != "") {
        var re = new RegExp("^[0-9]{1,13}$");
        console.log(re, $('[id="createTelecomNumber"] #contactNumberss').val(), 'regex1');
        if (re.test($('[id="createTelecomNumber"] #contactNumberss').val())) {
            $('[id="createTelecomNumber"] #contactNumberss_error').html('');
        } else {
            $('[id="createTelecomNumber"] #contactNumberss_error').html('Please enter valid phone number');
            isInvalid = true;
        }
    } else {
        if ($('[id="createTelecomNumber"] #contactNumberss_error') != undefined)
            $('[id="createTelecomNumber"] #contactNumberss_error').html('');
    }
    return isInvalid;
}

function formSubmissionCreate() {
    var isReturnFalse = false;
    var fromId = "createPostalAddress";
    // for input select fields
    $('[id="createPostalAddress"] input[type="text"], [id="createPostalAddress"] select')
        .each(
            function() {
                if (!$(this).prop('required')) {
                    // console.log("NR");
                } else {
                    // console.log("IR");
                    if (this.value == "" || this.value == undefined ||
                        this.value == null) {
                        isReturnFalse = true;
                        return false;
                    }
                }
            });
    if ((!isReturnFalse) && (validatePostalCode() || validatePostalCodeExt())) {
        isReturnFalse = true;
    }

    $('[id="createEmailAddress"] #contactMechPurposeTypeId')
        .change(
            function() {
                console.log('gdfgdfgdf', $(this).val(), 'gdfgdfgd');
                if ($(this).val() == '')
                    $('[id="createEmailAddress"] #contactMechPurposeTypeId_error')
                    .html('Please fill out the field');
                else
                    $('[id="createEmailAddress"] #contactMechPurposeTypeId_error')
                    .html('');
            });

    <#if isActUspsAddrVal ? has_content && isActUspsAddrVal == "Y" >
        if (!isReturnFalse && $('#generalCountryGeoId').val() == "USA") {
            var data = {
                "Address1": "createPostalAddress input[name=address1]",
                "Address2": "createPostalAddress input[name=address2]",
                "Zip5": "createPostalAddress input[name=postalCode]",
                "Zip4": "createPostalAddress input[name=postalCodeExt]",
                "City": "createPostalAddress input[id=city_desc]",
                "State": "generalStateProvinceGeoId",
                "Business": "createPostalAddress input[name=isBusiness]",
                "Vacant": "createPostalAddress input[name=isVacant]"
            };
            isReturnFalse = !USPSUTIL.performUspsAddrValidation(data, '${requestAttributes.externalLoginKey!}');
            if (!isReturnFalse) {
                $('#' + fromId + " input[name=isUspsAddrVerified]").val('Y');
            }
        } 
  	</#if>
  	
  	$('#' + fromId + ' input[name="city"]').val( $("[id='"+fromId+"'] #city_desc").val() );

    return !isReturnFalse;
}

function createLoadZipCodeAssoc() {
    if (!$("[id='createPostalAddress'] #generalStateProvinceGeoId").val()) {
        return;
    }
    
    $('#zipcodeassoc-search-form input[name="state"]').val( $("[id='createPostalAddress'] #generalStateProvinceGeoId").val() );
	$('#zipcodeassoc-search-form input[name="zip"]').val( $("[id='createPostalAddress'] #postalCode").val() );
	    
    //var cityOptions = '<option value="" selected="">Select City</option>';
    var countyOptions = '<option value="" selected="">Select County</option>';
    var zipOptions = '<option value="" selected="">Select Zip Code</option>';

    //let cityList = new Map();
    let countyList = new Map();
    let zipList = new Map();

    $.ajax({
        type: "POST",
        url: "/uiadv-portal/control/searchZipCodeAssocs",
        data: {
            "state": $("[id='createPostalAddress'] #generalStateProvinceGeoId").val(),
            "zip": $("[id='createPostalAddress'] #postalCode").val(),
            "externalLoginKey": "${requestAttributes.externalLoginKey!}"
        },
        async: false,
        success: function(result) {
            if (result.code == 200) {
                for (var i = 0; i < result.data.length; i++) {
                    var data = result.data[i];
                    //cityList.set(data.city, data.city);
                    countyList.set(data.county, data.county);
                    //zipList.set(data.zip, data.zip); 
                }
            }
        }
    });
    /*
    for (let key of cityList.keys()) {
    	if(cityList.size===1){
    		cityOptions += '<option value="'+key+'" selected>'+cityList.get(key)+'</option>';
    	} else {
    		cityOptions += '<option value="'+key+'">'+cityList.get(key)+'</option>';
    	}
    }
    */
    for (let key of countyList.keys()) {
        if (countyList.size === 1) {
            countyOptions += '<option value="' + key + '" selected>' + countyList.get(key) + '</option>';
        } else {
            countyOptions += '<option value="' + key + '">' + countyList.get(key) + '</option>';
        }
    }

    //$("[id='createPostalAddress'] #city").html( cityOptions );
    //$("[id='createPostalAddress'] #city").dropdown('refresh');

    $("[id='createPostalAddress'] #countyGeoId").html(countyOptions);
    $("[id='createPostalAddress'] #countyGeoId").dropdown('refresh');
}

function formMailSubmission(cmpTypeId, eId) {
    var isInValid = false;
    var contactMechPurposeTypeId = $('[id="createEmailAddress"] #contactMechPurposeTypeId').val();
    if (contactMechPurposeTypeId == "") {
        $('[id="createEmailAddress"] #contactMechPurposeTypeId_error').html('Please fill out the field');
        isInValid = true;
    }
    if ($('[id="createEmailAddress"] #emailAddress').val() == "") {
        $('[id="createEmailAddress"] #emailAddress_error').html('Please fill out the field');
        isInValid = true;
    }
    if (isInValid || validateEmailAddress()) {
        return false;
    }

    var primaryEmail = eId.value;
    var cmpTypeId = cmpTypeId.value;
    //var accType = document.getElementById("accType").value;
    var partyId = document.getElementById("partyId").value;
    var formName = "createEmailAddress";
    if (cmpTypeId === "PRIMARY_EMAIL") {
        $.ajax({
            type: "POST",
            url: "/common-portal/control/getDuplicateEmailList",
            async: true,
            data: {
                "primaryEmail": primaryEmail,
                "partyId": partyId,
                "screenType": "CREATE"
            },
            success: function(data) {
                var message = data.Error_Message;
                loadActivityCreate(message, formName);
            }
        });
    } else {
        var message = "NO_RECORDS";
        loadActivityCreate(message, formName);
    }
}

function formTelecomNumberSubmission(cmpTypeId, cnId) {
    var isInValid = false;
    var contactMechPurposeTypeId = $('[id="createTelecomNumber"] #contactMechPurposeTypeId').val();
    if (contactMechPurposeTypeId == "") {
        $('[id="createTelecomNumber"] #contactMechPurposeTypeId_error').html('Please fill out the field');
        isInValid = true;
    }

    if ($('[id="createTelecomNumber"] #contactNumberss').val() == "") {
        $('[id="createTelecomNumber"] #contactNumberss_error').html('Please fill out the field');
        isInValid = true;
    }
    if (isInValid || validatePhoneNumber()) {
        return false;
    }

    var primaryPhoneNumber = cnId.value;
    var partyId = document.getElementById("partyId").value;
    var cmtype = cmpTypeId.value;
    var formName = "createTelecomNumber";

    if ((cmtype === "PHONE_MOBILE")) {
        $.ajax({
            type: "POST",
            url: "/common-portal/control/getDuplicatePhoneNumber",
            async: true,
            data: {
                "primaryPhoneNumber": primaryPhoneNumber,
                "partyId": partyId,
                "screenType": "CREATE"
            },
            success: function(data) {
                var message = data.Error_Message;
                loadActivityCreate(message, formName);
            }
        });
    } else {
        var message = "NO_RECORDS";
        loadActivityCreate(message, formName);
    }
}

function formWeb() {

    var isInValid = false;
    var contactMechPurposeTypeId = $('#createWEBcontactInfo #contactMechPurposeTypeId').val();
    if (contactMechPurposeTypeId == "") {
        $('#createWEBcontactInfo #contactMechPurposeTypeId_error').html('Please fill out the field');
        isInValid = true;
    }
    if ($('#createWEBcontactInfo #infoString').val() == "") {
        $('#createWEBcontactInfo #infoString_error').html('Please fill out the field');
        isInValid = true;
    }
    if (isInValid) {
        return false;
    }

}

function formSocialMedia() {

    var isInValid = false;
    var contactMechPurposeTypeId = $('#createSocialMediaTypeWebm #contactMechPurposeTypeId').val();
    if (contactMechPurposeTypeId == "") {
        $('#createSocialMediaTypeWebm #contactMechPurposeTypeId_error').html('Please fill out the field');
        isInValid = true;
    }
    if ($('#createSocialMediaTypeWebm #socialMediaId').val() == "") {
        $('#createSocialMediaTypeWebm #socialMediaId_error').html('Please fill out the field');
        isInValid = true;
    }
    if (isInValid) {
        return false;
    }

}

function formPAddressSubmission(pCode, pContactMechId) {

    var isInValid = false;
    var contactMechPurposeTypeId = $('[id="createPostalAddress"] #contactMechPurposeTypeId').val();
    if (contactMechPurposeTypeId == "") {
        $('[id="createPostalAddress"] #contactMechPurposeTypeId_error').html(
            'Please fill out the field');
        isInValid = true;
    }

    if ($('[id="createPostalAddress"] #contactMechPurposeTypeId').val() == "") {
        $('[id="createPostalAddress"] #contactMechPurposeTypeId_error').html(
            'Please fill out the field');
        isInValid = true;
    }
    if ($('[id="createPostalAddress"] #postalCode').val() == "") {
        $('[id="createPostalAddress"] #postalCode_error').html(
            'Please fill out the field');
        isInValid = true;
    }

    var postalCode = pCode.value;
    var groupName = this.createPostalAddress.groupName.value;
    var firstName = this.createPostalAddress.firstName.value;
    var lastName = this.createPostalAddress.lastName.value;
    var partyId = document.getElementById("partyId").value;
    var accType = document.getElementById("accType").value;
    var formName = "createPostalAddress";
    var ctmId = pContactMechId.value;
    if (ctmId === "" || ctmId === undefined || ctmId === null) {
        return false;
    }
    $.ajax({
        type: "POST",
        url: "/common-portal/control/getDuplicateAddress",
        async: true,
        data: {
            "groupName": groupName,
            "firstName": firstName,
            "accType": accType,
            "lastName": lastName,
            "partyId": partyId,
            "postalCode": postalCode,
            "screenType": "CREATE"
        },
        success: function(data) {
            var message = data.Error_Message;
            loadActivityCreate(message, formName);
        }
    });
    return false;
}

function loadActivityCreate(message, formName) {
    var formName = formName;
    if (message === "NO_RECORDS") {
        if (formName === "createEmailAddress") $('form#createEmailAddress').submit();
        if (formName === "createPostalAddress") $('form#createPostalAddress').submit();
        if (formName === "createTelecomNumber") $('form#createTelecomNumber').submit();

    } else {
        if (formName === "createEmailAddress") {
            $('#createEmailAddressModal').modal('show');
            $("#EDC_message").html(message);
        }
        if (formName === "createPostalAddress") {
            $('#createPostalAddressModal').modal('show');
            $("#ADC_message").html(message);
        }
        if (formName === "createTelecomNumber") {
            $('#createTelecomNumberModal').modal('show');
            $("#TNC_message").html(message);
        }
    }
}
</script>