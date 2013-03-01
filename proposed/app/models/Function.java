package models;

import java.util.List;

import javax.persistence.*;

import play.Logger;
import play.db.ebean.Model;

@Entity
@Table(name = "functions")
//@Inheritance
//@DiscriminatorValue("operators")
public class Function extends Operator {
	@Id
	public Long id;
	
	@ManyToOne
	public User owner;
	
	@ManyToMany(cascade=CascadeType.ALL)
	public List<Stream> outputStreams;

	@ManyToMany(cascade=CascadeType.ALL)
	public List<Stream> inputStreams;
	
	public Function() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Function(User user, String output, String inputStream1, String inputStream2) {
		this.owner = user;
		Logger.warn("Operator() "+output+" "+inputStream1+" "+inputStream2);
	}

	public static Function create(User user, String output, String inputStream1, String inputStream2) {
		Logger.warn("Function.create() "+output+" "+inputStream1+" "+inputStream2);
		Function function = new Function(user,output,inputStream1,inputStream2);
		try { function.save(); }
		catch (Exception e) {}
		return function;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 5004038592549122787L;
	
	public static Model.Finder<Long,Operator> find = new Model.Finder<Long, Operator>(Long.class, Operator.class);
}
