<#if requestAttributes.errorMessageList?has_content><#assign errorMessageList=requestAttributes.errorMessageList></#if>
<#if requestAttributes.eventMessageList?has_content><#assign eventMessageList=requestAttributes.eventMessageList></#if>
<#if requestAttributes.serviceValidationException?exists><#assign serviceValidationException = requestAttributes.serviceValidationException></#if>
<#if requestAttributes.uiLabelMap?has_content><#assign uiLabelMap = requestAttributes.uiLabelMap></#if>

<#if !errorMessage?has_content>
  	<#assign errorMessage = requestAttributes._ERROR_MESSAGE_?if_exists>
</#if>
<#if !errorMessageList?has_content>
  	<#assign errorMessageList = requestAttributes._ERROR_MESSAGE_LIST_?if_exists>
</#if>
<#if !eventMessage?has_content>
  	<#assign eventMessage = requestAttributes._EVENT_MESSAGE_?if_exists>
</#if>
<#if !eventMessageList?has_content>
  	<#assign eventMessageList = requestAttributes._EVENT_MESSAGE_LIST_?if_exists>
</#if>

<div class="container">
	
<ul class="list-unstyled alert-messages">

	<#if errorMessageList?has_content>	
	<ul class="list-unstyled alert-messages">
		<#list errorMessageList as errorMsg>
			<li>
				<div class="alert alert-message alert-danger">
					<a data-dismiss="alert" class="close" href="#">&times;</a>
					<strong>Error!</strong> ${StringUtil.wrapString(errorMsg?if_exists)}
				</div>
			</li>		
		</#list> 
		</ul>	
	</#if>
	
	<#if errorMessage?has_content>
		<ul class="list-unstyled alert-messages">
			<li>
				<div class="alert alert-message alert-danger">
					<a data-dismiss="alert" class="close" href="#">&times;</a>
					<strong>Error!</strong> ${StringUtil.wrapString(errorMessage?if_exists)}
				</div>
			</li>		
		</ul>	
	</#if>
	
</ul>

<#if (eventMessage?has_content || eventMessageList?has_content)>	
	<ul class="list-unstyled alert-messages">
	
		<#if eventMessageList?has_content>	
		<ul class="list-unstyled alert-messages">
			<#list eventMessageList as eventMsg>
				<li>
					<div class="alert alert-message alert-info">
						<a data-dismiss="alert" class="close" href="#">&times;</a>
						<strong>Info!</strong> ${StringUtil.wrapString(eventMsg?if_exists)}
					</div>
				</li>		
			</#list> 
		</ul>	
		</#if>
		
		<#if eventMessage?has_content>
			<ul class="list-unstyled alert-messages">
				<li>
					<div class="alert alert-message alert-info">
						<a data-dismiss="alert" class="close" href="#">&times;</a>
						<strong>Info!</strong> ${StringUtil.wrapString(eventMessage?if_exists)}
					</div>
				</li>		
			</ul>	
		</#if>
		
	</ul>
</#if>

</div>