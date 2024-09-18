<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>

<div class="page-header border-b pt-2">
    <@headerH2 title="Customized Offers" />
</div>

<div class="clearfix"> </div>
<div class="bg-light">
    <div class="row">
        <div class="col-md-12 col-lg-4 col-sm-12">
            <div class="field-text ml-0"><strong>Personal Loan Tier Group:DEFAULT</strong>-${plTierGroupData!}</div>
        </div>
        <div class="col-md-12 col-lg-4 col-sm-12">
            <div class="field-text ml-0"><strong>Balance Transfer Load Tier Group:DEFAULT</strong>-${btlTierGroupData!}</div>
        </div>
        <div class="col-md-12 col-lg-4 col-sm-12">
            <div class="field-text ml-0"><strong>Debit Consolidation Plan Tier Group:DEFAULT</strong>-${dcpTierGroupData!}</div>
        </div>
    </div>
</div>

<#if headerList?has_content>
    <div class="page-header border-b pt-1">
        <@headerH2 title="Eligible for Customized Personal Loan Offer"/>
    </div>
    <div class="col-sm-12">
        <table class="table table-bordered table-striped">
            <thead>
                <tr>
                    <#list headerList as header>
                        <th>${(header)!}</th>
                    </#list>
                </tr>
            </thead>
            <tbody>
                <#list fieldList as data>
                    <tr>
                        <#list data as field>
                            <td>${(field)!}</td>
                        </#list>
                    </tr>
                </#list>
            </tbody>
        </table>
    </div>
</#if>

<#if balancedTransferHeaders?has_content>
    <div class="clearfix"></div>
    <div class="page-header border-b pt-1">
        <@headerH2 title="Eligible for Customized Balanced Transfer Offer"/>
    </div>
    <div class="col-sm-12">
        <table class="table table-bordered table-striped">
            <thead>
                <tr>
                    <#list balancedTransferHeaders as header>
                        <th>${(header)!}</th>
                    </#list>
                </tr>
            </thead>
            <tbody>
                <#list balancedTransferData as data>
                    <tr>
                        <#list data as field>
                            <td>${(field)!}</td>
                        </#list>
                    </tr>
                </#list>
            </tbody>
        </table>
    </div>
</#if>
