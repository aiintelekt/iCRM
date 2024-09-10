<#include "component://bootstrap/lib/ofbizFormMacros.ftl"/>
      <div class="row">
        <div id="main" role="main">
        <#assign helpUrl = Static["org.groupfio.common.portal.util.DataHelper"].getHelpUrl(delegator, request.getRequestURI()) />  

         
          <div class="col-lg-12 col-md-12 col-sm-12 dash-panel">
          <@sectionFrameHeader title="${uiLabelMap.FindSrSubCategory!}" />
           <div id="">
              <#--  <div class="row">
                <div class="iconek">
                  <div class="arrow-down" style="margin-bottom: 10px;" onclick="this.classList.toggle('active')"></div>
                </div>
              </div>-->
                    <form action="activeSrSubCategory" method="post" id="searchForm" name="searchForm">
                      <@inputHidden 
                        id="searchCriteria"
                      />
                       <div class="row p-2"> 
                        <div class="col-md-4 col-lg-6 col-sm-12">
                         <#assign srCategories = delegator.findByAnd("CustRequestAssoc", {"type" : "SRCategory"}, null, false)>
                           <#assign srCategoryList = Static["org.fio.admin.portal.util.DataHelper"].getDropDownOptions(srCategories, "code","value")?if_exists />
                           <@dropdownCell 
                              id="srCategory"
                              name="srCategory"
                              placeholder="Select SR Category"
                              options=srCategoryList!
                              value="${requestParameters.srCategory?if_exists}"
                              allowEmpty=true
                              />
                       
                        </div>
                        <div class="col-md-4 col-lg-6 col-sm-12">
                        <#assign srSubCategories = delegator.findByAnd("CustRequestAssoc", {"type" : "SRSubCategory"}, null, false)>
                           <#assign srSubCategoryList = Static["org.fio.admin.portal.util.DataHelper"].getDropDownOptions(srSubCategories, "code","value")?if_exists />
                           <@dropdownCell 
                              id="srSubCategory"
                              name="srSubCategory"
                              placeholder="Select SR Sub Category"                             
                              value="${requestParameters.srCategory?if_exists}"
                              allowEmpty=true
                              />
                          </div>
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
	            	     </div>
                        </div>
                     
                  </form>
              </div>
           
            <div class="clearfix"></div>
            <div  style="width:100%">
	
			<div class="col-lg-12 col-md-12 col-sm-12 dash-panel">
            <#assign rightContent='<a title="Create" href="/admin-portal/control/srSubCategory" class="btn btn-primary btn-xs ml-2" target="_blank"> <i class="fa fa-plus" aria-hidden="true"></i> Create </a>' />   
						 		
			<#-- <@AgGrid
			gridheadertitle=uiLabelMap.ListOfSrSubCategory
			gridheaderid="sr-subcategory-grid-action-container"
			savePrefBtn=true
			clearFilterBtn=true
			exportBtn=true
			insertBtn=false
			updateBtn=false
			removeBtn=false
			headerextra=rightContent
			
		    userid="${userLogin.userLoginId}" 
		    shownotifications="true" 
		    instanceid="PARAM_SR_SUB_AREA" 
		    autosizeallcol="true"
		    debug="false"
		    gridoptions='{"pagination": true,"enableBrowserTooltips": true,"filter": true,
		    		"floatingFilter": true,
		    		"domLayout": "autoHeight", "paginationPageSize": 10 }'
		    />  
		    
		    <script type="text/javascript" src="/admin-portal-resource/js/ag-grid/param-unit/sr-subcategory.js"></script>-->	
    <input type="hidden" id="userId" value="${userLogin.userLoginId!}"/>
	<input type="hidden" id="externalLoginKey" value="${requestAttributes.externalLoginKey!}"/>

		<@fioGrid 
			id="ListOfSrSubCategory-Grid"
			instanceId="PARAM_SR_SUB_AREA"
			jsLoc="/admin-portal-resource/js/ag-grid/param-unit/sr-subcategory.js"
			headerLabel=uiLabelMap.ListOfSrSubCategory!
			headerId="sr-subcategory-grid-action-container"
			subFltrClearId="sr-subcategory-sub-filter-clear-btn"
			savePrefBtnId="sr-subcategory-save-pref-btn"
			clearFilterBtnId="sr-subcategory-clear-filter-btn"
			headerBarClass="grid-header-no-bar"
			savePrefBtn=false
			clearFilterBtn=false
			exportBtn=true
			subFltrClearBtn=false
			exportBtnId="sr-subcategory-list-export-btn"			
			headerExtra=rightContent!
			/>                            
          </div>
        </div>
      </div>
  </div>
      </div>    
<script>

$("#srCategory").change(function() {
	   var srCategoryId  = $("#srCategory").val();
	 
	   if (srCategoryId != "") {
	   	   $('.srSubCategory .clear').click();
	       loadSubCategory(srCategoryId);
	   }else{		  
		   $('.srSubCategory .clear').click();
		   $("#srSubCategory").html('');	
			$("#srSubCategory").dropdown('refresh');
	   }
	});
function loadSubCategory(srCategoryId) {
	$('.srSubCategory .clear').click();
    var nonSelectContent = "<span class='nonselect'>Please Select</span>";
    var subCategoryOptions = '<option value="" data-content="'+nonSelectContent+'" selected="">Please Select</option>';
    $.ajax({
        type: "POST",
        url: "getSrSubCategory",
        data: { "srCategory": srCategoryId },
        async: false,
        success: function(data) {
        	var sourceDesc = data.results;
            for (var i = 0; i < data.length; i++) {
            	var category = data[i];            	
                subCategoryOptions += '<option value="'+category.custRequestCategoryId+'">'+category.value+'</option>';
          	}
        }
    });
    $("#srSubCategory").html(subCategoryOptions);
    $("#srSubCategory").dropdown('refresh');
}
</script>