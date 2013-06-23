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

/* Description:
 * TODO:
 * */

package models;

import javax.persistence.Id;

import play.db.ebean.Model;

public class Location extends Model {

    /**
     *
     */
    private static final long serialVersionUID = 1968322834503672949L;

    @Id
	public Long id;
	private double lat;
	private double lon;
	private double elevation; // not used
	private double angle;
	private double azimuth;
	private double radius; // not used

	public static Model.Finder<Long, Location> find = new Model.Finder<Long, Location>(Long.class, Location.class);

	public Location() {
		this.lat=0;
		this.lon=0;
		this.angle=0;
		this.azimuth=0;
	}
	public Location(double lat, double lon) {
		this.lat=lat;
		this.lon=lon;
		this.angle=0;
		this.azimuth=0;
	}
	public Location(String lat, String lon) {
		this(Double.parseDouble(lat),Double.parseDouble(lon));
	}

	public double getLat() {return lat;}
	public double getLon() {return lon;}
	public double getAngle() {return angle;}
	public double getAzimuth() {return azimuth;}

	public void setLatLon(double lat, double lon) {
		this.lat = lat;
		this.lon = lon;
		// angle & azimuth!
		// ...
	}
	public void setLatLon(String lat, String lon) {
		setLatLon(Double.parseDouble(lat),Double.parseDouble(lon));
	}

	public void setSphere(double angle, double azimuth) {
		this.angle = angle;
		this.azimuth = azimuth;
		// lat & lon!
		// ...
	}

	public double distanceToLatLon(double lat, double lon) {
		// should account for wrapping
		double latDelta = this.lat - lat;
		double lonDelta = this.lon - lon;
		return Math.sqrt( Math.pow(latDelta,2) + Math.pow(lonDelta,2));
	}

	public String toString() {
		return String.valueOf(lat)+","+String.valueOf(lon);
	}
}
