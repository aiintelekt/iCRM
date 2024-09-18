<!DOCTYPE html>
<html lang="en">
	<head>
		 ${sections.render("theme-header-content")}
	</head>
   <body>
      <!-- BEGIN CONTAINER -->
        <div class="container-fluid">
	      <div class="container">
	        <div class="row" id="pwd-container">
            	${sections.render("top-content")}
            </div>
          </div>
      	</div>
      <!-- END CONTAINER -->
      ${sections.render("theme-footer-content")}
      ${sections.render("message-content")}
   </body>
</html>