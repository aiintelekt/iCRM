<#include "component://homeapps/webapp/homeapps/lib/ofbizFormMacros_dbs.ftl"/>

<div class="page-header border-b">
	<h1>${uiLabelMap.pendingRequests}</h1>
</div>

<div class="card-header">
   <form method="post" action="#" id="findProdHierMainForm" class="form-horizontal" name="findProdHierMainForm" novalidate="novalidate" data-toggle="validator">
      <div class="row">
      	 <div class="col-md-2 col-sm-2">  
      	 <@fromSimpleAction id="maker-checker-audit" showCancelBtn=false isSubmitAction=false submitLabel="Group Inbox"/>
      	 </div>
      	 <div class="col-md-2 col-sm-2">        
         <@fromSimpleAction id="maker-inbox" showCancelBtn=false isSubmitAction=false submitLabel="User Inbox"/>
        	</div>
      </div>
   </form>
   <div class="clearfix"> </div>
</div>
