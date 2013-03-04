package controllers;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import models.*;
import play.data.validation.Constraints;

public class SkeletonSource {
	
	public Long pollingPeriod = 0L;
	
	public String pollingUrl = null;
	
	public String pollingAuthenticationKey = null;
	
	public List<StreamParserWraper> streamParserWrapers;

	public SkeletonSource(Long pollingPeriod, String pollingUrl, String pollingAuthenticationKey) {
		this.pollingPeriod = pollingPeriod;
		this.pollingUrl = pollingUrl;
		this.pollingAuthenticationKey = pollingAuthenticationKey;
	}
	
	public SkeletonSource(Long pollingPeriod, String pollingUrl, String pollingAuthenticationKey, List<StreamParserWraper> streamParserWrapers) {
		this(pollingPeriod, pollingUrl, pollingAuthenticationKey);
		this.streamParserWrapers = streamParserWrapers;
	}

	public SkeletonSource(Source source) {
		this(source, (List<StreamParserWraper>) null);
	}
	
	public SkeletonSource(Source source, List<StreamParserWraper> streamParserWrapers) {
		if(source != null) {
			this.pollingPeriod = source.pollingPeriod;
			this.pollingUrl = source.pollingUrl;
			this.pollingAuthenticationKey = source.pollingAuthenticationKey;
		}
		this.streamParserWrapers = streamParserWrapers;
	}

	public SkeletonSource(Source source, StreamParserWraper... spws) {
		if(source != null) {
			this.pollingPeriod = source.pollingPeriod;
			this.pollingUrl = source.pollingUrl;
			this.pollingAuthenticationKey = source.pollingAuthenticationKey;
		}
		this.streamParserWrapers = new ArrayList<StreamParserWraper>(spws.length);
		for(StreamParserWraper spw : spws) {
			this.streamParserWrapers.add(spw);
		}
	}

	public SkeletonSource(Long pollingPeriod, String pollingUrl, String pollingAuthenticationKey, StreamParserWraper... spws) {
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
		return new Source(user,
				pollingPeriod, pollingUrl,
				pollingAuthenticationKey);
	}

	public List<StreamParser> getStreamParsers(Source source) {
		List<StreamParser> list = new ArrayList<StreamParser>();
		for (int i = 0; i < streamParserWrapers.size(); i++) {
			StreamParser sp = streamParserWrapers.get(i).getStreamParser(source);
			list.add(sp);
		}
		return list;
	}
	public boolean FillFromSource(Source source) {
		if(source != null) {
			this.pollingPeriod = source.pollingPeriod;
			this.pollingUrl = source.pollingUrl;
			this.pollingAuthenticationKey = source.pollingAuthenticationKey;
			return true;
		}
		return false;
	}
	public static class StreamParserWraper {
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

		public StreamParserWraper(String vfilePath, String inputParser, String inputType) {
			this.vfilePath = vfilePath;
			this.inputParser = inputParser;
			this.inputType = inputType;
		}

		public StreamParserWraper() {
		}

		public StreamParser getStreamParser(Source source) {
			return new StreamParser(source,
					this.inputParser, this.inputType,
					this.vfilePath);
		}
	}
}
