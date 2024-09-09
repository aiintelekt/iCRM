<#assign contextPath = request.getContextPath()/>
<#assign srNumber= request.getParameter("srNumber")! />
<div class="pt-2 align-lists">
	
    <form method="post" action="" id="findInvoices" name="findInvoices" novalidate="novalidate" data-toggle="validator">
		<input type="hidden" name="srNumber" value="${srNumber!}">
		<input type="hidden" name="externalLoginKey" value="${requestAttributes.externalLoginKey!}"/>
	</form>
    
</div>
