<div id="viewAlerts" class="modal fade" role="dialog">
	<div class="modal-dialog modal-lg">
    	<!-- Modal content-->
    	<div class="modal-content">
        	<div class="modal-header">
            	<@headerH3
          			id="alert"
           			title="${uiLabelMap.alert!}"
          		/>
                <button type="reset" class="close" data-dismiss="modal">&times;</button>
            </div>
            <div class="modal-body">
            	<div class="table-responsive">
					<div id="viewAlertsGrid" style="width: 100%;" class="ag-theme-balham"></div>
   					<script type="text/javascript" src="/sales-portal-resource/js/ag-grid/viewAlerts.js"></script>
   				</div>
   			</div>
   		</div>
    </div>
</div>





