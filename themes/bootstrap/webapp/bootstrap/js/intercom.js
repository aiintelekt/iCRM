var api_base= "https://api-iam.intercom.io/";
var app_Id = "wpb5mlsc";
var user_id = $('#intercom input[name="userId"]').val();
var name = $('#intercom input[name="userName"]').val();
var email = $('#intercom input[name="email"]').val();
window.intercomSettings = {
	api_base: api_base,
	app_id: app_Id,
	user_id: user_id, // IMPORTANT: Replace "user.id" with the variable you use to capture the user's ID
	name: name, // IMPORTANT: Replace "user.name" with the variable you use to capture the user's name
	email: email, // IMPORTANT: Replace "user.email" with the variable you use to capture the user's email address
	created_at: '', // IMPORTANT: Replace "user.createdAt" with the variable you use to capture the user's sign-up date
};

(function(){var w=window;var ic=w.Intercom;if(typeof ic==="function"){ic('reattach_activator');ic('update',w.intercomSettings);}else{var d=document;var i=function(){i.c(arguments);};i.q=[];i.c=function(args){i.q.push(args);};w.Intercom=i;var l=function(){var s=d.createElement('script');s.type='text/javascript';s.async=true;s.src='https://widget.intercom.io/widget/wpb5mlsc';var x=d.getElementsByTagName('script')[0];x.parentNode.insertBefore(s,x);};if(document.readyState==='complete'){l();}else if(w.attachEvent){w.attachEvent('onload',l);}else{w.addEventListener('load',l,false);}}})();