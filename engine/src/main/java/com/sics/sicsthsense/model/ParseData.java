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
package com.sics.sicsthsense.model;

import java.util.Date;
import java.util.Locale;
import java.util.Iterator;
import java.util.List;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;

import com.sics.sicsthsense.jdbi.*;
import com.sics.sicsthsense.core.*;
import com.sics.sicsthsense.resources.atmosphere.*;

public class ParseData {
	private final Logger logger = LoggerFactory.getLogger(ParseData.class);
	ObjectMapper mapper;
	StorageDAO storage;
  Pattern regexPattern;

	public ParseData() {
		storage = DAOFactory.getInstance();
	}
	public ParseData(ObjectMapper mapper) {
		this();
		this.mapper=mapper;
	}

	public void setMapper(ObjectMapper mapper) {
		this.mapper=mapper;
	}

	// Apply this parser to the supplied data
	public void apply(Parser parser, String data) throws Exception {
		logger.info("Applying Parser to JSON data: "+parser);
		if (storage==null) { logger.error("StorageDAO has not been set!"); return; }
		Long currentTime = System.currentTimeMillis();
		if ("application/json".equalsIgnoreCase(parser.getInput_type()) 
			//|| "application/json".equalsIgnoreCase(request.getHeader("Content-Type"))
		) {
			//logger.info("Applying Parser to JSON data: "+data);
			JsonNode rootNode = PollSystem.getInstance().mapper.readTree(data);
			parseJsonResponse(parser, rootNode, currentTime);
		} else {
			//logger.info("Applying Parser to text data: "+data);
			parseTextResponse(parser, data, currentTime);
		}
	}

	/*
	 * parses requests as JSON inputParser is used as the path to the nested json node i.e.
	 * inputParser could be: room1/sensors/temp/value
	 */
	private boolean parseJsonResponse(Parser parser, JsonNode root, Long currentTime) {
			// TODO check concat path against inputParser, get the goal and stop
			// TODO (linear time) form a list of nested path elements from the gui, and
			if (root == null) { logger.error("JSON Root is null"); return false; }
			logger.info("node:"+root.getValueAsText() );
			String[] levels = parser.getInput_parser().split("/");
			JsonNode node = root;
			for (int i = 1; i < levels.length; i++) {
					//Logger.info(levels[i]);
					node = node.get(levels[i]);
					if (node == null) {
							return false;
					}
			}

			if (node.isValueNode()) { // it is a simple primitive
					logger.info("Posting: " + node.getDoubleValue() + " " + currentTime);
					//return stream.post(node.getDoubleValue(), System.currentTimeMillis());
					storage.insertDataPoint(parser.getStream_id(), node.getDoubleValue(), System.currentTimeMillis() );
					storage.updatedStream(parser.getStream_id(), System.currentTimeMillis() );
					return true;
			} else if (node.get("value") != null) { // it may be value:X
					double value = node.get("value").getDoubleValue();
					// should be resource timestamp
					if (node.get("time") != null) { // it may have time:Y
							if (parser.getTimeformat() != null && !"".equalsIgnoreCase(parser.getTimeformat().trim())
											&& !"unix".equalsIgnoreCase(parser.getTimeformat().trim())) {
									currentTime = parseDateTime(node.get("time").getTextValue(),parser.getTimeformat());
							} else {
									currentTime = node.get("time").getLongValue();
							}
					}
					logger.info("posting: " + node.getDoubleValue() + " " + currentTime);
					//return stream.post(value, currentTime);
					//storage.insertDataPoint(-1,stream_id,currentTime,value); 
					return true;
			}
			return false;
	}
	/**
	 * Parses the request using inputParser as regex and posts the first match
	 * 
	 * @param textBody
	 * @return true if could post
	 */
	private boolean parseTextResponse(Parser parser, String textBody, Long currentTime)
					throws NumberFormatException, Exception {
			boolean success = false;
			// try {
			double number = 0.0;
			String value = "", time = "";
			if (parser.getInput_parser() != null && !parser.getInput_parser().equalsIgnoreCase("")) {
					regexPattern = Pattern.compile(parser.getInput_parser());
					Matcher matcher = regexPattern.matcher(textBody);
					for (int i = 0; (i < parser.getNumber_of_points() || parser.getNumber_of_points() < 1) && textBody != null
									&& matcher.find(); i++) {
							// try to match value from the group called :value: otherwise, use the first
							// matching group
							try {
									value = matcher.group("value");
							} catch (IllegalArgumentException iae) {
									try {
											value = matcher.group(parser.getData_group());
									} catch (IndexOutOfBoundsException iob) {
											value = matcher.group(1);
									}
							}
							number = Double.parseDouble(value);

							// try to match time from the group called :time: otherwise, use the second matching
							// group
							try { time = matcher.group("time");
							} catch (IllegalArgumentException iae) {
								try { time = matcher.group(parser.getTime_group()); } 
								catch (IndexOutOfBoundsException iob) { time = null; }
							}

							// if there is a match for time, parse it; otherwise, use the system time (provided
							// in the parameter currentTime)
							if (time != null) {
									if (parser.getTimeformat() != null && !"".equalsIgnoreCase(parser.getTimeformat())
													&& !"unix".equalsIgnoreCase(parser.getTimeformat())) {
											// inputParser REGEX should match the whole date/time string! It is
											// not enough to provide the time format only!
											currentTime = parseDateTime(time, parser.getTimeformat());
									} else {
											currentTime = Long.parseLong(time);
									}
							}
					}
					//success |= stream.post(number, currentTime);
					storage.insertDataPoint(parser.getStream_id(),number,currentTime); 
			} else {
					number = Double.parseDouble(textBody);
					//success |= stream.post(number, currentTime);
					storage.insertDataPoint(parser.getStream_id(),number,currentTime); 
			}
			return success;
	}

	public static void makeStreamAndParser(Resource resource, String nodePath) {
			//Logger.info("addParser() "+resource.id+" "+nodePath+" "+"application/json"+" "+ resource.label+nodePath);
			Stream stream = new Stream();
			stream.setResource_id(resource.getId());
			stream.setOwner_id(resource.getOwner_id());
			long streamId = StreamResource.insertStream(stream);
			long vfileId = StreamResource.insertVFile(nodePath,resource.getOwner_id(),"D",streamId);


			Parser parser = new Parser();
			parser.setResource_id(resource.getId());
			parser.setStream_id(streamId);
			parser.setInput_parser(nodePath);
			ParserResource.insertParser(parser);
	}

	// Auto parsing
  // Walk Json tree creating resource parsers
  public static void parseJsonNode(Resource resource, JsonNode node, String parents) {
    // descend to all nodes to find all primitive element paths...
    //logger.info("parsing Nodes below "+parents);
    System.out.println("parsing Nodes below "+parents);
    Iterator<String> nodeIt = node.getFieldNames();
    while (nodeIt.hasNext()) {
      String field = nodeIt.next();
      JsonNode n = node.get(field);
      if (n.isValueNode()) {
        System.out.println("value node: " + parents + "/" + field);
        // TODO: try to guess time format instead of defaulting to "unix"!
        String nodePath = parents+"/"+field;
				makeStreamAndParser(resource, nodePath);
      } else {
        String fullNodeName = parents + "/" + field;
        //Logger.info("Node: " + fullNodeName);
        parseJsonNode(resource, n, fullNodeName);
      }
    }
  }

  // Parse Json into resource parsers
  //@Security.Authenticated(Secured.class)
  public static boolean autoCreateJsonParsers(ObjectMapper mapper, Resource resource, String data) throws Exception {
    //Logger.info("createJsonParsers() Trying to parse Json to then auto fill in StreamParsers!");
    try {
      // recusively parse JSON and add() all fields
			JsonNode root = mapper.readTree(data);
      parseJsonNode(resource, root, "");
    } catch (Exception e) { // nevermind, move on...
      System.out.println("createJsonParsers() had problems parsing JSON: "+  data);
      System.out.println("Error: "+e.toString());
			throw e;
    }
    return true;
  }


	private Long parseDateTime(String input, String timeformat) {
			DateFormat df = new SimpleDateFormat(timeformat, Locale.ENGLISH);
			Long result = -1L;
			try {
					result = df.parse(input).getTime();
			} catch (ParseException e) {
					logger.info("[StreamParser] Exception " + e.getMessage()
									+ e.getStackTrace()[0].toString());
					logger.info("[StreamParser] Exception timeformat: " + timeformat + "input: " + input);
			}
			return result;
	}


}
