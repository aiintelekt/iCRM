<#include "component://bootstrap/lib/ofbizFormMacros.ftl"/>
<#assign readOnlyPermission = Static["org.fio.homeapps.util.DataUtil"].hasPermissionWoFullPerm(request, "READ_ONLY_PERM")?if_exists />

<#macro exportFileModal instanceId exportType>

<div id="${instanceId!}" class="modal fade bd-example-modal-lg" tabindex="-1" role="dialog" aria-labelledby="myLargeModalLabel" aria-hidden="true">
	<div class="modal-dialog modal-lg" style="max-width: 1200px;">
		<div class="modal-content">
			<div class="modal-header">
                <h4 class="modal-title">Export List</h4>
                <button type="button" class="close" data-dismiss="modal" id="close">&times;</button>
            </div>         
            <div class="modal-body" style="padding-bottom: 8px;">
            	<div class="popup-bot">
            	<form method="post" name="exp-search-form" id="exp-search-form" class="form-horizontal" novalidate="novalidate" data-toggle="validator">
            		<input type="hidden" name="exportType" value="${exportType!}"/>
            		<input type="hidden" name="externalLoginKey" value="${requestAttributes.externalLoginKey!}"/>
	        	   
	        	   	<div class="row">
                
	                	<div class="col-lg-10 col-md-10 col-sm-10">
							<@dynaScreen 
								instanceId="FIND_EXP_FILE"
								modeOfAction="CREATE"
								/>
						</div>
						
						<div class="col-lg-2 col-md-2 col-sm-2">
							<div class="text-right" style="padding-top: 45px;">
						     	<@button
						        id="exp-search-btn"
						        label="${uiLabelMap.Find}"
						        />
				            </div>
						</div>
						                    
	            	</div>
	             
	            </form>     
	            </div>
	            
				<#assign rightContent='<button title="Refresh" id="exp-refresh-btn" class="btn btn-primary btn-xs ml-2"> <i class="fa fa-refresh" aria-hidden="true"></i>  </button>'/>
				<@AgGrid
					gridheadertitle=""
					gridheaderid="${instanceId!}-grid-action-container"
					savePrefBtn=true
					clearFilterBtn=true
					exportBtn=true
					insertBtn=false
					updateBtn=false
					removeBtn=false
					headerextra=rightContent!
					refreshPrefBtnId="exp-refresh-pref-btn"
					savePrefBtnId="exp-save-pref-btn"
					clearFilterBtnId="exp-clear-filter-btn"
				    userid="${userLogin.userLoginId}" 
				    shownotifications="true" 
				    instanceid="EXPORT_FILE_LIST" 
				    autosizeallcol="true"
				    debug="false"
				    />    
			         
				<script type="text/javascript" src="/common-portal-resource/js/ag-grid/export/find-export-list.js"></script>
      		</div>
    	</div>
  	</div>
</div>

<script>
$(document).ready(function() {

$('#${instanceId!}').on('shown.bs.modal', function (e) {
	$("#exp-refresh-btn").trigger('click');
});

$("#grid-exp-download-btn").click(function () {
	$("#${instanceId!}").modal('show');
});

});
</script>
</#macro>

