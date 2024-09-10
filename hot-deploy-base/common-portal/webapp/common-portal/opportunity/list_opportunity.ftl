<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>

<#assign helpUrl = Static["org.groupfio.common.portal.util.DataHelper"].getHelpUrl(delegator, request.getRequestURI(), "opportunities") />

<div class="row">
	<#assign partyId= request.getParameter("partyId")! />
<div class="col-lg-12 col-md-12 col-sm-12">
<div  style="width: 100%;" class="ag-theme-balham"></div>
<#if partySummary?has_content>
	<#if partySummary.statusId == "LEAD_QUALIFIED" >
	<#assign rightContent='<a title="Create" href="/opportunity-portal/control/createOpportunity?externalLoginKey=${externalLoginKey}&partyId=${partyId}" target="_blank" class="btn btn-primary btn-xs ml-2"> <i class="fa fa-plus" aria-hidden="true"></i> Create </a>' />   
	 <#else>
	 </#if> 
 </#if>
 <#if partySummary.statusId?has_content && partySummary.statusId?if_exists != "PARTY_DISABLED">
 <#if (domainEntityType! == "ACCOUNT" || domainEntityType! == "CUSTOMER") && domainEntityType! != "CONTACT">
 	<#assign rightContent='<a title="Create" href="/opportunity-portal/control/createOpportunity?externalLoginKey=${externalLoginKey}&partyId=${partyId}" target="_blank" class="btn btn-primary btn-xs ml-2"> <i class="fa fa-plus" aria-hidden="true"></i> Create </a>' />
 </#if>  
 </#if>
 <#assign defaultCurrencyUom = Static["org.fio.homeapps.util.DataUtil"].getGlobalValue(delegator, "DEFAULT_CURRENCY_UOM","USD")?if_exists>
 <#assign totalOppEstAmount= Static["org.ofbiz.base.util.UtilFormatOut"].formatCurrency(totalOppEstAmount, defaultCurrencyUom, locale)?default("0")?if_exists/>
 
  <#assign totalWonAmount= Static["org.ofbiz.base.util.UtilFormatOut"].formatCurrency(totalWonAmount, defaultCurrencyUom, locale)?default("0")?if_exists/>
  <#assign totalLostAmount= Static["org.ofbiz.base.util.UtilFormatOut"].formatCurrency(totalLostAmount, defaultCurrencyUom, locale)?default("0")?if_exists/>
   
 <#assign enableOppoTypeStatisticFilter = Static["org.fio.homeapps.util.DataUtil"].getGlobalValue(delegator, "ENBL_OPPO_TAB_STATISTIC_FLTR","N")?if_exists>
 <#assign extraLeft = '<div class="form-check form-check-inline" style="padding-left: 10px">
			<input class="form-check-input oppo-status" name="oppoOpen" id="oppoOpen" value="OPPO_OPEN" type="checkbox" checked>
			<label class="form-check-label">Open</label>
		</div>
		
		<div class="form-check form-check-inline">
			<input class="form-check-input" name="estClosedDays" id="estClosedDays" type="text" size="4">
		  	<label class="form-check-label">Days before Target Completion Date</label>
		</div>
		
		<div class="form-check form-check-inline">
			<input class="form-check-input oppo-status" name="oppoClosed" id="oppoClosed" value="OPPO_CLOSED" type="checkbox">
		  	<label class="form-check-label">Completed</label>
		</div>
		 
		<select id="oppo_type_id" class="oppoType custom-selectbox">
			<option value="">Select oppo Type</option>
			<option value="BASE">Base</option>
			<option value="OPTION">Optional</option>
			<option value="CHANGE_ORDER">Change order</option>
		</select>
		<div class="form-check form-check-inline" style="padding-left: 10px">
			<h2 id="totalOppCount">${totalOppCount!}</h2>
		  	<label class="form-check-label"> <h4>Total </h4> </label>
		</div>
		
		<div class="form-check form-check-inline">
			<h2 id="totalWonOppCount">${totalWonOppCount!}</h2>
		  	<label class="form-check-label"> <h4>Won </h4> </label>
		</div>
		
		<div class="form-check form-check-inline">
			<h2 id="totalLostOppCount">${totalLostOppCount!}</h2>
		  	<label class="form-check-label"> <h4>Lost </h4> </label>
		</div>
		
		<div class="form-check form-check-inline">
			<h2 id="totalOpenOppCount">${totalOpenOppCount!}</h2>
		  	<label class="form-check-label"> <h4> Open </h4> </label>
		</div>
		
		<div class="form-check form-check-inline">
			<h2 id="totalOppEstAmount">${totalOppEstAmount!}</h2>
		  	<label class="form-check-label"> <h4>Amount </h4> </label>
		</div>
		
		<div class="form-check form-check-inline">
			<h2 id="OppWonPercent">${OppWonPercent!}%</h2>
		  	<label class="form-check-label"> <h4>Won </h4> </label>
		</div>
		
		<div class="form-check form-check-inline">
			<h2 id="OppLossPercent">${OppLossPercent!}%</h2>
		  	<label class="form-check-label"> <h4>Lost </h4> </label>
		</div>
		
		<div class="form-check form-check-inline">
			<h2 id="totalWonAmount">${totalWonAmount!}</h2>
		  	<label class="form-check-label"> <h4>Won amount </h4> </label>
		</div>
		
		<div class="form-check form-check-inline">
			<h2 id="totalLostAmount">${totalLostAmount!}</h2>
		  	<label class="form-check-label"> <h4>Lost amount</h4> </label>
		</div>
		' />

<#assign extraContent='
<button id="refresh-opportunity-btn" type="button" class="btn btn-xs btn-primary m5"><i class="fa fa-refresh" aria-hidden="true"></i></button>
' />

<#if rightContent?has_content>
	<#assign extraContent =  extraContent + rightContent />
</#if> 
 <#-- 
<@AgGrid
	gridheadertitle="Opportunities"
	gridheaderid="opportunity-grid-action-container"
	savePrefBtn=true
	clearFilterBtn=true
	exportBtn=true
	insertBtn=false
	updateBtn=false
	removeBtn=false
	helpBtn=true
	helpUrl=helpUrl!
	headerextra=extraContent!
	headerextraleft = extraLeft!
	refreshPrefBtnId="opportunity-refresh-pref-btn"
	savePrefBtnId="opportunity-save-pref-btn"
	clearFilterBtnId="opportunity-clear-filter-btn"
	exportBtnId="opportunity-export-btn"
    userid="${userLogin.userLoginId}" 
    shownotifications="true" 
    instanceid="OPPORTUNITYS" 
    autosizeallcol="true"
    debug="false"
    />    

    <span id="search-oppo-btn"></span>  
	<script type="text/javascript" src="/common-portal-resource/js/ag-grid/opportunity/find-opportunity.js"></script>
	-->
	<input type="hidden" id="userId" value="${userLogin.userLoginId!}">
    <input type="hidden" id="externalLoginKey" value="${requestAttributes.externalLoginKey!}"/>
	<@fioGrid 
		instanceId="OPPORTUNITYS"
		jsLoc="/common-portal-resource/js/ag-grid/opportunity/find-opportunity.js"
		headerLabel="Opportunities"
		headerId="oppo_list_tle"
		headerExtra=extraContent!
		headerBarClass="grid-header-no-bar"
		headerExtraLeft = extraLeft!
		savePrefBtnId="opportunity-save-pref-btn"
		clearFilterBtnId="opportunity-clear-filter-btn"
		subFltrClearId="opportunity-clear-sub-ftr"
		exportBtn=true
		exportBtnId="opportunity-list-export-btn"
		savePrefBtn=false
		clearFilterBtn=false
		subFltrClearBtn=false
		/>   
</div>
</div>
	
<script>
jQuery(document).ready(function() {
	$("#estimatedClosedDays").bind("keypress", function (e) {
		if (e.keyCode == 13) {
			return false;
		}
	});
	<#if (oppTypeEnabled?has_content && oppTypeEnabled!="Y") || (enableOppoTypeStatisticFilter?has_content && enableOppoTypeStatisticFilter!="Y")>	
		$("#oppo_type_id").hide();
	</#if>
	
	$("#oppo_type_id").change(function(){
		let salesOpportunityTypeId = $(this).val();
		$("#salesOpportunityTypeId").val(salesOpportunityTypeId);
		 let inputData = {};
		 inputData=$("#opportunity-search-form").serialize(); //+ "&salesOpportunityTypeId="+salesOpportunityTypeId;
		$.ajax({
		  async: false,
		  url:'/common-portal/control/getOpportunityStatistics',
		  type:"POST",
		  data: inputData,
		  success: function(result){
			  if (result){
			  	let responseMessage = result["responseMessage"];
			  	let data = result["list"];
			  	if (responseMessage && responseMessage=="success"){
			  		
			  		$("#refresh-opportunity-btn").trigger('click');
			  		
			  		$("#totalOppCount").html(data.totalOppCount);
					$("#totalWonOppCount").html(data.totalWonOppCount);
					$("#totalLostOppCount").html(data.totalLostOppCount);
					$("#totalOpenOppCount").html(data.totalOpenOppCount);
					$("#totalOppEstAmount").html('$'+data.totalOppEstAmount);
					$("#OppWonPercent").html(data.OppWonPercent+"%");
					$("#OppLossPercent").html(data.OppLossPercent+"%");
					
					$("#totalWonAmount").html(data.totalWonAmount);
			  		$("#totalLostAmount").html(data.totalLostAmount);
			  	
			  	}
			  }
		  }
		});
	});
});
</script>