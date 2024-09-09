<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<#include "component://common-portal/webapp/common-portal/export/modal_window.ftl"/>

<#assign requestURI = ""/>
<#assign isShowHelpUrl="Y">
<#assign findCustomerGridInstanceId ="CUSTOMERS" />
<#assign findCustomerDynaScreenInstanceId ="FIND_CUSTOMER" />
<#if isPhoneCampaignEnabled?has_content && isPhoneCampaignEnabled?if_exists=="Y">
<#assign isShowHelpUrl="N">
<#assign findCustomerGridInstanceId ="CUSTOMERS_CALL_LIST" />
<#assign findCustomerDynaScreenInstanceId ="FIND_CUSTOMER_CALL" />
</#if>
<div class="row">
	<div id="main" role="main" class="pd-btm-title-bar">

	<div class="col-lg-12 col-md-12 col-sm-12 dash-panel">
	
	<@sectionFrameHeader title="Find Customers" isShowHelpUrl=isShowHelpUrl!/>
		
	<div class="panel-group" id="accordionMenu" role="tablist" aria-multiselectable="true">
		<div class="panel panel-default">
			<div class="panel-heading" role="tab" id="headingTwo">
				<h4 class="panel-title">
					<a role="button" data-toggle="collapse" data-parent="#accordionMenu"
						href="#accordionDynaBase" aria-expanded="false"
						aria-controls="collapseOne"> ${uiLabelMap.MainFilter} </a>
				</h4>
			</div>	
			
			<div id="accordionDynaBase" class="panel-collapse collapse" role="tabpanel" aria-labelledby="headingOne">
				<form method="post" id="searchForm" class="form-horizontal" novalidate="novalidate" data-toggle="validator">
					<input type="hidden" name="owner" value="${loggedUserPartyId!}">
					<input type="hidden" name="externalLoginKey" value="${requestAttributes.externalLoginKey!}">
					<@inputHidden id="expFileTemplateId" value="CUST_EXP_TPL"/>
					<@inputHidden id="exportDataType" value="CUSTOMER_LIST"/>
					
					<#if activityTypeList?has_content>
					<#list activityTypeList.entrySet() as entry>  
					    <input type="hidden" name="defaultActivityTypes" value="${entry.key!}">
					</#list>
					</#if>
					<div class="panel-body">
						<input type="hidden" id="findCustomerGridInstanceId" value="${findCustomerGridInstanceId!}"/>
						<@dynaScreen 
						instanceId=findCustomerDynaScreenInstanceId!
						modeOfAction="CREATE"
						/>
												
						<div class="text-right pd-cbx-lbls pad-10" style="padding-top: 0px;">
					     	<@button
					        id="main-search-btn"
					        label="${uiLabelMap.Find}"
					        />	
					     	<@reset
							label="${uiLabelMap.Reset}"/>
			            </div>
			      		
					</div>	
				</form>
			</div>	
		</div>	
	</div>	
		
	</div>
	</div>
</div>

<@exportFileModal
instanceId="exp-file-modal" exportType="CUSTOMER_LIST"
/>

<script>     
$(document).ready(function() {
	if($('#generalCountryGeoId').val()) {
		 getDependentDropdownValues('getAssociatedStateList', 'countryGeoId', 'generalCountryGeoId', 'generalStateProvinceGeoId', 'stateList', 'geoId', 'geoName', '${stateValue!}');
	}
		
	$('#generalCountryGeoId').change(function(e, data) {
		$("#generalStateProvinceGeoId").dropdown('clear');
		$('#generalPostalCode').val('');
		$('#generalPostalCodeExt').val('');
		getDependentDropdownValues('getAssociatedStateList', 'countryGeoId', 'generalCountryGeoId', 'generalStateProvinceGeoId', 'stateList', 'geoId', 'geoName','${stateValue!}');
		var countryGeoId = $('#generalCountryGeoId').val();
		if(countryGeoId != ''){
			regexJson = getServiceResult("getZipCodeRegex",'countryGeoId', countryGeoId);
			regex = regexJson.regex;
		} else {
			$('#generalStateProvinceGeoId').html('<option value="">Please Select</option>');
		}
	});

	if($('#personResponsibleFor') != undefined){
		getCsrDropdown();
		function getCsrDropdown(){
			var optionList = "<option value=''></option>";
			$.ajax({
				type: "GET",
				url: "/customer-portal/control/getCsrDropdown",
				async: false,
				success: function(data){
					if(data.list){
						data.list.forEach(element => {optionList += '<option value="'+element.partyId+'">'+element.name+'</option>';});
					}
				}
			});
			$("#personResponsibleFor").html(optionList);
			$("#personResponsibleFor").dropdown('refresh');
		}
	}
});

</script>