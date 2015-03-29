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
package se.sics.sicsthsense.core;

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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import se.sics.sicsthsense.jdbi.*;

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
		this.input_type = "application/json";
		this.timeformat = "unix";
		this.data_group = 1; // first element
		this.time_group = 2; // second element
		this.number_of_points = 1; // match count
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
