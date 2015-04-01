package DAO;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Date;
import java.util.List;

import entity.Comment;
import entity.Event;
import entity.User;

public interface ISocialEventService extends Remote {
	public Event addEvent(String name, String city, Date start, Date end, String description) throws RemoteException;
	public void editEvent(int eventID, String name, String city, Date start, Date end, String description) throws RemoteException;
	public void removeEvent(int eventID) throws RemoteException;
	public List<Event> getAllEvents(int userID) throws RemoteException;
	
	public User addUser(String firstName, String lastName) throws RemoteException;
	public void editUser(int userID, String firstName, String lastName)throws RemoteException;
	public void removeUser(int userID) throws RemoteException;	
	public User getUser(int userID) throws RemoteException;
	public void setImage(int userID, byte[] image) throws RemoteException;
	
	public Comment addComment(int eventID, int userID, String comment, Date time) throws RemoteException;
	public void editComment(int commentID, String comment, Date time) throws RemoteException;
	public void removeComment(int commentID) throws RemoteException;
	public List<Comment> getAllComments(int userID) throws RemoteException;
	
	public void addOrganizer(int eventID, int userID) throws RemoteException;
	public void removeOrganizer(int eventID, int userID) throws RemoteException;
}
