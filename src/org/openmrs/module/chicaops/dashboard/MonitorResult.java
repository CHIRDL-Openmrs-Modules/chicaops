package org.openmrs.module.chicaops.dashboard;

import org.openmrs.module.chicaops.xmlBeans.dashboard.StateToMonitor;

/**
 * Bean to hold the information from the state monitoring.
 * 
 * @author Steve McKee
 */
public class MonitorResult {

	private StateToMonitor stateToMonitor;
	private boolean issueOccurred = false;
	private String locationName;
	private int numOccurrences;


	/**
	 * Constructor method
	 */
	public MonitorResult() {
	}
	
	/**
	 * Constructor method
	 * 
	 * @param stateToMonitor The state that was monitor based on its 
	 * containing information.
	 * @param issueOccurred Boolean telling whether or not this situation 
	 * occurred.
	 * @param locationName Name of the location where the issue occurred.
	 * @param numOccurrences The number of times this issues was encountered.
	 */
	public MonitorResult(StateToMonitor stateToMonitor, boolean issueOccurred, 
	                      String locationName, int numOccurrences) {
		this.stateToMonitor = stateToMonitor;
		this.issueOccurred = issueOccurred;
		this.locationName = locationName;
		this.numOccurrences = numOccurrences;
	}
    
    /**
     * @return the stateToMonitor
     */
    public StateToMonitor getStateToMonitor() {
    	return stateToMonitor;
    }

	/**
     * @return whether or not the issue explained in the object occurred.
     */
    public boolean issueOccurred() {
    	return issueOccurred;
    }
    
    /**
     * @return location where the issue occurred.
     */
    public String getLocationName() {
    	return locationName;
    }
    
    /**
     * @return the numOccurrences
     */
    public int getNumOccurrences() {
    	return numOccurrences;
    }
}
