//date field comparator
function dateFieldComparator(date1, date2) {
	var date1Number = date1 && new Date(date1).getTime();
	var date2Number = date2 && new Date(date2).getTime();

	if (date1Number == null && date2Number == null) {
		return 0;
	}

	if (date1Number == null) {
		return -1;
	} else if (date2Number == null) {
	    return 1;
	}

	return date1Number - date2Number;
}

// ag grid custom dropdown implementation start
function extractValues(mappings) {
	return Object.keys(mappings);
}
function lookupValue(mappings, key) {
	return mappings[key];
}
function lookupKey(mappings, name) {
	for ( var key in mappings) {
		if (mappings.hasOwnProperty(key)) {
			if (name === mappings[key]) {
				return key;
			}
		}
	}
}