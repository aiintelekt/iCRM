<#include "component://bootstrap/lib/ofbizFormMacros.ftl"/>
<div class="row">
	<div id="main" role="main">
		
		<#assign extras=''/>
		<div class="col-lg-12 col-md-12 col-sm-12 dash-panel">
			<div><@sectionFrameHeader title="${uiLabelMap.ExportXml!}"/></div>
			<div class="row padding-r" id="">
            		<div class="col-md-4 col-sm-4 form-horizontal">
            			<@radioInputCell 
							name="exportBy"
							id="exportBy"
							options=exportBy!
							value="BY_ENTITY"
							label="Export By"
							/>	
            		</div>	
            	</div>
			<form id="exportXmlDataForm" name="exportXmlDataForm" method="post" action="#" data-toggle="validator" novalidate="novalidate">
				
				<div class="row padding-r">
            		<div class="col-md-4 col-sm-4 form-horizontal"  id="by_entity">
                        <@dropdownCell 
							id="exportEntityName"
							label="Entity"
							options=entities!
							allowEmpty=true
							placeholder="Please select entity"
							value=parameters.exportEntityName!
							/>
                    </div>
                    <div class="col-md-4 col-sm-4 form-horizontal" style="display:none;" id="by_sql_script">
                        <@inputArea  
                        	label="SQL Query" 
                        	id="sql_script" 
                        	rows="4"
                        	labelColSize="col-sm-4"
                        	inputColSize="col-sm-8"
                        	/>
                    </div>
                    
                    <div class="col-md-4 col-sm-4 form-horizontal">
                        <@inputRow
							id="exportFileName"
							label="File Name"
							/>
                    </div>
                </div>
                
                <div class="row padding-r" id="entity_fields" style="display:none;">
            		
            	</div>	
            	<div class="row padding-r" id="">
            		<div class="col-md-4 col-sm-4 form-horizontal">
						<div class="form-group row exportEntityName" style="">
				    		<label class="col-sm-4 field-text" id=""></label>
				            <div class="col-sm-8 exportEntityName-input ">
				            	<input type="button" id="export-btn" class="btn btn-sm btn-primary disabled" value="Export"/>
								<@reset
								id="reset-btn"
								label="${uiLabelMap.Reset}"
								/>
				            </div>
				        </div>
            		</div>	
            	</div>	
            	
                <#-- 
                <div class="row padding-r" id="entity_fields">
            			
            			<#if fieldMap?exists && fieldMap?has_content>
            				<#assign count = 0 />
							<#list fieldMap.entrySet() as entry>
								<#if count == 0 >
									<div class="col-md-4 col-sm-4 form-horizontal">
									<@inputRow
										id=entry.key!
										label=entry.value!
										/>
									</div>	
								</#if>
								<#if count == 1 >
									<div class="col-md-4 col-sm-4 form-horizontal">
									<@inputRow
										id=entry.key!
										label=entry.value!
										/>
									</div>	
								</#if>
								<#if count == 2 >
									<div class="col-md-4 col-sm-4 form-horizontal">
									<@inputRow
										id=entry.key!
										label=entry.value!
										/>
									</div>
									
								</#if>
								
								<#assign count = count+1 />
								<#if count == 3>
									<#assign count = 0 />	
								</#if>
							</#list>
						</#if>	
                </div> -->
            	
				
				<div class="offset-md-2 col-sm-10">
					
				</div>
				<br>
			</form>
		</div>
	</div>
</div>
<script>
$(function(){
	$('#exportEntityName_error').html('');
	$('#sql_script_error').html('');
	
	$('input[type=radio][name=exportBy]').change(function(){
		$('#exportEntityName_error').html('');
		$('#sql_script_error').html('');
		var exportBy = $("input[type='radio'][name=exportBy]:checked").val();
		if("BY_SQL_SCRIPT" === exportBy){
			$("#exportEntityName").dropdown('clear');
			$("#exportEntityName").dropdown('set selected', '');
			$("#exportEntityName").dropdown('refresh');
			$("#by_entity").hide();
			$("#by_sql_script").show();
		} else{
			$("#by_entity").show();
			$("#by_sql_script").hide();
		}
	});
	
	$("#entity_fields").html('');
	$("#entity_fields").hide();
	$('#exportEntityName').change(function(){
		var selectedEntityName = $(this).val();
		// get the table columns
		if(selectedEntityName){
			let fields = "";
			$.post("getEntityFields",{"targetTable":selectedEntityName},function(data){
				if(data !=null && data !=""){
					let count = 0;				
					for(var i=0;i<data.length;i++){
						
						if(count == 0){
							fields = fields + '<div class="col-md-4 col-sm-4 form-horizontal"><div class="form-group row '+data[i].value+'" id="'+data[i].value+'_row" style="">';
							fields = fields + '<label class="col-sm-4 field-text" id="'+data[i].value+'_label">'+data[i].label+' </label>';
							fields = fields + '<div class=" col-sm-8 left">';
							fields = fields + '<input type="text" class="form-control input-sm" value="" id="'+data[i].value+'" name="'+data[i].value+'" placeholder="" autocomplete="off">';
							fields = fields + '<div class="help-block with-errors" id="'+data[i].value+'_error"></div></div></div></div>';
						}
						if(count == 1){
							fields = fields + '<div class="col-md-4 col-sm-4 form-horizontal"><div class="form-group row '+data[i].value+'" id="'+data[i].value+'_row" style="">';
							fields = fields + '<label class="col-sm-4 field-text" id="'+data[i].value+'_label">'+data[i].label+' </label>';
							fields = fields + '<div class=" col-sm-8 left">';
							fields = fields + '<input type="text" class="form-control input-sm" value="" id="'+data[i].value+'" name="'+data[i].value+'" placeholder="" autocomplete="off">';
							fields = fields + '<div class="help-block with-errors" id="'+data[i].value+'_error"></div></div></div></div>';
						}
						if(count == 2){
							fields = fields + '<div class="col-md-4 col-sm-4 form-horizontal"><div class="form-group row '+data[i].value+'" id="'+data[i].value+'_row" style="">';
							fields = fields + '<label class="col-sm-4 field-text" id="'+data[i].value+'_label">'+data[i].label+' </label>';
							fields = fields + '<div class=" col-sm-8 left">';
							fields = fields + '<input type="text" class="form-control input-sm" value="" id="'+data[i].value+'" name="'+data[i].value+'" placeholder="" autocomplete="off">';
							fields = fields + '<div class="help-block with-errors" id="'+data[i].value+'_error"></div></div></div></div>';
						}
						count = count+1;
						if(count ==3) count=0;
					}
				}
				$("#entity_fields").show();
				$("#entity_fields").html(fields);
			});
		} else {
			$("#entity_fields").html('');
			$("#entity_fields").hide();
		}
		
	});

});
$("#export-btn").on('click', function(){
	var exportBy = $("input[type='radio'][name=exportBy]:checked").val();
	let valid = true;
	if("BY_ENTITY" === exportBy){
		let exportEntityName = $('#exportEntityName').val();
		if(!exportEntityName) {
			$('#exportEntityName_error').html('<ul class="list-unstyled"><li>Please select entity.</li></ul>');	
			valid=false;
		} 
		else{
			$("#exportXmlDataForm").attr("action", "exportXmlData");
		}
	}
	if("BY_SQL_SCRIPT" === exportBy){
		let sql_script = $('#sql_script').val();
		if(!sql_script) {
			$('#sql_script_error').html('Please enter the SQL Query.');
			valid=false;
		} 
		else{
			$("#exportXmlDataForm").attr("action", "exportXmlDataBySqlQuery");
		}
	}
    if(valid){
    	$("#exportXmlDataForm").submit();
    }
});

</script>
<#-- 
<div class="row" style="width:100%">
	<div class="col-lg-12 col-md-12 col-sm-12 dash-panel">
		<@AgGrid
			gridheadertitle=uiLabelMap.DataList
			gridheaderid="TABLE_DATA_LIST-grid-action-container"
			savePrefBtn=true
			clearFilterBtn=true
			exportBtn=false
			insertBtn=false
			updateBtn=false
			removeBtn=false
			headerextra=""
			userid="${userLogin.userLoginId}"
			removeBtnId=""
			refreshPrefBtnId="table-data-refresh-pref-btn"
			savePrefBtnId="table-data-save-pref-btn"
			clearFilterBtnId="table-data-clear-filter-btn"
			subFltrClearId="table-data-sub-filter-clear-btn"
			exportBtnId="table-data-export-btn"
			shownotifications="true"
			instanceid="TABLE_DATA_LIST"
			autosizeallcol="true"
			debug="false"
			/>
		<script type="text/javascript" src=""></script>
	</div>
</div>
-->