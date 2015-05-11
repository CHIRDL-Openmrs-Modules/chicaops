package org.openmrs.module.chicaops.dashboard;

import org.openmrs.Location;
import org.openmrs.module.chicaops.xmlBeans.dashboard.ManualCheckinChecks;

/**
 * 
 * @author wang417
 * used to hold the result for manual check-in monitor
 */
public class ManualCheckinNumResult {
	private ManualCheckinChecks manualCheckinChecks;
	private Location location;
	
	private Integer numberOfManualCheckins; // DWE CHICA-367 Adding numberOfManualCheckins so that the email can tell us how many manual check-ins have occurred over the given time period at this location
	
	public ManualCheckinNumResult(){
	}
	
	public ManualCheckinChecks getManualCheckinChecks() {
		return manualCheckinChecks;
	}
	public void setManualCheckinChecks(ManualCheckinChecks manualCheckinChecks) {
		this.manualCheckinChecks = manualCheckinChecks;
	}
	public Location getLocation() {
		return location;
	}
	public void setLocation(Location location) {
		this.location = location;
	}

	/**
	 * DWE CHICA-367
	 * @return - the number of manual check-ins for this location
	 */
	public Integer getNumberOfManualCheckins(){
		return numberOfManualCheckins;
	}
	
	/**
	 * DWE CHICA-367
	 * Set the number of manual check-ins that have occurred for this location
	 * @param numberOfManualCheckins
	 */
	public void setNumberOfManualCheckins(Integer numberOfManualCheckins){
		this.numberOfManualCheckins = numberOfManualCheckins;
	}
	
	/**
	 * DWE CHICA-367
	 * @return - the message that will be displayed in the UI as well as in the email
	 */
	public String getMessage(){

		return "There " + (numberOfManualCheckins > 1 ? "have " : "has ") + "been " 
				+ numberOfManualCheckins + " manual check-in" + (numberOfManualCheckins > 1 ? "s" : "") 
				+ " over the past " + manualCheckinChecks.getTimePeriod() + " " 
				+ (manualCheckinChecks.getTimePeriod() > 1 ? manualCheckinChecks.getTimePeriodUnit() + "s" : manualCheckinChecks.getTimePeriodUnit());
	}
}
