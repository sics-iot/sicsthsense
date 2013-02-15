package models;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.Table;

import play.db.ebean.Model;


@Entity
@Inheritance
@DiscriminatorValue("oper_tmplate")
public class OperatorTemplate extends UserOwnedResource {

	 /**
	 * The serialization runtime associates with each serializable class 
	 * a version number, called a serialVersionUID
	 */
	private static final long serialVersionUID = -2838719001727581588L;

  public static Model.Finder<Long,OperatorTemplate> find = new Model.Finder<Long, OperatorTemplate>(Long.class, OperatorTemplate.class);
  
  
  /** This class is intended for enabling users to 
   * create new types of operators */
	public OperatorTemplate(User user) {
		super(user);
		// TODO Auto-generated constructor stub
	}

}
