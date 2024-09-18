<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<div class="row">
<div id="main" role="main" class="pd-btm-title-bar">
	<div class="col-lg-12 col-md-12 col-sm-12 dash-panel">
		<@sectionFrameHeader title="${uiLabelMap.AuditLogByEntity!}" />
		<div class="panel-group" id="accordionMenu" role="tablist" aria-multiselectable="true">
			<div class="panel panel-default">
				<div class="panel-heading" role="tab" id="headingTwo">
					<h4 class="panel-title">
						<a role="button" data-toggle="collapse" data-parent="#accordionMenu"
							href="#accordionDynaBase" aria-expanded="true"
							aria-controls="collapseOne"> ${uiLabelMap.MainFilter!}</a>
					</h4>
				</div>
				<div id="accordionDynaBase" class="panel-collapse collapse show" role="tabpanel" aria-labelledby="headingOne">
					<form method="post" id="searchForm" class="form-horizontal" novalidate="novalidate" data-toggle="validator">
						<div class="panel-body">
							<@inputHidden id="searchCriteria"/>
							<div class="row p-2">
								<div class="col-lg-6 col-md-6 col-sm-12">
									<@dropdownCell 
										id="entity"
										name="entity"
										placeholder=uiLabelMap.SelectEntity
										options=changedEntityNames!
										value="${requestParameters.entity?if_exists}"
										allowEmpty=true
										/>
								</div>
								<div class="col-lg-6 col-md-6 col-sm-12">
									<@dropdownCell 
										id="field"
										name="field"
										placeholder=uiLabelMap.SelectField
										options=changedFieldNames!
										value="${requestParameters.field?if_exists}"
										allowEmpty=true
										/>
								</div>
								<div class="col-md-6 col-lg-6 col-sm-12">
									<@inputDate
										id="startDate"
										type="date"
										placeholder=uiLabelMap.StartDate!
										value="${requestParameters.startDate?if_exists}"
										/>
								</div>
								<div class="col-md-6 col-lg-6 col-sm-12">
									<@inputDate
										id="endDate"
										type="date"
										placeholder=uiLabelMap.EndDate!
										value="${requestParameters.endDate?if_exists}"
										/>
								</div>
								<div class="col-lg-12 col-md-12 col-sm-12">
									<div class="text-right">
										<@button 
											label="${uiLabelMap.Search}"
											id="main-search-btn"
											/>
									</div>
								</div>
							</div>
					</form>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>
<script>
	$(document).ready(function() {
	
	});
</script>