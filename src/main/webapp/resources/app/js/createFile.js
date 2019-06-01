var fileDir = location.protocol + '//' + location.host + '/BookStoreVictim/resources/app/temp/';

(function() {
	$("#fileTable").hide();
	$("#errorMessage").html("");
	$("#successMessage").html("");
	
	showAllFiles('');

	$("#createFileForm").submit(function(e) {
		$("#errorMessage").html("");
		$("#successMessage").html("");
		$('#fileTable tbody').empty();
		var form = $(this);
		var url = form.attr('action');
		var method = form.attr('method');
		$.ajax({
			type : method,
			url : url,
			data : form.serialize(),
			success : function(response) {
				if (response.status == "success") {
					var fileMap = response.data;
					for (var fileName in fileMap) {
						$("#fileTable tbody").append("<tr><td><a href='" 
								+ fileDir + encodeHtml(fileName) + "' target='_blank'>"
								+ encodeHtml(fileName) + "</a>"
								+ "</td><td>"
								+ encodeHtml(fileMap[fileName])
								+ "</td></tr>"
						);
					}
					$("#fileTable").show();
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
		e.preventDefault();
	});

})();

function showAllFiles() {
	$("#errorMessage").html("");
	$("#successMessage").html("");
	$("#fileTable").hide();
	$("#fileTable tbody").empty();

	$.ajax({
		type : 'POST',
		url : 'AppController',
		data : 'requestType=fetchAllFiles',
		success : function(response) {
			if (response.status == "success") {
				var fileMap = response.data;
				for (var fileName in fileMap) {
					$("#fileTable tbody").append("<tr><td><a href='" 
							+ fileDir + encodeHtml(fileName) + "' target='_blank'>"
							+ encodeHtml(fileName) + "</a>"
							+ "</td><td>"
							+ encodeHtml(fileMap[fileName])
							+ "</td></tr>"
					);
				}
				$("#fileTable").show();
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
