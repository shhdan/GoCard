/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.gocard.dataprocess;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

/**
 *
 * @author uqdhe
 */
public class LoadData {

	public static String stopFile = "C:\\Users\\uqdhe\\Desktop\\stopinfo.csv";
	public static String journeyfile = "C:\\Users\\uqdhe\\Desktop\\Mywork\\GoCard\\BrisbaneGoCardData2013\\Data\\12\\journey_b.csv";
	public static String garbagefile = "C:\\Users\\uqdhe\\Desktop\\Mywork\\GoCard\\BrisbaneGoCardData2013\\Data\\12\\garbage_b.csv";
	public static String csvFile = "C:\\Users\\uqdhe\\Desktop\\Mywork\\GoCard\\BrisbaneGoCardData2013\\Data\\Original\\february_2013.csv";
	public static HashMap<String, String> stopinfo = new HashMap<String, String>();

	public static void main(String[] args) throws FileNotFoundException, IOException {

		BufferedReader inputStream_1 = new BufferedReader(new InputStreamReader(new FileInputStream(stopFile)));
		String line_1 = "";
		String csvSplitBy_1 = ";";

		while ((line_1 = inputStream_1.readLine()) != null) {
			String[] rowdata = new String[5];
			rowdata = line_1.split(csvSplitBy_1);

			for (int j = 0; j < 4; j++) {
				if (rowdata[j] == null)
					continue;
				else {
					int b = rowdata[j].indexOf("\"");
					if (b == -1 || b > 1) {
						continue;
					} else {

						int e = rowdata[j].lastIndexOf("\"");

						rowdata[j] = rowdata[j].substring(b + 1, e);
					}
				}
			}
			stopinfo.put(rowdata[0], rowdata[1]);
		}
		inputStream_1.close();

		BufferedReader inputStream = new BufferedReader(new InputStreamReader(new FileInputStream(csvFile)));
		BufferedWriter outputWriter_journey = new BufferedWriter(
				new OutputStreamWriter(new FileOutputStream(journeyfile)));
		BufferedWriter outputWriter_garbage = new BufferedWriter(
				new OutputStreamWriter(new FileOutputStream(garbagefile)));
		File journey = new File(
				"C:\\Users\\uqdhe\\Desktop\\Mywork\\GoCard\\BrisbaneGoCardData2013\\Data\\12\\journey_b.csv");
		File garbage = new File(
				"C:\\Users\\uqdhe\\Desktop\\Mywork\\GoCard\\BrisbaneGoCardData2013\\Data\\12\\garbage_b.csv");

		String line = "";
		String csvSplitBy = ",";

		int i = 0;
		while ((line = inputStream.readLine()) != null) {

			String[] rowdata = new String[15];
			rowdata = line.split(csvSplitBy);
			i++;
			if (i == 1)
				continue;

			// if(i > 10000)
			// break;

			for (int j = 0; j < 15; j++) {
				if (rowdata[j] == null)
					continue;
				else {
					int b = rowdata[j].indexOf("\"");
					if (b == -1 || b > 1) {
						continue;
					} else {

						int e = rowdata[j].lastIndexOf("\"");
						if (e < 1) {
							rowdata[j] = rowdata[j] + rowdata[j + 1];
							e = rowdata[j].lastIndexOf("\"");
							int k = j + 1;
							while (k < 14) {
								rowdata[k] = rowdata[k + 1];
								k++;
							}
							rowdata[14] = null;
						}

						rowdata[j] = rowdata[j].substring(b + 1, e);
					}
				}
			}

			rowdata[1] = convertStringToDate(rowdata[1]);
			rowdata[8] = convertStringToDatetime(rowdata[8]);
			rowdata[9] = convertStringToDatetime(rowdata[9]);

			int detect = DetectDataBeforeLoad(rowdata, garbage, outputWriter_garbage);

			if (detect != 0) {
				// Data has been input into garbage table
				continue;
			}

			String Boarding_ID = stopinfo.get(rowdata[11]);
			String Alighting_ID = stopinfo.get(rowdata[12]);

			// System.out.print("***"+Boarding_ID);

			if (Boarding_ID == null || Alighting_ID == null) {

				DataInput(rowdata, garbage, outputWriter_garbage);
				continue;
			}

			rowdata[11] = Boarding_ID;
			rowdata[12] = Alighting_ID;

			if (!journey.exists()) {
				journey.createNewFile();
			}

			for (int k = 0; k < 15; k++) {
				outputWriter_journey.append(rowdata[k] + ",");
			}
			outputWriter_journey.newLine();

		}
		inputStream.close();
		outputWriter_journey.flush();
		outputWriter_journey.close();
		outputWriter_garbage.flush();
		outputWriter_garbage.close();
	}

	public static String convertStringToDate(String dateString) {

		java.util.Date date = null;
		String format_date = null;
		DateFormat df = new SimpleDateFormat("dd-MMM-yy");
		DateFormat mysql_df = new SimpleDateFormat("yyyy-MM-dd");
		try {
			date = df.parse(dateString);
			format_date = mysql_df.format(date);
		} catch (Exception ex) {
			System.out.println(ex);
		}
		return format_date;
	}

	public static String convertStringToDatetime(String datetimeString) {

		java.util.Date date = null;
		String format_date = null;
		DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		DateFormat mysql_df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			date = df.parse(datetimeString);
			format_date = mysql_df.format(date);
		} catch (Exception ex) {
			System.out.println(ex);
		}
		return format_date;
	}

	public static int DetectDataBeforeLoad(String[] Row, File filename, BufferedWriter outputWriter)
			throws FileNotFoundException, IOException {
		// To detect the data quality
		int Detect_Factor = 0;

		if (Row[1] == null || Row[2] == null || Row[3] == null || Row[4] == null || Row[5] == null || Row[7] == null
				|| Row[8] == null || Row[9] == null || Row[11] == null || Row[12] == null || Row[13] == null
				|| Row[14] == null) {
			DataInput(Row, filename, outputWriter);
			Detect_Factor = 1;
			// System.out.print("+++");
		}

		// The second step: Check if the Passenger ID is valid. If the ID shows
		// 'unknown', it is invalid
		// According to the select distinct from old dataset, I surpose the
		// length > 8
		else if (Row[7].length() < 8) {
			DataInput(Row, filename, outputWriter);
			Detect_Factor = 2;
			// System.out.print("---");
		}

		else if ((!isInt(Row[14])) || (Integer.parseInt(Row[14]) > 5) || (Integer.parseInt(Row[14]) < 1)) {
			DataInput(Row, filename, outputWriter);
			Detect_Factor = 3;
			// System.out.print("&&&");
		}

		else if (DetectDateTime(Row[8], Row[9]) == 0) {
			DataInput(Row, filename, outputWriter);
			Detect_Factor = 4;
			// System.out.print("^^^");
		}

		return Detect_Factor;
	}

	public static int DetectDateTime(String Boarding_DT, String Alighting_DT) {
		int Datetime_factor = 0;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			java.util.Date BD = sdf.parse(Boarding_DT);
			Calendar BD_cal = Calendar.getInstance();
			BD_cal.setTime(BD);
			java.util.Date AD = sdf.parse(Alighting_DT);
			Calendar AD_cal = Calendar.getInstance();
			AD_cal.setTime(AD);
			if (AD_cal.after(BD_cal))
				Datetime_factor = 1;
		} catch (Exception ex) {
			System.out.println(ex);
		}
		return Datetime_factor;
	}

	public static boolean isInt(String s) {
		try {
			Integer.parseInt(s);
		} catch (NumberFormatException e) {
			return false;
		} catch (NullPointerException e) {
			return false;
		}
		// only got here if we didn't return false
		return true;
	}

	public static void DataInput(String[] Row, File filename, BufferedWriter outputWriter)
			throws FileNotFoundException, IOException {

		if (!filename.exists()) {
			filename.createNewFile();
		}

		for (int k = 0; k < 15; k++) {
			outputWriter.append(Row[k] + ",");
		}
		outputWriter.newLine();
	}
}
