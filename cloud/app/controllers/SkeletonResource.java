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
package controllers;

import logic.Argument;
import models.Resource;
import models.StreamParser;
import play.Logger;

import java.util.ArrayList;
import java.util.List;

public class SkeletonResource {
    private final static Logger.ALogger logger = Logger.of(SkeletonResource.class);

    public Long id;
    public String label = null;
    public String key = null;
    public String pollingUrl = null;
    public String description = null;
    public Long pollingPeriod = 0L;
    public String pollingAuthenticationKey = null;
    public List<StreamParserWrapper> streamParserWrappers = null;

    public SkeletonResource(String label, String key, Long pollingPeriod, String pollingUrl,
                            String pollingAuthenticationKey, List<StreamParserWrapper> streamParserWrappers) {
        this.label = label;
        this.key = key;
        this.pollingPeriod = pollingPeriod;
        this.pollingUrl = pollingUrl;
        this.pollingAuthenticationKey = pollingAuthenticationKey;
        this.streamParserWrappers = streamParserWrappers;
    }

    public SkeletonResource(Resource resource, List<StreamParserWrapper> streamParserWrappers) {
        Argument.notNull(resource);

        this.id = resource.id;
        this.key = resource.getKey();
        this.label = resource.label;
        this.pollingPeriod = resource.pollingPeriod;
        this.pollingUrl = resource.getPollingUrl();
        this.pollingAuthenticationKey = resource.pollingAuthenticationKey;
        this.description = resource.description;
        this.streamParserWrappers = streamParserWrappers;
    }

    public SkeletonResource(Resource resource) {
        Argument.notNull(resource);

        this.id = resource.id;
        this.label = resource.label;
        this.key = resource.getKey();
        this.pollingPeriod = resource.pollingPeriod;
        this.pollingUrl = resource.getPollingUrl();
        this.pollingAuthenticationKey = resource.pollingAuthenticationKey;
        this.description = resource.description;

        if (resource.streamParsers != null) {
            streamParserWrappers = new ArrayList<StreamParserWrapper>(resource.streamParsers.size() + 1);

            for (StreamParser sp : resource.streamParsers) {
                streamParserWrappers.add(new StreamParserWrapper(sp));
            }
        }
    }

    public SkeletonResource(Resource resource, StreamParserWrapper... spws) {
        Argument.notNull(resource);

        this.id = resource.id;
        this.key = resource.getKey();
        this.label = resource.label;
        this.pollingPeriod = resource.pollingPeriod;
        this.pollingUrl = resource.getPollingUrl();
        this.pollingAuthenticationKey = resource.pollingAuthenticationKey;
        this.description = resource.description;

        this.streamParserWrappers = new ArrayList<StreamParserWrapper>(spws.length + 1);
        for (StreamParserWrapper spw : spws) {
            this.streamParserWrappers.add(spw);
        }
    }

    public SkeletonResource(Long id, String label, String key, Long pollingPeriod,
                            String pollingUrl, String pollingAuthenticationKey, StreamParserWrapper... spws) {
        this.id = id;
        this.label = label;
        this.key = key;
        this.pollingPeriod = pollingPeriod;
        this.pollingUrl = pollingUrl;
        this.pollingAuthenticationKey = pollingAuthenticationKey;
        this.streamParserWrappers = new ArrayList<StreamParserWrapper>();
        for (StreamParserWrapper spw : spws) {
            this.streamParserWrappers.add(spw);
        }
    }

    public SkeletonResource() {
    }


    public Resource getResource() {
        Resource src =
                new Resource(null, null, label, pollingPeriod, pollingUrl,
                        pollingAuthenticationKey, description);

        src.streamParsers = new ArrayList<StreamParser>();

        if (streamParserWrappers == null)
            return src;

        for (StreamParserWrapper spw : streamParserWrappers) {
            if (spw.vfilePath != null) {
                StreamParser sp = spw.getStreamParser(src);
                // if (sp != null) { // ignore bad parsers (probably regex failure)
                src.streamParsers.add(sp); // add null streamparsers, to give feedback
                // }
            } else {
                logger.warn("Got a null vfilePath");
            }
        }

        return src;
    }
}
