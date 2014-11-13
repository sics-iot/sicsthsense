/*
 * Copyright (c) 2013, Swedish Institute of Computer Science
 * All rights reserved.
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *	   * Redistributions of source code must retain the above copyright
 *		 notice, this list of conditions and the following disclaimer.
 *	   * Redistributions in binary form must reproduce the above copyright
 *		 notice, this list of conditions and the following disclaimer in the
 *		 documentation and/or other materials provided with the distribution.
 *	   * Neither the name of The Swedish Institute of Computer Science nor the
 *		 names of its contributors may be used to endorse or promote products
 *		 derived from this software without specific prior written permission.
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
import se.sics.sicsthsense.Utils;
import se.sics.sicsthsense.jdbi.*;

public class Intensity extends Function {
	private final Logger logger = LoggerFactory.getLogger(Intensity.class);
	public int AccHistorySize  = 1;
	public int GyroHistorySize = 1;
	public long streamId;
	private boolean DEBUG = false;

	public Intensity(StorageDAO storage, long streamId) {
		super(storage, "intensity");
		this.type = "intensity";
		this.streamId = streamId;
	}

	public double magnitude3D(double x, double y, double z) {
	  //logger.error("X Y Z: "+x+" "+y+" "+z);
	  return Math.sqrt(x*x + y*y + z*z);
	}

	// Given 3 stream ID of acceleration, calculate the acceleration intensity history
	public List<DataPoint> getAccel(long Xstream, long Ystream, long Zstream) throws Exception {
	  List<DataPoint> rv = new ArrayList<DataPoint>();
	  List<DataPoint> X = storage.findPointsByStreamId(Xstream,AccHistorySize);
	  List<DataPoint> Y = storage.findPointsByStreamId(Ystream,AccHistorySize);
	  List<DataPoint> Z = storage.findPointsByStreamId(Zstream,AccHistorySize);
	  if (X==null || Y==null || Z==null) {throw new Exception("No acceleration data");}
	  for (int c=0; c<X.size(); ++c) {
		double magnitude = magnitude3D(X.get(c).getValue(), Y.get(c).getValue(), Z.get(c).getValue());
		if (DEBUG) {logger.warn("Accel: "+X.get(c).getValue()+" "+ Y.get(c).getValue()+" "+ Z.get(c).getValue());}
		double gravity = 10.0;//9.8;
		if (DEBUG) {logger.info("magnitude: "+magnitude);}
		magnitude -= gravity;
		if (DEBUG) {logger.info("magnitude - gravity: "+magnitude);}
		if (magnitude<0) {magnitude=0.0;}
		rv.add(new DataPoint(X.get(c).getTimestamp(), magnitude));
	  }
	  return rv;
	}

	// calculate the gyro scope history
	public List<DataPoint> getGyro(long Xstream, long Ystream, long Zstream) throws Exception {
	  List<DataPoint> rv = new ArrayList<DataPoint>();
	  List<DataPoint> X = storage.findPointsByStreamId(Xstream,AccHistorySize);
	  List<DataPoint> Y = storage.findPointsByStreamId(Ystream,AccHistorySize);
	  List<DataPoint> Z = storage.findPointsByStreamId(Zstream,AccHistorySize);
	  if (X==null || Y==null || Z==null) {throw new Exception("No gyro data");}
	  for (int c=0; c<X.size(); ++c) {
		double magnitude = Math.abs(X.get(c).getValue()) + Math.abs(Y.get(c).getValue()) + Math.abs(Z.get(c).getValue());
		if (DEBUG) {logger.error("Gyro: "+Math.abs(X.get(c).getValue()) +" "+ Math.abs(Y.get(c).getValue()) +" "+ Math.abs(Z.get(c).getValue())+" "+magnitude);}
		rv.add(new DataPoint(X.get(c).getTimestamp(), magnitude));
	  }

	  return rv;
	}

	public List<DataPoint> apply(List<Long> streamIds) throws Exception {
		List<DataPoint> rv = new ArrayList<DataPoint>();
		int maxPossible=10; // max possible intensity score

		if (streamIds==null) { logger.error("Stream IDs are null!!"); return rv; }
		int streamCount = streamIds.size(); // how many input streams?

		if (streamCount!=3 && streamCount!=6  && streamCount!=7) { throw new Exception("Error: Stream count wrong (should be 3, 6 or 7)!"); }

		//logger.info("Stream ids: "+streamIds.get(0)+" "+ streamIds.get(1)+" "+streamIds.get(2));
		// We need acceleration data
		List<DataPoint> accel = getAccel(streamIds.get(0), streamIds.get(1), streamIds.get(2));
		// Do we have gyro data?
		List<DataPoint> gyro = null;
		if (streamCount>=6) { gyro = getGyro(streamIds.get(3), streamIds.get(4), streamIds.get(5)); }
		// Do we have heartrate?
		List<DataPoint> heartrate = null;
		if (streamCount>=7) { heartrate = storage.findPointsByStreamId(streamIds.get(6),1); }

		// combine all the separate readings
		for (int c=0; c<accel.size(); ++c) {
			double intensity=0.0;
			double acc = accel.get(c).getValue();
			//logger.info("acc raw: "+acc);
			double accFudge = 0.25;
			acc = 1+(acc*accFudge); // tune the value
			//logger.info("fudge multiply: "+acc);
			intensity += 10 - (10.0/acc);
			if (DEBUG) {logger.info("intensity with acc: "+intensity+" / "+maxPossible+" = "+(intensity/maxPossible));}

		if (gyro!=null) {
			double gy = gyro.get(c).getValue();
			double gyFudge=2;
			gy = gy * gyFudge;
			if (gy > 0.5) {
				maxPossible += 5.0;
				intensity += 5.0 - (5.0/gy);
			 }
		}
		if (DEBUG) {logger.info("intensity with gyro: "+intensity+" / "+maxPossible+" = "+(intensity/maxPossible));}

		if (heartrate!=null) {
			double hr = heartrate.get(c).getValue();
			if (DEBUG) {logger.warn("heartrate: "+hr);}
			if (hr<60.0) {
			  //do nothing, bad reading
			} else {
			  maxPossible += 3.0;
			  if (hr>120.0) {hr=120.0;} // danger! danger!
			  hr -= 60; // hr now 0-60
			  intensity += hr/20.0;
			}
		  }

		  if (DEBUG) {logger.info("intensity with all components: "+intensity+" / "+maxPossible+" = "+(intensity/maxPossible));}
		  intensity = (intensity/maxPossible) * 100;
		  if (intensity<0.0) {intensity=0.0;}

		// do some smoothing
		List<DataPoint> dps = storage.findPointsByStreamId(this.streamId,2);
		if (dps==null)     { logger.error("Stream not valid!"); return rv;}

		// group statistics
		if (false) {
			// include stats from the group Stream
			List<DataPoint> groupdps = storage.findPointsByStreamId(this.streamId,1);
			if (groupdps!=null && groupdps.size()==1) {
				maxPossible += 5;
				intensity += dps.get(0).getValue() / 20; // reduce from %
			}
		}

		double decayFactor=0.50;// how much of the *current* value to use?
		double smoothIntensity=0.0;
		double prevValue;
		smoothIntensity = intensity; // just use the current value

		if (dps.size()>0) {//use a proportion of the prev value
			// get latest point that is not of the same time
			if (dps.get(0).getTimestamp() == (accel.get(0).getTimestamp())) {
				if (dps.size()>1) { prevValue = dps.get(1).getValue();
				} else {prevValue = intensity;} // no prev values
			} else { prevValue = dps.get(0).getValue(); }
			//logger.info("prev Intensity "+prevValue+" current "+intensity);
			//logger.info("decay Intensity "+ (prevValue*(1-decayFactor)) +" % "+ (intensity*decayFactor));
			smoothIntensity = prevValue*(1-decayFactor) + intensity*decayFactor;
		} else {
			smoothIntensity = intensity; // just use the current value
		}
		//if (smoothIntensity<0.0)   {smoothIntensity=0.0;}
		//if (smoothIntensity>100.0) {smoothIntensity=100.0;}

		rv.add(new DataPoint(accel.get(0).getTimestamp(), smoothIntensity)); // scale to 0-100

		// add this point for others
		//DataPoint groupdp = new DataPoint(accel.get(0).getTimestamp(), smoothIntensity); // scale to 0-100
		//groupdp.setStreamId(9999);
		//Utils.insertDataPoint(storage,groupdp);
		}
		return rv;
	}

	public String toString() {
		return type;
	}

}
