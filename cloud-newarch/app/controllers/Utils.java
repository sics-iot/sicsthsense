package controllers;

public class Utils {
	public static long currentTime() {
		return System.currentTimeMillis();
	}
	
	public static String concatPath(String ... paths) {
    String combined = "";
    for(String path: paths) {
      while(path.startsWith("/")) path = path.substring(1);
      while(path.endsWith("/")) path = path.substring(0,path.length()-1);
      combined += "/" + path;
    }
    if(combined.contains("://")) {
      return combined.substring(1);
    } else {
      return combined;
    }
  }
}
