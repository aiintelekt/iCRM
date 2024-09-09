<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<#include "component://common-portal/webapp/common-portal/order/modal_window.ftl"/>

<div class="align-lists pt-2">

<form method="post" id="order-search-form" class="form-horizontal" novalidate="novalidate" data-toggle="validator">	
	<input type="hidden" name="domainEntityType" value="${domainEntityType!}"/>
	<input type="hidden" name="domainEntityId" value="${domainEntityId!}"/>
	<input type="hidden" name="externalLoginKey" value="${requestAttributes.externalLoginKey!}"/>	
	<input type="hidden" name="srNumber" value="${srNumber?if_exists}"/>
	<input type="hidden" name="isEnabledEdit" value="${isEnabledOrderEdit?if_exists}"/>
</form>
<#-- 
<div class="col-lg-12 col-md-12 col-sm-12 check-list" style="margin-left:-15px;">

	<h2 class="right-icones"></h2>
	<div class="float-right">
		<form method="post" id="order-search-form" class="form-horizontal" novalidate="novalidate" data-toggle="validator">	
			<input type="hidden" name="domainEntityType" value="${domainEntityType!}"/>
        	<input type="hidden" name="domainEntityId" value="${domainEntityId!}"/>
			<input type="hidden" name="externalLoginKey" value="${requestAttributes.externalLoginKey!}"/>	
			<input type="hidden" name="srNumber" value="${srNumber?if_exists}"/>
		</form>
	</div>
</div>
-->
</div>

<@showDescription 
	instanceId="show-des-modal"
	/>

<script>
jQuery(document).ready(function() {
		 
});
</script>