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
package controllers;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Date;

public class Utils {
		
	public static long currentTime() {
		//return System.currentTimeMillis();
		return new Date().getTime();
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

  public static String dateFormatter(long time) {
    return new Date(time).toString();
  }
  
	public static boolean isValidURL(String surl) {
		URL url = null;
		try {
			url = new URL(surl);
			url.toURI();
		} catch (MalformedURLException e) {
			return false;
		} catch (URISyntaxException e) {
			return false;
		}	
		return true;
	}

}
