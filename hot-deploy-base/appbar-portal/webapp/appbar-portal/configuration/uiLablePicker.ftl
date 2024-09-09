<div id="uiLabel" class="modal fade mt-2 save-modal" role="dialog">
    <div class="modal-dialog modal-lg">
        <!-- Modal content-->
        <div class="modal-content">
            <div class="modal-header">
                <h3 class="modal-title">Find UI Labels</h3>
                <button type="reset" class="close" data-dismiss="modal">&times;</button>
            </div>
            <div class="modal-body">
            	<form method="post" id="searchForm"  name="searchForm" class="form-horizontal" data-toggle="validator">
            		<input type="hidden" name="externalLoginKey" value="${requestAttributes.externalLoginKey!}"/>
            		<input type="hidden" name="userId" value="${userLogin.userLoginId?if_exists}" />
            	<div class="row p-2">
            		<div class="col-md-4 col-sm-4">
	            		<#assign componentList = (Static["org.ofbiz.entity.util.EntityQuery"].use(delegator).select("componentName","uiLabels").from("OfbizComponentAccess").where("isHide","N").queryList())?if_exists />
     					<#assign components = Static["org.fio.admin.portal.util.DataUtil"].getMapFromGeneric(componentList, "componentName", "uiLabels", false)?if_exists />       		
			         	<@dropdownCell 
							id="labelComponentName"
							options=components!
							placeholder=uiLabelMap.module
							required=false
							allowEmpty=true
							/>	
			         </div>
			         
			         <div class="col-md-2 col-sm-2">
			         	<@button
			            id="find-btn"
			            label="${uiLabelMap.Find}"
			            />	
			         </div>
			    </div>
            	</form>
            	
            	<#--
           		<@AgGrid 
	            	userid="${userLogin.userLoginId!}" 
		            instanceid="UI_LABELS"
		            shownotifications="true"
		            autosizeallcol="true"
		            debug="false"
		            gridheadertitle=uiLabelMap.ListOfUiLabel!
			    	gridheaderid="listOfUiLabelBtns"
	            	/>
	       		<script type="text/javascript" src="/appbar-portal-resource/js/ag-grid/ui-label.js"></script>
	       		-->
	       		<@fioGrid
					id="ui-label-list"
					instanceId="UI_LABELS"
					jsLoc="/appbar-portal-resource/js/ag-grid/ui-label.js"
					headerLabel=uiLabelMap.ListOfUiLabel!
					headerExtra=extraRight!
					headerBarClass="grid-header-no-bar"
					headerId="ui-label-list-tle"
					savePrefBtnId="ui-label-list-save-pref"
					clearFilterBtnId="ui-label-list-clear-pref"
					subFltrClearId="ui-label-list-clear-sub-ftr"
					serversidepaginate=false
					statusBar=false
					exportBtn=true
					exportBtnId="ui-label-list-export-btn"
					savePrefBtn=false
					clearFilterBtn=false
					subFltrClearBtn=false
					/>

            </div>
        </div>
    </div>
</div>