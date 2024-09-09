<#include "component://ab-ag-grid-support/webapp/ab-ag-grid-support/lib/ag_grid_header.ftl"/>
<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<div class="row">
	<div id="main" role="main">
		<div class="col-lg-12 col-md-12 col-sm-12 dash-panel">
		<@sectionFrameHeader title="${uiLabelMap.apiLogs!}" />
			<div class="panel-heading" role="tab" id="headingTwo">
				<h4 class="panel-title">
					<a role="button" data-toggle="collapse" data-parent="#accordionMenu"
						href="#accordionDynaBase" aria-expanded="true"
						aria-controls="collapseOne"> ${uiLabelMap.MainFilter}
					</a>
				</h4>
			</div>
			<div id="accordionDynaBase" class="panel-collapse collapse show" role="tabpanel" aria-labelledby="headingOne">
				<form action="apiLogs" method="post" id="searchForm" name="searchForm">
				<@inputHidden id="searchCriteria" />
					<div class="panel-body">
					<@dynaScreen
						instanceId="FIND_API_LOGS"
						modeOfAction="CREATE"
						/>
						<div class="row find-srbottom">
							<div class="col-lg-12 col-md-12 col-sm-12">
								<div class="text-right pd-cbx-lbls" style="padding-top: 0px;">
								<@button 
									label="${uiLabelMap.Search}"
									id="main-search-btn"/>
								</div>
							</div>
						</div>
					</div>
				</form>
			</div>
		</div>
		<div class="col-m-12 col-sm-12 col-lg-12 dash-panel">
			<#-- <@AgGrid
				gridheadertitle=uiLabelMap.ListOfAPIRequestAndResponseLogs
				userid="${userLogin.userLoginId}"
				instanceid="API_LOG"
				autosizeallcol="true"
				insertBtn=false
				updateBtn=false
				removeBtn=false
				gridoptions='{"pagination": true, "paginationPageSize": 10 }' />
			<script type="text/javascript" src="/admin-portal-resource/js/ag-grid/apiLog/api-log.js"></script>-->
<input type="hidden" id="userId" value="${userLogin.userLoginId!}"/>
<input type="hidden" id="externalLoginKey" value="${requestAttributes.externalLoginKey!}"/>

		<@fioGrid 
			id="api-log-Grid"
			instanceId="API_LOG"
			jsLoc="/admin-portal-resource/js/ag-grid/apiLog/api-log.js"
			headerLabel=uiLabelMap.ListOfAPIRequestAndResponseLogs!
			headerId="api-log-grid-action-container"
			subFltrClearId="api-log-sub-filter-clear-btn"
			savePrefBtnId="api-log-save-pref-btn"
			clearFilterBtnId="api-log-clear-filter-btn"
			headerBarClass="grid-header-no-bar"
			savePrefBtn=false
			clearFilterBtn=false
			exportBtn=true
			subFltrClearBtn=false
			exportBtnId="api-log-list-export-btn"
			/>
		</div>
	</div>
</div>
<script>
	$(document).ready(function() {
		initDateRange("lastUpdatedTxStampFrom_date_picker", "lastUpdatedTxStampTo_date_picker", null, null);
	});
</script>
         <#-- 
         <div class="table-responsive">
         	<div class="loader text-center" id="loader" sytle="display:none;">
                  <span></span>
                  <span></span>
                  <span></span>
                </div>
               <div id="grid1" style="width: 100%;" class="ag-theme-balham"></div>
               <script type="text/javascript" src="/admin-portal-resource/js/ag-grid/external-api/external-api.js"></script>
        </div>
         -->
<#-- Display Req Json -->
<div id="requestJson" class="modal fade mt-2" role="dialog">
  <div class="modal-dialog modal-lg">
    <!-- Modal content-->
    <div class="modal-content rounded-5 border-0">
      <div class=" border-bottom-1 pt-2 pb-1 bg-light rounded-5">
        <h4 class="text-center">Request JSON</h4>
      </div>
      <div class="modal-body pb-0">
        <div class="pt-3 break-word" id="requestData"></div>
        <div class="row border-top">
          <div class="col-12 p-1 border-center border-light"><a href="#" data-dismiss="modal" class="btn btn-sm bg-light1 rounded-0 btn-block text-dark">Ok</a></div>
          <#-- <div class="col-6 p-1"><a href="#" data-dismiss="modal" class="btn btn-sm bg-light1 rounded-0 btn-block text-dark">No</a></div> -->
        </div>
        <div class="clearfix"> </div>
      </div>
    </div>
  </div>
</div>
<#-- Display Response Json -->
<div id="responseJson" class="modal fade mt-2" role="dialog">
  <div class="modal-dialog modal-lg">
    <!-- Modal content-->
    <div class="modal-content rounded-5 border-0">
      <div class=" border-bottom-1 pt-2 pb-1 bg-light rounded-5">
        <h4 class="text-center">Response JSON</h4>
      </div>
      <div class="modal-body pb-0">
        <div class="pt-3 break-word" id="responseData"></div>
        <div class="row border-top">
          <div class="col-12 p-1 border-center border-light"><a href="#" data-dismiss="modal" class="btn btn-sm bg-light1 rounded-0 btn-block text-dark">Ok</a></div>
          <#-- <div class="col-6 p-1"><a href="#" data-dismiss="modal" class="btn btn-sm bg-light1 rounded-0 btn-block text-dark">No</a></div> -->
        </div>
        <div class="clearfix"> </div>
      </div>
    </div>
  </div>
</div>
<script>
function loadRequest(reqVal){
    if(reqVal != null){
        $("#requestData").html("");
        $.ajax({
            type: "POST",
            url: "getJsonData",
            data: { "logId": reqVal , "type":"REQUEST"},
            async: false,
            success: function(data) {
                //console.log("--result success-----"); 
                if(data.code == 200) {
                  $("#requestData").html(data.json);
                }
            }
        });
    }
    $('#requestJson').modal('toggle');
    $('#requestJson').modal('show');
    $('#requestJson').modal('hide');
    $('#requestJson').modal({
        show: 'true'
    }); 
}
function loadResponse(respVal){
    if(respVal != null){
      $("#responseData").html("");
      $.ajax({
            type: "POST",
            url: "getJsonData",
            data: { "logId": respVal , "type":"RESPONSE"},
            async: false,
            success: function(data) {
              //console.log("--result success-----"); 
              if(data.code == 200) {
                $("#responseData").html(data.json);
              }
            }
        });
    }
    $('#responseJson').modal('toggle');
    $('#responseJson').modal('show');
    $('#responseJson').modal('hide');
    $('#responseJson').modal({
        show: 'true'
    }); 
}

<#--function prepareForm(){
    var logId = $("#logId").val();
    var serviceName = $("#serviceName").val();
    var systemName = $("#systemName").val();
    var channelId = $("#channelId").val();
    
    item = {}
    item ["ofbizApiLogId"] = logId;
    item ["serviceName"] = serviceName;
    item ["systemName"] = systemName;
    item ["channelId"] = channelId;
    
    jsonString = JSON.stringify(item);
    $("#searchCriteria").val(jsonString);
    $("#searchForm").submit();
    
}-->
</script>
 <script>
 /* 
    function doSearch(){
       
        loadAgGrid();
    }
    $("#logId").change(function() {
         loadAgGrid();
    });
    $("#serviceName").change(function() {
         loadAgGrid();
    });
    $("#systemName").change(function() {
         loadAgGrid();
    });
    $("#channelId").change(function() {
         loadAgGrid();
    });
    
    */
</script>