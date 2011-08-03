package org.openmrs.module.chicaops.util;

import org.openmrs.module.chicaops.xmlBeans.dashboard.DashboardConfig;

/**
 * Utility class for the chicaops module.
 *
 * @author Steve McKee
 */
public class ChicaopsUtil {

	/**
     * @return The time period in milliseconds.
     */
    public static long getTimePeriodInMilliseconds(int timePeriod, String timePeriodUnit) {
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
