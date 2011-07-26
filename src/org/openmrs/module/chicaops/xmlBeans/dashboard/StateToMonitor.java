package org.openmrs.module.chicaops.xmlBeans.dashboard;

/**
 * Information and parameters about a specific state that should be monitored.
 * 
 * @author Steve McKee
 */
public class StateToMonitor {

	private String name;
	private int elapsedTime;
	private String elapsedTimeUnit;
	private int timePeriod;
	private String timePeriodUnit;
	private int numErrors;
	private String severity;
	private FixTips fixTips;
	private Notification notification;

	/**
	 * Default Constructor
	 */
	public StateToMonitor() {
	}
	
	/**
	 * Constructor method
	 * 
	 * @param name The name of the state to be monitored. This should 
	 * match what's in the name column of the atd_state table.
	 * @param elapsedTime The threshold time used to report errors/warnings.
	 * @param elapsedTimeUnit The time unit for the elapsed time 
	 * measurement (seconds, minutes, days).
	 * @param timePeriod The time period in which the measurements will 
	 * be taken.
	 * @param timePeriodUnit The time unit for the time period (seconds, 
	 * minutes, hours, days).
	 * @param numErrors The number of errors to reach before a problem is 
	 * reported.
	 * @param severity The severity of the issue if a problem is 
	 * found (warning, error).
	 * @param fixTips Tips that can be displayed to the user to help resolve 
	 * the issue.
	 * @param notification Notification information if the check fails.
	 */
	public StateToMonitor(String name, int elapsedTime, 
	                      String elapsedTimeUnit, int timePeriod, 
	                      String timePeriodUnit, int numErrors, 
	                      String severity, FixTips fixTips, Notification notification) {
		this.name = name;
		this.elapsedTime = elapsedTime;
		this.elapsedTimeUnit = elapsedTimeUnit;
		this.timePeriod = timePeriod;
		this.timePeriodUnit = timePeriodUnit;
		this.numErrors = numErrors;
		this.severity = severity;
		this.fixTips = fixTips;
		this.notification = notification;
	}
	
	/**
	 * @return The state name.
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * @return The threshold time used to report errors/warnings.
	 */
	public int getElapsedTime() {
		return elapsedTime;
	}
	
	/**
	 * @return The time unit for the elapsed time measurement.
	 */
	public String getElapsedTimeUnit() {
		return elapsedTimeUnit;
	}
	
	/**
	 * @return The time period in which the measurements were 
	 * taken.
	 */
	public int getTimePeriod() {
		return timePeriod;
	}
	
	/**
	 * @return The time unit for the time period.
	 */
	public String getTimePeriodUnit() {
		return timePeriodUnit;
	}
	
	/**
	 * @return The number of errors to reach before a problem is 
	 * reported.
	 */
	public int getNumErrors() {
		return numErrors;
	}
	
	/**
	 * @return The.severity of the issue
	 */
	public String getSeverity() {
		return severity;
	}
	
    /**
     * @return the fixTips
     */
    public FixTips getFixTips() {
    	return fixTips;
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
