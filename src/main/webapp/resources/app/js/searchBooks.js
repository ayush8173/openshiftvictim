(function() {
	$("#bookTable").hide();
	$("#errorMessage").html("");
	$("#successMessage").html("");

	$("#searchBooksForm").submit(function(e) {
		$("#errorMessage").html("");
		$("#successMessage").html("");
		$("#bookTable").hide();
		$("#bookTable tbody").empty();

		var form = $(this);
		var url = form.attr('action');
		var method = form.attr('method');
		$.ajax({
			type : method,
			url : url,
			data : form.serialize(),
			success : function(response) {
				if (response.status == "success") {
					var bookList = response.data;
					for (var i = 0; i < bookList.length; i++) {
						$("#bookTable tbody").append("<tr><td>" 
								+ encodeHtml(bookList[i].bookId)
								+ "</td><td><a href='#' onclick='viewBook(\""
								+ encodeHtml(bookList[i].bookId) + "\")'>"
								+ encodeHtml(bookList[i].bookTitle) + "</a>"
								+ "</td><td>"
								+ encodeHtml(bookList[i].bookAuthor)
								+ "</td><td>₹ "
								+ encodeHtml(bookList[i].bookPrice)
								+ " /-</td></tr>"
						);
					}
					$("#bookTable").show();
				} else {
					$("#errorMessage").html(encodeHtml(response.data));
				}
			},
			error : function(xhr) {
				if(xhr.status == 401) {
					if((location.hostname).endsWith("openshiftapps.com")) {
						location.href = location.protocol + '//' + location.host;
					} else {
						location.href = location.protocol + '//' + location.host + '/ROOT';
					}
				}
			}
		});
		e.preventDefault();
	});

	$("#buyReturnForm").submit(function(e) {
		var form = $(this);
		var url = form.attr('action');
		var method = form.attr('method');
		$.ajax({
			type : method,
			url : url,
			data : form.serialize(),
			success : function(response) {
				if (response.status == "success") {
					var book = response.data;
					
					$("#bookIdBuyReturn").val(encodeHtml(book.bookId));
					if('Y' != book.hasBook) {
						$("#requestType").val("buyBook");
						$("#buyReturnButton").text("Buy This Book");
					} else {
						$("#requestType").val("returnBook");
						$("#buyReturnButton").text("Return This Book");
					}
				} else {
					alert(response.data);
				}
			},
			error : function(xhr) {
				if(xhr.status == 401) {
					if((location.hostname).endsWith("openshiftapps.com")) {
						location.href = location.protocol + '//' + location.host;
					} else {
						location.href = location.protocol + '//' + location.host + '/ROOT';
					}
				}
			}
		});
		e.preventDefault();
	});
	
	$("#commentForm").submit(function(e) {
		var form = $(this);
		var url = form.attr('action');
		var method = form.attr('method');
		$.ajax({
			type : method,
			url : url,
			data : form.serialize(),
			success : function(response) {
				$("#bookComment").val("");
				$("#bookComments").html("");
				
				if (response.status == "success") {
					var book = response.data;
					
					$("#bookIdComment").val(encodeHtml(book.bookId));
					var bookComments = book.bookComment;
					for (var i = 0; i < bookComments.length; i++) {
						for (var i = 0; i < bookComments.length; i++) {
							$("#bookComments").append("<div class='panel panel-default panel-footer'>["
									+ encodeHtml(bookComments[i].commentDate) + "] "
									+ encodeHtml(bookComments[i].commentor) + " - "
									+ encodeHtml(bookComments[i].comment)
									+ "</div>"
							);
						}
					}
				}
			},
			error : function(xhr) {
				if(xhr.status == 401) {
					if((location.hostname).endsWith("openshiftapps.com")) {
						location.href = location.protocol + '//' + location.host;
					} else {
						location.href = location.protocol + '//' + location.host + '/ROOT';
					}
				}
			}
		});
		e.preventDefault();
	});

})();

function sortBooks(sortBy) {
	$("#bookSortParam").val(sortBy);
	$("#searchBookButton").click();
	$("#bookSortParam").val("");
}

function viewBook(bookId) {
	$("#bookDetailsTable").empty();
	$("#bookComments").html("");
	
	$.ajax({
		type : 'POST',
		url : 'AppController',
		data : 'requestType=displayBook&bookId=' + bookId,
		success : function(response) {
			if (response.status == "success") {
				var book = response.data;
				$("#bookDetailsHeader").html("Book Details (" + book.bookId + ")");
				$("#bookImage").attr("src","data:image/jpg;base64," + book.bookImage);
				
				$("#bookDetailsTable").append("<tr><th>Title</th><td>"
						+ encodeHtml(book.bookTitle) + "</td></tr>"
						+ "<tr><th>Author</th><td>"
						+ encodeHtml(book.bookAuthor) + "</td></tr>"
						+ "<tr><th>Price</th><td>₹ "
						+ encodeHtml(book.bookPrice) + " /-</td></tr>"
				);
				
				$("#bookIdBuyReturn").val(encodeHtml(book.bookId));
				$("#bookPriceBuyReturn").val(encodeHtml(book.bookPrice));
				if('Y' != book.hasBook) {
					$("#requestType").val("buyBook");
					$("#buyReturnButton").text("Buy This Book");
				} else {
					$("#requestType").val("returnBook");
					$("#buyReturnButton").text("Return This Book");
				}
				
				$("#bookIdComment").val(encodeHtml(book.bookId));
				var bookComments = book.bookComment;
				for (var i = 0; i < bookComments.length; i++) {
					$("#bookComments").append("<div class='panel panel-default panel-footer'>["
							+ encodeHtml(bookComments[i].commentDate) + "] "
							+ encodeHtml(bookComments[i].commentor) + " - "
							+ encodeHtml(bookComments[i].comment)
							+ "</div>"
					);
				}
				$("#bookDetailsModalButton").click();
			}
		},
		error : function(xhr) {
			if(xhr.status == 401) {
				if((location.hostname).endsWith("openshiftapps.com")) {
					location.href = location.protocol + '//' + location.host;
				} else {
					location.href = location.protocol + '//' + location.host + '/ROOT';
				}
			}
		}
	});
}