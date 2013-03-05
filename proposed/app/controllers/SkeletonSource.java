package controllers;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import models.*;
import play.Logger;
import play.data.validation.Constraints;

public class SkeletonSource {
	public Long id;
	public String label = null;
	public String pollingUrl = null;
	public Long pollingPeriod = 0L;
	public String pollingAuthenticationKey = null;
	public List<StreamParserWraper> streamParserWrapers;

	public SkeletonSource(String label, Long pollingPeriod, String pollingUrl, String pollingAuthenticationKey, List<StreamParserWraper> streamParserWrapers) {
		this.label = label;
		this.pollingPeriod = pollingPeriod;
		this.pollingUrl = pollingUrl;
		this.pollingAuthenticationKey = pollingAuthenticationKey;
		this.streamParserWrapers = streamParserWrapers;
	}

	public SkeletonSource(Source source, List<StreamParserWraper> streamParserWrapers) {
		if(source != null) {
			this.id = source.id;
			this.label = source.label;
			this.pollingPeriod = source.pollingPeriod;
			this.pollingUrl = source.pollingUrl;
			this.pollingAuthenticationKey = source.pollingAuthenticationKey;
		}
		this.streamParserWrapers = streamParserWrapers;
	}

	public SkeletonSource(Source source) {
		if(source != null) {
			this.id = source.id;
			this.label = source.label;
			this.pollingPeriod = source.pollingPeriod;
			this.pollingUrl = source.pollingUrl;
			this.pollingAuthenticationKey = source.pollingAuthenticationKey;
			if (source.streamParsers != null) {
				streamParserWrapers = new ArrayList<StreamParserWraper>(source.streamParsers.size());
				for (StreamParser sp : source.streamParsers) {
					streamParserWrapers.add(new StreamParserWraper(sp));
				}
			}
		}
	}
	
	public SkeletonSource(Source source, StreamParserWraper... spws) {
		if(source != null) {
			this.id = source.id;
			this.label = source.label;
			this.pollingPeriod = source.pollingPeriod;
			this.pollingUrl = source.pollingUrl;
			this.pollingAuthenticationKey = source.pollingAuthenticationKey;
		}
		this.streamParserWrapers = new ArrayList<StreamParserWraper>(spws.length);
		for(StreamParserWraper spw : spws) {
			this.streamParserWrapers.add(spw);
		}
	}

	public SkeletonSource(Long id, String label, Long pollingPeriod, String pollingUrl, String pollingAuthenticationKey, StreamParserWraper... spws) {
		this.id = id;
		this.label = label;
		this.pollingPeriod = pollingPeriod;
		this.pollingUrl = pollingUrl;
		this.pollingAuthenticationKey = pollingAuthenticationKey;
		this.streamParserWrapers = new ArrayList<StreamParserWraper>();
		for(StreamParserWraper spw : spws) {
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
		if(streamParserWrapers == null) {
			return null;
		}
		List<StreamParser> list = new ArrayList<StreamParser>();
		for (int i = 0; i < streamParserWrapers.size(); i++) {
			StreamParser sp = streamParserWrapers.get(i).getStreamParser(source);
			list.add(sp);
		}
		return list;
	}
	public boolean FillFromSource(Source source) {
		if(source != null) {
			this.id = source.id;
			this.label = source.label;
			this.pollingPeriod = source.pollingPeriod;
			this.pollingUrl = source.pollingUrl;
			this.pollingAuthenticationKey = source.pollingAuthenticationKey;
			return true;
		}
		return false;
	}
	public static class StreamParserWraper {
		public Long parserId;
		
		@Constraints.Required
		public String vfilePath;
		/** RegEx, Xpath, JSON path */
		public String inputParser;
		/**
		 * JSON, HTML, text, XML, ... to override MIME contentType of input Right now,
		 * it could be defined as application/json, otherwise, request's content is
		 * handled as text
		 */
		public String inputType;

		public StreamParserWraper(Long id, String vfilePath, String inputParser, String inputType) {
			this.parserId = id;
			this.vfilePath = vfilePath;
			this.inputParser = inputParser;
			this.inputType = inputType;
		}
		
		public StreamParserWraper(String vfilePath, String inputParser, String inputType) {
			this(null, vfilePath, inputParser, inputType);
		}
		
		public StreamParserWraper(StreamParser sp) {
			try{
				this.vfilePath = sp.stream.file.getPath();
				this.parserId = sp.id;
				this.inputParser = sp.inputParser;
				this.inputType = sp.inputType;
			}catch(Exception e) {
				Logger.error("Error creating StreamParserWraper from StreamParser: " + e.getMessage() + "Stack trace:\n" + e.getStackTrace().toString());
			}
		}

		public StreamParserWraper() {
		}

		public StreamParser getStreamParser(Source source) {
			StreamParser sp = new StreamParser(source,
					this.inputParser, this.inputType,
					this.vfilePath);
			sp.id = this.parserId;
			return sp;
		}
	}
}
