package models;

// Liam: naive copy of Resource.java, just to have dummy

import java.util.*;

import javax.persistence.*;

import org.codehaus.jackson.JsonNode;

import play.Logger;
import play.db.ebean.*;
import play.data.format.*;
import play.data.validation.*;

import com.avaje.ebean.*;

import controllers.Utils;
import play.libs.F.*;
import play.libs.WS;

import java.nio.ByteBuffer;
import java.util.regex.*;
import org.w3c.dom.Document;
import play.libs.XPath;


@Entity
@Table(name = "pipeline", uniqueConstraints = { @UniqueConstraint(columnNames = {
		"end_point_id", "path" }) })
public class Pipeline extends Resource {
	public enum TRANSFORM {
		REGEX, XPATH
	}
	public TRANSFORM type;
	public Pattern regex;
	public String xpathString;

	public Pipeline(String path, EndPoint endPoint) {
		super(path,endPoint);
	}

	// should be mime types!
	public float transform(float input) {
		return 0.0f;
	}
	public ByteBuffer transform(ByteBuffer input) {
		return input;
	}

	public String transform(String input) {
		// read from input resource

		switch (type) {
			case REGEX:
				return matchRegex(input);
			case XPATH:
				return matchXPath(input);
			default:
				return input;
		}
	}

	public void setRegex(String regexString) {
		regex = Pattern.compile(regexString);
		type = TRANSFORM.REGEX;
	}

	public String matchRegex(String input) {
		Matcher m = regex.matcher(input);
		if (m.find()) {
			return m.group();
		} else {
			return "Not Match";
		}
	}


	public void setXPath(String xpathString) {
		this.xpathString = xpathString;
		type = TRANSFORM.XPATH;
	}

	public String matchXPath(String input) {
		org.w3c.dom.Document xmlDoc = null;
		return XPath.selectText(xpathString,xmlDoc);
	}

}
