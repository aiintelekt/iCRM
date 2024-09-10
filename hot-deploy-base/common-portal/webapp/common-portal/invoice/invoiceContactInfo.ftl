<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<#include "component://common-portal/webapp/common-portal/contact/update_contact_info.ftl"/>
<#assign partyId= request.getParameter("partyId")! />
 <#assign helpUrl = Static["org.groupfio.common.portal.util.DataHelper"].getTabHelpUrl(delegator, request.getRequestURI(), "contactInfo") />  

<#if partyStatusId?if_exists == "PARTY_DISABLED">
<@pageSectionHeader title=uiLabelMap.contactInformation  />
<#else>
<@pageSectionHeader title="Contact Information"
	extra='' />
   </#if>
<#-- Create Contact Information Start -->
<#include "component://common-portal/webapp/common-portal/contact/create_contact_info.ftl" />

<#assign requestURI = ""/>
<#if request.getRequestURI().contains("viewLead")>
<#assign requestURI = "viewLead"/>
<#elseif request.getRequestURI().contains("viewAccount")>
<#assign requestURI = "viewAccount"/>
</#if>
<div class="pt-2"></div>
<#-- Create Contact Intformation End -->
<#if contactMeches?has_content>
<div class="contact-table">
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
            <#if postalAddress.address1?has_content>${postalAddress.address1?if_exists}<br/></#if>
            <#if postalAddress.address2?has_content>${postalAddress.address2}<br/></#if>
            
              <#assign geoCity = delegator.findOne("Geo", {"geoId" : "${postalAddress?if_exists.city?if_exists}"}, false)!>
              <#assign city = "${postalAddress?if_exists.city?if_exists}"/>
              <#if geoCity?exists && geoCity?has_content>
                 <#assign city = geoCity.geoName/>                  
              </#if>    
              <#if city?has_content>${city?if_exists},</#if>    
          	<#if postalAddress.countyGeoId?has_content>${postalAddress.countyGeoId},</#if>	 	   
            <#if postalAddress?if_exists.stateProvinceGeoId?has_content>
            <#assign geo = delegator.findOne("Geo",Static["org.ofbiz.base.util.UtilMisc"].toMap("geoId",postalAddress.stateProvinceGeoId?if_exists),true)?if_exists/>
             ${geo.geoCode?if_exists}
            </#if>
            ${postalAddress.postalCode?if_exists}
            <#if postalAddress?if_exists.postalCodeExt?has_content>-${postalAddress.postalCodeExt}</#if>
            <#if postalAddress?if_exists.directions?has_content><br/>[${postalAddress.directions}]</#if>
            
            <#if postalAddress?if_exists.countryGeoId?has_content>
            <#assign country = delegator.findOne("Geo", {"geoId" : "${postalAddress?if_exists.countryGeoId?if_exists}"}, false)!>
            , ${country.geoName?default(country.geoId)}<br/>
            </#if>
            
            <#if postalAddress?if_exists.phoneNumber?has_content>
            <#assign ph1=postalAddress.phoneNumber?substring(0,3) />
            <#assign ph2=postalAddress.phoneNumber?substring(3,6) />
            <#assign ph3=postalAddress.phoneNumber?substring(6) />
            ${ph1?if_exists}-${ph2?if_exists}-${ph3?if_exists}
            </#if>
            <#if postalAddress.isUspsAddrVerified?has_content && postalAddress.isUspsAddrVerified=="Y">
            	<img src="/bootstrap/images/usps-icon.png" title="USPS postal verified"/>
            	<b><a target="_blank" href="/uiadv-portal/control/geoMap?lat=${postalAddress.latitude!}&lan=${postalAddress.longitude!}&contactMechId=${postalAddress.contactMechId!}&externalLoginKey=${requestAttributes.externalLoginKey!}"><img src="/bootstrap/images/marker-icon.png" title="Map It"/></a></a></b>
            </#if>
            <#elseif "TELECOM_NUMBER" == contactMech.contactMechTypeId && contactMechMap.telecomNumber?exists>
            <#assign telecomNumber = contactMechMap.telecomNumber?if_exists>
            <#assign contactNumber=Static["org.groupfio.common.portal.util.DataHelper"].preparePhoneNumber(delegator, telecomNumber.contactNumber?default("0000000000"))/>
            <#if telecomNumber.askForName?has_content><b>${uiLabelMap.toName}:</b> ${telecomNumber.askForName}<br/></#if>
            ${telecomNumber.countryCode?if_exists}
            <#if telecomNumber.areaCode?has_content>${telecomNumber.areaCode?default("000")}-</#if>${contactNumber!}
            <#if partyContactMech.extension?has_content>${uiLabelMap.PartyContactExt}&nbsp;${partyContactMech.extension}</#if>
            
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
          <#--  <#assign partySolicitation = delegator.findByAnd("PartyContactMech", {"partyId" : partySummary.partyId,"contactMechId":contactMech.contactMechId?if_exists}, [], false)?if_exists/>
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
            </#if>-->
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
           <#assign emailAddressDeliverable = Static["org.ofbiz.entity.util.EntityUtil"].getFirst(delegator.findByAnd("PartyContactMech", {"partyId" : partyId,"contactMechId":contactMech.contactMechId}, [], false))?if_exists/>
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
            <#assign idealType = "N"/>
            <#list contactMechMap.partyContactMechPurposes as partyContactMechPurpose>
            <#if partyContactMechPurpose.contactMechPurposeTypeId.contains("IDEAL")>
            <#assign idealType = "Y"/>
            </#if>
            </#list>
            
         </td>
      </tr>
      </#list>
   </tbody>
</table>
</div>

<#if requestURI != "viewCustomer">
<div class="panel-group" id="accordionMenu" role="tablist" aria-multiselectable="true">

</div>
</#if>
</#if>