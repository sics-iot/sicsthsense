package models;

import java.util.*;

import javax.persistence.*;

import org.codehaus.jackson.JsonNode;

import play.Logger;
import play.db.ebean.*;
import play.data.format.*;
import play.data.validation.*;

import com.avaje.ebean.*;

import controllers.Utils;
import play.libs.F.*;
import play.libs.WS;

@Entity
@Table(name = "resource", uniqueConstraints = { @UniqueConstraint(columnNames = {
		"end_point_id", "path" }) })
public class Resource extends Model implements Comparable<Resource> {

	@Id
	public Long id;

	@Constraints.Required
	public String path;
	@ManyToOne
	// (cascade = CascadeType.REMOVE)
	public EndPoint endPoint;
	@ManyToOne
	// (cascade = CascadeType.REMOVE)
	public User user;

	public long pollingPeriod;
	public long lastPolled;
	public long lastUpdated;

	/* Is it publicly available to others? */
	public boolean publicAccess = false;

	//this seems redundant now. Maybe it should be: allowedUsers?
//	@ManyToMany
//	public List<User> followingUsers = new ArrayList<User>();
	
	/* List of users that can read this resource */
	@ManyToMany
	public Set<User> sharedWithUsers = new ArrayList<User>();


	public static Model.Finder<Long, Resource> find = new Model.Finder(
			Long.class, Resource.class);

	public Resource(String path, EndPoint endPoint) {
		this.endPoint = endPoint;
		this.user = endPoint.getUser();
		this.path = path;
		this.pollingPeriod = 0;
		this.lastPolled = 0;
		this.lastUpdated = 0;
	}

	public Boolean addShare(User user) {
		return sharedWithUsers.add(user);
	}
	
	public Boolean removeShare(User user) {
		return sharedWithUsers.remove(user);
	}
	
	public Boolean hasData() {
		return lastUpdated != 0;
	}

	public EndPoint getEndPoint() {
		return EndPoint.get(endPoint.id);
	}

	public User getUser() {
		return User.get(user.id);
	}

	public String fullPath() {
		return Utils.concatPath(user.userName, endPoint.label, path);
	}

	public String getUrl() {
		return Utils.concatPath(endPoint.url, path);
	}

	public static List<Resource> all() {
		return find.where().orderBy("path").findList();
	}

	public static Resource getByPath(EndPoint endPoint, String path) {
		return find.where().eq("endPoint", endPoint)
				.eq("path", Utils.concatPath(path)).findUnique();
	}

	public static List<Resource> getWithData() {
		return find.where().gt("lastUpdated", 0).orderBy("path").findList();
	}

	public static List<Resource> getByUser(User user) {
		return find.where().eq("user", user).orderBy("endPoint.label, path")
				.findList();
	}

	public static List<Resource> getByUserWithData(User user) {
		return find.where().gt("lastUpdated", 0).eq("user", user)
				.orderBy("path").findList();
	}

	public static List<Resource> getByEndPoint(EndPoint endPoint) {
		List<Resource> set = find.where().eq("endPoint", endPoint)
				.orderBy("path").findList();
		Collections.sort(set);
		return set; // hack for the lab, as sorting doesn't seem to work
		// return find.where()
		// .eq("endPoint", endPoint)
		// .orderBy("path")
		// .findList();
	}

	public static List<Resource> getByEndPointWithData(EndPoint endPoint) {
		return find.where().gt("lastUpdated", 0).eq("endPoint", endPoint)
				.orderBy("path").findList();
	}

	public void post(float data, long time) {
		DataPoint.add(this, data, time);
		lastUpdated = time;
		update();
	}

	public static Resource get(Long id) {
		return find.byId(id);
	}

	public static void delete(Long id) {
		// TODO should enable cascading instead
		// TODO To enable cascading the model should be reconstructed to
		// have a OneToMany relationship from a resource to dataPoints;
		// thus, dataPoints should be stored as a list in a resource instead
		// and this should be the same to all other ManyToOne relationships
		clearStream(id);
		find.ref(id).delete();
	}

	public static void setPeriod(Long id, Long period) {
		Resource resource = get(id);
		if (resource != null) {
			resource.pollingPeriod = period;
			resource.update();
		}
	}

	public static void clearStream(Long id) {
		Resource resource = get(id);
		resource.lastPolled = 0;
		resource.lastUpdated = 0;
		resource.update();
		if (resource != null) {
			DataPoint.deleteByStream(resource);
		}
	}

	public static Resource add(String path, EndPoint endPoint) {
		Resource resource = new Resource(path, endPoint);
		try {
			resource.save();
			// TODO should I saveManyToManyAssociations("followingUsers") in
			// other places?
			//resource.saveManyToManyAssociations("followingUsers");
		} catch (Exception e) {
		}
		return resource;
	}

	public static void deleteByEndPoint(EndPoint endPoint) {
		// TODO Change relationship to OntToMany DataPoints to enable cascading
		// correctly...
		// Doesnt work: //I am deleting the list with SQL. Is it better to
		// delete with Ebean?
		// Ebean.createSqlUpdate(
		// "delete from resource where end_point_id = :endPoint"
		// ).setParameter("endPoint", endPoint.id).execute();

		List<Resource> list = find.where()
		 .eq("endPoint", endPoint)
		 .findList();
		for (Resource rs : list) {
			rs.delete();
		}
		// List<Long> ids = new LinkedList<Long>();
		// for(Resource element: list) {
		// ids.add(element.id);
		// }
		// does not work, because it doesnt delete dataPoints (clearStream)
		// Ebean.delete(list);

		// TODO this is an ugly workaround, we need to find out how to SQL
		// delete directly

//		List<Object> ids = find.where().eq("endPoint", endPoint).findIds();
//
//		for (Object id : ids) {
//			delete((Long) id);
//		}

	}

	public int compareTo(Resource resource) {
		return this.fullPath().compareTo(resource.fullPath());
	}

	public void verify() {
		path = Utils.concatPath(path);
		super.save();
	}

	public void save() {
		verify();
		super.save();
	}

	public void update() {
		verify();
		super.update();
	}

	public void delete() {
		//verify();
		clearStream(this.id);

		super.delete();
	}

}
