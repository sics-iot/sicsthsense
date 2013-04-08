package controllers;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;

import play.Logger;
import play.libs.Json;
import play.mvc.*;
import models.*;
import views.html.*;

public class CtrlFile extends Controller {

	@Security.Authenticated(Secured.class)
	public static Result getFiles() {
		User currentUser = Secured.getCurrentUser();
		List<Vfile> vfiles = FileSystem.listFiles(currentUser);
		Map<String, String> fileTree = new HashMap<String, String>(vfiles.size()+1);
		for(Vfile vf : vfiles) {
			vf.getPath();
		}
		
		
		return TODO; 
	}
	/** Generates a path listing
	 * @param path: the folder to explore
	 * @param full: whether to generate the full html page or only the table body
	 * @returns: an html page representing the result
	 * */
	@Security.Authenticated(Secured.class)
	public static Result lsDir(String path, Boolean full) {
		User currentUser = Secured.getCurrentUser();
		List<Vfile> vfiles = FileSystem.lsDir(currentUser, path);
		if(full) {
	    return ok(filesPage.render(FileSystem.lsDir(currentUser,path)));
		}
    return ok(views.html.filesUtils.listDir.render(FileSystem.lsDir(currentUser,path)));
	}
	
	private static void parsePath(Vfile vfile) {
		String path = vfile.getPath();
		User user = vfile.getOwner();
		int i=0;
		int sep=2;
		while ( (sep=path.indexOf('/',sep)) != -1 ) { // for each subdir into path
			String ancestors = path.substring(0, sep);
			if (FileSystem.isDir(user, ancestors)) { // if parent is a dir
				//create dir
				FileSystem.addDirectory(user, ancestors);
			} else if (FileSystem.isFile(user, path)) { // if it is a file
				// complain
				Logger.info("Path already exists as a file: "+path);
			} else if (FileSystem.isFile(user, ancestors)) {
				// complain
				Logger.error("Subpath already exists as a file: "+ancestors);
				//return null;
			}
		}
	}

}
