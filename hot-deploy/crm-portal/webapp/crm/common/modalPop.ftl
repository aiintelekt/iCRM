<!-- Reassign the person Responsible-->
<div id="reassign1" class="modal fade" role="dialog">
   <div class="modal-dialog modal-md">
      <!-- Modal content-->
      <div class="modal-content">
         <div class="modal-header">
            <h4 class="modal-title">Modal Header</h4>
            <button type="reset" class="close" data-dismiss="modal">&times;</button>
         </div>
         <div class="modal-body">
            <p>Some text in the modal.</p>
         </div>
         <div class="modal-footer">
            <button type="reset" class="btn btn-sm btn-primary navbar-dark" data-dismiss="modal">Close</button>
         </div>
      </div>
   </div>
</div>
<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<#assign partyId= request.getParameter("partyId")!>
<#if partyId?exists && partyId?has_content>
<#assign requestURI = "viewContact"/>
<#if request.getRequestURI().contains("viewLead")>
<#assign requestURI = "viewLead"/>
<#elseif request.getRequestURI().contains("viewAccount")>
<#assign requestURI = "viewAccount"/>
</#if>
<!-- Email Address -->
<#if contactMeches?exists && contactMeches?has_content>
<#list contactMeches as contactMechMap>
<#assign contactMech = contactMechMap.contactMech>
<#assign partycontactMech = contactMechMap.partyContactMech>
<#if contactMech.contactMechId ==  partycontactMech.contactMechId && contactMech.contactMechTypeId == "EMAIL_ADDRESS">
<div id="EMAILcontactInfo_${contactMech.contactMechId}" class="modal fade" role="dialog">
   <div class="modal-dialog modal-md">
      <!-- Modal content-->
      <div class="modal-content">
         <div class="modal-header">
            <h4 class="modal-title">${uiLabelMap.email}</h4>
            <button type="reset" class="close" data-dismiss="modal">&times;</button>
         </div>
         <div class="modal-body">
            <div class="row padding">
               <div class="col-md-4 col-sm-4 ">
                  <div class="form-group row text-danger">
                     <label  class="col-sm-12 col-form-label">${uiLabelMap.contactPurpose}*</label>
                  </div>
               </div>
               <div class="col-md-8 col-sm-8">
                  <#assign i = 0/>
                  <#list contactMechMap.partyContactMechPurposes as partyContactMechPurpose>
                  <form method="post" action="<@ofbizUrl>deletePartyContactMechPurpose</@ofbizUrl>" class="form-horizontal" name="deletePersonContactMechPurpose_EMAIL_${partyContactMechPurpose.contactMechId}_${i}" novalidate="novalidate" data-toggle="validator">
                     <input type="hidden" name="activeTab" value="contactInfo" />
                     <input type="hidden" name="donePage" value='${requestURI?if_exists}'/>
                     <input type="hidden" name="contactMechId" value="${partyContactMechPurpose.contactMechId}"/>
                     <input type="hidden" name="partyId" value="${partyId?if_exists}"/>
                     <input type="hidden" name="contactMechPurposeTypeId" value="${partyContactMechPurpose.contactMechPurposeTypeId}"/>
                     <input type="hidden" name="fromDate" value="${partyContactMechPurpose.fromDate?if_exists}"/>
                     <div class="form-group row">
                        <div class="col-sm-10">
                           <label class="col-form-label input-sm fw">
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
                        <div class="col-sm-2">
                           <#if !partyContactMechPurpose.contactMechPurposeTypeId.contains("IDEAL")>
                           <a class="btn btn-xs btn-secondary btn-danger tooltips confirm-message" href="javascript:document.deletePersonContactMechPurpose_EMAIL_${partyContactMechPurpose.contactMechId}_${i}.submit()" data-original-title="${uiLabelMap.delete}"><i class="fa fa-times red"></i></a>
                           </#if>
                        </div>
                     </div>
                  </form>
                  <#assign i = i+1/>
                  </#list>
                  <form method="post" action="<@ofbizUrl>createPartyContactMechPurpose</@ofbizUrl>" class="form-horizontal" name="createPartyContactMechPurpose_EMAIL_${contactMech.contactMechId}" novalidate="novalidate" data-toggle="validator">
                     <input type="hidden" name="activeTab" value="contactInfo" />
                     <input type="hidden" name="donePage" value='${requestURI?if_exists}'/>
                     <input type="hidden" name="contactMechId" value="${contactMech.contactMechId}"/>
                     <input type="hidden" name="partyId" value="${partyId?if_exists}"/>
                     <div class="form-group row">
                        <#assign contactMechTPEmail = delegator.findByAnd("ContactMechTypePurpose", {"contactMechTypeId" : "EMAIL_ADDRESS"}, [], false)>
                        <div class="col-sm-10">
                           <select name="contactMechPurposeTypeId" id="contactMechPurposeTypeId" class="ui dropdown search form-control input-sm" >
                              <#--<option value="" data-content="<span class='nonselect'>Please Select</span>" selected>Please Select</option>-->
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
                        <div class="col-sm-2">
                           <a class="btn btn-xs btn-primary tooltips" href="javascript:document.createPartyContactMechPurpose_EMAIL_${contactMech.contactMechId}.submit()" data-original-title="${uiLabelMap.addPurpose}"><i class="fa fa-plus info"></i></a>
                        </div>
                     </div>
                  </form>
               </div>
            </div>
            <hr/>
            <form method="post" action="<@ofbizUrl>updateEmailAddress</@ofbizUrl>" id="updateEmailAddress" class="form-horizontal" name="updateEmailAddress" novalidate="novalidate" data-toggle="validator">
               <input type="hidden" name="activeTab" value="contactInfo" />
               <input type="hidden" name="donePage" value='${requestURI?if_exists}'/>
               <input type="hidden" name="contactMechId" value="${contactMech.contactMechId}"/>
               <input type="hidden" name="partyId" value="${partyId?if_exists}"/>
               
               <@inputRow
               id="emailAddress"
               name="emailAddress"
               label=uiLabelMap.email
               placeholder=uiLabelMap.email
               value="${contactMech.infoString?if_exists}"
               type="email"
               required=true
               dataError="Please enter valid email address"
               />
               <#assign emailAllowSolicitation = "Y"/>
               <#assign emailValidInd = "Y"/>
               <#assign partySolicitation = Static["org.ofbiz.entity.util.EntityUtil"].getFirst(delegator.findByAnd("PartyContactMech", {"partyId" : partySummary.partyId,"contactMechId":contactMech.contactMechId}, [], false))?if_exists/>
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
                        <@submit class="btn btn-sm btn-primary navbar-dark mt" label="${uiLabelMap.update}"/>
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
<#list contactMeches as contactMechMap>
<#assign contactMech = contactMechMap.contactMech>
<#assign partycontactMech = contactMechMap.partyContactMech>
<#if contactMech.contactMechId ==  partycontactMech.contactMechId && contactMech.contactMechTypeId == "POSTAL_ADDRESS">
<div id="POSTALcontactInfo_${contactMech.contactMechId}" class="modal fade" role="dialog">
   <div class="modal-dialog modal-lg">
      <div class="modal-content">
         <div class="modal-header">
            <h4 class="modal-title">${uiLabelMap.postalAddress}</h4>
            <button type="reset" class="close" data-dismiss="modal">&times;</button>
         </div>
         <div class="modal-body">
            <!-- Modal content-->
            <div class="row padding">
               <div class="col-md-4 col-sm-4 ">
                  <div class="form-group row text-danger">
                     <label  class="col-sm-12 col-form-label">${uiLabelMap.contactPurpose}*</label>
                  </div>
               </div>
               <div class="col-md-8 col-sm-8">
                  <#assign i = 0/>
                  <#list contactMechMap.partyContactMechPurposes as partyContactMechPurpose>
                  <form method="post" action="<@ofbizUrl>deletePartyContactMechPurpose</@ofbizUrl>" class="form-horizontal" name="deletePersonContactMechPurpose_EMAIL_${partyContactMechPurpose.contactMechId}_${i}" novalidate="novalidate" data-toggle="validator">
                     <input type="hidden" name="activeTab" value="contactInfo" />
                     <input type="hidden" name="donePage" value='${requestURI?if_exists}'/>
                     <input type="hidden" name="contactMechId" value="${partyContactMechPurpose.contactMechId}"/>
                     <input type="hidden" name="partyId" value="${partyId?if_exists}"/>
                     <input type="hidden" name="contactMechPurposeTypeId" value="${partyContactMechPurpose.contactMechPurposeTypeId}"/>
                     <input type="hidden" name="fromDate" value="${partyContactMechPurpose.fromDate?if_exists}"/>
                     <div class="form-group row">
                        <div class="col-sm-10">
                           <label class="col-form-label input-sm fw">
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
                        <div class="col-sm-2">
                           <#if !partyContactMechPurpose.contactMechPurposeTypeId.contains("IDEAL")>
                           <a class="btn btn-xs btn-secondary btn-danger tooltips confirm-message" href="javascript:document.deletePersonContactMechPurpose_EMAIL_${partyContactMechPurpose.contactMechId}_${i}.submit()" data-original-title="${uiLabelMap.delete}"><i class="fa fa-times red"></i></a>
                           </#if>
                        </div>
                     </div>
                  </form>
                  <#assign i = i+1/>
                  </#list>
                  <form method="post" action="<@ofbizUrl>createPartyContactMechPurpose</@ofbizUrl>" class="form-horizontal" name="createPartyContactMechPurpose_EMAIL_${contactMech.contactMechId}" novalidate="novalidate" data-toggle="validator">
                     <input type="hidden" name="activeTab" value="contactInfo" />
                     <input type="hidden" name="donePage" value='${requestURI?if_exists}'/>
                     <input type="hidden" name="contactMechId" value="${contactMech.contactMechId}"/>
                     <input type="hidden" name="partyId" value="${partyId?if_exists}"/>
                     <div class="form-group row">
                        <#assign contactMechTPPostal = delegator.findByAnd("ContactMechTypePurpose", {"contactMechTypeId" : "POSTAL_ADDRESS"}, [], false)>
                        <div class="col-sm-10">
                           <select name="contactMechPurposeTypeId" id="contactMechPurposeTypeId" class="ui dropdown search form-control input-sm" >
                              <#--<option value="" data-content="<span class='nonselect'>Please Select</span>" selected>Please Select</option>-->
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
                        <div class="col-sm-2">
                           <a class="btn btn-xs btn-primary tooltips" href="javascript:document.createPartyContactMechPurpose_EMAIL_${contactMech.contactMechId}.submit()" data-original-title="${uiLabelMap.addPurpose}"><i class="fa fa-plus info"></i></a>
                        </div>
                     </div>
                  </form>
               </div>
            </div>
            <hr/>
            <form method="post" action="<@ofbizUrl>updatePostalAddressData</@ofbizUrl>" id="updatePostalAddress" class="form-horizontal" name="updatePostalAddress" novalidate="novalidate" data-toggle="validator">
               <input type="hidden" name="activeTab" value="contactInfo" />
               <input type="hidden" name="donePage" value='${requestURI?if_exists}'/>
               <input type="hidden" name="partyId" value="${partyId?if_exists}"/>
               <input type="hidden" name="contactMechId" value="${contactMech.contactMechId}"/>
               <#assign postalAddress = delegator.findOne("PostalAddress", {"contactMechId", "${contactMech.contactMechId}"}, false)?if_exists />
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
               <#assign geo = delegator.findOne("Geo", {"geoId" : "${postalAddress?if_exists.city?if_exists}"}, false)!>
              <#assign city = ""/>
              <#if geo?exists && geo?has_content>
                 <#assign city = geo.geoName/>
              </#if>
               <input type="hidden" id="stateProvinceGeoId" value="${postalAddress.stateProvinceGeoId?if_exists}"/>
               
               <#if request.getRequestURI().contains("viewLead")>
               <div class="form-group row row">
                  <label  class="col-sm-4 col-form-label">${uiLabelMap.city}</label>
                  <div class="col-sm-7">
                     <#assign defaultStateProvinceGeoId="${postalAddress.stateProvinceGeoId?if_exists}"/>
                     <#assign cities = delegator.findByAnd("GeoAssocSummary",Static["org.ofbiz.base.util.UtilMisc"].toMap("geoId","${postalAddress.stateProvinceGeoId?if_exists}","geoAssocTypeId", "COUNTY_CITY"),[] ,false)/>
                     <select class="custom-select  ui dropdown search form-control input-sm" name="city" id="cityLi" >
                       <#if cities?exists && cities?has_content>
                          <option value="" data-content="<span class='nonselect'>Please Select</span>" >Please Select</option>
                          <#list cities as cityGeo>
                            <option <#if "${postalAddress?if_exists.city?if_exists}"="${cityGeo.geoIdTo?if_exists}"> selected="selected" </#if> value="${cityGeo.geoIdTo?if_exists}"> ${cityGeo.geoName?if_exists}</option>
                          </#list>
                        <#else>
                          
                        </#if>
                     </select>
                     <div class="help-block with-errors"></div>
                  </div>
               </div>
               <#else>
                 <@inputRow 
                   id="cityLi"
                   name="city"
                   label=uiLabelMap.city
                   placeholder=uiLabelMap.city
                   value="${city?if_exists}"
                   required=false
               />
               </#if>
               <div class="form-group row row">
                  <label  class="col-sm-4 col-form-label">${uiLabelMap.country}</label>
                  <div class="col-sm-7">
                     <#--<#assign defaultCountryGeoId = (Static["org.ofbiz.base.util.UtilProperties"].getPropertyValue("crm.properties", "defaultCountryGeoId"))?default("SGP")/>
                     <#if postalAddress.countryGeoId?has_content>
                    
                     </#if>
                     -->
                     
                     <#assign defaultCountryGeoId="${postalAddress.countryGeoId?if_exists}"/>
                     <#if !defaultCountryGeoId?has_content && request.getRequestURI().contains("viewLead")>
                         <#assign defaultCountryGeoId="IND"/>
                     </#if>
                     <#assign countries = delegator.findByAnd("Geo",Static["org.ofbiz.base.util.UtilMisc"].toMap("geoTypeId", "COUNTRY"),[] ,false)/>
                     <select name="countryGeoId" id="generalCountryGeo_${contactMech.contactMechId}"  class="custom-select ui dropdown search form-control input-sm generalCountryGeo" >
                        <#if !countries?has_content>
                        <#else>
                        <option value="">---Select---</option>
                        <#list countries as country>
                        <#if defaultCountryGeoId == country.geoId><#assign selected="selected=\"selected\""><#else><#assign selected=""></#if>
                        <option ${selected} value="${country.geoId}">${country.get("geoName", locale)}</option>
                        </#list>
                        </#if>
                     </select>
                  </div>
               </div>
               <div class="form-group row row">
                  <label  class="col-sm-4 col-form-label">${uiLabelMap.state}</label>
                  <div class="col-sm-7">
                     <#assign defaultStateProvinceGeoId="${postalAddress.stateProvinceGeoId?if_exists}"/>
                     <select class="custom-select  ui dropdown search form-control input-sm generalStateGeo" name="stateProvinceGeoId" id="generalStateProvinceGeo_${contactMech.contactMechId}" >
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
               type="number"
               required=false
               />
               <@inputRow 
               id="postalCodeExt"
               name="postalCodeExt"
               label=uiLabelMap.postalCodeExt
               placeholder=uiLabelMap.postalCodeExt
               value="${postalAddress?if_exists.postalCodeExt?if_exists}"
               type="number"
               required=false
               />
               <#assign postalAllowSolicitation = "Y"/>
               <#assign partySolicitation = Static["org.ofbiz.entity.util.EntityUtil"].getFirst(delegator.findByAnd("PartyContactMech", {"partyId" : partySummary.partyId,"contactMechId":contactMech.contactMechId}, [], false))?if_exists/>
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
               <div class="col-md-12 col-sm-12">
                  <div class="form-group row">
                     <div class="offset-sm-4 col-sm-9">
                        <@submit class="btn btn-sm btn-primary navbar-dark mt" label=uiLabelMap.update/>
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
<!--Postal Address End-->
<!-- Telecom Number-->
<#assign numberCnt = 1/>
<#list contactMeches as contactMechMap>
<#assign contactMech = contactMechMap.contactMech>
<#assign partycontactMech = contactMechMap.partyContactMech>
<#if contactMech.contactMechId ==  partycontactMech.contactMechId && contactMech.contactMechTypeId == "TELECOM_NUMBER">
<div id="TELECOMcontactInfo_${contactMech.contactMechId}" class="modal fade" role="dialog">
   <div class="modal-dialog modal-md">
      <div class="modal-content">
         <div class="modal-header">
            <h4 class="modal-title">${uiLabelMap.phoneNumber}</h4>
            <button type="reset" class="close" data-dismiss="modal">&times;</button>
         </div>
         <div class="modal-body">
            <div class="row padding">
               <div class="col-md-4 col-sm-4 ">
                  <div class="form-group row text-danger">
                     <label  class="col-sm-12 col-form-label">${uiLabelMap.contactPurpose}*</label>
                  </div>
               </div>
               <div class="col-md-8 col-sm-8">
                  <#assign i = 0/>
                  <#list contactMechMap.partyContactMechPurposes as partyContactMechPurpose>
                  <form method="post" action="<@ofbizUrl>deletePartyContactMechPurpose</@ofbizUrl>" class="form-horizontal" name="deletePersonContactMechPurpose_EMAIL_${partyContactMechPurpose.contactMechId}_${i}" novalidate="novalidate" data-toggle="validator">
                     <input type="hidden" name="activeTab" value="contactInfo" />
                     <input type="hidden" name="donePage" value='${requestURI?if_exists}'/>
                     <input type="hidden" name="contactMechId" value="${partyContactMechPurpose.contactMechId}"/>
                     <input type="hidden" name="partyId" value="${partyId?if_exists}"/>
                     <input type="hidden" name="contactMechPurposeTypeId" value="${partyContactMechPurpose.contactMechPurposeTypeId}"/>
                     <input type="hidden" name="fromDate" value="${partyContactMechPurpose.fromDate?if_exists}"/>
                     <div class="form-group row">
                        <div class="col-sm-10">
                           <label class="col-form-label input-sm fw">
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
                        <div class="col-sm-2">
                           <#if !partyContactMechPurpose.contactMechPurposeTypeId.contains("IDEAL")>
                           <a class="btn btn-xs btn-secondary btn-danger tooltips confirm-message" href="javascript:document.deletePersonContactMechPurpose_EMAIL_${partyContactMechPurpose.contactMechId}_${i}.submit()" data-original-title="${uiLabelMap.delete}"><i class="fa fa-times red"></i></a>
                           </#if>
                        </div>
                     </div>
                  </form>
                  <#assign i = i+1/>
                  </#list>
                  <form method="post" action="<@ofbizUrl>createPartyContactMechPurpose</@ofbizUrl>" class="form-horizontal" name="createPartyContactMechPurpose_EMAIL_${contactMech.contactMechId}" novalidate="novalidate" data-toggle="validator">
                     <input type="hidden" name="activeTab" value="contactInfo" />
                     <input type="hidden" name="donePage" value='${requestURI?if_exists}'/>
                     <input type="hidden" name="contactMechId" value="${contactMech.contactMechId}"/>
                     <input type="hidden" name="partyId" value="${partyId?if_exists}"/>
                     <div class="form-group row">
                        <#assign contactMechTPTelecom = delegator.findByAnd("ContactMechTypePurpose", {"contactMechTypeId" : "TELECOM_NUMBER"}, [], false)>
                        <div class="col-sm-10">
                           <select name="contactMechPurposeTypeId" id="contactMechPurposeTypeId" class="ui dropdown search form-control input-sm" >
                              <#--<option value="" data-content="<span class='nonselect'>Please Select</span>" selected>Please Select</option>-->
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
                        </div>
                        <div class="col-sm-2">
                           <a class="btn btn-xs btn-primary tooltips" href="javascript:document.createPartyContactMechPurpose_EMAIL_${contactMech.contactMechId}.submit()" data-original-title="${uiLabelMap.addPurpose}"><i class="fa fa-plus info"></i></a>
                        </div>
                     </div>
                  </form>
               </div>
            </div>
            <hr/>
            <form method="post" action="<@ofbizUrl>updateTelecomNumber</@ofbizUrl>" id="updateTelecomNumber" class="form-horizontal" name="updateTelecomNumber" novalidate="novalidate" data-toggle="validator">
               <input type="hidden" name="activeTab" value="contactInfo" />
               <input type="hidden" name="donePage" value='${requestURI?if_exists}'/>
               <input type="hidden" name="partyId" value="${partyId?if_exists}"/>
               <input type="hidden" name="contactMechId" value="${contactMech.contactMechId}"/>
               <#assign telecomNumber = delegator.findOne("TelecomNumber", {"contactMechId", "${contactMech.contactMechId}"}, false)?if_exists />
               <@inputRow 
               id="contactNumbers${numberCnt!}"
               name="contactNumber"
               label=uiLabelMap.phoneNumber
               placeholder=uiLabelMap.phoneNumber
               value="${telecomNumber?if_exists.contactNumber?if_exists}"
               required=true
               dataError="Please enter phone number"
               maxlength=10
               onkeyup="phoneNumber(${numberCnt!})"
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
               <#assign partySolicitation = Static["org.ofbiz.entity.util.EntityUtil"].getFirst(delegator.findByAnd("PartyContactMech", {"partyId" : partySummary.partyId,"contactMechId":contactMech.contactMechId}, [], false))?if_exists/>
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
                        <@submit class="btn btn-sm btn-primary navbar-dark mt" onclick="javascript:return onSubmitValidate(${numberCnt!});" label="${uiLabelMap.update}"/>
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
   <div class="modal-dialog modal-md">
      <div class="modal-content">
         <div class="modal-header">
            <h4 class="modal-title">${uiLabelMap.webAddress}</h4>
            <button type="reset" class="close" data-dismiss="modal">&times;</button>
         </div>
         <div class="modal-body">
            <div class="row padding">
               <div class="col-md-4 col-sm-4 ">
                  <div class="form-group row text-danger">
                     <label  class="col-sm-12 col-form-label">${uiLabelMap.contactPurpose}*</label>
                  </div>
               </div>
               <div class="col-md-8 col-sm-8">
                  <#assign i = 0/>
                  <#list contactMechMap.partyContactMechPurposes as partyContactMechPurpose>
                  <form method="post" action="<@ofbizUrl>deletePartyContactMechPurpose</@ofbizUrl>" class="form-horizontal" name="deletePersonContactMechPurpose_EMAIL_${partyContactMechPurpose.contactMechId}_${i}" novalidate="novalidate" data-toggle="validator">
                     <input type="hidden" name="activeTab" value="contactInfo" />
                     <input type="hidden" name="donePage" value='${requestURI?if_exists}'/>
                     <input type="hidden" name="contactMechId" value="${partyContactMechPurpose.contactMechId}"/>
                     <input type="hidden" name="partyId" value="${partyId?if_exists}"/>
                     <input type="hidden" name="contactMechPurposeTypeId" value="${partyContactMechPurpose.contactMechPurposeTypeId}"/>
                     <input type="hidden" name="fromDate" value="${partyContactMechPurpose.fromDate?if_exists}"/>
                     <div class="form-group row">
                        <div class="col-sm-10">
                           <label class="col-form-label input-sm fw">
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
                        <div class="col-sm-2">
                           <#if !partyContactMechPurpose.contactMechPurposeTypeId.contains("IDEAL")>
                           <a class="btn btn-xs btn-secondary btn-danger tooltips confirm-message" href="javascript:document.deletePersonContactMechPurpose_EMAIL_${partyContactMechPurpose.contactMechId}_${i}.submit()" data-original-title="${uiLabelMap.delete}"><i class="fa fa-times red"></i></a>
                           </#if>
                        </div>
                     </div>
                  </form>
                  <#assign i = i+1/>
                  </#list>
                  <form method="post" action="<@ofbizUrl>createPartyContactMechPurpose</@ofbizUrl>" class="form-horizontal" name="createPartyContactMechPurpose_EMAIL_${contactMech.contactMechId}" novalidate="novalidate" data-toggle="validator">
                     <input type="hidden" name="activeTab" value="contactInfo" />
                     <input type="hidden" name="donePage" value='${requestURI?if_exists}'/>
                     <input type="hidden" name="contactMechId" value="${contactMech.contactMechId}"/>
                     <input type="hidden" name="partyId" value="${partyId?if_exists}"/>
                     <div class="form-group row">
                        <#assign contactMechTPWeb = delegator.findByAnd("ContactMechTypePurpose", {"contactMechTypeId" : "WEB_ADDRESS"}, [], false)>
                        <div class="col-sm-10">
                           <select name="contactMechPurposeTypeId" id="contactMechPurposeTypeId" class="ui dropdown search form-control input-sm" >
                              <#--<option value="" data-content="<span class='nonselect'>Please Select</span>" selected>Please Select</option>-->
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
                        <div class="col-sm-2">
                           <a class="btn btn-xs btn-primary tooltips" href="javascript:document.createPartyContactMechPurpose_EMAIL_${contactMech.contactMechId}.submit()" data-original-title="${uiLabelMap.addPurpose}"><i class="fa fa-plus info"></i></a>
                        </div>
                     </div>
                  </form>
               </div>
            </div>
            <hr/>
            <form method="post" action="<@ofbizUrl>updateContactMech</@ofbizUrl>" id="updateContactMech" class="form-horizontal" name="updateContactMech" novalidate="novalidate" data-toggle="validator">
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
               <#assign partySolicitation = Static["org.ofbiz.entity.util.EntityUtil"].getFirst(delegator.findByAnd("PartyContactMech", {"partyId" : partySummary.partyId,"contactMechId":contactMech.contactMechId}, [], false))?if_exists/>
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
                        <@submit class="btn btn-sm btn-primary navbar-dark mt" label="${uiLabelMap.update}"/>
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
   <div class="modal-dialog modal-md">
      <div class="modal-content">
         <div class="modal-header">
            <h4 class="modal-title">${uiLabelMap.socialMediaType}</h4>
            <button type="reset" class="close" data-dismiss="modal">&times;</button>
         </div>
         <div class="modal-body">
            <div class="row padding">
               <div class="col-md-4 col-sm-4 ">
                  <div class="form-group row text-danger">
                     <label  class="col-sm-12 col-form-label">${uiLabelMap.contactPurpose}*</label>
                  </div>
               </div>
               <div class="col-md-8 col-sm-8">
                  <#assign i = 0/>
                  <#list contactMechMap.partyContactMechPurposes as partyContactMechPurpose>
                  <form method="post" action="<@ofbizUrl>deletePartyContactMechPurpose</@ofbizUrl>" class="form-horizontal" name="deletePersonContactMechPurpose_EMAIL_${partyContactMechPurpose.contactMechId}_${i}" novalidate="novalidate" data-toggle="validator">
                     <input type="hidden" name="activeTab" value="contactInfo" />
                     <input type="hidden" name="donePage" value='${requestURI?if_exists}'/>
                     <input type="hidden" name="contactMechId" value="${partyContactMechPurpose.contactMechId}"/>
                     <input type="hidden" name="partyId" value="${partyId?if_exists}"/>
                     <input type="hidden" name="contactMechPurposeTypeId" value="${partyContactMechPurpose.contactMechPurposeTypeId}"/>
                     <input type="hidden" name="fromDate" value="${partyContactMechPurpose.fromDate?if_exists}"/>
                     <div class="form-group row">
                        <div class="col-sm-10">
                           <label class="col-form-label input-sm fw">
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
                        <div class="col-sm-2">
                           <#if !partyContactMechPurpose.contactMechPurposeTypeId.contains("IDEAL")>
                           <a class="btn btn-xs btn-secondary btn-danger tooltips confirm-message" href="javascript:document.deletePersonContactMechPurpose_EMAIL_${partyContactMechPurpose.contactMechId}_${i}.submit()" data-original-title="${uiLabelMap.delete}"><i class="fa fa-times red"></i></a>
                           </#if>
                        </div>
                     </div>
                  </form>
                  <#assign i = i+1/>
                  </#list>
                  <form method="post" action="<@ofbizUrl>createPartyContactMechPurpose</@ofbizUrl>" class="form-horizontal" name="createPartyContactMechPurpose_EMAIL_${contactMech.contactMechId}" novalidate="novalidate" data-toggle="validator">
                     <input type="hidden" name="activeTab" value="contactInfo" />
                     <input type="hidden" name="donePage" value='${requestURI?if_exists}'/>
                     <input type="hidden" name="contactMechId" value="${contactMech.contactMechId}"/>
                     <input type="hidden" name="partyId" value="${partyId?if_exists}"/>
                     <div class="form-group row">
                        <#assign contactMechTPMediaType = delegator.findByAnd("ContactMechTypePurpose", {"contactMechTypeId" : "SOCIAL_MEDIA_TYPE"}, [], false)>
                        <div class="col-sm-10">
                           <select name="contactMechPurposeTypeId" id="contactMechPurposeTypeId" class="ui dropdown search form-control input-sm" >
                           <#--<option value="" data-content="<span class='nonselect'>Please Select</span>" selected>Please Select</option>-->
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
                        <div class="col-sm-2">
                           <a class="btn btn-xs btn-primary tooltips" href="javascript:document.createPartyContactMechPurpose_EMAIL_${contactMech.contactMechId}.submit()" data-original-title="${uiLabelMap.addPurpose}"><i class="fa fa-plus info"></i></a>
                        </div>
                     </div>
                  </form>
               </div>
            </div>
            <hr/>
            <form method="post" action="<@ofbizUrl>updateSocialMediaTypeWebm</@ofbizUrl>" id="updateSocialMediaTypeWebm" class="form-horizontal" name="updateSocialMediaTypeWebm" novalidate="novalidate" data-toggle="validator">
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
               <#assign partySolicitation = Static["org.ofbiz.entity.util.EntityUtil"].getFirst(delegator.findByAnd("PartyContactMech", {"partyId" : partySummary.partyId,"contactMechId":contactMech.contactMechId}, [], false))?if_exists/>
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
                        <@submit class="btn btn-sm btn-primary navbar-dark mt" label="${uiLabelMap.update}"/>
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
<script>
   $(document).ready(function() {
   
       $('.generalCountryGeo').each(function () {
           var countryListId = $(this).attr("id");
           if (countryListId != null && countryListId != "" && countryListId != 'undefined') {
              populateLists($(this).attr("id"));
           }
       });
       $(".generalCountryGeo").change(function() {
           populateLists($(this).attr("id"));
       });
   
       function populateLists(listType) {
           var splitGeneralCountryGeo = listType.split('_');
           var GeoIdVal = $("#"+listType).val();
           var GeoId = splitGeneralCountryGeo[0];
           var contactMechId = splitGeneralCountryGeo[1];
           var list = "";
           var stateProvinceId = "generalStateProvinceGeo_"+contactMechId;
           var state =  $("#"+stateProvinceId).val();alert("hellow");
           <#assign defaultStateProvinceGeoId="${postalAddress?if_exists.stateProvinceGeoId?if_exists}"/>
           var defaultStateProvinceGeoId = "${defaultStateProvinceGeoId!}";
           console.log("defaultStateProvinceGeoId=="+GeoIdVal);
           if (GeoIdVal != null && GeoIdVal != "") {
               var urlString = "getStateDataJSON?countryGeoId=" + GeoIdVal;
               $.ajax({
                   type: 'POST',
                   async: false,
                   url: urlString,
                   success: function(states) {
                       $("#"+stateProvinceId).empty();
                       if (GeoId == "generalCountryGeo") {
                           list = $("#"+stateProvinceId);
                       }
                       list.append("<option value=''>Select State</option>");
                       if (states.length == 0) {
                           list.append("<option value = ''>N/A</option>");
                       } else {
                           for (var i = 0; i < states.length; i++) {
                               if(state != null && state != "" && states[i].geoId==state) {
                                  list.append("<option  value =" + states[i].geoId + " selected>" + states[i].geoName + " </option>");
                               } else {
                                  list.append("<option  value =" + states[i].geoId + ">" + states[i].geoName + "</option>");
                               }
                           }
                       }
                   }
               });
               
               $('#'+stateProvinceId).append(list);
               $('#'+stateProvinceId).dropdown('refresh');
           } else {
           		<#assign selectedState = ""/>
           		<#if postalAddress?has_content>
               <#assign selectedState = delegator.findOne("Geo",Static["org.ofbiz.base.util.UtilMisc"].toMap("geoId","${postalAddress?if_exists.stateProvinceGeoId?if_exists}"),false)/>
               </#if>
               var selectedState= "";
               <#if selectedState?has_content>
               selectedState= "${selectedState.geoName!}";
               </#if>
               console.log("===selectedState==="+selectedState);
               $("#"+stateProvinceId).empty();
               list = $("#"+stateProvinceId);
               list.append("<option value="+defaultStateProvinceGeoId+">" + selectedState + "</option>");
               $('#'+stateProvinceId).append(list);
               $('#'+stateProvinceId).dropdown('refresh');
           }
       }
       console.log("===stateProvinceGeoId===="+$("#stateProvinceGeoId").val());
       loadCityList();
      $('.generalStateGeo').each(function () {
           var stateListId = $(this).attr("id");
           if (stateListId != null && stateListId != "" && stateListId != 'undefined') {
              loadCityList($(this).attr("id"));
           }
       });
   });
   $(".generalStateGeo").change(function() {
           loadCityList($(this).attr("id"));
   });
   function loadCityList(listType) {
           var splitGeneralStateGeo = listType.split('_');
           var GeoIdVal = $("#"+listType).val();
           var GeoId = splitGeneralStateGeo[0];
           var contactMechId = splitGeneralStateGeo[1];
           var list = "";
           var stateProvinceId = "generalStateProvinceGeo_"+contactMechId;
           var state =  $("#"+stateProvinceId).val();
	      var nonSelectContent = "<span class='nonselect'>Please Select</span>";
	      var groupNameOptions = '<option value="" data-content="'+nonSelectContent+'" selected="">Please Select</option>';			
	    console.log("===state calling===="+$("#"+stateProvinceId).val());
	if ( $("#"+stateProvinceId).val() ) {
		
		$.ajax({
			      
			type: "POST",
	     	url: "getGeoAssocList",
	        data:  {"geoId": $("#"+stateProvinceId).val(), "geoAssocTypeId": "COUNTY_CITY"},
	        async: false,
	        success: function (data) {   
	            
	            if (data.code == 200) {
	            
	            	for (var i = 0; i < data.results.length; i++) {
	            		var result = data.results[i];
	            		groupNameOptions += '<option value="'+result.geoIdTo+'">'+result.geoName+'</option>';
	            		
	            	}
	            	
	            }
				    	
	        }
	        
		});    
		console.log("===groupNameOptions===="+groupNameOptions);
		$("#cityLi").html( groupNameOptions );
		$('#cityLi').dropdown('refresh');
	
	}
		
}
   //contactNumber
function phoneNumber(x){  
  var phoneNumber = $("#contactNumbers"+x).val();
  
  if(phoneNumber !=''){
      var re = new RegExp("^[0-9]{10}$");
      if (re.test(phoneNumber)) {
           $("#contactNumbers"+x+"_error").empty();
      } else {
           $("#contactNumbers"+x+"_error").empty();
           $("#contactNumbers"+x+"_error").append('<ul class="list-unstyled text-danger"><li>Please enter valid phone number.</li></ul>');
      }
  }else{
    $("#contactNumbers"+x+"_error").html("");
    $("#contactNumbers"+x+"_error").append('<ul class="list-unstyled text-danger"><li>Please enter phone number</li></ul>');
  }
  if(phoneNumber.length > 10) {
        $("#contactNumbers"+x+"_error").html("");
  }
}
function onSubmitValidate(x){
   var phoneNumber = $("#contactNumbers"+x).val();
   if(phoneNumber !=''){
      var re = new RegExp("^[0-9]{10}$");
      if (re.test(phoneNumber)) {
           $("#contactNumbers"+x+"_error").empty();
           return true;
      } else {
           $("#contactNumbers"+x+"_error").empty();
           $("#contactNumbers"+x+"_error").append('<ul class="list-unstyled text-danger"><li>Please enter valid phone number</li></ul>');
           return false;
      }
  }else{
    $("#contactNumbers"+x+"_error").html("");
    $("#contactNumbers"+x+"_error").append('<ul class="list-unstyled text-danger"><li>Please enter phone number</li></ul>');
    return false;
  }
}
</script>
</#if>