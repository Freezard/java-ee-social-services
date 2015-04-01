package test;


import java.io.IOException;
import java.net.URL;
import java.util.TimeZone;

import com.google.gdata.client.calendar.CalendarService;
import com.google.gdata.data.DateTime;
import com.google.gdata.data.PlainTextConstruct;
import com.google.gdata.data.calendar.CalendarEntry;
import com.google.gdata.data.calendar.CalendarEventEntry;
import com.google.gdata.data.calendar.CalendarEventFeed;
import com.google.gdata.data.calendar.CalendarFeed;
import com.google.gdata.data.calendar.ColorProperty;
import com.google.gdata.data.calendar.HiddenProperty;
import com.google.gdata.data.calendar.TimeZoneProperty;
import com.google.gdata.data.extensions.When;
import com.google.gdata.data.extensions.Where;
import com.google.gdata.util.AuthenticationException;
import com.google.gdata.util.ServiceException;

import entity.Event;

public class GoogleCalendarService {
	private static final GoogleCalendarService instance = new GoogleCalendarService();
	private CalendarService myService;
	
	private GoogleCalendarService() {		
		try {
			myService = new CalendarService("Assignment");
			myService.setUserCredentials("mama1004test@gmail.com", "superdupertest");
		} catch (AuthenticationException e) {
			e.printStackTrace();
		}
	}

	public static GoogleCalendarService getInstance() {
		return instance;
	}
	
	public CalendarEntry CreateCalendar(String title)
			throws IOException, ServiceException {	
		// Create the calendar
		CalendarEntry calendar = new CalendarEntry();
		calendar.setTitle(new PlainTextConstruct(title));
		calendar.setTimeZone(new TimeZoneProperty("Europe/Stockholm"));
		calendar.setHidden(HiddenProperty.FALSE);
		calendar.setColor(new ColorProperty("#2952A3"));
		calendar.addLocation(new Where("", "", title));

		// Insert the calendar
		URL postUrl = new URL(
				"https://www.google.com/calendar/feeds/default/owncalendars/full");
		CalendarEntry returnedCalendar = myService.insert(postUrl, calendar);
		return returnedCalendar;
	}

	public CalendarEntry GetCalendar(String title)
			throws IOException, ServiceException {
		URL feedUrl = new URL(
				"https://www.google.com/calendar/feeds/default/owncalendars/full");
		CalendarFeed resultFeed = myService
				.getFeed(feedUrl, CalendarFeed.class);

		for (int i = 0; i < resultFeed.getEntries().size(); i++) {
			CalendarEntry entry = resultFeed.getEntries().get(i);
			if (entry.getTitle().getPlainText().equals(title))
				return entry;
		}
		return null;
	}

	public void AddCalendarEvent(Event event)
			throws IOException, ServiceException {
		CalendarEntry entry = GetCalendar(event.getCity());

		if (entry == null)
			entry = CreateCalendar(event.getCity());

		String[] url = entry.getId().split("default/calendars/");
		URL postURL = new URL(url[0] + url[1] + "/private/full");
		CalendarEventEntry myEvent = new CalendarEventEntry();

		// Set the title and description
		myEvent.setTitle(new PlainTextConstruct(event.getTitle()));
		myEvent.setContent(new PlainTextConstruct(event.getContent()));

		// Create DateTime events and create a When object to hold them, then
		// add the When event to the event
		DateTime startTime = new DateTime(event.getStart(),
				TimeZone.getDefault());
		DateTime endTime = new DateTime(event.getEnd(), TimeZone.getDefault());
		When eventTimes = new When();
		eventTimes.setStartTime(startTime);
		eventTimes.setEndTime(endTime);
		myEvent.addTime(eventTimes);

		// POST the request and receive the response:
		CalendarEventEntry insertedEntry = myService.insert(postURL, myEvent);
	}

	public boolean RemoveCalendarEvent(Event event)
			throws IOException, ServiceException {
		CalendarEntry entry = GetCalendar(event.getCity());

		if (entry == null)
			return false;

		String[] url = entry.getId().split("default/calendars/");
		URL feedURL = new URL(url[0] + url[1] + "/private/full");
		CalendarEventFeed myFeed = myService.getFeed(feedURL,
				CalendarEventFeed.class);

		for (CalendarEventEntry calendarEvent : myFeed.getEntries())
			if (calendarEvent.getTitle().getPlainText().equals(event.getTitle())) {
				calendarEvent.delete();
				if (myFeed.getEntries().size() == 0)
					entry.delete();
				return true;
			}

		return false;
	}
}
