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
	private ArrayList<ScanProblem> scanProblems = new ArrayList<ScanProblem>();
	private Map<String, Integer> hl7ExportProblems = new HashMap<String, Integer>();
	private HL7ExportChecks hl7ExportChecks;
	private ManualCheckinNumResult manualCheckinNumResult; // DWE CHICA-367
	private WifiIssueNumResult wifiIssueNumResult;

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
    	return this.careCenterName;
    }
    
    /**
     * @return the careCenterDescription
     */
    public String getCareCenterDescription() {
    	return this.careCenterDescription;
    }
	
    /**
     * @return the stateResults
     */
    public ArrayList<MonitorResult> getStateResults() {
    	return this.stateResults;
    }
    
    /**
     * Adds a new MonitorResult to this CareCenterResult
     * 
     * @param result The MonitorResult to add
     */
    public void addStateResult(MonitorResult result) {
        this.stateResults.add(result);
    	if (result.issueOccurred()) {
    		if (DashboardConfig.SEVERITY_ERROR.equals(
    			result.getStateToMonitor().getSeverity())) {
    		    this.hasErrors = true;
    		} else if (DashboardConfig.SEVERITY_WARNING.equals(
    			result.getStateToMonitor().getSeverity())) {
    		    this.hasWarnings = true;
    		}
    	}
    }
	
    /**
     * @return the hasErrors
     */
    public boolean getHasErrors() {
    	return this.hasErrors;
    }
	
    /**
     * @return the hasWarnings
     */
    public boolean getHasWarnings() {
    	return this.hasWarnings;
    }
	
    /**
     * @return the forcedOutPWSs
     */
    public ArrayList<ForcedOutPWSProblem> getForcedOutPWSs() {
    	return this.forcedOutPWSs;
    }
    
    /**
     * Add a ForcedOutPWSCheckResult to the current list of them
     * 
     * @param forcedOutPWSCheckResult
     */
    public void addForcedOutPWS(ForcedOutPWSProblem forcedOutPWSCheckResult) {
        this.forcedOutPWSs.add(forcedOutPWSCheckResult);
		String severity = forcedOutPWSCheckResult.getForcedOutPWSCheck().getSeverity();
		if (DashboardConfig.SEVERITY_ERROR.equals(severity)) {
		    this.hasErrors = true;
		} else if (DashboardConfig.SEVERITY_WARNING.equals(severity)) {
		    this.hasWarnings = true;
		}
    }

    /**
     * @return the hl7ExportProblems
     */
    public Map<String, Integer> getHl7ExportProblems() {
    	return this.hl7ExportProblems;
    }
    
    /**
     * Add a new HL7 Export Problem
     * 
     * @param status The status name of the export problem.
     * @param hl7ExportSeverity The severity of the problem.
     */
    public void addHl7ExportProblem(String status) {
    	Integer count = this.hl7ExportProblems.get(status);
    	if (count == null) {
    		count = 1;
    		this.hl7ExportProblems.put(status, count);
    	} else {
    	    this.hl7ExportProblems.put(status, ++count);
    	}
    	
    	if (this.hl7ExportChecks != null) {
    		String severity = this.hl7ExportChecks.getSeverity();
    		if (DashboardConfig.SEVERITY_ERROR.equals(severity)) {
    		    this.hasErrors = true;
    		} else if (DashboardConfig.SEVERITY_WARNING.equals(severity)) {
    		    this.hasWarnings = true;
    		}
    	}
    }

    /**
     * @return the hl7ExportChecks
     */
    public HL7ExportChecks getHl7ExportChecks() {
    	return this.hl7ExportChecks;
    }

    /**
     * @param hl7ExportChecks the hl7ExportChecks to set
     */
    public void setHl7ExportChecks(HL7ExportChecks hl7ExportChecks) {
    	this.hl7ExportChecks = hl7ExportChecks;
    }
    
    /**
     * @return the scanProblems
     */
    public ArrayList<ScanProblem> getScanProblems() {
    	return this.scanProblems;
    }
    
    /**
     * Add a ScanProblem to the current list of them
     * 
     * @param scanProblem
     */
    public void addScanProblem(ScanProblem scanProblem) {
        this.scanProblems.add(scanProblem);
		String severity = scanProblem.getScanCheck().getSeverity();
		if (DashboardConfig.SEVERITY_ERROR.equals(severity)) {
		    this.hasErrors = true;
		} else if (DashboardConfig.SEVERITY_WARNING.equals(severity)) {
		    this.hasWarnings = true;
		}
    }
    
    /**
     * DWE CHICA-367
     * Sets hasWarnings or hasErrors and sets the ManualCheckinNumResult object
     * @param manualCheckinNumResult the manualCheckinNumResult to set
     */
    public void setManualCheckinNumResult (ManualCheckinNumResult manualCheckinNumResult){
    	this.manualCheckinNumResult = manualCheckinNumResult;
    	
    	String severity = manualCheckinNumResult.getManualCheckinChecks().getSeverity();
    	if (DashboardConfig.SEVERITY_ERROR.equals(severity)) {
    	    this.hasErrors = true;
		} else if (DashboardConfig.SEVERITY_WARNING.equals(severity)) {
		    this.hasWarnings = true;
		}
    }
    
    /**
     * DWE CHICA-367
     * @return the manualCheckinNumResult
     */
    public ManualCheckinNumResult getManualCheckinNumResult(){
    	return this.manualCheckinNumResult;
    }
    
    /**
     * Sets hasWarnings or hasErrors and sets the WifiIssueNumResult object
     * @param wifiIssueNumResult the wifiIssueNumResult to set
     */
    public void setWifiIssueNumResult (WifiIssueNumResult wifiIssueNumResult){
        this.wifiIssueNumResult = wifiIssueNumResult;
        
        String severity = wifiIssueNumResult.getWifiIssueChecks().getSeverity();
        if (DashboardConfig.SEVERITY_ERROR.equals(severity)) {
            this.hasErrors = true;
        } else if (DashboardConfig.SEVERITY_WARNING.equals(severity)) {
            this.hasWarnings = true;
        }
    }
    
    /**
     * @return the wifiIssueNumResult
     */
    public WifiIssueNumResult getWifiIssueNumResult(){
        return this.wifiIssueNumResult;
    }
}
