<#include "component://bootstrap/lib/ofbizFormMacros.ftl"/>
<#include "component://admin-portal/webapp/admin-portal/sql-group/item/modal_window.ftl"/>
	
<#assign contextPath = request.getContextPath()/>
<div class="pt-2 align-lists">
	
    <form id="item-search-form" name="item-search-form" method="post">	
    	<input type="hidden" name="orderByColumn" value=""/>
        <input type="hidden" name="externalLoginKey" value="${requestAttributes.externalLoginKey!}"/>
        <input type="hidden" name="userId" value="${userLogin.userLoginId?if_exists}" />
    </form>
    
</div>

<@createItemModal 
instanceId="create-item-modal"
/>

<script>
jQuery(document).ready(function() {
	
$('#create-item-btn').on('click', function() {
	$('#create-item-modal').modal("show");
	$('#add-item-form #description').val('');
});

});

</script>