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
//
////@Entity
////@Table(name = "resource_logs", uniqueConstraints = { 
////		@UniqueConstraint(columnNames = {"stream_parser_id", "resource_poll_log_id", "resource_post_log_id" }) 
////		})
//public class ParsersLog extends Model {
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
//	public StreamParser streamParser;
//	
//	@OneToOne
//	public ResourcePollLog resourcePollLog;
//	
//	@OneToOne
//	public ResourcePostLog resourcePostLog;
//	
//	public Long parsedTimestamp;
//	
//	public String parsedValue;
//	
//	public Long creationTimestamp;
//	
//	@Version //for concurrency protection
//	private int version;
//	
//	public static Model.Finder<Long, ParsersLog> find = new Model.Finder<Long, ParsersLog>(Long.class, ParsersLog.class);
//
//	public ParsersLog() {
//		super();
//	}
//	
//	public ParsersLog(StreamParser streamParser, ResourcePollLog resourcePollLog,
//			ResourcePostLog resourcePostLog, Long parsedTimestamp,
//			String parsedValue, Long creationTimestamp) {
//		super();
//		this.streamParser = streamParser;
//		this.resourcePollLog = resourcePollLog;
//		this.resourcePostLog = resourcePostLog;
//		this.parsedTimestamp = parsedTimestamp;
//		this.parsedValue = parsedValue;
//		this.creationTimestamp = creationTimestamp;
//	}
//	
//	public static List<ParsersLog> getByResource(Resource resource) {
//		if(resource == null) {
//			return null;
//		}
//		return find.where().eq("resource", resource).findList();
//	}
//	
//	public static ParsersLog getById(Long id) {
//		return find.byId(id);
//	}
//	
//	public static void delete(Long id) {
//		find.ref(id).delete();
//	}
//}
