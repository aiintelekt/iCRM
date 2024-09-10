<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<style>
body {
  pointer-events:none;
}
</style>
<div class="row">
    <div id="main" role="main" class="pd-btm-title-bar">
        <#assign extraLeft='' />
        <div class="col-lg-12 col-md-12 col-sm-12">

            <#assign addActivities = '' />
            <div class="card-head margin-adj mt-2" id="view-detail">
          	  <div class="col-lg-12 col-md-12 dot-line">
			    <div class="row">
			     <div class="col-lg-6 col-md-6">
			           <h3 class="float-left mr-2 mb-0 header-title view-title">Store Receipt Details</h3>
			         </div>
			     <div class="col-lg-6 col-md-6">
			           <a href="<@ofbizUrl>findStoreReceipts</@ofbizUrl>" class="btn btn-xs btn-primary float-right text-right"><i class="fa fa-chevron-circle-left"></i> Back</a>
			     </div>
			    </div>
			   </div>
			    <div class="row">
			    <div class="col-lg-12 col-md-12 col-sm-12" id="appbar1">
                	<span id="E10000" class="name h2 text-info disabled" title="Store Name"> ${inputContext.storeName!}</span>
                </div>
			    </div>
            </div>
		<@navTab
			instanceId="VIEW_STORE_RECEIPT"
			activeTabId="r-details"
			/>
    </div>
</div>

<script>
	$(document).ready(() => {
		$('body').css('pointer-events', 'all') //activate all pointer-events on body
	});
</script>

