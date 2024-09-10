<#include "component://homeapps/webapp/homeapps/lib/ofbizFormMacros.ftl"/>
<#include "component://crm/webapp/crm/account/accountModels.ftl">
    <@sectionHeader title="${uiLabelMap.convertLead!}" />
    <#assign partySummaryDetailsView = (delegator.findOne("PartySummaryDetailsView", {"partyId" : "${parameters.partyId?if_exists}"}, false))?if_exists/>
    <#if partySummaryDetailsView?has_content>
    <form method="post" action="convertLead" id="convertLead" class="form-horizontal" name="convertLead" novalidate="novalidate" data-toggle="validator">
      <input type="hidden" name="leadPartyId" value="${partySummaryDetailsView.partyId?if_exists}">
      <div class="row padding-r">
        <div class="col-md-6 col-sm-6">
          <div class="form-group row">
            <label for="${uiLabelMap.CommonLead}" class="col-sm-4 col-form-label">${uiLabelMap.CommonLead}</label>
            <div class="col-sm-7">
              <label class="col-sm-12 col-form-label fw">
                ${partySummaryDetailsView.firstName?if_exists}
                ${partySummaryDetailsView.lastName?if_exists}
                (${partySummaryDetailsView.partyId?if_exists})
              </label>
            </div>
          </div>
          <@generalInputModal 
          id="accountPartyId"
          name="accountPartyId"
          label=uiLabelMap.commonAccount
          placeholder=uiLabelMap.commonAccount
          modalName="parentAccountModal"
          required=false
          />
          <div class="form-group row">
            <label for="" class="col-sm-4 col-form-label"></label>
            <div class="col-sm-7">
              <label class="col-sm-12 col-form-label fw">
                <#-- ${uiLabelMap.CrmLeadLeaveBlankToCreateNewAccount} "${partySummaryDetailsView.companyName}" -->
                <span class="with-errors">Please key in the MAOS Id</span>
              </label>
            </div>
          </div>
          <@fromActions showCancelBtn=true cancelUrl="viewLead?partyId=${partySummaryDetailsView.partyId}" offsetSize="4" showSubmitBtn=false/>
        </div>
      </div>
    </form>
    </#if>
<script>
$('#parentAccountModal').on('click', '.parentSet', function(){
    var value = $(this).children("span").attr("value");
    $('#accountPartyId').val(value);
    $('#parentAccountModal').modal('hide');
});
</script>