package org.openmrs.module.chicaops.dashboard;

import org.openmrs.Location;
import org.openmrs.module.chicaops.xmlBeans.dashboard.WifiIssueChecks;

/**
 * 
 * @author tmdugan
 * used to hold the result for wifi issue monitor
 */
public class WifiIssueNumResult {
	private WifiIssueChecks wifiIssueChecks;
	private Location location;
	
	private Integer numberOfWifiIssues; 
	
	public WifiIssueNumResult(){
	}
	
	public WifiIssueChecks getWifiIssueChecks() {
		return this.wifiIssueChecks;
	}
	public void setWifiIssueChecks(WifiIssueChecks wifiIssueChecks) {
		this.wifiIssueChecks = wifiIssueChecks;
	}
	public Location getLocation() {
		return this.location;
	}
	public void setLocation(Location location) {
		this.location = location;
	}

	/**
	 * @return - the number of wifi issues for this location
	 */
	public Integer getNumberOfWifiIssues(){
		return this.numberOfWifiIssues;
	}
	
	/**
	 * Set the number of wifi issues that have occurred for this location
	 * @param numberOfWifiIssues
	 */
	public void setNumberOfWifiIssues(Integer numberOfWifiIssues){
		this.numberOfWifiIssues = numberOfWifiIssues;
	}
	
	/**
	 * @return - the message that will be displayed in the UI as well as in the email
	 */
	public String getMessage(){

		return "There " + (this.numberOfWifiIssues > 1 ? "have " : "has ") + "been " 
				+ this.numberOfWifiIssues + " wifi issues" + (this.numberOfWifiIssues > 1 ? "s" : "") 
				+ " over the past " + this.wifiIssueChecks.getTimePeriod() + " " 
				+ (this.wifiIssueChecks.getTimePeriod() > 1 ? this.wifiIssueChecks.getTimePeriodUnit() + "s" : this.wifiIssueChecks.getTimePeriodUnit());
	}
}
