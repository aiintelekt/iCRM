<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>

<script type="text/javascript" language="javascript" src="/bootstrap/js/bootstrap-datetimepicker.min.js"></script>
<script type="text/javascript" language="javascript" src="/bootstrap/js/screen-render-custom.js"></script>

<#-- 
<#assign tabContent = tabContent!  />
<#if mainScreens?has_content>
	${mainScreens.render(tabContent!)}
<#else>
	${screens.render(tabContent!)}
</#if>
-->

${screens.render(tabContent!)}
<#if externalLoginKey?has_content && !requestAttributes.externalLoginKey?has_content >
	${setRequestAttribute("externalLoginKey", externalLoginKey!)}
	${request.setAttribute("externalLoginKey", externalLoginKey!)}
    ${session.setAttribute("externalLoginKey", externalLoginKey!)}
</#if>
<script>
</script>