<#include "component://homeapps/webapp/homeapps/lib/ofbizFormMacros.ftl"/>
<#include "component://crm/webapp/crm/account/accountModels.ftl">
      <#if request.getRequestURI().contains("updateLeadForm") && partySummary?exists && partySummary.partyId?has_content>
        <@sectionHeader title="${uiLabelMap.editLead!}" />
      <#else>
        <@sectionHeader title="${uiLabelMap.createLead!}" />
      </#if>
      <#if request.getRequestURI().contains("updateLeadForm") && partySummary?exists && partySummary.partyId?has_content>
        <form method="post" action="updateLead" id="leadForm" class="form-horizontal" name="leadForm" novalidate="novalidate" data-toggle="validator" onsubmit="return clearPostalCodeErr(this);">
        <input type="hidden" value="${partySummary.partyId?if_exists}" name="partyId">
      <#else>
        <form method="post" action="createLead" id="leadForm" class="form-horizontal" name="leadForm" novalidate="novalidate" data-toggle="validator" onsubmit="return clearPostalCodeErr(this);">
      </#if>
         <div class="row padding-r">
            <div class="col-md-6 col-sm-6">
               <@generalInput 
               id="companyName"
               name="companyName"
               label=uiLabelMap.companyName
               placeholder=uiLabelMap.companyName
               value="${partySummary?if_exists.companyName?if_exists}"
               required=true
               />
               <@generalInput 
               id="firstName"
               name="firstName"
               label=uiLabelMap.firstName
               placeholder=uiLabelMap.firstName
               value="${partySummary?if_exists.firstName?if_exists}"
               required=true
               />
               <@generalInput 
               id="personalTitle"
               name="personalTitle"
               label=uiLabelMap.salutation
               placeholder=uiLabelMap.salutation
               value="${partySummary?if_exists.personalTitle?if_exists}"
               required=false
               />
               <@generalInput 
               id="generalProfTitle"
               name="generalProfTitle"
               label=uiLabelMap.title
               placeholder=uiLabelMap.title
               value="${partySummary?if_exists.generalProfTitle?if_exists}"
               required=false
               />
               <@generalInput 
               id="annualRevenue"
               name="annualRevenue"
               label=uiLabelMap.annualRevenue
               placeholder=uiLabelMap.annualRevenue
               value="${partySummary?if_exists.annualRevenue?if_exists}"
               required=false
               />
               <@dropdownInput 
               id="industryEnumId"
               name="industryEnumId"
               label=uiLabelMap.DBSIC
               options=industryEnumList
               required=false
               value="${partySummary?if_exists.industryEnumId?if_exists}"
               allowEmpty=true
               dataLiveSearch=true
               />
               <#--<@generalInput 
               id="sicCode"
               name="sicCode"
               label=uiLabelMap.sicCode
               placeholder=uiLabelMap.sicCode
               value="${partySummary?if_exists.sicCode?if_exists}"
               required=false
               />-->
               <#if request.getRequestURI().contains("updateLeadForm") && partySummary?exists && partySummary.partyId?has_content>
               <div class="form-group row">
               <label for="${uiLabelMap.callBackDate}" class="col-sm-4 col-form-label">${uiLabelMap.callBackDate}</label>
               <div class="col-sm-7">
                 <div class="input-group date" id="callBackDate_datetimepicker">
                   <input type='text' class="form-control input-sm" placeholder="YYYY-MM-DD" value="<#if partySummary?if_exists.callBackDate?has_content>${partySummary.callBackDate?if_exists?string["yyyy-MM-dd"]}</#if>" data-date-format="YYYY-MM-DD" id="callBackDate" name="callBackDate"/>
                   <span class="input-group-addon"><span class="glyphicon glyphicon-calendar"></span></span>
                 </div>
               </div>
               </div>
               <#else>
               <@dropdownInput 
               id="dataSourceId"
               name="dataSourceId"
               label=uiLabelMap.sourceOfLeads
               options=dataSourceList
               required=false
               allowEmpty=true
               dataLiveSearch=true
               />
               </#if>
               <#--<@generalInput 
               id="tickerSymbol"
               name="tickerSymbol"
               label=uiLabelMap.tickerSymbol
               placeholder=uiLabelMap.tickerSymbol
               value="${partySummary?if_exists.tickerSymbol?if_exists}"
               required=false
               />-->
            </div>
            <div class="col-md-6 col-sm-6">
               <@generalInputModal 
               id="parentPartyId"
               label=uiLabelMap.parentAccount
               placeholder=uiLabelMap.parentAccount
               value="${partySummary?if_exists.parentPartyId?if_exists}"
               modalName="parentAccountModal"
               required=false
               />
               <@generalInput 
               id="lastName"
               name="lastName"
               label=uiLabelMap.lastName
               placeholder=uiLabelMap.lastName
               value="${partySummary?if_exists.lastName?if_exists}"
               required=true
               />
               <div class="form-group row">
               <label for="${uiLabelMap.birthDate}" class="col-sm-4 col-form-label">${uiLabelMap.birthDate}</label>
               <div class="col-sm-7">
                 <div class="input-group date" id="birthDate_datetimepicker">
                   <input type='text' class="form-control input-sm" placeholder="YYYY-MM-DD" value="<#if partySummary?if_exists.birthDate?has_content>${partySummary.birthDate?if_exists?string["yyyy-MM-dd"]}</#if>" data-date-format="YYYY-MM-DD" id="birthDate" name="birthDate"/>
                   <span class="input-group-addon"><span class="glyphicon glyphicon-calendar"></span></span>
                 </div>
               </div>
               </div>
               <@generalInput 
               id="departmentName"
               name="departmentName"
               label=uiLabelMap.department
               placeholder=uiLabelMap.department
               value="${partySummary?if_exists.departmentName?if_exists}"
               required=false
               />
               <#--<div class="form-group row">
                  <label for="${uiLabelMap.preferredCurrency}" class="col-sm-4 col-form-label">${uiLabelMap.preferredCurrency}</label>
                  <div class="col-sm-7">
                     <@inputCurrency  
                     name="currencyUomId"
                     value="${partySummary?if_exists.preferredCurrencyUomId?if_exists}"
                     defaultCourrencyUom=true
                     dataLiveSearch=true
                     required=false
                     />
                  </div>
               </div>-->
               <@dropdownInput 
               id="ownershipEnumId"
               name="ownershipEnumId"
               label=uiLabelMap.constitution
               options=ownershipList
               required=false
               value="${partySummary?if_exists.ownershipEnumId?if_exists}"
               allowEmpty=true
               dataLiveSearch=true
               />
               <@generalInput 
               id="numberEmployees"
               name="numberEmployees"
               label=uiLabelMap.numberOfEmployees
               placeholder=uiLabelMap.numberOfEmployees
               value="${partySummary?if_exists.numberEmployees?if_exists}"
               required=false
               />
            </div>
         </div>
         <#if request.getRequestURI().contains("updateLeadForm") && partySummary?exists && partySummary.partyId?has_content>
         <div class="col-md-12 col-sm-12">
            <@fromActions submitLabel="Update" showCancelBtn=true cancelUrl="viewLead?partyId=${partySummary.partyId}" offsetSize="2"/>
         </div>
         <#else>
         <@sectionTitle title="${uiLabelMap.contactInformation!}" />
         <div class="row padding-r">
            <div class="col-md-6 col-sm-6">
               <@generalInput 
               id="primaryPhoneNumber"
               name="primaryPhoneNumber"
               label=uiLabelMap.phoneNumber
               placeholder="(845)555-1212"
               value=""
               required=false
               />
               <@generalInput 
               id="primaryEmail"
               name="primaryEmail"
               label=uiLabelMap.email
               placeholder="example@company.com"
               value=""
               required=false
               />
            </div>
            <div class="col-md-6 col-sm-6">
               <@generalInput 
               id="primaryPhoneAskForName"
               name="primaryPhoneAskForName"
               label=uiLabelMap.personToAskFor
               placeholder="Person Name"
               value=""
               required=false
               />
               <@generalInput 
               id="primaryWebUrl"
               name="primaryWebUrl"
               label=uiLabelMap.webURL
               placeholder="http://domain.com"
               value=""
               required=false
               />
            </div>
         </div>
         <@sectionTitle title="${uiLabelMap.primaryAddress!}" />
         <div class="row padding-r">
            <div class="col-md-6 col-sm-6">
               <@generalInput 
               id="generalToName"
               name="generalToName"
               label=uiLabelMap.toName
               placeholder=uiLabelMap.toName
               value=""
               required=false
               />
               <@generalInput 
               id="generalAddress1"
               name="generalAddress1"
               label=uiLabelMap.address1
               placeholder=uiLabelMap.address1
               value=""
               required=false
               />
               <@generalInput 
               id="generalCity"
               name="generalCity"
               label=uiLabelMap.city
               placeholder=uiLabelMap.city
               value=""
               required=false
               />
               <@generalInput 
               id="generalPostalCode"
               name="generalPostalCode"
               label=uiLabelMap.postalCode
               placeholder=uiLabelMap.postalCode
               value=""
               required=false
               />
               <@generalInput 
               id="generalPostalCodeExt"
               name="generalPostalCodeExt"
               label=uiLabelMap.postalCodeExt
               placeholder=uiLabelMap.postalCodeExt
               value=""
               required=false
               />
            </div>
            <div class="col-md-6 col-sm-6">
               <@generalInput 
               id="generalAttnName"
               name="generalAttnName"
               label=uiLabelMap.attentionName
               placeholder=uiLabelMap.attentionName
               value=""
               required=false
               />
               <@generalInput 
               id="generalAddress2"
               name="generalAddress2"
               label=uiLabelMap.address2
               placeholder=uiLabelMap.address2
               value=""
               required=false
               />
               <div class="form-group row">
                  <label for="${uiLabelMap.country}" class="col-sm-4 col-form-label">${uiLabelMap.country}</label>
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
                  <label for="${uiLabelMap.state}" class="col-sm-4 col-form-label">${uiLabelMap.state}</label>
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
            <@fromActions showCancelBtn=false showClearBtn=true offsetSize="2"/>
         </div>
         </#if>
      </form>
      <#if request.getRequestURI().contains("updateLeadForm") && partySummary?exists && partySummary.partyId?has_content>
      <div class="panel-group" id="accordionMenu" role="tablist" aria-multiselectable="true">
         <div class="panel panel-default">
            <div class="panel-heading" role="tab" id="headingTwo">
               <h4 class="panel-title">
                  <a class="collapsed" role="button" data-toggle="collapse" data-parent="#accordionMenu" href="#sources" aria-expanded="false" aria-controls="headingTwo">
                  ${uiLabelMap.sourcesList}
                  </a>
               </h4>
            </div>
            <div id="sources" class="panel-collapse collapse show" data-parent="#accordionMenu" aria-labelledby="">
               <div class="panel-body">
                  <form name="addLeadDataSource" method="post" action="addLeadDataSource">
                     <div class="row padding-r">
                        <div class="col-md-6 col-sm-6">
                           <input type="hidden" name="partyId" id="partyId" value="${parameters.partyId!}"/>
                           <@dropdownInput 
                           id="dataSourceId"
                           name="dataSourceId"
                           label=uiLabelMap.sourceOfLeads
                           options=dataSourceList
                           required=true
                           allowEmpty=true
                           dataLiveSearch=true
                           />
                           <div class="form-group row">
                              <div class="offset-sm-4 col-sm-9">
                                 <input type="submit" class="btn btn-sm btn-primary mb-2" value="${uiLabelMap.CommonAdd}"/>
                              </div>
                           </div>
                        </div>
                  </form>
                  <#if dataSources?exists && dataSources?has_content>
                  <div class="table-responsive">
                  <table class="table table-striped" id="dataSources">
                  <thead>
                  <tr>
                  <th>${uiLabelMap.sourceOfLeads!}</th>
                  <th>${uiLabelMap.fromDate!}</th>
                  <th>${uiLabelMap.removeLabel!}</th>
                  </tr>
                  </thead>
                  <tbody>
                  <#assign i=0/>
                  <#list dataSources as dataSource>
                  <tr>
                  <#assign dataSourceGv = delegator.findOne("DataSource",{"dataSourceId",dataSource.dataSourceId?if_exists}, false)?if_exists/>
                  <td>${dataSourceGv.description}</td>
                  <td>${dataSource.fromDate}</td>
                  <td>
                  <form method="post" action="removeLeadDataSource" name="removeDataSource_${i}" id="removeDataSource_${i}" >
                  <input type="hidden" name="partyId" id="partyId" value="${parameters.partyId?if_exists}"/>
                  <input type="hidden" name="dataSourceId" id="dataSourceId" value="${dataSource.dataSourceId?if_exists}"/>
                  <input type="hidden" name="fromDate" id="fromDate" value="${dataSource.fromDate?if_exists}"/>
                  </form>
                  <a href="javascript:document.removeDataSource_${i}.submit();" ><span class="glyphicon glyphicon-remove btn btn-xs btn-danger"></span></a>
                  </td>
                  </tr>
                  <#assign i=i+1/>
                  </#list>
                  </tbody>
                  </table>
                  </div>
                  </#if>
                  </div>
               </div>
            </div>
         </div>
      </div>
      </#if>
<script>
   $(document).ready (function(){
     $('#birthDate_datetimepicker').datetimepicker({
       useCurrent: false
     });
     $('#callBackDate_datetimepicker').datetimepicker({
      useCurrent: false
     });
     $("#success-alert").hide();
     $("#myWish").click(function showAlert() {
     $("#success-alert").fadeTo(2000, 500).slideUp(500, function(){
       $("#success-alert").slideUp(500);
     });
     });
    });
    
    /*$('#parentAccountModal').on('click', '.parentSet', function(){
     var value = $(this).children("span").attr("value");
     console.log(value);
     $('#parentPartyId').val(value);
     $('#parentAccountModal').modal('hide');
    });*/
   
   $(document).ready(function() {
    $('#dataSources').DataTable({
      "pageLength": 3
    });
   });
   $( "#generalPostalCode" ).keyup(function() {
       clearPostalCodeErr();
   });
   $("#generalStateProvinceGeoId, #generalCountryGeoId").change(function(){
       clearPostalCodeErr();
   });
   function clearPostalCodeErr(){
      var postalCodeErr = $("#generalPostalCode_error").val();
      var postalCode = $("#generalPostalCode").val();
      $("#postalCodeErr").html("");
      if(postalCode !="" || postalCode !=null){
          var country = $('select#generalCountryGeoId option:selected').val();
          var state = $('select#generalStateProvinceGeoId option:selected').val();
          if(country != null && country != "" && state != null && state != "" ){
           if(country=="CAN" || country=="USA"){
           if(country=="CAN"){
            if(postalCode.length > 7 || postalCode.length < 7 ){
              $("#generalPostalCode_error").append('<ul class="list-unstyled"><li>Postal Code Length Should be 7</li></ul>');
              return false;
            }else{
              $("#generalPostalCode_error").html("");
            }
           }
           else if(country=="USA"){
             if(postalCode.length > 5 || postalCode.length < 5){
               var postalCodeErr = $( "div#generalPostalCode_error" ).text();
               if(postalCodeErr != "") {
               }else{
                 $("#generalPostalCode_error").append('<ul class="list-unstyled"><li>Postal Code Length Should be 5</li></ul>');
               }
               return false;
             }else{
              $("#generalPostalCode_error").html("");
            } 
           }
         }
         }
         if(postalCodeErr !="" || postalCodeErr !=null){
           $("#generalPostalCode_error").html("");
         }
      }
  }

</script>