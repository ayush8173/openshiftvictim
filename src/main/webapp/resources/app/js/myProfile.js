(function() {
	$("#myProfileTable").hide();
	$("#errorMessage").html("");
	$("#successMessage").html("");
	
	fetchMyProfile();
	
	$('#transferCreditModal').on('hidden.bs.modal', function () {
		fetchMyProfile();
	});

	$("#requestCreditsForm").submit(function(e) {
		var form = $(this);
		var url = form.attr('action');
		var method = form.attr('method');
		$.ajax({
			type : method,
			url : url,
			data : form.serialize(),
			success : function(response) {
				alert(response.status + " : " + response.data);
			},
			error : function(xhr) {
				if(xhr.status == 401) {
					if(location.hostname == 'localhost') {
						location.href = location.protocol + '//' + location.host + '/ROOT';
					} else {
						location.href = location.protocol + '//' + location.host;
					}
				}
			}
		});
		e.preventDefault();
	});
	
	$("#transferCreditsForm").submit(function(e) {
		var form = $(this);
		var url = form.attr('action');
		var method = form.attr('method');
		$.ajax({
			type : method,
			url : url,
			data : form.serialize(),
			success : function(response) {
				alert(response.status + " : " + response.data);
			},
			error : function(xhr) {
				if(xhr.status == 401) {
					if(location.hostname == 'localhost') {
						location.href = location.protocol + '//' + location.host + '/ROOT';
					} else {
						location.href = location.protocol + '//' + location.host;
					}
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
	
	$.ajax({
		type : 'POST',
		url : 'AppController',
		data : 'requestType=fetchMyProfile',
		success : function(response) {
			if (response.status == "success") {
				var user = response.data;
				$("#myProfileTable").append("<tr><th class='center'>Username</th><td class='center'>"
						+ encodeHtml(user.username) + "</td></tr>"
						+ "<tr><th class='center'>Name</th><td class='center'>"
						+ encodeHtml(user.firstName) + " " + encodeHtml(user.lastName) + "</td></tr>"
						+ "<tr><th class='center'>Credits</th><td class='center'>₹ "
						+ encodeHtml(user.credits) + " /-</td></tr>"
				);
				$("#myProfileTable").show();
			}
		},
		error : function(xhr) {
			if(xhr.status == 401) {
				if(location.hostname == 'localhost') {
					location.href = location.protocol + '//' + location.host + '/ROOT';
				} else {
					location.href = location.protocol + '//' + location.host;
				}
			}
		}
	});
}