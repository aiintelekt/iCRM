<div class="panel panel-default">
   <div class="panel-heading" role="tab" id="headingTwo">
      <h4 class="panel-title">
         <a class="collapsed" role="button" data-toggle="collapse" data-parent="#accordionMenu" href="#Bookmarks" aria-expanded="false" aria-controls="headingTwo">
         Attachments
         </a>
      </h4>
   </div>
     <#assign roleType = "CONTACT"/>
     <#assign menuId = "ContactBar"/>
     <#if request.getRequestURI().contains("viewLead")>
     <#assign roleType = "LEAD"/>
     <#assign menuId = "LeadBar"/>
     <#elseif request.getRequestURI().contains("viewAccount")>
     <#assign roleType = "ACCOUNT"/>
     <#assign menuId = "AccountBar"/>
     </#if>
   <form method="post" action="createBookMarkUrl?partyId=${partySummary?if_exists.partyId?if_exists}" id="createBookMarkUrl" class="form-horizontal" name="createBookMarkUrl" novalidate="novalidate" data-toggle="validator">
      <div id="Bookmarks" class="panel-collapse collapse" data-parent="#accordionMenu" aria-labelledby="PendingActivities">
         <div class="panel-body">
            <div class="float-right">
               <#-- <a class="btn btn-xs btn-primary m5" href="createBookMarkUrl?partyId=${partySummary?if_exists.partyId?if_exists}&url=Y&select=${menuId}">Bookmark URL</a>-->
               <a class="btn btn-xs btn-primary m5" href="createBookMarkUrl?partyId=${partySummary?if_exists.partyId?if_exists}&url=N&select=${menuId}">Attachments</a>
            </div>
            <div class="table-responsive">
               <table class="table table-striped">
                  <thead>
                     <tr>
                        <th>Name</th>
                        <th>Classification</th>
                        <th>Description</th>
                        <th>Created Date </th>
                     </tr>
                  </thead>
                  <#assign objectIdParam="partyId=" + partySummary?if_exists.partyId?if_exists?default( "")/>
                  <#assign downloadLink="downloadPartyContent?" + objectIdParam/>
                  <#if content?exists && content.size() !=0>
                  <#assign rowCount=0 />
                  
                  <#assign content = (Static["org.fio.crm.content.ContentHelper"].getContentInfoForParty(partySummary?if_exists.partyId?if_exists,roleType,delegator))?if_exists>
                  <#list content as item>
                  <#assign rowClass="rowLightGray" />
                  <#if rowCount % 2==0>
                  <#assign rowClass="rowWhite" />
                  </#if>
                  <#assign hyperlink=false/>
                  <#assign file=false/>
                  <#assign document=false/>
                  <#--<#assign data=i tem.getRelatedOne( "DataResource")/>-->
                  <#assign data=delegator.findOne( "DataResource",Static[ "org.ofbiz.base.util.UtilMisc"].toMap( "dataResourceId",item.dataResourceId?if_exists),true)?if_exists/>
                  <#-- <#assign classification=item.getRelatedOneCache( "Enumeration")?if_exists/> -->
                  <#assign classification=delegator.findOne( "Enumeration",Static[ "org.ofbiz.base.util.UtilMisc"].toMap( "enumId",item.classificationEnumId?if_exists),true)?if_exists/>
                  <#if data?exists && data.objectInfo?has_content>
                  <#if item.contentTypeId=="HYPERLINK">
                  <#assign hyperlink=true/>
                  <#elseif item.contentTypeId=="FILE">
                  <#assign file=true/>
                  </#if>
                  <#else>
                  <#if item.contentTypeId=="DOCUMENT">
                  <#assign document=true/>
                  </#if>
                  </#if>
                  <tbody>
                     <tr class="${rowClass}">
                        <td class="tabletext">
                           <#if hyperlink>
                           <a target="top" class="linktext" href="${data.objectInfo}"></#if>
                           <#if file>
                           <a class="linktext" href="<@ofbizUrl>${downloadLink}&contentId=${item.contentId}</@ofbizUrl>"></#if>
                           <#if document>
                           <a class="linktext" href="<@ofbizUrl>ViewSimpleContent?contentId=${item.contentId}</@ofbizUrl>"></#if>
                           ${item.contentName?if_exists}
                           <#if (hyperlink || file || document)>
                           </a>
                           </#if>
                        </td>
                        <td class="tabletext">${classification.description?if_exists}</td>
                        <td class="tabletext">${item.description?if_exists}</td>
                        <td class="tabletext">${item.createdDate}</td>
                        <td class="tabletext">
                           <a class="fa fa-edit btn btn-xs btn-primary m5" href="createBookMarkUrl?partyId=${partySummary?if_exists.partyId?if_exists}&url=<#if hyperlink>Y<#else>N</#if>&contentId=${item.contentId?if_exists}"></a>
                        </td>
                        <td class="tabletext">
                           <a class="fa fa-times btn btn-xs btn-danger m5" href="createBookMarkUrl?partyId=${partySummary?if_exists.partyId?if_exists}&url=<#if hyperlink>Y<#else>N</#if>&contentId=${item.contentId?if_exists}"></a>
                        </td>
                        <#--
                        <td class="tabletext">
                           <@form name="removeContent_${item.contentId}" url="removeContent" contentId="${item.contentId}" partyId="" custRequestId="" workEffortId="" salesOpportunityId="" orderId="" quoteId="" donePage="${donePage}" />
                           <a href="<@ofbizUrl>${updateContentTarget}&contentId=${item.contentId}&contentTypeId=${item.contentTypeId}</@ofbizUrl>" class="buttontext">${uiLabelMap.CommonEdit}</a>
                        </td>
                        <td class="tabletext">
                           <a class="buttontext" href="javascript:opentaps.submitForm('removeContent_${item.contentId}', null, {'${objectIdParamJs}'});">${uiLabelMap.CommonRemove}</a
                        </td>
                        -->
                     </tr>
                  </tbody>
                  <#assign rowCount = rowCount + 1/>
                  </#list>
                  </#if>
               </table>
            </div>
         </div>
      </div>
</form>
</div>