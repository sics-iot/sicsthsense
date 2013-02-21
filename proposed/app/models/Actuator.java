package models;
import java.util.*;

import javax.persistence.*;

import play.db.ebean.*;
import scala.reflect.internal.Trees.Super;

@Entity
@Table(name = "actuators")
public class Actuator extends Model {
	 /**
	 * The serialization runtime associates with each serializable class 
	 * a version number, called a serialVersionUID
	 */
	//@Transient
	private static final long serialVersionUID = 6496834518631996535L;

	@OneToMany(mappedBy="source", cascade=CascadeType.ALL)
	public List<Stream> outputStreams;

	/** HTML, JSON, RegEx */
	private String inputParser;
	
	public static Model.Finder<Long,Actuator> find = new Model.Finder<Long, Actuator>(Long.class, Actuator.class);

	public Actuator(User user) {
		super(user);
	}
	public Actuator() {
		super();
	}

	public static Actuator create(User user) {
		Actuator source = new Actuator(user);
		source.save();
	}

	public void setInputParser(String inputParser) {
		this.inputParser = inputParser;
		if(this.id != 0) {
			this.update();
		}			
	}
  

}
