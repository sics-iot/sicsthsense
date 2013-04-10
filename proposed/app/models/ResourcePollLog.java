//package models;
//
//import java.util.Date;
//import java.util.List;
//import java.util.Random;
//
//import javax.persistence.*;
//
//import controllers.Utils;
//
//import play.db.ebean.*;
//import play.Logger;
//import play.libs.WS;
//import play.libs.WS.WSRequestHolder;
//import play.mvc.Http.Request;
//
//@Entity
//@Table(name = "resource_poll_logs")
//public class ResourcePollLog extends Model {
//
//	/**
//	 * The serialization runtime associates with each serializable class a version
//	 * number, called a serialVersionUID
//	 */
//	private static final long serialVersionUID = 3007568121115498216L;
//
//	@Id
//	public Long id;
//
//	@OneToOne
//	@Column(unique = true, nullable = false)
//	public Resource resource;
//
//	public WSRequestHolder request;
//	
//	public WS.Response response;
//
//	public Long creationTimestamp;
//
//	public Long responseTimestamp;
//
//	public Boolean parsedSuccessfully = false;
//
//	public String message = "";
//
//	@Version
//	// for concurrency protection
//	private int version;
//
//	public static Model.Finder<Long, ResourcePollLog> find = new Model.Finder<Long, ResourcePollLog>(
//			Long.class, ResourcePollLog.class);
//
//	public ResourcePollLog() {
//		super();
//	}
//
//	public ResourcePollLog(Resource resource, WSRequestHolder request,
//			WS.Response response, Long creationTimestamp, Long responseTimestamp) {
//		super();
//		this.resource = resource;
//		this.request = request;
//		this.response = response;
//		this.creationTimestamp = creationTimestamp;
//		this.responseTimestamp = responseTimestamp;
//	}
//
//	public boolean updateResourcePollLog(ResourcePollLog rpl) {
//		this.resource = rpl.resource;
//		this.request = rpl.request;
//		this.response = rpl.response;
//		this.creationTimestamp = rpl.creationTimestamp;
//		this.responseTimestamp = rpl.responseTimestamp;
//		this.parsedSuccessfully = rpl.parsedSuccessfully;
//		this.message = rpl.message;
//		if (id != null) {
//			this.update();
//			return true;
//		}
//		return false;
//	}
//
//	public void updateParsedSuccessfully(Boolean parsedSuccessfully) {
//		this.parsedSuccessfully = parsedSuccessfully;
//		if (id != null) {
//			this.update();
//		}
//	}
//
//	public void updateMessages(String msg) {
//		this.message = msg;
//		if (id != null) {
//			this.update();
//		}
//	}
//
//	public static ResourcePollLog create(ResourcePollLog resourcePollLog) {
//		if (resourcePollLog.resource != null && resourcePollLog.response != null) {
//			if (resourcePollLog.creationTimestamp == null
//					|| resourcePollLog.creationTimestamp == 0L) {
//				resourcePollLog.creationTimestamp = Utils.currentTime();
//			}
//			ResourcePollLog rplCopy = getByResource(resourcePollLog.resource);
//			if (rplCopy != null) {
////				resourcePollLog.id = rplCopy.id;
////				resourcePollLog.version = rplCopy.version;
////				resourcePollLog.update();
////				return resourcePollLog;
//				rplCopy.updateResourcePollLog(resourcePollLog);
//				return rplCopy;
//			} else {
//				resourcePollLog.save();
//				return resourcePollLog;
//			}
//			
//		} else {
//			// Logger.warn("[ResourcePostLog] Could not create resourcePostLog for " +
//			// resourcePostLog.resource.label + ", resource or input bad");
//			if (resourcePollLog.resource == null) {
//				Logger.warn("[ResourcePollLog] resource null");
//			}
//			if (resourcePollLog.response == null) {
//				Logger.warn("[ResourcePollLog] response null");
//			}
//		}
//		return null;
//	}
//
//	public static ResourcePollLog getByResource(Resource resource) {
//		if(resource == null) {
//			Logger.warn("[ResourcePollLog] Could not find one for resource: Null");
//			return null;
//		}
//		ResourcePollLog rpl = find.where().eq("resource_id", resource.id).findUnique();
//		if(rpl == null){Logger.warn("[ResourcePollLog] Could not find one for resource: " + resource.id.toString() + resource.label);}
//		else {	
//		}
//		return rpl;
//	}
//
//	public static ResourcePollLog getById(Long id) {
//		return find.byId(id);
//	}
//
//	public static void delete(Long id) {
//		find.ref(id).delete();
//	}
//}
