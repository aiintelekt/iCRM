<#macro includeContent id>
<#if context??>
<#assign empty>${context.put("contentId", id)!}</#assign>
${screens.render("component://ecommerce/widget/CommonScreens.xml#include-content")}
</#if>
</#macro>
<#function getBaseUrl https=false>
<#return Static["org.ofbiz.webapp.control.RequestHandler"].getDefaultServerRootUrl(request, https)>
</#function>
<#function createUrl url>
<#return response.encodeUrl(url)>
</#function>
<#macro fullUrlPath url>
<#compress>
<#assign baseUrl=getBaseUrl()>
<#if url?starts_with("http")>
${url}
<#elseif StringUtil.wrapString(url)?starts_with("/")>
${baseUrl}${url}
<#else>
${baseUrl!}/${url}
</#if>
</#compress>
</#macro>
<#assign null="NUL" />
<#function is_null variable>
<#return true />
<#if variable?is_string & variable == null>
<#return true />
<#else>
<#return false />
</#if>
</#function>
<#macro includeAnalytics>
<script type="text/javascript">
   (function(i,s,o,g,r,a,m){i['GoogleAnalyticsObject']=r;i[r]=i[r]||function(){
   (i[r].q=i[r].q||[]).push(arguments)},i[r].l=1*new Date();a=s.createElement(o),
   m=s.getElementsByTagName(o)[0];a.async=1;a.src=g;m.parentNode.insertBefore(a,m)
   })(window,document,'script','//www.google-analytics.com/analytics.js','ga');
   
   ga('create', 'UA-48096108-1', 'toothtotail.com');
   ga('send', 'pageview');
   
</script>
</#macro>
<#macro accordionLink url text iconClass="">
<a href="${url}">
<#if iconClass?has_content>
<i class="${iconClass}"></i>	
</#if>
${text}
</a>
</#macro>
<#macro accordionItemLink url text iconClass="">
<#assign canonical_url = parameters._CONTROL_PATH_ + "/" + parameters._CURRENT_VIEW_>
<#assign accordion_url = parameters._CONTROL_PATH_ + "/" + url>
<li <#if canonical_url == accordion_url>class="active"</#if> >
<@accordionLink url="${url}" text="${text}" iconClass="${iconClass}"/>
</li>
</#macro>
<#macro generalInput label id name="" value="" placeholder="" iconClass="" inputType="text" dataError="" tooltip="" iconLeft=true disabled=false readonly=false required=false  min="" max="" maxlength="">
<div class="form-group row <#if required>has-error</#if>">
   <label class="col-sm-4 col-form-label" for="${id}">${label}</label>
   <div class="col-sm-7">
      <div class="input-icon <#if !iconLeft>right</#if>">
         <input type="${inputType}" class="form-control input-sm <#if tooltip?has_content>tooltips</#if> " autocomplete="off"  value="${value!}" id="${id}" name="<#if !name?has_content>${id}<#else>${name}</#if>" placeholder="${placeholder}" <#if disabled>disabled</#if> <#if readonly>readonly</#if> <#if required>required</#if> <#if dataError?has_content>data-error="${dataError}"</#if> <#if tooltip?has_content>data-original-title="${tooltip}"</#if> <#if inputType=="number" && min?has_content> min="${min}" </#if> maxlength="${maxlength!}" >
         <#if disabled>
         <input type="hidden" id="${id}" name="<#if !name?has_content>${id}<#else>${name}</#if>" value="${value!}"/>	
         </#if>
         <div class="help-block with-errors" id="${id}_error"></div>
         <#if iconClass?exists>
         <i class="${iconClass}"></i>
         </#if>
      </div>
   </div>
</div>
</#macro>
<#macro generalInputFloating label id name="" value="" hint="" inputSize="md" iconClass="" iconLeft=true disabled=false readonly=false required=false>
<div class="form-group form-md-floating-label">
   <div class="input-icon <#if !iconLeft>right</#if>">
      <input type="text" class="form-control input-${inputSize}" value="${value!}" id="${id}" name="<#if !name?has_content>${id}<#else>${name}</#if>" <#if disabled>disabled</#if> <#if readonly>readonly</#if> <#if required>required</#if> >
      <#if disabled>
      <input type="hidden" id="${id}" name="<#if !name?has_content>${id}<#else>${name}</#if>" value="${value!}"/>	
      </#if>
      <label for="${id}">${label}</label>
      <#if hint?has_content>
      <span class="help-block">${hint}</span>
      </#if>	
      <#if iconClass?exists>
      <i class="${iconClass}"></i>
      </#if>
   </div>
</div>
</#macro>
<#macro inputWithHint label id hint name="" value="" placeholder="" inputSize="md" iconClass="" iconLeft=true disabled=false readonly=false required=false>
<div class="form-group row">
   <label class="col-md-4 col-form-label" for="${id}">${label}<#if required><span class="text-danger">&#42;</span></#if></label>
   <div class="col-md-8">
      <div class="input-icon <#if !iconLeft>right</#if>">
         <input type="text" class="form-control input-${inputSize}" value="${value!}" id="${id}" name="<#if !name?has_content>${id}<#else>${name}</#if>" placeholder="${placeholder}" <#if disabled>disabled</#if> <#if readonly>readonly</#if> <#if required>required</#if> >
         <#if disabled>
         <input type="hidden" id="${id}" name="<#if !name?has_content>${id}<#else>${name}</#if>" value="${value!}"/>	
         </#if>
         <div class="form-control-focus">
         </div>
         <span class="help-block with-errors">${hint}</span>
      </div>
   </div>
</div>
</#macro>
<#macro successInput label id hint="" name="" value="" placeholder="" inputSize="md" iconClass="" iconLeft=true disabled=false readonly=false required=false>
<div class="form-group has-success row">
   <label class="col-md-4 col-form-label" for="${id}">${label}<#if required><span class="text-danger">&#42;</span></#if></label>
   <div class="col-md-8">
      <div class="input-icon <#if !iconLeft>right</#if>">
         <input type="text" class="form-control input-${inputSize}"" value="${value!}" id="${id}" name="<#if !name?has_content>${id}<#else>${name}</#if>" placeholder="${placeholder}" <#if disabled>disabled</#if> <#if readonly>readonly</#if> <#if required>required</#if> >
         <#if disabled>
         <input type="hidden" id="${id}" name="<#if !name?has_content>${id}<#else>${name}</#if>" value="${value!}"/>	
         </#if>
         <div class="form-control-focus">
         </div>
         <span class="help-block with-errors">${hint}</span>
      </div>
   </div>
</div>
</#macro>
<#macro warningInput label id hint="" name="" value="" placeholder="" inputSize="md" iconClass="" iconLeft=true disabled=false readonly=false required=false>
<div class="form-group has-warning row">
   <label class="col-md-4 col-form-label" for="${id}">${label}<#if required><span class="text-danger">&#42;</span></#if></label>
   <div class="col-md-8">
      <div class="input-icon <#if !iconLeft>right</#if>">
         <input type="text" class="form-control input-${inputSize}" value="${value!}" id="${id}" name="<#if !name?has_content>${id}<#else>${name}</#if>" placeholder="${placeholder}" <#if disabled>disabled</#if> <#if readonly>readonly</#if> <#if required>required</#if> >
         <#if disabled>
         <input type="hidden" id="${id}" name="<#if !name?has_content>${id}<#else>${name}</#if>" value="${value!}"/>	
         </#if>
         <div class="form-control-focus">
         </div>
         <span class="help-block with-errors">${hint}</span>
      </div>
   </div>
</div>
</#macro>
<#macro errorInput label id hint="" name="" value="" placeholder="" inputSize="md" iconClass="" iconLeft=true disabled=false readonly=false required=false>
<div class="form-group has-error row">
   <label class="col-md-4 col-form-label" for="${id}">${label}<#if required><span class="text-danger">&#42;</span></#if></label>
   <div class="col-md-8">
      <div class="input-icon <#if !iconLeft>right</#if>">
         <input type="text" class="form-control input-${inputSize}"" value="${value!}" id="${id}" name="<#if !name?has_content>${id}<#else>${name}</#if>" placeholder="${placeholder}" <#if disabled>disabled</#if> <#if readonly>readonly</#if> <#if required>required</#if> >
         <#if disabled>
         <input type="hidden" id="${id}" name="<#if !name?has_content>${id}<#else>${name}</#if>" value="${value!}"/>	
         </#if>
         <div class="form-control-focus">
         </div>
         <span class="help-block with-errors">${hint}</span>
      </div>
   </div>
</div>
</#macro>
<#macro dropdownInput label id options="" hint="" name="" value="" required=false disabled=false tooltip="" allowEmpty=false dataLiveSearch=false>
<div class="form-group row <#if required>has-error</#if> ${id}">
   <label class="col-sm-4 col-form-label" for="${id}">${label}</label>
   <div class="col-sm-7">
      <select class="selectpicker show-tick form-control input-sm <#if tooltip?has_content>tooltips</#if>" id="${id}" name="<#if !name?has_content>${id}<#else>${name}</#if>" <#if required>required</#if> <#if disabled>disabled</#if> <#if tooltip?has_content>data-original-title="${tooltip}"</#if> <#if dataLiveSearch>data-live-search="true"</#if>  >
      <#if allowEmpty>
      <option value="">Please Select</option>
      </#if>
      <#if options?has_content>
      <#list options.entrySet() as entry>  
      <option value="${entry.key}" <#if value?exists && value == entry.key>selected</#if> >${entry.value!}</option>
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
<#macro textareaInput label id rows hint="" name="" value="" placeholder="" state="" tooltip="" required=false>
<div class="form-group row <#if state?exists>has-${state!}</#if> <#if required>has-error</#if>">
   <label class="col-sm-4 col-form-label" for="${id}">${label}</label>
   <div class="col-sm-7">
      <textarea class="form-control <#if tooltip?has_content>tooltips</#if>" rows="${rows}" id="${id}" name="<#if !name?has_content>${id}<#else>${name}</#if>" placeholder="${placeholder}" <#if required>required</#if> <#if tooltip?has_content>data-original-title="${tooltip}"</#if> >${value!}</textarea>
      <div class="form-control-focus">
      </div>
      <span class="help-block with-errors" id="${id}_errors">${hint}</span>
   </div>
</div>
</#macro>
<#macro readonlyInput label id name="" value="" placeholder="" isHiddenInput=false isDate=false>
<div class="form-group row">
   <label class="col-md-4 col-form-label" for="${id}">${label}</label>
   <div class="col-md-8">
      <div class="form-control form-control-static">
         <#if isDate && value?has_content>
         ${value?string.medium}
         <#else>
         ${value!}	
         </#if>
         <#if isHiddenInput>
         <input type="hidden" name="<#if name?has_content>${name}<#else>${id!}</#if>" value="${value!}"/>
         </#if>
      </div>
      <div class="form-control-focus">
      </div>
   </div>
</div>
</#macro>
<#macro checkboxInput label id name="" value="">
<div class="form-group row">
   <label class="col-md-4 col-form-label" for="${id}">${label}</label>
   <div class="col-md-8">
      <div class="md-checkbox-inline">
         <div class="md-checkbox">
            <input type="checkbox" id="${id}" name="<#if !name?has_content>${id}<#else>${name}</#if>" class="md-check" <#if value?exists && (value == "Y" || value == "true") >checked</#if> >
            <label for="${id}">
            <span></span>
            <span class="check"></span>
            <span class="box"></span>
            </label>
         </div>
      </div>
   </div>
</div>
</#macro>
<#macro checkboxesInput label name options value="" isCheckboxInline=false>
<div class="form-group row">
   <label class="col-md-4 col-form-label">${label}</label>
   <div class="col-md-8">
      <div class="<#if isCheckboxInline> md-checkbox-inline <#else> md-checkbox-list </#if> ">
         <#list options.entrySet() as entry>  
         <div class="md-checkbox">
            <input type="checkbox" id="${entry.key}" name="${name}" value="${entry.key}" class="md-check" <#if value?exists && entry.key == value>checked</#if> >
            <label for="${entry.key}">
            <span></span>
            <span class="check"></span>
            <span class="box"></span>
            ${entry.value!} </label>
         </div>
         </#list>
      </div>
   </div>
</div>
</#macro>
<#macro radioInput label name options value="" radioInline=false>
<div class="form-group row">
   <label class="col-md-4 col-form-label">${label}</label>
   <div class="col-md-8">
      <div class="<#if radioInline> md-radio-inline <#else> md-radio-list </#if> ">
         <#list options.entrySet() as entry>  
         <div class="md-radio">
            <input type="radio" id="${entry.key}" name="${name}" value="${entry.key}" class="md-radiobtn" <#if value?exists && entry.key == value>checked</#if> >
            <label for="${entry.key}">
            <span></span>
            <span class="check"></span>
            <span class="box"></span>
            ${entry.value!} </label>
         </div>
         </#list>
      </div>
   </div>
</div>
</#macro>
<#macro dateInput label id name="" value="" dateViewmode="" dateStartFrom="" inputSize="medium" dateFormat="yyyy-mm-dd" disablePastDate=false required=false>
<div class="form-group row">
   <label class="col-md-4 col-form-label" for="${id}">${label}<#if required><span class="text-danger">&#42;</span></#if></label>
   <div class="col-md-8">
      <div class="input-group input-${inputSize!} date date-picker" <#if dateStartFrom?exists>data-date="${dateStartFrom!}"</#if> data-date-format="${dateFormat}" <#if disablePastDate>data-date-start-date="+0d"</#if> <#if dateViewmode?exists>data-date-viewmode="${dateViewmode!}"</#if> >
      <input type="text" class="form-control" id="${id}" name="<#if !name?has_content>${id}<#else>${name}</#if>" value="<#if value?has_content>${value!}</#if>" readonly <#if required>required</#if>>
      <span class="input-group-btn">
      <button class="btn default" type="button"><i class="fa fa-calendar"></i></button>
      </span>
   </div>
</div>
</div>
</#macro>
<#macro dateRangeInput label idFrom idTo nameFrom="" nameTo="" valueFrom="" valueTo="" dateViewmode="" dateStartFrom="" inputSize="medium" dateFormat="dd-mm-yyyy" disablePastDate=false>
<div class="form-group row">
   <label class="col-md-4 col-form-label" for="${idFrom}">${label}</label>
   <div class="col-md-8">
      <div class="input-group input-${inputSize!} date-picker input-daterange" <#if dateStartFrom?exists>data-date="${dateStartFrom!}"</#if> data-date-format="${dateFormat}" <#if disablePastDate>data-date-start-date="+0d"</#if> <#if dateViewmode?exists>data-date-viewmode="${dateViewmode!}"</#if> >
      <input type="text" class="form-control" id="${idFrom}" name="<#if !nameFrom?has_content>${idFrom}<#else>${nameFrom}</#if>" value="<#if value?has_content>${valueFrom!?iso_utc}></#if>" readonly>
      <span class="input-group-addon">
      to </span>
      <input type="text" class="form-control" id="${idTo}" name="<#if !nameTo?has_content>${idTo}<#else>${nameTo}</#if>" value="<#if value?has_content>${valueTo!?iso_utc}</#if>">
   </div>
</div>
</div>
</#macro>
<#macro inlineDateInput label id name="" value="" dateViewmode="" dateStartFrom="" inputSize="medium" dateFormat="dd-mm-yyyy" disablePastDate=false>
<div class="form-group row">
   <label class="col-md-4 col-form-label" for="${id}">${label}</label>
   <div class="col-md-8">
      <div class="input-group input-${inputSize!} date date-picker" <#if dateStartFrom?exists>data-date="${dateStartFrom!}"</#if> data-date-format="${dateFormat}" <#if disablePastDate>data-date-start-date="+0d"</#if> <#if dateViewmode?exists>data-date-viewmode="${dateViewmode!}"</#if> >
   </div>
</div>
</div>
</#macro>
<#macro dateTimeInput label id name="" value="" dateViewmode="" dateStartFrom="" inputSize="medium" required=false dateFormat="mm/dd/yyyy HH:mm" disablePastDate=false>
<div class="form-group row">
   <label class="col-md-4 col-form-label" for="${id}">${label}<#if required><span class="text-danger">&#42;</span></#if></label>
   <div class="col-md-8">
      <div class="input-group input-${inputSize!} date form_datetime" <#if dateStartFrom?exists>data-date="${dateStartFrom!}"</#if> data-date-format="${dateFormat}" <#if disablePastDate>data-date-start-date="+0d"</#if> <#if dateViewmode?exists>data-date-viewmode="${dateViewmode!}"</#if> >
      <input type="text" class="form-control" id="${id}" name="<#if !name?has_content>${id}<#else>${name}</#if>" value="<#if value?has_content>${value!}</#if>" <#if required>required<#else>readonly</#if>>
      <span class="input-group-btn">
      <button class="btn default date-set" type="button"><i class="fa fa-calendar"></i></button>
      </span>
   </div>
</div>
</div>
</#macro>
<#macro dateTimeAdvanceInput label id name="" value="" dateViewmode="" dateStartFrom="" inputSize="medium" dateFormat="mm/dd/yyyy HH:mm" disablePastDate=false required=false>
<div class="form-group row">
   <label class="col-md-4 col-form-label" for="${id}">${label}<#if required><span class="text-danger">&#42;</span></#if></label>
   <div class="col-md-8">
      <div class="input-group input-${inputSize!} date form_advance_datetime" <#if dateStartFrom?exists>data-date="${dateStartFrom!}"</#if> data-date-format="${dateFormat}" <#if disablePastDate>data-date-start-date="+0d"</#if> <#if dateViewmode?exists>data-date-viewmode="${dateViewmode!}"</#if> >
      <input type="text" class="form-control" id="${id}" name="<#if !name?has_content>${id}<#else>${name}</#if>" value="<#if value?has_content>${value!}</#if>" readonly <#if required>required</#if>>
      <span class="input-group-btn">
      <button class="btn default date-reset" type="button"><i class="fa fa-times"></i></button>
      <button class="btn default date-set" type="button"><i class="fa fa-calendar"></i></button>
      </span>
   </div>
</div>
</div>
</#macro>
<#macro timeInput label id name="" value="" inputSize="medium" hourFormat24=false>
<div class="form-group row">
   <label class="col-md-4 col-form-label" for="${id}">${label}</label>
   <div class="col-md-8">
      <div class="input-icon input-${inputSize!}" >
         <i class="fa fa-clock-o"></i>
         <input type="text" class="form-control timepicker timepicker-no-seconds <#if hourFormat24>timepicker-24</#if>" id="${id}" name="<#if !name?has_content>${id}<#else>${name}</#if>" value="${value!}" readonly>
      </div>
   </div>
</div>
</#macro>
<#macro fromCommonAction cancelUrl="" cancelLabel="Cancel" submitLabel="Submit" iconClass="" showCancelBtn=true showSubmitBtn=true>
<div class="clearfix"> </div>
<div class="col-md-12 col-sm-12">
   <div class="form-group row">
      <div class="offset-sm-4 col-sm-7">
         <#if showCancelBtn>
         <a href="<#if cancelUrl?has_content><@ofbizUrl>${cancelUrl}</@ofbizUrl><#else>#</#if>" class="btn btn-sm btn-secondary mt">${cancelLabel}</a>
         </#if>
         <#if showSubmitBtn>
         <button type="submit" class="btn btn-sm btn-primary mt"><#if iconClass?exists><#if iconClass?has_content><i class="${iconClass}"></i></#if> </#if>${submitLabel}</button>
         </#if>
      </div>
   </div>
</div>
<div class="clearfix"> </div>
</#macro>
<#macro button label id="" name="" type="button" btnClass="btn blue" iconClass="">
<button type="${type!}" class="${btnClass}" id="${id!}" name="${name!}"><#if iconClass?exists><i class="${iconClass}"></i> </#if>${label}</button>
</#macro>
<#macro noteSuccess content header="" isShadow=true>
<div class="note note-success <#if isShadow>note-shadow</#if>">
   <h4 class="block">Success! ${header!}</h4>
   ${content}
</div>
</#macro>
<#macro noteInfo content header="" isShadow=true>
<div class="note note-info <#if isShadow>note-shadow</#if>">
   <h4 class="block">Info! ${header!}</h4>
   ${content}
</div>
</#macro>
<#macro noteDanger content header="" isShadow=true>
<div class="note note-danger <#if isShadow>note-shadow</#if>">
   <h4 class="block">Danger! ${header!}</h4>
   ${content}
</div>
</#macro>
<#macro noteWarning content header="" isShadow=true>
<div class="note note-warning <#if isShadow>note-shadow</#if>">
   <h4 class="block">Warning! ${header!}</h4>
   ${content}
</div>
</#macro>
<#macro tabComponent tabs activeTabId>
<div class="tabbable-line">
   <ul class="nav nav-tabs">
      <#list tabs.entrySet() as entry>  
      <#assign tab = entry.value>
      <li class="<#if activeTabId == entry.key>active</#if>">
         <a href="#${entry.key}" data-toggle="tab">
         ${tab.get("tabName")} </a>
      </li>
      </#list>
   </ul>
   <div class="tab-content">
      <#list tabs.entrySet() as entry>  
      <#assign tab = entry.value>
      <div class="tab-pane <#if activeTabId == entry.key>active</#if>" id="${entry.key}">
         <#if tab["tabDetailScreen"]?exists>
         ${screens.render(tab["tabDetailScreen"])}
         </#if>
      </div>
      </#list>
   </div>
</div>
</#macro>
<#macro accordionComponent id accordions activeAccordionId>
<div class="panel-group accordion" id="${id}">
   <#list accordions.entrySet() as entry>  
   <#assign accordion = entry.value>
   <div class="panel panel-default">
      <div class="panel-heading">
         <h4 class="panel-title">
            <a class="accordion-toggle accordion-toggle-styled <#if activeAccordionId != entry.key>collapsed</#if>" data-toggle="collapse" data-parent="#accordion3" href="#${entry.key}">
            ${accordion.get("accordionName")} </a>
         </h4>
      </div>
      <div id="${entry.key}" class="panel-collapse <#if activeAccordionId == entry.key>in<#else>collapse</#if>">
         <div class="panel-body">
            ${screens.render(accordion["accordionDetailScreen"])}
         </div>
      </div>
   </div>
   </#list>
</div>
</#macro>
<#macro button label id="" name="" type="button" btnClass="btn blue" iconClass="">
<button type="${type!}" class="${btnClass}" id="${id!}" name="${name!}"><#if iconClass?exists><i class="${iconClass}"></i> </#if>${label}</button>
</#macro>
<#macro modalWindow id modalDetailScreen modalHeader="" btnLabel="" btnClass="btn default" iconClass="" headerIconClass="" isDraggable=false isLarge=false isSmall=false isFull=false isResponsive=false isShowFooter=true isStaticBackground=false isShowBtn=true okIconClass="fa fa-check" okBtnLabel="OK" modalDismissOk=true>
<#if isLarge>
<#assign modalSize = "lg">
<#elseif isSmall>	
<#assign modalSize = "sm">
</#if>
<#if isShowBtn>
<a class="${btnClass}" data-toggle="modal" href="#${id!}"><#if iconClass?exists><i class="${iconClass}"></i> </#if>${btnLabel!} </a>
</#if>
<div class="modal fade <#if isDraggable> draggable-modal</#if><#if modalSize?exists> bs-modal-${modalSize}</#if>" id="${id!}" tabindex="-1" role="basic" aria-hidden="true" <#if isStaticBackground>data-backdrop="static" data-keyboard="false"</#if> >
<div class="modal-dialog<#if modalSize?exists> modal-${modalSize}</#if><#if isFull> modal-full</#if>">
   <div class="modal-content">
      <div class="modal-header">
         <button type="button" class="close" data-dismiss="modal" aria-hidden="true"></button>
         <h4 class="modal-title"><#if headerIconClass?exists><i class="${headerIconClass}"></i> </#if>${modalHeader!}</h4>
      </div>
      <div class="modal-body">
         <#if isResponsive>
         <div class="scroller" data-always-visible="1" data-rail-visible1="1">
            </#if>
            ${screens.render(modalDetailScreen)}	
            <#if isResponsive>
         </div>
         </#if>					 
      </div>
      <#if isShowFooter>
      <div class="modal-footer">
         <button type="button" class="btn default modal-ok" <#if modalDismissOk> data-dismiss="modal" </#if> ><#if okIconClass?exists><i class="${okIconClass}"></i> </#if>${okBtnLabel!}</button>
         <#--<button type="button" class="btn blue">Save changes</button>-->
      </div>
      </#if>
   </div>
</div>
</div>
</#macro>
<#macro draggablePortlets portletScreens applicationId="custom-field-template" id="" url="" layoutSize="3-col" isAnimation=false isAutoArrange=false >
<form method="post" id="frmSort">
   <input type="hidden" id="portletIdlist" name="portletIdlist" value=""/>
   <input type="hidden" id="action" name="action" value="UPDATE_LOCATIONS" />
   <input type="hidden" id="applicationId" name="applicationId" value="${applicationId}" />
   <input type="hidden" name="widgetId" value="${id}"/>
   <input type="hidden" name="layoutSize" value="${layoutSize}"/>
   <div class="row" id="${id}">
      <#assign portletPrefList = Static["org.fio.template.util.CommonUtils"].getPortletPrefList(delegator, applicationId, id, userLogin.partyId) >
      <#if portletPrefList?has_content && (portletPrefList.size() == portletScreens.size()) >
      <#if layoutSize == "3-col">
      <#assign rowCountPortlet = (portletPrefList.size() / 3)?round >
      <#assign colSize = "4" >
      <#elseif layoutSize == "2-col">
      <#assign rowCountPortlet = (portletPrefList.size() / 2)?round >
      <#assign colSize = "6" >
      <#elseif layoutSize == "1-col">
      <#assign rowCountPortlet = (portletPrefList.size() / 1)?round >
      <#assign colSize = "12" >
      </#if>
      <#assign count = -1>	
      <#assign prevColPos = 0>
      <div class="col-md-${colSize} column sortable mix-grid">
         <#list portletPrefList as portletPref>
         <#assign portletScreen = portletScreens[portletPref.portletId]>
         <#assign count = count + 1>
         <#assign arrangeCondition = (portletPref.colPos > (prevColPos))>
         <#if isAutoArrange>
         <#assign arrangeCondition = (count == rowCountPortlet)>
         </#if>
         <#if arrangeCondition >
         <div class="portlet portlet-sortable-empty"></div>
      </div>
      <div class="col-md-${colSize} column sortable mix-grid">
         <#assign count = 0>
         <#assign prevColPos = portletPref.colPos>		
         </#if>
         ${setRequestAttribute("portletId", portletPref.portletId)} 
         ${setRequestAttribute("portletName", portletScreen["portletName"])}
         ${screens.render(portletScreen["portletDetailScreen"])}
         </#list>
      </div>
      <#else>
      <#if layoutSize == "3-col">
      <#assign rowCountPortlet = (portletScreens.size() / 3)?round >
      <#assign colSize = "4" >
      <#elseif layoutSize == "2-col">
      <#assign rowCountPortlet = (portletScreens.size() / 2)?round >
      <#assign colSize = "6" >
      <#elseif layoutSize == "1-col">
      <#assign rowCountPortlet = (portletScreens.size() / 1)?round >
      <#assign colSize = "12" >
      </#if>
      <#assign count = 0>	
      <div class="col-md-${colSize} column sortable mix-grid">
         <#list portletScreens.entrySet() as entry>  
         <#assign portletScreen = entry.value>
         <#assign count = count + 1>
         ${setRequestAttribute("portletId", entry.key)} 
         ${setRequestAttribute("portletName", portletScreen["portletName"])}
         ${screens.render(portletScreen["portletDetailScreen"])}
         <#if count == rowCountPortlet>
         <div class="portlet portlet-sortable-empty"></div>
      </div>
      <div class="col-md-${colSize} column sortable mix-grid">
         <#assign count = 0>	
         </#if>
         </#list>
      </div>
      </#if>
   </div>
</form>
<script type="text/javascript">
   $("#${id}").sortable({
          connectWith: ".portlet",
          items: ".portlet", 
          opacity: 0.8,
          handle : '.portlet-title',
          coneHelperSize: true,
          placeholder: 'portlet-sortable-placeholder',
          forcePlaceholderSize: true,
          tolerance: "pointer",
          helper: "clone",
          tolerance: "pointer",
          forcePlaceholderSize: !0,
          helper: "clone",
          cancel: ".portlet-sortable-empty, .portlet-fullscreen", // cancel dragging if portlet is in fullscreen mode
          revert: 250, // animation in milliseconds
          update: function(b, c) {
          	
          	buildDraggablePortletPreference();
          	
              if (c.item.prev().hasClass("portlet-sortable-empty")) {
                  c.item.prev().before(c.item);
              }  
                                
          }
      });
      
      <#if !portletPrefList?has_content || (portletPrefList.size() != portletScreens.size()) >
      	buildDraggablePortletPreference();
      </#if>
      
      function buildDraggablePortletPreference() {
      	
      	var sortedIDs = $("#${id}").sortable( "toArray" );
      	$('#portletIdlist').val( sortedIDs );
      	
      	$.post("${url}",
   		$('#frmSort').serialize(),
   		function(data){
   			//alert(data._RESULT_); 
   			
   			if (data._RESULT_ != undefined && data._RESULT_ == "error") {
                  console.log(data._ERROR_MESG_);
                  showAlert("error", data._ERROR_MESG_);
               }else if (data._SUCCESS_MESG_ != "") {
               	//showAlert("success", data._SUCCESS_MESG_);
               }
   			
   		 }, "json");
   		 
      }
      
      $('.mix-grid').mixItUp({
      	<#if isAnimation>
       animation: {
           effects: 'fade rotateZ(-90deg)',
           duration: 700
       }
       </#if>
   });
   
</script>	
</#macro>
<#macro ajaxDataTableSection id header columns url isColReorder=false>
<div class="row">
   <div class="col-md-12">
      <div class="portlet light">
         <div class="portlet-title">
            <div class="caption">
               <i class="icon-bar-chart font-green-haze"></i>
               <span class="caption-subject bold uppercase font-green-haze"> ${header}</span>
               <span class="caption-helper"></span>
            </div>
            <div class="tools">
               <a href="javascript:;" class="collapse">
               </a>
               <a href="javascript:;" class="fullscreen">
               </a>
            </div>
         </div>
         <div class="portlet-body">
            <@ajaxDataTable 
            id=id
            url=url
            columns=datatableColumns
            isColReorder=isColReorder
            />
         </div>
      </div>
   </div>
</div>
</#macro>
<#macro ajaxDataTable id columns url isColReorder=false params="" param1="" param2="" param3="" param4="" param5="">
<div class="table-toolbar">
   <div class="row">
      <div class="col-md-6">
      </div>
      <div class="col-md-6">
         <div class="btn-group btn-group-solid pull-right">
            <button type="button" class="btn btn-xs default dropdown-toggle" data-toggle="dropdown">
            Columns<i class="fa fa-angle-down"></i>
            </button>
            <div id="${id}_column_toggler" class="dropdown-menu hold-on-click dropdown-checkboxes pull-right">
               <#list columns.entrySet() as entry>
               <label><input type="checkbox" checked data-column="${entry_index}" data-key="${entry.key}" data-value="${entry.value!}">${entry.value!}</label>
               </#list>
            </div>
         </div>
      </div>
   </div>
</div>
<table class="table table-striped table-bordered table-hover" id="${id}">
   <thead>
      <tr>
         <#list columns.entrySet() as entry>
         <th>${entry.value!}</th>
         </#list>
      </tr>
   </thead>
   <tbody>
   </tbody>
</table>
<script type="text/javascript">
   var columns_${id} = [
               <#list columns.entrySet() as entry>
   				{ "data": "${entry.key}" },
   			</#list>
           ];
   
   var ${id} = function () {
          var table_${id} = $('#${id}');
   	
          // Set tabletools buttons and button container
          $.extend(true, $.fn.DataTable.TableTools.classes, {
              "container": "btn-group tabletools-btn-group pull-right",
              "buttons": {
                  "normal": "btn btn-sm default",
                  "disabled": "btn btn-sm default disabled"
              }
          });
   	
          var oTable_${id} = table_${id}.dataTable({
   		
   		"processing": true,
           "serverSide": true,
           
           //"ajax": "${url}",
           "ajax": {
               "url": "${url}",
               "data": function ( d ) {
                   //d.myKey = "myValue";
                   //d = pata;
                   //d["myKey"] = "myValue";
                   
                   <#list params.entrySet() as entry>
   				 	d["${entry.key}"] = "${entry.value!}";
   				</#list>
   				
   				<#if param1?has_content>
   					d["param1"] = "${param1}";
                   </#if>
                   <#if param2?has_content>
   					d["param2"] = "${param2}";
                   </#if>
                   <#if param3?has_content>
   					d["param3"] = "${param3}";
                   </#if>
                   <#if param4?has_content>
   					d["param4"] = "${param4}";
                   </#if>
                   <#if param5?has_content>
   					d["param5"] = "${param5}";
                   </#if>
               }
           },
           
           "deferRender": true,
           "columns": columns_${id},
           "stateSave": true,
           "stateDuration": 60 * 60 * 24 * 30,  // for 30 days state will be store
           "pagingType": "full_numbers",
   		
              // Internationalisation. For more info refer to http://datatables.net/manual/i18n
              "language": {
                  "aria": {
                      "sortAscending": ": activate to sort column ascending",
                      "sortDescending": ": activate to sort column descending"
                  },
                  "emptyTable": "No data available in table",
                  "info": "Showing _START_ to _END_ of _TOTAL_ entries",
                  "infoEmpty": "No entries found",
                  "infoFiltered": "(filtered1 from _MAX_ total entries)",
                  "lengthMenu": "Show _MENU_ entries",
                  "search": "Search:",
                  "zeroRecords": "No matching records found"
              },
   		
   		"columnDefs": [
   			{
                   "orderable": true,
                   "targets": [0]
               }
              ],
              
              "order": [
                  [0, 'asc']
              ],
              "lengthMenu": [
                  [5, 15, 20, -1],
                  [5, 15, 20, "All"] // change per page values here
              ],
   		
              // set the initial value
              "pageLength": 10,
              "dom": "<'row' <'col-md-12'T>><'row'<'col-md-6 col-sm-12'l><'col-md-6 col-sm-12'f>r><'table-scrollable't><'row'<'col-md-5 col-sm-12'i><'col-md-7 col-sm-12'p>>", // horizobtal scrollable datatable
   			
              // Uncomment below line("dom" parameter) to fix the dropdown overflow issue in the datatable cells. The default datatable layout
              // setup uses scrollable div(table-scrollable) with overflow:auto to enable vertical scroll(see: assets/global/plugins/datatables/plugins/bootstrap/dataTables.bootstrap.js). 
              // So when dropdowns used the scrollable div should be removed. 
              //"dom": "<'row' <'col-md-12'T>><'row'<'col-md-6 col-sm-12'l><'col-md-6 col-sm-12'f>r>t<'row'<'col-md-5 col-sm-12'i><'col-md-7 col-sm-12'p>>",
   		
              "tableTools": {
                  "sSwfPath": "/metronic/swf/copy_csv_xls_pdf.swf",
                  "aButtons": [{
                      "sExtends": "pdf",
                      "sButtonText": "PDF"
                  }, {
                      "sExtends": "csv",
                      "sButtonText": "CSV"
                  }, {
                      "sExtends": "xls",
                      "sButtonText": "Excel"
                  }, {
                      "sExtends": "print",
                      "sButtonText": "Print",
                      "sInfo": 'Please press "CTRL+P" to print or "ESC" to quit',
                      "sMessage": "Generated by DataTables"
                  }, {
                      "sExtends": "copy",
                      "sButtonText": "Copy"
                  }]
              }
          });
          
          <#if isColReorder>
          	new $.fn.dataTable.ColReorder( oTable_${id} );
          </#if>
   	
          var tableWrapper_${id} = $('#${id}_wrapper'); // datatable creates the table wrapper by adding with id {your_table_jd}_wrapper
          var tableColumnToggler_${id} = $('#${id}_column_toggler');
          
          tableWrapper_${id}.find('.dataTables_length select').select2(); // initialize select2 dropdown
          
          $('input[type="checkbox"]', tableColumnToggler_${id}).change(function () {
          	
          	var dataValue = $(this).attr('data-value');
          	var iCol = 0;
          	$('#${id}_column_toggler').find(":checkbox").each(function() {
          		 var title = oTable_${id}.api().column( parseInt($(this).attr("data-column")) ).header();
          		 var title = $(title).html();
          		 if (dataValue == title) {
          		 	iCol = parseInt($(this).attr("data-column"));
          		 }
          	});
          	
              var bVis = oTable_${id}.fnSettings().aoColumns[iCol].bVisible;
              bVis = (bVis ? false : true);
              oTable_${id}.fnSetColumnVis(iCol, bVis);
              
              columnToggler_${id}[$(this).attr('data-key')] = bVis;
              localStorage.setItem("columnToggler_${id}_${userLogin.partyId}", JSON.stringify(columnToggler_${id}));
              
          });
          
          //localStorage.clear();
          //alert(localStorage.getItem("columnToggler_${id}_${userLogin.partyId}"));
   	var columnTogglerFromLocalStorage = null;
   	var columnToggler_${id} = {};
   	if (localStorage.getItem("columnToggler_${id}_${userLogin.partyId}") != null) {
   		columnTogglerFromLocalStorage = $.parseJSON('[' + localStorage.getItem("columnToggler_${id}_${userLogin.partyId}") + ']');
   	}
   	
   	$('#${id}_column_toggler').find(":checkbox").each(function() {
   			    	
       	var columnVisibility = ($(this).is(":checked")) ? true : false;
       	//alert(columnTogglerFromLocalStorage);
       	if (columnTogglerFromLocalStorage != null) {
       		columnVisibility = columnTogglerFromLocalStorage[0][$(this).attr('data-key')];
       	}
       	
       	if (columnVisibility) {
       		$(this).attr('checked', columnVisibility);
       	} else {
       		$(this).removeAttr('checked');
       	}
       	
       	var dataValue = $(this).attr('data-value');
          	var iCol = 0;
          	$('#${id}_column_toggler').find(":checkbox").each(function() {
          		 var title = oTable_${id}.api().column( parseInt($(this).attr("data-column")) ).header();
          		 var title = $(title).html();
          		 if (dataValue == title) {
          		 	iCol = parseInt($(this).attr("data-column"));
          		 }
          	});
   			    	
       	columnToggler_${id}[$(this).attr('data-key')] = columnVisibility;
       	
       	//var iCol = parseInt($(this).attr("data-column"));
   				    	
              oTable_${id}.fnSetColumnVis(iCol, columnVisibility);
       	
   	});
   	
   	localStorage.setItem("columnToggler_${id}_${userLogin.partyId}", JSON.stringify(columnToggler_${id}));
          
          //eval('(' + oTable_${id}.api().column( 0 ) + ')');
          //alert( 'Data source: '+oTable_${id}.api().column( 0 ).dataSrc() );
          
      }
      
      ${id}();
   
</script>
</#macro>

<#macro tableComponent id header >
<div class="row">
   <div class="col-md-12">
      <div class="portlet light">
         <div class="portlet-title">
            <div class="caption">
               <i class="icon-settings font-green-haze"></i>
               <span class="caption-subject bold uppercase font-green-haze"> ${header}</span>
               <#--<span class="caption-helper">${currentDate?string["EEEE, MMMM dd, yyyy"]}</span>-->
            </div>
            <div class="tools">
               <a href="javascript:;" class="collapse">
               </a>
               <#--<a href="#portlet-config" data-toggle="modal" class="config">
               </a>
               <a href="javascript:;" class="reload">
               </a>-->
               <a href="javascript:;" class="fullscreen">
               </a>
               <#--<a href="javascript:;" class="remove">
               </a>-->
            </div>
         </div>
         <div class="portlet-body">
            <div class="table-toolbar">
               <div class="row">
                  <div class="col-md-12">
                     <div class="btn-group btn-group-solid pull-right">
                        <button type="button" class="btn btn-xs default dropdown-toggle" data-toggle="dropdown">
                        Columns<i class="fa fa-angle-down"></i>
                        </button>
                        <div id="${id}_column_toggler" class="dropdown-menu hold-on-click dropdown-checkboxes pull-right">
                           <label><input type="checkbox" checked data-column="0">Layout Name</label>
                           <label><input type="checkbox" checked data-column="1">Layout Type</label>
                           <label><input type="checkbox" checked data-column="2">Is Default ?</label>
                           <label><input type="checkbox" checked data-column="3">Actions</label>
                        </div>
                     </div>
                  </div>
               </div>
            </div>
            <table class="table table-striped table-bordered table-hover" id="${id}">
               <thead>
                  <tr>
                     <th>Layout Name</th>
                     <th>Layout Type</th>
                     <th>Is Default ?</th>
                     <th>Description</th>
                     <th class="text-center">Actions</th>
                  </tr>
               </thead>
               <tbody>
                  <#if screenLayoutList?has_content>
                  <#list screenLayoutList as screenLayout>
                  <tr>
                     <td>${screenLayout.screenLayoutName!}</td>
                     <td>${screenLayout.screenLayoutId!}</td>
                     <td>
                        <#if screenLayout.isDefault?exists && screenLayout.isDefault == "Y">
                        Yes
                        <#else>
                        No
                        </#if>
                     </td>
                     <td>${screenLayout.description!}</td>
                     <td class="text-center">
                        <div class="btn-group btn-group-solid">
                           <a href="editScreenLayout?screenLayoutId=${screenLayout.screenLayoutId}" class="btn btn-xs default tooltips" data-original-title="Edit"><i class="fa fa-pencil info"></i></a>
                           <a href="#" class="btn btn-xs default tooltips" data-original-title="Remove"><i class="fa fa-times red"></i></a>
                        </div>
                     </td>
                  </tr>
                  </#list>
                  </#if>
               </tbody>
            </table>
         </div>
      </div>
   </div>
</div>
<script type="text/javascript">
   var TableAdvanced_${id} = function () {
   	
   	var initTable2 = function () {
           var table = $('#${id}');
   
   		/* Formatting function for row details */
           function fnFormatDetails(oTable, nTr) {
               var aData = oTable.fnGetData(nTr);
               var sOut = '<table>';
               sOut += '<tr><td>Layout Name:</td><td>' + aData[1] + '</td></tr>';
               sOut += '<tr><td>Layout Type:</td><td>' + aData[2] + '</td></tr>';
               sOut += '<tr><td>Is Default:</td><td>' + aData[3] + '</td></tr>';
               sOut += '<tr><td>Description:</td><td>' + aData[4] + '</td></tr>';
               //sOut += '<tr><td>Others:</td><td>Could provide a link here</td></tr>';
               sOut += '</table>';
   			
               return sOut;
           }
   
           /*
            * Insert a 'details' column to the table
            */
           var nCloneTh = document.createElement('th');
           nCloneTh.className = "table-checkbox";
   
           var nCloneTd = document.createElement('td');
           nCloneTd.innerHTML = '<span class="row-details row-details-close"></span>';
   
           table.find('thead tr').each(function () {
               this.insertBefore(nCloneTh, this.childNodes[0]);
           });
   
           table.find('tbody tr').each(function () {
               this.insertBefore(nCloneTd.cloneNode(true), this.childNodes[0]);
           });
   
           /* Set tabletools buttons and button container */
           $.extend(true, $.fn.DataTable.TableTools.classes, {
               "container": "btn-group tabletools-btn-group pull-right",
               "buttons": {
                   "normal": "btn btn-sm default",
                   "disabled": "btn btn-sm default disabled"
               }
           });
   
           var oTable = table.dataTable({
   
               // Internationalisation. For more info refer to http://datatables.net/manual/i18n
               "language": {
                   "aria": {
                       "sortAscending": ": activate to sort column ascending",
                       "sortDescending": ": activate to sort column descending"
                   },
                   "emptyTable": "No data available in table",
                   "info": "Showing _START_ to _END_ of _TOTAL_ entries",
                   "infoEmpty": "No entries found",
                   "infoFiltered": "(filtered1 from _MAX_ total entries)",
                   "lengthMenu": "Show _MENU_ entries",
                   "search": "Search:",
                   "zeroRecords": "No matching records found"
               },
   
   			"columnDefs": [
   				{
   	                "orderable": false,
   	                "targets": [0]
   	            },
   	            {
                  		"targets": [ 4 ],
                  		"visible": false,
                  		"searchable": false
              		},
               ],
               "order": [
                   [0, 'asc']
               ],
               "lengthMenu": [
                   [5, 15, 20, -1],
                   [5, 15, 20, "All"] // change per page values here
               ],
   
               // set the initial value
               "pageLength": 10,
               "dom": "<'row' <'col-md-12'T>><'row'<'col-md-6 col-sm-12'l><'col-md-6 col-sm-12'f>r><'table-scrollable't><'row'<'col-md-5 col-sm-12'i><'col-md-7 col-sm-12'p>>", // horizobtal scrollable datatable
   
               // Uncomment below line("dom" parameter) to fix the dropdown overflow issue in the datatable cells. The default datatable layout
               // setup uses scrollable div(table-scrollable) with overflow:auto to enable vertical scroll(see: assets/global/plugins/datatables/plugins/bootstrap/dataTables.bootstrap.js). 
               // So when dropdowns used the scrollable div should be removed. 
               //"dom": "<'row' <'col-md-12'T>><'row'<'col-md-6 col-sm-12'l><'col-md-6 col-sm-12'f>r>t<'row'<'col-md-5 col-sm-12'i><'col-md-7 col-sm-12'p>>",
   
               "tableTools": {
                   "sSwfPath": "/metronic/swf/copy_csv_xls_pdf.swf",
                   "aButtons": [{
                       "sExtends": "pdf",
                       "sButtonText": "PDF"
                   }, {
                       "sExtends": "csv",
                       "sButtonText": "CSV"
                   }, {
                       "sExtends": "xls",
                       "sButtonText": "Excel"
                   }, {
                       "sExtends": "print",
                       "sButtonText": "Print",
                       "sInfo": 'Please press "CTRL+P" to print or "ESC" to quit',
                       "sMessage": "Generated by DataTables"
                   }, {
                       "sExtends": "copy",
                       "sButtonText": "Copy"
                   }]
               }
           });
   
           var tableWrapper = $('#${id}_wrapper'); // datatable creates the table wrapper by adding with id {your_table_jd}_wrapper
           var tableColumnToggler = $('#${id}_column_toggler');
           
           tableWrapper.find('.dataTables_length select').select2(); // initialize select2 dropdown
           
           /* Add event listener for opening and closing details
            * Note that the indicator for showing which row is open is not controlled by DataTables,
            * rather it is done here
            */
           table.on('click', ' tbody td .row-details', function () {
               var nTr = $(this).parents('tr')[0];
               if (oTable.fnIsOpen(nTr)) {
                   /* This row is already open - close it */
                   $(this).addClass("row-details-close").removeClass("row-details-open");
                   oTable.fnClose(nTr);
               } else {
                   /* Open this row */
                   $(this).addClass("row-details-open").removeClass("row-details-close");
                   oTable.fnOpen(nTr, fnFormatDetails(oTable, nTr), 'details');
               }
           });
           
           /* handle show/hide columns*/
           $('input[type="checkbox"]', tableColumnToggler).change(function () {
               /* Get the DataTables object again - this is not a recreation, just a get of the object */
               var iCol = parseInt($(this).attr("data-column"));
               iCol = iCol +1;
               var bVis = oTable.fnSettings().aoColumns[iCol].bVisible;
               oTable.fnSetColumnVis(iCol, (bVis ? false : true));
           });
           
       }
       
   	return {
   
           //main function to initiate the module
           init: function () {
   
               if (!jQuery().dataTable) {
                   return;
               }
   
               //console.log('me 1');
   
               initTable2();
               
               //console.log('me 2');
           }
   
       };
   
   }();
   
   TableAdvanced_${id}.init();
   
</script>
</#macro>

<#-- Format should be one of the DATE_TIME, DATE, TIME, DATE_ONLY -->
<#function getLocalizedDate date="" format="DATE_TIME" encode=false>
<#-- returns String unchanged -->
<#if date?is_string>
<#return date/>
</#if>
<#-- If locale or timeZone are empty, get them from the request -->
<#if !locale?has_content><#assign locale = Static["org.ofbiz.base.util.UtilHttp"].getLocale(request)/></#if>
<#if !timeZone?has_content><#assign timeZone = Static["org.ofbiz.base.util.UtilHttp"].getTimeZone(request)/></#if>
<#if date?has_content && date?is_date>
<#if format == "DATE_TIME">
<#assign fmt = Static["org.ofbiz.base.util.UtilDateTime"].getDateTimeFormat(locale)/>
<#elseif format == "DATE">
<#assign fmt = Static["org.ofbiz.base.util.UtilDateTime"].getDateFormat(locale)/>
<#elseif format == "TIME">
<#assign fmt = Static["org.ofbiz.base.util.UtilDateTime"].getTimeFormat(locale)/>
<#elseif format == "DATE_ONLY">
<#assign fmt = Static["org.ofbiz.base.util.UtilDateTime"].getDateFormat(locale)/>
<#-- If format is DATE_ONLY, just format the date portion and return (this seems to be rendered identically to DATE)-->
<#return date?date?string(fmt) />
</#if>
<#assign dateString = Static["org.ofbiz.base.util.UtilDateTime"].timeStampToString(date?datetime, fmt, timeZone, locale)/>
<#if encode>
<#return Static["org.ofbiz.base.util.UtilHttp"].encodeBlanks(dateString)/>
<#else>
<#return dateString/>
</#if>
<#else>
<#return ""/>
</#if> 
</#function>

<#macro sectionHeader title extra="" titleId="" class="" titleClass="" style="">
<div class="page-header border-b" style="${style}">
   <h1 class="float-left">${title!}</h1>
   <#if extra?has_content>
   <div class="float-right">
      ${extra}
   </div>
   </#if>
</div>
</#macro>

<#macro dropDown label id options="" hint="" name="" value="" onchange ="" required=false disabled=false tooltip="" allowEmpty=false dataLiveSearch=false>
<div class="form-group row <#if required>has-error</#if>">
   <label class="col-sm-4 col-form-label" for="${id}">${label}</label>
   <div class="col-sm-7">
      <select class="selectpicker show-tick form-control input-sm <#if tooltip?has_content>tooltips</#if>" id="${id}" name="<#if !name?has_content>${id}<#else>${name}</#if>" <#if required>required</#if> <#if disabled>disabled</#if> <#if tooltip?has_content>data-original-title="${tooltip}"</#if> <#if dataLiveSearch>data-live-search="true"</#if> onchange="${onchange}" >
      <#if allowEmpty>
      <option value="">Please Select</option>
      </#if>
      <#if options?has_content>
      <#list options.entrySet() as entry>  
      <option value="${entry.key}" <#if value?exists && value == entry.key>selected</#if> >${entry.value!}</option>
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