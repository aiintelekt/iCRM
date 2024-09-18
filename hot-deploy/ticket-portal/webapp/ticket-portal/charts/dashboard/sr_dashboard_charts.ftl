<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>

<script type="text/javascript" src="/bootstrap/js/charts/echarts.min.js"></script>
<script type="text/javascript" src="/bootstrap/js/charts/china.js"></script>
<script type="text/javascript" src="/bootstrap/js/charts/world.js"></script> 
<script type="text/javascript" src="/bootstrap/js/charts/bmap.min.js"></script>
<script type="text/javascript" src="/bootstrap/js/dataTool.min.js"></script>
<script type="text/javascript" src="/bootstrap/js/charts/ecStat.min.js"></script>
<script type="text/javascript" src="/bootstrap/js/charts/echarts-gl.min.js"></script>

<div class="row">
    <div id="main" role="main">

	<@sectionFrameHeader title="SR Dashboard" />

 	</div>
</div>
<#assign presentDate = "${presentDate?if_exists}">     

	<div class="col-lg-12 col-md-12 col-sm-12 mt-3">
         <div class="row">
            
              <div class="col-xl-4 col-lg-4 col-md-12 col-sm-12">
               ${screens.render("component://ticket-portal/widget/dashboard/DashboardScreens.xml#srByuserChart")}
			  </div>

              <div class="col-xl-4 col-lg-4 col-md-12 col-sm-12">
               ${screens.render("component://ticket-portal/widget/dashboard/DashboardScreens.xml#srByTypeChart")}
               </div>
             

              <div class="col-xl-4 col-lg-4 col-md-12 col-sm-12">
                ${screens.render("component://ticket-portal/widget/dashboard/DashboardScreens.xml#srByCateChart")}
			  </div>
              

              <div class="col-xl-4 col-lg-4 col-md-12 col-sm-12">
               ${screens.render("component://ticket-portal/widget/dashboard/DashboardScreens.xml#srByDayChart")}
              </div>
             

              <div class="col-xl-4 col-lg-4 col-md-12 col-sm-12">
               ${screens.render("component://ticket-portal/widget/dashboard/DashboardScreens.xml#srByCumChart")}
              </div>
              
              <div class="col-xl-4 col-lg-4 col-md-12 col-sm-12">
               ${screens.render("component://ticket-portal/widget/dashboard/DashboardScreens.xml#srByEowChart")}
              </div>
                    
         </div>
     </div>



