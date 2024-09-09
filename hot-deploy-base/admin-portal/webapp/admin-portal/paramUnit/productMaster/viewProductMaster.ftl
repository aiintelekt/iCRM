<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
 <div class="container-fluid">
        <div class="row">
            <div id="main" role="main">
                <#assign extra='<a href="findProductMaster" class="btn btn-xs btn-primary"><i class="fa fa-chevron-circle-left" aria-hidden="true"></i> Back </a>'/>
        <@sectionFrameHeader title="${uiLabelMap.ViewProductMasterSetUp!}"  extra=extra/>
                    <div class="col-md-12 col-lg-12 col-sm-12">
                        <div class="row">
                            <div class="col-md-12 col-lg-6 col-sm-12 ">
                                 <@displayCell    
                                    label="${uiLabelMap.ProductName!}"
                                    value="${productName!}"
                                    />
                                 <@displayCell    
                                    label="${uiLabelMap.ProductCode!}"
                                    value="${productCode!}"
                                    />
                                 <@displayCell    
                                    label="${uiLabelMap.Status!}"
                                    value="${status!}"
                                    />
                                 <@displayCell    
                                    label="${uiLabelMap.ProductType!}"
                                    value="${productType!}"
                                    />
                                 <@displayCell    
                                    label="${uiLabelMap.SequenceNumber!}"
                                    value="${seqNo!}"
                                    />
                                 <@displayCell    
                                    label="${uiLabelMap.SourceSystemType!}"
                                    value="${sourceSystem!}"
                                    />
                                 <@displayCell    
                                    label="${uiLabelMap.FundCode!}"
                                    value="${fundCode!}"
                                    />
                            </div>
                            <div class="col-md-12 col-lg-6 col-sm-12 ">
                                 <@displayCell    
                                    label="${uiLabelMap.ProductSubCategory!}"
                                    value="${productSubCategory!}"
                                    />
                                 <@displayCell    
                                    label="${uiLabelMap.SchemeCode!}"
                                    value="${schemeCode!}"
                                    />
                            </div>
                            
                        </div>
             <div class="clearfix"></div> 
                </div>
                
            </div>
        </div>
    </div>
  