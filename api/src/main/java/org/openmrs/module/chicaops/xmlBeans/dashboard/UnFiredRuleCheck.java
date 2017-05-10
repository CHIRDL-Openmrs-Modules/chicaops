package org.openmrs.module.chicaops.xmlBeans.dashboard;

/**
 * Bean containing information for a rule check.  The rule check will be 
 * intended to be used to return rules that have not fired since a 
 * stated period of time.
 *
 * @author Steve McKee
 */
public class UnFiredRuleCheck {

	private String severity;
	private Integer timePeriod = 0;
	private String timePeriodUnit = DashboardConfig.SECOND;
	private FixTips fixTips;
	private Notification notification;
	
	/**
	 * Default Constructor
	 */
	private UnFiredRuleCheck() {
	}
	
	/**
	 * Constructor method
	 * 
	 * @param severity The severity of the resultant check.  Constants for 
	 * severities can be found in the DashboardConfig file in this package.
	 * @param timePeriod The time Period used to see if a rule has fired.
	 * @param timePeriodUnit The unit for the time period.  Constants for 
	 * time period units can be found in the DashboardConfig file in this package.
	 * @param fixTips Tips that can be displayed to the user to help resolve 
	 * the issue.
	 * @param notification Notification information if the check fails.
	 */
	private UnFiredRuleCheck(String severity, Integer timePeriod, String timePeriodUnit,
	                         FixTips fixTips, Notification notification) {
		this.severity = severity;
		this.timePeriod = timePeriod;
		this.timePeriodUnit = timePeriodUnit;
		this.fixTips = fixTips;
		this.notification = notification;
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
