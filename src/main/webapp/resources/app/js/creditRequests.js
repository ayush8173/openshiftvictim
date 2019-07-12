(function() {
	$("#creditRequestsTable").hide();
	$("#errorMessage").html("");
	$("#successMessage").html("");
	
	showCreditRequests();

})();

function showCreditRequests() {
	$("#errorMessage").html("");
	$("#successMessage").html("");
	$("#creditRequestsTable").hide();
	$("#creditRequestsTable tbody").empty();

	$.ajax({
		type : 'POST',
		url : 'AppController',
		data : 'requestType=fetchCreditRequests',
		success : function(response) {
			if (response.status == "success") {
				var creditRequestList = response.data;
				for (var i = 0; i < creditRequestList.length; i++) {
					$("#creditRequestsTable tbody").append("<tr><td>"
							+ encodeHtml(creditRequestList[i].username)
							+ "</td><td>₹ "
							+ encodeHtml(creditRequestList[i].creditAmount)
							+ " /-</td><td>"
							+ encodeHtml(creditRequestList[i].requestDate)
							+ "</td><td>"
							+ "<button class='btn btn-xs btn-success' onclick='processRequest(\"approve\", \"" 
							+ encodeHtml(creditRequestList[i].username) 
							+"\")'>Approve</button> <button class='btn btn-xs btn-danger' onclick='processRequest(\"reject\", \"" 
							+ encodeHtml(creditRequestList[i].username) +"\")'>Reject</button>"
							+ "</td></tr>"
					);
				}
				$("#creditRequestsTable").show();
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

function processRequest(action, username) {
	$.ajax({
		type : 'POST',
		url : 'AppController',
		data : 'requestType=processCreditRequests&approveReject=' + action + '&username=' + username + csrfTokenParam,
		success : function(response) {
			showCreditRequests();
			if (response.status == "success") {
				// do nothing
			}
		},
		error : function(xhr) {
			if(xhr.status == 401) {
				location.href = location.protocol + '//' + location.host + '/BookStoreVictim';
			}
		}
	});
}