<#include "component://ofbiz-ag-grid/webapp/ofbiz-ag-grid/lib/agGridMacro.ftl"/>
<#include "component://dyna-screen/webapp/dyna-screen/lib/dyna_screen_macros.ftl"/>
<#include "component://appbar-portal/webapp/appbar-portal/global/appBarMacros.ftl"/>

<#macro sectionFrameHeader title titleId="" class="" extra="" extraLeft="" leftCol="col-lg-5 col-md-12 col-sm-12" rightCol="col-lg-7 col-md-12 col-sm-12" isShowHelpUrl="Y" style="">
<div class="top-band" style="${style!}">
   <div class="col-lg-12 col-md-12 col-sm-12 dot-line">
      <div class="row ${class!}">
         <div class="${leftCol!}">
            <h1 <#if titleId?has_content>id="${titleId}"</#if>  class="float-left mr-2 mb-0 mt-1 header-title mb-1vw">${title!}</h1>
            <#if extraLeft?has_content>
                <div class="text-left">
                    ${extraLeft!}
                </div>
            </#if>
         </div>
         <#if extra?has_content || (isShowHelpUrl?has_content && isShowHelpUrl=="Y")>
            <div class="${rightCol!}">
                <div class="text-right" id="extra-header-right-container">
                  ${extra!} 
                  <#if isShowHelpUrl?has_content && isShowHelpUrl=="Y">
                  	<#assign helpUrl = Static["org.groupfio.common.portal.util.DataHelper"].getHelpUrl(delegator, request.getRequestURI()) />
                  	${helpUrl!}
                  </#if>
               </div>
            </div>
         </#if>
      </div>
   </div>
</div>
</#macro>
<#macro sectionFrameHeaderTab title extra="" extraLeft="" isShowHelpUrl="Y" helpBtnStyle="" tabId="">
<div class="">
	<h2 class="d-inline-block">${title!}</h2>
	<#if extraLeft?has_content>
    	${extraLeft!}
    </#if>
	<#if extra?has_content || (isShowHelpUrl?has_content && isShowHelpUrl=="Y")>
	<span class="float-right" style="${helpBtnStyle!}">
	${extra!} 
	<#if isShowHelpUrl?has_content && isShowHelpUrl=="Y">
    	<#assign helpUrl = Static["org.groupfio.common.portal.util.DataHelper"].getTabHelpUrl(delegator, request.getRequestURI(), tabId) />
       	${helpUrl!}
   	</#if>
	</span>
	</#if>
</div>
</#macro>
<#macro sectionFrameHeaderActivity title extra="" extraLeftActivity="" isShowHelpUrl="Y">
<div class="top-band bg-light">
   <div class="col-lg-12 col-md-12 col-sm-12">
      <div class="row">
         <div class="col-lg-12 col-md-12 col-sm-12">
            <h1 class="float-left mr-2 mb-0">${title!}</h1>
            <#if extraLeftActivity?has_content>
                <div class="text-left">
                    ${extraLeftActivity!}
                </div>
            </#if>
         </div>
         <#if extra?has_content>
            <div class="col-lg-6 col-md-6 col-sm-12">
                <div class="text-right" id="extra-header-right-container">
                  ${extra!}
               </div>
            </div>
         </#if>
      </div>
   </div>
</div>
</#macro>

<#macro pageSectionHeader title id="" class ="pt-2 mb-2" extra="" extraLeft="">
   <div <#if id?has_content>id="${id!}"</#if> class="${class!}">
      <h2 class="d-inline-block">${title!}</h2>
      <#if extraLeft?has_content>
	       ${extraLeft!}
	  </#if>
	  <#if extra?has_content>
		  <div class="flot-icone">
		  	${extra!}
		  </div>
	  </#if>
	  <div class="clearfix"></div>
   </div>
</#macro>

<#macro errorRow id labelColSize="col-sm-4" inputColSize="col-sm-7" class="" isDisplay=false>
	<div class="form-group row" style="<#if isDisplay>display:block;<#else>display:none;</#if>" id="${id!}">
	    <label class="${labelColSize!} col-form-label"></label>
	    <div class="${inputColSize!}">
	        <div class="help-block with-errors list-unstyled" id="${id!}_error"></div>
	    </div>
	</div>
</#macro>

<#macro labels label required=false id="" labelColSize="col-sm-4" isMakerChange=false style="">
    <label class="${labelColSize} field-text<#if isMakerChange> bg-light</#if><#if required> font-weight-bold</#if>" ${style!} <#if id?has_content>id="${id}_label"</#if> >${label} <#if required><span class="text-danger font-weight-bold"> &#42;</span></#if></label>
</#macro>

<#macro inputCell id name="" type="text" value="" inputColSize="form-group" hint="" placeholder="" dataError="" tooltip="" style="" disabled=false readonly=false required=false  min="" max="" maxlength="" minlength="" pattern="" onkeyup="" step="">
	<div class="${inputColSize} ${id}" style="${style!}">
	   <input type="${type!}" class="form-control<#if required> required</#if>" value="${value!}"  id="${id}" name="<#if !name?has_content>${id}<#else>${name}</#if>" placeholder="${placeholder}" <#if disabled>disabled</#if> <#if readonly>readonly</#if> <#if required>required</#if> <#if dataError?has_content>data-error="${dataError}"</#if> <#if tooltip?has_content>data-original-title="${tooltip}"</#if> <#if onkeyup?has_content>onkeyup="${onkeyup}"</#if> <#if type=="number" && min?has_content> min="${min}" </#if> minlength="${minlength!}" maxlength="${maxlength!}" <#if pattern?has_content>pattern="${pattern!}"</#if> step="${step!}"/>
	   <div class="help-block with-errors" id="${id}_error"></div>
	</div>
<#if disabled>
    <input type="hidden" id="${id}" name="<#if !name?has_content>${id}<#else>${name}</#if>" value="${value!}"/> 
</#if>
</#macro>

<#macro inputRow id label="" name="" type="text" value="" labelColSize="col-sm-4" inputColSize="col-sm-8" hint="" class="" placeholder="" autocomplete="off" dataError="" style="" tooltip="" disabled=false readonly=false required=false  min="" max="" maxlength="" minlength="" pattern="" onkeyup="" onkeypress= "" onblur="" isMakerChange=false step="" iconClass="" multiple=false>
	<div class="form-group row ${id}" id="${id}_row" style="${style!}">
	    <#if label?has_content><@labels label=label id=id required=required labelColSize=labelColSize isMakerChange=isMakerChange/> </#if>
	    <div class="<#if iconClass?has_content>input-group</#if> ${inputColSize} left">
	       <input type="${type!}" class="form-control input-sm<#if class?has_content> ${class}</#if><#if required> required</#if>" value="${value!}"  id="${id}" name="<#if !name?has_content>${id}<#else>${name}</#if>" placeholder="${placeholder}" autocomplete="${autocomplete!}" <#if disabled>disabled</#if> <#if readonly>readonly</#if> <#if required>required</#if> <#if dataError?has_content>data-error="${dataError}"</#if> <#if tooltip?has_content>data-original-title="${tooltip}"</#if> <#if onkeyup?has_content>onkeyup="${onkeyup}"</#if> <#if onblur?has_content>onblur="${onblur}"</#if> <#if onkeypress?has_content>onkeypress="${onkeypress!}"</#if>  <#if type=="number" && min?has_content> min="${min}" </#if> <#if type=="number" && max?has_content> max="${max!}" </#if> <#if minlength?has_content>minlength="${minlength!}"</#if> <#if maxlength?has_content>maxlength="${maxlength!}"</#if> <#if pattern?has_content>pattern="${pattern!}"</#if> <#if step?has_content>step="${step!}"</#if> <#if multiple>multiple</#if> />
	       <#if iconClass?has_content>
	       <div class="input-group-append">
           	<span class="input-group-text"> 
             	<i class="${iconClass}" aria-hidden="true"></i>
			</span>
          	</div>
          	</#if>
	       <div class="help-block with-errors" id="${id}_error"></div>
	    </div>
	    <#if disabled>
	        <input type="hidden" id="${id}" name="<#if !name?has_content>${id}<#else>${name}</#if>" value="${value!}"/> 
	    </#if>
	</div>
</#macro>

<#macro inputArea id label="" name="" value="" labelColSize="col-sm-4" inputColSize="col-sm-8" placeholder="" rows="4" dataError="" style="" disabled=false readonly=false required=false maxlength="" minlength="" pattern="" onkeyup="" isMakerChange=false>
	<div class="form-group row ${id}" style="${style!}">
	   <#if label?has_content><@labels label=label id=id required=required labelColSize=labelColSize isMakerChange=isMakerChange/></#if>
	   <div class="${inputColSize}">
	      <textarea class="form-control" rows="${rows}" id="${id}" name="<#if !name?has_content>${id}<#else>${name}</#if>" placeholder="${placeholder}" <#if disabled>disabled</#if> <#if readonly>readonly</#if> <#if required>required</#if> minlength="${minlength!}" maxlength="${maxlength!}" <#if dataError?has_content>data-error="${dataError}"</#if> <#if tooltip?has_content>data-original-title="${tooltip}"</#if> <#if onkeyup?has_content>onkeyup="${onkeyup}"</#if> >${value!}</textarea>
	      <div class="help-block with-errors" id="${id}_error"></div>
	   </div>
	   <#if disabled>
	        <input type="hidden" id="${id}" name="<#if !name?has_content>${id}<#else>${name}</#if>" value="${value!}"/> 
	    </#if>
	</div>
</#macro>

<#macro textareaLarge label id rows hint="" name="" value="" txareaClass="" groupId="" dataError="" placeholder="" state=""  tooltip="" required=false>
<div class="form-group row <#if state?has_content>has-${state!}</#if>" <#if groupId?has_content>id="${groupId!}</#if>">
   <label  class="col-sm-4 col-form-label <#if required>text-danger</#if>" for="${id}">
     <h2 class="float-left col-form-label has-error">${label!}</h2>
   </label>
   <div class="col-sm-12">
      <textarea class="form-control <#if tooltip?has_content>tooltips</#if> <#if txareaClass?has_content>${txareaClass}</#if>" rows="${rows}" id="${id}" name="<#if !name?has_content>${id}<#else>${name}</#if>" placeholder="${placeholder}" <#if required>required</#if> <#if tooltip?has_content>data-original-title="${tooltip}"</#if> <#if dataError?has_content>data-error="${dataError}"</#if> autocomplete="off" >${value!}</textarea>
      <div class="form-control-focus">
      </div>
      <div class="help-block with-errors" id="${id}_error"></div>
      <#if hint?has_content>
        <span class="help-block with-errors">${hint}</span>
      </#if>
   </div>
</div>
</#macro>
<#macro textareaLargeWithoutLabel  id rows hint="" name="" value="" txareaClass="" groupId="" dataError="" placeholder="" state=""  tooltip="" required=false>
<div class="form-group row <#if state?has_content>has-${state!}</#if>" <#if groupId?has_content>id="${groupId!}</#if>">
     <h2 class="float-left col-form-label has-error">${label!}</h2>
   </label>
   <div class="col-sm-12">
      <textarea class="form-control <#if tooltip?has_content>tooltips</#if> <#if txareaClass?has_content>${txareaClass}</#if>" rows="${rows}" id="${id}" name="<#if !name?has_content>${id}<#else>${name}</#if>" placeholder="${placeholder}" <#if required>required</#if> <#if tooltip?has_content>data-original-title="${tooltip}"</#if> <#if dataError?has_content>data-error="${dataError}"</#if> >${value!}</textarea>
      <div class="form-control-focus">
      </div>
      <div class="help-block with-errors" id="${id}_error"></div>
      <#if hint?has_content>
        <span class="help-block with-errors">${hint}</span>
      </#if>
   </div>
</div>
</#macro>
<#macro formButton btn1=true btn1label="Submit" btn1type="button" btn1id="" btn2id="" btn3id="" btn2type="" btn3type="" btn2=false btn2label="" btn3=false btn3label="" btn1onclick="" btn2onclick="" btn3onclick="">
<div class="text-left ml-1">
   <#if btn1><button id="${btn1id!}" type="${btn1type}" class="btn btn-sm btn-primary" <#if btn1onclick?has_content>onclick="${btn1onclick!}" </#if> > ${btn1label}</button></#if>
   <#if btn2><button id="${btn2id!}" type="${btn2type}" class="btn btn-sm btn-secondary" <#if btn2onclick?has_content>onclick="${btn2onclick!}" </#if> > ${btn2label}</button></#if>
   <#if btn3><button id="${btn3id!}" type="${btn3type}" class="btn btn-sm btn-secondary" <#if btn3onclick?has_content>onclick="${btn3onclick!}" </#if> > ${btn3label}</button></#if>
</div>
</#macro>

<#macro submit label id="" onclick="" class="btn btn-sm btn-primary">
    <input type="submit" id="${id!}" name="<#if !name?has_content>${id}<#else>${name}</#if>" class="${class!}" <#if onclick?has_content>onclick="${onclick!}" </#if> value="${label!}" />
</#macro>
<#macro button label id="" onclick="" class="btn btn-sm btn-primary">
    <input type="button" id="${id!}" name="<#if !name?has_content>${id}<#else>${name}</#if>" class="${class!}" <#if onclick?has_content>onclick="${onclick!}" </#if> value="${label!}" />
</#macro>
<#macro cancel label id="" onclick="" class="btn btn-sm btn-secondary">
    <a href="${onclick!}" id="${id!}" name="<#if !name?has_content>${id}<#else>${name}</#if>" class="${class!}" <#if onclick?has_content>onclick="${onclick!}"</#if> >${label!} </a>
</#macro>
<#macro reset label id="" onclick="" class="btn btn-sm btn-secondary">
    <input id="${id!}" type="reset" class="${class!}" <#if onclick?has_content>onclick="${onclick!}" </#if> value="${label!}" />
</#macro>

<#-- Dropdown -->
<#macro dropdownCell id name="" options="" label="" labelColSize="col-sm-4" inputColSize="col-sm-8" placeholder="" value="" dataError="" style="" class="" readonly=false required=false disabled=false hint="" tooltip="" allowEmpty=false isMultiple="" dataLiveSearch=true onchange="" isMakerChange=false glyphiconClass= "" addOnTarget="" dataAttributes="">
    <#if label?has_content>
        <div class="form-group row ${id}" style="${style!}">
            <@labels label=label id=id required=required labelColSize=labelColSize isMakerChange=isMakerChange/>
            <div class="${inputColSize} ${id!}-input <#if class?has_content>${class!}</#if>" <#if dataAttributes?has_content>${dataAttributes!}</#if> >
               <select class="ui dropdown <#if dataLiveSearch>search</#if> form-control fluid show-tick <#if tooltip?has_content>tooltips</#if> ${id} " id="${id}" name="<#if !name?has_content>${id}<#else>${name}</#if>" <#if required>required</#if> <#if readonly>readonly</#if> <#if disabled>disabled</#if> <#if tooltip?has_content>data-original-title="${tooltip}"</#if> <#if onchange?has_content> onchange="${onchange!}"</#if> <#if dataLiveSearch> data-live-search="true" </#if><#if dataError?has_content>data-error="${dataError}"</#if> <#if isMultiple?has_content && isMultiple=="Y">multiple</#if> autocomplete="off" >
                <#if allowEmpty || placeholder?has_content>
                    <option value="">${placeholder!}</option>
                </#if>
                <#if options?has_content>
                    <#list options.entrySet() as entry>
                    	<#assign optionText = entry.value! />
                    	<#if optionText?has_content>
		                    <#if isMultiple?has_content && isMultiple=="Y" && value?contains(",") >
		                    	<#assign selectedVal = false />
		                    	<#list value?split(',') as val>
					        		<#assign listvalue = val?trim />
					        		<#if listvalue?has_content && listvalue == entry.key>
					        			<#assign selectedVal = true />
					        		</#if>
					        	</#list>
					        	<option value="${entry.key!}" <#if value?has_content && selectedVal>selected</#if> >${entry.value!}</option>
							<#else>
								<option value="${entry.key!}" <#if value?has_content && value == entry.key>selected</#if> >${entry.value!}</option>
	                    	</#if>
                    	</#if> 
                    </#list>
                </#if>
                </select>
                <div class="help-block with-errors" id="${id}_error">${hint!}</div>
            </div>
            <#if addOnTarget?has_content>
		        <div class="col-sm-1">
					<span class="glyphicon ${glyphiconClass!}" data-toggle="modal" data-target="#${addOnTarget!}" id="${addOnTarget!}_id"></span>                  
		        </div>
            </#if>
    <#else>
        <div class="form-group ${id} ${id!}-input <#if class?has_content>${class!}</#if>" style="${style!}">
         <select class="ui dropdown <#if dataLiveSearch>search</#if> form-control fluid show-tick <#if tooltip?has_content>tooltips</#if> ${id} " id="${id}" name="<#if !name?has_content>${id}<#else>${name}</#if>" <#if required>required</#if> <#if disabled>disabled</#if> <#if tooltip?has_content>data-original-title="${tooltip}"</#if> <#if onchange?has_content> onchange="${onchange!}"</#if> <#if dataLiveSearch> data-live-search="true"</#if> <#if isMultiple?has_content && isMultiple=="Y">multiple</#if> autocomplete="off" >
            <#if allowEmpty || placeholder?has_content>
                <option value="">${placeholder!}</option>
            </#if>
            <#if options?has_content>
                <#list options.entrySet() as entry>  
                	<#assign optionText = entry.value! />
                	<#if optionText?has_content>
	               		<#if isMultiple?has_content && isMultiple=="Y" && value?contains(",")>
		                	<#assign selectedVal = false />
		                	<#list value?split(',') as val>
				        		<#assign listvalue = val?trim />
				        		<#if listvalue?has_content && listvalue == entry.key>
				        			<#assign selectedVal = true />
				        		</#if>
				        	</#list>
				        	<option value="${entry.key!}" <#if value?has_content && selectedVal>selected</#if> >${entry.value!}</option>
				        <#else>
	                    	<option value="${entry.key!}" <#if value?has_content && optionText?has_content && value == entry.key>selected</#if> >${optionText!}</option>
	                    </#if>
                    </#if>
                </#list>
            </#if>
        </select>
        <#if addOnTarget?has_content>
	        <div class="col-sm-1">
				<span class="glyphicon ${glyphiconClass!}" data-toggle="modal" data-target="#${addOnTarget!}" id="${addOnTarget!}_id"></span>                  
	        </div>
        </#if>
        <div class="help-block with-errors" id="${id}_error">${hint!}</div>
    </#if>
        <#if disabled>
            <input type="hidden" id="${id}" name="<#if !name?has_content>${id}<#else>${name}</#if>" value="${value!}"/> 
        </#if>
            
        </div>
</#macro>

<#macro inputDate id name="" label="" labelColSize="col-sm-4" inputColSize="col-sm-8" placeholder="" value="" style="" default=true type="date" startDate="" dateViewmode="" dateFormat="" disablePastDate="N" disableFutureDate="N" required=false disabled=false isMakerChange=false minDate="" maxDate="">
<#if label?has_content>
    <div class="form-group row ${id}" style="${style!}">
       <@labels label=label id=id required=required labelColSize=labelColSize isMakerChange=isMakerChange/>
       <div class="input-group ${inputColSize} <#if type?has_content>${type!}</#if>" data-provide="datepicker" <#if startDate?has_content>data-date="${startDate!}"</#if> id="${id}_picker" >
          <input type="text" class="form-control input-sm" id="${id}" name="<#if !name?has_content>${id}<#else>${name}</#if>" placeholder="${placeholder!}" value="<#if value?has_content>${value!}</#if>" <#if required>required</#if> <#if disablePastDate?has_content && disablePastDate=="Y">data-date-min-date="<#if value?has_content && .now gte value?datetime('yyyy-MM-dd') >${value?datetime('yyyy-MM-dd')}<#else>${.now?string["yyyy-MM-dd"]}</#if>"</#if> <#if disableFutureDate?has_content && disableFutureDate=="Y">data-date-max-date="<#if value?has_content && .now lte value?datetime('yyyy-MM-dd') >${value?datetime('yyyy-MM-dd')}<#else>${.now?string["yyyy-MM-dd"]}</#if>"</#if> <#if dateViewmode?has_content>data-date-viewmode="${dateViewmode!}"</#if> <#if disabled>disabled</#if> <#if minDate?has_content>data-date-min-date="${minDate!}"</#if> <#if maxDate?has_content>data-date-max-date="${maxDate!}"</#if> <#if dateFormat?has_content>data-date-format="${dateFormat?upper_case}"</#if> autocomplete="off" />
           <div class="input-group-addon">
	         <i class="fa fa-calendar"></i>
	      </div>
          <div class="help-block with-errors col-12 p-0" id="${id}_error"></div>
          <#if disabled>
            <input type="hidden" id="${id}" name="<#if !name?has_content>${id}<#else>${name}</#if>" value="${value!}"/> 
          </#if>
       </div>
    </div>
<#else>
    <div class="form-group ${id}" style="${style!}">
       <div class="input-group <#if type?has_content>${type!}</#if>" data-provide="datepicker" <#if startDate?has_content>data-date="${startDate!}"</#if> id="${id}_picker" >
          <input type="text" class="form-control" id="${id}" name="<#if !name?has_content>${id}<#else>${name}</#if>" placeholder="${placeholder!}" value="<#if value?has_content>${value!}</#if>" <#if required>required</#if> <#if disablePastDate?has_content && disablePastDate=="Y">data-date-min-date="<#if value?has_content && .now gte value?datetime('yyyy-MM-dd') >${value?datetime('yyyy-MM-dd')}<#else>${.now?string["yyyy-MM-dd"]}</#if>"</#if> <#if disableFutureDate?has_content && disableFutureDate=="Y">data-date-max-date="<#if value?has_content && .now lte value?datetime('yyyy-MM-dd') >${value?datetime('yyyy-MM-dd')}<#else>${.now?string["yyyy-MM-dd"]}</#if>"</#if> <#if dateViewmode?has_content>data-date-viewmode="${dateViewmode!}"</#if> <#if disabled>disabled</#if> <#if minDate?has_content>data-date-min-date="${minDate!}"</#if> <#if maxDate?has_content>data-date-max-date="${maxDate!}"</#if> <#if dateFormat?has_content>data-date-format="${dateFormat?upper_case}"</#if> autocomplete="off" />
          <div class="input-group-addon">
	         <i class="fa fa-calendar"></i>
	      </div>
          <div class="help-block with-errors" id="${id}_error"></div>
          <#if disabled>
            <input type="hidden" id="${id}" name="<#if !name?has_content>${id}<#else>${name}</#if>" value="${value!}"/> 
          </#if>
       </div>
    </div>
</#if>

</#macro>

<#macro inputTime id name="" label="" labelColSize="col-sm-4" inputColSize="col-sm-8" placeholder="" value="" style="" default=true required=false disabled=false isMeridianTimeMode="N" isCurrentDefaultTime="Y" isShowSeconds="N" minuteStep="15" secondStep="15" isMakerChange=false>
<#if label?has_content>
    <div class="form-group row ${id}" style="${style!}">
       <@labels label=label id=id required=required labelColSize=labelColSize isMakerChange=isMakerChange/>
       <div class="input-group ${inputColSize} bootstrap-timepicker timepicker" >
          <input type="text" class="form-control input-sm" id="${id}" name="<#if !name?has_content>${id}<#else>${name}</#if>" placeholder="${placeholder!}" value="<#if value?has_content>${value!}</#if>" <#if required>required</#if> <#if disabled>disabled</#if> autocomplete="off" />
           <div class="input-group-addon">
	         <i class="fa fa-clock-o" aria-hidden="true"></i>
	      </div>
          <div class="help-block with-errors col-12 p-0" id="${id}_error"></div>
          <#if disabled>
            <input type="hidden" id="${id}" name="<#if !name?has_content>${id}<#else>${name}</#if>" value="${value!}"/> 
          </#if>
       </div>
    </div>
<#else>
    <div class="form-group ${id}" style="${style!}">
       <div class="input-group bootstrap-timepicker timepicker">
          <input type="text" class="form-control" id="${id}" name="<#if !name?has_content>${id}<#else>${name}</#if>" placeholder="${placeholder!}" value="<#if value?has_content>${value!}</#if>" <#if required>required</#if> <#if disabled>disabled</#if> autocomplete="off" />
          <div class="input-group-addon">
	         <i class="fa fa-clock-o" aria-hidden="true"></i>
	      </div>
          <div class="help-block with-errors" id="${id}_error"></div>
          <#if disabled>
            <input type="hidden" id="${id}" name="<#if !name?has_content>${id}<#else>${name}</#if>" value="${value!}"/> 
          </#if>
       </div>
    </div>
</#if>
<script>
$(document).ready(function() {  
	$('#${id}').timepicker({
		<#if isMeridianTimeMode?has_content && isMeridianTimeMode=="N">
		showMeridian: false,
		</#if>
		<#if isCurrentDefaultTime?has_content && isCurrentDefaultTime=="Y">
		defaultTime: 'current',
		</#if>
		<#if isShowSeconds?has_content && isShowSeconds=="Y">
		showSeconds: true,
		</#if>
		<#if minuteStep?has_content>
		minuteStep: ${minuteStep!},
		</#if>
		<#if secondStep?has_content>
		secondStep: ${secondStep!},
		</#if>
	});
});
</script>
</#macro>

<#macro inputDateTime id name="" label="" labelColSize="col-sm-4" inputColSize="col-sm-8" placeholder="" value="" style="" type="date" default=true required=false disabled=false   dateFormat="" disablePastDate="N" disableFutureDate="N"   isMeridianTimeMode="N" isCurrentDefaultTime="N" isShowSeconds="N" minuteStep="15" secondStep="15" isMakerChange=false isDisablePastTime="N">
<input type="hidden" id="${id}" name="<#if !name?has_content>${id}<#else>${name}</#if>" value="${value!}"/>
<#if isMeridianTimeMode?has_content && isMeridianTimeMode=="N"><#local timeFormat="HH:mm"><#else><#local timeFormat="hh:mm"></#if>
<#if label?has_content>
    <div class="form-group row ${id}" style="${style!}">
       <@labels label=label id=id required=required labelColSize=labelColSize isMakerChange=isMakerChange/>
       <div class="${inputColSize}">
       	<div class="row">
	       <div id="${id}_date_picker" class="input-group col-sm-6 <#if type?has_content>${type!}</#if>" >
	          <input type="text" class="form-control input-sm" id="${id}_date" name="<#if !name?has_content>${id}_date<#else>${name}_date</#if>" placeholder="${placeholder!}" value="<#if value?has_content>${value?string[dateFormat]}</#if>" <#if required>required</#if> <#if disablePastDate?has_content && disablePastDate=="Y">data-date-min-date="<#if value?has_content && .now gte value >${value?string["yyyy-MM-dd"]}<#else>${.now?string["yyyy-MM-dd"]}</#if>"</#if> <#if disableFutureDate?has_content && disableFutureDate=="Y">data-date-max-date="<#if value?has_content && .now lte value >${value?string["yyyy-MM-dd"]}<#else>${.now?string["yyyy-MM-dd"]}</#if>"</#if> <#if dateViewmode?has_content>data-date-viewmode="${dateViewmode!}"</#if> <#if disabled>disabled</#if> <#if minDate?has_content>data-date-min-date="${minDate!}"</#if> <#if maxDate?has_content>data-date-max-date="${maxDate!}"</#if> <#if dateFormat?has_content>data-date-format="${dateFormat?upper_case}"</#if> autocomplete="off" />
	          <div class="input-group-addon">
		         <i class="fa fa-calendar"></i>
		      </div>
	          <div class="help-block with-errors col-12 p-0"></div>
	          <#if disabled>
	          	<input type="hidden" id="${id}_date" name="<#if !name?has_content>${id}_date<#else>${name}_date</#if>" value="${value!}"/>
	          </#if>
	       </div>
	       
	       <div id="${id}_time_picker" class="input-group col-sm-6 bootstrap-timepicker timepicker " >
	          <input type="text" class="form-control input-sm" id="${id}_time" name="<#if !name?has_content>${id}_time<#else>${name}_time</#if>" placeholder="${placeholder!}" value="<#if value?has_content>${value?string[timeFormat!]}</#if>" <#if required>required</#if> <#if disabled>disabled</#if> autocomplete="off" />
	           <div class="input-group-addon">
		         <i class="fa fa-clock-o" aria-hidden="true"></i>
		      </div>
	          <div class="help-block with-errors col-12 p-0" id="${id}_time_error"></div>
	          <#if disabled>
	            <input type="hidden" id="${id}_time" name="<#if !name?has_content>${id}_time<#else>${name}_time</#if>" value="${value!}"/> 
	          </#if>
	       </div>
       	</div>
       </div>
       
    </div>
<#else>
    <div class="form-group ${id}" style="${style!}">
    
    <div class="row">	
    	<div class="input-group col-sm-6 <#if type?has_content>${type!}</#if>">
          <input type="text" class="form-control" id="${id}_date" name="<#if !name?has_content>${id}_date<#else>${name}_date</#if>" placeholder="${placeholder!}" value="<#if value?has_content>${value?string[timeFormat!]}</#if>" <#if required>required</#if> <#if disablePastDate?has_content && disablePastDate=="Y">data-date-min-date="<#if value?has_content && .now gte value >${value?string["yyyy-MM-dd"]}<#else>${.now?string["yyyy-MM-dd"]}</#if>"</#if> <#if disableFutureDate?has_content && disableFutureDate=="Y">data-date-max-date="<#if value?has_content && .now lte value >${value?string["yyyy-MM-dd"]}<#else>${.now?string["yyyy-MM-dd"]}</#if>"</#if> <#if dateViewmode?has_content>data-date-viewmode="${dateViewmode!}"</#if> <#if disabled>disabled</#if> <#if minDate?has_content>data-date-min-date="${minDate!}"</#if> <#if maxDate?has_content>data-date-max-date="${maxDate!}"</#if> <#if dateFormat?has_content>data-date-format="${dateFormat?upper_case}"</#if> autocomplete="off" />
          <div class="input-group-addon">
	         <i class="fa fa-calendar"></i>
	      </div>
          <div class="help-block with-errors" id="${id}_date_error"></div>
          <#if disabled>
            <input type="hidden" id="${id}_date" name="<#if !name?has_content>${id}_date<#else>${name}_date</#if>" value="${value!}"/> 
          </#if>
       </div>
       <div class="input-group col-sm-6 bootstrap-timepicker timepicker">
          <input type="text" class="form-control" id="${id}_time" name="<#if !name?has_content>${id}_time<#else>${name}_time</#if>" placeholder="${placeholder!}" value="<#if value?has_content>${value!}</#if>" <#if required>required</#if> <#if disabled>disabled</#if> autocomplete="off" />
          <div class="input-group-addon">
	         <i class="fa fa-clock-o" aria-hidden="true"></i>
	      </div>
          <div class="help-block with-errors" id="${id}_time_error"></div>
          <#if disabled>
            <input type="hidden" id="${id}_time" name="<#if !name?has_content>${id}_time<#else>${name}_time</#if>" value="${value!}"/> 
          </#if>
       </div>
   	</div>
   	    
    </div>
</#if>
<script>
$(document).ready(function() {  
	$('#${id}_time').timepicker({
		<#if isMeridianTimeMode?has_content && isMeridianTimeMode=="N">
		showMeridian: false,
		</#if>
		<#if isCurrentDefaultTime?has_content && isCurrentDefaultTime=="Y">
		defaultTime: 'current',
		<#else>
		defaultTime: "00:00",
		</#if>
		<#if isShowSeconds?has_content && isShowSeconds=="Y">
		showSeconds: true,
		</#if>
		<#if minuteStep?has_content>
		minuteStep: ${minuteStep!},
		</#if>
		<#if secondStep?has_content>
		secondStep: ${secondStep!},
		</#if>
	});
	<#-- 
	$('#${id}_time').timepicker().on('changeTime.timepicker', function(e) {
		<#if isDisablePastTime?has_content && isDisablePastTime=="Y">
		var selectedDate = new Date($('#${id}_date').val() + " " + $('#${id}_time').val());
		//console.log('date: '+$('#${id}_date').val());
		//console.log('time: '+$('#${id}_time').val());
		console.log('selectedDate: '+selectedDate);
		
		var currentDate = new Date();
		currentDate.setSeconds(0,0);
		console.log('currentDate: '+currentDate);
		
		if (selectedDate.getTime() < currentDate.getTime()) {
			var resetDate = moment().format('YYYY-MM-DD');
			console.log('resetDate: '+resetDate);
			$('#${id}_date').val( resetDate );
			
			console.log('reset to current time: '+currentDate.getHours()+':'+currentDate.getMinutes());
			$('#${id}_time').timepicker('setTime', currentDate.getHours()+':'+currentDate.getMinutes());
		}
		</#if>
	});
	 -->
});
</script>
</#macro>

<#macro inputDateRange id idFrom idTo nameFrom="" nameTo="" valueFrom="" valueTo="" label="" labelColSize="col-sm-4" inputColSize="col-sm-8" placeholderFrom="From Date" placeholderTo="To Date" style="" type="date" dateFormat="" disablePastDate="N" disableFutureDate="N" required=false disabled=false isMakerChange=false minDate="" maxDate="">
<div class="form-group row ${id}" style="${style!}">
   <@labels label=label id=id required=required labelColSize=labelColSize isMakerChange=isMakerChange/>
   <div class="input-group ${inputColSize}" >
	   <div class="input-group <#if type?has_content>${type!}</#if>" style="width: 45%;" id="${idFrom}_picker1">
	      <input type="text" class="form-control input-sm" id="${idFrom}" name="<#if !nameFrom?has_content>${idFrom}<#else>${nameFrom}</#if>" placeholder="${placeholderFrom!}" value="<#if valueFrom?has_content>${valueFrom!}</#if>" <#if required>required</#if> <#if disablePastDate?has_content && disablePastDate=="Y">data-date-min-date="<#if valueFrom?has_content && .now gte valueFrom?datetime('yyyy-MM-dd') >${valueFrom?datetime('yyyy-MM-dd')}<#else>${.now?string["yyyy-MM-dd"]}</#if>"</#if> <#if disableFutureDate?has_content && disableFutureDate=="Y">data-date-max-date="<#if valueFrom?has_content && .now lte valueFrom?datetime('yyyy-MM-dd') >${valueFrom?datetime('yyyy-MM-dd')}<#else>${.now?string["yyyy-MM-dd"]}</#if>"</#if> <#if disabled>disabled</#if> <#if minDate?has_content>data-date-min-date="${minDate!}"</#if> <#if maxDate?has_content>data-date-max-date="${maxDate!}"</#if> <#if dateFormat?has_content>data-date-format="${dateFormat?upper_case}"</#if> autocomplete="off" />
	       <div class="input-group-addon">
	         <i class="fa fa-calendar"></i>
	      </div>
	      <div class="help-block with-errors col-12 p-0" id="${idFrom}_error"></div>
	      
	      <#if disabled>
	        <input type="hidden" id="${idFrom}" name="<#if !nameFrom?has_content>${idFrom}<#else>${nameFrom}</#if>" value="${valueFrom!}"/> 
	      </#if>
	   </div>
	   <span class="input-group-addon var-btn">to</span>
	   <div class="input-group <#if type?has_content>${type!}</#if>" style="width: 45%;" id="${idTo}_picker2">
	      <input type="text" class="form-control input-sm" id="${idTo}" name="<#if !nameTo?has_content>${idTo}<#else>${nameTo}</#if>" placeholder="${placeholderTo!}" value="<#if valueTo?has_content>${valueTo!}</#if>" <#if required>required</#if> <#if disabled>disabled</#if> <#if dateFormat?has_content>data-date-format="${dateFormat?upper_case}"</#if> autocomplete="off" />
	       <div class="input-group-addon">
	         <i class="fa fa-calendar"></i>
	      </div>
	      <div class="help-block with-errors col-12 p-0" id="${idTo}_error"></div>
	      
	      <#if disabled>
	        <input type="hidden" id="${idTo}" name="<#if !nameTo?has_content>${idTo}<#else>${nameTo}</#if>" value="${valueTo!}"/>
	      </#if>
	   </div>
   </div>
<script>
$(document).ready(function() {  
	$("#${idFrom}_picker1").on("dp.change", function (e) {
     	$('#${idTo}_picker2').data("DateTimePicker").minDate(e.date);
   	});      
   
   	$("#${idTo}_picker2").on("dp.change", function (e) {
       $('#${idFrom}_picker1').data("DateTimePicker").maxDate(e.date);
   	});
});
</script>
</div>
</#macro>

<#macro inputDate2 id name="" label="" glyphicons="" labelColSize="col-sm-4" inputColSize="col-sm-8" placeholder="" value="" style="" default=true type="date" startDate="" dateViewmode="" dateFormat="YYYY-MM-DD" disablePastDate=false required=false disabled=false isMakerChange=false minDate="" maxDate="">

    <div class="form-group ${id}" style="${style!}">
       <div class="input-group <#if type?has_content>${type!}</#if>" data-provide="datepicker" <#if startDate?has_content>data-date="${startDate!}"</#if>>
          <input type="text" class="form-control" id="${id}" name="<#if !name?has_content>${id}<#else>${name}</#if>" placeholder="${placeholder!}" value="<#if value?has_content>${value!}</#if>" <#if required>required</#if> <#if disablePastDate>data-date-min-date="${.now?string["yyyy-MM-dd"]}"</#if> <#if dateViewmode?has_content>data-date-viewmode="${dateViewmode!}"</#if> <#if disabled>disabled</#if> <#if minDate?has_content>data-date-min-date="${minDate!}"</#if> <#if maxDate?has_content>data-date-max-date="${maxDate!}"</#if> autocomplete="off" />
          <div class="input-group-addon">
             <i class="${glyphicons}"></i>
          </div>
          <div class="help-block with-errors" id="${id}_error"></div>
          <#if disabled>
            <input type="hidden" id="${id}" name="<#if !name?has_content>${id}<#else>${name}</#if>" value="${value!}"/> 
          </#if>
       </div>
    </div>

</#macro>
<#macro dateTimeCell id name="" placeholder="" value="" startDate="" dateViewmode="" dateFormat="YYYY-MM-DD" disablePastDate=false required=false>
	<div class="form-group ${id}">
	   <div class="input-group datetime" data-provide="datepicker" <#if startDate?has_content>data-date="${startDate!}"</#if> <#if disablePastDate>data-date-start-date="+0d"</#if> <#if dateViewmode?has_content>data-date-viewmode="${dateViewmode!}"</#if>>
	      <input type="text" class="form-control" id="${id}" name="<#if !name?has_content>${id}<#else>${name}</#if>" placeholder="${placeholder!}" value="<#if value?has_content>${value!}</#if>" <#if required>required</#if> autocomplete="off" />
	      <div class="input-group-addon">
	         <i class="fa fa-calendar"></i>
	      </div>
	      <div class="help-block with-errors" id="${id}_error"></div>
	   </div>
	</div>
</#macro>

<#-- View page header -->
<#macro viewSectionHeader  title title1="" extra="">
	<div class="card-head margin-adj mt-0">
	   <div class="row">
	      <div class="col-md-6 col-sm-12">
	         <h6>${title!}</h6>
	         <h3>${title1}</h3>
	      </div>
	      <div class="col-md-6 col-sm-12">
	        <div class="text-right">
	            ${extra!}
	        </div>
	      </div>
	   </div>
	</div>
</#macro>

<#-- Use this macro to display the data in view mode -->
<#macro displayCell label value="" id="" labelColSize="col-sm-4" inputColSize="col-sm-8" style="" isLink="" isPhoneNumber="" desValue="" linkValue="" linkMap="">
	<#if isPhoneNumber?has_content && isPhoneNumber=="Y">
	<#local value=Static["org.groupfio.common.portal.util.DataHelper"].preparePhoneNumber(delegator, value)>
	</#if>
	<div class="form-group row" style="${style}">
	   	<div class="${labelColSize} col-form-label field-text" <#if id?has_content>id="${id}_label"</#if> >${label!}</div>
	   	<div id="${id}" class="${inputColSize} value-text">
	   	<#if isLink?has_content && isLink=="Y" && linkMap?has_content>
	   		<#if linkMap?has_content>
				<#assign linkMapData = Static["org.fio.admin.portal.util.DataUtil"].convertToMapIgnore(linkMap!) />
				<#local count = 0 />
                <#list linkMapData.entrySet() as entry>
                	<#local count = count+1 />
                	<#assign key = entry.key! />
                	<#assign val = entry.value! />
                	
                	
                	<a href="${val?trim}" id="link_${count!}" class="" target="_blank"><#if key?has_content>${key!}</#if></a><#if (linkMapData.entrySet()?size > 1) && (count<linkMapData.entrySet()?size)>, </#if>
                </#list>
            </#if>
			<input type="hidden" id="${id!}_desc" value="<#if desValue?has_content>${desValue!}<#else>${value!}</#if>"/>
	   	<#elseif isLink?has_content && isLink=="Y" && (desValue?contains(", ") || value?contains(", "))>
	   		<#assign linkList = ""/>
	   		<#if desValue?has_content>
	   			<#assign linkList = desValue! />
	   		<#elseif value?has_content>
	   			<#assign linkList = value! />
	   		</#if>
	   		<#assign linkValueList = linkList?split(", ") />
	   		<#if linkValueList?has_content>
	   			<#assign count = 0 />
		   		<#list linkValueList as val>
		   			<#assign count = count+1 />
				  	<a href="${linkValue!}${val?trim}" id="link_${id!}" class="" target="_blank"><#if val?has_content>${val!}<#else>${value!}</#if></a><#if (linkValueList?size > 1) && (count<linkValueList?size)>, </#if>
				</#list>
				
			</#if>
			<input type="hidden" id="${id!}_desc" value="<#if desValue?has_content>${desValue!}<#else>${value!}</#if>"/>
			
	   	<#elseif isLink?has_content && isLink=="Y">
	   		<a href="${linkValue!}" id="link_${id!}" class="" target="_blank"><#if desValue?has_content>${desValue!}<#else>${value!}</#if></a>
	   		<input type="hidden" id="${id!}_desc" value="<#if desValue?has_content>${desValue!}<#else>${value!}</#if>"/>
	   	<#else>
	   		<#if desValue?has_content>${desValue!}<#else>${value!}</#if>
	   		
	   	</#if>
	   	</div>
	</div>
</#macro>
<#macro displayCellForDiffLabelSize label value="" id="" labelColSize="col-lg-5" inputColSize="col-lg-8">
	<div class="form-group row">
	   <div class="${labelColSize} col-form-label field-text">${label!}</div>
	   <div id="${id}" class="${inputColSize} value-text">${value!}</div>
	</div>
</#macro>

<#macro sectionHeader title >
	<div class="page-header">
	   <h2>${title!}</h2>
	   <div class="clearfix"></div>
	</div>
</#macro>

<#macro headerH1 title class="bg-light pl-1 mt-2">
    <h1 class="${class!}">${title!} </h1>
</#macro>
<#macro headerH2 title id="" href="" class="bg-light pl-1 mt-2">
    <h2 class="${class!}" id="${id}" href="${href}">${title!} </h2>
</#macro>
<#macro headerH3 title id="" class="bg-light pl-1 mt-2">
    <h3 id="${id}" class="${class!}">${title!} </h3>
</#macro>
<#macro headerH4 title class="bg-light pl-1 mt-2">
    <h4 class="${class!}">${title!} </h4>
</#macro>
<#macro headerH5 title id="" class="bg-light pl-1 mt-2">
    <h5 id="${id}" class="${class!}">${title!} </h5>
</#macro>
<#macro span title id="" icon="" class="pl-1 mt-2">
    <span class="${class!}" id="${id!}">${icon!}${title!} </span>
</#macro>


<#macro arrowDownToggle>
	<div class="iconek">
	   <div class="arrow-down" style="margin-top: 5px;" onclick="this.classList.toggle('active')"></div>
	</div>
</#macro>

<#macro inputHidden id=""  name="" value="">
    <input type="hidden" id="${id!}" name="<#if !name?has_content>${id}<#else>${name}</#if>" value="${value!}"/> 
</#macro>

<#macro checkbox id label="" type="checkbox" class="form-check-input" name="" value="" style="" checked=false required=false disabled=false labelColSize="col-sm-4" inputColSize="col-sm-8" isMakerChange=false>
	<div class="form-group row ${id}" style="${style!}">
	   <#if label?has_content>			
	   <@labels label=label id=id required=required labelColSize=labelColSize isMakerChange=isMakerChange/>
	   </#if>
	   <div class="${inputColSize}">
	      <div class="form-check form-check-inline mt-2">
	         <input class="${class}" type="${type!}" id="${id}" name="<#if !name?has_content>${id}<#else>${name}</#if>" value="${value}" <#if checked || value=="Y">checked</#if> <#if disabled>disabled</#if> <#if required>required</#if> autocomplete="off" >
	      </div>
	      <div class="help-block with-errors" id="${id}_error"></div>
	   </div>
	</div>
</#macro>

<#macro checkboxField id type="checkbox" class="form-check-input" name="" value="" style="" checked=false required=false disabled=false>
	<div class="form-check form-check-inline mt-2">
		<input class="${class}" type="${type!}" id="${id}" name="<#if !name?has_content>${id}<#else>${name}</#if>" value="${value}" <#if checked>checked</#if> <#if disabled>disabled</#if> <#if required>required</#if> autocomplete="off" >
	</div>
</#macro>

<#macro inputCheckBox id name="" label="" optionList="" optionValues="" enableCheckLabel=true class="form-check-input" disabled=false labelColSize="col-sm-4" inputColSize="col-sm-8" isMakerChange=false required=false>
	<div class="form-group row ${id}">
	    <@labels label=label id=id required=required labelColSize=labelColSize isMakerChange=isMakerChange/>
	    <div class="${inputColSize}">
	        <#list optionList.entrySet() as entry>
	            <#assign checked="" />
	            <#if optionValues?has_content>
	                <#list optionValues as value>
	                    <#if entry.key==value>
	                        <#assign checked="checked" />
	                    </#if>
	                </#list>
	            </#if>
	            <div class="form-check form-check-inline mt-2">
	                <input class="${class} ${id}" type="checkbox" name="<#if !name?has_content>${id}<#else>${name}</#if>" id="${entry.key!id}" value="${entry.value!}" <#if checked?has_content>${checked!}</#if> <#if disabled>disabled</#if> <#if required>required</#if>>
	                <#if enableCheckLabel>
	                    <label class="form-check-label noselect" for="${entry.key!}">${entry.value!}</label>
	                </#if>
	            </div>
	        </#list>
	        <div class="text-danger" id="<#if !name?has_content>${id}<#else>${name}</#if>_error"></div>
	    </div>
	</div>
</#macro>

<#macro inputCountry name label="" countryValue="" stateValue="" dataError = "" required=false defaultCountry=true tooltip="" dataLiveSearch=true disabled=false onchange="" isMakerChange=false labelColSize="col-sm-4" inputColSize="col-sm-8">
    
<#assign defaultCountryGeoId = ""/>
<#if defaultCountry>
  	<#assign findMap = Static["org.ofbiz.base.util.UtilMisc"].toMap("parameterId", "DEFAULT_COUNTRY")>
	<#assign defaltCountryValue = delegator.findOne("PretailLoyaltyGlobalParameters", findMap, true)!>
    
    <#if defaltCountryValue?has_content>
    <#assign defaultCountryGeoId = defaltCountryValue.value?if_exists/>
    <#else>
    <#assign defaultCountryGeoId = (Static["org.ofbiz.base.util.UtilProperties"].getPropertyValue("admin-portal.properties", "default.country.geo.id"))?default("SGP")/>
    </#if>
</#if>
<#if countryValue?has_content>
    <#assign defaultCountryGeoId="${countryValue}"/>
</#if>
<#assign countries = delegator.findByAnd("Geo",Static["org.ofbiz.base.util.UtilMisc"].toMap("geoTypeId", "COUNTRY"),[] ,false)/>
    
<div class="form-group <#if label?has_content> row</#if>">
	<#if label?has_content>
    <@labels label=label required=required labelColSize=labelColSize isMakerChange=isMakerChange/>
    </#if>
    <div class="${inputColSize} generalCountryGeoId-input">
        <select class="ui dropdown <#if dataLiveSearch>search</#if> form-control fluid show-tick <#if tooltip?has_content>tooltips</#if> ${name}" id="generalCountryGeoId" name="<#if !name?has_content>${id}<#else>${name}</#if>" <#if required>required</#if> <#if dataError?has_content>data-error="${dataError}"</#if> <#if disabled>disabled</#if> <#if tooltip?has_content>data-original-title="${tooltip}"</#if> <#if onchange?has_content> onchange="${onchange!}"</#if> <#if dataLiveSearch> data-live-search="true"</#if> autocomplete="off" >
            <option value="">Please Select</option>
            <#list countries as country>
                <#if defaultCountryGeoId?has_content>
                    <#if defaultCountryGeoId == country.geoId><#assign selected="selected=\"selected\""><#else><#assign selected=""></#if>
                    <#else>
                        <#assign selected="">
                    </#if>
                <option ${selected} value="${country.geoId}">${country.get("geoName", locale)}</option>
            </#list>
        </select>
        <#if disabled>
            <input type="hidden" id="generalCountryGeoId" name="<#if !name?has_content>${id}<#else>${name}</#if>" value="${value!}"/>   
        </#if>
        <div class="help-block with-errors" id="${name}_error"></div>
    </div>    
</div>
    
<script type="text/javascript">
jQuery(document).ready(function() {
	if (jQuery('#generalCountryGeoId').length) {
		jQuery('#generalCountryGeoId').change(function(e, data) {
			getDependentDropdownValues('getAssociatedStateList', 'countryGeoId', 'generalCountryGeoId', 'generalStateProvinceGeoId', 'stateList', 'geoId', 'geoName', '${stateValue!}');
		});
		
		$(".generalCountryGeoId-input").one( "click",function(){
			getDependentDropdownValues('getAssociatedStateList', 'countryGeoId', 'generalCountryGeoId', 'generalStateProvinceGeoId', 'stateList', 'geoId', 'geoName', '${stateValue!}');
		});
	}
});
</script>
</#macro>

<#macro inputState name label="" tooltip="" dataLiveSearch=true required=false isMakerChange=false labelColSize="col-sm-4" inputColSize="col-sm-8">
    <#if label?has_content>
        <div class="form-group row">
            <@labels label=label required=required labelColSize=labelColSize isMakerChange=isMakerChange/>
            <div class="${inputColSize}">
                <select class="ui dropdown <#if dataLiveSearch>search</#if> form-control input-sm <#if tooltip?has_content>${tooltip}</#if>" name=${name} id="generalStateProvinceGeoId"  <#if required>required</#if> autocomplete="off" >
                    <option value="" data-content="<span class='nonselect'>Please Select</span>" selected>Please Select</option>
                </select>
            </div>
    <#else>
        <div class="form-group">
            <select class="ui dropdown <#if dataLiveSearch>search</#if> form-control input-sm <#if tooltip?has_content>${tooltip}</#if>" name=${name} id="generalStateProvinceGeoId"  <#if required>required</#if>>
                <option value="" data-content="<span class='nonselect'>Please Select</span>" selected>Please Select</option>
            </select>
    </#if>
    
        <div class="help-block with-errors" id="${name}_error"></div>
    </div>
</#macro>
	
<#macro radioInputCell name options label="" id="" value="" radioInline=false required=false disabled=false isMakerChange=false labelColSize="col-sm-4" inputColSize="col-sm-8" style="">
	<div class="form-group <#if label?has_content>row</#if> ${id}" style="${style!}">
		<#if label?has_content>
	   <@labels label=label id=id required=required labelColSize=labelColSize isMakerChange=isMakerChange/>
	   </#if>
	   <div class="${inputColSize}">
	      <#if options?has_content>
	      	<#local index=0>
	         <#list options.entrySet() as entry>  
	         <div class="form-check-inline" style="margin-top: 6px;">
	            <label for="${id}_${index}">
	            <input type="radio" id="${id}_${index}" name="${name}" value="${entry.key!}" class="form-check-input" <#if value?has_content && entry.key == value>checked</#if> <#if disabled>disabled</#if> <#if required>required</#if> >
	            <span></span>
	            <span class="check"></span>
	            <span class="box"></span>
	            ${entry.value!}</label>
	         </div>
	         <#local index=index+1>
	         </#list>
	      </#if>
	      <div class="help-block with-errors" id="${id}_error"></div>
	   </div>
	   
	</div>
</#macro>

<#macro inputRowAddOn id label name="" type="text" value="" labelColSize="col-sm-4" inputColSize="col-sm-8" hint="" class="" placeholder="" autocomplete="off" dataError="" style="" tooltip="" disabled=false readonly=false required=false  min="" max="" maxlength="" minlength="" pattern="" onkeyup="" onblur="" isMakerChange=false step="" glyphiconClass= "" addOnTarget="">
	<div class="form-group row ${id}" id="${id}_row" style="${style!}">
	    <@labels label=label id=id required=required labelColSize=labelColSize isMakerChange=isMakerChange/>
	    <div class="${inputColSize!}">
	      <div class="input-group">
	       <input type="${type!}" class="form-control input-sm<#if class?has_content> ${class}</#if>" value="${value!}"  id="${id}" name="<#if !name?has_content>${id}<#else>${name}</#if>" placeholder="${placeholder}" autocomplete="${autocomplete!}" <#if disabled>disabled</#if> <#if readonly>readonly</#if> <#if required>required</#if> <#if dataError?has_content>data-error="${dataError}"</#if> <#if tooltip?has_content>data-original-title="${tooltip}"</#if> <#if onkeyup?has_content>onkeyup="${onkeyup}"</#if> <#if onblur?has_content>onblur="${onblur}"</#if> <#if type=="number" && min?has_content> min="${min}" </#if> minlength="${minlength!}" maxlength="${maxlength!}" <#if pattern?has_content>pattern="${pattern!}"</#if> step="${step!}" />
	         <#if addOnTarget?has_content>
		         <span class="input-group-addon">
					<span class="glyphicon ${glyphiconClass!}" data-toggle="modal" data-target="#${addOnTarget!}" id="${addOnTarget!}_id"></span>                  
		         </span>
	         </#if>
	      </div>
	       <div class="help-block with-errors" id="${id}_error"></div>
	    </div>
	    <#if disabled>
	        <input type="hidden" id="${id}" name="<#if !name?has_content>${id}<#else>${name}</#if>" value="${value!}"/> 
	    </#if>
	</div>
</#macro>
<#macro inputRowAddOn2 id label="" name="" type="text" value="" labelColSize="col-sm-4" inputColSize="col-sm-8" hint="" class="" placeholder="" autocomplete="off" dataError="" style="" tooltip="" disabled=false readonly=false required=false  min="" max="" maxlength="" minlength="" pattern="" onkeyup="" onblur="" isMakerChange=false step="" glyphiconClass= "" addOnTarget="">
	<div class="form-group row ${id}" id="${id}_row" style="${style!}">
	    <div class="${inputColSize!}">
	      <div class="input-group">
	       <input type="${type!}" class="form-control input-sm<#if class?has_content> ${class}</#if>" value="${value!}"  id="${id}" name="<#if !name?has_content>${id}<#else>${name}</#if>" placeholder="${placeholder}" autocomplete="${autocomplete!}" <#if disabled>disabled</#if> <#if readonly>readonly</#if> <#if required>required</#if> <#if dataError?has_content>data-error="${dataError}"</#if> <#if tooltip?has_content>data-original-title="${tooltip}"</#if> <#if onkeyup?has_content>onkeyup="${onkeyup}"</#if> <#if onblur?has_content>onblur="${onblur}"</#if> <#if type=="number" && min?has_content> min="${min}" </#if> minlength="${minlength!}" maxlength="${maxlength!}" <#if pattern?has_content>pattern="${pattern!}"</#if> step="${step!}" />
	         <#if addOnTarget?has_content>
		         <span class="input-group-addon">
					<span class="glyphicon ${glyphiconClass!}" data-toggle="modal" data-target="#${addOnTarget!}" id="${addOnTarget!}_id"></span>
		         </span>
	         </#if>
	      </div>
	       <div class="help-block with-errors" id="${id}_error"></div>
	    </div>
	    <#if disabled>
	        <input type="hidden" id="${id}" name="<#if !name?has_content>${id}<#else>${name}</#if>" value="${value!}"/>
	    </#if>
	</div>
</#macro>
<#macro inputPhoneRow label name="" values="" labelColSize="col-sm-4" inputColSize="col-sm-8" required=false isCountryCode=true isAreaCode=false isContactNumber=true isExtension=false countryCodePattern="([+]?\\d{1,2})" areaCodePattern="(\\d{1,4})" phoneNumberPattern="(\\d*)" extensionPattern="(\\d{1,4})" countryError="Please enter a valid Country Code" areaCodeError="Please enter a valid Area Code" contactNumberError="Enter a valid Contact Number" extensionError="Enter a valid Extension"  isMakerChange=false>
	<#if values?has_content>
		<#list values.entrySet() as entry>
			<#if entry.key.contains("countryCode")>
				<#assign countryCode = entry.value />
			<#elseif entry.key.contains("areaCode")>
				<#assign areaCode = entry.value />
			<#elseif entry.key.contains("contactNumber") || entry.key.contains("mobileNumber") || entry.key.contains("phoneNumber")>
				<#assign contactNumber = entry.value />
			<#elseif entry.key.contains("extension")>
				<#assign extension = entry.value />
			</#if>
		</#list>
	</#if>
	
	<div class="form-group row ${name!}">
	    <@labels label=label required=required labelColSize=labelColSize isMakerChange=isMakerChange/>
	    <div class="${inputColSize!} row<#if name?has_content> ${name!}_1</#if>">
	    	<#if isCountryCode>
		        <div class="col-sm-2 pr-2">
		            <input type="tel" class="form-control tooltips" value="${countryCode!}" id="primaryPhoneCountryCode" name="primaryPhoneCountryCode" pattern="${countryCodePattern!}" data-error="${countryError!}" data-original-title="Country Code" autocomplete="off"  value="" maxlength="3" />
		        </div>
	        </#if>
	        <#if isAreaCode>
		        <div class="col-sm-3 <#if isCountryCode>pl-2</#if>">
		            <input type="tel" class="form-control tooltips" value="${areaCode!}" id="primaryPhoneAreaCode" name="primaryPhoneAreaCode" pattern="${areaCodePattern}" data-error="${areaCodeError!}" data-original-title="Area Code" autocomplete="off"  value="" maxlength="6" />
		        </div>
	        </#if>
	        
	        <#if isContactNumber>
		        <div class="col-sm-5 <#if isCountryCode || isAreaCode>pl-2</#if>">
		            <input type="tel" class="form-control tooltips" value="${contactNumber!}" id="primaryPhoneNumber" name="primaryPhoneNumber" pattern="${phoneNumberPattern!}" data-error="${contactNumberError!}" data-original-title="<#if isAreaCode>Phone Number<#else>Mobile Number</#if>" autocomplete="off"  value="" maxlength="10">
		        </div>
	        </#if>
	        
	        <#if isExtension>
	        <div class="col-sm-4 <#if isCountryCode || isAreaCode || isContactNumber>pl-2</#if>">
	            <input type="tel" class="form-control tooltips" value="${extension!}" id="primaryPhoneExtension" name="primaryPhoneExtension" pattern="${extensionPattern!}" data-error="${extensionError!}" data-original-title="Phone Extension" autocomplete="off"  value="" maxlength="10">
	        </div>
	        </#if>
	        
	        <div class="col-sm-8">
	            <div class="help-block with-errors" id="phone_error"></div>
	        </div>
	    </div>
	</div>
</#macro>
<#-- newly added as temporary -->
<#macro multiSelectInput id label="" options="" hint="" name="" value="" labelColSize="col-sm-4" inputColSize="col-sm-8" required=false multiple=false disabled=false tooltip="" title="" onchange =""  emptyText="Please Select" allowEmpty=false dataLiveSearch=false isMakerChange=false>
	<div class="form-group row mr <#if required>has-error</#if> ${id} ">
	   <#if label?has_content>
	   		<@labels label=label id=id required=required labelColSize=labelColSize isMakerChange=isMakerChange/>
	   </#if>
	   <div class="${inputColSize!}">
	      <select class="ui dropdown <#if dataLiveSearch>search</#if> form-control input-sm <#if tooltip?has_content>tooltips</#if>" id="${id}" name="<#if !name?has_content>${id}<#else>${name}</#if>" title="${title!}" <#if required>required</#if> <#if disabled>disabled</#if> <#if tooltip?has_content>data-original-title="${tooltip}"</#if> onchange="${onchange!}" <#if multiple>multiple</#if> autocomplete="off" >
	      <#if allowEmpty>
	      <option value="" data-content="<span class='nonselect'>Select ${emptyText!}</span>" selected>Select ${emptyText!}</option>
	      </#if>
	      <#if options?has_content>
	      <#list options.entrySet() as entry>  
	      <option value="${entry.key}" <#if value?has_content && value == entry.key>selected</#if> >${entry.value!}</option>
	      </#list>
	      </#if>
	      </select>
	      <#if disabled>
	      <input type="hidden" id="${id}" name="<#if !name?has_content>${id}<#else>${name}</#if>" value="${value!}"/>	
	      </#if>
	      <div class="form-control-focus">
	      </div>
	      <span class="help-block with-errors">${hint}</span>
	   </div>
	</div>
</#macro>
<#-- newly added as temporary -->

<#macro inputRowPicker id pickerWindow label="" name="" value="" desValue="" labelColSize="col-sm-4" inputColSize="col-sm-8" hint="" class="" placeholder="" autocomplete="off" dataError="" style="" tooltip="" required=false isMakerChange=false glyphiconClass= "fa fa-id-card" readonly=false onkeydown=false isAutoCompleteEnable="N" isTriggerChangeEvent="Y" autoCompleteMinLength="3" autoCompleteUrl="" autoCompleteLabelFieldId="" autoCompleteValFieldId="" autoCompleteFormId="" modalData="">
<#if isAutoCompleteEnable?has_content && isAutoCompleteEnable=="Y" && autoCompleteUrl?has_content && autoCompleteLabelFieldId?has_content && autoCompleteValFieldId?has_content>
<#local onkeydown = true>
</#if>
<div class="form-group <#if label?has_content>row</#if> ${id}" id="${id}_row" style="${style!}">
    <#if label?has_content><@labels label=label id=id required=required labelColSize=labelColSize isMakerChange=isMakerChange/></#if>
    <div class="${inputColSize!}">
      <div class="input-group">
       	<input type="text" class="form-control input-sm<#if class?has_content> ${class}</#if>" value="${desValue!}"  id="${id}_desc" tabindex="-1" placeholder="${placeholder}" autocomplete="${autocomplete!}" <#if required>required</#if> <#if dataError?has_content>data-error="${dataError!}"</#if> <#if tooltip?has_content>data-original-title="${tooltip!}"</#if> <#if readonly>readonly</#if> <#if !onkeydown>onkeydown="return false"<#else>onkeydown="return true"</#if>/>
       	<#-- <input type="text" class="form-control input-sm<#if class?has_content> ${class}</#if>" value="${desValue!}"  id="${id}_desc" tabindex="-1" placeholder="${placeholder}" autocomplete="${autocomplete!}" <#if required>required</#if> <#if dataError?has_content>data-error="${dataError}"</#if> <#if tooltip?has_content>data-original-title="${tooltip}"</#if> <#if readonly>readonly</#if> /> -->
         <#if pickerWindow?has_content>
	         <span class="input-group-addon picker-window-erase" data-pickerWindow="${pickerWindow}" data-pickerInputId="${id}" tabindex="0">
				<i class="fa fa-eraser" aria-hidden="true"></i>                  
	         </span>
	         <span class="input-group-addon picker-window" data-pickerWindow="${pickerWindow}" data-pickerInputId="${id}" tabindex="0" <#if modalData?has_content>${modalData!}</#if>>
				<i class="${glyphiconClass!}" aria-hidden="true"></i>                  
	         </span>
         </#if>
      </div>
       <div class="help-block with-errors" id="${id}_error"></div>
    </div>
    <input type="hidden" id="${id}_val" name="<#if !name?has_content>${id}<#else>${name}</#if>" value="${value!}"/> 
    <input type="hidden" id="${id}_alter" name="${id!}_alter" value=""/> 
</div>
<script>
$(document).ready(function() {
	<#if isAutoCompleteEnable?has_content && isAutoCompleteEnable=="Y" && autoCompleteUrl?has_content && autoCompleteLabelFieldId?has_content && autoCompleteValFieldId?has_content>
  	$("#${id!}_desc").autocomplete({
	    source: function (request, response) {
			let inputData = new Map();
			inputData.set('searchText', $('#${id}_desc').val());
			inputData.set('externalLoginKey', '${requestAttributes.externalLoginKey!}');
			<#if autoCompleteFormId?has_content>
		    $.each($("#${autoCompleteFormId}").serializeArray(), function( i, field ) {
		      	inputData.set(field.name, field.value);
		    });
			</#if>
			inputData = Array.from(inputData, ([name, value]) => ({ name, value }));
	        $.ajax({
	            url: '${autoCompleteUrl!}',
	            //data: {'searchText': $('#${id}_desc').val(), 'externalLoginKey': '${requestAttributes.externalLoginKey!}'},
	            data: JSON.parse(JSON.stringify(inputData)),
	            type: "POST",
	            success: function (result) {
	            	var dataList = Array.isArray(result) ? result : result.data;
	                response($.map(dataList, function (item) {
						var labelItems = "";
						var alternative = "";
	                    <#if autoCompleteLabelFieldId?contains(",")>
					  		<#local filedIdList = autoCompleteLabelFieldId?split(',') />
					  		<#local i = 1 />
					  		<#list filedIdList as fieldId>
					    		<#assign labelField = fieldId?trim />
					    		<#if i == 1>
					    			alternative = item['${labelField!}'];
					    		</#if>
					    		<#if i == filedIdList?size!>
					    			labelItems = labelItems+ item['${labelField!}'];
					    		<#else>
					    			labelItems = labelItems+ item['${labelField!}'] + ' - ';
					    		</#if>
					    		<#local i = i+1/>
					        </#list>
					    <#else>
					    	labelItems=item['${autoCompleteLabelFieldId!}'];
					  	</#if>
	                    return {
	                        label: labelItems,
	                        val: item['${autoCompleteValFieldId!}'],
	                        alter: alternative
	                        
	                    }			              
	                }));
	                $(".ui-helper-hidden-accessible").hide();
	            }
	        });
	    },
	    select: function (e, i) {
	        console.log('label: '+i.item.label+', val: '+i.item.val);
	        $('#${id}_val').val(i.item.val);
	        $('#${id}_alter').val(i.item.alter);
	        <#if isTriggerChangeEvent?has_content && isTriggerChangeEvent=="Y">
	         setTimeout(function(){
	        	$("#${id}_desc").blur();
	        }, 1);
	        </#if>
	    },
	    minLength: ${autoCompleteMinLength!},
	      
	});
	</#if>
});
</script>	
</#macro>

<#macro displayCurrency id="" currencyUomId="" value="" inputColSize="col-sm-7">
  	<#if currencyUomId == "">
    	<#assign isoCode = Static["org.fio.homeapps.util.DataUtil"].getGlobalValue(delegator, "CURRENCY_UOM_ID") />
  	<#else>
    	<#assign isoCode = currencyUomId />
  	</#if>
  	<div id="${id}" class="${inputColSize} value-text">
  		<#if value?has_content>  
  			<@ofbizCurrency amount=value isoCode=isoCode />
  		</#if>
  	</div>
</#macro>
<#macro displayCurrencyRow label id="" value="" labelColSize="col-sm-4" inputColSize="col-sm-7" style="">
	<div class="form-group row" style="${style}">
	   <div class="${labelColSize} col-form-label field-text" <#if id?has_content>id="${id}_label"</#if> >${label!}</div>
	   <@displayCurrency id=id currencyUomId=currencyUomId value=value inputColSize=inputColSize />
	</div>
</#macro>

<#macro inputRichTextArea id name="" value="" placeholder="" height="100" dataError="" editorType="LITE_RICH_TEXT" disabled=false readonly=false required=false minHeight="" maxHeight="" onkeyup="" onkeydown="">
  	<textarea class="form-control" id="${id}" name="<#if !name?has_content>${id}<#else>${name}</#if>" placeholder="${placeholder}" <#if readonly>readonly</#if> <#if required>required</#if> <#if dataError?has_content>data-error="${dataError}"</#if> autocomplete="off" >${value!}</textarea>
   	<div class="help-block with-errors" id="${id}_error"></div>
   	<#if disabled>
        <input type="hidden" id="${id}" name="<#if !name?has_content>${id}<#else>${name}</#if>" value="${value!}"/> 
    </#if>
<script>
$(document).ready(function() {
  	$('#${id}').summernote({
  		focus: true,
  		lang: 'en-US',
  		spellCheck: true,
  		fontName: "Arial",
  		<#if editorType?has_content && editorType == "FULL_RICH_TEXT">
  		toolbar: [
  		  ['view', ['codeview', 'fullscreen']],	
		  ['style', ['style']],
		  ['font', ['fontname', 'fontsize', 'bold', 'underline', 'clear', 'backcolor']],
		  ['color', ['color']],
		  ['para', ['ul', 'ol', 'paragraph', 'height']],
		  ['insert', ['link', 'picture', 'video', 'table', 'hr']],
		],
		<#elseif editorType?has_content && editorType == "LITE_RICH_TEXT">
  		toolbar: [
  		    ['view', ['codeview']],
		    ['style', ['bold', 'italic', 'underline', 'clear']],
		    ['font', ['fontname']],
		    ['fontsize', ['fontsize']],
		    ['color', ['color']],
		    ['para', ['ul', 'ol', 'paragraph']],
		    ['height', ['height']]
		  ],
		</#if>
  		<#if placeholder?has_content>placeholder: "${placeholder!}",</#if>
  		<#if height?has_content>height: "${height!}",</#if>
  		<#if minHeight?has_content>minHeight: "${minHeight!}",</#if>
  		<#if maxHeight?has_content>maxHeight: "${maxHeight!}",</#if>
  		callbacks: {
  			<#if onkeyup?has_content>
  			onKeyup: function(e) {
      			console.log('Key is released:', e.keyCode);
      			${onkeyup}
    		},
  			</#if>
  			<#if onkeydown?has_content>
  			onKeydown: function(e) {
      			console.log('Key is released:', e.keyCode);
      			${onkeydown}
    		},
  			</#if>
  		}
  		
  	});
  	<#if disabled>$('#${id}').summernote('disable');</#if>
});
</script>	
</#macro>
<#macro inputRichTextAreaCell id name="" value="" inputColSize="col-sm-8" placeholder="" height="100" dataError="" editorType="LITE_RICH_TEXT" disabled=false readonly=false required=false minHeight="" maxHeight="" onkeyup="" onkeydown="">
	<div class="${inputColSize}">
		<@inputRichTextArea id=id name=name value=value placeholder=placeholder height=height dataError=dataError editorType=editorType disabled=disabled readonly=readonly required=required minHeight=minHeight maxHeight=maxHeight onkeyup=onkeyup onkeydown=onkeydown/>
   	</div>
</#macro>
<#macro inputRichTextAreaRow id label name="" value="" labelColSize="col-sm-4" inputColSize="col-sm-8" placeholder="" height="100" dataError="" editorType="LITE_RICH_TEXT" style="" disabled=false readonly=false required=false minHeight="" maxHeight="" pattern="" onkeyup="" onkeydown="" isMakerChange=false>
	<div class="form-group row ${id}" style="${style!}">
	   <@labels label=label id=id required=required labelColSize=labelColSize isMakerChange=isMakerChange/>
	   <@inputRichTextAreaCell id=id name=name value=value inputColSize=inputColSize placeholder=placeholder height=height dataError=dataError editorType=editorType disabled=disabled readonly=readonly required=required minHeight=minHeight maxHeight=maxHeight onkeyup=onkeyup onkeydown=onkeydown/>
	</div>
</#macro>

<#macro inputRowFilePicker id label="" name="" value="" labelColSize="col-sm-4" inputColSize="col-sm-8" class="" placeholder="" dataError="" style="" tooltip="" disabled=false readonly=false required=false isMultiple=true pattern="" onkeyup="" onblur="">
<div class="form-group row ${id} file-content" id="${id}_row" style="${style!}">
    <#if label?has_content><@labels label=label id=id required=required labelColSize=labelColSize/> </#if>
    <div class=" ${inputColSize} left">
    	<div class="input-group">
       		<input type="file" class="form-control input-sm<#if class?has_content> ${class}</#if>" value="${value!}"  id="${id}" name="<#if !name?has_content>${id}<#else>${name}</#if>" placeholder="${placeholder}" <#if disabled>disabled</#if> <#if readonly>readonly</#if> <#if required>required</#if> <#if dataError?has_content>data-error="${dataError}"</#if> <#if tooltip?has_content>data-original-title="${tooltip}"</#if> <#if onkeyup?has_content>onkeyup="${onkeyup}"</#if> <#if onblur?has_content>onblur="${onblur}"</#if> <#if pattern?has_content>pattern="${pattern!}"</#if> />
       		<#if isMultiple>
       		<span class="input-group-addon"> 
				<a onclick="addAttachmentRepeateContent(this)" class="gd ml-1"><i class="fa fa-plus-circle"></i></a>
			</span>
			</#if>
       		<div class="help-block with-errors" id="${id}_error"></div>
       	</div>	
    </div>
</div>
</#macro>
<#macro displayRowFileContent id activityId="" commEventId="" label="" labelColSize="col-sm-4" inputColSize="col-sm-8">
<div class="form-group row ${id} file-content" id="${id}_row" style="${style!}">
	
	<#assign contentDetail = dispatcher.runSync("common.getFileContentData", {
                "requestContext" : Static["org.ofbiz.base.util.UtilMisc"].toMap("workEffortId", activityId, "communicationEventId", commEventId),
                "userLogin" : userLogin 
            })>

    <#if label?has_content><@labels label=label id=id labelColSize=labelColSize/> </#if>
    <div class=" ${inputColSize} left">
    	<#if contentDetail.resultMap?has_content>
    
    	<#if contentDetail.resultMap.fileContents?has_content>
    	<#list contentDetail.resultMap.fileContents as fileContent>
       	<a href="/partymgr/control/ViewSimpleContent?contentId=${fileContent.contentId}&externalLoginKey=${requestAttributes.externalLoginKey!}" class="buttontext"><u>${fileContent.contentName}</u></a>&nbsp;
       	</#list>
       	</#if>
       	
       	</#if>
    </div>
</div>
</#macro>
<#macro displayIframeRowFileContent id activityId='' commEventId='' label='' labelColSize='col-sm-4' inputColSize='col-sm-8'>
<div class='form-group row ${id} file-content' id='${id}_row' style='${style!}'>
	<#assign contentDetail = dispatcher.runSync('common.getFileContentData', {
	'requestContext': Static['org.ofbiz.base.util.UtilMisc'].toMap('workEffortId', activityId, 'communicationEventId', commEventId),
	'userLogin': userLogin 
	})><br>
	<div class='${inputColSize} left'>
		<#if contentDetail.resultMap?has_content>
		<#if contentDetail.resultMap.fileContents?has_content>
		<#list contentDetail.resultMap.fileContents as fileContent>
		<#if label?has_content>
			<label class='${labelColSize!} <#if id?has_content>id='${id}_label'</#if>' style ='font-weight: 700;'>${label} &nbsp;  &nbsp;  &nbsp;   </label>
		</#if>
			<a href='/partymgr/control/ViewSimpleContent?contentId=${fileContent.contentId}&externalLoginKey=${requestAttributes.externalLoginKey!}' class='buttontext'>
				<u>${fileContent.contentName}</u>
			</a>&nbsp;
		</#list>
		</#if>
		</#if>
	</div>
</div>
</#macro>

<#macro inputAutoComplete id pickerWindow="" label="" name="" value="" desValue="" labelColSize="col-sm-4" inputColSize="col-sm-8" hint="" class="" placeholder="" autocomplete="off" dataError="" style="" tooltip="" required=false isMakerChange=false glyphiconClass= "" readonly=false onkeydown=false isAutoCompleteEnable="N" isTriggerChangeEvent="Y" autoCompleteMinLength="3" autoCompleteUrl="" autoCompleteLabelFieldId="" autoCompleteValFieldId="" autoCompleteFormId=""
	onSelectfn="">
<#if isAutoCompleteEnable?has_content && isAutoCompleteEnable=="Y" && autoCompleteUrl?has_content && autoCompleteLabelFieldId?has_content && autoCompleteValFieldId?has_content>
<#local onkeydown = true>
</#if>
<div class="form-group <#if label?has_content>row</#if> ${id}" id="${id}_row" style="${style!}">
    <#if label?has_content><@labels label=label id=id required=required labelColSize=labelColSize isMakerChange=isMakerChange/></#if>
    <div class="${inputColSize!}">
      <div class="input-group">
       	<input type="text" class="form-control input-sm<#if class?has_content> ${class}</#if>" value="${desValue!}"  id="${id}_desc" tabindex="-1" placeholder="${placeholder}" autocomplete="${autocomplete!}" <#if required>required</#if> <#if dataError?has_content>data-error="${dataError}"</#if> <#if tooltip?has_content>data-original-title="${tooltip}"</#if> <#if readonly>readonly</#if> <#if !onkeydown>onkeydown="return false"<#else>onkeydown="return true"</#if>/>
       	<#-- <input type="text" class="form-control input-sm<#if class?has_content> ${class}</#if>" value="${desValue!}"  id="${id}_desc" tabindex="-1" placeholder="${placeholder}" autocomplete="${autocomplete!}" <#if required>required</#if> <#if dataError?has_content>data-error="${dataError}"</#if> <#if tooltip?has_content>data-original-title="${tooltip}"</#if> <#if readonly>readonly</#if> /> -->
         <#if pickerWindow?has_content>
	         <span class="input-group-addon picker-window-erase" data-pickerWindow="${pickerWindow}" data-pickerInputId="${id}" tabindex="0">
				<i class="fa fa-eraser" aria-hidden="true"></i>                  
	         </span>
	         <span class="input-group-addon picker-window" data-pickerWindow="${pickerWindow}" data-pickerInputId="${id}" tabindex="0">
				<i class="${glyphiconClass!}" aria-hidden="true"></i>                  
	         </span>
         </#if>
      </div>
       <div class="help-block with-errors" id="${id}_error"></div>
    </div>
    <input type="hidden" id="${id}_val" name="<#if !name?has_content>${id}<#else>${name}</#if>" value="${value!}"/> 
    <input type="hidden" id="${id}_alter" name="${id!}_alter" value=""/> 
</div>
<script>
$(document).ready(function() {
	<#if isAutoCompleteEnable?has_content && isAutoCompleteEnable=="Y" && autoCompleteUrl?has_content && autoCompleteLabelFieldId?has_content && autoCompleteValFieldId?has_content>
  	$("#${id!}_desc").autocomplete({
	    source: function (request, response) {
			let inputData = new Map();
			inputData.set('searchText', $('#${id}_desc').val());
			inputData.set('externalLoginKey', '${requestAttributes.externalLoginKey!}');
			<#if autoCompleteFormId?has_content>
		    $.each($("#${autoCompleteFormId}").serializeArray(), function( i, field ) {
		      	inputData.set(field.name, field.value);
		    });
			</#if>
			inputData = Array.from(inputData, ([name, value]) => ({ name, value }));
	        $.ajax({
	            url: '${autoCompleteUrl!}',
	            //data: {'searchText': $('#${id}_desc').val(), 'externalLoginKey': '${requestAttributes.externalLoginKey!}'},
	            data: JSON.parse(JSON.stringify(inputData)),
	            type: "POST",
	            success: function (result) {
	            	var dataList = Array.isArray(result) ? result : result.data;
	            	if(!dataList){
	            		$('#${id}_val').val($('#${id}_desc').val());
	            		response('');
	            	} else {
	                response($.map(dataList, function (item) {
						var labelItems = "";
						var alternative = "";
	                    <#if autoCompleteLabelFieldId?contains(",")>
					  		<#local filedIdList = autoCompleteLabelFieldId?split(',') />
					  		<#local i = 1 />
					  		<#list filedIdList as fieldId>
					    		<#assign labelField = fieldId?trim />
					    		<#if i == 1>
					    			alternative = item['${labelField!}'];
					    		</#if>
					    		<#if i == filedIdList?size!>
					    			labelItems = labelItems+ item['${labelField!}'];
					    		<#else>
					    			labelItems = labelItems+ item['${labelField!}'] + ' - ';
					    		</#if>
					    		<#local i = i+1/>
					        </#list>
					    <#else>
					    	labelItems=item['${autoCompleteLabelFieldId!}'];
					  	</#if>
	                    return {
	                        label: labelItems,
	                        val: item['${autoCompleteValFieldId!}'],
	                        alter: alternative
	                        
	                    }			              
	                }));
	                $(".ui-helper-hidden-accessible").hide();
	              }
	            }
	        });
	    },
	    select: function (e, i) {
	        console.log('label: '+i.item.label+', val: '+i.item.val);
	        $('#${id}_val').val(i.item.val);
	        $('#${id}_alter').val(i.item.alter);
	        <#if isTriggerChangeEvent?has_content && isTriggerChangeEvent=="Y">
	         setTimeout(function(){
	        	$("#${id}_desc").blur();
	        }, 1);
	        </#if>
	        <#if onSelectfn?has_content>
	        	${onSelectfn!}
	        </#if>
	    },
	    minLength: ${autoCompleteMinLength!},
	}).focus(function(){            
    	$(this).data("uiAutocomplete").search($(this).val());
    });
	</#if>
});
</script>	
</#macro>

<#macro navTabOld instanceId componentId="" navTabStyle="" navItemStyle="" activeTabId="" hideTabIds="">
	<#local currentComponent = webappName!'' />
	<#if componentId?has_content>
		<#local currentComponent = webappName!'' />
	</#if>
	<#assign navTab = dispatcher.runSync("ap.getNavTabConfiguration", Static["org.ofbiz.base.util.UtilMisc"].toMap("componentId", currentComponent!, "tabConfigId", instanceId!, "hideTabIds", hideTabIds!, "userLogin", userLogin!, "session", session)) />
	<#if navTab?has_content>
		<#local tabConfiguration = navTab.tabConfiguration!>
		<#local navTabList =  tabConfiguration.navTabList! />
		
		<#if navTabList?has_content>
			<ul class="nav nav-tabs mt-3" id="nav-tab-focus" style="${navTabStyle!}">
			<#local count1 = 0 />
			<#list navTabList as navTab>
				<#if !activeTabId?has_content && count1 == 0>
					<#local activeTabId = navTab.tabId!'' />
				</#if>
				<li class="nav-item" id="${navTab?if_exists.tabId!}_nav_tab">
					<a data-toggle="tab" class="nav-link <#if (activeTabId! == navTab.tabId!)> active</#if>" href="#${navTab.tabId!}" style="${navItemStyle!}">
						 ${navTab.tabName!}
					</a>
				</li>
				<#local count1 = count1+1 />
			</#list>
			</ul>
			<div class="tab-content" id="tab-content">
				<#local count = 0 />
				<#list navTabList as navTab1>
					<#if !activeTabId?has_content && count == 0>
						<#local activeTabId = navTab1.tabId!'' />
					</#if>
					<#local tabContent = navTab1["tabContent"]!'' />
					<div id="${navTab1.tabId!}" class="tab-pane fade <#if (activeTabId! == navTab1.tabId!)> active show</#if>">
                  		<#if tabContent?has_content>
                  			${screens.render(tabContent!)}
                  		<#else>
	                  		<div class="row justify-content-md-center">
								<div class="pt-2">
									<div class="alert alert-danger show">
										<span> Please configure tab content to display the respective data!</span>
									</div>	
								</div>
					   		</div>
	                  	</#if>
	                </div>
                  	<#local count = count+1 />
				</#list>
            </div>
		</#if>
		
	</#if>
</#macro>

<#macro navTab instanceId componentId="" navTabStyle="" navItemStyle="" activeTabId="" hideTabIds="" requestUri ="" mainTab="">
	<#local tabsToSkip = Static["org.fio.homeapps.util.DataUtil"].getGlobalValue(delegator, "TABS_TO_SKIP")?if_exists>
	<#local currentComponent = webappName!'' />
	<#if componentId?has_content>
		<#local currentComponent = webappName!'' />
	</#if>
	<#if !requestUri?has_content>
		<#local requestUri = request.getRequestURI() />
	</#if>
	<#assign navTab = dispatcher.runSync("ap.getNavTabConfiguration", Static["org.ofbiz.base.util.UtilMisc"].toMap("componentId", currentComponent!, "tabConfigId", instanceId!, "hideTabIds", hideTabIds!, "userLogin", userLogin!, "session", session)) />
	<#if navTab?has_content>
		<#local tabConfiguration = navTab.tabConfiguration!>
		<#local navTabList =  tabConfiguration.navTabList! />
		<#local tabContentMap = tabConfiguration.tabContentMap!>
		
		<#local selectedTab = "" />
		<#if navTabList?has_content>
			<ul class="nav nav-tabs mt-3" id="nav-tab-${instanceId!}" style="${navTabStyle!}">
			<#local count1 = 0 />
			<#list navTabList as navTab>
				<#if !activeTabId?has_content && count1 == 0>
					<#local activeTabId = navTab.tabId!'' />
					<#local selectedTab = navTab.tabId!'' />
				</#if>
				<li class="nav-item" id="${navTab?if_exists.tabId!}_nav_tab">
					<a data-toggle="tab" class="nav-link <#if (activeTabId! == navTab.tabId!)> active</#if>" href="#${navTab.tabId!}" style="${navItemStyle!}" <#-- onclick="loadTabContent('${instanceId!}','${navTab.tabId!}') "-->   >
						 ${navTab.tabName!}
					</a>
				</li>
				<#local count1 = count1+1 />		
			</#list>
			</ul>
			<#assign selTab = "" />
			<div class="tab-content" id="tab-content">
				<#local count = 0 />
				<#list navTabList as navTab1>
					
					<#if !activeTabId?has_content && count == 0>
						<#local activeTabId = navTab1.tabId!'' />
						<#local selectedTabContent = navTab1["tabContent"]!'' />
					</#if>
					<#local tabContent = "" />
					<#if (activeTabId! == navTab1.tabId!)>
						<#local tabContent = navTab1["tabContent"]!'' />
					</#if>
					
					
					
					<div id="${navTab1.tabId!}" class="tab-pane fade <#if (activeTabId! == navTab1.tabId!)> active show</#if>">
						
						<div id="loading-img_${navTab1.tabId!}" class="loading-img" style="text-align: center;display:none;"><img src="/bootstrap/images/loading.gif" width="100" height="100" class="img-responsive" alt=""></div>
						<span id="tab_content_${navTab1.tabId!}">
							<#-- 
							<#if tabContent?has_content && activeTabId! == navTab1.tabId!>
                  				${screens.render(tabContent!)}
	                  		</#if>
	                  		-->
						</span>
						
						<#-- 
						<#local tabContent = navTab1["tabContent"]!'' />
					
						<#if tabContent?has_content>
                  			${screens.render(tabContent!)}
                  		<#else>
	                  		<div class="row justify-content-md-center">
								<div class="pt-2">
									<div class="alert alert-danger show">
										<span> Please configure tab content to display the respective data!</span>
									</div>	
								</div>
					   		</div>
	                  	</#if>
	                  	 -->
	                </div>
	                
	                
                  	<#local count = count+1 />
				</#list>
            </div>
            
            <#local uniqueId = Static["org.fio.homeapps.util.UtilGenerator"].getNextSeqId(5) />
            ${session.setAttribute("mainScreenRenderer-${uniqueId}", screens!)}
            <form id="${instanceId!}-tab-form" name="${instanceId!}-tab-form" method="post" >
            	<@inputHidden id="componentId" value="${currentComponent!}" />
            	<@inputHidden id="tabConfigId" value="${instanceId!}" />
            	<@inputHidden id="tabId" value="${selTab!}" />
            	<@inputHidden id="requestUri" value="" />
            	<@inputHidden id="uniqueId" value="${uniqueId!}" />
            	<@inputHidden id="externalLoginKey" value="${requestAttributes.externalLoginKey!}" />
            	<@inputHidden id="tabsToSkip" value="${tabsToSkip!}" />
            	<#-- 
            	<#local inputCon = Static["org.fio.admin.portal.util.DataUtil"].convertToJsonStr(inputContext!) />
            	<@inputHidden id="inputContext" value="${inputCon!}" />
            	-->
            	<#if contextData?has_content>
		        	<#local contextDa = Static["org.fio.admin.portal.util.DataUtil"].convertToJsonStr(contextData!) />
		        	<@inputHidden id="contextData" value="${contextDa!}" />
            	</#if>
            </form>
            
            <form id="${instanceId!}-tab-form-data" action="" name="${instanceId!}-tab-form-data" method="post" >
            	<@inputHidden id="tabIdToLoad" value="" />
            </form>
            <script>
            	$(function(){
            		<#-- 
            		<#if mainTab?has_content>
	            		if("${mainTab}" === "Y") {
	            			$("#nav-tab-${instanceId!} li a[data-toggle='tab']").on("click",function(){
	            				var tabId = $(this).attr("href");
					   			tabId = tabId.substring(1);
					   			var hashVal = window.location.hash.substr(1);
					   			if(tabId != null && hashVal != null && tabId != hashVal){
					   				let location = window.location.href;
					   				var formAction = location.substr(0,location.indexOf('#'));
					   					formAction = formAction + "#"+tabId;
					   				$("#${instanceId!}-tab-form-data #tabIdToLoad").val(tabId);
					   				$("#${instanceId!}-tab-form-data").attr('action', formAction);
					   				$("#${instanceId!}-tab-form-data").submit();
					   			}
	            			});
	            		}
            		</#if>
            		
            		 -->
            		 
            		<#--  
            		<#if selTab?has_content>
            			$('#nav-tab-${instanceId!} li a[href="#${selTab!}"]').trigger('click');
            		</#if>
            		 -->
            		let queryParams_${instanceId!} = new URLSearchParams(window.location.search);
					if(queryParams_${instanceId!}){
						queryParams_${instanceId!}.forEach((value, key) => {
						  $('<input />').attr('type', 'hidden')
					          .attr('name', key)
					          .attr('value', value)
					          .appendTo('#${instanceId!}-tab-form');
						});	
					}
            	});
            	<#-- 
				let tabs_${instanceId!} = new Set();
				<#if selTab?has_content >
					tabs_${instanceId!}.add('${selTab!}');
				</#if>
				 -->
				var locAddrBar = window.location.pathname;
				$("#${instanceId!}-tab-form #requestUri").val(locAddrBar);
				
				<#if activeTabId?has_content>
		    		loadTabContent('${instanceId!}','${activeTabId!}');
			    </#if>
				$("#nav-tab-${instanceId!} li a[data-toggle='tab']").on('click',function(event){
				    var tabId = $(this).attr("href");
				    tabId = tabId.substring(1);
				    loadTabContent('${instanceId!}',tabId);
				   
				    /*
				    if(tabs_${instanceId!}.has(tabId)){}
				    else{
				    	tabs_${instanceId!}.add(tabId);
				    	loadTabContent('${instanceId!}',tabId);
				    }
				    */
				}); 
			</script>
		</#if>
		
	</#if>
</#macro>


<#macro customDatePicker id name="" label="" yearAdd="" labelColSize="col-sm-4" inputColSize="col-sm-8" placeholder="" value="" style="" default=true required=false disabled=false isMakerChange=false>
<style>
.select-bor {
border-radius: 5px;
}
.dat-fields {
padding: 0px 3px;
}
</style>

<#local val = value?split("-")>
<#local selDate = val[2]!'' />
<#local selMonth = val[1]!'' />
<#local selYear = val[0]!''/>

<div class="form-group row ${id!} file-content" id="${id!}_row" style="${style!}">
    <#if label?has_content><@labels label=label id=id required=required labelColSize=labelColSize/> </#if>
    <div class=" ${inputColSize} left">
    	<div class="">
       		<div class="row">
       			<div class="col-sm-4 left">
				<select name="month_${id!}" id="month_${id!}" class="ui dropdown search form-control fluid show-tick month_${id!}">
					<option value=""></option>
					<option value="01" <#if selMonth! == "01" >selected</#if> >JAN</option>
					<option value="02" <#if selMonth! == "02" >selected</#if> >FEB</option>
					<option value="03" <#if selMonth! == "03" >selected</#if> >MAR</option>
					<option value="04" <#if selMonth! == "04" >selected</#if> >APR</option>
					<option value="05" <#if selMonth! == "05" >selected</#if> >MAY</option>
					<option value="06" <#if selMonth! == "06" >selected</#if> >JUN</option>
					<option value="07" <#if selMonth! == "07" >selected</#if> >JUL</option>
					<option value="08" <#if selMonth! == "08" >selected</#if> >AUG</option>
					<option value="09" <#if selMonth! == "09" >selected</#if> >SEP</option>
					<option value="10" <#if selMonth! == "10" >selected</#if> >OCT</option>
					<option value="11" <#if selMonth! == "11" >selected</#if> >NOV</option>
					<option value="12" <#if selMonth! == "12" >selected</#if> >DEC</option>
				</select>
				</div>
				<div class="col-sm-4 left">
				<#assign datecnt=31 />
				<select name="date_${id!}" id="date_${id!}" class="ui dropdown search form-control fluid show-tick date_${id!}">
					<option value=""></option>
					<#list 	1..datecnt as date>
						<#local days = date?if_exists?string("00") />
				    	<option value="${days!}" <#if selDate! ==days! >selected</#if> >${date?if_exists}</option>
				    </#list>
				</select>
				</div>
				<div class="col-sm-4 left">
				<select name="year_${id!}" id="year_${id!}" class="ui dropdown search form-control fluid show-tick year_${id!}">
					<option value=""></option>
					<option value="9999" <#if selYear! == "9999" >selected</#if> >9999</option>
					<#assign x= .now?string('yyyy')?number />
					<#if yearAdd?has_content>
						<#assign years = yearAdd?number />
						<#assign x = x+years />
					</#if>					
					<#list 1900..x as yr>
						<option value="${yr!}" <#if selYear! == yr?if_exists?string >selected</#if> >${yr!}</option>
					</#list>
				</select>
				</div>
				<input type="hidden" id="${id!}_custom" name="<#if !name?has_content>${id!}<#else>${name}</#if>" value="${value!}"/> 
			</div>
			
			<#if disabled>
				<input type="hidden" id="${id!}_custom" name="<#if !name?has_content>${id!}<#else>${name}</#if>" value="${value!}"/> 
			</#if>
       	</div>
       	<div class="help-block with-errors" id="${id!}_error"></div>	
    </div>
</div>

<script>
$(function(){
	$("#year_${id!}").dropdown();
	$("#month_${id!}").dropdown();
	$("#date_${id!}").dropdown();
	
	$("#year_${id!}").dropdown('refresh');
	$("#month_${id!}").dropdown('refresh');
	$("#date_${id!}").dropdown('refresh');
});
$("#year_${id!}").change(function(){
	validateDataSelect_${id!}();
});
$("#month_${id!}").change(function(){
	validateDataSelect_${id!}();
});
$("#date_${id!}").change(function(){
	validateDataSelect_${id!}();
});
function validateDataSelect_${id!}(){
	$("#${id!}_error").html("");
	var year = $("#year_${id!}").val();
	var month = $("#month_${id!}").val();
	var date = $("#date_${id!}").val();
	if(date =="" || month =="" || year == ""){
		$("#${id!}_error").html("Month/Date/Year should not be empty");
	} else{
		let selectedDate = month+"/"+date+"/"+year;
		$("#${id!}_custom").val(selectedDate);	
	}
}
</script>

</#macro>


<#macro fioGrid instanceId jsLoc id="" headerLabel="" headerId="" headerBarClass="grid-header" headerClass="float-left" gridTheme="ag-theme-balham" 
		btnSectionId="" headerExtra="" headerExtraLeft="" headerExtraRight=""
		savePrefBtn=true savePrefBtnClass="btn-xs btn-primary-white" savePrefBtnId="save-pref-btn" savePrefBtnLabel="${uiLabelMap.SaveGridPreference!}"
		clearFilterBtn=true clearFilterBtnClass="btn-xs btn-primary-white" clearFilterBtnId="clear-filter-btn" clearFilterBtnLabel="${uiLabelMap.ClearGridFilter!}"
		subFltrClearBtn=true subFltrClearBtnClass="btn-xs btn-primary-white" subFltrClearId="sub-filter-clear-btn" subFltrClearBtnLabel = "Clear Sub Filter"
		exportBtn=false exportBtnClass="btn-xs btn-primary-white" exportBtnId="export-btn" exportBtnLabel="${uiLabelMap.ExportGridData!}"
		helpBtn=false helpBtnClass="btn-xs btn-primary-white" helpBtnId="help-btn" helpBtnLabel="${uiLabelMap.Help!}" helpUrl="#"
		serversidepaginate=false statusBar=false
		>
	<#-- 
	<div id="${titleId!}_header" class="popup-agtitle">
		<#if title?has_content>
			<@headerH2 title= title! id=titleId! class=titleClass!/>
		</#if>
	</div> -->
	<div class="row">
	<div class="col-lg-12 col-md-12 col-sm-12">
	<@fioGridHeader header=headerLabel! headerBarClass=headerBarClass! headerId=headerId! headerClass=headerClass! btnSectionId=btnSectionId! instanceId=instanceId!
		extra=headerExtra! extraLeft=headerExtraLeft! extraRight=headerExtraRight!
		savePrefBtn=savePrefBtn! savePrefBtnClass=savePrefBtnClass! savePrefBtnId=savePrefBtnId! savePrefBtnLabel=savePrefBtnLabel!
		clearFilterBtn=clearFilterBtn! clearFilterBtnClass=clearFilterBtnClass! clearFilterBtnId=clearFilterBtnId! clearFilterBtnLabel=clearFilterBtnLabel!
		exportBtn=exportBtn! exportBtnClass=exportBtnClass! exportBtnId=exportBtnId! exportBtnLabel=exportBtnLabel!
		helpBtn=helpBtn! helpBtnClass=helpBtnClass! helpBtnId=helpBtnId! helpBtnLabel=helpBtnLabel! helpUrl=helpUrl!
		serversidepaginate=serversidepaginate!
		subFltrClearBtn=subFltrClearBtn! subFltrClearBtnClass=subFltrClearBtnClass! subFltrClearId=subFltrClearId! subFltrClearBtnLabel=subFltrClearBtnLabel! 
		/>
	</div>
	</div>
	
	<div class="row">
	<div class="col-lg-12 col-md-12 col-sm-12">
	<@inputHidden id="${instanceId!}_instanceId" value="${instanceId!}" />
	
	<#--<div id="grid_loading_${instanceId!}" style="width: 100%;margin-top: 20px; text-align: center;border: 1px solid #dcd5d5;">
		<span style="display: inline-block;"><img src="/bootstrap/images/loading.gif" style="width: 100px; height: 100px;"></img></span> -->
		<#-- <img src="/bootstrap/images/ajax_loader_100px.gif"></img> -->
	<#--</div>-->
	<div id="${instanceId!}" style="width: 100%;" class="${gridTheme!}">
	</div>
	
	<div class="">
		<#assign isDisplayGridName = Static["org.fio.homeapps.util.DataUtil"].getGlobalValue(delegator, "DISPLAY_GRID_NAME", "Y") />
	    <#if "Y" == isDisplayGridName>
		    <#assign gridInstance = (Static["org.ofbiz.entity.util.EntityQuery"].use(delegator).from("GridUserPreferences").where("instanceId", instanceId!, "userId", "admin", "role", "ADMIN").queryOne())?if_exists />
		    <#assign hasPermission = Static["org.fio.homeapps.util.DataUtil"].hasPermission(request, "GRID_PREF_VIEW")?if_exists />
		    <#if gridInstance?has_content>
			    <#assign gridName = "" />
			    <#if hasPermission>
			    	<#assign gridName = "<a target='_blank' href='/ofbiz-ag-grid/control/viewAgGrid?instanceId=${instanceId!}&userId=admin&role=ADMIN&externalLoginKey=${requestAttributes.externalLoginKey!}'>${gridInstance.getString('name')!}</a>" />
			    <#else>
			    	<#assign gridName = gridInstance.getString("name")! />
			    </#if>
			    <#if gridName?has_content>
			   		<span class="pl-0" style="font-size: 16px;font-weight: bold;font-family: 'frutigernextltmedium';">Grid Name : ${gridName!}</span>
			   	</#if>
		    </#if>
	    </#if>
		<#if statusBar && serversidepaginate>
			<span class="pl-3" id="totalRecord" title="${totalRecordLabel!}"><i class="fa fa-clipboard" aria-hidden="true"></i> Total Records: <b id="totalRecordCount">0</b></span>
			<span class="pl-3" id="timeElapsed" title="${timeTakenLabel!}"><i class="fa fa-clock-o" aria-hidden="true"></i> Time Elapsed: <b id="timeTaken">0</b> seconds</span>
			<span class="pl-3" id="gridChunkSize" title="${chunkSizeLabel!}"><i class="fa fa-cube" aria-hidden="true"></i> Chunk Size: <b id="chunkSize">0</b></span>
			<span class="pl-3" id="chunks" title="${chunksLabel!}"><i class="fa fa-cubes" aria-hidden="true"></i> Chunks: <b id="chunkCount">0</b></span>
		</#if>
		<span id="fio_grid_dynamic_status_bar" class="fio_grid_dynamic_status_bar">
		</span>
	</div>
	<#if !context.isInitiateNewFioAgGrid?exists>
		<link rel="stylesheet" href="/ofbiz-ag-grid-resource/css/ag-grid/ag-grid.min.css">
		<link rel="stylesheet" href="/ofbiz-ag-grid-resource/css/ag-grid/ag-theme-balham.min.css">
		<script src="/ofbiz-ag-grid-resource/js/v31.0.0/ag-grid-community.min.js"></script>
		${setContextField("isInitiateNewFioAgGrid", "Y")}
	</#if>
	<script src="${jsLoc!}"></script>
	</div>
	</div>
</#macro>

<#macro fioGridHeader header headerId="" headerBarClass="" btnSectionId="" extra="" extraLeft="" extraRight="" instanceId="" headerClass=""
	savePrefBtn=true savePrefBtnClass="btn-xs btn-primary-white" savePrefBtnId="save-pref-btn" savePrefBtnLabel="${uiLabelMap.SaveGridPreference!}"
	clearFilterBtn=true clearFilterBtnClass="btn-xs btn-primary-white" clearFilterBtnId="clear-filter-btn" clearFilterBtnLabel="${uiLabelMap.ClearGridFilter!}"
	exportBtn=false exportBtnClass="btn-xs btn-primary-white" exportBtnId="export-btn" exportBtnLabel="${uiLabelMap.ExportGridData!}"
	helpBtn=false helpBtnClass="btn-xs btn-primary-white" helpBtnId="help-btn" helpBtnLabel="${uiLabelMap.Help!}" helpUrl="#"
	serversidepaginate=false
	subFltrClearBtn = true subFltrClearBtnClass="btn-xs btn-primary-white" subFltrClearId="sub-filter-clear-btn" subFltrClearBtnLabel = "Clear Sub Filter"
	>
	
	<div class="${headerBarClass!'grid-header-no-bar'}" id="<#if headerId?has_content>root_${headerId!}</#if>">
		
		<#if header?has_content>
			<@span title=header! id=headerId! class="${headerClass!''} fioGridHeaderFontStyle"/>
		</#if>		
		<#if headerBarClass?has_content && headerBarClass == 'grid-header'>	
		<#else>
			<#local savePrefBtnClass="btn-xs btn-primary" />
			<#local clearFilterBtnClass="btn-xs btn-primary" />
			<#local exportBtnClass="btn-xs btn-primary" />
			<#local helpBtnClass="btn-xs btn-primary" />
			<#local subFltrClearBtnClass="btn-xs btn-primary" />
			
		</#if>
	    ${extraLeft!}
	    <span class="float-right noselect" id="${btnSectionId!}">
	    	${extra!}
	    	<#-- <#if colmgmtBtn><a href="columnManagement?gridInstanceId=${instanceid!}" class="btn ${colmgmtBtnClass!}" id="${colmgmtBtnId!}" title="${colmgmtBtnLabel!}"><i class="fa fa-plus" aria-hidden="true"></i> ${colmgmtBtnLabel!}</a> </#if> -->
			<#if subFltrClearBtn><span class="btn ${subFltrClearBtnClass!}" id="${subFltrClearId!}" title="${subFltrClearBtnLabel!}"><i class="fa fa-refresh" aria-hidden="true"></i> ${subFltrClearBtnLabel!}</span> </#if>
			<#if savePrefBtn><span class="btn ${savePrefBtnClass!}" id="${savePrefBtnId!}" title="${savePrefBtnLabel!}"><i class="fa fa-save" aria-hidden="true"></i> ${savePrefBtnLabel!}</span> </#if>
			<#if clearFilterBtn><span class="btn ${clearFilterBtnClass!}" id="${clearFilterBtnId}" data-toggle="confirmation" title="Do you want to remove user preference?" title="${clearFilterBtnLabel!}"><i class="fa fa-eraser" aria-hidden="true"></i> ${clearFilterBtnLabel!}</span> </#if>
			<#if exportBtn><span class="btn ${exportBtnClass}" id="${exportBtnId}" title="${exportBtnLabel!}"><i class="fa fa-file-excel-o" aria-hidden="true"></i> ${exportBtnLabel!}</span> </#if>
			<#if helpBtn><span class="btn ${helpBtnClass}" id="${helpBtnId}" title="${helpBtnLabel!}"><i class="fa fa-question-circle" aria-hidden="true"></i> <a target="_blank" class="btn-primary" href="${helpUrl!'#'}">${helpBtnLabel!}</a></span> </#if>
			${extraRight!}
			
			<#if serversidepaginate>
	    		<#assign gridInstanceAttribute = (Static["org.ofbiz.entity.util.EntityQuery"].use(delegator).select("attrValue").from("GridInstanceAttribute").where("instanceId",instanceId!,"attrName","fio.grid.fetch.limit").queryOne())?if_exists />
	    		<#if gridInstanceAttribute?has_content>
	    			<#local fetchLimit = gridInstanceAttribute?if_exists.attrValue! />
	    		<#else>
	    			<#local fetchLimit = 1000 />
	    		</#if>
	    		<#-- 
	    		<span class="btn btn-xs btn-primary" id="fetch-previous" title="Fetch Previous ${fetchLimit!}"><i class="fa fa-arrow-circle-left" aria-hidden="true"></i></span>
	    		<span class="btn btn-xs btn-primary" id="fetch-next" title="Fetch Next ${fetchLimit!}"><i class="fa fa-arrow-circle-right" aria-hidden="true"></i></span>
	    		-->
	    		<span class="btn btn-xs btn-primary" id="fetch-first" title="First"><i class="fa fa-angle-double-left" aria-hidden="true"></i></span>
	    		<span class="btn btn-xs btn-primary" id="fetch-previous" title="Fetch Previous ${fetchLimit!}"><i class="fa fa-angle-left" aria-hidden="true"></i></span>
	    		<span class="btn-xs"><input type="text" class="form-control goto-btn" value="1" id="goto-page" name="goto-page" autocomplete="off" maxlength="3"></span>
	    		<span class="btn btn-xs btn-primary" id="fetch-next" title="Fetch Next ${fetchLimit!}"><i class="fa fa-angle-right " aria-hidden="true"></i></span>
	    		<span class="btn btn-xs btn-primary" id="fetch-last" title="Last"><i class="fa fa-angle-double-right" aria-hidden="true"></i></span>
	    		<style>
	    			.goto-btn {
	    			    display: inline-block;
					    width: 35px;
					    height: 25px;
					    text-align: center;
					    vertical-align: bottom;
					}
	    		</style>
	    		
	    	<form id="limitForm_${instanceId!}" name="limitForm_${instanceId!}" action="#" method="">
    			<@inputHidden id="TOTAL_CHUNK" value="" />
    			<@inputHidden id="VIEW_INDEX" value="0" />
    			<@inputHidden id="VIEW_SIZE" value="${fetchLimit!}" />
    		</form>	
	    	</#if>
	    	<#-- 
	    	<#assign userGridInstance = (Static["org.ofbiz.entity.util.EntityQuery"].use(delegator).from("GridUserPreferences").where("instanceId",instanceid!,"role","USER","userId",userid).queryOne())?if_exists />
	    	<#if columnConfig && userGridInstance?has_content>
	    		<a class="settings" target="_blank" href="/ofbiz-ag-grid/control/userColumnManagement?gridInstanceId=${instanceid!}&gridUserId=${userid!}&externalLoginKey=${requestAttributes.externalLoginKey!}" id="columnManagement" name="columnManagement"><i class="fa fa-cog" aria-hidden="true"></i></a>
	    	</#if> -->
	    </span>
	    <div class="clearfix"></div>
	</div>
<script>
initiateDefaultEvents();
</script>
</#macro>
