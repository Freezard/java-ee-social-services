<%@ page import="java.util.*" import="java.io.*" import="java.awt.Toolkit" import ="java.awt.Image" import="entity.*" language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>User profile</title>
</head>
<body>

<%
User user = (User) request.getAttribute("user");
@SuppressWarnings("unchecked")
List<Event> userFutureEvents = (List<Event>) request.getAttribute("userFutureEvents");
@SuppressWarnings("unchecked")
List<Event> userPastEvents = (List<Event>) request.getAttribute("userPastEvents");

if (userFutureEvents != null && userPastEvents != null && user != null) {
	int commentsNr = 0;
	if (user.getComments() != null)
		commentsNr = user.getComments().size(); %>
	<table>
	<tr><td>First name</td><td>Last name</td><td>Future events</td><td>Past events</td><td>Comments</td></tr>
	<tr><td>${user.firstName}</td><td>${user.lastName}</td>
	<td><%= userFutureEvents.size() %></td>
	<td><%= userPastEvents.size() %></td>
	<td><%= commentsNr %></td></tr>
	</table>
<% }

%>

<img src="ViewImage?user=<%= user.getID() %>"/><br/><br/>

<form action='Controller'>
	<input type="submit" value="Back">
</form>
</body>
</html>
