<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<div class="row">
    <div id="main" role="main">
    	<@sectionFrameHeader title="Update Contact" />
        <div class="col-md-12 col-lg-12 col-sm-12 ">
        <form method="post" action="updateContact" id="updateContactForm" class="form-horizontal" name="updateContactForm" novalidate="novalidate" data-toggle="validator">
          <input type="hidden" value="${partySummary?if_exists.partyId?if_exists}" name ="partyId">
		  <div class="row padding-r">
          <div class="col-md-6 col-sm-6">
            <@inputRow 
			         id="firstName"
		             label=uiLabelMap.firstName
			         placeholder=uiLabelMap.firstName
			         value="${partySummary?if_exists.firstName?if_exists}"
			         required=true
			         maxlength="100"
			         />
            <@dropdownCell 
				id = "personalTitle"
				label = uiLabelMap.salutation
				options = salutationList
				value = "${partySummary?if_exists.personalTitle?if_exists}"
				allowEmpty=true
				dataLiveSearch = true
				/>
            <@inputRow 
		         id="departmentName"
	             label=uiLabelMap.department
		         placeholder=uiLabelMap.department
		         value="${partySummary?if_exists.departmentName?if_exists}"
		         />
             <#assign classifications = delegator.findByAnd("CustomFieldPartyClassification",{"groupId","GENDER","partyId","${partySummary?if_exists.partyId?if_exists}"},[], false)?if_exists/>
             <#if classifications?exists && classifications?has_content>
             	<#assign gender = classifications.get(0).customFieldId?if_exists />
             </#if>
             <@dropdownCell 
				id = "gender"
				label = uiLabelMap.gender
				options = genderList
				value = gender?if_exists
				allowEmpty=true
				dataLiveSearch = true
				/>
			<@inputArea 
		         id="description"
	             label=uiLabelMap.description
		         placeholder=uiLabelMap.description
		         value="${partySummary?if_exists.description?if_exists}"
		         rows="3"
		         />
          </div>
          <div class="col-md-6 col-sm-6">
           <@inputRow
			         id="lastName"
		             label=uiLabelMap.lastName
			         placeholder=uiLabelMap.lastName
			         value="${partySummary?if_exists.lastName?if_exists}"
			         required=false
			         maxlength="100"
			         />
			 <@inputDate
			         id="birthDate"
		             label=uiLabelMap.birthDate
			         placeholder="YYYY-MM-DD"
			         value="${partySummary?if_exists.birthDate?if_exists}"
			         />  
			 <@inputArea 
		         id="importantNote"
	             label=uiLabelMap.note
		         placeholder=uiLabelMap.note
		         value="${partySummary?if_exists.importantNote?if_exists}"
		         rows="3"
		         />                                 
          	</div>
          </div>		  
            <div class="col-md-12 col-sm-12">
            <div class="form-group row row">
              <div class="offset-sm-2 col-sm-9 pl-0">
              <@cancel onclick="viewContact?partyId=${partySummary?if_exists.partyId?if_exists}" label="Back" />
              <@submit label="Update"/>
              </div>
            </div>
          </div>
        </form>
      </div>
    </div>
    </div>
    
    <!-- Marketing Campaign -->
      <div class="panel panel-default">
        <div class="panel-heading" role="tab" id="headingTwo">
          <h4 class="panel-title">
            <a class="collapsed" role="button" data-toggle="collapse" data-parent="#accordionMenu" href="#mktCampaigns" aria-expanded="false" aria-controls="headingTwo">
            Marketing Campaigns
            </a>
          </h4>
        </div>
        <div id="mktCampaigns" class="panel-collapse collapse" role="tabpanel" aria-labelledby="">
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
                  <#assign j=0/>
                  <#list marketingCampaigns as campaign>
                  <tr>
                    <td><a href="viewMarketingCampaign?marketingCampaignId=${campaign.marketingCampaignId?if_exists}">${campaign.campaignName?if_exists}</a></td>
                    <td>
                      <form method="post" action="removeContactMarketingCampaign" name="removeMktCampaign_${j}" id="removeMktCampaign_${j}" >
                        <@inputHidden id="partyId" value="${parameters.partyId?if_exists}"/>
                        <@inputHidden id="marketingCampaignId" value="${campaign.marketingCampaignId?if_exists}"/>
                      </form>
                      <a href="javascript:document.removeMktCampaign_${j}.submit();" ><span class="glyphicon glyphicon-remove btn btn-xs btn-danger"></span></a>
                    </td>
                  </tr>
                  <#assign j = j+1>
                  </#list>
                </tbody>
              </table>
            </div>
            </#if>
            <form name="addContactMarketingCampaign" method="post" action="addContactMarketingCampaign">
              <div class="row padding-r">
                <div class="col-md-6 col-sm-6">
                  <div class="form-group row has-error">
                    <label class="col-sm-4 col-form-label">New Marketing Campaign*</label>
                    <div class="col-sm-7">
                      <input type="hidden" name="partyId" id="partyId" value="${parameters.partyId!}"/>
                     <#assign marketingCampainList = delegator.findByAnd("MarketingCampaign", Static["org.ofbiz.base.util.UtilMisc"].toMap("statusId", "MKTG_CAMP_INPROGRESS"), Static["org.ofbiz.base.util.UtilMisc"].toList("campaignName ASC"),false) />
                      <select class="ui dropdown search form-control input-sm" name="marketingCampaignId" id="marketingCampaignId" >
                        <option value="" disabled selected>Select Campaign</option>
                        <#if marketingCampainList?has_content>
                        <#list marketingCampainList as marketingCampain>
                        <option value="${marketingCampain.marketingCampaignId}">${marketingCampain.campaignName}</option>
                        </#list>
                        </#if>
                      </select>
                      <div class="help-block with-errors"></div>
                    </div>
                  </div>
                  <div class="form-group row">
                    <div class="offset-sm-4 col-sm-9">
                      <@submit label="${uiLabelMap.CommonAdd}"/>
                    </div>
                  </div>
                </div>
            </form>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</div>
   