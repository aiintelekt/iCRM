/**
 * @license Copyright (c) 2003-2014, CKSource - Frederico Knabben. All rights reserved.
 * For licensing, see LICENSE.md or http://ckeditor.com/license
 */

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

};
