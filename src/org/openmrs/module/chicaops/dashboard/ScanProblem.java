package org.openmrs.module.chicaops.dashboard;

import org.openmrs.module.chicaops.xmlBeans.dashboard.ScanCheck;

/**
 * Indicates a problem was found while processing a scan check.  Contains 
 * the failed check that was performed.
 *
 * @author Steve McKee
 */
public class ScanProblem {

	private ScanCheck scanCheck;
	
	/**
	 * Constructor method
	 * 
	 * @param scanCheck The ScanCheck that was performed.
	 */
	public ScanProblem(ScanCheck scanCheck) {
		this.scanCheck = scanCheck;
	}

    /**
     * @return the scanCheck
     */
    public ScanCheck getScanCheck() {
    	return scanCheck;
    }
}
