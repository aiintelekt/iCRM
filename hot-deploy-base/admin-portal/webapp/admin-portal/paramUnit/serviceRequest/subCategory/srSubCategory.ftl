<#include "component://bootstrap/lib/ofbizFormMacros.ftl"/>
<#include "component://homeapps/webapp/homeapps/user-audit/audit_page_header.ftl"/>

      <div class="row">
        <div id="main" role="main">
          <#assign extra='<a href="activeSrSubCategory" class="btn btn-xs btn-primary"><i class="fa fa-chevron-circle-left" aria-hidden="true"></i> Back</a>' />
         
<form action ="<@ofbizUrl>createSrSubCategory</@ofbizUrl>" method="post" id="createSrSubCategoryForm" data-toggle="validator">

<#include "component://homeapps/webapp/homeapps/user-audit/audit_from_header.ftl"/>

          <div class="col-md-12 col-lg-12 col-sm-12 dash-panel">
         
         <@sectionFrameHeader title="${uiLabelMap.CreateSrSubCategory!}" extra=extra />
          
          	<@dynaScreen 
				instanceId="PARAM_SR_SUB_AREA"
				modeOfAction="CREATE"
				/>
               
          <div class="form-group offset-2">
            <div class="text-left ml-1 pad-10">
              
              <@formButton
                     btn1type="button"
                     btn1label="${uiLabelMap.Save}"
                     btn1onclick="return srSubCategoryValidation();"
                     btn2=true
                     btn2type="reset"
                     btn2label="${uiLabelMap.Clear}"
                     btn2id="sr-subcategory-reset-btn"
                   />
               
                    
            </div>
          </div>
          
          </div>
          </form>
        </div>
      </div>
 <script>
    $('#typeId').change(function(){
    var selectedItem = $(this).val();
    
    $.post("getCategoryList",{"typeId":selectedItem},function(data){
            var len = data.length;
            var options = '<option value="" data-content="<span>Please Select</span>" selected>Please Select</option>';
            if(data !=null && data !=""){
               for(var i=0;i<data.length;i++){
                     options += '<option value="'+data[i].code+'">'+data[i].description+'</option>';
               }
            }
                $("#srCategoryId").empty();
                $("#srCategoryId").append(options);
                $("#srCategoryId").dropdown("refresh");
                
          });
    })
    
 /*$( document ).ready(function() {
     getCategoryFunction();
     });
   function getCategoryFunction() 
    {
      var first = document.getElementById("pe").value;
      var selectedItem = $(typeId).val();
      alert('hi'+selectedItem);
    $.post("getsubCategoryList",{"typeId":selectedItem},function(data){
            var len = data.length;
            var options = '<option value="" data-content="<span>Please Select</span>" selected>Please Select</option>';
            if(data !=null && data !=""){
               for(var i=0;i<data.length;i++){
               if(first==data[i].code)
                   {
                    options += '<option value="'+data[i].code+'" selected>'+data[i].description+'</option>';
                   } else{
                  options += '<option value="'+data[i].code+'">'+data[i].description+'</option>';
                   }
               }
            }
                $("#srCategoryId").append(options);
                $("#srCategoryId").dropdown("refresh");
          });
}*/
	$('#srCategoryId').change(function () {
		$('#srCategoryId_error').html("");
	});
	$('#srSubArea').click(function () {
		$('#srSubArea_error').html("");
	});
	$('#statusId').change(function () {
		$('#statusId_error').html("");
	});
	$(document).ready(function() {
		$('#sr-subcategory-reset-btn').click(function() {
			$("#sequenceNumber").val('');
			$('#srSubArea').val('');
		});
	});
	function srSubCategoryValidation() {
		var srCategoryId = $('#srCategoryId').val();
		var srSubArea = $('#srSubArea').val();
		var statusId = $('#statusId').val();
		if(srCategoryId != "" && srSubArea != "" && statusId != ""){
			$.ajax({
				type: "POST",
				url: "validateSrCategory",
				async: true,
				data: {
					"srCategoryId": srCategoryId,
					"srSubArea" : srSubArea
				},
				success: function(data) {
					if(data.isAvailable == "Y"){
						$('#srSubArea_error').html("Entered SR SubCategory name already exists!!");
					}else{
						$('#createSrSubCategoryForm').submit();
					}
				}
			});
		return false;
		}else{
			if(srCategoryId == ""){
				$('#srCategoryId_error').html("Please select an item in the list.");
			}
			if(srSubArea == ""){
				$('#srSubArea_error').html("Please fill out this field");
			}
			if(statusId == ""){
				$('#statusId_error').html("Please select an item in the list.");
			}
		}
	}
   </script>
   
