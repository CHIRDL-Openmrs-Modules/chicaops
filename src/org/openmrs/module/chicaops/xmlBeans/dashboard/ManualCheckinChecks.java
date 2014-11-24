package org.openmrs.module.chicaops.xmlBeans.dashboard;

public class ManualCheckinChecks {
	private Integer timePeriod;
	private String timePeriodUnit;
	private Integer manualCheckinNum;
	private String severity;
	private Notification notification;
	
	public ManualCheckinChecks(Integer timePeriod, String timePeriodUnit, Integer manualCheckinNum, String severity, Notification notification) {
		super();
		this.timePeriod = timePeriod;
		this.timePeriodUnit = timePeriodUnit;
		this.manualCheckinNum = manualCheckinNum;
		this.severity = severity;
		this.notification = notification;
	}
	
	public ManualCheckinChecks(){
		
	}

	public Integer getTimePeriod() {
		return timePeriod;
	}

	public void setTimePeriod(Integer timePeriod) {
		this.timePeriod = timePeriod;
	}

	public String getTimePeriodUnit() {
		return timePeriodUnit;
	}

	public void setTimePeriodUnit(String timePeriodUnit) {
		this.timePeriodUnit = timePeriodUnit;
	}

	public Integer getManualCheckinNum() {
		return manualCheckinNum;
	}

	public void setManualCheckinNum(Integer manualCheckinNum) {
		this.manualCheckinNum = manualCheckinNum;
	}

	public String getSeverity() {
		return severity;
	}

	public void setSeverity(String severity) {
		this.severity = severity;
	}

	public Notification getNotification() {
		return notification;
	}

	public void setNotification(Notification notification) {
		this.notification = notification;
	}
	
	
	
	
}
