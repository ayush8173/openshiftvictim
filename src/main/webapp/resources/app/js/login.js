$(document).ready(function() {

	var forgotPasswordLink = location.protocol + "//" + location.host + "/BookStoreVictim/forgotPassword.jsp";
	$("#forgotPasswordLink").attr("href", forgotPasswordLink);

	var username = readCookie("username");
	var password = readCookie("password");
	var remember_me = readCookie("remember_me");

	if(username != "" && username != null  && username != undefined 
			&& password != "" && password != null && password != undefined) {
		$("#username").val(username);
		$("#password").val(password);
		$("#submit").click();
	}
	
	if(username != "" && username != null  && username != undefined 
			&& remember_me != "" && remember_me != null && remember_me != undefined) {
		$("#lbUsername").val(username);
		$("#lbRememberMe").val(remember_me);
		$("#lbSubmit").click();
	}
});

function readCookie(cookieName) {
	var cookieNameEQ = cookieName + "=";
	var cookieArray = document.cookie.split(';');
	for (var i = 0; i < cookieArray.length; i++) {
		var cookie = cookieArray[i];
		while (cookie.charAt(0) == ' ') {
			cookie = cookie.substring(1, cookie.length);
		}
		if (cookie.indexOf(cookieNameEQ) == 0) {
			return cookie.substring(cookieNameEQ.length, cookie.length);
		}
	}
	return null;
}