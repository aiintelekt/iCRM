<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl" />

<div class="page-header border-b pt-2">
	<h2 class="d-inline-block">Contact Information</h2>
</div>
<h4 class="bg-light pl-1 mt-2">Phone </h4>
<div class="col-md-12 col-lg-6 col-sm-12">
	<div class="row">
		<table class="table table-striped border">
			<thead>
				<tr>
					<th width="14%">Type </th>
					<th width="70%">Contact Information</th>
					<th></th>
				</tr>
			</thead>
			<tbody>
				<tr>
					<form method="post" action="<@ofbizUrl>removePhoneNumberView</@ofbizUrl>" class="form-horizontal" name="removeAddress" id="removeAddress">
						<td>Phone</td>
						<td class=" value-text">${phone!}</td>
						<@inputHidden id="productStoreGroupId" name="productStoreGroupId" value="${productStoreGroupId!}" />
						<@inputHidden id="phoneMech" name="phoneMechId" value="${phoneMechId!}" />
						<td class="text-right p-1">
							<a href="#" data-toggle="modal" data-target="#updatephone" class="btn btn-xs btn-primary fiotooltip" title="Update">
								<i class="fa fa-edit" aria-hidden="true"></i>
							</a>
							<button class="btn btn-xs btn-primary fiotooltip" title="Remove" onclick="return removePhoneAlert();"><i class="fa fa-times" aria-hidden="true"></i> </button>
						</td>
					</form>

				</tr>
				<tr>
					<form method="post" action="<@ofbizUrl>removeMobileView</@ofbizUrl>" class="form-horizontal" name="removeAddress" id="removeAddress">
						<td>Mobile</td>
						<td class=" value-text">${mobile!}</td>
						<@inputHidden id="productStoreGroupId" name="productStoreGroupId" value="${productStoreGroupId!}" />
						<@inputHidden id="mobileNoId" name="mobileId" value="${mobileId!}" />
						<td class="text-right p-1">
							<a href="#" data-toggle="modal" data-target="#updatemobile" class="btn btn-xs btn-primary fiotooltip" title="Update">
								<i class="fa fa-edit" aria-hidden="true"></i>
							</a>
							<button class="btn btn-xs btn-primary fiotooltip" title="Remove" onclick="return removeMobileAlert();"><i class="fa fa-times" aria-hidden="true"></i> </button>
						</td>
					</form>
				</tr>
			</tbody>
		</table>
	</div>
</div>
<h4 class="bg-light pl-1 mt-2">Email </h4>
<div class="col-md-12 col-lg-6 col-sm-12">
	<div class="row">
		<table class="table table-striped border">
			<thead>
				<tr>
					<th width="14%">Type </th>
					<th width="70%">Contact Information</th>
					<th></th>
				</tr>
			</thead>
			<tbody>
				<tr>
					<form method="post" action="<@ofbizUrl>removeEmailView</@ofbizUrl>" class="form-horizontal" name="removeAddress" id="removeAddress">
						<td>Office</td>
						<td class=" value-text">${email!}</td>
						<@inputHidden id="productStoreGroupId" name="productStoreGroupId" value="${productStoreGroupId!}" />
						<@inputHidden id="emailRem" name="emailId" value="${emailId!}" />
						<td class="text-right p-1">
							<a href="#" data-toggle="modal" data-target="#updatemail" class="btn btn-xs btn-primary fiotooltip" title="Update">
								<i class="fa fa-edit" aria-hidden="true"></i>
							</a>
							<button class="btn btn-xs btn-primary fiotooltip" title="Remove" onclick="return removeEmailAlert();"><i class="fa fa-times" aria-hidden="true"></i> </button>
						</td>
					</form>
				</tr>
			</tbody>
		</table>
	</div>
</div>
<h4 class="bg-light pl-1 mt-2">Website </h4>
<div class="col-md-12 col-lg-6 col-sm-12">
	<div class="row">
		<table class="table table-striped border">
			<tbody>
				<tr>
					<form method="post" action="<@ofbizUrl>removeWebView</@ofbizUrl>" class="form-horizontal" name="removeAddress" id="removeAddress">
						<td width="14%">Website </td>
						<td width="70%" class=" value-text">${web!}</td>
						<@inputHidden id="productStoreGroupId" name="productStoreGroupId" value="${productStoreGroupId!}" />
						<@inputHidden id="websiteRem" name="websiteId" value="${websiteId!}" />
						<td class="text-right p-1">
							<a href="#" data-toggle="modal" data-target="#updateweb" class="btn btn-xs btn-primary fiotooltip" title="Update">
								<i class="fa fa-edit" aria-hidden="true"></i>
							</a>
							<button class="btn btn-xs btn-primary fiotooltip" title="Remove" onclick="return removeWebAlert();"><i class="fa fa-times" aria-hidden="true"></i> </button>
						</td>
					</form>
				</tr>
			</tbody>
		</table>
	</div>
</div>
<h4 class="bg-light pl-1 mt-2">Address </h4>
<div class="col-md-12 col-lg-6 col-sm-12">
	<div class="row">
		<table class="table table-striped border">
			<thead>
				<tr>
					<th width="14%">Type </th>
					<th width="70%">Contact Information</th>
					<th></th>
				</tr>
			</thead>
			<tbody>
				<tr>
					<form method="post" action="<@ofbizUrl>removeAddressView</@ofbizUrl>" class="form-horizontal" name="removeAddress" id="removeAddress">
						<td>Office </td>
						<td class=" value-text">${address!}</td>
						<@inputHidden id="productStoreGroupId" name="productStoreGroupId" value="${productStoreGroupId!}" />
						<@inputHidden id="postalRem" name="postalId" value="${postalId!}" />
						<td class="text-right p-1">
							<a href="#" data-toggle="modal" data-target="#updateaddress" class="btn btn-xs btn-primary fiotooltip" title="Update">
								<i class="fa fa-edit" aria-hidden="true"></i>
							</a>
							<button class="btn btn-xs btn-primary fiotooltip" title="Remove" onclick="return removeAddressAlert();"><i class="fa fa-times" aria-hidden="true"></i> </button>
						</td>
					</form>
				</tr>
			</tbody>
		</table>
	</div>
</div>
<!--added for url-->
<h4 class="bg-light pl-1 mt-2">URL </h4>
<div class="col-md-12 col-lg-6 col-sm-12">
	<div class="row">
		<table class="table table-striped border">
			<thead>
				<tr>
					<th width="14%">Type </th>
					<th width="70%">URL Information</th>
					<th></th>
				</tr>
			</thead>
			<tbody>
				<tr>
					<form method="post" action="<@ofbizUrl>removeUrl</@ofbizUrl>" class="form-horizontal" name="removeUrl1" id="removeUrl1">
						<td>Url 1 </td>
						<td class=" value-text">${url1!}</td>
						<@inputHidden id="productStoreGroupId" name="productStoreGroupId" value="${productStoreGroupId!}" />
						<@inputHidden id="url1Rem" name="url1" value="${url1!}" />
						<@inputHidden id="toRemove" name="toRemove" value="url1" />

						<td class="text-right p-1"><a href="#" data-toggle="modal" data-target="#updateurl1" class="btn btn-xs btn-primary fiotooltip" title="Update"><i class="fa fa-edit" aria-hidden="true"></i> </a>
							<button class="btn btn-xs btn-primary fiotooltip" title="Remove" onclick="return removeUrl1Alert();"><i class="fa fa-times" aria-hidden="true"></i> </button>
						</td>
					</form>
				</tr>
				<tr>
					<form method="post" action="<@ofbizUrl>removeUrl</@ofbizUrl>" class="form-horizontal" name="removeUrl2" id="removeUrl2">
						<td>Url 2 </td>
						<td class=" value-text">${url2!}</td>
						<@inputHidden id="productStoreGroupId" name="productStoreGroupId" value="${productStoreGroupId!}" />
						<@inputHidden id="url2Rem" name="url2" value="${url2!}" />
						<@inputHidden id="toRemove" name="toRemove" value="url2" />
						<td class="text-right p-1"><a href="#" data-toggle="modal" data-target="#updateurl2" class="btn btn-xs btn-primary fiotooltip" title="Update"><i class="fa fa-edit" aria-hidden="true"></i> </a>
							<button class="btn btn-xs btn-primary fiotooltip" title="Remove" onclick="return removeUrl2Alert();"><i class="fa fa-times" aria-hidden="true"></i> </button>
						</td>
					</form>
				</tr>
				<tr>
					<form method="post" action="<@ofbizUrl>removeUrl</@ofbizUrl>" class="form-horizontal" name="removeUrl3" id="removeUrl3">
						<td>Url 3 </td>
						<td class=" value-text">${url3!}</td>
						<@inputHidden id="productStoreGroupId" name="productStoreGroupId" value="${productStoreGroupId!}" />
						<@inputHidden id="url3Rem" name="url3" value="${url3!}" />
						<@inputHidden id="toRemove" name="toRemove" value="url3" />
						<td class="text-right p-1"><a href="#" data-toggle="modal" data-target="#updateurl3" class="btn btn-xs btn-primary fiotooltip" title="Update"><i class="fa fa-edit" aria-hidden="true"></i> </a>
							<button class="btn btn-xs btn-primary fiotooltip" title="Remove" onclick="return removeUrl3Alert();"><i class="fa fa-times" aria-hidden="true"></i> </button>
						</td>
					</form>
				</tr>
				<tr>
					<form method="post" action="<@ofbizUrl>removeUrl</@ofbizUrl>" class="form-horizontal" name="removeUrl4" id="removeUrl4">
						<td>Url 4 </td>
						<td class=" value-text">${url4!}</td>
						<@inputHidden id="productStoreGroupId" name="productStoreGroupId" value="${productStoreGroupId!}" />
						<@inputHidden id="url4Rem" name="url4" value="${url4!}" />
						<@inputHidden id="toRemove" name="toRemove" value="url4" />
						<td class="text-right p-1"><a href="#" data-toggle="modal" data-target="#updateurl4" class="btn btn-xs btn-primary fiotooltip" title="Update"><i class="fa fa-edit" aria-hidden="true"></i> </a>
							<button class="btn btn-xs btn-primary fiotooltip" title="Remove" onclick="return removeUrl4Alert();"><i class="fa fa-times" aria-hidden="true"></i> </button>
						</td>
					</form>
				</tr>
				<tr>
					<form method="post" action="<@ofbizUrl>removeUrl</@ofbizUrl>" class="form-horizontal" name="removeUrl5" id="removeUrl5">
						<td>Url 5 </td>
						<td class=" value-text">${url5!}</td>
						<@inputHidden id="productStoreGroupId" name="productStoreGroupId" value="${productStoreGroupId!}" />
						<@inputHidden id="url5Rem" name="url5" value="${url5!}" />
						<@inputHidden id="toRemove" name="toRemove" value="url5" />
						<td class="text-right p-1"><a href="#" data-toggle="modal" data-target="#updateurl5" class="btn btn-xs btn-primary fiotooltip" title="Update"><i class="fa fa-edit" aria-hidden="true"></i> </a>
							<button class="btn btn-xs btn-primary fiotooltip" title="Remove" onclick="return removeUrl5Alert();"><i class="fa fa-times" aria-hidden="true"></i> </button>
						</td>
					</form>
				</tr>

				</tr>
			</tbody>
		</table>
	</div>
</div>