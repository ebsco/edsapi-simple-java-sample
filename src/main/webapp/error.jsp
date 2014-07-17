<html>
<head>
<%@page import="com.eds.bean.*" language="java"%>
<link rel="stylesheet" href="css/style.css" type="text/css"
	media="screen" />

	<% 
		ApiErrorMessage apiErrorMessage = (ApiErrorMessage)request.getSession().getAttribute("errorMessage");
		String errorCode = (null == apiErrorMessage) ? "" : apiErrorMessage.getErrorNumber();
		if(null == errorCode)
			errorCode = "";
		String description = (null == apiErrorMessage) ? "" : apiErrorMessage.getErrorDescription();
		if(null == description)
			description = "";
		String detailedDescription = (null == apiErrorMessage) ? "" : apiErrorMessage.getDetailedErrorDescription();
		if(null == detailedDescription)
			detailedDescription = "";
	%>
</head>
<body>
	<div >
			<span>
				An Error Occurred
				<br />
				Error Number: <%out.println(errorCode);%>
				<br />
				Error Description: <%out.println(description);%>
				<br />
				Detailed Error Description: <%out.println(detailedDescription);%>
			</span>
	</div>
</body>
</html>