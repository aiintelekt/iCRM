<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<div class="col-lg-12 col-md-12 col-sm-12">
    <div class="row">
        <div class="col-lg-12 col-md-12 col-sm-12">
        <#assign CustRequestSrSummaryDetails = (Static["org.ofbiz.entity.util.EntityQuery"].use(delegator).select("seqId").from("UserLoginHistory").where("entity","CustRequestSrSummary","userLoginId",userLogin.userLoginId).maxRows(5).orderBy("-fromDate").distinct().queryList())?if_exists />
		<#assign custRequestIds = Static["org.ofbiz.entity.util.EntityUtil"].getFieldListFromEntityList(CustRequestSrSummaryDetails, "seqId", true)>
		<#assign extraLeft='<a href="findServiceRequests" class="btn btn-xs btn-primary float-right text-right">Back</a>'/>
		<div id class="text-left float-left"><h3 class="float-left">View Service Request </h3>
	    <#list custRequestIds as custRequestId>
	    <a href="<@ofbizUrl>viewServiceRequest?srNumber=${custRequestId!}&seqId=${custRequestId!}&entity=CustRequestSrSummary</@ofbizUrl>" class="btn btn-xs btn-primary">${custRequestId!}</a>
	    </#list>
	    </div>
	    <#if extraLeft?has_content>
            <div class="col-lg-12 col-md-6 col-sm-12">
                <div class="text-right" id="extra-header-right-container">
                  ${extraLeft!}
               </div>
            </div>
         </#if>
        </div>
    </div>
</div>