
function setBannerStyle(editor){
	function hideBanner() {
		var banner = document.getElementById('wsc_img_banner');
		if (banner) {
			banner.style.display = 'none';
		}
	}
	hideBanner();
	setInterval(hideBanner, 1);
}

CKEDITOR.on('instanceReady', function(ev) {
	setBannerStyle(ev.editor);
});

class CKEditorUtil {
	static autoGrowMinHeight = '200';
	static removePlugins = 'forms,checkbox,radio,textfield,textarea,select,button,imagebutton,hiddenfield,find,selectall,iframe,blockquote,anchor,link,increaseindent,replace,preview,templates';
}