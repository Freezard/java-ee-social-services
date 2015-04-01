package entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

/*
 * Comment.java
 * 
 * A comment on a specific event, written by a user.
 * Each comment requires an ID, a user/event, 
 * the date when published and the text content.
 */
@Entity(name = "COMMENT") 
public class Comment implements Serializable {
	private static final long serialVersionUID = 227L;
	
	@Id //signifies the primary key
	@Column(name = "COMMENT_ID", nullable = false)
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long ID;
	
	@Column(name = "USER_ID", nullable = false)
	private long userID;
	
	@Column(name = "EVENT_ID", nullable = false)
	private long eventID;
	
	@Column(name = "COMMENT", nullable = false, length = 100)
	private String comment;
	
	@Column(name = "TIME", nullable = false)
	private Date time;
	
	@ManyToOne(cascade=CascadeType.PERSIST)
	@JoinColumn(name = "USER_ID", referencedColumnName = "USER_ID")
	private User user;
	
	@ManyToOne(cascade=CascadeType.PERSIST)
	@JoinColumn(name = "EVENT_ID", referencedColumnName = "EVENT_ID")
	private Event event;
	
	public Comment(int eventID, int userID, String comment, Date time) {
		this.userID = userID;
		this.eventID = eventID;
		this.comment = comment;
		this.time = time;
	}
	
	public Comment() {}
	
	@Override
	public String toString() {
	       StringBuffer sb = new StringBuffer();
	       sb.append("ID : " + ID);
	       sb.append("   userID : " + userID);
	       sb.append("   eventID : " + eventID);
	       sb.append("   comment : " + comment);
	       sb.append("   time : " + time);
	       return sb.toString();
	}
	
	public long getID() {
		return ID;
	}
	
	public void setID(long ID) {
		this.ID = ID;
	}
	
	public long getUserID() {
		return userID;
	}
	
	public void setUserID(long userID) {
		this.userID = userID;
	}
	
	public long getEventID() {
		return eventID;
	}
	
	public void setEventID(long eventID) {
		this.eventID = eventID;
	}
	
	public String getComment() {
		return comment;
	}
	
	public void setComment(String comment) {
		this.comment = comment;
	}
	
	public Date getTime() {
		return time;
	}
	
	public void setTime(Date time) {
		this.time = time;
	}
	
	public User getUser() {
		return user;
	}
	
	public void setUser(User user) {
		this.user = user;
	}
	
	public Event getEvent() {
		return event;
	}
	
	public void setEvent(Event event) {
		this.event = event;
	}
}
