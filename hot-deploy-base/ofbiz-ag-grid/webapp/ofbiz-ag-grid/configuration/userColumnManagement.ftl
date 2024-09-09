<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<div class="row">
    <div id="main" role="main">
        <#assign extra='<a href="#" onclick="javascript: onSubmit();" class="btn btn-xs btn-primary"><i class="fa fa-edit" aria-hidden="true"></i> Update</a>
        <pre style="font-style: italic;font-size: smaller;"><i style="color:red;">*</i> Note: User preference settings are subjected to change <br/> if admin update the fio grid structure.</pre>
        '/>
        <#assign title = uiLabelMap.UserColumnManagement! />
        <#if gridName?exists && gridName?has_content>
        	<#assign title = title + " - "+ gridName?if_exists />
        </#if>
        <@sectionFrameHeader 
	        title=title!
	        extra=extra?if_exists
	        
	        />
	    <div class="col-md-2 col-lg-2 col-sm-2">
			<input type="text" class="form-control input-sm" value="" id="searchField" name="searchField" placeholder="Search" autocomplete="off">
	    </div>
        <div class="col-md-12 col-lg-12 col-sm-12">
			<#assign gridColumnList = Static["org.fio.ag.grid.util.DataUtil"].getColumns(delegator, requestParameters.gridInstanceId!,requestParameters.gridUserId!)?if_exists />
            <#assign columnDefs = gridColumnList.columnDefs!>
            
			<div class="table-responsive">
				
			    <table style="width: 35%;">
			        <thead class="thead-dark">
			            <tr class="border-bottom">
			                <th>Fields</th>
			                <th>Hide</th>
			            </tr>
			        </thead>
			        <tbody>
			        <#assign seqId = 1 />
					<#list columnDefs as column>
						<tr>
							<input type="hidden" class="priority" value="${seqId!}" name="${column.field!}_SeqId" id="${column.field!}_SeqId" />
			                <td><label class="">${column.headerName!}</label></td>
			                <td>
			                	<#assign isActive = column.hide?string('Y','N') />
			                    	<#assign checked = false />
			                    <#if "Y" == isActive>
			                    	<#assign checked = true />	
			                    </#if>
			                    <@checkboxField
				                    id="${column.field!}"
				                    name="isHide"
				                    class="form-check-input checkMe"
				                    value=isActive!
				                    checked = checked!
				                    />
			                </td>
			            </tr>
			            <#assign seqId = seqId+1 />	
					</#list>
					</tbody>
			    </table>
			    <form name="updateUserGridConfigForm" id="updateUserGridConfigForm" action="updateUserGridConfiguration" method="post" data-toggle="validator">
			    	<@inputHidden 
	                	id="gridInstanceId"
	                	value="${requestParameters.gridInstanceId!}"
	                    />
	                <@inputHidden 
	                	id="gridUserId"
	                	value="${requestParameters.gridUserId!}"
	                    />
			    	<@inputHidden 
	                	id="fieldToHide"
	                    />
			    </form>
			    
			</div>
            <div class="clearfix"></div>
        </div>
    </div>
</div>

<script>
$(document).ready(function() {
	var options = $("input[name='isHide']:checked").map(function() {return this.id;}).get().join(',');
	$('#fieldToHide').val(options);
});
function onSubmit(){
	var options = $("input[name='isHide']:checked").map(function() {return this.id;}).get().join(',');
  	$('#fieldToHide').val(options);
	$("#updateUserGridConfigForm").submit();
}
$("#searchField").on("keyup", function() {
    var value = $(this).val();

    $("table tr").each(function(index) {
        if (index !== 0) {

            $row = $(this);

            var id = $row.find("td:first").text();

            if (id.toLowerCase().indexOf(value) !== 0) {
                $row.hide();
            }
            else {
                $row.show();
            }
        }
    });
});
</script>