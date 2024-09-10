<#include "component://bootstrap/lib/ofbizFormMacros.ftl"/>
<div class="row" style="width:100%" >
	<div class="col-lg-12 col-md-12 col-sm-12 dash-panel">
	<@AgGrid
		gridheadertitle="Segment Mapping"
		gridheaderid="${instanceId!}-grid-action-container"
		savePrefBtn=true
		clearFilterBtn=true
		exportBtn=true
		insertBtn=false
		updateBtn=false
		removeBtn=false
		userid="admin"
		refreshPrefBtnId="segmentMapping-refresh-pref-btn"
		savePrefBtnId="segmentMapping-save-pref-btn"
		clearFilterBtnId="segmentMapping-clear-filter-btn"
		subFltrClearId="segmentMapping-sub-filter-clear-btn"
		exportBtnId="segmentMapping-export-btn"
		instanceid="SEGMENT_MAPPING"
		autosizeallcol="true"
		/>
		<script type="text/javascript" src="/cf-resource/js/ag-grid/segment-mapping/segmentMappingList.js"></script>
	</div>
</div>