<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1">
<meta name="description" content="LoginPage">
<meta name="author" content="siddhant.singh@nagarro.com">

<title>Login</title>

<!-- Bootstrap core CSS -->
<link href="resources/lib/css/bootstrap.min.css" rel="stylesheet">

<!-- Custom styles for this template -->
<link href="resources/app/css/login.css" rel="stylesheet">

<!-- HTML5 shim and Respond.js for IE8 support of HTML5 elements and media queries -->
<!--[if lt IE 9]>
      <script src="https://oss.maxcdn.com/html5shiv/3.7.3/html5shiv.min.js"></script>
      <script src="https://oss.maxcdn.com/respond/1.4.2/respond.min.js"></script>
    <![endif]-->
</head>

<body class="bg-image">
	<%
		String errorMessage = (String) request.getAttribute("errorMessage");
		String logoutMessage = (String) request.getAttribute("logoutMessage");
	%>

	<div class="container">
		<div
			class="col-xs-6 col-sm-6 col-md-4 col-xs-offset-3 col-sm-offset-3 col-md-offset-4">

			<div class="panel panel-default form-panel panel-semi-transparent">
				<div class="panel-heading center">Book Store Victim</div>

				<div class="panel-body">
					<form class="form-horizontal" action="AppController" method="POST">

						<input type="hidden" name="requestType" value="forgotPassword" />

						<div class="form-group">
							<div class="col-md-10 col-md-offset-1">
								<input type="text" class="form-control textfield" id="username"
									name="username" placeholder="Enter Username" autocomplete="off" />
							</div>
						</div>

						<div class="form-group">
							<div class="col-md-10 col-md-offset-1">
								<select class="form-control" name="securityType">
									<option value="insecure">Insecure Reset</option>
									<option value="secure">Secure Reset</option>
								</select>
							</div>
						</div>

						<div class="form-group">
							<div class="col-md-10 col-md-offset-1">
								<button type="submit" class="btn btn-primary btn-block"
									id="submit" name="submit" value="submit">Forgot Password</button>
							</div>
						</div>

						<div class="form-group">
							<div class="center">
								<a id="loginLink" href="#">Go back to login!</a>
							</div>
						</div>
					</form>
				</div>
			</div>

			<%
				if (null != errorMessage && !"".equals(errorMessage)) {
			%>
			<div id="errorMessage" class="center error-message"><%=errorMessage%></div>
			<%
				}
			%>
			<%
				if (null != logoutMessage && !"".equals(logoutMessage)) {
			%>
			<div id="logoutMessage" class="center logout-message"><%=logoutMessage%></div>
			<%
				}
			%>

		</div>
	</div>

	<!-- Bootstrap core JavaScript - Placed at the end of the document so the pages load faster -->
	<script src="resources/lib/js/jquery.min.js"></script>
	<script src="resources/lib/js/bootstrap.min.js"></script>
	<script src="resources/app/js/forgotPassword.js"></script>
</body>
</html>