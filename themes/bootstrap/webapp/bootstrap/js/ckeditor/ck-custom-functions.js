
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