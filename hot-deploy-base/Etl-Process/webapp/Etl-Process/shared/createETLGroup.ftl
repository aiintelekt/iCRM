<script type="text/javascript" src="/crmsfa_js/jquery.validate.js"></script>
<script> 
 function submitUpdateGroupForm(fieldIndex) {
  var form = document.getElementById("updateEtlGroup_" + fieldIndex);
  form.action = "updateEtlGroup#Grouping";
  form.submit();
  
  $("#groupTab").addClass("active");
}

 function submitDeleteGroupForm(fieldIndex) {
  var form = document.getElementById("updateEtlGroup_" + fieldIndex);
  form.action = "removeEtlGroup#Grouping";
  form.submit();
  $("#groupTab").addClass("active");
}


  // When the browser is ready...
  $(function() {
  
    // Setup form validation on the #register-form element
    $("#addFioGroup").validate({
    
        // Specify the validation rules
        rules: {
            groupId: "required",
            sequence: "required",
            groupName: "required",

        },
        
        // Specify the validation error messages
        messages: {
            groupId: "Please enter group id",
            groupName: "Please enter group name",
            sequence: "Please enter sequence",

        },
	       errorPlacement: function(error,element){
			   if(element.attr("name")=="groupId")  
		          error.appendTo("#groupIdError");
		     else if(element.attr("name")=="groupName")  
		          error.appendTo("#groupNameError");
		     else if(element.attr("name")=="sequence")  
		          error.appendTo("#sequenceError");		          
		   }
    });

  });
</script> 
  <div class="screenlet-body" style="">
		<form method="post" action="<@ofbizUrl>addETLGroup#Grouping</@ofbizUrl>" name="addFioGroup" id="addFioGroup">			  
			<input type="hidden" name="partyId" value="Company"/>
		
			<table>
				<tbody>
					<tr>
						<td width=""><span class="">${uiLabelMap.groupId}</span></td>
						<td width="" style="padding-left: 20px;"><span class="">${uiLabelMap.groupName}</span></td>
						<td width="" style="padding-left: 20px;"><span class="">${uiLabelMap.sequenceNo}</span></td>
						
					  </tr>
					<tr>
						
						<td>
							<input type="text" class="form-control" style="font-family: Tahoma,Verdana,Arial;font-size: 11px;" name="groupId" id="groupId_1" value=""/>
						</td>
						
						
						
						<td style="padding-left: 20px;">
							<input type="text" class="form-control" style="font-family: Tahoma,Verdana,Arial;font-size: 11px;" name="groupName" id="groupName_1" value=""/>
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
						<td><span id="groupIdError" style="color:red"></span></td>
						<td style="padding-left: 20px;"><span id="groupNameError" style="color:red"></span></td>
						<td style="padding-left: 20px;"><span id="sequenceError" style="color:red"></span></td>
					</tr>
				</tbody>
			</table>
		</form>
</div>

<div class="table-scrollable">
    <table class="table table-responsive">
        <thead>
	        <tr>
	            <th>${uiLabelMap.groupId} </th>
	            <th>${uiLabelMap.groupName}</th>
	            <th>${uiLabelMap.sequenceNo}</th>
	            <th>  </th>
	            <th>  </th>
	            
	            
	        </tr>
		    <tr>
		    	<#assign i=1000/>
		    	<#if EtlGrouping?has_content>
				<#list EtlGrouping as etlGroup>
				
						<form method="post" action="<@ofbizUrl>updateEtlGroup#Grouping</@ofbizUrl>" name="updateEtlGroup_${i}" id="updateEtlGroup_${i}">			  
							<input type="hidden" name="groupId" value="${etlGroup.groupId}"/>
							<td>${etlGroup.groupId}</td>
							<td><input type="text" class="form-control" style="font-family: Tahoma,Verdana,Arial;font-size: 11px;"  name="groupName" value="${etlGroup.groupName}"/></td>
							<td><input type="text" class="form-control" style="width:110px; font-family: Tahoma,Verdana,Arial;font-size: 11px;" name="sequenceNumber" value="${etlGroup.sequenceNo?if_exists}"/></td>
					
							<td>
							<a class="btn btn-sm btn-primary" href="javascript:submitUpdateGroupForm(${i});">${uiLabelMap.configure}</a>						
							</td>
							<td>
							<a class="btn btn-sm btn-danger" href="javascript:submitDeleteGroupForm(${i});">${uiLabelMap.delete}</a>	
							</td>						
						</tr>
					</form>
					<#assign i = i+1 />
				</#list>
				</#if>		    
		    </tr>      
        </thead>
        <tbody>
        </tbody>
    </table> 
   <#--  <table align="left" cellpadding="0px" cellspacing="0px" border="0" width="100%" style="margin-right:0%;margin-top:5px" >
<tr><td align="right" >
	<#if record?has_content>
               <#assign viewIndexMax = Static["java.lang.Math"].ceil(listSize?double / viewSize?double)>
            
             <#if (viewIndex?int > 1)>
                   <a class="btn btn-primary btn-xs" href="<@ofbizUrl>/etlConfiguration?VIEW_SIZE=${viewSize}&VIEW_INDEX=${viewIndex?int - 1}&typeId=etlGroup</@ofbizUrl>#Grouping" class="gheadertext">
				   <b>${uiLabelMap.CommonPrevious}</b></a> <font color="#FFFFFF">|</font>
              </#if>
              <#if (listSize?int > 0)>
                   <span class="gheadertext">${lowIndex} - ${highIndex} ${uiLabelMap.CommonOf} ${listSize}</span>
              </#if>
              <#if highIndex?int < listSize?int>
                   <font color="#FFFFFF">|</font> <a class="btn btn-primary btn-xs" style="margin-right: 3px;margin-bottom: 3px;" href="<@ofbizUrl>/etlConfiguration?VIEW_SIZE=${viewSize}&VIEW_INDEX=${viewIndex?int + 1}&typeId=etlGroup</@ofbizUrl>#Grouping" class="gheadertext">
				   ${uiLabelMap.CommonNext}</a>
              </#if>
    </#if>
</td><tr></table>-->
</div>