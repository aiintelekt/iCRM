<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<#assign srNumberUrlParam = request.getAttribute("srNumber")?if_exists/>

<style>
html {
	overflow-y: overlay !important;
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
				<@sectionFrameHeaderTab title="All Notes" isShowHelpUrl=isShowHelpUrl! />
				<#if noteList?has_content>
				<#list noteList as eachNote>
				<#assign createdByName = eachNote.createdByName!>
				<#assign noteInfo = eachNote.noteInfo!>   
				<#assign noteDateTime = eachNote.noteDateTime!>
				<#assign noteType = eachNote.noteType!>
				<div class="col-md-12 col-lg-12 col-sm-12" style="background-color: #02829d;">
					<div style="color: white;"> <span class="all-sr-notes"> <b> ${noteDateTime!} </b> </span> 
					<span class="note-category"> <b> User: ${createdByName!} </b> </span> 
					<#if !isPhoneCampaignEnabled?has_content || isPhoneCampaignEnabled?if_exists!="Y">
					<span class="float-right"> <b> Category: ${noteType!} </b> </span>
					</#if></div>
				</div>
				<div> &nbsp; </div>
				<div class="col-md-12 col-lg-12 col-sm-12">
				<#if !isPhoneCampaignEnabled?has_content || isPhoneCampaignEnabled?if_exists!="Y">
					<@displayCell
						label="Note Title"
						value="${eachNote.noteName!}"
						id="noteName"
						labelColSize="col-sm-2"
						inputColSize="col-sm-10"
						/>
					</#if>
					<@displayCell
						label="Note Description"
						value="${StringUtil.wrapString(noteInfo)}"
						id="noteInfo"
						labelColSize="col-sm-2"
						inputColSize="col-sm-10"
						/>
					<#-- Note Title: ${eachNote.noteName!}<br>
					Note Description: ${StringUtil.wrapString(noteInfo)} -->
				</div>
				<div> &nbsp; </div>
				</#list>
				<#else>
				<div class="col-md-12 col-lg-12 col-sm-12" style="text-align: center;padding-bottom: 10px;">
					<h1>No Records To Show</h1>
				</div>
				</#if>
			</div>
		</div>
	</div>
</div>
<script type="text/javascript">

  $(document).ready(function() {

      window.history.pushState(null, "", window.location.href);        

      window.onpopstate = function() {

          window.history.pushState(null, "", window.location.href);

      };

  });

</script> 
          