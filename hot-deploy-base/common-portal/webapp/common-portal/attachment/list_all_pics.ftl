<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>

<html>
	<head>
		<meta name="viewport" content="width=device-width, initial-scale=1.0">
		<title>View All Pictures</title>
		<link rel="stylesheet" href="/bootstrap/css/gallerifficCssPlugin/basic.css" type="text/css" />
		<link rel="stylesheet" href="/bootstrap/css/gallerifficCssPlugin/galleriffic-2.css" type="text/css" />
		<script type="text/javascript" src="/bootstrap/js/gallerifficJsPlugin/jquery-1.3.2.js"></script>
		<script type="text/javascript" src="/bootstrap/js/gallerifficJsPlugin/jquery.galleriffic.js"></script>
		<script type="text/javascript" src="/bootstrap/js/gallerifficJsPlugin/jquery.opacityrollover.js"></script>
		<style>
		.sticky-panel{
			color: #02829d;
			text-align : right;
			top: 6.6rem;
			display: block;
			font-size: 1.3vw;
			font-weight: 600;
		}
		.sticky-bar {
			position: fixed;
			max-width: 97vw;
			width: 100%;
			z-index: 999;
		}
		html {
			overflow-y: overlay !important;
		}
		#thumbs ul.thumbs li img {
			width: 75px;
			height: 75px;
		}
		#gallery img {
			max-width: 100%;
			height: 286px;
		}
		a.view-attachments:hover,a.play:hover,a.pause:hover,a.prev:hover,a.next:hover {
			text-decoration: none !important;
		}
		a.view-attachments,a.play,a.pause,a.next,a.prev {
			text-decoration: none !important;
		}
		.attachment-header {
			font-family: 'Frutiger Next Pro', Frutiger, Arial, sans-serif; 
			font-weight: 700;
			font-size: 21px;
			line-height: 30px;
			color: rgb(33, 37, 41);
			letter-spacing: 0em;
		}
		.title {
			font-weight: bold;
		}
		.description-col {
			display: flex;
			align-items: center;
		}
		.container {
			padding-right: 33px !important;
			padding-left: 47px !important;
		}
		.attachment-header-tag {
			font-family: 'Frutiger Next Pro', Frutiger, Arial, sans-serif; 
			font-weight: 700;
			font-size: 21px;
			line-height: 30px;
			color: rgb(33, 37, 41);
			letter-spacing: 0em;
		}
		.contentheaderfont {
			font-size: 16px;
			font-weight: 400;
			line-height: 23px;
		}
		.contentbodyfont {
			font-weight: 700;
			font-size: 15px;
			line-height: 22px;
			color: rgb(33, 37, 41);
		}
		</style>
		<!-- We only want the thunbnails to display when javascript is disabled -->
		<script type="text/javascript">
			document.write('<style>.noscript { display: none; }</style>');
		</script>
	</head>
	<body>
	<br><br><br><br><br>
	<div class="card-head margin-adj mt-0 sticky-bar" id="view-detail">
			<div class="col-lg-12 col-md-12 dot-line">
				<div class="row">
					<div class="col-lg-6 col-md-6">
						<h3 class="float-left mr-2 mb-0 header-title">${domainEntityTypeDesc!}<#if domainEntityName?has_content>: ${domainEntityName!}</#if></h3>
						<span class="sticky-panel">${domainEntityId!}</span>
					</div>
					<div class="col-lg-6 col-md-6" style="padding-bottom: 6px">
						<a href="${domainEntityLink!}" class="btn btn-xs btn-primary float-right text-right">Back </a>
					</div>
				</div>
			</div>
		</div>
		<br><br><br><br>
		<div id="body-content" style="width: 100%;background-color: white;border-radius: 4px;">
		<div class="row"> <div class="col-lg-6 col-md-6 attachment-header" style=""><h2 class="attachment-header-tag">View All Attachments</h2></div></div>
		<div class="container-fluid test">
		<div class="row main-content" style="width: 100%;margin-left: 0px; background-color: white;margin-right: 10px;">
		<div class="col-lg-5 col-md-5 col-sm-6">
			<div id="thumbs-container" style="padding: 27px;margin-left: 7px;">
				<div id="thumbs" class="" style="width: 420px;">
					<ul class="thumbs noscript">
					<#if attachmentList?has_content>
					<#list attachmentList as attachments>
					<#assign userName = attachments.userName!>
					<#assign description = attachments.description!>
					<#assign createdDate = attachments.createdDate!>
					<#assign contentId = attachments.contentId!>
					<#assign imageUrl = attachments.imageUrl!>
					<#assign contentName = attachments.contentName!>
					<li>
						<a class="thumb" name="image-attachments" href="${imageUrl!}" title="Image">
							<img src="${imageUrl!}" alt="Image"/>
						</a>
						<div class="caption">
							<div class="download">
								<span class="fa fa-download btn btn-xs btn-primary button-align" title="Download" onclick="downloadAttachment('${contentId!}','')" style="line-height: 20px;"><span>&nbsp;Download</span></span>
							</div>
						<div class="container" id="file-name-container" style="padding-top: 24px; padding-right: 74px !important;">
							<div class="row">
								<div class="col-4 contentheaderfont" style="padding-left: 6px;">File Name</div>
								<div class="col-6 overflow-hidden contentbodyfont" style="text-align: start;">
								<div class="description-col contentbodyfont" style="margin-left: -5px;">${contentName!}</div>
								</div>
							</div>
						</div>
						<div class="container" style="padding-right :79px !important">
							<div class="row">
								<div class="col-4 contentheaderfont">Description</div>
								<div class="col-6 overflow-hidden" style="text-align: start;">
								<div class="description-col contentbodyfont" style="margin-left: -3px;">${StringUtil.wrapString(description)} </div>
								</div>
							</div>
						</div>
						</div>
					</li>
					</#list>
					<#else>
					<li>
					<div class="col-md-12 col-lg-12 col-sm-12" style="padding-top: 250px;text-align: center;padding-bottom: 280px;background-color: rgb(255, 255, 255);">
						 <h1>No Records To Show</h1>
					</div>
					</li>
					</#if>
					</ul>
				</div>
			</div>
		</div>
		<div class="col-lg-4 col-md-4 col-sm-6 image-container">
		<div id="gallery-container" style="margin: -23px;  margin-left: -78px; margin-bottom: -385px;">
			<div id="gallery" class="content" style="">
					<div id="controls" class="controls"></div>
					<div class="slideshow-container">
						<div id="loading" class="loader"></div>
						<div id="slideshow" class="slideshow"></div>
					</div>
					<div id="caption" class="caption-container"></div>
				</div>
			</div>
		</div>
	</div>
	<div style="clear: both;"></div>
			</div></div>
		<script type="text/javascript">
			jQuery(document).ready(function($) {
				// We only want these styles applied when javascript is enabled
				$('div.navigation').css({'width' : '300px', 'float' : 'left'});
				$('div.content').css('display', 'block');

				// Initially set opacity on thumbs and add
				// additional styling for hover effect on thumbs
				var onMouseOutOpacity = 0.67;
				$('#thumbs ul.thumbs li').opacityrollover({
					mouseOutOpacity:   onMouseOutOpacity,
					mouseOverOpacity:  1.0,
					fadeSpeed:         'fast',
					exemptionSelector: '.selected'
				});
				
				// Initialize Advanced Galleriffic Gallery
				var gallery = $('#thumbs').galleriffic({
					delay:                     2500,
					numThumbs:                 ${attachmentCount!},
					preloadAhead:              10,
					enableTopPager:            true,
					enableBottomPager:         true,
					maxPagesToShow:            7,
					imageContainerSel:         '#slideshow',
					controlsContainerSel:      '#controls',
					captionContainerSel:       '#caption',
					loadingContainerSel:       '#loading',
					renderSSControls:          true,
					renderNavControls:         true,
					playLinkText:              'Play Slideshow',
					pauseLinkText:             'Pause Slideshow',
					prevLinkText:              'Previous Photo',
					nextLinkText:              'Next Photo',
					nextPageLinkText:          'Next &rsaquo;',
					prevPageLinkText:          '&lsaquo; Prev',
					enableHistory:             false,
					autoStart:                 false,
					syncTransitions:           true,
					defaultTransitionDuration: 900,
					onSlideChange:             function(prevIndex, nextIndex) {
						// 'this' refers to the gallery, which is an extension of $('#thumbs')
						this.find('ul.thumbs').children()
							.eq(prevIndex).fadeTo('fast', onMouseOutOpacity).end()
							.eq(nextIndex).fadeTo('fast', 1.0);
					},
					onPageTransitionOut:       function(callback) {
						this.fadeTo('fast', 0.0, callback);
					},
					onPageTransitionIn:        function() {
						this.fadeTo('fast', 1.0);
					}
				});
			});
		</script>
	</body>
</html>
<script>
	function downloadAttachment(contentId) {
		window.location = '/common-portal/control/downloadPartyContent?contentId=' + contentId;
	}
	function trackScreenPercentage() {
		const screenPercentage = calculateScreenPercentage();
		const thresholds = [90, 80, 70];
		thresholds.forEach(threshold => {
			if (screenPercentage == "400" || screenPercentage == "300") {
				document.getElementsByClassName("main-content")[0].style.width = "30%";
			} else if (screenPercentage == "200") {
				document.getElementsByClassName("main-content")[0].style.width = "50%";
			} else if (screenPercentage == "150") {
				document.getElementsByClassName("main-content")[0].style.width = "60%";
			} else if (screenPercentage == "133.30893118594435") {
				document.getElementsByClassName("main-content")[0].style.width = "70%";
			} else {
				document.getElementsByClassName("main-content")[0].style.width = "100%";
			}
		});
	}
	function calculateScreenPercentage() {
		const screenWidth = window.screen.width;
		const windowWidth = window.innerWidth;
		return (windowWidth / screenWidth) * 100;
	}
	window.addEventListener('resize', trackScreenPercentage);
</script>