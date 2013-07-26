package org.openmrs.module.chicaops.xmlBeans.dashboard;

import org.openmrs.module.chicaops.util.ChicaopsUtil;

/**
 * Bean used to contain the parameters to find files in the imageDir that do 
 * not exist in the scanDir and vice versa.
 *
 * @author Steve McKee
 */
public class DirectoryCheck {

	private String imageDir;
	private String scanDir;
	private Integer numErrors = 1;
	private String severity;
	private Integer timePeriod = 0;
	private String timePeriodUnit = DashboardConfig.SECOND;
	private Integer bufferTimePeriod = 0;
	private String bufferTimeUnit = DashboardConfig.SECOND;
	private FixTips fixTips;
	private Notification notification;
	
	/**
	 * Default Constructor
	 */
	public DirectoryCheck() {
	}
	
	/**
	 * Constructor method
	 * 
	 * @param imageDir The directory containing the image files.
	 * @param scanDir The directory containing the scan XML files.
	 * @param severity The severity of the check if it occurs (error, warning).
	 * @param timePeriod The amount of time to check against the last modified 
	 * dates of the files to compare.
	 * @param timePeriodUnit The unit measurement for the time (seconds, minutes, 
	 * hours, days, etc.).
	 * @param fixTips Tips that can be displayed to the user to help resolve 
	 * the issue.
	 * @param numErrors The number of errors to reach before a problem is 
	 * reported.
	 * @param notification Notification information if the check fails.
	 */
	public DirectoryCheck(String imageDir, String scanDir, String severity, 
	                      Integer timePeriod, String timePeriodUnit, FixTips fixTips, 
	                      Integer numErrors, Notification notification, Integer bufferTimePeriod, String bufferTimeUnit) {
		this.imageDir = imageDir;
		this.scanDir = scanDir;
		this.severity = severity;
		this.timePeriod = timePeriod;
		this.timePeriodUnit = timePeriodUnit;
		this.fixTips = fixTips;
		this.numErrors = numErrors;
		this.notification = notification;
		this.bufferTimePeriod = bufferTimePeriod;
		this.bufferTimeUnit = bufferTimeUnit;
	}
    
    /**
     * @return the bufferTimePeriod
     */
    public Integer getBufferTimePeriod() {
    	return bufferTimePeriod;
    }
	
    /**
     * @param bufferTimePeriod the bufferTimePeriod to set
     */
    public void setBufferTimePeriod(Integer bufferTimePeriod) {
    	this.bufferTimePeriod = bufferTimePeriod;
    }
	
    /**
     * @return the bufferTimeUnit
     */
    public String getBufferTimeUnit() {
    	return bufferTimeUnit;
    }
	
    /**
     * @param bufferTimeUnit the bufferTimeUnit to set
     */
    public void setBufferTimeUnit(String bufferTimeUnit) {
    	this.bufferTimeUnit = bufferTimeUnit;
    }

	/**
     * @return the imageDir
     */
    public String getImageDir() {
    	return imageDir;
    }
	
    /**
     * @param imageDir the imageDir to set
     */
    public void setImageDir(String imageDir) {
    	this.imageDir = imageDir;
    }
	
    /**
     * @return the scanDir
     */
    public String getScanDir() {
    	return scanDir;
    }
	
    /**
     * @param scanDir the scanDir to set
     */
    public void setScanDir(String scanDir) {
    	this.scanDir = scanDir;
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
    
    /**
     * @return The time period in milliseconds.
     */
    public Long getTimePeriodInMilliseconds() {
    	return ChicaopsUtil.getTimePeriodInMilliseconds(timePeriod, timePeriodUnit);
    }
    
    /**
     * @return The buffer time in milliseconds.
     */
    public Long getBufferTimeInMilliseconds() {
    	return ChicaopsUtil.getTimePeriodInMilliseconds(bufferTimePeriod, bufferTimeUnit);
    }
}
