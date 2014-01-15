package org.openmrs.module.chicaops.dashboard;

import java.util.HashMap;
import java.util.Map;

import org.openmrs.module.chicaops.xmlBeans.dashboard.DashboardConfig;
import org.openmrs.module.chicaops.xmlBeans.dashboard.ImmunizationChecks;

/**
 * Used to hold the results of all the server checks.
 *
 * @author Steve McKee
 */
public class ImmunizationCheckResult {


	private Map<String, Integer> immunizationProblems = new HashMap<String, Integer>();
	private ImmunizationChecks immunizationChecks;
	boolean hasErrors = false;
	boolean hasWarnings = false;
	

	

	/**
	 * Constructor method
	 */
	public ImmunizationCheckResult() {
	}
	
   
	 /**
     * Add a new HL7 Export Problem
     * 
     * @param status The status name of the export problem.
     * @param hl7ExportSeverity The severity of the problem.
     */
    public void addImmunizationProblem(String error) {
    	Integer count = immunizationProblems.get(error);
    	if (count == null) {
    		count = 1;
    		immunizationProblems.put(error, count);
    	} else {
    		immunizationProblems.put(error, ++count);
    	}
    	
    	if (immunizationChecks != null) {
    		String severity = immunizationChecks.getSeverity();
    		if (DashboardConfig.SEVERITY_ERROR.equals(severity)) {
    			hasErrors = true;
    		} else if (DashboardConfig.SEVERITY_WARNING.equals(severity)) {
    			hasWarnings = true;
    		}
    	}
    }


	public Map<String, Integer> getImmunizationProblems() {
		return immunizationProblems;
	}


	public void setImmunizationProblems(Map<String, Integer> immunizationProblems) {
		this.immunizationProblems = immunizationProblems;
	}


	public ImmunizationChecks getImmunizationChecks() {
		return immunizationChecks;
	}


	public void setImmunizationChecks(ImmunizationChecks immunizationChecks) {
		this.immunizationChecks = immunizationChecks;
	}


	public boolean isHasErrors() {
		return hasErrors;
	}


	public void setHasErrors(boolean hasErrors) {
		this.hasErrors = hasErrors;
	}


	public boolean isHasWarnings() {
		return hasWarnings;
	}


	public void setHasWarnings(boolean hasWarnings) {
		this.hasWarnings = hasWarnings;
	}

   
}
