package org.openmrs.module.chicaops.dashboard;

import java.util.ArrayList;
import java.util.List;

import org.openmrs.module.chicaops.xmlBeans.dashboard.DashboardConfig;
import org.openmrs.module.chicaops.xmlBeans.dashboard.RuleChecks;

/**
 * Bean used to store the results of all the rule checks.
 *
 * @author Steve McKee
 */
public class RuleCheckResult {

	private RuleChecks ruleChecks;
	private List<RuleIdentifier> neverFiredRules = new ArrayList<RuleIdentifier>();
	private List<String> unFiredRules = new ArrayList<String>();
	private boolean hasErrors;
	private boolean hasWarnings;
	
	/**
	 * Constructor method
	 * 
	 * @param unfiredRules ArrayList of unfired rule titles.
	 */
	public RuleCheckResult(RuleChecks ruleChecks) {
		this.ruleChecks = ruleChecks;;
	}
    
    /**
     * @return the ruleChecks
     */
    public RuleChecks getRuleChecks() {
    	return ruleChecks;
    }
    
    public void addUnFiredRule(String ruleTitle) {
    	unFiredRules.add(ruleTitle);
    	if (DashboardConfig.SEVERITY_ERROR.equals(ruleChecks.getUnFiredCheck().getSeverity())) {
    		hasErrors = true;
    	} else if (DashboardConfig.SEVERITY_WARNING.equals(ruleChecks.getUnFiredCheck().getSeverity())) {
    		hasWarnings = true;
    	}
    }
    
    public void addNeverFiredRule(RuleIdentifier ruleIdentifier) {
    	neverFiredRules.add(ruleIdentifier);
    	if (DashboardConfig.SEVERITY_ERROR.equals(ruleChecks.getNeverFiredCheck().getSeverity())) {
    		hasErrors = true;
    	} else if (DashboardConfig.SEVERITY_WARNING.equals(ruleChecks.getNeverFiredCheck().getSeverity())) {
    		hasWarnings = true;
    	}
    }

	/**
     * @return the unFiredRules
     */
    public List<String> getUnFiredRules() {
    	return unFiredRules;
    }
    
	/**
     * @return the neverFiredRules
     */
    public List<RuleIdentifier> getNeverFiredRules() {
    	return neverFiredRules;
    }
    
    /**
     * @return the hasErrors
     */
    public boolean isHasErrors() {
    	return hasErrors;
    }
	
    /**
     * @return the hasWarnings
     */
    public boolean isHasWarnings() {
    	return hasWarnings;
    }
}
