<#assign contextPath = request.getContextPath()/>
<#assign partyId= request.getParameter("partyId")! />
<div class="pt-2 align-lists">
	
    <form method="post" action="" id="findReceipts" name="findReceipts" novalidate="novalidate" data-toggle="validator">
		<input type="hidden" name="partyId" value="${partyId!}">
	</form>
    
</div>
