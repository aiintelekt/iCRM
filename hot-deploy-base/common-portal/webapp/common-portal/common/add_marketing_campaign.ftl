<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
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
                      <td>${campaign.campaignName?if_exists}
                      	<#-- <a href="viewMarketingCampaign?marketingCampaignId="+ ${campaign.marketingCampaignId?if_exists} >${campaign.campaignName?if_exists}</a> -->
                      </td>
                      <td>
                      
                      <span class="glyphicon glyphicon-remove btn btn-xs btn-danger removeMktCampaign" data-marketingCampaignId="${campaign.marketingCampaignId?if_exists}"></span>
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
            <form name="addMarketingCampaign" id="addMarketingCampaign" method="post" action="addMarketingCampaign" class="row">
            	<input type="hidden" name="partyId" id="partyId" value="${parameters.partyId!}"/>
            	<input type="hidden" name="roleTypeId" value="${partyRoleTypeId!}"/>
            	
             <#assign marketingCampainList = delegator.findByAnd("MarketingCampaign", Static["org.ofbiz.base.util.UtilMisc"].toMap("statusId", "MKTG_CAMP_INPROGRESS"), Static["org.ofbiz.base.util.UtilMisc"].toList("campaignName ASC"),false) />
               <select class="ui dropdown search form-control input-sm"  name="marketingCampaignId" id="marketingCampaignId">
                  <option value="" disabled selected>Select Campaign</option>
                  <#if marketingCampainList?has_content>
                  <#list marketingCampainList as marketingCampain>
                    <option value="${marketingCampain.marketingCampaignId}">${marketingCampain.campaignName}</option>
                  </#list>
                  </#if>
               </select>
               <div class="help-block with-errors"></div>
               <@submit label="${uiLabelMap.CommonAdd}"/>
            </form>
            </div>
         </div>
       </div>
      </div>
    </div>
    </div>
 </div>

<form method="post" action="removeMarketingCampaign" id="removeMktCampaign" >
	<input type="hidden" name="partyId" value="${parameters.partyId?if_exists}"/>
	<input type="hidden" name="roleTypeId" value="${partyRoleTypeId!}"/>
	<input type="hidden" name="marketingCampaignId" value=""/>
</form> 
   
<script>     
$(document).ready(function() {

$('#addMarketingCampaign').validator().on('submit', function (e) {
	if (e.isDefaultPrevented()) {
    	// handle the invalid form...
  	} else {
  		var valid = true;
		if (!$("#marketingCampaignId").val()) {
			valid = false;
			showAlert("error", "Select marketing campaign");
		}  
  		if (!valid){
  			e.preventDefault();
  		}
  	}
});

$('.removeMktCampaign').on('click', function(){
    
	//alert( $(this).attr("data-marketingCampaignId") );    
	$('#removeMktCampaign input[name="marketingCampaignId"]').val( $(this).attr("data-marketingCampaignId") );
	
	$('#removeMktCampaign').submit();
                                                                                                    
});	
	
});
</script>
 
 
 
 