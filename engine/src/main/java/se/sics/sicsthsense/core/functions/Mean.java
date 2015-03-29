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

/* Description:
 * TODO:
 * */
package se.sics.sicsthsense.core.functions;

import java.util.List;
import java.util.ArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.sics.sicsthsense.core.*;
import se.sics.sicsthsense.jdbi.*;

public class Mean extends Function {
	private final Logger logger = LoggerFactory.getLogger(Mean.class);

	public Mean(StorageDAO storage) {
		super(storage, "mean");
		this.type = "mean";
	}

	public List<DataPoint> apply(List<Long> streamIds) {
		int k=0;
		double total=0.0;
		long latest=-1;
		List<DataPoint> rv = new ArrayList<DataPoint>();

		if (streamIds==null) {
			logger.error("Stream IDs are null!!");
			return rv;
		}
		for (long streamId: streamIds) {
			List<DataPoint> dps = storage.findPointsByStreamId(streamId,1);
			if (dps.size()>0) {
				DataPoint dp = dps.get(0);
				k++;
				total += dp.getValue();
				if (dp.getTimestamp() > latest) {latest = dp.getTimestamp();}
			}
		}
		if (k>0) { //check we have some data points
			rv.add(new DataPoint(latest, (total/k) ));
			//logger.info("Calculated mean: "+(total/k));
		} else {
			logger.warn("No points to calculate mean of!");
		}
		return rv;
	}

	public String toString() {
		return type;
	}

}
