package servlets;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.rmi.RemoteException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.owasp.esapi.ESAPI;

import test.GoogleCalendarService;

import com.google.gdata.data.calendar.CalendarEntry;
import com.google.gdata.util.ServiceException;

import DAO.ISocialEventService;
import DAO.IUserSecretService;

import entity.Event;
import entity.User;
import exceptions.TooManyFailedTriesException;

/**
 * Controller.java
 * 
 * Servlet controller for the website.
 *
 */

public class Controller extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static String NEW_EVENT_JSP = "/NewEvent.jsp";
	private static String USER_PROFILE_JSP = "/UserProfile.jsp";
	private static String EVENTS_OVERVIEW_JSP = "/EventsOverview.jsp";

	@EJB
	private ISocialEventService service;
	@EJB
	private IUserSecretService userSecretService;
	private List<Event> eventList;
	private List<User> userList;	
	private GoogleCalendarService calendarService;
	
	public void init() {
		eventList = new ArrayList<Event>();
		userList = new ArrayList<User>();
		calendarService = GoogleCalendarService.getInstance();
		
		// Test data. Three events and one user.
		try {
			fillData();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String forward = "";
		// Get a map of the request parameters.
		@SuppressWarnings("unchecked")
		Map<String, String[]> parameters = request.getParameterMap();		
		
		// ******* TESTING UserSecretService.java *******
		/*
		userSecretService.resetSecret("hey", "eva");
		boolean authenticated = false;
		try {
			authenticated = userSecretService.checkSecret("hey", "eva");
			System.out.println(authenticated);
			for (int i = 0; i < 4; i++) {
				authenticated = userSecretService.checkSecret("hey", "adam");
				System.out.println(authenticated);
			}
		} catch (TooManyFailedTriesException e1) {
			e1.printStackTrace();
		}		
		*/
		// ******* TESTING UserSecretService.java *******
		
		if (parameters.containsKey("newEvent")){
			// A user has added a new event. Add the event to the list,
			// set the organizer and forward to events overview.
			if (request.getParameter("newEvent").equals("Submit")) {
				// First check if valid inputs. If not valid then redirect to new event.
				if (ESAPI.validator().isValidInput("Title", request.getParameter("title"), "Address", 30, false)
						&& ESAPI.validator().isValidInput("City", request.getParameter("city"), "Address", 30, false)
						&& ESAPI.validator().isValidDate("Start", request.getParameter("start"), new SimpleDateFormat("yyyy-MM-dd"), false)
						&& ESAPI.validator().isValidDate("End", request.getParameter("end"), new SimpleDateFormat("yyyy-MM-dd"), false)
						&& ESAPI.validator().isValidInput("Content", request.getParameter("content"), "SafeString", 255, false)) {
				
					forward = EVENTS_OVERVIEW_JSP;
					
					try {
						Event e = service.addEvent(request.getParameter("title"), 
						request.getParameter("city"), 
						new SimpleDateFormat("yyyy-MM-dd").parse(request.getParameter("start")), 
						new SimpleDateFormat("yyyy-MM-dd").parse(request.getParameter("end")), 
						request.getParameter("content"));
						User user = service.getUser(Integer.valueOf(request.getParameter("organizer")));
						
						service.addOrganizer((int) e.getID(), (int) user.getID());
						e.addUser(user);
						user.addEvent(e);			
						eventList.add(e);
					} catch (ParseException e1) {
						e1.printStackTrace();
					}
				
					request.setAttribute("eventList", eventList);
				}
				else {
					forward = NEW_EVENT_JSP;
					
					request.setAttribute("users", userList);
				}
			}
			// New event button clicked. Forward to the JSP page with
			// a list of all users (used in the organizer drop-down list).
			else {
				forward = NEW_EVENT_JSP;
			
				request.setAttribute("users", userList);
			}
		} else if (parameters.containsKey("userProfile")){
			// Forward to a user profile.
			forward = USER_PROFILE_JSP;
			
			// Attach the user and its future/past event lists.
			// Check for valid user ID.
			String userID = request.getParameter("userProfile");			
			if (userID != null
			     && ESAPI.validator().isValidInteger("User ID", userID, 1, Integer.MAX_VALUE, false)) {
				User user = service.getUser(Integer.valueOf(userID));
				List<Event> userEventList = user.getEventList();
				List<Event> userFutureEvents = new ArrayList<Event>();
				List<Event> userPastEvents = new ArrayList<Event>();
				
				// User's event list is split up in future/past events
				for (int i = 0; i < userEventList.size(); i++)
					if (userEventList.get(i).getStart().compareTo(new Date()) > 0)
						userFutureEvents.add(userEventList.get(i));
					else userPastEvents.add(userEventList.get(i));		
				
				request.setAttribute("userFutureEvents", userFutureEvents);
				request.setAttribute("userPastEvents", userPastEvents);
				request.setAttribute("user", user);
			}
			else forward = EVENTS_OVERVIEW_JSP;
		} else {
			// Forward to events overview.
			forward = EVENTS_OVERVIEW_JSP;			
						
			// If the search field was used (and not empty),
			// filter the event list by the specified city
			// and attach this list.
			String city = request.getParameter("eventCity");			
			if (city != null && !city.equals("") 
			 && ESAPI.validator().isValidInput("Event City", city, "Address", 30, false)) {
				List<Event> temp = new ArrayList<Event>();
				for (int i = 0;i < eventList.size();i++)
					if (eventList.get(i).getCity().equals(city))
						temp.add(eventList.get(i));
				request.setAttribute("eventList", temp);
				
				// Get the Google calendar for this city and send the embed URL to the view.
				try {
					CalendarEntry calendar = calendarService.GetCalendar(city);
					
					if (calendar != null) {
						String calendarURL = calendar.getId().split(("http://www.google.com/calendar/feeds/default/calendars/"))[1];
						request.setAttribute("calendarURL", calendarURL);
					}
				} catch (ServiceException e) {
					e.printStackTrace();
				}
			}
			// Else attach the list of all events.
			else request.setAttribute("eventList", eventList);
		}
		
		RequestDispatcher view = request.getRequestDispatcher(forward);
		view.forward(request, response);
	}
	
	// Creates three events and one user.
	private void fillData() throws RemoteException {
		User user = service.addUser("Adam", "Allsing");
		userList.add(user);

		try {
			String path = this.getServletContext().getRealPath("images/profil.jpg");
			File file = new File(path);
			byte[] buf = new byte[1024];
			InputStream input = new FileInputStream(file);
			ByteArrayOutputStream output = new ByteArrayOutputStream();
			 for (int readNum; (readNum = input.read(buf)) != -1;)
		            output.write(buf, 0, readNum);
			 byte[] bytes = output.toByteArray();
			 service.setImage((int) user.getID(), bytes);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
        Calendar start = new GregorianCalendar(2011, 11, 20, 8, 0);
		Calendar end = new GregorianCalendar(2011, 11, 20, 16, 0);
		
        Event e1 = service.addEvent("Cleaning", "Gothenburg", new Date(start.getTimeInMillis()),
        		new Date(end.getTimeInMillis()), "Come and join us cleaning the streets!");
        service.addOrganizer((int) e1.getID(), (int) user.getID());

		e1.addUser(user);
		user.addEvent(e1);
        
		start.set(2011, 12, 20, 13, 0);
		end.set(2011, 12, 20, 15, 0);
		
		Event e2 = service.addEvent("Charity", "Lund", new Date(start.getTimeInMillis()),
				new Date(end.getTimeInMillis()), "Let's play music to gather money!");
		
		Event e3 = service.addEvent("Charity", "Lund", new Date(start.getTimeInMillis()),
				new Date(end.getTimeInMillis()), "Let's play music to gather money!");

		eventList.add(e1);
		eventList.add(e2);
		eventList.add(e3);
	}
}