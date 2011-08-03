package org.openmrs.module.chicaops.xmlBeans.dashboard;

import java.util.ArrayList;

/**
 * Contains an ArrayList of scanning checks to be made.
 * 
 * @author Steve McKee
 */
public class ScanChecks {

	private ArrayList<ScanCheck> scanChecks;
	
	/**
	 * Sets the scanChecks to check.
	 * 
	 * @param scanChecks ArrayList of ScanCheck objects.
	 */
	public void setScanChecks(ArrayList<ScanCheck>scanChecks) {
		this.scanChecks = scanChecks;
	}
	
	/**
	 * @return ArrayList of ScanCheck objects.
	 */
	public ArrayList<ScanCheck> getScanChecks() {
		return scanChecks;
	}
}
