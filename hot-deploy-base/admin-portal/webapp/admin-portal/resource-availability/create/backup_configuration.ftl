<#include "component://bootstrap/lib/ofbizFormMacros.ftl"/>
<#assign extra=''/>
<div class="row">
	<div id="main" role="main">
		<form id="mainFrom" method="post" action="" data-toggle="validator">
			<div class="col-lg-12 col-md-12 col-sm-12">
				<@sectionFrameHeader title="${uiLabelMap.BackupResourceSetup!}" extra=extra/>
				<@dynaScreen
					instanceId="BACKUP_CONFIG_BASE"
					modeOfAction="CREATE"
					/>
			</div>
			<div class="offset-md-2 col-sm-12">
				<@formButton
					btn1type="button"
					btn1id="back_config_btn"
					btn1label="${uiLabelMap.Add}"
					btn2=true
					btn2onclick = "resetForm()"
					btn2type="reset"
					btn2label="${uiLabelMap.Clear}"
					/>
			</div>
		</form>
	</div>
</div>
<script type="text/javascript" src="/admin-portal-resource/js/backup-configuration/backup-configuration.js"></script>

<div class="row" style="width:100%">
	<div class="col-lg-12 col-md-12 col-sm-12 dash-panel">
	<#assign rightContent='' />   
	    
	<@AgGrid
		gridheadertitle=uiLabelMap.BackupConfigurationList
		gridheaderid="backup-config-grid-action-container"
		savePrefBtn=true
		clearFilterBtn=true
		exportBtn=true
		insertBtn=false
		updateBtn=false
		removeBtn=true
		headerextra=rightContent
		removeBtnId="backup-config-remove-btn"
	    userid="${userLogin.userLoginId}" 
	    shownotifications="true" 
	    instanceid="BACKUP_CONFIGURATION" 
	    autosizeallcol="true"
	    debug="false"
	    />
	<script type="text/javascript" src="/admin-portal-resource/js/ag-grid/resource-availability/backup-config.js"></script>
	</div>	  	
</div>
<span id="create-click"></span>
<script>
	$(document).ready(function() {
	   	loadCoordinator();
	});
	
</script>