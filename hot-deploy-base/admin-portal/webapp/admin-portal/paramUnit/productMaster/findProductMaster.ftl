<#include "component://ab-ag-grid-support/webapp/ab-ag-grid-support/lib/ag_grid_header.ftl"/>
<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<div class="row">
    <div id="main" role="main">
       <@sectionFrameHeader title="${uiLabelMap.ProductMasters!}" />
        <div class="col-lg-12 col-md-12 col-sm-12">
            <div class="col-lg-12 col-md-12 col-sm-12">
                <div id="accordion">
                    <div class="row">
                        <div class="iconek">
                          <div class="arrow-down" style="margin-bottom: 10px;" onclick="this.classList.toggle('active')"></div>
                        </div>
                    </div>
                    <div>
                        <div>
                            <div class="border rounded bg-light margin-adj-accordian pad-top">
                                <form action="findProductMaster" method="post" id="searchForm" name="searchForm">
                                    <@inputHidden 
                                        id="searchCriteria"
                                        />
                                    <div class="row p-2">
                                       <div class="col-md-4 col-lg-4 col-sm-12">
                                        <@dropdownCell
                                            name="productCode"
                                            id="productCode"
                                            options=productId
                                            value="${requestParameters.productCode?if_exists}"
                                            allowEmpty=true
                                            placeholder ="${uiLabelMap.ProductCode!}" 
                                            />
                                        </div>
                                        <div class="col-md-4 col-lg-4 col-sm-12">
                                        <@dropdownCell
                                            name="productName"
                                            id="productName"
                                            options=productNameId
                                            value="${requestParameters.productName?if_exists}"
                                            allowEmpty=true
                                            placeholder ="${uiLabelMap.ProductName!}" 
                                            />
                                        </div>
                                        <div class="col-md-4 col-lg-4 col-sm-12">
                                        <@dropdownCell
                                            name="productSubCategory"
                                            id="productSubCategory"
                                            allowEmpty=true
                                            options=categoryNameId
                                            value="${requestParameters.productSubCategory?if_exists}"
                                            placeholder = "${uiLabelMap.ProductSubCategory!}" 
                                            />
                                            <div class="text-right">
                                         <@button 
                                            label="${uiLabelMap.Search}"
                                            id="main-search-btn"
                                            />
                                            </div>
                                        </div>
                                    </div>
                                </form>
                            </div>
                        </div>
                    </div>
                </div>
                
                <div class="page-header border-b pt-2">
			        <@headerH2 title="List of Product Masters" class="float-left"/>
			        <div class="float-right" id="main-grid-action-container">
			        
			        </div>
			        <div class="clearfix"></div>
			    </div>
			    
			    <@AgGrid
					userid="${userLogin.userLoginId}" 
					instanceid="P001" 
					autosizeallcol="false" 
					gridoptions='{"pagination": true, "paginationPageSize": 10 }'
					requestbody='${searchCriteria!}'
					/>
                
            </div>
        </div>
    </div>
</div>

<script>

function loadMainGrid(fag) {
		
	$.ajax({
	  url:'/admin-portal/control/getProductMaster',
	  type:"POST",
	  data: JSON.parse(JSON.stringify($("#searchForm").serialize())),
	  success: function(data){
			// console.log("data: ", data)
		  fag.setRowData(data)
	  }
	})
	
}

/*
function prepareForm(){
    var productCode = $("#productCode").val();
    var productName = $("#productName").val();
    var productSubCategory = $("#productSubCategory").val();
   
    
    item = {}
    item ["productCode"] = productCode;
    item ["productName"] = productName;
    item ["productSubCategory"] = productSubCategory;
   
    jsonString = JSON.stringify(item);
    $("#searchCriteria").val(jsonString);
    $("#searchForm").submit();
    
}
*/
</script>