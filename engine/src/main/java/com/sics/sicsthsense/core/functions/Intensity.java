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

public class Intensity extends Function {
	private final Logger logger = LoggerFactory.getLogger(Intensity.class);
    public int AccHistorySize  = 1;
    public int GyroHistorySize = 1;

	public Intensity() {
		super("intensity");
		this.type = "intensity";
	}

    public double magnitude3D(double x, double y, double z) {
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
        double gravity = 0;//9.8;
		logger.info("magnitude: "+magnitude);
        rv.add(new DataPoint(X.get(c).getTimestamp(), magnitude - gravity));
      }
      return rv;
    }

	public List<DataPoint> getGyro(long Xstream, long Ystream, long Zstream) throws Exception {
      List<DataPoint> rv = new ArrayList<DataPoint>();
	  List<DataPoint> X = storage.findPointsByStreamId(Xstream,AccHistorySize);
	  List<DataPoint> Y = storage.findPointsByStreamId(Ystream,AccHistorySize);
	  List<DataPoint> Z = storage.findPointsByStreamId(Zstream,AccHistorySize);
      if (X==null || Y==null || Z==null) {throw new Exception("No acceleration data");}
      for (int c=0; c<X.size(); ++c) {
        double magnitude = X.get(c).getValue() + Y.get(c).getValue() + Z.get(c).getValue();
        rv.add(new DataPoint(X.get(c).getTimestamp(), magnitude));
      }

      return rv;
    }

	public List<DataPoint> apply(List<Long> streamIds) throws Exception {
		List<DataPoint> rv = new ArrayList<DataPoint>();
        int maxPossible=30;

		if (streamIds==null) { logger.error("Stream IDs are null!!"); return rv; }
        int streamCount = streamIds.size();

        if (streamCount!=3 && streamCount!=6  && streamCount!=7) {
          throw new Exception("Error: Stream count wrong (should be 3, 6 or 7)!");
        }
		//logger.info("Intensity stream count correct!!");

        // We need acceleration data
        List<DataPoint> accel = getAccel(streamIds.get(0), streamIds.get(1), streamIds.get(2));

        // Do we have gyro data?
        List<DataPoint> gyro = null;
        if (streamCount>=6) { gyro  = getGyro(streamIds.get(3), streamIds.get(4), streamIds.get(5)); }

        List<DataPoint> heartrate = null;
        if (streamCount>=7) { 
            heartrate = new ArrayList<DataPoint>();
            heartrate.add(new DataPoint(accel.get(0).getTimestamp(), streamIds.get(6)-40)); 
        }

        // combine all the seprate readings
        for (int c=0; c<accel.size(); ++c) {
          double intensity=0.0;

          double acc = accel.get(c).getValue();
          if (acc > 1.0) {
            intensity += 10 - (10.0/acc);
          } else { intensity += 0; }
          logger.info("intensity: "+intensity);

          if (gyro!=null) {
             maxPossible += 10.0;
             double gy = gyro.get(c).getValue();
             if (gy > 1.0) { 
              intensity +=  10.0 - (10.0/gy); 
             } else { intensity += 0; }
          }
          logger.info("intensity: "+intensity);

          if (heartrate!=null) { 
            maxPossible += 10.0;
            double hr = heartrate.get(c).getValue();
            if (hr<40.0) {
              //do nothing, bad reading
            } else {
              if (hr>140.0) {hr=140.0;} // danger! danger!
              hr -= 40; // hr now 0-100
              intensity += hr/10.0;
            }
          }
          logger.info("intensity: "+intensity);
          intensity = (intensity/maxPossible) * 100;
          logger.info("corrected intensity: "+intensity);
          rv.add(new DataPoint(accel.get(0).getTimestamp(), intensity)); // scale to 0-100
        }
		logger.info("apply Intensity end()!!");
		return rv;
	}

	public String toString() {
		return type;
	}

}
