package org.openmrs.module.chicaops.xmlBeans.dashboard;

import java.util.ArrayList;

/**
 * Checks performed against the chica_hl7_export table.  This bean 
 * contains the parameters used to perform the checks.
 *
 * @author Steve McKee
 */
public class ImmunizationChecks {
	
	private ArrayList<String> checks = new ArrayList<String>();
	private Integer timePeriod = 0;
	private String timePeriodUnit = DashboardConfig.SECOND;
	private Integer numErrors = 1;
	private String severity;
	private FixTips fixTips;
	private Notification notification;

	/**
	 * Default Constructor
	 */
	public ImmunizationChecks() {
	}
	
	/**
	 * Constructor method
	 * 
	 * @param checks the chirdlutilbackports error table for immunization related errors.
	 * @param timePeriod The time Period used to check for immunization registry availability.
	 * @param timePeriodUnit The unit for the time period (second, minute, hour, day, etc.).
	 * @param numErrors The number of errors to reach before a problem is 
	 * reported.
	 * @param severity The severity of the problem if it occurs (error, warning).
	 * @param fixTips Tips that can be displayed to the user to help resolve 
	 * the issue.
	 * @param notification Notification information if the check fails.
	 */
	public ImmunizationChecks(ArrayList<String> checks, Integer timePeriod, String timePeriodUnit, 
	                       Integer numErrors, String severity, FixTips fixTips, Notification notification) {
		this.checks = checks;
		this.timePeriod = timePeriod;
		this.timePeriodUnit = timePeriodUnit;
		this.numErrors = numErrors;
		this.severity = severity;
		this.fixTips = fixTips;
		this.notification = notification;
	}

    /**
     * @return the checks
     */
    public ArrayList<String> getChecks() {
    	return checks;
    }

    /**
     * @param checks the checks to set
     */
    public void setChecks(ArrayList<String> checks) {
    	this.checks = checks;
    }

    /**
     * @return the timePeriod
     */
    public Integer getTimePeriod() {
    	return timePeriod;
    }
	
    /**
     * @param timePeriod the timePeriod to set
     */
    public void setTimePeriod(Integer timePeriod) {
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
     * @return the numErrors
     */
    public Integer getNumErrors() {
    	return numErrors;
    }

    /**
     * @param numErrors the numErrors to set
     */
    public void setNumErrors(Integer numErrors) {
    	this.numErrors = numErrors;
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
}
