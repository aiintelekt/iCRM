<script type="text/javascript">

<#if requestAttributes.errorMessageList?has_content><#assign errorMessageList=requestAttributes.errorMessageList></#if>
<#if requestAttributes.eventMessageList?has_content><#assign eventMessageList=requestAttributes.eventMessageList></#if>
<#if !errorMessageList?has_content>
  <#assign errorMessageList = requestAttributes._ERROR_MESSAGE_LIST_?if_exists>
</#if>
<#if !eventMessageList?has_content>
  <#assign eventMessageList = requestAttributes._EVENT_MESSAGE_LIST_?if_exists>
</#if>

<#if errorMessageList?exists>
	<#list errorMessageList as errorMsg>
        showAlert("error", "${errorMsg}");
     </#list>
</#if>
<#if eventMessageList?exists>
	<#list eventMessageList as eventMsg>
        showAlert("success", "${eventMsg}");
     </#list>
</#if>

</script>