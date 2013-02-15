package models;

import play.db.ebean.Model;

public class Operator extends GenericSource {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5004038592549122787L;
	
	public static Model.Finder<Long,Operator> find = new Model.Finder<Long, Operator>(Long.class, Operator.class);
}
