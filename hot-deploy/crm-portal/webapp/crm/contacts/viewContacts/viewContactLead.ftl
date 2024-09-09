<#include "component://homeapps/webapp/homeapps/lib/ofbizFormMacros.ftl"/>
<#assign leadPartyId = parameters.partyId?if_exists />

<div class="clearfix"> </div>
<div class="page-header">
   <h2 class="float-left">${uiLabelMap.CommonLead}</h2>
   <div class="float-right">
      <a class="btn btn-xs btn-primary m5 li-modal" href="<@ofbizUrl>findLeads?select=Y</@ofbizUrl>">${uiLabelMap.assignLead}</a>
   </div>
</div>

<#--  Find Lead Modal Start ==================================-->
<div id="assignLeadModal" class="modal fade" role="dialog">
   <div class="modal-dialog modal-lg">
      <#-- Modal content-->
      <div class="modal-content" style="overflow-x:hidden;overflow-y:scroll;weight:700px;height:600px;">
		 <#-- <div class="modal-header">
            <h4 class="modal-title">${uiLabelMap.CommonLead!}</h4>
            <button type="reset" class="close" data-dismiss="modal">&times;</button>
         </div>
         <div class="modal-body">
            <div class="card-header" style="overflow-x: scroll;">
            </div>
            </div> -->
		</div>
	</div>
</div>

<script>
$('.li-modal').on('click', function(e){
      e.preventDefault();
      $('#assignLeadModal').modal('show').find('.modal-content').load($(this).attr('href'));
    });
</script>