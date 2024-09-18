<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>

<div class="page-header"> 
   <div class="float-right">
      <span class="fa fa-files-o btn btn-xs btn-primary m5" data-toggle="modal" data-target="#myModal" alt="Report" title="Report"></span>
      <a href="<@ofbizUrl>editAccount?partyId=${parameters.partyId?if_exists}</@ofbizUrl>"><span class="fa fa-edit btn btn-xs btn-primary m5" data-toggle="modal" data-target="" alt="Edit" title="Edit"></span></a>
      <#if hasDeactivatePermission?default(false)>
      	<a class="fa fa-times btn btn-xs btn-danger m5" data-toggle="confirmation" href="javascript:document.deactivateAccountForm.submit();" alt="Deactivate Account" title="Are you sure?	Do you want to deactivate"></a>
      	<form name="deactivateAccountForm" id="deactivateAccountForm" action="deactivateAccount" method="post">
      		<input type="hidden" name="partyId" value="${parameters.partyId!}">
      	</form>
      </#if>
   </div>
   <h2 class="">Account
   <#if accountDeactivated?exists>
    <span class="text-danger font-weight-bold align-middle pl-3"> ${uiLabelMap.CrmAccountDeactivated} ${getLocalizedDate(accountDeactivatedDate, "DATE_TIME")}</span>
   </#if>
   </h2>
</div>
<div class="row mx-1">
   <div class="col-md-4 col-sm-4 form-horizontal pl-0">
      <div class="form-group row has-error mb-0">
         <label  class="col-sm-5 col-form-label has-error">${uiLabelMap.companyName!}</label>
         <div class="col-sm-7">
            <label class="col-form-label input-sm fw">${partySummary?if_exists.groupName?if_exists}  <#if partySummary?exists>(${partySummary?if_exists.partyId?if_exists})</#if></label>
         </div>
      </div>
      <div class="form-group row mb-0">
         <label  class="col-sm-5 col-form-label has-error">Site Name</label>
         <div class="col-sm-7">
            <label class="col-form-label input-sm fw">${partySummary?if_exists.officeSiteName?if_exists}</label>
         </div>
      </div>
      <div class="form-group row mb-0">
         <label  class="col-sm-5 col-form-label has-error">Number Of Employees</label>
         <div class="col-sm-7">
            <label class="col-form-label input-sm fw">${partySummary?if_exists.numberEmployees?default("-")}</label>
         </div>
      </div>
      <div class="form-group row mb-0">
         <label  class="col-sm-5 col-form-label">${uiLabelMap.constitution!}</label>
         <div class="col-sm-7">
         <#assign ownershipList = delegator.findByAnd("Enumeration",Static["org.ofbiz.base.util.UtilMisc"].toMap("enumTypeId","PARTY_OWNERSHIP"),[],false)?if_exists/>
            <#list ownershipList as owner>
           <#if partySummary?if_exists.ownershipEnumId?if_exists==owner.enumId?if_exists>
           		<label class="col-form-label input-sm fw">${owner?if_exists.description?default("-")}</label>
           </#if>
            </#list>
         </div>
      </div>
      <div class="form-group row mb-0">
         <label  class="col-sm-5 col-form-label">Description</label>
         <div class="col-sm-7">
            <label class="col-form-label input-sm fw">${partySummary?if_exists.description?default("-")}</label>
         </div>
      </div>
      <#if hasReassignPermission?default(false)>
      <div class="form-group row mb-0">
         <label  class="col-sm-5 col-form-label">Person Responsible For</label>
         <#--<#assign responsibleForName><#if responsibleParty?exists>${responsibleParty?if_exists.firstName?if_exists} ${responsibleParty?if_exists.lastName?if_exists}<#else>${uiLabelMap.CommonNone}</#if></#assign>-->
         <div class="col-sm-7">
            <label class="col-form-label input-sm fw"><#if personResponsible?exists && personResponsible?has_content>${personResponsible?if_exists}<#else>${uiLabelMap.CommonNone}</#if></label>
            <span class="fa fa-user-o btn btn-xs btn-primary" data-toggle="modal" data-target="#teamMemberModal" alt="Reassign" title="Reassign"></span>
         </div>
      </div>
      </#if>
      <div class="form-group row mb-0">
         <label  class="col-sm-5 col-form-label">GCIN</label>
         <div class="col-sm-7">
            <label class="col-form-label input-sm fw"><#if hdpCustomerSme?exists>${hdpCustomerSme.gcin?if_exists?default("-")}<#else>-</#if></label>
         </div>
      </div>
      <div class="form-group row mb-0">
         <label  class="col-sm-5 col-form-label">Date of Incorporation</label>
         <div class="col-sm-7">
            <label class="col-form-label input-sm fw"><#if hdpCustomerSme?exists>${hdpCustomerSme.dateOfRegistration?if_exists?default("-")}<#else>-</#if></label>
         </div>
      </div>
       <div class="form-group row mb-0">
         <label  class="col-sm-5 col-form-label">Constituion Code</label>
         <div class="col-sm-7">
            <label class="col-form-label input-sm fw"><#if hdpCustomerSme?exists>${hdpCustomerSme.constitution?if_exists?default("-")}<#else>-</#if></label>
         </div>
      </div>
      <#--
      <div class="form-group row">
         <label  class="col-sm-5 col-form-label">Total of Sales per Year</label>
         <div class="col-sm-7">
            <label class="col-form-label fw">
               <div class="table-responsive">
                  <table class="table table-striped">
                     <thead>
                        <tr>
                           <th>Sales 2018</th>
                           <th>Sales 2017	</th>
                           <th>Sales 2016</th>
                           <th>Sales 2015</th>
                        </tr>
                     </thead>
                     <tbody>
                        <tr>
                           <td>$0.00</td>
                           <td>$0.00</td>
                           <td>$0.00</td>
                           <td>$0.00</td>
                        </tr>
                     </tbody>
                  </table>
               </div>
            </label>
         </div>
      </div>
      -->
      <div class="form-group row mb-0"></div>
   </div>
   <div class="col-md-4 col-sm-4 form-horizontal">
      <div class="form-group row mb-0">
         <label  class="col-sm-5 col-form-label">Parent Account</label>
         <div class="col-sm-7">
            <label class="col-form-label input-sm fw">
            <a href="viewAccount?partyId=${parentParty?if_exists.partyId?if_exists}">${parentParty?if_exists.groupName?if_exists} <#if parentParty?exists && parentParty?has_content>(${parentParty?if_exists.partyId?if_exists})</#if></a>
            </label>
         </div>
      </div>
      <div class="form-group row mb-0">
         <label  class="col-sm-5 col-form-label">Preferred Currency</label>
         <div class="col-sm-7">
            <label class="col-form-label input-sm fw">${partySummary?if_exists.currencyUomId?default("-")}</label>
         </div>
      </div>
      <div class="form-group row mb-0">
         <label  class="col-sm-5 col-form-label">${uiLabelMap.DBSIC}</label>
         <div class="col-sm-7">
            <#assign partySummaryindustry = ""/>
            <#if partySummary?if_exists.industryEnumId?exists && partySummary?if_exists.industryEnumId?has_content>
            <#assign Enumeration = delegator.findOne("Enumeration",{"enumId":partySummary?if_exists.industryEnumId?if_exists},true)?if_exists />    		
            <#if Enumeration?exists && Enumeration?has_content>
            <#assign industryEnum = Enumeration.description?if_exists/>
            <#if industryEnum.contains("Select")>
            <#assign partySummaryindustry = " "/>
            <#else>
            <#assign partySummaryindustry = "${industryEnum?if_exists}"/>
            </#if>
            </#if>
            </#if>
            <label class="col-form-label input-sm fw">${partySummaryindustry?default("-")}
            	<#if partySummary?has_content && partySummary.industryEnumId?has_content>
            		(${partySummary?if_exists.industryEnumId?if_exists})
            	</#if>
            </label>
         </div>
      </div>
      <div class="form-group row mb-0">
         <label  class="col-sm-5 col-form-label">LRM</label>
         <div class="col-sm-7">
            <label class="col-form-label input-sm fw"><#if hdpCustomerSme?exists>${hdpCustomerSme.rm?if_exists?default("-")}<#else>-</#if></label>
         </div>
      </div>
      <div class="form-group row mb-0">
         <label  class="col-sm-5 col-form-label">Customer Owner</label>
         <div class="col-sm-7">
            <label class="col-form-label input-sm fw"><#if hdpCustomerSme?exists>${hdpCustomerSme.seg?if_exists?default("-")}<#else>-</#if></label>
         </div>
      </div>
      <div class="form-group row mb-0">
         <label  class="col-sm-5 col-form-label">IBG Segment</label>
         <div class="col-sm-7">
            <label class="col-form-label input-sm fw"><#if hdpCustomerSme?exists>${hdpCustomerSme.segment?if_exists?default("-")}<#else>-</#if></label>
         </div>
      </div>
      <div class="form-group row mb-0">
         <label  class="col-sm-5 col-form-label">IBG Team</label>
         <div class="col-sm-7">
            <label class="col-form-label input-sm fw"><#if hdpCustomerSme?exists>${hdpCustomerSme.team?if_exists?default("-")}<#else>-</#if></label>
         </div>
      </div>
      <#--<div class="form-group row mb-0">
         <label  class="col-sm-5 col-form-label">Ticker Symbol</label>
         <div class="col-sm-7">
            <label class="col-form-label input-sm fw">${partySummary?if_exists.tickerSymbol?default("-")}</label>
         </div>
      </div>
      <div class="form-group row mb-0">
         <label  class="col-sm-5 col-form-label">Note</label>
         <div class="col-sm-7">
            <label class="col-form-label input-sm fw">${partySummary?if_exists.importantNote?default("-")}</label>
         </div>
      </div>
      <div class="form-group row mb-0">
         <label  class="col-sm-5 col-form-label">Marketing Campaigns</label>
         <div class="col-sm-7">
            <label class="col-form-label input-sm fw">${marketingCampaignsAsString?default("-")}</label>
         </div>
      </div>
      <div class="form-group row mb-0">
         <label  class="col-sm-5 col-form-label">Payments received 2018	</label>
         <div class="col-sm-7">
            <label class="col-form-label input-sm fw">
            <@ofbizCurrency amount=paymentReceived?default({}).amount?default(0) currencyUomId=partySummary?if_exists.currencyUomId?default("") />
            </label>
         </div>
      </div>-->
   </div>
   <div class="col-md-4 col-sm-4 form-horizontal">
      <div class="form-group row mb-0">
         <label  class="col-sm-5 col-form-label">Local Name	</label>
         <div class="col-sm-7">
            <label class="col-form-label input-sm fw">${partySummary?if_exists.groupNameLocal?default("-")}</label>
         </div>
      </div>
      <div class="form-group row mb-0">
         <label  class="col-sm-5 col-form-label">Annual Revenue</label>
         <div class="col-sm-7">
            <label class="col-form-label input-sm fw">
            <@ofbizCurrency amount= partySummary?if_exists.annualRevenue?default(0) isoCode=partySummary?if_exists.currencyUomId?default("")/>
            </label>
         </div>
      </div>
      <div class="form-group row mb-0">
         <label  class="col-sm-5 col-form-label">Is Tax Exempt</label>
         <div class="col-sm-7">
            <label class="col-form-label input-sm fw">${isExempt?default("-")}</label>
         </div>
      </div>
      <div class="form-group row mb-0">
         <label  class="col-sm-5 col-form-label">Tax Exempt Number	</label>
         <div class="col-sm-7">
            <label class="col-form-label input-sm fw">${partyTaxId?default("-")}</label>
         </div>
      </div>
       <div class="form-group row mb-0">
         <label  class="col-sm-5 col-form-label">Finacle ID</label>
         <div class="col-sm-7">
            <label class="col-form-label input-sm fw"><#if hdpCustomerSme?exists>${hdpCustomerSme.cifId?if_exists?default("-")}<#else>-</#if></label>
         </div>
      </div>
       <div class="form-group row mb-0">
         <label  class="col-sm-5 col-form-label">ACRR</label>
         <div class="col-sm-7">
            <label class="col-form-label input-sm fw"><#if hdpCustomerSme?exists>${hdpCustomerSme.acrr?if_exists?default("-")}<#else>-</#if></label>
         </div>
      </div>
       <div class="form-group row mb-0">
         <label  class="col-sm-5 col-form-label">Watchlist</label>
         <div class="col-sm-7">
            <label class="col-form-label input-sm fw"><#if hdpCustomerSme?exists>${hdpCustomerSme.smeWatchlistInd?if_exists?default("-")}<#else>-</#if></label>
         </div>
      </div>
	  
      <#-- <div class="form-group row mb-0">
         <label  class="col-sm-5 col-form-label">Classifications</label>
         <div class="col-sm-7">
            <label class="col-form-label input-sm fw">${partyClassificationGroupsDisplay?default("-")}</label>
         </div>
      </div>-->
      <div class="form-group row mb-0">
         <label  class="col-sm-5 col-form-label">Sources</label>
         <div class="col-sm-7">
            <label class="col-form-label input-sm fw">
            <#if dataSources?exists && dataSources?has_content>
            <#assign count=0/>
            <#list dataSources as dataSources>
            <#assign dataSourct = delegator.findOne("DataSource", {"dataSourceId" : dataSources.dataSourceId?if_exists}, true)?if_exists />
            <#if dataSourct?if_exists.description?if_exists != "Select">
            <#if count == 1>,</#if>
            	${dataSourct?if_exists.description?if_exists}
            <#assign count=count+1/>
            </#if>
            </#list>
            <#else>
               -
            </#if>
            </label>
         </div>
      </div>
	  
	  <#--
	  <form method="post" action="assignTeamToAccount" id="assignteamForm" class="form-horizontal" name="assignteamForm" novalidate="novalidate" data-toggle="validator">
      <div class="form-group row">
         <label  class="col-sm-5 col-form-label">Assign Team</label>
            <input type="hidden" name="partyId" value="${parameters.partyId!}">
            <input type="hidden" name="accountPartyId" value="${parameters.partyId!}">
         <div class="col-sm-5">
            <select class="ui dropdown form-control input-sm" data-live-search="true">
               <option value="" disabled selected>Select Team</option>
               <#if teamList?exists && teamList?has_content>
		        <#list teamList as team>
		        	 <option value="${team.partyId}">${team.groupName}</option>
		        </#list>
		      </#if>
            </select>
         </div>
         <div class="col-sm-2"><button type="reset" class="btn btn-xs btn-primary navbar-dark">Assign </button></div>
      </div>
      </form>
      -->
   </div>
</div>
<div class="clearfix"> </div>

<#--
<form method="post" action="createLead" id="createLeadForm" class="form-horizontal" name="createLeadForm" novalidate="novalidate" data-toggle="validator">
   <input type="hidden" name="duplicatingPartyId">
   <input type="hidden" name=" ">
   <input type="hidden" name="">
   <div class="panel-group" id="accordionMenu" role="tablist" aria-multiselectable="true">
      <div class="panel panel-default">
         <div class="panel-heading" role="tab" id="headingTwo">
            <h4 class="panel-title">
               <a class="collapsed" role="button" data-toggle="collapse" data-parent="#accordionMenu" href="#CustomerHistory" aria-expanded="false" aria-controls="headingTwo">
               Customer History    &nbsp;&nbsp;<span class="text-right text-primary"> LTD 0pts.&nbsp;&nbsp; TYTD 0pts.&nbsp;&nbsp; LYTD 0pts. </span>
               </a>
            </h4>
         </div>ditAccount?partyId=10640
         <div id="CustomerHistory" class="panel-collapse collapse show" data-parent="#accordionMenu" aria-labelledby="PendingActivities">
            <div class="panel-body">
               <ul class="nav nav-tabs">
                  <li class="nav-item"><a data-toggle="tab" href="#Sales" class="nav-link active">Sales </a></li>
                  <li class="nav-item"><a data-toggle="tab" href="#Coupons" class="nav-link">Coupons</a></li>
               </ul>
               <div class="tab-content">
                  <div id="Sales" class="tab-pane fade show active">
                  </div>
                  <div id="Coupons" class="tab-pane fade in">
                  </div>
               </div>
               <div class="row">
                  <div class="col-md-2 col-sm-2">
                     <div class="form-group row">
                        <div class="col-sm-12">
                           <select class="ui dropdown form-control input-sm" data-live-search="true">
                              <option>Select Campaign</option>
                              <option value="Excel">All</option>
                           </select>
                        </div>
                     </div>
                  </div>
                  <div class="col-md-2 col-sm-2">
                     <div class="form-group row">
                        <div class="col-sm-12">
                           <select class="ui dropdown form-control input-sm" data-live-search="true">
                              <option>Select Sales REP</option>
                              <option value="Excel">All</option>
                           </select>
                        </div>
                     </div>
                  </div>
                  <div class="col-md-2 col-sm-2">
                     <div class="form-group row">
                        <div class="col-sm-12">
                           <select class="ui dropdown form-control input-sm" data-live-search="true">
                              <option>Select Time Zone</option>
                              <option value="Excel">All</option>
                           </select>
                        </div>
                     </div>
                  </div>
                  <div class="col-md-2 col-sm-2">
                     <div class="form-group row">
                        <div class="col-sm-12">
                           <select class="ui dropdown form-control input-sm" data-live-search="true">
                              <option>Select Call Status</option>
                              <option value="Excel">All</option>
                           </select>
                        </div>
                     </div>
                  </div>
               </div>
            </div>
         </div>
      </div>
      <div class="panel panel-default">
         <div class="panel-heading" role="tab" id="headingTwo">
            <h4 class="panel-title">
               <a class="collapsed" role="button" data-toggle="collapse" data-parent="#accordionMenu" href="#Payment" aria-expanded="false" aria-controls="headingTwo">
               Payment and Shipping Accounts
               </a>
            </h4>
         </div>
         <div id="Payment" class="panel-collapse collapse" data-parent="#accordionMenu" aria-labelledby="PendingActivities">
            <div class="panel-body">
               <p class="float-right">
                  <select class="ui dropdown form-control input-sm" data-live-search="true">
                     <option value="">Create New</option>
                     <option value="">Begins With</option>
                     <option value="">Is Empty</option>
                     <option value="">Not Equal</option>
                  </select>
               </p>
               <div class="clearfix"> </div>
               No payment method information on file.
               <div class="clearfix"> </div>
            </div>
         </div>
      </div> -->
    <div class="panel-group" id="accordionMenu" role="tablist" aria-multiselectable="true">
      <#--<div class="panel panel-default">
         <div class="panel-heading" role="tab" id="headingTwo">
            <h4 class="panel-title">
               <a class="collapsed" role="button" data-toggle="collapse" data-parent="#accordionMenu" href="#ActivitiesHistory" aria-expanded="false" aria-controls="headingTwo">
               Activities History
               </a>
            </h4>
         </div>
         <div id="ActivitiesHistory" class="panel-collapse collapse show" data-parent="#accordionMenu" aria-labelledby="PendingActivities">
            <div class="panel-body">
               <p class="float-right">
                  <button type="reset" class="btn btn-xs btn-primary mt"> Log Call</button>
                  <button type="reset" class="btn btn-xs btn-primary mt"> Log Email</button>
               </p>
               <div class="table-responsive">
                  <table class="table table-striped">
                     <thead>
                        <tr>
                           <th>Type</th>
                           <th>Purpose</th>
                           <th>Activity</th>
                           <th>Status</th>
                           <th>Scheduled Date</th>
                           <th>Due Date</th>
                        </tr>
                     </thead>
                     <tbody>
                        <tr>
                           <td>Task</td>
                           <td>Phone Call</td>
                           <td>Test Log call</td>
                           <td>Completed</td>
                           <td>2016-07-27 07:39:00.0</td>
                           <td>2016-07-27 07:39:00.0</td>
                        </tr>
                        <tr>
                           <td>Task</td>
                           <td>Phone Call</td>
                           <td>Test Log call</td>
                           <td>Completed</td>
                           <td>2016-07-27 07:39:00.0</td>
                           <td>2016-07-27 07:39:00.0</td>
                        </tr>
                     </tbody>
                  </table>
               </div>
            </div>
         </div>
      </div>-->
      
      <#-- Call Status History -->
      <div class="panel panel-default">
        <div class="panel-heading" role="tab" id="headingTwo">
          <h4 class="panel-title">
            <a class="panel-collapse collapse show" role="button" data-toggle="collapse" data-parent="#accordionMenu" href="#callHistory" aria-expanded="false" aria-controls="headingTwo">
            Call Status History
            </a>
          </h4>
       </div>
       <div id="callHistory" class="panel-collapse collapse show" data-parent="#accordionMenu" aria-labelledby="CallHistory">
         <div class="panel-body">
           ${screens.render("component://crm/webapp/widget/crm/screens/common/CommonScreens.xml#callHistory")}
         </div>
       </div>
     </div>
  
      <#-- Cross Reference -->
     <#--  <#assign partyId = requestParameters.partyId?if_exists>
      <div class="panel panel-default">
         <div class="panel-heading" role="tab" id="headingTwo">
            <h4 class="panel-title">
               <a class="collapsed" role="button" data-toggle="collapse" data-parent="#accordionMenu" href="#crossRef" aria-expanded="false" aria-controls="headingTwo">
               Cross References
               </a>
            </h4>
         </div>
         <div id="crossRef" class="panel-collapse collapse" role="tabpanel" aria-labelledby="PendingActivities" data-parent="#accordionMenu">
            <div class="panel-body">
               <div class="form-group row">
                   <div class="col-md-6 col-sm-6 form-horizontal">
                      <#if crossReferenceList?exists && crossReferenceList?has_content>
                          <#list crossReferenceList as crossReference>
                          <form method="post" action="<@ofbizUrl>createCustomCrossReference</@ofbizUrl>" name="updateCustomField_${crossReference_index?if_exists}" id="updateCustomField_${crossReference_index?if_exists}" class="form-horizontal" novalidate="novalidate" data-toggle="validator" onsubmit="return validate('${crossReference_index?if_exists}')">
                              <#assign customeFieldValues = delegator.findOne("PartyAttribute", {"partyId" : partyId?if_exists, "attrName":crossReference.crossReferenceId?if_exists}, true)?if_exists>
                              <#assign enableLoy = delegator.findOne("PretailLoyaltyGlobalParameters", {"parameterId" : "ENABLE_LOYALTY_PROGRAM"}, true)?if_exists>
                              <input type="hidden" name="enbLoy" value="${enableLoy.value?if_exists}"/>
                              <input type="hidden" name="partyId" value="${partyId?if_exists}"/>
                              <input type="hidden" name="crossReferenceId" value="${crossReference.crossReferenceId?if_exists}"/>
                              <div class="form-group row">
                                  <label class="col-sm-4 col-form-label">${crossReference.crossReferenceName?if_exists}</label>
                                  <div class="col-sm-7">
                                      <div class="input-group">
                                          <#if customeFieldValues?exists && customeFieldValues?has_content>
                                              <input type="text" class="form-control input-sm" id="referenceValue_${crossReference_index?if_exists}" name="referenceValue" value="${customeFieldValues.attrValue?if_exists}" required>
                                          <#else>
                                              <input type="text" class="form-control input-sm" id="referenceValue_${crossReference_index?if_exists}" name="referenceValue" value="">
                                          </#if>
                                          <a class="input-group-addon" href="javascript:validate('${crossReference_index?if_exists}','${crossReference.crossReferenceId?if_exists}')"><span class="fa fa-edit" data-toggle="modal" data-target="" alt="Edit" title="Save"></span></a>
                                          <div class="help-block with-errors"></div>
                                      </div>
                                  </div>
                              </div>
                          </form>
                          </#list>
                      </#if>
                  </div>
               </div>
            </div>
         </div>
      </div>-->
      <#-- end -->
      <#-- <#include "component://crm/webapp/crm/common/bookMark.ftl"/>-->

      <#--
      <div class="panel panel-default">
         <div class="panel-heading" role="tab" id="headingTwo">
            <h4 class="panel-title">
               <a class="collapsed" role="button" data-toggle="collapse" data-parent="#accordionMenu" href="#TeamMembers" aria-expanded="false" aria-controls="headingTwo">
               Team Members Assigned To This Account
               </a>
            </h4>
         </div>
         <div id="TeamMembers" class="panel-collapse collapse" data-parent="#accordionMenu" aria-labelledby="PendingActivities">
            <div class="panel-body">
               <div class="table-responsive">
                  <table class="table table-striped">
                     <thead>
                        <tr>
                           <th>Name</th>
                           <th>Role - Update </th>
                           <th>Remove</th>
                        </tr>
                     </thead>
                     <tbody>
                        <tr>
                           <td colspan="3">No records to display</td>
                        </tr>
                     </tbody>
                  </table>
               </div>
               <div class="row padding-r">
                  <div class="col-md-5 col-sm-5">
                     <div class="form-group row">
                        <label for="inputEmail3" class="col-sm-5 col-form-label">New Team Member </label>
                        <div class="col-sm-7">
                           <div class="input-group">
                              <input type="text" class="form-control input-sm" placeholder="New Team Member	">
                              <span class="input-group-addon">
                              <span class="glyphicon glyphicon-list-alt
                                 " data-toggle="modal" data-target="#myModal">
                              </span>
                              </span>
                           </div>
                        </div>
                     </div>
                     <div class="form-group row">
                        <label for="inputEmail3" class="col-sm-5 col-form-label">Role</label>
                        <div class="col-sm-7">
                           <select class="ui dropdown form-control input-sm" data-live-search="true">
                              <option value="CSR">Customer Service Rep</option>
                              <option value="SALES_MANAGER">Team Leader</option>
                              <option value="SALES_REP">Full Team Member</option>
                              <option value="SALES_REP_LIMITED">View-Only Team Member</option>
                              <option value="TM_SALES_MANAGER">HR Team Manager</option>
                              <option value="TM_SALES_MEMBER">HR Team Member </option>
                           </select>
                        </div>
                     </div>
                  </div>
               </div>
               <div class="col-md-12 col-sm-12">
                  <div class="form-group row">
                     <div class="offset-sm-2 col-sm-5">
                        <button type="reset" class="btn btn-sm btn-primary mt">Add</button>
                     </div>
                  </div>
               </div>
            </div>
         </div>
      </div>
  
</form>
-->
 </div>
<script type="text/javascript">

function validate(index,crossReferenceId)
{
    var crossReferenceIds=document.getElementsByName("Loyalty ID");
    var enbLoy=document.getElementsByName("enbLoy")[0].value;
    var nodeArray = [];
    var referenceValue = document.getElementById("referenceValue_"+index).value;
    // for (var i = 0; i < crossReferenceIds.length; ++i) {
    //  nodeArray[i] = crossReferenceIds[i].value;
    if(crossReferenceId =="LOYALTY_ID" && referenceValue!="" && enbLoy!="Yes" )
    {
      //document.getElementById("crossRefError_"+index).innerHTML="Loyalty cannot be enabled for this customer";
      return false;
    }
    //}
    if(referenceValue == "")
    {
        //document.getElementById("crossRefError_"+index).innerHTML="Please Enter the value";
        return false;
    }
    else{
        //document.getElementById("crossRefError_"+index).innerHTML="";
        document.getElementById('updateCustomField_'+index).submit();
    }
    //alert("referenceValue-->"+referenceValue);
}
</script>

