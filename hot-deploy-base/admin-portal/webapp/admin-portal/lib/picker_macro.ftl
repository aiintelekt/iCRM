<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<style>
	.popover.confirmation{
	z-index:1000000000!important;
	}
</style>
<#macro productCategoryPicker instanceId>
<div  id="${instanceId!}" class="modal fade bd-example-modal-lg" tabindex="-1" role="dialog" aria-labelledby="myLargeModalLabel" aria-hidden="true">
	<div class="modal-dialog modal-lg" style="max-width: 1200px;">
		<div class="modal-content">
			<div class="modal-header">
				<h4 class="modal-title">Find Category</h4>
				<button type="button" class="close" data-dismiss="modal" id="close">&times;</button>
			</div>
			<div class="modal-body" style="padding-bottom: 8px;">
				<div class="popup-bot">
					<form method="post" name="find-categories-form" action="#" id="search-cat-form" class="form-horizontal" novalidate="novalidate" data-toggle="validator">
						<input type="hidden" name="externalLoginKey" value="${requestAttributes.externalLoginKey!}"/>
						<div class="row">
							<div class="col-lg-12 col-md-12 col-sm-12">
								<@dynaScreen
									instanceId="FIND_CATEGORY"
									modeOfAction="CREATE"
									/>
							</div>
						</div>
						<div class="text-right pd-cbx-lbls" style="padding-top: 0px;">
							<@button
								id="${instanceId!}-search-btn"
								label="${uiLabelMap.Find}"
								/>
							<@reset
								id="${instanceId!}-reset-btn"
								label="${uiLabelMap.Reset}"
								/>
						</div>
					</form>
				</div>
				<#assign rightContent='<button title="Add Categories" class="btn btn-primary btn-xs ml-2" id="add_rule_category"> <i class="fa fa-plus" aria-hidden="true"></i> Add Categories</button>
				<button class="btn btn-primary btn-xs ml-2" id="refresh_rule_category" style="display:none;"> <i class="fa fa-refresh" aria-hidden="true"></i></button>
				'/>
				<@AgGrid
					gridheadertitle="Product Categories"
					gridheaderid="prodCategory-grid-action-container"
					savePrefBtn=false
					clearFilterBtn=true
					exportBtn=true
					insertBtn=false
					updateBtn=false
					removeBtn=false
					headerextra=rightContent
					refreshPrefBtnId="category-refresh-pref-btn"
					savePrefBtnId="category-save-pref-btn"
					clearFilterBtnId="category-clear-filter-btn"
					exportBtnId="category-export-btn"
					userid="${userLogin.userLoginId}"
					shownotifications="true"
					instanceid="PICKER_LIST_OF_CATEGORIES"
					autosizeallcol="true"
					debug="false"
					statusBar=flase
					serversidepaginate=false
					serversidepaginate=false
					statusBar=true
					/>
				<script type="text/javascript" src="/loyalty-portal-resource/js/ag-grid/category/findCategoryList.js"></script>
			</div>
		</div>
	</div>
</div>
<script>
	$(document).ready(function(){
	});
	function refreshCategoryPickerGrid(){
		$("#refresh_rule_category").click();
	}
</script>
</#macro>
<#macro productPicker instanceId>
<div  id="${instanceId!}" class="modal fade bd-example-modal-lg" tabindex="-1" role="dialog" aria-labelledby="myLargeModalLabel" aria-hidden="true">
	<div class="modal-dialog modal-lg" style="max-width: 1200px;">
		<div class="modal-content">
			<div class="modal-header">
				<h4 class="modal-title">Find Products</h4>
				<button type="button" class="close" data-dismiss="modal" id="close">&times;</button>
			</div>
			<div class="modal-body" style="padding-bottom: 8px;">
				<div class="popup-bot">
					<form method="post" name="find-products-form" action="#" id="productSearchForm" class="form-horizontal" novalidate="novalidate" data-toggle="validator">
						<input type="hidden" name="externalLoginKey" value="${requestAttributes.externalLoginKey!}"/>
						<div class="row">
							<div class="col-lg-12 col-md-12 col-sm-12">
								<@dynaScreen
									instanceId="SEARCH_PRODUCT"
									modeOfAction="CREATE"
									/>
							</div>
						</div>
						<div class="text-right pd-cbx-lbls" style="padding-top: 0px;">
							<@button
								id="${instanceId!}-search-btn"
								label="${uiLabelMap.Find}"
								/>
							<@reset
								id="${instanceId!}-reset-btn"
								label="${uiLabelMap.Reset}"
								/>
						</div>
					</form>
				</div>
				<#assign rightContent='<button title="Add Products" class="btn btn-primary btn-xs ml-2" id="add_rule_product"> <i class="fa fa-plus" aria-hidden="true"></i> Add Products</button>
				<button class="btn btn-primary btn-xs ml-2" id="refresh_rule_product" style="display:none;"> <i class="fa fa-refresh" aria-hidden="true"></i></button>
				'/>
				<#-- <@AgGrid
					gridheadertitle="Products"
					gridheaderid="products-grid-action-container"
					savePrefBtn=true
					clearFilterBtn=true
					exportBtn=true
					insertBtn=false
					updateBtn=false
					removeBtn=false
					headerextra=rightContent
					refreshPrefBtnId="products-refresh-pref-btn"
					savePrefBtnId="products-save-pref-btn"
					clearFilterBtnId="products-clear-filter-btn"
					exportBtnId="products-export-btn"
					userid="${userLogin.userLoginId}"
					shownotifications="true"
					instanceid="PICKER_LIST_OF_PRODUCTS"
					autosizeallcol="true"
					debug="false"
					statusBar=true
					serversidepaginate=false
					serversidepaginate=false
					statusBar=true
					/>
				<script type="text/javascript" src="/loyalty-portal-resource/js/ag-grid/product/findProductList.js"></script>-->
	<input type="hidden" id="userId" value="${userLogin.userLoginId}"/>
  	<input type="hidden" id="externalLoginKey" value="${requestAttributes.externalLoginKey!}"/>

	 				<@fioGrid 
						id="productsGrid"
						instanceId="PICKER_LIST_OF_PRODUCTS"
						jsLoc="/loyalty-portal-resource/js/ag-grid/product/findProductList.js"
						headerLabel="Products"
						headerId="products-grid-action-container"
						savePrefBtn=false
						clearFilterBtn=false
						subFltrClearBtn=false
						headerBarClass="grid-header-no-bar"
						clearFilterBtnId="products-clear-filter-btn"
						subFltrClearId="products-sub-filter-clear-btn"
						savePrefBtnId="products-save-pref-btn"
						headerExtra=rightContent!
						exportBtn=true
						exportBtnId="products-export-btn"
						/>
			</div>
		</div>
	</div>
</div>
<script>
	$(document).ready(function(){
	});
	function refreshProductPickerGrid(){
		$("#refresh_rule_product").click();
	}
</script>
</#macro>
<#macro promoRuleCategoriePicker instanceId>
<div  id="${instanceId!}" class="modal fade bd-example-modal-lg" tabindex="-1" role="dialog" aria-labelledby="myLargeModalLabel" aria-hidden="true">
	<div class="modal-dialog modal-lg" style="max-width: 1200px;">
		<div class="modal-content">
			<div class="modal-header">
				<h4 class="modal-title">Promo Category List</h4>
				<button type="button" class="close" data-dismiss="modal" id="close">&times;</button>
			</div>
			<div class="modal-body" style="padding-bottom: 8px;">
				<div class="popup-bot">
					<form method="post" name="list-promo-categories-form" action="#" id="promoCategorySearchForm" class="form-horizontal" novalidate="novalidate" data-toggle="validator">
						<input type="hidden" name="externalLoginKey" value="${requestAttributes.externalLoginKey!}"/>
						<input type="hidden" name="productPromoId" id="${instanceId!}-productPromoId" value="${productPromoId!}"/>
						<input type="hidden" name="productPromoRuleId" id="${instanceId!}-productPromoRuleId" value=""/>
						<input type="hidden" name="productPromoCondSeqId" id="${instanceId!}-productPromoCondSeqId" value=""/>
						<input type="hidden" name="productPromoActionSeqId" id="${instanceId!}-productPromoActionSeqId" value=""/>
						<input type="hidden" name="productCategoryId" id="${instanceId!}-productCategoryId" value=""/>
						<input type="hidden" name="andGroupId" id="${instanceId!}-andGroupId" value=""/>
						<div class="row">
							<div class="col-lg-12 col-md-12 col-sm-12">
								<@dynaScreen
									instanceId="FIND_PROMO_CATEGORY"
									modeOfAction="CREATE"
									/>
							</div>
						</div>
						<div class="text-right pd-cbx-lbls" style="padding-top: 0px;">
							<@button
								id="${instanceId!}-search-btn"
								label="${uiLabelMap.Find}"
								/>
							<@reset
								id="${instanceId!}-reset-btn"
								label="${uiLabelMap.Reset}"
								/>
						</div>
					</form>
				</div>
				<#assign rightContent='<button title="Delete Categories" class="btn btn-primary btn-xs ml-2" id="remove-category-btn" data-toggle="confirmation" data-original-title="Are you sure to delete categories"> <i class="fa fa-times" aria-hidden="true"></i> Delete</button>'/>
				<@AgGrid
					gridheadertitle="Promo Rule Categories List"
					gridheaderid="promocategory-grid-action-container"
					savePrefBtn=false
					clearFilterBtn=true
					exportBtn=true
					insertBtn=false
					updateBtn=false
					removeBtn=false
					headerextra=rightContent
					refreshPrefBtnId="refresh-pref-btn"
					savePrefBtnId="save-pref-btn"
					clearFilterBtnId="clear-filter-btn"
					exportBtnId="export-btn"
					userid="${userLogin.userLoginId}"
					shownotifications="true"
					instanceid="PROMO_RULE_CATEGORYS_LIST"
					autosizeallcol="true"
					debug="false"
					statusBar=true
					serversidepaginate=false
					serversidepaginate=false
					statusBar=true
					/>
				<script type="text/javascript" src="/loyalty-portal-resource/js/ag-grid/category/findPromoCategoryList.js"></script>
			</div>
		</div>
	</div>
</div>
<script>
	$(document).ready(function(){
	});

</script>
</#macro>
<#macro promoRuleProductPicker instanceId>
<div  id="${instanceId!}" class="modal fade bd-example-modal-lg" tabindex="-1" role="dialog" aria-labelledby="myLargeModalLabel" aria-hidden="true">
	<div class="modal-dialog modal-lg" style="max-width: 1200px;">
		<div class="modal-content">
			<div class="modal-header">
				<h4 class="modal-title">Find Products</h4>
				<button type="button" class="close" data-dismiss="modal" id="close">&times;</button>
			</div>
			<div class="modal-body" style="padding-bottom: 8px;">
				<div class="popup-bot">
					<form method="post" name="find-products-form" action="#" id="promoProductSearchForm" class="form-horizontal" novalidate="novalidate" data-toggle="validator">
						<input type="hidden" name="externalLoginKey" value="${requestAttributes.externalLoginKey!}"/>
						<input type="hidden" name="productPromoId" id="${instanceId!}-productPromoId" value="${productPromoId!}"/>
						<input type="hidden" name="productPromoRuleId" id="${instanceId!}-productPromoRuleId" value=""/>
						<input type="hidden" name="productPromoCondSeqId" id="${instanceId!}-productPromoCondSeqId" value=""/>
						<input type="hidden" name="productPromoActionSeqId" id="${instanceId!}-productPromoActionSeqId" value=""/>
						<input type="hidden" name="productId" id="${instanceId!}-productId" value=""/>
						<div class="row">
							<div class="col-lg-12 col-md-12 col-sm-12">
								<@dynaScreen
									instanceId="FIND_PROMO_PRODUCT"
									modeOfAction="CREATE"
									/>
							</div>
						</div>
						<div class="text-right pd-cbx-lbls" style="padding-top: 0px;">
							<@button
								id="${instanceId!}-search-btn"
								label="${uiLabelMap.Find}"
								/>
							<@reset
								id="${instanceId!}-reset-btn"
								label="${uiLabelMap.Reset}"
								/>
						</div>
					</form>
				</div>
				<#assign rightContent='<button title="Delete Products" class="btn btn-primary btn-xs ml-2" id="remove-product-btn" data-toggle="confirmation" data-original-title="Are you sure to delete products"> <i class="fa fa-times" aria-hidden="true"></i> Delete</button>'/>
				<@AgGrid
					gridheadertitle="Promo Rule Products List"
					gridheaderid="products-grid-action-container"
					savePrefBtn=false
					clearFilterBtn=true
					exportBtn=true
					insertBtn=false
					updateBtn=false
					removeBtn=false
					headerextra=rightContent
					refreshPrefBtnId="product-refresh-pref-btn"
					savePrefBtnId="product-save-pref-btn"
					clearFilterBtnId="product-clear-filter-btn"
					exportBtnId="product-export-btn"
					userid="${userLogin.userLoginId}"
					shownotifications="true"
					instanceid="PROMO_RULE_PRODUCTS_LIST"
					autosizeallcol="true"
					debug="false"
					statusBar=true
					serversidepaginate=false
					serversidepaginate=false
					statusBar=true
					/>
				<script type="text/javascript" src="/loyalty-portal-resource/js/ag-grid/product/findPromoProductList.js"></script>
			</div>
		</div>
	</div>
</div>
<script>
	$(document).ready(function(){
	});
</script>
</#macro>
<#-- Store Picker -->
<#macro storePicker instanceId fromAction="">
<div  id="${instanceId!}" class="modal fade bd-example-modal-lg" tabindex="-1" role="dialog" aria-labelledby="myLargeModalLabel" aria-hidden="true" style="z-index: 99999;">
	<div class="modal-dialog modal-lg" style="max-width: 1200px;">
		<div class="modal-content" id="mainFrom">
			<div class="modal-header">
				<h1 class="modal-title">${uiLabelMap.FindProductStore!}</h1>
				<button type="button" class="close" data-dismiss="modal" id="close">&times;</button>
			</div>
			<div class="modal-body">
				<form method="post" id="findProductStoreForm" class="form-horizontal" novalidate="novalidate" data-toggle="validator">
					<input type="hidden" name="externalLoginKey" value="${requestAttributes.externalLoginKey!}"/>
					<#if isShowRoleFilter?has_content && isShowRoleFilter=="N" && roleTypeFilter?has_content>
					<input type="hidden" name="roleTypeId" value="${roleTypeFilter!}">
					</#if>
					<div class="row padding-r"">
						<div class="col-md-6 col-sm-6 form-horizontal">
							<@inputAutoComplete
								id="searchProductStoreKey"
								label="Store Name/ID"
								isAutoCompleteEnable="Y"
								onkeydown=true
								autoCompleteMinLength=0
								placeholder="Store Name/ID"
								autoCompleteLabelFieldId="storeIdName"
								autoCompleteValFieldId="productStoreId"
								autoCompleteFormId="findProductStoreForm"
								autoCompleteUrl="/loyalty-portal/control/findStoreAjax"
								/>
						</div>
						<div class="text-right pd-cbx-lbls" style="padding-top: 0px;">
							<@button
								label="${uiLabelMap.Find}"
								id="storePicker-search-btn"
								/>
							<@reset
								label="${uiLabelMap.Reset}"
								id="store-reset"
								onclick="clearStorePicker()"
								/>
						</div>
					</div>
				</form>
				<div class="clearfix"></div>
				<div class="row padding-r">
					<div class="col-md-12 col-sm-12">
						<#assign rightContent='<button title="Add Stores" class="btn btn-primary btn-xs ml-2" id="add_promo_store"> <i class="fa fa-plus" aria-hidden="true"></i> Add Stores</button>'/>
						<#-- <@AgGrid
							gridheadertitle=uiLabelMap.ListOfStores
							gridheaderid="${instanceId!}_store-grid-action-container"
							savePrefBtn=true
							clearFilterBtn=true
							exportBtn=false
							insertBtn=false
							updateBtn=false
							removeBtn=false
							refreshPrefBtnId="storePicker-refresh-pref-btn"
							savePrefBtnId="storePicker-save-pref-btn"
							clearFilterBtnId="storePicker-clear-filter-btn"
							subFltrClearId="storePicker-sub-filter-clear-btn"
							userid="${userLogin.userLoginId}"
							shownotifications="true"
							headerextra=rightContent
							instanceid="PICKER_STORE_LIST"
							autosizeallcol="true"
							debug="false"
							/>
						<script type="text/javascript" src="/loyalty-portal-resource/js/ag-grid/picker/findStore.js"></script>-->
	  <input type="hidden" id="userId" value="${userLogin.userLoginId!}"/>
	  <input type="hidden" id="externalLoginKey" value="${requestAttributes.externalLoginKey!}"/>

        					<@fioGrid 
								id="storePickerGrid"
								instanceId="PICKER_STORE_LIST"
								jsLoc="/loyalty-portal-resource/js/ag-grid/picker/findStore.js"
								headerLabel=uiLabelMap.ListOfStores
								headerId="${instanceId!}_store-grid-action-container"
								savePrefBtnId="storePicker-save-pref-btn"
								clearFilterBtnId="storePicker-clear-filter-btn"
								headerBarClass="grid-header-no-bar"
								savePrefBtn=false
								clearFilterBtn=false
								exportBtn=false
								subFltrClearBtn=false
								subFltrClearId="storePicker-sub-filter-clear-btn"
								exportBtnId="storePicker-export-btn"
								headerExtra=rightContent!
								/>
						<div class="clearfix"></div>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>
<script>
$(document).ready(function() {

$('#${instanceId!}').on('shown.bs.modal', function (e) {
	$("#storePicker-search-btn").trigger('click');
});

});
</script>
</#macro>
<script>
	function clearStorePicker(){
		$('#searchProductStoreKey_val').val("");
		$('#searchProductStoreKey_alter').val("");
	}
$(document).ready(function(){
	$("#searchProductStoreKey_desc").keyup(function(){
		if ($("#searchProductStoreKey_val").val() != ""){
			$('#searchProductStoreKey_val').val("");
		} else {
			$('#searchProductStoreKey_val').val("");
		}
	});
});
</script>
<#-- Store Picker Single -->
<#macro productStorePicker instanceId fromAction="">
<div  id="${instanceId!}" class="modal fade bd-example-modal-lg" tabindex="-1" role="dialog" aria-labelledby="myLargeModalLabel" aria-hidden="true" style="z-index: 99999;">
	<div class="modal-dialog modal-lg" style="max-width: 1200px;">
		<div class="modal-content" id="mainFrom">
			<div class="modal-header">
				<h1 class="modal-title">${uiLabelMap.FindProductStore!}</h1>
				<button type="button" class="close" data-dismiss="modal" id="close">&times;</button>
			</div>
			<div class="modal-body">
				<form method="post" id="findProductStoreForm" class="form-horizontal" novalidate="novalidate" data-toggle="validator">
					<input type="hidden" name="externalLoginKey" value="${requestAttributes.externalLoginKey!}"/>
					<div class="row padding-r"">
						<div class="col-md-6 col-sm-6 form-horizontal">
							<@inputAutoComplete
								id="searchProductStoreKey"
								label="Store Name/ID"
								isAutoCompleteEnable="Y"
								onkeydown=true
								autoCompleteMinLength=0
								placeholder="Store Name/ID"
								autoCompleteLabelFieldId="storeIdName"
								autoCompleteValFieldId="productStoreId"
								autoCompleteFormId="findProductStoreForm"
								autoCompleteUrl="/loyalty-portal/control/findStoreAjax"
								/>
						</div>
						<div class="text-right pd-cbx-lbls" style="padding-top: 0px;">
							<@button
								label="${uiLabelMap.Find}"
								id="storePicker-search-btn"
								/>
							<@reset
								label="${uiLabelMap.Reset}"
								id="reset-product-store"
								onclick="clearProductStorePicker()"
								/>
						</div>
					</div>
				</form>
				<div class="clearfix"></div>
				<div class="row padding-r">
					<div class="col-md-12 col-sm-12">
						<#assign rightContent='<button title="Add Stores" class="btn btn-primary btn-xs ml-2" id="add_promo_store"> <i class="fa fa-plus" aria-hidden="true"></i> Add Stores</button>'/>
						<#-- <@AgGrid
							gridheadertitle=uiLabelMap.ListOfStores
							gridheaderid="${instanceId!}_store-grid-action-container"
							savePrefBtn=true
							clearFilterBtn=true
							exportBtn=false
							insertBtn=false
							updateBtn=false
							removeBtn=false
							refreshPrefBtnId="storePicker-refresh-pref-btn"
							savePrefBtnId="storePicker-save-pref-btn"
							clearFilterBtnId="storePicker-clear-filter-btn"
							subFltrClearId="storePicker-sub-filter-clear-btn"
							userid="${userLogin.userLoginId}"
							shownotifications="true"
							headerextra=rightContent
							instanceid="PICKER_PRODUCT_STORE"
							autosizeallcol="true"
							debug="false"
							/>
						<script type="text/javascript" src="/loyalty-portal-resource/js/ag-grid/picker/findProductStore.js"></script>-->
	<input type="hidden" id="userId" value="${userLogin.userLoginId}"/>
  	<input type="hidden" id="externalLoginKey" value="${requestAttributes.externalLoginKey!}"/>

	 				<@fioGrid 
						id="ListOfStoresGrid"
						instanceId="PICKER_PRODUCT_STORE"
						jsLoc="/loyalty-portal-resource/js/ag-grid/picker/findProductStore.js"
						headerLabel=uiLabelMap.ListOfStores
						headerId="ListOfStores-grid-action-container"
						savePrefBtn=false
						clearFilterBtn=false
						subFltrClearBtn=false
						exportBtn=false
						headerBarClass="grid-header-no-bar"
						clearFilterBtnId="storePicker-clear-pref-btn"
						subFltrClearId="storePicker-sub-filter-clear-btn"
						savePrefBtnId="storePicker-save-filter-btn"
						headerExtra=rightContent!
						/>
						<div class="clearfix"></div>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>
<script>
$(document).ready(function() {

$('#${instanceId!}').on('shown.bs.modal', function (e) {
	$("#storePicker-search-btn").trigger('click');
});

});
</script>
</#macro>
<script>
	function clearProductStorePicker(){
		$('#searchProductStoreKey_val').val("");
		$('#searchProductStoreKey_alter').val("");
	}
$(document).ready(function(){
	$("#searchProductStoreKey_desc").keyup(function(){
		if ($("#searchProductStoreKey_val").val() != ""){
			$('#searchProductStoreKey_val').val("");
		} else {
			$('#searchProductStoreKey_val').val("");
		}
	});
});
</script>
<#-- Store group Picker -->
<#macro storeGroupPicker instanceId fromAction="">
<div  id="${instanceId!}" class="modal fade bd-example-modal-lg" tabindex="-1" role="dialog" aria-labelledby="myLargeModalLabel" aria-hidden="true" style="z-index: 99999;">
	<div class="modal-dialog modal-lg" style="max-width: 1200px;">
		<div class="modal-content" id="mainFrom">
			<div class="modal-header">
				<h1 class="modal-title">${uiLabelMap.FindProductStoreGroup!}</h1>
				<button type="button" class="close" data-dismiss="modal" id="close">&times;</button>
			</div>
			<div class="modal-body">
				<form method="post" id="findProductStoreGroupForm" class="form-horizontal" novalidate="novalidate" data-toggle="validator">
					<input type="hidden" name="externalLoginKey" value="${requestAttributes.externalLoginKey!}"/>
					<div class="row">
						<div class="col-md-6 col-sm-6 form-horizontal">
							<@inputAutoComplete
								id="searchStoreGroupKey"
								label="Store Group Name/ID"
								isAutoCompleteEnable="Y"
								onkeydown=true
								autoCompleteMinLength=0
								placeholder="Store Group Name/ID"
								autoCompleteLabelFieldId="productStoreGroupIdName"
								autoCompleteValFieldId="productStoreGroupId"
								autoCompleteFormId="findProductStoreGroupForm"
								autoCompleteUrl="/loyalty-portal/control/findStoreGroupAjax"
								/>
						</div>
						<div class="text-right pd-cbx-lbls" style="padding-top: 0px;">
							<@button
								label="${uiLabelMap.Find}"
								id="storeGroupPicker-search-btn"
								/>
							<@reset
								label="${uiLabelMap.Reset}"
								id="reset-store-group"
								onclick="clearStoreGroupPicker()"
								/>
						</div>
					</div>
				</form>
				<div class="clearfix"></div>
				<div class="row padding-r"></div>
				<div class="row padding-r">
					<div class="col-md-12 col-sm-12">
						<#assign rightContent='<button title="Add Store Group" class="btn btn-primary btn-xs ml-2" id="add_promo_store_group"> <i class="fa fa-plus" aria-hidden="true"></i> Add Store Group</button>'/>
						<#-- <@AgGrid
							gridheadertitle=uiLabelMap.ListOfStoreGroup
							gridheaderid="${instanceId!}_storeGroup-grid-action-container"
							savePrefBtn=true
							clearFilterBtn=true
							exportBtn=false
							insertBtn=false
							updateBtn=false
							removeBtn=false
							refreshPrefBtnId="storeGroupPicker-refresh-pref-btn"
							savePrefBtnId="storeGroupPicker-save-pref-btn"
							clearFilterBtnId="storeGroupPicker-clear-filter-btn"
							subFltrClearId="storeGroupPicker-sub-filter-clear-btn"
							userid="${userLogin.userLoginId}"
							headerextra=rightContent
							shownotifications="true"
							instanceid="PICKER_STORE_GROUP_LIST"
							autosizeallcol="true"
							debug="false"
							/>
						<script type="text/javascript" src="/loyalty-portal-resource/js/ag-grid/picker/findStoreGroup.js"></script>-->
	  <input type="hidden" id="userId" value="${userLogin.userLoginId!}"/>
	  <input type="hidden" id="externalLoginKey" value="${requestAttributes.externalLoginKey!}"/>

        					<@fioGrid 
								id="ListOfStoreGroupGrid"
								instanceId="PICKER_STORE_GROUP_LIST"
								jsLoc="/loyalty-portal-resource/js/ag-grid/picker/findStoreGroup.js"
								headerLabel=uiLabelMap.ListOfStoreGroup
								headerId="storeGroupPicker-grid-action-container"
								savePrefBtnId="storeGroupPicker-save-pref-btn"
								clearFilterBtnId="storeGroupPicker-clear-filter-btn"
								headerBarClass="grid-header-no-bar"
								clearFilterBtn=false
								exportBtn=false
								subFltrClearBtn=false
								subFltrClearId="storeGroupPicker-sub-filter-clear-btn"
								exportBtnId="storeGroupPicker-export-btn"
								headerExtra=rightContent!
								/>
						<div class="clearfix"></div>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>
</#macro>
<script>
	function clearStoreGroupPicker() {
		$('#searchStoreGroupKey_val').val("");
		$('#searchStoreGroupKey_alter').val("");
	}
$(document).ready(function() {
	$("#searchStoreGroupKey_desc").keyup(function() {
		if ($("#searchStoreGroupKey_val").val() != "") {
			$('#searchStoreGroupKey_val').val("");
		} else {
			$('#searchStoreGroupKey_val').val("");
		}
	});
});
</script>
<#-- Store group Picker Single -->
<#macro productStoreGroupPicker instanceId fromAction="">
<div  id="${instanceId!}" class="modal fade bd-example-modal-lg" tabindex="-1" role="dialog" aria-labelledby="myLargeModalLabel" aria-hidden="true" style="z-index: 99999;">
	<div class="modal-dialog modal-lg" style="max-width: 1200px;">
		<div class="modal-content" id="mainFrom">
			<div class="modal-header">
				<h1 class="modal-title">${uiLabelMap.FindProductStoreGroup!}</h1>
				<button type="button" class="close" data-dismiss="modal" id="close">&times;</button>
			</div>
			<div class="modal-body">
				<form method="post" id="findProductStoreGroupPickerForm" class="form-horizontal" novalidate="novalidate" data-toggle="validator">
					<input type="hidden" name="externalLoginKey" value="${requestAttributes.externalLoginKey!}"/>
					<input type="hidden" name="userId" value="${userLogin.userLoginId?if_exists}" />
					<div class="row">
						<div class="col-md-6 col-sm-6 form-horizontal">
							<@inputAutoComplete
								id="searchStoreGroupKey"
								label="Store Group Name/ID"
								isAutoCompleteEnable="Y"
								onkeydown=true
								autoCompleteMinLength=0
								placeholder="Store Group Name/ID"
								autoCompleteLabelFieldId="productStoreGroupIdName"
								autoCompleteValFieldId="productStoreGroupId"
								autoCompleteFormId="findProductStoreGroupPickerForm"
								autoCompleteUrl="/loyalty-portal/control/findStoreGroupAjax"
								/>
						</div>
						<div class="text-right pd-cbx-lbls" style="padding-top: 0px;">
							<@button
								label="${uiLabelMap.Find}"
								id="psg-search-btn"
								/>
							<@reset
								label="${uiLabelMap.Reset}"
								id="reset-product-store-group"
								onclick="clearProductStoreGroupPicker()"
								/>
						</div>
					</div>
				</form>
				<div class="clearfix"></div>
				<div class="row padding-r"></div>
				<div class="row padding-r">
					<div class="col-md-12 col-sm-12">
						<#-- <@AgGrid
							gridheadertitle=uiLabelMap.ListOfStoreGroup
							gridheaderid="${instanceId!}_storeGroup-grid-action-container"
							savePrefBtn=true
							clearFilterBtn=true
							exportBtn=false
							insertBtn=false
							updateBtn=false
							removeBtn=false
							refreshPrefBtnId="psg-refresh-pref-btn"
							savePrefBtnId="psg-save-pref-btn"
							clearFilterBtnId="psg-clear-filter-btn"
							subFltrClearId="psg-sub-filter-clear-btn"
							userid="${userLogin.userLoginId}"
							shownotifications="true"
							instanceid="PICKER_STORE_GROUP"
							autosizeallcol="true"
							debug="false"
							/>
						<script type="text/javascript" src="/loyalty-portal-resource/js/ag-grid/picker/findProductStoreGroup.js"></script>
						-->
						<@fioGrid
							id="picker-product-store-group"
							instanceId="PICKER_STORE_GROUP"
							jsLoc="/loyalty-portal-resource/js/ag-grid/picker/findProductStoreGroup.js"
							headerLabel=uiLabelMap.ListOfStoreGroup
							headerExtra=rightContent!
							headerBarClass="grid-header-no-bar"
							headerId="picker-product-store-group-tle"
							savePrefBtnId="picker-product-store-group-save-pref"
							clearFilterBtnId="picker-product-store-group-clear-pref"
							subFltrClearId="picker-product-store-group-clear-sub-ftr"
							exportBtnId="picker-product-store-group-export-btn"
							serversidepaginate=false
							statusBar=false
							savePrefBtn=false
							clearFilterBtn=false
							subFltrClearBtn=false
							exportBtn=false
							/>

						<div class="clearfix"></div>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>
</#macro>
<script>
	function clearProductStoreGroupPicker(){
		$('#searchStoreGroupKey_val').val("");
		$('#searchStoreGroupKey_alter').val("");
	}
$(document).ready(function(){
	$("#searchStoreGroupKey_desc").keyup(function(){
		if ($("#searchStoreGroupKey_val").val() != ""){
			$('#searchStoreGroupKey_val').val("");
		} else {
			$('#searchStoreGroupKey_val').val("");
		}
	});
});
</script>
<#macro promoCampaignPicker instanceId fromAction="" isShowRoleFilter="Y" roleTypeFilter="" isShowPartyLevelFilter="">
<div  id="${instanceId!}" class="modal fade bd-example-modal-lg" tabindex="-1" role="dialog" aria-labelledby="myLargeModalLabel" aria-hidden="true">
	<div class="modal-dialog modal-lg" style="max-width: 1200px;">
		<div class="modal-content">
			<div class="modal-header">
				<h4 class="modal-title">Find Promo Campaign</h4>
				<button type="button" class="close" data-dismiss="modal" id="close">&times;</button>
			</div>
			<div class="modal-body" style="padding-bottom: 8px;">
				<div class="popup-bot">
					<form method="post" name="find-pc-form" id="find-pc-form" class="form-horizontal" novalidate="novalidate" data-toggle="validator">
						<input type="hidden" name="externalLoginKey" value="${requestAttributes.externalLoginKey!}"/>
						<input type="hidden" name="productPromoId" value="${productPromoId!}">
						<div class="row">
							<div class="col-lg-12 col-md-12 col-sm-12">
								<@dynaScreen
									instanceId="FIND_PROMO_CAMPAIGN"
									modeOfAction="CREATE"
									/>
							</div>
						</div>
						<div class="text-right pd-cbx-lbls" style="padding-top: 0px;">
							<@button
								id="${instanceId!}-search-btn"
								label="${uiLabelMap.Find}"
								/>
							<@reset
								id="${instanceId!}-reset-btn"
								label="${uiLabelMap.Reset}"
								/>
						</div>
					</form>
				</div>
				<#-- <@AgGrid
					gridheadertitle="Find Promo Campaign"
					gridheaderid="pc-grid-action-container"
					savePrefBtn=true
					clearFilterBtn=true
					exportBtn=true
					insertBtn=false
					updateBtn=false
					removeBtn=false
					refreshPrefBtnId="pc-refresh-pref-btn"
					savePrefBtnId="pc-save-pref-btn"
					clearFilterBtnId="pc-clear-filter-btn"
					exportBtnId="pc-export-btn"
					userid="${userLogin.userLoginId}"
					shownotifications="true"
					instanceid="LIST_PROMO_CAMPAIGN"
					autosizeallcol="true"
					debug="false"
					statusBar=true
					serversidepaginate=false
					serversidepaginate=false
					statusBar=true
					/>
				<script type="text/javascript" src="/loyalty-portal-resource/js/ag-grid/promoCampaign/find-promo-campaign.js"></script>-->
	<input type="hidden" id="userId" value="${userLogin.userLoginId}"/>
  	<input type="hidden" id="externalLoginKey" value="${requestAttributes.externalLoginKey!}"/>

	 				<@fioGrid 
						id="pc-list-grid"
						instanceId="LIST_PROMO_CAMPAIGN"
						jsLoc="/loyalty-portal-resource/js/ag-grid/promoCampaign/find-promo-campaign.js"
						headerLabel="Find Promo Campaign"
						headerId="pc-list-grid-action-container"
						savePrefBtn=false
						clearFilterBtn=false
						subFltrClearBtn=false
						headerBarClass="grid-header-no-bar"
						subFltrClearId="pc-list-sub-filter-clear-btn"
						savePrefBtnId="pc-list-save-pref-btn"
                   		clearFilterBtnId="pc-list-clear-filter-btn"
                    	exportBtnId="pc-list-export-btn"
						exportBtn=true
						/>
			</div>
		</div>
	</div>
</div>
<script></script>
</#macro>
<#macro categoryTreePicker instanceId>
<div  id="${instanceId!}" class="modal fade bd-example-modal-lg" tabindex="-1" role="dialog" aria-labelledby="myLargeModalLabel" aria-hidden="true">
	<div class="modal-dialog modal-lg" style="max-width: 1200px;">
		<div class="modal-content">
			<div class="modal-header">
				<h4 class="modal-title">Find Category</h4>
				<button type="button" class="close" data-dismiss="modal" id="close">&times;</button>
			</div>
			<div class="modal-body" style="padding-bottom: 8px;">
				<div class="popup-bot">
					<form method="post" id="findCategoryForm" class="form-horizontal" novalidate="novalidate" data-toggle="validator">
						<input type="hidden" name="domainEntityType" value="${domainEntityType!}">
						<input type="hidden" name="externalLoginKey" value="${requestAttributes.externalLoginKey!}"/>
						<div class="row">
							<div class="col-md-4 col-md-4 form-horizontal">
								<@dropdownCell
									id="${instanceId!}_catalogId"
									name="${instanceId!}_searchCatalogId"
									required=true
									allowEmpty=false
									tooltip = ""
									placeholder = "Select Catalog Id"
									dataLiveSearch=true
									options=prodCatalogs!
									label="Catalog Id"
									/>
							</div>
							<div class="text-right pd-cbx-lbls" style="padding-top: 0px;">
							</div>
						</div>
					</form>
				</div>
				<hr>
				</hr>
				<div id="tree_div">
					<div id="tree">
						Select Catalog
					</div>
				</div>
			</div>
		</div>
	</div>
</div>
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/jstree/3.2.1/themes/default/style.min.css" /> 
<script src="/loyalty-portal-resource/js/jstree/3.2.1/jstree.min.js"></script>
<script type="text/javascript">
	$(document).ready(function(){
		
		$("#${instanceId!}_catalogId").change(function(){
			$("#tree_div").empty().append("<div id ='tree'>Select Catalog</div>");
			if ($(this).val()){
				//loadCatalogCategoryTree();
				loadTree();
			}
			
		});
	});
	function callDocument(){
		$('#tree').on("select_node.jstree", function (e, data){
				setPickerWindowValue(data.node.text, data.node.id);
		});
	}
	
	function loadTree(){
	$("#tree_div").empty().append("<div id ='tree'>Select Catalog</div>");
	let catalogId = $("#${instanceId!}_catalogId").val();
	//AJAX
	$('#tree').jstree({
		'core':{
			'data':{
				//'url':'<@ofbizUrl>getCatalogCategories</@ofbizUrl>',
				'url':function (node) {
					return node.id === '#' ?
					'<@ofbizUrl>getCatalogCategories?type=Catalog&catalogId='+catalogId+'</@ofbizUrl>':
					'<@ofbizUrl>getChildCategoryTree?type=Category&catalogId='+catalogId+'</@ofbizUrl>';
				},
				'data':function (node){
				console.log("J--"+JSON.stringify(node));
					return { 'id' : node.id };
				}
			}
			}
		});
		callDocument();
	}
</script>
</#macro>
<#macro loyaltyPartyPicker instanceId fromAction="">

<div  id="${instanceId!}" class="modal fade bd-example-modal-lg" tabindex="-1" role="dialog" aria-labelledby="myLargeModalLabel" aria-hidden="true">
	<div class="modal-dialog modal-lg" style="max-width: 1200px;">
		<div class="modal-content">
			<div class="modal-header">
                <h4 class="modal-title">${uiLabelMap.findParty!}</h4>
                <button type="button" class="close" data-dismiss="modal" id="close">&times;</button>
            </div>         
            <div class="modal-body" style="padding-bottom: 8px;">
            	<div class="popup-bot">
            		<form method="post" id="findPartyForm" class="form-horizontal" novalidate="novalidate" data-toggle="validator">
            			<input type="hidden" name="domainEntityType" value="${domainEntityType!}">
            			<input type="hidden" name="externalLoginKey" value="${requestAttributes.externalLoginKey!}"/>
						<div class="row">
						<div class="col-lg-12 col-md-12 col-sm-12">
							<@dynaScreen 
								instanceId="FIND_CUSTOMER_LYT"
								modeOfAction="CREATE"
							/>
					   </div>
					</div>
					<div class="row">  
                    	 <div class="col-md-12 col-md-12 form-horizontal text-right">
                    	 	<div class="search-btn">
	                           <@button 
				            		label="${uiLabelMap.Find}"
				            		id="partyPicker-search-btn"
				            	/>
				           	 </div>	
                    	 </div>
                    </div>
				</form>
			</div>
			<#assign rightContent=''/>
			<@AgGrid
				gridheadertitle=uiLabelMap.ListOfPartys
				gridheaderid="${instanceId!}_party-grid-action-container"
				savePrefBtn=true
				clearFilterBtn=true
				exportBtn=false
				insertBtn=false
				updateBtn=false
				removeBtn=false
				headerextra=rightContent!				
				refreshPrefBtnId="partyPicker-refresh-pref-btn"
				savePrefBtnId="partyPicker-save-pref-btn"
				clearFilterBtnId="partyPicker-clear-filter-btn"	
				subFltrClearId="partyPicker-sub-filter-clear-btn"
			    userid="${userLogin.userLoginId}" 
			    shownotifications="true" 
			    instanceid="PICKER_PARTY_LIST_LYT" 
			    autosizeallcol="true"
			    debug="false"
			    />    
			         
			<script type="text/javascript" src="/loyalty-portal-resource/js/ag-grid/picker/find-loyalty-party.js"></script>
			
      	</div>
    	</div>
  	</div>
</div>

<script>
$(document).ready(function() {
});
</script>

</#macro>

<#macro findTemplatePicker instanceId templateCategoryId="" fromAction="">
<div id="${instanceId!}" class="modal fade bd-example-modal-lg" tabindex="-1" role="dialog" aria-labelledby="myLargeModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-lg" style="max-width: 1200px;">
        <!-- Modal content-->
        <div class="modal-content" id="searchTemplate">
            <div class="modal-header">
                <@headerH4 title="${uiLabelMap.findTemplates}" class="modal-title">${uiLabelMap.findTemplates}</@headerH4>
                <button type="button" class="close" data-dismiss="modal" id="close">&times;</button>
            </div>
            <div class="modal-body">
            	<input type="hidden" name="temp_picker_instance" id="temp_picker_instance" value="${instanceId!}" />
            	<form method="post" id="${instanceId!'find'}_Form" name="${instanceId!'find'}_Form" action="" class="form-horizontal" novalidate="novalidate" data-toggle="validator">
            	<input type="hidden" name="templateCategories" value="${templateCategoryId!}"/>
            	<input type="hidden" name="externalLoginKey" value="${requestAttributes.externalLoginKey!}"/>
                <div class="row">
                    <div class="col-md-4 col-sm-4">
                        <div class="form-group row mr">
                            <@inputCell 
	                            id="tempalateName"
	                            inputColSize="col-sm-12"
	                            value=tempName!
	                            placeholder="Templates Name"
	                            required=false
	                            />
                        </div>
                    </div>
                    <div class="col-md-3 col-sm-2">
                        <div class="form-group">
                            <@dropdownCell 
	                            id = "emailEngine"
	                            options = emailEngineTypeList
	                            value=""
	                            placeholder="Email Engine"
	                            dataLiveSearch = true
	                             required=false
	                             allowEmpty=true
	                            />
                        </div>
                        
                    </div>
                    <div class="col-md-3 col-sm-3">
                    	<@button 
		            		id="find-temp-search-btn" 
                        	label="${uiLabelMap.Find}"
		            		/>
		           		<@reset
		        			label="${uiLabelMap.Reset}"
		        			id="temp-reset-btn" 
		        			/>
                    </div>
                </div>
                </form>
                <br>
                <div class="clearfix"></div>
               <#-- <@AgGrid
					gridheadertitle="List of Templates"
					gridheaderid="${instanceId!}_campaign-grid-action-container"
					savePrefBtn=true
					clearFilterBtn=true
					exportBtn=false
					insertBtn=false
					updateBtn=false
					removeBtn=false
					refreshPrefBtnId="template-refresh-pref-btn"
					savePrefBtnId ="template-save-pref-btn"
					clearFilterBtnId ="template-clear-filter-btn"
					exportBtnId ="template-export-btn"
										
				    userid="${userLogin.userLoginId}" 
				    shownotifications="true" 
				    instanceid="TEMPLATE_PICKER_LIST" 
				    autosizeallcol="true"
				    debug="false"
				    />  -->
<input type="hidden" id="userId" value="${userLogin.userLoginId!}"/>
<input type="hidden" id="externalLoginKey" value="${requestAttributes.externalLoginKey!}"/>

		<@fioGrid 
			id="ListOfTemplates"
			instanceId="TEMPLATE_PICKER_LIST"
			jsLoc="/common-portal-resource/js/ag-grid/picker/find-template.js"
			headerLabel="List of Templates"
			headerId="${instanceId!}_campaign-grid-action-container"
			savePrefBtn=false
			clearFilterBtn=false
			exportBtn=false
			subFltrClearBtn = false
			exportBtnId="template-list-export-btn"
			headerBarClass="grid-header-no-bar"
			savePrefBtnId ="template-save-pref-btn"
			clearFilterBtnId ="template-clear-filter-btn"
			subFltrClearId="template-sub-filter-clear-btn"
			/>
				 <script>
				 
				 	$('#${instanceId!}').on('shown.bs.modal', function (e) {
						$('#temp_picker_instance').val('${instanceId!}');
						$('#find-temp-search-btn').trigger('click');
					});
				 
				 </script>
                 <#-- <script type="text/javascript" src="/common-portal-resource/js/ag-grid/picker/find-template.js"></script> -->
                <div class="clearfix"></div>
                <span id="find_temp_trigger" ></span>
            </div>
        </div>
    </div>
</div>
</#macro>