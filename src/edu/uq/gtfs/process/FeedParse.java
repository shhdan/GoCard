package edu.uq.gtfs.process;

import java.io.FileInputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.google.transit.realtime.GtfsRealtime.FeedEntity;
import com.google.transit.realtime.GtfsRealtime.FeedMessage;

public class FeedParse {

	public static int[] feedParse(String path) throws IOException {

		FileInputStream input = new FileInputStream(path);
		FeedMessage feedMessage = FeedMessage.parseFrom(input);

		// Deal with all entities inside the feed
		List<FeedEntity> entity = feedMessage.getEntityList();
		int countTrip = 0;
		int countAlert = 0;
		int countVehicle = 0;
		Date firstTimeT = new Date();
		Date lastTimeT = new Date();
		Date firstTimeV = new Date();
		Date lastTimeV = new Date();
		System.out.println(entity.get(0).toString());
		System.out.println(entity.get(100).toString());

		for (FeedEntity rec : entity) {
			if (rec.hasTripUpdate()) {
				SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
				try {
					Date parse = format.parse(rec.getTripUpdate().getTrip().getStartTime());
					if (countTrip == 0) {
						// System.out.println(rec.toString());
						firstTimeT = parse;
						lastTimeT = parse;
					} else if (parse.compareTo(firstTimeT) < 0)
						firstTimeT = parse;
					else if (parse.compareTo(lastTimeT) > 0)
						lastTimeT = parse;
				} catch (ParseException pe) {
					System.out.println("ERROR:Parse " + rec.getTripUpdate().getTrip().getStartTime());

				}
				countTrip++;
			} else if (rec.hasAlert()) {
				if (countAlert == 0)
					// System.out.println(rec.toString());
					countAlert++;

			} else if (rec.hasVehicle()) {
				Date time = new Date();
				time.setTime(rec.getVehicle().getTimestamp() * 1000);
				if (countVehicle == 0) {
					// System.out.println(rec.toString());
					firstTimeV = time;
					lastTimeV = time;
				} else if (time.compareTo(firstTimeV) < 0)
					firstTimeV = time;
				else if (time.compareTo(lastTimeV) > 0)
					lastTimeV = time;
				countVehicle++;
			} else
				System.out.println("ERROR:Record " + rec.toString());
		}

		int[] res = { countTrip, countAlert, countVehicle, countTrip + countAlert + countVehicle };
		input.close();
		return res;

		// System.out.println(firstTimeT + " " + lastTimeT + " " +firstTimeV + "
		// " + lastTimeV + " " + countTrip + " " + countAlert + " " +
		// countVehicle);
	}
}
