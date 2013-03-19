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
	
  public static String timeStr(long time) {
    time /= 1000;
  	if(time < 60) return time + " sec";
    time /= 60;
    if(time < 60) return time + " min";
    time /= 60;
    if(time < 24) return time + "h";
    long days = time / 24;
    if(days < 7) return days + (days == 1 ? " day" : " days");
    long weeks = days / 7;
    if(days < 31) return weeks + (weeks == 1 ? " week" : " weeks");
    long months = days / 31;
    if(days < 365) return months + (months == 1 ? " month" : " months");
    long years = days / 365;
    return years + (years == 1 ? " year" : " years");
  }
}
