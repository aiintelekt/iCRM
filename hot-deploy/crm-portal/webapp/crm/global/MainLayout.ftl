<!DOCTYPE html>
<html lang="en">
      ${sections.render("theme-header-content")}
   <body>
      ${sections.render("header-common-content")}
      ${sections.render("message-content")}
      ${sections.render("header-main-content")}	
      <!-- BEGIN CONTAINER -->
      <div class="jumbotron">
         <!-- BEGIN CONTENT -->
         <div class="container-fluid" >
            ${sections.render("top-content")}
            ${sections.render("bottom-content")}
            ${sections.render("extra-bottom-content")}
            ${sections.render("body-end")}
            ${sections.render("quick-sidebar-content")}
         </div>
         <!-- END CONTENT -->
      </div>
      <!-- END CONTAINER -->
      ${sections.render("footer-content")}
      ${sections.render("theme-footer-content")}
   </body>
</html>