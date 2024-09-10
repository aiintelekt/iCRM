var permissionImages = new Map([
	["1", "/bootstrap/images/type-1-red.png"],
	["2", "/bootstrap/images/type-2-yellow.png"],
	["3", "/bootstrap/images/type-3-yellow.png"],
	["4", "/bootstrap/images/type-4-green.png"],
	["5", "/bootstrap/images/type-5-green.png"],
	["6", "/bootstrap/images/type-6-red.png"]
	
]);

function permissionAction(elementId, fieldId) {
	var element = $("#" + elementId);
	var noOfClick = element.attr('value');
	noOfClick = parseInt(noOfClick) + 1;
	var image = permissionImages.get(noOfClick + "");
	$("#" + fieldId).val("L" + noOfClick);
	element.attr("value", noOfClick);
	if (parseInt(noOfClick) > 5) {
		element.attr("value", 0);
	}
	element.attr("src", image);
}