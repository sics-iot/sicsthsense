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
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

/*
 * Description: TODO:
 */

package models;

import com.avaje.ebean.annotation.Transactional;
import controllers.Utils;
import logic.Argument;
import logic.FileSystem;
import logic.State;
import org.codehaus.jackson.JsonNode;
import play.Logger;
import play.db.ebean.Model;
import play.libs.Json;

import javax.persistence.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Entity
@Table(name = "parsers")
public class StreamParser extends Model {
    private final static Logger.ALogger logger = Logger.of(StreamParser.class);

    /**
     * The serialization runtime associates with each serializable class a version number, called a
     * serialVersionUID
     */
    private static final long serialVersionUID = 2972078391685154152L;

    @Id
    public Long id;

    @ManyToOne(optional = false, cascade = {CascadeType.ALL})
    public Resource resource;

    @ManyToOne(optional = false, cascade = {CascadeType.ALL})
    public Stream stream;

    /**
     * RegEx, Xpath, JSON path, CSV separator
     */
    public String inputParser;

    /**
     * JSON, HTML, text, XML, ... to override MIME contentType of input Right now, it could be
     * defined as application/json, otherwise, request's content is handled as text
     */
    public String inputType = null;

    /**
     * Format of the posted time string to be parsed to UNIX time = "Thu Sep 28 20:29:30 JST 2000";
     * "EEE MMM dd kk:mm:ss z yyyy"; or "" or "unix" for unix timestamp
     */
    public String timeformat = null;

    /**
     * The number of the field containing the value of datapoint (mainly used in parsing CSV ^
     * RegEx) Starts from 1
     */
    public int dataGroup = 1;

    /**
     * The number of the field containing the value of datapoint (mainly used in parsing CSV &
     * RegEx) Starts from 1
     */
    public int timeGroup = 2;

    /**
     * How many points to match? values <= 0 mean parsing all possible matches
     */
    public int numberOfPoints = 1;

    public static Model.Finder<Long, StreamParser> find = new Model.Finder<Long, StreamParser>(
            Long.class, StreamParser.class);

    public StreamParser() {
        super();
    }

    public StreamParser(String inputParser, String inputType, String path,
                        String timeformat, int dataGroup, int timeGroup, int numberOfPoints) {
        super();

        Argument.notEmpty(path);

        this.inputParser = inputParser;
        this.inputType = inputType;
        this.timeformat = timeformat;
        this.dataGroup = dataGroup;
        this.timeGroup = timeGroup;
        this.numberOfPoints = numberOfPoints;
    }

    public StreamParser(Resource resource, String inputParser, String inputType, String path,
                        String timeformat, int dataGroup, int timeGroup, int numberOfPoints) {
        super();

        Argument.notNull(resource);
        Argument.notEmpty(path);

        this.inputParser = inputParser;
        this.inputType = inputType;
        this.resource = resource;
        this.timeformat = timeformat;
        this.dataGroup = dataGroup;
        this.timeGroup = timeGroup;
        this.numberOfPoints = numberOfPoints;

        if (resource.owner != null) {
            Vfile f = FileSystem.read(resource.owner, path);
            this.stream = (f != null) ? f.linkedStream : null;

        }
    }

    public StreamParser updateStreamParser(StreamParser changes) {
        this.inputParser = changes.inputParser;
        this.inputType = changes.inputType;
        this.dataGroup = changes.dataGroup;
        this.numberOfPoints = changes.numberOfPoints;
        this.timeformat = changes.timeformat;
        this.timeGroup = changes.timeGroup;

        save();

        return this;
    }

    public List<DataPoint> parse(String body, String contentType) throws Exception {
        // Logger.info("StreamParser: parseResponse(): post: "+stream.file.getPath());
        if ("application/json".equalsIgnoreCase(inputType)
                || "application/json".equalsIgnoreCase(contentType)) {
            JsonNode jsonBody = Json.parse(body);
            // Logger.info("[StreamParser] as json");
            return parseJson(jsonBody);
        } else {
            // Logger.info("[StreamParser] as text");
            return parseText(body);
        }
    }

    private Long parseDateTime(String input) {
        DateFormat df = new SimpleDateFormat(timeformat, Locale.ENGLISH);
        Long result = -1L;
        try {
            result = df.parse(input).getTime();
        } catch (ParseException e) {
            logger.error("Error while parsing timeformat: " + timeformat + ", input: " + input, e);
        }
        return result;
    }

    /**
     * Parses the request using inputParser as regex and returns the first match
     */
    private List<DataPoint> parseText(String text) throws Exception {
        long currentTime = Utils.currentTime();
        final Vector<DataPoint> data = new Vector<DataPoint>();

        if (text == null || "".equalsIgnoreCase(text)) {
            return data;
        }

        if (inputParser == null && inputParser.equalsIgnoreCase("")) {
            data.add(new DataPointDouble(Double.parseDouble(text), currentTime));
        } else {
            final Pattern pattern = Pattern.compile(inputParser);
            final Matcher matcher = pattern.matcher(text);

            for (int i = 0; (i < numberOfPoints || numberOfPoints < 1) && matcher.find(); i++) {
                String valueString = null;

                // try to match value from the group called :value: otherwise, use the first
                // matching group
                try {
                    valueString = matcher.group("value");
                } catch (IllegalArgumentException iae) {
                    try {
                        valueString = matcher.group(dataGroup);
                    } catch (IndexOutOfBoundsException iob) {
                        valueString = matcher.group(1);
                    }
                }

                String timeString = null;

                // try to match time from the group called :time: otherwise, use the second matching
                // group
                try {
                    timeString = matcher.group("time");
                } catch (IllegalArgumentException iae) {
                    try {
                        timeString = matcher.group(timeGroup);
                    } catch (IndexOutOfBoundsException iob) {
                    }
                }

                // if there is a match for time, parse it; otherwise, use the system time (provided
                // in the parameter currentTime)
                if (timeString != null) {
                    if (timeformat != null && !"".equalsIgnoreCase(timeformat)
                            && !"unix".equalsIgnoreCase(timeformat)) {
                        // inputParser REGEX should match the whole date/time string! It is
                        // not enough to provide the time format only!
                        currentTime = parseDateTime(timeString);
                    } else {
                        currentTime = Long.parseLong(timeString);
                    }
                }

                data.add(new DataPointDouble(Double.parseDouble(valueString), currentTime));
            }
        }

        return data;
    }

    private List<DataPoint> parseCSV(String text) {
        long currentTime = Utils.currentTime();
        final Vector<DataPoint> data = new Vector<DataPoint>();

        final String[] separators = inputParser.split("_SEP_");
        final String fieldSeparator = separators[0];
        final String lineSeparator = (separators.length > 1) ? separators[1] : "";

        final int timeIndex = timeGroup - 1;
        final int dataIndex = dataGroup - 1;

        final String[] lines = text.split(lineSeparator);
        final int maxIndex =
                numberOfPoints < 1 ? Math.min(numberOfPoints, lines.length) : lines.length;

        for (int i = 0; i < maxIndex; i++) {
            final String[] fields = lines[i].split(fieldSeparator);

            if (timeIndex > -1 && timeIndex < fields.length) {
                String time = fields[timeIndex];

                if (timeformat != null && !"".equalsIgnoreCase(timeformat.trim())
                        && !"unix".equalsIgnoreCase(timeformat.trim())) {
                    // inputParser REGEX should match the whole date/time string! It is
                    // not enough to provide the time format only!
                    currentTime = parseDateTime(time);
                } else {
                    currentTime = Long.parseLong(time);
                }
            }

            Double value = Double.parseDouble(fields[dataIndex]);
            data.add(new DataPointDouble(value, currentTime));
        }

        return data;
    }

    /*
     * parses requests as JSON inputParser is used as the path to the nested json node i.e.
     * inputParser could be: room1/sensors/temp/value
     */
    private List<DataPoint> parseJson(JsonNode root) {
        // TODO check concat path against inputParser, get the goal and stop
        // TODO (linear time) form a list of nested path elements from the gui, and
        long currentTime = Utils.currentTime();
        final Vector<DataPoint> data = new Vector<DataPoint>();

        if (root == null) {
            return data;
        }

        String[] levels = inputParser.split("/");
        JsonNode node = root;

        for (int i = 1; i < levels.length; i++) {
            node = node.path(levels[i]);
        }

        if (node.isMissingNode()) {
            // Do nothing
        } else if (node.isValueNode()) { // it is a simple primitive
            data.add(new DataPointDouble(node.getDoubleValue(), currentTime));

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

            data.add(new DataPointDouble(value, currentTime));
        }

        return data;
    }

    @Transactional
    private Stream getOrCreateStream(String path) {
        Argument.absolutePath(path);

        State.notNull(resource);
        State.notNull(resource.owner);

        Stream s = Stream.getByUserPath(resource.owner, path);

        if (s == null) {
            s = Stream.create(path, new Stream(resource.owner, resource));
        }

        return s;
    }

    public static StreamParser create(StreamParser parser) {
        Argument.notNull(parser.resource);
        Argument.notNull(parser.inputParser);

        return create("/" + parser.resource.label, parser);
    }

    public static StreamParser create(String streamPath, StreamParser parser) {
        Argument.absolutePath(streamPath);
        Argument.notNull(parser.resource);
        Argument.notNull(parser.inputParser);

        if (parser.stream == null) {
            parser.stream = parser.getOrCreateStream(streamPath);

            if (parser.stream.type == Stream.StreamType.UNDEFINED) {
                parser.stream.type = Stream.StreamType.DOUBLE;
            }
        }

        parser.save();

        return parser;
    }

    public static void delete(Long id) {
        find.ref(id).delete();
    }

    public static List<StreamParser> forResource(Resource res) {
        return find.where().eq("resource", res).findList();
    }
}
