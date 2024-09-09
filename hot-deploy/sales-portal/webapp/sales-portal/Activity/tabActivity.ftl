<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<style>
#dropdown10 {
    color: black;
}
</style>

     <div class="page-header border-b pt-2"> 
     <#assign extraLeft='<a href="#" title="Add Activity" id="dropdown10" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false" class="text-drak left-icones"><i class="fa fa-plus fa-1 right-icones ml-2" aria-hidden="true" style="font-size: 18px;"></i></a>
          <div class="dropdown-menu" aria-labelledby="cust-icon"></a>
           <h4 class="bg-light pl-1 mt-2">Add Activities<h4/>
           <a class="dropdown-item" href="/sales-portal/control/addTask?salesOpportunityId=${requestParameters.salesOpportunityId?if_exists}"><i class="fa fa-clipboard" aria-hidden="true"></i> Task</a>
           <a class="dropdown-item" href="/sales-portal/control/addPhoneCall?salesOpportunityId=${requestParameters.salesOpportunityId?if_exists}"><i class="fa fa-phone" aria-hidden="true"></i> Phone Call</a>
           <a class="dropdown-item" href="/sales-portal/control/addEmail?salesOpportunityId=${requestParameters.salesOpportunityId?if_exists}"><i class="fa fa-envelope" aria-hidden="true"></i> Email</a>
           <a class="dropdown-item" href="/sales-portal/control/addAppointment?salesOpportunityId=${requestParameters.salesOpportunityId?if_exists}"><i class="fa fa-calendar" aria-hidden="true"></i> Appointment</a>
           <a class="dropdown-item" href="/sales-portal/control/addOthers?salesOpportunityId=${requestParameters.salesOpportunityId?if_exists}"><i class="fa fa-square" aria-hidden="true"></i> Others</a>
          </div>' 
        extra='<a href="#" class="text-dark"><i class="fa fa-refresh fa-1" aria-hidden="true"></i></a><small>2019/03/26 01:10:14</small>'/>
       <@sectionFrameHeader  title="Activities" extraLeft=extraLeft />   
          </div>                                                     
           <div class="table-responsive">
                            <div id="activityGrid" style="height:380px; width: 100%;" class="ag-theme-balham"></div>
                              <script type="text/javascript" src="/sales-portal-resource/js/ag-grid/tabactiv.js"></script>
            </div>
                       
<script>
$(document).ready(function(){
  //loadActivityGrid();
});
</script>


