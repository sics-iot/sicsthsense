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
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE. */

/* Description: Exponential smoothing function.
 * TODO:
 * */
package se.sics.sicsthsense.core.functions;

import java.util.List;
import java.util.ArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.sics.sicsthsense.core.*;
import se.sics.sicsthsense.jdbi.*;

public class Smooth extends Function {
	private final Logger logger = LoggerFactory.getLogger(Smooth.class);

	public Smooth(StorageDAO storage) {
		super(storage,"smooth");
		this.type = "smooth";
	}

	public List<DataPoint> apply(List<Long> streamIds) {
		int historySize=10;
		double decay=0.3;

		long latest=-1;
		List<DataPoint> rv = new ArrayList<DataPoint>();

		if (streamIds==null) { logger.error("Stream IDs are null!!"); return rv; }
		if (streamIds.size()!=1) { logger.error("Stream antecedants size 1!= "+streamIds.size()); return rv; }

		List<DataPoint> dps = storage.findPointsByStreamId(streamIds.get(0),historySize);
		int count=dps.size(); // we may have less than
		double acc = dps.get(0).getValue();
		logger.info("Acc: "+acc);
		for (int c=dps.size()-1; c>=0; --c) {
			DataPoint dp = dps.get(c);
			logger.info("dp: "+dp.toString());
			logger.info("Value: "+dp.getValue());
			acc = acc*decay + dp.getValue()*(1.0-decay);
			logger.info("Acc: "+acc);
			if (dp.getTimestamp() > latest) {latest = dp.getTimestamp();}
		}
		if (count>0) { //check we have some data points
			rv.add(new DataPoint(latest, acc));
		} else {
			logger.warn("No points to smooth!");
		}
		return rv;
	}

	public String toString() {
		return type;
	}

}
