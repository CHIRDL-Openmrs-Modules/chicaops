package org.openmrs.module.chicaops.xmlBeans.dashboard;

import org.openmrs.module.chicaops.util.ChicaopsUtil;

/**
 * Bean used to contain the parameters to find any clinics that have not scanned any 
 * specific forms over the past specified time line.
 *
 * @author Steve McKee
 */
public class ScanCheck {

	private String formName;
	private String severity;
	private int timePeriod;
	private String timePeriodUnit;
	private FixTips fixTips;
	private Notification notification;
	
	/**
	 * Default Constructor
	 */
	public ScanCheck() {
	}
	
	/**
	 * Constructor method
	 * 
	 * @param formName The name of the form we want to check to see if it's been scanned.
	 * @param severity The severity of the check if it occurs (error, warning).
	 * @param timePeriod The amount of time to check against the last modified 
	 * dates of the files to compare.
	 * @param timePeriodUnit The unit measurement for the time (seconds, minutes, 
	 * hours, days, etc.).
	 * @param fixTips Tips that can be displayed to the user to help resolve 
	 * the issue.
	 * @param notification Notification information if the check fails.
	 */
	public ScanCheck(String formName, String severity, int timePeriod, String timePeriodUnit, 
	                 FixTips fixTips, Notification notification) {
		this.formName = formName;
		this.severity = severity;
		this.timePeriod = timePeriod;
		this.timePeriodUnit = timePeriodUnit;
		this.fixTips = fixTips;
		this.notification = notification;
	}
	
	/**
     * @return the formName
     */
    public String getFormName() {
    	return formName;
    }
	
    /**
     * @param formName the formName to set
     */
    public void setFormName(String formName) {
    	this.formName = formName;
    }
    
    /**
     * @return the severity
     */
    public String getSeverity() {
    	return severity;
    }
	
    /**
     * @param severity the severity to set
     */
    public void setSeverity(String severity) {
    	this.severity = severity;
    }

    /**
     * @return the timePeriod
     */
    public int getTimePeriod() {
    	return timePeriod;
    }
	
    /**
     * @param timePeriod the timePeriod to set
     */
    public void setTimePeriod(int timePeriod) {
    	this.timePeriod = timePeriod;
    }
	
    /**
     * @return the timePeriodUnit
     */
    public String getTimePeriodUnit() {
    	return timePeriodUnit;
    }
	
    /**
     * @param timePeriodUnit the timePeriodUnit to set
     */
    public void setTimePeriodUnit(String timePeriodUnit) {
    	this.timePeriodUnit = timePeriodUnit;
    }
    
    /**
     * @return the fixTips
     */
    public FixTips getFixTips() {
    	return fixTips;
    }

    /**
     * @param fixTips the fixTips to set
     */
    public void setFixTips(FixTips fixTips) {
    	this.fixTips = fixTips;
    }
    
    /**
     * @return the notification
     */
    public Notification getNotification() {
    	return notification;
    }
	
    /**
     * @param notification the notification to set
     */
    public void setNotification(Notification notification) {
    	this.notification = notification;
    }
    
    /**
     * @return The time period in milliseconds.
     */
    public long getTimePeriodInMilliseconds() {
    	return ChicaopsUtil.getTimePeriodInMilliseconds(timePeriod, timePeriodUnit);
    }
}
