package DAO;

import java.rmi.RemoteException;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.UserTransaction;

import test.GoogleCalendarService;

import entity.Comment;
import entity.Event;
import entity.User;

/**
 * SocialEventService.java
 * 
 * Implementation of the ISocialEventService interface. Used as a DAO object for
 * clients to interact with the social event service on the server.
 * 
 */
@Stateless
@javax.ejb.ApplicationException(rollback = true)
@TransactionManagement(TransactionManagementType.BEAN)
@Remote
public class SocialEventService implements ISocialEventService {
	@Resource
	private UserTransaction utx;

	@PersistenceContext(unitName = "Assignment")
	private EntityManager em;
	
	private GoogleCalendarService calendarService;
	
	@PostConstruct
	private void init() {
		calendarService = GoogleCalendarService.getInstance();
	}
	
	@Override
	public Event addEvent(String name, String city, Date start, Date end,
			String description) {
		Event event = null;
		try {
			utx.begin();
			event = new Event(name, city, start, end, description);
			em.persist(event);
			utx.commit();
			// Add calendar event
			calendarService.AddCalendarEvent(event);
		} catch (Exception e) {
			e.printStackTrace();
			try {
				utx.rollback();
				calendarService.RemoveCalendarEvent(event);
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		return event;
	}

	@Override
	public void editEvent(int eventID, String name, String city, Date start,
			Date end, String description) {
		Event event = em.find(Event.class, eventID);
		event.setTitle(name);
		event.setCity(city);
		event.setStart(start);
		event.setEnd(end);
		event.setContent(description);
	}

	@Override
	public void removeEvent(int eventID) {
		try {
			utx.begin();
			Event event = em.find(Event.class, eventID);
			em.remove(event);
			utx.commit();
			// Remove calendar event
			calendarService.RemoveCalendarEvent(event);
		} catch (Exception e) {
			e.printStackTrace();
			try {
				utx.rollback();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
	}

	@Override
	public List<Event> getAllEvents(int userID) {
		User user = em.find(User.class, userID);
		return user.getEventList();
	}

	@Override
	public User addUser(String firstName, String lastName) {
		User user = null;
		try {
			utx.begin();
			user = new User(firstName, lastName);
			em.persist(user);
			utx.commit();
		} catch (Exception e) {
			e.printStackTrace();
			try {
				utx.rollback();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		return user;
	}

	@Override
	public void editUser(int userID, String firstName, String lastName) {
		User user = em.find(User.class, userID);
		user.setFirstName(firstName);
		user.setLastName(lastName);
	}

	@Override
	public void removeUser(int userID) throws RemoteException {
		User user = em.find(User.class, userID);
		em.remove(user);
	}

	@Override
	public User getUser(int userID) throws RemoteException {
		User user = em.find(User.class, userID);
		return user;
	}

	@Override
	public void setImage(int userID, byte[] image) {		
		try {
			utx.begin();
			User user = em.find(User.class, userID);
			user.setImage(image);
			utx.commit();
		} catch (Exception e) {
			e.printStackTrace();
			try {
				utx.rollback();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
	}
	
	@Override
	public Comment addComment(int eventID, int userID, String comment, Date time) {
		Event event = em.find(Event.class, eventID);
		User user = em.find(User.class, userID);

		Comment c = new Comment(eventID, userID, comment, time);
		event.addComment(c);
		user.addComment(c);
		c.setUser(user);
		return c;
	}

	@Override
	public void editComment(int commentID, String comment, Date time) {
		Comment c = em.find(Comment.class, commentID);
		c.setComment(comment);
		c.setTime(time);
	}

	@Override
	public void removeComment(int commentID) {
		Comment comment = em.find(Comment.class, commentID);
		em.remove(comment);
	}

	@Override
	public List<Comment> getAllComments(int userID) {
		User user = em.find(User.class, userID);
		
		return user.getComments();
	}

	@Override
	public void addOrganizer(int eventID, int userID) {	
		try {
			utx.begin();
			Event event = em.find(Event.class, eventID);
			User user = em.find(User.class, userID);
			event.addUser(user);
			user.addEvent(event);
			utx.commit();
		} catch (Exception e) {
			e.printStackTrace();
			try {
				utx.rollback();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
	}

	@Override
	public void removeOrganizer(int eventID, int userID) {
		Event event = em.find(Event.class, eventID);
		User user = em.find(User.class, userID);

		event.getUserList().remove(user);
	}
}