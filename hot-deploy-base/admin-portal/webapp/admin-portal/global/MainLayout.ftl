<!DOCTYPE html>
<html lang="en">
	<head>
		 ${sections.render("theme-header-content")}
	</head>
   <body>
      ${sections.render("header-common-content")}
      ${sections.render("header-main-content")}	
      <!-- BEGIN CONTAINER -->
         <div class="container-fluid" >
            ${sections.render("top-content")}
            ${sections.render("bottom-content")}
            ${sections.render("extra-bottom-content")}
            ${sections.render("body-end")}
            ${sections.render("quick-sidebar-content")}
      	</div>
      <!-- END CONTAINER -->
      ${sections.render("footer-center-content")}
      ${sections.render("theme-footer-content")}
      ${sections.render("message-content")}
   </body>
</html>