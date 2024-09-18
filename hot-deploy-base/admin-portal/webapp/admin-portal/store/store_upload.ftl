<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl" />
<style>
  body {
    pointer-events: none;
  }
</style>
<#assign processed = Static["org.fio.loyalty.portal.util.DataUtil"].getProcessedStoreReceiptCount(delegator)?if_exists />
<#assign notProcessed = Static["org.fio.loyalty.portal.util.DataUtil"].getNotProcessedStoreReceiptCount(delegator)?if_exists />
<div class="row">
  <div id="main" role="main" class="pd-btm-title-bar">
    <#assign extraLeft='' />
    <div class="col-lg-12 col-md-12 col-sm-12">
      <#assign addActivities='' />
      <div class="card-head margin-adj mt-2" id="view-detail">
        <div class="col-lg-12 col-md-12 dot-line">
          <div class="row">
            <div class="col-lg-6 col-md-6">
              <h3 class="float-left mr-2 mb-0 header-title view-title">Store Upload</h3>
            </div>
            <div class="col-lg-6 col-md-6">
              <a href="/loyalty-portal/control/findStoreGroup" class="btn btn-xs btn-primary float-right text-right"><i class="fa fa-chevron-circle-left"></i> Back</a>
            </div>
          </div>
        </div>
        <div class="row">
          <div class="col-lg-12 col-md-12 col-sm-12" id="appbar1">
            <div class="table-responsive">
              <table class="table table-bordered">
                <thead style="background-color: #e1e1e1;font-size: 15px;font-weight: 600;">
                  <tr>
                    <th style="padding: 5px 10px;">Stored Receipt Lines:</th>
                    <th style="padding: 5px 10px;"># Processed</th>
                    <th style="padding: 5px 10px;"># Not Processed</th>
                  </tr>
                </thead>
                <tbody>
                  <tr>
                    <td>Stored Receipt Lines</td>
                    <td>${processed?default(0)!}</td>
                    <td>${notProcessed?default(0)!}</td>
                  </tr>
                </tbody>
              </table>
            </div>
          </div>
        </div>
      </div>
      <div class="card-head margin-adj mt-2">
    <div class="">
        <div class="page-title1">
            <h2 class="float-left font-h2">Upload a File</h2>
            <div class="clearfix"></div>
        </div>
    </div>
     <form id="" method="post" action="<@ofbizUrl>uploadStoreReceipts</@ofbizUrl>" data-toggle="validator" enctype="multipart/form-data">
      <@inputHidden name="POOL_NAME" value="pool"/>
      <@inputHidden name="importedFrom" value="STORE"/>
      <@inputHidden name="sectionHeaderUiLabel" value="DataImportImportFromFile"/>
      
    <div class="row">
        <div class="col-lg-6 col-md-6 col-sm-12">
            <div class="form-group row companyName" id="companyName_row" style="">
                <label class="col-sm-4 field-text" id="companyName_label">File to Import</label>
                <div class=" col-sm-8 left">
                   <#--  <input type="file" id="myfile" name="myfile"> -->
                   <@inputRow id="uploadFile_thirdpty" name="uploadFile" type="file" label="" required=true /> 
                    <p style="font-size: 12px;">Pick the right excel (2003 edition) and upload / upload text file with tab delimiter</p>
                </div>
            </div>

            <div class="form-group row firstName" id="firstName_row" style="">
                <label class="col-sm-4 field-text" id="firstName_label">File Format</label>
                <div class=" col-sm-8 left">
                    <@dropdownCell 
						id="fileFormat"
						name="fileFormat"
						required=true
						allowEmpty=false
						tooltip = "File Formate"
						placeholder = ""
						dataLiveSearch=true
						options=storeUploadFormat!
						label=""
					/>
                    <div class="help-block with-errors" id="firstName_error"></div>
                </div>
            </div>

            <div class="form-group row companyName" id="companyName_row" style="">
                <label class="col-sm-4 field-text" id="companyName_label">Excel Format Template</label>
                <div class=" col-sm-8 left">
                	<a style="padding-left:4px;" href="<@ofbizContentUrl>/loyalty-portal-resource/template/store-file.xls</@ofbizContentUrl>" target="_blank" class="linktext">Download</a> 
                </div>
            </div>

            <div class="clearfix"></div>
            <div class="offset-md-4 col-sm-10">
                <div class="text-left ml-1">
                    <button id="create-cust-btn" type="submit" class="btn btn-sm btn-primary disabled"> Upload</button>
                </div>
            </div>
            <br>
            </form>
        </div>
    </div>
</div>
		<@navTab
			instanceId="STORE_UPLOAD"
			activeTabId="failed"
			/>
		</div>
	</div>
</div>
<script>
  $(document).ready(() => {
    $('body').css('pointer-events', 'all') //activate all pointer-events on body
   
   $('#uploadFile_thirdpty').on( 'change', function() {
	   myfile= $(this).val();
	   var ext = myfile.split('.').pop().toLowerCase();
	   if(ext!="xls"){
	   	showAlert("error","Only XLS file allowed");
		$(this).val('');
	   	return false;
	   }
   });
  });
</script>