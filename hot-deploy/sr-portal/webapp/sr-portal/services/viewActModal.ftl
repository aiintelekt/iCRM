

<div  id="resolveModal" class="modal fade bd-example-modal-lg" tabindex="-1" role="dialog" aria-labelledby="myLargeModalLabel" aria-hidden="true">
	<div class="modal-dialog modal-lg" style="max-width: 1200px;">
		<div class="modal-content">
			<div class="modal-header">
                <h4 class="modal-title">There are pending activities linked to this SR. Please close the activities before closing the SR</h4>
                <button type="reset" class="close" data-dismiss="modal" id="close">&times;</button>
            </div>
            	
            <div class="modal-body" style="padding-bottom: 8px;">
           
            	<div class="table-responsive">
            		 <div id="openActivityGrid" style="width: 100%;" class="ag-theme-balham"></div>
            		 <script type="text/javascript" src="/sr-portal-resource/js/ag-grid/services/oppModal.js"></script>
			    </div>
      	</div>
	      	<div class="modal-footer" style="border-top: 0px;padding-top: 0px;">
	            
	        </div>
    	</div>
  	</div>
</div>
<script>
$(document).ready(function(){
  loadoppActivityGrid();
});
</script>