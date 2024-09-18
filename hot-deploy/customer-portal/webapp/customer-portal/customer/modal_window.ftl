<div id="updateNote" class="modal fade" role="dialog">
    <div class="modal-dialog modal-md modal-lg" style="max-width: 1000px;">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title">Notes</h4>
                <button type="reset" class="close" data-dismiss="modal">&times;</button>
            </div>
            <div class="modal-body">
               <form id="editNoteForms" name="editNoteInfoForm" action="#" method="post" data-toggle="validator" >
                     <@dynaScreen 
	                instanceId="UPDATE_COUPON_NOTE"
	                modeOfAction="UPDATE"
	                />
                    <div class="modal-footer" id="addFooter">
                        <div class="text-left ml-1">
                            <input type="submit" class="btn btn-sm btn-primary disabled" value="Update"  onsubmit="return submitForm();"/>
                            <button type="submit" class="btn btn-sm btn-primary navbar-dark" data-dismiss="modal">Cancel</button>
                        </div>
                    </div>
                    <form>
            </div>
        </div>
    </div>
</div>