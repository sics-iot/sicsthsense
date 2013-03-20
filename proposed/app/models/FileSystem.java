package models;

import java.util.*;
import java.lang.StringBuffer;

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
		return Vfile.find.where().eq("owner",user).orderBy("path").findList();
	}


	public static String listHTMLFileSystem(User user) {
		StringBuffer sb = new StringBuffer();
		String ancestors = "";
		int prevdepth=0;
		String[] prevdirs={};
		List<Vfile> files = Vfile.find.where().eq("owner",user).orderBy("path asc").findList();
	
		//for (Vfile f: files) { Logger.warn(f.getPath()); }

		for (Vfile f: files) {
			String[] dirs = f.path.split("/");
			int thisdepth = dirs.length-1; // how many subdirs deep
			int sharedAncestors = 0; // how many ancestors are shared with prev path

			for (int i=1; (i<dirs.length && i<prevdirs.length); i++) { // count shared parents
				if (dirs[i].equals(prevdirs[i])) { sharedAncestors++; } else {break;}
			}
			//Logger.info("Path: "+f.path+"\tDepth: "+thisdepth+" Prevdepth: "+prevdepth+" shared: "+sharedAncestors);
			for (int i=prevdepth; i>sharedAncestors; i--) { sb.append("</ul></li>\n"); }
		
			prevdirs = dirs;
			prevdepth = thisdepth;
			if (f.isDir()) {
				sb.append("<li class='jstree-open'><a><b>"+ dirs[dirs.length-1] +"</b></a>\n<ul>\n"); // give node name
			} else { 
				prevdepth--; // if we were file, this doesnt count
				sb.append("<li><a a='#'>"+ dirs[dirs.length-1] +"</a></li>\n"); // give node name
			}
		}
		return sb.toString();
	}

	public static Vfile addFile(User user, String path) {
		int i=0;
		int sep=2;
		//Logger.info(path+" sep "+Integer.toString(sep));
		while ( (sep=path.indexOf('/',sep+1))+1 != -1 ) { // for each subdir into path
			if (sep==-1) {break;}
			//Logger.info(path+" sep "+Integer.toString(sep));
			String ancestors = path.substring(0, sep);
			//Logger.info("ancestor: "+ancestors);
			if (!fileExists(user, ancestors)) { // if parent doesnt exist
				//Logger.info("add dir: "+ancestors);
				addDirectory(user, ancestors);
			} else if (isFile(user, ancestors)) {
				Logger.error("Path already exists as a file: "+ancestors);
				//return null;
			} else if (isDir(user, ancestors)) { // if file is dir
				//Logger.info("Dir exists: "+ancestors);
				// probably fine	
			} else {
				Logger.info("File system broke! "+ancestors);
			}
		}
		// create file, filename: sep-end
		Vfile f = Vfile.create(new Vfile(user,path,Vfile.Filetype.FILE));
		Logger.info("add file: "+path);
		return f;
	}
				
	public static Vfile addDirectory(User user, String path) {
		// create file, filename: sep-end
		Vfile dir = Vfile.create( new Vfile(user, path, Vfile.Filetype.DIR) );
		//Logger.info("add dir: "+path);
		return dir;
	}

	public static boolean fileExists(User user, String path) {
		Vfile f = Vfile.find.where().eq("owner",user).eq("path", path).findUnique();
		if (f!=null) { // if file exists
			return true;
		} else {
			return false;
		}
	}

	public static boolean isDir(User user, String path) {
		Vfile f = Vfile.find.where().eq("owner",user).eq("path", path).findUnique();
		if (f != null && f.type == Vfile.Filetype.DIR) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean isEmptyDir(User user, String path) {
		Vfile f = Vfile.find.where().eq("owner",user).eq("path", path).findUnique();
		if (f == null || f.type != Vfile.Filetype.DIR) { return false; } 
		List<Vfile> children = Vfile.find.where().eq("owner",user).startsWith("path",path).findList();
		if (children.size()>1) { return false; }
		return true; // has children
	}

	public static boolean isFile(User user, String path) {
		Vfile f = Vfile.find.where().eq("owner",user).eq("path", path).findUnique();
		if (f != null && f.type == Vfile.Filetype.FILE) {
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
			Logger.info("Vfile path does not exist:: "+path);
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
