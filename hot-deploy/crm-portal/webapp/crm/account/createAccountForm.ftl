<#include "component://homeapps/webapp/homeapps/lib/ofbizFormMacros.ftl"/>
      <div class="page-header border-b">
         <h1 class="float-left">${uiLabelMap.createAccount!}</h1>
      </div>
      <form method="post" action="<@ofbizUrl>storeAccountData</@ofbizUrl>" id="createAccountForm" class="form-horizontal" name="createAccountForm" novalidate="novalidate" data-toggle="validator" onsubmit="return clearPostalCodeErr(this);">
         <div class="row padding-r">
            <div class="col-md-6 col-sm-6">
               <div class="form-group row has-error">
                  <label class="col-sm-4 col-form-label">${uiLabelMap.accountName!}</label>
                  <div class="col-sm-7">
                     <input type="text" class="form-control input-sm" name="accountName" id="accountName" value="${partySummary?if_exists.groupName?if_exists}" placeholder="Account Name"  required>
                     <div class="help-block with-errors"></div>
                  </div>
               </div>
               <div class="form-group row">
                  <label class="col-sm-4 col-form-label">${uiLabelMap.localName!}</label>
                  <div class="col-sm-7">
                     <input type="text" class="form-control input-sm"  id="groupNameLocal" name="groupNameLocal" placeholder="Local Name" > 
                  </div>
               </div>
               <div class="form-group row">
                  <label  class="col-sm-4 col-form-label">${uiLabelMap.annualRevenue!}</label>
                  <div class="col-sm-7">
                     <input type="text" class="form-control input-sm" id="annualRevenue" name="annualRevenue" placeholder="Annual Revenue">
                  </div>
               </div>
               <div class="form-group row">
                  <label  class="col-sm-4 col-form-label">${uiLabelMap.industry!}</label>
                  <div class="col-sm-7">
                  <#assign enumerations = Static["org.fio.crm.util.UtilCommon"].getEnumerations("PARTY_INDUSTRY", delegator)/>
                     <select class="ui dropdown search form-control input-sm"  name="industryEnumId">
                        <option value="" disabled selected>Select Industry</option>
                        <#if enumerations?has_content>
                        <#list enumerations as enum>
                        <#if enum.disabled?has_content && enum.disabled="Y">
                        <#else>
                        <option value="${enum.enumId}"<#if partySummary?if_exists.industryEnumId?if_exists?if_exists==enum.enumId?if_exists>Selected</#if>>${enum.description}</option>   
                        </#if>
                        </#list>
                       </#if> 
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
                     <input type="text" class="form-control input-sm" id="sicCode" name="sicCode" placeholder="SIC Code">
                  </div>
               </div>-->
               <div class="form-group row">
                  <label  class="col-sm-4 col-form-label">${uiLabelMap.classifications!}</label>
                  <div class="col-sm-7">
                  <#assign classifications = delegator.findByAnd("PartyClassificationGroup",{"partyClassificationTypeId","CUST_CLASSIFICATION"},Static["org.ofbiz.base.util.UtilMisc"].toList("description"), false)?if_exists/>
                  <select name="partyClassificationGroupId" id="partyClassificationGroupId" class="ui dropdown search form-control input-sm"  >
	                <option value="" disabled selected>Select Classification</option>
	               <#if classifications?has_content>
				   	<#list classifications as classification>
				   		<option value="${classification.partyClassificationGroupId}">${classification.description}</option>   
					</#list>
					</#if>
	              </select>
                  </div>
               </div>
               <div class="form-group row">
                  <label  class="col-sm-4 col-form-label">${uiLabelMap.source!}</label>
                  <div class="col-sm-7">
                     <select id="dataSourceId" name="dataSourceId" class="ui dropdown search form-control input-sm" >
                        <option value="" disabled selected>Select Data Source</option>
                        <#if dataSourceList?has_content>
                          <#list dataSourceList as dataSource>
                            <option value="${dataSource.dataSourceId}">${dataSource.description}</option>
                          </#list>
                        </#if>
                     </select>
                  </div>
               </div>
               <div class="form-group row">
                  <label  class="col-sm-4 col-form-label">${uiLabelMap.initialTeam!}</label>
                  <div class="col-sm-7">
                     <select id="initialTeamPartyId" name="initialTeamPartyId" class="ui dropdown search form-control input-sm" >
                        <#if teamList?exists && teamList?has_content>
					      <#list teamList as team>
					         <option value="${team.partyId}">${team.groupName}</option>
					      </#list>
					    </#if>
                     </select>
                  </div>
               </div>
               <div class="form-group row">
                  <label  class="col-sm-4 col-form-label">${uiLabelMap.description!}</label>
                  <div class="col-sm-7">
                     <textarea name="description" rows="3" placeholder="Description" class="form-control" ></textarea>
                  </div>
               </div>
            </div>
            <div class="col-md-6 col-sm-6">
               <div class="form-group row">
                  <label  class="col-sm-4 col-form-label">${uiLabelMap.parentAccount!}</label>
                  <div class="col-sm-7">
                     <div class="input-group">
                        <input type="text" name="parentPartyId" id="parentPartyId" class="form-control input-sm" placeholder="Parent Account">
                        <span class="input-group-addon">
                        <span class="glyphicon glyphicon-list-alt
                           " data-toggle="modal" data-target="#parentAccountModal" id="findAccount">
                        </span>
                        </span>
                     </div>
                  </div>
               </div>
               <div class="form-group row">
                  <label  class="col-sm-4 col-form-label">${uiLabelMap.siteName!}</label>
                  <div class="col-sm-7">
                     <input type="text" class="form-control input-sm" id="officeSiteName" name="officeSiteName" placeholder="Site Name">
                  </div>
               </div>
               <div class="form-group row">
                  <label  class="col-sm-4 col-form-label">${uiLabelMap.preferredCurrency!}</label>
                  <div class="col-sm-7">
                     <#assign currencies = delegator.findByAnd("Uom",{"uomTypeId","CURRENCY_MEASURE"},Static["org.ofbiz.base.util.UtilMisc"].toList("abbreviation"), false)?if_exists/>
                     <#assign defaultCourrencyUomId = (Static["org.ofbiz.base.util.UtilProperties"].getPropertyValue("crm", "defaultCurrencyUomId"))?default("USD")/>
                     <select class="ui dropdown search form-control input-sm"  id="currencyUomId" name="currencyUomId">
                        <option value="" disabled selected>Select Currency</option>
                        <#list currencies as currency>
                        	<option value="${currency.uomId}">${currency.description}</option>
                        </#list>
                     </select>
                  </div>
               </div>
               <div class="form-group row">
                  <label  class="col-sm-4 col-form-label">${uiLabelMap.numberOfEmployees!}</label>
                  <div class="col-sm-7">
                     <input type="text" class="form-control input-sm" id="numberEmployees" name="numberEmployees" placeholder="Number Of Employees">
                  </div>
               </div>
               <div class="form-group row">
                  <label  class="col-sm-4 col-form-label">${uiLabelMap.tickerSymbol!}</label>
                  <div class="col-sm-7">
                     <input type="text" class="form-control input-sm" id="tickerSymbol" name="tickerSymbol" placeholder="Ticker Symbol">
                  </div>
               </div>
               <#assign marketingCampainList  = delegator.findByAnd("MarketingCampaign",Static["org.ofbiz.base.util.UtilMisc"].toMap("statusId","MKTG_CAMP_INPROGRESS"),marketingCampainList ,false)/>
               <div class="form-group row">
                  <label  class="col-sm-4 col-form-label">${uiLabelMap.marketingCampaign!}</label>
                  <div class="col-sm-7">
                     <select class="ui dropdown search form-control input-sm"  name="marketingCampaignId" id="marketingCampaignId" >
                        <option value="" disabled selected>Select Campaign</option>
                        <#if marketingCampainList?has_content>
                        <#list marketingCampainList as marketingCampain>
                        <option value="${marketingCampain.marketingCampaignId}">${marketingCampain.campaignName}</option>
                        </#list>
                        </#if>
                     </select>
                  </div>
               </div>
               <div class="form-group row">
                  <label  class="col-sm-4 col-form-label">${uiLabelMap.note!}</label>
                  <div class="col-sm-7">
                     <textarea id="importantNote" name="importantNote" rows="3" placeholder="Note" class="form-control" ></textarea>
                  </div>
               </div>
               <div class="form-group row">
                  <label  class="col-sm-4 col-form-label">${uiLabelMap.isTaxExempt!}</label>
                  <div class="col-sm-7">
                     <select name="isExempt" class="ui dropdown search form-control input-sm" >
                        <option value="N">No</option>
                        <option value="Y">Yes</option>
                     </select>
                  </div>
               </div>
               <div class="form-group row">
                  <label  class="col-sm-4 col-form-label">${uiLabelMap.taxExemptNumber!}</label>
                  <div class="col-sm-7">
                     <input type="text" class="form-control input-sm" name = "partyTaxId" placeholder="Tax Exempt Number">
                  </div>
               </div>
            </div>
         </div>
         <div class="clearfix"> </div>
         <div class="page-header">
            <h2 class="float-left">${uiLabelMap.contactInformation!}</h2>
         </div>
         <div class="row padding-r">
            <div class="col-md-6 col-sm-6">
               <@generalInput
	                id="primaryPhoneNumber"
					label=uiLabelMap.phoneNumber
					placeholder="(845)555-1212"
					value=""
					required=false	
					maxlength="13"
					inputType= "number"
				/>
               <div class="form-group row ">
                  <label  class="col-sm-4 col-form-label">${uiLabelMap.email!}</label>
                  <div class="col-sm-7">
                     <input type="email" class="form-control input-sm" id="primaryEmail" name="primaryEmail" placeholder="example@company.com">
                     <div class="help-block with-errors"></div>
                  </div>
               </div>
            </div>
            <div class="col-md-6 col-sm-6">
               <div class="form-group row">
                  <label  class="col-sm-4 col-form-label">${uiLabelMap.personToAskFor!}</label>
                  <div class="col-sm-7">
                     <input type="text" class="form-control input-sm" id="primaryPhoneAskForName" name="primaryPhoneAskForName" placeholder="Person Name">
                  </div>
               </div>
               <div class="form-group row">
                  <label  class="col-sm-4 col-form-label">${uiLabelMap.webURL!}</label>
                  <div class="col-sm-7">
                     <input type="text" class="form-control input-sm" id="primaryWebUrl" name="primaryWebUrl" placeholder="http://domain.com">
                  </div>
               </div>
            </div>
         </div>
         <div class="clearfix"> </div>
         <div class="page-header">
            <h2 class="float-left">${uiLabelMap.primaryAddress!}</h2>
         </div>
         <div class="row padding-r">
            <div class="col-md-6 col-sm-6">
               <div class="form-group row">
                  <label  class="col-sm-4 col-form-label">${uiLabelMap.toName!}</label>
                  <div class="col-sm-7">
                     <input type="text" class="form-control input-sm" id="generalToName" name="generalToName" placeholder="To Name">
                  </div>
               </div>
               <div class="form-group row ">
                  <label  class="col-sm-4 col-form-label">${uiLabelMap.address1!}</label>
                  <div class="col-sm-7">
                     <input type="text" class="form-control input-sm" id="generalAddress1" name="generalAddress1" placeholder="Address Line 1">
                     <div class="help-block with-errors"></div>
                  </div>
               </div>
               <div class="form-group row">
                  <label  class="col-sm-4 col-form-label">${uiLabelMap.city!}</label>
                  <div class="col-sm-7">
                     <input type="text" class="form-control input-sm" id="generalCity" name="generalCity" placeholder="City">
                     <div class="help-block with-errors"></div>
                  </div>
               </div>
               <div class="form-group row">
                  <label  class="col-sm-4 col-form-label">${uiLabelMap.postalCode!}</label>
                  <div class="col-sm-7">
                     <input type="text" class="form-control input-sm" id="generalPostalCode" name="generalPostalCode" placeholder="${uiLabelMap.postalCode!}">
                     <div class="help-block with-errors" id="postalCodeErr"></div>
                  </div>
               </div>
               <div class="form-group row">
                  <label  class="col-sm-4 col-form-label">${uiLabelMap.postalCodeExt!}</label>
                  <div class="col-sm-7">
                     <input type="text" class="form-control input-sm" id="generalPostalCodeExt" name="generalPostalCodeExt" placeholder="${uiLabelMap.postalCodeExt!}">
                  </div>
               </div>
            </div>
            <div class="col-md-6 col-sm-6">
               <div class="form-group row">
                  <label  class="col-sm-4 col-form-label">${uiLabelMap.attentionName!}</label>
                  <div class="col-sm-7">
                     <input type="text" class="form-control input-sm" id="generalAttnName" name="generalAttnName" placeholder="Attention Name	">
                  </div>
               </div>
               <div class="form-group row">
                  <label  class="col-sm-4 col-form-label">${uiLabelMap.address2!}</label>
                  <div class="col-sm-7">
                     <input type="text" class="form-control input-sm" id="generalAddress2" name="generalAddress2" placeholder="Address Line 2">
                  </div>
               </div>
               <div class="form-group row">
                  <label  class="col-sm-4 col-form-label">${uiLabelMap.country!}</label>
                  <div class="col-sm-7">
                     <@inputCountry  
                     name="generalCountryGeoId"
                     defaultCountry=true
                     dataLiveSearch=true
                     required=false
                     />
                  </div>
               </div>
               <div class="form-group row">
                  <label  class="col-sm-4 col-form-label">${uiLabelMap.state!}</label>
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
            <div class="form-group row">
               <div class="offset-sm-2 col-sm-9">
                 <button type="submit" class="btn btn-sm btn-primary mt-2">Submit </button>
                  <button type="reset" class="btn btn-sm btn-secondary mt-2">Clear</button>
               </div>
            </div>
         </div>
      </form>
	<#include "component://crm/webapp/crm/account/accountModels.ftl">
<script>
$( "#generalPostalCode" ).keyup(function() {
       clearPostalCodeErr();
   });
   $("#generalStateProvinceGeoId, #generalCountryGeoId").change(function(){
       clearPostalCodeErr();
   });
function clearPostalCodeErr(){
      var postalCodeErr = $("#postalCodeErr").val();
      var postalCode = $("#generalPostalCode").val();
      $("#postalCodeErr").html("");
      if(postalCode !=" " || postalCode !=null){
          var country = $('select#generalCountryGeoId option:selected').val();
          var state = $('select#generalStateProvinceGeoId option:selected').val();
          if(country != null && country != "" && state != null && state != "" ){
           if(country=="CAN" || country=="USA"){
           if(country=="CAN"){
            if(postalCode.length > 7 || postalCode.length < 7 ){
              $("#postalCodeErr").append('<ul class="list-unstyled"><li>Postal Code Length Should be 7</li></ul>');
              return false;
            }else{
              $("#postalCodeErr").html("");
            }
           }
           else if(country=="USA"){
             if(postalCode.length > 5 || postalCode.length < 5){
               var postalCodeErr = $("#postalCodeErr").val();
               if(postalCodeErr != "") {
               }else{
                 $("#postalCodeErr").append('<ul class="list-unstyled"><li>Postal Code Length Should be 5</li></ul>');
               }
               return false;
             }else{
              $("#postalCodeErr").html("");
            } 
           }
         }
         }
         if(postalCodeErr !="" || postalCodeErr !=null){
           $("#postalCodeErr").html("");
         }
      }
  }
</script>