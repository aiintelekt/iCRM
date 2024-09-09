<style>

.table-scrollable {

margin: 0px 0 !important;
}
</style>		

		<script>
		function getRandomColor() {
		    var letters = '0123456789ABCDEF'.split('');
		    var color = '#';
		    for (var i = 0; i < 6; i++ ) {
		        color += letters[Math.floor(Math.random() * 16)];
		    }
		    return color;
		}
		
		$(document).ready(function(){
			//delay messages
			$(".hideWhen").delay(5000).slideUp(1000);
		
			$(".page-container").attr("class","");
			$(".page-content-wrapper").attr("class","");
			
			
			
		});
		</script>
		<body>
		<#assign shortcutGroupReq = request.getParameter("sessionTab")?if_exists/>
		
		<div class="">
			
				<div class="col-lg-12 col-md-12 col-sm-12 col-xs-12">
					<#--<#include "commonHeader.ftl" />-->
				</div>
				
				   
					<div id="shortcut_text" class="col-lg-3 col-md-3 col-sm-3 col-xs-3">
						<#include "ShortcutGroup.ftl" />
						
					</div>	
					
					<#--<div class="col-lg-3 col-md-3 col-sm-3 col-xs-3 hidden-lg hidden-md">
						<#include "ShortcutGroup.ftl" />
					</div>-->
						
				
				
				<div id="long_text" class="col-lg-9 col-md-9 col-sm-9 col-xs-9">
							${sections.render("body")}
				</div>
			
		</div>
		
		
		<script type="text/javascript">
		
		$('.page-sidebar-menu').find('li.active').each(function () {
			var activeLink = $(this);
			$(this).parents('li').each(function () {
				$(this).addClass( "active" );
				$(this).children('a').children('span.arrow').addClass( "open" );
			});
			
		});
		
		</script>
		</body>
