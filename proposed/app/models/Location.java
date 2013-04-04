package models;

import java.lang.Math;
import javax.persistence.*;
import models.*;
import play.Logger;
import play.db.ebean.*;
import play.data.format.*;
import play.data.validation.*;

import com.avaje.ebean.*;

public class Location extends Model {

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
