package edu.uq.gtfs.process;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import edu.uq.gtfs.io.*;



public class DataCapture extends TimerTask {

	public static String logPath;
	public final static Logger logger = Logger.getLogger("MyLog");
	public static FileHandler fh;
	public static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
	public static SimpleDateFormat feedFormat = new SimpleDateFormat("yyyy_MM_dd");
	public static String gtfsUrl = "https://gtfsrt.api.translink.com.au/api/realtime/SEQ/";
	public static String feedDir = "feeds" + File.separator;
	
	SimpleDateFormat day_format = new SimpleDateFormat("E");
	SimpleDateFormat date_time = new SimpleDateFormat("yyyyMMddHHmmss");
	SimpleDateFormat date_format = new SimpleDateFormat("yyyyMMdd");
	SimpleDateFormat time_format = new SimpleDateFormat("HHmmss");

	/*
	 * Output data structure: TripID, RouteID, VehicleID, StopID,
	 * StopSequence, ArrivalTime, DepartureTime, ArrivalDelay, DepartureDelay,
	 * ArrivalUncertainty, DepartureUncertainty, ScheduleRelationship
	 */

	// Timer task entry.
	@Override
	public void run() {

		// Set up the proxy for java
//		System.getProperties().put("https.proxyHost", "proxy");
//		System.getProperties().put("https.proxyPort", "8080");
//		System.getProperties().put("proxySet", "true");

		// TimerTask started
		logger.info("Data capturing started at: " + new Date());
		captureTask();
		logger.info("Data capturing finished at: " + new Date());

	}

	// Main data capturing task.
	public void captureTask() {

		try {

			File feedDirectory = new File(feedDir);
			if (!feedDirectory.isDirectory()) {

				feedDirectory.mkdir();

			}

			Date fileCreateDate = new Date();
			String feedName = feedDir + "GTFS_Feed_" + dateFormat.format(fileCreateDate);
			logger.info("Start to download new feed file.");
			

			// Create a temporal file for new GTFS feed.
			File newTripFile = new File(feedName);
			newTripFile.createNewFile();

			// Fetch online feed file and save into local directory
			saveUrl(feedName, gtfsUrl);
			logger.info("New feed file downloaded.");

			
			//Output to txt for analysis
			/*
			Connection con = null;
			ResultSet result = null;
			String url="jdbc:postgresql:seqbus";
			String username="postgres"; //database username
		    String password="shhd1229"; //database password
		    try {
		    	Class.forName("org.postgresql.Driver");
		    	con=DriverManager.getConnection(url,username,password);
		    }
		    catch (Exception ex) { ex.printStackTrace(); }
		    
		    */
		    
		    File writename = new File("C:\\Users\\uqdhe\\Desktop\\Bus_Timetable.txt"); 
	        writename.createNewFile(); 
	        //BufferedWriter outPut = new BufferedWriter(new FileWriter(writename,true));

	        
			// For a new day, create a new file and clear the hash map buckets
			// in order to save memory.
			String outputTripUpdateFileName = feedDir + "Trips_Data_" + feedFormat.format(fileCreateDate) + ".csv";
			File outputTripUpdateFile = new File(outputTripUpdateFileName);
			if (!outputTripUpdateFile.exists()) {
				logger.info("New day comes, create new file.");
				outputTripUpdateFile.createNewFile();
				TripUpdatesExtraction.recordMap.clear();
			}

			// Find all the trip updates in each file.
			List<TripUpdates> trips = new ArrayList<TripUpdates>();
			trips = TripUpdatesExtraction.tripUpdatesExtraction(feedName);


			// Write all the new trip updates into file.
			Iterator<TripUpdates> iter = trips.iterator();

			BufferedWriter outputTripUpdate = new BufferedWriter(new FileWriter(outputTripUpdateFile, true));
			//Statement stmt;

				
		
			while (iter.hasNext()) {

				TripUpdates trip = iter.next();

				/*
				try {
					

					stmt = con.createStatement();
					//get current time
					Date now=new Date();
					//generate sql statement to delete duplicated records before insert new records
					//same trip id
					String sql="delete from translink_real_operation where trip_id = '" + trip.getTripID()
					//same stop id
					+ "' and stop_id = '" +trip.getStopID()
					//old records captured in last 2 hours
					+"' and '" + date_time.format(now)+"' - date_time  <20000";
					//print sql
					System.out.println(sql);
					stmt.executeQuery(sql);		
				
				
					
					//insert new records
					sql="INSERT INTO translink_real_operation VALUES ('"+date_time.format(now)+"', '"
					+ date_format.format(now)+"', '"
					+ time_format.format(now)+"', '"
					+ day_format.format(now)+"', '"
					+ trip.getTripID()+"', '" 
					+ trip.getRouteID() + "','"
					+ trip.getVehicleID() + "','"
					+ trip.getStopID()+ "','"
					+ trip.getStopSequence() + "',"
					+ trip.getArrivalDepartureInfo()+")";
					//print sql
					System.out.println(sql);
					//execute sql
					stmt.executeQuery(sql);
					stmt.close();
				
				} catch (SQLException e) {

					e.printStackTrace();
				}
			    */
				
	
				outputTripUpdate.write(
						trip.getTripID() + "," + trip.getRouteID() + "," + trip.getVehicleID() + "," + trip.getStopID()
								+ "," + trip.getStopSequence() + "," + trip.getTimeStamp() + "," + trip.getArrivalDepartureInfo() + "\n");

			}
			outputTripUpdate.flush();
			outputTripUpdate.close();
			
			String outputVehicleFileName = feedDir + "Vehicle_" + feedFormat.format(fileCreateDate) + ".csv";
			File outputVehicleFile = new File(outputVehicleFileName);
			if (!outputTripUpdateFile.exists()) {
				logger.info("New day comes, create new file.");
				outputVehicleFile.createNewFile();
				TripUpdatesExtraction.recordMap.clear();
			}

			// Find all the trip updates in each file.
			List<VehiclePositions> vehicles = new ArrayList<VehiclePositions>();
			vehicles = VehiclePositionExtraction.vehiclePositionExtraction(feedName);

			newTripFile.delete();

			// Write all the new trip updates into file.
			Iterator<VehiclePositions> iter_ve = vehicles.iterator();

			BufferedWriter outputVehicle = new BufferedWriter(new FileWriter(outputVehicleFile, true));

				
		
			while (iter_ve.hasNext()) {

				VehiclePositions vehicle = iter_ve.next();
					
				outputVehicle.write(
						vehicle.getTripID() + "," + vehicle.getRouteID() + "," + vehicle.getVehicleID() + "," + vehicle.getDirection()
								+ "," + vehicle.getLatitude() + "," + vehicle.getLongitude() +  "," + vehicle.getTimeStamp() +  "," + vehicle.getStartDay()
								+ "," + vehicle.getStartTime() +  "," + vehicle.occStatus() +  "," + vehicle.scheRelation() +"\n");

			}
			outputVehicle.flush();
			outputVehicle.close();
			
			newTripFile.delete();

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static void main(String args[]) {

		if (args.length == 0) {

			// Log directory
			logPath = "logs" + File.separator + "DataCapture_" + dateFormat.format(new Date()) + ".log";

			// Set up logger.
			try {

				File log = new File(logPath);
				if (!log.exists()) {

					log.getParentFile().mkdirs();
					log.createNewFile();

				}

				fh = new FileHandler(logPath);
				logger.addHandler(fh);
				SimpleFormatter formatter = new SimpleFormatter();
				fh.setFormatter(formatter);

			} catch (IOException e) {

				e.printStackTrace();

			}

			logger.info("Task started at: " + new Date());
			TimerTask dataCap = new DataCapture();

			// running timer task as daemon thread
			Timer timer = new Timer(true);

			// Timer will be waken up every 10 seconds and start the job.
			timer.scheduleAtFixedRate(dataCap, 0, 10 * 1000);
			logger.info("Data capturing started");

			try {

				// Sleep for ten years :P
				long duration = 1000 * 3600 * 24 * 365;
				Thread.sleep(duration);

			} catch (InterruptedException e) {

				e.printStackTrace();

			}
		}
	}

	// Download GTFS file from "urlString", and save as name "fileName"
	public void saveUrl(String fileName, String urlString) throws IOException {

		// Create a new trust manager that trust all certificates
		TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {

			public java.security.cert.X509Certificate[] getAcceptedIssuers() {
				return null;
			}

			public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) {
			}

			public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType) {
			}
		} };

		// Activate the new trust manager
		try {

			SSLContext sc = SSLContext.getInstance("SSL");
			sc.init(null, trustAllCerts, new java.security.SecureRandom());
			HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

		} catch (Exception e) {

			e.printStackTrace();
			logger.warning("Authorification error!");

		}

		// Download the file
		URL url = new URL(urlString);
		ReadableByteChannel rbc = Channels.newChannel(url.openStream());
		FileOutputStream fos = new FileOutputStream(fileName);
		fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
		fos.close();

	}

	// write log to the file "logPath"
	public static Logger setUpLog(String logPath) {

		try {

			// This block configure the logger with handler and formatter
			fh = new FileHandler(logPath);
			logger.addHandler(fh);
			SimpleFormatter formatter = new SimpleFormatter();
			fh.setFormatter(formatter);

		} catch (SecurityException e) {

			e.printStackTrace();

		} catch (IOException e) {

			e.printStackTrace();

		}
		return logger;

	}
}