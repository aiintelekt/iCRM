<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<@pageSectionHeader title=uiLabelMap.contactInformation
	extra='<div class="float-right">
      <p>
         <span class="glyphicon glyphicon-user btn btn-xs btn-primary tooltips" data-toggle="modal" data-target="#createPOSTALcontactInfo" data-original-title="${uiLabelMap.createPostalAddress}"></span>
         <span class="glyphicon glyphicon-envelope btn btn-xs btn-primary tooltips" data-toggle="modal" data-target="#createEMAILcontactInfo" data-original-title="${uiLabelMap.createEmail}"></span>
         <span class="glyphicon glyphicon-phone-alt btn btn-xs btn-primary tooltips" data-toggle="modal" data-target="#createTELECOMcontactInfo" data-original-title="${uiLabelMap.createPhoneNumber}"></span> 
         <span class="glyphicon glyphicon-globe btn btn-xs btn-primary tooltips" data-toggle="modal" data-target="#createWEBcontactInfo" data-original-title="${uiLabelMap.createWebAddress}"></span>
         <span class="glyphicon glyphicon-share btn btn-xs btn-primary tooltips" data-toggle="modal" data-target="#createSocialMediacontactInfo" data-original-title="${uiLabelMap.createSocialMediaType}"></span>
      </p>
   </div>' />
<#-- Create Contact Information Start -->
<#include "component://crm/webapp/crm/common/createContactInfo.ftl" />

<#assign requestURI = ""/>
<#if request.getRequestURI().contains("viewLead")>
<#assign requestURI = "viewLead"/>
<#elseif request.getRequestURI().contains("viewCustomer")>
<#assign requestURI = "viewCustomer"/>
</#if>

<#-- Create Contact Information End -->
<#if contactMeches?has_content>
<div class="table-responsive">
<table class="table table-striped">
   <thead>
      <tr>
         <th>${uiLabelMap.contactType}</th>
         <th>${uiLabelMap.contactInformation}</th>
         <th>${uiLabelMap.CommonPurpose}</th>
         <th>${uiLabelMap.solicitation}</th>
         <th>${uiLabelMap.deliverable}</th>
         <#if requestURI?exists && requestURI?has_content && requestURI == "viewLead"><th>DND</th></#if>
         <th></th>
      </tr>
   </thead>
   <tbody>
      <#list contactMeches as contactMechMap>
      <#assign contactMech = contactMechMap.contactMech>
      <#assign partyContactMech = contactMechMap.partyContactMech>
      <tr>
         <td>${contactMechMap.contactMechType.get("description",locale)}</td>
         <td>
            <#if "POSTAL_ADDRESS" == contactMech.contactMechTypeId && contactMechMap.postalAddress?exists>
            <#assign postalAddress = contactMechMap.postalAddress?if_exists />
            <#if postalAddress.toName?has_content><b>${uiLabelMap.toName}:</b> ${postalAddress.toName}<br/></#if>
            <#if postalAddress.attnName?has_content><b>${uiLabelMap.attentionName}:</b> ${postalAddress.attnName}<br/></#if>
            ${postalAddress.address1?if_exists}<br/>
            <#if postalAddress.address2?has_content>${postalAddress.address2}<br/></#if>
            <#-- <#assign enumeration = delegator.findOne("Enumeration", {"enumId" : "${postalAddress?if_exists.city?if_exists}"}, false)!>
              <#assign city = ""/>
              <#if enumeration?exists && enumeration?has_content>
                 <#assign city = enumeration.description/>
              </#if> -->
              <#assign geoCity = delegator.findOne("Geo", {"geoId" : "${postalAddress?if_exists.city?if_exists}"}, false)!>
              <#assign city = ""/>
              <#if geoCity?exists && geoCity?has_content>
                 <#assign city = geoCity.geoName/>
              </#if>
             ${city?if_exists}
            <#if postalAddress?if_exists.stateProvinceGeoId?has_content>
            <#assign geo = delegator.findOne("Geo",Static["org.ofbiz.base.util.UtilMisc"].toMap("geoId",postalAddress.stateProvinceGeoId?if_exists),true)?if_exists/>
            , ${geo.geoCode?if_exists}
            </#if>
            ${postalAddress.postalCode?if_exists}
            <#if postalAddress?if_exists.postalCodeExt?has_content>-${postalAddress.postalCodeExt}</#if>
            <#if postalAddress?if_exists.directions?has_content><br/>[${postalAddress.directions}]</#if>
            <#if postalAddress?if_exists.countryGeoId?default("") == "USA">
            <#assign country = postalAddress?if_exists.getRelatedOne("CountryGeo",true)>
            ${country.geoName?default(country.geoId)}<br/>
            <#else>
            <#-- <#if postalAddress?if_exists.countryGeoId?has_content><br />
            <#assign country = postalAddress?if_exists.getRelatedOneCache("CountryGeo",true)>
            ${country.geoName?default(country.geoId)}<br/>
            </#if>-->
            </#if>
            <#if postalAddress?if_exists.phoneNumber?has_content>
            <#assign ph1=postalAddress.phoneNumber?substring(0,3) />
            <#assign ph2=postalAddress.phoneNumber?substring(3,6) />
            <#assign ph3=postalAddress.phoneNumber?substring(6) />
            ${ph1?if_exists}-${ph2?if_exists}-${ph3?if_exists}
            </#if>
            <#elseif "TELECOM_NUMBER" == contactMech.contactMechTypeId && contactMechMap.telecomNumber?exists>
            <#assign telecomNumber = contactMechMap.telecomNumber?if_exists>
            <#if telecomNumber.askForName?has_content><b>${uiLabelMap.toName}:</b> ${telecomNumber.askForName}<br/></#if>
            ${telecomNumber.countryCode?if_exists}
            <#if telecomNumber.areaCode?has_content>${telecomNumber.areaCode?default("000")}-</#if>${telecomNumber.contactNumber?default("000-0000")}
            <#if partyContactMech.extension?has_content>${uiLabelMap.PartyContactExt}&nbsp;${partyContactMech.extension}</#if>
            <#-- <#if telecomNumber.askForName?has_content>
            <br/><span>${uiLabelMap.personToAskFor}:</span> ${telecomNumber.askForName}
            </#if> -->
            <#elseif "EMAIL_ADDRESS" == contactMech.contactMechTypeId>
            ${contactMech.infoString?if_exists}
            <#elseif "WEB_ADDRESS" == contactMech.contactMechTypeId>
            ${contactMech.infoString?if_exists}
            <#assign openAddress = contactMech.infoString?default("")>
            <#if !openAddress?starts_with("http") && !openAddress?starts_with("HTTP")><#assign openAddress = "http://" + openAddress></#if>
            <a target="_blank" href="${openAddress}">(${uiLabelMap.CommonOpenPageNewWindow})</a>
            <#elseif "SKYPE" == contactMech.contactMechTypeId>
            <a href="skype:${contactMech.infoString?if_exists}?call" class="linktext">${contactMech.infoString?if_exists}</a>&nbsp;<img src="http://mystatus.skype.com/smallicon/${contactMech.infoString?if_exists}" style="vertical-align:middle"/>
            <#elseif "SOCIAL_MEDIA_TYPE" == contactMech.contactMechTypeId>
            ${contactMech.infoString?if_exists}
            <#else>
            ${contactMech.infoString?if_exists}
            </#if>
            <#if partyContactMech.thruDate?has_content><b>${uiLabelMap.PartyContactEffectiveThru}:&nbsp;${getLocalizedDate(partyContactMech.thruDate)}</b></#if>
         </td>
         <td>
            <#list contactMechMap.partyContactMechPurposes as partyContactMechPurpose>
            <#assign contactMechPurposeType = partyContactMechPurpose.getRelatedOne("ContactMechPurposeType",true)>
            <#if contactMechPurposeType?has_content>
            ${contactMechPurposeType.get("description",locale)}<br>
            <#else>
            ${uiLabelMap.PartyMechPurposeTypeNotFound}: "${partyContactMechPurpose.contactMechPurposeTypeId}"
            </#if>
            <#if partyContactMechPurpose.thruDate?has_content>
            (${uiLabelMap.CommonExpire}: ${getLocalizedDate(partyContactMechPurpose.thruDate)})
            </#if>
            </#list>
         </td>
         <td>
            <#assign partySolicitation = delegator.findByAnd("PartyContactMech", {"partyId" : partySummary.partyId,"contactMechId":contactMech.contactMechId}, [], false)?if_exists/>
            <#if partySolicitation?has_content>
            <#list partySolicitation as partySolicitation>
            <#if requestURI?exists && requestURI?has_content && requestURI == "viewLead" && "TELECOM_NUMBER" == contactMech.contactMechTypeId && contactMechMap.telecomNumber?exists>
            <#if partySolicitation.allowSolicitation?has_content && partySolicitation.allowSolicitation="N">
            <button class="btn btn-xs btn-danger">(N)</button>
            <#else>
            <button class="btn btn-xs btn-success">(Y)</button>
            </#if>
            <#else>
            <#if partySolicitation.allowSolicitation?has_content && partySolicitation.allowSolicitation="N">
            <font color="red"><b>(N)</b></font>
            <#else>
            <b>(Y)</b>
            </#if>
            </#if>
            </#list>
            </#if>
         </td>
         <td>
         <#if "POSTAL_ADDRESS" == contactMech.contactMechTypeId && contactMechMap.postalAddress?exists>
           <#assign postalAddressDeliverable = contactMechMap.postalAddress?if_exists />
           <#if postalAddressDeliverable.addressValidInd?has_content && postalAddressDeliverable.addressValidInd=="N">
             <font color="red"><b>(N)</b></font>
           <#else>
             <b>(Y)</b>
           </#if>
         <#elseif "TELECOM_NUMBER" == contactMech.contactMechTypeId && contactMechMap.telecomNumber?exists>
           <#assign telecomNumberDeliverable = contactMechMap.telecomNumber?if_exists />
           <#if telecomNumberDeliverable.phoneValidInd?has_content && telecomNumberDeliverable.phoneValidInd=="N">
             <font color="red"><b>(N)</b></font>
           <#else>
             <b>(Y)</b>
           </#if>
         <#elseif "EMAIL_ADDRESS" == contactMech.contactMechTypeId>
           <#assign emailAddressDeliverable = Static["org.ofbiz.entity.util.EntityUtil"].getFirst(delegator.findByAnd("PartyContactMech", {"partyId" : partySummary.partyId,"contactMechId":contactMech.contactMechId}, [], false))?if_exists/>
           <#if emailAddressDeliverable.emailValidInd?has_content && emailAddressDeliverable.emailValidInd=="N">
             <font color="red"><b>(N)</b></font>
           <#else>
             <b>(Y)</b>
           </#if>
         </#if>
         </td>
         
         <#if requestURI?exists && requestURI?has_content && requestURI == "viewLead">
         <td>
         <#if "TELECOM_NUMBER" == contactMech.contactMechTypeId && contactMechMap.telecomNumber?exists>
           <#assign telecomNumberDeliverable = contactMechMap.telecomNumber?if_exists />
           <#if telecomNumberDeliverable.dndStatus?has_content && telecomNumberDeliverable.dndStatus=="Y">
             <button class="btn btn-xs btn-danger">(Y)</button>
           <#else>
             <button class="btn btn-xs btn-success">(N)</button>
           </#if>
         </#if>
         </td>
         </#if>
         <td>
           <#-- <form name="deleteContactMechForm${contactMechMap_index}" method="post" action="deleteContactMech" class="row">
               <#assign requestURI = "viewContact"/>        
               <#if request.getRequestURI().contains("viewLead")>
               <#assign requestURI = "viewLead"/>
               <#elseif request.getRequestURI().contains("viewAccount")>
               <#assign requestURI = "viewAccount"/>
               </#if>
               <input type="hidden" name="activeTab" value="contactInfo" />
               <input type="hidden" name="donePage" value='${requestURI?if_exists}'/>
               <input type="hidden" name="partyId" value="${partySummary.partyId}"/>
               <input type="hidden" name="contactMechId" value="${contactMech.contactMechId}"/>
            </form>-->
            <#assign idealType = "N"/>
            <#list contactMechMap.partyContactMechPurposes as partyContactMechPurpose>
            <#if partyContactMechPurpose.contactMechPurposeTypeId.contains("IDEAL")>
            <#assign idealType = "Y"/>
            </#if>
            </#list>
            <#if idealType=="N">
            <#if contactMechMap.contactMechType.get("description",locale).contains("Postal")>
            <span class="glyphicon glyphicon-edit btn btn-xs btn-primary tooltips" data-toggle="modal" href="#POSTALcontactInfo_${partyContactMech.contactMechId}" data-original-title="${uiLabelMap.edit}"></span>
            <#elseif contactMechMap.contactMechType.get("description",locale).contains("Phone")>
            <span class="glyphicon glyphicon-edit btn btn-xs btn-primary tooltips" data-toggle="modal" href="#TELECOMcontactInfo_${partyContactMech.contactMechId}" data-original-title="${uiLabelMap.edit}"></span>
            <#elseif contactMechMap.contactMechType.get("description",locale).contains("Email")>
            <span class="glyphicon glyphicon-edit btn btn-xs btn-primary tooltips" data-toggle="modal" href="#EMAILcontactInfo_${partyContactMech.contactMechId}" data-original-title="${uiLabelMap.edit}"></span>
            <#elseif contactMechMap.contactMechType.get("description",locale).contains("Web")>
            <span class="glyphicon glyphicon-edit btn btn-xs btn-primary tooltips" data-toggle="modal" href="#WEBcontactInfo_${partyContactMech.contactMechId}" data-original-title="${uiLabelMap.edit}"></span>
            <#elseif contactMechMap.contactMechType.get("description",locale).contains("Skype")>
            <span class="glyphicon glyphicon-edit btn btn-xs btn-primary tooltips" data-toggle="modal" href="#SKYPEcontactInfo_${partyContactMech.contactMechId}" data-original-title="${uiLabelMap.edit}"></span>
            <#elseif contactMechMap.contactMechType.get("description",locale).contains("Social")>
            <span class="glyphicon glyphicon-edit btn btn-xs btn-primary tooltips" data-toggle="modal" href="#SocialMediacontactInfo_${partyContactMech.contactMechId}" data-original-title="${uiLabelMap.edit}"></span>
            </#if>
           <#-- <a class="btn btn-xs btn-secondary btn-danger tooltips confirm-message" href="javascript:document.deleteContactMechForm${contactMechMap_index}.submit();" data-original-title="${uiLabelMap.delete}"><i class="fa fa-times red"></i></a>-->
            </#if>
         </td>
      </tr>
      </#list>
   </tbody>
</table>
</div>

<#if requestURI != "viewCustomer">
<div class="panel-group" id="accordionMenu" role="tablist" aria-multiselectable="true">
<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>

<div class="panel panel-default">
      <div class="panel-heading" role="tab" id="headingTwo">
        <h4 class="panel-title">
          <a class="collapsed" role="button" data-toggle="collapse" data-parent="#accordionMenu" href="#LeadHistoryDnd" aria-expanded="false" aria-controls="headingTwo">
          DND Status History
          </a>
        </h4>
      </div>
      <div id="LeadHistoryDnd" class="panel-collapse collapse" data-parent="#accordionMenu" aria-labelledby="LeadHistoryDndPen">
        <div class="panel-body">
			<@inputHidden id="leadDndStatusListData" value=leadDndStatusListStr />
			<div class="table-responsive">				
				<div id="leadDndStatusGrid" style="width: 100%;" class="ag-theme-balham"></div>   			
			</div>
			<script type="text/javascript" src="/crm-resource/js/ag-grid/lead/leadDndStatus.js"></script>          
        </div>
      </div>
    </div>
    
</div>
</#if>
</#if>