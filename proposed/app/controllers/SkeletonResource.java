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

import java.util.ArrayList;
import java.util.List;

import play.Logger;
import play.data.validation.Constraints;

import models.*;

public class SkeletonResource {
	public Long id;
	public String label = null;
	public String key = null;
	public String pollingUrl = null;
	public String description = null;
	public Long pollingPeriod = 0L;
	public String pollingAuthenticationKey = null;
	public List<StreamParserWraper> streamParserWrapers;

	public SkeletonResource(String label, String key, Long pollingPeriod, String pollingUrl, String pollingAuthenticationKey, List<StreamParserWraper> streamParserWrapers) {
		this.label = label;
		this.key = key;
		this.pollingPeriod = pollingPeriod;
		this.pollingUrl = pollingUrl;
		this.pollingAuthenticationKey = pollingAuthenticationKey;
		this.streamParserWrapers = streamParserWrapers;
	}

	public SkeletonResource(Resource resource, List<StreamParserWraper> streamParserWrapers) {
		if (resource != null) {
			this.id = resource.id;
			this.key = resource.getKey();
			this.label = resource.label;
			this.pollingPeriod = resource.pollingPeriod;
			this.pollingUrl = resource.getPollingUrl();
			this.pollingAuthenticationKey = resource.pollingAuthenticationKey;
			this.description = resource.description;
		}
		this.streamParserWrapers = streamParserWrapers;
	}

	public SkeletonResource(Resource resource) {
		if (resource != null) {
			this.id = resource.id;
			this.label = resource.label;
			this.key = resource.getKey();
			this.pollingPeriod = resource.pollingPeriod;
			this.pollingUrl = resource.getPollingUrl();
			this.pollingAuthenticationKey = resource.pollingAuthenticationKey;
			this.description = resource.description;
			if (resource.streamParsers != null) {
				streamParserWrapers = new ArrayList<StreamParserWraper>(resource.streamParsers.size()+1);
				for (StreamParser sp : resource.streamParsers) {
					streamParserWrapers.add(new StreamParserWraper(sp));
				}
			}
		}
	}
	
	public SkeletonResource(Resource resource, StreamParserWraper... spws) {
		if(resource != null) {
			this.id = resource.id;
			this.key = resource.getKey();
			this.label = resource.label;
			this.pollingPeriod = resource.pollingPeriod;
			this.pollingUrl = resource.getPollingUrl();
			this.pollingAuthenticationKey = resource.pollingAuthenticationKey;
			this.description = resource.description;
		}
		this.streamParserWrapers = new ArrayList<StreamParserWraper>(spws.length+1);
		for (StreamParserWraper spw : spws) {
			this.streamParserWrapers.add(spw);
		}
	}

	public SkeletonResource(Long id, String label, String key, Long pollingPeriod, String pollingUrl, String pollingAuthenticationKey, StreamParserWraper... spws) {
		this.id = id;
		this.label = label;
		this.key = key;
		this.pollingPeriod = pollingPeriod;
		this.pollingUrl = pollingUrl;
		this.pollingAuthenticationKey = pollingAuthenticationKey;
		this.streamParserWrapers = new ArrayList<StreamParserWraper>();
		for (StreamParserWraper spw : spws) {
			this.streamParserWrapers.add(spw);
		}
	}
	
	public SkeletonResource() {
	}


	public Resource getResource(User user) {
		Resource src = new Resource(user, label,
				pollingPeriod, pollingUrl,
				pollingAuthenticationKey);
		src.id = id;
		src.description = description;
		return src;
	}

	public List<StreamParser> getStreamParsers(Resource resource) {
		if (streamParserWrapers == null) { return null; }
		List<StreamParser> list = new ArrayList<StreamParser>();

		for (int i = 0; i < streamParserWrapers.size(); i++) {
			if (streamParserWrapers.get(i).vfilePath != null) {
				StreamParser sp = streamParserWrapers.get(i).getStreamParser(resource);
				//if (sp != null) { // ignore bad parsers (probably regex failure)
				list.add(sp); // add null streamparsers, to give feedback
				//}
			} else {
				Logger.warn("Got a null vfilePath");
			}
		}
		return list;
	}

	public boolean FillFromResource(Resource resource) {
		if(resource != null) {
			this.id = resource.id;
			this.key = resource.key;
			this.label = resource.label;
			this.pollingPeriod = resource.pollingPeriod;
			this.pollingUrl = resource.getPollingUrl();
			this.pollingAuthenticationKey = resource.pollingAuthenticationKey;
			this.description = resource.description;
			return true;
		}
		return false;
	}
	public void addStreamParser(StreamParserWraper spw) {
		streamParserWrapers.add(spw);
	}

	public void addStreamParser(String vfilePath, String inputParser, String inputType, String timeformat,int dataGroup, int timeGroup, int numberOfPoints) {
		streamParserWrapers.add(new StreamParserWraper(vfilePath,inputParser,inputType, timeformat, dataGroup, timeGroup, numberOfPoints));
	}
	
	public void addStreamParser(String vfilePath, String inputParser, String inputType, String timeformat) {
		streamParserWrapers.add(new StreamParserWraper(vfilePath,inputParser,inputType, timeformat, 1,2,1));
	}

	public static class StreamParserWraper {
		public Long parserId;
		@Constraints.Required
		public String vfilePath;
		//@Constraints.Required
		/** RegEx, Xpath, JSON path */
		public String inputParser;
		/**
		 * JSON, HTML, text, XML, ... to override MIME contentType of input Right now,
		 * it could be defined as application/json, otherwise, request's content is
		 * handled as text
		 */
		public String inputType;
		
		public String timeformat;
		
		/**
		 * The number of the field containing the value of datapoint (mainly used in parsing CSV ^ RegEx)
		 * Starts from 1
		 */
		int dataGroup = 1;
		
		/**
		 * The number of the field containing the value of datapoint (mainly used in parsing CSV & RegEx)
		 * Starts from 1
		 */
		int timeGroup = 2;
		
		/**
		 * How many points to match?
		 * values <= 0 mean parsing all possible matches
		 */
		int numberOfPoints=1;

		public StreamParserWraper(Long id, String vfilePath, String inputParser, String inputType, String timeformat, int dataGroup, int timeGroup, int numberOfPoints) {
			this.parserId  = id;
			this.vfilePath = vfilePath;
			this.inputType = inputType;
			this.inputParser = inputParser;
			this.timeformat = timeformat;
			this.dataGroup = dataGroup;
			this.timeGroup = timeGroup;
			this.numberOfPoints = numberOfPoints;
		}
		
		public StreamParserWraper(String vfilePath, String inputParser, String inputType, String timeformat, int dataGroup, int timeGroup, int numberOfPoints) {
			this(null, vfilePath, inputParser, inputType, timeformat, dataGroup, timeGroup, numberOfPoints);
		}
		
		public StreamParserWraper(StreamParser sp) {
			try {
				this.vfilePath   = sp.stream.file.getPath();
				this.inputType   = sp.inputType;
				this.inputParser = sp.inputParser;
				this.parserId = sp.id;
				this.timeformat = sp.timeformat;
				this.dataGroup = sp.dataGroup;
				this.timeGroup = sp.timeGroup;
				this.numberOfPoints = sp.numberOfPoints;
			} catch (Exception e) {
				Logger.error("Error creating StreamParserWraper from StreamParser: " + e.getMessage() + "Stack trace:\n" + e.getStackTrace()[0].toString());
			}
		}

		public StreamParserWraper() {
		}

		public StreamParser getStreamParser(Resource resource) {
			try {
				StreamParser sp = new StreamParser(resource,
					this.inputParser, this.inputType,
					this.vfilePath, this.timeformat, this.dataGroup, this.timeGroup, this.numberOfPoints);
				sp.id = this.parserId;
				return sp;
			} catch (Exception e) {
				return null; // Parser not reconstructed correctly	
			}
		}
	}
}
