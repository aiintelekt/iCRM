<#assign logoPath=Static["org.groupfio.common.portal.util.DataUtil"].getLogoFilePath(delegator)?if_exists />
<#macro logo class="img-responsive" alt="logo" width="">
	<img src="${logoPath!}" class="${class!}" alt="${alt!}" width="${width!}" />
</#macro>