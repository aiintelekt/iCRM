<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<#include "component://common-portal/webapp/common-portal/lib/picker_macro.ftl"/>
<script type="text/javascript" src="/common-portal-resource/js/ag-grid/service-request/service_request.js"></script>

<div class="row">
	<div id="main" role="main" class="pd-btm-title-bar">

	<div class="col-lg-12 col-md-12 col-sm-12 dash-panel">
	
	<@sectionFrameHeader title="${uiLabelMap.FindServiceRequest!}" />
	
	<div class="panel-group" id="accordionMenu" role="tablist" aria-multiselectable="true">
		<div class="panel panel-default">
			<div class="panel-heading" role="tab" id="headingTwo">
				<h4 class="panel-title">
					<a role="button" class="collapsed" data-toggle="collapse" data-parent="#accordionMenu"
						href="#accordionDynaBase" aria-expanded="true"
						aria-controls="collapseOne"> ${uiLabelMap.MainFilter} </a>
				</h4>
			</div>	
			
			<div id="accordionDynaBase" class="panel-collapse collapse show" role="tabpanel" aria-labelledby="headingOne">
				<form method="post" id="searchForm" class="form-horizontal" novalidate="novalidate" data-toggle="validator">
				<input type="hidden" name="searchType" value="SR">
				<input type="hidden" name="isFilterByEarliestDueDate" value="Y">
				<input type="hidden" name="externalLoginKey" value="${requestAttributes.externalLoginKey!}"/>
				<div class="panel-body">
					
					<#include "component://sr-portal/webapp/sr-portal/services/find_sr_menu.ftl"/>
					<div class="tab-content">
						
						<div id="sr-main-search" class="tab-pane fade active show">
							<@dynaScreen 
								instanceId="FIND_SR_CNSL"
								modeOfAction="CREATE"
								/>
						</div>
						<div id="ho-postal-search" class="tab-pane fade">
							<@dynaScreen 
								instanceId="FIND_SR_POSTAL_CNSL"
								modeOfAction="CREATE"
								/>
						</div>
						<div id="tech-search" class="tab-pane fade">
							<@dynaScreen 
								instanceId="FIND_SR_TECH"
								modeOfAction="CREATE"
								/>
						</div>
					</div>
					<div class="row find-srbottom">
		            	<div class="col-lg-12 col-md-12 col-sm-12">
		                	<div class="text-right pd-cbx-lbls" style="padding-top: 0px;">
		                	
		                		<div class="form-check-inline">
		                        	<label class="form-check-label flx-cbx-lbl">
		                          		<input name="open" class="form-check-input" value="Y"  type="checkbox" checked="checked">Open
		                          	</label>
		                        </div>                
		        				<div class="form-check-inline">
		          					<label class="form-check-label flx-cbx-lbl">
		          						<input name="closed" class="form-check-input" value="Y" type="checkbox">Completed
		          					</label>
		        				</div>
		                	
						     	<@button
						        id="sr-search-btn"
						        label="${uiLabelMap.Find}"
						        />	
						     	<@reset
								label="${uiLabelMap.Reset}"/>
		                    </div>
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
 
<@partyPicker 
instanceId="partyPicker"
/>
<#-- <@dealerPicker 
instanceId="dealerPicker"
/>-->
<@customerPicker 
instanceId="customerPicker"
/>
<@contractorPicker 
instanceId="contractorPicker"
/>
<@orderPicker 
instanceId="orderPicker"
/>
<@findOrderDealerPicker 
instanceId="findOrderDealerPicker"
/>
<script>

$(document).ready(function() {

$("#partyId_desc").on("change", function() {
	var partyId = $("#partyId_val").val();
	if (partyId != "") {
   		getPrimaryContacts(partyId);
   }
});

$(".srArea-input").one( "click",function(){
	loadCategory();
});
$(".owner-input").one( "click",function(){
	getUsers();
});
$(".technician-input").one( "click",function(){
	getTechnicians();
});
$(".salesPerson-input").one( "click",function(){
	getSalesPerson();
});
$(".primaryTechnicain-input").one( "click",function(){
	getPrimaryTechnician();
});

var countryGeoId = $('#countryGeoId').val();
if($('#countryGeoId').val()) {
	getDependentDropdownValues('getAssociatedStateList', 'countryGeoId', 'countryGeoId', 'stateProvinceGeoId', 'stateList', 'geoId', 'geoName', '${stateValue!}', null, true);
}
$('#countryGeoId').change(function(e, data) {
	$("#stateProvinceGeoId").html("");
	$("#stateProvinceGeoId").dropdown('refresh');
	getDependentDropdownValues('getAssociatedStateList', 'countryGeoId', 'countryGeoId', 'stateProvinceGeoId', 'stateList', 'geoId', 'geoName','${stateValue!}', null, true);
});

$("a[href='#sr-main-search']").on('shown.bs.tab', function(e) {
	$("#searchForm input[name='searchType']").val("SR");
});
$("a[href='#ho-postal-search']").on('shown.bs.tab', function(e) {
	$("#searchForm input[name='searchType']").val("POSTAL");
});
$("a[href='#sr-homeowner-search']").on('shown.bs.tab', function(e) {
	$("#searchForm input[name='searchType']").val("HOMEOWNER");
});
$("a[href='#sr-contractor-search']").on('shown.bs.tab', function(e) {
	$("#searchForm input[name='searchType']").val("CONTRACTOR");
});
$("a[href='#sr-attribute-search']").on('shown.bs.tab', function(e) {
	$("#searchForm input[name='searchType']").val("ATTRIBUTE");
});
$("a[href='#tech-search']").on('shown.bs.tab', function(e) {
	$("#searchForm input[name='searchType']").val("ACTIVITY");
});

$("#srArea").change(function() {
	loadSubCategory($(this).val());
});

$("#stateProvinceGeoId").change(function() {
	
	$("#city").dropdown("clear");
	$("#countyGeoId").dropdown("clear");
	
	var cityOptions = '<option value="" selected="">Select City</option>';
	var countyOptions = '<option value="" selected="">Select County</option>';
	//var zipOptions = '<option value="" selected="">Select Zip Code</option>';	
	
	let cityList = new Map();
	let countyList = new Map();
	//let zipList = new Map();	
								
	$.ajax({
		type: "POST",
     	url: "/uiadv-portal/control/searchZipCodeAssocs",
        data: {"state": $(this).val(), "externalLoginKey": "${requestAttributes.externalLoginKey!}"},
        async: false,
        success: function (result) {   
            if (result.code == 200) {
            	for (var i = 0; i < result.data.length; i++) {
					var data = result.data[i];
					cityList.set(data.city, data.city);
					countyList.set(data.county, data.county);
					//zipList.set(data.zip, data.zip); 
				}
            }
        }
	});   
	
	for (let key of cityList.keys()) {
  		cityOptions += '<option value="'+key+'">'+cityList.get(key)+'</option>';
	}
	for (let key of countyList.keys()) {
  		countyOptions += '<option value="'+key+'">'+countyList.get(key)+'</option>';
	}
	/*for (let key of zipList.keys()) {
  		zipOptions += '<option value="'+key+'">'+zipList.get(key)+'</option>';
	}*/
	
	$("#city").html( cityOptions );
	$("#city").dropdown('refresh');
	
	$("#countyGeoId").html( countyOptions );
	$("#countyGeoId").dropdown('refresh');
	/*
	$("#zipCode").html( zipOptions );
	$("#zipCode").dropdown('refresh');
	*/
});

$(".finishType-input").one("click", function() {
    loadSegmentCodeData('FSR_FINISH_TYPE', 'finishType');
});

$(".programTemplateId-input").one("click", function() {
	loadProgramTemplate("programTemplateId", null, "${requestAttributes.externalLoginKey!}");
});

<#-- 
<#if defaultLocationId?has_content>
$("#location").val("${defaultLocationId!}");
$("#location").trigger( "change" )
$("div.ui.dropdown.search.form-control.fluid.show-tick.location.selection > i").addClass("clear");
</#if>
-->

});

function getUsers() {
	var userOptionList = '<option value=""></option>'; //'<option value="${loggedUserId!}">${loggedUserPartyName!}</option>';
	$.ajax({
		type: "GET",
		//url:'/common-portal/control/getUsersList?roleTypeId=CUST_SERVICE_REP,SALES_REP,SR_OWNER&isIncludeLoggedInUser=Y&externalLoginKey=${requestAttributes.externalLoginKey!}',
		url:'/common-portal/control/getUsersList?roleTypeId=SR_OWNER&isIncludeLoggedInUser=Y&isIncludeInactiveUser=N&externalLoginKey=${requestAttributes.externalLoginKey!}',
		async: false,
		success: function(data) {
			for (var i = 0; i < data.length; i++) {
				var type = data[i];
				//userOptionList += '<option value="'+type.userLoginId+'">'+type.userName+' ('+ type.roleDesc +')</option>';
				userOptionList += '<option value="'+type.userLoginId+'">'+type.userName+'</option>';
			}
		}
	});
	$("#owner").html(userOptionList);
	$("#owner").dropdown('refresh');
}

function getPrimaryContacts(partyId){
	$("#srPrimaryContactId").empty();
	var nonSelectContent = "<span class='nonselect'>Please Select</span>";
	var dataSourceOptions = '<option value=""></option>';
	var selectedContactId  = $("#selectedContactId").val();
	$('#srPrimaryContactId').dropdown('clear');
	$("div.ui.dropdown.search.form-control.fluid.show-tick.srPrimaryContactId.selection > i").removeClass("clear");
	$.ajax({
		type: "POST",
		url: "/common-portal/control/getPrimaryContacts",
		data: {"partyId": partyId, "externalLoginKey": "${requestAttributes.externalLoginKey!}"},
		async: false,
		success: function (data) {
			if((data != null && data != "" && data !="undefined") && data.responseMessage=="success" && data.partyRelContacts.length > 0){
					for (var i = 0; i < data.partyRelContacts.length; i++) {
						var entry = data.partyRelContacts[i];
						//if(entry.selected!=null){
		            		//dataSourceOptions += '<option value="'+entry.contactId+'" selected>'+entry.name+'</option>';
						//}else{
		            		dataSourceOptions += '<option value="'+entry.contactId+'">'+entry.name+'</option>';
						//}
					}
				//$("div.ui.dropdown.search.form-control.fluid.show-tick.srPrimaryContactId.selection > i").addClass("clear");
			}
		}
	});
	$("#srPrimaryContactId").append(dataSourceOptions);
	$("#srPrimaryContactId").dropdown('refresh');
}

function loadCategory() {
    var nonSelectContent = "<span class='nonselect'>Please Select</span>";
    //var categoryOptions = '<option value="" data-content="'+nonSelectContent+'" selected="">Please Select</option>';
    var categoryOptions = '<option value="NA" selected="">NA</option>';
    $("#srSubArea").html(categoryOptions);
    $.ajax({
        type: "POST",
        url: "getSrCategory",
        async: false,
        success: function(data) {
        	var sourceDesc = data.results;
            for (var i = 0; i < data.length; i++) {
            	var category = data[i];
                categoryOptions += '<option value="'+category.srCategoryId+'">'+category.srCategoryDesc+'</option>';
          	}
        }
    });
    $("#srArea").html(categoryOptions);
    $("#srArea").dropdown('refresh');
}

function loadSubCategory(srCategoryId) {
    var nonSelectContent = "<span class='nonselect'>Please Select</span>";
    //var subCategoryOptions = '<option value="" data-content="'+nonSelectContent+'" selected="">Please Select</option>';
    var subCategoryOptions = '<option value="NA" selected="">NA</option>';
    $.ajax({
        type: "POST",
        url: "getSrSubCategory",
        data: { "srCategoryId": srCategoryId },
        async: false,
        success: function(data) {
        	var sourceDesc = data.results;
            for (var i = 0; i < data.length; i++) {
            	var category = data[i];
                subCategoryOptions += '<option value="'+category.srSubCategoryId+'">'+category.srSubCategoryDesc+'</option>';
          	}
        }
    });
    $("#srSubArea").html(subCategoryOptions);
    $("#srSubArea").dropdown('refresh');
}

function getTechnicians() {
    var userOptionList = '<option value=""></option>';
    $.ajax({
        type: "GET",
        url:'/common-portal/control/getUsersList?roleTypeId=TECHNICIAN&isIncludeInactiveUser=N&externalLoginKey=${requestAttributes.externalLoginKey!}',
        async: false,
        success: function(data) {
        	for (var i = 0; i < data.length; i++) {
                var type = data[i];
                userOptionList += '<option value="'+type.userLoginId+'">'+type.userName+'</option>';
            }
        }
    });
   $("#technician").html(userOptionList);
   $("#technician").dropdown('refresh');
}


$('input[type=radio][name=dateRangeType]').change(function() {
	if ($(this).is(':checked')) {
		var val = $(this).val();
		if("CLOSE" === val) {
			$('input[type=checkbox][name=open]').attr("checked", false);
			$('input[type=checkbox][name=closed]').attr("checked", true);
		} else{
			$('input[type=checkbox][name=open]').attr("checked", true);
			$('input[type=checkbox][name=closed]').attr("checked", false);
		}
	}
});

$("#srStatus").change(function(){
	var val = this.value;
	if("SR_CLOSED"=== val || "SR_CANCELLED" === val){
		$('input[type=checkbox][name=open]').attr("checked", false);
		$('input[type=checkbox][name=closed]').attr("checked", true);
	} else{
		$('input[type=checkbox][name=open]').attr("checked", true);
		$('input[type=checkbox][name=closed]').attr("checked", false);
	}
});


 

function getSalesPerson(){
	$("#salesPerson").empty();
	var loggedInUserId  = $("#loggedInUserId").val();
	var selectedSalesPerson  = $("#selectedSalesPerson").val();	
	var externalLoginKey = $("#externalLoginKey").val();	
	var userOptionList = '<option value="" selected="">Select Sales Person</option>';	
	$.ajax({
		type: "GET",
		url:'/common-portal/control/getUsersList?roleTypeId=SALES_REP&isIncludeLoggedInUser=Y&isIncludeInactiveUser=N&externalLoginKey=${requestAttributes.externalLoginKey!}',
		async: false,
		success: function(data) {
			for (var i = 0; i < data.length; i++) {
				var type = data[i];		
				if(selectedSalesPerson && selectedSalesPerson === type.partyId){
					userOptionList += '<option value="'+type.partyId+'" selected="selected">'+type.userName+'</option>';
				}else{
					userOptionList += '<option value="'+type.partyId+'">'+type.userName+'</option>';	
				}				
			}
		}
	});
	
	$("#salesPerson").html(userOptionList);
	$("#salesPerson").dropdown('refresh');
	
}

function getPrimaryTechnician(){
	$("#primaryTechnician").empty();
	var externalLoginKey = $("#externalLoginKey").val();	
	var existsPrimTech= $("#selectedPrimaryTechnician").val();
	var userOptionList = '<option value="" selected="">Select Primary Technician</option>';	
	var flag = true;
	$.ajax({
		type: "GET",
		url:'/common-portal/control/getUsersList?roleTypeId=TECHNICIAN&isIncludeInactiveUser=N&externalLoginKey=${requestAttributes.externalLoginKey!}',
		async: false,
		success: function(data) {
			for (var i = 0; i < data.length; i++) {
				var type = data[i];		
				if(existsPrimTech && existsPrimTech === type.partyId){
					userOptionList += '<option value="'+type.partyId+'" selected="selected">'+type.userName+'</option>';
				}else{
					userOptionList += '<option value="'+type.partyId+'">'+type.userName+'</option>';	
				}				
			}
		}
	});
	$("#primaryTechnicain").html(userOptionList);
	$("#primaryTechnicain").dropdown('refresh');	
}

$("#ticketNumber_desc").change(function() {
	$("#location").dropdown('clear');
	$("#location").dropdown('set selected', '');
	$("#location").dropdown('refresh');
	$.ajax({
		type: "POST",
     	url: "/sr-portal/control/getOrderDetail",
        data: {"orderId": $("#searchForm input[name=ticketNumber]").val(), "externalId": $("#searchForm input[id=ticketNumber_alter]").val(), "srNumber": $("#searchForm input[name='srNumber']").val(), "externalLoginKey": "${requestAttributes.externalLoginKey!}"},
        async: false,
        success: function (data) {   
            if (data.code == 200) {
            	/*
            	if (data.orderDate) {
            		$("#orderDate").val(data.orderDate);
            	}
            	*/
            	
            	if (data.locationId) {
            		$('#location').val(data.locationId);
					$('#location').trigger('change');
					$("div.ui.dropdown.search.form-control.fluid.show-tick.location.selection > i").addClass("clear");
            	}
            }
        }
        
	});
	
});

</script>