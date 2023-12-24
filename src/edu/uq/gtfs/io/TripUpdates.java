package edu.uq.gtfs.io;

import java.util.Date;

public final class TripUpdates {

	// Data structure.
	String tripID;
	String routeID;
	String stopID;
	int stopSequence;
	Date arrivalTime = new Date(0);
	int arrivalDelay;
	Date departureTime = new Date(0);
	int departureDelay;
	String vehicleID;
	int arrivalUncertainty;
	int departureUncertainty;
	Date timeStamp = new Date(0);
	String scheRelation;

	// Initiate method.
	public TripUpdates() {

		tripID = "";
		routeID = "";
		stopID = "";
		stopSequence = 0;
		arrivalDelay = 0;
		departureDelay = 0;
		vehicleID = "";
		arrivalUncertainty = 0;
		departureUncertainty = 0;
		scheRelation = "";

	}

	public TripUpdates(String trpID, String rtID, String stID, int stSeq, long arvTime, long depTime, String vehID,
			int arvUncert, int depUncert, long tmStamp, String relation, int arvDelay, int depDelay) {

		tripID = trpID;
		routeID = rtID;
		stopID = stID;
		stopSequence = stSeq;
		arrivalDelay = arvDelay;
		departureDelay = depDelay;
		arrivalTime.setTime(arvTime);
		departureTime.setTime(depTime);
		vehicleID = vehID;
		arrivalUncertainty = arvUncert;
		departureUncertainty = depUncert;
		timeStamp.setTime(tmStamp);
		scheRelation = relation;

	}

	public String getTripID() {
		return tripID;
	}

	public String getRouteID() {
		return routeID;
	}

	public String getStopID() {
		return stopID;
	}

	public int getStopSequence() {
		return stopSequence;
	}

	public String getVehicleID() {
		return vehicleID;
	}

	// Output all the time information.
	public String getArrivalDepartureInfo() {
		return "'"+arrivalTime.toString() + "','" + departureTime.toString() + "','" + arrivalDelay + "','" + departureDelay + "','"
				+ arrivalUncertainty + "','" + departureUncertainty + "','" + scheRelation + "'";
	}
	public String getArrivalTime() {
		return arrivalTime.toString(); 
	}
	public String departureTime() {
		return departureTime.toString(); 
	}
	public int arrivalDelay() {
		return arrivalDelay; 
	}
	public int departureDelay() {
		return departureDelay; 
	}
	public int arrivalUncertainty() {
		return arrivalUncertainty; 
	}
	public int departureUncertainty() {
		return departureUncertainty; 
	}
	public Date getTimeStamp() {
		return timeStamp;
	}
	public String scheRelation() {
		return scheRelation.toString(); 
	}
}
