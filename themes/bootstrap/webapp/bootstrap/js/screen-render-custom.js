$('.date').datetimepicker({
  useCurrent: false,
  format: 'DD/MM/YYYY'
});

$('.datetime').datetimepicker({
	  useCurrent: false,
	  format: 'DD/MM/YYYY HH:mm:ss'
});

$('.picker-window').keypress(function (e) {
	var key = e.which;
	if(key == 13)  // the enter key code
	{
		$('.picker-window').click();
	    return false;  
	}
}); 
$('.picker-window-erase').keypress(function (e) {
	var key = e.which;
	if(key == 13)  // the enter key code
	{
		$('.picker-window-erase').click();
	    return false;  
	}
}); 

var parentPickerInputId;
var parentPickerWindowId;
var currentPickerInputId;
var currentPickerWindowId;
$('.picker-window').click(function () {
	var pickerWindowId = $(this).attr("data-pickerWindow");
	var pickerInputId = $(this).attr("data-pickerInputId");
	if (pickerWindowId) {
		$('#'+pickerWindowId).modal('show');
	}
	currentPickerInputId = pickerInputId;
	currentPickerWindowId = pickerWindowId;
});
$('.picker-window-erase').click(function () {
	var pickerWindowId = $(this).attr("data-pickerWindow");
	var pickerInputId = $(this).attr("data-pickerInputId");
	currentPickerInputId = "";
	currentPickerWindowId = "";
	console.log('pickerInputId> '+pickerInputId);
	$('#'+pickerInputId+'_val').val("");
	$('#'+pickerInputId+'_alter').val("");
	$('#'+pickerInputId+'_desc').val("");
	$('#'+pickerInputId+'_desc').change();
});
function setPickerWindowValue(desc, val) {
	$('#'+currentPickerInputId+'_alter').val("");
	//console.log('parentPickerInputId: '+parentPickerInputId+', parentPickerWindowId: '+parentPickerWindowId);
	currentPickerInputId = currentPickerInputId ? currentPickerInputId : parentPickerInputId;
	currentPickerWindowId = currentPickerWindowId ? currentPickerWindowId : parentPickerWindowId;
	$('#'+currentPickerWindowId).modal('hide');
	console.log('currentPickerInputId: '+currentPickerInputId);
	$('#'+currentPickerInputId+'_desc').val(desc);
	$('#'+currentPickerInputId+'_val').val(val);
	$('#'+currentPickerInputId+'_alter').val(desc);
	$('#'+currentPickerInputId+'_desc').trigger('change');
}