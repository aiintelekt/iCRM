/**
 * @license Copyright (c) 2003-2014, CKSource - Frederico Knabben. All rights reserved.
 * For licensing, see LICENSE.md or http://ckeditor.com/license
 */

var defaultTheme = "white";
/*
$.post("/admin-portal/control/getCkEditorTheme", {}, function(data) {
	if (data) {
		defaultTheme = data.ckEditorTheme;
		
		alert("defaultTheme---3--->"+defaultTheme);
	}
});
*/

$.ajax({
	async: false,
	url:"/admin-portal/control/getCkEditorTheme",
	type:"POST",
	data: {},
	success: function(data){
		defaultTheme = data.ckEditorTheme;
	}
});


if("white" === defaultTheme){
	/*
	CKEDITOR.editorConfig = function( config ) {
		// Define changes to default configuration here. For example:
		// config.language = 'fr';
		// config.uiColor = '#AADC6E';
		config.filebrowserUploadUrl = 'http://localhost:6511/ecommerce/control/showcart';
		config.autoParagraph = false;
		config.fillEmptyBlocks = false;
		config.tabSpaces = 0;
		CKEDITOR.config.fullPage = true;
		config.htmlEncodeOutput = false;
		config.entities = false;
		config.allowedContent=true;
	
	};*/
	
	CKEDITOR.editorConfig = function( config ) {
		config.autoParagraph = false;
		config.fillEmptyBlocks = false;
		config.tabSpaces = 0;
		CKEDITOR.config.fullPage = false;
		config.htmlEncodeOutput = false;
		config.entities = false;
		config.allowedContent=true;
		config.extraPlugins = 'autogrow';
		config.autoGrow_minHeight = 250;
		config.autoGrow_maxHeight = 600;
		config.autoGrow_onStartup = true;
		config.autoGrow_bottomSpace = 30;
	};
} else if("dark" === defaultTheme){
	CKEDITOR.editorConfig = function( config ) {
		config.skin = 'moono-dark';
		config.autoParagraph = false;
		config.fillEmptyBlocks = false;
		config.tabSpaces = 0;
		CKEDITOR.config.fullPage = false;
		config.htmlEncodeOutput = false;
		config.entities = false;
		config.allowedContent=true;
		config.extraPlugins = 'autogrow';
		config.autoGrow_minHeight = 250;
		config.autoGrow_maxHeight = 600;
		config.autoGrow_onStartup = true;
		config.autoGrow_bottomSpace = 30;
	};
}
