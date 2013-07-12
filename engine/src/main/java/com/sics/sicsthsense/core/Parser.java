package com.sics.sicsthsense.core;

public class Parser {
	private long id;
	private long resource_id;
	private long stream_id;
	private String input_parser;
	private String input_type;
	private String timeformat;
	private int data_group;
	private int time_group;
	private int number_of_points;

    public Parser() {}
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
			this();
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
}
