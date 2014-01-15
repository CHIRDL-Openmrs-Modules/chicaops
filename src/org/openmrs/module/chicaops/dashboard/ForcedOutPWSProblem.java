package org.openmrs.module.chicaops.dashboard;

import org.openmrs.module.chicaops.xmlBeans.dashboard.ForcedOutPWSCheck;

/**
 * Represents that we found there have been PWSs forced out. 
 *
 * @author Steve McKee
 */
public class ForcedOutPWSProblem {

	private ForcedOutPWSCheck forcedOutPWSCheck;
	
	/**
	 * Constructor method
	 * 
	 * @param forcedOutPWSCheck The check that was performed resulting in 
	 * finding forced out PWSs.
	 */
	public ForcedOutPWSProblem(ForcedOutPWSCheck forcedOutPWSCheck) {
		this.forcedOutPWSCheck = forcedOutPWSCheck;
	}

    /**
     * @return the forcedOutPWSCheck
     */
    public ForcedOutPWSCheck getForcedOutPWSCheck() {
    	return forcedOutPWSCheck;
    }
}
