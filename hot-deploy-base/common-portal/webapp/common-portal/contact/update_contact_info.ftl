<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>

<style>
.modal.fade{
display:none;

}
<#-- .modal {
    height: 650px;
    width:1700px;
    margin-left: -95px;  -->

}
</style>

<#-- <#assign partyId= request.getParameter("partyId")! />
<#if partyId?exists && partyId?has_content>
<#if request.getRequestURI().contains("viewLead")>
<#assign requestURI = "viewLead"/>
<#assign accType = "LEAD"/>
<#elseif request.getRequestURI().contains("viewAccount")>
<#assign requestURI = "viewAccount"/>
<#assign accType = "ACCOUNT"/>
<#elseif request.getRequestURI().contains("viewContact")>
<#assign requestURI = "viewContact"/>
<#assign accType = "CONTACT"/>
<#elseif request.getRequestURI().contains("viewCustomer")>
<#assign requestURI = "viewCustomer"/>
<#assign accType = "CUSTOMER"/>
</#if>-->
<#assign requestUri=request.getParameter("requestUri")!>
<#assign partyId= request.getParameter("partyId")! />
<#if partyId=="" && inputContext?has_content>
<#assign partyId= "${inputContext.partyId?if_exists}" />
</#if>
<#if requestUri?has_content && partyId?exists && partyId?has_content>
<#assign requestURI = "viewContact"/>
<#if requestUri.contains("viewLead")!>
<#assign requestURI = "viewLead"/>
<#assign accType = "LEAD"/>
<#elseif requestUri.contains("viewAccount")!>
<#assign requestURI = "viewAccount"/>
<#assign accType = "ACCOUNT"/>
<#elseif requestUri.contains("viewCustomer")!>
<#assign requestURI = "viewCustomer"/>
<#assign accType = "CUSTOMER"/>
<#elseif requestUri.contains("viewContact")!>
<#assign requestURI = "viewContact"/>
<#assign accType = "CONTACT"/>
</#if>


<#assign globalDateTimeFormat = Static["org.fio.homeapps.util.DataHelper"].getGlobalDateTimeFormat(delegator)?if_exists />

<!-- Email Address -->
<#if contactMeches?exists && contactMeches?has_content>
<#list contactMeches as contactMechMap>
<#assign contactMech = contactMechMap.contactMech>
<#assign partycontactMech = contactMechMap.partyContactMech>



<#if contactMech.contactMechId ==  partycontactMech.contactMechId && contactMech.contactMechTypeId == "EMAIL_ADDRESS">
<div id="EMAILcontactInfo_${contactMech.contactMechId}" class="modal fade" role="dialog">
   <div class="modal-dialog modal-lg">
      <!-- Modal content-->
      <div class="modal-content">
         <div class="modal-header">
            <h4 class="modal-title">${uiLabelMap.updateEmail}</h4>
            <button type="reset" class="close" data-dismiss="modal">&times;</button>
         </div>
         <div class="modal-body poplabel-left">
            <div class="row padding">
               <div class="col-md-4 col-sm-4 ">
                  <div class="form-group row text-danger">
                     <label  class="col-sm-12 field-text">${uiLabelMap.contactPurpose}*</label>
                  </div>
               </div>
               <div class="col-md-8 col-sm-8">
                  <#assign i = 0/>
                   <#assign existingCmptSize = contactMechMap.partyContactMechPurposes?size />
                   <input type="hidden" name="existingEmailCmptSize" id="existingEmailCmptSize" value=${existingCmptSize} />
                  <#list contactMechMap.partyContactMechPurposes as partyContactMechPurpose>
                  <form method="post" action="<@ofbizUrl>deletePartyContactMechPurpose#${tabIdForCurrentTab!}</@ofbizUrl>" class="form-horizontal" name="deletePersonContactMechPurpose_EMAIL_${partyContactMechPurpose.contactMechId}_${i}" novalidate="novalidate" data-toggle="validator">
                     <input type="hidden" name="activeTab" value="contactInfo" />
                     <input type="hidden" name="donePage" value='${requestURI?if_exists}'/>
                     <input type="hidden" name="contactMechId" value="${partyContactMechPurpose.contactMechId}"/>
                     <input type="hidden" name="partyId" value="${partyId?if_exists}"/>
                     <input type="hidden" name="contactMechPurposeTypeId" value="${partyContactMechPurpose.contactMechPurposeTypeId}"/>
                     <input type="hidden" name="fromDate" value="${partyContactMechPurpose.fromDate?if_exists}"/>
                     <div class="form-group row">
                        <div class="col-sm-7">
                           <label class="field-text input-sm fw">
                          
                           <#assign contactMechPurposeType = partyContactMechPurpose.getRelatedOne("ContactMechPurposeType",true)>
                           <#if contactMechPurposeType?has_content>
                           ${contactMechPurposeType.get("description",locale)}<br>
                           <#else>
                           ${uiLabelMap.PartyMechPurposeTypeNotFound}: "${partyContactMechPurpose.contactMechPurposeTypeId}"
                           </#if>
                           <#if partyContactMechPurpose.thruDate?has_content>
                           (${uiLabelMap.CommonExpire}: ${getLocalizedDate(partyContactMechPurpose.thruDate)})
                           </#if>
                           </label>
                        </div>
                        <div class="col-sm-5">
                           <#if !partyContactMechPurpose.contactMechPurposeTypeId.contains("IDEAL")>
                            <input type="checkbox" class="checkbox" name="contactMechPurposeTypeIds" id="contactMechPurposeTypeId_${i}" value ="${partyContactMechPurpose.contactMechPurposeTypeId}"/>
			              	<label  for="contactMechPurposeTypeId_${i}">Remove
			              	<input type="hidden" name="contactMechPurposeTypeId" id="contactMechPurposeTypeId"/>
			               </label>
                           <!--<a class="btn btn-xs btn-secondary btn-danger tooltips confirm-message" href="javascript:document.deletePersonContactMechPurpose_EMAIL_${partyContactMechPurpose.contactMechId}_${i}.submit()" data-original-title="${uiLabelMap.delete}"><i class="fa fa-times red"></i></a>-->
                           </#if>
                        </div>
                     </div>
                  </form>
                  <#assign i = i+1/>
                  </#list>
                  <form method="post" action="<@ofbizUrl>createPartyContactMechPurpose#${tabIdForCurrentTab!}</@ofbizUrl>" class="form-horizontal" name="createPartyContactMechPurpose_EMAIL" id="createPartyContactMechPurpose_EMAIL_${contactMech.contactMechId}" novalidate="novalidate" data-toggle="validator">
                     <input type="hidden" name="activeTab" value="contactInfo" />
                     <input type="hidden" name="donePage" value='${requestURI?if_exists}'/>
                     <input type="hidden" name="contactMechId" value="${contactMech.contactMechId}"/>
                     <input type="hidden" name="partyId" value="${partyId?if_exists}"/>
                     <input type="hidden" name="externalLoginKey" value="${requestAttributes.externalLoginKey!}"/>
                     <div class="form-group row">
                        <#assign contactMechTPEmail = delegator.findByAnd("ContactMechTypePurpose", {"contactMechTypeId" : "EMAIL_ADDRESS"}, [], false)>
                        <div class="col-sm-10">
                           <select name="contactMechPurposeTypeId" id="contactMechPurposeTypeId" class="ui dropdown search form-control input-sm">
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
                        <div class="col-sm-2">
                           <!--<a class="btn btn-xs btn-primary tooltips" href="javascript:document.createPartyContactMechPurpose_EMAIL_${contactMech.contactMechId}.submit()" data-original-title="${uiLabelMap.addPurpose}"><i class="fa fa-plus info"></i></a>-->
                        </div>
                     </div>
                  </form>
               </div>
            </div>
            <hr/>
            <form method="post" action="<@ofbizUrl>updateEmailWithPurpose#${tabIdForCurrentTab!}</@ofbizUrl>" id="updateEmailAddress_${contactMech.contactMechId}" class="form-horizontal" name="updateEmailAddress" novalidate="novalidate" data-toggle="validator">
               <input type="hidden" name="activeTab" value="contactInfo" />
               <input type="hidden" name="donePage" value='${requestURI?if_exists}'/>
               <input type="hidden" id ="contactMechId" name="contactMechId" value="${contactMech.contactMechId}"/>
               <input type="hidden" name="partyId" value="${partyId?if_exists}"/>
                <input type="hidden" id="accType" name="accType" value="${accType?if_exists}"/>
                <#assign cmId = contactMech.contactMechId/>
               <#assign entities = delegator.findByAnd("PartyContactMechPurpose", Static["org.ofbiz.base.util.UtilMisc"].toMap("contactMechId",cmId), [], false)?if_exists>
                   <@inputRow
               id="emailAddress"
               name="emailAddress"
               label=uiLabelMap.email
               placeholder=uiLabelMap.emailPlaceholder
               value="${contactMech.infoString?if_exists}"
               type="email"
               required=true
               dataError="Please enter valid email address"
               />
               <#assign emailAllowSolicitation = "Y"/>
               <#assign emailValidInd = "Y"/>
                 <#if partySummary?exists && partySummary?has_content>
               <#assign partySolicitation = Static["org.ofbiz.entity.util.EntityUtil"].getFirst(delegator.findByAnd("PartyContactMech", {"partyId" : partySummary.partyId,"contactMechId":contactMech.contactMechId}, [], false))?if_exists/>
             </#if>
               <#if partySolicitation?exists && partySolicitation?has_content>
               <#if partySolicitation.allowSolicitation?has_content && partySolicitation.allowSolicitation=="N">
               <#assign emailAllowSolicitation = "N"/>
               </#if>
               <#if partySolicitation.emailValidInd?has_content && partySolicitation.emailValidInd=="N">
               <#assign emailValidInd = "N"/>
               </#if>
               </#if>
               <@dropdownCell 
               id="allowSolicitation"
               name="allowSolicitation"
               label=uiLabelMap.allowSolicitation
               options=yesNoOptions
               value="${emailAllowSolicitation?if_exists}"
               required=false
               allowEmpty=true
               dataLiveSearch=true
               />
               <@dropdownCell 
               id="emailValidInd"
               name="emailValidInd"
               label=uiLabelMap.deliverable
               options=yesNoOptions
               value="${emailValidInd?if_exists}"
               required=false
               allowEmpty=false
               dataLiveSearch=true
               />
                
               <div class="col-md-12 col-sm-12">
                  <div class="form-group row">
                     <div class="offset-sm-4 col-sm-9">
                     <@formButton
                     btn1type="button"
                     btn1label="${uiLabelMap.Update}"
                     btn1onclick="formSubmissionEmail('${contactMech.contactMechId}','${entities.get(0).contactMechPurposeTypeId}',updateEmailAddress_${contactMech.contactMechId}.emailAddress);"                                 
                   />
                     </div>
                  </div>
               </div>
            </form>
         </div>
      </div>
   </div>
</div>
</#if>
</#list>
<!-- Email Address End-->
<!--Postal Address -->
<style>
#scroll {
  height: 500px;
  overflow-y: scroll;
  overflow-x: hidden; 
}
</style>
<#list contactMeches as contactMechMap>
<#assign contactMech = contactMechMap.contactMech>
<#assign partycontactMech = contactMechMap.partyContactMech>
<#if contactMech.contactMechId ==  partycontactMech.contactMechId && contactMech.contactMechTypeId == "POSTAL_ADDRESS">
<div id="POSTALcontactInfo_${contactMech.contactMechId}" mechId="${contactMech.contactMechId}" class="modal fade POSTALcontactInfo" role="dialog">
   <div class="modal-dialog modal-lg">
      <div class="modal-content" id="contact-info">
         <div class="modal-header">
            <h4 class="modal-title">${uiLabelMap.updatePostalAddress}</h4>
            <button type="reset" class="close" data-dismiss="modal">&times;</button>
         </div>
         <div id="scroll">
         <div class="modal-body poplabel-left">
            <!-- Modal content-->
            <div class="row padding">
               <div class="col-md-4 col-sm-4 ">
                  <div class="form-group row text-danger">
                     <label  class="col-sm-12 field-text">${uiLabelMap.contactPurpose}*</label>
                  </div>
               </div>
               <div class="col-md-8 col-sm-8">
                  <#assign i = 0/>
                   <#assign existingPostalCmptSize = contactMechMap.partyContactMechPurposes?size />
                   <input type="hidden" name="existingPostalCmptSize" id="existingPostalCmptSize" value="${existingPostalCmptSize}" />
                   <#list contactMechMap.partyContactMechPurposes as partyContactMechPurpose>
                  <form method="post" action="<@ofbizUrl>deletePartyContactMechPurpose#${tabIdForCurrentTab!}</@ofbizUrl>" class="form-horizontal" name="deletePersonContactMechPurpose_PA_${partyContactMechPurpose.contactMechId}_${i}" id ="deletePersonContactMechPurpose_PA_${partyContactMechPurpose.contactMechId}" novalidate="novalidate" data-toggle="validator">
                     <input type="hidden" name="activeTab" value="contactInfo" />
                     <input type="hidden" name="donePage" value='${requestURI?if_exists}'/>
                     <input type="hidden" name="contactMechId" value="${partyContactMechPurpose.contactMechId}"/>
                     <input type="hidden" name="partyId" value="${partyId?if_exists}"/>
                     <input type="hidden" name="contactMechPurposeTypeId" value="${partyContactMechPurpose.contactMechPurposeTypeId}"/>
                     <input type="hidden" name="fromDate" value="${partyContactMechPurpose.fromDate?if_exists}"/>
                     <div class="form-group row">
                        <div class="col-sm-7">
                           <label class="field-text input-sm fw">
                           <#assign contactMechPurposeType = partyContactMechPurpose.getRelatedOne("ContactMechPurposeType",true)>
                           <#if contactMechPurposeType?has_content>
                           ${contactMechPurposeType.get("description",locale)}<br>
                           <#else>
                           ${uiLabelMap.PartyMechPurposeTypeNotFound}: "${partyContactMechPurpose.contactMechPurposeTypeId}"
                           </#if>
                           <#if partyContactMechPurpose.thruDate?has_content>
                           (${uiLabelMap.CommonExpire}: ${getLocalizedDate(partyContactMechPurpose.thruDate)})
                           </#if>
                           </label>
                           </div>
                           <div class="col-sm-5">
                           <#if !partyContactMechPurpose.contactMechPurposeTypeId.contains("IDEAL")>
			              	<input type="checkbox" class="checkbox" name="contactMechPurposeTypeIds" id="contactMechPurposeTypeId_${i}" value ="${partyContactMechPurpose.contactMechPurposeTypeId}"/>
			              	<label  for="contactMechPurposeTypeId_${i}">Remove
			              	<input type="hidden" name="contactMechPurposeTypeId" id="contactMechPurposeTypeId"/>
			               </label> 
	                           <!--<a class="btn btn-xs btn-secondary btn-danger tooltips confirm-message" href="javascript:document.deletePersonContactMechPurpose_PA_${partyContactMechPurpose.contactMechId}_${i}.submit()" data-original-title="${uiLabelMap.delete}"><i class="fa fa-times red"></i></a>-->
                           </#if>
                        </div>
                        <#-- <div class="col-sm-5">  -->
                           
                        <#-- </div>  -->
                     </div>
                  </form>
                  <#assign i = i+1/>
                  </#list>
                  <form method="post" action="<@ofbizUrl>createPartyContactMechPurpose#${tabIdForCurrentTab!}</@ofbizUrl>" class="form-horizontal" id="createPartyContactMechPurpose_PA_${contactMech.contactMechId}" name ="createPartyContactMechPurpose_PA" novalidate="novalidate" data-toggle="validator">
                     <input type="hidden" name="activeTab" value="contactInfo" />
                     <input type="hidden" name="donePage" value='${requestURI?if_exists}'/>
                     <input type="hidden" name="contactMechId" id="contactMechId" value="${contactMech.contactMechId}"/>
                     <input type="hidden" name="partyId" id="partyId" value="${partyId?if_exists}"/>
                     <div class="form-group row">
                        <#assign contactMechTPPostal = delegator.findByAnd("ContactMechTypePurpose", {"contactMechTypeId" : "POSTAL_ADDRESS"}, [], false)>
                        <div class="col-sm-10">
                           <select name="contactMechPurposeTypeId" id="contactMechPurposeTypeId" class="ui dropdown search form-control input-sm" >
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
                        <div class="col-sm-2">
                         <!--  <a class="btn btn-xs btn-primary tooltips" href="#" onclick="createPartyContactMechPurposePostal('${contactMech.contactMechId}')" data-original-title="${uiLabelMap.addPurpose}"><i class="fa fa-plus info"></i></a>-->
                        </div>
                     </div>
                  </form>
               </div>
            </div>
            <hr/>
            <form method="post" action="<@ofbizUrl>updatePostalAddressWithPurpose#${tabIdForCurrentTab!}</@ofbizUrl>" id="updatePostalAddress_${contactMech.contactMechId}" class="form-horizontal" name="updatePostalAddress" onSubmit = "return formSubmissionUpdate('${contactMech.contactMechId}');" novalidate="novalidate" data-toggle="validator" >
	           <input type="hidden" name="activeTab" value="contactInfo" />
               <input type="hidden" name="donePage" value='${requestURI?if_exists}'/>
               <input type="hidden" name="partyId" value="${partyId?if_exists}"/>
               <input type="hidden" name="contactMechId" id="contactMechId" value="${contactMech.contactMechId}"/>
               <input type="hidden" name="accType" value="${accType?if_exists}"/>
               <input type="hidden" name="groupName" value="${partySummary.groupName?if_exists}"/>
               <input type="hidden" name="firstName" value="${partySummary.firstName?if_exists}"/>
               <input type="hidden" name="lastName" value="${partySummary.lastName?if_exists}"/>
               <!-- <input type="hidden" name="contactMechPurposeTypeId_APA" id="contactMechPurposeTypeId_APA" value=""/>-->
               <!--<input type="hidden" name="deleteCMPurposeTypeIds_DPA" id="deleteCMPurposeTypeIds_DPA" value=""/>-->
               
               	<input type="hidden" name="isBusiness" value="">
		        <input type="hidden" name="isVacant" value="">
		        <input type="hidden" name="isUspsAddrVerified" value="">
               
                <#assign postalAddress = delegator.findOne("PostalAddress", {"contactMechId", "${contactMech.contactMechId}"}, false)?if_exists />
               <@inputRow 
               id="toName"
               name="toName"
               label="To Name"
               placeholder="To Name"
               value="${postalAddress?if_exists.toName?if_exists}"
               required=false
               />
              <@inputRow 
               id="attnName"
               name="attnName"
               label=uiLabelMap.attentionName
               placeholder=uiLabelMap.attentionName
               value="${postalAddress?if_exists.attnName?if_exists}"
               required=false
               />
                <@inputRow 
               id="address1"
               name="address1"
               label=uiLabelMap.address1
               placeholder=uiLabelMap.address1
               value="${postalAddress?if_exists.address1?if_exists}"
               required=false
               />
               <@inputRow
               id="address2"
               name="address2"
               label=uiLabelMap.address2
               placeholder=uiLabelMap.address2
               value="${postalAddress?if_exists.address2?if_exists}"
               required=false
               />
               
               <input type="hidden" id="stateProvinceGeoId" value="${postalAddress.stateProvinceGeoId?if_exists}"/>
               
               <div class="form-group row row">
                  <label  class="col-sm-4 field-text">${uiLabelMap.country} <span class="text-danger"> &#42;</span></label> 
                  <div class="col-sm-8">
                     <#assign defaultCountryGeoId=postalAddress.countryGeoId!/>
                     <#assign countries = delegator.findByAnd("Geo",Static["org.ofbiz.base.util.UtilMisc"].toMap("geoTypeId", "COUNTRY"),[] ,false)/>
                     <select required name="countryGeoId" id="generalCountryGeo_${contactMech.contactMechId}"  class="ui dropdown search form-control fluid show-tick generalCountryGeo custom-select" >
                        <#if !countries?has_content>
                        <#else>
	                        <option value="">---Select---</option>
	                        <#list countries as country>
		                        <#if defaultCountryGeoId == country.geoId>
		                        	<#assign selected="selected=\"selected\"">
		                        <#else>
		                        	<#assign selected="">
		                        </#if>
		                        <option ${selected} value="${country.geoId}">${country.get("geoName", locale)}</option>
	                        </#list>
                        </#if>
                     </select>
                     <div class="help-block with-errors"></div>
                  </div>
               </div>
               <div class="form-group row row">
                  <label  class="col-sm-4 field-text">${uiLabelMap.state}</label>
                  <div class="col-sm-8">
                     <#assign defaultStateProvinceGeoId="${postalAddress.stateProvinceGeoId?if_exists}"/>
                     <select class="ui dropdown search form-control fluid show-tick generalStateGeo custom-select" name="stateProvinceGeoId" id="generalStateProvinceGeo_${contactMech.contactMechId}" >
                        <#if postalAddress.stateProvinceGeoId?has_content>
                        <option value="${postalAddress.stateProvinceGeoId?if_exists}">N/A</option>
                        <#else>
                        <option value="_NA_">N/A</option>
                        </#if>
                     </select>
                     <div class="help-block with-errors"></div>
                  </div>
               </div>
								               
               <@inputRow 
               id="postalCode"
               name="postalCode"
               label=uiLabelMap.postalCode
               placeholder=uiLabelMap.postalCode
               value="${postalAddress?if_exists.postalCode?if_exists}"
               required=true
               />
               <@inputRow 
               id="postalCodeExt"
               name="postalCodeExt"
               label=uiLabelMap.postalCodeExt
               placeholder=uiLabelMap.postalCodeExt
               value="${postalAddress?if_exists.postalCodeExt?if_exists}"
               required=false
               />
               
               <#-- 
               <#assign geo = delegator.findOne("Geo", {"geoId" : "${postalAddress?if_exists.city?if_exists}"}, false)!>
              	<#assign city = "${postalAddress?if_exists.city?if_exists}"/>
              	<#if geo?exists && geo?has_content>
                 	<#assign city = geo.geoName/>
              	</#if>
               <@inputRow 
               id="cityLi"
               name="city"
               label=uiLabelMap.city
               placeholder=uiLabelMap.city
               value="${postalAddress?if_exists.city?if_exists}"
               required=false
               />
                -->
              
              <input type="hidden" name="city" value="${postalAddress?if_exists.city?if_exists}"> 	
               <input type="hidden" id="city_${contactMech.contactMechId}" value="${postalAddress?if_exists.city?if_exists}"> 
               <input type="hidden" id="countyGeoId_${contactMech.contactMechId}" value="${postalAddress?if_exists.county?if_exists}"> 
              
              <@inputAutoComplete
					id="cityLi_${contactMech.contactMechId}"
					label=uiLabelMap.city
					value="${postalAddress?if_exists.city?if_exists}"
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
               id="cityLi_${contactMech.contactMechId}"
               name="city"
               label=uiLabelMap.city
               placeholder=uiLabelMap.city
               value="${postalAddress?if_exists.city?if_exists}"
               required=false
               allowEmpty=true
               dataLiveSearch=true
               />	
                -->
               
               <@dropdownCell 
               id="countyGeoIdLi_${contactMech.contactMechId}"
               name="county"
               label=uiLabelMap.county
               placeholder=uiLabelMap.county
               value="${postalAddress?if_exists.county?if_exists}"
               required=false
               allowEmpty=true
               dataLiveSearch=true
               />    
                
               <#assign postalAllowSolicitation = "Y"/>
                 <#if partySummary?exists && partySummary?has_content>
               <#assign partySolicitation = Static["org.ofbiz.entity.util.EntityUtil"].getFirst(delegator.findByAnd("PartyContactMech", {"partyId" : partySummary.partyId,"contactMechId":contactMech.contactMechId}, [], false))?if_exists/>
              </#if>
               <#if partySolicitation?exists && partySolicitation?has_content>
               <#if partySolicitation.allowSolicitation?has_content && partySolicitation.allowSolicitation=="N">
               <#assign postalAllowSolicitation = "N"/>
               </#if>
               </#if> 
               <@dropdownCell 
               id="allowSolicitation"
               name="allowSolicitation"
               label=uiLabelMap.allowSolicitation
               options=yesNoOptions
               value="${postalAllowSolicitation?if_exists}"
               required=false
               allowEmpty=true
               dataLiveSearch=true
               />
               <#assign addressValidInd = "Y"/>
               <#if postalAddress.addressValidInd?has_content && postalAddress.addressValidInd=="N">
               <#assign addressValidInd = "N"/>
               </#if>
               <@dropdownCell 
               id="addressValidInd"
               name="addressValidInd"
               label=uiLabelMap.deliverable
               options=yesNoOptions
               value="${addressValidInd?if_exists}"
               required=false
               allowEmpty=false
               dataLiveSearch=true
               />
               
               <@inputDateTime 
				   id="solicitChangeDt"
				   label="Solicitation Change Date"
				   value=partycontactMech.solicitChangeDt!
				   dateFormat="MM/dd/yyyy"
				   isMeridianTimeMode="N"
				   />
               <@inputRow 
	               id="ip"
	               name="ip"
	               label=uiLabelMap.IP!
	               placeholder=uiLabelMap.IP!
	               value="${partycontactMech.ip!}"
	               required=false
	               />
               <@inputRow 
	               id="device"
	               name="device"
	               label=uiLabelMap.Device!
	               placeholder=uiLabelMap.Device!
	               value="${partycontactMech.device!}"
	               required=false
	               />
               
		       <div id="submitAddressModal" class="modal fade" role="dialog">
		            <div class="modal-dialog modal-md">
				     <div class="modal-dialog">
				      <div class="modal-content">
		            	<div class="modal-header">
		            	<span id="AD_message"></span>
		            	<span id="AD_cmId" style = "display:none"></span>
		            	<span id="AD_cmAId" style = "display:none"></span>
		            	<span id="AD_cmDId" style = "display:none"></span>
			               <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
			            	</div>
				            <div>
				            </div>
		            		<div class="modal-footer">
				              <input type="submit" class="btn btn-sm btn-primary navbar-dark"  value="Yes" onclick="onUpdatePaddress();">
				              <input type="button" class="btn btn-sm btn-primary navbar-dark" data-dismiss="modal" value="No" onclick="return false;">
					        </div>
					      </div>
					      </div>
					   </div>
					</div>
               
               <div class="col-md-12 col-sm-12">
                  <div class="form-group row">
                     <div class="offset-sm-4 col-sm-9">
                     <@formButton
                     btn1type="button"
                     btn1label="${uiLabelMap.Update}"
                     btn1onclick="return formSubmissionPaddress('${contactMech.contactMechId}',updatePostalAddress_${contactMech.contactMechId}.postalCode);"                 
                   />
                   
                       <#-- <@submit class="btn btn-sm btn-primary navbar-dark mt" label=uiLabelMap.update /> -->
                     </div>
                  </div>
               </div>
            </form>
         </div></div>
      </div>
   </div>
</div>
</#if>
</#list>
<!--Postal Address End-->
<!-- Telecom Number-->
<#assign numberCnt = 1/>
<#list contactMeches as contactMechMap>
<#assign contactMech = contactMechMap.contactMech>
<#assign partycontactMech = contactMechMap.partyContactMech>
<#if contactMech.contactMechId ==  partycontactMech.contactMechId && contactMech.contactMechTypeId == "TELECOM_NUMBER">
<div id="TELECOMcontactInfo_${contactMech.contactMechId}" class="modal fade TELECOMcontactInfo" role="dialog">
   <div class="modal-dialog modal-lg">
      <div class="modal-content">
         <div class="modal-header">
            <h4 class="modal-title">${uiLabelMap.updatePhoneNumber}</h4>
            <button type="reset" class="close" data-dismiss="modal">&times;</button>
         </div>
         <div class="modal-body poplabel-left">
            <div class="row padding">
               <div class="col-md-4 col-sm-4 ">
                  <div class="form-group row text-danger">
                     <label  class="col-sm-12 field-text">${uiLabelMap.contactPurpose}*</label>
                  </div>
               </div>
               <div class="col-md-8 col-sm-8">
                  <#assign i = 0/>
                  <#assign existingTelecomCmptSize = contactMechMap.partyContactMechPurposes?size />
                   <input type="hidden" name="existingTelecomCmptSize" id="existingTelecomCmptSize" value="${existingTelecomCmptSize}" />
                  <#list contactMechMap.partyContactMechPurposes as partyContactMechPurpose>
                  <form method="post" action="<@ofbizUrl>deletePartyContactMechPurpose#${tabIdForCurrentTab!}</@ofbizUrl>" class="form-horizontal" name="deletePersonContactMechPurpose_EMAIL_${partyContactMechPurpose.contactMechId}_${i}" novalidate="novalidate" data-toggle="validator">
                     <input type="hidden" name="activeTab" value="contactInfo" />
                     <input type="hidden" name="donePage" value='${requestURI?if_exists}'/>
                     <input type="hidden" name="contactMechId" value="${partyContactMechPurpose.contactMechId}"/>
                     <input type="hidden" name="partyId" value="${partyId?if_exists}"/>
                     <input type="hidden" name="contactMechPurposeTypeId" value="${partyContactMechPurpose.contactMechPurposeTypeId}"/>
                     <input type="hidden" name="fromDate" value="${partyContactMechPurpose.fromDate?if_exists}"/>
                     <div class="form-group row">
                        <div class="col-sm-8">
                           <label class="field-text input-sm fw">
                           <#assign contactMechPurposeType = partyContactMechPurpose.getRelatedOne("ContactMechPurposeType",true)>
                           <#if contactMechPurposeType?has_content>
                           ${contactMechPurposeType.get("description",locale)}<br>
                           <#else>
                           ${uiLabelMap.PartyMechPurposeTypeNotFound}: "${partyContactMechPurpose.contactMechPurposeTypeId}"
                           </#if>
                           <#if partyContactMechPurpose.thruDate?has_content>
                           (${uiLabelMap.CommonExpire}: ${getLocalizedDate(partyContactMechPurpose.thruDate)})
                           </#if>
                           </label>
                        </div>
                        <div class="">
                           <#if !partyContactMechPurpose.contactMechPurposeTypeId.contains("IDEAL")>
			              	<input type="checkbox" class="checkbox" name="contactMechPurposeTypeIds" id="contactMechPurposeTypeId_${i}" value ="${partyContactMechPurpose.contactMechPurposeTypeId}"/>
			              	<label  for="contactMechPurposeTypeId_${i}">Remove
			              	<input type="hidden" name="contactMechPurposeTypeId" id="contactMechPurposeTypeId"/>
			               </label>
                           <!-- <a class="btn btn-xs btn-secondary btn-danger tooltips confirm-message" href="javascript:document.deletePersonContactMechPurpose_EMAIL_${partyContactMechPurpose.contactMechId}_${i}.submit()" data-original-title="${uiLabelMap.delete}"><i class="fa fa-times red"></i></a>-->
                           </#if>
                        </div>
                     </div>
                  </form>
                  <#assign i = i+1/>
                  </#list>
                  <form method="post" action="<@ofbizUrl>createPartyContactMechPurpose#${tabIdForCurrentTab!}</@ofbizUrl>" class="form-horizontal" name="createPartyContactMechPurpose_TELECOM" id="createPartyContactMechPurpose_TELECOM_${contactMech.contactMechId}" novalidate="novalidate" data-toggle="validator">
                     <input type="hidden" name="activeTab" value="contactInfo" />
                     <input type="hidden" name="donePage" value='${requestURI?if_exists}'/>
                     <input type="hidden" name="contactMechId" value="${contactMech.contactMechId}"/>
                     <input type="hidden" name="partyId" value="${partyId?if_exists}"/>
                     <div class="form-group row">
                        <#assign contactMechTPTelecom = delegator.findByAnd("ContactMechTypePurpose", {"contactMechTypeId" : "TELECOM_NUMBER"}, [], false)>
                        <div class="col-sm-11">
                           <select name="contactMechPurposeTypeId" id="contactMechPurposeTypeId" class="ui dropdown search form-control input-sm" >
                              <option value="" data-content="<span class='nonselect'>Please Select</span>" selected>Please Select</option>
                              <#if contactMechTPTelecom?exists && contactMechTPTelecom?has_content>
                              <#list contactMechTPTelecom as contactMechTPTelecomGV>
                              <#assign contactMechPTTelecom = delegator.findOne("ContactMechPurposeType", {"contactMechPurposeTypeId" : contactMechTPTelecomGV.contactMechPurposeTypeId}, false)>
                              <#if contactMechPTTelecom?exists && contactMechPTTelecom?has_content>
                              <option value="${contactMechPTTelecom.contactMechPurposeTypeId}" <#if ("${contactMechPTTelecom.contactMechPurposeTypeId}"="IDEAL_MOBILE_PHONE" || "${contactMechPTTelecom.contactMechPurposeTypeId}"="AOS_MOBILE_PHONE") || ("${contactMechPTTelecom.contactMechPurposeTypeId}" = "IDEAL_PRIMARY_PHONE" ) > disabled data-content="<span class='nonselect'>${contactMechPTTelecom.description}</span>" </#if> >
                              ${contactMechPTTelecom.description} 
                              </option>
                              </#if>
                              </#list>
                              </#if>
                           </select>
                            <div class="help-block with-errors" id="contactMechPurposeTypeId_error"></div>
                        </div>
                        <div class="col-sm-2">
                          <!-- <a class="btn btn-xs btn-primary tooltips" href="javascript:document.createPartyContactMechPurpose_EMAIL_${contactMech.contactMechId}.submit()" data-original-title="${uiLabelMap.addPurpose}"><i class="fa fa-plus info"></i></a>-->
                        </div>
                     </div>
                  </form>
               </div>
            </div>
            <hr/>
            <form method="post" action="<@ofbizUrl>updateTelecomNumberWithPurpose#${tabIdForCurrentTab!}</@ofbizUrl>" id="updateTelecomNumber_${contactMech.contactMechId}" class="form-horizontal updateTelecomNumber" name="updateTelecomNumber" novalidate="novalidate" data-toggle="validator">
               <input type="hidden" name="activeTab" value="contactInfo" />
               <input type="hidden" name="donePage" value='${requestURI?if_exists}'/>
               <input type="hidden" name="partyId" value="${partyId?if_exists}"/>
               <input type="hidden" name="contactMechId" value="${contactMech.contactMechId}"/>
               <input type="hidden" id="accType" name="accType" value="${accType?if_exists}"/>
                <#assign telecomNumber = delegator.findOne("TelecomNumber", {"contactMechId", "${contactMech.contactMechId}"}, false)?if_exists />
                <#assign cmId = contactMech.contactMechId/>
               <#assign entities = delegator.findByAnd("PartyContactMechPurpose", Static["org.ofbiz.base.util.UtilMisc"].toMap("contactMechId",cmId), [], false)>
              
               <#assign teleCode = Static["org.fio.homeapps.util.DataUtil"].getCountryTeleCode(delegator, "") />
               <#assign countryCode="">
               <#if telecomNumber?has_content && telecomNumber.countryCode?has_content>
               <#assign countryCode=telecomNumber.countryCode?if_exists>
               <#elseif teleCode?has_content>
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
	               id="contactNumbers${numberCnt!}"
	               name="contactNumber"
	               label=uiLabelMap.phoneNumber
	               placeholder=uiLabelMap.phoneNumber
	               value="${telecomNumber?if_exists.contactNumber?if_exists}"
	               required=true
	               dataError="Please fill out the field"
	               />
	           <@inputRow 
	               id="extension${numberCnt!}"
	               name="extension"
	               label=uiLabelMap.extension
	               placeholder=uiLabelMap.extension
	               value="${partycontactMech?if_exists.extension?if_exists}"
	               pattern="^[0-9]+$"
               	   maxlength=5
	               />
               <@inputRow 
               id="askForName"
               name="askForName"
               label=uiLabelMap.personToAskFor
               placeholder=uiLabelMap.personToAskFor
               value="${telecomNumber?if_exists.askForName?if_exists}"
               required=false
               />
               <#assign phoneAllowSolicitation = "Y"/>
                 <#if partySummary?exists && partySummary?has_content>
               <#assign partySolicitation = Static["org.ofbiz.entity.util.EntityUtil"].getFirst(delegator.findByAnd("PartyContactMech", {"partyId" : partySummary.partyId,"contactMechId":contactMech.contactMechId}, [], false))?if_exists/>
               </#if>
               <#if partySolicitation?exists && partySolicitation?has_content>
               <#if partySolicitation.allowSolicitation?has_content && partySolicitation.allowSolicitation=="N">
               <#assign phoneAllowSolicitation = "N"/>
               </#if>
               </#if>
               <@dropdownCell
               id="allowSolicitation"
               name="allowSolicitation"
               label=uiLabelMap.allowSolicitation
               options=yesNoOptions
               value="${phoneAllowSolicitation?if_exists}"
               required=false
               allowEmpty=true
               dataLiveSearch=true
               />
               <#assign phoneValidInd = "Y"/>
               <#if telecomNumber.phoneValidInd?has_content && telecomNumber.phoneValidInd=="N">
               <#assign phoneValidInd = "N"/>
               </#if>
               <@dropdownCell
               id="phoneValidInd"
               name="phoneValidInd"
               label=uiLabelMap.deliverable
               options=yesNoOptions
               value="${phoneValidInd?if_exists}"
               required=false
               allowEmpty=false
               dataLiveSearch=true
               />
               
               <#if requestURI?exists && requestURI?has_content && requestURI == "viewLead">
               <#assign dndStatus = "N"/>
               <#if telecomNumber.dndStatus?has_content && telecomNumber.dndStatus=="Y">
               <#assign dndStatus = "Y"/>
               </#if>
               <@dropdownCell
               id="dndStatus"
               name="dndStatus"
               label="DND Status"
               options=yesNoOptions
               value="${dndStatus?if_exists}"
               required=false
               allowEmpty=true
               dataLiveSearch=true
               disabled=true
               />
               </#if>
          
               <div class="col-md-12 col-sm-12">
                  <div class="form-group row">
                     <div class="offset-sm-4 col-sm-9">
                     <@formButton
                     btn1type="button"
                     btn1label="${uiLabelMap.Update}"
                     btn1onclick="formSubmissionPhone('${contactMech.contactMechId}','${entities.get(0).contactMechPurposeTypeId}');"                 
                   />
                        <#--<@submit class="btn btn-sm btn-primary navbar-dark mt" label="${uiLabelMap.update}"/>-->
                     </div>
                  </div>
               </div>
            </form>
         </div>
      </div>
   </div>
</div>
</#if>
<#assign numberCnt = numberCnt + 1/>
</#list>
<!-- Web Address-->
<#list contactMeches as contactMechMap>
<#assign contactMech = contactMechMap.contactMech>
<#assign partycontactMech = contactMechMap.partyContactMech>
<#if contactMech.contactMechId ==  partycontactMech.contactMechId && contactMech.contactMechTypeId == "WEB_ADDRESS">
<div id="WEBcontactInfo_${contactMech.contactMechId}" class="modal fade" role="dialog">
   <div class="modal-dialog modal-lg">
      <div class="modal-content">
         <div class="modal-header">
            <h4 class="modal-title">${uiLabelMap.updateWebAddress}</h4>
            <button type="reset" class="close" data-dismiss="modal">&times;</button>
         </div>
         <div class="modal-body poplabel-left">
            <div class="row padding">
               <div class="col-md-4 col-sm-4 ">
                  <div class="form-group row text-danger">
                     <label  class="col-sm-12 field-text">${uiLabelMap.contactPurpose}*</label>
                  </div>
               </div>
               <div class="col-md-8 col-sm-8">
                  <#assign i = 0/>
                  <#assign existingWebCmptSize = contactMechMap.partyContactMechPurposes?size />
                   <input type="hidden" name="existingWebCmptSize" id="existingWebCmptSize" value="${existingWebCmptSize}" />
                  <#list contactMechMap.partyContactMechPurposes as partyContactMechPurpose>
                  <form method="post" action="<@ofbizUrl>deletePartyContactMechPurpose#${tabIdForCurrentTab!}</@ofbizUrl>" class="form-horizontal" name="deletePersonContactMechPurpose_EMAIL_${partyContactMechPurpose.contactMechId}_${i}" novalidate="novalidate" data-toggle="validator">
                     <input type="hidden" name="activeTab" value="contactInfo" />
                     <input type="hidden" name="donePage" value='${requestURI?if_exists}'/>
                     <input type="hidden" name="contactMechId" value="${partyContactMechPurpose.contactMechId}"/>
                     <input type="hidden" name="partyId" value="${partyId?if_exists}"/>
                     <input type="hidden" name="contactMechPurposeTypeId" value="${partyContactMechPurpose.contactMechPurposeTypeId}"/>
                     <input type="hidden" name="fromDate" value="${partyContactMechPurpose.fromDate?if_exists}"/>
                     <div class="form-group row">
                        <div class="col-sm-8">
                           <label class="field-text input-sm fw">
                           <#assign contactMechPurposeType = partyContactMechPurpose.getRelatedOne("ContactMechPurposeType",true)>
                           <#if contactMechPurposeType?has_content>
                           ${contactMechPurposeType.get("description",locale)}<br>
                           <#else>
                           ${uiLabelMap.PartyMechPurposeTypeNotFound}: "${partyContactMechPurpose.contactMechPurposeTypeId}"
                           </#if>
                           <#if partyContactMechPurpose.thruDate?has_content>
                           (${uiLabelMap.CommonExpire}: ${getLocalizedDate(partyContactMechPurpose.thruDate)})
                           </#if>
                           </label>
                        </div>
                        <div class="col-sm-5">
                           <#if !partyContactMechPurpose.contactMechPurposeTypeId.contains("IDEAL")>
                          <input type="checkbox" class="checkbox" name="contactMechPurposeTypeIds" id="contactMechPurposeTypeId_${i}" value ="${partyContactMechPurpose.contactMechPurposeTypeId}"/>
                          <label  for="contactMechPurposeTypeId_${i}">Remove
			              <input type="hidden" name="contactMechPurposeTypeId" id="contactMechPurposeTypeId"/>
			               </label>
                          <!--<a class="btn btn-xs btn-secondary btn-danger tooltips confirm-message" href="javascript:document.deletePersonContactMechPurpose_EMAIL_${partyContactMechPurpose.contactMechId}_${i}.submit()" data-original-title="${uiLabelMap.delete}"><i class="fa fa-times red"></i></a>-->
                           </#if>
                        </div>
                     </div>
                  </form>
                  <#assign i = i+1/>
                  </#list>
                  <form method="post" action="<@ofbizUrl>createPartyContactMechPurpose#${tabIdForCurrentTab!}</@ofbizUrl>" class="form-horizontal" name="createPartyContactMechPurpose_WEBURL" id="createPartyContactMechPurpose_WEBURL_${contactMech.contactMechId}" novalidate="novalidate" data-toggle="validator">
                     <input type="hidden" name="activeTab" value="contactInfo" />
                     <input type="hidden" name="donePage" value='${requestURI?if_exists}'/>
                     <input type="hidden" name="contactMechId" value="${contactMech.contactMechId}"/>
                     <input type="hidden" name="partyId" value="${partyId?if_exists}"/>
                     <div class="form-group row">
                        <#assign contactMechTPWeb = delegator.findByAnd("ContactMechTypePurpose", {"contactMechTypeId" : "WEB_ADDRESS"}, [], false)>
                        <div class="col-sm-8">
                           <select name="contactMechPurposeTypeId" id="contactMechPurposeTypeId" class="ui dropdown search form-control input-sm" >
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
                        <div class="col-sm-2">
                          <!-- <a class="btn btn-xs btn-primary tooltips" href="javascript:document.createPartyContactMechPurpose_EMAIL_${contactMech.contactMechId}.submit()" data-original-title="${uiLabelMap.addPurpose}"><i class="fa fa-plus info"></i></a>-->
                        </div>
                     </div>
                  </form>
               </div>
            </div>
            <hr/>
            <form method="post" action="<@ofbizUrl>updateContactMechWithPurpose#${tabIdForCurrentTab!}</@ofbizUrl>" id="updateContactMech_${contactMech.contactMechId}" class="form-horizontal" name="updateContactMech" novalidate="novalidate" data-toggle="validator">
               <input type="hidden" name="activeTab" value="contactInfo" />
               <input type="hidden" name="donePage" value='${requestURI?if_exists}'/>
               <input type="hidden" name="partyId" value="${partyId?if_exists}"/>
               <input type="hidden" name="contactMechId" value="${contactMech.contactMechId}"/>
               <input type="hidden" name="contactMechTypeId" value="${contactMech.contactMechTypeId}"/>
               <@inputRow 
               id="infoString"
               name="infoString"
               label=uiLabelMap.webURL
               placeholder=uiLabelMap.webURL
               value="${contactMech.infoString?if_exists}"
               required=true
               />
               <#assign webAllowSolicitation = "Y"/>
                <#if partySummary?exists && partySummary?has_content>
               <#assign partySolicitation = Static["org.ofbiz.entity.util.EntityUtil"].getFirst(delegator.findByAnd("PartyContactMech", {"partyId" : partySummary.partyId,"contactMechId":contactMech.contactMechId}, [], false))?if_exists/>
             </#if>
               <#if partySolicitation?exists && partySolicitation?has_content>
               <#if partySolicitation.allowSolicitation?has_content && partySolicitation.allowSolicitation=="N">
               <#assign webAllowSolicitation = "N"/>
               </#if>
               </#if>
               <@dropdownCell
               id="allowSolicitation"
               name="allowSolicitation"
               label=uiLabelMap.allowSolicitation
               options=yesNoOptions
               value="${webAllowSolicitation?if_exists}"
               required=false
               allowEmpty=true
               dataLiveSearch=true
               />
               <div class="col-md-12 col-sm-12">
                  <div class="form-group row">
                     <div class="offset-sm-4 col-sm-9">
                     <@formButton
                     btn1type="button"
                     btn1label="${uiLabelMap.Update}"
                     btn1onclick="formSubmissionWebUrl('${contactMech.contactMechId}');"                 
                   />
                        <#--
                        <@submit class="btn btn-sm btn-primary navbar-dark mt" label="${uiLabelMap.update}"/>-->
                     </div>
                  </div>
               </div>
            </form>
         </div>
      </div>
   </div>
</div>
</#if>
</#list>
<!-- Social Media Type -->
<#list contactMeches as contactMechMap>
<#assign contactMech = contactMechMap.contactMech>
<#assign partycontactMech = contactMechMap.partyContactMech>
<#if contactMech.contactMechId ==  partycontactMech.contactMechId && contactMech.contactMechTypeId == "SOCIAL_MEDIA_TYPE">
<div id="SocialMediacontactInfo_${contactMech.contactMechId}" class="modal fade" role="dialog">
   <div class="modal-dialog modal-lg">
      <div class="modal-content">
         <div class="modal-header">
            <h4 class="modal-title">${uiLabelMap.updateSocialMediaType}</h4>
            <button type="reset" class="close" data-dismiss="modal">&times;</button>
         </div>
         <div class="modal-body poplabel-left">
            <div class="row padding">
               <div class="col-md-4 col-sm-4 ">
                  <div class="form-group row text-danger">
                     <label  class="col-sm-12 field-text">${uiLabelMap.contactPurpose}*</label>
                  </div>
               </div>
               <div class="col-md-8 col-sm-8">
                  <#assign i = 0/>
                  <#assign existingSMCmptSize = contactMechMap.partyContactMechPurposes?size />
                   <input type="hidden" name="existingSMCmptSize" id="existingSMCmptSize" value="${existingSMCmptSize}" />
                  <#list contactMechMap.partyContactMechPurposes as partyContactMechPurpose>
                  <form method="post" action="<@ofbizUrl>deletePartyContactMechPurpose#${tabIdForCurrentTab!}</@ofbizUrl>" class="form-horizontal" name="deletePersonContactMechPurpose_EMAIL_${partyContactMechPurpose.contactMechId}_${i}" novalidate="novalidate" data-toggle="validator">
                     <input type="hidden" name="activeTab" value="contactInfo" />
                     <input type="hidden" name="donePage" value='${requestURI?if_exists}'/>
                     <input type="hidden" name="contactMechId" value="${partyContactMechPurpose.contactMechId}"/>
                     <input type="hidden" name="partyId" value="${partyId?if_exists}"/>
                     <input type="hidden" name="contactMechPurposeTypeId" value="${partyContactMechPurpose.contactMechPurposeTypeId}"/>
                     <input type="hidden" name="fromDate" value="${partyContactMechPurpose.fromDate?if_exists}"/>
                     <div class="form-group row">
                        <div class="col-sm-8">
                           <label class="field-text input-sm fw">
                           <#assign contactMechPurposeType = partyContactMechPurpose.getRelatedOne("ContactMechPurposeType",true)>
                           <#if contactMechPurposeType?has_content>
                           ${contactMechPurposeType.get("description",locale)}<br>
                           <#else>
                           ${uiLabelMap.PartyMechPurposeTypeNotFound}: "${partyContactMechPurpose.contactMechPurposeTypeId}"
                           </#if>
                           <#if partyContactMechPurpose.thruDate?has_content>
                           (${uiLabelMap.CommonExpire}: ${getLocalizedDate(partyContactMechPurpose.thruDate)})
                           </#if>
                           </label>
                        </div>
                        <div class="col-sm-5">
                           <#if !partyContactMechPurpose.contactMechPurposeTypeId.contains("IDEAL")>
                           <input type="checkbox" class="checkbox" name="contactMechPurposeTypeIds" id="contactMechPurposeTypeId_${i}" value ="${partyContactMechPurpose.contactMechPurposeTypeId}"/>
                           <label  for="contactMechPurposeTypeId_${i}">Remove
			              	<input type="hidden" name="contactMechPurposeTypeId" id="contactMechPurposeTypeId"/>
			               </label>
                            <!--<a class="btn btn-xs btn-secondary btn-danger tooltips confirm-message" href="javascript:document.deletePersonContactMechPurpose_EMAIL_${partyContactMechPurpose.contactMechId}_${i}.submit()" data-original-title="${uiLabelMap.delete}"><i class="fa fa-times red"></i></a>-->
                           </#if>
                        </div>
                     </div>
                  </form>
                  <#assign i = i+1/>
                  </#list>
                  <form method="post" action="<@ofbizUrl>createPartyContactMechPurpose#${tabIdForCurrentTab!}</@ofbizUrl>" class="form-horizontal" name="createPartyContactMechPurpose_SM" id="createPartyContactMechPurpose_SM_${contactMech.contactMechId}" novalidate="novalidate" data-toggle="validator">
                     <input type="hidden" name="activeTab" value="contactInfo" />
                     <input type="hidden" name="donePage" value='${requestURI?if_exists}'/>
                     <input type="hidden" name="contactMechId" value="${contactMech.contactMechId}"/>
                     <input type="hidden" name="partyId" value="${partyId?if_exists}"/>
                     <div class="form-group row">
                        <#assign contactMechTPMediaType = delegator.findByAnd("ContactMechTypePurpose", {"contactMechTypeId" : "SOCIAL_MEDIA_TYPE"}, [], false)>
                        <div class="col-sm-8">
                           <select name="contactMechPurposeTypeId" id="contactMechPurposeTypeId" class="ui dropdown search form-control input-sm" >
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
                        <div class="col-sm-2">
                           <!--<a class="btn btn-xs btn-primary tooltips" href="javascript:document.createPartyContactMechPurpose_EMAIL_${contactMech.contactMechId}.submit()" data-original-title="${uiLabelMap.addPurpose}"><i class="fa fa-plus info"></i></a>-->
                        </div>
                     </div>
                  </form>
               </div>
            </div>
            <hr/>
            <form method="post" action="<@ofbizUrl>updateSocialMediaTypeWebmWithPurpose#${tabIdForCurrentTab!}</@ofbizUrl>" id="updateSocialMediaTypeWebm_${contactMech.contactMechId}" class="form-horizontal" name="updateSocialMediaTypeWebm" novalidate="novalidate" data-toggle="validator">
               <input type="hidden" name="activeTab" value="contactInfo" />
               <input type="hidden" name="donePage" value='${requestURI?if_exists}'/>
               <input type="hidden" name="partyId" value="${partyId?if_exists}"/>
               <input type="hidden" name="contactMechId" value="${contactMech.contactMechId}"/>
               <input type="hidden" name="contactMechTypeId" value="${contactMech.contactMechTypeId}"/>
               <@inputRow 
               id="socialMediaId"
               name="socialMediaId"
               label=uiLabelMap.socialMediaTypeId
               placeholder=uiLabelMap.socialMediaTypeId
               value="${contactMech.infoString?if_exists}"
               required=true
               />
               <#assign socialMediaAllowSolicitation = "Y"/>
                 <#if partySummary?exists && partySummary?has_content>
               <#assign partySolicitation = Static["org.ofbiz.entity.util.EntityUtil"].getFirst(delegator.findByAnd("PartyContactMech", {"partyId" : partySummary.partyId,"contactMechId":contactMech.contactMechId}, [], false))?if_exists/>
              </#if>
               <#if partySolicitation?exists && partySolicitation?has_content>
               <#if partySolicitation.allowSolicitation?has_content && partySolicitation.allowSolicitation=="N">
               <#assign socialMediaAllowSolicitation = "N"/>
               </#if>
               </#if>
               <@dropdownCell
               id="allowSolicitation"
               name="allowSolicitation"
               label=uiLabelMap.allowSolicitation
               options=yesNoOptions
               value="${socialMediaAllowSolicitation?if_exists}"
               required=false
               allowEmpty=true
               dataLiveSearch=true
               />
               <div class="col-md-12 col-sm-12">
                  <div class="form-group row">
                     <div class="offset-sm-4 col-sm-9">
                       <@formButton
                     btn1type="button"
                     btn1label="${uiLabelMap.Update}"
                     btn1onclick="formSubmissionSocialMedia('${contactMech.contactMechId}');"                 
                   />
                        <#--
                        <@submit class="btn btn-sm btn-primary navbar-dark mt" label="${uiLabelMap.update}"/>-->
                     </div>
                  </div>
               </div>
            </form>
         </div>
      </div>
   </div>
</div>
</#if>
</#list>
</#if>
				
				<div id="updateEmailAddressModal" class="modal fade" role="dialog">
		            <div class="modal-dialog modal-md">
				     <div class="modal-dialog">
				      <div class="modal-content">
		            	<div class="modal-header">
		            	<span id="ED_message"></span>
		            	<span id="ED_cmId" style = "display:none"></span>
		            	<span id="ED_cmAId" style = "display:none"></span>
		            	<span id="ED_cmDId" style = "display:none"></span>
			               <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
			            	</div>
				            <div>
				            </div>
		            		<div class="modal-footer">
				              <input type="button" class="btn btn-sm btn-primary navbar-dark"  value="Yes" onclick="onUpdateEmailAddress();">
				              <input type="button" class="btn btn-sm btn-primary navbar-dark" data-dismiss="modal" value="No" onclick="return false;">
					        </div>
					      </div>
					      </div>
					   </div>
					</div>
					
				<div id="submitPhoneModal" class="modal fade" role="dialog">
		            <div class="modal-dialog modal-md">
				     <div class="modal-dialog">
				      <div class="modal-content">
		            	<div class="modal-header">
		            	<span id="TN_message"></span>
		            	<span id="TN_cmId" style = "display:none"></span>
		            	<span id="TN_cmAId" style = "display:none"></span>
		            	<span id="TN_cmDId" style = "display:none"></span>
			               <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
			            	</div>
				            <div>
				            </div>
		            		<div class="modal-footer">
				              <input type="submit" class="btn btn-sm btn-primary navbar-dark"  value="Update" onclick="onUpdatePhNumber();">
				              <input type="button" class="btn btn-sm btn-primary navbar-dark" data-dismiss="modal" value="Cancel" onclick="return false;">
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
    $('[name="updatePostalAddress"] #postalCode').keyup(function(e) {
        validatePostalCodeUpdate();
    });
    $('[name="updatePostalAddress"] #postalCode').change(function(e) {
        updateLoadZipCodeAssoc();
    });

    $('[name="updatePostalAddress"] #postalCodeExt').keyup(function(e) {
        validatePostalCodeExtUpdate();
    });

    $(".POSTALcontactInfo").on('shown.bs.modal', function() {
        updateModalDivId = $($(this)[0]).attr('id');
        regexUpdate = "";
        var countryGeoId = $('[id="' + updateModalDivId + '"] [name=countryGeoId]').val();
        if (countryGeoId != '') {
            regexJson = getServiceResult("getZipCodeRegex", 'countryGeoId', countryGeoId);
            regexUpdate = regexJson.regex;
        }
        updateLoadZipCodeAssoc(updateModalDivId.split('_')[1]);
        //alert(updateModalDivId.split('_')[1])
    });

    $('[name="updatePostalAddress"] .generalCountryGeo').each(function() {
        var countryListId = $(this).find(">:first-child").attr("id");
        if (countryListId) {
            populateLists($(this).find(">:first-child").attr("id"));
        }
    });

    $('[name="updatePostalAddress"] .generalCountryGeo').change(function() {
        console.log('change country...........');
        console.log('reges', regexUpdate, updateModalDivId);
        populateLists($(this).find(">:first-child").attr("id"));
        $('[id="' + updateModalDivId + '"] #postalCode').val('');
        $('[id="' + updateModalDivId + '"] #postalCodeExt').val('');
        regexUpdate = "";
        var countryGeoId = $('[id="' + updateModalDivId + '"] [name=countryGeoId]').val();
        if (countryGeoId != '') {
            regexJson = getServiceResult("getZipCodeRegex", 'countryGeoId', countryGeoId);
            regexUpdate = regexJson.regex;
        }
    });

    $('[name="updatePostalAddress"] .generalStateGeo').change(function() {
        console.log('change state...........');
        updateLoadZipCodeAssoc('');
    });
	
    console.log("===stateProvinceGeoId====" + $("#stateProvinceGeoId").val());
    updateLoadZipCodeAssoc();
  
  	$('[name="createPartyContactMechPurpose_PA"] #contactMechPurposeTypeId').change(function() {
        if ($(this).val() == '')
            $('[name="createPartyContactMechPurpose_PA"] #contactMechPurposeTypeId_error').html('Please fill out the field');
        else
            $('[name="createPartyContactMechPurpose_PA"] #contactMechPurposeTypeId_error').html('');
    });

    $('[name="createPartyContactMechPurpose_EMAIL"] #contactMechPurposeTypeId').change(function() {
        if ($(this).val() == '')
            $('[name="createPartyContactMechPurpose_EMAIL"] #contactMechPurposeTypeId_error').html('Please fill out the field');
        else
            $('[name="createPartyContactMechPurpose_EMAIL"] #contactMechPurposeTypeId_error').html('');
    });

    $('[name="createPartyContactMechPurpose_TELECOM"] #contactMechPurposeTypeId').change(function() {
        if ($(this).val() == '')
            $('[name="createPartyContactMechPurpose_TELECOM"] #contactMechPurposeTypeId_error').html('Please fill out the field');
        else
            $('[name="createPartyContactMechPurpose_TELECOM"] #contactMechPurposeTypeId_error').html('');
    });

    $('[name="createPartyContactMechPurpose_WEBURL"] #contactMechPurposeTypeId').change(function() {
        if ($(this).val() == '')
            $('[name="createPartyContactMechPurpose_WEBURL"] #contactMechPurposeTypeId_error').html('Please fill out the field');
        else
            $('[name="createPartyContactMechPurpose_WEBURL"] #contactMechPurposeTypeId_error').html('');
    });

    $('[name="createPartyContactMechPurpose_SM"] #contactMechPurposeTypeId').change(function() {
        if ($(this).val() == '')
            $('[name="createPartyContactMechPurpose_SM"] #contactMechPurposeTypeId_error').html('Please fill out the field');
        else
            $('[name="createPartyContactMechPurpose_SM"] #contactMechPurposeTypeId_error').html('');
    });
  
  	$('.updateTelecomNumber [name="contactNumber"]').keyup(function(e) {
        validatePhoneNumberUpdate();
    });
    $(".TELECOMcontactInfo").on('shown.bs.modal', function() {
        updateModalDivId = $($(this)[0]).attr('id');
    });
    var re = new RegExp(regexUpdate);
    if (re.test($('[id="' + updateModalDivId + '"] #postalCode').val())) {
        $('[id="' + updateModalDivId + '"] #postalCode_error').html('');
    }
});

var regexUpdate = '';
var updateModalDivId = '';

function populateLists(listType) {
  var splitGeneralCountryGeo = listType.split('_');
  var country = $("#" + listType).val();
  var contactMechId = splitGeneralCountryGeo[1];
  var list = "";
  var stateProvinceId = "generalStateProvinceGeo_" + contactMechId;
  var state = $("#" + stateProvinceId).val();
  $('#' + stateProvinceId).html("");
  list = $("#" + stateProvinceId);
  list.append("<option value=''>Select State</option>"); 
  <#assign defaultStateProvinceGeoId = "${postalAddress?if_exists.stateProvinceGeoId?if_exists}" />
  var defaultStateProvinceGeoId = "${defaultStateProvinceGeoId!}";
  if (country != null && country != "") {
    var urlString = "/common-portal/control/getStateDataJSON?countryGeoId=" + country + "&externalLoginKey=${requestAttributes.externalLoginKey!}";
    $.ajax({
      type: 'POST',
      async: true,
      url: urlString,
      success: function(states) {
        $("#" + stateProvinceId).empty();
        list = $("#" + stateProvinceId);
        list.append("<option value=''>Select State</option>");
        if (states.length == 0) {
          list.append("<option value = ''>N/A</option>");
        } else {
          for (var i = 0; i < states.length; i++) {
            if (state != null && state != "" && states[i].geoId == state) {
              list.append("<option  value =" + states[i].geoId + " selected>" + states[i].geoName + " </option>");
            } else {
              list.append("<option  value =" + states[i].geoId + ">" + states[i].geoName + "</option>");
            }
          }
        }
      }
    });

    $('#' + stateProvinceId).append(list);
    $('#' + stateProvinceId).dropdown('refresh');
  }
}

function validatePostalCodeUpdate() {
    var isInvalid = false;
    if (regexUpdate != '') {
        var re = new RegExp(regexUpdate);
        if (re.test($('[id="' + updateModalDivId + '"] #postalCode').val())) {
            $('[id="' + updateModalDivId + '"] #postalCode_error').html('');
        } else {
            $('[id="' + updateModalDivId + '"] #postalCode_error').html('Please enter the valid zip code');
            isInvalid = true;
        }
    }
    return isInvalid;
}

function validatePostalCodeExtUpdate() {
    var isInvalid = false;
    if ($('[id="' + updateModalDivId + '"] #postalCodeExt').val() != "" && $('[id="' + updateModalDivId + '"] .generalCountryGeo .selected').attr('data-value') == "USA") {
        var re = new RegExp("^([0-9]{4})$");
        if (re.test($('#postalCodeExt').val())) {
            $('[id="' + updateModalDivId + '"] #postalCodeExt_error').html('');
        } else {
            $('[id="' + updateModalDivId + '"] #postalCodeExt_error').html('Please enter the valid zip code extension');
            isInvalid = true;
        }
    } else {
        if ($('[id="' + updateModalDivId + '"] #postalCodeExt_error') != undefined)
            $('[id="' + updateModalDivId + '"] #postalCodeExt_error').html('');
    }
    return isInvalid;
}

function validatePhoneNumberUpdate() {
    var isInvalid = false;
    var obj = $('[id="' + updateModalDivId + '"] [name="contactNumber"]');
    if (obj.val() != "") {
        var re = new RegExp("^([0-9]{1,13})$");
        console.log(re, obj[0].id, obj.val());
        if (re.test(obj.val())) {
            $('[id="' + updateModalDivId + '"] #' + obj[0].id + '_error').html('');
        } else {
            $('[id="' + updateModalDivId + '"] #' + obj[0].id + '_error').html('Please enter the valid phone number');
            isInvalid = true;
        }
    }
    return isInvalid;
}

function updateLoadZipCodeAssoc(contactMechId) {
    if (!$('[id="' + updateModalDivId + '"] [name=stateProvinceGeoId]').val()) {
        return;
    }
    
    //$('#cityLi_' + contactMechId + '_desc').val($('#city_' + contactMechId).val());
	//$('#cityLi_' + contactMechId + '_val').val($('#city_' + contactMechId).val());
    
    $('#zipcodeassoc-search-form input[name="state"]').val( $('[id="' + updateModalDivId + '"] [name=stateProvinceGeoId]').val() );
	$('#zipcodeassoc-search-form input[name="zip"]').val( $('[id="' + updateModalDivId + '"] [name=postalCode]').val() );
    
    //var cityOptions = '<option value="" selected="">Select City</option>';
    var countyOptions = '<option value="" selected="">Select County</option>';

    //let cityList = new Map();
    let countyList = new Map();

    $.ajax({
        type: "POST",
        url: "/uiadv-portal/control/searchZipCodeAssocs",
        data: {
            "state": $('[id="' + updateModalDivId + '"] [name=stateProvinceGeoId]').val(),
            "zip": $('[id="' + updateModalDivId + '"] [name=postalCode]').val(),
            "externalLoginKey": "${requestAttributes.externalLoginKey!}"
        },
        async: false,
        success: function(result) {
            if (result.code == 200) {
                for (var i = 0; i < result.data.length; i++) {
                    var data = result.data[i];
                    //cityList.set(data.city, data.city);
                    countyList.set(data.county, data.county);
                }
            }
        }
    });
	/*
    for (let key of cityList.keys()) {
        if (cityList.size === 1) {
            cityOptions += '<option value="' + key + '" selected>' + cityList.get(key) + '</option>';
        } else {
            cityOptions += '<option value="' + key + '">' + cityList.get(key) + '</option>';
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

	$('#cityLi_' + contactMechId + '_desc').val($('#city_' + contactMechId).val());
	$('#cityLi_' + contactMechId + '_val').val($('#city_' + contactMechId).val());
	/*
    $('[id="' + updateModalDivId + '"] [name=city]').html(cityOptions);
    if (contactMechId) {
        $('#cityLi_' + contactMechId).val($('#city_' + contactMechId).val());
    }
    $('[id="' + updateModalDivId + '"] [name=city]').dropdown('refresh');
	*/
	
    $('[id="' + updateModalDivId + '"] [name=county]').html(countyOptions);
    if (contactMechId) {
        $('#countyGeoIdLi_' + contactMechId).val($('#countyGeoId_' + contactMechId).val());
    }
    $('[id="' + updateModalDivId + '"] [name=county]').dropdown('refresh');

}

//added for duplicate records check
function formSubmissionEmail(contactMechId, cmpTypeId, eId) {
    var partyId = '';
    var contactMechId = contactMechId;
    var cmpTypeId = cmpTypeId;
    var eId = eId;
    var primaryEmail = eId.value;
    var accType = document.getElementById("accType").value;
    var partyId = document.getElementById("partyId").value;
    var ctId = $('#contactMechPurposeTypeId').val();
    var formName = "updateEmailAddress";
    var allVals = [];
    $('input[name="contactMechPurposeTypeIds"]:checked').each(function() {
        allVals.push($(this).val());

    });
    // alert(allVals.length);     
    var fName = "createPartyContactMechPurpose_EMAIL_" + contactMechId;
    var cmpType = document.getElementById(fName).elements.namedItem("contactMechPurposeTypeId").value;
    var delName = "EMAILcontactInfo_" + contactMechId;
    var existingEmailCmptSize = $('[id="' + delName + '"] #existingEmailCmptSize').val();
    if ((existingEmailCmptSize === "0")) {
        if (cmpType == "") {
            // $('[id="'+fName+'"] #'+errorId).html('Please fill out the field');
            $('[id="' + fName + '"] #contactMechPurposeTypeId_error').html('Please fill out the field');
            return false;
        } else {
            $('[id="' + fName + '"] #contactMechPurposeTypeId_error').html('');
        }
    }
    if ((allVals.length == existingEmailCmptSize)) {
        if (cmpType == "") {
            // $('[id="'+fName+'"] #'+errorId).html('Please fill out the field');
            $('[id="' + fName + '"] #contactMechPurposeTypeId_error').html('Please Select Contact Purpose To Remove Existing One.');
            return false;
        } else {
            $('[id="' + fName + '"] #contactMechPurposeTypeId_error').html('');
        }
    }
    if (cmpType === "PRIMARY_EMAIL") {
        $.ajax({
            type: "POST",
            url: "/common-portal/control/getDuplicateEmailList",
            async: true,
            data: {
                "primaryEmail": primaryEmail,
                "partyId": partyId,
                "accType": accType,
                "screenType": "UPDATE",
                "externalLoginKey": "${requestAttributes.externalLoginKey!}"
            },
            success: function(data) {
                var message = data.Error_Message;
                loadActivity(message, formName, contactMechId, allVals, cmpType);
            }
        });
    } else {
        var message = "NO_RECORDS";
        loadActivity(message, formName, contactMechId, allVals, cmpType);
    }
}

//added for duplicate records check
function formSubmissionPhone(contactMechId, cmpTypeId) {
    if (validatePhoneNumberUpdate()) {
        return false;
    }
    var primaryPhoneNumber = document.getElementsByName("contactNumber")[0].value;
    var accType = document.getElementById("accType").value;
    var partyId = document.getElementById("partyId").value;
    var cmtype = document.getElementById("contactMechPurposeTypeId").value;
    var formName = "updateTelecomNumber";
    var contactMechId = contactMechId;
    var cmpTypeId = cmpTypeId;
    var ctId = $('#contactMechPurposeTypeId_TELECOM').val();
    var allVals = [];
    $('input[name="contactMechPurposeTypeIds"]:checked').each(function() {
        allVals.push($(this).val());
    });
    var fName = "createPartyContactMechPurpose_TELECOM_" + contactMechId;
    var cmpType = document.getElementById(fName).elements.namedItem("contactMechPurposeTypeId").value;
    var errorId = "contactMechPurposeTypeId_error_" + contactMechId;
    var delName = "TELECOMcontactInfo_" + contactMechId;
    var existingTelecomCmptSize = $('[id="' + delName + '"] #existingTelecomCmptSize').val();
    if ((existingTelecomCmptSize === "0")) {
        if (cmpType == "") {
            $('[id="' + fName + '"] #contactMechPurposeTypeId_error').html('Please fill out the field');
            return false;
        } else {
            $('[id="' + fName + '"] #contactMechPurposeTypeId_error').html('');
        }
    }

    if ((allVals.length == existingTelecomCmptSize)) {
        if (cmpType == "") {
            $('[id="' + fName + '"] #contactMechPurposeTypeId_error').html('Please Select Contact Purpose To Remove Existing One.');
            return false;
        } else {
            $('[id="' + fName + '"] #contactMechPurposeTypeId_error').html('');
        }
    }
    if (cmpType === "PHONE_MOBILE") {
        $.ajax({
            type: "POST",
            url: "/common-portal/control/getDuplicatePhoneNumber",
            async: true,
            data: {
                "primaryPhoneNumber": primaryPhoneNumber,
                "partyId": partyId,
                "accType": accType,
                "screenType": "UPDATE",
                "externalLoginKey": "${requestAttributes.externalLoginKey!}"
            },
            success: function(data) {
                var message = data.Error_Message;
                loadActivity(message, formName, contactMechId, allVals, cmpType);
            }
        });
    } else {
        var message = "NO_RECORDS";
        loadActivity(message, formName, contactMechId, allVals, cmpType);
    }
}

function formSubmissionUpdate(contactMechId) {
    var valid = true;
    var fromId = "updatePostalAddress_" + contactMechId; 
  	<#if isActUspsAddrVal ? has_content && isActUspsAddrVal == "Y" >
        if (valid && $('#generalCountryGeo_' + contactMechId).val() == "USA") {
            var data = {
                "Address1": fromId + " input[name=address1]",
                "Address2": fromId + " input[name=address2]",
                "Zip5": fromId + " input[name=postalCode]",
                "Zip4": fromId + " input[name=postalCodeExt]",
                "City": fromId + " input[id=cityLi_"+contactMechId+"_desc]",
                "State": "generalStateProvinceGeo_" + contactMechId,
                "Business": fromId + " input[name=isBusiness]",
                "Vacant": fromId + " input[name=isVacant]"
            };
            valid = USPSUTIL.performUspsAddrValidation(data, '${requestAttributes.externalLoginKey!}');
            if (valid) {
                $('#' + fromId + " input[name=isUspsAddrVerified]").val('Y');
            }
        } 
  	</#if>
  	
  	$('#' + fromId + ' input[name="city"]').val( $("[id='"+fromId+"'] #cityLi_"+contactMechId+"_desc").val() );
  	
    return valid;
}

//added for duplicate records check paddress
function formSubmissionPaddress(contactMechId, pCode) {
    // validation
    if (validateCurrentForm()) {
        return false;
    }
    console.log('sdgsg', this.updatePostalAddress);
    // end validation

    var allVals = [];
    $('input[name="contactMechPurposeTypeIds"]:checked').each(function() {
        allVals.push($(this).val());
    });
    var fName = "createPartyContactMechPurpose_PA_" + contactMechId;
    var cmpType = document.getElementById(fName).elements
        .namedItem("contactMechPurposeTypeId").value;
    var groupName = $('#groupName').val();
    var firstName = $('#firstName').val();
    var lastName = $('#lastName').val();
    var accType = $('#accType').val();
    var postalCode = pCode.value;
    var partyId = document.getElementById("partyId").value;
    var formName = "updatePostalAddress";
    var delName = "POSTALcontactInfo_" + contactMechId;
    var existingPostalCmptSize = $(
        '[id="' + delName + '"] #existingPostalCmptSize').val();
    if ((existingPostalCmptSize === "0")) {
        if (cmpType == "") {
            $('[id="' + fName + '"] #contactMechPurposeTypeId_error').html(
                'Please fill out the field');
            return false;
        } else {
            $('[id="' + fName + '"] #contactMechPurposeTypeId_error').html('');
        }
    }
    if ((allVals.length == existingPostalCmptSize)) {
        if (cmpType == "") {
            $('[id="' + fName + '"] #contactMechPurposeTypeId_error').html(
                'Please Select Contact Purpose To Remove Existing One.');
            return false;
        } else {
            $('[id="' + fName + '"] #contactMechPurposeTypeId_error').html('');
        }
    }
    $.ajax({
        type: "POST",
        url: "/common-portal/control/getDuplicateAddress",
        async: true,
        data: {
            "groupName": groupName,
            "accType": accType,
            "firstName": firstName,
            "lastName": lastName,
            "partyId": partyId,
            "postalCode": postalCode,
            "screenType": "UPDATE",
            "externalLoginKey": "${requestAttributes.externalLoginKey!}"
        },
        success: function(data) {
            var message = data.Error_Message;
            loadActivity(message, formName, contactMechId, allVals, cmpType);
        }
    });
    return false;
}

function formSubmissionWebUrl(contactMechId) {
    var allVals = [];
    $('input[name="contactMechPurposeTypeIds"]:checked').each(function() {
        allVals.push($(this).val());
    });
    var formName = "createPartyContactMechPurpose_WEBURL_" + contactMechId;
    var cmpType = document.getElementById(formName).elements.namedItem("contactMechPurposeTypeId").value;
    var fName = "#updateContactMech_" + contactMechId;
    var errorId = "contactMechPurposeTypeId_error_" + contactMechId;
    var delName = "WEBcontactInfo_" + contactMechId;
    var existingWebCmptSize = $('[id="' + delName + '"] #existingWebCmptSize').val();
    if ((existingWebCmptSize === "0")) {
        if (cmpType == "") {
            $('[id="' + formName + '"] #contactMechPurposeTypeId_error').html('Please fill out the field');
            return false;
        } else {
            $('[id="' + formName + '"] #contactMechPurposeTypeId_error').html('');
        }
    }
    if ((allVals.length == existingWebCmptSize)) {
        if (cmpType == "") {
            $('[id="' + formName + '"] #contactMechPurposeTypeId_error').html('Please Select Contact Purpose To Remove Existing One.');
            return false;
        } else {
            $('[id="' + formName + '"] #contactMechPurposeTypeId_error').html('');
        }
    }
    $(fName).append("<input type='hidden' id='contactMechPurposeTypeId_APA' name='contactMechPurposeTypeId_APA' value=\"" + cmpType + "\">");
    $(fName).append("<input type='hidden' id='deleteCMPurposeTypeIds_DPA' name='deleteCMPurposeTypeIds_DPA' value=\"" + allVals + "\">");
    $(fName).submit();
}

function formSubmissionSocialMedia(contactMechId) {
    var allVals = [];
    $('input[name="contactMechPurposeTypeIds"]:checked').each(function() {
        allVals.push($(this).val());
    });
    var formName = "createPartyContactMechPurpose_SM_" + contactMechId;
    var cmpType = document.getElementById(formName).elements.namedItem("contactMechPurposeTypeId").value;
    var fName = "#updateSocialMediaTypeWebm_" + contactMechId;
    var errorId = "contactMechPurposeTypeId_error_" + contactMechId;
    var delName = "SocialMediacontactInfo_" + contactMechId;
    var existingSMCmptSize = $('[id="' + delName + '"] #existingSMCmptSize').val();
    if ((existingSMCmptSize === "0")) {
        if (cmpType == "") {
            $('[id="' + formName + '"] #contactMechPurposeTypeId_error').html('Please fill out the field');
            return false;
        } else {
            $('[id="' + formName + '"] #contactMechPurposeTypeId_error').html('');
        }
    }
    if ((allVals.length == existingSMCmptSize)) {
        if (cmpType == "") {
            $('[id="' + formName + '"] #contactMechPurposeTypeId_error').html('Please Select Contact Purpose To Remove Existing One.');
            return false;
        } else {
            $('[id="' + formName + '"] #contactMechPurposeTypeId_error').html('');
        }
    }
    $(fName).append("<input type='hidden' id='contactMechPurposeTypeId_APA' name='contactMechPurposeTypeId_APA' value=\"" + cmpType + "\">");
    $(fName).append("<input type='hidden' id='deleteCMPurposeTypeIds_DPA' name='deleteCMPurposeTypeIds_DPA' value=\"" + allVals + "\">");
    $(fName).submit();
}

function loadActivity(message, formName, contactMechId, allVals, cmpType) {
    //alert(message);
    //alert(formName);
    var formName = formName;
    var contactMechId = contactMechId;
    var allVals = allVals;
    var cmpType = cmpType;
    if (message === "NO_RECORDS") {
        if (formName === "updateEmailAddress") {
            var fName = "#updateEmailAddress_" + contactMechId;
            $(fName).append("<input type='hidden' id='contactMechPurposeTypeId_APA' name='contactMechPurposeTypeId_APA' value=\"" + cmpType + "\">");
            $(fName).append("<input type='hidden' id='deleteCMPurposeTypeIds_DPA' name='deleteCMPurposeTypeIds_DPA' value=\"" + allVals + "\">");
            $(fName).submit();
        }
        if (formName === "updatePostalAddress") {
            var fName = "#updatePostalAddress_" + contactMechId;
            document.getElementById("contactMechId").value = contactMechId;
            // document.getElementById("contactMechPurposeTypeId_APA").value=cmpType;
            //document.getElementById("deleteCMPurposeTypeIds_DPA").value=allVals;
            $(fName).append("<input type='hidden' id='contactMechPurposeTypeId_APA' name='contactMechPurposeTypeId_APA' value=\"" + cmpType + "\">");
            $(fName).append("<input type='hidden' id='deleteCMPurposeTypeIds_DPA' name='deleteCMPurposeTypeIds_DPA' value=\"" + allVals + "\">");
            $(fName).submit();
        }
        if (formName === "updateTelecomNumber") {
            var fName = "#updateTelecomNumber_" + contactMechId;
            $(fName).append("<input type='hidden' id='contactMechPurposeTypeId_APA' name='contactMechPurposeTypeId_APA' value=\"" + cmpType + "\">");
            $(fName).append("<input type='hidden' id='deleteCMPurposeTypeIds_DPA' name='deleteCMPurposeTypeIds_DPA' value=\"" + allVals + "\">");
            $(fName).submit();
        }
    } else {
        //alert("in else "+formName);
        if (formName === "updateEmailAddress") {
            $('#updateEmailAddressModal').modal('show');
            $("#ED_message").html(message);
            $("#ED_cmId").html(contactMechId);
            $("#ED_cmAId").html(cmpType);
            $("#ED_cmDId").html(allVals);

        }
        if (formName === "updatePostalAddress") {
            $('#submitAddressModal').modal('show');
            $("#AD_message").html(message);
            $("#AD_cmId").html(contactMechId);
            $("#AD_cmAId").html(cmpType);
            $("#AD_cmDId").html(allVals);
        }
        if (formName === "updateTelecomNumber") {
            $('#submitPhoneModal').modal('show');
            $("#TN_message").html(message);
            $("#TN_cmId").html(contactMechId);
            $("#TN_cmAId").html(cmpType);
            $("#TN_cmDId").html(allVals);
        }
    }
}

//ended
function onUpdateEmailAddress() {
    var cid = document.getElementById('ED_cmId').innerHTML;
    var cmpType = document.getElementById('ED_cmAId').innerHTML;
    var allVals = document.getElementById('ED_cmDId').innerHTML;
    //alert(cid);
    var fName = "#updateEmailAddress_" + cid;
    $(fName).append("<input type='hidden' id='contactMechPurposeTypeId_APA' name='contactMechPurposeTypeId_APA' value=\"" + cmpType + "\">");
    $(fName).append("<input type='hidden' id='deleteCMPurposeTypeIds_DPA' name='deleteCMPurposeTypeIds_DPA' value=\"" + allVals + "\">");
    $(fName).submit();
}

function onUpdatePaddress() {
    var cid = document.getElementById('AD_cmId').innerHTML;
    var cmpType = document.getElementById('AD_cmAId').innerHTML;
    var allVals = document.getElementById('AD_cmDId').innerHTML;
    var fName = "#updatePostalAddress_" + cid;
    $(fName).append("<input type='hidden' id='contactMechPurposeTypeId_APA' name='contactMechPurposeTypeId_APA' value=\"" + cmpType + "\">");
    $(fName).append("<input type='hidden' id='deleteCMPurposeTypeIds_DPA' name='deleteCMPurposeTypeIds_DPA' value=\"" + allVals + "\">");
    $(fName).submit();
}

function onUpdatePhNumber() {
    var cid = document.getElementById('TN_cmId').innerHTML;
    var cmpType = document.getElementById('TN_cmAId').innerHTML;
    var allVals = document.getElementById('TN_cmDId').innerHTML;
    var fName = "#updateTelecomNumber_" + cid;
    $(fName).append("<input type='hidden' id='contactMechPurposeTypeId_APA' name='contactMechPurposeTypeId_APA' value=\"" + cmpType + "\">");
    $(fName).append("<input type='hidden' id='deleteCMPurposeTypeIds_DPA' name='deleteCMPurposeTypeIds_DPA' value=\"" + allVals + "\">");
    $(fName).submit();
}

function validateCurrentForm() {
    //alert("in form validation");
    var isReturnFalse = false;

    //for input select fields
    $('[id="' + updateModalDivId + '"] #updatePostalAddress input, [id="' + updateModalDivId + '"] #updatePostalAddress select').each(function() {
        if (!$(this).prop('required')) {
            //    		console.log("NR");
        } else {
            //	        console.log("IR");
            if (this.value == "" || this.value == undefined || this.value == null) {
                isReturnFalse = true;
                return false;
            }
        }
    });
    if ((!isReturnFalse) && (validatePostalCodeUpdate() || validatePostalCodeExtUpdate())) {
        isReturnFalse = true;
    }
    return isReturnFalse;
}
</script>
</#if>