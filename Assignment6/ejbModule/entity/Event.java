package entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;

/*
 * Event.java
 * 
 * A social event. Each event requires an ID, a title,
 * city location, start/ending dates and a description.
 * 
 * An event may have comments written by users, and must
 * also state who the arrangers of the event are.
 */
@Entity(name = "EVENT") 
public class Event implements Serializable {
	private static final long serialVersionUID = 227L;
	
	@Id //signifies the primary key
	@Column(name = "EVENT_ID", nullable = false)
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long ID;
	
	@Column(name = "TITLE", nullable = false, length = 50)
	private String title;
	
	@Column(name = "CITY", nullable = false, length = 50)
	private String city;
	
	@Column(name = "START_TIME", nullable = false)
	private Date start;
	
	@Column(name = "END_TIME", nullable = false)
	private Date end;
	
	@Column(name = "CONTENT", nullable = false, length = 100)
	private String content;	
	
	@OneToMany(mappedBy = "event", targetEntity = Comment.class, cascade=CascadeType.PERSIST)
	private List<Comment> comments;
	
	@ManyToMany(fetch = FetchType.EAGER, cascade=CascadeType.PERSIST)
	@JoinTable(name="ORGANIZER",
			joinColumns=
	            @JoinColumn(name = "EVENT_ID", referencedColumnName = "EVENT_ID"),
	        inverseJoinColumns=
	            @JoinColumn(name = "USER_ID", referencedColumnName = "USER_ID")
	)
	private List<User> userList;
	
	public Event(String title, String city, Date start, Date end, String content) {
		this.title = title;
		this.city = city;
		this.start = start;
		this.end = end;
		this.content = content;
				
		comments = new ArrayList<Comment>();
		userList = new ArrayList<User>();
	}
	
	public Event() {}
	
	@Override
	public String toString() {
	       StringBuffer sb = new StringBuffer();
	       sb.append("ID : " + ID);
	       sb.append("   title : " + title);
	       sb.append("   city : " + city);
	       sb.append("   start : " + start);
	       sb.append("   end : " + end);
	       sb.append("   content : " + content);
	//      sb.append("   comments : " + comments.toString());
	//       sb.append("   organizers : " + userList.toString());
	       return sb.toString();
	}
	
	public long getID() {
		return ID;
	}
	
	public void setID(long ID) {
		this.ID = ID;
	}
	
	public String getTitle() {
		return title;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getCity() {
		return city;
	}
	
	public void setCity(String city) {
		this.city = city;
	}
	
	public Date getStart() {
		return start;
	}
	
	public void setStart(Date start) {
		this.start = start;
	}
	
	public Date getEnd() {
		return end;
	}
	
	public void setEnd(Date end) {
		this.end = end;
	}
	
	public String getContent() {
		return content;
	}
	
	public void setContent(String content) {
		this.content = content;
	}
	
	public List<Comment> getComments() {
		return comments;
	}

	public void addComment(Comment comment) {
		comments.add(comment);
	}
	
	public List<User> getUserList() {
		return userList;
	}

	public void addUser(User user) {
		userList.add(user);
	}
}
