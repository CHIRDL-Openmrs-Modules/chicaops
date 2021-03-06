package org.openmrs.module.chicaops.db;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.openmrs.Location;
import org.openmrs.module.chicaops.dashboard.MonitorResult;
import org.openmrs.module.chicaops.xmlBeans.dashboard.ForcedOutPWSCheck;
import org.openmrs.module.chicaops.xmlBeans.dashboard.HL7ExportChecks;
import org.openmrs.module.chicaops.xmlBeans.dashboard.StateToMonitor;
import org.openmrs.module.chicaops.xmlBeans.dashboard.UnFiredRuleCheck;
import org.openmrs.module.chicaops.xmlBeans.dashboard.WifiIssueChecks;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.PatientState;
import org.openmrs.module.dss.hibernateBeans.RuleEntry;

/**
 * Interface to define the data layer methods for the Operations Dashboard.
 * 
 * @author Steve McKee
 */
public interface ChicaopsDAO {

	/**
	 * Checks the supplied state and returns any errors/warnings resulting 
	 * from the check.
	 * 
	 * @param state Contains the information needed to perform checks on 
	 * a state.
	 * 
	 * @return ArrayList of MonitorResult objects containing the result of the 
	 * checks.  This will not return null.
	 */
	public ArrayList<MonitorResult> checkState(StateToMonitor state) throws Exception;
	
	/**
	 * Retrieves all the PatientState objects where a patient has had PSF_wait_to_scan 
	 * state with a null end time and a PWS_create state exists for the same session.
	 * 
	 * @param forcedOutPWSCheck Contains the parameter for checking for forced out PWSs.
	 */
	public List<PatientState> getForcedOutPWSs(ForcedOutPWSCheck forcedOutPWSCheck);
	
	/**
	 * Retrieve the number of suspected wifi issues
	 * @param wifiIssueChecks
	 * @param location
	 * @return
	 */
	public Integer getWifiIssues(WifiIssueChecks wifiIssueChecks, Location location);
    
	/**
	 * Finds status in the HL7 Export table occurring based on the specified parameter in 
	 * the provided HL7ExportAlerts object. 
	 * 
	 * @param alerts HL7ExportAlerts object containing the parameters used to find issues 
	 * in the HL7 Export table.
	 * @return List of Object arrays with a status name at the first index and the 
	 * location id at the second index.
	 */
	public List<Object[]> getHL7ExportAlerts(HL7ExportChecks alerts);
	
	/**
	 * Retrieves all the rule entries that have never fired.
	 * 
	 * @return List of Rule objects.
	 */
	public List<RuleEntry> getNeverFiredRules();
	
	/**
	 * Retrieves all the rule entries that have not been fired within a specified period of time.  
	 * This does not include rule entries that have never fired at all.  This only includes rule entries
	 * that have fired at least one time.
	 * 
	 * @param check UnFiredRuleCheck containing the information needing to be checked.
	 * @return List of RuleEntry objects.
	 */
	public List<RuleEntry> getUnFiredRules(UnFiredRuleCheck check);
	
	/**
	 * Retrieves all the PatientState objects for a particular form that have been recorded 
	 * since the provided Date.
	 * 
	 * @param formId The form ID used for limiting the query.
	 * @param locationId The location ID used for limiting the query.
	 * @param sinceDate Only include entries since this date.
	 * @param reprintStateName reprint State Name.
	 * @return List of PatientState objects for a particular form that have been recored 
	 * since the provided Date.
	 */
	public List<PatientState> getPatientsStates(Integer formId, Integer locationId, Date sinceDate, String reprintStateName);
	
	
}
