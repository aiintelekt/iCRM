<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>				
 <div class="panel panel-default">
    <div class="panel-heading" role="tab" id="headingTwo">
       <h4 class="panel-title">
          <a class="collapsed" role="button" data-toggle="collapse" data-parent="#accordionMenu" href="#sources" aria-expanded="false" aria-controls="headingTwo">
          Sources
          </a>
       </h4>
    </div>
    <div id="sources" class="panel-collapse collapse show" data-parent="#accordionMenu" aria-labelledby="">
      <div class="panel-body">               
       <#if dataSources?exists && dataSources?has_content>
          <div class="table-responsive">
             <table class="table table-striped">
                <thead>
                   <tr>
                      <th>Source</th>
                      <th>From Date</th>
                      <th>Remove</th>
                   </tr>
                </thead>
                <tbody>
                <#list dataSources as dataSource>
                   <tr>
                      <td>
                      		<#assign dataSourceGv = dataSource.getRelatedOne("DataSource", false)! />
                      		<#if dataSourceGv?has_content>
                      			${dataSourceGv.description?if_exists}
                      		</#if>
                      </td>
                      <td>${dataSource.fromDate}</td>
                      <td>
                      <span class="glyphicon glyphicon-remove btn btn-xs btn-danger removeDataSource" data-dataSourceId="${dataSource.dataSourceId?if_exists}" data-fromDate="${dataSource.fromDate?if_exists}"></span>
                      </td>
                   </tr>
                </#list>
                </tbody>
             </table>
          </div>
       </#if>
       <div class="row padding-r">
       <div class="col-md-6 col-sm-6">
         <div class="form-group row has-error">
            <label class="col-sm-4 col-form-label">New Data Source</label>
            <div class="col-sm-7">
            <form name="addAccountDataSource" id="addAccountDataSource" method="post" action="addAccountDataSource">
            	<input type="hidden" name="partyId" id="partyId" value="${parameters.partyId!}"/>
               <select class="ui dropdown search form-control input-sm"  name="dataSourceId" id="dataSourceId">
                  <option value="" disabled selected>Select Data Source</option>
                  <#if dataSourceList?has_content>
                  <#list dataSourceList as dataSource>
                    <option value="${dataSource.dataSourceId}">${dataSource.description}</option>
                  </#list>
                  </#if>
               </select>
               <div class="help-block with-errors"></div>
               <@submit label="${uiLabelMap.CommonAdd}"/>
            </form>
            </div>
         </div>
       </div>
       </div>
	  </div> 
    </div>
 </div>
 
<form method="post" action="removeAccountDataSource" name="removeDataSource" id="removeDataSource" >
	<input type="hidden" name="partyId" id="partyId" value="${parameters.partyId?if_exists}"/>
	<input type="hidden" name="dataSourceId" id="dataSourceId" value=""/>
	<input type="hidden" name="fromDate" id="fromDate" value=""/>
</form> 
 
<script>     
$(document).ready(function() {

$('#addAccountDataSource').validator().on('submit', function (e) {
	if (e.isDefaultPrevented()) {
    	// handle the invalid form...
  	} else {
  		var valid = true;
		if (!$("#dataSourceId").val()) {
			valid = false;
			showAlert("error", "Select data source");
		}  
  		if (!valid){
  			e.preventDefault();
  		}
  	}
});

$('.removeDataSource').on('click', function(){
    
	//alert( $(this).attr("data-dataSourceId") );    
	$('#removeDataSource input[name="dataSourceId"]').val( $(this).attr("data-dataSourceId") );
	$('#removeDataSource input[name="fromDate"]').val( $(this).attr("data-fromDate") );
	
	$('#removeDataSource').submit();
                                                                                                    
});	
	
});
</script> 
