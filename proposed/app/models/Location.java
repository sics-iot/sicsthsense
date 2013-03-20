package models;

import javax.persistence.*;
import models.*;
import play.Logger;
import play.db.ebean.*;
import play.data.format.*;
import play.data.validation.*;

import com.avaje.ebean.*;

public class Location {

	private double lat;
	private double lon;
	private double elevation; // not used

	private double angle;
	private double azimuth;
	private double radius; // not used

	public Location() {
		lat=0;
		lon=0;
		angle=0;
		azimuth=0;
	}

	public double getLat() {return lat;}
	public double getLon() {return lon;}
	public double getAngle() {return angle;}
	public double getAzimuth() {return azimuth;}

	public void setLatLon(double lat, double lon) {
		this.lat = lat;
		this.lon = lon;
		// angle & azimuth!
	}
	
	public void setPolar(double angle, double azimuth) {
		this.angle = angle;
		this.azimuth = azimuth;
		// lat & lon!
	}

	public double distanceToLatLon(double lat, double lon) {
		return 0.0;
	}

	public double distanceToPolar(double angle, double azimuth) {
		return 0.0;
	}

}
