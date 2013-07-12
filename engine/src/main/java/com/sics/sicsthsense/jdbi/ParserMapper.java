package com.sics.sicsthsense.jdbi;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.skife.jdbi.v2.*;
import org.skife.jdbi.v2.sqlobject.*;
import org.skife.jdbi.v2.tweak.*;

import com.yammer.dropwizard.jdbi.*;

import com.sics.sicsthsense.core.*;

public class ParserMapper implements ResultSetMapper<Parser> {
	public Parser map(int index, ResultSet r, StatementContext ctx) throws SQLException {
		return new Parser(
				r.getInt("id"), 
				r.getInt("resource_id"), 
				r.getInt("stream_id"), 
				r.getString("input_parser"),
				r.getString("input_type"),
				r.getString("timeformat"),
				r.getInt("data_group"),
				r.getInt("time_group"),
				r.getInt("number_of_points")
				);
	}
}
