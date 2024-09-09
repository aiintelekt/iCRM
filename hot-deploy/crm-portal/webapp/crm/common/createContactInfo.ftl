<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<#assign partyId= request.getParameter("partyId")!>
<#if partyId?exists && partyId?has_content>
<#assign requestURI = "viewContact"/>
<#if request.getRequestURI().contains("viewLead")>
<#assign requestURI = "viewLead"/>
<#elseif request.getRequestURI().contains("viewAccount")>
<#assign requestURI = "viewAccount"/>
<#elseif request.getRequestURI().contains("viewCustomer")>
<#assign requestURI = "viewCustomer"/>
</#if>
<!-- Email Address -->
<div id="createEMAILcontactInfo" class="modal fade" role="dialog">
   <div class="modal-dialog modal-md">
      <!-- Modal content-->
      <form method="post" action="<@ofbizUrl>createEmailAddress</@ofbizUrl>" id="createEmailAddress" class="form-horizontal" name="createEmailAddress" novalidate="novalidate" data-toggle="validator">
         <input type="hidden" name="activeTab" value="contactInfo" />
         <input type="hidden" name="donePage" value='${requestURI?if_exists}'/>
         <input type="hidden" name="contactMechTypeId" value="EMAIL_ADDRESS">
         <input type="hidden" name="partyId" value="${partyId?if_exists}">
         <div class="modal-content">
            <div class="modal-header">
               <h4 class="modal-title">${uiLabelMap.email}</h4>
               <button type="reset" class="close" data-dismiss="modal">&times;</button>
            </div>
            <div class="modal-body">
            
               <#assign contactMechTPEmail = delegator.findByAnd("ContactMechTypePurpose", {"contactMechTypeId" : "EMAIL_ADDRESS"}, [], false)>
               <div class="form-group row">
                  <label  class="col-sm-4 col-form-label">${uiLabelMap.CommonPurpose}</label> 
                  <div class="col-sm-7">
                     <select name="contactMechPurposeTypeId" id="contactMechPurposeTypeId" class="ui dropdown form-control input-sm" >
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
                  </div>
               </div>
               
               <@inputRow 
               id="emailAddress"
               name="emailAddress"
               label=uiLabelMap.email
               placeholder=uiLabelMap.email
               value=""
               type="email"
               required=true
               dataError="Please enter valid email address"
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
                        <input type="submit" class="btn btn-sm btn-primary navbar-dark mt" value="${uiLabelMap.create}"/>
                     </div>
                  </div>
               </div>
            </div>
         </div>
      </form>
   </div>
</div>
<!-- Email Address End-->
<!--Postal Address -->
<div id="createPOSTALcontactInfo" class="modal fade" role="dialog">
   <div class="modal-dialog modal-lg">
      <form method="post" action="<@ofbizUrl>createPostalAddress</@ofbizUrl>" id="createPostalAddress" class="form-horizontal" name="createPostalAddress" novalidate="novalidate" data-toggle="validator">
         <input type="hidden" name="activeTab" value="contactInfo" />
         <input type="hidden" name="donePage" value='${requestURI?if_exists}'/>
         <input type="hidden" name="contactMechTypeId" value="POSTAL_ADDRESS">
         <input type="hidden" name="partyId" value="${partyId?if_exists}">
         <div class="modal-content">
            <div class="modal-header">
               <h4 class="modal-title">${uiLabelMap.postalAddress}</h4>
               <button type="reset" class="close" data-dismiss="modal">&times;</button>
            </div>
            <div class="modal-body">
               <!-- Modal content-->
               <#assign contactMechTPPostal = delegator.findByAnd("ContactMechTypePurpose", {"contactMechTypeId" : "POSTAL_ADDRESS"}, [], false)>
               <div class="form-group row">
                  <label  class="col-sm-4 col-form-label">${uiLabelMap.CommonPurpose}</label> 
                  <div class="col-sm-7">
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
               <@inputRow 
               id="city"
               name="city"
               label=uiLabelMap.city
               placeholder=uiLabelMap.city
               value=""
               required=false
               />
               <div class="form-group row">
                  <label class="col-sm-4 col-form-label">${uiLabelMap.country}</label>
                  <div class="col-sm-7">
                     <@inputCountry  
                     name="countryGeoId"
                     defaultCountry=true
                     dataLiveSearch=true
                     tooltip=uiLabelMap.country
                     required=false
                     />
                     <div class="help-block with-errors"></div>
                  </div>
               </div>
               <div class="form-group row">
                  <label class="col-sm-4 col-form-label">${uiLabelMap.state}</label>
                  <div class="col-sm-7">
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
               label=uiLabelMap.postalCode
               placeholder=uiLabelMap.postalCode
               value=""
               type="number"
               required=false
               min=1
               />
               <@inputRow 
               id="postalCodeExt"
               name="postalCodeExt"
               label=uiLabelMap.postalCodeExt
               placeholder=uiLabelMap.postalCodeExt
               value=""
               type="number"
               required=false
               min=1
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
               id="addressValidInd"
               name="addressValidInd"
               label=uiLabelMap.deliverable
               options=yesNoOptions
               required=false
               allowEmpty=false
               dataLiveSearch=true
               />
               <div class="col-md-12 col-sm-12">
                  <div class="form-group row">
                     <div class="offset-sm-4 col-sm-9">
                        <@submit class="btn btn-sm btn-primary navbar-dark mt" label="${uiLabelMap.create}"/>
                     </div>
                  </div>
               </div>
            </div>
         </div>
      </form>
   </div>
</div>
<!--Postal Address End-->
<!-- Telecom Number-->
<div id="createTELECOMcontactInfo" class="modal fade" role="dialog">
   <div class="modal-dialog modal-md">
      <form method="post" action="<@ofbizUrl>createTelecomNumber</@ofbizUrl>" id="createTelecomNumber" class="form-horizontal" name="createTelecomNumber" novalidate="novalidate" data-toggle="validator">
         <input type="hidden" name="activeTab" value="contactInfo" />
         <input type="hidden" name="donePage" value='${requestURI?if_exists}'/>
         <input type="hidden" name="contactMechTypeId" value="TELECOM_NUMBER">
         <input type="hidden" name="partyId" value="${partyId?if_exists}">
         <div class="modal-content">
            <div class="modal-header">
               <h4 class="modal-title">${uiLabelMap.phoneNumber}</h4>
               <button type="reset" class="close" data-dismiss="modal">&times;</button>
            </div>
            <div class="modal-body">
            <#assign contactMechTPTelecom = delegator.findByAnd("ContactMechTypePurpose", {"contactMechTypeId" : "TELECOM_NUMBER"}, [], false)>
               <div class="form-group row">
                  <label  class="col-sm-4 col-form-label">${uiLabelMap.CommonPurpose}</label> 
                  <div class="col-sm-7">
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
                  </div>
               </div>
               <@inputRow 
               id="contactNumberss"
               name="contactNumber"
               label=uiLabelMap.phoneNumber
               placeholder=uiLabelMap.phoneNumber
               value=""
               required=true
               dataError="Please enter phone number"
               maxlength=10
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
                        <@submit class="btn btn-sm btn-primary navbar-dark mt" label="${uiLabelMap.create}"/>
                     </div>
                  </div>
               </div>
            </div>
         </div>
      </form>
   </div>
</div>
<!-- Web Address-->
<div id="createWEBcontactInfo" class="modal fade" role="dialog">
   <div class="modal-dialog modal-md">
      <form method="post" action="<@ofbizUrl>createContactMech</@ofbizUrl>" id="createContactMech" class="form-horizontal" name="createContactMech" novalidate="novalidate" data-toggle="validator">
         <input type="hidden" name="activeTab" value="contactInfo" />
         <input type="hidden" name="donePage" value='${requestURI?if_exists}'/>
         <input type="hidden" name="contactMechTypeId" value="WEB_ADDRESS">
         <input type="hidden" name="partyId" value="${partyId?if_exists}">
         <div class="modal-content">
            <div class="modal-header">
               <h4 class="modal-title">${uiLabelMap.webAddress}</h4>
               <button type="reset" class="close" data-dismiss="modal">&times;</button>
            </div>
            <div class="modal-body">
               <#assign contactMechTPWeb = delegator.findByAnd("ContactMechTypePurpose", {"contactMechTypeId" : "WEB_ADDRESS"}, [], false)>
               <div class="form-group row">
                  <label  class="col-sm-4 col-form-label">${uiLabelMap.CommonPurpose}</label> 
                  <div class="col-sm-7">
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
                        <@submit class="btn btn-sm btn-primary navbar-dark mt" label="${uiLabelMap.create}"/>
                     </div>
                  </div>
               </div>
            </div>
         </div>
      </form>
   </div>
</div>
<!-- Social Media Type -->
<div id="createSocialMediacontactInfo" class="modal fade" role="dialog">
   <div class="modal-dialog modal-md">
      <form method="post" action="<@ofbizUrl>createSocialMediaTypeWebm</@ofbizUrl>" id="createSocialMediaTypeWebm" class="form-horizontal" name="createSocialMediaTypeWebm" novalidate="novalidate" data-toggle="validator">
         <input type="hidden" name="activeTab" value="contactInfo" />
         <input type="hidden" name="donePage" value='${requestURI?if_exists}'/>
         <input type="hidden" name="contactMechTypeId" value="SOCIAL_MEDIA_TYPE">
         <input type="hidden" name="partyId" value="${partyId?if_exists}">
         <div class="modal-content">
            <div class="modal-header">
               <h4 class="modal-title">${uiLabelMap.socialMediaType}</h4>
               <button type="reset" class="close" data-dismiss="modal">&times;</button>
            </div>
            <div class="modal-body">
               <#assign contactMechTPMediaType = delegator.findByAnd("ContactMechTypePurpose", {"contactMechTypeId" : "SOCIAL_MEDIA_TYPE"}, [], false)>
               <div class="form-group row">
                  <label  class="col-sm-4 col-form-label">${uiLabelMap.CommonPurpose}</label> 
                  <div class="col-sm-7">
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
                        <input type="submit" class="btn btn-sm btn-primary navbar-dark mt" value="${uiLabelMap.create}"/>
                     </div>
                  </div>
               </div>
            </div>
         </div>
      </form>
   </div>
</div>
</#if>
<script>
$("#contactNumberss").keyup(function (e){  
  var phoneNumber = $("#contactNumberss").val();
  
  if(phoneNumber !=''){
      var re = new RegExp("^[0-9]{10}$");
      if (re.test(phoneNumber)) {
           $("#contactNumberss_error").empty();
      } else {
           $("#contactNumberss_error").empty();
           $("#contactNumberss_error").append('<ul class="list-unstyled text-danger"><li>Please enter valid phone number.</li></ul>');
      }
  }else{
    $("#contactNumberss_error").html("");
    $("#contactNumberss_error").append('<ul class="list-unstyled text-danger"><li>Please enter phone number</li></ul>');
  }
  if(phoneNumber.length > 10) {
        $("#contactNumberss_error").html("");
  }
});
$("#createTelecomNumber").submit(function (){
  var phoneNumber = $("#contactNumberss").val();
  
  if(phoneNumber !=''){
      var re = new RegExp("^[0-9]{10}$");
      if (re.test(phoneNumber)) {
           $("#contactNumberss_error").empty();
           return true;
      } else {
           $("#contactNumberss_error").empty();
           $("#contactNumberss_error").append('<ul class="list-unstyled text-danger"><li>Please enter valid phone number</li></ul>');
           return false;
      }
  }else{
    $("#contactNumberss_error").html("");
    $("#contactNumberss_error").append('<ul class="list-unstyled text-danger"><li>Please enter phone number</li></ul>');
    return false;
  }
});
</script>