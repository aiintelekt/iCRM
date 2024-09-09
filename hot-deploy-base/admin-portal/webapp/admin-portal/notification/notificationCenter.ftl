<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>


<div class="row pt-2">
	<div class="col-md-6 col-lg-6 col-sm-6 ">
		<@pageSectionHeader title="${uiLabelMap.NotificationSubscribe!}" extra="" />
		<form action="subscribeNotification" id="notifySubscribeForm" name="notifySubscribeForm" method="POST" data-toggle="validator">
			<@inputHidden 
                  	id="customFieldIds"
                  	/>
            <@inputHidden 
              	id="partyId"
              	value=userLoginPartyId!
              	/>
            
            <#if notificationSubscribeList?has_content>
            	<div class="row padding-r">
					<div class="col-3 field-text font-weight-bold">
						Event Name
					</div>
					<div class="col-3 field-text font-weight-bold">
						Is Subscribed?
					</div>
        		</div>
        		<#assign count = 1 />
            	<#list notificationSubscribeList as notificationSubscribe>
            		<div class="row">
            			<div class="col-3 field-text ">
    						${notificationSubscribe.eventTypeName!}
    					</div>
    					<div class="col-3 field-text ">
    						<#assign isSubscribed = notificationSubscribe.isSubscribed?if_exists />
    						<#assign checked = false />
							<#if "Y" == isSubscribed>
								<#assign checked = true />	
							</#if>
    						<@checkboxField
								id="isSubscribe_${count!}"
								name=""
								class="form-check-input checkMe isSubscribe"
								value=notificationSubscribe.customFieldId!
								checked = checked!
								/>
    					</div>
            		</div>
            		<#assign count = count+1 />
            	</#list>
            	
            	<div class="offset-md-3 col-sm-10">
					<@formButton
						btn1type="button"
	                    btn1label="${uiLabelMap.Save}"
	                    btn1onclick="subscribeNotify()"
	                 	/>
				</div>
            </#if>
		</form>
	</div>
</div>

<script>
	$(function(){
		var isSubscribeList = $("input[name^='isSubscribe_']:checked").map(function() {return this.value;}).get().join(',');
		$("#customFieldIds").val(isSubscribeList);
		
	});
	
	
	$('.isSubscribe').change(function() {
	 	var isSubscribeList = $("input[name^='isSubscribe_']:checked").map(function() {return this.value;}).get().join(',');
		$("#customFieldIds").val(isSubscribeList);
	});
	
	function subscribeNotify(){
		var isSubscribeList = $("input[name^='isSubscribe_']:checked").map(function() {return this.value;}).get().join(',');
		/*
		if(isSubscribeList == null || isSubscribeList == "" || isSubscribeList == "undefined"){
			showAlert("error", "Please select atleast one");
			return false;
		} */
		
		$.ajax({
	            async: false,
	            type: "POST",
	            url: "subscribeNotification",
	            data: JSON.parse(JSON.stringify($("#notifySubscribeForm").serialize())),
	            success: function (data) {
	            	if(data){
		            	if(data.responseMessage=="success"){
		            		showAlert('success', data.successMessage);
		            	} else{
		            		showAlert('error', data.errorMessage);
		            	}
	            	}
				}
			});
	}
	
</script>