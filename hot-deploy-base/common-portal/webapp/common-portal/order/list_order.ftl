<#if isEnableIUCInt?has_content && isEnableIUCInt=="Y">
	<#include "component://common-portal/webapp/common-portal/order/list_order_main.ftl"/>
<#else>
	<#include "component://common-portal/webapp/common-portal/order/list_order_rms.ftl"/>
</#if>