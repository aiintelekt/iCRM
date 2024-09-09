<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
	
<div class="row">
<div id="main" role="main">
	<div class="col-lg-12 col-md-12 col-sm-12 dash-panel">
<@sectionFrameHeader title="${uiLabelMap.settings!}"  />

<form id="mainForm" method="post" action="<@ofbizUrl>uploadDynaConfiguration</@ofbizUrl>" data-toggle="validator" enctype="multipart/form-data">

<div class="row padding-r">

<div class="col-md-6 col-sm-6">

<@dropdownCell 
		id="uploadMode"
		label="${uiLabelMap.uploadMode}"
		placeholder="${uiLabelMap.uploadMode}"
		options=uploadModes
		required=true
		allowEmpty=false
		value="OVERIDE"
		/>	

<@inputRow id="uploadFile" type="file" label="Upload File" required=true/>

</div>

</div>

<div class="form-group offset-2">
	<div class="text-left ml-1 p-2">
      
      <@formButton
	     btn1type="submit"
	     btn1label="${uiLabelMap.Upload}"
	     btn2=true
	     btn2type="reset"
	     btn2label="${uiLabelMap.Clear}"
	   />
 	
	</div>
</div>

</form>
		  
</div>
</div>
</div>
<script>

jQuery(document).ready(function() {	

$('#uploadFile').on('change', function() { 
	const size = (this.files[0].size / 1024 / 1024).toFixed(2);
   	var fileInput = document.getElementById('uploadFile');
	var filePath = fileInput.value;
    var ext = filePath.substring(filePath.lastIndexOf('.') + 1);
    if(ext != "xml"){
    	showAlert ("error", "Only xml file allowed to upload!");
    	$("#uploadFile").val('');
    }  
    if (size > 5) { 
    	showAlert ("error", "The attachment is too large, preferred size is less than 5 MB");
    	$("#uploadFile").val('');
    }
});

});	

</script>
   