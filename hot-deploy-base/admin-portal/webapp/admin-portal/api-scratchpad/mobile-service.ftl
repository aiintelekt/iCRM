<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<style>
	#XmlInput {
	width: 100%;
	box-sizing: border-box;
	}
	#OutputXml{
	width: 100%;
	}
	@media (max-width: 768px) {
	table {
	width: 100%;
	border-collapse: collapse;
	}
	table tr {
	display: block;
	width: 100%;
	margin-bottom: 15px;
	}
	table td {
	display: block;
	text-align: left;
	}
	}
	@media (max-width: 768px) {
	#XmlInput {
	width: 67%;
	}
	#OutputXml{
	width: 67%;
	}
	}
</style>
<div class="row">
	<div id="main" role="main" class="pd-btm-title-bar">
		<div class="col-lg-12 col-md-12 col-sm-12 dash-panel">
			<div><@sectionFrameHeader title=uiLabelMap.MobileApiScratchPad!/></div>
			<div class="form-group row " id="dropDowm_row">
				<label class="col-sm-1 field-text " style="font-weight:bold;" for="sections">Section</label>
				<div class="col-sm-3">
					<select name="serviceName" id="serviceName" onChange="clearTextBox()" class="ui dropdown search form-control input-sm" >
						<option value="">${uiLabelMap.SelectSection!}</option>
						<#if sectionDetails?exists && sectionDetails?has_content>
						<#list sectionDetails as li>
						<option value="${li.enumId!}">${li.description!}</option>
						</#list>
						</#if>
						<#if webServiceTest?has_content>
						<#list webServiceTest as service>
						<option value="${service.serviceName!}">${service.description?default(service.serviceName)}</option>
						</#list>
						</#if>
					</select>
					<div class="help-block with-errors" id="sections_error"></div>
				</div>
				&nbsp;<span style="position: absolute;" id="ajax-input"></span>
			</div>
			<div class="frameSectionBody">
				<br>
				<table>
					<tr>
						<td style="font-weight:bold;">Enter input as Xml</td>
						<td style="font-weight:bold;">Response XML</td>
					</tr>
					<br>
					<tr>
						<td>
							<table class="xml">
								<form name="findCustomers" action="" method="post">
									<tr>
										<td>
											<textarea rows="15" cols="70" id="XmlInput" name="XmlInput"></textarea>
										</td>
									</tr>
								</form>
							</table>
						</td>
						<td>
							<table  class="xml">
								<tr>
									<td>
										<textarea rows="15" cols="70" id="OutputXml" name="OutputXml"></textarea>
									</td>
								</tr>
							</table>
						</td>
					</tr>
					<tr>
						<td>
							<button type="button" onclick="postServiceXml()" class="btn btn-sm btn-primary smallSubmit">Submit</button>&nbsp;&nbsp;<span id="ajax-load"></span>
						</td>
						<td>
							<span class="tabletext" id="time"></span>
						</td>
					</tr>
				</table>
				<br><br>
			</div>
		</div>
	</div>
</div>
</div>
<script>
	function postServiceXml() {

		var start = new Date().getTime();
		var serviceName = document.getElementById("serviceName").value;
		console.log("serviceName---" + serviceName);
		var xmlInput = encodeURIComponent(document.getElementById("XmlInput").value);
		var params = "XmlInput=" + xmlInput;
		if (xmlInput === null || xmlInput === "") {
			params = "";
		}

		document.getElementById("ajax-load").innerHTML = "<img src='/bootstrap/images/ajax-loader.gif'></img>";
		var xmlhttp;
		if (window.XMLHttpRequest) { // code for IE7+, Firefox, Chrome, Opera, Safari
			xmlhttp = new XMLHttpRequest();
		} else { // code for IE6, IE5
			xmlhttp = new ActiveXObject("Microsoft.XMLHTTP");
		}
		xmlhttp.onreadystatechange = function() {
			if (xmlhttp.readyState == 4) {
				if (xmlhttp.status == 200) {
					var responseText = xmlhttp.responseText;
					if (responseText.includes(":ERROR MESSAGE:")) {
						// Handle error here
						document.getElementById("OutputXml").value = ""; // Set OutputXml to empty
						console.log("Error: " + responseText);
					} else {
						document.getElementById("OutputXml").value = responseText;
					}
				} else {
					// Handle error here
					document.getElementById("OutputXml").value = ""; // Set OutputXml to empty
					console.log("Error: " + xmlhttp.status);
				}
				document.getElementById("ajax-load").innerHTML = "";
				var end = new Date().getTime();
				var minutes = (end - start) / 1000;
				minutes = Math.round(minutes * 100) / 100;
				document.getElementById("time").innerHTML = "Exceution Time - " + minutes + "s";
			}
		}
		console.log("serviceName---" + serviceName);
		xmlhttp.open("POST", "<@ofbizUrl>" + serviceName + "</@ofbizUrl>", true);
		xmlhttp.setRequestHeader("X-tenant-Key", $("#tenantKey").val());
		xmlhttp.setRequestHeader("accessTokenKey", $("#accessApiKey").val());
		xmlhttp.setRequestHeader("tenantKey", "${valueTenantApKey?if_exists}");
		xmlhttp.setRequestHeader("serviceName", serviceName);
		xmlhttp.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
		xmlhttp.send(params);
	}


function getSampleRequest() {

	var serviceName = document.getElementById("serviceName").value;
	document.getElementById("ajax-input").innerHTML = "<img src='/bootstrap/images/ajax-loader.gif'></img>";
	var params = "serviceName=" + serviceName;
	if (serviceName === null || serviceName === "") {
		params = "";
	}
	var xmlhttp;
	if (window.XMLHttpRequest) { // code for IE7+, Firefox, Chrome, Opera, Safari
		xmlhttp = new XMLHttpRequest();
	} else { // code for IE6, IE5
		xmlhttp = new ActiveXObject("Microsoft.XMLHTTP");
	}

	xmlhttp.onreadystatechange = function() {
		if (xmlhttp.readyState == 4 && xmlhttp.status == 200) {
			document.getElementById("XmlInput").value = xmlhttp.responseText;
			document.getElementById("ajax-input").innerHTML = "";
		}
	}
	xmlhttp.open("POST", "<@ofbizUrl>getSampleRequest</@ofbizUrl>", true);
	xmlhttp.setRequestHeader("Content-type", "application/x-www-form-urlencoded")
	xmlhttp.send(params);
}

function clearTextBox() {
	document.getElementById("OutputXml").value = "";
	document.getElementById("XmlInput").value = "";
	document.getElementById("time").innerHTML = "";
	getSampleRequest();
}
getSampleRequest();
</script>

