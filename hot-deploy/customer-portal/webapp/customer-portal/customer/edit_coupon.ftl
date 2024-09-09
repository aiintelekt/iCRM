<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl" />
<body onload="disableField()">
<div class="row">
    <div id="main" role="main">
   <#assign partyIdData = delegator.findByAnd("ProductPromoCodeParty", {"productPromoCodeId" : productPromoCodeId, "partyId" : partyId}, null, false)>
    <#if partyIdData?has_content>
    <#assign extra='<a href="/customer-portal/control/viewCustomer?partyId=${partyId!}#coupons"  class="btn btn-xs btn-primary back-btn">
<i class="fa fa-chevron-circle-left" aria-hidden="true"></i> Back</a>' />
      <#else>
    <#assign extra='<a href="/loyalty-portal/control/findCoupons?externalLoginKey=${requestAttributes.externalLoginKey!}"  class="btn btn-xs btn-primary back-btn">
<i class="fa fa-chevron-circle-left" aria-hidden="true"></i> Back</a>' />
</#if>
<#assign isShowHelpUrl="Y">
	<#if isPhoneCampaignEnabled?has_content && isPhoneCampaignEnabled?if_exists=="Y">
	<#assign isShowHelpUrl="N">
	</#if>
        <div class="clearfix"></div>
        <form id="mainFrom" method="post" action="<@ofbizUrl>editCoupon</@ofbizUrl>" data-toggle="validator">
            <div class="col-lg-12 col-md-12 col-sm-12">
                <div><@sectionFrameHeader title="Edit Coupon" extra=extra isShowHelpUrl=isShowHelpUrl!/></div>
                <@dynaScreen instanceId="EDIT_CUSTOMER_COUPON" modeOfAction="UPDATE" />
                <@inputHidden id="couponStatusValue" value="${couponStatus!}" />
                <@inputHidden id="domainEntityType" value="${domainEntityType!}" />
            </div>
            <div class="clearfix"></div>
            <div class="offset-md-2 col-sm-10">
                <@formButton btn1type="submit" btn1label="${uiLabelMap.Update}" />
            </div>
        </form>
    </div>
</div>
</body>
<script>	
	function disableField() {
	var partyId=$('#partyId').val();
  	if(partyId=="N/A"){
	$("a#link_partyId").replaceWith($("a#link_partyId").text());
  	}
	}
</script>
<script>
var status =$("#couponStatusValue").val();
if(status == "Coupon Redeemed"){
var element = document.getElementById("couponStatus");
element.style.color = "#FFA500";
}
if(status == "Coupon Available"){
var element = document.getElementById("couponStatus");
element.style.color = "#66BB55";
}
if(status == "Coupon Expired"){
var element = document.getElementById("couponStatus");
element.style.color = "#FF0000";
}
if(status == "Coupon Audited"){
var element = document.getElementById("couponStatus");
element.style.color = "#A52A2A";
}
</script>