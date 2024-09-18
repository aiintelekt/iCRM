<#setting number_format="#.##"/>

<style type="text/css">
.page-content-wrapper .page-content {
	margin-left: 0px;
}

.login .content {
	width: 50%;	
}
</style>

<div class="row">
	<div class="col-md-12">
		<div class="portlet light">
			<div class="portlet-title">
				<div class="caption" style="max-width: 82%;">
					<i class="fa fa-dashboard font-green-haze"></i>
					<span class="caption-subject bold uppercase font-green-haze"> Welcome</span>
					<span class="caption-helper">${loggedPartyName!}</span>
				</div>
				<div class="tools">
					
					<a href="<@ofbizUrl>logout</@ofbizUrl>">
						<i class="icon-key"></i> Log Out </a>
					
					<#--
					<a href="javascript:;" class="collapse">
					</a>
					<a href="#portlet-config" data-toggle="modal" class="config">
					</a>
					<a href="javascript:;" class="reload">
					</a>
					<a href="javascript:;" class="fullscreen">
					</a>
					<a href="javascript:;" class="remove">
					</a>-->
					
				</div>
			</div>
			<div class="portlet-body">
				
				<div class='row mix-grid'>
				
					<div class='col-lg-3 col-md-3 col-sm-4 col-xs-4 mix' data-order="5" style="padding-bottom: 10px;">
						<a class="icon-btn" href="javascript:;">
						<i class="fa fa-bullhorn"></i>
						<div>
							 Notification
						</div>
						<span class="badge badge-danger">
						3 </span>
						</a>
					</div>
				
					<div class='col-lg-3 col-md-3 col-sm-4 col-xs-4 mix' data-order="4" style="padding-bottom: 10px;">
						<a class="icon-btn" href="javascript:;">
						<i class="fa fa-envelope"></i>
						<div>
							 Inbox
						</div>
						<span class="badge badge-info">
						4 </span>
						</a>
					</div>
					
					<div class='col-lg-3 col-md-3 col-sm-4 col-xs-4 mix' data-order="3" style="padding-bottom: 10px;">
						<a class="icon-btn" href="#">
						<i class="fa fa-calendar"></i>
						<div>
							 Time Sheet
						</div>
						</a>
					</div>
					<div class='col-lg-3 col-md-3 col-sm-4 col-xs-4 mix' data-order="2" style="padding-bottom: 10px;">
						<a class="icon-btn" href="<@ofbizUrl>listScreenLayout</@ofbizUrl>">
						<i class="icon-settings"></i>
						<div>
							 Widgets
						</div>
						</a>
					</div>
					
					<div class='col-lg-3 col-md-3 col-sm-4 col-xs-4 mix' data-order="1" style="padding-bottom: 10px;">
						<a class="icon-btn" href="#">
						<i class="fa fa-bar-chart-o"></i>
						<div>
							 Dashboard
						</div>
						</a>
					</div>
										
				</div>	
							
			</div>
		</div>
	</div>
	
</div>

<script type="text/javascript">
jQuery(document).ready(function() {     
    
	$('.mix-grid').mixItUp({
	    animation: {
	        effects: 'fade rotateZ(-180deg)',
	        duration: 700
	    }
	});
	
	$('.mix-grid').mixItUp('sort', 'order : asc', true);
    
});
</script>