<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>

<div id="main" role="main" class="pd-btm-title-bar">
    <#assign extra='
                  <a href="viewScreenConfigs" class="btn btn-xs btn-primary text-right">
                     Back
                  </a>' />
      <@sectionFrameHeader title="View Cls Specifications" extra=extra/>
      <div class="col-lg-12 col-md-12 col-sm-12">
      
      <input type="hidden" name="clsId" value="${parameters.clsId?if_exists}" id="clsId"/>

      <div class="clearfix"></div>
      <div id="viewClsSpecGrid" style="width: 100%;" class="ag-theme-balham"></div>
      <script type="text/javascript" src="/admin-portal-resource/js/ag-grid/screenConfigs/viewClsSpecGrid.js"></script>
               
      </div>
</div>


<script>     
$(document).ready(function() {
	    loadClsSpecGrid();
});
</script>