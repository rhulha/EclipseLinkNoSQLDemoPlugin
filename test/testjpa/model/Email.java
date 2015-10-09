package testjpa.model;

import static javax.persistence.GenerationType.IDENTITY;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.eclipse.persistence.nosql.annotations.DataFormatType;
import org.eclipse.persistence.nosql.annotations.Field;
import org.eclipse.persistence.nosql.annotations.NoSql;

@Entity
@NoSql(dataFormat = DataFormatType.MAPPED)
public class Email {

	@Id
	//@ReturnInsert
    @GeneratedValue(strategy = IDENTITY)
	@Field(name = "id")
	private Long id;

	@Field(name = "sender")
	private String sender;

	@Field(name = "subject")
	private String subject;
	@Field(name = "textpreview")
	private String textpreview;
	@Field(name = "hash")
	private String hash;
	@Field(name = "tags")
	private String tags;

	@Temporal(TemporalType.TIMESTAMP)
	@Field(name = "archived")
	private Date archived;
	@Temporal(TemporalType.TIMESTAMP)
	@Field(name = "received")
	private Date received;
	@Temporal(TemporalType.TIMESTAMP)
	@Field(name = "created")
	private Date created;

	public Email() {
	}

	public Email(String subject, String sender, String textpreview, String hash, String tags, Date archived, Date received, Date created) {
		this.subject = subject;
		this.sender = sender;
		this.textpreview = textpreview;
		this.hash = hash;
		this.tags = tags;
		this.archived = archived;
		this.received = received;
		this.created = created;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getSender() {
		return sender;
	}

	public void setSender(String o) {
		this.sender = o;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String o) {
		this.subject = o;
	}

	public String getTextpreview() {
		return textpreview;
	}

	public void setTextpreview(String o) {
		this.textpreview = o;
	}

	public String getHash() {
		return hash;
	}

	public void setHash(String o) {
		this.hash = o;
	}

	public String getTags() {
		return tags;
	}

	public void setTags(String o) {
		this.tags = o;
	}

	public Date getArchived() {
		return archived;
	}

	public void setArchived(Date o) {
		this.archived = o;
	}

	public Date getReceived() {
		return received;
	}

	public void setReceived(Date o) {
		this.received = o;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date o) {
		this.created = o;
	}

    @Override
    public String toString() {
        String s = ""+ this.id;
        s = s + ", " + this.sender;
        s = s + ", " + this.subject;
        s = s + ", " + this.textpreview;
        s = s + ", " + this.hash;
        s = s + ", " + this.tags;
        s = s + ", " + this.archived;
        s = s + ", " + this.received;
        s = s + ", " + this.created;
        return s;
    }

}
