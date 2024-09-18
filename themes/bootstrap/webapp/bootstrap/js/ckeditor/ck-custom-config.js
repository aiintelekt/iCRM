/**
 * @license Copyright (c) 2003-2014, CKSource - Frederico Knabben. All rights reserved.
 * For licensing, see LICENSE.md or http://ckeditor.com/license
 */

var defaultTheme = "white";
$.ajax({
	async: false,
	url:"/admin-portal/control/getCkEditorTheme",
	type:"POST",
	data: {},
	success: function(data){
		defaultTheme = data.ckEditorTheme;
	}
});

CKEDITOR.editorConfig = function(config) {
	config.autoParagraph = false;
	config.fillEmptyBlocks = false;
	config.tabSpaces = 0;
	CKEDITOR.config.fullPage = false;
	config.htmlEncodeOutput = false;
	config.entities = false;
	config.allowedContent = true;
	config.disableNativeSpellChecker = false;
	config.colorButton_enableAutomatic = true;
	config.extraPlugins = 'autogrow,font,filebrowser,scayt,iframe,smiley,forms,find,colorbutton,panelbutton,print,preview,templates,selectall';
	config.autoGrow_minHeight = 250;
	config.autoGrow_maxHeight = 600;
	config.autoGrow_onStartup = true;
	config.autoGrow_bottomSpace = 30;

	if ("dark" === defaultTheme) {
		config.skin = 'moono-dark';
	}
};
