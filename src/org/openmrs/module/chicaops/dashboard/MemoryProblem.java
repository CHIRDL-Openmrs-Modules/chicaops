package org.openmrs.module.chicaops.dashboard;

import org.openmrs.module.chicaops.xmlBeans.dashboard.MemoryCheck;

/**
 * Bean used to represent a memory problem.  This can be a problem with 
 * heap or non-heap occurring over a specified percentage.
 *
 * @author Steve McKee
 */
public class MemoryProblem {

	public static final String HEAP = "heap";
	public static final String NON_HEAP = "non-heap";
	
	private MemoryCheck memCheck;
	private int percentageUsed;
	private String memType;
	
	/**
	 * Constructor method
	 * 
	 * @param memCheck The MemoryCheck object containing the memory check
	 * that was performed.
	 * @param percentageUsed The actual percentage of memory used (used 
	 * memory/total memory).
	 * @param memType The type of memory problem (heap/non-heap).
	 */
	public MemoryProblem(MemoryCheck memCheck, int percentageUsed, String memType) {
		this.memCheck = memCheck;
		this.percentageUsed = percentageUsed;
		this.memType = memType;
	}
	
    /**
     * @return the memCheck
     */
    public MemoryCheck getMemCheck() {
    	return memCheck;
    }

    /**
     * @return the percentageUsed
     */
    public int getPercentageUsed() {
    	return percentageUsed;
    }
	
    /**
     * @return the memType
     */
    public String getMemType() {
    	return memType;
    }
}
