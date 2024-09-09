<#include "component://bootstrap/lib/ofbizFormMacros.ftl"/>
<div class="row">
	<div id="main" role="main" class="pd-btm-title-bar">

	<div class="col-lg-12 col-md-12 col-sm-12 dash-panel">
	
	<@sectionFrameHeader title="Find Store Receipts" />
		
	<div class="panel-group" id="accordionMenu" role="tablist" aria-multiselectable="true">
		<div class="panel panel-default">
			<div class="panel-heading" role="tab" id="headingTwo">
				<h4 class="panel-title">
					<a role="button" data-toggle="collapse" data-parent="#accordionMenu"
						href="#accordionDynaBase" aria-expanded="false"
						aria-controls="collapseOne"> ${uiLabelMap.MainFilter} </a>
				</h4>
			</div>	
			
			<div id="accordionDynaBase" class="panel-collapse" role="tabpanel" aria-labelledby="headingOne">
				<form method="post" id="storeReceipt-search-form" class="form-horizontal" novalidate="novalidate" data-toggle="validator">
				<input type="hidden" name="externalLoginKey" value="${requestAttributes.externalLoginKey!}"/>
				<div class="panel-body">
				
					<div class="row">
					<div class="col-lg-12 col-md-12 col-sm-12">
						<@dynaScreen 
							instanceId="FIND_STORE_RECEIPTS"
							modeOfAction="CREATE"
						/>
					</div>
					
					</div>
											
					<div class="text-right pd-cbx-lbls pad-10" style="padding-top: 0px;">
				     	<@button
				        id="storeReceipt-search-btn"
				        label="${uiLabelMap.Find}"
				        />	
				     	<@reset
						label="${uiLabelMap.Reset}"/>
		            </div>
		      		
				</div>	
				</form>
			</div>	
		</div>	
	</div>	
		
	</div>
	</div>
</div>
