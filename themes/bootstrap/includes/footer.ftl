    <#-- <footer class="footer">
      <div class="container-fluid">
        <div class="row">
          <div class="col-sm-12 col-md-6 col-lg-6 ">
            <p class="text-muted"> Time Zone : Eastern Daylight Time</p>
          </div>
          <div class="col-sm-12 col-md-6  col-lg-6">
            <p class="text-muted text-right">2020 &copy; <img alt="Group FiO" src="/bootstrap/images/logo.png" height="20" width="44" border="0"> 
              All Rights Reserved
            </p>
          </div>
        </div>
      </div>
    </footer> -->
    <#assign nowTimestamp = Static["org.ofbiz.base.util.UtilDateTime"].nowTimestamp()>
    <#assign year = Static["org.ofbiz.base.util.UtilDateTime"].toDateString(nowTimestamp, "yyyy")/>
    <footer class="footer" id="footer">
		<div class="container-fluid">
			<div class="row">
				<div class="col-sm-12 col-md-12 col-lg-12">
					<p class="text-muted text-right">${year!} &copy; Professional Edition of iCRM. All Rights Reserved
				</p>
			</div>
			</div>
		</div>
	</footer>
	
    <a id="back-to-top" href="#" class="btn btn-xs btn-primary btn-lg back-to-top rounded-circle" 
    	role="button" title="Back to Top" 
    	data-toggle="tooltip" data-container="body" data-placement="right"
    	>
    	<span class="fa fa-chevron-up"></span>
    </a>
	
	<!-- Bootstrap core JavaScript
	  ================================================== -->
	<!-- Placed at the end of the document so the pages load faster -->
	<script type="text/javascript" src="/bootstrap/js/validator.min.js"></script>
	<#-- <script type="text/javascript" language="javascript" src="/bootstrap/js/popper.min.js"></script> --> 
	
	<script>window.jQuery || document.write('<script src="/bootstrap/js/jquery.min.js"><\/script>')</script>    
	<#-- <script type="text/javascript" language="javascript" src="/bootstrap/js/bootstrap.min.js" ></script> -->

	<!-- DataTable core JavaScript-->
	<#-- 
	<script type="text/javascript" language="javascript" src="/bootstrap/js/jquery.dataTables.min.js"></script>
	<script type="text/javascript" language="javascript" src="/bootstrap/js/dataTables.bootstrap.min.js"></script>
	<script type="text/javascript" language="javascript" src="/bootstrap/js/dataTables.fixedHeader.min.js"></script>
	<script type="text/javascript" language="javascript" src="/bootstrap/js/dataTables.responsive.min.js"></script>
	<script type="text/javascript" language="javascript" src="/bootstrap/js/responsive.bootstrap.min.js"></script>
	-->
	<!-- Select box Drop down JavaScript-->
	<script type="text/javascript" language="javascript" src="/bootstrap/js/dropdown.js"></script>
	<script type="text/javascript" language="javascript" src="/bootstrap/js/transition.min.js"></script>
	<!-- Bootstrap Date time picker JavaScript-->
	<script type="text/javascript" language="javascript" src="/bootstrap/js/moment.js"></script>
	<script type="text/javascript" language="javascript" src="/bootstrap/js/bootstrap-datetimepicker.min.js"></script>
	<!-- Custom core JavaScript-->
	<script type="text/javascript" language="javascript" src="/bootstrap/js/bootstrap-notify.min.js"></script>
	
	<#-- <script src="/bootstrap/js/bootstrap-confirmation.js"></script> -->
	<#assign requestURI = request.getRequestURI()/>
	<#if requestURI.contains("AccountDashboard") || requestURI.contains("srDashboard")>
	<script type="text/javascript" src="/bootstrap/js/charts/echarts.min.js"></script>
	<script type="text/javascript" src="/bootstrap/js/charts/china.js"></script>
	<script type="text/javascript" src="/bootstrap/js/charts/world.js"></script> 
	<script type="text/javascript" src="/bootstrap/js/charts/bmap.min.js"></script>
	</#if>
	
	<script type="text/javascript" language="javascript" src="/bootstrap/js/usps-utils.js"></script>
	
	<script type="text/javascript" language="javascript" src="/bootstrap/js/custom.js"></script>
	<#-- commenting out intercom chatbox code
	<#if userLogin?has_content>
		<form id="intercom">
			<input name="userId" type="hidden" value="${userLogin.userLoginId}">
			<input name="userName" type="hidden" value="${userName}">
			<input name="email" type="hidden" value="">
		</form>
		<script type="text/javascript" language="javascript" src="/bootstrap/js/intercom.js"></script>
	</#if>-->