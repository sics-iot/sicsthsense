package models;

import java.util.Date;
import java.util.List;
import java.util.Random;

import javax.persistence.*;

import controllers.Utils;

import play.db.ebean.*;
import play.Logger;
import play.mvc.Http.Request;


@Entity
@Table(name = "resource_logs")
public class ResourcePostLog extends Model {

	/**
	 * The serialization runtime associates with each serializable class a version
	 * number, called a serialVersionUID
	 */
	private static final long serialVersionUID = 3007568121115498216L;

	@Id
	public Long id;

	@OneToOne	
	@Column(unique = true, nullable = false)
	public Resource resource;
	
	public Request request;
	
	public Long creationTimestamp;

	public Boolean parsedSuccessfully = false;

	public String message = "";
	@Version //for concurrency protection
	private int version;
	
	public static Model.Finder<Long, ResourcePostLog> find = new Model.Finder<Long, ResourcePostLog>(Long.class, ResourcePostLog.class);

	public ResourcePostLog() {
		super();
	}
	
	public ResourcePostLog(Resource resource, Request request, Long creationTimestamp) {
		super();
		this.resource = resource;
		this.request = request;
		this.creationTimestamp = creationTimestamp;
	}
	
	public static ResourcePostLog create(ResourcePostLog resourcePostLog) {
		if (resourcePostLog.resource != null && resourcePostLog.request != null) {
			if(resourcePostLog.creationTimestamp==null || resourcePostLog.creationTimestamp == 0L) {
				resourcePostLog.creationTimestamp = Utils.currentTime();
			}
			ResourcePostLog rplCopy = getByResource( resourcePostLog.resource );
			if( rplCopy != null ) {
				resourcePostLog.id = rplCopy.id;
				resourcePostLog.update();
			} else {
				resourcePostLog.save();
			}
			return resourcePostLog;

		} else {
			//Logger.warn("[ResourcePostLog] Could not create resourcePostLog for " + resourcePostLog.resource.label + ", resource or input bad");
			if (resourcePostLog.resource == null) { Logger.warn("[ResourcePostLog] resource null"); }
			if (resourcePostLog.request == null) { Logger.warn("[ResourcePostLog] request null"); }
		}
		return null;
	}
	
	public void updateParsedSuccessfully(Boolean parsedSuccessfully) {
		this.parsedSuccessfully = parsedSuccessfully;
		if(id != null) {
			this.update();
		}
	}
	
	public void updateMessages(String msg) {
		this.message = msg;
		if(id != null) {
			this.update();
		}
	}
	
	public static ResourcePostLog getByResource(Resource resource) {
		if(resource == null) {
			return null;
		}
		return find.where().eq("resource", resource).findUnique();
	}
	
	public static ResourcePostLog getById(Long id) {
		return find.byId(id);
	}
	
	public static void delete(Long id) {
		find.ref(id).delete();
	}
}
