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
package controllers;

import models.Resource;
import models.StreamParser;
import play.data.validation.Constraints;

public class StreamParserWrapper {

    public Long parserId;

    @Constraints.Required
    public String vfilePath;
    // @Constraints.Required
    /**
     * RegEx, Xpath, JSON path
     */
    public String inputParser;
    /**
     * JSON, HTML, text, XML, ... to override MIME contentType of input Right now, it could be
     * defined as application/json, otherwise, request's content is handled as text
     */
    public String inputType;

    public String timeformat;

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

    /**
     * How many points to match? values <= 0 mean parsing all possible matches
     */
    int numberOfPoints = 1;

    public StreamParserWrapper(Long id, String vfilePath, String inputParser, String inputType,
                               String timeformat, int dataGroup, int timeGroup, int numberOfPoints) {
        this.parserId = id;
        this.vfilePath = vfilePath;
        this.inputType = inputType;
        this.inputParser = inputParser;
        this.timeformat = timeformat;
        this.dataGroup = dataGroup;
        this.timeGroup = timeGroup;
        this.numberOfPoints = numberOfPoints;
    }

    public StreamParserWrapper(String vfilePath, String inputParser, String inputType,
                               String timeformat, int dataGroup, int timeGroup, int numberOfPoints) {
        this(null, vfilePath, inputParser, inputType, timeformat, dataGroup, timeGroup,
                numberOfPoints);
    }

    public StreamParserWrapper(StreamParser sp) {
        this.inputType = sp.inputType;
        this.inputParser = sp.inputParser;
        this.parserId = sp.id;
        this.timeformat = sp.timeformat;
        this.dataGroup = sp.dataGroup;
        this.timeGroup = sp.timeGroup;
        this.numberOfPoints = sp.numberOfPoints;

        if (sp.stream != null && sp.stream.file != null) {
            this.vfilePath = sp.stream.file.getPath();
        }
    }

    public StreamParserWrapper() {
    }

    public StreamParser getStreamParser(Resource resource) {
        StreamParser sp =
                new StreamParser(resource, inputParser, inputType, vfilePath, timeformat,
                        dataGroup, timeGroup, numberOfPoints);
        sp.id = parserId;
        return sp;
    }
}
