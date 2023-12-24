package edu.uq.gtfs.io;

import java.util.Date;

public final class VehiclePositions {

	// Data structure.
	String tripID;
	String routeID;
	String startTime;
	String startDate;
	int direction;
	String vehicleID;
	float latitude;
	float longitude;
	int occupancyStatus;
	Date timeStamp = new Date(0);
	String scheRelation;

	// Initiate method.
	public VehiclePositions() {

		tripID = "";
		routeID = "";
		startTime = "";
		startDate = "";
		direction = 0;
		vehicleID = "";
		latitude = 0;
		longitude = 0;
		occupancyStatus = 0;
		scheRelation = "";

	}

	public VehiclePositions(String trpID, String rtID, String stTime, String stDay, int dir, String vehID,
			float lat, float lon, int occ, long tmStamp, String relation) {

		tripID = trpID;
		routeID = rtID;
		startTime = stTime;
		startDate = stDay;
		direction = dir;
		vehicleID = vehID;
		latitude = lat;
		longitude = lon;
		occupancyStatus = occ;
		timeStamp.setTime(tmStamp);
		scheRelation = relation;

	}

	public String getTripID() {
		return tripID;
	}

	public String getRouteID() {
		return routeID;
	}

	public String getStartTime() {
		return startTime;
	}
	
	public String getStartDay() {
		return startDate;
	}
	
	public int getDirection() {
		return direction;
	}

	public String getVehicleID() {
		return vehicleID;
	}

	public float getLatitude() {
		return latitude;
	}
	
	public float getLongitude() {
		return longitude;
	}
	
	public int occStatus() {
		return occupancyStatus;
	}

	public Date getTimeStamp() {
		return timeStamp;
	}
	
	public String scheRelation() {
		return scheRelation.toString(); 
	}
}
