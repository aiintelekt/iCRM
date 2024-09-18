<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>

<div class="row">
	<div id="main" role="main" class="pd-btm-title-bar">
		<div class="col-lg-12 col-md-12 col-sm-12 ">
			<div class="row" style="width:100%">
				<div class="col-lg-12 col-md-12 col-sm-12 dash-panel" >
					<div><@sectionFrameHeader title="${uiLabelMap.FindBusinessUnits!}"/></div>
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
								<form action="#" method="post" id="searchForm" name="searchForm" class="form-horizontal" data-toggle="validator">
									<input type="hidden" name="externalLoginKey" id="externalLoginKey" value="${requestAttributes.externalLoginKey?if_exists}" />
									<input type="hidden" name="userId" id="userId" value="${userLogin.userLoginId?if_exists}" />
									<div class="panel-body">
										<@dynaScreen 
											instanceId="FIND_BUSINESS_UNITS"
											modeOfAction="CREATE"
											/>
										<div class="row find-srbottom">
											<div class="col-lg-12 col-md-12 col-sm-12">
												<div class="text-right pd-cbx-lbls" style="padding-top: 0px;">
													<@button 
														label="${uiLabelMap.Search}"
														id="main-search-btn"
														/>
												</div>
											</div>
										</div>
									</div>
								</form>
								<@inputHidden 
									id="buStatusList"
									value="${buStatus?if_exists}"
									/>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>
</div><#-- End accordion-->
<#-- </div>   -->
<div class="clearfix"></div>

	<div class="col-lg-12 col-md-12 col-sm-12 dash-panel">
	<#assign rightContent='<a title="Create" href="/admin-portal/control/createBusinessUnits" class="btn btn-primary btn-xs ml-2"> <i class="fa fa-plus" aria-hidden="true"></i> Create </a>' />
	<#assign rightContent=rightContent+'<span class="btn btn-xs btn-primary" id="remove-btn" data-toggle="confirmation" title="" data-original-title="Are you sure to REMOVE ?"><i class="fa fa-remove" aria-hidden="true"></i> Remove</span>' />
		<#-- <@AgGrid
			gridheadertitle=uiLabelMap.ListOfBusinessUnits
			gridheaderid="businessUnit-grid-action-container"
			savePrefBtn=true
			clearFilterBtn=true
			exportBtn=true
			insertBtn=false
			updateBtn=false
			removeBtn=true
			headerextra=rightContent
			userid="${userLogin.userLoginId}" 
			shownotifications="true" 
			instanceid="ORG_BUSINESS_UNIT" 
			autosizeallcol="true"
			debug="false"
			/> 
		<script type="text/javascript" src="/admin-portal-resource/js/ag-grid/org-Hierarchy/businessUnit.js"></script>    
		-->
		<@fioGrid
			id="business-unit"
			instanceId="ORG_BUSINESS_UNIT"
			jsLoc="/admin-portal-resource/js/ag-grid/org-Hierarchy/businessUnit.js"
			headerLabel="${uiLabelMap.ListOfBusinessUnits}"
			headerExtra=rightContent!
			headerBarClass="grid-header-no-bar"
			headerId="business-unit-tle"
			savePrefBtnId="business-unit-save-pref"
			clearFilterBtnId="business-unit-clear-pref"
			subFltrClearId="business-unit-clear-sub-ftr"
			serversidepaginate=false
			statusBar=false
			exportBtnId="business-unit-list-export-btn"
			savePrefBtn=true
			clearFilterBtn=true
			exportBtn=true
			subFltrClearBtn = true
			/>
</div>
         <#--  <div class="col-lg-12 col-md-12 col-sm-12 dash-panel">
           	
	        <#assign rightContent='<a title="Create" href="/admin-portal/control/createBusinessUnits" class="btn btn-primary btn-xs ml-2"> <i class="fa fa-plus" aria-hidden="true"></i> Create </a>' />   
				            
	      	<@AgGrid
				gridheadertitle=uiLabelMap.ListOfBusinessUnits
				gridheaderid="businessUnit-grid-action-container"
				savePrefBtn=true
				clearFilterBtn=true
				exportBtn=true
				insertBtn=false
				updateBtn=false
				removeBtn=true
				headerextra=rightContent
			    userid="${userLogin.userLoginId}" 
			    shownotifications="true" 
			    instanceid="ORG_BUSINESS_UNIT" 
			    autosizeallcol="true"
			    debug="false"
			    />       
		   	
           	<script type="text/javascript" src="/admin-portal-resource/js/ag-grid/org-Hierarchy/businessUnit.js"></script> 
           	
          </div>-->
          
           	 <#--  
           	 <@AgGrid
					userid="${userLogin.userLoginId}" 
					instanceid="ORG_BUSINESS_UNIT" 
					autosizeallcol="true" 
					gridoptions='{"pagination": true, "paginationPageSize": 10 }'
					/>
            <div class="table-responsive">
	            <div class="loader text-center" id="loader" sytle="display:none;">
	                  <span></span>
	                  <span></span>
	                  <span></span>
	            </div>
	            <div id="grid1" style="width: 100%;" class="ag-theme-balham"></div>
	            <script type="text/javascript" src="/admin-portal-resource/js/ag-grid/org-Hierarchy/businessUnit.js"></script> 
               
            </div>
             -->
             
        </div>
    </div><#-- End main-->
</div><#-- End row-->

<script>
$(".parentBuName-input").one( "click",function(){
	getParentBuName();
});

$(".buName-input").one( "click",function(){
	getBuName();
});

function getParentBuName() {
    var userOptionList = '<option value=""></option>';
    $.ajax({
        type: "GET",
        url:'/admin-portal/control/getParentBuName?externalLoginKey=${requestAttributes.externalLoginKey!}',
        async: false,
        success: function(data) {
        	for (var i = 0; i < data.length; i++) {
                var type = data[i];
                userOptionList += '<option value="'+type.primaryParentGroupId+'">'+type.productStoreGroupName+'</option>';
            }
        }
    });
   $("#parentBuName").html(userOptionList);
}

function getBuName() {
    var userOptionList = '<option value=""></option>';
    $.ajax({
        type: "GET",
        url:'/admin-portal/control/getBuName?externalLoginKey=${requestAttributes.externalLoginKey!}',
        async: false,
        success: function(data) {
        	for (var i = 0; i < data.length; i++) {
                var type = data[i];
                userOptionList += '<option value="'+type.productStoreGroupId+'">'+type.productStoreGroupName+'</option>';
            }
        }
    });
   $("#buName").html(userOptionList);
}

/*
function loadMainGrid(fag) {
		
	$.ajax({
	  url:'/admin-portal/control/getBusinessUnit',
	  type:"POST",
	  data: JSON.parse(JSON.stringify($("#searchForm").serialize())),
	  success: function(data){
			// console.log("data: ", data)
		  fag.setRowData(data)
	  }
	})
	
}
*/
</script>

<script language="JavaScript" type="text/javascript">

	//var buStatusList = "${buStatus?if_exists}";

</script>

    
    