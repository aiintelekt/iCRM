 // specify the columns
   var columnDefs = [
	    {
			headerName: '#',
			width: 100,
			cellRenderer: function(params) {
				return params.node.id + 1;
			}
		},
    {headerName: "Name", field: "Name", sortable: true, filter: true,checkboxSelection: true ,filter: 'agTextColumnFilter'},
      {headerName: "Language", field: "Language" , sortable: true, filter: true,filter: 'agNumberColumnFilter',cellEditor:'agSelectCellEditor',
        cellEditorParams: {
            values: ['French','Italian','Spanish','English', 'Chinese']
        }},
      {headerName: "Country", field: "Country" ,sortable: true, filter: true,filter: 'agNumberColumnFilter'},
	    {headerName: "Account Name", field: "Account Name"},
      {headerName: "Bought", field: "Bought" , sortable: true, filter: true},
      {headerName: "Bank Balance", field: "Bank Balance" , sortable: true, filter: true,cellStyle: {'text-align': 'right'},
        cellRenderer: 'currencyCellRenderer'},
	    {headerName: "Rating", field: "Rating" , sortable: true, filter: true,filter: 'agNumberColumnFilter',cellEditor:'agSelectCellEditor',
        cellEditorParams: {
            values: ['1','2','3','4', '5']
        }},
      {headerName: "Total Winnings", field: "Total Winnings",cellStyle: {'text-align': 'right'},
        cellRenderer: 'currencyCellRenderer'},
      {headerName: "Jan", field: "Jan",cellStyle: {'text-align': 'right'},
        cellRenderer: 'currencyCellRenderer'},
	    {headerName: "Feb", field: "Feb",cellStyle: {'text-align': 'right'},
        cellRenderer: 'currencyCellRenderer'},
      {headerName: "Mar", field: "Mar",cellStyle: {'text-align': 'right'},
        cellRenderer: 'currencyCellRenderer'},
      {headerName: "Apr", field: "Apr",cellStyle: {'text-align': 'right'},
        cellRenderer: 'currencyCellRenderer'},
	    {headerName: "May", field: "May",cellStyle: {'text-align': 'right'},
        cellRenderer: 'currencyCellRenderer'},
      {headerName: "Jun", field: "Jun",cellStyle: {'text-align': 'right'},
        cellRenderer: 'currencyCellRenderer'},
      {headerName: "Jul", field: "Jul",cellStyle: {'text-align': 'right'},
        cellRenderer: 'currencyCellRenderer'},
	  {headerName: "Aug", field: "Aug",cellStyle: {'text-align': 'right'},
        cellRenderer: 'currencyCellRenderer'},
	  {headerName: "Sep", field: "Sep",cellStyle: {'text-align': 'right'},
        cellRenderer: 'currencyCellRenderer'},
	  {headerName: "Oct", field: "Oct",cellStyle: {'text-align': 'right'},
        cellRenderer: 'currencyCellRenderer'},
	  {headerName: "Nov", field: "Nov",cellStyle: {'text-align': 'right'},
        cellRenderer: 'currencyCellRenderer'},
	    {headerName: "Dec", field: "Dec",cellStyle: {'text-align': 'right'},
        cellRenderer: 'currencyCellRenderer'}
    ];

    // let the grid know which columns to use
    var gridOptions = {
      
     // autoGroupColumnDef: autoGroupColumnDef,
     // groupSelectsChildren: true,
      // rowSelection: 'multiple',
	  
									defaultColDef: {
													filter: true,
													editable: true,
													sortable: true,
													resizable: true,
													width: 200,
													 // allow every column to be aggregated
													//enableValue: true,
													// allow every column to be grouped
													//enableRowGroup: true,
													// allow every column to be pivoted
													//enablePivot: true,
													
										       },
												columnDefs: columnDefs,
												//	rowData: null,
												animateRows: true,
												isExternalFilterPresent: isExternalFilterPresent,
												doesExternalFilterPass: doesExternalFilterPass,
												//sideBar: 'columns',
												paginationPageSize: 100,
												pagination: true,
												suppressPaginationPanel: false,
												//suppressScrollOnNewData: true,
												//onPaginationChanged: onPaginationChanged,
												rowSelection: 'multiple',
							
										
												  editType: 'fullRow',
												onCellValueChanged: onCellValueChanged,
												 components:{
																currencyCellRenderer:getCurrencyCellRenderer()
															}
												
	  
    };
	
	var autoGroupColumnDef = {
        headerName: "Name", 
        field: "Name", 
        cellRenderer:'agGroupCellRenderer',
        cellRendererParams: {
            checkbox: true
        }
    }

  // lookup the container we want the Grid to use
  var eGridDiv = document.querySelector('#myGrid');

  // create the grid passing in the div to use together with the columns & data we want to use
  new agGrid.Grid(eGridDiv, gridOptions);
  
  fetch('https://api.myjson.com/bins/b1kbe').then(function(response) {
    return response.json();
  }).then(function(data) {
    gridOptions.api.setRowData(data);
  })
  
  function getSelectedRows() {
    const selectedNodes = gridOptions.api.getSelectedNodes()  
    const selectedData = selectedNodes.map( function(node) { return node.data })
    const selectedDataStringPresentation = selectedData.map( function(node) { return node.Name + ' ' + node.Country }).join(', ')
    alert('Selected nodes: ' + selectedDataStringPresentation);
  }

  function setText(selector, text) {
    document.querySelector(selector).innerHTML = text;
}



function onCellValueChanged(params) {
    var colId = params.column.getId();
    if (colId === 'country') {

        var selectedCountry = params.data.country;
        var selectedCity = params.data.city;

        var allowedCities = countyToCityMap(selectedCountry);
        var cityMismatch = allowedCities.indexOf(selectedCity) < 0;

        if (cityMismatch) {
            params.node.setDataValue('city', null);
        }
    }
}
		
function onPaginationChanged() {
    console.log('onPaginationPageLoaded');

    // Workaround for bug in events order
    if (gridOptions.api) {
        setText('#lbLastPageFound', gridOptions.api.paginationIsLastPageFound());
        setText('#lbPageSize', gridOptions.api.paginationGetPageSize());
        // we +1 to current page, as pages are zero based
        setText('#lbCurrentPage', gridOptions.api.paginationGetCurrentPage() + 1);
        setText('#lbTotalPages', gridOptions.api.paginationGetTotalPages());

        setLastButtonDisabled(!gridOptions.api.paginationIsLastPageFound());
    }
}

function setLastButtonDisabled(disabled) {
    document.querySelector('#btLast').disabled = disabled;
}

function setRowData(rowData) {
    allOfTheData = rowData;
    createNewDatasource();
}

function onBtFirst() {
    gridOptions.api.paginationGoToFirstPage();
}

function onBtLast() {
    console.log("here");
    gridOptions.api.paginationGoToLastPage();
}

function onBtNext() {
    gridOptions.api.paginationGoToNextPage();
}

function onBtPrevious() {
    gridOptions.api.paginationGoToPreviousPage();
}

function onBtPageFive() {
    // we say page 4, as the first page is zero
    gridOptions.api.paginationGoToPage(4);
}

function onBtPageFifty() {
    // we say page 49, as the first page is zero
    gridOptions.api.paginationGoToPage(49);
}
		
		var ageType = 'All';

function isExternalFilterPresent() {
    // if ageType is not everyone, then we are filtering
    return ageType != 'All';
}

function doesExternalFilterPass(node) {
    switch (ageType) {
        case 'below 2': return node.data.Rating < 2;
        case 'Rating 3 and 4': return node.data.Rating >= 3 && node.data.Rating <= 4;
        case 'Rating 5': return node.data.Rating > 4;
        case 'Language': return node.data.Language =='French';
        default: return true;
    }
}


function externalFilterChanged(newValue) {
    ageType = newValue;
    gridOptions.api.onFilterChanged();
}


function onBtExport() {
    var params = {
        skipHeader: getBooleanValue('#skipHeader'),
        columnGroups: getBooleanValue('#columnGroups'),
        skipFooters: getBooleanValue('#skipFooters'),
        skipGroups: getBooleanValue('#skipGroups'),
        skipPinnedTop: getBooleanValue('#skipPinnedTop'),
        skipPinnedBottom: getBooleanValue('#skipPinnedBottom'),
        allColumns: getBooleanValue('#allColumns'),
        onlySelected: getBooleanValue('#onlySelected'),
        fileName: document.querySelector('#fileName').value,
        sheetName: document.querySelector('#sheetName').value,
        exportMode: document.querySelector('input[name="mode"]:checked').value
    };

    if (getBooleanValue('#skipGroupR')) {
        params.shouldRowBeSkipped = function(params) {
            return params.node.data.country.charAt(0) === 'R';
        };
    }

    if (getBooleanValue('#useCellCallback')) {
        params.processCellCallback = function(params) {
            if (params.value && params.value.toUpperCase) {
                return params.value.toUpperCase();
            } else {
                return params.value;
            }
        };
    }

    if (getBooleanValue('#useSpecificColumns')) {
        params.columnKeys = ['Name','Country'];
    }

    if (getBooleanValue('#processHeaders')) {
        params.processHeaderCallback  = function(params) {
            return params.column.getColDef().headerName.toUpperCase();
        };
    }

    if (getBooleanValue('#appendHeader')) {
        params.customHeader  = [
            [],
            [{data:{type:'String', value:'Summary'}}],
            [
                {data:{type:'String', value:'Sales'}, mergeAcross:2},
                {data:{type:'Number', value:'3695.36'}}
            ],
            []
        ];
    }

    if (getBooleanValue('#appendFooter')) {
        params.customFooter  = [
            [],
            [{data:{type:'String', value:'Footer'}}],
            [
                {data:{type:'String', value:'Purchases'}, mergeAcross:2},
                {data:{type:'Number', value:'7896.35'}}
            ],
            []
        ];
    }

    gridOptions.api.exportDataAsExcel(params);
}

function getBooleanValue(cssSelector) {
    return document.querySelector(cssSelector).checked === true;
}

function getCurrencyCellRenderer() {
    var gbpFormatter = new Intl.NumberFormat('en-US', {
        style: 'currency',
        currency: 'GBP',
        minimumFractionDigits: 2
    });
    var eurFormatter = new Intl.NumberFormat('en-US', {
        style: 'currency',
        currency: 'EUR',
        minimumFractionDigits: 2
    });
    var usdFormatter = new Intl.NumberFormat('en-US', {
        style: 'currency',
        currency: 'USD',
        minimumFractionDigits: 2
    });
var curvar='USD';
    function currencyCellRenderer(params) {
        switch (curvar) {
            case 'EUR':
                return eurFormatter.format(params.value);
            case 'USD':
                return usdFormatter.format(params.value);
            case 'GBP':
                return gbpFormatter.format(params.value);
        }
    }

    return currencyCellRenderer;
}