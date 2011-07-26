package org.openmrs.module.chicaops.xmlBeans.dashboard;

/**
 * Bean containing information about the "rule never fired" check.
 *
 * @author Steve McKee
 */
public class NeverFiredRuleCheck {

	private String severity;
	private FixTips fixTips;
	private Notification notification;
	
	/**
	 * Default Constructor
	 */
	public NeverFiredRuleCheck() {
	}
	
	/**
	 * Constructor method
	 * 
	 * @param severity The severity of the resultant check.  Constants for 
	 * severities can be found in the DashboardConfig file in this package.
	 * @param fixTips Tips that can be displayed to the user to help resolve 
	 * the issue.
	 * @param notification Notification information if the check fails.
	 */
	public NeverFiredRuleCheck(String severity, FixTips fixTips, Notification notification) {
		this.severity = severity;
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
