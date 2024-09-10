<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<div class="page-header border-b pt-2">
    <div class="row">
    &nbsp;&nbsp;&nbsp;<h2 class="d-inline-block">Related Opportunities</h2>&nbsp;<div id="cinHref"><a id="csr_icon" href="/sales-portal/control/newOpportunity?" class="text-dark "> <i class="fa fa-plus fa-1 right-icones" aria-hidden="true" style="font-size: 18px;"></i></a></div>
    &nbsp;&nbsp;
    <form method="post" action="" name="relatedOpportunityForm" id="relatedOpportunityForm" data-toggle="validator">
    <div class="text-right pd-cbx-lbls" style="padding-top: 0px;">
    <div class="form-check form-check-inline">
       <input class="form-check-input" name="statusOpen" id="statusOpen" value="SOSTG_OPEN" checked="checked" type="checkbox" class="chk" >
       <label class="form-check-label"><b>Open</b></label>
    &nbsp;&nbsp;
    <input type="number" id="numberOfDays" name="numberOfDays" min="1" max="99">
     <label class="form-check-label">&nbsp;<b>Days before Target Completion Date</b></label>
     &nbsp;&nbsp;
       <input class="form-check-input" name="statusCompleted" id="statusCompleted"  value="COMPLETED"  type="checkbox" class="chk" >
       <label class="form-check-label"><b>Completed</b></label>
    </div>
    <@button 
     label="${uiLabelMap.Search}"
     id="doSearch" />
     </div>
    </form>                                       
    &nbsp;&nbsp;&nbsp;&nbsp;<b><p id="totalStatus" ></p></b> &nbsp;<b>Total</b>
    &nbsp;&nbsp;&nbsp;&nbsp;<b><p id="won" ></p></b> &nbsp; <b>Won</b>
    &nbsp;&nbsp;&nbsp;&nbsp;<b><p id="lost" ></p></b>&nbsp; <b>Lost</b>
    &nbsp;&nbsp;&nbsp;&nbsp;<b><p id="open" ></p></b>&nbsp; <b>Open</b>
    &nbsp;&nbsp;&nbsp;&nbsp;<b><p id="percentageWon" ></p></b> &nbsp; <b>%Won</b>
    </div>
    
    <div class="row">
      
      
       <div class="col">
          <div class="float-right">
        <div class="row">
           &nbsp;&nbsp&nbsp;&nbsp;<b><p id="percentageLost" ></p></b>&nbsp;<b>%Lost</b> &nbsp; &nbsp;
                <label > 
                   <a href="#" class=" text-dark">
                     <i class="fa fa-refresh fa-1" aria-hidden="true"></i>
                   </a>
                </label>
                <label >
                  <small>${Static["org.ofbiz.base.util.UtilDateTime"].nowDateString("yyyy/MM/dd HH:mm:ss")}</small>
                </label> &nbsp; &nbsp; 
         </div> </div>
      </div>
    	
    </div>
    
</div>
  <script>
  	$('input.chk').on('change', function() {
    	$('input.chk').not(this).prop('checked', false);  
	});
  </script>         
  <div class="table-responsive">
  	<div id="relatedOpportunityGrid" style="height:420px; width: 100%;" class="ag-theme-balham"></div>
    	<script type="text/javascript" src="/sales-portal-resource/js/ag-grid/relatedOpportunityTab.js"></script>
  </div>
    
  <script>   
  	$("#doSearch").click(function(event) {
		event.preventDefault(); 
		    loadRelatedOpportunityGrid();
	});
	const queryString = window.location.search;
	const urlParams = new URLSearchParams(queryString);
	const soi = urlParams.get('salesOpportunityId')
	$('#cinHref a').each(function(){
		this.href += "&salesOpportunityId="+soi;
	})    
  </script>                           
