<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>

<script >						
var noteId="";
$(function() {
const url=window.location.search;
			const urlParam=new URLSearchParams(url);
			const noteId=urlParam.get("noteId");
			getNoteData(noteId);
});
function getNoteData(noteId) {
    var result = null;
    $.ajax({
        type: "POST",
        url: "getNoteData",
        async: false,
         data:  {"noteId": noteId},
        success: function(data) {
            result=data[0];
             var noteId=result.noteId;
    		var noteName=result.noteName;
    		var noteInfo=result.noteInfo;
    		var moreInfoItemId=result.moreInfoItemId;
   			var moreInfoItemName=result.moreInfoItemName;
   			var createdStamp=result.createdStamp;
    		var noteParty=result.noteParty;
    		var createdBy = result.createdBy;
    		var moreInfoUrl = result.moreInfoUrl;
    		
            document.getElementById("test1").innerHTML=noteId;
            document.getElementById("test2").innerHTML=noteName;
            document.getElementById("test3").innerHTML=noteInfo;
            document.getElementById("test4").innerHTML=moreInfoItemId;
            if(moreInfoUrl != null && moreInfoUrl != "" && moreInfoUrl != undefined){
            	document.getElementById("test5").innerHTML='<a download href="' + moreInfoUrl + '">' + moreInfoItemName + '</a>';
            }else{
            	document.getElementById("test5").innerHTML=moreInfoItemName;
            }
            document.getElementById("test6").innerHTML=createdBy;
            document.getElementById("test7").innerHTML=createdStamp;
            document.getElementById("test8").innerHTML="admin";
        },error: function(data) {
        	result=data;
			console.log('Error occured');
			showAlert("error", "Error occured while fetching Tiles Data!");
		}
    });
}

</script>


<div class="page-header border-b pt-2">
	<div id="main" role="main">
	
  
  		<h6>Attachments : Information</h6>	
     				<@headerH2
        				title="${requestParameters.noteId?if_exists}"
       				/>
   		<div class="page-header border-b pt-2">
     		<@headerH1
        		title="Notes and Attachments"
       			/>
   		</div>
        <div class="row">
		   <div class="col-md-12 col-lg-6 col-sm-12">
		   <@displayCell
		     label="Note ID"
		     value="${noteId!}"
		     id="test1"
		     />
		   <@displayCell
		     label="Note Title"
		     value="${noteName!}"
		     id="test2"
		     />
		   <@displayCell
		     label="Note Description"
		     value="${noteInfo!}"
		     id="test3"
		     />
		   <@displayCell
		     label="File Source"
		     value="${moreInfoItemId!}"
		     id="test4"
		     />
		    <div class="offset-sm-0 col-sm-4">
		    </div>
		   </div>
		   <div class="col-md-12 col-lg-6 col-sm-12">
		   <@displayCell
		     label="File Name"
		     value="${moreInfoItemName!}"
		     id="test5"
		     />
		     
		   <@displayCell
		     label="Created By"
		     value="${createdBy!}"
		     id="test6"
		     />
		     <#assign date = Static["org.ofbiz.base.util.UtilDateTime"].toDateString(createdStamp, "dd/MM/yyyy")/>
		     
		   <@displayCell
		     label="Created On"
		     value="${date!}"
		     id="test7"
		     />
		   <@displayCell
		     label="Owner"
		     value="${owner!}"
		     id="test8"
		     />
		   </div>
  </div>
  </div>
  </div>
