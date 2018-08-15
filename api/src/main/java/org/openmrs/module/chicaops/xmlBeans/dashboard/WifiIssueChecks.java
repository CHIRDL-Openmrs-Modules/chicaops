package org.openmrs.module.chicaops.xmlBeans.dashboard;

import org.openmrs.module.chicaops.util.ChicaopsUtil;

/**
 * 
 * @author tmdugan
 * count the number of wifi issues in the latest period of time
 */
public class WifiIssueChecks {
	private Integer timePeriod;
	private String timePeriodUnit;
	private Integer wifiIssueNum;
	private String severity;
	private Notification notification;
	
	/**
	 * constructor
	 * @param timePeriod checking time period
	 * @param timePeriodUnit the unit of time period
	 * @param wifiIssueNum the minimal number of wifi issues to trigger report
	 * @param severity severity
	 * @Param Notification a Notification object
	 */
	public WifiIssueChecks(Integer timePeriod, String timePeriodUnit, Integer wifiIssueNum, String severity, Notification notification) {
		super();
		this.timePeriod = timePeriod;
		this.timePeriodUnit = timePeriodUnit;
		this.wifiIssueNum = wifiIssueNum;
		this.severity = severity;
		this.notification = notification;
	}
	
	public WifiIssueChecks(){
		
	}

	public Integer getTimePeriod() {
		return this.timePeriod;
	}

	public void setTimePeriod(Integer timePeriod) {
		this.timePeriod = timePeriod;
	}

	public String getTimePeriodUnit() {
		return this.timePeriodUnit;
	}

	public void setTimePeriodUnit(String timePeriodUnit) {
		this.timePeriodUnit = timePeriodUnit;
	}
	
    public Integer getWifiIssueNum() {
        return this.wifiIssueNum;
    }

    
    public void setWifiIssueNum(Integer wifiIssueNum) {
        this.wifiIssueNum = wifiIssueNum;
    }

    public String getSeverity() {
		return this.severity;
	}

	public void setSeverity(String severity) {
		this.severity = severity;
	}

	public Notification getNotification() {
		return this.notification;
	}

	public void setNotification(Notification notification) {
		this.notification = notification;
	}
	
	
	/**
     * @return The time period in milliseconds.
     */
    public Long getTimePeriodInMilliseconds() {
    	return ChicaopsUtil.getTimePeriodInMilliseconds(this.timePeriod, this.timePeriodUnit);
    }
	
}
