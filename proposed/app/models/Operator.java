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
	
	public Operator() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 5004038592549122787L;
	
	public static Model.Finder<Long,Operator> find = new Model.Finder<Long, Operator>(Long.class, Operator.class);
}
