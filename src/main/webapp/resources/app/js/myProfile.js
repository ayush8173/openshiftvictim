(function() {
	$("#myProfileTable").hide();
	$("#errorMessage").html("");
	$("#successMessage").html("");
	
	fetchMyProfile();
	
	$('#transferCreditModal').on('show.bs.modal', function () {
		if($("#htmlSecurityType").val() != "secure") {
			$("input[name='password']").hide();
		}
	});
	
	$('#transferCreditModal').on('hidden.bs.modal', function () {
		fetchMyProfile();
	});

	$("#requestCreditsForm").submit(function(e) {
		$("#errorMessage").html("");
		$("#successMessage").html("");
		var form = $(this);
		var url = form.attr('action');
		var method = form.attr('method');
		$.ajax({
			type : method,
			url : url,
			data : form.serialize() + csrfTokenParam,
			success : function(response) {
				alert(response.status + " : " + response.data);
			},
			error : function(xhr) {
				if(xhr.status == 401) {
					location.href = location.protocol + '//' + location.host + '/BookStoreVictim';
				}
			}
		});
		e.preventDefault();
	});
	
	$("#transferCreditsForm").submit(function(e) {
		$("#errorMessage").html("");
		$("#successMessage").html("");
		var form = $(this);
		var url = form.attr('action');
		var method = form.attr('method');
		$.ajax({
			type : method,
			url : url,
			data : form.serialize() + csrfTokenParam,
			success : function(response) {
				alert(response.status + " : " + response.data);
			},
			error : function(xhr) {
				if(xhr.status == 401) {
					location.href = location.protocol + '//' + location.host + '/BookStoreVictim';
				}
			}
		});
		e.preventDefault();
	});

})();

function fetchMyProfile() {
	$("#errorMessage").html("");
	$("#successMessage").html("");
	$("#myProfileTable").hide();
	$("#myProfileTable").empty();
	
	var usernameParam;
	
	if($("#htmlSecurityType").val() != "secure") {
		usernameParam = "&username=" + $("#loggedInUsername").val();
	} else {
		usernameParam = "";
	}
	
	$.ajax({
		type : 'POST',
		url : 'AppController',
		data : 'requestType=fetchMyProfile' + usernameParam,
		success : function(response) {
			if (response.status == "success") {
				var user = response.data;
				$("#myProfileTable").append("<tr><th class='center'>Username</th><td class='center'>"
						+ encodeHtml(user.username) + "</td></tr>"
						+ "<tr><th class='center'>Name</th><td class='center'>"
						+ encodeHtml(user.firstName) + " " + encodeHtml(user.lastName) + "</td></tr>"
						+ "<tr><th class='center'>Email</th><td class='center'>"
						+ encodeHtml(user.email) + "</td></tr>"
						+ "<tr><th class='center'>Credits</th><td class='center'>₹ "
						+ encodeHtml(user.credits) + " /-</td></tr>"
				);
				$("#myProfileTable").show();
			}
		},
		error : function(xhr) {
			if(xhr.status == 401) {
				location.href = location.protocol + '//' + location.host + '/BookStoreVictim';
			}
		}
	});
}