<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<div class="row">
    <div id="main" role="main">
    <@sectionFrameHeader title="${uiLabelMap.UpdateAccount!}" />  
    <div class="col-md-12 col-lg-12 col-sm-12 ">
  <form method="post" action="<@ofbizUrl>updateAccount</@ofbizUrl>" id="updateAccountForm" class="form-horizontal" name="updateAccountForm" novalidate="novalidate" data-toggle="validator">
     <@inputHidden id="partyId" value="${parameters.partyId?if_exists}"/>
     <@inputHidden id="accountName" value="${partySummary?if_exists.groupName?if_exists}" />
     <div class="row p-2">
        <div class="col-md-6 col-sm-6">
           <div class="form-group row has-error">
              <label class="col-sm-4 col-form-label">${uiLabelMap.companyName!}</label>
              <div class="col-sm-7">
                 <input type="text" class="form-control input-sm" value="${partySummary?if_exists.groupName?if_exists}" placeholder="${uiLabelMap.companyName!}"  required readonly>
                 <div class="help-block with-errors"></div>
              </div>
           </div>
           <@inputRow 
               id="groupNameLocal"
               label=uiLabelMap.localName
               placeholder=uiLabelMap.localName
               value="${partySummary?if_exists.groupNameLocal?if_exists}"
               />
           <@inputRow 
               id="annualRevenue"
               label=uiLabelMap.annualRevenue
               placeholder=uiLabelMap.annualRevenue
               value="${partySummary?if_exists.annualRevenue?if_exists}"
               />
           <div class="form-group row">
              <label  class="col-sm-4 col-form-label">${uiLabelMap.DBSIC!}</label>
              <div class="col-sm-7">
                 <#assign enumerations = Static["org.fio.crm.util.UtilCommon"].getEnumerations("PARTY_INDUSTRY", delegator)/>
                 <select class="ui dropdown search form-control input-sm" name="industryEnumId">
                    <option value="" disabled selected>Select DBSIC</option>
                    <#list enumerations as enum>
                    <#if enum.disabled?has_content && enum.disabled="Y">
                    <#else>
                    <option value="${enum.enumId}"<#if partySummary?if_exists.industryEnumId?if_exists?if_exists==enum.enumId?if_exists>Selected</#if>>${enum.description}</option>   
                    </#if>
                    </#list>
                 </select>
              </div>
           </div>
           <div class="form-group row">
              <label  class="col-sm-4 col-form-label">${uiLabelMap.constitution!}</label>
              <div class="col-sm-7">
                 <#assign ownershipList = delegator.findByAnd("Enumeration",Static["org.ofbiz.base.util.UtilMisc"].toMap("enumTypeId","PARTY_OWNERSHIP"),[],false)?if_exists/>
                 <select class="ui dropdown search form-control input-sm" name="ownershipEnumId" >
                    <option value="" disabled selected>Select Constitution</option>
                    <#list ownershipList as owner>
                    <option value="${owner.enumId?if_exists}"  <#if partySummary?if_exists.ownershipEnumId?if_exists==owner.enumId?if_exists>selected</#if> >${owner.description?if_exists}</option>
                    </#list>
                 </select>
              </div>
           </div>
           <#--<div class="form-group row">
              <label  class="col-sm-4 col-form-label">${uiLabelMap.sicCode!}</label>
              <div class="col-sm-7">
                 <input type="text" class="form-control input-sm" id="sicCode" name="sicCode" value="${partySummary?if_exists.sicCode?if_exists}" placeholder="SIC Code">
              </div>
           </div>-->
           <@inputArea 
               id="description"
               label=uiLabelMap.description
               placeholder=uiLabelMap.description
               value="${partySummary?if_exists.description?if_exists}"
               rows=3
               />           
           <@inputRow 
               id="partyTaxId"
               label=uiLabelMap.taxExemptNumber
               placeholder=uiLabelMap.taxExemptNumber
               value="${partyTaxId?if_exists}"
               />
        </div>
        <div class="col-md-6 col-sm-6">
           <div class="form-group row">
              <label  class="col-sm-4 col-form-label">${uiLabelMap.parentAccount!}</label>
              <div class="col-sm-7">
                 <div class="input-group">
                    <input type="text" name="parentPartyId" id="parentPartyId" class="form-control input-sm" value="${parentParty?if_exists.partyId?if_exists}" placeholder="Parent Account">
                    <span class="input-group-addon">
                    <span class="glyphicon glyphicon-list-alt" data-toggle="modal" data-target="#parentAccountModal" id="findAccount">
                    </span>
                    </span>
                 </div>
              </div>
           </div>
           <@inputRow 
               id="officeSiteName"
               label=uiLabelMap.siteName
               placeholder=uiLabelMap.siteName
               value="${partySummary?if_exists.officeSiteName?if_exists}"
               />
           <div class="form-group row">
              <label  class="col-sm-4 col-form-label">${uiLabelMap.preferredCurrency!}</label>
              <div class="col-sm-7">
                 <#assign currencies = delegator.findByAnd("Uom",{"uomTypeId","CURRENCY_MEASURE"},Static["org.ofbiz.base.util.UtilMisc"].toList("abbreviation"), false)?if_exists/>
                 <#assign defaultCourrencyUomId = (Static["org.ofbiz.base.util.UtilProperties"].getPropertyValue("crm", "defaultCurrencyUomId"))?default("USD")/>
                 <select class="ui dropdown search form-control input-sm"  id="currencyUomId" name="currencyUomId">
                    <option value="" disabled selected>Select Currency</option>
                    <#list currencies as currency>
                    	<option value="${currency.uomId}" <#if partySummary?if_exists.currencyUomId?if_exists==currency.uomId?if_exists>selected</#if>>${currency.description}</option>
                    </#list>
                 </select>
              </div>
           </div>
           <@inputRow 
               id="numberEmployees"
               label=uiLabelMap.numberOfEmployees
               placeholder=uiLabelMap.numberOfEmployees
               value="${partySummary?if_exists.numberEmployees?if_exists}"
               />
           <@inputRow 
               id="tickerSymbol"
               label=uiLabelMap.tickerSymbol
               placeholder=uiLabelMap.tickerSymbol
               value="${partySummary?if_exists.tickerSymbol?if_exists}"
               />
     		<@inputArea 
               id="importantNote"
               label=uiLabelMap.note
               placeholder=uiLabelMap.note
               value="${partySummary?if_exists.importantNote?if_exists}"
               rows=3
               />               
              <#if PartyTaxAuthInfoList?has_content>
			    <#list PartyTaxAuthInfoList as PartyTaxAuthInfo>
			    <#if PartyTaxAuthInfo?has_content>
			    	<#assign partyTaxId = PartyTaxAuthInfo.partyTaxId?if_exists />
			    	<#assign isExempt = PartyTaxAuthInfo.isExempt?if_exists />
			    	<#break/>
			    </#if>
			    </#list>
			    </#if>
           <@dropdownCell required=true
                id="isExempt"
                label=uiLabelMap.isTaxExempt
                options=yesNoOptions                
                allowEmpty=false
                placeholder=uiLabelMap.isTaxExempt
                value="${isExempt}"
                />
        </div>
     </div>
     <div class="col-md-12 col-sm-12">
        <div class="form-group row">
           <div class="offset-sm-2 col-sm-9 mb-3 pl-2">
              <@submit label=uiLabelMap.UpdateAccount />
           </div>
        </div>
     </div>
  </form>
  <div class="clearfix"> </div>
  <div class="panel-group" id="accordionMenu" role="tablist" aria-multiselectable="true">
     <div class="panel panel-default">
        <div class="panel-heading" role="tab" id="headingTwo">
           <h4 class="panel-title">
              <a class="collapsed" role="button" data-toggle="collapse" data-parent="#accordionMenu" href="#sources" aria-expanded="false" aria-controls="headingTwo">
              Sources
              </a>
           </h4>
        </div>
        <div id="sources" class="panel-collapse collapse show" data-parent="#accordionMenu" aria-labelledby="">
          <div class="panel-body">               
           <#if dataSources?exists && dataSources?has_content>
              <div class="table-responsive">
                 <table class="table table-striped">
                    <thead>
                       <tr>
                          <th>Source</th>
                          <th>From Date</th>
                          <th>Remove</th>
                       </tr>
                    </thead>
                    <tbody>
                    <#list dataSources as dataSource>
                       <tr>
                          <td>
                          		<#assign dataSourceGv = dataSource.getRelatedOne("DataSource", false)! />
                          		<#if dataSourceGv?has_content>
                          			${dataSourceGv.description?if_exists}
                          		</#if>
                          </td>
                          <td>${dataSource.fromDate}</td>
                          <td>
                          	<form method="post" action="removeAccountDataSource" name="removeDataSource" id="removeDataSource" >
                          		<input type="hidden" name="partyId" id="partyId" value="${parameters.partyId?if_exists}"/>
                          		<input type="hidden" name="dataSourceId" id="dataSourceId" value="${dataSource.dataSourceId?if_exists}"/>
                          		<input type="hidden" name="fromDate" id="fromDate" value="${dataSource.fromDate?if_exists}"/>
                          	</form>
                          <a href="javascript:document.removeDataSource.submit();" ><span class="glyphicon glyphicon-remove btn btn-xs btn-danger"></span></a></td>
                       </tr>
                    </#list>
                    </tbody>
                 </table>
              </div>
           </#if>
           <div class="row padding-r">
           <div class="col-md-6 col-sm-6">
             <div class="form-group row has-error">
                <label class="col-sm-4 col-form-label">New Data Source</label>
                <div class="col-sm-7">
                <form name="addAccountDataSource" method="post" action="addAccountDataSource">
                	<input type="hidden" name="partyId" id="partyId" value="${parameters.partyId!}"/>
                   <select class="ui dropdown search form-control input-sm"  name="dataSourceId" id="dataSourceId" >
                      <option value="" disabled selected>Select Data Source</option>
                      <#if dataSourceList?has_content>
                      <#list dataSourceList as dataSource>
                        <option value="${dataSource.dataSourceId}">${dataSource.description}</option>
                      </#list>
                      </#if>
                   </select>
                   <div class="help-block with-errors"></div>
                </form>
                </div>
             </div>
             <div class="form-group row">
                <div class="offset-sm-2 col-sm-9">
                   <a href="javascript:document.addAccountDataSource.submit();"><span class="btn btn-sm btn-primary mb-2">${uiLabelMap.CommonAdd}</span></a>
                </div>
             </div>
           </div>
           </div>
		  </div> 
        </div>
     </div> <!-- data source end-->
     
     <div class="panel panel-default">
        <div class="panel-heading" role="tab" id="headingTwo">
           <h4 class="panel-title">
              <a class="collapsed" role="button" data-toggle="collapse" data-parent="#accordionMenu" href="#mktCampaigns" aria-expanded="false" aria-controls="headingTwo">
              Marketing Campaigns
              </a>
           </h4>
        </div>
        <div id="mktCampaigns" class="panel-collapse collapse" data-parent="#accordionMenu" aria-labelledby="">
          <div class="panel-body">
           <#if marketingCampaigns?exists && marketingCampaigns?has_content>
              <div class="table-responsive">
                 <table class="table table-striped">
                    <thead>
                       <tr>
                          <th>Marketing Campaign</th>
                          <th>Remove</th>
                       </tr>
                    </thead>
                    <tbody>
                       <#list marketingCampaigns as campaign>
                       <tr>
                          <td><a href="viewMarketingCampaign?marketingCampaignId="+ ${campaign.marketingCampaignId?if_exists} >${campaign.campaignName?if_exists}</a></td>
                          <td>
                          <form method="post" action="removeAccountMarketingCampaign" name="removeMktCampaign" id="removeMktCampaign" >
                          		<input type="hidden" name="partyId" id="partyId" value="${parameters.partyId?if_exists}"/>
                          		<input type="hidden" name="marketingCampaignId" id="marketingCampaignId" value="${campaign.marketingCampaignId?if_exists}"/>
                          	</form>
                          <a href="javascript:document.removeMktCampaign.submit();" ><span class="glyphicon glyphicon-remove btn btn-xs btn-danger"></span></a>
                          </td>
                          
                          
                       </tr>
                       </#list>
                    </tbody>
                 </table>
              </div>
           </#if>
         <div class="row padding-r">
           <div class="col-md-6 col-sm-6">
             <div class="form-group row has-error">
                <label class="col-sm-4 col-form-label">New Marketing Campaign</label>
                <div class="col-sm-7">
                <form name="addAccountMarketingCampaign" method="post" action="addAccountMarketingCampaign" class="row" novalidate="true" data-toggle="validator">
                	<input type="hidden" name="partyId" id="partyId" value="${parameters.partyId!}"/>
                 <#assign marketingCampainList = delegator.findByAnd("MarketingCampaign", Static["org.ofbiz.base.util.UtilMisc"].toMap("statusId", "MKTG_CAMP_INPROGRESS"), Static["org.ofbiz.base.util.UtilMisc"].toList("campaignName ASC"),false) />
                   <select class="ui dropdown search form-control input-sm"  name="marketingCampaignId" id="marketingCampaignId" required>
                      <option value="" disabled selected>Select Campaign</option>
                      <#if marketingCampainList?has_content>
                      <#list marketingCampainList as marketingCampain>
                        <option value="${marketingCampain.marketingCampaignId}">${marketingCampain.campaignName}</option>
                      </#list>
                      </#if>
                   </select>
                   <div class="help-block with-errors"></div>
                </form>
                </div>
             </div>
             <div class="form-group row">
                <div class="offset-sm-2 col-sm-9">
                   <a href="javascript:document.addAccountMarketingCampaign.submit();"><span class="btn btn-sm btn-primary mt">${uiLabelMap.CommonAdd}</span></a>
                </div>
             </div>
           </div>
          </div>
        </div>
        </div>
     </div>
     <div class="clearfix"> </div>
  </div>
<#include "component://crm/webapp/crm/account/accountModels.ftl">
	</div>
	</div><#-- End main-->
</div><#-- End row-->
