<%@ page import="java.util.*" import="entity.*" language="java"
	contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>New event</title>
</head>
<body>

<%
@SuppressWarnings("unchecked")
List<User> users = (List<User>) request.getAttribute("users");
%>

<form method="GET" action='Controller'>
	<p>
	Title: <input type='text' name='title'/>
	</p>
	<p>
	City: <input type='text' name='city'/>
	</p>
	<p>
	Start: <input type='text' name='start'/> (YYYY-MM-DD)
	</p>
	<p>
	End: <input type='text' name='end'/> (YYYY-MM-DD)
	</p>
  	<p>
  	Content:<br/>
  	<textarea name="content" rows="7" cols="30"></textarea>
  	</p>
	<p>
	Organizer:
	</p>
	<p>
	<select name="organizer">
	<%
	for (User user : users) { %>
		<option value=<%= user.getID() %>><%= user.getFirstName() %> <%= user.getLastName() %></option>
	<% } %>
	</select>
	</p>
   <p>
   <input type='submit' name='newEvent' value='Submit'/>
   </p>
</form>

</body>
</html>