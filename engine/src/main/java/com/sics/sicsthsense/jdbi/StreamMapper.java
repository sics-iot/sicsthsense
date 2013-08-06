package com.sics.sicsthsense.jdbi;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.skife.jdbi.v2.*;
import org.skife.jdbi.v2.sqlobject.*;
import org.skife.jdbi.v2.tweak.*;

import com.yammer.dropwizard.jdbi.*;

import com.sics.sicsthsense.core.*;

public class StreamMapper implements ResultSetMapper<Stream> {
	public Stream map(int index, ResultSet r, StatementContext ctx) throws SQLException {
		return new Stream(
				r.getLong("id"), 
				"t",
				r.getDouble("latitude"), 
				r.getDouble("longitude"), 
				r.getString("description"),
				false,false,false,
				//r.getBoolean("public_access"), r.getBoolean("public_search"), r.getBoolean("frozen"),
				r.getInt("history_size"),
				r.getLong("last_updated"),
				r.getString("secret_key"),
				r.getLong("owner_id"),
				r.getLong("resource_id"),
				r.getInt("version")
				);
	}
}
