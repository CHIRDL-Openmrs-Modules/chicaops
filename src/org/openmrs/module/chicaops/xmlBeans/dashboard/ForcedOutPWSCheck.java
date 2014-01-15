package org.openmrs.module.chicaops.xmlBeans.dashboard;

/**
 * Bean containing the parameters to use for checking if there were any 
 * forced out PWSs.
 *
 * @author Steve McKee
 */
public class ForcedOutPWSCheck {

	private Integer timePeriod = 0;
	private String timePeriodUnit = DashboardConfig.SECOND;
	private String severity;
	private FixTips fixTips;
	private Notification notification;
	
	/**
	 * Default Constructor
	 */
	public ForcedOutPWSCheck() {
	}
	
	/**
	 * Constructor method
	 * 
	 * @param timePeriod The time Period used to check for forced out PWSs.
	 * @param timePeriodUnit The unit for the time period (second, minute, hour, day, etc.).
	 * @param severity The severity of the problem if it occurs (error, warning).
	 * @param fixTips Tips that can be displayed to the user to help resolve 
	 * the issue.
	 * @param notification Notification information if the check fails.
	 */
	public ForcedOutPWSCheck(Integer timePeriod, String timePeriodUnit, String severity, 
	                         FixTips fixTips, Notification notification) {
		this.timePeriod = timePeriod;
		this.timePeriodUnit = timePeriodUnit;
		this.severity = severity;
		this.fixTips = fixTips;
		this.notification = notification;
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
