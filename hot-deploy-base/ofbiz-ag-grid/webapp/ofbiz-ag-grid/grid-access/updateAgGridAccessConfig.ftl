<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<#include "component://homeapps/webapp/homeapps/user-audit/audit_page_header.ftl"/>
<div class="row">
    <div id="main" role="main">
        <#assign extra='<a href="viewAgGridAccessConfig?instanceId=${agGridAccessConfig?if_exists.instanceId!}&groupId=${agGridAccessConfig?if_exists.groupId!}" class="btn btn-xs btn-primary"><i class="fa fa-chevron-circle-left" aria-hidden="true"></i> Back</a>'/>
        <#assign extraLeft=''/>
        
        <form name="updateAgGridAccessForm" id="updateAgGridAccessForm" action="updateAgGridAccessConfiguration" method="post" data-toggle="validator" onsubmit="javascript:return validation();">
            <#include "component://homeapps/webapp/homeapps/user-audit/audit_from_header.ftl"/>
            <div class="col-lg-12 col-md-12 col-sm-12 dash-panel">
	        <@sectionFrameHeaderTab title="${uiLabelMap.UpdateAgGridAccessConfig!}" tabId="UpdateAgGridAccessConfig" extra=extra?if_exists/> 
                <div class="row">
                    <div class="col-md-12 col-lg-6 col-sm-12 ">
                        <#-- 
                        <@inputRow
                            id="instanceId"
                            label="${uiLabelMap.InstanceId}"
                            value="${agGridAccessConfig?if_exists.instanceId!}"
                            placeholder="${uiLabelMap.InstanceId}"
                            required=true
                            dataError="Please enter instance id"
                            maxlength=60
                            disabled=true
                            /> -->
                        <@dropdownCell
                            label="${uiLabelMap.InstanceId!}"
                            id="instanceId" 
                            placeholder="Select Grid Instance"
                            options=gridInstanceList!
                            value="${agGridAccessConfig?if_exists.instanceId!}"
                            required=true
                            dataError="Please Grid Instance"
                            disabled=true
                        	/> 
                        	
                        <@dropdownCell
                            label="${uiLabelMap.SecurityGroup!}"
                            id="groupId" 
                            placeholder="Select security group"
                            options=groupList!
                            value="${agGridAccessConfig?if_exists.groupId!}"
                            required=true
                            dataError="Please select security Group"
                            disabled=true
                        	/>  
                        <@inputHidden 
	                      	id="gridOptions"
	                      	/>
                        <#assign agGridOptionsEnum = Static["org.fio.admin.portal.util.EnumUtil"].getEnums(delegator, "AG_GRID_OPTIONS")?if_exists />
                 		<#assign agGridOptionsList = Static["org.fio.admin.portal.util.DataHelper"].getDropDownOptions(agGridOptionsEnum, "enumId","description")?if_exists />  
                  
						<@inputCheckBox
							id="options"
							label="${uiLabelMap.Options}"
							optionList=agGridOptionsList!
							optionValues=agGridAccessConfig?if_exists.options!
							/>
                    </div>
                </div>
                <div class="clearfix"></div>
                <div class="form-group offset-2">
                    <div class="text-left ml-3 pad-10">
                    	<@submit
                            label="${uiLabelMap.Update!}"
                            />
                        <@cancel
                            label="${uiLabelMap.Cancel!}"
                            onclick="viewAgGridAccessConfig?instanceId=${agGridAccessConfig?if_exists.instanceId!}&groupId=${agGridAccessConfig?if_exists.groupId!}"
                            />
                    </div>
                </div>
                
            </div>
        </form>
    </div>
    <#-- main end -->
</div>
<#-- row end-->

<script>
$(document).ready(function() {
	var options = $("input[name='options']:checked").map(function() {return this.id;}).get().join(',');
	$('#gridOptions').val(options);
});
function validation(){
	var options = $("input[name='options']:checked").map(function() {return this.id;}).get().join(',');
	var flag = "Y";
  	if(options == null || options == "" || options == 'undefined') {
  		
  	} else{
  		$('#gridOptions').val(options);
  	}
  	if("Y" == flag){
        return true;
    } 
    else {
        return false;
    }
}
</script>