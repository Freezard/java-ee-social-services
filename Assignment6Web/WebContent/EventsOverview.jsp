<%@ page import="java.util.*" import="entity.*" import="org.owasp.esapi.ESAPI" language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Events Overview</title>
</head>
<body>

<form method="GET" action='Controller'>
<table>
	<tr>
		<td><input type="text" name="eventCity"/></td>
		<td><input type="submit" value="Search Event"/></td>
	</tr>
</table>
</form>

<%
String event = request.getParameter("event");
String city = ESAPI.encoder().encodeForURL(request.getParameter("eventCity"));

@SuppressWarnings("unchecked")
List<Event> eventList = (List<Event>) request.getAttribute("eventList");
if (eventList != null) {			
	Iterator<Event> it = eventList.iterator();
  	while (it.hasNext()) {
  		Event e = it.next(); %>
  		
		<table><tr><td>  				  			
		 <% if (city != null) { %> 				
  		 	<a href="Controller?event=<%= e.getID() %>&eventCity=<%= city %>"> <%= e.getTitle() %> </a>
  		 <% }
		 else { %>
  		 	<a href="Controller?event=<%= e.getID() %>"> <%= e.getTitle() %> </a>  				 
  		<% } %>
  		</td></tr></table>
  				
  		<% if (event != null)
				if (event.equals(Long.toString(e.getID()))) {
  					String info = e.getCity() + " " + e.getContent() + " " + e.getStart() + " " + e.getEnd();
  					out.print(info);  					
  					
  					List<User> userList = e.getUserList();
  					for (int i = 0; i < userList.size();i++) {
  						User u = userList.get(i); %>
  						<a href="Controller?userProfile=<%= u.getID() %>"> <%= u.getFirstName() %> <%= u.getLastName() %> </a>
  					<% }
				}
 	}
} 

String calendarURL = (String) request.getAttribute("calendarURL");

if (calendarURL != null) { %>
	<br/><br/>
	<iframe src="https://www.google.com/calendar/embed?src=<%= calendarURL %>" style="border: 0" width="300" height="300" frameborder="0" scrolling="no"></iframe>
<% } %>

<form action='Controller'>
<p>
   <input type="submit" name="newEvent" value="New event"/>
</p>
</form>

</body>
</html>
	