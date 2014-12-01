package org.openmrs.module.chicaops.dashboard;

import org.openmrs.Location;
import org.openmrs.module.chicaops.xmlBeans.dashboard.ManualCheckinChecks;

public class ManualCheckinNumResult {
	private boolean shouldSend;
	private ManualCheckinChecks manualCheckinChecks;
	private Location location;
	
	
	public boolean isShouldSend() {
		return shouldSend;
	}
	public void setShouldSend(boolean shouldSend) {
		this.shouldSend = shouldSend;
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

	
	
}
