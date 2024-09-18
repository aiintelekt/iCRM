    <div class="row">
        <div class="col-lg-12 col-md-12 col-sm-12">
        <#assign workEfforts = (Static["org.ofbiz.entity.util.EntityQuery"].use(delegator).select("workEffortId").from("WorkEffort").where("workEffortTypeId","85028").maxRows(5).orderBy("-estimatedStartDate").distinct().queryList())?if_exists />
		<#assign workEffortIds = Static["org.ofbiz.entity.util.EntityUtil"].getFieldListFromEntityList(workEfforts, "workEffortId", true)>
		<#assign extraLeft=''/>
		<div id class="text-left float-left"><h3 class="float-left">Recent Activities: </h3>
	    <#list workEffortIds as workEffortId>
	    <a href="<@ofbizUrl>viewActivity?workEffortId=${workEffortId!}</@ofbizUrl>" class="btn btn-xs btn-primary">${workEffortId!}</a>
	    </#list>
        </div>
    </div>
</div>
	
	
