package controllers;

import java.util.ArrayList;
import java.util.List;
import models.*;
import play.Logger;
import play.data.validation.Constraints;

public class SkeletonSource {
	public Long id;
	public String label = null;
	public String key = null;
	public String pollingUrl = null;
	public Long pollingPeriod = 0L;
	public String pollingAuthenticationKey = null;
	public List<StreamParserWraper> streamParserWrapers;

	public SkeletonSource(String label, String key, Long pollingPeriod, String pollingUrl, String pollingAuthenticationKey, List<StreamParserWraper> streamParserWrapers) {
		this.label = label;
		this.key = key;
		this.pollingPeriod = pollingPeriod;
		this.pollingUrl = pollingUrl;
		this.pollingAuthenticationKey = pollingAuthenticationKey;
		this.streamParserWrapers = streamParserWrapers;
	}

	public SkeletonSource(Source source, List<StreamParserWraper> streamParserWrapers) {
		if (source != null) {
			this.id = source.id;
			this.key = source.getKey();
			this.label = source.label;
			this.pollingPeriod = source.pollingPeriod;
			this.pollingUrl = source.pollingUrl;
			this.pollingAuthenticationKey = source.pollingAuthenticationKey;
		}
		this.streamParserWrapers = streamParserWrapers;
	}

	public SkeletonSource(Source source) {
		if (source != null) {
			this.id = source.id;
			this.label = source.label;
			this.key = source.getKey();
			this.pollingPeriod = source.pollingPeriod;
			this.pollingUrl = source.pollingUrl;
			this.pollingAuthenticationKey = source.pollingAuthenticationKey;
			if (source.streamParsers != null) {
				streamParserWrapers = new ArrayList<StreamParserWraper>(source.streamParsers.size()+1);
				for (StreamParser sp : source.streamParsers) {
					streamParserWrapers.add(new StreamParserWraper(sp));
				}
			}
		}
	}
	
	public SkeletonSource(Source source, StreamParserWraper... spws) {
		if(source != null) {
			this.id = source.id;
			this.key = source.getKey();
			this.label = source.label;
			this.pollingPeriod = source.pollingPeriod;
			this.pollingUrl = source.pollingUrl;
			this.pollingAuthenticationKey = source.pollingAuthenticationKey;
		}
		this.streamParserWrapers = new ArrayList<StreamParserWraper>(spws.length+1);
		for (StreamParserWraper spw : spws) {
			this.streamParserWrapers.add(spw);
		}
	}

	public SkeletonSource(Long id, String label, String key, Long pollingPeriod, String pollingUrl, String pollingAuthenticationKey, StreamParserWraper... spws) {
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
	
	public SkeletonSource() {
	}


	public Source getSource(User user) {
		Source src = new Source(user, label,
				pollingPeriod, pollingUrl,
				pollingAuthenticationKey);
		src.id = id;
		return src;
	}

	public List<StreamParser> getStreamParsers(Source source) {
		if (streamParserWrapers == null) { return null; }
		List<StreamParser> list = new ArrayList<StreamParser>();

		for (int i = 0; i < streamParserWrapers.size(); i++) {
			if (streamParserWrapers.get(i).vfilePath != null) {
				StreamParser sp = streamParserWrapers.get(i).getStreamParser(source);
				list.add(sp);
			} else {
				Logger.warn("Got a null vfilePath");
			}
		}
		return list;
	}

	public boolean FillFromSource(Source source) {
		if(source != null) {
			this.id = source.id;
			this.key = source.key;
			this.label = source.label;
			this.pollingPeriod = source.pollingPeriod;
			this.pollingUrl = source.pollingUrl;
			this.pollingAuthenticationKey = source.pollingAuthenticationKey;
			return true;
		}
		return false;
	}
	public void addStreamParser(StreamParserWraper spw) {
		streamParserWrapers.add(spw);
	}

	public void addStreamParser(String vfilePath, String inputParser, String inputType, String timeformat) {
		streamParserWrapers.add(new StreamParserWraper(vfilePath,inputParser,inputType, timeformat));
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

		public StreamParserWraper(Long id, String vfilePath, String inputParser, String inputType, String timeformat) {
			this.parserId  = id;
			this.vfilePath = vfilePath;
			this.inputType = inputType;
			this.inputParser = inputParser;
			this.timeformat = timeformat;
		}
		
		public StreamParserWraper(String vfilePath, String inputParser, String inputType, String timeformat) {
			this(null, vfilePath, inputParser, inputType, timeformat);
		}
		
		public StreamParserWraper(StreamParser sp) {
			try {
				this.vfilePath   = sp.stream.file.getPath();
				this.inputType   = sp.inputType;
				this.inputParser = sp.inputParser;
				this.parserId = sp.id;
				this.timeformat = sp.timeformat;
			} catch (Exception e) {
				Logger.error("Error creating StreamParserWraper from StreamParser: " + e.getMessage() + "Stack trace:\n" + e.getStackTrace()[0].toString());
			}
		}

		public StreamParserWraper() {
		}

		public StreamParser getStreamParser(Source source) {
			StreamParser sp = new StreamParser(source,
					this.inputParser, this.inputType,
					this.vfilePath, this.timeformat);
			sp.id = this.parserId;
			return sp;
		}
	}
}
