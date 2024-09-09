<#assign content=delegator.findOne( "Content",Static[ "org.ofbiz.base.util.UtilMisc"].toMap( "contentId",requestParameters.contentId?if_exists),true)?if_exists/>
<#if requestParameters.url?has_content>
    <#if requestParameters.url=="Y">
        <#assign header="Bookmark URL">
            <#else>
                <#assign header="Attachments">
    </#if>
</#if>
<#if requestParameters.contentId?has_content>
    <#assign button="Update">
        <#assign action="updateContentForParty">
            <#else>
                <#assign button="Create">
                    <#assign action="storeBoookmark">
</#if>

        <div class="page-header border-b">
            <h1 class="float-left">${header?if_exists}</h1>
        </div>
        <form method="post" action="${action?if_exists}" enctype="multipart/form-data" id="${action?if_exists}" class="form-horizontal" name="${action?if_exists}" novalidate="novalidate" data-toggle="validator">
            <#assign requestURI="viewContact" />
            <#if request.getRequestURI().contains( "viewLead")>
                <#assign requestURI="viewLead" />
                <#elseif request.getRequestURI().contains( "viewAccount")>
                    <#assign requestURI="viewAccount" />
            </#if>
            <input type="hidden" name="donePage" value='${requestURI?if_exists}' />
            <input type="hidden" name="partyId" value="${requestParameters.partyId?if_exists}">
            <#if requestParameters.url?has_content>
                <#if requestParameters.url=="Y">
                    <input type="hidden" name="contentTypeId" value="HYPERLINK">
                    <#else>
                        <input type="hidden" name="contentTypeId" value="FILE">
                </#if>
            </#if>

            <div class="row padding-r">
                <div class="col-md-6 col-sm-6">
                    <div class="form-group row row">
                        <label class="col-sm-4 col-form-label">Classifications</label>
                        <#assign enumeration=delegator.findByAnd( "Enumeration",{ "enumTypeId", "CONTENT_CLASS"},Static[ "org.ofbiz.base.util.UtilMisc"].toList( "description"), false)?if_exists/>
                        <div class="col-sm-7">
                            <select name="classificationEnumId" id="classificationEnumId" class="ui dropdown search form-control input-sm" >
                                <option value="">---Select---</option>
                                <#if enumeration?has_content>
                                    <#list enumeration as enumeration>
                                        <option value="${enumeration?if_exists.enumId?if_exists}">${enumeration?if_exists.description?if_exists}</option>
                                    </#list>
                                </#if>
                            </select>
                        </div>
                    </div>
                    <#if requestParameters.url?has_content>
                        <#if requestParameters.url=="Y">
                            <div class="form-group row row has-error">
                                <label class="col-sm-4 col-form-label">URL Address</label>
                                <div class="col-sm-7">
                                    <input type="url" class="form-control input-sm" id="url" name="url" value="${content?if_exists.contentName?if_exists}" placeholder="http://domain.com" required>
                                    <div class="help-block with-errors"></div>
                                </div>
                            </div>
                            <#else>
                                <div class="form-group row row has-error">
                                    <label class="col-sm-4 col-form-label">Upload</label>
                                    <div class="col-sm-7">
                                        <input type="file" class="form-control" id="uploadedFile" name="uploadedFile" required>
                                        <div class="help-block with-errors"></div>
                                    </div>
                                </div>
                        </#if>
                    </#if>
                    <div class="form-group row">
                        <label class="col-sm-4 col-form-label">${uiLabelMap.description!}</label>
                        <div class="col-sm-7">
                            <textarea name="description" rows="3" placeholder="Description" class="form-control">${content?if_exists.description?if_exists}</textarea>
                        </div>
                    </div>
                    <div class="col-md-12 col-sm-12">
                        <div class="form-group row row">
                            <div class="offset-sm-4 col-sm-9">
                                <input type="submit" class="btn btn-sm btn-primary mt" value="${button?if_exists}" />
                            </div>
                        </div>
                    </div>

        </form>