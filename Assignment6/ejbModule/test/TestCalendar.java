package test;

import com.google.gdata.client.calendar.*;
import com.google.gdata.data.*;
import com.google.gdata.data.calendar.*;
import com.google.gdata.data.extensions.*;
import com.google.gdata.util.*;

import entity.Event;

import java.net.*;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import java.io.*;

public class TestCalendar {
    public static void main(String[] args) throws IOException, ServiceException {    	
        CalendarService myService = new CalendarService("exampleCo-exampleApp-1.0");
        myService.setUserCredentials("mama1004test@gmail.com", "superdupertest");
        
        Calendar start = new GregorianCalendar(2012, 11, 20, 8, 0);
		Calendar end = new GregorianCalendar(2012, 11, 20, 16, 0);

        Event e1 = new Event("Cleaning", "Gothenburg", new Date(start.getTimeInMillis()),
        		new Date(end.getTimeInMillis()), "Come and join us cleaning the streets!");
        
     //   AddCalendarEvent(e1, myService);
        
        URL feedUrl = new URL("http://www.google.com/calendar/feeds/default/allcalendars/full");
        CalendarFeed resultFeed = myService.getFeed(feedUrl, CalendarFeed.class);

        System.out.println("Your calendars:");
        System.out.println();

        for (int i = 0; i < resultFeed.getEntries().size(); i++) {
          CalendarEntry entry = resultFeed.getEntries().get(i);          
          System.out.println("\t" + entry.getTitle().getPlainText());
          System.out.println("\t" + entry.getId());
        }
    }
    
    static CalendarEntry CreateCalendar(String title, CalendarService myService) throws IOException, ServiceException {
        // Create the calendar
        CalendarEntry calendar = new CalendarEntry();
        calendar.setTitle(new PlainTextConstruct(title));
        calendar.setTimeZone(new TimeZoneProperty("Europe/Stockholm"));
        calendar.setHidden(HiddenProperty.FALSE);
        calendar.setColor(new ColorProperty("#2952A3"));
        calendar.addLocation(new Where("","",title));

        // Insert the calendar
        URL postUrl = new URL("https://www.google.com/calendar/feeds/default/owncalendars/full");
        CalendarEntry returnedCalendar = myService.insert(postUrl, calendar);
        return returnedCalendar;
    }
    
    static CalendarEntry GetCalendar(String title, CalendarService myService) throws IOException, ServiceException {
    	URL feedUrl = new URL("https://www.google.com/calendar/feeds/default/owncalendars/full");
    	CalendarFeed resultFeed = myService.getFeed(feedUrl, CalendarFeed.class);
    	    	
    	for (int i = 0; i < resultFeed.getEntries().size(); i++) {
    		CalendarEntry entry = resultFeed.getEntries().get(i);    		
    		if (entry.getTitle().getPlainText().equals(title))
    			return entry;
    	}    	
    	return null;
    }
    
    static void AddCalendarEvent(Event event, CalendarService myService) throws IOException, ServiceException {
    	CalendarEntry entry = GetCalendar(event.getCity(), myService);
    	
    	if (entry == null)
    		entry = CreateCalendar(event.getCity(), myService);
    	
    	String[] url = entry.getId().split("default/calendars/");    	
    	URL postURL = new URL(url[0]+url[1]+"/private/full");
    	CalendarEventEntry myEvent = new CalendarEventEntry();

    	//Set the title and description
    	myEvent.setTitle(new PlainTextConstruct(event.getTitle()));
    	myEvent.setContent(new PlainTextConstruct(event.getContent()));

    	//Create DateTime events and create a When object to hold them, then add
    	//the When event to the event    	
    	DateTime startTime = new DateTime(event.getStart(), TimeZone.getDefault());
    	DateTime endTime = new DateTime(event.getEnd(), TimeZone.getDefault());
    	When eventTimes = new When();
    	eventTimes.setStartTime(startTime);
    	eventTimes.setEndTime(endTime);
    	myEvent.addTime(eventTimes);

    	// POST the request and receive the response:
    	CalendarEventEntry insertedEntry = myService.insert(postURL, myEvent);
    }
    
    static boolean DeleteCalendarEvent(Event event, CalendarService myService) throws IOException, ServiceException {
    	CalendarEntry entry = GetCalendar(event.getCity(), myService);
    	
    	if (entry == null)
    		return false;
    	
    	String[] url = entry.getId().split("default/calendars/");    	
    	URL feedURL = new URL(url[0]+url[1]+"/private/full");    	
    	CalendarEventFeed myFeed = myService.getFeed(feedURL, CalendarEventFeed.class);
    	
    	for (CalendarEventEntry calendarEvent : myFeed.getEntries())
    		if (calendarEvent.getTitle().getPlainText().equals(event.getTitle())) {    			
    			calendarEvent.delete();
    			return true;
    		}
    	
    	return false;
    }
}