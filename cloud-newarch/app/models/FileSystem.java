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



public class FileSystem {

	public FileSystem() {
	}

	public static void createError() {
		// give a warning to user?
	}

	public static List<File> listFiles(User user) {
		return File.find.where().eq("owner",user).findList();
	}

	private static void createDirectory(User user, String dirpath) {
		// should check exists?
		
		File dir = new File(user, dirpath, File.Filetype.DIR);
		dir.save();
	}

	public static File addFile(User user, String path) {
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
		File f = new File(user,path,File.Filetype.FILE);
		f.save();
		return f;
	}
				
	public static void addDirectory(User user, String path) {
		// create file, filename: sep-end
		File dir = new File(user, path, File.Filetype.DIR);
		dir.save();
	}

	public static boolean fileExists(User user, String path) {
		File f = File.find.where().eq("owner",user).eq("path", path).findUnique();
		if (f==null) { // if file exists
			return true;
		} else {
			return false;
		}
	}

	public static boolean isDir(User user, String path) {
		File f = File.find.where().eq("owner",user).eq("path", path).findUnique();
		if (f.type == File.Filetype.DIR) {
			return true;
		} else {
			return false;
		}
	}

	public static File readFile(User user, String path) {
		File f = File.find.where().eq("owner",user).eq("path", path).findUnique();
		if (f != null) { // if file exists
			return f;
		} else {
			Logger.error("File path does not exist:: "+path);
			return null;
		}
	}

	public static void deleteFile(User user, String path) {
		File f = readFile(user,path);
		if (f!=null) {
			f.delete();
		} else {
			Logger.warn("File path to delete does not exist:: "+path);
		}
	}

}
