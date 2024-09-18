<#include "component://bootstrap/lib/ofbizFormMacros.ftl"/>
<div class="row">
    <div id="main" role="main">
        <div class="col-lg-12 col-md-12 col-sm-12 dash-panel">
    <@sectionFrameHeader title="${uiLabelMap.OpportunityStaging!}" />
            <form method="post" id="oppo-stage-search-form" class="form-horizontal" novalidate="novalidate" data-toggle="validator">
                <div class="">
                    <div class="row">
                        <div class="col-lg-4 col-md-6 col-sm-12">
                            <@inputRow 
                            id="opportunityStageId"
                            placeholder="Stage ID"
                            inputColSize="col-sm-12"
                            required=false
                            /> 
                            <@inputRow 
                            id="stageName"
                            placeholder="Stage Name"
                            inputColSize="col-sm-12"
                            required=false
                            /> 
                        </div>
                        <div class="col-lg-4 col-md-6 col-sm-12">
                            <@dropdownCell
                            id="opportunityStatusId"
                            allowEmpty=true
                            options=oppoStatusList!
                            placeholder="Select Oppo Status"
                            />
                            <@dropdownCell
                            id="enable"
                            allowEmpty=true
                            options=yesNoOptions!
                            placeholder="Is Enable"
                            />    
                        </div>
                        <div class="col-md-2 col-sm-2">
                            <@button
                            id="main-search-btn"
                            label="${uiLabelMap.Find}"
                            />
                            <@reset
                            label="${uiLabelMap.Reset}"
                            />
                        </div>
                    </div>
                </div>
            </form>
        </div>
    </div>
</div>
<script>     
    $(document).ready(function() {
    
    });
</script>