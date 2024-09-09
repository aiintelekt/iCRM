<script>
   $(document).ready(function(){
   	$("input[type='text']").addClass("form-control");
   	$("select").addClass("form-control");
   	
   	//added by m.vijayakumar for group id selection
   	
   	$("a[data-toggle='tab']").click(function(){
   		$(".activeGroupId").val($(this).attr("data_id"));
   	});
   	
   	var groupId = "${parameters.activeGroupId?if_exists}";
   	
   	if(groupId!="")
   	{
   		//remove avilable active classes
   		$(".nav-tabs li").each(function(){
   			$(this).removeClass("active");
   		});
   		
   		$(".tab-content div").each(function(){
   			$(this).removeClass("active");
   			$(this).removeClass("in");
   		});
   		
   		$(".nav-tabs li").each(function(){
   			var group_id = $(this).children().attr("data_id");
   			if(groupId==group_id)
   			{
   				$(this).addClass("active");
   				$("#"+groupId).addClass("active in");
   			}
   		});
   		$(".activeGroupId").val(groupId);
   		
   	}
   		
   	//end @vijayakumar
   });
   
</script>
<style>
   .inline-reflex
   {
   display: inline-flex;
   }
   @media screen and (max-width: 750px) {
   tbody, th { float: left; }
   th { min-width: 200px }
   td,th { display: block }
   .make-fixed_width{
   float:right !important;
   width:100% !important;
   }
   }
</style>
<#--<#if projectId?exists && projectId!= "" && projectId!= null >-->
<@import location="component://opentaps-common/webapp/common/includes/lib/opentapsFormMacros.ftl"/>
<div class="col-lg-12 col-md-12 col-xs-12 col-sm-12">
<div id="file_upload_view" style="">
   <#--starting of upload form process-->
   <div class="portlet light" style="">
      <div class="portlet-title band">
         <div class="caption font-red-sunglo">
            <i class="icon-settings font-red-sunglo"></i>
            <span class="caption-subject bold uppercase">Data Mapping</span>
         </div>
         <div class="actions">
            <#assign etlGroups=delegator.findAll("EtlGrouping")/>
            <#if etlGroups?has_content>	
            <select class="bs-select form-control "  data-live-search="true" data-size="8" onchange="getGroupList(this);">
               <option value="">Select Channels</option>
               <#list etlGroups as group>
               <option value="${group.groupId?if_exists}"<#if group.groupId?if_exists==requestParameters.groupId?if_exists>selected</#if>>${group.groupName?if_exists} </option>	
               </#list>
            </select>
            </#if>
         </div>
         <#if dataMappingModels?has_content>
         <div class="actions" style="padding-right: 6px;">
            <select class="form-control" name="model" id="model" onChange="getModelFields(this);">
               <option value="">Select Model</option>
               <#list dataMappingModels as etlMapping>
               <option value="${etlMapping.modelId?if_exists}"
               <#if requestParameters.model?if_exists==etlMapping.modelId?if_exists>selected</#if>>${etlMapping.modelName?if_exists} </option>	
               </#list>
            </select>
         </div>
         </#if>
         </div><!--end of portlet -title-->
         <div class="portlet-body" style="">
            <ul class="nav nav-tabs">
               <li><a  data-toggle="tab" data_id="Account" href="#Account">Account</a></li>
               <li><a data-toggle="tab" data_id="Contact" href="#Contact">Contact</a></li>
               <li><a data-toggle="tab" data_id="Person" href="#Person">Person</a></li>
               <li><a data-toggle="tab" data_id="Lead" href="#Lead">Lead</a></li>
               <li><a data-toggle="tab" data_id="Supplier" href="#Supplier">Supplier</a></li>
               <li  class="active"><a data-toggle="tab" data_id="Product" href="#Product">Product</a></li>
               <li><a data-toggle="tab" data_id="SupplierProduct" href="#SupplierProduct">Supplier Product</a></li>
               <li><a data-toggle="tab" data_id="paymentMethods" href="#paymentMethods">Payment Methods</a></li>
               <li><a data-toggle="tab" data_id="shipmentMethods" href="#shipmentMethods">Shipment Methods</a></li>
            </ul>
            <#--for lead tab-->
            <div class="tab-content">
               <div id="Account" class="tab-pane fade in active">
                  ${screens.render("component://Etl-Process/webapp/widget/Etl-Process/screens/myhome/MyHomeScreens.xml#accountmapping")}
               </div>
               <#--end of lead tab-->
               <#--for customer tab-->
               <div id="Contact" class="tab-pane fade">
                  ${screens.render("component://Etl-Process/webapp/widget/Etl-Process/screens/myhome/MyHomeScreens.xml#contactmapping")}
               </div>
               <#--end of customer tab-->
               <#--for contact tab-->
               <div id="Person" class="tab-pane fade">
                  ${screens.render("component://Etl-Process/webapp/widget/Etl-Process/screens/myhome/MyHomeScreens.xml#customermapping")}
               </div>
               <#--end of  contact tab-->
               <#--for account tab-->
               <div id="Lead" class="tab-pane fade">
                  ${screens.render("component://Etl-Process/webapp/widget/Etl-Process/screens/myhome/MyHomeScreens.xml#leadmapping")}
               </div>
               <#--end of  account tab-->
               <#--for opportunity tab-->
               <div id="Supplier" class="tab-pane fade">
                  ${screens.render("component://Etl-Process/webapp/widget/Etl-Process/screens/myhome/MyHomeScreens.xml#suppliermapping")}
               </div>
               <#--end of opportunity tab-->
               <#--for supplier tab-->
               <div id="Product" class="tab-pane fade">
                  ${screens.render("component://Etl-Process/webapp/widget/Etl-Process/screens/myhome/MyHomeScreens.xml#productmapping")} 
               </div>
               <#--end of Supplier tab-->
               <#--for category tab-->
               <div id="SupplierProduct" class="tab-pane fade">
                  ${screens.render("component://Etl-Process/webapp/widget/Etl-Process/screens/myhome/MyHomeScreens.xml#supplierProductMapping")}
               </div>
               <#--end of Category tab-->
               <#--for payment tab-->
              <div id="paymentMethods" class="tab-pane fade">
                  ${screens.render("component://Etl-Process/webapp/widget/Etl-Process/screens/myhome/MyHomeScreens.xml#paymentMethods")}
               </div>
               <#--end of payment tab-->
               <#--for shipment tab-->
              <div id="shipmentMethods" class="tab-pane fade">
                  ${screens.render("component://Etl-Process/webapp/widget/Etl-Process/screens/myhome/MyHomeScreens.xml#shipmentMethods")}
               </div>
               <#--end of shipment tab-->
            </div><!--end of portlet body-->
         </div><!--end of portlet light-->
      </div><!--end of unknown div-->
   </div><!--end of column-->
<#--end of starting upload form process-->
</div>
<form name="EtlGroupList" id="EtlGroupList">
   <input  type="hidden" name="groupId"  id="groupId">
   <input type="hidden" name="activeGroupId" class="activeGroupId" value="">
</form>
<form name="EtlModelList" id="EtlModelList">
   <input  type="hidden" name="groupId"  id="groupId" value="${requestParameters.groupId?if_exists}">
   <input  type="hidden" name="model"  id="model1">
   <input type="hidden" name="activeGroupId" class="activeGroupId" value="">
</form>
<script>
   function  getGroupList(element)
   	 {
   	 	var selectedModel = $(element).val();
   	 	if(selectedModel!="")
   	 	{
   	 		$("#groupId").val(selectedModel);
   	 		$("#EtlGroupList").submit();
   	 	}
   	 }
     function  getModelFields(element)
   {
   var selectedModel = $(element).val();
   if(selectedModel!="")
   {
   	$("#model1").val(selectedModel);
   	$("#EtlModelList").submit();
   }
   }
    
</script>