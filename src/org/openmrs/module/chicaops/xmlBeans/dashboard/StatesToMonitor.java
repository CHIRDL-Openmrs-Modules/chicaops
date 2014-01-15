package org.openmrs.module.chicaops.xmlBeans.dashboard;

import java.util.ArrayList;

/**
 * Contains an ArrayList of states to monitor for issues.
 * 
 * @author Steve McKee
 */
public class StatesToMonitor {

	private ArrayList<StateToMonitor> statesToMonitor;
	
	/**
	 * Sets the states that should be monitored.
	 * 
	 * @param statesToMonitor ArrayList of StateToMonitor objects.
	 */
	public void setStatesToMonitor(ArrayList<StateToMonitor>statesToMonitor) {
		this.statesToMonitor = statesToMonitor;
	}
	
	/**
	 * @return ArrayList of StateToMonitor objects.
	 */
	public ArrayList<StateToMonitor> getStatesToMonitor() {
		return statesToMonitor;
	}
}
