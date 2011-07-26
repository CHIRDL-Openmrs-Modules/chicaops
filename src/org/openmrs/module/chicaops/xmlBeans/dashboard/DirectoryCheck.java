package org.openmrs.module.chicaops.xmlBeans.dashboard;

/**
 * Bean used to contain the parameters to find files in the imageDir that do 
 * not exist in the scanDir and vice versa.
 *
 * @author Steve McKee
 */
public class DirectoryCheck {

	private String imageDir;
	private String scanDir;
	private int numErrors = 1;
	private String severity;
	private int timePeriod;
	private String timePeriodUnit;
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
	                      int timePeriod, String timePeriodUnit, FixTips fixTips, 
	                      int numErrors, Notification notification) {
		this.imageDir = imageDir;
		this.scanDir = scanDir;
		this.severity = severity;
		this.timePeriod = timePeriod;
		this.timePeriodUnit = timePeriodUnit;
		this.fixTips = fixTips;
		this.numErrors = numErrors;
		this.notification = notification;
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
    public int getNumErrors() {
    	return numErrors;
    }
	
    /**
     * @param numErrors the numErrors to set
     */
    public void setNumErrors(int numErrors) {
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
    	long time = timePeriod;
    	if (DashboardConfig.SECOND.equals(timePeriodUnit)) {
    		return time * 1000;
    	} else if (DashboardConfig.MINUTE.equals(timePeriodUnit)) {
    		return time * 60 * 1000;
    	} else if (DashboardConfig.HOUR.equals(timePeriodUnit)) {
    		return time * 60 * 60 * 1000;
    	} else if (DashboardConfig.DAY.equals(timePeriodUnit)) {
    		return time * 24 * 60 * 60 * 1000;
    	} else if (DashboardConfig.WEEK.equals(timePeriodUnit)) {
    		return time * 7 * 24 * 60 * 60 * 1000;
    	} else if (DashboardConfig.QUARTER.equals(timePeriodUnit)) {
    		return time * 13 * 7 * 24 * 60 * 60 * 1000;
    	} else if (DashboardConfig.YEAR.equals(timePeriodUnit)) {
    		return time * 52 * 7 * 24 * 60 * 60 * 1000;
    	}
    	
    	return time;
    }
}
