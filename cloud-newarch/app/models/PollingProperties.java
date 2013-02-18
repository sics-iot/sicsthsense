package models;

import java.net.URL;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import play.data.validation.Constraints;
import play.db.ebean.Model;

@Entity
@Table(name = "polling_properties")
public class PollingProperties extends Model{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1976815085801134322L;

	@Id
	public Long id;
	
	@Column(nullable = false)
	@Constraints.Required
	@OneToOne
	public GenericSource resource;
	
	public Long pollingPeriod=0L;
	public Long lastPolled=0L;
  public URL pollingUrl=null;
  public String pollingAuthenticationKey=null;
  
	public PollingProperties() { 	
		super();
	}
	
	public PollingProperties(GenericSource resource, Long pollingPeriod, URL pollingUrl,
			String pollingAuthenticationKey) {
		super();
		this.resource = resource;
		this.pollingPeriod = pollingPeriod;
		this.pollingUrl = pollingUrl;
		this.pollingAuthenticationKey = pollingAuthenticationKey;
	}
	
	public void setPeriod(Long period) {
		this.pollingPeriod = period;
	}
}
