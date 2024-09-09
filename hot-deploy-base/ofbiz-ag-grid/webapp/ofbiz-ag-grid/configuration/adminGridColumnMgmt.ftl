<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<style>
    body {
    margin: 0;
    font-family: "Lato", sans-serif;
    }
    .table>tbody>tr>td{
    padding: 5px;
    }
    .table>thead>tr>th{
    padding: 5px;	
    }
</style>
<style>
/* Chrome, Safari, Edge, Opera */
input::-webkit-outer-spin-button,
input::-webkit-inner-spin-button {
  -webkit-appearance: none;
  margin: 0;
}

/* Firefox */
input[type=number] {
  -moz-appearance: textfield;
}

.ui-sortable tr {
	cursor:pointer !important;
}
		
.ui-sortable tr:hover {
	background: rgb(90 73 183 / 47%) !important;
}
</style>
<div class="row">
    <div id="main" role="main">
    	<#-- 
    	<#assign extra='<a href="#" onclick="javascript: onSubmit();" class="btn btn-xs btn-primary"><i class="fa fa-edit" aria-hidden="true"></i> Update</a>' />
    	-->
    	<#assign extra = '<a href="#" data-toggle="modal" data-target="#updateConfirm" class="btn btn-xs btn-primary" title="Save Instance"><i class="fa fa-save" aria-hidden="true"></i> Save Instance</a>' />
        <#assign title = uiLabelMap.AdminGridColumnManagement! />
        <#--
        <#if gridPreference?exists && gridPreference?has_content>
        	<#assign title = title + " - "+ gridPreference.description!gridPreference.name?if_exists />
        </#if> -->
        <@sectionFrameHeader 
	        title=title!
	        extra=extra?if_exists
	        />
	    <#assign gridColumnList = Static["org.fio.ag.grid.util.DataUtil"].getColumns(delegator, requestParameters.gridInstanceId! , 'admin','ADMIN')?if_exists />
	    <#if gridColumnList?has_content>
	    <form method="post" action="updateAdminGridInstance" name="adminGridUpdateForm" id="adminGridUpdateForm" data-toggle="validator">
	    	<@inputHidden id="gridInstanceId" value="${requestParameters.gridInstanceId!}" />
	    	<@inputHidden id="gridUserId" value="${requestParameters.gridUserId!'admin'}" />
	    	<@inputHidden id="gridRole" value="${requestParameters.gridRole!'ADMIN'}" />
        <div class="col-md-12 col-lg-12 col-sm-12">
            <div class="row">
                <div class="col-3">
                    <#assign columnDefs = gridColumnList.columnDefs!>
                    
                    <#assign gridJsonMap = gridColumnList.gridJsonMap!>
                    
                    
                    <h1 class=""><#if gridPreference?exists && gridPreference?has_content>${gridPreference.description!gridPreference.name?if_exists} </#if>${uiLabelMap.AdminInstance!}</h1>
                    <hr/>
                    <div class="col-md-12 col-lg-12 col-sm-12">
						<input type="text" class="form-control input-sm" value="" id="searchField" name="searchField" placeholder="Search" autocomplete="off">
				    </div>
				    <hr/>
				    <h3>${uiLabelMap.ListofFields!}</h3>
                    <div class="table-responsive">
                        <table class="table w-auto">
                            <thead class="thead-light">
                                <tr>
                                    <th>${uiLabelMap.FieldName!}</th>
                                   	<th>${uiLabelMap.IsHide!} <input type="checkbox" name="select-all" id="select-all" title="Select All" style="vertical-align: text-bottom;" /></th>
                                </tr>
                            </thead>
                            <tbody>
                            	<#assign seqId = 1 />
                                <#list columnDefs as column>
                                <#-- <a href="#${column.field!}">${column.headerName!}</a> -->
                                <tr>
                                	<input type="hidden" class="priority" value="${seqId!}" name="${column.field!}_SeqId" id="${column.field!}_SeqId" />
                                	<#assign columnStr = Static["org.fio.admin.portal.util.DataUtil"].convertToJson(column!)?if_exists />
                                    <td><a id="${column.field!}_field" class="" onclick="javascript: loadColumnData('${columnStr!}');">${column.headerName!}</a></td>
                                    <@inputHidden id="${column.field!}" value="${columnStr!}"/>
                                    <td>
                                    	<#assign checked = false />
                                    	<#if column.hide?exists && column.hide?has_content>
	                                        <#assign isActive = column.hide?string('Y','N') />
	                                        <#if "Y" == isActive>
	                                        <#assign checked = true />	
	                                        </#if>
                                        </#if>
                                        
                                        <@checkboxField
	                                        id="${column.field!}"
	                                        name="isHide"
	                                        class="form-check-input checkMe"
	                                        value=isActive!
	                                        checked = checked!
	                                        />
                                    </td> 
                                </tr>
                                <#assign seqId = seqId+1 />
                                </#list>
                                <@inputHidden 
				                	id="fieldToHide"
				                    />
                            </tbody>
                        </table>
                    </div>
                </div>
                <div class="col-9">
                    <div class="col-12">
                        <div class="row">
                            <div class="col-6">
                                <@dropdownCell
	                                label="${uiLabelMap.Pagination!}"
	                                id="pagination"  
	                                placeholder="Please select"
	                                options=booleanList!
	                                value="${gridJsonMap.pagination?string('true','false')!'true'}"
	                                required=true
	                                dataError="Please select"
	                                />
                                <@dropdownCell
	                                label="${uiLabelMap.PaginationPageSize!}"
	                                id="paginationPageSize"  
	                                placeholder="Select Page Size"
	                                options=pageSizeList!
	                                value="${gridJsonMap.paginationPageSize!'20'}"
	                                required=true
	                                dataError="Please select page size"
	                                />
                                <@dropdownCell
	                                label="${uiLabelMap.Filter!}"
	                                id="filter"  
	                                placeholder="Please select"
	                                options=booleanList!
	                                value="${gridJsonMap.filter?string('true','false')!'true'}"
	                                />
                                <@dropdownCell
	                                label="${uiLabelMap.FloatingFilter!}"
	                                id="floatingFilter"  
	                                placeholder="Please select"
	                                options=booleanList!
	                                value="${gridJsonMap.floatingFilter?string('true','false')!'true'}"
	                                />
                                <@dropdownCell
	                                label="${uiLabelMap.DomLayout!}"
	                                id="domLayout"  
	                                placeholder="Please select"
	                                options=domLayoutList!
	                                value="${gridJsonMap.domLayout!'autoHeight'}"
	                                />
                                <h3 class="pg-light">${uiLabelMap.Components!} <small style="font-size: 60%;"><span class="text-danger"> &#42;</span><i> Please enter custom components name with comma separator.</i></small></h3>
                                <div class="clearfix"></div>
                                <#assign components = gridJsonMap.components />
                                <#if components?has_content>
                                	<#assign gridComponents = [] />
                                	<#list components.entrySet() as entry>
                                		<#assign gridComponents = gridComponents + [entry.value]!/>
                                	</#list>
                                </#if>
                                <@inputArea
	                                id="components"
	                                label="${uiLabelMap.Components!}"
	                                value="${gridComponents?join(',')!''}"
	                                placeholder="Please enter the components name"
	                                rows="2"
	                                />
                            </div>
                            <div class="col-6">
                                <h3 class="pg-light">${uiLabelMap.Custom!}</h3>
                                <div class="clearfix"></div>
                                <#assign custom = gridJsonMap.custom! />
                                <@inputRow
	                                label="${uiLabelMap.UniqueField!}"
	                                id="dataUniqueIdField"
	                                placeholder ="${uiLabelMap.UniqueField!}"
	                                value="${custom.dataUniqueIdField!''}"
	                                required=true
	                                dataError="Please enter any one of primary field"
	                                />
                                <@dropdownCell
	                                label="${uiLabelMap.RowSelection!}"
	                                id="rowSelection"  
	                                placeholder="Please select"
	                                options=rowSelectionList!
	                                value="${custom.rowSelection!''}"
	                                />
                                <h3 class="pg-light">${uiLabelMap.CSVExport!}</h3>
                                <div class="clearfix"></div>
                                <#assign csvExportOptions = custom.csvExportOptions! />
                                <@inputRow
	                                label="${uiLabelMap.FileName!}"
	                                id="fileName"
	                                placeholder ="${uiLabelMap.FileName!}"
	                                value="${csvExportOptions.fileName!''}"
	                                />
                                <@dropdownCell
	                                label="${uiLabelMap.SkipHeader!}"
	                                id="skipHeader"  
	                                placeholder ="${uiLabelMap.skipHeader!}"
	                                options=booleanList!
	                                value="${csvExportOptions.skipHeader?string('true','false')!'true'}"
	                                />        
                            </div>
                        </div>
                    </div>
                    <hr />
                    <div class="col-12">
                        <div class="row">
                            <div class="col-6">
                                <h3>${uiLabelMap.ColumnProperty!} <small style="font-size: 60%;"><span class="text-danger"> &#42;</span><i>Please select anyone of the column to see the properties</i></small></h3>
                                <@inputRow
	                                label="${uiLabelMap.HeaderName!}"
	                                id="headerName"
	                                placeholder ="${uiLabelMap.HeaderName!}"
	                                />
                                <@inputRow
	                                label="${uiLabelMap.Field!}"
	                                id="field"
	                                placeholder ="${uiLabelMap.Field!}"
	                                />
                                <@dropdownCell
	                                label="${uiLabelMap.Filter!}"
	                                id="columnFilter"  
	                                placeholder ="${uiLabelMap.Filter!}"
	                                options=gridColumnFilterList!
	                                /> 
                                <@dropdownCell
	                                label="${uiLabelMap.Hide!}"
	                                id="hide"  
	                                placeholder ="${uiLabelMap.Hide!}"
	                                options=booleanList!
	                                />    
                                <@dropdownCell
	                                label="${uiLabelMap.LockPosition!}"
	                                id="lockPosition"  
	                                placeholder ="${uiLabelMap.LockPosition!}"
	                                options=booleanList!
	                                />    
                                <@dropdownCell
	                                label="${uiLabelMap.Sortable!}"
	                                id="sortable"  
	                                placeholder ="${uiLabelMap.Sortable!}"
	                                options=booleanList!
	                                /> 
                                <@inputRow
	                                label="${uiLabelMap.CellRenderer!}"
	                                id="cellRenderer"
	                                placeholder ="${uiLabelMap.CellRenderer!}"
	                                />
	                            <div class="form-group row">
								    <label class="col-sm-4 field-text"></label>
								    <div class=" col-sm-7 left">
								       <@button id="updateColProp" label="Add to Save" onclick="javascript: appendColumnProperties();"/>
								    </div>
								</div>
                            </div>
                            <div class="col-6">
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        </form>
        <#else>
        	<div class="row justify-content-md-center">
				<div class="pt-2">
					<div class="alert alert-danger fade show">
						<strong>Warning!</strong> Admin instance is empty! - ${requestParameters.gridInstanceId!}
					</div>	
				</div>
	   		</div>	
        </#if>
    </div>
</div>
<script>
	$(function(){
		$('input[type="checkbox"]').each(function(){
		    var $t=$(this);
		    var set = $t.prop('checked') ? 'Y' : 'N';
		    $('input[name="'+$t.attr('name')+'"]').val(set);
		}); 
		
		var options = $("input[name='isHide']:checked").map(function() {return this.id;}).get().join(',');
		$('#fieldToHide').val(options);
	});
	
	$("#select-all").click(function () {
		$('input:checkbox').not(this).prop('checked', this.checked);
		
		$('input[type="checkbox"]').each(function(){
		    var $t=$(this);
		    var set = $t.prop('checked') ? 'Y' : 'N';
		    $('input[name="'+$t.attr('name')+'"]').val(set);
		});
	});
	
	$(document).on('change', '.checkMe', function () {
	    var $t=$(this);
		var set = $t.prop('checked') ? 'Y' : 'N';
		$('input[name="'+$t.attr('name')+'"]').val(set);
		if("N" === set)
			$("#select-all").prop("checked", false);
	});
	
	function onFormSubmit(){
		$('#updateConfirm').modal('hide');
		var options = $("input[name='isHide']:checked").map(function() {return this.id;}).get().join(',');
	  	$('#fieldToHide').val(options);
		$("#adminGridUpdateForm").submit();
	}
	
	$("#save-btn").click(function () {
	  alert( "Handler for .click() called." );
	  onFormSubmit();
	});
	function appendColumnProperties(){
		var headerName = $('#headerName').val();
        var field = $('#field').val();
        var columnFilter = $('#columnFilter').val();
        var hide = $('#hide').val();
        var lockPosition = $('#lockPosition').val();
        var sortable = $('#sortable').val();
        var cellRenderer = $('#cellRenderer').val();
        console.log("headerName-->"+headerName+"--field-->"+field+"--columnFilter-->"+columnFilter+"--hide-->"+hide+"--lockPosition-->"+lockPosition+"--sortable-->"+sortable+"--cellRenderer-->"+cellRenderer);
        var columnMap = new Map();
        if(headerName != null && headerName !="" && headerName !="undefined")
        	columnMap.set("headerName",headerName);
        if(field != null && field !="" && field !="undefined")
        	columnMap.set("field",field);
        if(columnFilter != null && columnFilter !="" && columnFilter !="undefined")
        	columnMap.set("filter",columnFilter);
        if(hide != null && hide !="" && hide !="undefined")
        	columnMap.set("hide", hide.toLowerCase() == 'true' ? true : false);
        if(lockPosition != null && lockPosition !="" && lockPosition !="undefined")
        	columnMap.set("lockPosition",lockPosition.toLowerCase() == 'true' ? true : false);
        if(sortable != null && sortable !="" && sortable !="undefined")
        	columnMap.set("sortable",sortable.toLowerCase() == 'true' ? true : false);
        if(cellRenderer != null && cellRenderer !="" && cellRenderer !="undefined")
        	columnMap.set("cellRenderer",cellRenderer);
        
        let jsonObject = {};  
        columnMap.forEach((value, key) => {  
		    jsonObject[key] = value  
		});  
		//console.log("Test------->"+JSON.parse(JSON.stringify(jsonObject)));
        var columnJson = JSON.stringify(jsonObject);
        $('#'+field).val(columnJson);
        showAlert('info','Column Properties are stored temporarily. Please click Save Instance to store permanently');
        $('#columnFilter').dropdown('clear');
        $('#hide').dropdown('clear');
        $('#lockPosition').dropdown('clear');
        $('#sortable').dropdown('clear');
        $('#headerName').val('');
        $('#field').val('');
        $('#cellRenderer').val('');
	}
	
    function loadColumnData(columnDef) {
    	var columnMap = new Map();
    	var columnProp = JSON.parse(columnDef);
    	
    	var headerName = columnProp.headerName;
        var field = columnProp.field;
        var filter = columnProp.filter;
        var hide = columnProp.hide+'';
        var lockPosition = columnProp.lockPosition+'';
        var sortable = columnProp.sortable+'';
        var cellRenderer = columnProp.cellRenderer;
    	
    	/*
    	columnDef
		  .trim()
		  .slice(1, -1)
		  .split(',')
		  .forEach(function(v) {
		    var val = v.trim().split('=');
		    columnMap.set(val[0],val[1]);
		  });
		
		var headerName = columnMap.get("headerName");
        var field = columnMap.get("field");
        var filter = columnMap.get("filter");
        var hide = columnMap.get("hide");
        var lockPosition = columnMap.get("lockPosition");
        var sortable = columnMap.get("sortable");
        var cellRenderer = columnMap.get("cellRenderer");
        */
        $('#columnFilter').dropdown('clear');
        $('#hide').dropdown('clear');
        $('#lockPosition').dropdown('clear');
        $('#sortable').dropdown('clear');
        $('#headerName').val(headerName);
        $('#field').val(field);
        $('#columnFilter').dropdown('set value', filter).dropdown('destroy').dropdown();
        $('#hide').dropdown('set value', hide).dropdown('destroy').dropdown();
        $('#lockPosition').dropdown('set value', lockPosition).dropdown('destroy').dropdown();
        $('#sortable').dropdown('set value', sortable).dropdown('destroy').dropdown();
        $('#cellRenderer').val(cellRenderer);
        
    }
    $("#searchField").on("keyup", function() {
	    var value = $(this).val();
	    $("table tr").each(function(index) {
	        if (index !== 0) {
	            $row = $(this);
	            var id = $row.find("td:first").text();
	            if (id.toLowerCase().indexOf(value) !== 0) {
	                $row.hide();
	            }
	            else {
	                $row.show();
	            }
	        }
	    });
	});
	
	$(document).ready(function() {
	    //Helper function to keep table row from collapsing when being sorted
		var sortingHelper = function(e, tr) {
			var $originals = tr.children();
			var $helper = tr.clone();
			$helper.children().each(function(index) {
			  $(this).width($originals.eq(index).width())
			});
			return $helper;
		};
	
		//Make diagnosis table sortable
		$("#adminGridUpdateForm tbody").sortable({
	    	helper: sortingHelper,
			stop: function(event,ui) {reorderTable('#adminGridUpdateForm')}
		}).disableSelection();
	});
	
	//Renumber table rows
	function reorderTable(tableId) {
		$(tableId + " tr").each(function() {
			count = $(this).parent().children().index($(this)) + 1;
			//$(this).find('.priority').html(count);
			$(this).find('.priority').val(count);
		});
	}
</script>
<div class="modal fade" id="updateConfirm" tabindex="-1" role="dialog" aria-labelledby="updateConfirm" aria-hidden="true">
    <div class="modal-dialog" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title" id="exampleModalLabel">Confirmation</h5>
                <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                <span aria-hidden="true">&times;</span>
                </button>
            </div>
            <div class="modal-body">
                <h3>Do you want to save admin instance?</h3>
                If yes, then all the user preferences will reset as default.
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-dismiss="modal">Cancel</button>
                <button type="button" id="save-btn" name="save-btn" onclick="javascript: onFormSubmit();" class="btn btn-primary">Yes</button>
            </div>
        </div>
    </div>
</div>