package com.sics.sicsthsense.core;

import java.util.Date;
import java.util.Locale;
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

import com.sics.sicsthsense.jdbi.*;

public class Parser {
	private final Logger logger = LoggerFactory.getLogger(Parser.class);
	private long id;
	private long resource_id;
	private long stream_id;
	private String input_parser;
	private String input_type;
	private String timeformat;
	private int data_group;
	private int time_group;
	private int number_of_points;
	ObjectMapper mapper;
	StorageDAO storage;
  Pattern regexPattern;
	// Matching configuration
	/**
	 * How many points to match? values <= 0 mean parsing all possible matches
	 */
  int numberOfPoints = 1;
	/**
	 * The number of the field containing the value of datapoint (mainly used in parsing CSV ^
	 * RegEx) Starts from 1
	 */
  int dataGroup = 1;
	/**
	 * The number of the field containing the value of datapoint (mainly used in parsing CSV &
	 * RegEx) Starts from 1
	 */
  int timeGroup = 2;

	public Parser() {
	}
	public Parser(long id) {
		this();
		this.id			= id;
	}
	public Parser(long id,
			long resource_id,
			long stream_id,
			String input_parser,
			String input_type,
			String timeformat,
			int data_group,
			int time_group,
			int number_of_points
		) {
			this(id);
			this.resource_id  = resource_id;
			this.stream_id    = stream_id;
			this.input_parser = input_parser;
			this.input_type   = input_type;
			this.timeformat   = timeformat;
			this.data_group   = data_group;
			this.time_group   = time_group;
			this.number_of_points = number_of_points;
	}
	public void setStorage(StorageDAO storage) {
		this.storage=storage;
	}
	public void setMapper(ObjectMapper mapper) {
		this.mapper=mapper;
	}

	// apply this parser to the supplied data
	public void apply(String data) throws Exception {
		Long currentTime = System.currentTimeMillis();
		//logger.info("Applying Parser to data: "+data);
		if ("application/json".equalsIgnoreCase(input_type) 
		//|| "application/json".equalsIgnoreCase(request.getHeader("Content-Type"))
		) {
			//logger.info("Applying Parser to JSON data: "+data);
			//mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);                 
			JsonNode rootNode = mapper.readTree(data);
			parseJsonResponse(rootNode, currentTime);
		} else {
			//logger.info("Applying Parser to text data: "+data);
			parseTextResponse(data, currentTime);
		}
	}

	/*
	 * parses requests as JSON inputParser is used as the path to the nested json node i.e.
	 * inputParser could be: room1/sensors/temp/value
	 */
	private boolean parseJsonResponse(JsonNode root, Long currentTime) {
			// TODO check concat path against inputParser, get the goal and stop
			// TODO (linear time) form a list of nested path elements from the gui, and
			if (root == null) {
					return false;
			}
			String[] levels = input_parser.split("/");
			JsonNode node = root;
			for (int i = 1; i < levels.length; i++) {
					//Logger.info(levels[i]);
					node = node.get(levels[i]);
					if (node == null) {
							return false;
					}
			}

			if (node.isValueNode()) { // it is a simple primitive
					logger.info("posting: " + node.getDoubleValue() + " " + currentTime);
					//return stream.post(node.getDoubleValue(), System.currentTimeMillis());
					//storage.insertDataPoint(-1,stream_id, System.currentTimeMillis(), node.getDoubleValue());
					return true;
			} else if (node.get("value") != null) { // it may be value:X
					double value = node.get("value").getDoubleValue();
					// should be resource timestamp
					if (node.get("time") != null) { // it may have time:Y
							if (timeformat != null && !"".equalsIgnoreCase(timeformat.trim())
											&& !"unix".equalsIgnoreCase(timeformat.trim())) {
									currentTime = parseDateTime(node.get("time").getTextValue());
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
	private boolean parseTextResponse(String textBody, Long currentTime)
					throws NumberFormatException, Exception {
			boolean success = false;
			// try {
			double number = 0.0;
			String value = "", time = "";
			if (input_parser != null && !input_parser.equalsIgnoreCase("")) {
					regexPattern = Pattern.compile(input_parser);
					Matcher matcher = regexPattern.matcher(textBody);
					for (int i = 0; (i < numberOfPoints || numberOfPoints < 1) && textBody != null
									&& matcher.find(); i++) {
							// try to match value from the group called :value: otherwise, use the first
							// matching group
							try {
									value = matcher.group("value");
							} catch (IllegalArgumentException iae) {
									try {
											value = matcher.group(dataGroup);
									} catch (IndexOutOfBoundsException iob) {
											value = matcher.group(1);
									}
							}
							number = Double.parseDouble(value);

							// try to match time from the group called :time: otherwise, use the second matching
							// group
							try { time = matcher.group("time");
							} catch (IllegalArgumentException iae) {
								try { time = matcher.group(timeGroup); } 
								catch (IndexOutOfBoundsException iob) { time = null; }
							}

							// if there is a match for time, parse it; otherwise, use the system time (provided
							// in the parameter currentTime)
							if (time != null) {
									if (timeformat != null && !"".equalsIgnoreCase(timeformat)
													&& !"unix".equalsIgnoreCase(timeformat)) {
											// inputParser REGEX should match the whole date/time string! It is
											// not enough to provide the time format only!
											currentTime = parseDateTime(time);
									} else {
											currentTime = Long.parseLong(time);
									}
							}
					}
					//success |= stream.post(number, currentTime);
					storage.insertDataPoint(-1,stream_id,currentTime,number); 
			} else {
					number = Double.parseDouble(textBody);
					//success |= stream.post(number, currentTime);
					storage.insertDataPoint(-1,stream_id,currentTime,number); 
			}
			return success;
	}
	private Long parseDateTime(String input) {
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

	public long getId()								{ return id; }
	public long getResource_id()			{ return resource_id; }
	public long getStream_id()				{ return stream_id; }
	public String getInput_parser()		{ return input_parser; }
	public String getInput_type()			{ return input_type; }
	public String getTimeformat()			{ return timeformat; }
	public int getData_group()				{ return data_group; }
	public int getTime_group()				{ return time_group; }
	public int getNumber_of_points()	{ return number_of_points; }

	public void setId(long id)												{ this.id = id; }
	public void setResource_id(long resource_id)			{ this.resource_id = resource_id; }
	public void setStream_id(long stream_id)					{ this.stream_id = stream_id; }
	public void setInput_parser(String input_parser)	{ this.input_parser = input_parser; }
	public void setInput_type(String input_type)			{ this.input_type = input_type; }
	public void setTimeformat(String timeformat)			{ this.timeformat = timeformat; }
	public void setData_group(int data_group)					{ this.data_group = data_group; }
	public void setTime_group(int time_group)					{ this.time_group = time_group; }
	public void setNumber_of_points(int number_of_points)		{ this.number_of_points = number_of_points; }

	public String toString() {
		return "a parser: "+input_parser;
	}

}
