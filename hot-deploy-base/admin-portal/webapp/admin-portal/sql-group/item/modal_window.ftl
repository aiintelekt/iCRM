<#include "component://bootstrap/lib/ofbizFormMacros.ftl"/>

<#macro createItemModal instanceId>
<div id="${instanceId!}" class="modal fade" >
    <div class="modal-dialog modal-lg" style="max-width: 1700px;">
        <!-- Modal content-->
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title">SqlGroup Item</h4>
                <button type="button" class="close" data-dismiss="modal">&times;</button>
            </div>
            <div class="modal-body">
                <form id="add-item-form" method="post" data-toggle="validator">
                    <input type="hidden" name="activeTab" value="sg-items" />
                    <input type="hidden" name="sqlGroupId" value="${inputContext.sqlGroupId!}">
                    <input type="hidden" name="itemId">
                    <input type="hidden" name="externalLoginKey" value="${requestAttributes.externalLoginKey!}"/>
                    <@dynaScreen 
                    instanceId="SQLGRP_ITEM"
                    modeOfAction="CREATE"
                    />
                    <div class="form-group offset-2">
                        <div class="text-left ml-3">
                            <@formButton
                            btn1type="submit"
                            btn1label="${uiLabelMap.Save}"
                            btn2=true
                            btn2id="item-reset-btn"
                            btn2type="reset"
                            btn2label="${uiLabelMap.Clear}"
                            />
                        </div>
                    </div>
                </form>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-sm btn-primary navbar-dark" data-dismiss="modal">Close</button>
            </div>
        </div>
    </div>
</div>

<script>
$(document).ready(function() {
    
$('#${instanceId!}').on('hidden.bs.modal', function (e) {
	console.log('hide modal');
  	$('#add-item-form input[name=itemId]').val("");
  	$('#add-item-form #description').val('');
  	$("#item-reset-btn").trigger( "click" );
});

});
    
function editSqlGrpItem(sqlGroupId, itemId) {
	$('#${instanceId!}').modal('show');
	$('#add-item-form input[name=sqlGroupId]').val(sqlGroupId);
	$('#add-item-form input[name=itemId]').val(itemId);
	$.ajax({
		type: "POST",
     	url: "/approval-portal/control/getSqlGrpItemData",
        data: {"sqlGroupId": sqlGroupId, "itemId": itemId, "externalLoginKey": "${requestAttributes.externalLoginKey!}"},
        async: false,
        success: function (result) {   
            if (result.code == 200) {
            	for (var fieldName in result.data){
				    //console.log("name: "+fieldName+", value: "+result.data[fieldName]);
				    if (result.data[fieldName]) {
				    	$('#add-item-form #'+fieldName).val(result.data[fieldName]);
				    }
				}
				$('.ui.dropdown.search').dropdown({
					clearable: true
				});
				$('#isEnabled_error').html("");
            }
        }
	}); 
}

function executeSqlGrpItem(sqlGroupId, itemId) {
	$('#${instanceId!}').modal('show');
	$('#add-item-form input[name=sqlGroupId]').val(sqlGroupId);
	$('#add-item-form input[name=itemId]').val(itemId);
	$.ajax({
		type: "POST",
     	url: "/approval-portal/control/executeSqlGrpItemSingle",
        data: {"sqlGroupId": sqlGroupId, "itemId": itemId, "externalLoginKey": "${requestAttributes.externalLoginKey!}"},
        async: false,
        success: function (result) {   
            if (result.code == 200) {
				showAlert ("success", "Successfully execute sql group item# "+itemId);
				getSqlGrouptItemListRowData();
			} else {
				showAlert ("error", data.message);
			}
        }
	}); 
}

</script> 
</#macro>