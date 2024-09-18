<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>

<div class="modal fade modal-lg" id="important_note" tabindex="-1" role="dialog" aria-labelledby="important-note" aria-hidden="true">
	<div class="modal-dialog modal-lg modal-dialog-scrollable" style="max-width: 1200px;">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title" id="exampleModalLabel">Important Notes</h5>
                <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                <span aria-hidden="true">&times;</span>
                </button>
            </div>
            <div class="modal-body">
            	<#-- <@sectionFrameHeaderTab title="All Notes" /> -->
				<#if noteList?has_content>
					<#list noteList as eachNote>
						<#assign createdByName = eachNote.createdByName!>
				        <#assign noteInfo = eachNote.noteInfo!>   
				        <#assign noteDateTime = eachNote.noteDateTime!>
				        <#assign noteType = eachNote.noteType!>
				        
						<div class="col-md-12 col-lg-12 col-sm-12" style="background-color: #02829d;"> 
						    <div style="color: white;"> <span class="all-sr-notes"> <b> User: ${createdByName!} </b> </span> <span class="note-category"> <b> Category: ${noteType!} </b> </span> <span class="float-right"> <b> ${noteDateTime!} </b> </span> </div>
						</div>
						<div> &nbsp; </div>
						<div class="col-md-12 col-lg-12 col-sm-12">
							<@displayCell
				               label="Note Title"
				               value="${eachNote.noteName!}"
				               id="noteName"
				               labelColSize="col-sm-1"
				               inputColSize="col-sm-11"
				               />
			               <@displayCell
				               label="Note Description"
				               value="${StringUtil.wrapString(noteInfo)}"
				               id="noteInfo"
				               labelColSize="col-sm-1"
				               inputColSize="col-sm-11"
				               />
						</div>
						<div> &nbsp; </div>
						
					</#list>
					
					<#else>
						<div class="col-md-12 col-lg-12 col-sm-12" style="text-align: center;padding-bottom: 10px;">
							 <h1>No Records To Show</h1>
						</div>				
				</#if>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-dismiss="modal">Close</button>
            </div>
        </div>
    </div>
</div>

