
<script type="text/javascript" src="http://ajax.aspnetcdn.com/ajax/jquery.validate/1.10.0/jquery.validate.min.js"></script>
<style>
#selectModels .form-control{
margin-top: 0px !important;
}
</style>
<script> 
 function submitUpdateEtlModelGrouping(fieldIndex) {
 	
  var form = document.getElementById("updateEtlModelGroup_" + fieldIndex);
  form.action = "updateEtlModelGrouping#ETLModel";
  form.submit();
  $("#ETLModel").addClass("active");
}

 function submitDeleteEtlModelGrouping(fieldIndex) {
 
  var form = document.getElementById("updateEtlModelGroup_" + fieldIndex);
  form.action = "removeEtlModelGrouping#ETLModel";
  form.submit();
  $("#ETLModel").addClass("active");
}

  // When the browser is ready...
  $(function() {
  
    // Setup form validation on the #register-form element
    $("#addModelGroup").validate({
    
        // Specify the validation rules
        rules: {
            groupId: "required",
            sequence: "required",
            modelId: "required"

        },
        
        // Specify the validation error messages
        messages: {
            groupId: "Please select group id",
            modelId: "Please select model id",
            sequence: "Please enter sequence"

        },
	       errorPlacement: function(error,element){
			   if(element.attr("name")=="groupId")  
		          error.appendTo("#mggroupIdError");
		     else if(element.attr("name")=="modelId")  
		          error.appendTo("#mgmodelIdError");
		     else if(element.attr("name")=="sequence")  
		          error.appendTo("#mgsequenceError");		          
		   }
    });

  });
</script> 
  <div class="screenlet-body" style="">
		<form method="post" action="<@ofbizUrl>addEtlModelGrouping#ETLModel</@ofbizUrl>" name="addModelGroup" id="addModelGroup">			  
			<input type="hidden" name="partyId" value="Company"/>
		
			<table>
				<tbody>
					<tr>
						<td width=""><span class="">${uiLabelMap.groupName}</span></td>
						<td width="" style="padding-left: 20px;"><span class="">${uiLabelMap.modelName}</span></td>
						<td width="" style="padding-left: 20px;"><span class="">${uiLabelMap.sequenceNo}</span></td>
						
					  </tr>
					<tr>
						
						<td>
							<#--<input type="text" class="form-control" style="font-family: Tahoma,Verdana,Arial;font-size: 11px;" name="groupId" id="groupId_1" value=""/>-->
							<select class="form-control" name="groupId" id="groupId" style="min-width:200px;">
								<option value="">${uiLabelMap.selectGroupId}</option>
								<#if EtlGrouping?has_content>
									<#list EtlGrouping as EtlGroup>
										<option value="${EtlGroup.groupId}">${EtlGroup.groupName}</option>
									</#list>
								</#if>							
							</select>							
						</td>
						
						
						
						<td style="padding-left: 20px;">
							<#--<input type="text" class="form-control" style="font-family: Tahoma,Verdana,Arial;font-size: 11px;" name="groupName" id="groupName_1" value=""/>-->
						<select class="form-control" name="modelId" id="modelId">
							<option value="">${uiLabelMap.selectModelId}</option>
							<#if ModelList?has_content>
								<#list ModelList as Model>
									<option value="${Model.modelId}">${Model.modelName}</option>
								</#list>
							</#if>							
						</select>							
						</td>
						
						<td style="padding-left: 20px;">
							<input type="text" class="form-control" style="font-family: Tahoma,Verdana,Arial;font-size: 11px;" name="sequence" id="sequence" value=""/>
						</td>

						
						<td style="padding-left: 20px;">
						<input type="submit" class="btn btn-sm btn-primary" value="${uiLabelMap.add}" >
								<!--<a class="buttontext" href="javascript:submitAddGroupForm();">Add</a>
							<input style="cursor:pointer;" type="button" onClick="javascript:submitAddForm();" value="Add" />-->
						</td>
					</tr>
					<tr>
						<td><span id="mggroupIdError" style="color:red"></span></td>
						<td style="padding-left: 20px;"><span id="mgmodelIdError" style="color:red"></span></td>
						<td style="padding-left: 20px;"><span id="mgsequenceError" style="color:red"></span></td>
					</tr>					
				</tbody>
			</table>
		</form>
</div>

<div class="table-scrollable">
    <table class="table table-responsive">
        <thead>
	        <tr>
	            <th>${uiLabelMap.groupName} </th>
	            <th> ${uiLabelMap.modelName} </th>
	            <th> ${uiLabelMap.sequenceNo} </th>
	            <th>  </th>
	            <th>  
					<select id="selectModels" class="form-control" onchange="getGroupBasedList(this);">
						<option value="ALL">${uiLabelMap.selectGroupId}</option>
						<#if EtlGrouping?has_content>
							<#list EtlGrouping as EtlGroup>
								<option value="${EtlGroup.groupId}" <#if filterdGroup?has_content><#if filterdGroup=EtlGroup.groupId>selected</#if></#if>>${EtlGroup.groupName}</option>
							</#list>
						</#if>							
					</select>	            
	            </th>
	            
	            
	        </tr>
		    <tr>
		    <#assign i=1000/>
				<#list EtlModelGrouping as EtlModelGroup>
				
						<form method="post" action="<@ofbizUrl>updateEtlGroup#Grouping</@ofbizUrl>" name="updateEtlModelGroup_${i}" id="updateEtlModelGroup_${i}">			  
							<input type="hidden" name="groupId" value="${EtlModelGroup.groupId}"/>
							<input type="hidden" name="modelId" value="${EtlModelGroup.modelId}"/>
							<td>${EtlModelGroup.groupName?if_exists}</td>
							<td>
								${EtlModelGroup.modelName?if_exists}
								<#--<select class="form-control" name="modelId" id="modelId">									
									<#if ModelList?has_content>
										<#list ModelList as Model>
											<option value="${Model.modelId}" <#if EtlModelGroup.modelId==Model.modelId> selected</#if>>${Model.modelId}</option>
										</#list>
									</#if>							
								</select>-->							
							</td>
							<td><input type="text" class="form-control" style="width:110px; font-family: Tahoma,Verdana,Arial;font-size: 11px;" name="sequenceNumber" value="${EtlModelGroup.sequenceNo?if_exists}"/></td>
					
							<td>
							<a class="btn btn-sm btn-primary" href="javascript:submitUpdateEtlModelGrouping(${i});">${uiLabelMap.configure}</a>						
							</td>
							<td>
							<a class="btn btn-sm btn-danger" href="javascript:submitDeleteEtlModelGrouping(${i});">${uiLabelMap.delete}</a>	
							</td>						
						</tr>
					</form>
					<#assign i = i+1 />
				</#list>		    
		    </tr>      
        </thead>
        <tbody>
        </tbody>
   </table> 
<#--      <table align="left" cellpadding="0px" cellspacing="0px" border="0" width="100%" style="margin-right:0%;margin-top:5px" >
<tr><td align="right" >
	<#if modelgrouprecords?has_content>
               <#assign modelgroupviewIndexMax = Static["java.lang.Math"].ceil(modelgrouplistSize?double / modelgroupviewSize?double)>
            
             <#if (modelgroupviewIndex?int > 1)>
                   <a class="btn btn-primary btn-xs" href="<@ofbizUrl>/etlConfiguration?MODELGROUP_VIEW_SIZE=${modelgroupviewSize}&MODELGROUP_VIEW_INDEX=${modelgroupviewIndex?int - 1}&filterdGroup=${filterdGroup?if_exists}</@ofbizUrl>#ETLModel" class="gheadertext">
				   <b style="">${uiLabelMap.CommonPrevious}</b></a> <font color="#FFFFFF">|</font>
              </#if>
              <#if (modelgrouplistSize?int > 0)>
                   <span class="gheadertext">${modelgrouplowIndex} - ${modelgrouphighIndex} ${uiLabelMap.CommonOf} ${modelgrouplistSize}</span>
              </#if>
              <#if modelgrouphighIndex?int < modelgrouplistSize?int>
                   <font color="#FFFFFF">|</font> <a class="btn btn-primary btn-xs" style="margin-right: 3px;margin-bottom: 3px;" href="<@ofbizUrl>/etlConfiguration?MODELGROUP_VIEW_SIZE=${modelgroupviewSize}&MODELGROUP_VIEW_INDEX=${modelgroupviewIndex?int + 1}&filterdGroup=${filterdGroup?if_exists}</@ofbizUrl>#ETLModel" class="gheadertext">
				  ${uiLabelMap.CommonNext}</a>
              </#if>
    </#if>
</td><tr></table> -->    
</div>
<script>
  function  getGroupBasedList(element)
  {

  	var selectedGroup = $(element).val();
  	if(selectedGroup!="")
  	{
  		$("#filterdGroup").val(selectedGroup);
  		$("#filterGrpForm").submit();
  	}
  }
</script>
<form name="filterGrpForm" id="filterGrpForm" method="get" action="etlConfiguration#ETLModel">
  <input  type="hidden" name="filterdGroup"  id="filterdGroup">
</form>	