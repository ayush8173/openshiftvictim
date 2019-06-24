(function() {
	$("#allUsersTable").hide();
	$("#errorMessage").html("");
	$("#successMessage").html("");
	
	showAllUsers();
	
	$('#createUserModal').on('show.bs.modal', function () {
		if($("#htmlSecurityType").val() == "secure") {
			$("input[name='cuPassword']").hide();
			$("input[name='cuPassword2']").hide();
		}
	});
	
	$('#createUserModal').on('hidden.bs.modal', function () {
		showAllUsers();
	});

	$("#createUserForm").submit(function(e) {
		$("#errorMessage").html("");
		$("#successMessage").html("");
		var form = $(this);
		var url = form.attr('action');
		var method = form.attr('method');
		$.ajax({
			type : method,
			url : url,
			data : form.serialize(),
			success : function(response) {
				alert(response.status + " : " + response.data);
				if(response.status == 'success') {
					$('#createUserForm')[0].reset();
				}
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

function showAllUsers() {
	$("#errorMessage").html("");
	$("#successMessage").html("");
	$("#allUsersTable").hide();
	$("#allUsersTable tbody").empty();

	$.ajax({
		type : 'POST',
		url : 'AppController',
		data : 'requestType=fetchAllUsers',
		success : function(response) {
			if (response.status == "success") {
				var userList = response.data;
				for (var i = 0; i < userList.length; i++) {
					$("#allUsersTable tbody").append("<tr><td>"
							+ encodeHtml(userList[i].username)
							+ "</td><td>"
							+ encodeHtml(userList[i].firstName)
							+ "</td><td>"
							+ encodeHtml(userList[i].lastName)
							+ "</td><td>"
							+ encodeHtml(userList[i].email)
							+ "</td><td>₹ "
							+ encodeHtml(userList[i].credits)
							+ " /-</td></tr>"
					);
				}
				$("#allUsersTable").show();
			} else {
				$("#errorMessage").html(encodeHtml(response.data));
			}
		},
		error : function(xhr) {
			if(xhr.status == 401) {
				location.href = location.protocol + '//' + location.host + '/BookStoreVictim';
			}
		}
	});
}