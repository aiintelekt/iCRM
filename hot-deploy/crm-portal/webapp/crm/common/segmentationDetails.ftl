<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<#assign requestURI = "viewContact"/>
<#if request.getRequestURI().contains("viewLead")>
   <#assign requestURI = "viewLead"/>
<#elseif request.getRequestURI().contains("viewAccount")>
   <#assign requestURI = "viewAccount"/>
</#if>
<div class="page-header border-b">
      <div class="float-right">
          <form name="segmentGroupsClear" method="post" action="<@ofbizUrl>${requestURI?if_exists}?partyId=${partyId?if_exists}</@ofbizUrl>" >
              <@inputHidden id="clearData" value="Y" />
              <@inputHidden id="activeTab" value="segmentation" />
          </form>
          <a href="javascript:document.segmentGroupsClear.submit();" ><span class="btn btn-xs btn-primary m5">Clear All Filters</span></a>
      </div>
      <h2 class="d-inline-block">Add Segment Value</h2>
   </div>

<div class="card-header mt-2 mb-3">      
   
	<form id="segmentForm" name="segmentForm" method="post" action="<@ofbizUrl>segmentForm</@ofbizUrl>" onsubmit="">
	
    <@inputHidden id="activeTab" value="segmentation" />	
   	<@inputHidden id="donePage" value="${requestURI?if_exists}"/>
   	<@inputHidden id="partyId" value="${partyId?if_exists}"/>
   	
   <#-- 
   <div class="col-sm-5 col-md-5">
      <@dropdownCell 
      id="segmentCode"
      label=uiLabelMap.segmentCode
      options=contactTypeList
      value=""
      required=false
      allowEmpty=true
      dataLiveSearch = true
      />
   </div>
    -->
    
	<div class="row">
		
	    <div class="col-md-2 col-sm-2">
	     	<@dropdownCell 
				id="segment_groupingCode"
				options=groupingCodeList
				required=false
				value=segmentSegmentCode.segment_groupingCode
				allowEmpty=true
				tooltip = uiLabelMap.groupingCode
				placeholder = uiLabelMap.groupingCode
				dataLiveSearch=true
				/>
	     </div>
    
   		<div class="col-md-2 col-sm-2">
     		<@dropdownCell
				id="segment_segmentCodeId"
				options=segmentCodeList
				required=false
				value=segmentSegmentCode.segment_segmentCodeId
				allowEmpty=true
				tooltip = uiLabelMap.segmentCode
				placeholder = uiLabelMap.segmentCode
				dataLiveSearch=true
				/>
     	</div>
     	
   	</div>
   
   	<div id="getSegValue">
      	<#-- Details Will Load ONChange -->
   	</div>
   
	</form>

</div>

<@pageSectionHeader title="List Segment Values" />
<#-- <div class="page-header border-0">
   <h2 class="">List Segment Values </h2>
</div> -->

<div class="card-header mt-2 mb-3">
   <form method="post" class="form-horizontal" data-toggle="validator">
   		
   		<@inputHidden id="activeTab" value="segmentation" />	
   		
      <div class="row">
      	
         <div class="col-md-2 col-sm-2">
	     	<@dropdownCell 
				id="filter_groupingCode"
				options=groupingCodeList
				required=false
				value=filterSegmentCode.filter_groupingCode
				allowEmpty=true
				tooltip = uiLabelMap.groupingCode
				placeholder = uiLabelMap.groupingCode
				dataLiveSearch=true
				/>
	     </div>
    
   		<div class="col-md-2 col-sm-2">
     		<@dropdownCell 
				id="filter_segmentCodeId"
				options=segmentCodeList
				required=false
				value=filterSegmentCode.filter_segmentCodeId
				allowEmpty=true
				tooltip = uiLabelMap.segmentCode
				placeholder = uiLabelMap.segmentCode
				dataLiveSearch=true
				/>
     	</div>
         <div class="clearfix"> </div>
			<div class="col-md-1 col-sm-1 pl-0">
         <@submit label="Find"/>
         </div>
         <div class="clearfix"> </div>
        	
      </div>
   </form>
   <div class="clearfix"> </div>
</div>
<@inputHidden id="segmentListData" value=partyClassificationListStr />
<div class="table-responsive">				
	<div id="segmentGrid" style="width: 100%;" class="ag-theme-balham"></div>   			
</div>
<script type="text/javascript" src="/crm-resource/js/ag-grid/segment/segment.js"></script>

<form name="removeSegment" id="removeSegment" action="removeSegmentValue" method="post">
	<input type="hidden" name="activeTab" value="segmentation" />	
   <#assign requestURI = "viewContact"/>
   <#if request.getRequestURI().contains("viewLead")>
   <#assign requestURI = "viewLead"/>
   <#elseif request.getRequestURI().contains("viewAccount")>
   <#assign requestURI = "viewAccount"/>
   </#if>
   <input type="hidden" name="donePage" value="${requestURI?if_exists}"/>
   <input type="hidden" name="partyId" value="${partyId?if_exists}"/>
   <input type="hidden" name="groupId"  id="groupId" value="">
   <input type="hidden" name="segmentValue" id="segmentValue" value="">
</form> 

<script type="text/javascript">
   	function callSubmit(a, b){
   			
   		$('#segmentValue').val(b);
    	$('#groupId').val(a);
    	var c= "";
    	if(a != "" && a!=null && b!="" && b!=""){
   			document.removeSegment.submit();
   		}
   		
   	}

jQuery(document).ready(function() {   

	loadSegmentTabSegmentCodeList("segment_groupingCode", "segment_segmentCodeId");
	$("#segment_groupingCode").change(function() {
		loadSegmentTabSegmentCodeList("segment_groupingCode", "segment_segmentCodeId");
	});
	
	loadSegmentTabSegmentCodeList("filter_groupingCode", "filter_segmentCodeId");
	$("#filter_groupingCode").change(function() {
		loadSegmentTabSegmentCodeList("filter_groupingCode", "filter_segmentCodeId");
	});
         
   $("#segment_segmentCodeId").change(function(){
       var groupType = "SEGMENTATION";
       var segmentCodeId = document.getElementById("segment_segmentCodeId").value;
      var urlString = "getSegmentValues?group="+segmentCodeId+"&groupType=SEGMENTATION"+"&partyId=${partyId!}";
        $.ajax({
          type: 'POST',
          async: false,
          url: urlString,
          success: function (data) {
          var list = [];
              var values ='';
              $('#getSegValue').empty();
              	 values = values+ '<div class="clearfix"></div>';
                values = values+ '<strong>Segment Values</strong>';
                values = values+ '<div class="clearfix"></div>';
                values = values+ '<div class="row">';
                values = values+ '<div class="col-sm-4 col-md-4" id="segmentValues">';
               for(var i=0;i<data.length;i++){
              	 var sequenceNo = data[i].sequenceNo;
              	 values = values+ '<div class="form-check-inline">';
              	 values = values+ '<div class="col-sm-3"><input type="checkbox" class="checkBox segment-values" name="segmentValue_o_'+i+'" value="'+data[i].customFieldId+'"></div>';
                values = values+ '<label  class="col-sm-11 col-form-check-inline fw">'+data[i].customFieldName+'</label></div>';
                values = values+ '<input type="hidden" name="partyId_o_'+i+'" id="partyId_o_'+i+'" value="${partyId?if_exists}">';
                values = values+ '<input type="hidden" name="groupId_o_'+i+'" id="groupId_o_'+i+'" value="'+segmentCodeId+'"></div></div>';
                }
                if(data == list || data != ""){
                values = values+ '<div class="col-md-12 col-sm-12">';
               values = values+ '<div class="form-group row">';
               values = values+ '<div class="col-sm-12">';
               values = values+ ' <button type="button" onclick="addAction()" class="btn btn-sm btn-primary mt">Add</button>';
               values = values+ '</div></div></div><div class="clearfix"></div>';
               }
               else{
               if(segmentCodeId != null && segmentCodeId !="" ){
                 showAlert ("error", "Segment Values not found to be add!!!");}
               }
               $('#getSegValue').append(values);
            }
        });
   });
   
});

function loadSegmentTabSegmentCodeList(groupingCode, segmentCodeId) {
	var nonSelectContent = "<span class='nonselect'>Select ${uiLabelMap.segmentCode!}</span>";
	var groupNameOptions = '<option value="" data-content="'+nonSelectContent+'" selected="">Select ${uiLabelMap.segmentCode!}</option>';		
		
	//if ( $("#"+groupingCode).val() ) {
		
		$.ajax({
			      
			type: "POST",
	     	url: "getCustomFieldGroups",
	        data:  {"groupingCode": $("#"+groupingCode).val(), "roleTypeId": "${roleTypeId!}", "isActive": "Y"},
	        async: false,
	        success: function (data) {   
	            
	            if (data.code == 200) {
	            
	            	for (var i = 0; i < data.groups.length; i++) {
	            		var group = data.groups[i];
	            		groupNameOptions += '<option value="'+group.groupId+'">'+group.groupName+'</option>';
	            	}
	            	
	            }
				    	
	        }
	        
		});    
		
		$("#"+segmentCodeId).html( groupNameOptions );
		
		<#if segmentSegmentCode.segment_segmentCodeId?has_content>
		$("#segment_segmentCodeId").val( "${segmentSegmentCode.segment_segmentCodeId}" );
		</#if>
		
		<#if filterSegmentCode.filter_segmentCodeId?has_content>
		$("#filter_segmentCodeId").val( "${filterSegmentCode.filter_segmentCodeId}" );
		</#if>
	
		$('#'+segmentCodeId).dropdown('refresh');
	//}
		
}

function addAction(){

	var rowsSelected = [];
			
	$('.segment-values:checked').each(function() {
		//alert(this.value);
   		console.log(this.value);
   		
   		rowsSelected.push(this.value);
   		
	});
	
	if (rowsSelected.length == 0) {
		showAlert ("error", "Please select segment values to be add");
		return;
	}
   			
	$('#segmentForm').submit();
	
}
   
</script>