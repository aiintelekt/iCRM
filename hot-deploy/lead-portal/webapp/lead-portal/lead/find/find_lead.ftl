<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>

<div class="row">
	<div id="main" role="main" class="pd-btm-title-bar">

	<div class="col-lg-12 col-md-12 col-sm-12 dash-panel">
	
	<@sectionFrameHeader title="Find Leads" />
		
	<div class="panel-group" id="accordionMenu" role="tablist" aria-multiselectable="true">
		<div class="panel panel-default">
			<div class="panel-heading" role="tab" id="headingTwo">
				<h4 class="panel-title">
					<a role="button" data-toggle="collapse" data-parent="#accordionMenu"
						href="#accordionDynaBase" aria-expanded="true"
						aria-controls="collapseOne"> ${uiLabelMap.MainFilter} </a>
				</h4>
			</div>	
			
			<div id="accordionDynaBase" class="panel-collapse collapse show" role="tabpanel" aria-labelledby="headingOne">
				<form method="post" id="searchForm" class="form-horizontal" novalidate="novalidate" data-toggle="validator">
				<div class="panel-body">
				
					<@dynaScreen instanceId="FIND_LEADS" 
					modeOfAction="CREATE" />
					<#--<div class="col-lg-4 col-md-6 col-sm-12">
						 
						<@inputRow 
						id="partyId"
						placeholder="Lead ID"
						inputColSize="col-sm-12"
						iconClass="fa fa-user"
						required=false
						/> 
						
						<@inputRow 
						id="email"
						placeholder=uiLabelMap.email
						inputColSize="col-sm-12"
						iconClass="fa fa-envelope"
						required=false
						/>
						
                       	<#assign dataSourceTypes = Static["org.ofbiz.entity.util.EntityQuery"].use(delegator).select("dataSourceId","description").from("DataSource").where("dataSourceTypeId","LEAD_GENERATION","disable","Y").orderBy("sequenceId").queryList()?if_exists />    
                        <#assign dataSourceList = Static["org.fio.admin.portal.util.DataHelper"].getDropDownOptions(dataSourceTypes,"dataSourceId","description")?if_exists />
                        
                       <@dropdownCell 
                          id="dataSourceId"
                          name="dataSourceId"
                          placeholder="Source"
                          options=dataSourceList!
                          value="${requestParameters.dataSourceId?if_exists}"
                          allowEmpty=true
                        /> 
												
					</div>
					<div class="col-lg-4 col-md-6 col-sm-12">
						
						<@inputRow 
						id="name"
						placeholder="Lead Name"
						inputColSize="col-sm-12"
						iconClass="fa fa-user-circle-o"
						required=false
						/> 
						
						<@inputRow 
						id="phone"
						placeholder="Phone"
						inputColSize="col-sm-12"
						iconClass="fa fa-phone"
						required=false
						/>
			            
			            <#assign industryTypes = Static["org.ofbiz.entity.util.EntityQuery"].use(delegator).select("enumId","description").from("Enumeration").where("enumTypeId","PARTY_INDUSTRY","isEnabled","Y").queryList()?if_exists />    
                        <#assign industryList = Static["org.fio.admin.portal.util.DataHelper"].getDropDownOptions(industryTypes,"enumId","description")?if_exists />
                        
                       <@dropdownCell 
                          id="industryEnumId"
                          name="industryEnumId"
                          placeholder="Industry"
                          options=industryList!
                          value="${requestParameters.industryEnumId?if_exists}"
                          allowEmpty=true
                        /> 
                        
					</div>
					<div class="col-lg-4 col-md-6 col-sm-12">
						<@inputRow 
						id="localName"
						placeholder=uiLabelMap.localName
						inputColSize="col-sm-12"
						iconClass="fa fa-user-circle-o"
						required=false
						/> 
					</div>
					 <div class="text-right pd-cbx-lbls" style="padding-top: 0px;">
				     	<@button
				        id="main-search-btn"
				        label="${uiLabelMap.Find}"
				        />	
				     	<@reset
						label="${uiLabelMap.Reset}"/>
		            </div> -->
						</div> 
			    <div class="row find-srbottom">
		           <div class="col-lg-12 col-md-12 col-sm-12">
		              <div class="text-right pd-cbx-lbls" style="padding-top: 0px;">
						<div class="search-btn">
		                	<@button
					        id="main-search-btn"
					        label="${uiLabelMap.Find}"
					        />	
					     	<@reset
							label="${uiLabelMap.Reset}"/>
						</div>
					</div>
					</div>
					<#-- <div class="text-right pd-cbx-lbls" style="padding-top: 0px;">
				     	<@button
				        id="main-search-btn"
				        label="${uiLabelMap.Find}"
				        />	
				     	<@reset
						label="${uiLabelMap.Reset}"/>
		            </div>  -->
				</div>	
				</form>
			</div>	
		</div>	
	</div>	
		
	</div>
	</div>
</div>

<script>     
$(document).ready(function() {

var countryGeoId = $('#countryGeoId').val();
if($('#countryGeoId').val()) {
	getDependentDropdownValues('getAssociatedStateList', 'countryGeoId', 'countryGeoId', 'stateProvinceGeoId', 'stateList', 'geoId', 'geoName', '${stateValue!}', null, true);
}
$('#countryGeoId').change(function(e, data) {
	$("#stateProvinceGeoId").dropdown('clear');
	getDependentDropdownValues('getAssociatedStateList', 'countryGeoId', 'countryGeoId', 'stateProvinceGeoId', 'stateList', 'geoId', 'geoName','${stateValue!}', null, true);
});

$("#stateProvinceGeoId").change(function() {
	$("#city").dropdown("clear");
		
	var cityOptions = '<option value="" selected="">Select City</option>';
		
	let cityList = new Map();
	var stateName = $(this).val();
	console.log("The state selected is"+stateName);							
	$.ajax({
		type: "POST",
     	url: "/uiadv-portal/control/searchZipCodeAssocs",
        data: {"state": stateName, "externalLoginKey": "${requestAttributes.externalLoginKey!}"},
        async: false,
        success: function (result) {   
            if (result.code == 200) {
            	for (var i = 0; i < result.data.length; i++) {
					var data = result.data[i];
					cityList.set(data.city, data.city);
				}
            }
        }
	});   
	
	for (let key of cityList.keys()) {
  		cityOptions += '<option value="'+key+'">'+cityList.get(key)+'</option>';
	}
	
	$("#city").html( cityOptions );
	$("#city").dropdown('refresh');
});
	
});
</script>