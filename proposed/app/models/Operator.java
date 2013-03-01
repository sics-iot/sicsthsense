package models;

import java.util.List;

import javax.persistence.*;

import play.Logger;
import play.db.ebean.Model;

@Entity
@Table(name = "operators")
//@Inheritance
//@DiscriminatorValue("operators")
public class Operator extends Model {
	@Id
	public Long id;
	
	@ManyToOne
	public User owner;
	
	@ManyToMany(cascade=CascadeType.ALL)
	public List<Stream> outputStreams;

	@ManyToMany(cascade=CascadeType.ALL)
	public List<Stream> inputStreams;
	
	public Operator() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Operator(User user, String output, String inputStream1, String inputStream2) {
		this.owner = user;
		Logger.warn("Operator() "+output+" "+inputStream1+" "+inputStream2);
	}

	public static Operator create(User user, String output, String inputStream1, String inputStream2) {
		Logger.warn("Operator.create() "+output+" "+inputStream1+" "+inputStream2);
		Operator operator = new Operator(user,output,inputStream1,inputStream2);
		try { operator.save(); }
		catch (Exception e) {}
		return operator;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 5004038592549122787L;
	
	public static Model.Finder<Long,Operator> find = new Model.Finder<Long, Operator>(Long.class, Operator.class);
}
