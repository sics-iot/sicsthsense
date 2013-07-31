package com.sics.sicsthsense.jdbi;

import java.text.ParseException;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.skife.jdbi.v2.*;
import org.skife.jdbi.v2.sqlobject.*;
import org.skife.jdbi.v2.tweak.*;
import com.yammer.dropwizard.jdbi.*;

import com.sics.sicsthsense.core.*;

public class UserMapper implements ResultSetMapper<User> {

	public User map(int index, ResultSet r, StatementContext ctx) throws SQLException {
		return new User(
			r.getInt("id"), 
			r.getString("user_name"),
			r.getString("first_name"),
			r.getString("last_name"),
			r.getString("description"),
			r.getString("latitude"),
			r.getString("longitude"),
			r.getString("creation_date"),
			r.getString("last_login")
			//, r.getString("admin")
		);
	}


}
