package models;

import java.lang.Long;
import java.util.*;

import javax.persistence.*;

import org.codehaus.jackson.JsonNode;

import play.Logger;
import play.db.ebean.*;
import play.data.format.*;
import play.data.validation.*;

import com.avaje.ebean.*;
import com.avaje.ebean.annotation.EnumValue;

import controllers.Utils;
import play.libs.F.*;
import play.libs.WS;


@Entity
@Table(name = "vfiles", uniqueConstraints = { 
		@UniqueConstraint(columnNames = {"owner_id", "path" }) 
		})
public class Vfile extends Model {

	public Vfile() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1766439519493690841L;

	public static enum Filetype {
		@EnumValue("F")
		FILE, 
		@EnumValue("D")
		DIR
		};

	/* should this be a UUID instead?*/
	@Id
	public Long id;

	@Column(nullable = false)
	@Constraints.Required
	String path;

	@Column(name = "owner_id", nullable = false)
	@Constraints.Required
	@ManyToOne
	User owner;

	public User getOwner() {
		return owner;
	}
	
	@Column(nullable = false)
	@Constraints.Required
	Filetype type;

	//@Column(nullable = false)
	//@Constraints.Required
	@OneToOne
	Stream linkedStream;

	public static Finder<Long,Vfile> find = new Finder<Long,Vfile>(Long.class, Vfile.class); 

	public Vfile(User user, String path, Filetype type) {
		this.owner = user;
		this.path= path;
		this.type = type;
	}
	
	public static Vfile create(Vfile file) {
		if (file.owner != null) {
			file.save();
			return file;
		}
		Logger.error("Could not create file because owner is null: " + file.path);
		return null;
	}
	
	public Filetype getType() {
		return type;
	}

	public boolean isFile() {return type==Filetype.FILE;}
	public boolean isDir() {return type==Filetype.DIR;}
	
	public String getPath() {
		return path;
	}
	
	public String getName() {
		if(path == null) {
			return null;
		}
		String [] subPaths = path.split("/");
		return subPaths[ subPaths.length-1 ];
	}
	
	public String getParentPath() {
		if(path == null) {
			return null;
		}
		int i = path.lastIndexOf("/"+getName());
		return path.substring(0, i);
	}
	
	public String getUpperLevel() {
		if(path == null) {
			return null;
		}
		int i = path.lastIndexOf("/"+getName());
		if(i<=0) {
			return "";
		} else {
		int j = path.substring(0, i-1).lastIndexOf('/');
		if(j<=0) {
			return "";
		} else {
			return path.substring(0, j);
		}
		}
	}
	
	public void setLink(Stream linkedStream) {
		this.linkedStream = linkedStream;
		if(id != null) {
			this.update();
		}
	}
	public Stream getLink() {
		return linkedStream;
	}
	
	public void delete() {
		//TODO: Check dependencies
		//this.linkedStream.file = null;
		super.delete();
	}

	public static String extractUpperLevelPath(String path) {
		if (path == null) {
			return "";
		}
		String[] subPaths = path.split("/");
		String name = subPaths[subPaths.length - 1];
		int i = path.lastIndexOf("/" + name);
		if (i <= 0) {
			return "";
		} else {
			int j = path.substring(0, i - 1).lastIndexOf('/');
			if (j <= 0) {
				return "";
			} else {
				return path.substring(0, j);
			}
		}
	}
}
