package models;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.Table;

import play.db.ebean.Model;

@Entity
@Inheritance
@DiscriminatorValue("oper_obj")
public class Operator extends GenericSource {

	public Operator() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Operator(User user) {
		super(user);
		// TODO Auto-generated constructor stub
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 5004038592549122787L;
	
	public static Model.Finder<Long,Operator> find = new Model.Finder<Long, Operator>(Long.class, Operator.class);
}
