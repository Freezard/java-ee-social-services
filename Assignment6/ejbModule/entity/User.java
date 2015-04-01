package entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;

/*
 * User.java
 * 
 * A user with a first and last name plus a unique ID.
 * May have associated events that the user is arranging,
 * as well as any comments written by this user.
 */
@Entity(name = "USERS")
public class User implements Serializable {
	private static final long serialVersionUID = 227L;
	
	@Id //signifies the primary key
	@Column(name = "USER_ID", nullable = false)
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long ID;
	
	@Column(name = "FIRST_NAME", nullable = false, length = 50)
	private String firstName;
	
	@Column(name = "LAST_NAME", nullable = false, length = 50)
	private String lastName;
	
	@OneToMany(mappedBy = "user", targetEntity = Comment.class, cascade = CascadeType.PERSIST)
	private List<Comment> comments;
	
	@ManyToMany(fetch = FetchType.EAGER, mappedBy = "userList", cascade = CascadeType.PERSIST)
	private List<Event> eventList;
	
	@Lob @Basic()
	@Column(name="IMAGE") // columnDefinition="BLOB NOT NULL"
	private byte[] image;
	
	public User(String firstName, String lastName) {
		this.firstName = firstName;
		this.lastName = lastName;
		
		comments = new ArrayList<Comment>();
		eventList = new ArrayList<Event>();		
	}
	
	public User() {}
	
	@Override
	public String toString() {
	       StringBuffer sb = new StringBuffer();
	       sb.append("ID : " + ID);
	       sb.append("   firstName : " + firstName);
	       sb.append("   lastName : " + lastName);
	  //     sb.append("   comments : " + comments.toString());
	  //     sb.append("   events : " + eventList.toString());
	       return sb.toString();
	}
	
	public long getID() {
		return ID;
	}
	
	public void setID(long ID) {
		this.ID = ID;
	}
	
	public String getFirstName() {
		return firstName;
	}
	
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	
	public String getLastName() {
		return lastName;
	}
	
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	
	public List<Comment> getComments() {
		return comments;
	}

	public void addComment(Comment comment) {
		comments.add(comment);
	}
	
	public List<Event> getEventList() {
		return eventList;
	}

	public void addEvent(Event event) {
		eventList.add(event);
	}
	
	public byte[] getImage() {
		return image;
	}
	
	public void setImage(byte[] image) {
		this.image = image;
	}
}
