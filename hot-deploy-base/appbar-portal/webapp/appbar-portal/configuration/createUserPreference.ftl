
<style>
/*
.borderless td, .borderless th {
    border: none;
}
*/
td, th { 
    padding: 5px !important;
}
/*
.table thead th {
	border-bottom: none;
} */
/* Chrome, Safari, Edge, Opera */
input::-webkit-outer-spin-button,
input::-webkit-inner-spin-button {
  -webkit-appearance: none;
  margin: 0;
}

/* Firefox */
input[type=number] {
  -moz-appearance: textfield;
}

.ui-sortable tr {
	cursor:pointer !important;
}
		
.ui-sortable tr:hover {
	background: rgb(90 73 183 / 47%) !important;
}
</style>
<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<#include "component://homeapps/webapp/homeapps/user-audit/audit_page_header.ftl"/>
<div class="row">
    <div id="main" role="main">
    <#assign extra='<a href="createUserPreference?appBarId=${appBarId!}&appBarTypeId=${appBarTypeId!}&externalLoginKey=${requestAttributes.externalLoginKey!}" class="btn btn-xs btn-primary"><i class="fa fa-refresh" aria-hidden="true"></i> Reset</a> <a href="#" onclick="javascript: formSubmission();" class="btn btn-xs btn-primary"><i class="fa fa-save" aria-hidden="true"></i> SaveUserPreference</a>' />
        <@sectionFrameHeader 
	        title="${uiLabelMap.ConfigureUserPreferenceAppBar}"
	        extra=extra!
	        />
	    <#if appBarElementList?has_content>
		    <form method="post" action="configureAppBarUserPreference" id="appBarUserPrefConfigForm" name="appBarUserPrefConfigForm" data-toggle="validator" >
		    	<#include "component://homeapps/webapp/homeapps/user-audit/audit_from_header.ftl"/>
		    	<@inputHidden 
		    		id="appBarId"
		    		value="${appBarId!}"
		    		/>
		    	<@inputHidden 
		    		id="appBarTypeId"
		    		value="${appBarTypeId!}"
		    		/>
		        <div class="col-lg-12 col-md-12 col-sm-12">
		            <div class="table-responsive">
		            	<table class="table table-bordered table-striped w-auto">  
							<thead class="thead-dark">
								<tr>
									<#-- <th>${uiLabelMap.ElementSequence!}</th> -->
									<th>${uiLabelMap.ElementName!}</th>
									<#-- <th>${uiLabelMap.ElementId!}</th> -->
									<th>${uiLabelMap.ElementPosition!}</th>
									<th>${uiLabelMap.LastUpdatedTxStamp!}</th>
									<th>${uiLabelMap.IsEnable!} <input type="checkbox" name="select-all" id="select-all" title="Select All" style="vertical-align: text-bottom;" /></th>
								</tr>
							</thead>
							<tbody>
								<#assign seqId = 1 />
								<#list appBarElementList as appBarElement>
									<tr>
										<input type="hidden" class="priority" value="${seqId!}" name="${appBarElement.appBarElementId!}_SeqId" id="${appBarElement.appBarElementId!}_SeqId" />
										<td><label class="">${appBarElement.appBarElementName!appBarElement.appBarElementUilabel!} (${appBarElement.appBarElementId!})</label></td>
										<#-- <td><label class="">${appBarElement.appBarElementId!}</label></td> -->
										<td><label class=""><#if appBarElement.appBarElementPosition! == "L">Left <#elseif appBarElement.appBarElementPosition! == "R">Right</#if> </label></td>
										<#-- <td>
											<input type="number" class="text-right" min="0" max="99" maxlength="2" step="1" oninput="javascript: if (this.value.length > this.maxLength) this.value = this.value.slice(0, this.maxLength);" pattern="^[0-9]+$" id="${appBarElement.appBarElementId!}_SeqId" style="width:100px;"  class="form-control" name="${appBarElement.appBarElementId!}_SeqId" value="${appBarElement.appBarElementSeqNum!}">
										</td> -->
										<td><label class="">${appBarElement.lastUpdatedTxStamp!}</label></td>
										<td>
											<#-- <input class="checkMe" type="checkbox" id="${appBarElement.appBarElementId!}_isEnable" name="${appBarElement.appBarElementId!}_isEnable" value=""> -->
											<#assign isActive = appBarElement.isActive?if_exists />
											<#assign checked = false />
											<#if "Y" == isActive>
												<#assign checked = true />	
											</#if>
											<@checkboxField
												id="${appBarElement.appBarElementId!}_isEnable"
												class="form-check-input checkMe"
												value=isActive!
												checked = checked!
												/>
										</td>
									</tr>
									<#assign seqId = seqId+1 />
								</#list>
							</tbody>
						</table>
		            </div>
		        </div>
	        </form>
        </#if>
    </div>
</div>


<script>
	$(function(){
		$('input[type="checkbox"]').each(function(){
		    var $t=$(this);
		    var set = $t.prop('checked') ? 'Y' : 'N';
		    $('input[name="'+$t.attr('name')+'"]').val(set);
		}); 
	});
	
	$("#select-all").click(function () {
		$('input:checkbox').not(this).prop('checked', this.checked);
		
		$('input[type="checkbox"]').each(function(){
		    var $t=$(this);
		    var set = $t.prop('checked') ? 'Y' : 'N';
		    $('input[name="'+$t.attr('name')+'"]').val(set);
		});
	});
	
	$(document).on('change', '.checkMe', function () {
	    var $t=$(this);
		var set = $t.prop('checked') ? 'Y' : 'N';
		$('input[name="'+$t.attr('name')+'"]').val(set);
		if("N" === set)
			$("#select-all").prop("checked", false);
	});
	
	function formSubmission() {
		var isChecked = $("input[type='checkbox']:checked").length > 2
		if(isChecked)
			$("#appBarUserPrefConfigForm").submit();
		else{
			showAlert("error", "Atleast three element should be enabled!");
		}
	}

	$(document).ready(function() {
	    //Helper function to keep table row from collapsing when being sorted
		var sortingHelper = function(e, tr) {
			var $originals = tr.children();
			var $helper = tr.clone();
			$helper.children().each(function(index) {
			  $(this).width($originals.eq(index).width())
			});
			return $helper;
		};
	
		//Make diagnosis table sortable
		$("#appBarUserPrefConfigForm tbody").sortable({
	    	helper: sortingHelper,
			stop: function(event,ui) {reorderTable('#appBarUserPrefConfigForm')}
		}).disableSelection();
	});
	
	//Renumber table rows
	function reorderTable(tableId) {
		$(tableId + " tr").each(function() {
			count = $(this).parent().children().index($(this)) + 1;
			//$(this).find('.priority').html(count);
			$(this).find('.priority').val(count);
		});
	}
	
</script>

