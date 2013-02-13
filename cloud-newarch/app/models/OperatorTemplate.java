package models;

import play.db.ebean.Model;

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
