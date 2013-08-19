package com.sics.sicsthsense.jdbi;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.skife.jdbi.v2.*;
import org.skife.jdbi.v2.sqlobject.*;
import org.skife.jdbi.v2.tweak.*;

import com.yammer.dropwizard.jdbi.*;

import com.sics.sicsthsense.core.*;

public class PointMapper implements ResultSetMapper<DataPoint> {
	public DataPoint map(int index, ResultSet r, StatementContext ctx) throws SQLException {
		return new DataPoint(
				r.getLong("id"), 
				r.getLong("stream_id"),
				r.getLong("timestamp"),
				r.getDouble("data")
				);
	}
}
