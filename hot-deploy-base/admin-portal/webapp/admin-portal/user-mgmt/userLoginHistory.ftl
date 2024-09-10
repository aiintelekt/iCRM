<#include "component://ab-ag-grid-support/webapp/ab-ag-grid-support/lib/ag_grid_header.ftl"/>
<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<div class="row">
	<div id="main" role="main" class="pd-btm-title-bar">

	<div class="col-lg-12 col-md-12 col-sm-12 dash-panel">
	<@sectionFrameHeaderTab title="${uiLabelMap.ViewLoginAndSessionHistory!}" />
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
               <form action="userLoginHistory" method="post" id="searchForm" name="searchForm">
				  <@inputHidden 
					  id="searchCriteria"
					  />
                  <div class="row">
                     <div class="col-lg-4 col-md-6 col-sm-12">
                        <@inputCell 
	                        id="visitId"
	                        placeholder="Visit ID"
	                        value="${requestParameters.visitId?if_exists}"
	                        />
                        <@inputCell 
	                        id="clientIpAddress"
	                        placeholder="Client IP Address"
	                        value="${requestParameters.clientIpAddress?if_exists}"
	                        />   
                     </div>
                     <div class="col-lg-4 col-md-6 col-sm-12">
                        <@inputCell 
	                        id="userId"
	                        placeholder="User Login ID"
	                        value="${requestParameters.userId?if_exists}"
	                        />
	                        
                        <@inputDate
	                        id="fromDate"
	                        type="date"
	                        value="${requestParameters.fromDate?if_exists}"
	                        placeholder="Start Date"
	                        />
                     </div>
                     <div class="col-lg-4 col-md-6 col-sm-12">
                        <#assign componentList = (Static["org.ofbiz.entity.util.EntityQuery"].use(delegator).select("componentName","uiLabels").from("OfbizComponentAccess").where("isHide","N").queryList())?if_exists />
                        <#assign components = (Static["org.fio.admin.portal.util.DataHelper"].getDropDownOptions(componentList, "componentName","uiLabels"))?if_exists />
                        <@dropdownCell
	                        id="module"  
	                        placeholder="Select Module"      
	                        allowEmpty=true        
	                        options=components!
	                        value="${requestParameters.module?if_exists}"     
	                        />   
                        <@inputDate
	                        id="toDate"
	                        type="date"
	                        placeholder="End Date"
	                        value="${requestParameters.toDate?if_exists}"
	                        />
	                        
	                    </div>
	                    </div>
                        <div class="text-right p-2">
                        	<@button 
                                label="${uiLabelMap.Search}"
                                id="main-search-btn"
                                />
                        </div>
               </form>
            </div> <#-- End pad-top-->
         </div> <#-- End accordion-->
         </div>
   </div> <#-- End main-->
</div>
</div> <#-- End row-->
         <div class="row" style="width:100%">
	
		<div class="col-lg-12 col-md-12 col-sm-12 dash-panel" id="main-grid-action-container>
		        <@headerH2 title="${uiLabelMap.SearchResults!}" class="float-left"/>
		         <@AgGrid
					userid="${userLogin.userLoginId}" 
					instanceid="USER_LOGIN_HISTORY" 
					autosizeallcol="true" 
					gridoptions='{"pagination": true, "paginationPageSize": 10 }'
					/>
		    </div>
		    </div>
		   
         <#-- 
         <div class="table-responsive">
         	<div class="loader text-center" id="loader" sytle="display:none;">
                  <span></span>
                  <span></span>
                  <span></span>
                </div>
               <div id="grid1" style="width: 100%;" class="ag-theme-balham"></div>
               <script type="text/javascript" src="/admin-portal-resource/js/ag-grid/user-mgmt/user-login-history.js"></script>
         </div>
			 -->
			 	        
      

<script>

function loadMainGrid(fag) {
		
	$.ajax({
	  url:'/admin-portal/control/getVisitorLoginHistory',
	  type:"POST",
	  data: JSON.parse(JSON.stringify($("#searchForm").serialize())),
	  success: function(data){
			// console.log("data: ", data)
		  fag.setRowData(data)
	  }
	})
	
}

/*
function doSearch(){
    loadAgGrid();
}
$("#module").change(function() {
     loadAgGrid();
});
*/

/*
function prepareForm(){
	var visitId = $("#visitId").val();
	var clientIpAddress = $("#clientIpAddress").val();
	var userId = $("#userId").val();
	var fromDate = $("#fromDate").val();
	var module = $("#module").val();
	var toDate = $("#toDate").val();
	
	item = {}
    item ["visitId"] = visitId;
    item ["clientIpAddress"] = clientIpAddress;
    item ["userId"] = userId;
    item ["module"] = module;
    item ["fromDate"] = fromDate;
    item ["toDate"] = toDate;
    
    jsonString = JSON.stringify(item);
    $("#searchCriteria").val(jsonString);
    $("#searchForm").submit();
    
}*/
</script>