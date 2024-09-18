 <div class="portlet light">
<div class="portlet-title band">
<div class="caption font-red-sunglo">
<i class="icon-share font-red-sunglo"></i>
<span class="caption-subject bold uppercase">${uiLabelMap.crossReferences}</span>
<span class="caption-helper"></span>
</div>
</div>


<div class="portlet-body form">
<form action="createCrossReference" id="createCrossReference" name="createCrossReference" class="form-horizontal">
<div class="form-body">
<div class="form-group">
<label class="col-md-2 control-label">${uiLabelMap.crossReferenceId}</label>
<div class="col-md-4">
<input type="text" class="form-control" name="crossReferenceId" id="crossReferenceId" value="${partyIdentificationTypeId?if_exists}" onkeyup="nospaces(this)">
<#--<span  id="crossReferenceError" style="color : red"></span>-->
</div>
</div>

<div class="form-group">
<label class="col-md-2 control-label">${uiLabelMap.description}</label>
<div class="col-md-4">
<input type="text" class="form-control" name="description" id="description" value="${description?if_exists}">
</div>
</div>

<div class="form-group">
<label class="col-md-2 control-label">${uiLabelMap.isEnabled}</label>
<div class="col-md-4">
<#if isEnabled?if_exists=="Yes">
<input type="checkbox" name="isEnabled" value="Yes" id="isEnabled"  checked>
<#elseif isEnabled?if_exists=="No">
<input type="checkbox" name="isEnabled" value="Yes" id="isEnabled" >
<#else>
<input type="checkbox" name="isEnabled" value="Yes" id="isEnabled" checked>
</#if>

</div>
</div>
<div class="">
<div class="row">
<div class="col-md-offset-2 col-md-9">
<button type="submit" class="btn btn-primary btn-xs">Submit</button>
</div>
</div>
</div>
 </form>
 <!-- END FORM-->
</div>
</div>                                       
<div>&nbsp;</div>       

                                 
<div class="portlet light">
<div class="portlet-title band">
<div class="caption font-red-sunglo">
<i class="icon-share font-red-sunglo"></i>
<span class="caption-subject bold uppercase">${uiLabelMap.crossReferencesList}</span>
<span class="caption-helper"></span>
</div>
</div>


<div class="portlet-body">
        <table class="table">
          <thead>
            <tr>
              <th>${uiLabelMap.crossReferenceId}</th>
              <th>${uiLabelMap.description}</th>
              <th>${uiLabelMap.isEnabled}</th>
              <th>${uiLabelMap.edit}</th>
            </tr>
          </thead>
          <tbody>
          <#list partyIdentificationType as partyIdentificationTypeList>
          <tr>
                <td>${partyIdentificationTypeList.partyIdentificationTypeId?if_exists}</td>
                <td>${partyIdentificationTypeList.description?if_exists}</td>
                <td>${partyIdentificationTypeList.isEnabled?if_exists}</td>
                <td><a href="<@ofbizUrl>crossReferences?partyIdentificationTypeId=${partyIdentificationTypeList.partyIdentificationTypeId?if_exists}</@ofbizUrl>" class="btn btn-primary btn-xs">${uiLabelMap.edit}</a></td>
           </tr>
           </#list>
           </tbody>
           </table>
           </div>
           </div>
           
           <script>
		function nospaces(event){
			if(event.value.match(/\s/g)){
			//document.getElementById("crossReferenceError").innerHTML = "Sorry, you are not allowed to enter any spaces";
			alert('Cross Reference Id Required without Spaces');
			event.value=event.value.replace(/\s/g,'');
			}
		}
    </script>