<#assign contextPath = request.getContextPath()/>
<#assign partyId= request.getParameter("partyId")! />
<#if !showCampaignTabs?has_content || showCampaignTabs?if_exists=="N">
	<div class="pt-2 align-lists">
	    <form method="post" action="" id="findMarketingCampaigns" name="findMarketingCampaigns" novalidate="novalidate" data-toggle="validator">
			<input type="hidden" name="partyId" value="${partyId!}">
			<input type="hidden" name="isCampaignForParty" value="Y">
		</form>
	</div>
<#else>
	<#include "component://common-portal/webapp/common-portal/campaign/tab_menu.ftl"/>
	<div class="tab-content" id="tab-content">
	    <div id="cc-phone" class="tab-pane fade active show">
	        ${screens.render("component://common-portal/widget/campaign/CampaignScreens.xml#ListPhoneCampaign")}
	    </div>
		<div id="cc-email" class="tab-pane fade">
	        ${screens.render("component://common-portal/widget/campaign/CampaignScreens.xml#ListEmailCampaign")}
		</div>
	    <div id="cc-sms" class="tab-pane fade">
	        ${screens.render("component://common-portal/widget/campaign/CampaignScreens.xml#ListSmsCampaign")}
	    </div>
	    <div id="cc-postal" class="tab-pane fade">
	        ${screens.render("component://common-portal/widget/campaign/CampaignScreens.xml#ListPostalCampaign")}
	    </div>
	</div>
</#if>
