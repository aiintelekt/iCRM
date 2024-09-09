<#if userLogin?exists>

<#assign partyRoleAssoc = delegator.findByAnd("PartyRoleNameDetail", {"partyId" : userLogin.partyId}, null, false)>
<#-- <#if partyRoleAssoc?has_content>
	<#assign loggedUseRoleTypeId = partyRoleAssoc.get(0).roleTypeId>
	${setRequestAttribute("loggedUseRoleTypeId", loggedUseRoleTypeId)} 
</#if> -->
<#assign loggedPartyName = Static["org.ofbiz.party.party.PartyHelper"].getPartyName(delegator, userLogin.partyId, true) >
${setRequestAttribute("loggedPartyName", loggedPartyName)}
${setRequestAttribute("loggedUserPartyId", userLogin.partyId)}
</#if> 