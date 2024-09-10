<#-- 
<#if layoutSettings.VT_SHORTCUT_ICON?has_content>
	<#assign shortcutIcon = layoutSettings.VT_SHORTCUT_ICON.get(0)/>
<#elseif layoutSettings.shortcutIcon?has_content>
	<#assign shortcutIcon = layoutSettings.shortcutIcon/>
</#if>
<#assign iconForAllScreens=Static["org.groupfio.common.portal.util.DataUtil"].getIconFilePath(delegator)?if_exists />
<#if !iconForAllScreens?has_content && shortcutIcon?has_content>
    <#assign iconForAllScreens = StringUtil.wrapString(shortcutIcon)>
</#if>
<#if iconForAllScreens?has_content>
    <link rel="shortcut icon" href="<@ofbizContentUrl>${iconForAllScreens!}</@ofbizContentUrl>" />
    <link rel="icon" type="image/x-icon" href="<@ofbizContentUrl>${iconForAllScreens!}</@ofbizContentUrl>" />
</#if>
-->
<#assign iconPath=Static["org.groupfio.common.portal.util.DataUtil"].getIconFilePath(delegator)?if_exists />
<#macro icon>
	<link rel="shortcut icon" href="<@ofbizContentUrl>${iconPath!}</@ofbizContentUrl>" />
	<link rel="icon" type="image/x-icon" href="<@ofbizContentUrl>${iconPath!}</@ofbizContentUrl>" />
</#macro>