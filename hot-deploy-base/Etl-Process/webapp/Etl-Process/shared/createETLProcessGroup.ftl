<script> 
 function submitUpdateEtlProcessGrouping(fieldIndex) {
 	
  var form = document.getElementById("updateEtlProcessGroup_" + fieldIndex);
  form.action = "updateEtlProcessGrouping#ETLProcess";
  form.submit();
  $("#ETLProcess").addClass("active");
}

 function submitDeleteEtlProcessGrouping(fieldIndex) {
 
  var form = document.getElementById("updateEtlProcessGroup_" + fieldIndex);
  form.action = "removeEtlProcessGrouping#ETLProcess";
  form.submit();
  $("#ETLProcess").addClass("active");
}

  // When the browser is ready...
  $(function() {
  
    // Setup form validation on the #register-form element
    $("#addEtlProcessGroupingForm").validate({
    
        // Specify the validation rules
        rules: {
            groupId: "required",
            sequence: "required",
            processId: "required"

        },
        
        // Specify the validation error messages
        messages: {
            groupId: "Please select group id",
            processId: "Please select process id",
            sequence: "Please enter sequence"

        },
	       errorPlacement: function(error,element){
			   if(element.attr("name")=="groupId")  
		          error.appendTo("#pggroupIdError");
		     else if(element.attr("name")=="processId")  
		          error.appendTo("#pgprocessIdError");
		     else if(element.attr("name")=="sequence")  
		          error.appendTo("#pgsequenceError");		          
		   }
    });

  });
</script> 
  <div class="screenlet-body" style="">
		<form method="post" action="<@ofbizUrl>addEtlProcessGrouping#ETLProcess</@ofbizUrl>" name="addEtlProcessGroupingForm" id="addEtlProcessGroupingForm">			  
			<input type="hidden" name="partyId" value="Company"/>
		
			<table>
				<tbody>
					<tr>
						<td width=""><span class="">${uiLabelMap.groupName}</span></td>
						<td width="" style="padding-left: 20px;"><span class="">${uiLabelMap.processName}</span></td>
						<td width="" style="padding-left: 20px;"><span class="">${uiLabelMap.sequenceNo}</span></td>
						
					  </tr>
					<tr>
						
						<td>
							<#--<input type="text" class="form-control" style="font-family: Tahoma,Verdana,Arial;font-size: 11px;" name="groupId" id="groupId_1" value=""/>-->
							<select class="form-control" name="groupId" id="groupId" style="min-width:200px;">
								<option value="">${uiLabelMap.selectGroupId}</option>
								<#if EtlGrouping?has_content>
									<#list EtlGrouping as EtlGroup>
										<option value="${EtlGroup.groupId?if_exists}">${EtlGroup.groupName?if_exists}</option>
									</#list>
								</#if>							
							</select>							
						</td>
						
						
						
						<td style="padding-left: 20px;">
							<#--<input type="text" class="form-control" style="font-family: Tahoma,Verdana,Arial;font-size: 11px;" name="groupName" id="groupName_1" value=""/>-->
						<select class="form-control" name="processId" id="processId">
							<option value="">${uiLabelMap.selectProcessId}</option>
							<#if EtlProcessList?has_content>
								<#list EtlProcessList as Process>
									<option value="${Process.processId?if_exists}">${Process.processName?if_exists}</option>
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
						<td><span id="pggroupIdError" style="color:red"></span></td>
						<td style="padding-left: 20px;"><span id="pgprocessIdError" style="color:red"></span></td>
						<td style="padding-left: 20px;"><span id="pgsequenceError" style="color:red"></span></td>
					</tr>					
				</tbody>
			</table>
		</form>
</div>

<div class="table-scrollable">
    <table class="table table-responsive">
        <thead>
	        <tr>
	            <th> ${uiLabelMap.groupName} </th>
	            <th> ${uiLabelMap.processName} </th>
	            <th> ${uiLabelMap.sequenceNo} </th>
	            <th>  </th>
	            <th>  
					<select class="form-control" onchange="getProcessGroupBasedList(this);">
						<option value="ALL">${uiLabelMap.selectGroupId}</option>
						<#if EtlGrouping?has_content>
							<#list EtlGrouping as EtlGroup>
								<option value="${EtlGroup.groupId}" <#if filterdProcessGroup?has_content><#if filterdProcessGroup=EtlGroup.groupId>selected</#if></#if>>${EtlGroup.groupName}</option>
							</#list>
						</#if>							
					</select>	            
	            </th>
	            
	            
	        </tr>
		    <tr>
		    	<#assign i=1000/>
				<#list EtlProcessGrouping as EtlProcessGroup>
				
						<form method="post" action="<@ofbizUrl>updateEtlGroup#Grouping</@ofbizUrl>" name="updateEtlProcessGroup_${i}" id="updateEtlProcessGroup_${i}">			  
							<input type="hidden" name="groupId" value="${EtlProcessGroup.groupId?if_exists}"/>
							<input type="hidden" name="processId" value="${EtlProcessGroup.processId?if_exists}"/>
							<td>${EtlProcessGroup.groupName?if_exists}</td>
							<td>
								${EtlProcessGroup.processName?if_exists}
								<#--<select class="form-control" name="modelId" id="modelId">									
									<#if ModelList?has_content>
										<#list ModelList as Model>
											<option value="${Model.modelId}" <#if EtlModelGroup.modelId==Model.modelId> selected</#if>>${Model.modelId}</option>
										</#list>
									</#if>							
								</select>-->							
							</td>
							<td><input type="text" class="form-control" style="width:110px; font-family: Tahoma,Verdana,Arial;font-size: 11px;" name="sequenceNumber" value="${EtlProcessGroup.sequenceNo?if_exists}"/></td>
					
							<td>
							<a class="btn btn-sm btn-primary" href="javascript:submitUpdateEtlProcessGrouping(${i});">${uiLabelMap.configure}</a>						
							</td>
							<td>
							<a class="btn btn-sm btn-danger" href="javascript:submitDeleteEtlProcessGrouping(${i});">${uiLabelMap.delete}</a>	
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
    <table align="left" cellpadding="0px" cellspacing="0px" border="0" width="100%" style="margin-right:0%;margin-top:5px" >
<tr><td align="right" >
	<#if processgrouprecord?has_content>
               <#assign processgroupviewIndexMax = Static["java.lang.Math"].ceil(processgrouplistSize?double / processgroupviewSize?double)>
           
             <#if (processgroupviewIndex?int > 1)>
                   <a class="btn btn-primary btn-xs" href="<@ofbizUrl>/etlConfiguration?PROCESSGROUP_VIEW_SIZE=${processgroupviewSize}&PROCESSGROUP_VIEW_INDEX=${processgroupviewIndex?int - 1}&filterdProcessGroup=${filterdProcessGroup?if_exists}</@ofbizUrl>#ETLProcess" class="gheadertext">
				   <b style="">${uiLabelMap.CommonPrevious}</b></a> <font color="#FFFFFF">|</font>
              </#if>
              <#if (processgrouplistSize?int > 0)>
                   <span class="gheadertext">${processgrouplowIndex} - ${processgrouphighIndex} ${uiLabelMap.CommonOf} ${processgrouplistSize}</span>
              </#if>
              <#if processgrouphighIndex?int < processgrouplistSize?int>
                   <font color="#FFFFFF">|</font> <a class="btn btn-primary btn-xs" style="margin-right: 3px;margin-bottom: 3px;" href="<@ofbizUrl>/etlConfiguration?PROCESSGROUP_VIEW_SIZE=${processgroupviewSize}&PROCESSGROUP_VIEW_INDEX=${processgroupviewIndex?int + 1}&filterdProcessGroup=${filterdProcessGroup?if_exists}</@ofbizUrl>#ETLProcess" class="gheadertext">
				   ${uiLabelMap.CommonNext}</a>
              </#if>
    </#if>
</td><tr></table>    
</div>
<script>
  function  getProcessGroupBasedList(element)
  {

  	var selectedProcessGroup = $(element).val();
  	if(selectedProcessGroup!="")
  	{
  		$("#filterdProcessGroup").val(selectedProcessGroup);
  		$("#filterProcessGrpForm").submit();
  	}
  }
</script>
<form name="filterProcessGrpForm" id="filterProcessGrpForm" method="get" action="etlConfiguration#ETLProcess">
  <input  type="hidden" name="filterdProcessGroup"  id="filterdProcessGroup">
</form>