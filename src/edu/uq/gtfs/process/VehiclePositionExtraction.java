package edu.uq.gtfs.process;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.google.transit.realtime.GtfsRealtime.FeedEntity;
import com.google.transit.realtime.GtfsRealtime.FeedMessage;
import com.google.transit.realtime.GtfsRealtime.VehiclePosition;

import edu.uq.gtfs.io.VehiclePositions;


public class VehiclePositionExtraction {


	public static List<VehiclePositions> vehiclePositionExtraction(String path) throws IOException {

		FileInputStream input = new FileInputStream(path);
		FeedMessage feedMessage = FeedMessage.parseFrom(input);

		// Deal with all entities inside the feed
		List<FeedEntity> entity = feedMessage.getEntityList();
		List<VehiclePositions> vehicles = new ArrayList<VehiclePositions>();

		String tripID;
		String routeID;
		String startTime;
		String startDate;
		int direction;
		String vehicleID;
		float latitude;
		float longitude;
		int occupancyStatus;
		long timeStamp;
		String scheRelation;
	

		for (FeedEntity rec : entity) {
			
			if (rec.hasVehicle()) {
				
				tripID = rec.getVehicle().getTrip().getTripId();
				routeID = rec.getVehicle().getTrip().getRouteId();
				startTime = rec.getVehicle().getTrip().getStartTime();
				startDate = rec.getVehicle().getTrip().getStartDate();
				direction = rec.getVehicle().getTrip().getDirectionId();				
				vehicleID = rec.getVehicle().getVehicle().getId();
				latitude = rec.getVehicle().getPosition().getLatitude();
				longitude = rec.getVehicle().getPosition().getLongitude();
				occupancyStatus = rec.getVehicle().getOccupancyStatus().getNumber();
				timeStamp = rec.getVehicle().getTimestamp() * 1000;
				scheRelation = rec.getVehicle().getTrip().getScheduleRelationship().name();
				
				vehicles.add(new VehiclePositions(tripID, routeID, startTime, startDate, direction, vehicleID,
						latitude, longitude, occupancyStatus, timeStamp, scheRelation));

				
			}
		}
		input.close();

		return vehicles;
	}
}