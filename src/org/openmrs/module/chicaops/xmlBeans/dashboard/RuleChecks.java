package org.openmrs.module.chicaops.xmlBeans.dashboard;

/**
 * Bean to hold all the checks that are to be performed against rules.
 *
 * @author Steve McKee
 */
public class RuleChecks {

	private NeverFiredRuleCheck neverFiredCheck;
	private UnFiredRuleCheck unFiredCheck;
	
	/**
	 * Default Constructor
	 */
	public RuleChecks() {
	}
	
	/**
	 * Constructor method
	 * 
	 * @param neverFiredCheck Check to see if rules have never been fired.
	 * @param unFiredCheck Check to see if rules have not fired since a 
	 * particular time.
	 */
	public RuleChecks(NeverFiredRuleCheck neverFiredCheck, UnFiredRuleCheck unFiredCheck) {
		this.neverFiredCheck = neverFiredCheck;
		this.unFiredCheck = unFiredCheck;
	}

    /**
     * @return the neverFiredCheck
     */
    public NeverFiredRuleCheck getNeverFiredCheck() {
    	return neverFiredCheck;
    }
	
    /**
     * @param neverFiredCheck the neverFiredCheck to set
     */
    public void setNeverFiredCheck(NeverFiredRuleCheck neverFiredCheck) {
    	this.neverFiredCheck = neverFiredCheck;
    }
	
    /**
     * @return the unFiredCheck
     */
    public UnFiredRuleCheck getUnFiredCheck() {
    	return unFiredCheck;
    }
	
    /**
     * @param unFiredCheck the unfiredCheck to set
     */
    public void setUnFiredCheck(UnFiredRuleCheck unFiredCheck) {
    	this.unFiredCheck = unFiredCheck;
    }
}
