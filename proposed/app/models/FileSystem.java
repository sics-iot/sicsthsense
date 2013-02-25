package models;

import java.util.*;

import javax.persistence.*;

import models.*;

import org.codehaus.jackson.JsonNode;

import play.Logger;
import play.db.ebean.*;
import play.data.format.*;
import play.data.validation.*;

import com.avaje.ebean.*;

import controllers.Utils;
import play.libs.F.*;
import play.libs.WS;



public class FileSystem {

	public FileSystem() {
	}

	public static void createError() {
		// give a warning to user?
	}

	public static List<Vfile> listFiles(User user) {
		return Vfile.find.where().eq("owner",user).findList();
	}

	public static Vfile addFile(User user, String path) {
		int i=0;
		int sep=-1;
		while ( (sep=path.indexOf('/',sep)) != -1 ) { // for each subdir into path
			String ancestors = path.substring(0, sep);
			if (!fileExists(user, ancestors)) { // if parent doesnt exist
				//create dir
				addDirectory(user, ancestors);
			} else if (!isDir(user, path)) { // if file isn't dir
				// complain
				Logger.error("Path already exists as a file: "+path);
				return null;
			}
		}
		// create file, filename: sep-end
		Vfile f = Vfile.create(new Vfile(user,path,Vfile.Filetype.FILE));
		return f;
	}
				
	public static Vfile addDirectory(User user, String path) {
		// create file, filename: sep-end
		Vfile dir = Vfile.create( new Vfile(user, path, Vfile.Filetype.DIR) );
		return dir;
	}

	public static boolean fileExists(User user, String path) {
		Vfile f = Vfile.find.where().eq("owner",user).eq("path", path).findUnique();
		if (f==null) { // if file exists
			return true;
		} else {
			return false;
		}
	}

	public static boolean isDir(User user, String path) {
		Vfile f = Vfile.find.where().eq("owner",user).eq("path", path).findUnique();
		if (f.type == Vfile.Filetype.DIR) {
			return true;
		} else {
			return false;
		}
	}

	public static Vfile readFile(User user, String path) {
		Vfile f = Vfile.find.where().eq("owner",user).eq("path", path).findUnique();
		if (f != null) { // if file exists
			return f;
		} else {
			Logger.error("Vfile path does not exist:: "+path);
			return null;
		}
	}

	public static void deleteFile(User user, String path) {
		Vfile f = readFile(user,path);
		if (f!=null) {
			f.delete();
		} else {
			Logger.warn("Vfile path to delete does not exist:: "+path);
		}
	}
	

}
