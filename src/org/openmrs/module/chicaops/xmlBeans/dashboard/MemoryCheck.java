package org.openmrs.module.chicaops.xmlBeans.dashboard;

/**
 * Bean to hold the percentile limit of memory usage to total memory.
 *
 * @author Steve McKee
 */
public class MemoryCheck {
	
	private int percentileToNotify;
	private String severity;
	private FixTips fixTips;
	private Notification notification;

	/**
	 * Default Constructor
	 */
	public MemoryCheck() {
		
	}
	
	/**
	 * Constructor method
	 * 
	 * @param percentileToNotify The percentile threshold limit for 
	 * the server memory usage.
	 * @param severity The severity of the problem if it occurs (error, warning).
	 * @param fixTips Tips that can be displayed to the user to help resolve 
	 * the issue.
	 * @param notification Notification information if the check fails.
	 */
	public MemoryCheck(int percentileToNotify, String severity, FixTips fixTips, Notification notification) {
		this.percentileToNotify = percentileToNotify;
		this.severity = severity;
		this.fixTips = fixTips;
		this.notification = notification;
	}
	
    /**
     * @return the percentileToNotify
     */
    public int getPercentileToNotify() {
    	return percentileToNotify;
    }
    
    /**
     * @return the severity
     */
    public String getSeverity() {
    	return severity;
    }
    
    /**
     * @param percentileToNotify the percentileToNotify to set
     */
    public void setPercentileToNotify(int percentileToNotify) {
    	this.percentileToNotify = percentileToNotify;
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
