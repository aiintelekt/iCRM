
<#macro auditLogModal id isShowAuditType=false >

<div id="${id!}" class="modal fade" >
  <div class="modal-dialog modal-lg">
    <!-- Modal content-->
    <div class="modal-content">
      <div class="modal-header">
        <h2 class="modal-title"></h2>
        <button type="reset" class="close" data-dismiss="modal">&times;</button>
      </div>
      <div class="modal-body">
        
        <div class="table-responsive">
        	<#-- 
        	<div class="float-right" id="exportLead">
				<div class="row">
					<@simpleDropdownInput 
						id="exportType" 
						options=exportTypeList
						required=false 
						allowEmpty=true 
						dataLiveSearch=true 
						emptyText="Select export type"
						/>
					<div class="float-right pr-3">
						<a href="javascript:  callExportBatchError();"
							class="btn btn-xs btn-primary">${uiLabelMap.export}</a>
					</div>
				</div>
			</div>
			<div class="clearfix"></div>
			 -->
			<table class="table table-striped error-logs">
			<thead>
			<tr>
				<#if isShowAuditType>
				<th>${uiLabelMap.auditType!}</th>
				</#if>
				
				<th>${uiLabelMap.oldValueText!}</th>
				<th>${uiLabelMap.newValueText!}</th>
				<th>${uiLabelMap.changedFieldName!}</th>
				<th>${uiLabelMap.changedByInfo!}</th>
				<th>${uiLabelMap.createTime!}</th>
				<th class="">${uiLabelMap.comments!}</th>
			</tr>
			</thead>
			<tbody>
				
			</tbody>
			</table>
		</div>
		                
      </div>
      <div class="modal-footer">
        <button type="sbmit" class="btn btn-sm btn-primary navbar-dark" data-dismiss="modal">Close</button>
      </div>
    </div>
  </div>
</div>
</div>

</#macro>