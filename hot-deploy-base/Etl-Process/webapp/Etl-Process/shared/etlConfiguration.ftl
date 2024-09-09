
<#--<#include "component://fio-responsive-template/webapp/fio-responsive-template/lib/mocaPortalMacros.ftl"/>-->
<style>
#generalCountryGeoId{width:50%;}
#generalStateProvinceGeoId{width:50%;}
#generalCountryGeoId1{width:50%;}
#generalStateProvinceGeoId1{width:50%;}
</style>
<script>
  function  getModelBasedList(element)
  {
  	var selectedModel = $(element).val();
  	if(selectedModel!="")
  	{
  		$("#etlDestTableName").val(selectedModel);
  		$("#EltBasedList").submit();
  	}
  }
   function  getModelFields(element)
  {
  	var selectedModel = $(element).val();
  	if(selectedModel!="")
  	{
  		$("#model").val(selectedModel);
  		$("#modelList").submit();
  	}
  }
  /*function defaultConfig(index){
  alert(index);
  	var fieldName = $("fieldName_"+index).val();
  	var nullValue = $("nullValue_"+index).val();
  	var overrideValue = $("overrideValue_"+index).val();
  	var defaultValue = $("defaultValue_"+index).val();
  	var tableName = $("tableName_"+index).val();
  	$("#tableName").val(tableName);
  	$("#defaultValue").val(defaultValue);
  	$("#overrideValue").val(overrideValue);
  	$("#nullValue").val(nullValue);
  	$("#fieldName").val(fieldName);
  	alert(fieldName);
  	alert(nullValue);
  	alert(overrideValue);
  	alert(tableName);
  	alert(defaultValue);
  	$("#createDefaultConfiguration").submit();
  }*/
  function checkThis(cb) {
  	var id = cb.id;
  if(cb.checked==true)
  	$("#nullValue_"+id).val("Y");
  if(cb.checked==false)
  	$("#nullValue_"+id).val("N");
  }
  function checkThis1(cb) {
  	var id = cb.id;
  if(cb.checked==true)
  	$("#overrideValue_"+id).val("Y");
  if(cb.checked==false)
  	$("#overrideValue_"+id).val("N");
  }
 function storePayments(storeId) {
	$("#paymentMethod").html('<option value=""></option>');
	if(storeId !=null && storeId != "") {
		$.post("getStorePayments",{"storeId":storeId},function(data) {
			if(data != null && data != "") {
				for(var i=0;i<data.length;i++) {
					$("#paymentMethod").append('<option value="'+data[i].paymentMethodTypeId+'">'+data[i].description+'</option>');
				}
			}
		});
	}
	$("#shipmentMethod").html('<option value=""></option>');
	if(storeId !=null && storeId != "") {
		$.post("getStoreShipments",{"storeId":storeId},function(data) {
			if(data != null && data != "") {
				for(var i=0;i<data.length;i++) {
					$("#shipmentMethod").append('<option value="'+data[i].shipmentMethodTypeId+'">'+data[i].description+'</option>');
				}
			}
		});
	}
}

 function storeShipments(storeId) {
	$("#shipmentMethod").html('<option value=""></option>');
	if(storeId !=null && storeId != "") {
		$.post("getStoreShipments",{"storeId":storeId},function(data) {
			if(data != null && data != "") {
				for(var i=0;i<data.length;i++) {
					$("#shipmentMethod").append('<option value="'+data[i].shipmentMethodTypeId+'">'+data[i].description+'</option>');
				}
			}
		});
	}
}
</script>
<form name="EltBasedList" id="EltBasedList" method="get" action="etlConfiguration">
  <input  type="hidden" name="etlDestTableName"  id="etlDestTableName">
</form>
<form name="modelList" id="modelList" method="" action="etlConfiguration">
  <input  type="hidden" name="model"  id="model">
  <input  type="hidden" name="etlDestTableName"  id="etlDestTableName1" value="${requestParameters.etlDestTableName?if_exists}">
</form>
<form name="createDefaultConfiguration" action="createDefaultConfiguration" id="createDefaultConfiguration" method="post">
  <input type="hidden" name="model" id="model" value="${requestParameters.model?if_exists}"/>
  <input type="hidden" name="tableName" id="tableName"  value=""/>
  <input type="hidden" name="fieldName" id="fieldName" value=""/>
  <input type="hidden" name="defaultValue" id="defaultValue" value=""/>
  <input type="hidden" name="nullValue" id="nullValue" value=""/>
  <input type="hidden" name="overrideValue" id="overrideValue" value=""/>
</form>
<div class="row-fluid">
<div class="col-lg-12">
  <div class="col-lg-12 col-md-12 col-sm-12 col-xs-12 portlet light bordered">
    <div class="portlet-title tin-tin">
      <div class="caption  band">
        <i class="icon-settings"></i>
        <span class="caption-subject bold uppercase">${uiLabelMap.defaults}</span>
      </div>
      <div class="actions">
        <#if etlPr_ocess?has_content>	
        <select class="form-control" onchange="getModelBasedList(this);">
          <option value="">${uiLabelMap.selectType}</option>
          <#list etlPr_ocess as listEtl>
          <option value="${listEtl.tableName?if_exists}" <#if etlDestTableName?has_content && etlDestTableName?if_exists==listEtl.tableName?if_exists>selected</#if>>${listEtl.tableTitle?if_exists} </option>	
          </#list>
        </select>
        </#if>	
      </div>
      <div class="actions" style="padding-right: 6px;">
        <#if requestParameters.model?has_content || requestParameters.etlDestTableName?has_content>	
        <select class="form-control" onchange="getModelFields(this);" >
          <option value="">${uiLabelMap.selectModel}</option>
          <#list etlSet as model>
          <option value="${model.listName?if_exists}" <#if model.listName?has_content && model.listName?if_exists==requestParameters.model?if_exists>selected</#if>>${model.listName?if_exists} </option>	
          </#list>
        </select>
        </#if>
      </div>
      <#--
      <div class="actions" style="padding-right: 6px;">
        <#if requestParameters.model?has_content>	
        <#assign etlFields=delegator.findByAnd("EtlSourceTable", Static["org.ofbiz.base.util.UtilMisc"].toMap("listName", "${requestParameters.model?if_exists}"))?if_exists>
        <select class="form-control" onchange="getModelFields(this);" >
          <option value="">SelectModel</option>
          <#if etlFields?has_content>
          <#list etlFields as field>
          <option value="${field.tableColumnName?if_exists}" <#if model.listName?has_content && model.listName?if_exists==requestParameters.model?if_exists>selected</#if>>${field.tableColumnName?if_exists} </option>	
          </#list>
          </#if>
        </select>
        </#if>
      </div>
      -->
      <#assign stores =  delegator.findAll("ProductStore",false)?if_exists/>
      <#assign channels =  delegator.findAll("ProductStore",false)?if_exists/>
      <div class="portlet-body">
        <table class="table">
          <thead>
            <tr>
              <th>${uiLabelMap.fieldName}</th>
              <th>${uiLabelMap.defaults}</th>
              <th></th>
              <th>${uiLabelMap.null}</th>
              <th>${uiLabelMap.override}</th>
              <th></th>
            </tr>
          </thead>
          <tbody>
          	<#assign storeDefault = ""/>
            <#assign index=0/>
            <#if defaultFields?has_content>
            <#list defaultFields as default>
            <form name="createDefaultConfiguration" action="createDefaultConfiguration" id="createDefaultConfiguration" method="post">
              <input type="hidden" name="model" id="model" value="${requestParameters.model?if_exists}"/>
              <input type="hidden" name="tableName" id="tableName_${index}" value="${default.table?if_exists}"/>
              <input type="hidden" name="fieldName" id="fieldName_${index}" value="${default.fieldName?if_exists}"/>
              <tr>
                <td>${default.fieldName?if_exists}</td>
                <#assign defaultValues=delegator.findByAnd("EtlDefaultsConfig", Static["org.ofbiz.base.util.UtilMisc"].toMap("etlFieldName", "${default.fieldName?if_exists}","etlTableName","${default.table?if_exists}"),null,false)?if_exists>
                <#assign defaultMapping=delegator.findByAnd("EtlDefaultsMapping", Static["org.ofbiz.base.util.UtilMisc"].toMap("etlFieldName", "${default.fieldName?if_exists}","etlTableName","${default.table?if_exists}","model","${requestParameters.model?if_exists}"),null,false)?if_exists>
                <#assign dValues1 =""/>
                <#assign nullValue =""/>
                <#assign overValue =""/>
                <#if defaultMapping?has_content>
                <#list defaultMapping as df>
                <#assign dValues1 = df.defaultValue?if_exists/>
                <#assign nullValue = df.nullValue?if_exists/>
                <#assign overValue = df.overrideValue?if_exists/>
                </#list>
                </#if>
                <td>
                <#if requestParameters.etlDestTableName?has_content && requestParameters.etlDestTableName=="EtlImportOrderFields">
                 <#if defaultValues?has_content>
                 <#assign field = default.fieldName?if_exists/>
                 <#if default.fieldName?has_content && default.fieldName?if_exists=="storeId">
                 	<#assign storeDefault = dValues1?if_exists/>
                 	<select class="form-control" name="defaultValue" id="defaultValue_${index}" style="width: 50%;" onChange = "javascript:storePayments(this.value);">
                 		<option></option>
                 		<#if stores?has_content>
                 			<#list stores as store>
                 				<option value="${store.productStoreId?if_exists}" <#if dValues1?if_exists==store.productStoreId?if_exists>selected</#if>>${store.storeName?if_exists}</option>
                 			</#list>
                 		</#if>
                 	</select>
          			<#elseif default.fieldName?has_content && default.fieldName?if_exists=="paymentMethod">
                 	<select class="form-control" name="defaultValue" id="paymentMethod" style="width: 50%;">
                 		<#if storeDefault?has_content>
                 		<#assign storePayments=delegator.findByAnd("ProductStorePaymentSetting", Static["org.ofbiz.base.util.UtilMisc"].toMap("productStoreId", "${storeDefault?if_exists}"),false)?if_exists>
                 			<#if storePayments?has_content>
                 				<#list storePayments as sPayment>
                 					<option value="${sPayment.paymentMethodTypeId?if_exists}">${sPayment.paymentMethodTypeId?if_exists}</option>
                 				</#list>
                 			</#if>
                 		<#else>
                 		    <option></option>
                 		</#if>
                 	</select>
              		<#elseif default.fieldName?has_content && default.fieldName?if_exists=="shipServiceLevel">
                 	<select class="form-control" name="defaultValue" id="shipmentMethod" style="width: 50%;">
                 		<#if storeDefault?has_content>
                 		<#assign storeShipments=delegator.findByAnd("ProductStoreShipmentMeth", Static["org.ofbiz.base.util.UtilMisc"].toMap("productStoreId", "${storeDefault?if_exists}"),false)?if_exists>
                 			<#if storeShipments?has_content>
                 				<#list storeShipments as shipment>
                 					<option value="${shipment.shipmentMethodTypeId?if_exists}">${shipment.shipmentMethodTypeId?if_exists}</option>
                 				</#list>
                 			</#if>
                 		<#else>
                 		    <option></option>
                 		</#if>
                 	</select>
                 <#elseif default.fieldName?has_content && default.fieldName?if_exists=="salesChannelEnumId">
                 	<select class="form-control" name="defaultValue" id="defaultValue_${index}" style="width: 50%;">
                 		<option></option>
                 		<#if channels?has_content>
                 			<#list channels as channel>
                 				<option value="${channel.channelEnumId?if_exists}" <#if dValues1?if_exists==channel.channelEnumId?if_exists>selected</#if>>${channel.description?if_exists}</option>
                 			</#list>
                 		</#if>
                 	</select>
                 <#elseif default.fieldName?has_content && default.fieldName?if_exists=="shipCountry">
           				<@inputCountry name="defaultValue" stateInputName="generalStateProvinceGeoId" id="generalCountryGeoId" stateId="generalStateProvinceGeoId" selectedCountryGeoId="${generalCountryGeoId?if_exists}"/><div>
           		 <#elseif default.fieldName?has_content && default.fieldName?if_exists=="shipState">
           				<@inputState name="defaultValue" id="generalStateProvinceGeoId" countryInputName="generalCountryGeoId" selectedStateProvinceGeoId="${generalStateProvinceGeoId?if_exists}" selectedCountryGeoId="${generalCountryGeoId?if_exists}"/>
          		 <#elseif default.fieldName?has_content && default.fieldName?if_exists=="billingCountry">
           				<@inputCountry name="defaultValue" stateInputName="generalStateProvinceGeoId1" id="generalCountryGeoId1" stateId="generalStateProvinceGeoId1" selectedCountryGeoId="${generalCountryGeoId?if_exists}"/>
           		 <#elseif default.fieldName?has_content && default.fieldName?if_exists=="billingState">
           				<@inputState name="defaultValue" id="generalStateProvinceGeoId1" countryInputName="generalCountryGeoId1" selectedStateProvinceGeoId="${generalStateProvinceGeoId?if_exists}" selectedCountryGeoId="${generalCountryGeoId?if_exists}"/>
                 <#else>
                   <input type="text" class="form-control" style="width: 50%;" name="defaultValue" id="defaultValue_${index}" value="${dValues1?if_exists}">
                 
                 </#if> 
                  <#else>
                  <span class="red">No defaults found.</span>
                  </#if>	
                <#else>
                 <#if defaultValues?has_content>
                  <select class="form-control" style="width: 50%;" name="defaultValue" id="defaultValue_${index}">
                    <option value="">Select default</option>
                    <#list defaultValues as dValues>
                    <option value="${dValues.defaultValue?if_exists}"
                    <#if dValues.defaultValue?if_exists==dValues1?if_exists>selected</#if>>${dValues.defaultDescription?if_exists}</option>
                    </#list>
                  </select>
                  <#else>
                  <span class="red">No defaults found.</span>
                  </#if>	          
                </#if>
                </td>
                <td>
         		 <#if default.fieldName?has_content && default.fieldName?if_exists=="shipCountry">
         		 		<input type="hidden" name ="defaultValue" value="${dValues1?if_exists}"/>
           				<#if dValues1?has_content>[Selected Value : ${dValues1?if_exists}]</div></#if>
           		 <#elseif default.fieldName?has_content && default.fieldName?if_exists=="shipState">
           		 		<input type="hidden" name ="defaultValue" value="${dValues1?if_exists}"/>
           				<#if dValues1?has_content>[Selected Value : ${dValues1?if_exists}]</div></#if>
          		 <#elseif default.fieldName?has_content && default.fieldName?if_exists=="billingCountry">
          		 		<input type="hidden" name ="defaultValue" value="${dValues1?if_exists}"/>
           				<#if dValues1?has_content>[Selected Value : ${dValues1?if_exists}]</div></#if>
           		 <#elseif default.fieldName?has_content && default.fieldName?if_exists=="billingState">
           		 	<input type="hidden" name ="defaultValue" value="${dValues1?if_exists}"/>
           			<#if dValues1?has_content>[Selected Value : ${dValues1?if_exists}]</div></#if>
           		</#if>     
                </td>
                <td>
                  <#if nullValue?if_exists=="Y">
                  <input type="hidden" name="nullValue" id="nullValue_${index}" value="Y"/>
                  <input type="checkbox" name="nullValue1" value="Y" id="${index}" onChange="checkThis(this)" checked>
                  <#else>
                  <input type="hidden" name="nullValue" id="nullValue_${index}" value=""/>
                  <input type="checkbox" name="nullValue1" value="Y" id="${index}" onChange="checkThis(this)" >
                  </#if>
                </td>
                <td class="">
                  <#if overValue?if_exists=="Y">
                  <input type="hidden" name="overrideValue" id="overrideValue_${index}" value="Y" />
                  <input type="checkbox" name="overrideValue1" checked value="Y" id="${index}" onChange="checkThis1(this)">
                  <#else>
                  <input type="hidden" name="overrideValue" id="overrideValue_${index}" value="" />
                  <input type="checkbox" name="overrideValue1" value="Y" id="${index}" onChange="checkThis1(this)">
                  </#if>
                </td>
                <td>
                  <#if defaultValues?has_content>
                  <button type="submit" class="btn btn-primary btn-xs" value="" onclickf="defaultConfig(${index})">Update</button>
                  <a href="removeDefault?model=${requestParameters.model?if_exists}&etlDestTableName=${requestParameters.etlDestTableName?if_exists}&fieldName=${default.fieldName?if_exists}" class="btn btn-danger btn-xs" value="">Clear</a>
                  </#if>
                </td>
              </tr>
            </form>
            <#assign index = index+1/>
            </#list>
            </#if>
          </tbody>
        </table>
        </form>
      </div>
    </div>
  </div>
</div>
</div>