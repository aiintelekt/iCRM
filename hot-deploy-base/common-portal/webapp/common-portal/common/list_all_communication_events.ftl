<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<#assign custRequestId = request.getParameter("custRequestId")?if_exists/>
<#-- <script type="text/javascript" src="/bootstrap/js/jquery.min.js"></script>-->

<style>
html {
	overflow-y: overlay !important;
}
.d-inline-block{
	font-size: 22px !important;
	color: inherit !important;
	font-weight: 500 !important;
}
.sticky-panel{
	color: #02829d; 
	text-align : right;
	top: 6.6rem;
	display: block;
	font-size: 1.3vw;
	font-weight: 600;
}
.sticky-bar {
	position: fixed;
	margin-top: -98px !important;
	max-width: 98vw;
	width: 100%;
	z-index: 999;
}
.sticky-bar1 {
	margin-top: 112px!important;
}

.pd-lr-5 {
	padding-left: 5px !important;
	padding-right: 5px !important;
}
</style>
<#assign isPhoneCampaignEnabled=Static["org.fio.homeapps.util.DataUtil"].isPhoneCampaignEnabled(delegator)!/>
<div class="row">
	<div id="main" role="main">
		<div class="col-lg-12 col-md-12 col-sm-12">
			<div class="card-head margin-adj mt-0 sticky-bar" id="view-detail">
				<div class="col-lg-12 col-md-12 dot-line">
					<div class="row">
						<div class="col-lg-6 col-md-6">
							<h3 class="float-left mr-2 mb-0 header-title">${domainEntityTypeDesc!}<#if domainEntityName?has_content>: ${domainEntityName!}</#if></h3>
							<span class="sticky-panel">
							<#if !isPhoneCampaignEnabled?has_content || isPhoneCampaignEnabled?if_exists!="Y">
							${domainEntityId!}</#if>
							</span>
						</div>
						<#if !isPhoneCampaignEnabled?has_content || isPhoneCampaignEnabled?if_exists!="Y">
							<div class="col-lg-6 col-md-6" style="padding-bottom: 6px">
								<a href="${domainEntityLink!}" class="btn btn-xs btn-primary float-right text-right"><i class="fa fa-chevron-circle-left"></i> Back</a>
							</div>
						</#if>
					</div>
				</div>
			</div>
			<div class="card-head margin-adj mt-0 sticky-bar1">
			<#assign isShowHelpUrl="Y" />
	         <#if Static["org.fio.homeapps.util.DataUtil"].isPhoneCampaignEnabled(delegator)?if_exists=="Y">
	             <#assign isShowHelpUrl="N" />
	         </#if>
				<@sectionFrameHeaderTab title="Communication History" isShowHelpUrl=isShowHelpUrl! />
				<div id="com-history"></div>
				<div class="col-md-12 col-lg-12 col-sm-12" id="no-records" style="text-align: center;padding-bottom: 10px;">
					<h1>No Records To Show</h1>
				</div>
			</div>
		</div>
	</div>
</div>
<script type="text/javascript">

	$(document).ready(function() {
		$("#no-records").hide();
		window.history.pushState(null, "", window.location.href);
		window.onpopstate = function() {
			window.history.pushState(null, "", window.location.href);
		};
		$("#com-history").html("");
		var history = '';
		$.ajax({
			type: "POST",
			url: "/sr-portal/control/getAllCommunicationHistory",
			async: false,
			data: { "custRequestId": '${custRequestId!}', "filters" : 'ALL' },
			success: function(data) {
				if(data && data.records && data.records.length > 0){
					data.records.forEach((item, index) => {
						var directionDesc = "";
						if(item.direction=="OUT"){
							directionDesc = '<i class="fa fa-share" aria-hidden="true" style="font-size:16px"></i>';
						}else if(item.direction=="IN"){
							directionDesc = '<i class="fa fa-reply" aria-hidden="true" style="font-size:16px"></i>';
						}
						item.communicationEventTypeDescIcon = item.communicationEventTypeDesc;
						if(item.communicationEventTypeDesc){
							if(item.communicationEventTypeDesc == 'SMS'){
								item.communicationEventTypeDescIcon = '<i class="fa fa-comments" aria-hidden="true" style="font-size:16px"></i>';
							}else if(item.communicationEventTypeDesc == 'EMAIL'){
								item.communicationEventTypeDescIcon = '<i class="fa fa-envelope" aria-hidden="true" style="font-size:16px"></i>';
							}
						}
						history += '<div class="col-md-12 col-lg-12 col-sm-12 pd-lr-5" style="background-color: #02829d;">';
						history += '<div style="color: white;"> <b> '+item.fromPartyName+' said. </b> <span style="padding-left:30%"><b>'+item.toPartyName+'</b></span><span class="float-right"><b>'+item.communicationEventTypeDescIcon+'</b> &nbsp; <b> '+item.entryDate+' </b> &nbsp; <b>'+directionDesc+'</b> </span> </div>';
						history += '</div>';
						history += '<div> &nbsp; </div>';
						history += '<div class="row">';
						if(item.communicationEventTypeDesc == 'SMS'){
							history += '<div class="col-md-9 col-lg-9 col-sm-9" >';
						}else{
							history += '<div class="col-md-10 col-lg-10 col-sm-10" >';
						}
		 			history += item.message;
					history += '</div>';
					if(item.communicationEventTypeDesc == 'SMS'){
						history += '<div class="col-md-3 col-lg-3 col-sm-3" >';
						if(item.toPartyNamePhone){
								let toPartyNamePhone = item.toPartyNamePhone.split(',');
								toPartyNamePhone.forEach(function(partyNamePhone) {
									history += partyNamePhone + "<br>";
								});
							}
						history += '</div>';
					}
					history += '</div>';
					if(item.isAttachment && item.isAttachment == "Y"){
						history += '<div class="row">';
						history += '<div class="col-md-6 col-sm-6">';
						history += '<div class="col-md-6 col-sm-6">';
						history += '<div class="form-group row attachment file-content" id="attachment_row">';
						history += '<label class="col-sm-2 field-text" id="'+item.eventId+'_label">Attachments </label>';
						history += '<div class=" col-sm-9 left">';
						item.fileContents.forEach((fileContent) => {
							history += '<a href="/partymgr/control/ViewSimpleContent?contentId='+fileContent.contentId+'&externalLoginKey=${requestAttributes.externalLoginKey!}" class="buttontext"><u>'+fileContent.contentName+'</u></a>&nbsp;';
						});
						history += '</div>';
						history += '</div>';
						history += '</div>';
						history += '</div>';
					}
					history += '<div> &nbsp; </div>';
					if(item.imgData){
						history += '<div class="row">';
						history += '<div class="col-md-6 col-sm-6">';
						history += '<div class="col-md-6 col-sm-6">';
						history += '<div class="form-group row attachment file-content" id="attachment_row">';
						history += '<label class="col-sm-2 field-text" id="'+item.eventId+'_label">Images</label>';
						history += '<div class=" col-sm-9 left">';
						let imgLength = item.imgData.split(",").length;
						item.imgData.split(",").forEach((img,index) => {
							history += '<a target="_Blank" href="'+img+'" class="buttontext"><u>'+img.split('/').pop().split('.').slice(0, -1).join('.')+'</u></a>';
							if(index !== (imgLength - 1)){
								history += ',&nbsp;'
							};
						});
						history += '</div>';
						history += '</div>';
						history += '</div>';
						history += '</div>';
					}
					history += '<div> &nbsp; </div>';
				});
			}
			$("#com-history").html(history);
		},
		error: function(data) {
			result=data;
			showAlert("error", "Error occured while fetching Communication History");
		}
	});
});
</script>