package com.sics.sicsthsense.jdbi;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.skife.jdbi.v2.*;
import org.skife.jdbi.v2.sqlobject.*;
import org.skife.jdbi.v2.tweak.*;

import com.yammer.dropwizard.jdbi.*;

import com.sics.sicsthsense.core.*;

public class ResourceMapper implements ResultSetMapper<Resource> {
	public Resource map(int index, ResultSet r, StatementContext ctx) throws SQLException {
		return new Resource(r.getInt("id"), r.getString("label"));
	}
}
