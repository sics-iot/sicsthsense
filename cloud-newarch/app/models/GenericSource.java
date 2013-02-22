package models;

import java.net.URL;
import java.util.*;

import javax.persistence.*;

import play.db.ebean.*;
import scala.reflect.internal.Trees.Super;

@Entity
@Inheritance
@DiscriminatorValue("generic_source")
@DiscriminatorColumn(length = 16)
public class GenericSource extends UserOwnedResource {
	/**
	 * The serialization runtime associates with each serializable class a
	 * version number, called a serialVersionUID
	 */
	// @Transient
	private static final long serialVersionUID = 6496834518631996535L;

	@OneToOne(cascade = CascadeType.ALL)
	public PollingProperties pollingProperties = null;

	@OneToMany(mappedBy = "source", cascade = CascadeType.ALL)
	public List<Stream> outputStreams;

	/** RegEx */
	private String inputParser;

	public static Model.Finder<Long, GenericSource> find = new Model.Finder<Long, GenericSource>(
			Long.class, GenericSource.class);

	public GenericSource(User user, String regEx, Long pollingPeriod, URL pollingUrl, String pollingAuthenticationKey) {
		this(user, regEx);
		if(pollingUrl != null) {
			this.pollingProperties = new PollingProperties(this, pollingPeriod, pollingUrl, pollingAuthenticationKey);
		}
	}
	
	public GenericSource(User user, String regEx) {
		this(user);
		this.inputParser = regEx;
	}
	
	public GenericSource(User user) {
		super(user);
	}

	public GenericSource() {
		super();
	}

	public static GenericSource create(User user) {
		return (GenericSource)(UserOwnedResource.create(user));
	}

	public void setInputParser(String inputParser) {
		this.inputParser = inputParser;
		if (this.id != 0) {
			this.update();
		}
	}
	
	public String getInputParser() {
		return inputParser;
	}
	
	public Stream createStream() {
		if (outputStreams == null) {
			outputStreams = new ArrayList<Stream>(2);					
		}
		//TODO complete..
		Stream stream = (Stream)Stream.create(user);
		outputStreams.add(stream);
		return stream;
	}

}
