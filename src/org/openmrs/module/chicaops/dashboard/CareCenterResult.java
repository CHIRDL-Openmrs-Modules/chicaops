package org.openmrs.module.chicaops.dashboard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.openmrs.module.chicaops.xmlBeans.dashboard.DashboardConfig;
import org.openmrs.module.chicaops.xmlBeans.dashboard.HL7ExportChecks;

/**
 * Contains the results of the monitoring performed by the CHICA Dashboard.
 * 
 * @author Steve McKee
 */
public class CareCenterResult {

	private String careCenterName;
	private String careCenterDescription;
	private boolean hasErrors = false;
	private boolean hasWarnings = false;
	private ArrayList<MonitorResult> stateResults = new ArrayList<MonitorResult>();
	private ArrayList<ForcedOutPWSProblem> forcedOutPWSs = new ArrayList<ForcedOutPWSProblem>();
	private Map<String, Integer> hl7ExportProblems = new HashMap<String, Integer>();
	private HL7ExportChecks hl7ExportChecks;

	/**
	 * Constructor method
	 * 
	 * @param careCenterName Name of the care center
	 */
	public CareCenterResult(String careCenterName, String careCenterDescription) {
		this.careCenterName = careCenterName;
		this.careCenterDescription = careCenterDescription;
	}
	
    /**
     * @return the careCenterName
     */
    public String getCareCenterName() {
    	return careCenterName;
    }
    
    /**
     * @return the careCenterDescription
     */
    public String getCareCenterDescription() {
    	return careCenterDescription;
    }
	
    /**
     * @return the stateResults
     */
    public ArrayList<MonitorResult> getStateResults() {
    	return stateResults;
    }
    
    /**
     * Adds a new MonitorResult to this CareCenterResult
     * 
     * @param result The MonitorResult to add
     */
    public void addStateResult(MonitorResult result) {
    	stateResults.add(result);
    	if (result.issueOccurred()) {
    		if (DashboardConfig.SEVERITY_ERROR.equals(
    			result.getStateToMonitor().getSeverity())) {
    			hasErrors = true;
    		} else if (DashboardConfig.SEVERITY_WARNING.equals(
    			result.getStateToMonitor().getSeverity())) {
    			hasWarnings = true;
    		}
    	}
    }
	
    /**
     * @return the hasErrors
     */
    public boolean getHasErrors() {
    	return hasErrors;
    }
	
    /**
     * @return the hasWarnings
     */
    public boolean getHasWarnings() {
    	return hasWarnings;
    }
	
    /**
     * @return the forcedOutPWSs
     */
    public ArrayList<ForcedOutPWSProblem> getForcedOutPWSs() {
    	return forcedOutPWSs;
    }
    
    /**
     * Add a ForcedOutPWSCheckResult to the current list of them
     * 
     * @param forcedOutPWSCheckResult
     */
    public void addForcedOutPWS(ForcedOutPWSProblem forcedOutPWSCheckResult) {
    	forcedOutPWSs.add(forcedOutPWSCheckResult);
		String severity = forcedOutPWSCheckResult.getForcedOutPWSCheck().getSeverity();
		if (DashboardConfig.SEVERITY_ERROR.equals(severity)) {
			hasErrors = true;
		} else if (DashboardConfig.SEVERITY_WARNING.equals(severity)) {
			hasWarnings = true;
		}
    }

    /**
     * @return the hl7ExportProblems
     */
    public Map<String, Integer> getHl7ExportProblems() {
    	return hl7ExportProblems;
    }
    
    /**
     * Add a new HL7 Export Problem
     * 
     * @param status The status name of the export problem.
     * @param hl7ExportSeverity The severity of the problem.
     */
    public void addHl7ExportProblem(String status) {
    	Integer count = hl7ExportProblems.get(status);
    	if (count == null) {
    		count = 1;
    		hl7ExportProblems.put(status, count);
    	} else {
    		hl7ExportProblems.put(status, ++count);
    	}
    	
    	if (hl7ExportChecks != null) {
    		String severity = hl7ExportChecks.getSeverity();
    		if (DashboardConfig.SEVERITY_ERROR.equals(severity)) {
    			hasErrors = true;
    		} else if (DashboardConfig.SEVERITY_WARNING.equals(severity)) {
    			hasWarnings = true;
    		}
    	}
    }

    /**
     * @return the hl7ExportChecks
     */
    public HL7ExportChecks getHl7ExportChecks() {
    	return hl7ExportChecks;
    }

    /**
     * @param hl7ExportChecks the hl7ExportChecks to set
     */
    public void setHl7ExportChecks(HL7ExportChecks hl7ExportChecks) {
    	this.hl7ExportChecks = hl7ExportChecks;
    }
}
