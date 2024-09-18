<#include "component://bootstrap/lib/ofbizFormMacros.ftl"/>

<#assign helpUrl = Static["org.groupfio.common.portal.util.DataHelper"].getHelpUrl(delegator, request.getRequestURI(), "listQuotes") />
<#assign rightContent='<button id="refresh-quote-btn" type="button" class="btn btn-xs btn-primary m5"><i class="fa fa-refresh" aria-hidden="true"></i></button>' />
<#assign quotePartyId = mainAssocPartyId!>
<#if !quotePartyId?has_content>
	<#assign quotePartyId = partyId!>
</#if>
<#if isEnableIUCInt?has_content && isEnableIUCInt=="Y">
	<#assign rightContent= rightContent + '<a title="Create" href="${iucUrl!}sales/control/EditQuote?oppoId=${salesOpportunityId!}&partyId=${quotePartyId!}&token=${token!}" target="_blank" class="btn btn-primary btn-xs ml-2"> <i class="fa fa-plus" aria-hidden="true"></i> Create </a>' />
</#if>

<div class="row">
	<div class="col-lg-12 col-md-12 col-sm-12">
		<@AgGrid
			gridheadertitle="Quotes"
			gridheaderid="quote-grid-action-container"
			savePrefBtn=true
			clearFilterBtn=true
			exportBtn=true
			insertBtn=false
			updateBtn=false
			removeBtn=false
			helpBtn=true
			helpUrl=helpUrl!
			headerextra=rightContent!
			refreshPrefBtnId="quote-refresh-pref-btn"
			savePrefBtnId="quote-save-pref-btn"
			subFltrClearId="quote-sub-filter-clear-btn"
			clearFilterBtnId="quote-clear-filter-btn"
			exportBtnId="quote-export-btn"
			userid="${userLogin.userLoginId}"
			shownotifications="true"
			instanceid="QUOTE_LIST"
			autosizeallcol="true"
			debug="false"
			/>
		<script type="text/javascript" src="/common-portal-resource/js/ag-grid/quote/find-quote.js"></script>
	</div>
</div>