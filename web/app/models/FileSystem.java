/*
 * Copyright (c) 2013, Swedish Institute of Computer Science
 * All rights reserved.
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of The Swedish Institute of Computer Science nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE SWEDISH INSTITUTE OF COMPUTER SCIENCE BE LIABLE 
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE. */

/* Description:
 * TODO:
 * */

package models;

import java.util.*;
import java.lang.StringBuffer;

import javax.persistence.*;

import com.avaje.ebean.*;

/*
import com.github.cleverage.elasticsearch.Index;
import com.github.cleverage.elasticsearch.IndexQuery;
import com.github.cleverage.elasticsearch.IndexResults;
import com.github.cleverage.elasticsearch.Indexable;
import com.github.cleverage.elasticsearch.annotations.IndexType;
*/
import com.fasterxml.jackson.databind.JsonNode;

import play.Logger;
import play.db.ebean.*;
import play.data.format.*;
import play.data.validation.*;
import play.libs.F.*;
import play.libs.WS;

import controllers.Utils;
import models.*;

public class FileSystem {

	public FileSystem() {
	}

	public static void createError() {
		// give a warning to user?
	}

	public static List<Vfile> listFiles(User user) {
		return Vfile.find.where().eq("owner",user).orderBy("path").findList();
	}

	/** LS into a dir */
	public static List<Vfile> lsDir(User user, String path) {
		if(!path.endsWith("/")) path += "/";
		return Vfile.find.where(
				Expr.and(
						Expr.eq("owner",user), Expr.and( 
								Expr.startsWith("path", path), Expr.not( Expr.like("path", path+"%/%") ) 
								) ) )
								.orderBy("type").orderBy("path")
								.findList();
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
				//sb.append("<li class='jstree-open dirNode' data-filepath='"+f.path+"'><a><b>"+ dirs[dirs.length-1] +"</b></a>\n<ul>\n"); // give node name
				sb.append("<li class='jstree-open'><i class='icon-folder-open hideFolder'></i><span class='dirNode' data-filepath='"+f.path+"'> "+ dirs[dirs.length-1] +"</span>\n<ul class='folderNodeUL'>\n"); // give node name
			} else {
				prevdepth--; // if we were file, this doesnt count
				//sb.append("<li class='fileNode' data-filepath='"+f.path+"'><a a='#'>"+ dirs[dirs.length-1] +"</a></li>\n"); // give node name
				sb.append("<li class='jstree-leaf'><i class='icon-file'></i><span class='fileNode' data-filepath='"+f.path+"'> "+ dirs[dirs.length-1] +"</span></li>\n"); // give node name
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
		if(path.endsWith("/")) path = path.substring(0, path.length()-1);
		Vfile f = Vfile.find.where().eq("owner",user).eq("path", path).findUnique();
		if (f != null) { // if file exists
			return f;
		} else {
			Logger.info("Vfile path does not exist:: "+path);
			return null;
		}
	}

	@Transactional
	public static boolean deleteFile(User user, String path) {
		Vfile f = readFile(user, path);
		if (f != null) {
			if (f.isDir()) {
				// loop throw all files and delete those with paths starting with this
				// path...
				List<Vfile> flist = Vfile.find.where().eq("owner", user)
						.istartsWith("path", path + "/").findList();
				Ebean.delete(flist);
			}
			f.delete();
			return true;
		} else {
			Logger.warn("Vfile path to delete does not exist:: " + path);
		}
		return false;
	}

	@Transactional
	public static boolean moveFile(User user, String path, String newPath) {
		try {
			Vfile f = readFile(user, path);
			if (f != null) {
				// XXX it was not working because I was trying to set path by direct
				// access, while it is not public (but it is accessible to this class).
				// However, due to play and ebean byte code modifications bug, we have
				// to use a setter or make the field public
				f.setPath( newPath );
				f.update();
				Logger.info("Main file moved from:: " + path + " ::to:: " + f.path);
				if (f.isDir()) {
					// loop throw all files and update those with paths starting with this
					// path...
					List<Vfile> flist = Vfile.find.select("path, owner, id").where().eq("owner_id", user.id)
							.istartsWith("path", path + "/").findList();
					for (Vfile subfile : flist) {
						Logger.info("Moving sub file:: " + subfile.path + " ::to:: "
								+ newPath + subfile.path.substring(path.length()));

						subfile.setPath( newPath + subfile.getPath().substring(path.length()) );
						subfile.update();
						Logger.info("File moved to:: " + subfile.path);
					}			
				}
				return true;
			} else {
				Logger.warn("Vfile path to delete does not exist:: " + path);
			}
		} catch (Exception e) {
			Logger.error("Error moving file from:: " + path + " ::to:: " + newPath
					+ "\n " + e.getMessage() + e.getStackTrace()[0].toString());
		}
		return false;
	}	
}
