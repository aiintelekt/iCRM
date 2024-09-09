<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>

<div class="row">
    <div id="main" role="main">
        <div class="clearfix"></div>
        <form id="mainFrom" method="post" action="<@ofbizUrl>storeHelpContent</@ofbizUrl>" data-toggle="validator">
            <@inputHidden id="sectionId" value="HELP_CONTENT"/>
            <div class="col-lg-12 col-md-12 col-sm-12">
            	<div><@sectionFrameHeader title="Help Content" extra=extra! /></div>	
				<div class="row padding-r">
					<div class="col-md-6 col-sm-6">
						<@inputAutoComplete
							id="parameterId"
							label="Parameter ID"
							isAutoCompleteEnable="Y"
							onkeydown=true
							autoCompleteMinLength=0
							placeholder="Parameter ID"
							autoCompleteLabelFieldId="description"
							autoCompleteValFieldId="parameterId"
							autoCompleteFormId="helpContentFrom"
							autoCompleteUrl="/admin-portal/control/findGlobalParamters"
							onSelectfn="onCustomerSelect();"
							/>
							
						<@inputRichTextAreaRow
					        id="value"
					        label="Value"
					        labelColSize="col-sm-4"
					        inputColSize="col-sm-8"
					        height="200"
					        placeholder="Value"
					        value=""
					        editorType="LITE_RICH_TEXT"
		       				 />	
							
						
					</div>		
				</div>
			<#-- 
                <@dynaScreen 
					instanceId="CREATE_HELP_CONTENT"
					modeOfAction="CREATE"
					/> 
					-->
            </div>
            
            <div class="clearfix"></div>
            <div class="offset-md-2 col-sm-10">
            	<@formButton
                     btn1type="submit"
                     btn1label="${uiLabelMap.Save}"
                     btn1onclick=""
                     btn1id="create-cust-btn"
                     btn2=true
                     btn2onclick = "resetForm()"
                     btn2type="reset"
                     btn2label="${uiLabelMap.Clear}"
                   />
            </div>
        </form>
    </div>
</div>

<form id="helpContentFrom" method="post" action="#" data-toggle="validator">
	<@inputHidden id="storeId" value="HELP_CONTENT"/>
</form>

<script>
	function onCustomerSelect(){
		var parameterId= $("#parameterId_val").val();
		$.ajax({
			type: "POST",
	     	url: "/admin-portal/control/getGlobalParamter",
	        data: {"parameterId": parameterId},
	        async: false,
	        success: function(data) {
				var result=data.data;
				$('#mainFrom #value').summernote('code', result.value);
			},error: function(data) {
				//result=data;
				$("#mainFrom #value").val(data.value);
			}
		});
	}
</script>