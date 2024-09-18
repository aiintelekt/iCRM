<#include "component://bootstrap/lib/ofbizFormMacros.ftl"/>
<#include "component://sr-portal/webapp/sr-portal/attribute/modal_window.ftl"/>

<form></form>
   
<form id="sr-attribute-form" name="sr-attribute-form" method="post" action="<@ofbizUrl>updateAttribute</@ofbizUrl>">   

<input type="hidden" name="activeTab" value="attributes" />  
<input type="hidden" name="donePage" value='${requestURI?if_exists}'/>
<input type="hidden" name="srNumber" value="${srNumber?if_exists}"/>     
<input type="hidden" name="externalLoginKey" value="${requestAttributes.externalLoginKey!}"/>

<#assign extra = '<span id="create-attr-btn" title="Create" class="btn btn-primary btn-xs ml-2 "><i class="fa fa-save" aria-hidden="true"></i> Create</span>
<span id="update-attr-btn" title="Update" class="btn btn-primary btn-xs ml-2 "><i class="fa fa-edit" aria-hidden="true"></i> Update</span>' />   
<#assign helpUrl = Static["org.groupfio.common.portal.util.DataHelper"].getTabHelpUrl(delegator, request.getRequestURI(), "Attributes") />      

<div class="page-header border-b pt-2">
	<@sectionFrameHeaderTab title="${uiLabelMap.Attributes!}" tabId="Attributes" extra=extra/> 
</div>

<div id="custom-field-accordion">
	
	<#if channelIdLst?has_content && channelIdLst?size!=0>
	
	<#assign count = 0>
    <#assign i = 0>
	
	<#list channelIdLst as channelId>
	<#assign channelName = Static["org.fio.homeapps.util.EnumUtil"].getEnumDescription(delegator, channelId, "SR_CHANNEL")?if_exists />
	<div class="card">
		<div class="card-header pt-1 pb-1 <#if count == 0>active</#if>">
			<a role="button" class="card-link" data-toggle="collapse"
				href="#acc1_o_${count}" aria-expanded="true"> ${channelName!} </a>
		</div>
		
		<div id="acc1_o_${count}" class="card-collapse collapse <#if count == 0> show </#if>"
			data-parent="#custom-field-accordion" style="">
			<div class="card-body">
				
				<#assign attrLi = delegator.findByAnd("CustRequestAttribute", {"custRequestId":"${srNumber?if_exists}", "channelId":"${channelId?if_exists}"}, Static["org.ofbiz.base.util.UtilMisc"].toList("sequenceNumber ASC"), false)?if_exists />
                
                <#if attrLi?has_content && attrLi?size!=0>
				
				<#list attrLi as attr>
				
				<input type="hidden" name="srNumber_o_${i}" id="srNumber_o_${i}" value="${srNumber?if_exists}">
              	<input type="hidden" name="attrName_o_${i}" id="attrName_o_${i}" value="${attr.attrName?if_exists}">
                
				<div class="row">

					<div class="col-lg-4 col-md-12 col-sm-12">
						<@inputRow    
                            id="attrValue_o_${i}"
                     		label=attr.attrName?if_exists
                            value=attr.attrValue?if_exists
                            required=false
                           />
                    </div>       
                	<div class="col-lg-4 col-md-12 col-sm-12">           
                      	<@inputRow    
                            id="sequenceNumber_o_${i}"
                     		label=uiLabelMap.sequenceNumber?if_exists
                            value=attr.sequenceNumber?if_exists
                            required=false
                           />     
					</div>
					
					<div class="col-lg-2 col-md-12 col-sm-12">
					<span data-toggle="confirmation" title="Are you sure to REMOVE ?" class="btn btn-danger btn-xs ml-2 sr-remove-attr" data-attrName="${attr.attrName!}"><i class="fa fa-remove" aria-hidden="true"></i></span>
					</div>
					
				</div>
				
				<#assign i=i+1>
				</#list>
				
				</#if>
								
			</div>
		</div>
	</div>
	<#assign count = count+i> 
    </#list>
         
	</#if>

</div>

</form>

<@createAttrModal 
	instanceId="create-attr-modal"
	/>

<script>

jQuery(document).ready(function() {

$('#create-attr-btn').on('click', function() {
	$('#create-attr-modal').modal("show");
});

$('#update-attr-btn').on('click', function() {
	$('#sr-attribute-form').submit();
});

$('.sr-remove-attr').on('click', function() {
	var attrContainer = $(this).parent().parent();
	$.ajax({
		type: "POST",
     	url: "/sr-portal/control/removeAttribute",
        data: {"custRequestId": "${srNumber!}", "attrName": $(this).attr("data-attrName"), "externalLoginKey": "${requestAttributes.externalLoginKey!}"},
        async: false,
        success: function (data) {   
            if (data.code == 200) {
            	showAlert ("success", data.message);
            	attrContainer.remove();
            } else {
				showAlert ("error", data.message);
			}
        }
        
	});
	
});

});

</script>                                          