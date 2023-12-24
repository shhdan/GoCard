package edu.uq.gtfs.process;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.google.transit.realtime.GtfsRealtime.FeedEntity;
import com.google.transit.realtime.GtfsRealtime.FeedMessage;
import com.google.transit.realtime.GtfsRealtime.TripUpdate.StopTimeUpdate;

import edu.uq.gtfs.io.TripUpdates;

enum ScheRelation {

	SCHEDULED, ADDED, UNSCHEDULED, CANCELED

}

public class TripUpdatesExtraction {

	// Create a hash map to handle all existing trip updates in one day.
	public static Map<String, String> recordMap = new HashMap<String, String>();

	public static List<TripUpdates> tripUpdatesExtraction(String path) throws IOException {

		FileInputStream input = new FileInputStream(path);
		FeedMessage feedMessage = FeedMessage.parseFrom(input);

		// Deal with all entities inside the feed
		List<FeedEntity> entity = feedMessage.getEntityList();
		List<TripUpdates> tripUpdate = new ArrayList<TripUpdates>();

		String tripID;
		String routeID;
		String stopID;
		int stopSequence;
		long arrivalTime;
		int arrivalDelay;
		int arvUncert;
		long departureTime;
		int departureDelay;
		int depUncert;
		String vehicleID;
		long timeStamp;
		String mapKey;
		int dupCount = 0;

		for (FeedEntity rec : entity) {
			
			if (rec.hasTripUpdate()) {
				
				// Every trip record inside a trip update has the same tripID, routeID, vehicleID and time stamp, but different stopID and stopSequence. 
				tripID = rec.getTripUpdate().getTrip().getTripId();
				routeID = rec.getTripUpdate().getTrip().getRouteId();
				vehicleID = rec.getTripUpdate().getVehicle().getId();
				timeStamp = rec.getTripUpdate().getTimestamp() * 1000;

				switch (rec.getTripUpdate().getTrip().getScheduleRelationship()) {

				case CANCELED:

					mapKey = "CANCELED" + "," + tripID + "," + routeID + "," + vehicleID;

					if (!recordMap.containsKey(mapKey)) {

						recordMap.put(mapKey, String.valueOf(timeStamp));
						tripUpdate.add(
								new TripUpdates(tripID, routeID, "", 0, 0, 0, vehicleID, 0, 0, 0, "CANCELED", 0, 0));

					} else
						dupCount++;
					break;

				case UNSCHEDULED:

					mapKey = "UNSCHEDULED" + "," + tripID + "," + routeID + "," + vehicleID;

					if (!recordMap.containsKey(mapKey)) {

						recordMap.put(mapKey, String.valueOf(timeStamp));
						tripUpdate.add(
								new TripUpdates(tripID, routeID, "", 0, 0, 0, vehicleID, 0, 0, 0, "UNSCHEDULED", 0, 0));

					} else
						dupCount++;
					break;

				case ADDED:

					Iterator<StopTimeUpdate> stopTimeUpAdd = rec.getTripUpdate().getStopTimeUpdateList().listIterator();
					while (stopTimeUpAdd.hasNext()) {

						StopTimeUpdate update = stopTimeUpAdd.next();
						stopID = update.getStopId();
						stopSequence = update.getStopSequence();
						arrivalTime = update.getArrival().getTime() * 1000;
						arrivalDelay = update.getArrival().getDelay();
						arvUncert = update.getArrival().getUncertainty();
						departureTime = update.getDeparture().getTime() * 1000;
						departureDelay = update.getDeparture().getDelay();
						depUncert = update.getDeparture().getUncertainty();

						mapKey = "ADDED" + "," + tripID + "," + routeID + "," + vehicleID + "," + stopID + ","
								+ stopSequence + "," + String.valueOf(arrivalTime) + ","
								+ String.valueOf(departureTime);

						if (!recordMap.containsKey(mapKey)) {

							recordMap.put(mapKey, String.valueOf(timeStamp));
							tripUpdate.add(new TripUpdates(tripID, routeID, stopID, stopSequence, arrivalTime,
									departureTime, vehicleID, arvUncert, depUncert, timeStamp, "ADDED", arrivalDelay,
									departureDelay));

						} else
							dupCount++;
					}
					break;

				case SCHEDULED:

					Iterator<StopTimeUpdate> stopTimeUpSche = rec.getTripUpdate().getStopTimeUpdateList()
							.listIterator();
					while (stopTimeUpSche.hasNext()) {

						StopTimeUpdate update = stopTimeUpSche.next();
						stopID = update.getStopId();
						stopSequence = update.getStopSequence();
						arrivalTime = update.getArrival().getTime() * 1000;
						arrivalDelay = update.getArrival().getDelay();
						arvUncert = update.getArrival().getUncertainty();
						departureTime = update.getDeparture().getTime() * 1000;
						departureDelay = update.getDeparture().getDelay();
						depUncert = update.getDeparture().getUncertainty();

						mapKey = "SCHEDULED" + "," + tripID + "," + routeID + "," + vehicleID + "," + stopID + ","
								+ stopSequence + "," + String.valueOf(arrivalTime) + ","
								+ String.valueOf(departureTime);

						if (!recordMap.containsKey(mapKey)) {

							recordMap.put(mapKey, String.valueOf(timeStamp));
							tripUpdate.add(new TripUpdates(tripID, routeID, stopID, stopSequence, arrivalTime,
									departureTime, vehicleID, arvUncert, depUncert, timeStamp, "SCHEDULED",
									arrivalDelay, departureDelay));

						} else
							dupCount++;
					}
					break;

				}
			}
		}
		input.close();

		// Output the total number of duplicated records.
		DataCapture.logger.info("Duplicated record number:" + dupCount);

		return tripUpdate;
	}
}